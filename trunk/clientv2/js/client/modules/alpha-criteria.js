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
					$(this).closest(".quintiles").addClass("per-" + (mine*20)).attr("data-class", "per-" + (mine*20));
					$(this).closest(".factor").attr("data-value", mine);
					$(".right-label", $(this).closest(".factor")).addClass("selected");
				});
				
	    	});
	    	
			$(".right-label", element).click(function() {
				if (!$(this).hasClass("selected")) return;
				$(this).removeClass("selected");
				var prg = $(".quintiles", element);
				$(prg).removeClass($(prg).attr("data-class")).removeAttr("data-class")
				$(element).removeAttr("data-value");
			});
	    	
	    }
	};
	
	var CRITERIA = {
			
		screener: null,
		
		factorDefaults: { "minLabel": "High", "maxLabel": "Low"  },
			
		init: function(screener, finalize) {
			
			var tmp = JSON.parse(FACTORS);
			$.each(tmp.factors, function(idx, field) {
				var item = { "minLabel": "High", "maxLabel": "Low"  };
				$.extend(item, field);
				tmp.factors[idx] = item;
			});
			
			$.extend(true, this, tmp);
			
			this.screener = screener;
			screener.criteria = this;
			
			if ($(".search-options[data-section='alpha-factors'] .alpha-factors[data-init='true']").length == 0) {
				ko.applyBindings(this, $(".search-options[data-section='alpha-factors']")[0]);
				$(".search-options[data-section='alpha-factors'] .alpha-factors").attr("data-init", "true");
				finalize(undefined);
				return;
			}
			
			this.reset(finalize);
			
		},
		
		renderInputs: function(data) {
			// NOTHING NEEDS TO HAPPEN
		},

    	reset: function(finished) {

    		$(".alpha-factors .right-label").click();
    		
    	}
		
	};
	
	return CRITERIA;
	
});