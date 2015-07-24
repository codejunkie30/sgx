define([ "wmsi/utils", "knockout", "client/modules/results", "client/modules/tearsheet" ], function(UTIL, ko, RESULTS, TS) {

	var CCOMP = {
			
		results: null,
		
		initPage: function() {
			
			// extend tearsheet
			$.extend(true, this, TS);
			
			// initialize results
			this.results = RESULTS.init(this);
			
			// initializes
			var self = this;
			this.init(function() { self.finish(self); });
			
		},
		
		finish: function(me) {
			
    		ko.applyBindings(this, $("body")[0]);

			me.trackPage("SGX Company Comparables - " + me.companyInfo.companyName);

			// make the call
    		var endpoint = "/sgx/company/relatedCompanies";
    		var params = { id: me.ticker };
    		me.results.retrieve(endpoint, params, undefined, function() { return 0 });

		}

	};

	return CCOMP;
	
});