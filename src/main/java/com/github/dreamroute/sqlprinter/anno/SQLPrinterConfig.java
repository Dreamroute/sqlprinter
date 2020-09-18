package com.github.dreamroute.sqlprinter.anno;

import com.github.dreamroute.sqlprinter.interceptor.SqlPrinter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

/**
 * @author w.dehai
 */
@EnableConfigurationProperties(SqlprinterProperties.class)
public class SQLPrinterConfig {


    private SqlprinterProperties sqlprinterProperties;

    public SQLPrinterConfig(SqlprinterProperties sqlprinterProperties) {
        this.sqlprinterProperties = sqlprinterProperties;
    }

    @Bean
    public SqlPrinter sqlPrinter() {
        SqlPrinter printer = new SqlPrinter();
        Properties props = new Properties();
        props.setProperty("sql-show", String.valueOf(sqlprinterProperties.isSqlShow()));
        printer.setProperties(props);
        return printer;
    }
}
