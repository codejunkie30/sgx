define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	
	var TEARSHEET = {
			
		initPage: function() {

			var endpoint = this.fqdn + "/sgx/company";
			var params = { id: UTIL.getParameterByName("code") };
			var self = this; 
			
			var tmpF = function(data) {
				var parent = self;
				parent.initCompanyData(data);
			}

			UTIL.handleAjaxRequest(endpoint, params, tmpF, undefined);
			
    		return this;
		},
		
		initCompanyData: function(data) {
			
			$.extend(true, this, data);
			
			// too long a variable name
			this.companyInfo = this.company.companyInfo;
			
			// set holders (too long) than sort
			this.holders = this.hasOwnProperty("holders") && this.holders.hasOwnProperty("holders") ? this.holders.holders : [];
			this.holders.sort(function(a, b) { return b.shares - a.shares; });
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);

    		// resize
    		this.resizeIframeSimple();
			
		}

	};
	
	return TEARSHEET;
	
});