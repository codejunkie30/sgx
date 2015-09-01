define([ "wmsi/utils", "knockout", "knockout-validate", "jquery-placeholder" ], function(UTIL, ko, validation) {
	
	var SIGNUP = {
		tempPassword: ko.observable().extend({
			required: { message: 'Temporary Password is required.'},
		}),		
		newPassword: ko.observable().extend({
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
		retypeNewPassword: ko.observable().extend({
			required: { message: 'Retype New Password is required.'},
			equal: { 
				message: 'Your passwords must match.',
				params: this.password			 
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
			
			$.each($('.checkbox'),function(){				
				$(this).click(function(){					
					($(this).hasClass('checked')) ? $(this).removeClass('checked') : $(this).addClass('checked');
				});
			});
			
			$('.form input').placeholder();
			
			
			// validation
			//this.initValidation();
    		return this;
		},
		startTrial: function(){
			
			if ($('.terms').hasClass('checked')) { 
				alert('terms');
			} else { 
				alert('nope'); 
			}			
		}

	};
	
	return SIGNUP;
	
});