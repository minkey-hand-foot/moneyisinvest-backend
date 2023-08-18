import React from "react";
import {ReactComponent as Right} from "../../assets/images/Right.svg";
import { Link } from "react-router-dom";


export default function StockTime({isOpen, holiday}) {

    const currentDate = new Date();
    const formattedDate = `${currentDate.getFullYear()}.${currentDate.getMonth() + 1}.${currentDate.getDate()}`;
  
    const startIndex = holiday.indexOf('(') + 1;
    const endIndex = holiday.indexOf(')');
    const holidaySubstring = holiday.slice(startIndex, endIndex);
  
    const hasNumber = /\d/.test(holidaySubstring);
    const holidayName = hasNumber ? null : holidaySubstring;

    return (
        <div className="companyStockTimeBox">
            <div className="companyStockTime">{formattedDate} {holiday ? holidayName : ""} 기준 장 거래 {isOpen ? "오픈" : "마감"}</div>
            <div className="companyStockTimeInfo">투자가 머니가 아닌, 실제 거래에서는 지금 주식을 사고 팔 수 {isOpen ? "있어요" : "없어요"}</div>
            <Link to= {`/tbDetail3`} style={{ textDecoration: "none" }}>
            <div className="companyStockTimeBook">
                <div>장 거래 시간 알아보기</div>
                <Right className="companyStockTimeIcon"/>
            </div>
            </Link>
        </div>
    )
}