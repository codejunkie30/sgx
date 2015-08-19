define(["wmsi/utils", "knockout", "client/company-financials", "client/company-tearsheet", "client/modules/price-chart", "text!client/data/factors.json", "text!client/data/financials.json", "client/modules/tearsheet" ], function(UTIL, ko, coFIN, coTS, PRICE_CHART, FACTORS, FINANCIALS, TS) {	
	ko.bindingHandlers.createFactor = {};
	var PDF = {
		letters: "ABCDEFGHIJSKLMNOPQRSTUVWXYZ",
		factors: ko.observable([]),
		sections: null,
		dataPoints: ko.observable([]),
		currency: ko.observable(""),
		series: ko.observable([]),
		legendItems: ko.computed(function() {}),
		
		initPage: function() {
			
			// extend tearsheet
			$.extend(true, this, TS);
			
			// set up some basics
			this.sections = JSON.parse(FINANCIALS).financials;
			this.series([]);
			this.dataPoints([]);
			this.currency("");

			this.legendItems = ko.computed(function() {
				if (this.series().length == 0) return [];
				var chart = $('#bar-chart').highcharts(), ret = [];
				$.each(chart.series, function(idx, series) { ret.push(series.userOptions) });
				return ret;
			}, this);
			
			var self = this;

			this.init(function() { self.finish(self); });
			
		},
		
		finish: function(me) {

    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
			
			// alpha factors
			var tmp = JSON.parse(FACTORS);
			$.each(tmp.factors, function(idx, field) {
				var item = { "minLabel": "High", "maxLabel": "Low"  };
				$.extend(item, field);
				tmp.factors[idx] = item;
			});
			this.factors(tmp.factors);
			
			// init charts
			var params = { id: me.ticker };
			var tearendpoint = me.fqdn + "/sgx/company/priceHistory";
			UTIL.handleAjaxRequest(tearendpoint, params, function(data) { me.initPriceChart(me, data); }, undefined);
			
			var finendpoint = me.fqdn + "/sgx/company/financials";
    		var params = { id: me.ticker };
    		UTIL.handleAjaxRequest(finendpoint, params, function(data) { me.initFinancials(me, data);  });

			return this;
			
		},
		initPriceChart: function(parent, data) {
			var finished = function() {
				parent.resizeIframeSimple();
				var myFin = function() { parent.resizeIframeSimple(); };
				parent.initNews(parent, data, myFin); 
			};
			
			var update = function() { parent.initNews(parent, data);  };
		
			PRICE_CHART.init("#price-volume", data, finished, update);
			
		},
		
		initNews: function(parent, data, finishedDrawing) {
			
			var chart = $('#price-volume').highcharts();
			var start = new Date(chart.xAxis[0].min);
			var end = new Date(chart.xAxis[0].max);
			var div = $(".stock-events");
			
			var curStart = typeof $(div).attr("start-dt") === "undefined" ? new Date() : $(div).attr("start-dt");
			var curEnd = typeof $(div).attr("end-dt") === "undefined" ? new Date() : $(div).attr("end-dt");
			
			if (start == curStart && end == curEnd) return;
			
			$(div).attr("start-dt", start).attr("end-dt", end);
			
			var endpoint = parent.fqdn + "/sgx/search/keydevs";
			var params = { tickerCode: parent.ticker, from: Highcharts.dateFormat("%Y-%m-%e", start), to: Highcharts.dateFormat("%Y-%m-%e", end) };
			
			UTIL.handleAjaxRequest(endpoint, params, function(data) {
				
				// just make it an empty array
				if (!data.hasOwnProperty("keyDevs")) data.keyDevs = [];
				
				// sort it
				data.keyDevs.sort(function(a, b) { return Date.fromISO(b.date) - Date.fromISO(a.date); });
				
				// restrict to no more then 10
				data.keyDevs = data.keyDevs.slice(0, 10);
				
				// display in panel
				parent.keyDevs(data.keyDevs);
				
				// add to chart
				var seriesData = [];
				$.each(parent.keyDevs(), function(idx, keyDev) {
					var point = { x: Date.fromISO(keyDev.date), title: parent.getKeyDevLetter(idx), shape: 'url(img/stock-marker.png)', id: parent.getKeyDevID(idx) };
					seriesData.push(point);
				});
				seriesData.sort(function(a, b) { return a.x - b.x;  });
				chart.series[2].update({ data: seriesData });
	
				// everything is done
				if (typeof finishedDrawing !== "undefined") finishedDrawing();
				
			});
			
		},
		
		getKeyDevLetter: function(idx) {
			return this.letters.substring(idx, idx+1);
		},
		
		getKeyDevID: function(idx) {
			return "keyDev-" + this.getKeyDevLetter(idx);
		},
		handleFactor: function(tearsheet, elements, data) {
			
			// nothing to do
			if (typeof tearsheet.alphaFactors === "undefined" || tearsheet.alphaFactors == null) return;

			// id and matching value
			var id = $("[data-id]", elements[0]).attr("data-id");
			var factor = typeof id !== "undefined" && id != "" ? tearsheet.alphaFactors[id] : undefined;
			
			if (typeof factor === "undefined" || factor == 0) {
				$(elements[0]).hide();
				return;
			}

			// handle the click
			var pg = tearsheet.getPage(tearsheet.pageData.getPage("index"), "type=alpha-factors&factor=" + id + "&quintile=" + factor);
			$(".quintiles", elements[0]).addClass("per-" + (factor*20)).click(function() { window.top.location.href = pg; });
			
		},
		initFinancials: function(me, data) {
			
    		var financials = data.financials.slice();
    		var currency = null;
    		
    		// let's make sure they're sorted
    		financials.sort(function(a, b) {
        		var a = parseInt(a.absPeriod.replace("FY", "").replace("LTM", ""));
        		var b = parseInt(b.absPeriod.replace("FY", "").replace("LTM", ""));
        		return a - b;
        	});          		
        	
        	if (financials.length == 5) return financials;

    		// we need to decide whether to use the latest year end
    		// or quarter data
    		var isQ4 = financials[financials.length - 1].absPeriod.indexOf("LTM4") != -1;
    		financials.splice(isQ4 ? financials.length - 1 : 0, 1);  
			
			this.dataPoints(financials);
			this.currency(financials[0].filingCurrency);
			
			// initialize the chart without a series
			//this.initChart(me);
			
    		// resize
			me.resizeIframeSimple();
			
			var pathName = window.location.pathname;
			var finalPath = pathName.replace('/','').replace('.html','');
			
			if (finalPath == 'print') {
				$('.financials-section > table:gt(2)').wrapAll('<div class="right"></div>');
				$('.financials-section > table:lt(3)').wrapAll('<div class="left"></div>');
			}			
		},
		canUncheck: function(model, name) {
			var ret = false;
			$.each(model.series(), function(idx, series) { if ($(".trigger", series).data().name == name) { ret = true; } });
			return ret;
		}

	}
	
	return PDF;
	
});