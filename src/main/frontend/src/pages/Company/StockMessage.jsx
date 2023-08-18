import React, {useEffect, useState} from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import Button from "components/Button";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function StockMessage({stockId, state, onClick, stockPrice}) {
    const MessageContainer = css`
    width: 28.1875rem;
    height: 17.25rem;
    border-radius: 1.25rem;
    background: #FFF;
    padding: 2.12rem 2.69rem;
    `;
    const MessageText = css`
    color: #000;
    font-size: 1.25rem;
    font-weight: 700;
    margin-bottom: 2.37rem;
    `;
    const MessageStock = css`
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.62rem;
    `;
    const MessageStockText = css`
    color: var(--main-3, #3EB7AF);
    font-size: 1.25rem;
    font-weight: 500;
    line-height: 1.59169rem;
    `;
    const MessageInfo = css`
    display: flex;
    flex-direction: column;
    text-align: center;
    color: var(--sub-text, #797979);
    font-size: 0.875rem;
    font-weight: 500;
    line-height: 1.11419rem;
    margin-bottom: 0.5rem;
    `;

    const apiClient = axios.create({
        baseURL: process.env.REACT_APP_API_URL,
    });

    // 보유 주식 상태 관리
    const [stock, setStock] = useState("");

    // 수량 변경 상태 관리
    const [quantity, setQuantity] = useState(0);

    const [stockNeed, setStockNeed] = useState(0);

    const getStockNeed = (newQuantity) => {
      apiClient
        .get(`/api/v1/stock/calculate`, {
          params: {
            amount: newQuantity,
            price: String(stockPrice),
          },
        })
        .then((res) => {
          console.log("필요 스톡 불러오기 성공", res.data);
          setStockNeed(res.data.msg);
        })
        .catch((err) => {
          console.log("필요 스톡 불러오기 실패:", err);
        });
    };
    
    useEffect(() => {
      getStockNeed(quantity);
    }, [quantity]);

    // 수량 증가 버튼 핸들러
    const handlePlusButtonClick = () => {
      setQuantity(quantity + 1);
    };

    // 수량 감소 버튼 핸들러
    const handleMinusButtonClick = () => {
      if (quantity > 0) {
        setQuantity(quantity - 1);
      }
    };

    const token = sessionStorage.getItem("token");
    useEffect(() => {
        apiClient
        .get("/api/v1/stock/get/users/stockquantity",
        {
          headers: {
            "X-AUTH-TOKEN": token,
          },
          params: {
            stockId: stockId,
          },
        })
        .then((res) => {
          console.log("보유 스톡 불러오기 성공", res);
          setStock(res.data.msg);
        })
        .catch((err) => {
          console.log("보유 스톡 불러오기 실패:", err);
        });
      }, [stockId]);

      const onClickDeal = () => {
        if(state === "buy") {
          apiClient.post("/api/v1/stock/buy", {
          conclusion_price: String(stockPrice),
          stockAmount: String(quantity),
          stockCode: stockId
        }, {
          headers: {
            "X-AUTH-TOKEN": sessionStorage.getItem("token")
          }
        }).then((res)=> {
          console.log(res.data);
          if (res.data.success === true) {
            alert("매수가 완료되었습니다!");
          } else {
            alert("매수를 완료하지 못했습니다!");
          }
          window.location.reload(); // 페이지 다시 로드
        }).catch((err) => {
          console.log(err);
        })
        } else {
          apiClient.post("/api/v1/stock/sell", {
            sell_price: String(stockPrice),
            stockAmount: String(quantity),
            stockCode: stockId
          }, {
            headers: {
              "X-AUTH-TOKEN": sessionStorage.getItem("token")
            }
          }).then((res)=> {
            console.log(res.data);
            if (res.data.success === true) {
              alert("매도가 완료되었습니다!");
            } else {
              alert("매도를 완료하지 못했습니다!");
            }
            window.location.reload(); // 페이지 다시 로드
          })  
        }
      }

    return (
        <div css={MessageContainer} onClick={onClick}>
            <div css={MessageText}>얼마나 {state === "buy" ? "매수" : "매도"} 하실건가요?</div>
            <div css={MessageStock}>
              <div onClick={handleMinusButtonClick}>
                <Button state={"minus"}/>
                </div>
                <div css={MessageStockText}>{quantity}주</div>
                <div onClick={handlePlusButtonClick}>
                <Button state={"plus"}/>
                </div>
            </div>
            <div css={MessageInfo}>
                <div>현재 {stock === "null" ? "0" : stock}주 보유하고 있어요</div>
                {(state === "buy") && (<div>{stockNeed}스톡이 필요해요</div>)}
            </div>
            <div onClick={onClickDeal}>
            <Button state={"stockDeal"}/>
            </div>
        </div>
    )
}