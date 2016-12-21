package com.taylor.token;

import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * token工具
 */
@SuppressWarnings("restriction")
public class TokenUtil {

    /**
     * 生成Token key
     *
     * @return 生成的token key
     */
    public static String generateTokenKey() {
        String token = String.valueOf(System.currentTimeMillis()) + new Random().nextInt(999999999);
        return "token-" + new BASE64Encoder().encode(token.getBytes());
    }

    /**
     * 生成Token key
     *
     * @return 生成的token key
     */
    public static String[] generateTokenKeys(int amount) {
        String[] tokenKeys = new String[amount];
        for (int i = 0; i < amount; i++) {
            tokenKeys[i] = generateTokenKey();
        }
        return tokenKeys;
    }

    /**
     * 生成Token key
     *
     * @return 生成的token key
     */
    public static boolean isRepeatedSubmission(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute(TokenConstant.IS_REPEATED_SUBMISSION));
    }

    /**
     * 增加删除token标记
     *
     * @param request
     */
    public static void addRemoveTokenFlag(HttpServletRequest request) {
        request.setAttribute(TokenConstant.IS_REMOVE_TOKEN, Boolean.TRUE);
    }
}