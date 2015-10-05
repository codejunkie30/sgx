define([ "wmsi/utils", "knockout", "client/modules/results", "jquery-placeholder" ], function(UTIL, ko, SEARCH) {
	
	
	var ALERTS = {
		
		watchList: ko.observable(true),
		premiumUser: ko.observable(),	
		premiumUserEmail: ko.observable(),		
		premiumUserAccntInfo: ko.observable(),
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(),
		currentDay: ko.observable(),
		
		defaultSearch: "advanced-screener",

		initPage: function() {
			
			this.results = SEARCH.init(this);

			// some base variables
			var searchType = UTIL.getParameterByName("type") == "" ? this.defaultSearch : UTIL.getParameterByName("type");


    		// apply bindings
    		var scrnr = this;
    		$(".screener-header").each(function(idx, el) { ko.applyBindings(scrnr, $(el)[0]); });
    		
			// reset keyword
			$(".searchtoggle .toggle:first").click();
    		
    		// load the default keyword/screener toggle
    		this.changeScreenerToggle(searchType);
    		
			PAGE.checkStatus();
			
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
			screener.hideLoading();
			
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
		},
		checkStatus: function(){
			
			var endpoint = PAGE.fqdn + "/sgx/account/info";
			var postType = 'POST';
			var params = {};
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				jsonp,
				function(data, textStatus, jqXHR){
					if (data.reason == 'Full authentication is required to access this resource'){
						//top.location.href = PAGE.getPage(PAGE.pageData.getPage('premium'));
					} else {
						PAGE.premiumUser(true);
						PAGE.premiumUserAccntInfo = data;
						PAGE.premiumUserEmail(PAGE.premiumUserAccntInfo.email);
						PAGE.timedLogout();
					}
					
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log(textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);			
		}

	};
	
	return ALERTS;
	
});