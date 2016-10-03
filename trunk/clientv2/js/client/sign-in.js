define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var SIGNIN = {
		email: ko.observable(),
		password: ko.observable(),
		messages: JSON.parse(MESSAGES),
		encEmail: null,
		encPassword: null,
		pubkey: null,
		
		initPage: function() {
    		var displayMessage = SIGNIN.messages.messages[0];
			
			//Displays verification message after user is re-directed after verifying account
			var verifiedToken = this.getURLParam('verified');			
			(verifiedToken != undefined) ? 	$('.message').html(displayMessage.signIn.verifySuccess) : $('.message').hide();
			
			this.isFormValid = ko.computed(function() {
			    return this.email() && this.password();
			}, this);			
			
			if(UTIL.retrieveRefreshNavigation() === "refresh"){
				$('.message').html("Please login again as the page has timed out. If you have used Reload/Refresh/Back button while accessing, please do not do so.");
				UTIL.saveRefreshNavigation("");
				$('.message').show();
			}
			
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
			var endpoint = me.fqdn + "/sgx/publickey";
			
			if (me.errors().length > 0 || me.isFormValid() == undefined) {				
	            return
	        }
			
			PAGE.showLoading();
			$.getJSON(endpoint, function( data ) {
				me.pubkey = data.pubKey;
				me.encryptUserNamePwd(data.timeStamp);
				me.loginUser();
    		});
		},
		
		encryptUserNamePwd: function(timeStamp){
			var me= this;
			var encrypt = new JSEncrypt();
			encrypt.setPublicKey( me.pubkey );
			me.encEmail = encrypt.encrypt( timeStamp +"@"+ me.email() );
			me.encPassword = encrypt.encrypt( me.password() );
		},
		
		loginUser: function(){
			var me= this;
			var displayMessage = SIGNIN.messages.messages[0];			
			var endpoint = me.fqdn + "/sgx/login";
			var postType = 'POST';
			var params = {username:me.encEmail, password:me.encPassword};
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){
					if (data.currency === undefined || data.currency === "") {
						data.currency = 'sgd';
					}
					if (typeof data.token === 'string' && data.token.length > 10){
						UTIL.saveAuthToken(data.token);
						UTIL.saveCurrency(data.currency.toLowerCase());
						top.location.href = PAGE.getPage(PAGE.pageData.getPage('index'));
						PAGE.hideLoading();
					} else {
						
						if (data.reason == 'Invalid username or password'){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.signIn.invalidUserPass).appendTo('.error-messages');
							PAGE.resizeIframeSimple();
							PAGE.hideLoading();
							return;
						} else if (data.reason == 'User account is locked'){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.signIn.accountLocked).appendTo('.error-messages');
							PAGE.resizeIframeSimple();
							PAGE.hideLoading();
							return;
						} else if (data.reason == 'User is disabled'){
							var endpoint = PAGE.fqdn + "/sgx/logout";
							var params = {};
							
							UTIL.handleAjaxRequestLogout(
								endpoint,
								function(data, textStatus, jqXHR){
									PAGE.resizeIframeSimple();
								}, 
								PAGE.customSGXError);
							
							$('.error-messages .resend-email').show();
							PAGE.resizeIframeSimple();	
							PAGE.hideLoading();
						} else {
							$('.error-messages').empty();
							var errorMessage = 'Your login attempt failed. Please try again. Error code('+data.reason+')';							
							$('<p/>').html(errorMessage).appendTo('.error-messages');
							PAGE.resizeIframeSimple();
							PAGE.hideLoading();
						}
					}
				},PAGE.customSGXError);
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