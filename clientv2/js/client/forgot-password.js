define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var SIGNIN = {
		email: ko.observable().extend({
			required: { message: 'Email Address is required.'},
			email: { message: 'Your email address must be in a valid format.'}
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
		forgotPass: function(){			
		}

	};
	
	return SIGNIN;
	
});