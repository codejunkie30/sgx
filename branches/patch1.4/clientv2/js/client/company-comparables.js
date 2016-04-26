define([ "wmsi/utils", "knockout", "client/modules/results", "client/modules/tearsheet" ], function(UTIL, ko, RESULTS, TS) {

	var CCOMP = {
		results: null,
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

		responseReceived: ko.observable(false),
		
		initPage: function() {
			// extend tearsheet
			$.extend(true, this, TS);
			
			// initialize results
			this.results = RESULTS.init(this);
			
			// initializes
			var self = this;
			this.init(function() { self.finish(self); });
			
			PAGE.checkStatus();
		},
		
		finish: function(me) {
			
    		ko.applyBindings(this, $("body")[0]);

				me.trackPage("SGX Company Comparables - " + me.companyInfo.companyName);

			// make the call
    		var endpoint = "/sgx/company/relatedCompanies";
    		var params = { id: me.ticker };
    		me.results.retrieve(endpoint, params, undefined, function() { return 0 }, 
    			function() { 
    				me.responseReceived(true);
    				var top = $('.profile').position().top;
    				PAGE.resizeIframeSimple(); 
    			});

		}

	};

	return CCOMP;
	
});