package com.ybveg.auth.config;

import com.ybveg.auth.AuthScanner;
import com.ybveg.auth.token.TokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther zbb
 * @create 2017/8/10
 */
@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class AuthAutoConfiguration {


  @Value("auth.module.scan")
  private String scan;  //模块扫描路径

  @Autowired
  private TokenProperties properties;

  @Bean
  public AuthScanner initScanner() {
    return new AuthScanner(scan);
  }

  @Bean
  public TokenFactory initTokenFactory() {
    return new TokenFactory(properties);
  }

}
