define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var SAVECHANGES = {			
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		showChange: ko.observable(false),
		userEmail: ko.observable(),
		userOptIn: ko.observable(),
		userCurrency: ko.observable(),
		subType: ko.observable(),
		subExpire: ko.observable(),
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
			    message: 'Your passwords must match.'
			};
			
			ko.validation.registerExtenders();
			
			var minMaxMessage = 'Your new password must be between 8 and 40 characters.';		
			
			SAVECHANGES.newPassword.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }
		        }).extend({
					pattern: {
						message: 'Your new password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
						params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
					}
				});
			
			SAVECHANGES.retypeNewPassword.extend({
				required: { message: 'Retype Password is required.' }}).extend({
				areSame: { 
					params: SAVECHANGES.newPassword
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
			
			var endpointPass = me.fqdn + "/sgx/account/password";
			var postType = 'POST';
			var params = { email: SAVECHANGES.userEmail(), password: SAVECHANGES.newPassword(), passwordMatch: SAVECHANGES.retypeNewPassword() };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			
			
			var endpointInfo = me.fqdn + "/sgx/account/infoPost";	
			
			UTIL.handleAjaxRequest(
				endpointChange,
				function(data, textStatus, jqXHR){
					console.log(data.email);
					console.log(data.startDate);
					console.log(data.expirationDate);
					console.log(data.type);
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
					console.log(jqXHR.statusCode() );
				});
						
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
					
					SAVECHANGES.userEmail(data.email);
					SAVECHANGES.userOptIn(data.contactOptIn);
					SAVECHANGES.userCurrency(data.userCurrency);
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
						var daysRemaiing = Math.floor(( Date.parse(end) - Date.parse(now) ) / 86400000);
						var currentTrialDay = Math.floor(trialPeriod-daysRemaiing);
						
						$('.settings .intro .content').html(displayMessage.accountSettings.introTrial);
						$('.settings .intro .content .current-day').html(currentTrialDay);
						
						$('.settings .intro .date').remove();
					}
					
					if (data.type == 'EXPIRED'){
						$('.settings .intro .content').html(displayMessage.accountSettings.introExpired);
						$('.settings .intro .date').remove();
					}
										
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