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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static java.lang.String.join;
import static java.util.Optional.ofNullable;

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
    public void setApplicationContext(@Nonnull ApplicationContext context) throws BeansException {
        Collection<Object> values = context.getBeansWithAnnotation(EnableSQLPrinter.class).values();
        values.forEach(e -> {
            EnableSQLPrinter annotation = AnnotationUtils.findAnnotation(e.getClass(), EnableSQLPrinter.class);
            Class<? extends ValueConverter>[] converters = annotation.converters();
            for (Class<? extends ValueConverter> converter : converters) {
                convs.add(ReflectUtil.newInstance(converter));
            }
        });
    }

    @Bean
    public SqlPrinter sqlPrinter(SqlprinterProperties sqlprinterProperties) {
        Properties props = new Properties();
        props.setProperty("show", String.valueOf(sqlprinterProperties.isShow()));
        String[] filter = ofNullable(sqlprinterProperties.getFilter()).orElseGet(() -> new String[0]);
        String result = join(",", filter);
        props.setProperty("filter", result);
        return new SqlPrinter(props, convs);
    }

}
