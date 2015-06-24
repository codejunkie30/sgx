define([ "wmsi/utils", "knockout", "text!client/data/fields.json" ], function(UTIL, ko, fieldData) {
	
	var CRITERIA = {
			
		screener: null,
			
		init: function(screener) {
			
			// parse in JSON field configuration
			$.extend(true, this, JSON.parse(fieldData));
			
			this.screener = screener;
			
			return this;
		},

    	reset: function(finished) {
        	
    		/**
        	$(".search-criteria tbody").children().remove();
        	
        	var sorter = $(".module-results thead th.companyName");
        	if (!$(sorter).hasClass("sort") && !$(sorter).hasClass("asc")) $(sorter).click();
            
        	// load up all fields for distributions
        	var allFields = SGX.screener.getAllCriteria();
        	var data = { "fields" : [] };
        	$.each(allFields, function(idx, field) { 
        		if (field.isDefault) data.fields.push(field.id); 
        	});
        	
        	SGX.showLoading();
        	
        	SGX.screener.criteriaChange.getDistributions(data, function(data) { 
        		$(".editSearchB .checkbox").removeClass("checked");
        		$(".editSearchB .checkbox.default").addClass("checked");
        		SGX.screener.drawCriteria(data); 
        		SGX.hideLoading(); 
        		if (finished) finished(data);
        	});
    		*/
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
        		
        		var el = $("<tr >").attr("data-bind", "template: 'criteria-" + field.template + "'").addClass("criteria");
        		$(el).appendTo(".search-criteria tbody");
        		ko.applyBindings(field, $(el)[0]);

        		/**
        		// draw the appropriate input
        		if (distribution.template == "select") template = SGX.screener.drawCriteriaSelect(distribution);
        		else if (distribution.template == "change") {
        			template = SGX.screener.drawCriteriaChange(distribution);
        			runsearch = false;
        		}
        		else template = SGX.screener.drawCriteriaSlider(distribution);
        		
            	$(".label .glossary-item", template).attr("glossary-key", distribution.id);
            	$(template).attr("data-name", distribution.id);

        		$("td.remove", template).click(function(e) {
                    e.stopPropagation();
                    e.preventDefault();
                    SGX.tooltip.close();
                    SGX.screener.criteriaChange.removeCriteria($(this).closest(".criteria"), function() {});
        		});
        		
        		SGX.tooltip.init(template);
        		*/
        		
        		
        	});
        	
        	/**
        	$(".search-criteria .criteria").removeClass("even");
        	$(".search-criteria .criteria:even").addClass("even");
        	SGX.formatter.formatElements(".search-criteria");
        	*/
        	
        	return runsearch;                	
        },
    	
    	clickEvents: {
    		
    		criteriaClick: function(data, event) {
    			
				var mainEl = $(event.currentTarget);
				
				// remove this field
				if ($(mainEl).hasClass("checked")) {
					var target = $(".search-criteria [data-name='" + $(mainEl).attr("data-name") + "']");
					$("td.remove", target).click();
					return;
				}
				
        		// check if we're maxed out first
        		if ($(".search-criteria .criteria").length >= 5) {
        			CRITERIA.maxCriteriaMsg();
        			return;
        		}
        		
        		/*
				// add in the field
				CRITERIA.screener.showLoading();        		
				//var data = { "fields" : [ $(this).attr("data-name") ] };
				//SGX.screener.criteriaChange.getDistributions(data, SGX.screener.criteriaChange.addCriteria);
    			*/
    		}
    		
    	}
		
	};
	
	return CRITERIA;
	
});