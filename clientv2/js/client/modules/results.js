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
			this.viewModel.search = true;
			
			var scroll = this.viewModel.criteriaTop();

			// handle the display
			var resultsBlock = $(".search-results")[0];
			if (typeof ko.dataFor(resultsBlock) === "undefined") {
				scroll = 0;
				ko.applyBindings(this, resultsBlock); 
			}
			$(resultsBlock).show();
			
			// resize
			PAGE.resizeIframe(PAGE.getTrueContentHeight(), scroll);
			
		},
		
		initModel: function(groups) {
			
			var mdl = {
				companies: ko.observable(null),
				fieldGroups: groups,
				staticFields: [ { "id":"companyName", "name":"Company Name", "format":"string" }, { "id":"tickerCode", "name":"Code", "format":"string" } ],
				resultSize: ko.observable(30),
				search: false,
				page: ko.observable(1).extend({ withPrevious: 1 })
			};
			
			/**
			 * headers and columns
			 */
			mdl.staticFields.push.apply(mdl.staticFields, groups[groups.length-1].fields.slice());
			mdl.fieldGroups.remove(groups[groups.length-1]);
			mdl.headers = ko.computed(function() {
				var arr = this.staticFields.slice();
				$.each(this.fieldGroups, function(idx, group) {
					var defs = $.grep(group.fields, function(obj, idx) { return obj.hasOwnProperty("isDefault") && obj.isDefault; });
					if (defs.length > 0) arr.push.apply(arr, defs);
				});
				return arr;				
			}, mdl);
			
			/**
			 * actual results
			 */
			mdl.currentCompanies = ko.computed(function() {
				if (this.companies() == null) return;
				var pg = this.page();
				var resultSize = this.resultSize();
				var startIdx = (pg-1)*resultSize;
				return this.companies().slice(startIdx);
			}, mdl);

			/**
			 * paging
			 * 
			 */
			mdl.page.subscribe(function(change) { 
				console.log(">>" + change); 
				if (isNaN(change) || change <= 0 || change > mdl.pages()) { 
					this.page(this.page.previous()); 
					$(".pager input").focus();
					return;
				} 
				setTimeout(function() { PAGE.resizeIframe(PAGE.getTrueContentHeight(), mdl.resultsTop()); }, 50);
			} , mdl);
			
			mdl.pages = ko.computed(function() { 
				if (this.companies() == null) return 0;
				if (this.resultSize().length > this.companies().length) return 1;
				var ret = Math.ceil(this.companies().length / this.resultSize());
				if (ret * this.resultSize() < this.companies().length) ret++;
				return ret;
			}, mdl);
			
			/** 
			 * resizing
			 */
			mdl.pageHeight = ko.computed(function() { this.currentCompanies(); this.page(); this.resultSize(); return $(".search-results tbody tr").length; }, mdl);
			mdl.resultsTop = function() { return $(".search-results").offset().top - 15; }
			mdl.criteriaTop = function() { return $(".screener-toggles").offset().top; }
			mdl.pageHeight.subscribe(function(change) { 
				var mdl = this; 
				var top = mdl.resultsTop();
				if (this.search) {
					this.search = false;
					top = mdl.criteriaTop();
				}
				setTimeout(function() { PAGE.resizeIframe(PAGE.getTrueContentHeight(), top); }, 50);
			}, mdl);			

			return mdl;    		
		}

    	
	};
	
	

	return RESULTS;
	
});