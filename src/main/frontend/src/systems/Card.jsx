import React from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";

/* isVisible={true} && rank={1} #3EB7AF */

export default function Card({isVisible, rank, img , company, code, rate, price, stock, rateStatus}) {
    const cardContainer = css`
    width: ${isVisible ? '20.125rem' : '19.5rem'};
    height: ${isVisible ? '12.6875rem' : '12.1875rem'};
    position: relative;
    `;
    const top = css`
    display: ${isVisible ? 'block' : 'none'};
    width: 1.875rem;
    height: 1.875rem;
    background-color: #3EB7AF;
    border-radius: 50%;
    color: #fff;
    text-align: center;
    font-size: 1rem;
    font-weight: 700;
    text-align: center;
    line-height: 1.875rem;
    position: ${isVisible ? 'absolute' : 'static'};
    z-index: 2;
`;
    const card = css`
    box-sizing: border-box;
    width: 19.5rem;
    height: 12.1875rem;
    border-radius: 1.25rem;
    background: ${rank === "1" ? "#D1EFEE" : "#F0F9F8"};
    padding-bottom: 1.81rem;
    position: ${isVisible ? 'absolute' : 'static'};
    top: 0.5rem;
    left: 0.625rem;
    .title {
        padding: 1.38rem 0 1.5rem 1.25rem;
        display: flex;
        align-items: center;
        text-align: center;
        img {
            width: 2.5rem;
            height: 2.5rem;
            background-color: #D9D9D9;
            border: none;
            border-radius: 50%;
        }
        div {
            margin-left: 0.69rem;
            padding: 0;
            height: 1rem;
            display: flex;
            align-items: flex-end;
            gap: 0.21rem;
            .name {
                color: #000;
                font-size: 1rem;
                font-weight: 600;
                text-align: center;
            }
            .code {
                color: #B0B0B0;
                font-size: 0.625rem;
                font-weight: 500;
                text-align: center;
            }
        }
    }
    .info {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 0 1.37rem 0 1.44rem;
        margin: 0;
        div {
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            .type {
                color: #3EB7AF;
                font-size: 0.875rem;
                font-weight: 500;
            }
            .percent {
                color: ${rateStatus ? "#FF1C1C" : "#1C77FF"};
                text-align: center;
                font-size: 0.875rem;
                font-weight: 500;
            }
            .data {
                color: #797979;
                text-align: center;
                font-size: 0.875rem;
                font-weight: 500;
            }
        }
    }
`;
    return (
        <div css={cardContainer}>
            <div css={top}>
                {rank}
            </div>
            <div css={card}>
                <div className="title">
                    <img src={img} alt={company} />
                    <div><span className="name">{company}</span><span className="code">{code}</span></div>
                </div>
                <div className="info">
                    <div>
                        <div className="type">수익률</div>
                        <div className="percent">{rateStatus ? "+" : ""}{rate}%</div>
                    </div>
                    <div>
                        <div className="type">주가</div>
                        <div className="data">{price}원</div>
                    </div>
                    <div>
                        <div className="type">스톡가</div>
                        <div className="data">{stock}스톡</div>
                    </div>
                </div>
            </div>
        </div>
    )
}