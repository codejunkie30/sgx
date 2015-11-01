define([ "wmsi/utils", "knockout", "client/modules/price-chart"], function(UTIL, ko, PRICE_CHART) {
	
	var TEARSHEET = {
			
		ticker: UTIL.getParameterByName("code"),
		priceData: ko.observable({}),
		gotCompanyData: ko.observable(false),
		
		init: function(finished) {

			var self = this;

			// init profile data
			var endpoint = this.fqdn + "/sgx/company";
			var postType = 'GET';
			var params = { id: this.ticker };
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { var parent = self; parent.initCompanyData(data, finished); }, undefined, undefined);
			
    		// init real-time/delayed pricing data
    		endpoint = this.fqdn + "/sgx/price";
    		params = { id: this.ticker };
    		UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { var parent = self; parent.initPriceData(parent, data); }, undefined, undefined);
			
    		return this;
		},
		
		initCompanyData: function(data, finished) {
			
			$.extend(true, this, data);			
			
			// too long a variable name
			this.companyInfo = this.company.companyInfo;

			// make keydevs observable
			this.keyDevs = ko.observable(this.keyDevs);
			
			// set holders (too long) than sort
			this.holders = this.hasOwnProperty("holders") && this.holders.hasOwnProperty("holders") ? this.holders.holders : [];
			this.holders.sort(function(a, b) { return b.shares - a.shares; });

			this.gotCompanyData(true);
			
    		if (typeof finished !== "undefined") finished();

		},
		
		initPriceData: function(parent, data) {
			
			if (!data.hasOwnProperty("price")) return;
			
    		var dateField = data.price.hasOwnProperty("lastTradeTimestamp") ? data.price.lastTradeTimestamp : data.price.previousDate;
    		var date = Date.fromISO(dateField);
    		var price = data.price.hasOwnProperty("lastPrice") && data.price.lastPrice != null ? data.price.lastPrice : data.price.closePrice;
    		
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
		
		industry: function() {
			return encodeURIComponent(this.companyInfo.industry);
		},
		
		industryGroup: function() {
			return encodeURIComponent(this.companyInfo.industryGroup);
		},
		
		printLink: function(id, extra) {
			var local = "http://" + window.location.hostname + "/print.html?code=" + id + (typeof extra === "undefined" ? "" : extra);
			var url = PAGE.pqdn + encodeURIComponent(local);
			return url;
		}


	};
	
	return TEARSHEET;
	
});