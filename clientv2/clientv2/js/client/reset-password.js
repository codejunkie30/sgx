define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var RESETPASS = {
		email: ko.observable(),		
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		messages: JSON.parse(MESSAGES),
		encEmail: null,
		encPassword: null,
		encPasswordMatch: null,
		pubkey: null,
		
		initPage: function() {
    		// finish other page loading
						
			this.isFormValid = ko.computed(function() {
			    return this.email() && this.newPassword() && this.retypeNewPassword();
			}, this);
			
			var displayMessage = RESETPASS.messages.messages[0];
			
			PAGE.trackPage("SGX Reset Password");
			
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
					minLength: { params: 8, message: displayMessage.passwordError },
					maxLength: { params: 40, message: displayMessage.passwordError }}).extend({
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
		
		resetPass: function(){
			var me= this;
			var endpoint = me.fqdn + "/sgx/publickey";
			
			if (me.errors().length > 0 || me.isFormValid() == undefined) {				
	            return
	        }
			
			PAGE.showLoading();
			
			if(!me.pubkey){
				$.getJSON(endpoint, function( data ) {
					me.pubkey = data.pubKey;
					me.encryptUserNamePwd();
					me.resetAccount();
	    		});
			}else{
				me.encryptUserNamePwd();
				me.resetAccount();
			}
		},
		
		encryptUserNamePwd: function(){
			var me= this;
			var encrypt = new JSEncrypt();
			encrypt.setPublicKey( me.pubkey );
			me.encEmail = encrypt.encrypt( me.email() );
			me.encPassword = encrypt.encrypt( me.newPassword() );
			me.encPasswordMatch = encrypt.encrypt( me.retypeNewPassword() );
		},
		
		resetAccount: function(){
			var me= this;
			var token = me.getURLParam('ref');
			var endpoint = PAGE.fqdn + "/sgx/user/password?ref="+token;
			var postType = 'POST';
			var params = { email: me.encEmail, password: me.encPassword, passwordMatch: me.encPasswordMatch };
			var displayMessage = RESETPASS.messages.messages[0];
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){					
					if (data == true){
						$('.form').empty().addClass('confirm');
						$('<p/>').html(displayMessage.resetPass.success).appendTo('.form.confirm');
						PAGE.resizeIframeSimple();
						PAGE.hideLoading();		
					} else {
						if (data.details.errorCode == 4005){
							$('.error-messages').empty();
							$('<p/>').html(displayMessage.resetPass.invaldToken).appendTo('.error-messages');
							PAGE.resizeIframeSimple();
							PAGE.hideLoading();
						} 
					}		
				}, 
				PAGE.customSGXError);
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