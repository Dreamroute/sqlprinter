package com.github.dreamroute.sqlprinter.boot.converters;

import com.github.dreamroute.mybatis.pro.base.EnumMarker;
import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;

/**
 * @author w.dehai.2021/9/7.15:51
 */
public class EnumConverter implements ValueConverter {
    @Override
    public Object convert(Object value) {
        if (value instanceof EnumMarker) {
            value = ((EnumMarker) value).getValue();
        }
        return value;
    }
}
