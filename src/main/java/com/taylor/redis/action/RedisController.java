package com.taylor.redis.action;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taylor.redis.annotation.RedisCacheGet;
import com.taylor.redis.service.RedisClientService;
import com.taylor.token.Token;
import com.taylor.token.TokenUtil;

@Controller
@RequestMapping("/redis/*")
public class RedisController {
	private static final Logger LOGGER = Logger.getLogger(RedisController.class);

	@Autowired
	private RedisClientService redisClientService;

	public static AtomicInteger success_count = new AtomicInteger(0);

	public static volatile int fail_count = 0;

	public static int version = 0;


	@RequestMapping("show")
	public String showValueByKey(ModelMap map, @RequestParam(value = "key", defaultValue = "") String key, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (!"".equals(key)) {
				map.put("show", redisClientService.get(key));
			} else {
				map.put("show", "æ²¡æåæ°");
			}
		} catch (Exception e) {
			LOGGER.error("è°ç¨RedisController.showåºé", e);
		}
		return "redis/index";
	}

	@RequestMapping("set")
	public String setKeyValue(ModelMap map, @RequestParam(value = "key", defaultValue = "") String key, @RequestParam(value = "value", defaultValue = "") String value, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (!"".equals(key)) {
				redisClientService.set(key, value);
			}
		} catch (Exception e) {
			LOGGER.error("è°ç¨RedisController.showåºé", e);
		}
		return "redis/index";
	}


	@RequestMapping("/test")
	@ResponseBody
	public String distributeLockTest(@RequestParam("key") String key) {
		Long value = System.currentTimeMillis();
		String oldValue = redisClientService.getSet(key, value + "");
		if (null == oldValue) {
			redisClientService.expire(key, 1);

			{
				System.out.println("doBusiness......");
			}
			redisClientService.del(key);
			success_count.incrementAndGet();
			System.out.println("成功次数：" + success_count.get());
			return "sucess";
		} else {
			Long timeGap = System.currentTimeMillis() - Long.parseLong(oldValue);
			if (timeGap > 500) {
				redisClientService.del(key);
			}
			fail_count++;
			System.out.println("失败次数："+fail_count);
			return "fail";
		}

	}

	@Token(remove = true)
	@RequestMapping("/token")
	@ResponseBody
	public String tokenTest(String message, HttpServletRequest request) {
		if (TokenUtil.isRepeatedSubmission(request)) {
			return "do not repeat submit";
		}
		try {
			TokenUtil.addRemoveTokenFlag(request);
		} catch (Throwable t) {
		}
		return request.getParameter("token");
	}

	@Token(generate = 1)
	@RequestMapping("/token_view")
	public String tokenView(HttpServletRequest request, HttpServletResponse response) {
		return "/redis/token_view";
	}

	@Token(generate = 1)
	@RequestMapping("/token_ajax")
	@ResponseBody
	public String tokenAjax(HttpServletRequest request, HttpServletResponse response) {
		return "haha";
	}
	
	@RedisCacheGet(key="'test_'+#key")
	@RequestMapping("/get_key")
	@ResponseBody
	public String getKey(HttpServletRequest request,HttpServletResponse response,@RequestParam("key")String key){
		System.out.println("from redis db");
		return redisClientService.get(key);
	}
}
