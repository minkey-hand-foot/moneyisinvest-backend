import React, {useState, useEffect} from 'react';
import "./AskPage.scss";
import Header from 'systems/Header';
import Profile from 'systems/Profile';
import Footer from 'components/Footer';
import Button from 'components/Button';
import {Link, useNavigate} from 'react-router-dom';
import axios from 'axios';

export default function AskPage() {

    const apiClient = axios.create({
        baseURL: process.env.REACT_APP_API_URL,
    });


    const [askList, setAskList] = useState([]);

    const navigate = useNavigate();

    function formatDate(dateString) {
        const weekDays = ["일", "월", "화", "수", "목", "금", "토"];
        const date = new Date(dateString);
      
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const dayOfWeek = weekDays[date.getDay()];
      
        return `${year}.${month}.${day}.${dayOfWeek}`;
      }

      useEffect (() => {
        const token = sessionStorage.getItem("token");
        const id = sessionStorage.getItem("id");
        if (token !== null) {
            apiClient.get("/api/v1/support/getAll", {
                headers: {
                    'X-AUTH-TOKEN': token,
                },
                params: {
                    uid: id
                }
            })
            .then((res) => {
                console.log("문의사항 리스트 렌더링 완료: ",res);
                setAskList(res.data);
            })
            .catch((err) => {
                if (err.response) {
                    // 서버 응답이 온 경우 (에러 응답)
                    console.log("Error response:", err, err.response, err.response.status, err.response.data);
                } else if (err.request) {
                    // 요청은 보내졌지만 응답이 없는 경우 (네트워크 오류)
                    console.log("Request error:", err.request);
                } else {
                    // 오류가 발생한 경우 (일반 오류)
                    console.log("General error:", err.message);
                }});
        } else {
            alert("로그인 해주세요!");
            navigate("/signIn", {replace: true});
            console.log("Token is null. Unable to send request.");
        }
    },[navigate]);

    /*const onClickDetail = (supportId) => {
        const token = sessionStorage.getItem("token");
        const id = sessionStorage.getItem("id");
        if (token !== null) {
          axios.get("/api/v1/support/getOne", {
            headers: {
              'X-AUTH-TOKEN': token,
            },
            params: {
              supportId: supportId,
              uid: id
            }
          })
          .then(response => {
            console.log(response.data);
          })
          .catch(err => {
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
          alert("로그인 해주세요!");
          console.log("Token is null. Unable to send request.");
        }
    }*/

    const onClickDelete = (supportId) => {
        const token = sessionStorage.getItem("token");
        const id = sessionStorage.getItem("id");
        if (token !== null) {
            apiClient.delete("/api/v1/support/remove", {
                headers: {
                    'X-AUTH-TOKEN': token,
                },
                params: {
                    uid: id,
                    supportId: parseInt(supportId),
                },
            })
            .then(response => {
                console.log("문의사항 삭제 완료",response.data);
                /*window.location.href = '/askpage';*/
                navigate("/askpage", {replace: true});
            })
            .catch(err => {
                if (err.response) {
                    // 서버 응답이 온 경우 (에러 응답)
                    console.log("Error response:", err.response.status, err.response.data);
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
          navigate("/signIn", {replace: true});
          console.log("Token is null. Unable to send request.");
        }
    }

    const AskListItem = askList.map((item) => (
        <div className="askInfo-content" key={item.id}>
            <div className="askInfo-itemTitle">
                <Link to={`/askpage/${item.id}`} style={{ textDecoration: "none",  color: "#000"}}>
                    {item.title}
                </Link>
            </div>
            <div className="askInfo-items">
                <div className="askInfo-itemProgress">{item.status}</div>
                <div className="askInfo-itemDate">{formatDate(item.createdAt)}</div>
                <div className="askInfo-itemButton" onClick={() => onClickDelete(item.id)}>
                    {item.status === "답변 완료" ? (
                    <div className="askInfo-itemButton-placeholder"></div>
                    ) : (
                    <Button state="ask" className="askInfo-itemdelete" />
                    )}
                </div>            
            </div>
        </div>
    ));

    return (
        <div className="askContainer">
            <Header />
            <div className="askBox">
                <div className="askContent">
                    <div className="profile">
                        <Profile/>
                    </div>
                    <div className="askProfile">
                        <div className="askTitle">문의사항</div>
                        <div className="askInfo">
                            <div className="askInfo-top">
                                <div className="askInfo-title">제목</div>
                                <div className="askInfo-progress">처리현황</div>
                                <div className="askInfo-date">문의일</div>
                                <div>삭제</div>
                            </div>
                            <div className="askInfo-scrollable">
                                {AskListItem}
                            </div>
                            <Link to="/askwrite" style={{ textDecoration: "none" }}>
                            <div className="askInfo-write"><Button state="askWrite" /></div>
                            </Link>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}