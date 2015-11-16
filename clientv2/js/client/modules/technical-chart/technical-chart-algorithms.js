/*The MIT License (MIT) technical-indicators

Copyright (c) 2013 highslide-software

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/
/*INDICATORS licence uri https://github.com/blacklabel/indicators/blob/master/license.txt*/

define([ "jquery" ], function( $ ) {

  return {

      /* Function using the global EMA function.
       * 
       * @return : an array of EMA data.
      **/
      EMA: function (xData, yData, periods) {

        return EMA(xData, yData, periods);
      }, 

        /* Function using the global SMA function.
       * 
       * @return : an array of SMA data.
      **/
      SMA: function (xData, yData, periods) {

        return SMA(xData, yData, periods);
      },


      /* Function that uses the calcMACD function to return the MACD line.
       * 
       * @return : the first index of the calcMACD return, the MACD.
      **/
      MACD: function (xData, yData, periods) {

        return calcMACD(xData, yData, periods)[0];
      },


      /* Function that uses the calcMACD function to return the histogram line.
       * 
       * @return : the third index of the calcMACD return, the histogram.
      **/
      histogram: function (xData, yData, periods) {

        return calcMACD(xData, yData, periods)[2];
      },


      /* Function that uses the global calcMACD.
       * 
       * @return : the second index of the calcMACD return, the signalLine.
      **/
      signalLine: function (xData, yData, periods) {

        return calcMACD(xData, yData, periods)[1];
      },


      /* Function that uses the global calcMACD.
       * 
       * @return : the second index of the calcMACD return, the signalLine.
      **/
      RSI: function (xData, yData, periods) {

        return RSI(xData, yData, periods);
      },


  }

    /* Function based on the idea of an exponential moving average.
     * 
     * Formula: EMA = Price(t) * k + EMA(y) * (1 - k)
     * t = today, y = yesterday, N = number of days in EMA, k = 2/(2N+1)
     *
     * @param yData : array of y variables.
     * @param xData : array of x variables.
     * @param periods : The amount of "days" to average from.
     * @return an array containing the EMA. 
    **/
    function EMA (xData, yData, periods) {

      var t,
        y = false,
        n = periods,
        k = (2 / (n + 1)),
        ema,  // exponential moving average.
        emLine = [],
        periodArr = [],
        length = yData.length,
        pointStart = xData[0];

      // loop through data
      for (var i = 0; i < length; i++) {


        // Add the last point to the period arr, but only if its set.
        if (yData[i-1]) {
          periodArr.push(yData[i]);
        }
        

        // 0: runs if the periodArr has enough points.
        // 1: set currentvalue (today).
        // 2: set last value. either by past avg or yesterdays ema.
        // 3: calculate todays ema.
        if (n == periodArr.length) {


          t = yData[i];

          if (!y) {
            y = arrayAvg(periodArr);
          } else {
            ema = (t * k) + (y * (1 - k));
            y = ema;
          }

          emLine.push([xData[i] , y]);

          // remove first value in array.
          periodArr.splice(0,1);

        } else {

          emLine.push([xData[i] , null]);
        }

      }

      return emLine;
    }



    /* Function that calculates the MACD (Moving Average Convergance-Divergence).
     *
     * @param yData : array of y variables.
     * @param xData : array of x variables.
     * @param periods : The amount of "days" to average from.
     * @return : An array with 3 arrays. (0 : macd, 1 : signalline , 2 : histogram) 
    **/
    function calcMACD (xData, yData, periods) {

      var chart = this,
        shortPeriod = 12,
        longPeriod = 26,
        signalPeriod = 9,
        shortEMA,
        longEMA,
        MACD = [], 
        xMACD = [],
        yMACD = [],
        signalLine = [],
        histogram = [];


      // Calculating the short and long EMA used when calculating the MACD
      shortEMA = EMA(xData, yData, 12);
      longEMA = EMA(xData, yData, 26);

      // subtract each Y value from the EMA's and create the new dataset (MACD)
      for (var i = 0; i < shortEMA.length; i++) {

        if (longEMA[i][1] == null) {

          MACD.push( [xData[i] , null]);

        } else {
          var macdY = (shortEMA[i][1] - longEMA[i][1]);
          macdY = +macdY.toFixed(15);
          MACD.push( [ xData[i] , macdY ] );
        }
      }

      // Set the Y and X data of the MACD. This is used in calculating the signal line.
      for (var i = 0; i < MACD.length; i++) {
        xMACD.push(MACD[i][0]);
        var temp  = MACD[i][1];
        if(temp != null) {
          temp = +temp.toFixed(15);
        }
         yMACD.push(temp)

      }

      // Setting the signalline (Signal Line: X-day EMA of MACD line).

      signalLine = EMA(xMACD, yMACD, signalPeriod);

      // Setting the MACD Histogram. In comparison to the loop with pure MACD this loop uses MACD x value not xData.
      for (var i = 0; i < MACD.length; i++) {

        if (MACD[i][1] == null) {

          histogram.push( [ MACD[i][0], null ] );
        
        } else {
          var histY = (MACD[i][1] - signalLine[i][1]);
          histY = +histY.toFixed(15);

          histogram.push( [ MACD[i][0], histY ] );

        }
      }

      return [MACD, signalLine, histogram];
    }



    /* Function based on the idea of a simple moving average.
     * @param yData : array of y variables.
     * @param xData : array of x variables.
     * @param periods : The amount of "days" to average from.
     * @return an array containing the SMA. 
    **/
    function SMA (xData, yData, periods) {

      var periodArr = [],
        smLine = [],
        length = yData.length,
        pointStart = xData[0];

      // Loop through the entire array.
      for (var i = 0; i < length; i++) {

        // add points to the array.
        periodArr.push(yData[i]);

        // 1: Check if array is "filled" else create null point in line.
        // 2: Calculate average.
        // 3: Remove first value.
        if (periods == periodArr.length) {

          smLine.push([ xData[i] , arrayAvg(periodArr)]);
          periodArr.splice(0,1);

        }  else {
          smLine.push([ xData[i] , null]);
        }
      }
      return smLine;
    }



    /* Function that returns average of an array's values.
     *
    **/
    function arrayAvg (arr) {
      var sum = 0,
        arrLength = arr.length,
        i = arrLength;

      while (i--) {
        sum = sum + arr[i];
      }

      return (sum / arrLength);
    }


    /* Function from another indicators plugin
     * @param yData : array of arrays. each individual array consists of [high, low, open, close] (prices)
     * @param xData : array of x variables.
     * @param periods : The amount of "days" to average from.
     * @return an array containing the RSI. 
    **/
    function RSI (xVal, yVal, period) {
        var period = period || 14;
        var yValLen = yVal ? yVal.length : 0;

        var decimals = 4,
            range = 1,
            RSI = [],
            xData = [],
            yData = [],
            index = 3,
            gain = [],
            loss = [],
            RSIPoint, change, RS, avgGain, avgLoss;

       // atr requires close value     
       if((xVal.length <= period) || !$.isArray(yVal[0]) || yVal[0].length != 4) {
          return;
       }
       
       // accumulate first N-points
       while(range < period + 1){
          change = toFixed(yVal[range][index] - yVal[range - 1][index], decimals);
          gain.push(change > 0 ? change : 0);
          loss.push(change < 0 ? Math.abs(change) : 0);
          range ++;
       }
       
       for(i = range - 1; i < yValLen; i++ ){

           if( i > range - 1) {
                // remove first point from array
                gain.shift();
                loss.shift();
                // calculate new change
                change = toFixed(yVal[i][index] - yVal[i - 1][index], decimals);
                // add to array
                gain.push(change > 0 ? change : 0);
                loss.push(change < 0 ? Math.abs(change) : 0);
           }
           
           // calculate averages, RS, RSI values:
           avgGain = toFixed(sumArray(gain) / period, decimals);
           avgLoss = toFixed(sumArray(loss) / period, decimals);  
           
           if(avgLoss === 0) {
              RS = 100;
           } else {
              RS = toFixed(avgGain / avgLoss, decimals);
           }
           RSIPoint = toFixed(100 - (100 / (1 + RS)), decimals);
           RSI.push([xVal[i], RSIPoint]);
           xData.push(xVal[i]);
           yData.push(RSIPoint);  

       }
       
       // options.yAxisMax = 100;
       // options.yAxisMin = 0;
       
       // return {
       //   values: RSI,
       //   xData: xData,
       //   yData: yData
       // };
       return RSI;
    }

    function sumArray(array){
        var sum = 0;
        for(var i=0, len = array.length; i < len; i++) {
          sum += array[i];
        }
        return sum;
      }

    function toFixed(a, n) {
        return parseFloat(a.toFixed(n));  
    }


})