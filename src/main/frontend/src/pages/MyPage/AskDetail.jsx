import React, {useState, useEffect} from 'react';
import "./AskDetail.scss";
import Header from 'systems/Header';
import Profile from 'systems/Profile';
import Footer from 'components/Footer';
import Button from 'components/Button';
import { useParams } from "react-router-dom";
import axios from 'axios';

export default function AskDetail() {

  const apiClient = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
  });

  
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [createdAt, setCreatedAt] = useState("");
    const { supportId } = useParams(); // URL로부터 supportId를 가져옵니다.

  // fetchData 함수 정의
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const fetchData = async () => {
    try {
      const token = sessionStorage.getItem("token");
      const id = sessionStorage.getItem("id");

      const response = await apiClient.get(
        `/api/v1/support/getOne`, {
          params: {
            supportId: supportId,
            uid: id
          },
          headers: {
            "X-AUTH-TOKEN": token,
          }
        }
      );

    const data = response.data;
    console.log("문의사항 상세보기 성공", data);
    setTitle(data.title);
    setContent(data.contents);
    setCreatedAt(data.createdAt);
  } catch (error) {
    console.log("문의사항 상세보기 실패:", error);
  }
};

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
  };

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
                            <div className="askDetailInfo-title">
                                <div>{title}</div>
                                <div className="askDetailInfo-date">{formatDate(createdAt)}</div>
                            </div>
                            <div className="askDetailInfo-content">
                                <div>{content}</div>
                            </div>
                            <div className="askWriteInfo-button"><Button state="askDetail"/></div>
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}