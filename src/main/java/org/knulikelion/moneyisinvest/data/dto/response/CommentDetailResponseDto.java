package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;
import org.knulikelion.moneyisinvest.data.dto.request.CommunityReplyDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CommentDetailResponseDto {
    private Long id;
    private String comment;
    private String profileUrl;
    private String uid;
    private String name;
    private List<CommentDetailResponseDto> communityReply;
    private Long replyCount;
    private String createdAt;
    private String updatedAt;
    private boolean wroteUser;
}
