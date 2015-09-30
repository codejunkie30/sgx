define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var RESETPASS = {
		email: ko.observable(),		
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		messages: JSON.parse(MESSAGES),
		
		initPage: function() {
    		// finish other page loading
						
			this.isFormValid = ko.computed(function() {
			    return this.email() && this.newPassword() && this.retypeNewPassword();
			}, this);
			
			var displayMessage = RESETPASS.messages.messages[0];
			
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
			
			RESETPASS.email.extend({
				required: { message: displayMessage.emailRequired },
				email: { message: displayMessage.emailValid }
			});
			
			RESETPASS.newPassword.extend({
				required: { message: displayMessage.password }}).extend({
					minLength: { params: 8, message: displayMessage.passwordMinMax },
					maxLength: { params: 40, message: displayMessage.passwordMinMax }}).extend({
					passwordComplexity: {
						message: displayMessage.passwordError
					}
				});
			
			RESETPASS.retypeNewPassword.extend({
				required: { message: displayMessage.passwordRetypeNew }}).extend({
					areSame: { 
						params: RESETPASS.newPassword,
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
		resetPass: function(RESETPASS, me){
			var token = this.getURLParam('ref');
			var endpoint = PAGE.fqdn + "/sgx/user/password?ref="+token;
			var postType = 'POST';
			var params = { email: RESETPASS.email(), password: RESETPASS.newPassword(), passwordMatch: RESETPASS.retypeNewPassword() };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
			
			var displayMessage = RESETPASS.messages.messages[0];
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined,
				function(data, textStatus, jqXHR){					
					if (data == true){
						$('.form').empty().addClass('confirm');
						$('<p/>').html(displayMessage.resetPass.success).appendTo('.form.confirm');
						PAGE.resizeIframeSimple();					
					} else {
						if (data.details.errorCode == 4005){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.resetPass.invaldToken).appendTo('.error-messages');
							PAGE.resizeIframeSimple();	
						} 
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