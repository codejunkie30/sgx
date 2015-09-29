define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var SAVECHANGES = {			
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		showChange: ko.observable(false),
		userEmail: ko.observable(),
		contactOptIn: ko.observable(),
		currency: ko.observable(),
		subType: ko.observable(),
		subExpire: ko.observable(),
		isFormValid: ko.observable(),
		messages: JSON.parse(MESSAGES),
		initPage: function() {

			var displayMessage = SAVECHANGES.messages.messages[0];

			this.accountSettings(displayMessage);			
			
			SAVECHANGES.showChange.subscribe(function(newValue){
				this.isFormValid = ko.computed(function() {
				    return SAVECHANGES.newPassword() && SAVECHANGES.retypeNewPassword();
				}, this);
			});
			
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
			        return /((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
			    },
			    message: displayMessage.passwordError
			};
			
			ko.validation.registerExtenders();
						
			SAVECHANGES.newPassword.extend({
				required: { message: displayMessage.passwordNew }}).extend({
					minLength: { params: 8, message: displayMessage.passwordMinMax },
					maxLength: { params: 40, message: displayMessage.passwordMinMax }}).extend({
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
			
			this.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});	
    		
			// resize
    		this.resizeIframeSimple();
			
			// Placeholder
			$('.form input').placeholder();		
			
    		return this;
		},
		saveChanges: function(me){
						
			if (SAVECHANGES.showChange() == false){
				//updates Currency & OptIn Status
				this.updateSettings();
				
			} else {
				if (this.errors().length > 0 || this.isFormValid == undefined) {				
		            return
		        } else {
					//updates Password
					this.updatePassword();
					//updates Currency & OptIn Status
					this.updateSettings();
				}				
			}

		},	
		
		updateSettings: function(){
			var endpoint = PAGE.fqdn + "/sgx/account/update";
			var postType = 'POST';
			var params = { contactOptIn: SAVECHANGES.contactOptIn(), currency: 'SGD' }; //SAVECHANGES.currency()
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				undefined,
				function(data, textStatus, jqXHR){
					console.log('update success');
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
					console.log(jqXHR.statusCode() );
				},jsonpCallback);			
		},
		
		updatePassword: function(){
			var endpoint = PAGE.fqdn + "/sgx/account/password";
			var postType = 'POST';
			var params = { password: SAVECHANGES.newPassword(), passwordMatch: SAVECHANGES.retypeNewPassword() };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				undefined,
				function(data, textStatus, jqXHR){
					console.log(SAVECHANGES.newPassword());
					console.log(SAVECHANGES.retypeNewPassword());
					
					console.log('update password success');
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
					console.log(jqXHR.statusCode() );
				},jsonpCallback);
			
			
		},
		
		accountSettings: function(displayMessage){
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
					console.log(data);
					SAVECHANGES.userEmail(data.email);
					SAVECHANGES.contactOptIn(data.contactOptIn);
					SAVECHANGES.currency(data.currency);
					SAVECHANGES.subExpire(data.expirationDate);
					
					
					
					if (data.type == 'PREMIUM'){
						$('.settings .intro .content').html(displayMessage.accountSettings.introPremium);
						$('.settings .intro a').remove();
					}
					
					if (data.type == 'TRIAL'){
						
						var start = $.datepicker.formatDate("dd/M/yy", Date.fromISO(data.startDate));
						var end = $.datepicker.formatDate("dd/M/yy", Date.fromISO(data.expirationDate));
						var now = $.datepicker.formatDate("dd/M/yy", Date.fromISO(new Date()));
						var trialPeriod = Math.floor(( Date.parse(end) - Date.parse(start) ) / 86400000);
						var daysRemaining = Math.floor(( Date.parse(end) - Date.parse(now) ) / 86400000);
						var currentTrialDay = Math.floor(trialPeriod-daysRemaining);
						
						$('.settings .intro .content').html(displayMessage.accountSettings.introTrial);
						$('.settings .intro .content .current-day').html(currentTrialDay);
						
						$('.settings .intro .date').remove();
					}
					
					if (data.type == 'EXPIRED'){
						$('.settings .intro .content').html(displayMessage.accountSettings.introExpired);
						$('.settings .intro .date').remove();
					}
										
					PAGE.timedLogout();					
					PAGE.resizeIframeSimple();
										
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log(textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);
			
		},
			
		cancel: function () {
        	history.back();
  		}

	};
	
	return SAVECHANGES;
	
});