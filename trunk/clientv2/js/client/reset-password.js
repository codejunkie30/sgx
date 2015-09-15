define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var RESETPASS = {
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
			
			RESETPASS.tempPassword.extend({
				required: { message: 'Temporary Password is required.'}}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }
		        }).extend({
					pattern: {
						message: 'Your temporary password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
						params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
					}
				});			
			
			RESETPASS.newPassword.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }}).extend({
					pattern: {
						message: 'Your new password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
						params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
					}
				});
			
			RESETPASS.retypeNewPassword.extend({
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
		resetPass: function(SIGNUP, me){
			var token = this.getURLParam('ref');
			var endpoint = me.fqdn + "/sgx/user/password?ref="+token;
			var params = { tempPassword: RESETPASS.tempPassword(), password: RESETPASS.password(), passwordMatch: RESETPASS.retypePassword() };
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }			
			
			var invalidMsg = 'Invalid Token.';
			var successMSG = 'Your password has been reset.'
			
			UTIL.handleAjaxRequestPost(
				endpoint, 
				params, 
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