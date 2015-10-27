define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "client/modules/tearsheet", "text!client/data/watchlists/watchlist.json", "text!client/data/watchlists/alerts.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, TS, WL, AL) {
	
	
	var ALERTS = {
		finalWL: ko.observableArray(),
		selectedValue: ko.observable(),
		displayList: ko.observable(),
		//companies: ko.observableArray(),
		weeks: ko.observableArray(),
		actualEstimates: ko.observableArray(),
		consensusRec: ko.observableArray(),
		displayListCompanies: ko.observableArray(),
		watchList: ko.observable(true),
		newWLName: ko.observable(),
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
		alerts: JSON.parse(AL),
		
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
			
			//Alerts select lists
			this.weeks(JSON.parse(AL).alerts[0].weeks);
			this.consensusRec(JSON.parse(AL).alerts[0].consensusRec);
			this.actualEstimates(JSON.parse(AL).alerts[0].actualEstimates);
			
			var wathclistDisplay = ko.computed(function(){
				
				ALERTS.finalWL(JSON.parse(WL).watchlists);
				
				$.each(ALERTS.finalWL(), function(idx, wl){					
					if (ALERTS.selectedValue() ==  wl.id){						
						var companies = wl.companies
						wl.companies = ko.observableArray(companies);						
						ALERTS.displayList(wl);
					}
				});
				
				$.each($('.alerts input[type=text]'),function(){
					if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
				});
				
				$('.alerts input[type=text]').change(function(){
					if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
				});
				
				PAGE.resizeIframeSimple();		
			});
			
			
			
			//var endpoint = "http://sgx.fakemsi.com/js/client/data/watchlists/watchlist.json";
//			var postType = 'GET';
//    		var params = {};
//			var jsonp = 'jsonp';
//			var jsonpCallback = 'jsonpCallback';
//    		UTIL.handleAjaxRequest(endpoint, postType, params, jsonp, function(data, textStatus, jqXHR){ console.log(data); },
//				function(jqXHR, textStatus, errorThrown){
//					console.log('fail');
//					console.log(textStatus);
//					console.log(errorThrown);
//					console.log(jqXHR);
//				},jsonpCallback);
						
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);	
			
			me.trackPage("SGX Company Watchlist - " + me.companyInfo.companyName);
    		
			ko.validation = validation;
			
    		validation.init({ insertMessages: true });
			
			this.newWLName.extend({
				required: { message: displayMessage.watchlist.error }}).extend({
					minLength: { params: 1, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
				});
			
			this.errors = ko.validation.group(this);			
			
			this.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});
			
			return this;
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
			if (this.errors().length > 0) {				
	            return
	        }
						
			console.log(ALERTS.finalWL());
			
				
				ALERTS.finalWL().push(
					{
					"id": 15,
					"name": ALERTS.newWLName(),
					"companies": [],
					"optionList": {
						"pcPriceDrop": false,
						"pcPriceDropBelow": null,
						"pcPriceRiseAbove": null,
						"pcTradingVolume": false,
						"pcTradingVolumeValue": null,
						"pcReachesWeek": false,
						"pcReachesWeekValue": 1,
						"estChangePriceDrop": false,
						"estChangePriceDropBelow": null,
						"estChangePriceDropAbove": null,
						"estChangeConsensus": false,
						"estChangeConsensusValue": 1,
						"estChangeEstimates": false,
						"estChangeEstimatesValue" : 1,		
						"kdAnounceCompTransactions": false,
						"kdTransactionUpdates": false,
						"kdBankruptcyUpdates": false,
						"kdCompanyForecasts": false,
						"kdCorporateStructureRelated": false,
						"kdCustProdRelated": false,
						"kdDividensSplits": false,
						"kdListTradeRelated": false,
						"kdPotentialRedFlags": false,
						"kdPotentialTransactions": false,
						"kdResultsCorpAnnouncements": false
					}
				}
			
			
			)
			console.log(ALERTS.finalWL());	
			
			
			
		}

	};
	
	return ALERTS;
	
});