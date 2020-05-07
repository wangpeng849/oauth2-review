package com.wangp.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @Author wangp
 * @Date 2020/5/7
 * @Version 1.0
 */
//接下来我们创建 AuthorizationServer 类继承自 AuthorizationServerConfigurerAdapter，
// 来对授权服务器做进一步的详细配置，
// AuthorizationServer 类记得加上 @EnableAuthorizationServer 注解，表示开启授权服务器的自动化配置。
@EnableAuthorizationServer
@Configuration
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    // AuthorizationServer 类中，我们其实主要重写三个 configure 方法。
    // 1.  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception
    // 2.  public void configure(ClientDetailsServiceConfigurer clients) throws Exception
    // 3.  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private ClientDetailsService clientDetailsService;

    // AuthorizationServerSecurityConfigurer 用来配置令牌端点的安全约束，也就是这个端点谁能访问，谁不能访问。
    // checkTokenAccess 是指一个 Token 校验的端点，
    // 这个端点我们设置为可以直接访问（在后面，当资源服务器收到 Token 之后，需要去校验 Token 的合法性，就会访问这个端点）。
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    //ClientDetailsServiceConfigurer 用来配置客户端的详细信息，
    // 授权服务器要做两方面的检验，一方面是校验客户端，另一方面则是校验用户，
    // 校验用户，我们前面已经配置了，这里就是配置校验客户端。
    // 客户端的信息我们可以存在数据库中，这其实也是比较容易的，和用户信息存到数据库中类似，
    // 但是这里为了简化代码，我还是将客户端信息存在内存中，
    // 这里我们分别配置了客户端的 id，secret、资源 id、授权类型、授权范围以及重定向 uri。
    // 授权类型一共四种，四种之中不包含 refresh_token 这种类型，但是在实际操作中，refresh_token 也被算作一种。
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("clientId")
                .secret(new BCryptPasswordEncoder().encode("123"))
                .resourceIds("res1")
                .authorizedGrantTypes("authorization_code", "refresh_token")  //授权码模式
//                .authorizedGrantTypes("refresh_token","implicit") //简化模式
                .scopes("all")
                .redirectUris("http://localhost:8082/index.html"); //授权码模式
//                .redirectUris("http://localhost:8082/index2.html");  //简化模式
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    // tokenServices 这个 Bean 主要用来配置 Token 的一些基本信息，
    // 例如 Token 是否支持刷新、Token 的存储位置、Token 的有效期以及刷新 Token 的有效期等等。
    // Token 有效期这个好理解，刷新 Token 的有效期我说一下，当 Token 快要过期的时候，
    // 我们需要获取一个新的 Token，在获取新的 Token 时候，需要有一个凭证信息，这个凭证信息不是旧的 Token，
    // 而是另外一个 refresh_token，这个 refresh_token 也是有有效期的。
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();
        services.setTokenStore(tokenStore);
        services.setClientDetailsService(clientDetailsService);
        services.setSupportRefreshToken(true);
        services.setAccessTokenValiditySeconds(60 * 60 * 2);
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 2);
        return services;
    }

    //AuthorizationServerEndpointsConfigurer 这里用来配置令牌的访问端点和令牌服务。
    // authorizationCodeServices用来配置授权码的存储，
    // 这里我们是存在在内存中，tokenServices 用来配置令牌的存储，即 access_token 的存储位置，这里我们也先存储在内存中。
    // 授权码和令牌有什么区别？授权码是用来获取令牌的，使用一次就失效，令牌则是用来获取资源的
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authorizationCodeServices(authorizationCodeServices())
                .tokenServices(tokenServices());
    }
}
