package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;
import org.knulikelion.moneyisinvest.data.entity.CommunityReply;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CommunityReplyDto {
    private Long id;
    private String comment;
    private String uid;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommunityReplyDto(CommunityReply communityReply) {
        this.id = communityReply.getId();
        this.comment = communityReply.getComment();
        this.uid = communityReply.getUser().getUid();
        this.name = communityReply.getUser().getName();
        this.createdAt = communityReply.getCreatedAt();
        this.updatedAt = communityReply.getUpdatedAt();
    }
}
