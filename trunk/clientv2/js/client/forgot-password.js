define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var FORGOTPASS = {
		email: ko.observable(),
		messages: JSON.parse(MESSAGES),
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
			
			var endpoint = me.fqdn + "/sgx/user/reset";
			var postType = 'POST';
			var params = { username: FORGOTPASS.email() };
			
			var displayMessage = FORGOTPASS.messages.messages[0];
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
			PAGE.showLoading();
			UTIL.handleAjaxRequestAccount(
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