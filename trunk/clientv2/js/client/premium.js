define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
		
	var PREMIUM = {
			
		initPage: function() {
			
			PAGE.trackPage("SGX Premium");
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);

    		// resize
    		this.resizeIframeSimple();
    		
    		return this;
		},		
		cancel: function () {
        	history.back();
  		}
	};
	
	return PREMIUM;
	
});