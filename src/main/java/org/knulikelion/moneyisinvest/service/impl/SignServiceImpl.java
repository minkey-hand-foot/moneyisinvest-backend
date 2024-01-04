package org.knulikelion.moneyisinvest.service.impl;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.knulikelion.moneyisinvest.common.CommonResponse;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.*;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignInResultDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignUpResultDto;
import org.knulikelion.moneyisinvest.data.entity.GoogleUser;
import org.knulikelion.moneyisinvest.data.entity.KakaoUser;
import org.knulikelion.moneyisinvest.data.entity.StockCoinBenefit;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.enums.RegisterType;
import org.knulikelion.moneyisinvest.data.repository.StockCoinBenefitRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.knulikelion.moneyisinvest.service.SignService;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import org.json.JSONArray;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

@Service
@Slf4j
public class SignServiceImpl implements SignService {
    @Resource(name = "tokenTemplate")
    private RedisTemplate<String, String> redisTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(SignServiceImpl.class);

    public UserRepository userRepository;
    public ProfileService profileService;
    public JwtTokenProvider jwtTokenProvider;
    public StockCoinService stockCoinService;
    public PasswordEncoder passwordEncoder;
    public StockCoinWalletService stockCoinWalletService;
    public StockCoinBenefitRepository stockCoinBenefitRepository;

    @Autowired
    public SignServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder, ProfileService profileService,
                           StockCoinWalletService stockCoinWalletService, StockCoinService stockCoinService,
                           StockCoinBenefitRepository stockCoinBenefitRepository) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.stockCoinWalletService = stockCoinWalletService;
        this.stockCoinService = stockCoinService;
        this.stockCoinBenefitRepository = stockCoinBenefitRepository;
    }

    @Value("${KAKAO.CLIENT.ID}")
    private String kakaoClientId;

    @Value("${KAKAO.REDIRECT.URI}")
    private String kakaoRedirectUri;

    @Value("${GOOGLE.APP.KEY}")
    private String googleAppKey;

    private static final String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

    private final String DEFAULT_PROFILE = "https://kr.object.ncloudstorage.com/moneyisinvest/default-profile.png";

    // 주어진 이메일 주소가 유효한지 검증하는 메서드
    public static boolean validateUid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public SignUpResultDto signUp(SignUpRequestDto signUpRequestDto) {
        LOGGER.info("[getSignUpResult] 회원가입 유효성 검사 진행");
        SignUpResultDto signUpResultDto = new SignInResultDto();

//        휴대폰 번호, 비밀번호 정규식 정의
        String PHONE_REG = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";
        String PASS_REG = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

        if(!validateUid(signUpRequestDto.getUid())) {
            signUpResultDto.setSuccess(false);
            signUpResultDto.setMsg("아이디는 이메일 주소 형식입니다.");
            signUpResultDto.setCode(-1);

            return signUpResultDto;
        } else if(userRepository.findByUid(signUpRequestDto.getUid()).isPresent()) {
            signUpResultDto.setSuccess(false);
            signUpResultDto.setMsg("이미 가입된 회원");
            signUpResultDto.setCode(-1);

            return signUpResultDto;
        } else if(!signUpRequestDto.getPhoneNum().matches(PHONE_REG)) {
            signUpResultDto.setSuccess(false);
            signUpResultDto.setMsg("휴대폰 번호는 '010-XXXX-XXXX' 형식으로 입력되어야 합니다.");
            signUpResultDto.setCode(-1);

            return signUpResultDto;
        } else if(!signUpRequestDto.getPassword().matches(PASS_REG)) {
            signUpResultDto.setSuccess(false);
            signUpResultDto.setMsg("비밀번호 정규식이 일치하지 않습니다.");
            signUpResultDto.setCode(-1);

            return signUpResultDto;
        }

        LOGGER.info("[getSignUpResult] 회원 가입 정보 전달");
//      회원가입 시 기본적으로 일반 유저 권한으로 가입 처리
        User user = User.builder()
                    .uid(signUpRequestDto.getUid())
                    .name(signUpRequestDto.getName())
                    .plan("basic")
                    .createdAt(LocalDateTime.now())
                    .useAble(true)
                    .registerType(RegisterType.WEB)
//                    .phoneNum(
//                            (signUpRequestDto.getPhoneNum() == null) ? "010-0000-0000" : signUpRequestDto.getPhoneNum()
//                    )
                    .phoneNum("010-0000-0000")
                    .recentLoggedIn(LocalDateTime.now())
                    .profileUrl(DEFAULT_PROFILE)
                    .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

        LOGGER.info("사용자의 새 지갑 생성, UID: " + signUpRequestDto.getUid());
        BaseResponseDto walletResult = stockCoinWalletService.createWallet(signUpRequestDto.getUid());

        if(walletResult.isSuccess()) {
            stockCoinService.giveSignUpCoin(walletResult.getMsg());
        }

        stockCoinBenefitRepository.save(StockCoinBenefit.builder()
                        .benefit(0)
                        .user(user)
                        .benefitAmount(0)
                        .loss(0)
                        .loseAmount(0)
                .build());
            

        User savedUser = userRepository.save(user);
        LOGGER.info("[getSignUpResult] userEntity 값이 들어왔는지 확인 후 결과값 주입");
        if (!savedUser.getName().isEmpty()) {
            LOGGER.info("[getSignUpResult] 정상 처리 완료");
            setSuccessResult(signUpResultDto);
        } else {
            LOGGER.info("[getSignUpResult] 실패 처리 완료");
            setFailResult(signUpResultDto);
        }

        return signUpResultDto;
    }

    @Override
    public SignInResultDto signIn(SignInRequestDto signInRequestDto) throws RuntimeException {
        LOGGER.info("[getSignInResult] signDataHandler 로 회원 정보 요청");
        User user = userRepository.getByUid(signInRequestDto.getUid());
        LOGGER.info("[getSignInResult] Id : {}", signInRequestDto.getUid());

        LOGGER.info("[getSignInResult] 패스워드 비교 수행");
        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }
        LOGGER.info("[getSignInResult] 패스워드 일치");

        if(!user.isUseAble()) {
            throw new RuntimeException();
        }

//        최근 로그인 일시 저장
        user.setRecentLoggedIn(LocalDateTime.now());
        userRepository.save(user);

        String ACCESS_TOKEN = jwtTokenProvider.createAccessToken(String.valueOf(user.getUid()), user.getRoles());
        String REFRESH_TOKEN = jwtTokenProvider.createRefreshToken(String.valueOf(user.getUid()));

        LOGGER.info("[getSignInResult] SignInResultDto 객체 생성");
        SignInResultDto signInResultDto = SignInResultDto.builder()
                .token(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .uid(user.getUid())
                .name(user.getName())
                .build();

        log.info("[SignIn] 사용자 Refresh Token 값을 Redis에 저장");
        String key = "RT:" + user.getUid();
        redisTemplate.opsForValue().set(key, REFRESH_TOKEN, 1209600, TimeUnit.SECONDS);

        LOGGER.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        setSuccessResult(signInResultDto);

        return signInResultDto;
    }

//    카카오 로그인 시 사용자 토큰 받기
    public String createKakaoToken(String code) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kauth.kakao.com/oauth/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=" + "authorization_code" +
                                "&client_id=" + kakaoClientId +
                                "&redirect_uri=" + kakaoRedirectUri +
                                "&code=" + code
                ))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonObject = new JSONObject(response.body());

        return jsonObject.getString("access_token");
    }

//    카카오 로그인 시 사용자 토큰으로 사용자 정보 가져오기
    public KakaoUser getKakaoInfo(String accessToken) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kapi.kakao.com/v2/user/me"))
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonObject = new JSONObject(response.body());

        // Accessing the nested JSON Objects
        JSONObject kakao_account = jsonObject.getJSONObject("kakao_account");
        JSONObject profile = kakao_account.getJSONObject("profile");

        KakaoUser kakaoUser = new KakaoUser();
        kakaoUser.setEmail(kakao_account.getString("email"));
        kakaoUser.setNickname(profile.getString("nickname"));
        kakaoUser.setProfileImageUrl(profile.getString("profile_image_url"));

        return kakaoUser;
    }

//    카카오 로그인 시 랜덤 비밀번호 생성
    public String getRandomPassword() {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";

        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);
        }

        return sb.toString();
    }

    @Override
    public SignInResultDto kakaoLogin(String code) throws RuntimeException, IOException, InterruptedException {
        String kakaoUserToken = createKakaoToken(code);
        KakaoUser kakaoUser = getKakaoInfo(kakaoUserToken);
        Optional<User> user = userRepository.findByUid(kakaoUser.getEmail());

//        카카오톡 로그인 시, 이미 가입된 회원이라면
        if(user.isPresent()) {
            User foundUser = user.get();

            foundUser.setRecentLoggedIn(LocalDateTime.now());
            userRepository.save(foundUser);

            SignInResultDto signInResultDto = SignInResultDto.builder()
                    .token(jwtTokenProvider.createAccessToken(String.valueOf(foundUser.getUid()), foundUser.getRoles()))
                    .refreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(foundUser.getUid())))
                    .uid(foundUser.getUid())
                    .name(foundUser.getName())
                    .build();

            setSuccessResult(signInResultDto);

            return signInResultDto;
        } else {
//            카카오 프로필 이미지를 가져올 수 없을 때, 서비스 기본 프로필 사용
            if(kakaoUser.getProfileImageUrl() == null) {
                kakaoUser.setProfileImageUrl(DEFAULT_PROFILE);
            }

            if(kakaoUser.getEmail() == null || kakaoUser.getNickname() == null) {
                throw new RuntimeException("카카오 정보에서 이메일 또는 닉네임을 가져올 수 없습니다");
            }

            User newUser = User.builder()
                    .uid(kakaoUser.getEmail())
                    .name(kakaoUser.getNickname())
                    .plan("basic")
                    .createdAt(LocalDateTime.now())
                    .useAble(true)
//                    휴대폰 번호 임시 Pass
                    .phoneNum("010-0000-0000")
                    .registerType(RegisterType.KAKAO)
                    .profileUrl(kakaoUser.getProfileImageUrl())
                    .password(passwordEncoder.encode(getRandomPassword()))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

            BaseResponseDto walletResult = stockCoinWalletService.createWallet(newUser.getUid());

            if(walletResult.isSuccess()) {
                stockCoinService.giveSignUpCoin(walletResult.getMsg());
            }

            if(!validateUid(newUser.getUid())) {
                throw new RuntimeException("이메일 주소 형식이 아닙니다.");
            } else if(userRepository.getByUid(kakaoUser.getEmail()) != null) {
                throw new RuntimeException("이미 존재하는 회원입니다.");
            } else {
                stockCoinBenefitRepository.save(StockCoinBenefit.builder() // benefit 저장
                        .benefit(0)
                        .user(newUser)
                        .benefitAmount(0)
                        .loss(0)
                        .loseAmount(0)
                        .build());
                userRepository.save(newUser);
            }

//            회원가입 후 로그인 정보 반환
            SignInResultDto signInResultDto = SignInResultDto.builder()
                    .token(jwtTokenProvider.createAccessToken(String.valueOf(newUser.getUid()), newUser.getRoles()))
                    .refreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(newUser.getUid())))
                    .uid(newUser.getUid())
                    .name(newUser.getName())
                    .build();

            setSuccessResult(signInResultDto);

            return signInResultDto;
        }
    }

    @Override
    public BaseResponseDto changePasswd(ChangePasswdRequestDto changePasswdRequestDto, String uid) {
        User foundUser = userRepository.getByUid(uid);
        BaseResponseDto baseResponseDto = new BaseResponseDto();

//        발견된 사용자가 없을 시
        if(foundUser == null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자를 찾을 수 없습니다.");

            return baseResponseDto;
        }

//        현재 패스워드와 입력된 현재 패스워드가 일치하지 않을 시
        if(!passwordEncoder.matches(changePasswdRequestDto.getCurrentPasswd(), foundUser.getPassword())) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("기존 비밀번호가 일치하지 않습니다.");

            return baseResponseDto;
        }

//        새 패스워드와 입력된 새 패스워드 재입력 값이 일치하지 않을 시
        if(!changePasswdRequestDto.getNewPasswd().equals(changePasswdRequestDto.getNewPasswdRe())) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("새로운 비밀번호와 새로운 비밀번호 재입력이 일치하지 않습니다.");

            return baseResponseDto;
        }

        foundUser.setPassword(passwordEncoder.encode(changePasswdRequestDto.getNewPasswd()));
        userRepository.save(foundUser);

        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg("비밀번호 변경이 완료되었습니다.");

        return baseResponseDto;
    }

    @Override
    public BaseResponseDto changeName(ChangeNameRequestDto changeNameRequestDto, String uid) {
        User foundUser = userRepository.getByUid(uid);
        BaseResponseDto baseResponseDto = new BaseResponseDto();

//        사용자의 비밀번호가 일치하지 않을 때
        if(!passwordEncoder.matches(changeNameRequestDto.getCurrentPasswd(), foundUser.getPassword())) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("기존 비밀번호가 일치하지 않습니다.");

            return baseResponseDto;
        }

//        새로운 이름을 입력하지 않았을 때
        if(changeNameRequestDto.getNewName().isEmpty()) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("새 이름이 입력되지 않았습니다.");
        }

        foundUser.setName(changeNameRequestDto.getNewName());
        userRepository.save(foundUser);

        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg("이름 변경이 완료되었습니다.");

        return baseResponseDto;
    }

    @Override
    public BaseResponseDto unRegister(UnRegisterRequestDto unRegisterRequestDto, String uid) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        User foundUser = userRepository.getByUid(uid);

        if(!passwordEncoder.matches(unRegisterRequestDto.getCurrentPasswd(), foundUser.getPassword())) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("비밀번호가 일치하지 않습니다.");

            return baseResponseDto;
        }

        foundUser.setUseAble(false);
        userRepository.save(foundUser);

        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg("회원 탈퇴가 완료되었습니다.");

        return baseResponseDto;
    }

    // 인가코드로 google User 정보 가져오는 메서드
    private GoogleUser getGoogleInfo(String code) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String binaryJson = "{\"idToken\":\""+code+"\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:lookup?key="+googleAppKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(binaryJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonObject = new JSONObject(response.body());

        // 유저,
        JSONArray jsonArray = jsonObject.getJSONArray("users");
        JSONObject userObject = jsonArray.getJSONObject(0);

        GoogleUser googleUser = new GoogleUser();

        if(userObject.has("displayName")&&!userObject.getString("displayName").isEmpty()){
            googleUser.setNickname(userObject.getString("displayName"));
        }else{
            googleUser.setNickname(null);
        }
        googleUser.setEmail(userObject.getString("email"));

        if(userObject.has("photoUrl")&&!userObject.getString("photoUrl").isEmpty()){
            googleUser.setNickname(userObject.getString("displayName"));
        }else{
            googleUser.setNickname(null);
        }
        return googleUser;
    }
    @Override
    public ResponseEntity<?> googleLogin(String code) throws RuntimeException, IOException, InterruptedException {
        GoogleUser googleUser = getGoogleInfo(code);
        // Google Login 이미 가입된 회원이라면
        if(userRepository.getByUid(googleUser.getEmail()) != null) {
            User user = userRepository.getByUid(googleUser.getEmail());

            user.setRecentLoggedIn(LocalDateTime.now());
            userRepository.save(user);

            SignInResultDto signInResultDto = SignInResultDto.builder()
                    .token(jwtTokenProvider.createAccessToken(String.valueOf(user.getUid()), user.getRoles()))
                    .refreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(user.getUid())))
                    .uid(user.getUid())
                    .name(user.getName())
                    .build();

            setSuccessResult(signInResultDto);

            return ResponseEntity.ok(signInResultDto);
        } else {
            // 구글 프로필 이미지를 가져올 수 없을 때, 서비스 기본 프로필 사용
            if(googleUser.getProfileImageUrl() == null) {
                googleUser.setProfileImageUrl(DEFAULT_PROFILE);
            }

            if(googleUser.getEmail() == null || googleUser.getNickname() == null) {
                throw new RuntimeException("구글 정보에서 이메일 또는 닉네임을 가져올 수 없습니다");
            }

            User user = User.builder()
                    .uid(googleUser.getEmail())
                    .name(googleUser.getNickname())
                    .plan("basic")
                    .createdAt(LocalDateTime.now())
                    .useAble(true)
                    .registerType(RegisterType.GOOGLE)
                    .profileUrl(googleUser.getProfileImageUrl())
                    .password(passwordEncoder.encode(getRandomPassword()))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

            BaseResponseDto walletResult = stockCoinWalletService.createWallet(user.getUid());

            if(walletResult.isSuccess()) {
                stockCoinService.giveSignUpCoin(walletResult.getMsg());
            }

            if(!validateUid(user.getUid())) {
                throw new RuntimeException("이메일 주소 형식이 아닙니다.");
            } else if(userRepository.getByUid(googleUser.getEmail()) != null) {
                throw new RuntimeException("이미 존재하는 회원입니다.");
            } else {
                userRepository.save(user);
            }

//            회원가입 후 로그인 정보 반환
            SignInResultDto signInResultDto = SignInResultDto.builder()
                    .token(jwtTokenProvider.createAccessToken(String.valueOf(user.getUid()), user.getRoles()))
                    .refreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(user.getUid())))
                    .uid(user.getUid())
                    .name(user.getName())
                    .build();

            setSuccessResult(signInResultDto);

            return ResponseEntity.ok(signInResultDto);
        }
    }



    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setSuccessResult(SignUpResultDto result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    // 결과 모델에 api 요청 실패 데이터를 세팅해주는 메소드
    private void setFailResult(SignUpResultDto result) {
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }
}
