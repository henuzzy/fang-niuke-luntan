package com.yue.nklt.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yue.nklt.dto.Result;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 验证码控制器
 */
@RestController
@RequestMapping("/api")
public class CaptchaController {

    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 图片宽高
        int width = 120;
        int height = 40;

        // 创建图片对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, width, height);

        // 生成随机验证码（4位数字+字母）
        String chars = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 24));

        // 绘制验证码字符
        for (int i = 0; i < 4; i++) {
            // 随机选择字符
            char c = chars.charAt(random.nextInt(chars.length()));
            captcha.append(c);

            // 随机颜色
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));

            // 随机旋转角度
            int degree = random.nextInt(30) - 15;
            double theta = degree * Math.PI / 180;

            // 字符位置
            int x = 20 + i * 25;
            int y = 28;

            // 旋转并绘制字符
            g.rotate(theta, x, y);
            g.drawString(String.valueOf(c), x, y);
            g.rotate(-theta, x, y);
        }

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillOval(x, y, 2, 2);
        }

        g.dispose();

        // 将验证码存入session
        session.setAttribute("captcha", captcha.toString());

        // 输出图片
        ImageIO.write(image, "png", response.getOutputStream());
    }

    /**
     * 生成验证码（Base64），用于前端 img dataURL 渲染
     */
    @GetMapping("/captcha/base64")
    public Result getCaptchaBase64(HttpSession session) throws IOException {
        int width = 120;
        int height = 40;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, width, height);

        String chars = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();

        g.setFont(new Font("Arial", Font.BOLD, 24));
        for (int i = 0; i < 4; i++) {
            char c = chars.charAt(random.nextInt(chars.length()));
            captcha.append(c);
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            int degree = random.nextInt(30) - 15;
            double theta = degree * Math.PI / 180;
            int x = 20 + i * 25;
            int y = 28;
            g.rotate(theta, x, y);
            g.drawString(String.valueOf(c), x, y);
            g.rotate(-theta, x, y);
        }

        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillOval(x, y, 2, 2);
        }
        g.dispose();

        // 保存到 session
        session.setAttribute("captcha", captcha.toString());

        // 转为 base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

        Map<String, Object> data = new HashMap<>();
        data.put("image", base64);
        return Result.ok(data);
    }
}

