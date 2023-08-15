import React, {useState, useContext} from 'react';
import "./AskWrite.scss";
import Header from 'systems/Header';
import Profile from 'systems/Profile';
import Footer from 'components/Footer';
import Button from 'components/Button';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from "context/AuthContext";

export default function AskWrite() {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");

    const navigate = useNavigate();

    const { isLoggedIn, token, userId } = useContext(AuthContext);

    const handleInputTitle = (e) => {
        setTitle(e.target.value);
    }
    const handleInputContent = (e) => {
        setContent(e.target.value);
    }

    const onClickSubmit = () => {
        if (isLoggedIn) {
            axios.post('/api/v1/support/post', {
                    uid: userId,
                    title: title,
                    contents: content
                }, {
                    headers: {
                        'X-Auth-Token': token,
                    }
                })
                .then((res) => {
                    console.log("문의사항 작성 완료",res.data);
                    navigate("/askpage", {replace: true})
                })
                .catch((err) => {
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
            navigate("/signIn", {replace: true});
            console.log("Token is null. Unable to send request.");
        }
    }

    return(
        <div className="askWriteContainer">
            <Header />
            <div className="askWriteBox">
                <div className="askWriteContent">
                    <div className="profile">
                        <Profile/>
                    </div>
                    <div className="askWriteProfile">
                        <div className="askWriteTitle">문의사항</div>
                        <div className="askWriteInfo">
                            <input type="text" className="askWriteInfo-title" placeholder='제목을 입력하세요' onChange={handleInputTitle} value={title}>
                            </input>
                            <div className="askWriteInfo-content">
                                <textarea placeholder="내용을 입력하세요" onChange={handleInputContent} value={content}/>
                            </div>
                            <div className="askWriteInfo-button" onClick={onClickSubmit}><Button state="askUpload"/></div>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}