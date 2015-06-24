define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	var SEARCH = {
			
    	criteriaSearch: function() {
    		/**
    		var type = $(".screener-toggles .button.selected").attr("data-name");
    		if (type == "alpha-factors") SGX.screener.search.alphaSearch();
    		else if (type == "all-companies") SGX.screener.search.showAll();
    		else SGX.screener.search.fullSearch(true);
    		*/
    	},
    	
    	showAll: function(params) {
    		/**
    		if (typeof params === "undefined") params = { criteria: [] };
    		var endpoint = SGX.fqdn + "/sgx/search";
    		if (!$(".module-results thead th:first").hasClass("asc")) $(".module-results thead th:first").click();
    		SGX.screener.search.simpleSearch(endpoint, params);
    		*/
    	},
    	
    	nameSearch: function(val, params) {
    		/**
    		val = $.trim(val);
    		var msg = null;

    		SGX.screener.changeSearchToggle("all-companies");

    		// nothing typed if, show all
    		if (val === "undefined" || val.length == 0) {
    			SGX.screener.search.showAll();
    			return;
    		}
    		
    		// validate string input
			var msg = executeFunctionByName($(".searchtoggle .toggle.selected").attr("data-validator"), SGX, val);
			if (msg != null) {
				SGX.modal.open({ content: "<p>" + msg + "</p>", type: 'alert', maxWidth: 1000 });
				return;
			}
			
			// set the keyword search
    		if (typeof params === "undefined") params = { criteria: [] };
			
    		var endpoint = "/sgx/search";
    		var cName = "companyName";
    		if ($(".searchtoggle .selected").attr("data-name") == "code") cName = "tickerCode";
    		params.criteria.push({ field: cName, value: val });
    		
    		SGX.trackPage("SGX Keyword Search by " + $(".searchtoggle .selected").attr("data-name") + " - " + val);
    		
    		SGX.screener.search.simpleSearch(SGX.fqdn + endpoint, params, val);
    		*/
    	}
		
	};
	
	return SEARCH;
	
});