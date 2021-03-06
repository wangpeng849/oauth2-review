package com.review.myspringsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.PrintWriter;

/**
 * @Author wangp
 * @Date 2020/5/6
 * @Version 1.0
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("wangp")
                .password("123")
                .roles("admin")
                //添加普通用户
                .and()
                .withUser("wp")
                .password("123")
                .roles("user")
            ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //释放静态资源
        web.ignoring().antMatchers("/js/**","/css/**","/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //允许所有用户访问"/","/jquery/**","/semantic/**","/css/**","/js/**","/images/**"
//                .antMatchers(
//                        "/jquery/**",
//                        "/css/**",
//                        "/js/**",
//                        "/images/**",
//                        "/fonts/**",
//                        "/**/favicon.ico")
//                .permitAll()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                //剩余的其他格式的请求路径，只需要认证（登录）后就可以访问。
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                /**
                 *用于前后端不分离项目  前后端分离使用 successHandler
                .defaultSuccessUrl("/index",true)//设置为true是和successForwardUrl作用一样
                .successForwardUrl("/index")
                 */
                .successHandler((req,resp,auth)->{
                    Object principal = auth.getPrincipal();
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(principal));
                    out.flush();
                    out.close();
                })
//                .failureForwardUrl("/fail")
                .failureHandler((req, resp, e) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(e.getMessage());
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                /** 注销跳转
                .logoutSuccessUrl("/index")
                .permitAll()
                 */
                .logoutSuccessHandler((req, resp, authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("注销成功");
                    out.flush();
                    out.close();
                })
                .and()
                .csrf().disable()
                //加上这个将对 没有重定向到登录页面，而是给用户一个尚未登录的提示，前端收到提示之后，再自行决定页面跳转。
                .exceptionHandling()
                .authenticationEntryPoint((req,resp,auth)->{
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write("尚未登录，请先登录");
                    out.flush();
                    out.close();
                })
           ;

    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.withUsername("farling").password("123").roles("admin").build());
        return inMemoryUserDetailsManager;
    }

    @Bean
    RoleHierarchy hierarchy(){
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }
}
