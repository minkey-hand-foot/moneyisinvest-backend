import React from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import Button from "components/Button";

export default function StockMessage() {
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
    return (
        <div css={MessageContainer}>
            <div css={MessageText}>얼마나 매수 하실건가요?</div>
            <div css={MessageStock}>
                <Button state={"plus"}/>
                <div css={MessageStockText}>200주</div>
                <Button state={"minus"}/>
            </div>
            <div css={MessageInfo}>
                <div>현재 100주 보유하고 있어요</div>
                <div>138800스톡이 필요해요</div>
            </div>
            <Button state={"stockDeal"}/>
        </div>
    )
}