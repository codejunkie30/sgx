define([ "wmsi/utils", "knockout", "client/modules/price-chart"], function(UTIL, ko, PRICE_CHART) {
	
	var TEARSHEET = {
			
		ticker: UTIL.getParameterByName("code"),
		priceData: ko.observable({}),
		gotCompanyData: ko.observable(false),
		currencySymbol: ko.observable(),
		
		init: function(finished) {

			var self = this;
			// init profile data
			var endpoint = this.fqdn + "/sgx/company";
			var postType = 'POST';
			var params;
			if ( location.pathname.split("/")[1] == "print.html" ) { params = { id: this.ticker, type : "pdf" };}
			else {params = { id: this.ticker };}
			 
			UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { 
				if (data.errorCode == 4004) {
					var home = PAGE.getPage(PAGE.pageData.getPage('index'));
					top.location.href = home;
					return;	
				}
				var parent = self; parent.initCompanyData(data, finished); 
				
				}, undefined, undefined);
			
    		// init real-time/delayed pricing data
    		endpoint = this.fqdn + "/sgx/price";
    		params = { id: this.ticker };
    		UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { var parent = self; parent.initPriceData(parent, data); }, PAGE.customSGXError, undefined);
			
    		return this;
		},
		
		initCompanyData: function(data, finished) {
			
			$.extend(true, this, data);
			
			// too long a variable name
			this.companyInfo = this.company.companyInfo;
			
			this.companyInfo.volume = roundMe(this.companyInfo.volume, 3)
			
			// make keydevs observable
			this.keyDevs = ko.observable(this.keyDevs);
			
			// set holders (too long) than sort
			this.holders = this.hasOwnProperty("holders") && this.holders.hasOwnProperty("holders") ? this.holders.holders : [];
			
			if (this.holders != null) { this.holders.sort(function(a, b) { return b.shares - a.shares; }); }

			this.gotCompanyData(true);
			
    		if (typeof finished !== "undefined") finished();

		},
		
		initPriceData: function(parent, data) {
			
			if (!data.hasOwnProperty("price")) return;
			
    		var dateField = data.price.hasOwnProperty("lastTradeTimestamp") && data.price.lastTradeTimestamp != null ? data.price.lastTradeTimestamp : data.price.previousDate;
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
			
			var dataCurrency = pdata.currency.toLowerCase();
			var lastPriceCurrency = PAGE["numberFormats-"+dataCurrency].chart.format;
			
			TEARSHEET.currencySymbol(lastPriceCurrency);
			
		},
		
		industry: function() {
			return encodeURIComponent(this.companyInfo.industry);
		},
		
		industryGroup: function() {
			return encodeURIComponent(this.companyInfo.industryGroup);
		},
		
		printLink: function(id, extra) {
			var retCurr = UTILS.retrieveCurrency();
			var defCurr = 'sgd';			
			var finCurr;
			if (retCurr != false) {
				finCurr = retCurr ;
			} else {
				finCurr = defCurr ;
			};
			var currency = "";
			if(window.location.hostname == "sgx.fakemsi.com" || window.location.hostname == "localhost"){
				currency = '&currency=';
			}
			else
				currency = '%26currency=';
			var local = "https://" + window.location.hostname + "/print.html?code=" + encodeURIComponent(encodeURIComponent(id)) + (typeof extra === "undefined" ? "" : extra) + currency + finCurr;
			var url = PAGE.pqdn + encodeURIComponent(local);
			return url;
		}


	};
	
	return TEARSHEET;
	
});
function roundMe(val, precision) {
	  var roundingMultiplier = Math.pow(10, precision);
	  var valAsNum = isNaN(val)? 0 : parseFloat(+val);
	  var returnVal = Math.round( valAsNum*roundingMultiplier) / roundingMultiplier;

	  return returnVal;
	}