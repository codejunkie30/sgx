define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json","jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {

	ko.components.register('premium-preview', { require: 'client/components/premium-preview'});
	
	ko.bindingHandlers.companytext = {
		    init: function (element, valueAccessor, allBindingsAccessor) {
		    	var value = ko.utils.unwrapObservable(valueAccessor());
		    	if(!UTIL.isEmpty(value) && value.length > 1){
		    		$(element).html("Companies:");
		    	}else{
		    		$(element).html("Company:");
		    	}
	    		
		    }
		};
	
	var KEYDEV = {
		sectionName:'Key developments',	
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
		noResultsFlag: true,
		
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
			me.trackPage("SGX - StockList Key Developments");
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
					me.finalWL(data.watchlists.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
					
					me.selectedValue(UTIL.getParameterByName("code"));
					
					var watchlists = me.finalWL();
					for(var i = 0, len = watchlists.length; i < len; i++) {
						var wl = watchlists[i]
						if( wl.id == UTIL.getParameterByName("code")) {
							me.allCompanies = wl.companies;
							me.clearWatchListErrors();
							me.editWLName(wl.name);
							me.populateWatchlistCompanies(wl, me);
							break;
						}
					}
					
					me.getKeyDevData(me, me.allCompanies);
					
					var arr = data.removed;					
					var removedTicker = arr.join(', ');
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your StockLists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
					}
				},PAGE.customSGXError
			);
			
			me.selectedValue.subscribe(function(data){
				
				
			}, me);
			
			ko.validation.init({insertMessages: false});
			me.newWLName.extend({
					minLength: { params: 2, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
			});

			me.wlNameError = ko.validation.group(me.newWLName);  //grouping error for wlName only
			
			me.editWLName
			.extend({
				minLength: { params: 2, message: displayMessage.watchlist.error },
				maxLength: { params: 40, message: displayMessage.watchlist.error }
			});

			this.editWLNameError = ko.validation.group(me.editWLName);  //grouping error for editWLName only
			
			me.errors = ko.validation.group(me);			
			me.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});
			
			return me;
		},
		
		watchListChange: function(data, event){
			var me = this;
			me.watchlistCompanies.removeAll();
			
			var watchlists = this.finalWL();
			for(var i = 0, len = watchlists.length; i < len; i++) {
				var wl = watchlists[i]
				if( wl.id == data.selectedValue()) {
					me.allCompanies = wl.companies;
					me.clearWatchListErrors();
					me.editWLName(wl.name);
					me.populateWatchlistCompanies(wl, me);
					break;
				}
			}
			$('#keyDevelopemntContentDiv input[type=checkbox]').each(function () {
				this.checked = true;
			});
			me.noResultsFlag = true;
			me.getKeyDevData(me, me.allCompanies);
			me.showChange(false);
		},
		
		searchKeyDev: function(){
			var me = this;
			me.showChange(false);
			me.noResultsFlag = true;
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
						if(!$.isEmptyObject(data)){
							$('#noRecordAvail').hide();
						}else{
							$('#noRecordAvail').show();
						}
						me.refractKeyDevData(data, me);
						me.showHideCheckboxes(me);
						if(me.noResultsFlag){
							$('#noRecordAvail').show();
						}else{
							$('#noRecordAvail').hide();
						}
						PAGE.hideLoading();
					}, 
					PAGE.customSGXError);
		},
		
		showHideCheckboxes: function(me){
			me.showHideCheckboxeRenderer($('#kdAnounceCompTransactionsCheckbox'), $('#kdAnounceCompTransactionsId'), me.kdAnounceCompTransactions() );
			me.showHideCheckboxeRenderer($('#kdCompanyForecastsCheckbox'), $('#kdCompanyForecastsId'), me.kdCompanyForecasts() );
			me.showHideCheckboxeRenderer($('#kdCorporateStructureRelatedCheckbox'), $('#kdCorporateStructureRelatedId'), me.kdCorporateStructureRelated() );
			me.showHideCheckboxeRenderer($('#kdCustProdRelatedCheckbox'), $('#kdCustProdRelatedId'), me.kdCustProdRelated() );
			me.showHideCheckboxeRenderer($('#kdDividensSplitsCheckbox'), $('#kdDividensSplitsId'), me.kdDividensSplits() );
			me.showHideCheckboxeRenderer($('#kdListTradeRelatedCheckbox'), $('#kdListTradeRelatedId'), me.kdListTradeRelated() );
			me.showHideCheckboxeRenderer($('#kdPotentialRedFlagsCheckbox'), $('#kdPotentialRedFlagsId'), me.kdPotentialRedFlags() );
			me.showHideCheckboxeRenderer($('#kdPotentialTransactionsCheckbox'), $('#kdPotentialTransactionsId'), me.kdPotentialTransactions() );
			me.showHideCheckboxeRenderer($('#kdResultsCorpAnnouncementsCheckbox'), $('#kdResultsCorpAnnouncementsId'), me.kdResultsCorpAnnouncements() );
		},
		
		showHideCheckboxeRenderer: function(idCheckbox, idDiv, array){
			if(idCheckbox.prop('checked') === true){
				idDiv.show();
				if(array.length > 0){
					this.noResultsFlag = false;
				}
	        }else{
	        	idDiv.hide();
	        }
		},
		
		getKeyDevData: function(me, tickerCodes){
			PAGE.showLoading();
	    	var endpoint = PAGE.fqdn + "/sgx/search/stockListKeydevs";
			var postType = 'POST';
    	    var params = {'tickerCodes' : tickerCodes};
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){	
						if(!$.isEmptyObject(data)){
							$('#noRecordAvail').hide();
						}else{
							$('#noRecordAvail').show();
						}
						me.refractKeyDevData(data, me);
						me.showHideCheckboxes(me);
						if(me.noResultsFlag){
							$('#noRecordAvail').show();
						}else{
							$('#noRecordAvail').hide();
						}
						PAGE.hideLoading();
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
			
			setTimeout(function(){ PAGE.resizeIframeSimple(100) }, 500);
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
			me.showChange(false);
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
			if (newWLNameLC.trim()==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name is empty.</p>', width: 600 }); return; }
			if ($.inArray( newWLNameLC.toLowerCase().trim(), me.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name already exists.</p>', width: 600 }); return; }
			if (wlLength >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 StockLists.</p>', width: 600 }); return; }
			
			PAGE.showLoading();
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){
					me.watchlistCompanies.removeAll();
					PAGE.hideLoading();
					me.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
					
					$.each(data, function(i,data){
						if (data.name == newWLNameLC){
							me.selectedValue(data.id);
							
							me.clearWatchListErrors();
							me.editWLName(data.name);
							
							me.watchlistCompanies.removeAll();
							me.kdAnounceCompTransactions.removeAll();
							me.kdCompanyForecasts.removeAll();
							me.kdCorporateStructureRelated.removeAll();
							me.kdCustProdRelated.removeAll();
							me.kdDividensSplits.removeAll();
							me.kdListTradeRelated.removeAll();
							me.kdPotentialRedFlags.removeAll();
							me.kdPotentialTransactions.removeAll();
							me.kdResultsCorpAnnouncements.removeAll();
							
							$('#keyDevelopemntContentDiv').hide();
							$('#keyDevNoCompaniesTextDiv').show();
							$('#allKeyDevDivId').hide();
						}						
					});
				}, 
				PAGE.customSGXError);	
			
			me.showChange(false);
			//Clears add WL after submit
			me.newWLName(null);
		},

		editWLNameSubmit: function(){
			var me=this;
			me.showChange(false);
			if(me.editWLNameError().length != 0) return;
			var editedName = me.editWLName().trim();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/rename";
			var postType = 'POST';
			var params = { "watchlistName": editedName, "id": me.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			
			if (editedName ==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name is empty.</p>', width: 600 }); return; }
			if ($.inArray( editedName.toLowerCase(), me.addWatchlistName() ) != -1) { PAGE.modal.open({ type: 'alert',  content: '<p>StockList name already exists.</p>', width: 600 }); return;  }
			
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
					me.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
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
			me.showChange(false);
			var deleteName = me.editWLName();
			var stockListName = $("#stockListSelect option:selected").text();
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + stockListName +'?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {
				 me.showChange(false);
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
					PAGE.hideLoading();
					me.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
				}, 
				PAGE.customSGXError,
				undefined
			);			
		},
		
		populateWatchlistCompanies:function(watchlistObject, me){
		    // JSON Call for populating the companies for the selected watchlist.
			if (Object.prototype.toString.call(watchlistObject.companies) == '[object Array]' && watchlistObject.companies.length == 0){ 
			    $('#watchlistCompaniesSelect').val(null);
			    $('#keyDevelopemntContentDiv').hide();
				$('#keyDevNoCompaniesTextDiv').show();
				$('#allKeyDevDivId').hide();
				return;
			}else{
				$('#keyDevNoCompaniesTextDiv').hide();
				$('#keyDevelopemntContentDiv').show();
				$('#allKeyDevDivId').show();
			}

		    var endpoint = PAGE.fqdn+"/sgx/price/companyPrices";
		    var params = { "companies": watchlistObject.companies };
		    var postType = 'POST';
		    $.getJSON(endpoint+"?callback=?", { 'json': JSON.stringify(params) }).done(function(data){
		    	if(!$.isEmptyObject(data)){
			    	me.watchlistCompanies(data.companyPrice);
			    	me.watchlistCompanies.sort(function(a, b){
						  var a = !UTIL.isEmpty(a.companyName) ? a.companyName.toLowerCase() : "";
						  var b = !UTIL.isEmpty(b.companyName) ? b.companyName.toLowerCase() : ""; 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					});
		    	}
			}).fail(function(jqXHR, textStatus, errorThrown){
				console.log('error making service call');
			});

		},
		
		clearWatchListErrors: function() {
			$('.error-messages').empty();
		},
		
		keyDevClick: function(item) {
			var me = this;
			me.showChange(false);
			var source;
			if (item.source != null){
				source = item.source
			} else {
				source = '-'
			}
			var copy = "<h4>" + item.headline + "</h4>" + 
			   "<p class='bold'>" + 
			   "Source: " + source + "<br />" +
			   "Type: " + item.type + "<br />" +
			   "From: " + PAGE.getFormatted("date", item.date) +  
			   "</p>" +
			   "<div class='news'>" + item.situation + "</div>";
			
			PAGE.modal.open({ content: copy, type: 'alert',maxHeight:700,scrolling:true });
			
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