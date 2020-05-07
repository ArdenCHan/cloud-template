package com.xxx.config;

import com.xxx.UserRealm;
import com.xxx.common.utils.RedisUtils;
import com.xxx.jwt.JwtCredentialsMatcher;
import com.xxx.jwt.JwtFilter;
import com.xxx.jwt.JwtRealm;
import com.xxx.redis.RedisCacheManager;
import com.xxx.redis.RedisSessionDAO;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author ArdenChan
 * @Date 2020/5/7
 */
@Configuration
public class ShiroConfig {


    /**
     * 设置多个realm
     * @param userRealm userRealm
     * @param jwtRealm  jwtRealm
     * @return authenticator
     */
    @Bean(name = "authenticator")
    public Authenticator getAuthenticator(@Qualifier("userRealm") UserRealm userRealm, @Qualifier("jwtRealm") JwtRealm jwtRealm) {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        //设置两个Realm，一个用于用户登录验证和访问权限获取；一个用于jwt token的认证
        authenticator.setRealms(Arrays.asList(userRealm, jwtRealm));
        //设置多个realm认证策略，一个成功即跳过其它的
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

    /**
     * 获得一个认证标准
     * @return userRealm
     */
    @Bean(name = "userRealm")
    public UserRealm getUserRealm(@Qualifier("credentialsMatcher") CredentialsMatcher credentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher);
        return userRealm;
    }

    @Bean(name = "jwtRealm")
    public JwtRealm getJwtRealm(JwtCredentialsMatcher credentialsMatcher) {
        JwtRealm jwtRealm = new JwtRealm();
        jwtRealm.setCredentialsMatcher(credentialsMatcher);
        return jwtRealm;
    }

    /**
     * 加密
     * @return md5加密 散列1024次
     */
    @Bean(name = "credentialsMatcher")
    public CredentialsMatcher getCredentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5");
        credentialsMatcher.setHashIterations(1024);
        return credentialsMatcher;
    }

    /**
     * 获得安全管理器
     * @param userRealm userRealm
     * @param sessionManager sessionManager
     * @param cacheManager cacheManager
     * @return securityManager
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultSecurityManager(@Qualifier("userRealm") UserRealm userRealm,
                                                               @Qualifier("sessionManager") SessionManager sessionManager,
                                                               @Qualifier("cacheManager") CacheManager cacheManager) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(userRealm);
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setCacheManager(cacheManager);
        return defaultWebSecurityManager;
    }

    /**
     * 将session存放到redis 实现分布式系统的session共享
     * @param sessionDAO
     * @return
     */
    @Bean(name = "sessionManager")
    public SessionManager getSessionManager(@Qualifier("sessionDAO") SessionDAO sessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        return sessionManager;
    }

    @Bean(name = "sessionDAO")
    public SessionDAO getSessionDAO(RedisUtils redisUtils) {
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setRedisUtils(redisUtils);
        return sessionDAO;
    }

    @Bean(name = "cacheManager")
    public CacheManager getCacheManager(RedisUtils redisUtils) {
        RedisCacheManager cacheManager = new RedisCacheManager();
        cacheManager.setRedisUtils(redisUtils);
        return cacheManager;
    }


    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        //添加自定义过滤器，过滤那些带有token的请求
        filterMap.put("jwt",new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        /*
         * 设置一些过滤器
         *      anon: 无需认证
         *      authc: 必须认证
         *      user: 使用rememberMe
         *      perms: 对应的权限
         *      role: 对应的角色
         */
        Map<String, String> filterChainMap = new LinkedHashMap<>();
        filterChainMap.put("/user/**", "anon");
        filterChainMap.put("/logout", "logout");
        filterChainMap.put("/**","jwt");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean(name = "advisorAutoProxyCreator")
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator getAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean(name = "authorizationAttributeSourceAdvisor")
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}