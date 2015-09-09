define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var SAVECHANGES = {			
		newPassword: ko.observable(),
		retypeNewPassword: ko.observable(),
		receiveEmails: ko.observable(true),
		showChange: ko.observable(false),
		detectChange: ko.observable(false),
		initPage: function() {
    		
			SAVECHANGES.receiveEmails.subscribe(function(newValue){
			  // alert('email');
			   this.test = ko.computed(function() {
				   
				   SAVECHANGES.detectChange = ko.observable(true);
				   
				   console.log(SAVECHANGES.detectChange());
				});
			   
			});
			
			
			SAVECHANGES.showChange.subscribe(function(newValue){
				this.isFormValid = ko.computed(function() {
				    return this.newPassword() && this.retypeNewPassword();
				}, this);
			});
			
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
			    message: 'Your passwords must match.'
			};
			
			ko.validation.registerExtenders();
			
			var minMaxMessage = 'Your new password must be between 8 and 40 characters.';		
			
			SAVECHANGES.newPassword.extend({
				required: { message: 'New Password is required.' }}).extend({
					minLength: { params: 8, message: minMaxMessage },
					maxLength: { params: 40, message: minMaxMessage }
		        }).extend({
					pattern: {
						message: 'Your new password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
						params: '((?!.*\s)(?=.*[A-Za-z0-9]))(?=(1)(?=.*\d)|.*[!@#$%\^&*\(\)-+])^.*$'
					}
				});
			
			SAVECHANGES.retypeNewPassword.extend({
				required: { message: 'Retype Password is required.' }}).extend({
				areSame: { 
					params: SAVECHANGES.newPassword
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
		startTrial: function(){
			
			if ($('.terms').hasClass('checked')) { 
				alert('terms');
			} else { 
				alert('nope'); 
			}			
		},		
		cancel: function () {
        	history.back();
  		}

	};
	
	return SAVECHANGES;
	
});