import React, {useEffect, useState, useRef} from "react";
import axios from "axios";
import "./MyPage.scss";
import Header from "../../systems/Header";
import Footer from "components/Footer";
import Profile from "../../systems/Profile";
import MyButton from "../../components/Button";
import { useNavigate } from "react-router-dom";

export default function MyPage({setIsLoggedIn}) {
    const apiClient = axios.create({
        baseURL: process.env.REACT_APP_API_URL,
    });

    const [isNameEditing, setNameEditing] = useState(false);
    const [name, setName] = useState("");
    const [profile, setProfile] = useState("");
    const [id, setId] = useState("");

    const nameRef = useRef("");

    const navigate = useNavigate();

    const handleNameEdit = () => {
        setNameEditing(true);
        if (nameRef.current) {
            nameRef.current.focus();
        }
    };

    const handleNameSave = () => {
        setNameEditing(false);
        // 여기서 서버로 이름 업데이트 요청
    };

    const handleNameChange = (event) => {
        setName(event.target.value);
    };

    const onClickEnter = (e) => {
        if (e.key === 'Enter') {
            handleNameSave();
        }
    }

    useEffect (() => {
        const token = sessionStorage.getItem("token");
        if (token !== null) {
            apiClient.get("/api/v1/profile/user/detail", {
                headers: {
                    'X-Auth-Token': token,
                }
            })
            .then((res) => {
                console.log(res.data);
                setName(res.data.name);
                setProfile(res.data.profileUrl);
                setId(res.data.uid);
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
            console.log("Token is null. Unable to send request.");
        }
    },[]);

    const onClickFileUpload = () => {
        const token = sessionStorage.getItem("token");
        const fileInput = document.getElementById("imgUpload");
        if(fileInput.files.length > 0) {
            const file = fileInput.files[0];
            console.log(file);
            const formData = new FormData();
            formData.append("file", file);
            apiClient.post("/api/v1/profile/upload", formData, {
                headers: {
                    'X-AUTH-TOKEN' : token,
                }
            }).then(res => {
                console.log("프로필 업로드 성공!!", res.data);
                apiClient.get("/api/v1/profile/get", {
                    headers: {
                        'X-Auth-Token': token,
                    }
                })
                .then((res) => {
                    console.log("프로필 불러오기 성공", res.data);
                    setProfile(res.data.url);
                }).catch((res) => {
                    console.log("프로필 불러오지 못함", res);
                    console.log("프로필 불러오지 못함", res);
                })                    
            }).catch((err) => {
                console.log("프로필 업로드 오류", err);
            });
        }  
    };

    const onClickLogout = () => {
        sessionStorage.clear();
        setIsLoggedIn(false);
        navigate("/", {replace: true});
    }

    return (
        <div className="myPageContainer">
            <Header />
            <div className="myPageBox">
                <div className="myPageContent">
                    <div className="profile">
                        <Profile/>
                    </div>
                    <div className="myPageProfile">
                        <div className="myPageTitle">마이페이지</div>
                        <div className="myPageInfo">
                            <div className="myPageInfo-content">
                                <table class="custom-table">
                                    <tr>
                                        <td>프로필 이미지</td>
                                        <td>
                                            <div className="profileChange">
                                                <img alt="profile" src={profile} className="myProfile"/>
                                                <div class="filebox">
                                                    <input type="file" id="imgUpload" onChange={onClickFileUpload}></input>
                                                    <label for="imgUpload">변경</label>
                                                </div>
                                            </div>
                                        </td>
                                        <td></td>
                                    </tr>
                                    <tr>
                                        <td>이름</td>
                                        <td>
                                            <div className="profileNameChange">
                                                {isNameEditing ? (
                                                    <input
                                                        className="myNameInput"
                                                        type="text"
                                                        value={name}
                                                        onChange={handleNameChange}
                                                        ref={nameRef}
                                                        onKeyPress={(e) => onClickEnter(e)}
                                                    />
                                                ) : (
                                                    <div className="myName">{name}</div>
                                                )}
                                                <div //onClick={
                                                        //isNameEditing ? handleNameSave : handleNameEdit}
                                                    >
                                                    <MyButton
                                                    state="mine"
                                                    />
                                                </div>
                                            </div>
                                        </td>
                                        <td></td>
                                    </tr>
                                    <tr>
                                        <td>아이디</td>
                                        <td>{id}</td>
                                        <td></td>
                                    </tr>
                                    <tr>
                                        <td>비밀번호</td>
                                        <td>변경일 2023.8.7.월</td>
                                        <td></td>
                                    </tr>
                                </table>
                                <div className="myPageOut" onClick={onClickLogout}>계정 로그아웃</div>
                            </div>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}
