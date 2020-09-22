package com.github.dreamroute.sqlprinter.boot.mapper;

import com.github.dreamroute.sqlprinter.boot.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author w.dehai
 */
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void selectByIdTest() {
        User user = userMapper.selectById(1L);
        assertEquals("w.dehai", user.getName());
    }

    @Test
    void insertTest() {
        User user = new User();
        user.setName("Jaedong");
        user.setPassword("123456");
        userMapper.insert(user);
        assertNotNull(user.getId());
    }

    @Test
    void updateTest() {
        User user = userMapper.selectById(1L);
        user.setPassword("update");
    }

}
