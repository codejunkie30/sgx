define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "client/modules/tearsheet",  "text!client/data/watchlists/alerts.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, TS, AL) {

	ko.components.register('premium-preview', { require: 'client/components/premium-preview'});
	
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
		
		defaultSearch: "",
		
		initPage: function() {
						
			// extend tearsheet
			$.extend(true, this, TS);
			 this.init();
			var self = this;
			
			
			PAGE.checkStatus();
				var waitForDataToInit = ko.computed({
		          read:function(){
		
		              var companyData = this.gotCompanyData();
		              var userStatus = this.userStatus();
		
		              if( companyData && userStatus ) {
		
		                  if ( userStatus == 'UNAUTHORIZED' || userStatus == 'EXPIRED' ) {
		                    //this.init_nonPremium();
		                  } else {
		                    this.init(function() { self.finish(self); });
		                  }
		              }
		
		          },
		          owner:this
		      });
			
			
		},
		
		finish: function(me) {
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
					
					console.log(data);
					
					ALERTS.finalWL(data);
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
						
						var companies = wl.companies
						wl.companies = ko.observableArray(companies);						
						ALERTS.displayList(wl);
						
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

						
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);	
			
			me.trackPage("SGX Company Watchlist - " + me.companyInfo.companyName);
    		
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
			
			return this;
			
		},
		
		init_nonPremium: function() {
            $('#alerts-content-alternative').show();
           // ko.applyBindings(this, $("body")[0]);
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
			$('.error-messages').empty();
			
			var wlLength = ALERTS.finalWL().length;				
			
			if (wlLength >= 3) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 Watch Lists.</p>', width: 600 }); return; }
			
			var endpoint = PAGE.fqdn + "/sgx/watchlist/create";
			var postType = 'POST';
    		var params = { "message": ALERTS.newWLName() };
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					console.log(data);
					ALERTS.finalWL(data);
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
		
		showEditWLName: function(){
			
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
					console.log(data);
					ALERTS.finalWL(data);
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
		
		deleteWatchlist: function(){
			
			var deleteName = ALERTS.editWLName();
			
			//PAGE.modal.open({ type: 'alert',  content: '<p>Are you sure you want to delete ' + deleteName +'</p>', width: 400 }); return; 
			
			var endpoint = PAGE.fqdn + "/sgx/watchlist/delete";
			var postType = 'POST';
    		var params = { "message": ALERTS.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					console.log(data);
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