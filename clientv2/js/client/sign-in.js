define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var SIGNIN = {
		email: ko.observable(),
		password: ko.observable(),
		messages: JSON.parse(MESSAGES),
		initPage: function() {
    		this.isFormValid = ko.computed(function() {
			    return this.email() && this.password();
			}, this);
			
			var displayMessage = SIGNIN.messages.messages[0];
			
			// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
			
			ko.validation = validation;
    		validation.init({ insertMessages: false });
			
			ko.validation.rules['passwordComplexity'] = {
			    validator: function (val) {
		        	return /((?=.*?\d)(?=.*?[a-zA-Z])(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
		    	},
			    message: displayMessage.passwordError
			};
			
			ko.validation.registerExtenders();
			
			SIGNIN.email.extend({
				required: { message: displayMessage.emailRequired },
				email: { message: displayMessage.emailValid }
			});
			
			SIGNIN.password.extend({
				required: { message: displayMessage.passwordNew }
			});
			
			this.errors = validation.group(this);			
			
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
		signIn: function(me){
			var displayMessage = SIGNIN.messages.messages[0];			
			var endpoint = me.fqdn + "/sgx/login";
			var postType = 'POST';
			var params = {username:me.email(), password:me.password()};
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
						
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					if (data == '' || data == undefined){
						top.location.href = PAGE.getPage(PAGE.pageData.getPage('index'));
					} else {
						
						if (data.reason == 'Invalid username or password'){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.signIn.invalidUserPass).appendTo('.error-messages');
							PAGE.resizeIframeSimple();	
						}
						if (data.reason == 'User account is locked'){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.signIn.accountLocked).appendTo('.error-messages');
							PAGE.resizeIframeSimple();	
						}
						if (data.reason == 'User is disabled'){
							$('.error-messages .resend-email').show();
							PAGE.resizeIframeSimple();	
						}					
					}
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);
			
		}

	};
	
	return SIGNIN;
	
});