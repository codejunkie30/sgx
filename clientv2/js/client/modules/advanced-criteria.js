define([ "wmsi/utils", "knockout", "text!client/data/fields.json", "text!client/templates/criteria-number.html", "text!client/templates/criteria-select.html", "text!client/templates/criteria-change.html", "jquery-ui" ], function(UTIL, ko, fieldData, nTemplate, sTemplate, cTemplate) {

	/**
	 * allows us to customize particular criteria
	 * assumes working with criteria object for model
	 */
	ko.bindingHandlers.configureAdditionalCriteria = {
	    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
	    	var template = ko.utils.unwrapObservable(valueAccessor());
	        var el = $("<div />").appendTo("body");
	    	ko.renderTemplate(
	    		template, 
	    		viewModel,
	    		{
	    			afterRender: function(nodes) {
	    				var settings = viewModel.criteria.additionalConfiguration[template];
	    				settings.content = $(el);
	    				settings.model = viewModel;
	    				var container = viewModel.criteria.screener.modal.open(settings);
	    			}
	    		},
	    		el[0],
	    		"replaceChildren"
	    	);
	    }
	};
	
	var CRITERIA = {
			
		screener: null,
		numberTemplate: null,
		selectTemplate: null,
		changeTemplate: null,
		textDistributions: [ "industryGroup" ],
			
		init: function(screener, finalize) {
			
    		// reset the val and the toggle
			$(".searchtoggle .toggle:first").click();
    		$(".searchbar input").val("");
			
			// parse in JSON field configuration
			$.extend(true, this, JSON.parse(fieldData));
			
			this.numberTemplate = nTemplate;
			this.selectTemplate = sTemplate;
			this.changeTemplate = cTemplate;
			this.screener = screener;
			screener.criteria = this;
			
			// clear the drawn the inputs
			$(".search-criteria tbody").children().remove();

			// only apply bindings first time
			if ($(".search-options[data-section='advanced-screener'] .criteria-select[data-init='true']").length == 0) {
				ko.applyBindings(screener, $(".search-options[data-section='advanced-screener'] .criteria-select")[0]);
				$(".search-options[data-section='advanced-screener'] .criteria-select").attr("data-init", "true");
				this.getDistributions(this.getSelectedFields(), finalize);
				return;
			}
			
			this.reset(finalize);
			
		},

    	reset: function(finished) {
        	
        	$(".search-criteria tbody").children().remove();
        	
        	/** handle sorting
        	var sorter = $(".module-results thead th.companyName");
        	if (!$(sorter).hasClass("sort") && !$(sorter).hasClass("asc")) $(sorter).click();
        	*/
        	
        	$(".criteria-select .checkbox").each(function(idx, el) { CRITERIA.clickEvents.uncheckCriteriaItem(el); });
        	$(".criteria-select [data-default='true']").each(function(idx, el) { CRITERIA.clickEvents.checkCriteriaItem(el); });
        	this.getDistributions(this.getSelectedFields(), this.screener.finalize);
        	
    	},
    	
    	getDistributions: function(data, finished) {

    		var tmpF = finished;
    		var endpoint = this.screener.fqdn + "/sgx/search/distributions";
    		var processFields = [], ignoreFields = []; 

    		// some fields require additional steps, remove these from this service
    		$.each(data.fields, function(idx, name) {
    			
    			var field = CRITERIA.getFieldById(name);
    			
    			// we can process it in the service
    			if (!field.hasOwnProperty("customDistribution") || !field.customDistribution) {
    				processFields.push(name);
    				return;
    			}
    			
    			// we need to do something else
    			ignoreFields.push({ "field": name, "buckets": [], "template": field.template  });
    			
    		});
    		
    		// reset the field list
    		data.fields = processFields;
    		
    		// change the function
    		if (ignoreFields.length > 0) {
    			tmpF = function(data) { 
    				data.distributions = data.distributions.concat(ignoreFields);
    				if (!data.hasOwnProperty("fieldValues")) data.fieldValues = [];
    				finished(data);
    			};
    		}
    		
    		// do something else
    		if (data.fields.length == 0) { 
    			data = { distributions: [] };
    			tmpF(data);
    			return;
    		}
    		
        	UTIL.handleAjaxRequest(endpoint, data, tmpF, undefined);

    	},
    	
        handleDistributions: function(data) {
        	
        	var textFields = [];
        	
        	// text ranges
        	$.each(data.distributions, function(idx, dist) {
        		
        		// let's get the fielded data as is (no need to calculate text)
        		var name = dist.field;
        		if ($.grep(data.fieldValues, function(val) { return val.field == name; }).length > 0) return;
        		var fieldData = CRITERIA.getFieldById(name);
        		fieldData.buckets = dist.buckets;
        		textFields.push(fieldData);
        		
        		// hack for industry groups
            	if (name == "industryGroup") fieldData.buckets.push({ "data-name": "industry", count: 0, key: "Real Estate Investment Trusts (REITs)" });
            	
            	// now sort the values
            	fieldData.buckets.sort(function(a,b) { return a.key.localeCompare(b.key); });
        		
        	});

        	// numeric ranges
        	$.each(data.fieldValues, function(idx, field) {
        		
        		var fieldData = CRITERIA.getFieldById(field.field);
        		
        		// force sort asc
        		field.values.sort(function(a, b) { return a - b; });
        		
        		// get the type and count
        		var type = fieldData.hasOwnProperty("distribution-type") ? fieldData["distribution-type"] : "normal";
        		var bCount = fieldData.hasOwnProperty("distribution-buckets") ? fieldData["distribution-buckets"] : 75;
        		
        		// get the random distributions
        		var buckets = {};
        		$.each(field.values, function(vIdx, val) { CRITERIA.randomizeBucket(buckets, val, type, bCount); });
        		
        		// build the collection
        		var arr = [], start = 0;
            	for (var prop in buckets) {
            		if (!buckets.hasOwnProperty(prop)) continue;
            		var bucket = {};
            		bucket.count = buckets[prop].length;
            		bucket.from = field.values[start];
            		bucket.to = field.values[(buckets[prop].length-1) + start];
            		arr.push(bucket);
            		start += buckets[prop].length;
            	}
            	
            	fieldData.buckets = arr;
            	fieldData.min = field.values[0];
            	fieldData.max = field.values[field.values.length-1];
        		
        	});
        	
        	delete data.fieldValues;
        	
        },
        
        randomizeBucket: function(bucket, val, type, cnt) {
        	var idx = (Math.round(val * 100000)%cnt);
        	if (type == "log") idx = (Math.round(Math.log(val * 100000))%cnt);
    		if (!bucket.hasOwnProperty(idx)) bucket[idx] = [];
    		bucket[idx].push(val);
        },
        
        getDistributionMatches: function(field, startVal, endVal) {
        	
        	var ret = 0;
        	
        	// primarily for consensus
        	if (!field.hasOwnProperty("values")) {
        		for (i = startVal; i <= endVal; i++) ret += field.buckets[i].count;
        		return ret;
        	}
        	
        	var sVal = field.buckets[startVal].from, eVal = field.buckets[endVal].to;
        	$.each(field.values, function(idx, val) {
        		if (val >= sVal && val <= eVal) ret++;
        	});
        	
        	return ret;
        	
        },
        
    	getSelectedFields: function() {
    		var data = { fields: [] };
    		$(".panel .checkbox.checked").each(function(idx, field) {
    			data.fields.push($(field).attr("data-id"));
    		});
    		return data;
    	},
    	
    	getFieldById: function(id) {
    		var ret = null;
    		var order = 0;
    		$(this.fieldGroups).each(function(idx, grp) {
    			$(grp.fields).each(function(f, field) {
					field.order = order;
    				if (field.id == id) ret = field;
    				order++;
    			});
    		});
    		return ret;
    	},
    	
    	maxCriteriaMsg: function() {
    		
            this.screener.modal.open({
                content: '<h4>Search Criteria <span>(select up to 5)</h4><p>You have reached the criteria limit. Please remove criteria before proceeding.</p>',
                type: 'alert'
            });
    		
    	},
    	
        renderInputs: function(data) {
        	
        	this.handleDistributions(data);
        	
        	var runsearch = true;
        	
        	if (data.distributions.length > 1) {
            	data.distributions.sort(function(a, b) {
            		return CRITERIA.getFieldById(a.field).order - CRITERIA.getFieldById(b.field).order;
            	});
        	}
        	
        	$.each(data.distributions, function(idx, distribution) {

        		var field = CRITERIA.getFieldById(distribution.field);
        		
        		if (field == null) return;
        		
        		var el = $("<tr >").attr("data-id", field.id).html(CRITERIA[field.template + "Template"]).addClass("criteria");
        		$(el).appendTo(".search-criteria tbody");
        		$(el).data(field);
        		
        		var mdl = {
        			'criteria': CRITERIA, 
        			'field': field,
        			'min': ko.observable(field.min),
        			'max': ko.observable(field.max),
        			'val': ko.observable(field.value)
        		};
        		
    			mdl.matches = ko.computed(function() { 
    				this.min(); this.max();
    				if (this.field.hasOwnProperty("buckets")) {
        				var slider = $(".search-criteria [data-id='" + this.field.id + "'] .slider-bar");
        				var min = slider.hasClass("ui-slider") ? $(slider).slider("values", 0) : 0;
        				var max = slider.hasClass("ui-slider") ? $(slider).slider("values", 1) : this.field.buckets.length - 1;
        				return this.criteria.getDistributionMatches(this.field, min, max);
    				}
    				return 0;
    			}, mdl);
    			
        		ko.applyBindings(mdl, $(el)[0]);
        		
        	});
        	
        	$(".search-criteria .criteria").removeClass("even");
        	$(".search-criteria .criteria:even").addClass("even");
        	
        	this.screener.dropdowns.init(".search-criteria");
        	
        	return runsearch;                	
        },
        
        addCriteria: function(data, finished) {
        	
    		$.each(data.distributions, function(idx, distribution) {
    			CRITERIA.clickEvents.checkCriteriaItem($(".search-criteria [data-id='" + distribution.field + "']"));
    		});
    		
    		CRITERIA.renderInputs(data);
        	
        },
        
    	removeCriteria: function(target, finished) {
    		
    		this.clickEvents.uncheckCriteriaItem(target);
    		this.screener.tooltips.close();
            $(target).remove();
            
        	$(".search-criteria .criteria").removeClass("even");
        	$(".search-criteria .criteria:even").addClass("even");
        	
        	CRITERIA.screener.hideLoading();
        	
            if (typeof finished !== "undefined") finished();
            
    	},
    	
    	clickEvents: {
    		
    		criteriaClick: function(data, event) {
    			
				var mainEl = $(event.currentTarget);
				
				// remove this field
				if ($(mainEl).hasClass("checked")) {
					event.currentTarget = $(".search-criteria [data-id='" + $(mainEl).attr("data-id") + "'] td.remove img")[0];
					CRITERIA.clickEvents.removeCriteria(data, event);
					return;
				}
				
        		// check if we're maxed out first
        		if ($(".search-criteria .criteria").length >= 5) {
        			CRITERIA.maxCriteriaMsg();
        			return;
        		}
        		
        		// let's show loading
        		var data = { "fields" : [ $(mainEl).attr("data-id") ] };
        		CRITERIA.getDistributions(data, CRITERIA.addCriteria);
        		
    		},
    		
        	removeCriteria: function(data, event) {
                event.stopPropagation();
                event.preventDefault();
        		CRITERIA.removeCriteria($(event.currentTarget).closest(".criteria"));
        	},
        	
        	checkCriteriaItem: function(criteria) {
        		var name = $(criteria).attr("data-id");
        		var checkEl = $(".criteria-select [data-id='" + name + "']");
                $(checkEl).addClass('checked');
                $(checkEl).find('input[type="checkbox"]').attr("checked", "checked");            		
        	},
        	
        	uncheckCriteriaItem: function(criteria) {
        		var name = $(criteria).attr("data-id");
        		var checkEl = $(".criteria-select [data-id='" + name + "']");
                $(checkEl).removeClass('checked');
                $(checkEl).find('input[type="checkbox"]').removeAttr("checked");            		
        	},
        	
        	reset: function(data, event) {

        		$(".search-criteria tbody").children().remove();
                	
            	/** handle sorting
            	var sorter = $(".module-results thead th.companyName");
            	if (!$(sorter).hasClass("sort") && !$(sorter).hasClass("asc")) $(sorter).click();
            	*/
            	
            	$(".criteria-select [data-default='true']").each(function(idx, el) { CRITERIA.clickEvents.checkCriteriaItem(el); });
            	CRITERIA.getDistributions(CRITERIA.getSelectedFields(), CRITERIA.screener.finalize);

        	}
    		
    	},

    	/**
    	 * settings
    	 */
    	additionalConfiguration: {
    		
    		'criteria-change-configure': {
    			
                type: 'prompt',
                
                maxWidth: 650,
                
                postLoad: function(options) {

                	$(".picker").each(function(idx, el) {
                		var data = $(el).data("config");
                		data.onSelect = function(date) { options.model[$(this).attr("id")]($(this).datepicker("getDate")); };
                		$(el).datepicker(data);
                		options.model[$(el).attr("id")]($(el).datepicker("getDate"));
                	});
                	
                },
                
                cancel: function(options) {
                	CRITERIA.removeCriteria($(".search-criteria [data-id='percentChange']"))
                },
                
                confirm: function(options) {
                	
                	
                	var model = options.model;
                	var percent = parseInt(model.val());
                	
                	if (isNaN(percent) || typeof percent === "undefined" || percent <= 0 || percent >= 100) {
                		alert("Percent Change must be between 1 and 100");
                		model.val("");
                		return;
                	}
                	
                	
                	CRITERIA.clickEvents.checkCriteriaItem($(".criteria-select [data-id='percentChange']"));
                	
                   	CRITERIA.screener.modal.close();
                	
                }
    			
    			
    		}
    		
    		
    	}
		
	};
	
	return CRITERIA;
	
});