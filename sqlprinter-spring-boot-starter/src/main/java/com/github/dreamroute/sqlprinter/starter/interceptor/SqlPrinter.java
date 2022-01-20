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

import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;
import com.github.dreamroute.sqlprinter.starter.util.PluginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * print simple sql
 *
 * @author 342252328@qq.com.2016-06-14
 * @version 1.0
 * @since JDK1.8
 */
@Slf4j
@RequiredArgsConstructor
@Intercepts({@Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})})
public class SqlPrinter implements Interceptor {

    private final Properties props;
    private final List<ValueConverter> converters;

    @Override
    public Object intercept(Invocation invocation) throws Exception {

        // invoke the original setParameters method
        Object result = invocation.proceed();

        // Print the simple SQL
        printSql(invocation);

        return result;
    }

    private void printSql(Invocation invocation) {
        String show = props.getProperty("show", "true");
        String filter = props.getProperty("filter");
        Map<String, String> methodNames = stream(ofNullable(filter).orElseGet(String::new).split(",")).collect(toMap(identity(), t -> ""));
        String methodName = null;
        ParameterHandler parameterHander = (ParameterHandler) PluginUtil.processTarget(invocation.getTarget());
        try {
            Field mappedStatement = DefaultParameterHandler.class.getDeclaredField("mappedStatement");
            mappedStatement.setAccessible(true);
            MappedStatement ms = (MappedStatement) mappedStatement.get(parameterHander);
            methodName = ms.getId();
        } catch (Exception e) {
            // ignore.
        }
        if (Boolean.parseBoolean(show) && !methodNames.containsKey(methodName)) {
            Object target = PluginUtil.processTarget(parameterHander);

            MetaObject handler = SystemMetaObject.forObject(target);
            Object parameterObject = handler.getValue("parameterObject");
            BoundSql boundSql = (BoundSql) handler.getValue("boundSql");
            String originalSql = boundSql.getSql();
            StringBuilder sb = new StringBuilder(originalSql);

            MappedStatement mappedStatement = (MappedStatement) handler.getValue("mappedStatement");
            ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings != null) {
                long versionValue = 0;
                for (ParameterMapping parameterMapping : parameterMappings) {
                    if (parameterMapping.getMode() != ParameterMode.OUT) {
                        Object value;
                        String propertyName = parameterMapping.getProperty();
                        if (boundSql.hasAdditionalParameter(propertyName)) {
                            value = boundSql.getAdditionalParameter(propertyName);
                        } else if (parameterObject == null) {
                            value = null;
                        } else if (mappedStatement.getConfiguration().getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                            value = parameterObject;
                        } else {
                            MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
                            value = metaObject.getValue(propertyName);
                        }

                        // 转换器
                        for (ValueConverter vc : converters) {
                            value = vc.convert(value);
                        }

                        // 将set中的version减1得到where后面的version的值
                        if (value != null && Objects.equals(propertyName, "version")) {
                            versionValue = (long) value - 1;
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
                String result = sb.toString().replace("version = ?", "version = " + versionValue);
                String id = mappedStatement.getId();
                String[] split = id.split("\\.");
                String name = split[split.length - 2] + "." + split[split.length - 1];
                log.info("\r\n===SQL===={}=======>\r\n{}", name, result);
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof ParameterHandler) {
            target = Plugin.wrap(target, this);
        }
        return target;
    }
}