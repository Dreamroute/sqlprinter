package com.github.dreamroute.sqlprinter.boot;

import com.github.dreamroute.sqlprinter.starter.anno.EnableSQLPrinter;
import com.github.dreamroute.sqlprinter.starter.converter.def.DateConverter;
import com.github.dreamroute.sqlprinter.starter.converter.def.EnumConverter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author w.dehai
 */
@SpringBootApplication
@MapperScan("com.github.dreamroute.sqlprinter.boot.mapper")
@EnableSQLPrinter(converters = {DateConverter.class, EnumConverter.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
