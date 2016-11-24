package com.zhf.spring_feature.aop_cache.util.store;

/**
 * 
 * 缓存接口，定义了具体的缓存调用方法:
 * 可以用redis或memcache来做实现
 * 
 *
 */
public interface CacheStoreService {

    /**
     * 最大缓存时间31天;
     */
    public static final int MAX_EXPIRE_SECONDS = 60*60*24*31;

    /**
     * 获取缓存
     * @param cacheKey
     * @return
     */
    public String getCache(String cacheKey);
    
    /**
     * 设置缓存, 返回true成功, false失败!
     * @param cacheKey
     * @param cacheValue
     * @param expireTimeSeconds 过期时间, 单位秒!
     * @return
     */
    public boolean setCache(String cacheKey, String cacheValue, int expireTimeSeconds);
    
    /**
     * 检查KEY是否存在
     * 
     * @param cacheKey
     * @return
     */
    public boolean isCacheKeyExists(String cacheKey);

    /**
     * 删除指定cacheKey
     * 
     * @param cacheKey
     * @return
     */
    public boolean deleteCache(String cacheKey);

    /**
     * 以step步长, cacheKey的自增, 过期时间为expireTimeSeconds秒
     * @param cacheKey
     * @param incrStep  以incrStep步长自增
     * @param expireTimeSeconds  过期时间, 单位秒!
     * @return 返回增长后的值
     */
    public long incrCacheKey(String cacheKey, long incrStep, int expireTimeSeconds);
}
