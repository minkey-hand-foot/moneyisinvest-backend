import React, { useState, useEffect } from "react";
import "./BuyList.scss";
import axios from "axios";
import Header from "systems/Header";
import Profile from "systems/Profile";
import Footer from "components/Footer";
import { useNavigate } from "react-router-dom";

export default function BuyList() {
  const apiClient = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
  });

  const navigate = useNavigate();

  const [buy, setBuy] = useState([]);

  useEffect(() => {
    const token = sessionStorage.getItem("token");
    if (token !== null) {
      apiClient
        .get("/api/v1/shop/get/history", {
          headers: {
            "X-AUTH-TOKEN": token,
          },
        })
        .then((res) => {
          console.log("상품 거래 내역 렌더링 완료: ", res.data);
          setBuy(res.data);
        })
        .catch((err) => {
          if (err.response) {
            // 서버 응답이 온 경우 (에러 응답)
            console.log(
              "Error response:",
              err,
              err.response,
              err.response.status,
              err.response.data
            );
          } else if (err.request) {
            // 요청은 보내졌지만 응답이 없는 경우 (네트워크 오류)
            console.log("Request error:", err.request);
          } else {
            // 오류가 발생한 경우 (일반 오류)
            console.log("General error:", err.message);
          }
        });
    } else {
      alert("로그인 해주세요!");
      navigate("/signIn", { replace: true });
      console.log("Token is null. Unable to send request.");
    }
  }, [navigate]);

  function formatDate(dateString) {
    const dateObject = new Date(dateString);
    return dateObject.toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    });
  }

  const buyItem = buy.map((item, index) => (
    <div className="buyItems" keys={index}>
      <div className="buyItem-title">
        <img alt="상품" src={item.imageUrl} className="buyItem-image"></img>
        <div className="buyItem-product">{item.itemName}</div>
      </div>
      <div className="buyItem-content">
        <div className="buyItem-price">{item.price}스톡</div>
        <div className="buyItem-date">{formatDate(item.createdAt)}</div>
        <div className="buyItem-used">{item.used ? "사용완료" : "사용전"}</div>
      </div>
    </div>
  ));

  return (
    <div className="buyContainer">
      <Header />
      <div className="buyBox">
        <div className="buyContent">
          <div className="profile">
            <Profile />
          </div>
          <div className="buyProfile">
            <div className="buyTitle">거래 내역</div>
            <div className="buyInfo">
              <div className="buyInfo-top">
                <div className="buyInfo-title">
                  <div className="buyInfo-image"></div>
                  <div className="buyInfo-product">상품명</div>
                </div>
                <div className="buyInfo-content">
                  <div className="buyInfo-price">교환가</div>
                  <div className="buyInfo-date">구매일자</div>
                  <div className="buyInfo-delete">사용 여부</div>
                </div>
              </div>
              <div className="buyInfo-scrollable">{buyItem}</div>
            </div>
          </div>
        </div>
        <Footer />
      </div>
    </div>
  );
}
