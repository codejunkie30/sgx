define([ "wmsi/utils", "knockout", "text!client/data/messages.json" ], function(UTIL, ko, MESSAGES) {
	
	var LOGOUT = {
		messages: JSON.parse(MESSAGES),
		initPage: function() {
			var token = this.getURLParam('ref');
			var endpoint = PAGE.fqdn + "/sgx/user/verify";
			var postType = 'POST';
			var params = { "token": token };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			var displayMessage = LOGOUT.messages.messages[0];			
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				undefined,
				function(data, textStatus, jqXHR){
					console.log(data);
					console.log(token);
					if (data == true){
						$('.message').html(displayMessage.verifyAccount.success);
						PAGE.resizeIframeSimple();
					} else {
						if (data.details.errorCode == 4004 || data.details.errorCode == 5001){
							$('.message').html(displayMessage.verifyAccount.invaldToken);							
							PAGE.resizeIframeSimple();	
						}
					}
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log(textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				}, jsonpCallback);
				
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
	
	return LOGOUT;
	
});