package com.example.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;//前面配置过
    @Autowired
    private PasswordEncoder passwordEncoder;//前面配置过

    //配置 Token 的一些基本信息
    @Bean
    AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        services.setReuseRefreshToken(true);//设置Token是否支持刷新
        services.setTokenStore(tokenStore);//设置Token的存储位置
        services.setAccessTokenValiditySeconds(60 * 60 * 2);//设置Token有效期
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);//设置Token刷新有效期
        return services;
    }


   //配置客户端的详细信息，这里存在内存中的实际上一般是存数据库
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //clientId,resourceIds,scopes,grantTypes,authorities
        clients.inMemory().withClient("client1")//配置clientId，唯一标识，表示客户端，即第三方应用
                .secret(new BCryptPasswordEncoder().encode("123456"))//客户端访问密码
                .autoApprove(true)
                //客户端支持的grant_type,多种类型可用逗号隔开
                .authorizedGrantTypes("authorization_code","refresh_token")
                //客户端申请的权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔
                .scopes("all")
                //客户端的重定向URI
                .redirectUris("http://localhost:8082/login")
                .and()
                .withClient("resource1")
                .secret(new BCryptPasswordEncoder().encode("123456"));
    }

    //打开验证Token的访问权限(后续客户端验证token使用),并允许通过表单形式提交clientSecret
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()")//Token 校验的端点，后续客户端验证Token使用
                .allowFormAuthenticationForClients()
                .passwordEncoder(passwordEncoder);
    }

    //配置令牌的访问端点和令牌服务
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authorizationCodeServices(authorizationCodeServices())
                .tokenServices(tokenServices());
    }

    //配置授权码的存储,也可以选择用数据库存储
    @Bean
    AuthorizationCodeServices authorizationCodeServices(){
        return new InMemoryAuthorizationCodeServices();
    }
}
