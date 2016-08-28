define([ "wmsi/utils", "knockout", "client/modules/price-chart-config", "client/modules/price-chart-config-premium", "highstock" ], function(UTIL, ko, CHART_DEFAULTS, CHART_DEFAULTS_PREM) {
	
	var CHART = {
		
		chartData: [],
		volumeData: [],
		closePrice: [],
		priceData: [],
		priceHistoryData: [],
		currentTicker: ko.observable,
		userStatus: ko.observable(),
		
		init: function(element, data, finished, periodChange, ticker, cpUserStatus) {
			//Set's ticker			
			CHART.currentTicker = ticker;
			// Set's User Status
			CHART.userStatus = cpUserStatus;
			
			var self = this;
			// let's get all the price data set up
			
			this.priceData = this.toHighCharts(data.price);
			var lowPrice = this.toHighCharts(data.lowPrice);
			var openPrice = this.toHighCharts(data.openPrice);
			var highPrice = this.toHighCharts(data.highPrice);
						
			$.each(this.priceData, function(idx, point) {
				var key = Highcharts.dateFormat("%e/%b/%Y", new Date(point.x));

//		        if( !lowPrice[idx] || !openPrice[idx] || !highPrice[idx]){
//		          return;
//		        }
		        self.chartData[key] = {}
				self.chartData[key].close = point.y;
				 if( lowPrice[idx])
					 self.chartData[key].low = lowPrice[idx].y;
				 if( openPrice[idx])
					 self.chartData[key].open = openPrice[idx].y;
				 if( highPrice[idx])
					 self.chartData[key].high = highPrice[idx].y;
			});

			// all the volume data						
			this.volumeData = this.toHighCharts(data.volume);
			
			// set the zoom
			Highcharts.setOptions({ lang: { rangeSelectorZoom: "", thousandsSep: "," }});
			
			// initialize the chart
			this.initChart(element, data, finished, periodChange);
			
		},
		
	    toHighCharts: function(data) {
	    	var ret = [];
	    	$.each(data, function(idx, row) {
	    		ret.push({ x: Date.fromISO(row.date).getTime(), y: row.value });
	    	});
	    	ret.sort(function(a, b) { return a.x - b.x; });
	    	return ret;
	    },

    cloneDataAndFormat: function(obj) {
      var returnObj = {};
      $.each(obj, function(key, val) {
    	if(isNaN(val)){
    		returnObj[key] = val;
    	}else{
    		returnObj[key] = roundMe(val, 3);	
    	}
      });

      return returnObj;
    },

		initChart: function(element, data, finished, periodChange) {
			var base = CHART_DEFAULTS_PREM;
			
			var self = this;
			
			base.tooltip.formatter = function() {
		    	
		    	if (!this.hasOwnProperty("points")) return;
		    	
		    	var key = Highcharts.dateFormat("%e/%b/%Y", this.points[0].x);
		    	var data = self.chartData[key];
		    	
		    	if(data){
		    		var point = self.cloneDataAndFormat( self.chartData[key] );
		    	}
		    	
		    	var ret = "<b>" + Highcharts.dateFormat("%e/%b/%Y", this.points[0].x) + "</b>";

		    	// not a trading day
		    	if (!point) {
		    		ret += "<br />";
		    		ret += "No trading data available.";
		    		return ret;
		    	}
		    	
		    	var currencyFormat = PAGE.currentFormats.chart.format;
		    	if(point.currencyFormat){
		    		currencyFormat = point.currencyFormat;
		    	}
				var openVal = Highcharts.numberFormat(point.open,3);
				if(parseFloat(openVal) == parseFloat("0.000"))
				{
					openVal = "-"
				}
				else
				{
					openVal = currencyFormat + openVal.replace(/\.?0+$/,'')
				}
				
				var closeVal = Highcharts.numberFormat(point.close,3);
				if(parseFloat(closeVal) == parseFloat("0.000"))
				{
					closeVal="-";
				}
				else
				{
					closeVal=currencyFormat + closeVal.replace(/\.?0+$/,'');
				}
				
				var lowVal = Highcharts.numberFormat(point.low,3);
				if(parseFloat(lowVal) == parseFloat("0.000"))
				{
					lowVal="-";
				}
				else
				{
					lowVal=currencyFormat + lowVal.replace(/\.?0+$/,'');
				}
				
				var highVal = Highcharts.numberFormat(point.high,3);
				if(parseFloat(highVal) == parseFloat("0.000"))
				{
					highVal="-";
				}
				else
				{
					highVal=currencyFormat + highVal.replace(/\.?0+$/,'');
				}
				
		    	// is a trading day
		    	ret += "<span class='chart-mouseover'>";
		    	ret += "<br />";
		    	ret += "<span>Open</span>: " + openVal;
		    	ret += "<br />";
		    	ret += "<span>Close</span>: " + closeVal;
		    	ret += "<br />";
		    	ret += "<span>Low</span>: " + lowVal;
		    	ret += "<br />";
		    	ret += "<span>High</span>: " + highVal;
		    	ret += "<br />";

		    	// no volume for this period
		    	if (this.points.length <= 1) return ret;

		    	// has volume too
		    	var vol = roundMe(this.points[1].y, 3);
		    	if(parseFloat(vol) == parseFloat("0.000"))
		    	{
		    		vol = "-";
		    	}
		    	else
		    	{
		    		vol = vol+" mm";
		    	}
		    	ret += "<span>Volume</span>: " + vol;
		    	ret += "</span>";
		    	
		    	return ret;
		    };			
			
			base.xAxis.events = {
				afterSetExtremes: function(e) {
					if (e.rangeSelectorButton && typeof periodChange !== "undefined") periodChange();
		        }
			};
			
			base.series = [
                {
                	name: 'Price',
                	data: this.priceData,
                	type: 'area',
                	id: 'priceData',
                	threshold: null,
                	turboThreshold: 5000
                	
                },
                {
                	name: 'Volume',
                	data: this.volumeData,
                	type: 'column',
		        	yAxis: 1,
                	turboThreshold: 5000
                },
                {
                	type: 'flags',
                    style: { 
                    	color: 'black',
                    	cursor: 'pointer'
                    },
                    events: {
                    	click: function(e) {
                    		$("#" + e.point.id).click();
                    	}
                    },
                    onSeries: 'priceData',
                    shape: 'circlepin',
                    width: 16,
                    y: -26,
                    data: []
                }    
             ];
			 //Adds real time data to chart if user is Plus and runs every minute
			//Pushes to events
			base.chart.events = {				
				load: function () {
//					CHART.getPriceData();
					if (UTIL.retrieveCurrency().toLowerCase() === "sgd" && (CHART.userStatus == 'TRIAL' || CHART.userStatus == 'PREMIUM')){
						setInterval(function () {							
							var today = new Date();
							var todaysDate = today.setMinutes(today.getMinutes() - 1);
							CHART.getPremData(todaysDate);											
						}, 60000);
					}
				}
			}
			
			$(element).highcharts('StockChart', base, function() {
				if (typeof finished !== "undefined") finished();
			});
		},
		
		getPriceData: function(){
			endpoint = PAGE.fqdn + "/sgx/price";
    		params = { id: CHART.currentTicker };
    		var postType = 'POST';
    		UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) {

			var chart = $('#price-volume').highcharts();
			if (data.hasOwnProperty("price")){
				data = data.price;
				if(data.tradingCurrency.toLowerCase() === "sgd"){
					var dateField = data.hasOwnProperty("lastTradeTimestamp") && data.lastTradeTimestamp != null ? data.lastTradeTimestamp : data.previousDate;
		    		var price = data.hasOwnProperty("lastPrice") && data.lastPrice != null ? data.lastPrice : data.closePrice;
					var x = Date.fromISO(dateField).getTime();
					var key = Highcharts.dateFormat("%e/%b/%Y", new Date(x));
					chart.series[0].addPoint([x, price], false, false);
					CHART.chartData[key] = {}
					 if( data.closePrice)
						 CHART.chartData[key].close = data.closePrice;
					 if( data.lowPrice)
						 CHART.chartData[key].low = data.lowPrice;
					 if( data.openPrice)
						 CHART.chartData[key].open = data.openPrice;
					 if( data.highPrice)
						 CHART.chartData[key].high = data.highPrice;
					 if(data.tradingCurrency){
						 CHART.chartData[key].currencyFormat = PAGE["numberFormats-"+ data.tradingCurrency.toLowerCase()].chart.format;	 
					 }
					 var volume = data.volume;
					 if(volume){
						 volume = data.volume/1000000.0;	 
					 }
					 chart.series[1].addPoint([x, volume], true, false);
					 chart.redraw();
				}
			}
    		} , PAGE.customSGXError, undefined);	
		},
		getPremData: function(todaysDate){
			var endpoint = PAGE.fqdn + "/sgx/price/pricingHistory";		
			var postType = 'POST';
			var params = { "id": CHART.currentTicker, "date": todaysDate };
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined,function(data) {
			/*var myprices='{"pricingHistory":[{"bidPrice":0.0,"askPrice":0.0,"highPrice":7.55,"lowPrice":7.5,"lastPrice":7.51,"lastTradeVolume":100.0,"openPrice":7.5,"closePrice":7.53,"previousDate":1470096000000,"currentDate":1470182400000,"lastTradeTimestamp":1470199777883,"tradingCurrency":"SGD","volume":648600.0,"change":-0.02,"percentChange":-0.2656}]}';
			data = JSON.parse(myprices)*/

			var chart = $('#price-volume').highcharts();
			if (data.pricingHistory && data.pricingHistory.length > 0 ){
				$.each(data, function(key,data){
					$.each(data,function(i,data){
						var x = Date.fromISO(data.lastTradeTimestamp).getTime()
						var key = Highcharts.dateFormat("%e/%b/%Y", new Date(x));
						chart.series[0].addPoint([x, data.lastPrice], false, false);
						CHART.chartData[key] = {}
						 if( data.closePrice)
							 CHART.chartData[key].close = data.closePrice;
						 if( data.lowPrice)
							 CHART.chartData[key].low = data.lowPrice;
						 if( data.openPrice)
							 CHART.chartData[key].open = data.openPrice;
						 if( data.highPrice)
							 CHART.chartData[key].high = data.highPrice;
						 if(data.tradingCurrency){
							 CHART.chartData[key].currencyFormat = PAGE["numberFormats-"+ data.tradingCurrency.toLowerCase()].chart.format;	 
						 }
						 var volume = data.volume;
						 if(volume){
							 volume = data.volume/1000000.0;	 
						 }
						 chart.series[1].addPoint([x, volume], false, false);
					});					
				});
				chart.redraw();
			}
		} , PAGE.customSGXError, undefined);	
		}
	    
	};
	
	return CHART;
	
});


function roundMe(val, precision) {
  var roundingMultiplier = Math.pow(10, precision);
  var valAsNum = isNaN(val)? 0 : parseFloat(+val);
  var returnVal = Math.round( valAsNum*roundingMultiplier) / roundingMultiplier;

  return returnVal;
}