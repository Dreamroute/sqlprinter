package com.github.dreamroute.sqlprinter.starter.anno;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.dreamroute.sqlprinter.starter.anno.DbType.MySQL;


/**
 * @author w.dehai
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SQLPrinterConfig.class)
public @interface EnableSQLPrinter {

    /**
     * SQL打印时候的值转换工具，比如属性值是Date类型，而在sql中希望展示成yyyy-MM-dd HH:mm:ss类型，那么就把转换工具配置在此
     */
    Class<? extends ValueConverter>[] converters() default {};

    /**
     * 数据库类型，根据数据库类型格式化打印SQL
     *
     * @return 返回数据库类型
     */
    DbType dbType() default MySQL;
}
