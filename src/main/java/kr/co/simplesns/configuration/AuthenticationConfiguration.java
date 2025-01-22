package kr.co.simplesns.configuration;

import kr.co.simplesns.configuration.filter.JwtTokenFilter;
import kr.co.simplesns.exception.CustomAuthenticationEntryPoint;
import kr.co.simplesns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;


@Configuration
@RequiredArgsConstructor
public class AuthenticationConfiguration {

    private final UserService userService;

    @Value("${jwt.secret-key}")
    private String key;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/*/users/join", "/api/*/users/login").permitAll()
                        .requestMatchers("/api/*/users/alarm/subscribe/*").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers(new RegexRequestMatcher("^(?!/api/).*", null)).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // UsernamePasswordAuthenticationFilter 접근 이전에 JwtTokenFilter 사용
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)
                // Jwt 토큰 관련 에러가 발생했을 때 지정한대로 에러를 처리하기 위해.
                .exceptionHandling(exception -> exception
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
                // "/api/"로 시작하지 않는 요청을 제외



        return http.build();
    }





}

