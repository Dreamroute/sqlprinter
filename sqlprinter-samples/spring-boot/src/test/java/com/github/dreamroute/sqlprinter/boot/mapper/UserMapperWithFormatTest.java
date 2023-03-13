package com.github.dreamroute.sqlprinter.boot.mapper;

import com.github.dreamroute.common.util.test.Appender;
import com.github.dreamroute.sqlprinter.boot.domain.User;
import com.github.dreamroute.sqlprinter.starter.anno.EnableSQLPrinter;
import com.github.dreamroute.sqlprinter.starter.anno.SqlprinterProperties;
import com.github.dreamroute.sqlprinter.starter.converter.def.DateConverter;
import com.github.dreamroute.sqlprinter.starter.converter.def.EnumConverter;
import com.github.dreamroute.sqlprinter.starter.interceptor.SqlPrinter;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;

import static com.github.dreamroute.sqlprinter.boot.domain.User.Gender.MALE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author w.dehai
 */
@SpringBootTest
@EnableSQLPrinter(converters = {DateConverter.class, EnumConverter.class})
class UserMapperWithFormatTest {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DataSource dataSource;
    @Resource
    private SqlprinterProperties sqlprinterProperties;

    private boolean format;

    @BeforeEach
    void beforeEach() {
        new DbSetup(new DataSourceDestination(dataSource), Operations.truncate("smart_user")).launch();
        Insert insert = Operations.insertInto("smart_user")
                .columns("id", "name")
                .values(1L, "w.dehai")
                .values(2L, "Dreamroute").build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();

        // 手动设置格式化
        format = sqlprinterProperties.isFormat();
        sqlprinterProperties.setFormat(false);
    }

    @AfterEach
    void afterEach() {
        // 手动还原格式化参数
        sqlprinterProperties.setFormat(format);
    }

    @Test
    void insertTest() throws Exception {
        User user = new User();
        user.setName("Jaedong");
        user.setPassword("123456");
        String time = "2020-01-01 01:01:10.111";
        user.setBirthday(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(time));
        user.setGender(MALE);
        Appender appender = new Appender(SqlPrinter.class);
        userMapper.insert(user);
        assertNotNull(user.getId());
        String sql = "insert into smart_user(birthday,password,gender,name,version) VALUES ('2020-01-01 01:01:10.111','123456',1,'Jaedong',null)";
        assertTrue(appender.contains(sql));
    }

    @Test
    void updateTest() {
        User user = userMapper.selectById(1L);
        user.setPassword("update");
        Appender appender = new Appender(SqlPrinter.class);
        userMapper.updateById(user);
        userMapper.updateByIdExcludeNull(user);
        assertTrue(appender.contains("update smart_user set birthday = null,password = 'update',gender = null,name = 'w.dehai',version = 0 where id = 1"));
        assertTrue(appender.contains(1, "update smart_user set  password = 'update',name = 'w.dehai',version = 0  where id = 1"));
    }

    @Test
    void selectUsersTest() {
        Appender appender = new Appender(SqlPrinter.class);
        userMapper.selectUsers();
        String sql = "select * from smart_user";
        assertTrue(appender.contains(sql));
    }

    @Test
    void selectUserByIdsTest() {
        Appender appender = new Appender(SqlPrinter.class);
        userMapper.selectUserByIds(newArrayList(1L, 2L));
        String sql = "select * from smart_user where id in\n" +
                "         (  \n" +
                "            (1)\n" +
                "         , \n" +
                "            (2)\n" +
                "         )";
        assertTrue(appender.contains(sql));
    }

}
