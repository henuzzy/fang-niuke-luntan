package com.yue.nklt.controller;

import com.yue.nklt.dto.*;
import com.yue.nklt.entity.User;
import com.yue.nklt.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegisterDTO registerDTO, BindingResult bindingResult) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getFieldError().getDefaultMessage());
        }

        Map<String, Object> map = userService.register(
                registerDTO.getUsername(),
                registerDTO.getPassword(),
                registerDTO.getConfirmPwd(),
                registerDTO.getEmail()
        );

        if (map.isEmpty()) {
            return Result.ok("注册成功", null);
        } else {
            String msg = map.values().iterator().next().toString();
            return Result.fail(msg);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@Valid @RequestBody UserLoginDTO loginDTO, 
                       BindingResult bindingResult,
                       HttpSession session) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getFieldError().getDefaultMessage());
        }

        // 验证验证码
        String captcha = (String) session.getAttribute("captcha");
        if (captcha == null || !captcha.equalsIgnoreCase(loginDTO.getCaptcha())) {
            return Result.fail("验证码不正确");
        }

        Map<String, Object> map = userService.login(
                loginDTO.getUsername(),
                loginDTO.getPassword(),
                loginDTO.getRememberMe()
        );

        if (map.containsKey("ticket")) {
            Map<String, Object> data = new HashMap<>();
            data.put("ticket", map.get("ticket"));
            data.put("expired", map.get("expired"));
            data.put("userId", map.get("userId"));
            data.put("username", map.get("username"));
            data.put("headerUrl", map.get("headerUrl"));
            return Result.ok("登录成功", data);
        } else {
            String msg = map.values().iterator().next().toString();
            return Result.fail(msg);
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result logout(@RequestParam String ticket) {
        userService.logout(ticket);
        return Result.ok("退出成功", null);
    }

    /**
     * 查看个人主页
     */
    @GetMapping("/{userId}/profile")
    public Result getProfile(@PathVariable Integer userId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("headerUrl", user.getHeaderUrl());
        data.put("createTime", user.getCreateTime());
        // TODO: 统计帖子数、评论数、点赞数、关注数、粉丝数
        data.put("followeeCount", 5);  // 关注的人数
        data.put("followerCount", 123); // 粉丝数
        data.put("likeCount", 87);      // 获得的赞数
        data.put("hasFollowed", false); // 当前用户是否已关注

        return Result.ok(data);
    }

    /**
     * 关注用户
     */
    @PostMapping("/follow/{userId}")
    public Result follow(@PathVariable Integer userId, HttpSession session) {
        // TODO: 从session或token中获取当前登录用户ID
        // Integer currentUserId = ...
        // userService.follow(currentUserId, userId);
        return Result.ok("关注成功", null);
    }

    /**
     * 取消关注用户
     */
    @PostMapping("/unfollow/{userId}")
    public Result unfollow(@PathVariable Integer userId, HttpSession session) {
        // TODO: 从session或token中获取当前登录用户ID
        // Integer currentUserId = ...
        // userService.unfollow(currentUserId, userId);
        return Result.ok("取消关注成功", null);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password/{id}")
    public Result updatePassword(@PathVariable Integer id,
                                 @Valid @RequestBody PasswordUpdateDTO updateDTO,
                                 BindingResult bindingResult) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getFieldError().getDefaultMessage());
        }

        Map<String, Object> map = userService.updatePassword(
                id,
                updateDTO.getOldPassword(),
                updateDTO.getNewPassword(),
                updateDTO.getConfirmPwd()
        );

        if (map.isEmpty()) {
            return Result.ok("密码修改成功，请重新登录", null);
        } else {
            String msg = map.values().iterator().next().toString();
            return Result.fail(msg);
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar/{id}")
    public Result uploadAvatar(@PathVariable Integer id, @RequestParam String headerUrl) {
        userService.updateHeaderUrl(id, headerUrl);
        Map<String, Object> data = new HashMap<>();
        data.put("headerUrl", headerUrl);
        return Result.ok("头像上传成功", data);
    }
}

