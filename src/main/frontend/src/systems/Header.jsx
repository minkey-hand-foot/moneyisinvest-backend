import React, {useEffect, useContext, useState, useRef} from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import axios from "axios";
import {ReactComponent as Logo} from '../assets/images/logo.svg';
import {ReactComponent as Search} from "../assets/images/search.svg";
import {ReactComponent as Coin} from "../assets/images/coin.svg";
import {Link, useNavigate} from "react-router-dom";
//import profileImage from "../assets/images/angma.jpg";
import { AuthContext } from "context/AuthContext";

export default function Header({coinNum}) {
    const {isLoggedIn, token, userName, userProfile} = useContext(AuthContext);

    const navigate = useNavigate();

    useEffect(() => {
        if (isLoggedIn) {
            axios.get("/api/v1/profile/get", {
                headers: {
                    'X-Auth-Token': token,
                }
            })
            .then((res) => {
                console.log("헤더 프로필 불러오기 성공", res.data);
            }).catch((res) => {
                console.log("헤더 프로필 불러오기 실패", res);
            })
        }
    }, [isLoggedIn, navigate, userProfile, userName, token]);

    const headerContainer = css`
    position: sticky;
    top: 0;
    flex-shrink: 0;
    fill: rgba(255, 255, 255, 0.19);
    background-blend-mode: overlay;
    backdrop-filter: blur(20px); 
    z-index: 999;
    `;
    const header = css`
    flex-shrink: 0;
    margin: 0 auto;
    display: flex;
    flex-direction: row;
    justify-content: center;
    width: 61.625rem;
    height: 3.875rem;
    `;
    const logo = css`
    width: 9.375rem;
    height: 3.125rem;
    flex-shrink: 0;
    margin: auto 0;
    `;
    const nav = css`
    display: inline-flex;
    align-items: flex-start;
    margin-top: auto;
    margin-bottom: auto;
    margin-left: ${isLoggedIn ? '2.63rem' : '3.25rem' };
    margin-right: ${isLoggedIn ? '2.62rem' : '3.31rem' };
    gap: ${isLoggedIn ? '3.25rem' : '5rem' };
    `;
    const item = css`
    color: #000;
    font-size: 1rem;
    font-weight: 600;
    `;
    const searchContainer = css`
    display: flex;
    flex-direction: column;
    position: relative;
    `;
    const searchBox = css`
    width: ${isLoggedIn ? '16.0625rem' : '17.25rem'};
    height: 2rem;
    flex-shrink: 0;
    border-radius: 1.25rem;
    background: #F1F1F1;
    margin-top: auto;
    margin-bottom: auto;
    margin-right: ${isLoggedIn ? '1.25rem' : '2rem'};
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    `;
    const search = css`
    color: #000;
    margin: auto 1.25rem;
    background-color: #F1F1F1;
    border: none;
    width: 100%;
    height: 100%;
    outline: none;
    &::placeholder {
        color: #B0B0B0;
        font-size: 0.75rem;
        font-weight: 500;
    }
    `;
    const searchLogo = css`
    width: 1.5rem;
    height: 1.5rem;
    flex-shrink: 0;
    margin: auto 0.94rem auto 0;
    `;
    const searchResultsContainer = css`
    display: flex;
    flex-direction: column;
    align-items: center;
    position: absolute;
    top: 76%;
    width: 76%;
    min-height: auto;
    max-height: 17.25rem;  
    overflow: scroll;
    background-color: #fff;
    border-radius: 0 0 0.625rem 0.625rem;
    z-index: 10;
    margin: auto 1.25rem;
    overflow-y: scroll;
    overflow-x: hidden;
    align-items: center;
    justify-content: center;
    padding-top: 2.5rem; /* 수정을 추가합니다. */
    &::-webkit-scrollbar {
    width: 0;
    }
    & > div:not(:last-child) {
        width: 100%;
        border-bottom: 1px solid #D1EFEE;
    }
    `
    const searchResultsItem = css`
    height: 2.5rem;
    width: 100%;
    padding : 0.81rem 0 0.81rem 1.06rem;
    font-size: 0.75rem;
    color: #797979;
    font-weight: 500;
    `;
    const login = css`
    color: #000;
    font-size: 1rem;
    font-weight: 600;
    margin: auto 0.94rem auto 0;
    flex-shrink: 0;
    display: ${isLoggedIn ? 'none' : 'block' }
    `;
    const coin = css`
    display: ${isLoggedIn ? 'block' : 'none'};
    color: #3eb7af;
    font-size: 0.8125rem;
    font-weight: 600;
    margin: auto 0.33rem auto 0;
    `;
    const coinLogo = css`
    display: ${isLoggedIn ? 'block' : 'none'};
    width: 1.838rem;
    height: 1.89344rem;
    flex-shrink: 0s;
    margin: auto 0.95rem auto 0;
    `;
    const profile = css`
    display: flex;
    flex-direction: row;
    `;
    const nickname = css`
    display: ${isLoggedIn ? 'block' : 'none'};
    color: #000;
    font-size: 1rem;
    font-weight: 600;
    margin: auto 0.37rem auto 0;
    `;
    const headerprofile = css`
    display: ${isLoggedIn ? 'block' : 'none'};
    width: 2.25rem;
    height: 2.25rem;
    margin: auto 0.75rem auto 0;
    border-radius: 50%;
    `

    const [searchTerm, setSearchTerm] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const searchResultRef = useRef();

    useEffect(() => {
        //검색어가 없으면 결과를 비움
        if (!searchTerm) {
            setSearchResults([]);
            return;
        }
        const fetchData = async () => {
        try {
            // API 호출을 통해 검색 결과를 가져옵니다.
            const response = await axios.get("/api/v1/stock/search", {
            params: { keyword: searchTerm }, // keyword 파라미터로 수정했습니다.
            headers: {
                "X-Auth-Token": token,
            },
            })
            console.log(response.data);
            setSearchResults(response.data); // 결과를 state에 저장합니다.
        } catch (error) {
            console.error("검색 중 오류가 발생했습니다.", error);
        }
        };

        // debounce 처리를 통해 API 호출을 제한합니다.
        const timeoutId = setTimeout(() => {
            fetchData();
        }, 500);

        return() => {
            clearTimeout(timeoutId);
        };
    }, [searchTerm, token])

    const handleClickOutside = (event) => {
        if (searchResultRef.current && !searchResultRef.current.contains(event.target)) {
            setSearchResults([]);
        }
    };

    useEffect(() => {
        document.addEventListener("mousedown", handleClickOutside);
        return() => {
            document.removeEventListener("mosedown", handleClickOutside);
        };
    }, []);

    const handleSearchChange = (event) => {
        setSearchTerm(event.target.value);
    }

    return (
        <div css={headerContainer}>
            <div css={header}>
                <Link to = "/" style={{ textDecoration: "none" }}>
                <Logo css={logo}/>
                </Link>
                <div css={nav}>
                    <div css={item}>회사</div>
                    <Link to = "/allNews" style={{ textDecoration: "none" }}>
                    <div css={item}>뉴스</div>
                    </Link>
                    <Link to = "/Textbook" style={{ textDecoration: "none" }}>
                    <div css={item}>교과서</div>
                    </Link>
                    <Link to = "/Store" style={{ textDecoration: "none" }}>
                    <div css={item}>상점</div>
                    </Link>
                </div>
                <div css={searchContainer}>
                    <div css={searchBox}>
                        <input css={search} placeholder="Search..." value={searchTerm} onChange={handleSearchChange}></input>
                        <Search css={searchLogo} />
                    </div>
                    {searchResults.length > 0 && (
                        <div ref={searchResultRef} css={searchResultsContainer}>
                            {searchResults.map((result) => (
                                <div key={result.stockId} css={searchResultsItem}>
                                    {result.stockName}
                                </div>
                            ))}
                        </div>
                    )}

                </div>
                <Link to = "/signIn" style={{ textDecoration: "none" }} css={login}>
                <div>로그인</div>
                </Link>
                <div css={coin}>{coinNum} 스톡</div>
                <Coin css={coinLogo}/>
                <Link to = "/mypage" style={{ textDecoration: "none" }} css={profile}>
                    <div css={nickname}>{userName}</div>
                    <img alt="profile" src={userProfile} css={headerprofile} />
                </Link>
            </div>
        </div>
    )
}