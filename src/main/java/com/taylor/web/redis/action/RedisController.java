package com.taylor.web.redis.action;

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

import com.taylor.redis.service.RedisClientService;
import com.taylor.token.Token;
import com.taylor.token.TokenUtil;

/**
 * @ClassName: ShopUserAction
 * @Function: ç¨æ·æ§å¶å±.
 * @date: 2016å¹´4æ18æ¥ ä¸å1:24:55
 * @author Taylor
 */
@Controller
@RequestMapping("/redis/*")
public class RedisController {
	private static final Logger LOGGER = Logger.getLogger(RedisController.class);

	@Autowired
	private RedisClientService redisClientService;

	public static AtomicInteger success_count = new AtomicInteger(0);

	public static volatile int fail_count = 0;

	public static int version = 0;

	/**
	 * 
	 * @desc showValueByKey(è¿éç¨ä¸å¥è¯æè¿°è¿ä¸ªæ¹æ³çä½ç¨)
	 * @param map
	 * @param key
	 * @param request
	 * @param response
	 * @return
	 * @author xiaolu.zhang
	 * @date 2016å¹´9æ1æ¥ ä¸å10:30:32
	 */
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

	/**
	 * @desc setKeyValue(åredisæå¥key-value)
	 * @param map
	 * @param key
	 * @param value
	 * @param request
	 * @param response
	 * @author xiaolu.zhang
	 * @date 2016å¹´9æ1æ¥ ä¸å11:06:00
	 */
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

	/**
	 * @desc distributeLockTest(è¿éç¨ä¸å¥è¯æè¿°è¿ä¸ªæ¹æ³çä½ç¨)
	 * @param request
	 * @param response
	 * @return
	 * @author xiaolu.zhang
	 * @date 2016å¹´12æ1æ¥ ä¸å11:06:27
	 */
	@RequestMapping("/test")
	@ResponseBody
	public String distributeLockTest(@RequestParam("key") String key) {
		/** è®¾ç½®éå¼ä¸ºå½åæ¶é´æ³ **/
		Long value = System.currentTimeMillis();
		/** getsetå½ä»¤ï¼è®¾ç½®æ°å¼å¹¶è¿ååæ¥çå¼ï¼å¦æåæ¥keyä¸å­å¨åè¿ånull **/
		String oldValue = redisClientService.getSet(key, value + "");
		/** ä¸å­å¨åæ¥æé **/
		if (null == oldValue) {
			/** è®¾ç½®æææ¶é´ä¸º1ç§ **/
			redisClientService.expire(key, 1);

			/** å¤çä¸å¡é»è¾ **/
			{
				System.out.println("doBusiness......");
			}
			/** å¤çå®ä¸å¡é»è¾ï¼éæ¾é **/
			redisClientService.del(key);
			success_count.incrementAndGet();
			System.out.println("æåæ¬¡æ°ï¼" + success_count.get());
			return "sucess";
		} else {
			Long timeGap = System.currentTimeMillis() - Long.parseLong(oldValue);
			/** ä¸å¡è¶æ¶,éæ¾é **/
			if (timeGap > 500) {
				redisClientService.del(key);
			}
			fail_count++;
			System.out.println("éè¯æ¬¡æ°ï¼" + fail_count);
			return "fail";
			// return this.distributeLockTest(key);
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
}
