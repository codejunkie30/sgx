define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "client/modules/tearsheet", "text!client/data/watchlist.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, TS, WL) {
	
	
	var ALERTS = {
		finalWL: ko.observableArray(),
		selectedValue: ko.observable(),
		displayList: ko.observable(),
		weeks: ko.observableArray(),
		actualEstimates: ko.observableArray(),
		consensusRec: ko.observableArray(),
		displayListCompanies: ko.observableArray(),
		watchList: ko.observable(true),
		name: ko.observable(),
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
		messages: JSON.parse(MESSAGES),
		
		defaultSearch: "",
		
		initPage: function() {
						
			// extend tearsheet
			$.extend(true, this, TS);
			
			var self = this;
			this.init(function() { self.finish(self); });
			
			PAGE.checkStatus();
			
		},
		
		finish: function(me) {
			var displayMessage = ALERTS.messages.messages[0];
			
			this.finalWL(JSON.parse(WL).watchlists);
			
			if (this.finalWL().length == 0) {
				$('.wl-container').hide();
			} else {
				$('.wl-container').show();
				PAGE.resizeIframeSimple();			
			}
			
			var test = ko.computed(function(){				
				ALERTS.displayList(ALERTS.finalWL()[ALERTS.selectedValue()-1]);
			
				ALERTS.weeks([
			        {"id":1, "name": "High"},
			        {"id":2, "name": "Low"}
			    ]);
				
				ALERTS.consensusRec([
			        {id:1, name: "Buy"},
			        {id:2, name: "Sell"},
			        {id:3, name: "Hold"}
			    ]);
				
				ALERTS.actualEstimates([
			        {id:1, name: "Beat"},
			        {id:2, name: "Miss"},
			        {id:3, name: "Flat"}
			    ]);
				
				
				
				console.log(ALERTS.actualEstimates());
				
				$.each($('.alerts input[type=text]'),function(){
					if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
				});
				
				$('.alerts input[type=text]').change(function(){
					if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
				});
				
				PAGE.resizeIframeSimple();		
			});
			
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);	
			
			me.trackPage("SGX Company Watchlist - " + me.companyInfo.companyName);
    		
			ko.validation = validation;
    		validation.init({ insertMessages: false });
			
			ko.validation.registerExtenders();
			
			ALERTS.name.extend({
				required: { message: displayMessage.watchlist.error}}).extend({
					minLength: { params: 1, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
				});
			
			this.errors = ko.validation.group(this);			
			
			this.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});
			
			
			 
			
			
			
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
		addWatchlist: function(){
			var displayMessage = ALERTS.messages.messages[0];
			if (this.errors().length > 0) {
	         	$('.error-messages').empty();
				$('<p/>').html(displayMessage.watchlist.error).appendTo('.error-messages');
			    return
	        }
		},
		showWatchlist: function(){
			selectedValue = ko.computed(function () {
				
			});alert('here');
		}
		

	};
	
	return ALERTS;
	
});