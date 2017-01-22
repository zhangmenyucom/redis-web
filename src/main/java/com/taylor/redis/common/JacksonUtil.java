package com.taylor.redis.common;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * json序列化与反序列化工具类 jackson的ObjectMapper是线程安全的，因此尽量使用单例
 * 
 * @Description 反序列化时，默认对未知属性不进行处理 序列化时，默认日期格式为"yyyy-MM-dd HH:mm:ss"
 * @author XiongMiao
 * 
 */
public class JacksonUtil {

	private static JacksonUtil instance = null;

	private final ObjectMapper objectMapper;

	public JacksonUtil() {
		objectMapper = new ObjectMapper();
		init();
	}

	/**
	 * 初始化工具对象 默认对未知属性不进行处理
	 */
	private void init() {
		unknownPropertiesUnDeserialization();
		// setDateFormat(DEFAULT_DATE_FORMMAT);
		// nonNullSerialize();
		// nonNullMapSerialize();
	}

	/**
	 * 获得singleton工具实例
	 * 
	 * @return
	 */
	public static JacksonUtil getInstance() {
		if (instance == null) {
			synchronized (JacksonUtil.class) {
				if (instance == null) {
					instance = newInstance();
				}
			}
		}
		return instance;
	}

	/**
	 * 创建新工具实例
	 * 
	 * @return
	 */
	public static JacksonUtil newInstance() {
		return new JacksonUtil();
	}

	/**
	 * 设置转换日期类型的时间科室,如果不设置默认打印Timestamp毫秒数.
	 * 
	 * @param pattern
	 *            时间格式化字符串
	 */
	public JacksonUtil setDateFormat(String pattern) {
		if (StringUtils.isNotBlank(pattern)) {
			DateFormat dateFormat = new SimpleDateFormat(pattern);
			objectMapper.setDateFormat(dateFormat);
		} else {
			objectMapper.setDateFormat(null);
		}
		return this;
	}

	/**
	 * bean中属性值为null的不序列化成json字符串
	 * 
	 * @return
	 */
	public JacksonUtil nonNullSerialize() {
		// bean中的null值不写入json字符串
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return this;
	}

	/**
	 * 反序列化成bean时，未知属性不进行反序列化
	 * 
	 * @return
	 */
	public JacksonUtil unknownPropertiesUnDeserialization() {
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return this;
	}

	/**
	 * 配置反序列化的特征
	 * 
	 * @param feature
	 * @param state
	 * @return
	 */
	private JacksonUtil configure(DeserializationFeature feature, boolean state) {
		objectMapper.configure(feature, state);
		return this;
	}

	/**
	 * 配置——Map中值为null的不序列化成json字符串，非空的value才序列化
	 * 
	 * @param feature
	 * @param state
	 * @return
	 */
	public JacksonUtil nonNullMapSerialize() {
		// Map中的null值不写入json字符串
		configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		return this;
	}

	/**
	 * 配置序列化的特征
	 * 
	 * @param feature
	 * @param state
	 * @return
	 */
	private JacksonUtil configure(SerializationFeature feature, boolean state) {
		objectMapper.configure(feature, state);
		return this;
	}

	/**
	 * 不需要序列化的配置
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private JacksonUtil setSerializationInclusion(JsonInclude.Include include) {
		objectMapper.setSerializationInclusion(include);
		return this;
	}

	/**
	 * Bean转化成Json
	 * @throws Exception 
	 * 
	 * @throws JsonProcessingException
	 */
	public String bean2Json(Object bean) throws Exception {
		try {
			return this.objectMapper.writeValueAsString(bean);
		} catch (Exception e) {
			throw new Exception("Exception when call bean2Json(): " + e.getMessage(), e);
		}
	}

	/**
	 * Bean转化成JsonNode
	 * 
	 * @throws JsonProcessingException
	 */
	public JsonNode bean2JsonNode(Object bean) {
		return this.objectMapper.valueToTree(bean);
	}

	/**
	 * Bean转化成TypeReference 对象
	 * 
	 * @throws JsonProcessingException
	 */
	public <T> T bean2Bean(Object bean, TypeReference<T> type) {
		return this.objectMapper.convertValue(bean, type);
	}

	/**
	 * Bean转化成bean
	 * 
	 * @throws JsonProcessingException
	 */
	public <T> T bean2Bean(Object bean, Class<T> dest) {
		return this.objectMapper.convertValue(bean, dest);
	}

	/**
	 * Json转化成Bean
	 * @throws Exception 
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public <T> T json2Bean(String jsonStr, Class<T> beanClazz) throws Exception {
		try {
			return this.objectMapper.readValue(jsonStr, beanClazz);
		} catch (Exception e) {
			throw new Exception("Exception when call json2Bean(): " + e.getMessage() + " with jsonStr: " + jsonStr + " and beanClazz: " + beanClazz, e);
		}
	}

	/**
	 * Json转化成Map
	 * @throws Exception 
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> json2Map(String jsonStr) throws Exception {
		try {
			return this.objectMapper.readValue(jsonStr, Map.class);
		} catch (Exception e) {
			throw new Exception("Exception when call json2Map: " + e.getMessage() + " with jsonStr: " + jsonStr, e);
		}
	}

	/**
	 * Json转化成指定Bean类型的Map
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @throws Exception 
	 */
	public <T> Map<String, T> json2Map(String jsonStr, Class<T> clazz) throws Exception {
		Map<String, Map<String, Object>> map = null;
		try {
			map = this.objectMapper.readValue(jsonStr, new TypeReference<Map<String, T>>() {
			});
		} catch (Exception e) {
			throw new Exception(e);
		}
		Map<String, T> result = new HashMap<String, T>();
		for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
			result.put(entry.getKey(), map2Bean(entry.getValue(), clazz));
		}
		return result;
	}

	/**
	 * Json转化成List
	 * @throws Exception 
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public <T> List<T> json2List(String jsonArrayStr, Class<T> clazz) throws Exception {
		List<Map<String, Object>> list = null;
		try {
			list = this.objectMapper.readValue(jsonArrayStr, new TypeReference<List<T>>() {
			});
		}catch (Exception e) {
			throw new Exception(e);
		}
		List<T> result = new ArrayList<T>();
		for (Map<String, Object> map : list) {
			result.add(map2Bean(map, clazz));
		}
		return result;
	}

	public <T> T json2TypeBean(String jsonStr, TypeReference<T> type) throws Exception {
		try {
			return this.objectMapper.readValue(jsonStr, type);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public <T> T json2TypeBean(JsonNode node, TypeReference<T> type) throws Exception {
		try {
			JsonParser jsonParser = this.objectMapper.treeAsTokens(node);
			return this.objectMapper.readValue(jsonParser, type);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Json转化成Bean
	 * @throws Exception 
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public <T> T json2Bean(JsonNode node, Class<T> beanClazz) throws Exception {
		try {
			return this.objectMapper.treeToValue(node, beanClazz);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * Map转化成Bean
	 * 
	 * @param map
	 * @param class
	 */
	public <T> T map2Bean(Map<String, Object> map, Class<T> clazz) {
		return this.objectMapper.convertValue(map, clazz);
	}

	/**
	 * json to javaType
	 * 
	 * @param jsonStr
	 * @param javaType
	 * @return
	 * @throws Exception 
	 */
	public <T> T json2JavaType(String jsonStr, JavaType javaType) throws Exception {
		try {
			return this.objectMapper.readValue(jsonStr, javaType);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}
}
