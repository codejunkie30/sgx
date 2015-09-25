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
			
			ko.validation.rules['passwordComplexity'] = {
			    validator: function (val) {
			        return /((?=.*?\d)(?=.*?[A-Za-z])|(?=.*?\d)(?=.*?[^\w\d\s]))^.*/.test('' + val + '');
			    },
			    message: 'Your new password does not meet the minimum requirements: it must include atleast one character, one number and one special character.'
			};
			
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
					passwordComplexity: {
						message: 'Your new password does not meet the minimum requirements: it must include an alphanumeric character, number and/or special character.',
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
			var postType = 'POST';
			var params = {username:me.email(), password:me.password()};
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			if (this.errors().length > 0 || this.isFormValid() == undefined) {				
	            return
	        }
			
			var invalidUserPassMsg = 'The email address and/or password you entered are not valid';
			var accountLockedMsg = 'You have surpassed the number of allowable logins (we allow three attempts). Your account has been locked. Please try again in 30 minutes.';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				jsonp, 
				function(data, textStatus, jqXHR){
					console.log(data);
					console.log(textStatus);
					if (data == '' || data == undefined){						
						PAGE.premiumUser(true);
						top.location.href = PAGE.getPage(PAGE.pageData.getPage('index'));
					}					
					if (data.reason == 'Invalid username or password'){
						$('<p/>').html(invalidUserPassMsg).appendTo('.error-messages');
						PAGE.resizeIframeSimple();	
					}
					if (data.reason == 'User is disabled' || data.reason == 'User account is locked'){
						$('<p/>').html(invalidUserPassMsg).appendTo('.error-messages');
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
	
	return SIGNIN;
	
});