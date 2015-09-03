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
			
			ko.validation.registerExtenders();
			
			var minMaxMessage = 'Your new password must be between 8 and 40 characters.';
			
			CHANGEPASS.tempPassword.extend({
				required: { message: 'Temporary Password is required.'}}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }
		        }).extend({
					pattern: {
						message: 'Your temporary password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
						params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
					}
				});			
			
			CHANGEPASS.newPassword.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }
		        }).extend({
					pattern: {
						message: 'Your new password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
						params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
					}
				});
			
			CHANGEPASS.retypeNewPassword.extend({
					required: { message: 'Retype Password is required.' }			
				}).extend({
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
			
			// validation
			$('.form input').placeholder();
			//this.initValidation();
    		return this;
		},
		resetPass: function(SIGNUP, me){
			
			var endpoint = me.fqdn + "/sgx/user/create";
			var params = { email: SIGNUP.email(), password: SIGNUP.password(), passwordMatch: SIGNUP.retypePassword() };
			
			if (this.errors().length > 0 || CHANGEPASS.isFormValid() == undefined) {				
	            return
	        }			
			
			UTIL.handleAjaxRequestPost(endpoint, params, function(data) {console.log( data );});		
		}

	};
	
	return CHANGEPASS;
	
});