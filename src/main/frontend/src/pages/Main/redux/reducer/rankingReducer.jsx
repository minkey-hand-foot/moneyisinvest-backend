const initialState = [];

const rankingReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'UPDATE_RANKING':
      return action.payload;
    default:
      return state;
  }
};

export default rankingReducer;
