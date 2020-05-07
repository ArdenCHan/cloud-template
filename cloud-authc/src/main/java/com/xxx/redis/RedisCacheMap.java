package com.xxx.redis;

import com.xxx.common.utils.RedisUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author ArdenChan
 * @Date 2020/5/5
 */
@Data
@NoArgsConstructor
@Component
public class RedisCacheMap<K,V> implements Cache<K,V> {

    public RedisCacheMap(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    private RedisUtils redisUtils;


    /**
     * session前缀
     */
    private static final String KEY_PREFIX = "SHIRO_REDIS_SESSION:";

    @Override
    public V get(K k) throws CacheException {
        return (V) redisUtils.get((String) k);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        redisUtils.set((String)k,v);
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        redisUtils.del((String)k);
        return null;
    }

    @Override
    public void clear() throws CacheException {
        redisUtils.keys(KEY_PREFIX)
                  .forEach(redisUtils::del);
    }

    @Override
    public int size() {
        return redisUtils.keys(KEY_PREFIX).size();
    }

    @Override
    public Set<K> keys() {
        return (Set<K>) redisUtils.keys(KEY_PREFIX);
    }

    @Override
    public Collection<V> values() {
        return (Collection<V>)redisUtils.multiGet(KEY_PREFIX).stream().collect(Collectors.toList());
    }

}
