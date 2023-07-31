package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.data.dto.request.CommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Community;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.CommunityRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityServiceImpl(CommunityRepository communityRepository, UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BaseResponseDto postComment(CommentRequestDto commentRequestDto) {
        User foundUser = userRepository.findByUid(commentRequestDto.getUid());

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        if(foundUser == null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자가 존재하지 않음");
        } else {
            Community community = new Community();
            community.setStockId(commentRequestDto.getStockId());
            community.setComment(commentRequestDto.getComment());
            community.setUser(foundUser);
            community.setCreatedAt(LocalDateTime.now());
            community.setUpdatedAt(LocalDateTime.now());

            communityRepository.save(community);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("댓글 등록 됨");
        }

        return baseResponseDto;
    }

    @Override
    public List<CommentResponseDto> getAllCommentByStockId(Long stockId) {
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        List<Community> foundComments = communityRepository.findAllByStockId(stockId);

        for (Community foundComment : foundComments) {
            CommentResponseDto commentResponseDto = new CommentResponseDto();

            commentResponseDto.setComment(foundComment.getComment());
            commentResponseDto.setUid(foundComment.getUser().getUid());
            commentResponseDto.setName(foundComment.getUser().getName());
            commentResponseDto.setCreatedAt(foundComment.getCreatedAt().toString());
            commentResponseDto.setUpdatedAt(foundComment.getUpdatedAt().toString());

            commentResponseDtoList.add(commentResponseDto);
        }

        return commentResponseDtoList;
    }

}
