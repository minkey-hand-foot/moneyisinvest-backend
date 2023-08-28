package org.knulikelion.moneyisinvest.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService; // Spring Security 에서 제공하는 서비스 레이어

    @Value("${springboot.jwt.secret}")
    private String secretKey = "secretKey";
    private final long accessTokenValidMillisecond = 1000L * 60 * 60; // 1시간 토큰 유효
    private final long refreshTokenValidMillisecond = 1000L * 60 * 60 * 24 * 14; /*2주 토큰 유효*/

    public String createAccessToken(String userEmail, List<String> roles){
        return createToken(userEmail, roles, accessTokenValidMillisecond);
    }

    public String createRefreshToken(String userEmail){
        return createToken(userEmail, new ArrayList<>(), refreshTokenValidMillisecond);
    }

    public String refreshToken(String refreshToken, String userEmail){
        LOGGER.info("[refreshToken] 리프레시 토큰으로 액세스 토큰 재발급 시작");

         /*리프레시 토큰 유효성 검사*/
        if (validateToken(refreshToken)){
            /*유저 이메일로 새로운 액세스 토큰 생성*/
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return createAccessToken(userEmail,roles);
        }else{
            LOGGER.error("[refreshToken] 리프레시 토큰이 유효하지 않음");
            throw new RuntimeException("Refresh token is not valid");
        }
    }

    @PostConstruct
    protected void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    // JWT 토큰 생성
    public String createToken(String userUid, List<String> roles, long validMillisecond) {
        LOGGER.info("[createToken] 토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(userUid);
        claims.put("roles", roles);

        Date now = new Date();
        String token = Jwts.builder()
//                정보 저장
                .setClaims(claims)
//                토큰 발행 시간 정보
                .setIssuedAt(now)
//                토큰 만료 시간 정보
                .setExpiration(new Date(now.getTime() + validMillisecond))
//                암호화 알고리즘, secret 값 세팅
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        LOGGER.info("[createToken] 토큰 생성 완료");
        return token;
    }

    // JWT 토큰으로 인증 정보 조회
    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {}",
                userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }

    // JWT 토큰에서 회원 구별 정보 추출
    public String getUsername(String token) {
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
                .getSubject();
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    /**
     * HTTP Request Header 에 설정된 토큰 값을 가져옴
     *
     * @param request Http Request Header
     * @return String type Token 값
     */
    public String resolveToken(HttpServletRequest request) {
        LOGGER.info("[resolveToken] HTTP 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    // JWT 토큰의 유효성 + 만료일 체크
    public boolean validateToken(String token) {
        LOGGER.info("[validateToken] 토큰 유효 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            LOGGER.info("[validateToken] 토큰 유효 체크 완료");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }
}
