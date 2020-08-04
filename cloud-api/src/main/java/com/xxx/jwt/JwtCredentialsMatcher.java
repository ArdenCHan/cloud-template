package com.xxx.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;

/**
 * @Author ArdenChan
 * @Date 2020/5/5
 */
@Slf4j
@Component
public class JwtCredentialsMatcher implements CredentialsMatcher {


    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        String token = ((JwtToken)authenticationToken).getToken();
        Object stored = authenticationInfo.getCredentials();
        String salt = stored.toString();
        String userName=authenticationInfo.getPrincipals().toString();
        try {
            Algorithm algorithm = Algorithm.HMAC256(salt);
            JWTVerifier verifier = JWT.require(algorithm)
                                      .withClaim("userName", userName)
                                      .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.info("Token Error:{}", e.getMessage());
        }
        return false;
    }
}
