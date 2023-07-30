package org.knulikelion.moneyisinvest.data.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PostDto {
    private String title;
    private String contents;
}
