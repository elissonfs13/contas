package com.contasapagar.config;

import com.contasapagar.filter.CustomBasicAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  protected SecurityFilterChain filterChain(
      HttpSecurity http,
      CustomBasicAuthFilter customBasicAuthFilter) throws Exception {

    http
        .authorizeHttpRequests(
            authorizeConfig -> {
              authorizeConfig.anyRequest().authenticated();
            }
        )
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilterBefore(customBasicAuthFilter, BasicAuthenticationFilter.class)
        .csrf().disable();

    return http.build();
  }
}
