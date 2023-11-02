package org.knulikelion.moneyisinvest.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.knulikelion.moneyisinvest.data.dto.request.TokenRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.ReissueTokenResponseDto;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.MessageQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService; // Spring Security 에서 제공하는 서비스 레이어
    private final UserRepository userRepository;
    private final MessageQueueService messageQueueService;

    @Resource(name = "tokenTemplate")
    private RedisTemplate<String, String> redisTemplate;

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

    public ResponseEntity<ReissueTokenResponseDto> refreshToken(TokenRequestDto tokenRequestDto) {
        log.info("[Refresh Token] Access Token 재발급 시작");

//        토큰 유효성 검사
        if(!validateToken(tokenRequestDto.getRefreshToken())) {
            log.error("[Refresh Token] 유효하지 않은 Refresh Token");
            throw new RuntimeException("유효하지 않은 토큰");
        }

//        Redis 토큰 유효성 검사
        if(!tokenRequestDto.getRefreshToken().equals(redisTemplate.opsForValue().get(
                "RT:" + getUsername(tokenRequestDto.getRefreshToken())))) {
            log.error("[Refresh Token] Redis에서 유효한 키의 값을 찾을 수 없음");
            throw new RuntimeException("유효하지 않은 토큰");
        }

//        Access Token, Refresh Token 재발급
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(tokenRequestDto.getRefreshToken()));
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String REFRESH_TOKEN = createRefreshToken(userDetails.getUsername());
            String key = "RT:" + userDetails.getUsername();
            log.info("[Refresh Token] 재발급된 Refresh Token을 Redis에 다시 저장");
            redisTemplate.opsForValue().set(key, REFRESH_TOKEN, 1209600, TimeUnit.SECONDS);

            log.info("[Refresh Token] Access Token과 Refresh Token 재발급 완료");
            return ResponseEntity.status(HttpStatus.OK).body(ReissueTokenResponseDto.builder()
                    .accessToken(createAccessToken(userDetails.getUsername(), roles))
                    .refreshToken(REFRESH_TOKEN)
                    .build());
        } catch (Exception e) {
            log.error("[Refresh Token] 에러 발생 {}", e);
            throw new RuntimeException(e);
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
        if(!validateUseAble(token)) {
            throw new RuntimeException();
        }
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
        if(!validateUseAble(token)) {
            return false;
        }

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            LOGGER.info("[validateToken] 토큰 유효 체크 완료");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }

    public Long getTokenExpirationTime(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return claims.getBody().getExpiration().getTime();
    }

    public boolean validateUseAble(String token) {
        User user = userRepository.getByUid(getUsername(token));
        System.out.println(user.isUseAble());
        return user.isUseAble();
    }
}
