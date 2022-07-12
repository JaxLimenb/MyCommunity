package com.nowcoder.mycommunity.util;

import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.internal.scripts.JO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-06-22:18
 */
public class MyCommunityUtil {

    /**
     * 生成随机字符串（激活码、文件名....）
     * @return 随机字符串
     */
    public static String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("_", "");
    }


    /**
     * MD5加密，将明文改为暗文， 密码+salt -> 暗文
     * @param key 密码+salt
     * @return 暗文
     */
    public static String md5(String key) {
        // isBlank方法判断key如果是（null，""，" "），都返回false
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 异步请求，获取JSON字符串
     * @param code 代码
     * @param msg 提示消息
     * @param map 业务数据
     * @return JSON字符串
     */
    private static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        return getJsonString(code, msg, map);
    }
    public static String getJSONString(int code, String msg) {
        return getJsonString(code, msg, null);
    }
    public static String getJSONString(int code, Map<String, Object> map) {
        return getJsonString(code, null, map);
    }
    public static String getJSONString(int code) {
        return getJsonString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }
}
