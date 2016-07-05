define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json","jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {

	var KEYDEV = {
		finalWL: ko.observableArray(),
		watchlistCompanies:ko.observableArray(),
		showChange: ko.observable(false),
		editWLName: ko.observable(),
		newWLName: ko.observable(),
		premiumUser: ko.observable(),
		selectedValue: ko.observable(),
		keydevCompSelectedVal: ko.observable(),
		addWatchlistName: ko.observableArray(),
		messages: JSON.parse(MESSAGES),
		allCompanies : [],
		companyNameAndTickerList : [],
		
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(),
		premiumUserEmail: ko.observable(),
		currentDay: ko.observable(),
		
		kdAnounceCompTransactions: ko.observableArray(),//Announced/Completed Transactions
		kdCompanyForecasts: ko.observableArray(),//Company Forecasts and Ratings
		kdCorporateStructureRelated: ko.observableArray(),//Corporate Structure Related
		kdCustProdRelated: ko.observableArray(),//Customer/Product Related
		kdDividensSplits: ko.observableArray(),//Dividends/Splits
		kdListTradeRelated: ko.observableArray(),//Listing/Trading Related
		kdPotentialRedFlags: ko.observableArray(),//Potential Red Flags/Distress Indicators
		kdPotentialTransactions: ko.observableArray(),//Potential Transactions
		kdResultsCorpAnnouncements: ko.observableArray(),//Results and Corporate Announcements
		
		
		initPage: function() {
			var me = this;
			
			ko.applyBindings(me, $("body")[0]);
			
			//To get the user status premium/non
			PAGE.checkStatus();
			
			//To get the select box for the currency change in the login bar
			PAGE.libCurrency(true);
			
			me.makeAggregateCompanyDataCall(me);
		},
		
		makeAggregateCompanyDataCall: function(me){
			PAGE.showLoading();
			var endpoint = me.fqdn+'/sgx/company/names';
			$.getJSON(endpoint+"?callback=?").done(function(data){
				me.companyNameAndTickerList = data.companyNameAndTickerList;
				me.getWatchListData(me); 
				PAGE.hideLoading();
			}).fail(function(jqXHR, textStatus, errorThrown){
				console.log('error making makeAggregateCompanyDataCall');
			});
		},

		getWatchListData: function(me) {
			var displayMessage = me.messages.messages[0];
			var endpoint = me.fqdn + "/sgx/watchlist/get";
			var postType = 'POST';
			var params = {};
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params, 
				function(data, textStatus, jqXHR){
				    	if(!data.watchlists){
				    	    console.log('Watchlists unavailable');
				    	    return;
				    	}
					me.finalWL(data.watchlists.sort(sortByName));
					function sortByName(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					me.selectedValue(UTIL.getParameterByName("code"));
					var arr = data.removed;					
					var removedTicker = arr.join(', ');
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your StockLists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
					}
				},PAGE.customSGXError
			);
			
			me.selectedValue.subscribe(function(data){
				var watchlists = this.finalWL();
				var keyDevFlag = true;
				for(var i = 0, len = watchlists.length; i < len; i++) {
					var wl = watchlists[i]
					if( wl.id == data) {
						me.allCompanies = wl.companies;
						me.clearWatchListErrors();
						me.editWLName(wl.name);
						me.populateWatchlistCompanies(wl, me);
						break;
					}
				}
				
				if(keyDevFlag && !UTIL.isEmpty(me.allCompanies)){
					keyDevFlag = false;
					me.getKeyDevData(me, me.allCompanies);
				}
				
			}, me);
			
			ko.validation.init({insertMessages: false});
			me.newWLName.extend({
					minLength: { params: 2, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
			});

			me.wlNameError = ko.validation.group(me.newWLName);  //grouping error for wlName only
			me.errors = ko.validation.group(me);			
			me.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});
			
			return me;
		},
		
		searchKeyDev: function(){
			var me = this;
			var tickers = me.allCompanies; 
			if(!UTIL.isEmpty(me.keydevCompSelectedVal())){
				tickers = $.makeArray(me.keydevCompSelectedVal());
			}
			me.getKeyDevSearchData(me, tickers);
		},
		
		getKeyDevSearchData: function(me, tickerCodes){
			PAGE.showLoading();
	    	var endpoint = PAGE.fqdn + "/sgx/search/stockListKeydevs";
			var postType = 'POST';
    	    var params = {'tickerCodes' : tickerCodes};
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){					
						console.log(data);
						me.refractKeyDevData(data, me);
						me.showHideCheckboxes(me);
						PAGE.hideLoading();
					}, 
					PAGE.customSGXError);
		},
		
		showHideCheckboxes: function(me){
			me.showHideCheckboxeRenderer($('#kdAnounceCompTransactionsCheckbox'), $('#kdAnounceCompTransactionsId') );
			me.showHideCheckboxeRenderer($('#kdCompanyForecastsCheckbox'), $('#kdCompanyForecastsId') );
			me.showHideCheckboxeRenderer($('#kdCorporateStructureRelatedCheckbox'), $('#kdCorporateStructureRelatedId') );
			me.showHideCheckboxeRenderer($('#kdCustProdRelatedCheckbox'), $('#kdCustProdRelatedId') );
			me.showHideCheckboxeRenderer($('#kdDividensSplitsCheckbox'), $('#kdDividensSplitsId') );
			me.showHideCheckboxeRenderer($('#kdListTradeRelatedCheckbox'), $('#kdListTradeRelatedId') );
			me.showHideCheckboxeRenderer($('#kdPotentialRedFlagsCheckbox'), $('#kdPotentialRedFlagsId') );
			me.showHideCheckboxeRenderer($('#kdPotentialTransactionsCheckbox'), $('#kdPotentialTransactionsId') );
			me.showHideCheckboxeRenderer($('#kdResultsCorpAnnouncementsCheckbox'), $('#kdResultsCorpAnnouncementsId') );
		},
		
		showHideCheckboxeRenderer: function(idCheckbox, idDiv){
			if(idCheckbox.prop('checked') === true){
				idDiv.show();
	        }else{
	        	idDiv.hide();
	        }
		},
		
		getKeyDevData: function(me, tickerCodes){
	    	var endpoint = PAGE.fqdn + "/sgx/search/stockListKeydevs";
			var postType = 'POST';
    	    var params = {'tickerCodes' : tickerCodes};
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){					
						console.log(data);
						me.refractKeyDevData(data, me);
						$('#allKeyDevDivId').show();
					}, 
					PAGE.customSGXError);
		},
		
		refractKeyDevData: function(data, me){
			me.kdAnounceCompTransactions.removeAll();
			me.kdCompanyForecasts.removeAll();
			me.kdCorporateStructureRelated.removeAll();
			me.kdCustProdRelated.removeAll();
			me.kdDividensSplits.removeAll();
			me.kdListTradeRelated.removeAll();
			me.kdPotentialRedFlags.removeAll();
			me.kdPotentialTransactions.removeAll();
			me.kdResultsCorpAnnouncements.removeAll();
			
			for(i in data){
				me.addKeyDevCompanies(data[i], me);
				if(i === "kdAnounceCompTransactions"){
					me.kdAnounceCompTransactions(data[i]);
				}else if(i === "kdCompanyForecasts"){
					me.kdCompanyForecasts(data[i]);
				}else if(i === "kdCorporateStructureRelated"){
					me.kdCorporateStructureRelated(data[i]);
				}else if(i === "kdCustProdRelated"){
					me.kdCustProdRelated(data[i]);
				}else if(i === "kdDividensSplits"){
					me.kdDividensSplits(data[i]);
				}else if(i === "kdListTradeRelated"){
					me.kdListTradeRelated(data[i]);
				}else if(i === "kdPotentialRedFlags"){
					me.kdPotentialRedFlags(data[i]);
				}else if(i === "kdPotentialTransactions"){
					me.kdPotentialTransactions(data[i]);
				}else if(i === "kdResultsCorpAnnouncements"){
					me.kdResultsCorpAnnouncements(data[i]);
				}
			}
		},
		
		addKeyDevCompanies: function(data, me){
			$.each(data, function (index, item) {
				  var companies = [];
				  $.each(item.tickerCodes, function (index, value) {
					  $.each(me.companyNameAndTickerList, function (index, record) {
						  if(value === record.tickerCode){
							  companies.push(record.companyName);
						  }
					  });
				  });
				  item ["companies"] = companies;
			});
		},
				
		addWatchlist: function(){
			var me = this;
			var newWLNameLC = me.newWLName();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/create";
			var postType = 'POST';
    	    var params = { "message": newWLNameLC };
			var wlLength = me.finalWL().length;
			
			me.addWatchlistName([]);
			$.each(me.finalWL(), function(i, data){
				me.addWatchlistName.push(data.name.toLowerCase());
			});
			
			if (me.wlNameError().length != 0) return;
			if (newWLNameLC.trim()==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watchlist name is empty.</p>', width: 600 }); return; }
			if ($.inArray( newWLNameLC.toLowerCase().trim(), me.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return; }
			if (wlLength >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 StockLists.</p>', width: 600 }); return; }
			
			PAGE.showLoading();
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){					
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					PAGE.hideLoading();
					me.finalWL(data.sort(sortByName));
					
					$.each(data, function(i,data){
						if (data.name == newWLNameLC){
							me.selectedValue(data.id);
						}						
					});
				}, 
				PAGE.customSGXError);	
			
			
			//Clears add WL after submit
			me.newWLName(null);
		},

		editWLNameSubmit: function(){
			var me=this;
			var editedName = me.editWLName().trim();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/rename";
			var postType = 'POST';
			var params = { "watchlistName": editedName, "id": me.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			
			if (editedName ==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watchlist name is empty.</p>', width: 600 }); return; }
			if ($.inArray( editedName.toLowerCase(), me.addWatchlistName() ) != -1) { PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return;  }
			
			me.addWatchlistName([]);
			$.each(me.finalWL(), function(i, data){
				me.addWatchlistName.push(data.name.toLowerCase());
			});	
    		
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
					me.finalWL(data.sort(sortByName));
				}, 
				PAGE.customSGXError,
				jsonpCallback
			);
			
				//Clears add WL after submit
			me.newWLName(null);
			me.showChange(false);
		},
		
		confirmDelete: function(){
			var me = this;
			var deleteName = me.editWLName();
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + deleteName +'?</p> <div class="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				me.deleteWatchlist();
				$('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });		
		},		
		
		deleteWatchlist: function(){			
			var me = this;
			var endpoint = PAGE.fqdn + "/sgx/watchlist/delete";
			var postType = 'POST';
			var params = { "message": me.selectedValue()};
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
					me.finalWL(data.sort(sortByName));
				}, 
				PAGE.customSGXError,
				undefined
			);			
		},
		
		populateWatchlistCompanies:function(watchlistObject, me){
		    // JSON Call for populating the companies for the selected watchlist.
			if (Object.prototype.toString.call(watchlistObject.companies) == '[object Array]' && watchlistObject.companies.length == 0){ 
				    $('#watchlistCompaniesSelect').empty()
				return;
			}

		    var endpoint = PAGE.fqdn+"/sgx/price/companyPrices";
		    var params = { "companies": watchlistObject.companies };
		    var postType = 'POST';
		    $.getJSON(endpoint+"?callback=?", { 'json': JSON.stringify(params) }).done(function(data){
				me.watchlistCompanies(data.companyPrice);

			}).fail(function(jqXHR, textStatus, errorThrown){
				console.log('error making service call');
			});

		},
		
		clearWatchListErrors: function() {
			$('.error-messages').empty();
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
		
	};
	
	return KEYDEV;
	
});