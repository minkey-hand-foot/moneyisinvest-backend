import React, { useContext } from "react";
import "./styles/global.scss";
import { Route, Routes } from "react-router-dom";
import { AuthProvider, AuthContext } from "context/AuthContext";
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

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

const AppContent = () => {
  const { isLoggedIn } = useContext(AuthContext);
  return (
    <Routes>
      <Route path="/" element={isLoggedIn ? <UserMain /> : <GuestMain />} />
      <Route path="/signIn" element={<SignIn />} />
      <Route path="/signUp" element={<SignUp />} />
      <Route path="/mypage" element={<MyPage />} />
      <Route path="/askpage" element={<AskPage />} />
      <Route path="/askwrite" element={<AskWrite />} />
      <Route path="/askpage/:supportId" element={<AskDetail />} />
      <Route path="/news" element={<News />} />
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
    </Routes>
  );
};

export default App;
