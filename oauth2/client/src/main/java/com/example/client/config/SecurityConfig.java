package com.example.client.config;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * Spring Security 配置
 */

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //安全策略
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http .authorizeRequests().antMatchers("/", "/login**").permitAll()
                .anyRequest()
                .authenticated();
    }

    


}
