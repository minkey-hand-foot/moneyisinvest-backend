import { createStore } from 'redux';
import reducer from '../../../redux/rootReducer';

const store = createStore(reducer);

export default store;
