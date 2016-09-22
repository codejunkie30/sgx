define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {

	var CRITERIA = {
			
		screener: null,
			
		init: function(screener, finalize) {
			
			this.screener = screener;
			screener.criteria = this;			
			
			finalize(undefined);
			
		},
		
		renderInputs: function(data) {

			// keyword value
			var val = $.trim($(".searchbar input").val());
			
    		// validate string input
			var msg = val != null && val != "" ? UTIL.executeFunctionByName($(".searchtoggle .toggle.selected").attr("data-validator"), this, val) : null;
			if (msg != null) {
				this.screener.modal.open({ content: "<p>" + msg + "</p>", type: 'alert', maxWidth: 1000 });
				val = null;
			}
			
			// set up the search params
			var params = typeof data !== "undefined" && data.hasOwnProperty("params") ? data.params : undefined;
    		if (typeof params === "undefined") params = { criteria: [] };

    		if (val != null && val != "") {
        		var cName = "companyName";
        		if ($(".searchtoggle .selected").attr("data-name") == "code") cName = "tickerCode";
        		params.criteria.push({ field: cName, value: val });
        		this.screener.trackPage("SGX Keyword Search by " + $(".searchtoggle .selected").attr("data-name") + " - " + val);
    		}
    		
    		// reset the val and the toggle
			$(".searchtoggle .toggle:first").click();
    		$(".searchbar input").val("");
    		
    		// search
    		var endpoint = "/sgx/search";
    		this.screener.results.retrieve(endpoint, params, val, function() { 
				viewAllLength = params.criteria.length;
				setTimeout(function(){
					if (viewAllLength == 0) $(".search-results th.companyName").click();
					PAGE.resizeIframeSimple();
	    		}, 500);
			 });
			
		},
		
    	reset: function(finished) {
        	
    		setTimeout(function(){
    			if (CRITERIA.screener.results.viewModel.keywords() == null) $(".search-results th.companyName").click(); 
    		}, 1000);
    		
    	},
    	
		isValidTicker: function(str) {
			if (/^\w+$/.test(str) && str.indexOf("_") == -1) return null;
			return "Only letters and numbers allowed in stock code search.";
		},
		
		isValidName: function(str) {
			return null;
		}
		
	};
	
	return CRITERIA;
	
});