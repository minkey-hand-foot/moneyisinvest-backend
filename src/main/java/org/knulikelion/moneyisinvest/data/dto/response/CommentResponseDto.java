package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CommentResponseDto {
    private Long id;
    private String comment;
    private String uid;
    private String name;
    private String createdAt;
    private String updatedAt;
}
