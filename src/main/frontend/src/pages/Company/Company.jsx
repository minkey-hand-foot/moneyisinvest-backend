import React, { useEffect, useState } from "react";
import "./Company.scss";
import Header from "systems/Header";
import Footer from "components/Footer";
import StockChart from "./redux/StockChart";
import { useSelector, useDispatch } from "react-redux";
import { updateStock, storeStock } from "./redux/action";
import { FiChevronDown } from "react-icons/fi";
import { ReactComponent as Warning } from "../../assets/images/warning.svg";
import StockTime from "./StockTime";
import { ReactComponent as Heart } from "../../assets/images/heart.svg";
import { ReactComponent as FilledHeart } from "../../assets/images/filledHeart.svg";
import Button from "components/Button";
import { Link, useParams } from "react-router-dom";
import axios from "axios";
import { ReactComponent as Profile } from "../../assets/images/profile.svg";
import { ReactComponent as CommentHeart } from "../../assets/images/commentHeart.svg";
import { ReactComponent as Comment } from "../../assets/images/comment.svg";
import { ReactComponent as Education } from "../../assets/images/더보기 화면.svg";
import StockMessage from "./StockMessage";
import Message from "./Message";

export default function Company({ handleSetCompanyName }) {
  const apiClient = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
  });

  const [stockName, setStockName] = useState("");
  const [news, setNews] = useState([]);
  const [community, setCommunity] = useState([]);
  const [isOpen, setIsOpen] = useState();
  const [holiday, setHoliday] = useState("");
  const [data, setData] = useState([]);

  const token = sessionStorage.getItem("token");
  const [isLoggedIn, setIsLoggedIn] = useState();


  const { stockId } = useParams(); // URL로부터 supportId를 가져옵니다.

  const companyName = stockName;

  const handleClick = () => {
    handleSetCompanyName(companyName);
  };

  const dispatch = useDispatch();
  const stock = useSelector((state) => state.stock);
  const storedStock = useSelector((state) => state.storedStock);

  const [stockCoin, setStockCoin] = useState("");
  const [stockPrice, setStockPrice] = useState("");

  useEffect(() => {
    if (token !== null) {
      setIsLoggedIn(true);
    } else {
      setIsLoggedIn(false);
    }
    apiClient
      .get(`/api/v1/stock/get/info?stockId=${stockId}`, {
        headers: {
          "X-AUTH-TOKEN": token,
        },
      })
      .then((res) => {
        console.log("회사 정보 불러오기 성공", res.data);
        setStockName(res.data.stockName);
      })
      .catch((err) => {
        console.log("회사 정보 불러오기 실패:", err);
      });

    apiClient
      .post("/api/v1/stock/get/stockByDay", {
        stockCode: stockId,
      })
      .then((res) => {
        console.log("일별 주식 조회", res.data);
        dispatch(storeStock(res.data));
      })
      .catch((err) => {
        console.log("일별 주식 조회 실패", err);
      });

    apiClient
      .get("/api/v1/stock/holiday/now")
      .then((response) => {
        console.log("장 시간 데이터:", response.data.opened);
        setIsOpen(response.data.opened);
        setHoliday(response.data.reason);
      })
      .catch((error) => {
        console.error("장 시간 조회 에러:", error);
      });

      apiClient.get(`/api/v1/favorite/get/status?stockCode=${stockId}`, {
        headers: {
            'X-Auth-Token': token,
        },
      })
      .then((res) => {
          console.log("관심 주식 렌더링 성공",res.data);
          setIsHeartFilled(res.data);
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

    

    const newsapiUrl = `/api/v1/stock/get/news?stockId=${stockId}`;
    apiClient
      .get(newsapiUrl)
      .then((response) => {
        console.log("뉴스 응답 데이터:", response.data);
        setNews(response.data.slice(0, 3));
      })
      .catch((error) => {
        console.error("뉴스 에러 발생:", error);
      });

    apiClient
      .get(`/api/v1/community/get?stockId=${stockId}`, {})
      .then((response) => {
        console.log("커뮤니티 응답 데이터:", response);
        setCommunity(response.data.slice(0, 3));
      })
      .catch((error) => {
        console.error("커뮤니티 에러 발생:", error);
      });

      apiClient
      .get(`/api/v1/stock/get/companyResult?stockId=${stockId}`)
      .then((response) => {
        console.log("재무제표 응답 데이터:", response);
        setData(response.data);
      })
      .catch((error) => {
        console.error("재무제표 에러 발생:", error);
      });


    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const webSocketUrl = `${protocol}//${window.location.hostname}:${window.location.port}/stock`;
    const stockSocket = new WebSocket(webSocketUrl);
    stockSocket.onopen = () => {
      console.log("Stock Connected");
      stockSocket.send(stockId);
    };
    stockSocket.onmessage = (res) => {
      const receivedData = JSON.parse(res.data); // 데이터를 객체로 변환
      dispatch(updateStock(receivedData));
      setStockCoin(receivedData.stock_coin);
      setStockPrice(receivedData.stock_price);
      //console.log(receivedData); // 수정된 데이터를 출력
    };
    stockSocket.onclose = () => {
      console.log("Stock DisConnnected");
    };
    stockSocket.onerror = (event) => {
      console.log(event);
    };

    return () => {
      stockSocket.close();
    };
  }, [dispatch, stockId]);

  // useState를 사용하여 Education 컴포넌트 표시 상태를 저장
  const [isEducationVisible, setIsEducationVisible] = useState(false);

  // 클릭 이벤트 핸들러를 작성
  const handleCompanyHelpClick = () => {
    setIsEducationVisible(!isEducationVisible);
  };

  // useState를 사용하여 하트의 토글 상태를 저장
  const [isHeartFilled, setIsHeartFilled] = useState(false);

  // 하트 클릭 시 상태를 변경하는 함수를 작성
  const handleHeartClick = () => {
    const token = sessionStorage.getItem("token");
    const newIsHeartFilled = !isHeartFilled;
    setIsHeartFilled(newIsHeartFilled);
  
    if (newIsHeartFilled) {
      apiClient
        .post(
          "/api/v1/favorite/post",
          {},
          {
            headers: {
              "X-AUTH-TOKEN": token,
            },
            params: {
              stockId: stockId,
            },
          }
        )
        .then((res) => {
          console.log("관심 주식 추가", res.data);
        })
        .catch((err) => {
          console.log(err);
          setIsHeartFilled(!newIsHeartFilled);
        });
    } else {
      apiClient
        .delete("/api/v1/favorite/remove", {},
         {
            headers: {
              "X-AUTH-TOKEN": token,
            },
            params: {
              stockId: stockId,
            },
          }
        )
        .then((res) => {
          console.log("관심 주식 삭제", res.data);
        })
        .catch((err) => {
          console.log(err);
          setIsHeartFilled(!newIsHeartFilled);
        });
    }
  };
  
  const [isPopupVisible, setIsPopupVisible] = useState(false);
  const [buttonState, setButtonState] = useState("");

  function handleSellButtonClick() {
    setButtonState('sell');
  }

  function handleBuyButtonClick() {
    setButtonState('buy');
  }

  const handleButtonClick = () => {
    setIsPopupVisible(true);
  };

  const closePopup = () => {
    setIsPopupVisible(false);
  };

  // 팝업 외부 영역 클릭 시 팝업 닫기
  const handleOutsideClick = (e) => {
    if (e.target && e.target.classList.contains("overlay")) {
      closePopup();
    }
  };

  // 이벤트 전파 방지
  const stopPropagation = (e) => {
    e.stopPropagation();
  };

  const newsItem = news.map((item) => (
    <div className="companynewsList">
      <div
        className="companynewsItems"
        onClick={() => window.open(item.newsUrl)}
      >
        <div className="companynewsItemCompany">{item.newsCompany}</div>
        <div className="companynewsItemTitle">{item.newsTitle}</div>
        <div className="companynewsItemContent">{item.newsPreview}</div>
      </div>
      <img alt="썸네일" src={item.newsThumbnail} className="companynewsImage" />
    </div>
  ));

  const communityItem = community.map((item) => (
    <div className="companycommunityList" key={item.id}>
      <div className="companycommunityProfile">
        <Profile className="companycommunityProfileImg" />
        <div className="companycommunityName">{item.name}</div>
      </div>
      <div className="companycommunityComment">{item.comment}</div>
      <div className="companycommunityReply">
        <div className="companycommunityIcons">
          <CommentHeart className="companycommunityIcon" />
          <div>0</div>
        </div>
        <div className="companycommunityIcons">
          <Comment className="companycommunityIcon" />
          <div>{item.replyCount}</div>
        </div>
      </div>
    </div>
  ));

  return (
    <div className="companyContainer">
      <Header />
      <div className="companyBox">
        <div className="companyContent">
          <div className="companyChart">
            <StockChart stock={stock} storedStock={storedStock} />
          </div>
          <div className="companyHelp" onClick={handleCompanyHelpClick}>
            <div className="companyHelpText">
              <Warning className="companyStockIcon" />
              <div>주식 차트에도 패턴이 있다는 거 알고 계신가요?</div>
            </div>
            <FiChevronDown className="companyStockDown" />
          </div>
          {isEducationVisible && <Education className="companyEducation" />}
          <div className="companyInfo">
            <div className="companyInformation">
              <div className="companyInfoTitle">
                <div className="companyName">{companyName}</div>
                <div className="companyStockDeal">
                  <div className="companyStockDeal-stock">{stockCoin}스톡</div>
                  <div className="companyStockDeal-price">{stockPrice}원</div>
                </div>
              </div>
              <div className="companyInfoContent">
                <StockTime isOpen={isOpen} holiday={holiday} />
              </div>
            </div>
            {isLoggedIn && (
            <div className="companyStockBtn">
              <div className="companyStockDealBtn">
                {isHeartFilled ? (
                  <FilledHeart
                    onClick={handleHeartClick}
                    className="companyStockHeart"
                    color="#85D6D1"
                  />
                ) : (
                  <Heart
                  onClick={handleHeartClick}
                    className="companyStockHeart"
                    color="#85D6D1"
                  />
                )}
                <div onClick={() => {handleSellButtonClick(); handleButtonClick();}}>
                  <Button state="stocksell" />
                </div>
                <div onClick={() => {handleBuyButtonClick(); handleButtonClick();}}>
                  <Button state="stockbuy" />
                </div>
              </div>
            </div>)}
            {isPopupVisible ? (
               <div className="overlay" onClick={handleOutsideClick}>
                <div className="stockMessage" onClick={stopPropagation}>
                    <StockMessage className="stockMessage" stockId={stockId} state={buttonState} stockPrice={stockPrice} onClick={stopPropagation}/>
                </div>
                </div>
            ):null}
          </div>
          <div className="companyNews">
            <div className="companyNewsText">
              <div className="companyNewsTitle">회사 뉴스</div>
              <Link
                to={`/news/${stockId}`}
                style={{ textDecoration: "none" }}
                onClick={handleClick}
              >
                <div className="companyNewsSubtitle">더보기</div>
              </Link>
            </div>
            <div className="companyNewsList">{newsItem}</div>
          </div>
          <div className="companyCommunity">
            <div className="companyCommunityText">
              <div className="companyCommunityTitle">커뮤니티</div>
              <Link
                to={`/Community/${stockId}`}
                style={{ textDecoration: "none" }}
                onClick={handleClick}
              >
                <div className="companyCommunitySubtitle">더보기</div>
              </Link>
            </div>
            <div className="companyCommunityList">{communityItem}</div>
          </div>
          <div className="companyTable">
            <div className="companyTableTitle">실적 분석</div>
            {
              data.length > 0 ? (
            <table>
              <thead>
                <tr>
                  <th>주요재무정보</th>
                  <th>{data[3].date}</th>
                  <th>{data[2].date}</th>
                  <th>{data[1].date}</th>
                  <th>{data[0].date}</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>매출액(억원)</td>
                  <td>{data[3].take}</td>
                  <td>{data[2].take}</td>
                  <td>{data[1].take}</td>
                  <td>{data[0].take}</td>
                </tr>
                <tr>
                  <td>영업이익(억원)</td>
                  <td>{data[3].operatingProfit}</td>
                  <td>{data[2].operatingProfit}</td>
                  <td>{data[1].operatingProfit}</td>
                  <td>{data[0].operatingProfit}</td>
                </tr>
                <tr>
                  <td>당기순이익(억원)</td>
                  <td>{data[3].netIncome}</td>
                  <td>{data[2].netIncome}</td>
                  <td>{data[1].netIncome}</td>
                  <td>{data[0].netIncome}</td>
                </tr>
                <tr>
                  <td>부채비율(%)</td>
                  <td>{data[3].debtRatio}</td>
                  <td>{data[2].debtRatio}</td>
                  <td>{data[1].debtRatio}</td>
                  <td>{data[0].debtRatio}</td>
                </tr>
                <tr>
                  <td>당좌비율(%)</td>
                  <td>{data[3].quickRatio}</td>
                  <td>{data[2].quickRatio}</td>
                  <td>{data[1].quickRatio}</td>
                  <td>{data[0].quickRatio}</td>
                </tr>
                <tr>
                  <td>유동비율(%)</td>
                  <td>{data[3].retentionRate}</td>
                  <td>{data[2].retentionRate}</td>
                  <td>{data[1].retentionRate}</td>
                  <td>{data[0].retentionRate}</td>
                </tr>
                <tr>
                  <td>PER(배)</td>
                  <td>{data[3].per}</td>
                  <td>{data[2].per}</td>
                  <td>{data[1].per}</td>
                  <td>{data[0].per}</td>
                </tr>
                <tr>
                  <td>PBR(배)</td>
                  <td>{data[3].pbr}</td>
                  <td>{data[2].pbr}</td>
                  <td>{data[1].pbr}</td>
                  <td>{data[0].pbr}</td>
                </tr>
              </tbody>
            </table>
              ):(
                <div>데이터가 없습니다. 새로고침을 눌러주세요</div>
              )
            }
            <Link to={`/tbDetail1`} style={{ textDecoration: "none" }}>
              <div className="companyHelp">
                <div className="companyHelpText">
                  <Warning className="companyStockIcon" />
                  <div>기업 실적 분석에 쓰이는 재무제표에 대해 알아볼까요?</div>
                </div>
              </div>
            </Link>
          </div>
        </div>
        <Footer />
      </div>
    </div>
  );
}
