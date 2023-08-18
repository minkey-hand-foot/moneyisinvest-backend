const initialState = [];

const storeReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'STORE_STOCK_DATA':
      return [...state, action.payload];
    default:
      return state;
  }
};

export default storeReducer;
