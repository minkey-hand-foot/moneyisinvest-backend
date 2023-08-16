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
//                UI 미사용으로 기본 설정 비활성화
        httpSecurity.httpBasic().disable() // REST API는 UI를 사용하지 않으므로 기본설정을 비활성화

//                CSRF 비활성화
                .csrf().disable() // REST API는 csrf 보안이 필요 없으므로 비활성화

                .sessionManagement()

//                Session 미사용으로 비활성화
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS)

                .and()
//                아래부터 Request 사용 권한 체크
                .authorizeRequests()
//                로그인, 회원가입 허용
                .antMatchers("/api/v1/sign-in", "/api/v1/sign-up").permitAll()
//                상점 기능 User 허용
                .antMatchers("/api/v1/shop/**").hasRole("USER")
//                커뮤니티 기능 User 허용
                .antMatchers("/api/v1/community/post").hasRole("USER")
                .antMatchers("/api/v1/community/remove").hasRole("USER")
                .antMatchers("/api/v1/community/update").hasRole("USER")
                .antMatchers("/api/v1/community/reply").hasRole("USER")
                .antMatchers("/api/v1/community/get").permitAll()
                .antMatchers("/api/detail").permitAll()

//                프로필 사진 조회: 전체. 프로필 업로드 및 조회: User
                .antMatchers("/api/v1/profile/images/**").permitAll()
                .antMatchers("/api/v1/profile/get", "/api/v1/profile/upload").hasRole("USER")
                .antMatchers("/api/v1/profile/user/detail").hasRole("USER")
//                주식 관련 전체 허용
                .antMatchers("/api/v1/stock/**").permitAll()
                .antMatchers("/api/v1/stock/buy").hasRole("USER")
                .antMatchers("/api/v1/stock/sell").hasRole("USER")
                .antMatchers("/stock").permitAll()
                .antMatchers("/stockRank").permitAll()
//                코인 관련 임시 전체 허용
                .antMatchers("/api/v1/coin/**").permitAll()
//                코인 get 요청 User 허용
                .antMatchers("/api/v1/coin/get/**").hasRole("USER")
//                카카오페이 결제 전체 허용
                .antMatchers("/api/v1/payment/kakao/pay").hasRole("USER")
                .antMatchers("/api/v1/payment/kakao/success").permitAll()
                .antMatchers("/api/v1/payment/kakao/cancel").permitAll()
                .antMatchers("/api/v1/payment/kakao/fail").permitAll()
                .antMatchers("/api/v1/favorite/post").hasRole("USER")
                .antMatchers("/api/v1/favorite/remove").hasRole("USER")
                .antMatchers("/api/v1/favorite/get").hasRole("USER")
                .antMatchers("/api/v1/support/**").hasRole("USER")

//                이외 요청 Admin 권한 요청 가능
                .anyRequest().hasRole("ADMIN")

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
        webSecurity.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**", "/images/**");
    }
}