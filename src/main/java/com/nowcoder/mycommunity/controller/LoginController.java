package com.nowcoder.mycommunity.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.mycommunity.annotation.LoginRequired;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.MyCommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-06-21:36
 */
@Controller
public class LoginController implements MyCommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/home/index");
            return "site/operate-result";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{actCode}", method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("actCode") String actCode) {
        // 通过service获取激活结果
        int activationResult = userService.activation(userId, actCode);
        switch (activationResult) {
            case ACTIVATION_SUCCESS:
                model.addAttribute("msg", "激活成功，您的账号可以正常使用了！");
                model.addAttribute("target", "/login");
                break;
            case ACTIVATION_REPEAT:
                model.addAttribute("msg", "无效操作，该账号已经激活过了！");
                model.addAttribute("target", "/home/index");
                break;
            case ACTIVATION_FAILURE:
                model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
                model.addAttribute("target", "/home/index");
                break;
        }
        return "site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成随机4位验证码（看配置文件KaptchaConfig）
        String text = kaptchaProducer.createText();
//        System.out.println(text);
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session，便于后续使用
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            // 整个输出流是由SpringMVC维护的，这里不用关闭，会自动关
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            LOGGER.error("响应验证码失败:" + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response) {
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "site/login";
        }
        long expiredSeconds = rememberMe ? REMEMBER_ME_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        int expSeconds = (int) expiredSeconds == expiredSeconds ? (int) expiredSeconds : Integer.MAX_VALUE;
        Map<String, Object> map = userService.login(username, password, expSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(expSeconds);
            response.addCookie(cookie);
            return "redirect:/home/index";
        }else {
//            Set<String> keySet = map.keySet();
//            for (String element : keySet) {
//                model.addAttribute(element, map.get(element));
//            }
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }
    }

    @LoginRequired
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }
}
