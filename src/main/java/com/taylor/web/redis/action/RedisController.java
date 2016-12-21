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

	public static AtomicInteger success_count = new AtomicInteger(0);

	public static volatile int fail_count = 0;

	public static int version = 0;

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

	/**
	 * @desc distributeLockTest(这里用一句话描述这个方法的作用)
	 * @param request
	 * @param response
	 * @return
	 * @author xiaolu.zhang
	 * @date 2016年12月1日 上午11:06:27
	 */
	@RequestMapping("/test")
	@ResponseBody
	public String distributeLockTest(@RequestParam("key") String key) {
		/** 设置锁值为当前时间戳 **/
		Long value = System.currentTimeMillis();
		/** getset命令：设置新值并返回原来的值，如果原来key不存在则返回null **/
		String oldValue = redisClientService.getSet(key, value + "");
		/** 不存在则拥有锁 **/
		if (null == oldValue) {
			/** 设置有效时间为1秒 **/
			redisClientService.expire(key, 1);

			/** 处理业务逻辑 **/
			{
				System.out.println("doBusiness......");
			}
			/** 处理完业务逻辑，释放锁 **/
			redisClientService.del(key);
			success_count.incrementAndGet();
			System.out.println("成功次数：" + success_count.get());
			return "sucess";
		} else {
			Long timeGap = System.currentTimeMillis() - Long.parseLong(oldValue);
			/** 业务超时,释放锁 **/
			if (timeGap > 500) {
				redisClientService.del(key);
			}
			fail_count++;
			System.out.println("重试次数：" + fail_count);
			 return "fail";
			//return this.distributeLockTest(key);
		}

	}
	
/*	@RequestMapping("/queue")
	@ResponseBody
	public String redusQueueTest(String message) {
		
		redisClientService.lpush("list", "message");
		redisClientService.blpop(message)
		
	}*/
}
