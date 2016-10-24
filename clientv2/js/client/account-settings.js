define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "text!client/data/currency.json", "moment", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, CUR, moment) {
	
	var SAVECHANGES = {			
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		showChange: ko.observable(false),
		userEmail: ko.observable(),
		contactOptIn: ko.observable(),
		getCurrencies: ko.observableArray(),
		selectedCurrency: ko.observable(),
		subType: ko.observable(),
		subExpire: ko.observable(),
		currency: ko.observableArray(),
		defaultCurrency: ko.observable(),
		isFormValid: ko.observable(true),
		messages: JSON.parse(MESSAGES),
		currencyDD: JSON.parse(CUR),
		encPassword: null,
		encPasswordMatch: null,
		pubkey: null,
		
		initPage: function() {
			
			PAGE.timedLogout();
			setTimeout(function(){ PAGE.callout(); }, PAGE.TIMEOUT_SECONDS);
			PAGE.TIMEOUT_SECONDS=100000000;//No need to call again!!
			
			var displayMessage = SAVECHANGES.messages.messages[0];

			this.accountSettings(displayMessage);
			
			    //898
			var endpoint = PAGE.fqdn + "/sgx/currencyList";
			var postType = 'POST';
			var params = {};
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){
					
					SAVECHANGES.getCurrencies(data);
				}, 
				PAGE.customSGXError);
		
		    //ends

			
			//this.getCurrencies(this.currencyDD.currencyList);
						
			SAVECHANGES.showChange.subscribe(function(newValue){	
				SAVECHANGES.isFormValid(false);			
				this.isFormValid = ko.computed(function() {
				    return SAVECHANGES.newPassword() && SAVECHANGES.retypeNewPassword();
				}, this);
			});
			
			PAGE.trackPage("SGX Account Settings");
					
    		ko.applyBindings(this, $("body")[0]);			
			
			ko.validation = validation;
    		validation.init({ insertMessages: false });
			
			validation.rules['areSame'] = {
			    getValue: function (o) {
			        return (typeof o === 'function' ? o() : o);
			    },
			    validator: function (val, otherField) {
			        return val === this.getValue(otherField);
					PAGE.resizeIframeSimple();
			    },
			    message: displayMessage.passwordMatch
			};			
			
			ko.validation.rules['passwordComplexity'] = {
			    validator: function (val) {
		        return /((?=.*?\d)(?=.*?[a-zA-Z])(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
		    	},
			    message: displayMessage.passwordError
			};
			
			ko.validation.registerExtenders();
						
			SAVECHANGES.newPassword.extend({
				required: { message: displayMessage.passwordNew }}).extend({
					minLength: { params: 8, message: displayMessage.passwordError },
					maxLength: { params: 40, message: displayMessage.passwordError }}).extend({
					passwordComplexity: {
						message: displayMessage.passwordError
					}
				});
			
			SAVECHANGES.retypeNewPassword.extend({
				required: { message: displayMessage.passwordRetypeNew }}).extend({
					areSame: { 
						params: SAVECHANGES.newPassword,
						message: displayMessage.passwordMatch
					}	
				});
			
			this.errors = ko.validation.group(this);
			
			this.errors.subscribe(function (data) {
				PAGE.resizeIframeSimple();
				if(data == '' || data == undefined){
					SAVECHANGES.isFormValid(true);
				}
			});			
			// resize
    		this.resizeIframeSimple();
			
			// Placeholder
			$('.form input').placeholder();		
			
    		return this;
		},
		saveChanges: function(me){
			var displayMessage = SAVECHANGES.messages.messages[0];
			if (SAVECHANGES.showChange() == false){
				//updates Currency & OptIn Status
				this.updateSettings();
    			$('.save').remove();
				$('<div class="save">Your changes have been saved.</div>').insertBefore('.form').delay(4000).fadeOut(function() {$(this).remove();});
    			return;				
			} else {
				if (this.errors().length > 0 && SAVECHANGES.isFormValid() == false) {
					$('.error-messages').empty();
					$('<p/>').html(displayMessage.passwordError).appendTo('.error-messages');
		            return
		        } else {
					//updates Password
					this.updatePassword();
					//updates Currency & OptIn Status
					this.updateSettings();
					
					SAVECHANGES.showChange(false);
					SAVECHANGES.isFormValid(true);
					$('.save').remove();
					$('<div class="save">Your changes have been saved.</div>').insertBefore('.form').delay(4000).fadeOut(function() {$(this).remove();});
    				return;
				}				
			}

		},	
		
		updateSettings: function(){
			var endpoint = PAGE.fqdn + "/sgx/account/update";
			var postType = 'POST';
			var params = { contactOptIn: SAVECHANGES.contactOptIn(), currency: SAVECHANGES.selectedCurrency() }; //SAVECHANGES.currency()
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				undefined,
				function(data, textStatus, jqXHR){
					console.log(data);
					UTILS.saveCurrency(SAVECHANGES.selectedCurrency());
				}, 
				PAGE.customSGXError
				,jsonpCallback);			
		},
		
		updatePassword: function(){
			var me = this;
			var endpoint = me.fqdn + "/sgx/publickey";
			if( !me.pubKey ){
				$.getJSON(endpoint, function( data ) {
					me.pubkey = data.pubKey;
					me.encryptPwd();
					me.changePwd();
	    		});
			}else{
				me.encryptPwd();
				me.changePwd();
			}
			
		},
		
		encryptPwd: function(){
			var me= this;
			var encrypt = new JSEncrypt();
			encrypt.setPublicKey( me.pubkey );
			me.encPassword = encrypt.encrypt( me.newPassword() );
			me.encPasswordMatch = encrypt.encrypt( me.retypeNewPassword() );
		},
		
		changePwd: function(){
			var me = this;
			var endpoint = PAGE.fqdn + "/sgx/account/password";
			var postType = 'POST';
			var params = { password: me.encPassword, passwordMatch: me.encPasswordMatch };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				undefined,
				function(data, textStatus, jqXHR){
					$('.error-messages').empty();
				}, 
				PAGE.customSGXError,
				jsonpCallback);
		},
		
		//getCurrency: function(){
//			var endpoint = PAGE.fqdn + "/sgx/currencyList";
//			var postType = 'POST';
//			var params = {};
//			UTIL.handleAjaxRequestJSON(
//				endpoint,
//				postType,
//				params,
//				function(data, textStatus, jqXHR){
//					
//					SAVECHANGES.currency([data]);
//					console.log(SAVECHANGES.currency());
//					console.log(data);
//				}, 
//				PAGE.customSGXError);
//		},
				
		accountSettings: function(displayMessage){
			var endpoint = PAGE.fqdn + "/sgx/account/info";
			var postType = 'POST';
			var params = {};			
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){
					SAVECHANGES.userEmail(data.email);
					SAVECHANGES.contactOptIn(data.contactOptIn);
					setTimeout(function(){ SAVECHANGES.selectedCurrency(data.currency); }, 400);
					if (data.type == '' || data.type == undefined || data.type == 'UNAUTHORIZED'){
						var home = PAGE.getPage(PAGE.pageData.getPage('index'));
						top.location.href = home;
					}					
					var start = moment(data.startDate);
					var end = moment(data.expirationDate);
					var daysRemaining = data.daysRemaining;
					if (data.type == 'PREMIUM'){						
					    	if(daysRemaining === 0){
					    		$('.settings .intro .content').html(displayMessage.accountSettings.introPremiumLastDay);
					    	}
						$('.settings .intro .content').html(displayMessage.accountSettings.introPremium);
						$('.settings .intro a').remove();
						var end = $.datepicker.formatDate("dd/M/yy", Date.fromISO(data.expirationDate));
						
						SAVECHANGES.subExpire(end);
						SAVECHANGES.defaultCurrency('PREMIUM');
						
					}
					
					if (data.type == 'TRIAL'){
												
						if (daysRemaining >= 1) {
							if(daysRemaining > 1)
								$('.settings .intro .content').html(displayMessage.accountSettings.introTrial);
							else
								$('.settings .intro .content').html(displayMessage.accountSettings.introTrialDayOne);
								$('.settings .intro .content .current-day').html(daysRemaining);
							
								$('.settings .intro .date').remove();
						}else if(daysRemaining == 0){
						    $('.settings .intro .content').html(displayMessage.accountSettings.introTrialLastDay);
						} else {
							$('.settings .intro .content').html(displayMessage.accountSettings.introExpired);
							$('.settings .intro .date').remove();
						}
						SAVECHANGES.defaultCurrency('TRIAL');
					}
					
					if (data.type == 'EXPIRED'){
						$('.settings .intro .content').html(displayMessage.accountSettings.introExpired);
						$('.settings .intro .date').remove();
						SAVECHANGES.defaultCurrency('EXPIRED');
					}
										
					PAGE.timedLogout();					
					PAGE.resizeIframeSimple();
										
				}, 
				PAGE.customSGXError);
			
		},
			
		cancel: function () {
			PAGE.validNavigation(true);
        	history.back();
  		}

	};
	
	return SAVECHANGES;
	
});