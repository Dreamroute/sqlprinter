package com.github.dreamroute.sqlprinter.starter.anno;

import cn.hutool.core.util.ReflectUtil;
import com.github.dreamroute.sqlprinter.starter.interceptor.SqlPrinter;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 初始化插件配置信息
 *
 * @author w.dehai
 */
@Configuration
@EnableConfigurationProperties(SqlprinterProperties.class)
public class SQLPrinterConfig implements ApplicationContextAware {

    private final List<ValueConverter> convs = new ArrayList<>();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        Collection<Object> values = context.getBeansWithAnnotation(EnableSQLPrinter.class).values();
        values.forEach(e -> {
            EnableSQLPrinter annotation = AnnotationUtils.findAnnotation(e.getClass(), EnableSQLPrinter.class);

            // 转换器
            Class<? extends ValueConverter>[] converters = annotation.converters();
            for (Class<? extends ValueConverter> converter : converters) {
                convs.add(ReflectUtil.newInstance(converter));
            }
        });
    }

    @Bean
    public SqlPrinter sqlPrinter(SqlprinterProperties sqlprinterProperties) {
        return new SqlPrinter(sqlprinterProperties, convs);
    }

}
