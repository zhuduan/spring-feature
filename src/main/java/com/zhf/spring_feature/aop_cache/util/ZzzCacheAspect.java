package com.zhf.spring_feature.aop_cache.util;

import java.lang.reflect.Method;
import java.util.Collections;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhf.spring_feature.aop_cache.util.store.CacheStoreService;

/**
 * 
 * 配置@MochaCache 注解的切面, 在方法上使用了@MochaCache表示就使用了该切面 
 * 切面使用Around
 * 
 *
 */
@Aspect
@Component
public class ZzzCacheAspect {
	
	
	@Autowired
    private CacheStoreService cacheStoreService;
	
	/**
	 * 以@MochaCache 注解作为aop切点
	 */
	@Pointcut("@annotation(com.zhf.spring_feature.aop_cache.util.ZzzCache)")
	public void pointcut(){ 
    }

	/***
	 * 切点Around方法实现
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("pointcut()")
	public Object doAround(final ProceedingJoinPoint pjp) throws Throwable {
		final long time_1 = System.currentTimeMillis();
		final MethodSignature ms = (MethodSignature) pjp.getSignature();
		final Method method = ms.getMethod();
		final ZzzCache cacheAnnotation = method.getAnnotation(ZzzCache.class);

		// 获取注解信息
		final String cacheKey = generateCacheKey(cacheAnnotation.key(), pjp);
		final int expire = cacheAnnotation.expire();
		final Class<?> cacheClazz = ((MethodSignature) pjp.getSignature()).getReturnType();
		
		// getCache
		final String cacheValueAlready = cacheStoreService.getCache(cacheKey);
		if (cacheValueAlready != null) {
			final Object cacheObj = ZzzzSerializeUtils.deserialize(cacheValueAlready, cacheClazz);
			final long time_2 = System.currentTimeMillis();
			System.out.println("hit cacheKey:" + cacheKey+", ms:" + (time_2-time_1));
			System.out.println("hit cacheKey:" + cacheKey+", cacheValueAlready:"+cacheValueAlready);
			return cacheObj;
		} 
		
		// 未命中缓存，查询结果，并放到缓存中
		final long time_3 = System.currentTimeMillis();
		final Object dbExecuteValue = pjp.proceed();
		if (dbExecuteValue != null) {
			final long time_4 = System.currentTimeMillis();
			String cacheValueSave = ZzzzSerializeUtils.serialize(dbExecuteValue);
			// 过滤java.util.Collections$EmptyMap
			if(cacheValueSave.indexOf("java.util.Collections$Empty") >= 0){
				cacheValueSave = cacheValueSave.replaceAll("java.util.Collections$EmptyListIterator", "java.util.LinkedList$ListItr");
				cacheValueSave = cacheValueSave.replaceAll("java.util.Collections$EmptyMap", "java.util.HashMap");
				cacheValueSave = cacheValueSave.replaceAll("java.util.Collections$EmptyIterator", "java.util.HashMap$KeyIterator");
			}
			// setCache
			cacheStoreService.setCache(cacheKey, cacheValueSave, expire);
			final long time_5 = System.currentTimeMillis();
			System.out.println("set cacheKey:" + cacheKey+", expire:" + expire + ", ms:" + (time_5 - time_4) + ", db ms:" + (time_4 - time_3));
			System.out.println("set cacheKey:" + cacheKey+", cacheValueSave:"+cacheValueSave);
		}
		return dbExecuteValue;
	}
	
	/**
	 * 用类名、方法名、参数名作为缓存的key
	 * @param pjp
	 * @return
	 */
	private static final String generateCacheKey(final String configKey, final ProceedingJoinPoint pjp){
		final Object[] methodArgs = pjp.getArgs();  
		if(configKey!=null && configKey.length()>0){
			return generateCacheKey(configKey, methodArgs);
		}
		StringBuilder sb = new StringBuilder();
		String className = pjp.getTarget().getClass().getSimpleName();  
        String methodName = pjp.getSignature().getName();  
        sb.append("cache.");  //以cache打头!
        sb.append(className);
        sb.append("_");
        sb.append(methodName);
        for(Object arg : methodArgs) {  
            if(arg != null) {  
            	// 参数名依赖于参数的toString方法，如果需要可以重载toString来处理
                sb.append("_").append(arg.toString());  
            }  
        }  
        return sb.toString(); 
	}
	
	/**
	 * 用类名、方法名、参数名作为缓存的key
	 * @param pjp
	 * @return
	 */
	private static final String generateCacheKey(String configKey, Object[] methodArgs){
		if(methodArgs == null || methodArgs.length==0){
			return configKey;
		}
        StringBuilder sb = new StringBuilder();
        sb.append(configKey);
        for(Object arg : methodArgs) {  
            if(arg != null) {  
                sb.append("_").append(arg.toString());  
            }  
        }  
        return sb.toString();
	}
	
	public static void main(String[] args) {
//		List<Integer> list = new ArrayList<>();
//		List<Integer> list2 = (List)Collections.emptyList();
//		System.out.println(Collections.emptyList().getClass().isAssignableFrom(list.getClass()));
//		System.out.println(Collections.emptyList().getClass().isAssignableFrom(list2.getClass()));
		System.out.println(ZzzzSerializeUtils.serialize(Collections.emptySet()));
		System.out.println(ZzzzSerializeUtils.serialize(Collections.emptyList()));
		System.out.println(ZzzzSerializeUtils.serialize(Collections.emptyEnumeration()));
		System.out.println(ZzzzSerializeUtils.serialize(Collections.emptyIterator()));
		System.out.println(ZzzzSerializeUtils.serialize(Collections.emptyListIterator()));
		System.out.println(ZzzzSerializeUtils.serialize(Collections.emptyMap()));
		

	}
}