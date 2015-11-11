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

		        if( !lowPrice[idx] || !openPrice[idx] || !highPrice[idx]){
		          return;
		        }
				self.chartData[key] = {}
				self.chartData[key].close = point.y;
				self.chartData[key].low = lowPrice[idx].y;
				self.chartData[key].open = openPrice[idx].y;
				self.chartData[key].high = highPrice[idx].y;
			});

			// all the volume data						
			this.volumeData = this.toHighCharts(data.volume);
			
			// set the zoom
			Highcharts.setOptions({ lang: { rangeSelectorZoom: "" }});
			
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
		    	
		    	// is a trading day
		    	ret += "<span class='chart-mouseover'>";
		    	ret += "<br />";
		    	ret += "<span>Open</span>: S$ " + point.open;
		    	ret += "<br />";
		    	ret += "<span>Close</span>: S$ " + point.close;
		    	ret += "<br />";
		    	ret += "<span>Low</span>: S$ " + point.low;
		    	ret += "<br />";
		    	ret += "<span>High</span>: S$ " + point.high;
		    	ret += "<br />";

		    	// no volume for this period
		    	if (this.points.length <= 1) return ret;

		    	// has volume too
		    	ret += "<span>Volume</span>: " + roundMe(this.points[1].y, 3) + " mm";
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
			 //Adds real time data to chart if user is premium and runs every minute
			if (CHART.userStatus == 'TRIAL'){
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
			var endpoint = PAGE.fqdn + "/sgx/price/pricingHistory";		
			var postType = 'GET';
			var params = { "id": CHART.currentTicker, "date": todaysDate };
			console.log(todaysDate);
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) {
				console.log(data);
				var todaysArray = [];
				//Runs data if it's there
				if ( data.pricingHistory.length > 0 ){
					$.each(data, function(key,data){
						$.each(data,function(i,data){											
							series.addPoint([data.currentDate, data.closePrice], true, true);
						});					
					});
				}
			}, undefined, undefined);	
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