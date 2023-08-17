import React, {useState} from 'react';
import "./Textbook.scss";
import Header from 'systems/Header';
import Footer from 'components/Footer';
import {ReactComponent as Search} from "../../assets/images/search.svg";
import tbDetail1 from "../../assets/images/tbDetail1.svg";
import tbDetail2 from "../../assets/images/tbDetail2.svg";
import tbDetail3 from "../../assets/images/tbDetail3.svg";
import { Link } from 'react-router-dom';

export default function Textbook() {
    const [textbook] = useState ([
        {
            tbDate: "2023.8.11.금",
            tbTitle: "기업 실적 분석에 쓰이는 재무제표에 대해 알아볼까요?",
            tbThumbnail: tbDetail1,
            tbUrl: "",
        },
        {
            tbDate: "2023.8.11.금",
            tbTitle: "주요 지수, 코스피 코스닥이란?",
            tbThumbnail: tbDetail2,
        },
        {
            tbDate: "2023.8.11.금",
            tbTitle: "주식 기본 용어, 알고 시작하자!",
            tbThumbnail: tbDetail3,
        },
        
    ]);

    const textbookItem = textbook.map((item, index) => (
        <div className="tbList" key={index}>            
                <div className="tbItems">  
                <Link to= {`/tbDetail${index + 1}`} state= {{tbTitle:item.tbTitle}}
                
                 style={{ textDecoration: 'none' }}  >        
                    <div className="tbItemTitle">{item.tbTitle}</div>
                    </Link>                    
                </div>            
                <img src={item.tbThumbnail} alt="썸네일" className="tbImage" />  
        </div>
    ))

    return(
        <div className="textbookContainer">
            <Header />
            <div className="tbBox">
                <div className="tbContent">
                        <div className="tbTop">
                            <div className="tbTitle">교과서</div>
                        </div>                        
                        <div className="tbInfo">
                            <div className="tbInfo-scrollable">
                                {textbookItem}
                            </div>
                        </div>
                </div>
                <Footer />
            </div>
        </div>
    )
    
}
