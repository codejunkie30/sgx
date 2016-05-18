define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder"], function(UTIL, ko, validation, MESSAGES) {
	
	var SIGNUP = {
		email: ko.observable(),
		password: ko.observable(),
		retypePassword: ko.observable(),		
		termsConditions: ko.observable(false),
		receiveEmails: ko.observable(false),
		messages: JSON.parse(MESSAGES),
		trialDays: ko.observable(),
		encEmail: null,
		encPassword: null,
		encPasswordMatch: null,
		pubkey: null,
		
		initPage: function() {
    		// finish other page loading
			
			var displayMessage = SIGNUP.messages.messages[0];
			
			// Returns if fields have been filled out
			this.isFormValid = ko.computed(function() {
				if (this.termsConditions() == true) { $('.error-messages .terms').hide(); }
			    return this.email() && this.password() && this.retypePassword() && this.termsConditions();
			}, this);
			
			PAGE.trackPage("SGX Sign Up");
			
			//Gets Trial Day Duration
			PAGE.getTrialDuration();
			
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
			
			SIGNUP.termsConditions.extend({ required: { message: displayMessage.signUp.termsCheck }});
			
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
			var endpoint = me.fqdn + "/sgx/publickey";
			
			if (this.errors().length > 0 || this.isFormValid() == undefined || this.termsConditions() == false) {
				if (this.termsConditions() == false) { 
					$('.error-messages .terms').remove(); 
					$('<p/>').addClass('terms').html(displayMessage.signUp.termsCheck).appendTo('.error-messages'); 
				}
	            return
	        }
			
			PAGE.showLoading();
			if(!me.pubkey){
				$.getJSON(endpoint, function( data ) {
					me.pubkey = data.pubKey;
					me.encryptUserNamePwd();
					me.CreateUser();
	    		});
			}else{
				me.encryptUserNamePwd();
				me.CreateUser();
			}
		},
		
		encryptUserNamePwd: function(){
			var me= this;
			var encrypt = new JSEncrypt();
			encrypt.setPublicKey( me.pubkey );
			me.encEmail = encrypt.encrypt( me.email() );
			me.encPassword = encrypt.encrypt( me.password() );
			me.encPasswordMatch = encrypt.encrypt( me.retypePassword() );
		},
		
		CreateUser: function(){
			var me= this;
			var displayMessage = SIGNUP.messages.messages[0];
			var endpoint = me.fqdn + "/sgx/user/create";
			var postType = 'POST';
			var params = {email:me.encEmail, password:me.encPassword, passwordMatch: me.encPasswordMatch, contactOptIn: SIGNUP.receiveEmails()};
			
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){
						if (data == true){
							$('.form').empty().addClass('confirm');
							$('<div/>').html(displayMessage.signUp.success).appendTo('.form.confirm');
							PAGE.resizeIframeSimple();
							PAGE.hideLoading();
						} else {
							if (data.details.errorCode == 4003){
								$('.error-messages').empty();
								$('<p/>').html(displayMessage.signUp.emailDuplicate).appendTo('.error-messages');
								PAGE.resizeIframeSimple();
								PAGE.hideLoading();
							}
						}
					}, 
					PAGE.customSGXError);
		},
		
		termsConditionsModal: function(){
			var displayMessage = SIGNUP.messages.messages[0];
			PAGE.modal.open({  width: 950, maxWidth: 950, height: 425, scrolling: true, content: displayMessage.signUp.termsConditions }); 
			return;
		},
		getTrialDuration: function(){
			var endpoint = PAGE.fqdn + "/sgx/properties/trialDuration";
			var postType = 'POST';
			var params = {};
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				undefined,
				function(data, textStatus, jqXHR){
					SIGNUP.trialDays(data.trialDays);
				}, 
				PAGE.customSGXError
				,jsonpCallback);
		}
	};
	
	return SIGNUP;
	
});