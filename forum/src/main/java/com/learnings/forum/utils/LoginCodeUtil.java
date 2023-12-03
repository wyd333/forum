package com.learnings.forum.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LoginCodeUtil extends CodeUtil{

    private static final int WIDTH = 200;
    private static final int HEIGHT = 50;
    private static final int FONT_SIZE = 30; //字符大小
    private String loginCode; //验证码


    /**
     * 生成验证码图片
     * @return
     */
    public BufferedImage getLoginCodeImage(){
        //验证码图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        // 1-生成随机验证码
        loginCode = createCode();

        // 2-设置背景
        drawBackground(graphics);

        // 3-绘制验证码
        drawRands(graphics, loginCode.toCharArray());

        // 4-关闭资源
        graphics.dispose();

        return image;
    }

    private static void drawRands(Graphics g, char[] rands) {
        g.setFont(new Font("Console", Font.BOLD, FONT_SIZE));
        for (int i = 0; i < rands.length; i++) {
            g.setColor(getRandomColor());
            g.drawString("" + rands[i], i * FONT_SIZE + 10, 30);
        }
    }


    /**
     * 生成 CODE_LENGTH 长度的随机验证码
     * @return
     */
    @Override
    public String createCode() {
        // 可以包含在验证码中的字符集合
        String charset = "0123456789" + "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(charset.length());
            code.append(charset.charAt(randomIndex));
        }
        return code.toString();
    }

    /**
     * 获取随机颜色给验证码文本
     * @return 随机颜色
     */
    private static Color getRandomColor() {
        Random ran = new Random();
        return new Color(ran.nextInt(220), ran.nextInt(220), ran.nextInt(220));
    }

    /**
     * 绘制验证码图片背景
     * @param g 图片对象
     */
    private static void drawBackground(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制验证码干扰点
        for (int i = 0; i < 200; i++) {
            int x = (int) (Math.random() * WIDTH);
            int y = (int) (Math.random() * HEIGHT);
            g.setColor(getRandomColor());
            g.drawOval(x, y, 1, 1);
        }
    }

    public String getLoginCode() {
        return loginCode;
    }

}
