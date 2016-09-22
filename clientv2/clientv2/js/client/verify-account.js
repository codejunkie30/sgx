define([ "wmsi/utils", "knockout", "text!client/data/messages.json" ], function(UTIL, ko, MESSAGES) {
	
	var VERIFY = {
		messages: JSON.parse(MESSAGES),
		initPage: function() {
			
			var token = this.getURLParam('ref');
			var endpoint = PAGE.fqdn + "/sgx/user/verify";
			var postType = 'POST';
			var params = { "token": token };
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			var displayMessage = VERIFY.messages.messages[0];
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				undefined,
				function(data, textStatus, jqXHR){
					if (data == true){
						var signIn = PAGE.getPage(PAGE.pageData.getPage('sign-in'));
						var verifyNum = VERIFY.randomNum();
						top.location.href = signIn+'&verified='+verifyNum;
					} else {
						if (data.details.errorCode == 4006){
							$('.message').html(displayMessage.verifyAccount.alreadyVerified);							
							PAGE.resizeIframeSimple();	
						}
						
						if (data.details.errorCode == 4004 || data.details.errorCode == 5001){
							$('.message').html(displayMessage.verifyAccount.invaldToken);							
							PAGE.resizeIframeSimple();	
						}
					}
				}, 
				PAGE.customSGXError,
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
		},
		randomNum: function(){
			return (Math.floor(Math.random()*1000)+1000);
		}

	};
	
	return VERIFY;
	
});