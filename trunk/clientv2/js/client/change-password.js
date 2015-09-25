define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var CHANGEPASS = {
		tempPassword: ko.observable(),		
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		
		initPage: function() {
    		// finish other page loading
						
			this.isFormValid = ko.computed(function() {
			    return this.tempPassword() && this.newPassword() && this.retypeNewPassword();
			}, this);
			
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
			
			ko.validation.rules['passwordComplexity'] = {
			    validator: function (val) {
			        return /((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
			    },
			    message: 'Your new password does not meet the minimum requirements: it must include atleast one character, one number and one special character.'
			};
			
			ko.validation.registerExtenders();
			
			var minMaxMessage = 'Your new password must be between 8 and 40 characters.';			
			
			CHANGEPASS.newPassword.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }}).extend({
					passwordComplexity: {
						message: 'Your new password does not meet the minimum requirements: it must include atleast one character, one number and one special character.'
					}
				});
			
			CHANGEPASS.retypeNewPassword.extend({
				required: { message: 'Retype Password is required.' }}).extend({
					areSame: { 
						params: CHANGEPASS.newPassword
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
		changePass: function(SIGNUP, me){
			
			var endpoint = me.fqdn + "/sgx/account/password";
			var params = { tempPassword: SIGNUP.tempPassword(), password: SIGNUP.password(), passwordMatch: SIGNUP.retypePassword() };
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }			
			
			UTIL.handleAjaxRequest(endpoint, params, function(data) {
				console.log( data );
			});
		}

	};
	
	return CHANGEPASS;
	
});