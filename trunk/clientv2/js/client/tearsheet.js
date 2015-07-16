define([ "wmsi/utils", "knockout", "client/modules/price-chart" ], function(UTIL, ko, PRICE_CHART) {
	
	ko.bindingHandlers.companyTabs = {
		init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			$(element).tabs({
	            active: 0,
	            load: function(event, ui) {
	            	ko.cleanNode(ui.panel[0]);
	            	var using = $(ui.tab[0]).attr("using");
	            	if (typeof using !== "undefined" && using != "") {
	            		viewModel.tabInit(using, viewModel, ui.panel[0]);
	            		return;
	            	}
	            	try { ko.applyBindings(viewModel, ui.panel[0]); } catch(err) {}
	            	PAGE.resizeIframeSimple();
	            },
	            beforeActivate: function(event, ui) {
	            	$.each(Highcharts.charts, function(idx, chart) { if (typeof chart !== "undefined") { chart.destroy(); } });
	            	ui.oldPanel.empty(); 
	            }
			});
		}
	};
	
	
	var TEARSHEET = {
			
		ticker: UTIL.getParameterByName("code"),
		
		profileTab: null,
		priceData: ko.observable({}),
		
		initPage: function() {

			var self = this; 

			// init profile data
			var endpoint = this.fqdn + "/sgx/company";
			var params = { id: this.ticker };
			UTIL.handleAjaxRequest(endpoint, params, function(data) { var parent = self; parent.initCompanyData(data); }, undefined);
			
			http://sgx-api-lb-195267723.ap-southeast-1.elb.amazonaws.com
			
    		// init real-time/delayed pricing data
    		endpoint = this.fqdn + "/sgx/price";
    		params = { id: this.ticker };
    		UTIL.handleAjaxRequest(endpoint, params, function(data) { var parent = self; parent.initPriceData(parent, data); }, undefined);
			
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
		
		initPriceData: function(parent, data) {
			
    		var dateField = data.price.hasOwnProperty("lastTradeTimestamp") ? data.price.lastTradeTimestamp : data.price.previousDate;
    		var date = Date.fromISO(dateField);
    		var price = data.price.hasOwnProperty("lastPrice") ? data.price.lastPrice : data.price.closePrice;
    		
    		if (typeof price === "undefined" || price == null) return;
    		
    		var pdata = { 
    			'price': price, 
    			'currency': data.price.tradingCurrency,  
    			'change': data.price.change,
    			'day': $.datepicker.formatDate( "dd/M/yy", date),
    			'time': date.getHours() + ":" + String("00" + date.getMinutes()).slice(-2) + ""
    		};
    		
    		parent.priceData(pdata);
			
		},
		
		tabInit: function(name, parent, element) {

			// initialize using custom module, please note that applyBindings 
			// will need to be called when initialization is done 
			require(["client/modules/tearsheet/" + name], function(tab) {
				parent.profileTab = tab.init(parent, element);
            	try { ko.applyBindings(parent, element); } catch(err) {}
            	PAGE.resizeIframeSimple();
			});

		},
		
		industry: function() {
			return encodeURIComponent(this.companyInfo.industry);
		},
		
		industryGroup: function() {
			return encodeURIComponent(this.companyInfo.industryGroup);
		}


	};
	
	return TEARSHEET;
	
});