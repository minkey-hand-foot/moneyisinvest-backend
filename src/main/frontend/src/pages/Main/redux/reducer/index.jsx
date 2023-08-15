import { combineReducers } from 'redux';
import rankingReducer from './rankingReducer';
import kospiReducer from './kospiReducer';
import kosdaqReducer from './kosdaqReducer';

const rootReducer = combineReducers({
  rank: rankingReducer,
  kospiData: kospiReducer,
  kosdaqData: kosdaqReducer,
});

export default rootReducer;
