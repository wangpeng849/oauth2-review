package com.review.userserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 * @Author wangp
 * @Date 2020/5/7
 * @Version 1.0
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    // tokenServices 我们配置了一个 RemoteTokenServices 的实例，
    // 这是因为资源服务器和授权服务器是分开的，资源服务器和授权服务器是放在一起的，就不需要配置 RemoteTokenServices 了。
    @Bean
    public RemoteTokenServices tokenServices(){
       RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
       remoteTokenServices.setClientId("clientId");
       remoteTokenServices.setClientSecret("123");
       remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
       return remoteTokenServices;
    }

    // RemoteTokenServices 中我们配置了 access_token 的校验地址、client_id、client_secret 这三个信息，
    // 当用户来资源服务器请求资源时，会携带上一个 access_token，通过这里的配置，就能够校验出 token 是否正确等。
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("res1").tokenServices(tokenServices());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .anyRequest().authenticated()
                .and()
                .cors();
        ;
    }
}
