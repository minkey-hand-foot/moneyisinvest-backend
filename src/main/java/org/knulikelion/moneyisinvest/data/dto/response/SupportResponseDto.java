package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SupportResponseDto {
    private Long supportId;
    private String title;
    private String contents;
    private String comment;
    private String closedDate;
    private boolean closed;
    private String status;
    private String createdAt;
    private String updatedAt;
}
