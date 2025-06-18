/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 342252328@qq.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.dreamroute.sqlprinter.starter.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.github.dreamroute.sqlprinter.starter.anno.SqlprinterProperties;
import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * print simple sql
 *
 * @author 342252328@qq.com.2016-06-14
 * @version 1.0
 * @since JDK1.8
 */
@Slf4j
@EnableConfigurationProperties(SqlprinterProperties.class)
@Intercepts(
        {
                @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}),
                @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
        }
)
public class SqlPrinter implements Interceptor, ApplicationListener<ContextRefreshedEvent> {

    private final SqlprinterProperties sqlprinterProperties;
    private final List<ValueConverter> converters;
    private final Set<String> filter;
    private final boolean show;
    private final boolean showResult;
    private final String[] showResultExclude;

    private Configuration config;

    public SqlPrinter(SqlprinterProperties props, List<ValueConverter> converters) {
        this.sqlprinterProperties = props;
        this.converters = converters;
        filter = new HashSet<>(Arrays.asList(ofNullable(props.getFilter()).orElseGet(() -> new String[0])));
        this.show = props.isShow();
        this.showResult = props.isShowResult();
        this.showResultExclude = props.getShowResultExclude();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SqlSessionFactory sqlSessionFactory = event.getApplicationContext().getBean(SqlSessionFactory.class);
        this.config = sqlSessionFactory.getConfiguration();
    }

    @Override
    public Object intercept(Invocation invocation) throws Exception {

        // 调用原始方法
        Object result = invocation.proceed();

        try {
            Object target = invocation.getTarget();
            Parse p = new Parse();
            // 非查询走这里(非查询不走ResultSetHandler接口，所以放在这里, 直接打印sql)
            if (target instanceof ParameterHandler) {
                MetaObject m = config.newMetaObject(target);
                MappedStatement ms = (MappedStatement) m.getValue("mappedStatement");
                SqlCommandType sqlCommandType = ms.getSqlCommandType();
                if (sqlCommandType != SqlCommandType.SELECT) {
                    p.id = ms.getId();
                    processSql(ms.getId(), ms.getBoundSql(m.getValue("parameterObject")), p);
                }
                // 对于((PreparedStatement) countStmt).execute()这种方式执行的sql, 不会走ResultSetHandler拦截, 所以不会走下方的查询逻辑, 因此打印sql放在这里, 而分页插件就包含((PreparedStatement) countStmt).execute()这种查询
                else if(ms.getId().contains("分页统计")) {
                    p = getSql(invocation);
                }
            }

            // 查询走这里, 这里会生成sql 
            else {
                p = getSql(invocation);
            }

            if (CharSequenceUtil.isNotBlank(p.sql)) {
                printResult(result, p);
            }
        } catch (Exception e) {
            // print sql is not important, so ignore it.
        }

        return result;
    }

    private Parse getSql(Invocation invocation) {

        Parse parse = new Parse();

        MetaObject resultSetHandler = config.newMetaObject(invocation.getTarget());
        MappedStatement mappedStatement = (MappedStatement) resultSetHandler.getValue("mappedStatement");
        String id = mappedStatement.getId();
        parse.id = id;
        BoundSql boundSql = (BoundSql) resultSetHandler.getValue("boundSql");
        processSql(id, boundSql, parse);
        return parse;
    }

    private void processSql(String id, BoundSql boundSql, Parse parse) {
        if (show && !filter.contains(id)) {
            
            Object parameterObject = boundSql.getParameterObject();
            String originalSql = boundSql.getSql();
            StringBuilder sb = new StringBuilder(originalSql);
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings != null) {
                for (ParameterMapping parameterMapping : parameterMappings) {
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        Object value;
                        String propertyName = parameterMapping.getProperty();
                        if (boundSql.hasAdditionalParameter(propertyName)) {
                            value = boundSql.getAdditionalParameter(propertyName);
                        } else if (parameterObject == null) {
                            value = null;
                        } else if (config.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                            value = parameterObject;
                        } else {
                            MetaObject metaObject = config.newMetaObject(parameterObject);
                            value = metaObject.getValue(propertyName);
                        }

                        // 转换器
                        for (ValueConverter vc : converters) {
                            value = vc.convert(value);
                        }

                        // sql中非数字类型和boolean类型的值加单引号
                        if (value != null && !(value instanceof Number) && !(value instanceof Boolean)) {
                            value = "'" + value + "'";
                        }

                        // 替换问号
                        int pos = sb.indexOf("?");
                        sb.replace(pos, pos + 1, String.valueOf(value));
                    }
                }

                String info = sb.toString();
                if (sqlprinterProperties.isFormat()) {
                    info = format(info);
                }

                parse.sql = info;
            }
        }
    }

    private void printResult(Object result, Parse parse) {

        String resp = "\r\n\r\n";
        resp += "==> " + parse.id + "\r\n";
        resp += parse.sql + "\r\n";

        String[] columnNames = null;
        List<String[]> data = null;
        if (showResult) {
            if (result instanceof List<?> && CollUtil.isNotEmpty((Collection<?>) result)) {
                Field[] fields = ReflectUtil.getFields(((List<?>) result).get(0).getClass());
                fields = filterExclude(fields);
                if (fields != null && fields.length > 0) {
                    columnNames = generateColumnNames(fields);
                    data = new ArrayList<>(((List<?>) result).size());
                    for (int i = 0; i < ((List<?>) result).size(); i++) {
                        String[] d = new String[fields.length];
                        for (int j = 0; j < fields.length; j++) {
                            Object v = ReflectUtil.getFieldValue(((List<?>) result).get(i), fields[j]);
                            for (ValueConverter vc : converters) {
                                v = vc.convert(v);
                            }
                            d[j] = StrUtil.toString(v);
                        }
                        data.add(d);
                    }
                }
            } else if (!(result instanceof List<?>) && result != null) {
                Field[] fields = ReflectUtil.getFields(result.getClass());
                fields = filterExclude(fields);
                columnNames = generateColumnNames(fields);
                data = new ArrayList<>(1);
                for (int i = 0; i < fields.length; i++) {
                    String[] d = new String[fields.length];
                    Object v = ReflectUtil.getFieldValue(result, fields[i]);
                    for (ValueConverter vc : converters) {
                        v = vc.convert(v);
                    }
                    d[i] = StrUtil.toString(v);
                    data.add(d);
                }
            }
        }

        if (columnNames != null && columnNames.length > 0 && CollUtil.isNotEmpty(data)) {
            PrettyTable table = new PrettyTable(columnNames);
            data.forEach(table::addRow);
            resp += table;
        }
        log.info(resp);
    }

    private Field[] filterExclude(Field[] fields) {
        if (showResultExclude != null && showResultExclude.length > 0) {
            return Arrays.stream(fields).filter(field -> !ArrayUtil.contains(showResultExclude, field.getName())).toArray(Field[]::new);
        }
        return fields;
    }

    private static String[] generateColumnNames(Field[] fields) {
        String[] columnNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            boolean accessible = fields[i].isAccessible();
            fields[i].setAccessible(true);
            columnNames[i] = fields[i].getName();
            fields[i].setAccessible(accessible);
        }
        return columnNames;
    }

    /**
     * 使用druid格式化sql，如果格式化失败，那么返回未经过格式化的sql，增加此格式化的原因是因为：
     * 1. 为了美观和打印的格式统一
     * 2. mysql的xml文件编写的sql带有动态标签，如果动态标签不满足条件时sql会有很多多余的换行和缩进
     *
     * @param sql 需要格式化的sql
     * @return 返回格式化之后的sql
     */
    private String format(String sql) {
        try {
            // 此格式化在不改变sql语义的情况下会移除一些括号，比如：
            // 格式化前：select * from xx where id = (#{id} and name = #{name}) and pwd = #{pwd}
            // 格式化后：select * from xx where id = #{id} and name = #{name} and pwd = #{pwd}（括号被移除）
            // 但是如果移除括号会改变sql语义，那就不会被移除
            return SQLUtils.formatMySql(sql);
        } catch (Exception e) {
            return sql;
        }
    }

    private static class Parse {
        String sql;
        String id;
    }
}