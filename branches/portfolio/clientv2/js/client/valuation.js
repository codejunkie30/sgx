define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "client/modules/tearsheet", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, TS) {

	var VALUATION = {
		finalWL: ko.observableArray(),
		showChange: ko.observable(false),
		editWLName: ko.observable(),
		newWLName: ko.observable(),
		premiumUser: ko.observable(),
		selectedValue: ko.observable(),
		addWatchlistName: ko.observableArray(),
		messages: JSON.parse(MESSAGES),
		
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(),
		premiumUserEmail: ko.observable(),
		currentDay: ko.observable(),
		
		initPage: function() {
			var me = this;
			
			ko.applyBindings(me, $("body")[0]);
			
			//To get the user status premium/non
			PAGE.checkStatus();
			
			//To get the select box for the currency change in the login bar
			PAGE.libCurrency(true);
			
			me.getWatchListData(me); 
		},

		getWatchListData: function(me) {
			PAGE.showLoading();
			
			var displayMessage = VALUATION.messages.messages[0];
			var endpoint = me.fqdn + "/sgx/watchlist/get";
			var postType = 'POST';
			var params = {};
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params, 
				function(data, textStatus, jqXHR){
					VALUATION.finalWL(data.watchlists.sort(sortByName));
					function sortByName(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					var arr = data.removed;					
					var removedTicker = arr.join(', ');
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your Watch Lists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
					}
				},PAGE.customSGXError
			);
			
			me.selectedValue.subscribe(function(data){
				var watchlists = this.finalWL();
				for(var i = 0, len = watchlists.length; i < len; i++) {
					var wl = watchlists[i]
					if( wl.id == data) {
						VALUATION.clearWatchListErrors();
						VALUATION.editWLName(wl.name);			
						break;
					}
				}				
			}, me);


			ko.validation.init({insertMessages: false});
			VALUATION.newWLName.extend({
					minLength: { params: 2, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
			});

			me.wlNameError = ko.validation.group(VALUATION.newWLName);  //grouping error for wlName only
			me.errors = ko.validation.group(me);			
			me.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});
			
			PAGE.hideLoading();
			
			return me;
		},
				
		addWatchlist: function(){
			var me = this;
			var newWLNameLC = VALUATION.newWLName();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/create";
			var postType = 'POST';
    	    var params = { "message": newWLNameLC };
			var wlLength = VALUATION.finalWL().length;
			
			if (me.wlNameError().length != 0) return;
			if (newWLNameLC.trim()==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watchlist name is empty.</p>', width: 600 }); return; }
			if ($.inArray( newWLNameLC.toLowerCase().trim(), VALUATION.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return; }
			if (wlLength >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 Watch Lists.</p>', width: 600 }); return; }
			
			VALUATION.addWatchlistName([]);
			$.each(VALUATION.finalWL(), function(i, data){
				VALUATION.addWatchlistName.push(data.name.toLowerCase());
			});			
			
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
					VALUATION.finalWL(data.sort(sortByName));
					
					$.each(data, function(i,data){
						if (data.name == newWLNameLC){
							VALUATION.selectedValue(data.id);
						}						
					});
				}, 
				PAGE.customSGXError);	
			
			
			//Clears add WL after submit
			VALUATION.newWLName(null);
		},

		editWLNameSubmit: function(){
			var me=this;
			var editedName = VALUATION.editWLName().trim();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/rename";
			var postType = 'POST';
			var params = { "watchlistName": editedName, "id": VALUATION.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			
			if (editedName ==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watchlist name is empty.</p>', width: 600 }); return; }
			if ($.inArray( editedName.toLowerCase(), VALUATION.addWatchlistName() ) != -1) { PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return;  }
			
			VALUATION.addWatchlistName([]);
			$.each(VALUATION.finalWL(), function(i, data){
				VALUATION.addWatchlistName.push(data.name.toLowerCase());
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
					VALUATION.finalWL(data.sort(sortByName));
				}, 
				PAGE.customSGXError,
				jsonpCallback
			);
			
				//Clears add WL after submit
			VALUATION.newWLName(null);
			VALUATION.showChange(false);
		},
		
		confirmDelete: function(){
			var deleteName = VALUATION.editWLName();
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + deleteName +'?</p> <div class="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				VALUATION.deleteWatchlist();
				$('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });		
		},		
		
		deleteWatchlist: function(){			
			var endpoint = PAGE.fqdn + "/sgx/watchlist/delete";
			var postType = 'POST';
			var params = { "message": VALUATION.selectedValue()};
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
					VALUATION.finalWL(data.sort(sortByName));
				}, 
				PAGE.customSGXError,
				undefined
			);			
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
	
	return VALUATION;
	
});