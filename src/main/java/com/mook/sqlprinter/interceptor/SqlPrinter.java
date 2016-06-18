/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.mook.sqlprinter.interceptor;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
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

import com.mook.sqlprinter.util.PluginUtil;

/**
 * print imple sql
 * 
 * @author 342252328@qq.com
 * @date 2016-06-14
 * @version 1.0
 * @since JDK1.7
 *
 */
@Intercepts({ @Signature(type = ParameterHandler.class, method = "setParameters", args = { PreparedStatement.class }) })
public class SqlPrinter implements Interceptor {
	
	private static final Log log = LogFactory.getLog(SqlPrinter.class);
	private Properties props = new Properties();

	@Override
	public Object intercept(Invocation invocation) throws Exception {
		
		// invoke the original setParameters method
		Object result = invocation.proceed();
		
		// Print the simple SQL
		printSql(invocation);
		
		return result;
	}

	private void printSql(Invocation invocation) {
		Object parameterHander =  invocation.getTarget();
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
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
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
					int pos = sb.indexOf("?");
					sb.replace(pos, pos + 1, String.valueOf(value));
				}
			}
		}
		
		String type = props.getProperty("type", "debug");
		if("error".equals(type)) {
			System.err.println("==>  Simple Sql: " + sb.toString());
		} else {
		if (log.isDebugEnabled()) log.debug("==>  Print simple sql: " + sb.toString());
		}
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof ParameterHandler) {
			target = Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {
		props = properties;
	}
}