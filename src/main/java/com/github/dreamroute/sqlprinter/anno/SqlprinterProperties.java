package com.github.dreamroute.sqlprinter.anno;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author w.dehai
 */
@ConfigurationProperties(prefix = "sqlprinter")
public class SqlprinterProperties {

    /**
     * 是否显示SQL，默认显示
     */
    private boolean sqlShow = true;

    public boolean isSqlShow() {
        return sqlShow;
    }

    public void setSqlShow(boolean sqlShow) {
        this.sqlShow = sqlShow;
    }
}
