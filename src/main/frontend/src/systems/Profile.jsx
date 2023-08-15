import React, {useEffect, useContext} from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import axios from "axios";
import {ReactComponent as Coin} from "../assets/images/coin.svg";
import { Link, useLocation } from "react-router-dom";
import { AuthContext } from "context/AuthContext";

export default function Profile(props) {

    const {token, userName, userProfile} = useContext(AuthContext);

    
    const profileContainer = css`
    width: 10.875rem;
    height: 29.5rem;
    `;
    const profile2 = css`
        width: 7.5rem;
        height: 7.5rem;
        display: flex;
        margin: 0.56rem auto 0.81rem auto;
    `
    const name = css`
        text-align: center;
        color: #000;
        font-size: 1.125rem;
        font-weight: 600;
    `;
    const coin = css`
        display: flex;
        margin: 0.25rem auto 0 auto;
        justify-content: center;
        height: 1.25rem;
        align-items: center;
        gap: 0.1875rem;
    `;
    const coinImage = css`
        width: 1.02113rem;
        height: 1.05194rem;
    `;
    const coinText = css`
        color: #000;
        font-size: 0.75rem;
        font-weight: 500;
        span {
            color: #000;
            font-size: 0.8125rem;
            font-weight: 600;
            margin-right: 0.12rem;
        }
    `
    const line = css`
        width: 9.25rem;
        border: 0;
        height: 0.0625rem;
        background: #85d6d1;
        display: flex;
        margin: 1rem auto 2rem auto;
    `
    const list = css`
        list-style: none;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2rem;
        li {
            font-size: 0.875rem;
            font-weight: 600;
        }
    `

    const location = useLocation();

    useEffect (() => {
        axios.get("/api/v1/profile/get", {
            headers: {
                'X-Auth-Token': token,
            }
        })
        .then((res) => {
            console.log("프로필 불러오기 성공", res.data);
        }).catch((res) => {
            console.log("프로필 불러오기 실패", res);
        })
    },[])

    return(
        <div css={profileContainer}>
            <img alt="profile" src={userProfile} css={profile2}/>
            <div css={name}>{userName}님</div>
            <div css={coin}>
                <Coin css={coinImage}/>
                <div css={coinText}><span>{props.coinNum}</span>스톡 보유중</div>
            </div>
            <hr css={line}/>
            <ul css={list}>
                <Link to = "/mypage" style={{ textDecoration: "none" }}>
                <li css={[css`color: #b0b0b0;`, location.pathname === "/mypage" && css`color: #3EB7AF;`,]}>마이페이지</li>
                </Link>
                <Link to = "/stockHold" style={{ textDecoration: "none" }}>
                <li css={[css`color: #b0b0b0;`, location.pathname === "/stockHold" && css`color: #3EB7AF;`,]}>보유 주식</li>
                </Link>
                <Link to = "/stockInterest" style={{ textDecoration: "none" }}>
                <li css={[css`color: #b0b0b0;`, location.pathname === "/stockInterest" && css`color: #3EB7AF;`,]}>관심 주식</li>
                </Link>
                <Link to = "/buyList" style={{ textDecoration: "none" }}>
                <li css={[css`color: #b0b0b0;`, location.pathname === "/buyList" && css`color: #3EB7AF;`,]}>거래 내역</li>
                </Link>
                <Link to = "/askpage" style={{ textDecoration: "none" }}>
                <li css={[css`color: #b0b0b0;`, (location.pathname === "/askpage" && css`color: #3EB7AF;`) || (location.pathname === "/askwrite" && css`color: #3EB7AF;`),]}>문의사항</li>
                </Link>
            </ul>
        </div>
    )
}