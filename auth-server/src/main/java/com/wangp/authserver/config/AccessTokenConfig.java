package com.wangp.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @Author wangp
 * @Date 2020/5/7
 * @Version 1.0
 */
@Configuration
public class AccessTokenConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;


    //首先我们提供了一个 TokenStore 的实例，
    // 这个是指你生成的 Token 要往哪里存储，
    // 我们可以存在 Redis 中，也可以存在内存中，也可以结合 JWT 等等，
    // 这里，我们就先把它存在内存中，所以提供一个 InMemoryTokenStore 的实例即可。
    @Bean
    public TokenStore tokenStore(){
//        return new InMemoryTokenStore(); //内存存储 access_token
        return new RedisTokenStore(redisConnectionFactory);   //redis存储 access_token
    }

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("123"));
    }
}
