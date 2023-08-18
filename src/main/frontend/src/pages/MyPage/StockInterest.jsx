import React, {useState, useEffect} from "react";
import "./StockInterest.scss";
//import axios from "axios";
import Header from "systems/Header";
import Profile from "systems/Profile";
import Footer from "components/Footer";
import { RxHeartFilled, RxHeart } from "react-icons/rx";
import axios from "axios";

export default function StockInterest() {
    const apiClient = axios.create({
        baseURL: process.env.REACT_APP_API_URL,
    });

    const token = sessionStorage.getItem("token");


    const [interestStock, setInterestStock] = useState([]);

    useEffect (() => {
        if (token !== null) {
            apiClient.get("/api/v1/favorite/get", {
                headers: {
                    'X-Auth-Token': token,
                }
            })
            .then((res) => {
                console.log("관심 주식 렌더링 성공",res);
                setInterestStock(res.data);
                
            })
            .catch((err) => {
                if (err.response) {
                    // 서버 응답이 온 경우 (에러 응답)
                    console.log("Error response:", err.response.status, err.response.data);
                } else if (err.request) {
                    // 요청은 보내졌지만 응답이 없는 경우 (네트워크 오류)
                    console.log("Request error:", err.request);
                } else {
                    // 오류가 발생한 경우 (일반 오류)
                    console.log("General error:", err.message);
                }});
        } else {
            console.log("Token is null. Unable to send request.");
        }
    },[]);

    const [jim, setJim] = useState(true);

    const handleToggleHeart = async (index) => {
        apiClient
        .delete(`/api/v1/favorite/remove?stockId=${interestStock[index].stockCode}`,{}, {
          headers: {
            "X-AUTH-TOKEN": token,
          },
        })
        .then((res) => {
          console.log("관심 주식 삭제", res.data);
          setJim(false);
          alert("관심 주식이 삭제되었습니다!");
          window.location.reload(); // 페이지 다시 로드
        })
        .catch((err) => {
          console.log(err);
        });
    }




    // API 연결 후 수정 (EX 찜 삭제 후 렌더링되게)
    const interestItem = interestStock.map((item, index) => (
        jim ? (
        <div className="interestItems" keys={index}>
           <div className="holdItem-title">
                <img alt="company" src={item.stockUrl} className="holdItem-image"></img>
                <div className="holdItem-events">
                    <div className="holdItem-event">{item.stockName}</div>
                    <div className="holdItem-code">{item.stockCode}</div>
                </div>
            </div>
            <div className="holdItem-content">
                <div className="holdItem-percent">{item.rate > 0 ? "+" : ""}
                    {item.rate}%</div>
                <div className="holdItem-price">{item.real_per_price}원</div>
                <div className="holdItem-price">{item.real_per_coin}스톡</div>
                <div className="holdItem-heart" onClick={() => handleToggleHeart(index)}>
                    {jim ? <RxHeartFilled color="#85D6D1" /> : <RxHeart color="#85D6D1" />}
                </div>
            </div>
        </div>
        ) : null
    ))

    return (
        <div className="holdContainer">
            <Header />
            <div className="holdBox">
                <div className="holdContent">
                    <div className="profile">
                        <Profile/>
                    </div>
                    <div className="holdProfile">
                        <div className="holdTitle">관심 주식</div>
                        <div className="holdInfo">
                            <div className="holdInfo-top">
                                <div className="holdInfo-title">
                                    <div className="holdInfo-image"></div>
                                    <div className="holdInfo-event">종목</div>
                                </div>
                                <div className="holdInfo-content">
                                    <div className="holdInfo-rate">
                                        <div className="holdInfo-percent">등락률</div>
                                        <div className="holdInfo-price">주가</div>
                                    </div>
                                    <div className="stockValue">
                                        <div className="holdInfo-value">스톡가</div>
                                        <div className="holdInfo-jim">찜</div>
                                    </div>
                                </div>
                            </div>
                            <div className="holdInfo-scrollable">
                                {interestItem}
                            </div>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>  
    )  
}