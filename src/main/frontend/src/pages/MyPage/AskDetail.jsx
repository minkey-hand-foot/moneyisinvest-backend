import React, { useState, useEffect } from "react";
import "./AskDetail.scss";
import Header from "systems/Header";
import Profile from "systems/Profile";
import Footer from "components/Footer";
import Button from "components/Button";
import axios from "axios";
import { Link, useNavigate, useParams } from "react-router-dom";

export default function AskDetail() {
  const apiClient = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
  });

  const navigate = useNavigate();
  const { supportId } = useParams(); // URL로부터 supportId를 가져옵니다.

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [createdAt, setCreatedAt] = useState("");

  // fetchData 함수 정의
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const fetchData = async () => {
    try {
      const token = sessionStorage.getItem("token");

      const response = await apiClient.get(`/api/v1/support/get/user-support?support_id=${supportId}`, {
        headers: {
          "X-AUTH-TOKEN": token,
        },
      });

      const data = response.data;
      console.log("문의사항 상세보기 성공", response);
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

  const onClickDelete = () => {
    const token = sessionStorage.getItem("token");
    const id = sessionStorage.getItem("id");
    if (token !== null) {
      apiClient
        .delete("/api/v1/support/remove", {
          headers: {
            "X-AUTH-TOKEN": token,
          },
          params: {
            supportId: parseInt(supportId),
          },
        })
        .then((response) => {
          alert("문의사항이 삭제되었습니다!");
          console.log("문의사항 삭제 완료", response.data);
          window.location.href = "/askPage";
        })
        .catch((err) => {
          if (err.response) {
            // 서버 응답이 온 경우 (에러 응답)
            console.log(
              "Error response:",
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
    } else {
      alert("로그인 해주세요!");
      navigate("/signIn", { replace: true });
      console.log("Token is null. Unable to send request.");
    }
  };

  return (
    <div className="askWriteContainer">
      <Header />
      <div className="askWriteBox">
        <div className="askWriteContent">
          <div className="profile">
            <Profile />
          </div>
          <div className="askWriteProfile">
            <div className="askWriteTitle">문의사항</div>
            <div className="askWriteInfo">
              <div className="askDetailInfo-title">
                <div>{title}</div>
                <div className="askDetailInfo-date">
                  {formatDate(createdAt)}
                </div>
              </div>
              <div className="askDetailInfo-content">
                <div>{content}</div>
              </div>
              <div className="askWriteInfo-button" onClick={onClickDelete}>
                <Button state="askDetail" />
              </div>
            </div>
          </div>
        </div>
        <Footer />
      </div>
    </div>
  );
}
