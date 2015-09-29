define([ "wmsi/utils", "knockout", "client/modules/results", "client/modules/tearsheet" ], function(UTIL, ko, RESULTS, TS) {

	var CCOMP = {
		results: null,		
		premiumUser: ko.observable(),	
		premiumUserEmail: ko.observable(),		
		premiumUserAccntInfo: ko.observable(),
		
		initPage: function() {
			
			// extend tearsheet
			$.extend(true, this, TS);
			
			// initialize results
			this.results = RESULTS.init(this);
			
			// initializes
			var self = this;
			this.init(function() { self.finish(self); });
			
			this.checkStatus();
		},
		
		finish: function(me) {
			
    		ko.applyBindings(this, $("body")[0]);

			me.trackPage("SGX Company Comparables - " + me.companyInfo.companyName);

			// make the call
    		var endpoint = "/sgx/company/relatedCompanies";
    		var params = { id: me.ticker };
    		me.results.retrieve(endpoint, params, undefined, function() { return 0 });

		},
		checkStatus: function(){
			
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
						PAGE.timedLogout();
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

	return CCOMP;
	
});