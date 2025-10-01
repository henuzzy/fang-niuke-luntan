package com.yue.nklt.service;

import com.yue.nklt.entity.LoginTicket;
import com.yue.nklt.entity.User;
import com.yue.nklt.mapper.LoginTicketMapper;
import com.yue.nklt.mapper.UserMapper;
import com.yue.nklt.utils.CommunityUtil;
import com.yue.nklt.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务层
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 根据id查询用户
     */
    public User findUserById(Integer id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户注册
     */
    public Map<String, Object> register(String username, String password, String confirmPwd, String email) {
        Map<String, Object> map = new HashMap<>();

        // 验证参数
        if (username == null || username.trim().isEmpty()) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (password == null || password.trim().isEmpty()) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (!password.equals(confirmPwd)) {
            map.put("confirmPwdMsg", "两次输入的密码不一致");
            return map;
        }
        if (email == null || email.trim().isEmpty()) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证账号是否已存在
        User u = userMapper.selectByUsername(username);
        if (u != null) {
            map.put("usernameMsg", "该用户名已被注册");
            return map;
        }

        // 验证邮箱是否已存在
        u = userMapper.selectByEmail(email);
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册用户
        User user = new User();
        user.setUsername(username);
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(MD5Util.md5(password + user.getSalt()));
        user.setEmail(email);
        user.setType(0); // 普通用户
        user.setStatus(0); // 未激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", (int) (Math.random() * 1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        return map;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password, Boolean rememberMe) {
        Map<String, Object> map = new HashMap<>();

        // 验证参数
        if (username == null || username.trim().isEmpty()) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (password == null || password.trim().isEmpty()) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在");
            return map;
        }

        // 验证密码
        password = MD5Util.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        // 记住我：30天，否则12小时
        long expiredSeconds = rememberMe ? 30 * 24 * 3600 * 1000L : 12 * 3600 * 1000L;
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        map.put("expired", loginTicket.getExpired());
        map.put("userId", user.getId());
        map.put("username", user.getUsername());
        map.put("headerUrl", user.getHeaderUrl());

        return map;
    }

    /**
     * 退出登录
     */
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     * 根据ticket查询登录凭证
     */
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 更新头像
     */
    public int updateHeaderUrl(Integer userId, String headerUrl) {
        return userMapper.updateHeaderUrl(userId, headerUrl);
    }

    /**
     * 修改密码
     */
    public Map<String, Object> updatePassword(Integer userId, String oldPassword, String newPassword, String confirmPwd) {
        Map<String, Object> map = new HashMap<>();

        // 验证参数
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            map.put("oldPasswordMsg", "原密码不能为空");
            return map;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            map.put("newPasswordMsg", "新密码不能为空");
            return map;
        }
        if (!newPassword.equals(confirmPwd)) {
            map.put("confirmPwdMsg", "两次输入的新密码不一致");
            return map;
        }

        // 验证原密码
        User user = userMapper.selectById(userId);
        oldPassword = MD5Util.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原密码错误");
            return map;
        }

        // 更新密码
        newPassword = MD5Util.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);

        return map;
    }
}

