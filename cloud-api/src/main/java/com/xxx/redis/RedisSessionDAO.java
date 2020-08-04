package com.xxx.redis;

import com.alibaba.fastjson.JSON;
import com.xxx.common.utils.RedisUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;

import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @Author ArdenChan
 * @Date 2020/5/5
 */
@Data
@NoArgsConstructor
public class RedisSessionDAO extends CachingSessionDAO {


    public RedisSessionDAO(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    private RedisUtils redisUtils;

    /**
     * session前缀
     */
    private static final String KEY_PREFIX = "SHIRO_REDIS_SESSION:";
    /**
     * 默认过期时间 15分钟
     */
    private static final Long DEFAULT_KEY_EXPIRE_TIME = 15 * 60 * 1000L;

    @Override
    protected void doUpdate(Session session) {
        this.saveSession(session);
    }

    @Override
    protected void doDelete(Session session) {
        redisUtils.del(KEY_PREFIX + session.getId());
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        saveSession(session);
        return sessionId;
    }

    private void saveSession(Session session) {
        //需要进行序列化
        redisUtils.set(KEY_PREFIX + session.getId(), JSON.toJSON(session), DEFAULT_KEY_EXPIRE_TIME);
    }


    @Override
    protected Session doReadSession(Serializable sessionId) {
        //需要进行反序列化
        return (Session) JSON.parse((byte[]) redisUtils.get(KEY_PREFIX + sessionId));
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return redisUtils.multiGet(KEY_PREFIX)
                .stream()
                .map(obj -> {
                    return (Session) obj;
                })
                .collect(Collectors.toList());
    }
}
