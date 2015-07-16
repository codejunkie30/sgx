define([ "wmsi/utils", "knockout", "client/modules/price-chart", "text!client/data/factors.json" ], function(UTIL, ko, PRICE_CHART, FACTORS) {
	
	/**
	 * no op
	 */
	ko.bindingHandlers.createFactor = {};
	
	var CP = {
			
		tearsheet: null,
		
		letters: "ABCDEFGHIJSKLMNOPQRSTUVWXYZ",
		
		init: function(tearsheet) {

			// set up some basics
			this.tearsheet = tearsheet;
			var self = this;

			// alpha factors
			var tmp = JSON.parse(FACTORS);
			$.each(tmp.factors, function(idx, field) {
				var item = { "minLabel": "High", "maxLabel": "Low"  };
				$.extend(item, field);
				tmp.factors[idx] = item;
			});
			$.extend(true, this, tmp);
			
			// init charts
			var params = { id: tearsheet.ticker };
			var endpoint = tearsheet.fqdn + "/sgx/company/priceHistory";
			UTIL.handleAjaxRequest(endpoint, params, function(data) { self.initPriceChart(self, data); }, undefined);

			return this;
			
		},

		initPriceChart: function(parent, data) {
			
			var finished = function() {
				parent.tearsheet.resizeIframeSimple();
				var myFin = function() { parent.tearsheet.resizeIframeSimple(); };
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
			
			var endpoint = parent.tearsheet.fqdn + "/sgx/search/keydevs";
			var params = { tickerCode: parent.tearsheet.ticker, from: Highcharts.dateFormat("%Y-%m-%e", start), to: Highcharts.dateFormat("%Y-%m-%e", end) };
			
			UTIL.handleAjaxRequest(endpoint, params, function(data) {
				
				// just make it an empty array
				if (!data.hasOwnProperty("keyDevs")) data.keyDevs = [];
				
				// sort it
				data.keyDevs.sort(function(a, b) { return Date.fromISO(b.date) - Date.fromISO(a.date); });
				
				// restrict to no more then 10
				data.keyDevs = data.keyDevs.slice(0, 10);
				
				// display in panel
				parent.tearsheet.keyDevs(data.keyDevs);
				
				// add to chart
				var seriesData = [];
				$.each(parent.tearsheet.keyDevs(), function(idx, keyDev) {
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
			
		},
		
		hasGTIs: function(model) {
			var ret = false;
			try { ret = model.gtis.gtis.length > 0; } catch(err) {}
			return ret;
		},
		
		handleFactor: function(tearsheet, elements, data) {

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
			
			
			
		}
		
	};
	
	
	return CP;

	
});