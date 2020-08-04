package com.xxx.redis;

import com.xxx.common.utils.RedisUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * @Author ArdenChan
 * @Date 2020/5/5
 */
@Data
@NoArgsConstructor
public class RedisCacheManager implements CacheManager {

    private RedisUtils redisUtils;

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new RedisCacheMap<>(this.redisUtils);
    }

}
