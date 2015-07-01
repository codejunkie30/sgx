define([ "wmsi/utils", "knockout", "text!client/data/fields.json", "text!client/templates/customize-display.html", "knockout-repeat" ], function(UTIL, ko, fieldData, customizeHTML) {
	
	ko.bindingHandlers.formatResultColumn = {
	    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
	    	var models = ko.utils.unwrapObservable(valueAccessor());
	    	var val = RESULTS.formatField(viewModel, models.row, models.prop);
	    	$(element).text(val);
	    }
	};
	
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
		
		formatField: function(field, row, id) {
			
			if (!row.hasOwnProperty(id) || row[id] == null) return;
			
	    	var val = row[id];
	    	
    		if (field.hasOwnProperty("formatter")) {
    			
    			// handle list lookup
    			if (field.formatter.type == "round-list") {
    				val = val | 0;
    				val = field.formatter.values[val];
    			}

    			// needs a minimum value
    			if (field.formatter.hasOwnProperty("minField") && row.hasOwnProperty(field.formatter.minField)) {
    				if (row[field.formatter.minField] < field.formatter.minValue) val = null;
    			}
    			
    		}
    		
			var fmt = field.format == "millions" ? "number1" : field.format;
			val = this.screener.getFormatted(fmt, val);
    		
    		return val;
			
		},
		
		customizeDisplay: function() {
			
			var settings = {
	    			
	                maxWidth: 700,
	                
                    content: customizeHTML,
                    
                    viewModel: this.viewModel,
                    
                    type: 'prompt',
                    
                    postLoad: function(settings) {
                    	
                    	ko.applyBindings(settings.viewModel, $(".modal-container")[0]);
                    	
                    	$(".modal-container .checkbox").click(function(e) {
                    		
                    		// already checked
                    		if ($(this).is(".checked")) {
                    			$(this).removeClass("checked");
                    		}
                    		else {

                        		// can't check anymore
                        		if ($(".modal-container .checked").length >= 4) { alert("Please remove a column before adidng a new one."); return; }
                        		
                        		// check it
                        		$(this).addClass("checked");	

                    		}
                    		
                    		var mdl = ko.dataFor($(this)[0]);
                    		mdl.dataDisplay = $(this).is(".checked");
                    		settings.viewModel.headersChanged(true);                    		
                    		
                    	});
                    	
                    }
			};
			
			this.screener.modal.open(settings);
			
		},
		
		initModel: function(groups) {
			
			var mdl = {
				screener: this.screener,
				companies: ko.observable(null),
				fieldGroups: groups,
				staticFields: [ { "id":"companyName", "name":"Company Name", "format":"string" }, { "id":"tickerCode", "name":"Code", "format":"string" } ],
				resultSize: ko.observable(30),
				search: false,
				page: ko.observable(1).extend({ withPrevious: 1 }),
				headersChanged: ko.observable(false)
			};
			
			/**
			 * headers and columns
			 */
			mdl.staticFields.push.apply(mdl.staticFields, groups[groups.length-1].fields.slice());
			mdl.fieldGroups.remove(groups[groups.length-1]);
			mdl.headers = ko.computed(function() {
				this.headersChanged();
				this.headersChanged(false);
				var arr = this.staticFields.slice();
				$.each(this.fieldGroups, function(idx, group) {
					var defs = $.grep(group.fields, function(obj, idx) {
						if (!obj.hasOwnProperty("dataDisplay") && obj.hasOwnProperty("isDefault") && obj.isDefault) obj.dataDisplay = true;
						return obj.hasOwnProperty("dataDisplay") && obj.dataDisplay; 
					});
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
			mdl.changePage = function(i) { mdl.page(mdl.page() + i); }
			
			mdl.page.subscribe(function(change) {
				console.log(change);
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