package com.github.dreamroute.sqlprinter.boot.mapper;

import com.github.dreamroute.sqlprinter.boot.domain.EnumDomain;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static com.github.dreamroute.sqlprinter.boot.domain.EnumDomain.Gender.FEMALE;
import static com.github.dreamroute.sqlprinter.boot.domain.EnumDomain.Gender.MALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author w.dehai
 */
@SpringBootTest
class EnumDomainMapperTest {

    @Autowired
    private EnumDomainMapper enumDomainMapper;

    @Resource
    private DataSource dataSource;

    @BeforeEach
    void beforeEach() {
        new DbSetup(new DataSourceDestination(dataSource), Operations.truncate("smart_typehandler")).launch();
        Insert insert = Operations.insertInto("smart_typehandler")
                .columns("id", "gender")
                .values(1L, 1)
                .values(2L, 2).build();
        new DbSetup(new DataSourceDestination(dataSource), insert).launch();
    }

    @Test
    void selectByIdTest() {
        EnumDomain ed = enumDomainMapper.selectById(1L);
        Assertions.assertEquals(MALE, ed.getGender());
    }

    @Test
    void insertTest() {
        EnumDomain ed = new EnumDomain();
        ed.setGender(MALE);
        enumDomainMapper.insert(ed);
        assertNotNull(ed.getId());
    }

    @Test
    void updateTest() {
        EnumDomain ed = enumDomainMapper.selectById(1L);
        ed.setGender(FEMALE);
        enumDomainMapper.updateById(ed);
        EnumDomain result = enumDomainMapper.selectById(1L);
        assertEquals(FEMALE, result.getGender());
    }

    @Test
    void deleteTest() {
        int result = enumDomainMapper.deleteById(1L);
        assertEquals(1, result);
    }

}
