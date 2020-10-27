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
    private boolean sqlShow = true;

    /**
     * 配置不需要打印SQL的mapper方法名
     */
    private String[] filter;
}
