package com.github.dreamroute.sqlprinter.starter.converter.def;

import com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarker;
import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;

/**
 * 枚举转换器
 *
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
