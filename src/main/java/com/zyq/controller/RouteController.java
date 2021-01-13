package com.zyq.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteController {
    @GetMapping("/login")
    public String toLogin(@RequestParam("name") String name,
                          @RequestParam("pwd") String pwd) {
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, DigestUtils.md5DigestAsHex(pwd.getBytes()));
        try {
            currentUser.login(token);
        } catch (UnknownAccountException e) {
           return "未知用户";
        }
        return "login_success";
    }
    @GetMapping("/noauth")
    public String noAuth(){
        return "no_auth";
    }
    @GetMapping("/logout")
    public String logout(){
        Subject currentUser = SecurityUtils.getSubject();
        //获取用户信息getPrincipal()
        System.out.println("当前登出用户=>"+currentUser.getPrincipal());
        currentUser.logout();
        return "log_out";
    }
}
