package com.example.demo.service;

import com.example.demo.entity.User;

/**
 * @author ly
 * @since 2021/5/13
 */
public interface IUserService {

    User selectUserById(int id);

    boolean login(User user);

    boolean register(User user);
}
