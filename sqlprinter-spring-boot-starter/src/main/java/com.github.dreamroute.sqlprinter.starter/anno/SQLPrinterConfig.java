package com.github.dreamroute.sqlprinter.starter.anno;

import com.github.dreamroute.sqlprinter.starter.interceptor.SqlPrinter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Properties;
import java.util.logging.Filter;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

/**
 * @author w.dehai
 */
@EnableConfigurationProperties(SqlprinterProperties.class)
public class SQLPrinterConfig {


    private final SqlprinterProperties sqlprinterProperties;

    public SQLPrinterConfig(SqlprinterProperties sqlprinterProperties) {
        this.sqlprinterProperties = sqlprinterProperties;
    }

    @Bean
    public SqlPrinter sqlPrinter() {
        SqlPrinter printer = new SqlPrinter();
        Properties props = new Properties();
        props.setProperty("sql-show", String.valueOf(sqlprinterProperties.isSqlShow()));
        String[] filter = ofNullable(sqlprinterProperties.getFilter()).orElseGet(() -> new String[0]);
        String result = stream(filter).collect(joining(","));
        props.setProperty("filter", result);
        printer.setProperties(props);
        return printer;
    }
}
