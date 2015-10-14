define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	var LOGOUT = {
				
		initPage: function() {
			var endpoint = PAGE.fqdn + "/sgx/logout";
			var params = {};
			
			UTIL.handleAjaxRequestLogout(
				endpoint,
				params,
				function(data, textStatus, jqXHR){
					PAGE.resizeIframeSimple();
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log('fail');
				});
				
			PAGE.trackPage("SGX Logout");
			// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
						
			// resize
    		this.resizeIframeSimple();
    		return this;
		}
	};
	
	return LOGOUT;
	
});