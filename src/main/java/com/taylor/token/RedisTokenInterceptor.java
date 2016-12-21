package com.taylor.token;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
public class RedisTokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AbstractTokenHandler redisTokenHadler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Token token = parseToken(handler);
        return redisTokenHadler.preHandle(request, response, token);
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Token token = parseToken(handler);
        redisTokenHadler.postHandle(request, response, modelAndView, token);
    }

    private Token parseToken(Object objHandler) {
        if (objHandler instanceof HandlerMethod) {
            HandlerMethod handler = (HandlerMethod) objHandler;
            return handler.getMethod().getAnnotation(Token.class);
        } else {
            throw new RuntimeException("handler not instanceof HandlerMethod");
        }
    }

}