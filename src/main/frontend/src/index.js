import React from "react";
import ReactDOM from "react-dom";
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import store from "./pages/Main/redux/store"; // Redux 스토어 불러오기
import App from "./App"; // 최상위 컴포넌트 불러오기
import "./index.css";

ReactDOM.render(
  <React.StrictMode>
    <Provider store={store}>
      {" "}
      {/* Provider로 Redux 스토어 감싸주기 */}
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </Provider>
  </React.StrictMode>,
  document.getElementById("root")
);
