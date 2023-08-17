import React, { useState } from "react";
import "./styles/global.scss";
import { Route, Routes } from "react-router-dom";
import SignIn from "./pages/SignIn/SignIn";
import SignUp from "./pages/SignUp/SignUp";
import MyPage from "pages/MyPage/MyPage";
import AskPage from "pages/MyPage/AskPage";
import AskWrite from "pages/MyPage/AskWrite";
import News from "pages/News/News";
import Store from "./pages/Store/Store";
import AllNews from "pages/News/AllNews";
import StockHold from "pages/MyPage/StockHold";
import StockInterest from "pages/MyPage/StockInterest";
import BuyList from "pages/MyPage/BuyList";
import GuestMain from "pages/Main/GuestMain";
import UserMain from "pages/Main/UserMain";
import Textbook from "pages/Education/Textbook";
import TbDetail1 from "pages/Education/TbDetail1";
import TbDetail2 from "pages/Education/tbDetail2";
import TbDetail3 from "pages/Education/TbDetail3";
import AskDetail from "pages/MyPage/AskDetail";
import Company from "pages/Company/Company";
import Community from "pages/Community/Community";
import Payment from "pages/Payment/Payment";
import MessagePage from "components/MessagePage";
import MyWallet from "pages/MyPage/MyWallet";

function App() {
  const [companyName, setCompanyName] = React.useState("");

  const handleSetCompanyName = (name) => {
    setCompanyName(name);
  };

  const [isLoggedIn, setIsLoggedIn] = useState(
    sessionStorage.getItem("token") !== null
  );

  return (
    <Routes>
      <Route path="/" element={isLoggedIn ? <UserMain /> : <GuestMain />} />
      <Route
        path="/signIn"
        element={<SignIn setIsLoggedIn={setIsLoggedIn} />}
      />
      <Route path="/signUp" element={<SignUp />} />
      <Route
        path="/mypage"
        element={<MyPage setIsLoggedIn={setIsLoggedIn} />}
      />
      <Route path="/askpage" element={<AskPage />} />
      <Route path="/askwrite" element={<AskWrite />} />
      <Route path="/askpage/:supportId" element={<AskDetail />} />
      <Route
        path="/news/:stockId"
        element={<News companyName={companyName} />}
      />
      <Route path="/Store" element={<Store />} />
      <Route path="/allNews" element={<AllNews />} />
      <Route path="/stockHold" element={<StockHold />} />
      <Route path="/stockInterest" element={<StockInterest />} />
      <Route path="/buyList" element={<BuyList />} />
      <Route path="/main" element={<UserMain />} />
      <Route path="/textbook" element={<Textbook />} />
      <Route path="/TbDetail1" element={<TbDetail1 />} />
      <Route path="/TbDetail2" element={<TbDetail2 />} />
      <Route path="/TbDetail3" element={<TbDetail3 />} />
      <Route
        path="/company/:stockId"
        element={<Company handleSetCompanyName={handleSetCompanyName} />}
      />
      <Route path="/Community" element={<Community />} />
      <Route path="/pay" element={<Payment />} />
      <Route path="/MessagePage" element={<MessagePage />} />
      <Route path="/myWallet" element={<MyWallet />} />
    </Routes>
  );
}

export default App;
