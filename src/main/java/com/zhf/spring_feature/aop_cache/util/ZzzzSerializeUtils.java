package com.zhf.spring_feature.aop_cache.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * 缓存的序列化和反序列化工具, 采用fastjson:
 * https://github.com/alibaba/fastjson
 * 
 * 性能高
 * 支持序列化标记为transient的关键字属性
 * 对于List<UserModel> 这种类型反序列化时, 可以到具体对象, 而不是JSONObject;
 * 
 * 
 * fastjson注意点:
 * 字符串时首字母变小写问题, 如果序列化和反序列化都使用fastjson就没有问题;
 * @see com.yunyao.mocha.test.model.PersonFastjsonTest
 * http://www.cnblogs.com/doit8791/p/4743633.html
 * 
 * 
 * 序列化时不能使用内部类，否则会报错
 *
 */
public class ZzzzSerializeUtils {

	/**
	 * serialize | deserialize 需使用同一种序列化的方案!
	 * 缓存数据序列化
	 * @param cacheObject
	 * @return
	 */
	public static final String serialize(final Object cacheObject){
		try{
			return serializeFastjsonTransient(cacheObject);
		} catch (Exception exp){
			// print something
		}
		// org.json.JSONObject 也能序列化标记为transient的关键字属性(异常时使用)
		return org.json.JSONObject.valueToString(cacheObject);
	}
	
	/***
	 * Fastjson序列化
	 * @param object
	 * @return
	 */
	public static String serializeFastjsonTransient(Object object) {
		SerializeWriter writer = new SerializeWriter();
		try {
			JSONSerializer serializer = new JSONSerializer(writer);
			// private transient int age; //序列化
			serializer.config(SerializerFeature.SkipTransientField, false);
			// java.lang.ClassCastException: com.alibaba.fastjson.JSONObject 
			// cannot be cast to com.yunyao.mocha.cache.model.UserModel
			serializer.config(SerializerFeature.WriteClassName, true);
			serializer.write(object);
			// serializer.close(); 源码中就是调用 this.out.close();
			// com.alibaba.fastjson.serializer.SerializeWriter.close(SerializeWriter.java:435)
			return writer.toString();
		} finally {
			writer.close();
		}
	}
	
	/**
	 * 缓存数据反序列化
	 * @param cacheValue
	 * @param clazz
	 * @return
	 */
	public static final <T> T deserialize(String cacheValue, Class<T> clazz) {
        return com.alibaba.fastjson.JSON.parseObject(cacheValue, clazz);
    }
}