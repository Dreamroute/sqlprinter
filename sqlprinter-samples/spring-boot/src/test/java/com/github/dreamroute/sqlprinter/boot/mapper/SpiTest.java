package com.github.dreamroute.sqlprinter.boot.mapper;

import com.github.dreamroute.sqlprinter.boot.domain.User;
import com.github.dreamroute.sqlprinter.starter.anno.ValueConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.ServiceLoader;

import static com.github.dreamroute.sqlprinter.boot.domain.User.Gender.FEMALE;

/**
 * @author w.dehai.2021/9/7.15:26
 */
@SpringBootTest
class SpiTest {

    @Resource
    private UserMapper userMapper;

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

    /**
     * 观察输出的SQL语句中的birthday和gender是否是预期的值
     */
    @Test
    void enumTest() {
        User user = new User();
        user.setBirthday(new Date());
        user.setGender(FEMALE);
        userMapper.insert(user);
    }

}
