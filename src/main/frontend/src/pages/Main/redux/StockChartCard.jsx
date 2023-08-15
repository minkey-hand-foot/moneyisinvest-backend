/* eslint-disable react-hooks/rules-of-hooks */
import React, { useState, useEffect } from 'react';
import { useScrollFadeIn } from '../../../hooks/useScrollFadeIn';
import PriceLineChart from './PriceLineChart';

const StockChartCard = ({ data, name }) => {
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    if (data && data.length > 0) {
      setLoading(false);
    } else {
      setLoading(true);
    }
  }, [data]);
  
  if (loading) {
    return (
      <div>
        <div {...useScrollFadeIn('up', 1, 0.25)}>Loading...</div>
      </div>
    );
  }
  
  const latestData = data[data.length - 1];
  const strokeColor = latestData.rate.includes('-') ? '#1C77FF' : '#FF1C1C';

  return (
    <div className="stockChartCard" {...useScrollFadeIn('up', 1, 0.25)}>
      <div className="stockChartName">{name}</div>
      <div className="stockChartList">
        <div className="stockChartItem">지수</div>
        <div className="stockChartRateValue">
          {(latestData && latestData.rate) ? (
            <span
              className={`${
                latestData.rate.includes('-') ? 'negative' : 'positive'
              }-rate`}
            >
              {latestData.rate.split(' ')[1]}
            </span>
          ) : (
            <span>Loading...</span>
          )}
        </div>
      </div>
      <div className="stockChartList">
        <div className="stockChartItem">주가</div>
        <div className="stockChartPriceValue">
          {latestData ? latestData.price : 'Loading...'}
        </div>
      </div>
      <div className="stockChart">
        <PriceLineChart data={data} strokeColor={strokeColor} name={name} />
      </div>
    </div>
  );
};

export default StockChartCard;
