define([ "wmsi/utils", "knockout", "text!client/data/financials.json" ], function(UTIL, ko, FINANCIALS) {
	
	
	var CF = {
			
		tearsheet: null,
		sections: null,
		dataPoints: ko.observable([]),
		currency: ko.observable(""),
		legendItems: ko.observable([]),
		hasChart: ko.observable(false),
		colors: [ '#565a5c', '#1e2171', '#BED600', '#0094B3', '#BF0052' ],
		
		init: function(tearsheet) {

			// set up some basics
			this.tearsheet = tearsheet;
			this.tearsheet.financialsTab = this;
			this.sections = JSON.parse(FINANCIALS).financials;
			
			var self = this;

    		var endpoint = tearsheet.fqdn + "/sgx/company/financials";
    		var params = { id: tearsheet.ticker };
    		UTIL.handleAjaxRequest(endpoint, params, function(data) { self.initFinancials(tearsheet, data);  });
			
		},
		
		initFinancials: function(tearsheet, data) {
			
    		var financials = data.financials.slice();
    		var currency = null;
    		
    		// let's make sure they're sorted
    		financials.sort(function(a, b) {
        		var a = parseInt(a.absPeriod.replace("FY", "").replace("LTM", ""));
        		var b = parseInt(b.absPeriod.replace("FY", "").replace("LTM", ""));
        		return a - b;
        	});          		
        	
        	if (financials.length == 5) return financials;

    		// we need to decide whether to use the latest year end
    		// or quarter data
    		var isQ4 = financials[financials.length - 1].absPeriod.indexOf("LTM4") != -1;
    		financials.splice(isQ4 ? financials.length - 1 : 0, 1);  
			
			this.dataPoints(financials);
			this.currency(financials[0].filingCurrency);
			
		},
		
		handleClick: function(model, data, event) {
			
			var el = $(event.currentTarget).closest(".checkbox");
			
			if ($(el).hasClass("checked")) $(el).removeClass("checked");
			else $(el).addClass("checked");
			
			model.financialsTab.renderChart();
			
		},
		
		renderChart: function() {
			
			
		}

	};
	
	
	return CF;

	
});