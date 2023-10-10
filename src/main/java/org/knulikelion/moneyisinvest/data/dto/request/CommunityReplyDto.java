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
    private boolean wroteUser;
}
