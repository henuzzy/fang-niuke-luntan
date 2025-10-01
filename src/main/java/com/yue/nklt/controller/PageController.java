package com.yue.nklt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面跳转控制器
 */
@Controller
public class PageController {

    /**
     * 发现页面
     */
    @GetMapping("/discover")
    public String discover() {
        return "discover";
    }

    /**
     * 话题页面
     */
    @GetMapping("/topics")
    public String topics() {
        return "topics";
    }

    /**
     * 课程页面
     */
    @GetMapping("/courses")
    public String courses() {
        return "courses";
    }

    /**
     * 个人主页
     */
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    /**
     * 账号设置
     */
    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }
}

