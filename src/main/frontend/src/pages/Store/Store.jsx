import React, { useState, useEffect } from 'react';
import "./Store.scss";
/** @jsxImportSource @emotion/react */
import Header from 'systems/Header';
import Button from "components/Button";
import Footer from "components/Footer";
import {ReactComponent as Search} from "../../assets/images/search.svg";
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Store = () => {

  //상점에 등록된 모든 상품 조회
  const [productsList, setProductsList] = useState([
    
      /*{
        id: 1,
        category: '카페',
        name: 'Product 1',
        price: 10000,
        imageUrl": 'https://via.placeholder.com/68x68'
      },
      {
        id: 2,
        category: '카페',
        name: 'Product 2',
        price: 15000,
        imageUrl": 'https://via.placeholder.com/68x68'
      }*/
   
  ]);

  
  
  useEffect(() => {
    const token = sessionStorage.getItem("token");

    //상점에 등록된 모든 상품 조회
    const fetchData = async () => {
      console.log("fetchData 호출"); 
      if (!token || token.trim() === "") {
        console.error("토큰이 누락되었습니다. 로그인 후 다시 시도해 주세요.");
        return;
      }
  
      try {
        const response = await axios.get("/api/v1/shop/get/items", {
          headers: {
            "X-AUTH-TOKEN": token,
          },
          params: {
            size: 5,
            page: 0,
          },
        });
        setProductsList(response.data);
        console.log("상점 상품 정보 load success");
        console.log(response);
      } catch (error) {
        // 에러 처리
        console.error("API 요청 중 에러가 발생했습니다:", error);
      }
    };
  
    fetchData();
  }, []); // 빈 배열을 넣어서 컴포넌트 마운트 시에만 실행되도록 합니다.
  

  //상품의 고유 ID 값으로 상품 구매
  const navigate = useNavigate();

  const onBuy = () => {
    const apiClient = axios.create({
      baseURL: process.env.REACT_APP_API_URL,
    });
    const token = sessionStorage.getItem("token");

    if (token !== null) {
      apiClient.post('/api/v1/shop/buy/items/id',{
          headers: {
            "X-Auth-Token": token,
          },
            })
            .then((res) => {
                console.log("구매 완료",res.data);
                alert("구매가 완료되었습니다!");
                setCart([]);
                navigate("/buyList", {replace: true})
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

   /* const apiClient = axios.create({
      baseURL: process.env.REACT_APP_API_URL,
    });

    const token = sessionStorage.getItem("token");
    const onBuy2 = () => {
      apiClient.post("/api/v1/shop/buy/items/id",{}, {
          params: { id: 1 }, // id
          headers: {
              'X-AUTH-TOKEN': token
          }
      }).then((res) => {
          console.log(res.data);
          const redirectUrl = res.data.next_redirect_pc_url;
          window.location.href = redirectUrl;
      },).catch((err)=> {
          console.log(err);
      })
    } */
  
    const [cart, setCart] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);

  const addToCart = (item) => {
    setCart([...cart, item]);
  };

  /*const onBuy = () => {
    alert("구매가 완료되었습니다!");
    setCart([]);
  };*/

    // 버튼을 클릭하면 선택된 카테고리를 설정하는 함수
    const selectCategory = (category) => {
      setSelectedCategory(category);
    };

    // 스토어의 모든 상품을 보여주는 함수
    const showAllProducts = () => {
      setSelectedCategory(null);
    };


    // productsList에 선택된 카테고리에 따라 필터 적용
    const filteredProducts = selectedCategory
    ? productsList.filter((product) => product.category === selectedCategory)
    : productsList;

  return (
    <div className='storeContainer'>
      <Header/>
      <div className='storeBox'>
        <div className='storeContent'>
          <div className="userCart">
            <div className="usercart-text">님이 담은 상품이에요 ({cart.length})</div>
            {cart.length > 0 && (
              <div className="cart-section">
                <ul className="cart-list">
                  {cart.map((item, index) => (
                    <li key={index}>
                      <img className='item-img' src={item.imageUrl} alt={item.name} />
                      {item.itemName} - {item.price.toLocaleString()} 스톡
                    </li>
                  ))}
                </ul>
                <hr className='hr'/>
                <div>
                  총합
                  {cart.reduce((total, item) => total + item.price, 0).toLocaleString()} 스톡
                  </div>
                <button  className='cartBuy-btn' onClick={onBuy}>구매하기</button>
              </div>
            )}
          </div>
          <div className="row-container">
            <div className='storeTitle'>상점</div>
      <div className='store-top'>
        <div className="category">
        <button
            onClick={showAllProducts}
            className={`category-btn ${selectedCategory === null ? 'active' : ''}`}
          >
            전체보기
          </button>
          <button
            onClick={() => selectCategory('식당')}
            className={`category-btn ${selectedCategory === '식당' ? 'active' : ''}`}
          >
            식당
          </button>
          <button
            onClick={() => selectCategory('카페')}
            className={`category-btn ${selectedCategory === '카페' ? 'active' : ''}`}
          >
            카페
          </button>
          <button
            onClick={() => selectCategory('패스트푸드')}
            className={`category-btn ${selectedCategory === '패스트푸드' ? 'active' : ''}`}
          >
            패스트푸드
          </button>
          <button
            onClick={() => selectCategory('전자기기')}
            className={`category-btn ${selectedCategory === '전자기기' ? 'active' : ''}`}
          >
            전자기기
          </button>
        </div>
        <div className="StoreSearch">
            <input type="text"/>
            <div><Search/></div>
        </div>
      </div>
      <div className="products-table">
        <table>
            <thead>
                <tr>
                <th></th>
                <th>상품명</th>
                <th>교환가</th>
                <th>장바구니 또는 구매하기</th>
                </tr>
            </thead>
            <tbody>
                {filteredProducts.map((product) => (
                <tr key={product.id} className="product-item">
                    <td>
                    <img className='product-img' src={product.imageUrl} alt={product.name} />
                    </td>
                    <td>{product.itemName}</td>
                    <td>{product.price.toLocaleString()}스톡</td>
                    <td className='td-btn'>
                    <div onClick={() => addToCart(product)}><Button state="basket" >장바구니</Button></div>
                    <div onClick={onBuy}><Button state="buy" >구매하기</Button></div>
                    </td>
                </tr>
                ))}
            </tbody>
        </table>
      </div>
      </div>
      </div>
      <Footer/>

    </div>
  </div>
  );
};

export default Store;
