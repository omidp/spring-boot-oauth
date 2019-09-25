package com.example.oauthserver;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableAuthorizationServer
@Configuration
public class AuthConfig extends AuthorizationServerConfigurerAdapter
{

    private AuthenticationManager authenticationManager;
    private DataSource dataSource;
    private PasswordEncoder passwordEncoder;

    public AuthConfig(AuthenticationConfiguration authenticationConf, DataSource dataSource, PasswordEncoder passwordEncoder)
            throws Exception
    {
        this.authenticationManager = authenticationConf.getAuthenticationManager();
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer)
    {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()").passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception
    {
        // @formatter:off
        clients.inMemory().withClient("reader").authorizedGrantTypes("password")
                .secret("$2a$04$j7QESBff8ycCsBTqGCUFoevy4tEL7bMJ/pQdaBTKMEQmOMRBpxb72")
                .scopes("message:read")
                .accessTokenValiditySeconds(600_000_000)
                .and()
                .withClient("writer")
                .authorizedGrantTypes("password")
                // secret
                .secret("$2a$04$j7QESBff8ycCsBTqGCUFoevy4tEL7bMJ/pQdaBTKMEQmOMRBpxb72")
                .scopes("message:write")
                .accessTokenValiditySeconds(600_000_000);
        // @formatter:on
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
    {
        // @formatter:off
        endpoints.authenticationManager(this.authenticationManager).tokenStore(tokenStore());
        // @formatter:on
    }

    @Bean
    public TokenStore tokenStore()
    {
        return new JdbcTokenStore(dataSource);
    }

}

@Configuration
class UserConfig extends WebSecurityConfigurerAdapter
{

    private PasswordEncoder passwordEncoder;

    public UserConfig(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http        
        .authorizeRequests()
            .antMatchers("/api/**").authenticated()
            .and()
        .httpBasic()
        ;
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService()
    {
        return new InMemoryUserDetailsManager(
                User.builder()
                .passwordEncoder(passwordEncoder::encode)
                .username("subject")
                .password("pass")
                .roles("USER").build());
    }
}