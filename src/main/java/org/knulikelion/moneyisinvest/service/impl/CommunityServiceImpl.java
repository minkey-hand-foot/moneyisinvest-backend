package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.CommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.CommentUpdateRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.ReplyCommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentDetailResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Community;
import org.knulikelion.moneyisinvest.data.entity.CommunityLike;
import org.knulikelion.moneyisinvest.data.entity.CommunityReply;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.CommunityLikeRepository;
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
import java.util.Optional;

@Service
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityReplyRepository communityReplyRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommunityLikeRepository communityLikeRepository;

    @Autowired
    public CommunityServiceImpl(
            CommunityRepository communityRepository,
            UserRepository userRepository,
            CommunityReplyRepository communityReplyRepository,
            JwtTokenProvider jwtTokenProvider,
            CommunityLikeRepository communityLikeRepository
    ) {
        this.communityRepository = communityRepository;
        this.userRepository = userRepository;
        this.communityReplyRepository = communityReplyRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.communityLikeRepository = communityLikeRepository;
    }

    @Override
    @Transactional
    public BaseResponseDto likeComment(Long id, String token) {
        Optional<Community> community = communityRepository.findById(id);

        if(community.isEmpty()) {
            throw new RuntimeException("해당 댓글을 찾을 수 없습니다.");
        }

        User user = userRepository.getByUid(jwtTokenProvider.getUsername(token));
        Optional<CommunityLike> communityLike = communityLikeRepository.getCommunityLikeByCommunityIdAndUserId(
                id, user.getId());
        List<CommunityLike> communityLikeList = communityLikeRepository.getCommunityLikeByCommunityId(id);

        if(communityLike.isEmpty()) {
            communityLikeRepository.save(CommunityLike.builder()
                            .community(community.get())
                            .user(user)
                            .likedAt(LocalDateTime.now())
                    .build());

            return BaseResponseDto.builder()
                    .success(true)
                    .msg(String.valueOf(communityLikeList.size() + 1))
                    .build();
        } else {
            CommunityLike nullCommunityLike = communityLike.get();
            nullCommunityLike.setUser(null);
            nullCommunityLike.setCommunity(null);
            communityLikeRepository.save(nullCommunityLike);
            communityLikeRepository.delete(nullCommunityLike);

            return BaseResponseDto.builder()
                    .success(true)
                    .msg(String.valueOf(communityLikeList.size() - 1))
                    .build();
        }
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
        Optional<Community> community = communityRepository.findById(replyCommentRequestDto.getTargetCommentId());
        Optional<User> user = userRepository.findByUid(jwtTokenProvider.getUsername(token));

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        if(community.isEmpty()) {
            throw new RuntimeException("해당 댓글을 찾을 수 없습니다.");
        }

        communityReplyRepository.save(CommunityReply.builder()
                        .community(community.get())
                        .comment(replyCommentRequestDto.getComment())
                        .user(user.get())
                        .updatedAt(LocalDateTime.now())
                        .createdAt(LocalDateTime.now())
                .build());

        return BaseResponseDto.builder()
                .success(true)
                .msg("댓글 등록이 완료되었습니다.")
                .build();
    }

    @Override
    public List<CommentResponseDto> getAllCommentByStockId(String stockId) {
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        List<Community> foundComments = communityRepository.findAllByStockId(stockId);

        for (Community foundComment : foundComments) {
            CommentResponseDto commentResponseDto = new CommentResponseDto();
            List<CommunityReply> communityReplyList = communityReplyRepository.findAllByCommunity_Id(
                    foundComment.getId());

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
    public List<CommentDetailResponseDto> getAllCommentByStockIdContainsAllReply(String stockId, String uid) {
        List<CommentDetailResponseDto> commentResponseDtoList = new ArrayList<>();
        List<Community> foundComments = communityRepository.findAllByStockId(stockId);
        Optional<User> user = userRepository.findByUid(uid);

        for (Community foundComment : foundComments) {
            List<CommunityReply> communityReplyList = communityReplyRepository.findAllByCommunity_Id(
                    foundComment.getId());
            List<CommentDetailResponseDto> commentDetailResponseDtoList = new ArrayList<>();
            List<CommunityLike> communityLikeList = communityLikeRepository.getCommunityLikeByCommunityId(
                    foundComment.getId());

            for(CommunityReply temp : communityReplyList) {
//                댓글에 포함된 대댓글 리스트 객체
                commentDetailResponseDtoList.add(CommentDetailResponseDto.builder()
                                .id(temp.getId())
                                .comment(temp.getComment())
                                .uid(temp.getUser().getUid())
                                .name(temp.getUser().getName())
                                .profileUrl(temp.getUser().getProfileUrl())
                                .updatedAt(temp.getUpdatedAt().toString())
                                .createdAt(temp.getCreatedAt().toString())
                                .wroteUser(user.isPresent() && user.get().equals(temp.getUser()))
                        .build());
            }

//            기본적인 댓글 객체 생성
            commentResponseDtoList.add(CommentDetailResponseDto.builder()
                            .id(foundComment.getId())
                            .uid(foundComment.getUser().getUid())
                            .profileUrl(foundComment.getUser().getProfileUrl())
                            .name(foundComment.getUser().getName())
                            .comment(foundComment.getComment())
                            .replyCount((long) communityReplyList.size())
                            .communityReply(commentDetailResponseDtoList)
                            .createdAt(foundComment.getCreatedAt().toString())
                            .updatedAt(foundComment.getUpdatedAt().toString())
                            .liked(String.valueOf(communityLikeList.size()))
                            .wroteUser(user.isPresent() && user.get().equals(foundComment.getUser()))
                    .build());
        }

        return commentResponseDtoList;
    }

    @Override
    public BaseResponseDto removeComment(Long id) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        Optional<Community> community = communityRepository.findById(id);

        if(community.isEmpty()) {
            throw new RuntimeException("해당 댓글을 찾을 수 없음.");
        }

        Community foundComment = community.get();
        List<CommunityReply> communityReplyList = communityReplyRepository.findAllByCommunity_Id(id);

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
        return baseResponseDto;
    }

    @Override
    public BaseResponseDto updateComment(CommentUpdateRequestDto commentUpdateRequestDto) {
        Optional<Community> community = communityRepository.findById(commentUpdateRequestDto.getId());

        if(community.isEmpty()) {
            throw new RuntimeException("해당 댓글을 찾을 수 없습니다.");
        }

        communityRepository.save(Community.builder()
                        .id(community.get().getId())
                        .comment(commentUpdateRequestDto.getComment())
                        .stockId(community.get().getStockId())
                        .user(community.get().getUser())
                        .updatedAt(LocalDateTime.now())
                        .createdAt(community.get().getCreatedAt())
                .build());

        return BaseResponseDto.builder()
                .success(true)
                .msg("댓글 업데이트가 완료되었습니다.")
                .build();
    }

    @Override
    public BaseResponseDto updateReplyComment(ReplyCommentRequestDto replyCommentRequestDto, String token) {
        CommunityReply communityReply = communityReplyRepository.getById(replyCommentRequestDto.getTargetCommentId());
        User user = userRepository.getByUid(jwtTokenProvider.getUsername(token));

//        답글 등록 사용자, 요청 사용자 일치 여부
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
        Optional<CommunityReply> communityReply = communityReplyRepository.findById(replyId);
        Optional<User> user = userRepository.findByUid(jwtTokenProvider.getUsername(token));

        if(communityReply.isEmpty()) {
            throw new RuntimeException("해당 대댓글을 찾을 수 없습니다.");
        }

        if(user.isEmpty()) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }

        if(communityReply.get().getUser().equals(user.get())) {
            communityReplyRepository.delete(communityReply.get());
        }

        return BaseResponseDto.builder()
                .success(true)
                .msg("대댓글 삭제가 완료되었습니다.")
                .build();
    }
}
