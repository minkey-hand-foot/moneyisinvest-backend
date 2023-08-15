import React, { useState } from 'react';
import "./Store.scss";
/** @jsxImportSource @emotion/react */
import Header from 'systems/Header';
import Button from "components/Button";
import Footer from "components/Footer";
import {ReactComponent as Search} from "../../assets/images/search.svg";

const productsList = [
  {
    id: 1,
    category: '카페',
    name: 'Product 1',
    price: 10000,
    image: 'https://via.placeholder.com/68x68'
  },
  {
    id: 2,
    category: '카페',
    name: 'Product 2',
    price: 15000,
    image: 'https://via.placeholder.com/68x68'
  },
  {
    id: 3,
    category: '식당',
    name: 'Product 3',
    price: 13000,
    image: 'https://via.placeholder.com/68x68'
  },
  {
    id: 4,
    category: '패스트푸드',
    name: 'Product 4',
    price: 13000,
    image: 'https://via.placeholder.com/68x68'
  },
  {
    id: 5,
    category: '전자기기',
    name: 'Product 5',
    price: 13000,
    image: 'https://via.placeholder.com/68x68'
  }
];


const Store = () => {
    const [cart, setCart] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);

  const addToCart = (item) => {
    setCart([...cart, item]);
  };

  const onBuy = () => {
    alert("구매가 완료되었습니다!");
    setCart([]);
  };

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
                      <img className='item-img' src={item.image} alt={item.name} />
                      {item.name} - {item.price.toLocaleString()} 스톡
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
                    <img className='product-img' src={product.image} alt={product.name} />
                    </td>
                    <td>{product.name}</td>
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
