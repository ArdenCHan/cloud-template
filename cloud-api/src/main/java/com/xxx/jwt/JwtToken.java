package com.xxx.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Author ArdenChan
 * @Date 2020/5/5
 */
@Data
@NoArgsConstructor
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = -8987338393762932853L;

    private String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
