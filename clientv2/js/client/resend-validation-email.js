define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES) {
	
	var RESEND = {
		email: ko.observable(),
		messages: JSON.parse(MESSAGES),
		initPage: function() {
						
			this.isFormValid = ko.computed(function() {
			    return this.email();
			}, this);
			
			var displayMessage = RESEND.messages.messages[0];
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
			
			ko.validation = validation;
    		validation.init({ insertMessages: false });
			
			ko.validation.registerExtenders();
			
			RESEND.email.extend({
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
		resendEmail: function(me){
			var endpoint = me.fqdn + "/sgx/user/resetToken";
			var postType = 'POST';
			var params = { username: RESEND.email() };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			var displayMessage = RESEND.messages.messages[0];
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				jsonp,
				function(data, textStatus, jqXHR){
					if (data == 'User is disabled'){
						$('.button.resend').click(this.resendEmail());
					}
					if (data == true){
						$('.form').empty().addClass('rp-sent');
						$('<p/>').html(displayMessage.resendValidation.emailResent).appendTo('.form.rp-sent');
						PAGE.resizeIframeSimple();	
					}
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log(textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);
			
		}
	};
	
	return RESEND;
	
});