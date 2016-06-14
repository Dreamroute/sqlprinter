package com.mook.locker.misc.test.mapper;

import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mook.locker.misc.domain.User;
import com.mook.locker.misc.mapper.UserMapper;

public class UserMapperTest {
	
	private static UserMapper userMapper = null;
	
	@BeforeClass
	public static void doInitTest() throws Exception {
		InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession sqlSession = sqlSessionFactory.openSession(true);
		userMapper = sqlSession.getMapper(UserMapper.class);
	}
	
	@Test
	public void insertUserTest() {
		User user = new User("test", "test", 10L);
		Integer result = userMapper.insertUser(user);
		System.err.println(result);
	}
	
	@Test
	public void updateUserTest() {
		User user = new User(100, "kk", "kk", 100L);
		Integer result = userMapper.updateUser(user);
		System.err.println(result);
	}
	
	@Test
	public void deleteUserTest() {
		Integer result = userMapper.deleteUser(201);
		System.err.println(result);
	}
	
	@Test
	public void selectUserByIdTest() {
		User user = userMapper.selectUserById(100);
		System.err.println(user);
	}
	
	@Test
	public void selectAllUserTest() {
		List<User> result = userMapper.selectAllUser();
		System.err.println(result);
	}
}







