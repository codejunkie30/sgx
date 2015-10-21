define([ "wmsi/utils", "knockout", "text!client/data/messages.json" ], function(UTIL, ko, MESSAGES) {
	
	var PURCHASE = {
		messages: JSON.parse(MESSAGES),
		initPage: function() {
			
			var token = this.getURLParam('ec');
			var endpoint = PAGE.fqdn + "/sgx/account/errorCode";
			var postType = 'POST';
			var params = { 'errorCode': token};
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			var displayMessage = PURCHASE.messages.messages[0];			
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				jsonp,
				function(data, textStatus, jqXHR){
					//Display success message
					console.log(data);
					if (token != undefined){
						console.log(data);
						$('.message').html(data.message);
						
					} else {
						$('.message').html('Account Created');
					}
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log(textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				}, jsonpCallback);
			
			PAGE.trackPage("SGX Purchase Account Confirmation");
			
			// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
						
			// resize
    		this.resizeIframeSimple();
    		return this;
		},
		getURLParam: function getURLParam(sParam) {
			var sPageURL = decodeURIComponent(window.location.search.substring(1)),
				sURLVariables = sPageURL.split('&'),
				sParameterName,
				i;
		
			for (i = 0; i < sURLVariables.length; i++) {
				sParameterName = sURLVariables[i].split('=');
		
				if (sParameterName[0] === sParam) {
					return sParameterName[1] === undefined ? true : sParameterName[1];
				}
			}
		}

	};
	
	return PURCHASE;
	
});