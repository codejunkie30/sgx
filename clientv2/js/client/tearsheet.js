define([ "wmsi/utils", "knockout", "client/modules/price-chart" ], function(UTIL, ko, PRICE_CHART) {
	
	
	var TEARSHEET = {
			
		letters: "ABCDEFGHIJSKLMNOPQRSTUVWXYZ",
			
		initPage: function() {

			var self = this; 

			// init profile data
			var endpoint = this.fqdn + "/sgx/company";
			var params = { id: UTIL.getParameterByName("code") };
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
			this.keyDevs = ko.observableArray(this.keyDevs);
			
			// set holders (too long) than sort
			this.holders = this.hasOwnProperty("holders") && this.holders.hasOwnProperty("holders") ? this.holders.holders : [];
			this.holders.sort(function(a, b) { return b.shares - a.shares; });
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);

    		// resize
    		this.resizeIframeSimple();
			
		},
		
		initStockCharts: function(parent, data) {
			
			PRICE_CHART.init(data, function() { parent.resizeIframeSimple(); });
			
		},
		
		getLetter: function(idx) {
			return this.letters.substring(idx, idx+1);
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