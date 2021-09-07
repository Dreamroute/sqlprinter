package com.github.dreamroute.sqlprinter.boot.converters;

import cn.hutool.core.date.DateUtil;
import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;

import java.util.Date;

/**
 * @author w.dehai.2021/9/7.15:35
 */
public class DateConverter implements ValueConverter {
    @Override
    public Object convert(Object value) {
        if (value instanceof Date) {
            value = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:sss.SSS");
        }
        return value;
    }
}
