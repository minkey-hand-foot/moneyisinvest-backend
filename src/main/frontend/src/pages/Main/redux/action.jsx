// actions.js 파일에서 updateRanking 액션을 정의한대로 그대로 사용합니다.
export const updateRanking = (data) => ({
  type: 'UPDATE_RANKING',
  payload: data,
});

export const updateKOSPIData = (data) => ({
  type: 'UPDATE_KOSPI_DATA',
  payload: data,
});

export const updateKOSDAQData = (data) => ({
  type: 'UPDATE_KOSDAQ_DATA',
  payload: data,
});
