package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PostUpdateRequestDto {
    private Long id;
    private String title;
    private String contents;
}
