import React, { useState } from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import { ReactComponent as PageButtonImage } from "../assets/images/pageButton.svg";

export default function Pagination({ totalPages }) {
  const [currentPage, setCurrentPage] = useState(1);
  const pageNumbers = Array.from({ length: totalPages }, (_, index) => index + 1);

  const pagination = css`
    display: flex;
    flex-direction: row;
    gap: 2.38rem;
    justify-content: center;
  `;
  const number = css`
    color: #a9a9a9;
    font-family: Noto Sans;
    font-size: 0.875rem;
    font-weight: 400;
    letter-spacing: -0.0175rem;
    border: 0;
    background-color: transparent;
    cursor: pointer;

    &:hover {
      color: #333;
    }
  `;

  const handlePageClick = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleImageClick = () => {
    if (currentPage > 10) {
      setCurrentPage(currentPage + 10); // 이미지 버튼을 누를 때 현재 페이지에 10을 더하여 페이지 번호를 11부터 시작하도록 설정
    }
  };

  return (
    <div css={pagination}>
      {pageNumbers.map((pageNumber) => (
        <button
          key={pageNumber}
          onClick={() => handlePageClick(pageNumber)}
          css={number}
        >
          {pageNumber}
        </button>
      ))}
      {totalPages > 10 && (
        <button onClick={handleImageClick}>
          <PageButtonImage />
        </button>
      )}
    </div>
  );
}
