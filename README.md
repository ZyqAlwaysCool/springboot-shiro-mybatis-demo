## springboot-shiro-mybatis-demo
### shiro
* [Shiro安全框架简介](https://www.jianshu.com/p/7a64c3f3b47d)
* 导入spring整合shiro依赖
```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.7.0</version>
</dependency>
```
### mysql
* 导入驱动
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```
* sql
```sql
create database `shiro-demo`;
use `shiro-demo`;

create table user(
    `id` varchar(100) not null,
    `name` varchar(20) not null,
    `pwd` varchar(100) not null,
    `perm` varchar(20) default null,
    primary key (`id`)
)engine=innodb charset=utf8;
```
### mybatis
* 导入依赖
```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.4</version>
</dependency>
```
* springboot-mybatis
```yml
mybatis:
  config-location: "classpath:mybatis/mapper/*.xml"
  type-aliases-package: "com.zyq.pojo"
```
* mapper
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.zyq.mapper.UserMapper">
    <select id="getUserByName" resultType="User">
        select * from user 
        where user.name = #{name}
    </select>
    <insert id="addUser">
        insert into user(`id`, `name`, `pwd`, `perm`)
        values (#{id}, #{name}, #{pwd}, #{perm})
    </insert>
</mapper>
```
### demo-api
* `/login`:登录
* `/logout`:登出
* `/noauth`:无授权跳转
* `/user/add`:增加用户,需要有user:add权限
* `/user/{name}`:通过用户名称查询用户,需要经过认证
### demo-pojo
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class User {
    private String id;
    private String name;
    private String pwd;
    private String perm;
}
```
### demo
* `ShiroConfig.java`：进行Shiro安全框架配置
    1. 自定义realm对象: `com.zyq.pojo.UserRealm`
    2. 创建WebSecurityManager, 装载realm对象
    3. 创建ShiroFilterFactoryBean, 装载WebSecurityManager
* `UserRealm.java`: 认证和授权操作
    * 认证：`doGetAuthenticationInfo`
    * 授权：`doGetAuthorizationInfo`
* 所有`/user/*`都需要通过认证，未经认证会跳转至`/login`，未经授权会跳转至`/noauth`
* `/user/add`操作需要授权权限，通过数据库中的perm字段进行授权
* demo数据:user无add权限，root拥有add权限
    * `User(id=ad27eabe7bcd4fe8b8f63946e891d810, name=user, pwd=ee11cbb19052e40b07aac0ca060c23ee, perm=null)`
    * `User(id=6126e967aa934242921b7791ce6c0f25, name=root, pwd=63a9f0ea7bb98050796b649e85481845, perm=user:add)`

```java

/**
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {
    // 1: 创建realm对象
    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }
    // 2: WebSecurityManager
    @Bean("securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }
    // 3: ShiroFilterFactoryBean
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager dwsm){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(dwsm);

        //添加shiro内置过滤器
        /*
            anon: 无需认证可以访问
            authc: 必须认证才能够访问
            user: 必须拥有【记住我】功能才能使用
            perms: 拥有对某个资源的权限才能访问
            roles: 拥有某个角色权限才能访问
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/user/add", "perms[user:add]");
        filterMap.put("/user/*", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        //设置登录请求
        shiroFilterFactoryBean.setLoginUrl("/login");
        //未授权跳转
        shiroFilterFactoryBean.setUnauthorizedUrl("/noauth");
        return shiroFilterFactoryBean;
    }
}
```

```java

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

```

