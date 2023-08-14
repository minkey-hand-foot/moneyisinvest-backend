package org.knulikelion.moneyisinvest.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic().disable() // REST API는 UI를 사용하지 않으므로 기본설정을 비활성화

                .csrf().disable() // REST API는 csrf 보안이 필요 없으므로 비활성화

                .sessionManagement()
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS) // JWT Token 인증방식으로 세션은 필요 없으므로 비활성화

                .and()
                .authorizeRequests() // 리퀘스트에 대한 사용권한 체크
                .antMatchers("/api/v1/sign-in", "/api/v1/sign-up",
                        "/api/v1/" +
                                "exception").permitAll()
                .antMatchers("/api/v1/shop/**").hasRole("USER")
                .antMatchers("/api/v1/community/**").hasRole("USER")
                .antMatchers("/api/v1/profile/images/**").permitAll()
                .antMatchers("/api/v1/profile/get", "/api/v1/profile/upload").hasRole("USER")
                .antMatchers("**exception**").permitAll()
                .antMatchers("/api/v1/stock/**").permitAll()
                .antMatchers("/stock").permitAll()
                .antMatchers("/stockRank").permitAll()
                .antMatchers("/api/v1/coin/**").permitAll()
                .antMatchers("/api/v1/coin/get/**").hasRole("USER")
                .antMatchers("/api/v1/profile/**").hasRole("USER")
                .antMatchers("/api/v1/user/detail").hasRole("USER")
                .antMatchers("/api/v1/payment/kakao/pay").hasRole("USER")
                .antMatchers("/api/v1/payment/kakao/success").permitAll()
                .antMatchers("/api/v1/payment/kakao/cancel").permitAll()
                .antMatchers("/api/v1/payment/kakao/fail").permitAll()
                .antMatchers("/api/v1/favorite/add").permitAll()
                .antMatchers("/api/v1/favorite/remove").permitAll()
                .antMatchers("/api/v1/favorite/get").permitAll()
                .antMatchers("/api/v1/support/post").permitAll()
                .antMatchers("/api/v1/support/getOne").permitAll()
                .antMatchers("/api/v1/support/getAll").permitAll()
                .antMatchers("/api/v1/support/remove").permitAll()

                .anyRequest().hasRole("ADMIN") // 나머지 요청은 인증된 ADMIN만 접근 가능

                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class); // JWT Token 필터를 id/password 인증 필터 이전에 추가
    }



    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception");
    }
}