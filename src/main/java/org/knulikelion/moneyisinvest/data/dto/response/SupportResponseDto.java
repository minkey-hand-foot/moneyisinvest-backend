package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SupportResponseDto {
    private Long id;
    private String uid;
    private String title;
    private String contents;
    private String createdAt;
    private String updatedAt;
}
