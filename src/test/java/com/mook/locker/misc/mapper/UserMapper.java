package com.mook.locker.misc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mook.locker.misc.domain.User;

public interface UserMapper {
	
	Integer insertUser(User user);
	
	Integer updateUser(User user);
	
	Integer deleteUser(Integer id);
	
	User selectUserById(Integer id);
	
	List<User> selectAllUser();
	
	User selectUserByNameAndPassword(@Param("name") String name, @Param("password") String password);
}
