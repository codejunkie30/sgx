define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var SIGNIN = {
		email: ko.observable().extend({
			required: { message: 'Email Address is required.'},
			email: { message: 'Your email address must be in a valid format.'}
		}),
		password: ko.observable().extend({
			required: { message: 'New Password is required.'}}).extend({
				minLength: 8,
				maxLength: 40,
		        message: 'Your new password must be between 8 and 40 characters.'
	        }).extend({
				pattern: {
					message: 'Your new password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
					params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
				}
		}),		
		initPage: function() {
    		ko.validation = validation;
    		ko.validation.init({ insertMessages: false });
			
			ko.validation.registerExtenders();
			
			this.errors = ko.validation.group(this);			
			
			this.errors.subscribe(function () {
			    PAGE.resizeIframeSimple();
		   });
			
			
			// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
    		
			// resize
    		this.resizeIframeSimple();			
						
			// validation
			$('.form input').placeholder();
			//this.initValidation();
    		return this;
		},
		signIn: function(){			
		}

	};
	
	return SIGNIN;
	
});