define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	
	var SIMPLE = {
			
		initPage: function() {
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);

    		// resize
    		this.resizeIframeSimple();
    		
    		return this;
		}		

	};
	
	return SIMPLE;
	
});