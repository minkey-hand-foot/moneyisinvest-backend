package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.MypageResponseDto;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final Path fileStorageLocation = Paths.get("./moneyisinvest/");
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public ProfileServiceImpl(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
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

        return mypageResponseDto;
    }

    @Override
    public BaseResponseDto storeFile(MultipartFile file, String uid) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Normalize file name
        String fileName = file.getOriginalFilename();

        try {
            // Creating directories
            Files.createDirectories(this.fileStorageLocation);

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            User user = userRepository.getByUid(uid);
            user.setProfileUrl(fileName);
            userRepository.save(user);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("프로필 사진 업로드가 완료되었습니다.");
        } catch (IOException e) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("Failed to store file " + fileName);
        }

        return baseResponseDto;
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found " + fileName, e);
        }
    }
}
