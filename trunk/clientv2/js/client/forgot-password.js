define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var FORGOTPASS = {
		email: ko.observable(),
		initPage: function() {
			
			this.isFormValid = ko.computed(function() {
			    return this.email();
			}, this);
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
			
			ko.validation = validation;
    		validation.init({ insertMessages: false });
			
			ko.validation.registerExtenders();
			
			FORGOTPASS.email.extend({
				required: { message: 'Email Address is required.' },
				email: { message: 'Your email address must be in a valid format.' }
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
			var endpoint = me.fqdn + "/sgx/user/reset";
			var postType = 'POST';
			var params = { username: FORGOTPASS.email() };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			var sentMsg = 'An email was sent to reset your password.';
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				jsonp,
				function(data, textStatus, jqXHR){
					if (data == true){
						$('.form').empty().addClass('rp-sent');
						$('<p/>').html(sentMsg).appendTo('.form.rp-sent');
						PAGE.resizeIframeSimple();	
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
	
	return FORGOTPASS;
	
});