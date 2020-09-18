package com.github.dreamroute.sqlprinter.anno;

import com.github.dreamroute.sqlprinter.interceptor.SqlPrinter;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

/**
 * @author w.dehai
 */
public class SQLPrinterConfig {
    @Bean
    public SqlPrinter sqlPrinter() {
        SqlPrinter printer = new SqlPrinter();
        Properties props = new Properties();
        props.setProperty("sql-show", "true");
        printer.setProperties(props);
        return printer;
    }
}
