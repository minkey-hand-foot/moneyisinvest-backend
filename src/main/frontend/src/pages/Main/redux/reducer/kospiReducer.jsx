const initialState = [];

const kospiReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'UPDATE_KOSPI_DATA':
      return [...state, action.payload];
    default:
      return state;
  }
};

export default kospiReducer;
