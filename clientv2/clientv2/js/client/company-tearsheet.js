define([ "wmsi/utils", "knockout", "client/modules/price-chart", "text!client/data/factors.json", "client/modules/tearsheet", "client/base" ], function(UTIL, ko, PRICE_CHART, FACTORS, TS, BASE) {
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
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(false),
		currentDay: ko.observable(),
		
		cpUserStatus: ko.observable(),
		currentTicker: ko.observable(),

		ajaxInAction: ko.observableArray([]),
		
		
		initPage: function(me) {
			// extend tearsheet
			$.extend(true, this, TS);

			var self = this;
			PAGE.ajaxInAction.push('initPage');

			this.init(function() { self.finish(self, function(){ PAGE.ajaxInAction.remove('initPage')}); });
			
			PAGE.checkStatus();

			PAGE.ajaxInAction.push('checkStatus');

			
			var cpUserStatus;
			
			PAGE.userStatus.subscribe(function(data){
				CP.cpUserStatus = data;
				if (data) {
					PAGE.ajaxInAction.remove('checkStatus');
				}
			});
			
		},
		
		finish: function(me, cb) {
			
    		// finish other page loading
    	ko.applyBindings(this, $("body")[0]);
			
			if (cb && typeof cb === 'function') {
				cb();
			}

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
			
			CP.currentTicker(me.ticker);

			PAGE.ajaxInAction.push('priceHistoryCall');
			var cb = function() {
				PAGE.ajaxInAction.remove('priceHistoryCall');
			}
			
			// init charts
			var params = { id: me.ticker };
			var postType = 'POST';
			var endpoint = me.fqdn + "/sgx/company/priceHistory";
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) {  me.initPriceChart(me, data, me, cb);  }, PAGE.customSGXError, undefined);
			
			return this;
			
		},
		initPriceChart: function(parent, data, me, cb) {

			if (cb && typeof cb === 'function') {
				cb();
			}
			var finished = function() {
				parent.resizeIframeSimple();
				var myFin = function() { parent.resizeIframeSimple(); };
				parent.initNews(parent, data, myFin); 
			};
			var update = function() { parent.initNews(parent, data);  };
			//Pushes params to chart for use
			if (data.price.length > 0) {
				if(UTIL.retrieveCurrency().toLowerCase() === "sgd"){
					//To add the real time pricing to the chart
					endpoint = PAGE.fqdn + "/sgx/price";
					var params = { id: me.ticker };
		    		var postType = 'POST';
					UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(priceData) {
						if(priceData && priceData.hasOwnProperty("price") && priceData.price.tradingCurrency.toLowerCase() === "sgd"){
							priceData = priceData.price;
							//Adding the same data point twice to handle 1d option being active always
							for(i=0;i<2;i++){
								var date = priceData.hasOwnProperty("lastTradeTimestamp") && priceData.lastTradeTimestamp != null ? priceData.lastTradeTimestamp : priceData.previousDate;
					    		var price = priceData.hasOwnProperty("lastPrice") && priceData.lastPrice != null ? priceData.lastPrice : priceData.closePrice;
								
								if( priceData.closePrice)
									data.price.push({date: date, value: price});
								 if( priceData.lowPrice)
									 data.lowPrice.push({date: date, value: priceData.lowPrice});
								 if( priceData.openPrice)
									 data.openPrice.push({date: date, value: priceData.openPrice});
								 if( priceData.highPrice)
									 data.highPrice.push({date: date, value: priceData.highPrice});
								 var volume = priceData.volume;
								 if(volume){
									 volume = priceData.volume/1000000.0;	 
								 }
								 data.volume.push({date: date, value: volume});
							}
						}
						PRICE_CHART.init("#price-volume", data, finished, update, me.ticker, CP.cpUserStatus);
					}, PAGE.customSGXError, undefined);
				}else{
					PRICE_CHART.init("#price-volume", data, finished, update, me.ticker, CP.cpUserStatus);
				}
			} else {
				parent.resizeIframeSimple();	
			}
			
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

			PAGE.ajaxInAction.push('keydevs');
			var cb = function() {
				PAGE.ajaxInAction.remove('keydevs');
			}
			
			var endpoint = parent.fqdn + "/sgx/search/keydevs";
			var postType = 'POST';
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

				if (cb && typeof cb === 'function') {
					cb();
				}
				
			}, PAGE.customSGXError, undefined);
			
		},
		
		getKeyDevLetter: function(idx) {
			return this.letters.substring(idx, idx+1);
		},
		
		getKeyDevID: function(idx) {
			return "keyDev-" + this.getKeyDevLetter(idx);
		},
		
		keyDevClick: function(model, data, event) {
			var source;
			if (data.source != null){
				source = data.source
			} else {
				source = '-'
			}
			var copy = "<h4>" + data.headline + "</h4>" + 
			   "<p class='bold'>" + 
			   "Source: " + source + "<br />" +
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
			
		}
		
	};
	CP.ajaxOngoingCalls = ko.computed(function() {
		return CP.ajaxInAction().length > 0;
	}).extend({throttle:250});

	CP.ajaxOngoingCalls.subscribe(function(data) {
		if (data) {
			if (document.getElementById('loading')) return;
			BASE.showLoading();
		} else {
			BASE.hideLoading();
		}
	})
	
	return CP;

	
});