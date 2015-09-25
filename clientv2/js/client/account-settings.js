define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/temp.json", "jquery-placeholder" ], function(UTIL, ko, validation, account) {
	
	var SAVECHANGES = {			
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		receiveEmails: ko.observable(true),
		showChange: ko.observable(false),
		initPage: function(data) {
    		
			//this.accountSettings(data);			
			
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
		accountSettings: function(){
			var endpoint = PAGE.fqdn + "/sgx/account/info";			
			
			UTIL.handleAjaxRequest(
				endpoint,
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
		saveChanges: function(me){
			
			var endpointPass = me.fqdn + "/sgx/account/password";	
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
		cancel: function () {
        	history.back();
  		}

	};
	
	return SAVECHANGES;
	
});