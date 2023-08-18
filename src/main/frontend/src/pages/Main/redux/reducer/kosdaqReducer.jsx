const initialState = [];

const kosdaqReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'UPDATE_KOSDAQ_DATA':
      return [...state, action.payload];
    default:
      return state;
  }
};

export default kosdaqReducer;
