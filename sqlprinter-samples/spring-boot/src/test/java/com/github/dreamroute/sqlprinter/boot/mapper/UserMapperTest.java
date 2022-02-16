package com.github.dreamroute.sqlprinter.boot.mapper;

import com.github.dreamroute.sqlprinter.boot.domain.User;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import static com.github.dreamroute.sqlprinter.boot.domain.User.Gender.MALE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author w.dehai
 */
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private DataSource dataSource;

    @BeforeEach
    void beforeEach() {
        new DbSetup(new DataSourceDestination(dataSource), Operations.truncate("smart_user")).launch();
        Insert insert = Operations.insertInto("smart_user")
                .columns("id", "name")
                .values(1L, "w.dehai")
                .values(2L, "Dreamroute").build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

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
        user.setBirthday(new Date());
        user.setGender(MALE);
        userMapper.insert(user);
        assertNotNull(user.getId());
    }

    @Test
    void updateTest() {
        User user = userMapper.selectById(1L);
        user.setPassword("update");
        userMapper.updateById(user);
        userMapper.updateByIdExcludeNull(user);
    }

    @Test
    void selectUsersTest() {
        userMapper.selectUsers();
    }

    @Test
    void selectUserByIdsTest() {
        List<User> users = userMapper.selectUserByIds(newArrayList(1L, 2L));
        System.err.println(users);
    }

}
