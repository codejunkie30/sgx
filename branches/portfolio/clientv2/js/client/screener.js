define([ "wmsi/utils", "knockout", "client/modules/results", "jquery-placeholder" ], function(UTIL, ko, SEARCH) {
	
	
	var SCREENER = {
		criteria: null,		
		results: null,		
		defaultSearch: "advanced-screener",		
		premiumUser: ko.observable(),	
		premiumUserEmail: ko.observable(),		
		premiumUserAccntInfo: ko.observable(),
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(false),
		currentDay: ko.observable(),
		initPage: function() {
			PAGE.checkStatus();
			
			this.results = SEARCH.init(this);

			// some base variables
			var searchType = UTIL.getParameterByName("type") == "" ? this.defaultSearch : UTIL.getParameterByName("type");

    		// load the marketing copy
    		this.loadMarketingCopy();

    		// apply bindings
    		var scrnr = this;
    		$(".screener-header").each(function(idx, el) { ko.applyBindings(scrnr, $(el)[0]); });
    		
			// reset keyword
			$(".searchtoggle .toggle:first").click();
    		
    		// load the default keyword/screener toggle
    		this.changeScreenerToggle(searchType);
			
    		// finish other page loading
    		ko.applyBindings(this, $(".disclosure")[0]);
			
    		return this;
		},
		
		finalize: function(data) {
		
			// get the current screener object
			var screener = ko.dataFor($(".screener-header")[0]);
			
			// render
			screener.criteria.renderInputs(data);
			
    		// show page
			PAGE.hideLoading();
			
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
            			//if promo.href is navigate to page within the application. Revert the code changes below if the href is an URL 
            			var parentURL;
            			if (typeof document.location.hash !== "undefined" && document.location.hash != "") {
	            			parentURL = decodeURIComponent(document.location.hash.replace(/^#/, ''));
	            			parentURL = parentURL.split("?")[0].split("#")[0];
            			}
            			else {
            				parentURL = window.location.pathname;
            			}
            			var pageURL = parentURL + promo.href;
            			$(".screener-header .message .promo-image a").attr("href", pageURL).attr("target", "_top");
            			
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
			
			// loading thingy
			this.showLoading();
			
			// confirm exists
			if ($(".screener-toggles .button[data-name='" + name + "']").length == 0) name = this.defaultSearch; 

			// remove all classes and add current class to results table
			$(".screener-toggles .button").each(function(idx, el) { $(".module-results").removeClass($(this).attr("data-name") + "-clz"); });
			$(".module-results").addClass(name + "-clz");

			// toggle tabs
			$(".screener-toggles .button, .screener-toggles .arrow").removeClass("selected");
			$(".screener-toggles span[data-name='" + name + "']").addClass("selected");
			
			// hide/show options
			$(".search-options").hide();
			$("[data-section='" + name + "']").show();
			
			this.trackPage("SGX - Screener (" + $(".screener-toggles span[data-name='" + name + "']").text() + ")");
			
			this.searchEvents[name](this);
			
			//PAGE.hideLoading();
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
			},
			
			/**
			 * toggle the screener display
			 */
			screenerToggle: function(data, event) {
				var name = $(event.currentTarget).attr("data-name");
				var screener = ko.dataFor($(".screener-header")[0]);
				screener.results.viewModel.sectors.val(null);
				screener.changeScreenerToggle(name);
			},
			
			/** 
			 * perform a keyword search
			 */
			keywordSearch: function(data, event) {
				var screener = ko.dataFor($(".screener-header")[0]);
				screener.changeScreenerToggle("all-companies");
			}
			
		},
		
		searchEvents: {
			
			"advanced-screener": function(screener) {
				// initialize using advanced criteria object
				require(["client/modules/screener/advanced-criteria"], function(crit) {
					crit.init(screener, screener.finalize);
				});
				
			},
			
			"alpha-factors": function(screener) {
				// initialize using advanced criteria object
				require(["client/modules/screener/alpha-criteria"], function(crit) {
					crit.init(screener, screener.finalize);
				});
				
			},
			
			"all-companies": function(screener) {
				
				// initialize using advanced criteria object
				require(["client/modules/screener/all-companies-criteria"], function(crit) {
					crit.init(screener, screener.finalize);
				});
			
			}			
		}

	};
	
	return SCREENER;
	
});