package com.taylor.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.taylor.redis.service.RedisClientService;

import lombok.extern.log4j.Log4j2;

/**
 * token 拦截器，token保存在redis中
 */
@Log4j2
@Component
public class RedisTokenHandler extends AbstractTokenHandler {

    public static final String ENCODING = "utf-8";
    
	@Autowired
	private RedisClientService redisSimpleClient;
	
    /**
     * 生成token 并放入session
     *
     * @param request request
     * @param amount  需要生成的token 数量
     */
    @Override
    protected String[] generateToken(HttpServletRequest request, int amount) {
        log.info("Start to call [generateToken]. Parameter amount is :{}", amount);
        //生成tokens
        String[] tokenKeys = TokenUtil.generateTokenKeys(amount);

        for (int i = 0; i < amount; i++) {
            redisSimpleClient.set(tokenKeys[i], TokenStatusEnum.INIT.getKey());
            redisSimpleClient.expire(tokenKeys[i], TokenConstant.SESSION_EXPIRE_MINUTES * 60);
        }
        log.info("Succeed to call [generateToken]");
        return tokenKeys;
    }

    /**
     * 删除token
     *
     * @param request request
     */
    @Override
    protected void removeToken(HttpServletRequest request) {
        String tokenKey = parseToken(request); //从request中获取tokenKey
        try {
            redisSimpleClient.del(tokenKey);
        } catch (Exception e) {
            log.warn("fail to del redis key: {}", tokenKey);
        }
        log.info("Succeed to del token:{} from session:{}", tokenKey);
    }

    @Override
    protected boolean lockToken(HttpServletRequest request, String tokenId) {
        //TODO 判断token的状态
        if (redisSimpleClient.exists(tokenId)) {
            redisSimpleClient.set(tokenId, TokenStatusEnum.IN_PROGRESS.getKey());
            return true;
        }
        return false;
    }

    @Override
    protected void resetToken(HttpServletRequest request, String tokenId) {
        if (redisSimpleClient.exists(tokenId)) {
            redisSimpleClient.set(tokenId, TokenStatusEnum.INIT.getKey());
        }
    }


    /**
     * 判断是否重复提交
     *
     * @param request request
     * @return
     */
    @Override
    protected boolean isRepeatSubmit(HttpServletRequest request) {
        String tokenKey = parseToken(request);//从request中获取tokenKey
        if (tokenKey == null) {
            log.debug("tokenKey is null");
            //如果client 端的token为空，则认为是重复提交,不允许提交
            return true;
        }

        String tokenValue = redisSimpleClient.get(tokenKey);
        log.debug("token value from redis is :{}. Token isRepeatSubmit:{}", tokenValue, !(TokenStatusEnum.INIT.getKey().equals(tokenValue)));
        //判断redis中token的值，如果不是INIT，则是重复提交
        return !TokenStatusEnum.INIT.getKey().equals(tokenValue);
    }

    /**
     * 解析token的key,先用getParameter方式获取(form方式)，如果未取到，则从json中获取(ajax方式)
     *
     * @param request
     * @return
     */
    private String parseToken(HttpServletRequest request) {
        String tokenKey = request.getParameter(TokenConstant.REQUEST_KEY_PREFIX); //从request中获取tokenKey
        if (tokenKey == null) {
            try {
                String requestJson = IOUtils.toString(request.getInputStream(), ENCODING);
                Map<?, ?> properties = JSONUtil.fromJson(requestJson, Map.class);
                return (String) properties.get(TokenConstant.REQUEST_KEY_PREFIX);
            } catch (IOException e) {
                log.error(e);
            }
        }
        return tokenKey;
    }
}