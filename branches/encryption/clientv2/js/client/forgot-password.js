define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var FORGOTPASS = {
		email: ko.observable(),
		messages: JSON.parse(MESSAGES),
		encEmail: null,
		initPage: function() {
			
			this.isFormValid = ko.computed(function() {
			    return this.email();
			}, this);
			
			var displayMessage = FORGOTPASS.messages.messages[0];
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
			
			ko.validation = validation;
    		validation.init({ insertMessages: false });
			
			PAGE.trackPage("SGX Forgot Password");
			
			ko.validation.registerExtenders();
			
			FORGOTPASS.email.extend({
				required: { message: displayMessage.emailRequired },
				email: { message: displayMessage.emailValid }
			});

			this.errors = validation.group(this);			
			
			this.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});
						
			// resize
    		this.resizeIframeSimple();
			
			// Placeholder
			$('.form input').placeholder();
    		return this;
		},
		forgotPass: function(me){
			// Adding logout out for error of user account being cached
			var endpoint = PAGE.fqdn + "/sgx/logout";
			var params = {};
			
			UTIL.handleAjaxRequestLogout(
				endpoint,
				params,
				function(data, textStatus, jqXHR){
					PAGE.resizeIframeSimple();
				}, 
				PAGE.customSGXError);
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
			
			PAGE.showLoading();
			var endpoint = me.fqdn + "/sgx/publickey";
			$.getJSON(endpoint, function( data ) {
				me.encryptUserName( data.pubKey );
				me.resetPwd();
    		});
			
		},
		
		encryptUserName: function( pubkey ){
			var me= this;
			var encrypt = new JSEncrypt();
			encrypt.setPublicKey( pubkey );
			me.encEmail = encrypt.encrypt( me.email() );
		},
		
		resetPwd: function(){
			var me= this;
			var endpoint = me.fqdn + "/sgx/user/reset";
			var postType = 'POST';
			var params = { username: me.encEmail };
			
			var displayMessage = FORGOTPASS.messages.messages[0];
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){
						if (data == true){
							$('.form').empty().addClass('rp-sent');
							$('<p/>').html(displayMessage.forgotPass.emailReset).appendTo('.form.rp-sent');
							PAGE.resizeIframeSimple();
							PAGE.hideLoading();
						}
					}, 
					PAGE.customSGXError);
		}
	};
	
	return FORGOTPASS;
	
});