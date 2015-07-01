define([ "wmsi/utils", "knockout", "text!client/data/fields.json", "knockout-repeat" ], function(UTIL, ko, fieldData) {
	
	var RESULTS = {
			
		screener: undefined,
		
		viewModel: null,
		
		init: function(screener) {
			
			this.screener = screener;
			this.viewModel = this.initModel(JSON.parse(fieldData).fieldGroups);

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
			
			$(".search-results").hide();
			
			UTIL.handleAjaxRequest(this.screener.fqdn + endpoint, params, success, this.fail);
			
		},

    	fail: function(xhr, ajaxOptions, thrownErr) {
    		console.log(xhr);
    		console.log(ajaxOptions);
    		console.log(thrownErr);
    	},
    	
		render: function(data) {

			this.viewModel.companies(data.companies);

			// handle the display
			var resultsBlock = $(".search-results")[0];
			if (typeof ko.dataFor(resultsBlock) === "undefined") ko.applyBindings(this, resultsBlock); 
			$(resultsBlock).show();
			
			// resize
			this.screener.resizeIframeSimple();

			
		},
		
		initModel: function(groups) {
			
			var mdl = {
			
				companies: ko.observable(null),
				fieldGroups: groups,
				staticFields: [ { "id":"companyName", "name":"Company Name", "format":"string" }, { "id":"tickerCode", "name":"Code", "format":"string" } ]
					
			};
			
			// handle changes
			mdl.changes = ko.computed(function() { return mdl.companies() != null ? mdl.companies().length : -1; });

			// add the industry in
			mdl.staticFields.push.apply(mdl.staticFields, groups[groups.length-1].fields.slice());
			mdl.fieldGroups.remove(groups[groups.length-1]);
			
			// headers
			mdl.headers = ko.computed(function() {
				var arr = this.staticFields.slice();
				$.each(this.fieldGroups, function(idx, group) {
					var defs = $.grep(group.fields, function(obj, idx) { return obj.hasOwnProperty("isDefault") && obj.isDefault; });
					if (defs.length > 0) arr.push.apply(arr, defs);
				});
				return arr;				
			}, mdl);
			
			return mdl;    		
		}

    	
	};
	
	

	return RESULTS;
	
});