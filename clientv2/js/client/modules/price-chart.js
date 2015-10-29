define([ "wmsi/utils", "knockout", "client/modules/price-chart-config", "highstock" ], function(UTIL, ko, CHART_DEFAULTS) {
	
	var CHART = {
		
		chartData: [],
		volumeData: [],
		priceData: [],
		
		init: function(element, data, finished, periodChange) {
			
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

		initChart: function(element, data, finished, periodChange) {
			
			var base = CHART_DEFAULTS;
			var self = this;
			
			base.tooltip.formatter = function() {
		    	
		    	if (!this.hasOwnProperty("points")) return;
		    	
		    	var key = Highcharts.dateFormat("%e/%b/%Y", this.points[0].x);
		    	var point = self.chartData[key];
		    	
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
		    	ret += "<span>Volume</span>: " + this.points[1].y.toFixed(3) + " mm";
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
			
			$(element).highcharts('StockChart', base, function() {
				if (typeof finished !== "undefined") finished();
			});
        
		}
	    
	};
	
	return CHART;
	
});
