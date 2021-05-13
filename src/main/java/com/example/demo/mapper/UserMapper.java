package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author ly
 * @since 2021/5/13
 */

@Mapper
public interface UserMapper {
    @Select("select * from user where id = #{id}")
    public User selectUserById(int id);

    @Select("select * from user where userName = #{name}")
    public User selectUserByName(String name);

    @Insert("insert into user(username,password) values(#{userName},#{password})")
    public int insertUser(User user);
}