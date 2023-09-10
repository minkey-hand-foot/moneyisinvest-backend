package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.data.dto.request.*;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.SignInResultDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignUpResultDto;
import org.knulikelion.moneyisinvest.data.dto.response.TokenResponseDto;
import org.knulikelion.moneyisinvest.service.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SignController {
    private final Logger LOGGER = LoggerFactory.getLogger(SignController.class);
    private final SignService signService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SignController(SignService signService, JwtTokenProvider jwtTokenProvider) {
        this.signService = signService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/sign-in")
    public SignInResultDto signIn(@RequestBody SignInRequestDto signInRequestDto) throws RuntimeException {
        LOGGER.info("[signIn] 로그인을 시도하고 있습니다. id : {}, pw : ****", signInRequestDto.getUid());
        SignInResultDto signInResultDto = signService.signIn(signInRequestDto);

        if (signInResultDto.getCode() == 0) {
            LOGGER.info("[signIn] 정상적으로 로그인되었습니다. id : {}, token : {}", signInRequestDto.getUid(),
                    signInResultDto.getToken());
        }
        return signInResultDto;
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequestDto tokenRequestDto){
        String accessToken = jwtTokenProvider.refreshToken(tokenRequestDto.getRefreshToken(), jwtTokenProvider.getUsername(tokenRequestDto.getRefreshToken()));
        return ResponseEntity.ok(new TokenResponseDto(accessToken));
    }

    @PostMapping(value = "/social/kakao")
    public ResponseEntity<SignInResultDto> kakaoLogin(@RequestParam String code) throws IOException, InterruptedException {
        return ResponseEntity.ok(signService.kakaoLogin(code));
    }

    @PostMapping(value = "/sign-up")
    public SignUpResultDto signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        LOGGER.info("[signUp] 회원가입을 수행합니다. id : {}, password : ****, name : {}", signUpRequestDto.getUid(),
                signUpRequestDto.getName());
        SignUpResultDto signUpResultDto = signService.signUp(signUpRequestDto);

        if(signUpResultDto.getCode() == 1) {
            LOGGER.info("[signUp] 회원가입을 완료할 수 없습니다. id : {}", signUpRequestDto.getUid());
        } else {
            LOGGER.info("[signUp] 회원가입을 완료했습니다. id : {}", signUpRequestDto.getUid());
        }

        return signUpResultDto;
    }

    @PostMapping(value = "/change-passwd")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    public BaseResponseDto changePasswd(@RequestBody ChangePasswdRequestDto changePasswdRequestDto, HttpServletRequest request) {
        return signService.changePasswd(
                changePasswdRequestDto,
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
    }

    @PostMapping("/change-name")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    public BaseResponseDto changeName(@RequestBody ChangeNameRequestDto changeNameRequestDto, HttpServletRequest request) {
        return signService.changeName(
                changeNameRequestDto,
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
    }

    @PostMapping(value = "/un-register")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    public BaseResponseDto unRegister(@RequestBody UnRegisterRequestDto unRegisterRequestDto, HttpServletRequest request) {
        return signService.unRegister(
                unRegisterRequestDto,
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
        );
    }

    @GetMapping(value = "/exception")
    public void exceptionTest() throws RuntimeException {
        throw new RuntimeException("접근이 금지되었습니다.");
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        LOGGER.error("ExceptionHandler 호출, {}, {}", e.getCause(), e.getMessage());

        Map<String, String> map = new HashMap<>();
        map.put("error type", httpStatus.getReasonPhrase());
        map.put("code", "400");
        map.put("message", "에러 발생");

        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }
}
