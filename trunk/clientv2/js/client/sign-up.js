define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder"], function(UTIL, ko, validation) {
	
	var SIGNUP = {
		email: ko.observable(),
		password: ko.observable(),
		retypePassword: ko.observable(),		
		termsConditions: ko.observable(false),
		receiveEmails: ko.observable(false),
		
		initPage: function() {
    		// finish other page loading
			
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
			    message: 'Your passwords must match.'
			};			
			
			ko.validation.rules['passwordComplexity'] = {
			    validator: function (val) {
			        return /((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
			    },
			    message: 'Your new password does not meet the minimum requirements: it must include atleast one character, one number and one special character.'
			};
			
			ko.validation.registerExtenders();
			
			SIGNUP.email.extend({
				required: { message: 'Email Address is required.' },
				email: { message: 'Your email address must be in a valid format.' }
			});
			
			var minMaxMessage = 'Your new password must be between 8 and 40 characters.';
			
			SIGNUP.password.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }}).extend({
					passwordComplexity: {
						message: 'Your new password does not meet the minimum requirements: it must include atleast one character, one number and one special character.'
					}
				});
			
			SIGNUP.retypePassword.extend({
				required: { message: 'Retype Password is required.' }}).extend({
					areSame: { 
						params: SIGNUP.password,
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
		startTrial: function(me){
			
			var endpoint = me.fqdn + "/sgx/user/create";
			var postType = 'POST';
			var params = { email: SIGNUP.email(), password: SIGNUP.password(), passwordMatch: SIGNUP.retypePassword(), contactOptIn: SIGNUP.receiveEmails() };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
					
			if (this.errors().length > 0 || this.isFormValid() == undefined || SIGNUP.termsConditions == false) {				
	            return
	        }
			
			var dupeEmailMsg = 'That email address is already registered with StockFacts. Please enter a new email address.';
			var successMsg = 'Your account has been created. The next step is to validate your email address. Please log into your email account and click the link to confirm your email address.';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				jsonp,
				function(data, textStatus, jqXHR){
					if (data == true){
						$('.form').empty().addClass('confirm');
						$('<p/>').html(successMsg).appendTo('.form.confirm');
						PAGE.resizeIframeSimple();
					} else {
						if (data.details.errorCode == 4003){
							$('<p/>').html(dupeEmailMsg).appendTo('.error-messages');
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