package org.knulikelion.moneyisinvest.data.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SupportRequestDto {
    private String title;
    private String contents;
}
