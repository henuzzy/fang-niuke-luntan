package com.yue.nklt.controller;

import com.yue.nklt.dto.PageDTO;
import com.yue.nklt.dto.PostVO;
import com.yue.nklt.service.DiscussPostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 首页控制器
 */
@Controller
public class IndexController {

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(required = false) String order) {
        // 默认按最新排序
        if (order == null || order.isEmpty()) {
            order = "latest";
        }
        
        PageDTO<PostVO> pageDTO = discussPostService.findDiscussPosts(page, pageSize, order);
        model.addAttribute("page", pageDTO);
        model.addAttribute("order", order);
        return "index";
    }

    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpSession session) throws IOException {
        // 生成验证码
        String code = generateCode();
        session.setAttribute("captcha", code);
        
        // 生成图片
        BufferedImage image = new BufferedImage(100, 40, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 100, 40);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(code, 20, 25);
        g.dispose();
    }

    /**
     * 生成随机验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}

