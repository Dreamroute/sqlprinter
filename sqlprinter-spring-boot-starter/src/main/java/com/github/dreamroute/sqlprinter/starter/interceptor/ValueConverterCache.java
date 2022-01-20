package com.github.dreamroute.sqlprinter.starter.interceptor;

import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 描述：转换器缓存，由于引入了SPI，为了提升性能这里引入缓存
 *
 * @author w.dehi.2022-01-20
 */
public class ValueConverterCache {
    private ValueConverterCache() {}

    static Set<ValueConverter> CONVERTERS = new HashSet<>();

    static {
        ServiceLoader<ValueConverter> converters = ServiceLoader.load(ValueConverter.class);
        converters.forEach(CONVERTERS::add);
    }

}
