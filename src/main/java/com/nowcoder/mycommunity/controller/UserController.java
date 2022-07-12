package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.annotation.LoginRequired;
import com.nowcoder.mycommunity.entity.User;
import com.nowcoder.mycommunity.service.UserService;
import com.nowcoder.mycommunity.util.HostHolder;
import com.nowcoder.mycommunity.util.MyCommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-08-20:56
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${mycommunity.path.uploadImage}")
    private String uploadPicturePath;
    @Value("${mycommunity.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片哦！");
            return "site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不对");
            return "site/setting";
        }

        // 生成随机文件名
        fileName = MyCommunityUtil.getRandomString() + suffix;
        // 确定文件存放路劲
        File dest = new File(uploadPicturePath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }
        // 更新当前用户头像路径（外部访问路径）
        // http://localhost:8081/mycommunity/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderPicture(user.getId(), headerUrl);

        return "redirect:/home/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = uploadPicturePath + "/" + fileName;
        // 解析文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        FileInputStream fis = null;
        try {
            OutputStream os = response.getOutputStream();
            fis = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int i = 0;
            while ((i = fis.read(buffer)) != -1) {
                os.write(buffer, 0, i);
            }
        } catch (IOException e) {
            LOGGER.error("读取头像失败: " + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @LoginRequired
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword, String confirmPassword) {
        User user = hostHolder.getUser();
        System.out.println(user);
        Map<String, Object> map = userService.updatePassword(user, oldPassword, newPassword, confirmPassword);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "您的密码已经修改成功啦！");
            model.addAttribute("target", "/home/index");
            return "site/operate-result";
        }else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));
            return "site/setting";
        }
    }
}
