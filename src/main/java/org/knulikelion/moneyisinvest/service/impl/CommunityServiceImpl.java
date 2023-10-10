package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.CommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.CommentUpdateRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.CommunityReplyDto;
import org.knulikelion.moneyisinvest.data.dto.request.ReplyCommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentDetailResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Community;
import org.knulikelion.moneyisinvest.data.entity.CommunityReply;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.CommunityReplyRepository;
import org.knulikelion.moneyisinvest.data.repository.CommunityRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityReplyRepository communityReplyRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public CommunityServiceImpl(
            CommunityRepository communityRepository,
            UserRepository userRepository,
            CommunityReplyRepository communityReplyRepository,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.communityReplyRepository = communityReplyRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public BaseResponseDto postComment(CommentRequestDto commentRequestDto, String token) {
        User foundUser = userRepository.getByUid(jwtTokenProvider.getUsername(token));

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
    public BaseResponseDto replyComment(ReplyCommentRequestDto replyCommentRequestDto, String token) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        if(userRepository.getByUid(jwtTokenProvider.getUsername(token)) == null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자를 찾을 수 없음");
        } else if(communityRepository.getById(replyCommentRequestDto.getTargetCommentId()) != null) {
            CommunityReply communityReply = new CommunityReply();

            communityReply.setCommunity(communityRepository.getById(replyCommentRequestDto.getTargetCommentId()));
            communityReply.setComment(replyCommentRequestDto.getComment());
            communityReply.setUser(userRepository.getByUid(jwtTokenProvider.getUsername(token)));
            communityReply.setUpdatedAt(LocalDateTime.now());
            communityReply.setCreatedAt(LocalDateTime.now());

            communityReplyRepository.save(communityReply);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("댓글 등록이 완료되었습니다.");
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("타겟 댓글을 찾을 수 없습니다.");
        }

        return baseResponseDto;
    }

    @Override
    public List<CommentResponseDto> getAllCommentByStockId(String stockId) {
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        List<Community> foundComments = communityRepository.findAllByStockId(stockId);

        for (Community foundComment : foundComments) {
            CommentResponseDto commentResponseDto = new CommentResponseDto();
            List<CommunityReply> communityReplyList = communityReplyRepository.findAllByCommunity_Id(foundComment.getId());

            commentResponseDto.setId(foundComment.getId());
            commentResponseDto.setComment(foundComment.getComment());
            commentResponseDto.setUid(foundComment.getUser().getUid());
            commentResponseDto.setName(foundComment.getUser().getName());
            commentResponseDto.setReplyCount((long) communityReplyList.size());
            commentResponseDto.setCreatedAt(foundComment.getCreatedAt().toString());
            commentResponseDto.setUpdatedAt(foundComment.getUpdatedAt().toString());

            commentResponseDtoList.add(commentResponseDto);
        }

        return commentResponseDtoList;
    }

    @Override
    @Transactional
    public List<CommentDetailResponseDto> getAllCommentByStockIdContainsAllReply(String stockId) {
        List<CommentDetailResponseDto> commentResponseDtoList = new ArrayList<>();
        List<Community> foundComments = communityRepository.findAllByStockId(stockId);

        for (Community foundComment : foundComments) {
            CommentDetailResponseDto commentDetailResponseDto = new CommentDetailResponseDto();

            List<CommunityReply> communityReplyList = communityReplyRepository.findAllByCommunity_Id(foundComment.getId());

            List<CommunityReplyDto> communityReplyDtoList = communityReplyList.stream()
                    .map(CommunityReplyDto::new)
                    .collect(Collectors.toList());

            commentDetailResponseDto.setId(foundComment.getId());
            commentDetailResponseDto.setComment(foundComment.getComment());
            commentDetailResponseDto.setUid(foundComment.getUser().getUid());
            commentDetailResponseDto.setName(foundComment.getUser().getName());
            commentDetailResponseDto.setCommunityReply(communityReplyDtoList);
            commentDetailResponseDto.setCreatedAt(foundComment.getCreatedAt().toString());
            commentDetailResponseDto.setUpdatedAt(foundComment.getUpdatedAt().toString());

            commentResponseDtoList.add(commentDetailResponseDto);
        }

        return commentResponseDtoList;
    }

    @Override
    public BaseResponseDto removeComment(Long id) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        Community foundComment = communityRepository.getById(id);
        List<CommunityReply> communityReplyList = communityReplyRepository.findAllByCommunity_Id(id);

        if (foundComment != null) {
            if(!communityReplyList.isEmpty()){
                for (CommunityReply temp : communityReplyList){
                    temp.setUser(null);
                    temp.setCommunity(null);
                    communityReplyRepository.delete(temp);
                }
            }
            foundComment.setUser(null);
            communityRepository.delete(foundComment);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("댓글 삭제가 완료되었습니다.");
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("해당 댓글을 찾을 수 없습니다.");
        }
        return baseResponseDto;
    }

    @Override
    public BaseResponseDto updateComment(CommentUpdateRequestDto commentUpdateRequestDto) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Community newComment = communityRepository.getById(commentUpdateRequestDto.getId());
        if(newComment != null) {
            newComment.setComment(commentUpdateRequestDto.getComment());
            newComment.setUpdatedAt(LocalDateTime.now());

            communityRepository.save(newComment);
            System.out.println(newComment);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("댓글 업데이트가 완료되었습니다.");
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("해당 댓글을 찾을 수 없습니다.");
        }
        return baseResponseDto;
    }

    @Override
    public BaseResponseDto updateReplyComment(ReplyCommentRequestDto replyCommentRequestDto, String token) {
        CommunityReply communityReply = communityReplyRepository.getById(replyCommentRequestDto.getTargetCommentId());
        User user = userRepository.getByUid(jwtTokenProvider.getUsername(token));

//        커뮤니티 답글을 등록한 사용자와 업데이트를 요청하는 사용자가 일치하지 않을 때
        if(!communityReply.getUser().equals(user)) {
            return BaseResponseDto.builder()
                    .success(false)
                    .msg("답글을 게시한 사용자가 아닙니다.")
                .build();
        }

        communityReply.setComment(replyCommentRequestDto.getComment());
        communityReply.setUpdatedAt(LocalDateTime.now());

        return BaseResponseDto.builder()
                .success(true)
                .msg("대댓글 업데이트가 완료되었습니다.")
                .build();
    }

    @Override
    public BaseResponseDto removeReplyComment(Long replyId, String token) {
        CommunityReply communityReply = communityReplyRepository.getById(replyId);
        User user = userRepository.getByUid(jwtTokenProvider.getUsername(token));

        if(communityReply == null) {
            return BaseResponseDto.builder()
                    .success(false)
                    .msg("삭제할 대댓글을 찾지 못 했습니다.")
                    .build();
        }

        if(!communityReply.getUser().equals(user)) {
            return BaseResponseDto.builder()
                    .success(false)
                    .msg("해당 대댓글을 게시한 사용자가 아닙니다.")
                    .build();
        }

        communityReplyRepository.delete(communityReply);

        return BaseResponseDto.builder()
                .success(true)
                .msg("대댓글 삭제가 완료되었습니다.")
                .build();
    }
}
