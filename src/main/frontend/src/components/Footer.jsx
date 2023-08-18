import React from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";

const footer = css`
    margin-bottom: 4rem;
    margin-top: 8.75rem;
    display: flex;
    height: 1rem;
    justify-content: center;
    width: 100%;
    tr {
        display: flex;
        margin: auto;
        td {
        color: #929292;
        font-size: 0.625rem;
        font-weight: 400;
        border-left: 0.0625rem #929292 solid;
        padding: 0 1.23569rem;
        text-align: center;
        &:before {
            padding: 0 1.23569rem;
            text-align: center;
            display: flex;
            margin: auto 0;
        }
    }
    td:first-of-type {
        border: none;
        padding: 0 1.23569rem 0 0;
    }
    td:last-of-type {
        padding: 0 0 0 1.23569rem;
    }
}
`
export default function Footer() {
    return (
        <table css={footer}>
            <tr>
                <td>이용약관</td>
                <td>개인정보처리방침</td>
                <td>고객센터</td>
                <td>사업자 정보확인</td>
            </tr>
        </table>
    )
}