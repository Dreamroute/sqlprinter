package com.github.dreamroute.sqlprinter.boot.mapper;

import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.ServiceLoader;

/**
 * @author w.dehai.2021/9/7.15:26
 */
@SpringBootTest
class SpiTest {

    @Test
    void spi() {
        Object date = new Date();
        System.err.println(date);
        ServiceLoader<ValueConverter> converters = ServiceLoader.load(ValueConverter.class);
        for (ValueConverter converter : converters) {
            date = converter.convert(date);
        }
        System.err.println(date);
    }

}
