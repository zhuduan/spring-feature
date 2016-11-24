package com.zhf.spring_feature.aop_cache.util.store.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhf.spring_feature.aop_cache.util.store.CacheStoreService;

import redis.clients.jedis.JedisCluster;

/**
 * 
 * CacheStoreService的redis实现
 * 
 *
 */
@Service
public class RedisCacheStoreServiceImpl implements CacheStoreService{
	
	// 是否使用缓存
	private static final boolean USER_CACHE = true;
	
	@Autowired
	private JedisCluster jedisClusterCache;

	/**
     * 设置缓存, 返回true成功, false失败!
     * @param cacheKey 缓存key
     * @param cacheValue 缓存value
     * @param expireTimeSeconds 过期时间, 单位秒!
     * @return
     */
    public boolean setCache(String cacheKey, String cacheValue, int expireTimeSeconds){
		if(! USER_CACHE){
			return false;
		}
		
    	if(StringUtils.isEmpty(cacheKey)){
    		throw new IllegalArgumentException("cacheKey not empty!");
    	}
    	if(StringUtils.isEmpty(cacheValue)){
    		throw new IllegalArgumentException("cacheValue not empty!");
    	}
    	if(expireTimeSeconds <= 0){
    		throw new IllegalArgumentException("expireTimeSeconds <= 0!");
    	} else if (expireTimeSeconds > MAX_EXPIRE_SECONDS){
    		throw new IllegalArgumentException("expireTimeSeconds > MAX_EXPIRE_SECONDS!");
    	}
    	try{
			jedisClusterCache.set(cacheKey, cacheValue);
			jedisClusterCache.expire(cacheKey, expireTimeSeconds);
			return true;
    	} catch (Exception exp){ // 防止缓存崩溃,影响主业务逻辑

    	}
    	return false;
    }

    /**
     * 获取缓存
     * @param cacheKey
     * @return null: 异常发生
     */
    public String getCache(String cacheKey){
    	if(! USER_CACHE){
			return null;
		}
    	try{
    		return jedisClusterCache.get(cacheKey);
    	} catch (Exception exp){ // 防止缓存崩溃,影响主业务逻辑

    	}
    	return null;
    }

    /**
     * 检查KEY是否存在
     * 
     * @param cacheKey
     * @return
     */
	public boolean isCacheKeyExists(String cacheKey) {
		if(! USER_CACHE){
			return false;
		}
		
		try{
			return jedisClusterCache.exists(cacheKey);
    	} catch (Exception exp){ // 防止缓存崩溃,影响主业务逻辑

    	}
    	return false;
	}

	/**
     * 删除指定cacheKey
     * 
     * @param cacheKey
     * @return
     */
	public boolean deleteCache(String cacheKey) {
		if(! USER_CACHE){
			return false;
		}
		
		try{
			if (isCacheKeyExists(cacheKey)) {
	            return jedisClusterCache.del(cacheKey) > 0;
	        }
	        return false;
    	} catch (Exception exp){ // 防止缓存崩溃,影响主业务逻辑

    	}
    	return false;
	}

    /**
     * 以step步长, cacheKey的自增, 过期时间为expireTimeSeconds秒
     * @param cacheKey
     * @param incrStep  以incrStep步长自增
     * @param expireTimeSeconds  过期时间, 单位秒!
     * @return 返回增长后的值
     */
	public long incrCacheKey(String cacheKey, long incrStep, int expireTimeSeconds) {
		if(! USER_CACHE){
			return 0L;
		}
		
		if(StringUtils.isEmpty(cacheKey)){
    		throw new IllegalArgumentException("cacheKey not empty!");
    	}
    	if(expireTimeSeconds <= 0){
    		throw new IllegalArgumentException("expireTimeSeconds <= 0!");
    	} else if (expireTimeSeconds > MAX_EXPIRE_SECONDS){
    		throw new IllegalArgumentException("expireTimeSeconds > MAX_EXPIRE_SECONDS!");
    	}
    	
    	try{
    		final long result = jedisClusterCache.incrBy(cacheKey, incrStep);
    		jedisClusterCache.expire(cacheKey, expireTimeSeconds);
    		return result;
    	} catch (Exception exp){ // 防止缓存崩溃,影响主业务逻辑

    	}
    	
		return 0L; // 需要业务程序手动处理!!!
	}    
}
