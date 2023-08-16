package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SupportResponseDto {
    private String uid;
    private String title;
    private String contents;
    private String status;
    private String createdAt;
    private String updatedAt;
}
