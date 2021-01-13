package com.zyq.service;

import com.zyq.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    boolean addUser(User user);

    User getUserByName(String name);
}
