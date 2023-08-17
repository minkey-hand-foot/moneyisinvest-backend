const initialState = [];

const stockReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'UPDATE_STOCK_DATA':
      return [...state, action.payload];
    default:
      return state;
  }
};

export default stockReducer;
