package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockPriceResponseDto {
    private String currnet_time;
    /*현재 시간*/

    private String stock_status_code;
    /*종목 상태 구분 코드 iscd_stat_cls_code*/

    private String stock_market_index;
    /*대표 시장 예) KOSPI200 rprs_mrkt_kor_name*/

    private String business_type;
    /*업종 bstp_kor_isnm */

    private String stock_price;
    /*주식 현재가 stck_prpr*/

    private String stock_coin;
    /*주식 스톡가*/

    private String preparation_day_before;
    /*전일 대비 예) -5600 prdy_vrss*/

    private String preparation_day_before_sign;
    /*전일 대비 부호 1 : 상한 2 : 상승 3 : 보합 4 : 하한 5 : 하락 prdy_vrss_sign*/

    private String preparation_day_before_rate;
    /*전일 대비율 예) -4.48 prdy_ctrt*/

    private String stock_open_price;
    /*주식 시가 stck_oprc*/

    private String stock_high_price;
    /*주식 최고가 stck_hgpr*/

    private String stock_low_price;
    /*주식 최저가 stck_lwpr*/

    private String stock_max_price;
    /*주식 상한가 stck_mxpr*/

    private String stock_price_floor;
    /*주식 하한가 stck_llam*/

    private String stock_base_price;
    /*주식 기준가 stck_sdpr*/

    private String weighted_average_stock_price;
    /*가중 평균 주식 가격 wghn_avrg_stck_prc*/

    private String per;
    /*주가 수익 비율 낮을 수록 저평가 되고 있다는 뜻 per*/

    private String pbr;
    /*해당 회사가 자기 주식을 얼마나 가지고 있냐 pbr*/
}
