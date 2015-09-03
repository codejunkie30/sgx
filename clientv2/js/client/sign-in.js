define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var SIGNIN = {
		email: ko.observable(),
		password: ko.observable(),		
		initPage: function() {
    		this.isFormValid = ko.computed(function() {
			    return this.email() && this.password();
			}, this);
			
			// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
			
			ko.validation = validation;
    		validation.init({ insertMessages: false });
			
			ko.validation.registerExtenders();
			
			SIGNIN.email.extend({
				required: { message: 'Email Address is required.'},
				email: { message: 'Your email address must be in a valid format.' }
			});
			
			var minMaxMessage = 'Your new password must be between 8 and 40 characters.';
			
			SIGNIN.password.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }
		        }).extend({
					pattern: {
						message: 'Your new password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
						params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
					}
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
			var endpoint = me.fqdn + "/sgx/login";
			var params = { username: me.email(), password: me.password() };
			
			if (this.errors().length > 0 || SIGNIN.isFormValid() == undefined) {				
	            return
	        }
			
			UTIL.handleAjaxRequestEncode(endpoint, params, function(success) {
				console.log(success);	
			}, function(error){
					console.log(error);
			}, function(callback){
				console.log(callback);
			});
		}

	};
	
	return SIGNIN;
	
});