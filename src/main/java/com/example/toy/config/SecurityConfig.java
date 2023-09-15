package com.example.toy.config;

import com.example.toy.handler.CustomLoginFailureHandler;
import com.example.toy.handler.CustomLoginSuccessHandler;
import com.example.toy.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider()).userDetailsService(customUserDetailsService());
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 접근 권한
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/signup").permitAll()
                .mvcMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated();

        // 폼 로그인
        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureHandler(failureHandler())
                .successHandler(successHandler())
                .usernameParameter("userId")
                .permitAll()
                .and()
                .csrf().disable();

        // 로그아웃
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("SESSION", "JSESSIONID");

        // 중복 로그인
        http.sessionManagement()
                .invalidSessionUrl("/login?invalidSession")
                .sessionAuthenticationErrorUrl("/login?maximumSessions")
                .maximumSessions(1) // 최대 허용 세션 수
                .maxSessionsPreventsLogin(false) // 중복 로그인하면 기존 세션 만료
                .expiredUrl("/login?expiredSession");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler successHandler(){
        SimpleUrlAuthenticationSuccessHandler successHandler = new CustomLoginSuccessHandler();
        successHandler.setDefaultTargetUrl("/");
        return successHandler;
    }

    @Bean
    public AuthenticationFailureHandler failureHandler(){
        return new CustomLoginFailureHandler();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
        customAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return customAuthenticationProvider;
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }
}