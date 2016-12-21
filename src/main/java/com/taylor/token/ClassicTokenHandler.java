package com.taylor.token;

import javax.servlet.http.HttpServletRequest;

/**
 * 传统token 拦截器，token保存在session中
 */
public class ClassicTokenHandler extends AbstractTokenHandler {

    /**
     * 保存token
     *
     * @param request request
     * @param amount  需要生成的token数量
     */
    @Override
    protected String[] generateToken(HttpServletRequest request, int amount) {
        String[] tokenKeys = new String[amount];
        for (int i = 0; i < amount; i++) {
            String tokenKey = TokenUtil.generateTokenKey();
            tokenKeys[i] = tokenKey;
            request.getSession(false).setAttribute(tokenKey, "1"); //session中的token,例：xx3reasSd:1
            request.setAttribute(TokenConstant.REQUEST_KEY_PREFIX + "_" + (i + 1), tokenKey); //request中的token, 例：token:xx3reasSd
        }
        return tokenKeys;
    }

    /**
     * 删除token,删除时的parameter的名称为token
     *
     * @param request request
     */
    @Override
    protected void removeToken(HttpServletRequest request) {
        String tokenKey = request.getParameter(TokenConstant.REQUEST_KEY_PREFIX); //从request中获取tokenKey
        request.getSession(false).removeAttribute(tokenKey);//从session中删除token
    }

    @Override
    protected boolean lockToken(HttpServletRequest request, String tokenKey) {
        //TODO
        return true;
    }


    @Override
    protected void resetToken(HttpServletRequest request, String tokenId) {
        //TODO
    }

    /**
     * 判断是否重复提交
     *
     * @param request request
     * @return 是否重复提交
     */
    protected boolean isRepeatSubmit(HttpServletRequest request) {
        String tokenKey = request.getParameter(TokenConstant.REQUEST_KEY_PREFIX);
        if (tokenKey == null) {
            //如果client 端的token为空，则认为是重复提交,不允许提交
            return true;
        }
        String serverToken = (String) request.getSession(false).getAttribute(tokenKey);
        return serverToken == null;
    }
}