package com.github.dreamroute.sqlprinter.boot.mapper;

import com.github.dreamroute.mybatis.pro.service.mapper.BaseMapper;
import com.github.dreamroute.sqlprinter.boot.domain.User;

import java.util.List;

/**
 * @author w.dehai
 */
public interface UserMapper extends BaseMapper<User, Long> {
    List<User> selectUsers();
    List<User> selectUserByIds(List<Long> ids);
}
