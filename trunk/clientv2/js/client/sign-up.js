define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder"], function(UTIL, ko, validation, MESSAGES) {
	
	var SIGNUP = {
		email: ko.observable(),
		password: ko.observable(),
		retypePassword: ko.observable(),		
		termsConditions: ko.observable(false),
		receiveEmails: ko.observable(false),
		messages: JSON.parse(MESSAGES),
		
		initPage: function() {
    		// finish other page loading
			
			var displayMessage = SIGNUP.messages.messages[0];
			
			// Returns if fields have been filled out
			this.isFormValid = ko.computed(function() {
			    return this.email() && this.password() && this.retypePassword();
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
			    message: displayMessage.passwordMatch
			};			
			
			ko.validation.rules['passwordComplexity'] = {
			    validator: function (val) {
		        return /((?=.*?\d)(?=.*?[a-zA-Z])(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
		    },
			    message: displayMessage.passwordError
			};
			
			ko.validation.registerExtenders();
			
			SIGNUP.email.extend({
				required: { message: displayMessage.emailRequired },
				email: { message: displayMessage.emailValid }
			});
						
			SIGNUP.password.extend({
				required: { message: displayMessage.password }}).extend({
					minLength: { params: 8, message: displayMessage.passwordError },
					maxLength: { params: 40, message: displayMessage.passwordError }}).extend({
					passwordComplexity: {
						message: displayMessage.passwordError
					}
				});
			
			SIGNUP.retypePassword.extend({
				required: { message: displayMessage.passwordRetype }}).extend({
					areSame: { 
						params: SIGNUP.password,
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
		startTrial: function(me){
			var displayMessage = SIGNUP.messages.messages[0];
			var endpoint = me.fqdn + "/sgx/user/create";
			var postType = 'POST';
			var params = { email: SIGNUP.email(), password: SIGNUP.password(), passwordMatch: SIGNUP.retypePassword(), contactOptIn: SIGNUP.receiveEmails() };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
					
			if (this.errors().length > 0 || this.isFormValid() == undefined || SIGNUP.termsConditions == false) {				
	            return
	        }

			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				jsonp,
				function(data, textStatus, jqXHR){
					if (data == true){
						$('.form').empty().addClass('confirm');
						$('<div/>').html(displayMessage.signUp.success).appendTo('.form.confirm');
						PAGE.resizeIframeSimple();
					} else {
						if (data.details.errorCode == 4003){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.signUp.emailDuplicate).appendTo('.error-messages');
							PAGE.resizeIframeSimple();	
						}
					}
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
					console.log(jqXHR.statusCode() );
				},jsonpCallback);
		}

	};
	
	return SIGNUP;
	
});