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
				PRICE_CHART.init("#price-volume", data, finished, update, me.ticker, CP.cpUserStatus);
			} else {
				parent.resizeIframeSimple();	
			}
			
			if(typeof(UTIL.retrieveTracking()) != "undefined" && UTILS.retrieveTracking().value == "true") {
				UTILS.saveTracking("false");
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
		
		savingTab: function(selectedTab) {
			
			if(typeof(UTIL.retrieveTracking()) != "undefined" && UTILS.retrieveTracking().value == "false") {
				if(typeof $(event.target)[0].href == "undefined") {
					var selectedTab = "overview";
					if((UTIL.retrieveCriteria() == null || UTIL.retrieveCriteria() == "undefined") || (UTIL.retrieveCriteria().value.companyProfile == null || UTIL.retrieveCriteria().value.companyProfile == "undefined")) {
		        	  	
		      			
		      			var companyProfile= {
		      				selectedTab:selectedTab
		      			}
		      			var criteria = {companyProfile: companyProfile};
		      			UTIL.saveCriteria(criteria);
		          }
		          else {
		        	  var criteria = UTIL.retrieveCriteria().value;
		    			
		    			
		    			var companyProfile= {
		    					selectedTab:selectedTab
		    				}
		    			criteria = {companyProfile: companyProfile};
		    			//criteria.push("companyFinancials",companyFinancials);
		    			UTIL.saveCriteria(criteria);
		          }
				}
				else {
					var arrTabs = ['overview','valuation','financials','dividends','ownership'];
					for(var i=0;i<arrTabs.length;i++){
						$('[name='+arrTabs[i]+']').attr('class','ui-state-default ui-corner-top').attr('aria-selected','false').attr('aria-expanded','false');
						$('[name='+arrTabs[i]+"Div"+']').attr('aria-hidden','true').attr('style','display: none;');
					}
					$('[name='+selectedTab+']').attr('class','ui-state-default ui-corner-top ui-state-hover ui-state-focus ui-tabs-active ui-state-active').attr('aria-selected','true').attr('aria-expanded','true');
					$('[name='+selectedTab+"Div"+']').attr('aria-hidden','false').attr('style','display: block;');
					
				if((UTIL.retrieveCriteria() == null || UTIL.retrieveCriteria() == "undefined") || (UTIL.retrieveCriteria().value.companyProfile == null || UTIL.retrieveCriteria().value.companyProfile == "undefined")) {
		        	  	
		      			
		      			var companyProfile= {
		      				selectedTab:selectedTab
		      			}
		      			var criteria = {companyProfile: companyProfile};
		      			UTIL.saveCriteria(criteria);
		          }
		          else {
		        	  var criteria = UTIL.retrieveCriteria().value;
		    			
		        	  var companyProfile = criteria.companyProfile;
		        	  if(companyProfile!=null) {
		        		  companyProfile.selectedTab = selectedTab
			    				
		        	  }
		        	  else {
		    			var companyProfile= {
		    					selectedTab:selectedTab
		    				}
		          	}
		    			criteria = {companyProfile: companyProfile};
		    			//criteria.push("companyFinancials",companyFinancials);
		    			UTIL.saveCriteria(criteria);
		          }
				}
			}
			else {
				if((UTIL.retrieveCriteria() != null || typeof UTIL.retrieveCriteria() != "undefined") || (UTIL.retrieveCriteria().value.companyProfile != null || typeof UTIL.retrieveCriteria().value.companyProfile != "undefined")) {
	        	  	
	      			
					var criteria = UTIL.retrieveCriteria().value;
					if(typeof criteria.companyProfile != "undefined" && criteria.companyProfile != null) {
						if(typeof criteria.companyProfile.selectedTab != "undefined" && criteria.companyProfile.selectedTab != null) {
							if(criteria.companyProfile.selectedTab == selectedTab) {
								$('[name='+selectedTab+']').attr('class','ui-state-default ui-corner-top ui-state-hover ui-state-focus ui-tabs-active ui-state-active').attr('aria-selected','true').attr('aria-expanded','true');
								$('[name='+selectedTab+"Div"+']').attr('aria-hidden','false').attr('style','display: block;');
							}
							else {
								$('[name='+selectedTab+']').attr('class','ui-state-default ui-corner-top').attr('aria-selected','false').attr('aria-expanded','false');
								$('[name='+selectedTab+"Div"+']').attr('aria-hidden','true').attr('style','display: none;');
							}
						}
					}
				}
	      			
			}
			return;
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
		//UTILS.saveTracking("false");
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