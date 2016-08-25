define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "client/modules/tearsheet", "text!client/data/watchlists/alerts.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, TS, AL) {

	ko.validation = validation;
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
		PCPriceCheck: ko.observable(),
		PCTradeVol: ko.observable(),
		ESTChangePrice: ko.observable(),
		defaultSearch: "",
		
		initPage: function() {
			///
			$("<div />").addClass('right-this').appendTo(".header .login-bar.premium");
			var self = this;
			var endpoint = this.fqdn+'/sgx/company/names';
			
			function makeAggregateCompanyDataCall() {

				$.getJSON(endpoint+"?callback=?").done(function(data){
					self.allCompanies = data.companyNameAndTickerList;
					self.searchReady(true);
					PAGE.resizeIframeSimple();

				}).fail(function(jqXHR, textStatus, errorThrown){
					console.log('error making makeAggregateCompanyDataCall');
				});

			}

			var self = this;
			
			PAGE.checkStatus();
			PAGE.libCurrency(true);
			
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
			
			$("<div />").addClass('right-this').appendTo(".header .login-bar.premium");
			$('<span/>').addClass('currency').text('Currency').appendTo(".login-bar.premium .right");
			$('<span/>').addClass('currency-select').text('Singapore Dollar').appendTo(".login-bar.premium .right");
			
			PAGE.showLoading();
			var displayMessage = ALERTS.messages.messages[0];
			var endpoint = me.fqdn + "/sgx/watchlist/get";
			var postType = 'POST';
    	var params = {};
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params, 
				function(data, textStatus, jqXHR){
					PAGE.hideLoading();
					ALERTS.finalWL(data.watchlists.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
						}));
					
					var watchlists = ALERTS.finalWL();
					var code = UTIL.getParameterByName("code");
					if(code.length < 1 && ALERTS.finalWL().length > 0){
						code = ALERTS.finalWL()[0].id;
					}
					ALERTS.selectedValue(code);
					callback();
					for(var i = 0, len = watchlists.length; i < len; i++) {
					var wl = watchlists[i];
					if( wl.id == code) {
							ALERTS.companies(wl.companies);
							ALERTS.displayList(wl);
							ALERTS.clearWatchListErrors();
							ALERTS.editWLName(wl.name);			
							break;
						}
					}				
					
					$.each($('.alerts input[type=text]'),function(){
						if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
					});
					
					$('.alerts input[type=text]').change(function(){
						if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
					});
					
					if(ALERTS.displayList()){
						ALERTS.PCPriceCheck(ALERTS.displayList().optionList.pcPriceDrop);
						ALERTS.PCTradeVol(ALERTS.displayList().optionList.pcTradingVolume);
						ALERTS.ESTChangePrice(ALERTS.displayList().optionList.estChangePriceDrop);
					}
					
					ALERTS.PCPriceCheck.subscribe(function (i,v) {
						if (ALERTS.PCPriceCheck() == false){
							$('.pcPriceDropBelow').val(null).removeClass('percent');
							$('.pcPriceRiseAbove').val(null).removeClass('percent');
						};
				    }, this);
					
					
					ALERTS.PCTradeVol.subscribe(function (i,v) {
						if (ALERTS.PCTradeVol() == false){
							$('.pcTradingVolumeValue').val(null).removeClass('percent');
						};
				    }, this);
					
					
					ALERTS.ESTChangePrice.subscribe(function (i,v) {
						if (ALERTS.ESTChangePrice() == false){
							$('.estChangePriceDropBelow').val(null).removeClass('percent');
							$('.estChangePriceDropAbove').val(null).removeClass('percent');
						};
				    }, this);
					
					var arr = data.removed;					
					var removedTicker = arr.join(', ');
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your StockLists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
					}
					
				}, 
				PAGE.customSGXError);
			
			//Alerts select lists
			this.weeks(JSON.parse(AL).alerts[0].weeks);
			this.consensusRec(JSON.parse(AL).alerts[0].consensusRec);
			this.actualEstimates(JSON.parse(AL).alerts[0].actualEstimates);

			me.searchInput.subscribe(function(data){
				ALERTS.showChange(false);
				//ratelimiting the search 200ms
				if(this._localTimeout) {
					clearTimeout(this._localTimeout);
					delete this._localTimeout;
				}

				var resultArray;
				if(data.length == 0) {
					me.searchResults.removeAll();
					PAGE.resizeIframeSimple(100);
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

				setTimeout(function(){ PAGE.resizeIframeSimple(100) }, 500);

			});
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);	
			

			me.trackPage("SGX - StockList Alerts");
    		
						$("<div />").addClass('right-this').appendTo(".header .login-bar.premium");

			
    	ko.validation.init({insertMessages: false});
			
			ALERTS.newWLName
				.extend({
					minLength: { params: 2, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
				});


			this.wlNameError = ko.validation.group(ALERTS.newWLName);  //grouping error for wlName only

			ALERTS.editWLName
			.extend({
				minLength: { params: 2, message: displayMessage.watchlist.error },
				maxLength: { params: 40, message: displayMessage.watchlist.error }
			});

			this.editWLNameError = ko.validation.group(ALERTS.editWLName);  //grouping error for editWLName only

			this.errors = ko.validation.group(this);			
			
			this.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});

			ALERTS.companies.subscribe(function(data){
				setTimeout(function(){ ALERTS.displayWatchlists(data); }, 400);
			});

			
			return this;
			
		},
		
		watchListChange: function(data, event){

			var watchlists = this.finalWL();

			for(var i = 0, len = watchlists.length; i < len; i++) {
				var wl = watchlists[i]
				if( wl.id == data.selectedValue()) {
					ALERTS.companies(wl.companies);
					ALERTS.displayList(wl);
					ALERTS.clearWatchListErrors();
					ALERTS.editWLName(wl.name);			
					break;
				}
			}				
			
			ALERTS.searchInput("");
			
			$.each($('.alerts input[type=text]'),function(){
				if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
			});
			
			$('.alerts input[type=text]').change(function(){
				if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
			});
			
			ALERTS.PCPriceCheck(ALERTS.displayList().optionList.pcPriceDrop);
			
			ALERTS.PCPriceCheck.subscribe(function (i,v) {
				if (ALERTS.PCPriceCheck() == false){
					$('.pcPriceDropBelow').val(null).removeClass('percent');
					$('.pcPriceRiseAbove').val(null).removeClass('percent');
				};
		    }, this);
			
			ALERTS.PCTradeVol(ALERTS.displayList().optionList.pcTradingVolume);
			
			ALERTS.PCTradeVol.subscribe(function (i,v) {
				if (ALERTS.PCTradeVol() == false){
					$('.pcTradingVolumeValue').val(null).removeClass('percent');
				};
		    }, this);
			
			ALERTS.ESTChangePrice(ALERTS.displayList().optionList.estChangePriceDrop);
			
			ALERTS.ESTChangePrice.subscribe(function (i,v) {
				if (ALERTS.ESTChangePrice() == false){
					$('.estChangePriceDropBelow').val(null).removeClass('percent');
					$('.estChangePriceDropAbove').val(null).removeClass('percent');
				};
		    }, this);
			
			ALERTS.showChange(false);
			PAGE.resizeIframeSimple();
		},
		
		init_nonPremium: function() {
            $('#alerts-content-alternative').show();
        },
		
		addWatchlist: function(){
			ALERTS.showChange(false);
			if(this.wlNameError().length != 0) return;
			var wlLength = ALERTS.finalWL().length;
			ALERTS.addWatchlistName([]);

			$.each(ALERTS.finalWL(), function(i, data){
				ALERTS.addWatchlistName.push(data.name.toLowerCase());
			});			
			
			var newWLNameLC = ALERTS.newWLName();
			
			if (newWLNameLC.trim()==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name is empty.</p>', width: 600 }); return; }
			if ($.inArray( newWLNameLC.toLowerCase().trim(), ALERTS.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name already exists.</p>', width: 600 }); return; }
			
			if (wlLength >= 10) { ALERTS.showChange(false);PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 StockLists.</p>', width: 600 }); return; }

			var endpoint = PAGE.fqdn + "/sgx/watchlist/create";
			var postType = 'POST';
			var params = { "message": ALERTS.newWLName() };
			PAGE.showLoading();
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){					
					PAGE.hideLoading();
					ALERTS.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
					
					$.each(data, function(i,data){
						if (data.name == newWLNameLC){
							//ALERTS.saveWatchlist();
							ALERTS.selectedValue(data.id);
							
							var watchlists = ALERTS.finalWL();
							
							for(var i = 0, len = watchlists.length; i < len; i++) {
								var wl = watchlists[i]
								if( wl.id == data.id) {
									ALERTS.companies(wl.companies);
									ALERTS.displayList(wl);
									ALERTS.clearWatchListErrors();
									ALERTS.editWLName(wl.name);			
									break;
								}
							}				
							
							$.each($('.alerts input[type=text]'),function(){
								if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
							});
							
							$('.alerts input[type=text]').change(function(){
								if ($(this).val() == '') { $(this).removeClass('percent') } else { $(this).addClass('percent'); }
							});
							
							ALERTS.PCPriceCheck(ALERTS.displayList().optionList.pcPriceDrop);
							
							ALERTS.PCPriceCheck.subscribe(function (i,v) {
								if (ALERTS.PCPriceCheck() == false){
									$('.pcPriceDropBelow').val(null).removeClass('percent');
									$('.pcPriceRiseAbove').val(null).removeClass('percent');
								};
						    }, this);
							
							ALERTS.PCTradeVol(ALERTS.displayList().optionList.pcTradingVolume);
							
							ALERTS.PCTradeVol.subscribe(function (i,v) {
								if (ALERTS.PCTradeVol() == false){
									$('.pcTradingVolumeValue').val(null).removeClass('percent');
								};
						    }, this);
							
							ALERTS.ESTChangePrice(ALERTS.displayList().optionList.estChangePriceDrop);
							
							ALERTS.ESTChangePrice.subscribe(function (i,v) {
								if (ALERTS.ESTChangePrice() == false){
									$('.estChangePriceDropBelow').val(null).removeClass('percent');
									$('.estChangePriceDropAbove').val(null).removeClass('percent');
								};
						    }, this);
							
							
							PAGE.resizeIframeSimple();
						}						
					});
				}, 
				PAGE.customSGXError);	
			
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
			var postType = 'POST';
			var params = { "companies": data };

			$.getJSON(endpoint+"?callback=?", { 'json': JSON.stringify(params) })
			//$.getJSON(endpoint, JSON.stringify(params) )

			.done(function(data){
				ALERTS.displayListCompanies(data.companyPrice);
				self._hideLoading();
				setTimeout(function(){ PAGE.resizeIframeSimple() }, 500);

			}).fail(PAGE.customSGXError);
		},

		editWLNameSubmit: function(){
			if(this.editWLNameError().length != 0) return;

			var endpoint = PAGE.fqdn + "/sgx/watchlist/rename";
			var postType = 'POST';
			
			ALERTS.addWatchlistName([]);

			$.each(ALERTS.finalWL(), function(i, data){
				ALERTS.addWatchlistName.push(data.name.toLowerCase());
			});		
			
			var editedName = ALERTS.editWLName().trim();
			if (editedName ==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name is empty.</p>', width: 600 }); return; }
			if ($.inArray( editedName.toLowerCase(), ALERTS.addWatchlistName() ) != -1) { PAGE.modal.open({ type: 'alert',  content: '<p>StockList name already exists.</p>', width: 600 }); return;  }
			
			
    		var params = { "watchlistName": ALERTS.editWLName(), "id": ALERTS.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					ALERTS.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
				}, 
				PAGE.customSGXError,
				jsonpCallback);
				//Clears add WL after submit
				ALERTS.newWLName(null);
				ALERTS.showChange(false);
			
		},
		confirmDelete: function(){
			var deleteName = ALERTS.editWLName();
			var stockListName = $("#stockListSelect option:selected").text();
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + stockListName +'?</p> <div calss="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				ALERTS.deleteWatchlist();
				$('.cboxWrapper').colorbox.close();
				ALERTS.showChange(false);
	        });
			
			 $('.cancel').click(function(e) {
				 ALERTS.showChange(false);
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
					PAGE.hideLoading();
					ALERTS.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
				}, 
				PAGE.customSGXError,
				undefined);			
		},
		deleteCompany: function(data){
			ALERTS.showChange(false);
			PAGE.modal.open({ content: '<p>By deleting this company from your StockList, you will also be removing any associated transactions.</p> <div calss="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				 var deletecompaniesObservableList = ALERTS.companies;
				 deletecompaniesObservableList.remove(data.ticker);
				 ALERTS.saveWatchlist( function(){deletecompaniesObservableList });
				 PAGE.resizeIframeSimple();
				 $('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });
		},

		addCompany: function(data){
			ALERTS.showChange(false);
			if (ALERTS.companies().length >= 25) { PAGE.modal.open({ type: 'alert',  content: '<p>You have reached the maximum number of companies that can be included in a StockList.</p>', width: 300 }); return; }
			if ($.inArray( data.tickerCode, ALERTS.companies() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>This company already exists in this StockList.</p>', width: 600 }); return; }
			
			//callback to update companies after the call succeeds.
			var addcompaniesObservableList = ALERTS.companies;
			addcompaniesObservableList.push(data.tickerCode);
			ALERTS.saveWatchlist( function(){ addcompaniesObservableList });
			
		},
		searchCompanies: function(){
			ALERTS.showChange(false);
			//noop

		},

		clearWatchListErrors: function() {
			$('.error-messages').empty();
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
					//console.log('fail');
					//console.log('sta', textStatus);
					//console.log(errorThrown);
					//console.log(jqXHR);
					alert('Email Sent');
				},jsonpCallback);
		},
		

		saveWatchlist: function(callback){
			ALERTS.showChange(false);
			var displayMessage = ALERTS.messages.messages[0];
			var errors = 0;
			var pcPriceDropError = 0;
			var pcTradingVolumeError = 0;
			var estChangePriceDropError = 0;
			var re = RegExp('^\\d*(\\.\\d{1,3})?$');
			
			if(ALERTS.PCPriceCheck() == true){
				ALERTS.displayList().optionList.pcPriceDropBelow = $('.pcPriceDropBelow').val();
				ALERTS.displayList().optionList.pcPriceRiseAbove = $('.pcPriceRiseAbove').val();
				if ((ALERTS.displayList().optionList.pcPriceDropBelow == null || ALERTS.displayList().optionList.pcPriceDropBelow == '') || (ALERTS.displayList().optionList.pcPriceRiseAbove == null || ALERTS.displayList().optionList.pcPriceRiseAbove == '')){
					$('.price-drop.error-messages').empty();
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.price-drop.error-messages');
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				} 
				else if (isNaN(ALERTS.displayList().optionList.pcPriceDropBelow) || isNaN(ALERTS.displayList().optionList.pcPriceRiseAbove) ) {
					$('.price-drop.error-messages').empty();
					$('<p/>').html('Please enter valid numbers.').appendTo('.price-drop.error-messages');
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				}
				else if (!ALERTS.displayList().optionList.pcPriceDropBelow.match(re) || !ALERTS.displayList().optionList.pcPriceRiseAbove.match(re) ||
						  ALERTS.displayList().optionList.pcPriceDropBelow < .001 || ALERTS.displayList().optionList.pcPriceRiseAbove < .001 || ALERTS.displayList().optionList.pcPriceDropBelow > 100 || ALERTS.displayList().optionList.pcPriceRiseAbove > 100 ) {
					$('.price-drop.error-messages').empty();
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.price-drop.error-messages');
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				}
				else {
					$('.price-drop.error-messages').empty();
					pcPriceDropError = 0;
				}
				
			} 
			else {
				$('.price-drop.error-messages').empty();
			}
			
			if(ALERTS.PCTradeVol() == true){
				ALERTS.displayList().optionList.pcTradingVolumeValue = $('.pcTradingVolumeValue').val();
				if (ALERTS.displayList().optionList.pcTradingVolumeValue == null || ALERTS.displayList().optionList.pcTradingVolumeValue == ''){
					$('.trade-volume.error-messages').empty();						
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.trade-volume.error-messages');						
					PAGE.resizeIframeSimple();
					pcTradingVolumeError = 1;
				} 
				else if (isNaN(ALERTS.displayList().optionList.pcTradingVolumeValue) ) {
					$('.trade-volume.error-messages').empty();
					$('<p/>').html('Please enter valid numbers.').appendTo('.trade-volume.error-messages');
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				}
				else if (!ALERTS.displayList().optionList.pcTradingVolumeValue.match(re) || ALERTS.displayList().optionList.pcTradingVolumeValue < .001 ||  ALERTS.displayList().optionList.pcTradingVolumeValue > 100) {
					$('.trade-volume.error-messages').empty();
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.trade-volume.error-messages');
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				}
				else {
					$('.trade-volume.error-messages').empty();
					pcTradingVolumeError = 0;
				}
				
			} 				
			else {
				$('.trade-volume.error-messages').empty();
			}
			
			if(ALERTS.ESTChangePrice() == true){
				ALERTS.displayList().optionList.estChangePriceDropBelow = $('.estChangePriceDropBelow').val();
				ALERTS.displayList().optionList.estChangePriceDropAbove = $('.estChangePriceDropAbove').val();
				if ((ALERTS.displayList().optionList.estChangePriceDropBelow == null) || (ALERTS.displayList().optionList.estChangePriceDropBelow == '') || (ALERTS.displayList().optionList.estChangePriceDropAbove == null || ALERTS.displayList().optionList.estChangePriceDropAbove == '')){						
					$('.target-price.error-messages').empty();
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.target-price.error-messages');						
					PAGE.resizeIframeSimple();
					estChangePriceDropError = 1;
					
				}
				else if (isNaN(ALERTS.displayList().optionList.estChangePriceDropBelow) || isNaN(ALERTS.displayList().optionList.estChangePriceDropAbove) ) {
					$('.target-price.error-messages').empty();
					$('<p/>').html('Please enter valid numbers.').appendTo('.target-price.error-messages');
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				}
				else if (!ALERTS.displayList().optionList.estChangePriceDropBelow.match(re) || !ALERTS.displayList().optionList.estChangePriceDropAbove.match(re) || ALERTS.displayList().optionList.estChangePriceDropBelow < .001 || ALERTS.displayList().optionList.estChangePriceDropAbove < .001 || ALERTS.displayList().optionList.estChangePriceDropBelow > 100 || ALERTS.displayList().optionList.estChangePriceDropAbove > 100) {
					$('.target-price.error-messages').empty();
					$('<p/>').html(displayMessage.watchlist.blankField).appendTo('.target-price.error-messages');
					PAGE.resizeIframeSimple();
					pcPriceDropError = 1;
				}
				else {
					$('.target-price.error-messages').empty();
					estChangePriceDropError = 0;
				}				
			}
			else {
				$('.target-price.error-messages').empty();
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
					"pcPriceDrop": (ALERTS.PCPriceCheck() == true) ? true : false,
					"pcPriceDropBelow": (ALERTS.PCPriceCheck() == true && ALERTS.displayList().optionList.pcPriceDropBelow != null) ? ALERTS.displayList().optionList.pcPriceDropBelow : null,
					"pcPriceRiseAbove": (ALERTS.PCPriceCheck() == true && ALERTS.displayList().optionList.pcPriceRiseAbove != null) ? ALERTS.displayList().optionList.pcPriceRiseAbove : null,
					"pcTradingVolume": (ALERTS.PCTradeVol() == true) ? true : false,
					"pcTradingVolumeValue": (ALERTS.PCTradeVol() == true && ALERTS.displayList().optionList.pcTradingVolumeValue != null) ? ALERTS.displayList().optionList.pcTradingVolumeValue : null,
					"pcReachesWeek": (ALERTS.displayList().optionList.pcReachesWeek != undefined) ? ALERTS.displayList().optionList.pcReachesWeek : false,
					"pcReachesWeekValue": ALERTS.displayList().optionList.pcReachesWeekValue,
					"estChangePriceDrop": (ALERTS.ESTChangePrice() == true) ? true : false,
					"estChangePriceDropBelow": (ALERTS.ESTChangePrice() == true && ALERTS.displayList().optionList.estChangePriceDropBelow != null) ? ALERTS.displayList().optionList.estChangePriceDropBelow : null,
					"estChangePriceDropAbove": (ALERTS.ESTChangePrice() == true && ALERTS.displayList().optionList.estChangePriceDropAbove != null) ? ALERTS.displayList().optionList.estChangePriceDropAbove : null,
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
			PAGE.showLoading();
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					$('.save').remove();
					$('<div class="save">Your changes have been saved.</div>').insertBefore('header.header').delay(4000).fadeOut(function() {$(this).remove();});
					PAGE.hideLoading();
					if( callback && typeof callback === 'function') { callback() };
					ALERTS.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
					var resetAfter = ko.computed(function(){
				if(ALERTS.displayList().optionList.pcPriceDrop == false){
					ALERTS.displayList().optionList.pcPriceDropBelow = null;
					ALERTS.displayList().optionList.pcPriceRiseAbove = null
				}
			});
				}, 
				PAGE.customSGXError,
				jsonpCallback);
		}

	};
	
	return ALERTS;
	
});