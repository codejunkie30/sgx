define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	var LOGOUT = {
				
		initPage: function() {
			PAGE.showLoading();
			var endpoint = PAGE.fqdn + "/sgx/logout";
			var params = {};
			
			UTIL.handleAjaxRequestLogout(
				endpoint,
				function(data, textStatus, jqXHR){
					PAGE.resizeIframeSimple();
					UTIL.deleteAuthToken();
					UTIL.deleteCurrency();
					UTIL.deleteState();
					PAGE.hideLoading();
				}, 
				PAGE.customSGXError);
			
			setTimeout(function(){
				url = PAGE.getPage(PAGE.pageData.getPage('index'));
				$('.form .link a').attr("href", url).attr("target", "_parent");
			},500);
			
			
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