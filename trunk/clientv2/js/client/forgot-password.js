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
						
			// validation
			$('.form input').placeholder();
			//this.initValidation();
    		return this;
		},
		forgotPass: function(){
			
		}
	};
	
	return FORGOTPASS;
	
});