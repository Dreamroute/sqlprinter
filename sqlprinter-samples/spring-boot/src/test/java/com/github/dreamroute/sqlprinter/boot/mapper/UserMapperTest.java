package com.github.dreamroute.sqlprinter.boot.mapper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.dreamroute.sqlprinter.boot.domain.User;
import com.github.dreamroute.sqlprinter.starter.interceptor.SqlPrinter;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.github.dreamroute.sqlprinter.boot.domain.User.Gender.MALE;
import static com.github.dreamroute.sqlprinter.boot.mapper.AppenderUtil.create;
import static com.github.dreamroute.sqlprinter.boot.mapper.AppenderUtil.getMessage;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        ListAppender<ILoggingEvent> appender = create(SqlPrinter.class);
        User user = userMapper.selectById(1L);
        assertEquals("w.dehai", user.getName());
        assertEquals(0, appender.list.size());
    }

    @Test
    void insertTest() throws Exception {
        User user = new User();
        user.setName("Jaedong");
        user.setPassword("123456");
        String time = "2020-01-01 01:01:10.111";
        user.setBirthday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
        user.setGender(MALE);
        ListAppender<ILoggingEvent> appender = create(SqlPrinter.class);
        userMapper.insert(user);
        assertNotNull(user.getId());
        String sql = "insert into smart_user(birthday,password,gender,name,version) VALUES ('" + time + "','123456',1,'Jaedong',null)";
        assertTrue(getMessage(appender, 0).contains(sql));
    }

    @Test
    void updateTest() {
        User user = userMapper.selectById(1L);
        user.setPassword("update");
        ListAppender<ILoggingEvent> appender = create(SqlPrinter.class);
        userMapper.updateById(user);
        userMapper.updateByIdExcludeNull(user);
        assertTrue(getMessage(appender, 0).contains("update smart_user set birthday = null,password = 'update',gender = null,name = 'w.dehai',version = 0 where id = 1"));
        assertTrue(getMessage(appender, 1).contains("update smart_user set  password = 'update',name = 'w.dehai',version = 0  where id = 1"));
    }

    @Test
    void selectUsersTest() {
        ListAppender<ILoggingEvent> appender = create(SqlPrinter.class);
        userMapper.selectUsers();
        String sql = "SELECT\n" +
                "            *\n" +
                "        FROM\n" +
                "            smart_user";
        assertTrue(getMessage(appender, 0).contains(sql));
    }

    @Test
    void selectUserByIdsTest() {
        ListAppender<ILoggingEvent> appender = create(SqlPrinter.class);
        List<User> users = userMapper.selectUserByIds(newArrayList(1L, 2L));
        String sql = "select * from smart_user where id in\n" +
                "         (  \n" +
                "            (1)\n" +
                "         , \n" +
                "            (2)\n" +
                "         )";
        assertTrue(getMessage(appender, 0).contains(sql));
    }

}
