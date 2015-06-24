define([ "wmsi/utils", "knockout", "text!client/data/fields.json", "text!client/templates/criteria-number.tmpl.html", "text!client/templates/criteria-select.tmpl.html" ], function(UTIL, ko, fieldData, nTemplate, sTemplate) {
	
	var CRITERIA = {
			
		screener: null,
		numberTemplate: null,
		selectTemplate: null,
			
		init: function(screener) {
			
			// parse in JSON field configuration
			$.extend(true, this, JSON.parse(fieldData));
			
			this.numberTemplate = nTemplate;
			this.selectTemplate = sTemplate;
			this.screener = screener;
			
			return this;
		},

    	reset: function(finished) {
        	
        	$(".search-criteria tbody").children().remove();
        	
        	/** handle sorting
        	var sorter = $(".module-results thead th.companyName");
        	if (!$(sorter).hasClass("sort") && !$(sorter).hasClass("asc")) $(sorter).click();
        	*/
        	
        	$(".criteria-select [data-default='true']").each(function(idx, el) { CRITERIA.clickEvents.checkCriteriaItem(el); });
        	this.getDistributions(this.getSelectedFields(), this.screener.finalize);
        	
    	},
    	
    	getDistributions: function(data, finished) {

    		var tmpF = finished;
    		var endpoint = this.screener.fqdn + "/sgx/search/distributions";
    		var hasPercentChange = $.inArray("percentChange", data.fields);
    		if (hasPercentChange != -1) data.fields.splice(hasPercentChange, 1);
    		
    		// change the function
    		if (hasPercentChange != -1) {
    			tmpF = function(data) { 
    				data.distributions.push({ "field": "percentChange", buckets: [], template: "change" }); 
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
        	
        	if (typeof data.fieldValues === "undefined") return;
        	
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
            	fieldData.max = field.values[field.values.length];
        		
        	});
        	
        	delete data.fieldValues;
        	
        },
        
        randomizeBucket: function(bucket, val, type, cnt) {
        	var idx = (Math.round(val * 100000)%cnt);
        	if (type == "log") idx = (Math.round(Math.log(val * 100000))%cnt);
    		if (!bucket.hasOwnProperty(idx)) bucket[idx] = [];
    		bucket[idx].push(val);
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
        		
        		ko.applyBindings({ 'criteria': CRITERIA, 'field': field }, $(el)[0]);
        		
        	});
        	
        	$(".search-criteria .criteria").removeClass("even");
        	$(".search-criteria .criteria:even").addClass("even");
        	//SGX.formatter.formatElements(".search-criteria");
        	
        	return runsearch;                	
        },
        
        addCriteria: function(data, finished) {
        	
    		var run = CRITERIA.renderInputs(data);
    		$.each(data.distributions, function(idx, distribution) {
    			CRITERIA.clickEvents.checkCriteriaItem($(".search-criteria [data-id='" + distribution.field + "']"));
    		});
    		
    		CRITERIA.screener.hideLoading();

    		//if (run) SGX.screener.search.criteriaSearch();
        	
        },
        
    	removeCriteria: function(target, finished) {
    		
    		this.clickEvents.uncheckCriteriaItem(target);
    		this.screener.tooltips.close();
            $(target).remove();
            
        	$(".search-criteria .criteria").removeClass("even");
        	$(".search-criteria .criteria:even").addClass("even");
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
        		CRITERIA.screener.showLoading();
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
    		
    	}
		
	};
	
	return CRITERIA;
	
});