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


    const [interestStock, setInterestStock] = useState([]);

    useEffect (() => {
        const token = sessionStorage.getItem("token");
        if (token !== null) {
            apiClient.get("/api/v1/favorite/get", {
                headers: {
                    'X-Auth-Token': token,
                }
            })
            .then((res) => {
                console.log("관심 주식 렌더링 성공",res);
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

    const handleToggleHeart = async (index) => {
        const updateInterestStock = [...interestStock];
        updateInterestStock[index].jim = !updateInterestStock[index].jim;
        setInterestStock(updateInterestStock);
        console.log(interestStock);

        // 수정 필요
        /* try {
            // 토큰 값이 있는 경우에는 백엔드에 토큰 포함하여 요청 보내기
            const token = sessionStorage.getItem("token");
    
            const response = await axios.post('/api/v1/favorite/add', {
                stockId: updatedHoldStock[index].id,
                jim: updatedHoldStock[index].jim,
            }, {
                headers: {
                    'X-Auth-Token': token,
                },
            });
    
            if (response.status === 200) {
                console.log('찜 상태 업데이트 성공');
            } else {
                console.error('찜 상태 업데이트 실패');
                // 실패 처리 로직 추가
            }
        } catch (error) {
            console.error('찜 상태 업데이트 에러:', error);
            // 에러 처리 로직 추가
        } */
    }

    // API 연결 후 수정 (EX 찜 삭제 후 렌더링되게)
    const interestItem = interestStock.map((item, index) => (
        item.jim ? (
        <div className="holdItems" keys={index}>
           <div className="holdItem-title">
                <img alt="company" src={item.stockLogoUrl} className="holdItem-image"></img>
                <div className="holdItem-events">
                    <div className="holdItem-event">{item.companyName}</div>
                    <div className="holdItem-code">{item.stockCode}</div>
                </div>
            </div>
            <div className="holdItem-content">
                <div className="holdItem-percent">+{item.preparation_day_before_rate}%</div>
                <div className="holdItem-price">{item.price}원</div>
                <div className="holdItem-price">{item.stockPrice}스톡</div>
                <div className="holdItem-heart" onClick={() => handleToggleHeart(index)}>
                    {item.jim ? <RxHeartFilled color="#85D6D1" /> : <RxHeart color="#85D6D1" />}
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