package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReplyCommentRequestDto {
    private Long targetCommentId;
    private String comment;
}
