import React from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";

/* 매도: stocksell, 매수: stockbuy, 장바구니 구매하기: shopping, *담기: basket, *구매: buy,
로그인: login, 글쓰기: write, *댓글: comment, 댓글수정: edit, 댓글삭제: delete, 대댓글작성: reply, *마이페이지: mine, *거래내역: interest, *문의사항: ask
인증하기: signConfirm, 문의사항글쓰기: askWrite 문의사항업로드: askUpload +: plus -: minus 거래하기: stockDeal 거래완료: dealdone, 거래취소: dealnone
완료: paydone, 결제취소: paynone*/



const buttonwidth = {
    default: "5.0625rem",
    stocksell: "6.25rem",
    stockbuy: "6.25rem",
    shopping: "8.875rem",
    login: "23.625rem",
    signup: "23.625rem",
    write: "2rem",
    comment: "2.9375rem",
    edit: "2.9375rem",
    delete: "2.9375rem",
    reply: "2.9375rem",
    mine: "2.9375rem",
    signConfirm: "2.9375rem",
    plus: "3.75rem",
    minus: "3.75rem",
    stockDeal: "22.6875rem",
    dealdone: "7.5rem",
    dealnone: "7.5rem",
    paydone: "8.875rem",
    paynone: "10.31463rem",
};
const buttonheight = {
    default: "2.125rem",
    stockbuy: "2.25rem",
    stocksell: "2.25rem",
    login: "3.25rem",
    signup: "3.25rem",
    write: "5.75rem",
    comment: "1.1875rem",
    delete: "1.1875rem",
    edit: "1.1875rem",
    reply: "1.1875rem",
    mine: "1.1875rem",
    signConfirm: "1.1875rem",
    plus: "2.875rem",
    minus: "2.875rem",
    stockDeal: "2.625rem",
    dealdone: "2.25rem",
    dealnone: "2.25rem",
    paydone: "1.93594rem",
    paynone: "2.25rem"
}
const buttonradius = {
    default: "0.4375rem",
    comment: "0.1875rem",
    edit: "0.1875rem",
    delete: "0.1875rem",
    reply: "0.1875rem",
    login: "0.625rem",
    signup: "0.625rem",
    mine: "0.1875rem",
    signConfirm: "0.1875rem",
}
const buttonborder = {
    default: "none",
    comment: "0.0625rem solid #3EB7AF",
    reply: "0.0625rem solid #3EB7AF",
    basket: "0.0625rem solid #3EB7AF",
    mine: "0.0625rem solid #3EB7AF",
    interest: "0.0625rem solid #3EB7AF",
    ask: "0.0625rem solid #3EB7AF",
    signConfirm: "0.0625rem solid #3EB7AF"
}
const buttonbackground = {
    default: "#85D6D1",
    stockbuy: "#69A5FF",
    stocksell: "#FF7474",
    comment: "none",
    reply: "none",
    basket: "none",
    mine: "none",
    interest: "none",
    ask: "none",
    signConfirm: "none",
    stockDeal: "#3EB7AF",
}
const buttoncolor = {
    default: "#000",
    stockbuy: "#fff",
    stocksell: "#fff",
    mine: "#797979",
    signConfirm: "#797979",
    stockDeal: "#fff"
}
const buttonsize = {
    default: "0.75rem",
    stocksell: "1rem",
    stockbuy: "1rem",   
    comment: "0.625rem",
    edit: "0.625rem",
    delete: "0.625rem",
    reply: "0.625rem",
    mine: "0.625rem",
    login: "0.875rem",
    signup: "0.875rem",
    signConfirm: "0.625rem",
    plus: "1.875rem",
    minus: "1.875rem",
    stockDeal: "0.875rem"
};
const buttonweight = {
    default: "500",
    login: "600",
    signup: "600",
    plus: "300",
    minus: "300",
    stockDeal: "700"
}
const buttontext = {
    default: "버튼",
    stockbuy: "매수",
    stocksell: "매도",
    shopping: "구매하기",
    basket: "장바구니",
    buy: "구매하기",
    login: "로그인",
    comment: "작성",
    edit: "수정",
    delete: "삭제",
    reply: "대댓글",
    mine: "변경",
    interest: "취소하기",
    ask: "삭제하기",
    signup: "회원가입",
    signConfirm: "인증하기",
    askWrite: "작성하기",
    askUpload: "업로드 하기",
    askDetail: "삭제하기",
    plus: "+",
    minus: "-",
    stockDeal: "거래하기",
    dealdone: "거래 내역 확인하기",
    dealnone: "다시 거래하기",
    paydone: "홈으로 돌아가기",
    paynone: "결제 페이지로 돌아가기",
}
export default function Button(props) {
    const button = css`
    display: flex;
    width: ${buttonwidth[props.state] || buttonwidth.default};
    height: ${buttonheight[props.state] || buttonheight.default};
    justify-content: center;
    align-items: center;
    gap: 0.625rem;
    border: ${buttonborder[props.state] || buttonborder.default};
    border-radius: ${buttonradius[props.state] || buttonradius.default};
    background: ${buttonbackground[props.state] || buttonbackground.default};
    color: ${buttoncolor[props.state] || buttoncolor.default};
    font-size: ${buttonsize[props.state] || buttonsize.default};
    font-weight: ${buttonweight[props.state] || buttonweight.default};
    line-height: 0.79588rem;
    &:hover {
        cursor: pointer;
    }
    `

    return (
        <div>
            <button css={button}>{buttontext[props.state] || buttontext.default}</button>
        </div>
    )
}