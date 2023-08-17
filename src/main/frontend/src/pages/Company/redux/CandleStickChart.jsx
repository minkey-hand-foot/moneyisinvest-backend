import React, { useEffect, useState } from 'react';
import Chart from 'react-apexcharts';

const getLatestArray = (data) => {
  if (data.length === 0) {
    return [];
  }

  const latestDataIndex = data.length - 1;
  return data[latestDataIndex];
};

const processData = (data, realTimeData) => {
  if (!data || data.length === 0) {
    return [];
  }

  const realTimeDataByDate = realTimeData && realTimeData.length > 0 ? realTimeData.reduce((dataMap, item) => {
    if(item.currnet_time) {
      const dateStr = new Date(item.currnet_time).toDateString();
      dataMap[dateStr] = item;
    }
    
    return dataMap;
  }, {}) : {};

  const result = data.map((item) => {
    const date = new Date(item.current_date.slice(0,4), item.current_date.slice(4,6)-1, item.current_date.slice(6,8));
    const dateStr = date.toDateString();
    const realTimeDataForDate = realTimeDataByDate[dateStr];

    if (realTimeDataForDate) {
      return {
        x: date,
        y: [
          Number(item.start_Price),
          Number(item.high_Price),
          Number(item.low_Price),
          Number(realTimeDataForDate.stock_price),
        ],
      };
    } else {
      return {
        x: date,
        y: [
          Number(item.start_Price),
          Number(item.high_Price),
          Number(item.low_Price),
          Number(item.end_Price),
        ],
      };
    }
  });

  const latestDate = result[result.length - 1].x;
  const latestRealTimeData = realTimeData[realTimeData.length - 1];
  const latestRealTimeDate = new Date(latestRealTimeData.currnet_time).toDateString();

  if (latestDate.toDateString() !== latestRealTimeDate) {
    result.push({
      x: new Date(latestRealTimeData.currnet_time),
      y: [
        Number(latestRealTimeData.stock_open_price),
        Number(latestRealTimeData.stock_high_price),
        Number(latestRealTimeData.stock_low_price),
        Number(latestRealTimeData.stock_price),
      ],
    });
  }

  return result;
};

const CandleStickChart = ({ chartData, realTimeData = []}) => {
  const [processedData, setProcessedData] = useState(
    processData(getLatestArray(chartData), realTimeData)
  );

  const chartWidth = 930;
  const chartHeight = 430;

  const options = {
    chart: {
      type: 'candlestick',
      toolbar: {
        show: false,
      },
    },
    plotOptions: {
      candlestick: {
        colors: {
          upward: '#FF1C1C',
          downward: '#1C77FF',
        },
      },
    },
    xaxis: {
      type: 'datetime',
      labels: {
        show: true,
      },
    },
    yaxis: {
      tooltip: {
        enabled: true,
      },
      labels: {
        show: true,
      },
    },
  };

  const series = [
    {
      name: 'candle',
      data: processedData,
    },
  ];

  useEffect(() => {
    setProcessedData(processData(getLatestArray(chartData), realTimeData));
  }, [chartData, realTimeData]);

  return (
    <Chart
      options={options}
      series={series}
      type="candlestick"
      width={chartWidth}
      height={chartHeight}
    />
  );
};

export default CandleStickChart;
