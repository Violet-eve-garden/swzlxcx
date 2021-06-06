package com.sysu.swzl.config;
import com.sysu.swzl.common.R;
import com.sysu.swzl.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 49367
 * @date 2021/5/23 18:27
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//表示开启全局方法注解，可在指定方法上面添加注解指定权限，需含有指定权限才可调用(基于表达式的权限控制)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter; // JWT 拦截器


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 请根据自身业务进行扩展
        // 去掉 CSRF
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //关闭session管理，使用token机制处理
                .and()
                .httpBasic().authenticationEntryPoint(new AjaxAuthenticationEntryPoint())
                //.and().antMatcher("/login")
                //.and().authorizeRequests().anyRequest().access("@rbacauthorityservice.hasPermission(request,authentication)")// 自定义权限校验  RBAC 动态 url 认证
                .and().authorizeRequests().antMatchers("/hello").permitAll()
                //.and().authorizeRequests().antMatchers("/search/**").permitAll()
                .and().authorizeRequests().antMatchers("/login").permitAll()
                .and().authorizeRequests().antMatchers("/img/upload/**").permitAll()
                .and().authorizeRequests().antMatchers("/push/uploadImg").permitAll()
                .and().authorizeRequests().antMatchers("/**").hasAuthority("common")
                .and().authorizeRequests().antMatchers("/admin/**").hasAuthority("admin"); //自定义登录

        http.exceptionHandling().accessDeniedHandler(new AjaxAccessDeniedHandler()); // 无权访问 JSON 格式的数据
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class); // JWT Filter

    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults(){
        return new GrantedAuthorityDefaults("");//remove the ROLE_ prefix
    }

    public class AjaxAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
            request.setCharacterEncoding("utf-8");
            response.getWriter().write(R.error(HttpStatus.FORBIDDEN.value(), "用户未登录").toString());
        }
    }

    public class AjaxAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(R.error(HttpStatus.FORBIDDEN.value(), "无权访问").toString());
        }
    }


}

