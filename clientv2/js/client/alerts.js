define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "client/modules/tearsheet", "text!client/data/watchlists/alerts.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, TS, AL) {

	ko.components.register('premium-preview', { require: 'client/components/premium-preview'});
	
	var ALERTS = {
		finalWL: ko.observableArray(),
		selectedValue: ko.observable(),
		displayList: ko.observable(),
		companies: ko.observableArray(),
		addWatchlistName: ko.observableArray(),
		weeks: ko.observableArray(),
		displayTempCom: ko.observableArray(),
		actualEstimates: ko.observableArray(),
		consensusRec: ko.observableArray(),
		displayListCompanies: ko.observableArray([]),
		searchInput: ko.observable(),
		searchResults: ko.observableArray(),
		searchReady: ko.observable(false),
		watchList: ko.observable(true),
		newWLName: ko.observable(),
		editWLName: ko.observable(),
		showChange: ko.observable(false),
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
		sectionName:'Alerts',
		allCompanies: [],
		skipUpdateCompanies: ko.observable(false),
		
		defaultSearch: "",
		
		initPage: function() {
			///
			var self = this;
			var endpoint = this.fqdn+'/sgx/company/names';
			
			function makeAggregateCompanyDataCall() {

				$.getJSON(endpoint+"?callback=?").done(function(data){
					self.allCompanies = data.companyNameAndTickerList;
					self.searchReady(true);

				}).fail(function(jqXHR, textStatus, errorThrown){
					console.log('error making makeAggregateCompanyDataCall');
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				});

			}

			var self = this;
			
			PAGE.checkStatus();
			var waitForDataToInit = ko.computed({
			  read:function(){
				 // var companyData = this.gotCompanyData();
				  var userStatus = PAGE.userStatus();
				  if( userStatus && userStatus != '' ) {
					  if ( userStatus == 'UNAUTHORIZED' || userStatus == 'EXPIRED' ) {
						this.init_nonPremium();
						ko.applyBindings(this, $("body")[0]);
					  } else {
						$('#alerts').show();
						/*
						* Get all company Names callback
						*/
						this.finish(this, makeAggregateCompanyDataCall); 

					  }
				  }		
			  },
			  owner:this
		  });
			
		},

		/*
		* Get all company Names callback
		*/
		finish: function(me, callback) {

			PAGE.showLoading();
			var displayMessage = ALERTS.messages.messages[0];
			var endpoint = me.fqdn + "/sgx/watchlist/get";
			var postType = 'GET';
    		var params = {};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					PAGE.hideLoading();
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					ALERTS.finalWL(data.watchlists.sort(sortByName));
					callback();
					var arr = data.removed;					
					var removedTicker = arr.toString();
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your Watch Lists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
					}
					
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);
			
			//Alerts select lists
			this.weeks(JSON.parse(AL).alerts[0].weeks);
			this.consensusRec(JSON.parse(AL).alerts[0].consensusRec);
			this.actualEstimates(JSON.parse(AL).alerts[0].actualEstimates);
			
			var watchlistDisplay = ko.computed(function(){
				
				
				$.each(ALERTS.finalWL(), function(idx, wl){
					if (ALERTS.selectedValue() ==  wl.id){
												
						ALERTS.companies(wl.companies);							
						
						firstRun = false;
						
						ALERTS.displayList(wl);

						ALERTS.clearWatchListErrors();
						
						ALERTS.editWLName(wl.name);						
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


			me.searchInput.subscribe(function(data){
				//ratelimiting the search 200ms
				if(this._localTimeout) {
					clearTimeout(this._localTimeout);
					delete this._localTimeout;
				}

				var resultArray;
				if(data.length == 0) {
					me.searchResults.removeAll();
					return;
				}

				this._localTimeout = setTimeout(function(){

					resultArray = ko.utils.arrayFilter(me.allCompanies, function(item){
						//var ticketMatch = false;
						if(item.companyName.toLowerCase().indexOf(data.toLowerCase()) != -1 || item.tickerCode.toLowerCase().indexOf(data.toLowerCase()) != -1 ) {
							return true;
						} else {
							return false;
						}
					});
					me.searchResults( resultArray );
				}, 200)


			});
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);	
			
			me.trackPage("SGX Company Watchlist");
    		
			ko.validation = validation;
			
    		validation.init({ insertMessages: true });
			
			ALERTS.newWLName.extend({
				required: { message: displayMessage.watchlist.error }}).extend({
					minLength: { params: 1, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
				});
			
			this.errors = ko.validation.group(this);			
			
			this.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});

			ALERTS.companies.subscribe(function(data){
				if ( !ALERTS.skipUpdateCompanies() ){  
					setTimeout(function(){ ALERTS.displayWatchlists(data); }, 400);
				}
				else 
					ALERTS.skipUpdateCompanies(false);
			});

			
			return this;
			
		},
		
		init_nonPremium: function() {
            $('#alerts-content-alternative').show();
        },
		
		addWatchlist: function(){
			var displayMessage = ALERTS.messages.messages[0];
			
			if (this.errors().length > 0) {
				$('.error-messages').empty();
				$('<p/>').html(displayMessage.watchlist.error).appendTo('.error-messages');			
	            return
	        }
			$('.error-messages').empty();
			
			var wlLength = ALERTS.finalWL().length;
			
			ALERTS.addWatchlistName([]);
			$.each(ALERTS.finalWL(), function(i, data){
				ALERTS.addWatchlistName.push(data.name.toLowerCase());
			});			
			
			var newWLNameLC = ALERTS.newWLName();
			
			if ($.inArray( newWLNameLC.toLowerCase(), ALERTS.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return; }
			
			if (wlLength >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 Watch Lists.</p>', width: 600 }); return; }

			var endpoint = PAGE.fqdn + "/sgx/watchlist/create";
			var postType = 'POST';
    		var params = { "message": ALERTS.newWLName() };
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';			
			PAGE.showLoading();
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){					
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					PAGE.hideLoading();
					ALERTS.finalWL(data.sort(sortByName));
					
					$.each(data, function(i,data){
						if (data.name == newWLNameLC){
							ALERTS.saveWatchlist();
							ALERTS.selectedValue(data.id);
							
							//setTimeout(function(){ ALERTS.displayWatchlists();}, 500);
						}						
					});
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);	
			
			
			//Clears add WL after submit
			ALERTS.newWLName(null);
		},

		_hideLoading: function() {
			$('#loading-alerts').remove();
			$('#grayout').remove();
		},
		
		_showLoading: function() {
			var container = $('.wl-companies');
			container.prepend($('<div id="grayout" class="grayout"></div>'));
			$('.wl-companies').prepend($('<div id="loading-alerts"><div class="loading-text"><img src="img/ajax-loader.gif"></div></div>'));
		},
		
		displayWatchlists: function(data){
			
			var self = this;
			
			if (Object.prototype.toString.call(data) == '[object Array]' && data.length == 0){ 
				$('.wl-companies ul').empty(); 
				return;
			}
			self._showLoading();
			var endpoint = PAGE.fqdn + "/sgx/price/companyPrices";
			var postType = 'GET';
			var params = { "companies": data };
			function jsonpCallback(){
				'jsonpCallback was called';
			}


			$.getJSON(endpoint+"?callback=?", { 'json': JSON.stringify(params) })

			.done(function(data){
				ALERTS.displayListCompanies(data.companyPrice);
				self._hideLoading();
				setTimeout(function(){ PAGE.resizeIframeSimple() }, 500);

			}).fail(function(jqXHR, textStatus, errorThrown){
				console.log('error making makeAggregateCompanyDataCall');
				console.log('fail');
				console.log('sta', textStatus);
				console.log(errorThrown);
				console.log(jqXHR);
			});
			// UTIL.handleAjaxRequest(
			// 	endpoint,
			// 	postType,
			// 	params, 
			// 	undefined, 
			// 	function(data){
			// 		ALERTS.displayListCompanies(data.companyPrice);
			// 		self._hideLoading();
			// 		PAGE.resizeIframeSimple();
			// 	}, 
			// 	function(jqXHR, textStatus, errorThrown){
			// 		console.log('fail');
			// 		console.log('sta', textStatus);
			// 		console.log(errorThrown);
			// 		console.log(jqXHR);
			// 	},'jsonpCallback');
		},

		editWLNameSubmit: function(){
			var endpoint = PAGE.fqdn + "/sgx/watchlist/rename";
			var postType = 'POST';
    		var params = { "watchlistName": ALERTS.editWLName(), "id": ALERTS.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					ALERTS.finalWL(data.sort(sortByName));
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);
				//Clears add WL after submit
				ALERTS.newWLName(null);
				ALERTS.showChange(false);
			
		},
		confirmDelete: function(){
			var deleteName = ALERTS.editWLName();
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + deleteName +'?</p> <div calss="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				ALERTS.deleteWatchlist();
				$('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });		
		},		
		deleteWatchlist: function(){			
			var endpoint = PAGE.fqdn + "/sgx/watchlist/delete";
			var postType = 'POST';
    		var params = { "message": ALERTS.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			PAGE.showLoading();
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					PAGE.hideLoading();
					ALERTS.finalWL(data.sort(sortByName));
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);			
		},
		deleteCompany: function(data){

			ALERTS.skipUpdateCompanies(true); //because saveWatchLists also updates the companies display
			ALERTS.companies.remove(data.ticker);
			ALERTS.saveWatchlist();
			PAGE.resizeIframeSimple();
		},
		addCompany: function(data){
			if (ALERTS.companies().length >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You have reached the maximum number of companies that can be included in a watch list.</p>', width: 300 }); return; }

			if ($.inArray( data.tickerCode, ALERTS.companies() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>This company already exists in this watch list.</p>', width: 600 }); return; }
			//PAGE.showLoading();
			ALERTS.skipUpdateCompanies(true);
			ALERTS.companies.push(data.tickerCode);
			ALERTS.saveWatchlist();
			//setTimeout(function(){ ALERTS.displayWatchlists(); PAGE.hideLoading();}, 500);
			//PAGE.resizeIframeSimple();
			
		},
		searchCompanies: function(){

			//noop

		},

		sendEmail: function() {
			var endpoint = PAGE.fqdn + "/sgx/watchlist/sendEmail";
			var postType = 'POST';
    		var params = {};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);
		},

		clearWatchListErrors: function() {
			$('.error-messages').empty();
		},
		
		clearWatchListErrors: function() {
			$('.error-messages').empty();
		},
		

		saveWatchlist: function(){
			var displayMessage = ALERTS.messages.messages[0];
			PAGE.showLoading();
			var errors = 0;
			var pcPriceDropError = 0;
			var pcTradingVolumeError = 0;
			var estChangePriceDropError = 0;
			
			if(ALERTS.displayList().optionList.pcPriceDrop == true){
				if ((ALERTS.displayList().optionList.pcPriceDropBelow == null || ALERTS.displayList().optionList.pcPriceDropBelow == '') || (ALERTS.displayList().optionList.pcPriceRiseAbove == null || ALERTS.displayList().optionList.pcPriceRiseAbove == '')){						
					$('.price-drop.error-messages').empty();
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.price-drop.error-messages');						
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				} else {
					$('.price-drop.error-messages').empty();
					pcPriceDropError = 0;
				}
				
			}
			
			if(ALERTS.displayList().optionList.pcTradingVolume == true){
				if (ALERTS.displayList().optionList.pcTradingVolumeValue == null || ALERTS.displayList().optionList.pcTradingVolumeValue == ''){
					$('.trade-volume.error-messages').empty();						
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.trade-volume.error-messages');						
					PAGE.resizeIframeSimple();
					pcTradingVolumeError = 1;
				}else {
					$('.trade-volume.error-messages').empty();
					pcTradingVolumeError = 0;
				}
				
			}
			
			if(ALERTS.displayList().optionList.estChangePriceDrop == true){
				if ((ALERTS.displayList().optionList.estChangePriceDropBelow == null) || (ALERTS.displayList().optionList.estChangePriceDropBelow == '') || (ALERTS.displayList().optionList.estChangePriceDropAbove == null || ALERTS.displayList().optionList.estChangePriceDropAbove == '')){						
					$('.target-price.error-messages').empty();
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.target-price.error-messages');						
					PAGE.resizeIframeSimple();
					estChangePriceDropError = 1;
					
				}else {
					$('.target-price.error-messages').empty();
					estChangePriceDropError = 0;
				}
				
			}
			
			errors = pcPriceDropError + pcTradingVolumeError + estChangePriceDropError;
			
			if (errors > 0) { return; }
			
			var endpoint = PAGE.fqdn + "/sgx/watchlist/edit";
			var postType = 'POST';
    		var params = {
				"id": ALERTS.selectedValue(),
				"name": ALERTS.editWLName(),
				"companies": ALERTS.companies(),
				"optionList": {
					"pcPriceDrop": (ALERTS.displayList().optionList.pcPriceDrop != undefined) ? ALERTS.displayList().optionList.pcPriceDrop : false,
					"pcPriceDropBelow": (ALERTS.displayList().optionList.pcPriceDropBelow != null) ? ALERTS.displayList().optionList.pcPriceDropBelow : null,
					"pcPriceRiseAbove": (ALERTS.displayList().optionList.pcPriceRiseAbove != null) ? ALERTS.displayList().optionList.pcPriceRiseAbove : null,
					"pcTradingVolume": (ALERTS.displayList().optionList.pcTradingVolume != undefined) ? ALERTS.displayList().optionList.pcTradingVolume : false,
					"pcTradingVolumeValue": (ALERTS.displayList().optionList.pcTradingVolumeValue != null) ? ALERTS.displayList().optionList.pcTradingVolumeValue : null,
					"pcReachesWeek": (ALERTS.displayList().optionList.pcReachesWeek != undefined) ? ALERTS.displayList().optionList.pcReachesWeek : false,
					"pcReachesWeekValue": ALERTS.displayList().optionList.pcReachesWeekValue,
					"estChangePriceDrop": (ALERTS.displayList().optionList.estChangePriceDrop != undefined) ? ALERTS.displayList().optionList.estChangePriceDrop : false,
					"estChangePriceDropBelow": (ALERTS.displayList().optionList.estChangePriceDropBelow != null) ? ALERTS.displayList().optionList.estChangePriceDropBelow : null,
					"estChangePriceDropAbove": (ALERTS.displayList().optionList.estChangePriceDropAbove != null) ? ALERTS.displayList().optionList.estChangePriceDropAbove : null,
					"estChangeConsensus": (ALERTS.displayList().optionList.estChangeConsensus != undefined) ? ALERTS.displayList().optionList.estChangeConsensus : false,
					"estChangeConsensusValue": ALERTS.displayList().optionList.estChangeConsensusValue,
					"kdAnounceCompTransactions": (ALERTS.displayList().optionList.kdAnounceCompTransactions != undefined) ? ALERTS.displayList().optionList.kdAnounceCompTransactions : false,
					"kdCompanyForecasts": (ALERTS.displayList().optionList.kdCompanyForecasts != undefined) ? ALERTS.displayList().optionList.kdCompanyForecasts : false,
					"kdCorporateStructureRelated": (ALERTS.displayList().optionList.kdCorporateStructureRelated != undefined) ? ALERTS.displayList().optionList.kdCorporateStructureRelated : false,
					"kdCustProdRelated": (ALERTS.displayList().optionList.kdCustProdRelated != undefined) ? ALERTS.displayList().optionList.kdCustProdRelated : false,
					"kdDividensSplits": (ALERTS.displayList().optionList.kdDividensSplits != undefined) ? ALERTS.displayList().optionList.kdDividensSplits : false,
					"kdListTradeRelated": (ALERTS.displayList().optionList.kdListTradeRelated != undefined) ? ALERTS.displayList().optionList.kdListTradeRelated : false,
					"kdPotentialRedFlags": (ALERTS.displayList().optionList.kdPotentialRedFlags != undefined) ? ALERTS.displayList().optionList.kdPotentialRedFlags : false,
					"kdPotentialTransactions": (ALERTS.displayList().optionList.kdPotentialTransactions != undefined) ? ALERTS.displayList().optionList.kdPotentialTransactions : false,
					"kdResultsCorpAnnouncements": (ALERTS.displayList().optionList.kdResultsCorpAnnouncements != undefined) ? ALERTS.displayList().optionList.kdResultsCorpAnnouncements : false
				}			
			};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					$('.save').remove();
					$('<div class="save">Your changes have been saved.</div>').insertBefore('header.header').delay(4000).fadeOut(function() {$(this).remove();});
					PAGE.hideLoading();
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					ALERTS.finalWL(data.sort(sortByName));
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);
		}

	};
	
	return ALERTS;
	
});