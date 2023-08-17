import React, {useState, useEffect} from 'react';
import axios from 'axios';
import "./News.scss";
import Header from 'systems/Header';
import Footer from 'components/Footer';
import {ReactComponent as Search} from "../../assets/images/search.svg";
import { useParams, useLocation } from 'react-router-dom';

export default function News({companyName}) {

    const apiClient = axios.create({
        baseURL: process.env.REACT_APP_API_URL,
    });

    const { stockId } = useParams(); // URL로부터 supportId를 가져옵니다.

    const location = useLocation();

    console.log(location);

    const [news, setNews] = useState([]);


    useEffect (() => {

        // GET 요청을 보낼 URL 설정 (query parameter 포함)
        const apiUrl = `/api/v1/stock/get/news?stockId=${stockId}`;
        
        apiClient.get(apiUrl)
          .then(response => {
            console.log('응답 데이터:', response.data);
            setNews(response.data);
          })
          .catch(error => {
            console.error('에러 발생:', error);
          });
    }, [stockId]);

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
                            <div className="newsTitle">{companyName} 뉴스</div>
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
