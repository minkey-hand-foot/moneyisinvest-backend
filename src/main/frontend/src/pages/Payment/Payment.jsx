import React from "react";
import "./Payment.scss";
import Header from "systems/Header";
import Footer from "components/Footer";
import {ReactComponent as Banner} from "../../assets/images/프리미엄.svg";
import {ReactComponent as Circle} from "../../assets/images/Ellipse 26.svg";
import {ReactComponent as Kakao} from "../../assets/images/kakao.svg";
import Button from "components/Button";
import axios from "axios";

export default function Payment() {

    const apiClient = axios.create({
        baseURL: process.env.REACT_APP_API_URL,
    });

    const token = sessionStorage.getItem("token");
    const onClickPay = () => {
        apiClient.post("/api/v1/payment/kakao/pay",{}, {
            headers: {
                'X-AUTH-TOKEN': token
            }
        }).then((res) => {
            console.log(res.data);
            const redirectUrl = res.data.next_redirect_pc_url;
            window.location.href = redirectUrl;
        },).catch((err)=> {
            console.log(err);
        })
    }

    return (
        <div className="PayContainer">
            <Header/>
            <div className="PayBox">
                <div className="PayContent">
                    <div className="payTitle">프리미엄 서비스</div>
                    <Banner className="payBanner"/>
                    <div className="payContent">
                        <div className="payContentBox1">
                            <div className="payContentTitle">총 3가지의 혜택을 제공해요</div>
                            <div className="payContentEvents">
                                <div className="payContentEvent">
                                    <Circle />
                                    <div>기존 거래 시 1.5%의 수수료를 0%로 변경해요</div>
                                </div>
                                <div className="payContentEvent">
                                    <Circle />
                                    <div>거래 시 1.5%의 보너스 스톡을 더 지급해요</div>
                                </div>
                                <div className="payContentEvent">
                                    <Circle />
                                    <div>서비스 구독 기간이 3개월이 지속되면 거래 시 지급되는 보너스 스톡이 3%로 증가해요</div>
                                </div>
                            </div>
                            <div className="payKakao">
                                <div className="payKakaopay">
                                    <Kakao />
                                    Kakao<span>pay</span>
                                </div>
                                <div>매월 3,300원</div>
                            </div>
                            <div className="payKakaoInfo">카카오페이는 카카오톡에서 카드를 등록, 간단하게 비밀번호만으로 결제할 수 있는 빠르고 편리한 결제 서비스 입니다</div>
                        </div>
                        <div className="payContentBox2">
                            <div className="paymentBox">
                                <div className="paymentText">
                                    <div>손민기님이</div>
                                    <div>결제 예정인 상품은</div>
                                </div>
                                <div className="paymentSubText">
                                    <div>프리미엄 서비스 구독</div>
                                    <div>구독료 3,300원</div>
                                </div>
                            </div>
                            <div className="paymentResult">
                                <div>총합</div>
                                <div>3,300원</div>
                            </div>
                            <div onClick={onClickPay}>
                            <Button state="shopping"/>
                            </div>
                            <div className="paymentInfo">
                                <div>구매시 카카오페이 사이트로 자동 연결됩니다 </div>
                                <div> 또한, 구독 취소를 따로 하지 않을 시 매월
                                정기적으로 구독료가 자동 결제됩니다 </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}