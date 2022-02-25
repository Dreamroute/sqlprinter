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

import com.github.dreamroute.sqlprinter.starter.anno.SqlprinterProperties;
import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;
import com.github.dreamroute.sqlprinter.starter.util.PluginUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@Intercepts({@Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})})
public class SqlPrinter implements Interceptor, ApplicationListener<ContextRefreshedEvent> {

    private final SqlprinterProperties sqlprinterProperties;
    private final List<ValueConverter> converters;
    private final Set<String> filter;
    private final boolean show;

    private Configuration config;

    public SqlPrinter(SqlprinterProperties props, List<ValueConverter> converters) {
        this.sqlprinterProperties = props;
        this.converters = converters;
        filter = new HashSet<>(Arrays.asList(ofNullable(props.getFilter()).orElseGet(() -> new String[0])));
        this.show = props.isShow();
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

        // 打印sql
        printSql(invocation);

        return result;
    }

    private void printSql(Invocation invocation) {

        DefaultParameterHandler parameterHander = (DefaultParameterHandler) PluginUtil.processTarget(invocation.getTarget());
        MetaObject handler = config.newMetaObject(parameterHander);
        MappedStatement mappedStatement = (MappedStatement) handler.getValue("mappedStatement");
        String id = mappedStatement.getId();

        if (show && !filter.contains(id)) {

            Object parameterObject = parameterHander.getParameterObject();
            BoundSql boundSql = (BoundSql) handler.getValue("boundSql");
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

                        // sql中非数字类型的值加单引号
                        if (value != null && !(value instanceof Number)) {
                            value = "'" + value + "'";
                        }

                        // 替换问号
                        int pos = sb.indexOf("?");
                        sb.replace(pos, pos + 1, String.valueOf(value));
                    }
                }
                String[] split = id.split("\\.");
                String name = split[split.length - 2] + "." + split[split.length - 1];

                String info;
                if (sqlprinterProperties.isFormat()) {
                    try {
                        info = CCJSqlParserUtil.parse(sb.toString()).toString();
                    } catch (JSQLParserException e) {
                        info = sb.toString();
                    }
                } else {
                    info = sb.toString();
                }
                log.info("\r\n===SQL===={}=======>\r\n{}", name, info);
            }
        }
    }
}