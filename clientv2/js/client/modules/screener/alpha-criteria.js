define([ "wmsi/utils", "knockout", "text!client/data/factors.json" ], function(UTIL, ko, FACTORS) {

	/**
	 * allows us to customize particular criteria
	 * assumes working with criteria object for model
	 */
	ko.bindingHandlers.createFactor = {
			
	    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
	    	
	    	$.each(new Array(5), function(quint) {
	    		
	    		var span = $("<span />").attr("data-quintile", quint + 1).appendTo($(".quintiles", element)); 
	    		
				$(span).hover(
						function(e) {
							var mine = $(this).attr("data-quintile");
							$(this).parent().removeClass(function(pos, css) { return (css.match(/(^|\s)per-\S+/g) || []).join(' '); });
							$(this).parent().addClass("per-" + (mine*20));
						},
						function(e) {
							var mine = $(this).attr("data-quintile");
							$(this).parent().removeClass(function(pos, css) { return (css.match(/(^|\s)per-\S+/g) || []).join(' '); });
							if (typeof $(this).parent().attr("data-class") !== "undefined") $(this).parent().addClass($(this).parent().attr("data-class"));
				});
				
				$(span).click(function(e) {
					var mine = $(this).attr("data-quintile");
					$(this).closest(".quintiles").addClass("per-" + (mine*20)).attr("data-class", "per-" + (mine*20)).attr("data-value", mine);
					$(".right-label", $(this).closest(".factor")).addClass("selected");
					bindingContext.$parent.runSearch();
				});
				
	    	});
	    	
			$(".right-label", element).click(function() {
				if (!$(this).hasClass("selected")) return;
				$(this).removeClass("selected");
				var prg = $(".quintiles", element);
				$(prg).removeClass($(prg).attr("data-class")).removeAttr("data-class")
				$(prg).removeAttr("data-value");
				bindingContext.$parent.runSearch();
			});
			
			
	    	
	    }
	};
	
	var CRITERIA = {
			
		screener: null,
		
		factor: UTIL.getParameterByName("factor"),
		quintile: parseInt(UTIL.getParameterByName("quintile")),
		paused: false,
		
		init: function(screener, finalize) {
			
    		// reset the val and the toggle
			$(".searchtoggle .toggle:first").click();
    		$(".searchbar input").val("");

    		if (!this.hasOwnProperty("factors")) {
    			var tmp = JSON.parse(FACTORS);
    			$.each(tmp.factors, function(idx, field) {
    				var item = { "minLabel": "High", "maxLabel": "Low"  };
    				$.extend(item, field);
    				tmp.factors[idx] = item;
    			});
    			$.extend(true, this, tmp);
    		}
			
			this.screener = screener;
			screener.criteria = this;
			
			if ($(".search-options[data-section='alpha-factors'] .alpha-factors[data-init='true']").length == 0) {
				ko.applyBindings(this, $(".search-options[data-section='alpha-factors']")[0]);
				$(".search-options[data-section='alpha-factors'] .alpha-factors").attr("data-init", "true");
				if (this.factor != "" && !isNaN(this.quintile) && this.quintile > 0 && this.quintile <= 5) return;
			}

			this.reset();
		},
		
		renderInputs: function(data) {
			// DO NOTHING
		},

    	reset: function() {
			
    		// remove all selected values
    		this.paused = true;
    		$(".alpha-factors .right-label").click();
    		this.pause = false;

    		// run the search
   			this.runSearch();
    		
    	},
    	
    	checkDefault: function(elements, data) {
    		
    		var factor = UTIL.getParameterByName("factor");
    		var quintile = parseInt(UTIL.getParameterByName("quintile"));
    		var curId = $(".quintiles", elements[0]).attr("data-id");
    		if (curId == factor && !isNaN(quintile) && quintile > 0 && quintile <= 5) {
    			$(".quintiles span:eq(" + (quintile-1) + ")", elements[0]).click();
    		}

    	},
    	
    	runSearch: function() {
    		
    		var endpoint = "/sgx/search/alphaFactors";
    		var params = {};
    		
    		// collect criteria
    		$(".alpha-factors .quintiles[data-value]").each(function(idx, el) { params[$(this).attr("data-id")] = parseInt($(this).attr("data-value")); });
    		
    		// just search all companies
    		if ($.isEmptyObject(params)) {
    			params.criteria = [{ field: "exchange", value: "SGX" },{ field: "exchange", value: "CATALIST" }];
    			endpoint = "/sgx/search";
    		}
    		
    		// search
    		this.screener.results.retrieve(endpoint, params);    		
    	}
		
	};
	
	return CRITERIA;
	
});