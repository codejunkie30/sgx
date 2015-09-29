define([ "wmsi/utils", "knockout", "client/modules/price-chart", "text!client/data/factors.json", "client/modules/tearsheet" ], function(UTIL, ko, PRICE_CHART, FACTORS, TS) {
	
	/**
	 * no op
	 */
	ko.bindingHandlers.createFactor = {};

	var CP = {
			
		letters: "ABCDEFGHIJSKLMNOPQRSTUVWXYZ",
		factors: ko.observable([]),
		premiumUser: ko.observable(),		
		premiumUserEmail: ko.observable(),		
		premiumUserAccntInfo: ko.observable(),
		
		initPage: function() {
			
			// extend tearsheet
			$.extend(true, this, TS);

			var self = this;

			this.init(function() { self.finish(self); });
			
			this.checkStatus();
		},
		
		finish: function(me) {

    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
			
			// track the view
			me.trackPage("SGX Company Profile - " + me.companyInfo.companyName);
			
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
			var postType = 'GET';
			var endpoint = me.fqdn + "/sgx/company/priceHistory";
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { me.initPriceChart(me, data); }, undefined, undefined);

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
			var postType = 'GET';
			var params = { tickerCode: parent.ticker, from: Highcharts.dateFormat("%Y-%m-%e", start), to: Highcharts.dateFormat("%Y-%m-%e", end) };
			
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) {
				
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
				
			}, undefined, undefined);
			
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
		checkStatus: function(){
//			$('body').idleTimeout({
//		      redirectUrl: 'sign-in.html',
//		      idleTimeLimit: 5,
//		      idleCheckHeartbeat: 1,
//		       customCallback:    function () {    // define optional custom js function
//		           alert('hi yo');
//				  // window.location.href= 'sign-in.html'
//		       },
//			  enableDialog: false,
//			  activityEvents: 'click keypress scroll wheel mousewheel mousemove',
//		      sessionKeepAliveTimer: false
//		    });
//			
			
			var endpoint = PAGE.fqdn + "/sgx/account/info";
			var postType = 'POST';
			var params = {};
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				jsonp,
				function(data, textStatus, jqXHR){
					if (data.reason == 'Full authentication is required to access this resource'){
						PAGE.premiumUser(false);
					} else {
						PAGE.premiumUser(true);
						PAGE.premiumUserAccntInfo = data;
						PAGE.premiumUserEmail(PAGE.premiumUserAccntInfo.email);
					}
					
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log(textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);			
		}
		
	};
	
	
	return CP;

	
});