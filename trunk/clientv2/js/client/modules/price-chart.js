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
        returnObj[key] = roundMe(val, 3);
      });

      return returnObj;
    },

		initChart: function(element, data, finished, periodChange) {
			// Premium chart options vs non premium
			if (CHART.userStatus == 'PREMIUM'){
				var base = CHART_DEFAULTS_PREM;
			} else {
				var base = CHART_DEFAULTS;
			}
			
			var self = this;
			
			base.tooltip.formatter = function() {
		    	
		    	if (!this.hasOwnProperty("points")) return;
		    	
		    	var key = Highcharts.dateFormat("%e/%b/%Y", this.points[0].x);
		    	var point = self.cloneDataAndFormat( self.chartData[key] );

		    	
		    	var ret = "<b>" + Highcharts.dateFormat("%e/%b/%Y", this.points[0].x) + "</b>";

		    	// not a trading day
		    	if (point == undefined) {
		    		ret += "<br />";
		    		ret += "No trading data available.";
		    		return ret;
		    	}
		    	
				var openVal = Highcharts.numberFormat(point.open,3);
				if(parseFloat(openVal) == parseFloat("0.000"))
				{
					openVal = "-"
				}
				else
				{
					openVal = PAGE.currentFormats.chart.format + openVal.replace(/\.?0+$/,'')
				}
				
				var closeVal = Highcharts.numberFormat(point.close,3);
				if(parseFloat(closeVal) == parseFloat("0.000"))
				{
					closeVal="-";
				}
				else
				{
					closeVal=PAGE.currentFormats.chart.format + closeVal.replace(/\.?0+$/,'');
				}
				
				var lowVal = Highcharts.numberFormat(point.low,3);
				if(parseFloat(lowVal) == parseFloat("0.000"))
				{
					lowVal="-";
				}
				else
				{
					lowVal=PAGE.currentFormats.chart.format + lowVal.replace(/\.?0+$/,'');
				}
				
				var highVal = Highcharts.numberFormat(point.high,3);
				if(parseFloat(highVal) == parseFloat("0.000"))
				{
					highVal="-";
				}
				else
				{
					highVal=PAGE.currentFormats.chart.format + highVal.replace(/\.?0+$/,'');
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
					if (typeof periodChange !== "undefined") periodChange();
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
			if (CHART.userStatus == 'TRIAL' || CHART.userStatus == 'PREMIUM'){
				//Pushes to events
				base.chart.events = {				
					load: function () {
						// set up the updating of the chart each second
						var series = this.series[0];
						var firstRun = true;
						var today = new Date();
						var todaysDate = today.setHours(0,0,0,0);
						CHART.getPremData(todaysDate);
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
		getPremData: function(todaysDate){
			var endpoint = PAGE.fqdn + "/sgx/price";		
			var postType = 'POST';
			var params = { "id": CHART.currentTicker };
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined,function(data) {
			/*var price = '{"price":{"bidPrice":null,"askPrice":null,"highPrice":0.73,"lowPrice":0.72,"lastPrice":0.72,"lastTradeVolume":0.1433,"openPrice":0.73,"closePrice":0.72,"previousDate":1469125800000,"currentDate":1469125800000,"lastTradeTimestamp":1470182400000,"tradingCurrency":"SGD","volume":2219800.0,"percentChange":0.0,"change":0.0}}';
		    data = JSON.parse(price);*/
			var chart = $('#price-volume').highcharts();
			if (data.price){
				data = data.price;
				
				var x = Date.fromISO(data.lastTradeTimestamp).getTime()
				var key = Highcharts.dateFormat("%e/%b/%Y", new Date(x));
				chart.series[0].addPoint([x, data.lastPrice], true, true);
				CHART.chartData[key] = {}
				CHART.chartData[key].close = data.closePrice;
				 if( data.lowPrice)
					 CHART.chartData[key].low = data.lowPrice;
				 if( data.openPrice)
					 CHART.chartData[key].open = data.openPrice;
				 if( data.highPrice)
					 CHART.chartData[key].high = data.highPrice;
				 var volume = data.volume;
				 if(volume){
					 volume = data.volume/1000000.0;	 
				 }
				 chart.series[1].addPoint([x, volume], true, true);
				
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