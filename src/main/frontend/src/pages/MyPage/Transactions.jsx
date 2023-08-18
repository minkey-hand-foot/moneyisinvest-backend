    import React, {useState, useEffect} from "react";
    import "./Transactions.scss";
    import axios from "axios";
    import Header from "systems/Header";
    import Profile from "systems/Profile";
    import Footer from "components/Footer";
    
    export default function StockTransaction() {
        const [transStock, setTransStock] = useState([
            /*{
                image: "",
                company: "삼",
                code: "005930",
                price: "500,000", //거래금
                value: "5,000", //거래 스톡가
                volume: "50", //거래 수량
                date: "2023.12.11", //거래 일시
                status: true, //매수/매도
            },
            {
                image: "",
                company: "삼성",
                code: "005930",
                price: "500,000", //거래금
                value: "5,000", //거래 스톡가
                volume: "50", //거래 수량
                date: "2023.12.11", //거래 일시
                status: "매수", //매수/매도
            },*/
        ]);
    

    
        /*useEffect(() => {
            const token = sessionStorage.getItem("token");
        const fetchStockHistory = async () => {
            console.log("fetchData 호출"); 
            if (!token || token.trim() === "") {
                console.error("토큰이 누락되었습니다. 로그인 후 다시 시도해 주세요.");
                return;
            }
            try {
              const response = await axios.get(

        
        useEffect(() => {
        const fetchStockHistory = async () => {
            try {
              // 토큰 값이 있는 경우에는 백엔드에 토큰 포함하여 요청 보내기
              const token = sessionStorage.getItem("token");
          
              const response = await axios.post(

                "api/v1/stock/get/users/stocks/history",
                {},
                {
                  headers: {
                    "X-Auth-Token": token,
                  },
                }
              );
              setTransStock(response.data);
              if (response.status === 200) {
                console.log("주식 내역 상태 업데이트 성공");
              } else {
                console.error("주식 내역 상태 업데이트 실패");
                // 실패 처리 로직 추가
              }
            } catch (error) {
              console.error("주식 내역 상태 업데이트 에러:", error);
              // 에러 처리 로직 추가
            }

          }; 
          
          fetchStockHistory();
        }, []); */

        useEffect(() => {
            const token = sessionStorage.getItem("token");
        
            //주식 거래 내역
            const fetchData = async () => {
              console.log("fetchData 호출"); 
              if (!token || token.trim() === "") {
                console.error("토큰이 누락되었습니다. 로그인 후 다시 시도해 주세요.");
                return;
              }
          
              try {
                const response = await axios.get("api/v1/stock/get/users/stocks/history", {
                  headers: {
                    "X-AUTH-TOKEN": token,
                  },
                });
                setTransStock(response.data);
                console.log(response);
                console.log("주식 거래 내역 load success");
              } catch (error) {
                // 에러 처리
                console.error("API 요청 중 에러가 발생했습니다:", error);
              }
            };
          
            fetchData();
          }, []); // 빈 배열을 넣어서 컴포넌트 마운트 시에만 실행되도록 합니다.

          /*const apiClient = axios.create({
            baseURL: process.env.REACT_APP_API_URL,
          });

          useEffect(() => {
            const token = sessionStorage.getItem("token");
            if (token !== null) {
              apiClient
                .get("api/v1/stock/get/users/stocks/history", {
                  headers: {
                    "X-AUTH-TOKEN": token,
                  },
                })
                .then((res) => {
                  console.log("주식 거래 내역 렌더링 완료: ", res.data);
                  setTransStock(res.data);
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
            } 
          }, []); 

          };
          
          fetchStockHistory();
        }, []); */
  

    
        const transItem = transStock.map((item) => (
            <div className="transItems" keys={item.id}>
               <div className="transItem-title">
                    <img alt="company" className="transItem-image" src={item.stockLogo}></img>
                    <div className="transItem-events">
                        <div className="transItem-event">{item.stockName}</div>
                        <div className="transItem-code">{item.stockCode}</div>
                    </div>
                </div>
                <div className="transItem-content">
                    <div className="transItem-price">{item.unitPrice}원</div>
                    <div className="transItem-value">{item.stockPrice}스톡</div>
                    <div className="transItem-volume">{item.quantity}주</div>
                    <div className="transItem-date">{item.transactionDate}</div>
                    <div className="transItem-status">{item.status}</div>
                </div>
            </div>
       
        ))
        return (
            <div className="transContainer">
                <Header />
                <div className="transBox">
                    <div className="transContent">
                        <div className="profile">
                            <Profile/>
                        </div>
                        <div className="transProfile">
                            <div className="transTitle">주식 거래 내역</div>
                            <div className="transInfo">
                                <div className="transInfo-top">
                                    <div className="transInfo-title">
                                        <div className="transInfo-image"></div>
                                        <div className="transInfo-event">종목</div>
                                    </div>
                                    <div className="transInfo-content">
                                        <div className="stockValue">
                                            <div className="transInfo-price">거래금</div>
                                            <div className="transInfo-value">스톡가</div>
                                            <div className="transInfo-volume">거래 수량</div>
                                            <div className="transInfo-date">거래 일시</div>
                                            <div className="transInfo-status">매수/매도</div>
                                        </div>
                                        {/*<div className="stockValue">
                                            <div className="transInfo-volume">거래 수량</div>
                                            <div className="transInfo-date">거래 일시</div>
                                        </div>
                                        <div className="stockValue">
                                            <div className="transInfo-status">매수/매도</div>
                                        </div>*/}
                                    </div>
                                </div>
                                <div className="holdInfo-scrollable">
                                    {transItem}
                                </div>
                            </div>
                        </div>
                    </div>
                    <Footer />
                </div>
            </div>  
        )  
    }
