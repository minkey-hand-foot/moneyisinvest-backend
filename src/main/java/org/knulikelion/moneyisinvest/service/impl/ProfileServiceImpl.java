package org.knulikelion.moneyisinvest.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.MypageResponseDto;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final AmazonS3 amazonS3Client;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private static final String BUCKET_NAME = "moneyisinvest";

    @Autowired
    public ProfileServiceImpl(AmazonS3 amazonS3Client, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.amazonS3Client = amazonS3Client;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public MypageResponseDto getUserDetail(String token) {
        User user = userRepository.getByUid(jwtTokenProvider.getUsername(token));

        MypageResponseDto mypageResponseDto = new MypageResponseDto();
        mypageResponseDto.setName(user.getName());
        mypageResponseDto.setUid(user.getUid());
        mypageResponseDto.setProfileUrl(user.getProfileUrl());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.E", Locale.KOREAN);

        ZonedDateTime zdt = user.getRecentLoggedIn().atZone(ZoneId.systemDefault());

        mypageResponseDto.setRecentLoggedIn(formatter.format(zdt));

        if(user.getPhoneNum() == null) {
            mypageResponseDto.setPhoneNum(null);
        } else {
            mypageResponseDto.setPhoneNum(user.getPhoneNum());
        }
        
        return mypageResponseDto;
    }

    @Override
    public BaseResponseDto resetProfile(String uid) {
        User user = userRepository.getByUid(uid);
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        String DEFAULT_PROFILE = "https://kr.object.ncloudstorage.com/moneyisinvest/default-profile.png";

        if(user.getProfileUrl().equals(DEFAULT_PROFILE)) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("이미 기본 프로필로 설정되어 있습니다.");
        } else {
            user.setProfileUrl(DEFAULT_PROFILE);
            userRepository.save(user);
            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("기본 프로필로 변경되었습니다.");
        }

        return baseResponseDto;
    }

    @Override
    public ResponseEntity<?> setUserPhoneNum(String phoneNum, String uid) {
        User user = userRepository.getByUid(uid);

        String PHONE_REG = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";

        if(phoneNum.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponseDto.builder()
                            .success(false)
                            .msg("입력된 값이 없습니다.")
                    .build()
            );
        }

        if(user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    BaseResponseDto.builder()
                            .success(false)
                            .msg("해당 사용자를 찾을 수 없습니다.")
                    .build()
            );
        }

        if(!phoneNum.matches(PHONE_REG)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    BaseResponseDto.builder()
                            .success(false)
                            .msg("휴대폰 번호는 '010-XXXX-XXXX' 형식으로 입력되어야 합니다.")
                            .build()
            );
        }

        user.setPhoneNum(phoneNum);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.builder()
                        .success(true)
                        .msg("휴대폰 번호 등록이 완료되었습니다.")
                .build()
        );
    }

    @Override
    public BaseResponseDto storeFile(MultipartFile file, String uid) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        User user = userRepository.getByUid(uid);
        if(user == null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자를 찾을 수 없습니다.");

            return baseResponseDto;
        } else {
            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME,
                        file.getOriginalFilename(), file.getInputStream(), metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead);

                amazonS3Client.putObject(putObjectRequest);

                baseResponseDto.setSuccess(true);
                baseResponseDto.setMsg(amazonS3Client.getUrl(BUCKET_NAME, file.getOriginalFilename()).toString());

                user.setProfileUrl(amazonS3Client.getUrl(BUCKET_NAME, file.getOriginalFilename()).toString());
                userRepository.save(user);

                return baseResponseDto;
            } catch (Exception e) {
                baseResponseDto.setSuccess(false);
                baseResponseDto.setMsg(HttpStatus.INTERNAL_SERVER_ERROR.toString());

                return baseResponseDto;
            }
        }
    }
}
