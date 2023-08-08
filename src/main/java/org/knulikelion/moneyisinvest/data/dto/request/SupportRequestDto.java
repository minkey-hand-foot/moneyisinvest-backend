package org.knulikelion.moneyisinvest.data.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SupportRequestDto {

    private Long id;
    private String uid;
    private String title;
    private String contents;

}
