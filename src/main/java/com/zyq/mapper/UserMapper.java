package com.zyq.mapper;

import com.zyq.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository //spring托管
public interface UserMapper {
    boolean addUser(User user);
    User getUserByName(@Param("name") String name);
}
