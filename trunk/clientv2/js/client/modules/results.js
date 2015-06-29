define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	var RESULTS = {
			
		screener: undefined,
		
		init: function(screener) {
			this.screener = screener;
			return this;
		},
		
		retrieve: function(endpoint, params, keywords) {

			var results = this;
			results.screener.showLoading();
			
			var success = function(data) { 
				if (typeof keyword !== "undefined") data.keywords = keyword;
				results.render(data); 
				results.screener.hideLoading(); 
			}
			
			UTIL.handleAjaxRequest(this.screener.fqdn + endpoint, params, success, this.fail);
			
		},
		
		render: function(data) {
			console.log(data);
		},
		
    	fail: function(xhr, ajaxOptions, thrownErr) {
    		console.log(xhr);
    		console.log(ajaxOptions);
    		console.log(thrownErr);
    	},
		
	};
	
	return RESULTS;
	
});