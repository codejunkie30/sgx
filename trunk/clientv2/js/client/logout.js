define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {
	
	var LOGOUT = {
				
		initPage: function() {
    		console.log('LO');
			var endpoint = PAGE.fqdn + "/sgx/logout";
			
			UTIL.handleAjaxRequestPost(
				endpoint,
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
			
			console.log('LOE');
			// finish other page loading
    		ko.applyBindings(this, $("body")[0]);
						
			// resize
    		this.resizeIframeSimple();
    		return this;
		},
		logout: function(me){
			
		}

	};
	
	return LOGOUT;
	
});