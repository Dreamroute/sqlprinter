package com.github.dreamroute.sqlprinter.starter.anno;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author w.dehai
 */
@Data
@ConfigurationProperties(prefix = "sqlprinter")
public class SqlprinterProperties {

    /**
     * 是否显示SQL，默认显示
     */
    private boolean show = true;

    /**
     * 是否显示查询结果表格
     */
    private boolean showResult = false;

    /**
     * 结果表格中不打印的字段, 默认全部打印
     */
    private String[] showResultExclude = {}; 

    /**
     * 是否格式化SQL，默认格式化
     */
    private boolean format = true;

    /**
     * 配置不需要打印SQL的mapper方法名
     */
    private String[] filter;
}
