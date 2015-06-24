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
    	
    	maxCriteriaMsg: function() {
    		
            this.screener.modal.open({
                content: '<h4>Search Criteria <span>(select up to 5)</h4><p>You have reached the criteria limit. Please remove criteria before proceeding.</p>',
                type: 'alert'
            });
    		
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