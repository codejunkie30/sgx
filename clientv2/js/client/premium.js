define([ "wmsi/utils", "knockout", "text!client/data/messages.json" ], function(UTIL, ko, MESSAGES) {
		
	var PREMIUM = {		
		messages: JSON.parse(MESSAGES),
		price: ko.observable(),
		initPage: function() {
			var displayMessage = PREMIUM.messages.messages[0];
			
			PREMIUM.price(displayMessage.premium.price);
			
			PAGE.trackPage("SGX Premium");
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);

    		// resize
    		this.resizeIframeSimple();
    		
    		return this;
		},		
		cancel: function () {
        	history.back();
  		}
	};
	
	return PREMIUM;
	
});