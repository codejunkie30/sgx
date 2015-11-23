define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var SIGNIN = {
		email: ko.observable(),
		password: ko.observable(),
		messages: JSON.parse(MESSAGES),
		initPage: function() {
    		var displayMessage = SIGNIN.messages.messages[0];
			
			//Displays verification message after user is re-directed after verifying account
			var verifiedToken = this.getURLParam('verified');			
			(verifiedToken != undefined) ? 	$('.message').html(displayMessage.signIn.verifySuccess) : $('.message').hide();
			
			this.isFormValid = ko.computed(function() {
			    return this.email() && this.password();
			}, this);			
			
			
			PAGE.trackPage("SGX Sign In");
			
			//PAGE.checkStatus();
			
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
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
						
			UTIL.handleAjaxRequestAccount(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){
					if (data == '' || data == undefined){
						top.location.href = PAGE.getPage(PAGE.pageData.getPage('index'));

					} else {
						
						if (data.reason == 'Invalid username or password'){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.signIn.invalidUserPass).appendTo('.error-messages');
							PAGE.resizeIframeSimple();
							return;
						}
						if (data.reason == 'User account is locked'){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.signIn.accountLocked).appendTo('.error-messages');
							PAGE.resizeIframeSimple();
							return;
						}
						if (data.reason == 'User is disabled'){
							var endpoint = PAGE.fqdn + "/sgx/logout";
							var params = {};
							
							UTIL.handleAjaxRequestLogout(
								endpoint,
								params,
								function(data, textStatus, jqXHR){
									PAGE.resizeIframeSimple();
								}, 
								function(jqXHR, textStatus, errorThrown){
									console.log('fail');
								});
							
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
	
	return SIGNIN;
	
});