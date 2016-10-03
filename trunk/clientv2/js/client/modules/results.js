define([ "wmsi/utils", "knockout", "text!client/data/fields.json", "text!client/templates/customize-display.html", "knockout-repeat" ], function(UTIL, ko, fieldData, customizeHTML) {
	
	ko.bindingHandlers.formatResultColumn = {
	    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
	    	var models = ko.utils.unwrapObservable(valueAccessor());
	    	var prop = viewModel.hasOwnProperty("resultDisplay") ? viewModel.resultDisplay : models.prop;
	    	var val = RESULTS.formatField(viewModel, models.row, prop);
	    	if (models.prop == "companyName") val = RESULTS.companyLink(models.row);
	    	if (val == null || val == "") val = "-";
	    	$(element).html(val);
	    },
	    update: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
	    	var models = ko.utils.unwrapObservable(valueAccessor());
	    	var prop = viewModel.hasOwnProperty("resultDisplay") ? viewModel.resultDisplay : models.prop;
	    	var val = RESULTS.formatField(viewModel, models.row, prop);
	    	if (models.prop == "companyName") val = RESULTS.companyLink(models.row);
	    	if (val == null || val == "") val = "-";
	    	$(element).html(val);
	    }
	};
	
	var RESULTS = {
			
		parent: undefined,
		
		viewModel: null,
		
		init: function(parent) {
			
			this.parent = parent;
			this.viewModel = this.initModel(JSON.parse(fieldData).fieldGroups);
			return this;
		},
		
		retrieve: function(endpoint, params, keywords, scrollPos, callback) {

			var results = this;
			results.parent.showLoading();
			
			var postType = 'POST';
			var success = function(data) { 
				data.scrollPos = scrollPos;
				data.endpoint = endpoint;
				data.params = params;
				if (typeof keywords !== "undefined") data.keywords = keywords;
				if(data.size === 1){
					PAGE.validNavigation(true);
				}
				results.render(data); 
				results.parent.hideLoading(); 

        if (callback) {
          callback();
        }
			}
			
			$(".search-results").hide();
			
			UTIL.handleAjaxRequest(this.parent.fqdn + endpoint, postType, params, undefined, success, PAGE.customSGXError, undefined);
		},

    	fail: function(xhr, ajaxOptions, thrownErr) {
    		console.log(xhr);
    		console.log(ajaxOptions);
    		console.log(thrownErr);
    	},
    	
		render: function(data) {
      //go to company details page directly if only one result returns
      if(data.size == 1) {
        window.top.location = this.parent.pageData.getCompanyPage(encodeURIComponent(data.companies[0].tickerCode));
        return;
      }
			if (data.hasOwnProperty("keywords")) this.viewModel.keywords(data.keywords);
			else this.viewModel.keywords(null);

			this.viewModel.page(1);
			this.viewModel.search = true;
			this.viewModel.postType = 'GET';
			this.viewModel.params = data.params;
			this.viewModel.endpoint = data.endpoint;
			this.viewModel.companies(data.companies);
			this.viewModel.search = true;
			
			var scroll = typeof data.scrollPos === "undefined" ? this.viewModel.criteriaTop() : data.scrollPos();
			
			// handle the display
			var resultsBlock = $(".search-results")[0];
			if (typeof ko.dataFor(resultsBlock) === "undefined") ko.applyBindings(this, resultsBlock);
			$(resultsBlock).show();
			
			// remove sort options
			$(".search-results th.asc, .search-results th.desc").removeClass("asc").removeClass("desc");
			
			// default sort
			if (this.viewModel.keywords() == null) $(".search-results th.companyName").click();
			
			// resize
			//PAGE.resizeIframe(PAGE.getTrueContentHeight(), -1);

     	PAGE.resizeIframeSimple(); 
			PAGE.hideLoading();
		},
		
		formatField: function(field, row, id) {
			
			if (!row.hasOwnProperty(id) || row[id] == null) return "";
	    	var val = row[id];
    		if (field.hasOwnProperty("formatter")) {
    			
    			// handle list lookup
    			if (field.formatter.type == "round-list") {
    				val = val | 0;
    				val = field.formatter.values[val];
    			}
    			// needs a minimum value
    			if (field.formatter.hasOwnProperty("minField") && row.hasOwnProperty(field.formatter.minField)) {
    				if (row[field.formatter.minField] < field.formatter.minValue) val = "";
    			}
    			
    		}
			
			var fmt = field.format == "millions" ? "number1" : field.format;
			var zeroValue =  this.parent.formatZeroValue(fmt, val);
			if(zeroValue=='-')return zeroValue;
			val = this.parent.getFormatted(fmt, val);
    		
    		return val;
			
		},
		
		companyLink: function(row) {
			var url = this.parent.pageData.getCompanyPage(encodeURIComponent(row.tickerCode));
			return '<a target="_parent" href="' + url + '">' + row.companyName + '</a>' 
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
                        		if ($(".modal-container .checked").length >= 4) { alert("Please remove a column before adding a new one."); return; }
                        		
                        		// check it
                        		$(this).addClass("checked");	

                    		}
                    		
                    		var mdl = ko.dataFor($(this)[0]);
                    		mdl.dataDisplay = $(this).is(".checked");
                    		settings.viewModel.headersChanged(true);
                    		
                    	});
                    	
                    },
                    
                    confirm: function(settings) {
                    	RESULTS.parent.modal.close();
                    }
			};
			
			this.parent.modal.open(settings);
			
		},
		
		initModel: function(groups) {
			
			var mdl = {
				parent: this.parent,
				companies: ko.observable(null),
				fieldGroups: groups,
				staticFields: [ { "id":"companyName", "name":"Company Name", "format":"string" }, { "id":"tickerCode", "name":"Code", "format":"string" } ],
				resultSize: ko.observable(30),
				search: false,
				page: ko.observable(1).extend({ withPrevious: 1 }),
				headersChanged: ko.observable(false),
				keywords: ko.observable(null)
			};
			
			/**
			 * headers and columns
			 * first two remove industry
			 */
			mdl.staticFields.push.apply(mdl.staticFields, groups[groups.length-2].fields.slice());
			mdl.fieldGroups.remove(groups[groups.length-2]);
			mdl.headers = ko.computed(function() {
				this.headersChanged();
				this.headersChanged(false);
				var arr = this.staticFields.slice();
				$.each(this.fieldGroups, function(idx, group) {
					var defs = $.grep(group.fields, function(obj, idx) {
						
						if (!obj.hasOwnProperty("dataDisplay") && obj.hasOwnProperty("isHeader") && obj.isHeader && obj.id != 'exchange') obj.dataDisplay = true;
						return obj.hasOwnProperty("dataDisplay") && obj.dataDisplay;
					});
					if (defs.length > 0) arr.push.apply(arr, defs);
				});
				return arr;				
			}, mdl);
			
			/**
			 * actual results
			 */
			mdl.refinedCompanies = ko.computed(function() {
				if (this.companies() == null) return [];
				var companies = this.companies().slice();
				if (typeof mdl.sectors.val() === "undefined" || mdl.sectors.val() == null) return companies;
				return $.grep(companies, function(el, idx) { return el.hasOwnProperty("industry") && el.industry == mdl.sectors.val(); });
			}, mdl);
			
			mdl.currentCompanies = ko.computed(function() {
				if (this.refinedCompanies() == null) return [];
				var pg = this.page();
				var resultSize = this.resultSize();
				var startIdx = (pg-1)*resultSize;
				return this.refinedCompanies().slice(startIdx);
			}, mdl);

			
			/**
			 * paging
			 * 
			 */
			mdl.changePage = function(i) { 
        if(mdl.pages() == 0) 
          return; 
        mdl.page(+mdl.page() + i); 
      };
			
			mdl.page.subscribe(function(change) {
				if (isNaN(change) || change <= 0 || change > mdl.pages()) { 
					this.page(this.page.previous()); 
					$(".pager input").focus();
					return;
				}

        // prev values do get to this place
        // so conditional for resize should be here
        if (this.page.previous() <= 0 || this.page.previous() > mdl.pages()) {
          //skip iframe resize
        } else {
          PAGE.resizeIframe(PAGE.getTrueContentHeight(), this.resultsTop());
        }
				PAGE.hideLoading();
			} , mdl);
			
			mdl.pages = ko.computed(function() { 
				if (this.refinedCompanies() == null) return 0;
				if (this.resultSize().length > this.refinedCompanies().length) return 1;
				var ret = Math.ceil(this.refinedCompanies().length / this.resultSize());
				if (ret * this.resultSize() < this.refinedCompanies().length) ret++;
				return ret;
			}, mdl);
			
			/** 
			 * resizing
			 */
			mdl.resultsTop = function() { return $(".search-results").offset().top - 15; }
			mdl.criteriaTop = function() { return $(".screener-toggles").offset().top; }
			mdl.currentCompanies.subscribe(function(change) {
				if (this.search) {
					this.search = false;
					return;
				}
				setTimeout(function() { PAGE.resizeIframe(PAGE.getTrueContentHeight(), -1); }, 50);
				PAGE.hideLoading();
			}, mdl);
			
			/**
			 * sorting
			 */
			mdl.sort = function(field, results) {
				
				var curField = $("th." + field.id);
				var direction = $(curField).hasClass("asc") ? "desc" : "asc";
				var companies = mdl.companies();
				var prop = field.hasOwnProperty("resultDisplay") ? field.resultDisplay : field.id;
        
				$(".search-results th").removeClass("asc").removeClass("desc");


				$(curField).addClass(direction);
            	// sort
            	companies.sort(function(a, b) {
            		var a1 = "asc" == direction ? a[prop] : b[prop];
            		var b1 = "asc" == direction ? b[prop] : a[prop];
            		
            		if (field.format == "lookup") {
        				a1 = "asc" == direction ? results.formatField(field, a, field.id).toLowerCase() : results.formatField(field, b, field.id).toLowerCase();
        				b1 = "asc" == direction ? results.formatField(field, b, field.id).toLowerCase() : results.formatField(field, a, field.id).toLowerCase();
            		}
            		else if (field.format != "string") {
            			if (typeof b1 === "undefined" || b1 === null) b1 = -99999999999999;
            			if (typeof a1 === "undefined" || a1 === null) a1 = -99999999999999;
            			return b1-a1;
            		}
              /*
                Sort doesn't handle well when industry is null.
                '0' to ensure they end up at the begining of the sort. 
               */
                if (prop.toLowerCase() === 'industry'){
                  if (a1 === null) {
                    a1 = '0';
                  }
                  if (b1 === null) {
                    b1 = '0';
                  }
                }
              /*   ----   */
    					if (a1 != null) { return a1.localeCompare(b1); }
            	});
            	mdl.companies(companies);
            	mdl.page(1);
				
			};
			

        	/**
        	 * refining, whole thing a hack till I think of something better
        	 */
        	mdl.sectors = {

                values: ko.computed(function() {
                	if (this.refinedCompanies() == null) return;
            		var industries = [];
            		$.each(this.refinedCompanies(), function(idx, company) {
            			if (!company.hasOwnProperty("industry") || $.inArray(company.industry, industries) != -1 || company.industry == null) return;
            			industries.push(company.industry);
            		});
            		industries.sort(function(a, b) { 
            			if (a == null) a = ""; if (b == null) b = "";
            			return a.toLowerCase().localeCompare(b.toLowerCase()); 
            		});
            		return industries;
            	}, mdl),
            	
            	val: ko.observable(UTIL.getParameterByName('industry') == "" ? null : UTIL.getParameterByName('industry'))
        			
        	};
        	
        	mdl.sectors.val.subscribe(function(change) { mdl.page(1); });

			return mdl;    		
		}

    	
	};
	
	

	return RESULTS;
	
});