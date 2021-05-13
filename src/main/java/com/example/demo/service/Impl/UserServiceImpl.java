package com.example.demo.service.Impl;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * @author ly
 * @since 2021/5/13
 */
@Service("iUserService")
@ComponentScan
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectUserById(int id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public boolean login(User user) {
        String userName = user.getName();
        String password = user.getPassword();
        User u1 = userMapper.selectUserByName(userName);
        if (u1 == null) {
            return false;
        } else {
            if (u1.getPassword().equals(password)) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean register(User user) {
        int x = userMapper.insertUser(user);
        if (x > 0) {
            return true;
        } else {
            return false;
        }
    }

}