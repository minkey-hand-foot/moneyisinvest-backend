import { combineReducers } from 'redux';
import rankingReducer from '../pages/Main/redux/reducer/rankingReducer';
import kospiReducer from '../pages/Main/redux/reducer/kospiReducer';
import kosdaqReducer from '../pages/Main/redux/reducer/kosdaqReducer';
import reducer from '../pages/Company/redux/reducer';
import storeReuducer from 'pages/Company/redux/storeReducer';

const rootReducer = combineReducers({
  rank: rankingReducer,
  kospiData: kospiReducer,
  kosdaqData: kosdaqReducer,
  stock: reducer,
  storedStock: storeReuducer
});

export default rootReducer;
