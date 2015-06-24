define([ "wmsi/utils", "knockout", "client/modules/search", "client/modules/criteria", "jquery-placeholder" ], function(UTIL, ko, SEARCH, CRITERIA) {
	
	var SCREENER = {
			
		criteria: null,
		
		search: null,

		initPage: function() {
			
			// set the page objects
			SCREENER.criteria = CRITERIA.init(this);
			SCREENER.search = SEARCH;

			// some base variables
			var searchType = UTIL.getParameterByName("type") == "" ? "advanced-screener" : UTIL.getParameterByName("type");
    		var factor = UTIL.getParameterByName("factor");
    		var quintile = parseInt(UTIL.getParameterByName("quintile"));

    		// load the marketing copy
    		SCREENER.loadMarketingCopy();
    		
    		// load the default keyword/screener toggle
    		SCREENER.changeScreenerToggle(searchType);

    		// apply bindings
    		ko.applyBindings(SCREENER);
    		
    		// now finish setting up page
    		$(".searchtoggle .toggle:first").click();
    		
    		this.tooltips.init("body");
    		
    		// show page
    		this.hideLoading();
    		
		},
		
		/**
		 * load a random ad from the JSON and display in header
		 */
		loadMarketingCopy: function() {
			
			$.getJSON( "data/homepage.json?time=" + new Date().getMilliseconds(), function(data) {
            	var promo = Math.floor(Math.random() * data.promos.length) + 1;
            	promo = data.promos[promo-1];
            	
            	var remove = promo.type == "image" ? "text" : "image";
            	$(".screener-header .message .promo-" + remove).remove();
            	
            	if (promo.type == "image") {
            		
            		$(".screener-header .message .promo-image img").attr("src", promo.src);
            		if (promo.hasOwnProperty("href")) {
            			$(".screener-header .message .promo-image a").attr("href", promo.href).attr("target", "_top");
            			
            		}
            		
            		return;
            	}
            	
            	// otherwise promo is text
            	$(".screener-header .message .intro-headline").html(promo.title);
            	$(".screener-header .message .copy").html(promo.copy);
			});
			
		},
		
		/**
		 * change the screener options 
		 */
		changeScreenerToggle: function(name) {
			
			$(".searchtoggle .toggle:first").click();

			// remove all classes and add current class to results table
			$(".screener-toggles .button").each(function(idx, el) { $(".module-results").removeClass($(this).attr("data-name") + "-clz"); });
			$(".module-results").addClass(name + "-clz");

			// toggle tabs
			$(".screener-toggles .button, .screener-toggles .arrow").removeClass("selected");
			$(".screener-toggles span[data-name='" + name + "']").addClass("selected");
			
			// hide/show options
			$(".search-options").hide();
			$("[data-section='" + name + "']").show();
			
			//SGX.trackPage("SGX - Screener (" + $(".screener-toggles span[data-name='" + name + "']").text() + ")");
			
		},
		
		hideLoading: function() {
			$('#loading').hide();
		},
		
		showLoading: function() {
			$('#loading').show();
		},
		
		/**
		 * handles keyword toogle
		 */
		clickEvents: {
			
			/**
			 * toggle the keyword field
			 */
			keywordToggle: function(data, event) {
				var me = event.currentTarget;
				$(".searchtoggle .toggle").removeClass("selected");
				$(".searchtoggle .s" + $(me).attr("data-name")).addClass("selected");
				$(".searchbar input").attr("placeholder", $(me).attr("data-placeholder"));
				$('.searchbar input').placeholder();
				$('.searchbar input').val("");
			},
			
			/**
			 * toggle the screener display
			 */
			screenerToggle: function(data, event) {
				var name = $(event.currentTarget).attr("data-name");
				SCREENER.changeScreenerToggle(name);
				if (name == "advanced-screener") SCREENER.criteria.reset(SCREENER.search.criteriaSearch); // returning to advanced means resetting search
				else SCREENER.search.criteriaSearch(); // otherwise just use what's there
			},
			
			/** 
			 * perform a keyword search
			 */
			keywordSearch: function(data, event) {
				var fn = function() { SCREENER.search.nameSearch($(".searchbar input").val()); };
				if ($.trim($(".searchbar input").val()) == "") fn = function() { SCREENER.search.showAll(); };
				SCREENER.criteria.reset(fn);
			}
			
		}

	};
	
	return SCREENER;
	
});