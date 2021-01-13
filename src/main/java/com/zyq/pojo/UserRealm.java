package com.zyq.pojo;

import com.zyq.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义的UserRealm
 */
public class UserRealm extends AuthorizingRealm {
    private static Logger log = LoggerFactory.getLogger(UserRealm.class);
    @Autowired
    private UserService userService;
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.warn("执行认证=>doGetAuthenticationInfo");
        UsernamePasswordToken userToken = (UsernamePasswordToken)authenticationToken;
        User user = userService.getUserByName(userToken.getUsername());
        log.warn("当前执行认证用户=>"+user);
        if(user == null){
            return null;
        }else {
            return new SimpleAuthenticationInfo(user, user.getPwd(), "");
        }
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.warn("执行授权=>doGetAuthorizationInfo");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User)subject.getPrincipal();
        log.warn("当前执行授权用户=>"+currentUser);
        info.addStringPermission(currentUser.getPerm());
        return info;
    }
}
