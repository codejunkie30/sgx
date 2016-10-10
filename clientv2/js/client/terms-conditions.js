define([ "wmsi/utils", "knockout", "text!client/data/messages.json" ], function(UTIL, ko, MESSAGES) {
	
	
	var TERMS = {
	messages: JSON.parse(MESSAGES),
			
		initPage: function() {
			if(UTILS.retrieveAuthToken()!=false){
				PAGE.timedLogout();
				setTimeout(function(){ PAGE.callout(); }, PAGE.TIMEOUT_SECONDS);
				PAGE.TIMEOUT_SECONDS=100000000;//No need to call again!!
			}
			
    		// resize
    		this.resizeIframeSimple();
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
    		
    		return this;
		},
		termsPremium: function(){
			var displayMessage = TERMS.messages.messages[0];
			PAGE.modal.open({  width: 950, maxWidth: 950, height: 475, scrolling: true, content: displayMessage.signUp.termsConditions }); 
			return;
		}	

	};
	
	return TERMS;
	
});