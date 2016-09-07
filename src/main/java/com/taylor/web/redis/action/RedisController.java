package com.taylor.web.redis.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taylor.redis.service.RedisClientService;

/**
 * @ClassName: ShopUserAction
 * @Function: 用户控制层.
 * @date: 2016年4月18日 上午1:24:55
 * @author Taylor
 */
@Controller
@RequestMapping("/redis/*")
public class RedisController {
	private static final Logger LOGGER = Logger.getLogger(RedisController.class);

	@Autowired
	private RedisClientService redisClientService;

	/**
	 * 
	 * @desc showValueByKey(这里用一句话描述这个方法的作用)
	 * @param map
	 * @param key
	 * @param request
	 * @param response
	 * @return
	 * @author xiaolu.zhang
	 * @date 2016年9月1日 下午10:30:32
	 */
	@RequestMapping("show")
	public String showValueByKey(ModelMap map, @RequestParam(value = "key", defaultValue = "") String key, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (!"".equals(key)) {
				map.put("show", redisClientService.get(key));
			} else {
				map.put("show", "没有参数");
			}
		} catch (Exception e) {
			LOGGER.error("调用RedisController.show出错", e);
		}
		return "redis/index";
	}

	/**
	 * @desc setKeyValue(向redis插入key-value)
	 * @param map
	 * @param key
	 * @param value
	 * @param request
	 * @param response
	 * @author xiaolu.zhang
	 * @date 2016年9月1日 下午11:06:00
	 */
	@RequestMapping("set")
	public String setKeyValue(ModelMap map, @RequestParam(value = "key", defaultValue = "") String key, @RequestParam(value = "value", defaultValue = "") String value, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (!"".equals(key)) {
				redisClientService.set(key, value);
			}
		} catch (Exception e) {
			LOGGER.error("调用RedisController.show出错", e);
		}
		return "redis/index";
	}
}
