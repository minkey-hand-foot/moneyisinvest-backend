import React, {useState} from "react";
import "./StockHold.scss";
//import axios from "axios";
import Header from "systems/Header";
import Profile from "systems/Profile";
import Footer from "components/Footer";
import { RxHeartFilled, RxHeart } from "react-icons/rx";

export default function StockHold() {
    const [holdStock, setHoldStock] = useState([
        {
            image: "",
            company: "삼",
            code: "005930",
            percent: "0",
            rate: true,
            price: "5",
            value: "5",
            jim: true,
        },
        {
            image: "",
            company: "삼성",
            code: "005930",
            percent: "99",
            rate: true,
            price: "50",
            value: "50",
            jim: false,
        },

        {
            image: "",
            company: "삼성전",
            code: "005930",
            percent: "99.9",
            rate: true,
            price: "500",
            value: "500",
            jim: true,
        },
        {
            image: "",
            company: "삼성전자자",
            code: "005930",
            percent: "100",
            rate: true,
            price: "50,000",
            value: "5,000",
            jim: true,
        },
        {
            image: "",
            company: "삼성전자ㅋㅋ",
            code: "005930",
            percent: "99.9",
            rate: true,
            price: "500,000",
            value: "5,000",
            jim: true,
        },
        {
            image: "",
            company: "삼성전자!!",
            code: "005930",
            percent: "99.9",
            rate: true,
            price: "500,000",
            value: "5,000",
            jim: true,
        },
        {
            image: "",
            company: "삼성전자",
            code: "005930",
            percent: "99.9",
            rate: true,
            price: "500,000",
            value: "5,000",
            jim: true,
        },
    ]);

    const handleToggleHeart = async (index) => {
        const updatedHoldStock = [...holdStock];
        updatedHoldStock[index].jim = !updatedHoldStock[index].jim;
        setHoldStock(updatedHoldStock);
        console.log(holdStock);

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

    const holdItem = holdStock.map((item, index) => (
        <div className="holdItems" keys={index}>
           <div className="holdItem-title">
                <img alt="company" className="holdItem-image"></img>
                <div className="holdItem-events">
                    <div className="holdItem-event">{item.company}</div>
                    <div className="holdItem-code">{item.code}</div>
                </div>
            </div>
            <div className="holdItem-content">
                <div className="holdItem-percent">+{item.percent}%</div>
                <div className="holdItem-price">{item.price}원</div>
                <div className="holdItem-price">{item.value}스톡</div>
                <div className="holdItem-heart" onClick={() => handleToggleHeart(index)}>
                    {item.jim ? <RxHeartFilled color="#85D6D1" /> : <RxHeart color="#85D6D1" />}
                </div>
            </div>
        </div>
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
                        <div className="holdTitle">보유 주식</div>
                        <div className="holdInfo">
                            <div className="holdInfo-top">
                                <div className="holdInfo-title">
                                    <div className="holdInfo-image"></div>
                                    <div className="holdInfo-event">종목</div>
                                </div>
                                <div className="holdInfo-content">
                                    <div className="holdInfo-rate">
                                        <div className="holdInfo-percent">수익률</div>
                                        <div className="holdInfo-price">주가</div>
                                    </div>
                                    <div className="stockValue">
                                        <div className="holdInfo-value">스톡가</div>
                                        <div className="holdInfo-jim">찜</div>
                                    </div>
                                </div>
                            </div>
                            <div className="holdInfo-scrollable">
                                {holdItem}
                            </div>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>  
    )  
}