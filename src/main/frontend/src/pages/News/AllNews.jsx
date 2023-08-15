import React, {useState, useEffect} from 'react';
import axios from 'axios';
import "./News.scss";
import Header from 'systems/Header';
import Footer from 'components/Footer';
import {ReactComponent as Search} from "../../assets/images/search.svg";

export default function AllNews() {
    const [news] = useState ([
        {
            newsCompany: "로보뉴스",
            newsTitle: "[리포트 브리핑] 삼성전자, ‘기회가 왔다’ 목표가 94,000원",
            newsContent: "[서울 = 뉴스핌]  로보뉴스 = 한국 투자 증권에서 03일 삼성 전자에 대해  ‘기회가 왔다’라며 투자의 ‘매수(유지)’의 신규 리포트를 발행하였고, 목표가 94,000원으로 내놓았다...",
            newsThumbnail: "",
            newsUrl: "",
        },
        {
            newsCompany: "로보뉴스",
            newsTitle: "[리포트 브리핑] 삼성전자, ‘기회가 왔다’ 목표가 94,000원",
            newsContent: "[서울 = 뉴스핌]  로보뉴스 = 한국 투자 증권에서 03일 삼성 전자에 대해  ‘기회가 왔다’라며 투자의 ‘매수(유지)’의 신규 리포트를 발행하였고, 목표가 94,000원으로 내놓았다...",
            newsThumbnail: "",
        },
        {
            newsCompany: "로보뉴스",
            newsTitle: "[리포트 브리핑] 삼성전자, ‘기회가 왔다’ 목표가 94,000원",
            newsContent: "[서울 = 뉴스핌]  로보뉴스 = 한국 투자 증권에서 03일 삼성 전자에 대해  ‘기회가 왔다’라며 투자의 ‘매수(유지)’의 신규 리포트를 발행하였고, 목표가 94,000원으로 내놓았다...",
            newsThumbnail: "",
        },
        {
            newsCompany: "로보뉴스",
            newsTitle: "[리포트 브리핑] 삼성전자, ‘기회가 왔다’ 목표가 94,000원",
            newsContent: "[서울 = 뉴스핌]  로보뉴스 = 한국 투자 증권에서 03일 삼성 전자에 대해  ‘기회가 왔다’라며 투자의 ‘매수(유지)’의 신규 리포트를 발행하였고, 목표가 94,000원으로 내놓았다...",
            newsThumbnail: "",
        },
        {
            newsCompany: "로보뉴스",
            newsTitle: "[리포트 브리핑] 삼성전자, ‘기회가 왔다’ 목표가 94,000원",
            newsContent: "[서울 = 뉴스핌]  로보뉴스 = 한국 투자 증권에서 03일 삼성 전자에 대해  ‘기회가 왔다’라며 투자의 ‘매수(유지)’의 신규 리포트를 발행하였고, 목표가 94,000원으로 내놓았다...",
            newsThumbnail: "",
        },
        {
            newsCompany: "로보뉴스",
            newsTitle: "[리포트 브리핑] 삼성전자, ‘기회가 왔다’ 목표가 94,000원",
            newsContent: "[서울 = 뉴스핌]  로보뉴스 = 한국 투자 증권에서 03일 삼성 전자에 대해  ‘기회가 왔다’라며 투자의 ‘매수(유지)’의 신규 리포트를 발행하였고, 목표가 94,000원으로 내놓았다...",
            newsThumbnail: "",
        },
        {
            newsCompany: "로보뉴스",
            newsTitle: "[리포트 브리핑] 삼성전자, ‘기회가 왔다’ 목표가 94,000원",
            newsContent: "[서울 = 뉴스핌]  로보뉴스 = 한국 투자 증권에서 03일 삼성 전자에 대해  ‘기회가 왔다’라며 투자의 ‘매수(유지)’의 신규 리포트를 발행하였고, 목표가 94,000원으로 내놓았다...",
            newsThumbnail: "",
        },
    ]);

    useEffect (() => {

        // GET 요청을 보낼 URL 설정 (query parameter 포함)
        const apiUrl = `/api/v1/stock/get/news`;
        
        axios.get(apiUrl)
          .then(response => {
            console.log('응답 데이터:', response.data);
          })
          .catch(error => {
            console.error('에러 발생:', error);
          });
    })

    const newsItem = news.map((item) => (
        <div className="newsList">
            <div className="newsItems" onClick={() => window.open("")}>
                <div className="newsItemCompany">{item.newsCompany}</div>
                <div className="newsItemTitle">{item.newsTitle}</div>
                <div className="newsItemContent">{item.newsPreview}</div>
            </div>
            <img alt="썸네일" className="newsImage"/>
        </div>
    ))

    return(
        <div className="newsContainer">
            <Header />
            <div className="newsBox">
                <div className="newsContent">
                        <div className="newsTop">
                            <div className="newsTitle">전체 뉴스</div>
                            <div className="newsSearch">
                                <input type="text" />
                                <div><Search /></div>
                            </div>
                        </div>
                        <div className="newsInfo">
                            <div className="newsInfo-scrollable">
                                {newsItem}
                            </div>
                        </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}
