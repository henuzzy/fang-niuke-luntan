package com.yue.nklt.mapper;

import com.yue.nklt.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /**
     * 根据id查询用户
     */
    User selectById(Integer id);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(String email);

    /**
     * 插入用户
     */
    int insertUser(User user);

    /**
     * 更新用户状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 更新用户头像
     */
    int updateHeaderUrl(@Param("id") Integer id, @Param("headerUrl") String headerUrl);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
}

