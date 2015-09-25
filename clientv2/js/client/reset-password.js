define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var RESETPASS = {
		email: ko.observable(),		
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		
		initPage: function() {
    		// finish other page loading
						
			this.isFormValid = ko.computed(function() {
			    return this.email() && this.newPassword() && this.retypeNewPassword();
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
			
			RESETPASS.email.extend({
				required: { message: 'Email Address is required.' },
				email: { message: 'Your email address must be in a valid format.' }
			});
			
			var minMaxMessage = 'Your new password must be between 8 and 40 characters.';
			
			RESETPASS.newPassword.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }}).extend({
					passwordComplexity: {
						message: 'Your new password does not meet the minimum requirements: it must include atleast one character, one number and one special character.'
					}
				});
			
			RESETPASS.retypeNewPassword.extend({
				required: { message: 'Retype New Password is required.' }}).extend({
					areSame: { 
						params: RESETPASS.newPassword,
						message: 'Your passwords must match.'
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
		resetPass: function(SIGNUP, me){
			var token = this.getURLParam('ref');
			var endpoint = PAGE.fqdn + "/sgx/user/password";
			var postType = 'POST';
			var params = { email: RESETPASS.email(), password: RESETPASS.newPassword(), passwordMatch: RESETPASS.retypeNewPassword(), token: token };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }			
			
			var invalidMsg = 'Invalid Token.';
			var successMsg = 'Your password has been reset.'
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				jsonp,
				function(data, textStatus, jqXHR){
					if (data.details.errorCode == 4005){
						$('<p/>').html(dupeEmailMsg).appendTo('.error-messages');
						PAGE.resizeIframeSimple();	
					} else {
						$('.form').empty().addClass('confirm');
						$('<p/>').html(successMsg).appendTo('.form.confirm');
						PAGE.resizeIframeSimple();	
					}
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
					console.log(jqXHR.statusCode() );
				});
		},
		getURLParam: function getURLParam(sParam) {
			var sPageURL = decodeURIComponent(window.location.search.substring(1)),
				sURLVariables = sPageURL.split('&'),
				sParameterName,
				i;
		
			for (i = 0; i < sURLVariables.length; i++) {
				sParameterName = sURLVariables[i].split('=');
		
				if (sParameterName[0] === sParam) {
					return sParameterName[1] === undefined ? true : sParameterName[1];
				}
			}
		}

	};
	
	return RESETPASS;
	
});