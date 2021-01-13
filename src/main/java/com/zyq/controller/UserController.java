package com.zyq.controller;

import com.zyq.pojo.User;
import com.zyq.service.UserService;
import com.zyq.service.UserServiceImpl;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import utils.UserIdBuilder;

@RestController
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/add")
    public String addUser(@RequestParam("name") String name,
                          @RequestParam("pwd") String pwd,
                          @RequestParam(name="perm", required=false, defaultValue = "None") String perm){
        String userId = UserIdBuilder.getUUID();
        String userPerm = perm.equals("None")? null:perm;
        String userPwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
        boolean result = userService.addUser(new User(userId, name, userPwd, userPerm));
        return String.valueOf(result);
    }
    @GetMapping("/{name}")
    public String getUserByName(@PathVariable("name")String name){
        return userService.getUserByName(name).toString();
    }
}
