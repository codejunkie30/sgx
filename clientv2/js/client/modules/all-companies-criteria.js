define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {

	var CRITERIA = {
			
		screener: null,
			
		init: function(screener, finalize) {
			
			this.screener = screener;
			screener.criteria = this;
			
			finalize(undefined);
			
		},

		renderInputs: function(data) {
			// TODO
		},

    	reset: function(finished) {
        	// TODO
    	}
		
	};
	
	return CRITERIA;
	
});