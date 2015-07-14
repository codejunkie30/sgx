define([ "wmsi/utils", "knockout", "client/modules/price-chart" ], function(UTIL, ko, PRICE_CHART) {
	
	
	var TEARSHEET = {
			
		letters: "ABCDEFGHIJSKLMNOPQRSTUVWXYZ",
		
		ticker: UTIL.getParameterByName("code"),
			
		initPage: function() {

			var self = this; 

			// init profile data
			var endpoint = this.fqdn + "/sgx/company";
			var params = { id: this.ticker };
			UTIL.handleAjaxRequest(endpoint, params, function(data) { var parent = self; parent.initCompanyData(data); }, undefined);
			
    		// init charts
    		endpoint = this.fqdn + "/sgx/company/priceHistory";
    		UTIL.handleAjaxRequest(endpoint, params, function(data) { var parent = self; parent.initStockCharts(parent, data); }, undefined);
			
    		return this;
		},
		
		initCompanyData: function(data) {
			
			$.extend(true, this, data);
			
			// too long a variable name
			this.companyInfo = this.company.companyInfo;

			// make keydevs observable
			this.keyDevs = ko.observable(this.keyDevs);
			
			// set holders (too long) than sort
			this.holders = this.hasOwnProperty("holders") && this.holders.hasOwnProperty("holders") ? this.holders.holders : [];
			this.holders.sort(function(a, b) { return b.shares - a.shares; });
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);

    		// resize
    		this.resizeIframeSimple();
			
		},
		
		initStockCharts: function(parent, data) {
			
			var finished = function() {
				parent.resizeIframeSimple();
				var myFin = function() { parent.resizeIframeSimple(); };
				parent.initNews(parent, data, myFin); 
			};
			
			PRICE_CHART.init("#price-volume", data, finished, finished);
			
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
		
		keyDevClick: function(model, data, event) {
			
			var copy = "<h4>" + data.headline + "</h4>" + 
			   "<p class='bold'>" + 
			   "Source: " + data.source + "<br />" +
			   "Type: " + data.type + "<br />" +
			   "From: " + model.getFormatted("date", data.date) +  
			   "</p>" +
			   "<div class='news'>" + data.situation + "</div>";
			
			model.modal.open({ content: copy, type: 'alert' });
			
		}

	};
	
	return TEARSHEET;
	
});