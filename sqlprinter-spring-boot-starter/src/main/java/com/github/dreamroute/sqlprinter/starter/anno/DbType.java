package com.github.dreamroute.sqlprinter.starter.anno;

/**
 * 描述：数据库类型，根据数据库类型格式化打印SQL
 *
 * @author w.dehai.2023/6/14.11:09
 */
public enum DbType {

    /**
     * mysql, 默认
     */
    MySQL,

    /**
     * Oracle
     */
    Oracle,

    /**
     * SqlServer
     */
    SqlServer,

    /**
     * PG
     */
    PG;
}
