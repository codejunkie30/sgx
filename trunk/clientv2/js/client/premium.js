define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
		
	var PREMIUM = {
			
		initPage: function() {
			
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