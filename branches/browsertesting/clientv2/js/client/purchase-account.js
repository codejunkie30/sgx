define([ "wmsi/utils", "knockout", "text!client/data/messages.json" ], function(UTIL, ko, MESSAGES) {
	
	var PURCHASE = {
		messages: JSON.parse(MESSAGES),
		purchaseToken: ko.observable(),
		initPage: function() {
			//To not logout the user
			PAGE.validNavigation(true);
			
			var endpoint = PAGE.fqdn + "/sgx/account/premiumMessage";
			var postType = 'POST';
			var params = {};
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
					
					//console.log(data);
					
					if (data.message != undefined){
						
						PURCHASE.purchaseToken($.trim(data.message));
						//PAGE.showLoading();
						PAGE.modal.open({  width: 550, maxWidth: 550, height: 200, content: '<p>You are being redirected to a third-party site, eNets to complete this transaction.</p>' });
						  if (isNaN($('#company-info').attr('height'))) $('#company-info').attr('height', '400px'); $('#company-info section').height('400px'); //PAGE.resizeIframeSimple();
						 
						setTimeout(function() {
						    $("#txnForm").submit();
						  }, 250);
					} else {
						$('.message').html(data.message);
					}
				}, 
				PAGE.customSGXError,
				jsonpCallback);
			
			PAGE.trackPage("SGX Purchase Account");
			
			// finish other page loading
    	ko.applyBindings(this, $("body")[0]);
			// resize
			//PAGE.resizeIframeSimple();
    		
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