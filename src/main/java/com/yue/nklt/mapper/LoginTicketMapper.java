package com.yue.nklt.mapper;

import com.yue.nklt.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 登录凭证Mapper接口
 */
@Mapper
public interface LoginTicketMapper {
    /**
     * 插入登录凭证
     */
    int insertLoginTicket(LoginTicket ticket);

    /**
     * 根据ticket查询登录凭证
     */
    LoginTicket selectByTicket(String ticket);

    /**
     * 更新登录凭证状态
     */
    int updateStatus(@Param("ticket") String ticket, @Param("status") Integer status);
}

