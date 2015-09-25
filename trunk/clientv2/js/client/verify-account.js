define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	var LOGOUT = {		
		initPage: function() {
			var token = this.getURLParam('ref');
			var endpoint = PAGE.fqdn + "/sgx/user/verify";
			var postType = 'POST';
			var params = { token: token };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			var verifyMsg = 'Your email address has been verified. Select the link below to access StockFacts Premium.';
			var invalidMsg = 'The time limit for validating your email address has expired. Select the link below to access StockFacts Premium, then login to trigger another validation email to be sent to you.';			
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				jsonp,
				function(data, textStatus, jqXHR){
					if (data == true){
						$('.message').html(verifyMsg);
						PAGE.resizeIframeSimple();
					} else {
						if (data.details.errorCode == 4004){
							$('.message').html(invalidMsg);							
							PAGE.resizeIframeSimple();	
						}
					}
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
					console.log(jqXHR.statusCode() );
				},
				jsonpCallback);
				
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