package com.zyq.service;

import com.zyq.mapper.UserMapper;
import com.zyq.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService{
    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean addUser(User user) {
        log.info("UserServiceImpl=>addUser()");
        return userMapper.addUser(user);
    }

    @Override
    public User getUserByName(String name) {
        log.info("UserServiceImpl=>getUserByName()");
        return userMapper.getUserByName(name);
    }
}
