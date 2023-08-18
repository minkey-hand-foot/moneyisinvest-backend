import React, {useState} from 'react';
import "./TbDetail.scss";
import Header from 'systems/Header';
import Footer from 'components/Footer';
import {ReactComponent as Kospi} from "../../assets/images/kospi.svg";
import {ReactComponent as Kosdaq} from "../../assets/images/kosdaq.svg";
import {useLocation} from 'react-router-dom';

export default function TbDetail2() {

    const [words] = useState ([
        {
            word: "코스피",
            content: "국내 종합주가지수. 증권거래소에 상장된 종목들의 주식 가격을 종합적으로 표시한 수치이다. 시장전체의 주가 움직임을 측정하는 지표로 이용되며, 투자성과 측정, 다른 금융상품과의 수익률 비교척도, 경제상황 예측지표로도 이용된다.",
        },
        {
            word: "코스닥",
            content: "코스닥의 개장으로 단순히 증권거래소 상장을 위한 예비적 단계였던 장외시장이 미국의 나스닥(NASDAQ)과 같이 자금조달 및 투자시장으로 독립적인 역할을 수행하게 되었다. 유가증권시장에 비해 진입요건이 상대적으로 덜 까다롭기 때문에 주로 중소벤처기업들이 상장되어 있다.",
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
                            <div className="tbDetailInfo-scrollable">
                                <div>                                
                                <Kospi className='tbDetail2Img' /><Kosdaq/>                                                          
                                </div>
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
