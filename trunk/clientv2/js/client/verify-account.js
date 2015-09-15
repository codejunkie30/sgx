define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	var LOGOUT = {		
		initPage: function() {
			var token = this.getURLParam('ref');
			var endpoint = PAGE.fqdn + "sgx/user/verify?ref="+token;
			var params = {name:'token', value:token};
			UTIL.handleAjaxRequest(
				endpoint,
				params,
				function(data, textStatus, jqXHR){
					console.log('success');
					console.log(data);
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
					console.log('sta', textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				});
				
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