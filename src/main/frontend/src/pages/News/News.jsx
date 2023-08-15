import React, {useState, useEffect} from 'react';
import axios from 'axios';
import "./News.scss";
import Header from 'systems/Header';
import Footer from 'components/Footer';
import {ReactComponent as Search} from "../../assets/images/search.svg";

export default function News() {
    const [news, setNews] = useState([]);

    useEffect (() => {
        const stockId = '005930'; // 주식 종목 코드

        // GET 요청을 보낼 URL 설정 (query parameter 포함)
        const apiUrl = `/api/v1/stock/get/news?stockId=${stockId}`;
        
        axios.get(apiUrl)
          .then(response => {
            console.log('응답 데이터:', response.data);
            setNews(response.data);
          })
          .catch(error => {
            console.error('에러 발생:', error);
          });
    }, []);

    const newsItem = news.map((item) => (
        <div className="newsList">
            <div className="newsItems" onClick={() => window.open(item.newsUrl)}>
                <div className="newsItemCompany">{item.newsCompany}</div>
                <div className="newsItemTitle">{item.newsTitle}</div>
                <div className="newsItemContent">{item.newsPreview}</div>
            </div>
            <img alt="썸네일" src={item.newsThumbnail} className="newsImage"/>
        </div>
    ))

    return(
        <div className="newsContainer">
            <Header />
            <div className="newsBox">
                <div className="newsContent">
                        <div className="newsTop">
                            <div className="newsTitle">삼성전자 뉴스</div>
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
