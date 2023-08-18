import React, {useState} from 'react';
import "./TbDetail.scss";
import Header from 'systems/Header';
import Footer from 'components/Footer';
import {useLocation} from 'react-router-dom';

export default function TbDetail3() {

    const [words] = useState ([
        {
            word: "주식시장 거래 시장",
            content: "09:00 ~ 15:30",
        },
        {
            word: "주가",
            content: "시장에서 주식을 사고팔 때의 가격",
        },
        {
            word: "시가",
            content: "장 시작(9:00)애 첫 거래된 주가",        
        },
        {
            word: "현재가",
            content: "현재 시점에서 장중에 거래되는 주가",        
        },
        {
            word: "종가",
            content: "장 마감(15:30)에 거래된 주가",        
        },
        {
            word: "기준가",
            content: "전일 종가",
        },
        {
            word: "상한가",
            content: "장중 오를 수 있는 최고 주가(기준가 대비 +30%)",
        },
        {
            word: "하한가",
            content: "장중 내릴 수 있는 최저 주가(기준가 대비 -30%)",
        },
        {
            word: "거래수수료",
            content: "주식을 거래할 때 발생하는 수수료, 이는 거래하는 증권사에 따라 다름",
        },
    ]);

    const tbDetailItem = words.map((item) => (
        <div className="tbDetailList">
            <div className="tbDetailItems">                
                <div className="tbDetailItemTitle">{item.word}</div>
                <div className="tbDetailItemContent">{item.content}</div>
            </div>
            
        </div>
    ))
    
    const location = useLocation();
    const tbTitle = location.state && location.state.tbTitle;

    return(
        <div className="tbDetailContainer">
            <Header />
            <div className="tbDetailBox">
                <div className="tbDetailContent">
                        <div className="tbDetailTop">
                            <div className="tbDetailTitle">교과서</div>

                        </div>
                        <div className='tbTitleInfo'>
                            {tbTitle}
                        </div>
                        <div className="tbDetailInfo">
                            <div className="tbInfo-scrollable">
                                <div className='table'></div>
                                <div className='detail'>
                                    {tbDetailItem}

                                </div>
                            </div>
                        </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}
