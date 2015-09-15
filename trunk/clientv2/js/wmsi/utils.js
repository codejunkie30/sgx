
define(["jquery"], function($) {
	
	UTILS = {
			
		ie1011Styles: [ 'msTouchAction', 'msWrapFlow', 'msWrapMargin', 'msWrapThrough', 'msOverflowStyle', 'msScrollChaining', 'msScrollLimit', 'msScrollLimitXMin', 'msScrollLimitYMin', 'msScrollLimitXMax', 'msScrollLimitYMax', 'msScrollRails', 'msScrollSnapPointsX', 'msScrollSnapPointsY', 'msScrollSnapType', 'msScrollSnapX', 'msScrollSnapY', 'msScrollTranslation', 'msFlexbox', 'msFlex', 'msFlexOrder', 'msTextCombineHorizontal' ],

		/**
		 * initializes some basic overrides
		 */
		init: function() {
			
			var self = this;
			
			// add remove function
			Array.prototype.remove= function(){
			    var what, a= arguments, L= a.length, ax;
			    while(L && this.length){
			        what= a[--L];
			        while((ax= this.indexOf(what))!= -1){
			            this.splice(ax, 1);
			        }
			    }
			    return this;
			}			
			
			// handle some date issues
			var D= new Date('2011-06-02T09:34:29+02:00');
			if(!D || +D!== 1307000069000){
			    Date.fromISO= function(s){
			    	var ret = self.fixIEDate(s);
			    	return self.toSGT(ret);
			    }
			}
			else{
			    Date.fromISO= function(s){
			    	var ret = new Date(s);
			    	if (!self.isValidDate(ret)) ret = self.fixIEDate(s);
			        return self.toSGT(ret);
			    }
			}
			
			return this;			
		},
		
		/**
		 * execute a function call based on a string representation
		 * @param functionName the name of the function
		 * @param context the object to call it on
		 * @param args an argument or array of arguments to pass the function
		 */
		executeFunctionByName: function(functionName, context /*, args */) {
			var args = [].slice.call(arguments).splice(2,3);
			var namespaces = functionName.split(".");
			var func = namespaces.pop();
			for(var i = 0; i < namespaces.length; i++) {
				context = context[namespaces[i]];
			}
			return context[func].apply(this||window, args);
		},
		
		/**
		 * handle an ajax request
		 * @param endpoint the URL to make the request to
		 * @param data the request parameters sent to the server
		 * @param successFN a function to call on success (data is passed in as argument)
		 * @param errorFN a function to call on error (data, status and error message passed as arguments in that order)
		 * @param jsonpCallback the function name for JSONP request
		 */
        handleAjaxRequest: function(endpoint, data, successFN, errorFN, jsonpCallback) {
        	
        	var config = {
                url: endpoint,
                type: 'GET',
                dataType: 'jsonp',
                contentType: 'text/plain; charset=UTF-8',           	
                success: typeof successFN !== "undefined" ? successFN : this.genericAjaxSuccess,
                error: typeof errorFN !== "undefined" ? errorFN : this.genericAjaxError
        	};
        	
        	// add data request
        	if (typeof data !== "undefined") {
        		config.data = { 'json': JSON.stringify(data) };
        	}
        	
        	// add callback function name if exists
        	if (typeof jsonpCallback !== "undefined") {
        		config.jsonpCallback = jsonpCallback;
        	}
        	
        	$.ajax(config);
        	
        },
        
		handleAjaxRequestPost: function(endpoint, data, successFN, errorFN, jsonpCallback) {
        	
        	var config = {
                url: endpoint,
                type: 'POST',
				timeout : 1500,
                dataType: 'jsonp',
				jsonp: 'callback',
    			data: {'json':JSON.stringify(data)},
                scriptCharset: "utf-8" , 
                contentType: 'application/json',          	
                success: typeof successFN !== "undefined" ? successFN : this.genericAjaxSuccess,
                error: typeof errorFN !== "undefined" ? errorFN : this.genericAjaxError,
				jsonpCallback:"callback"
        	};
        	
        	// add data request
        	if (typeof data !== "undefined") {
        		config.data = { 'json': JSON.stringify(data) };
        	}
        	
        	// add callback function name if exists
        	if (typeof jsonpCallback !== "undefined") {
        		config.jsonpCallback = jsonpCallback;
        	}
        	
        	$.ajax(config);
        	
        },
		
		handleAjaxRequestPostLogin: function(endpoint, data, successFN, errorFN, jsonpCallback) {
        	
        	var config = {
                url: endpoint,
                type: 'POST',
				timeout : 1500,
                dataType: 'jsonp',
				jsonp: 'callback',
    			data: {'json':JSON.stringify(data)},
                success: typeof successFN !== "undefined" ? successFN : this.genericAjaxSuccess,
                error: typeof errorFN !== "undefined" ? errorFN : this.genericAjaxError
        	};
        	
        	// add data request
        	if (typeof data !== "undefined") {
        		config.data = { 'json': JSON.stringify(data) };
        	}
        	
        	// add callback function name if exists
        	if (typeof jsonpCallback !== "undefined") {
        		config.jsonpCallback = jsonpCallback;
        	}
        	
        	$.ajax(config);
        	
        },
		
		
        /**
         * used when no success function passed to handleAjaxRequest
         * @param data the data returned from the server
         */
        genericAjaxSuccess: function(data) {
        	alert("NO success method provided");
        	console.log(data);
        },
        
        /**
         * used when no error function passed to handleAjaxRequest
         * @param data the data returned from the server
         * @param status the status code returned from the server
         * @param er the error message return from the server
         */
        genericAjaxError: function(data, status, er) {
        	alert("NO error method provided");
        	console.log(status);
        	console.log(data);
        	console.log(er);
        },
        
    	/**
    	 * get a querystring parameter by name
    	 * @param name the name of the querystring parameter
    	 * @return the value of the querystring parameter or null if it doesn't exist
    	 */
        getParameterByName: function(name) {
			name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
            var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
            return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    	},
    	
    	/**
    	 * convert to SGT
    	 * @param dt current date
    	 * @returns {Date} in proper TZ
    	 */
    	toSGT: function(dt) {
    		var lt = dt.getTime();
    		var lo = dt.getTimezoneOffset() * 60000;
    		var utc = lt + lo;
    		var sgt = utc + (3600000*8);
    		return new Date(sgt);
    	},
    	
    	/**
    	 * IE - strings dates don't always convert well, this helps them along 
    	 * @param s UTC date as string
    	 * @returns {Date}
    	 */
    	fixIEDate: function(s) {
    		if (typeof s === "undefined") return new Date();
    		s = s.split(/\D/);
    		return new Date(Date.UTC(s[0], --s[1]||'', s[2]||'', s[3]||'', s[4]||''));
    	},
    	
    	isAnyIE: function() {
    		var myNav = navigator.userAgent.toLowerCase();
    		var d = document,  b = d.body, s = b.style, ret = myNav.indexOf('msie') != -1;
    		$.each(UTILS.ie1011Styles, function(idx, property) { if (typeof s[property] !== "undefined") ret = true; });
    		return ret;
    	},
    	
    	isValidDate: function(d) {
    		if ( Object.prototype.toString.call(d) !== "[object Date]" ) return false;
    		return !isNaN(d.getTime());
    	}
		
	}

	UTILS.init();
	return UTILS;
	
});