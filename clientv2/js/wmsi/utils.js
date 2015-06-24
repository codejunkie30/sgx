
define(["jquery"], function($) {
	
	UTILS = {
			
		ie1011Styles: [ 'msTouchAction', 'msWrapFlow', 'msWrapMargin', 'msWrapThrough', 'msOverflowStyle', 'msScrollChaining', 'msScrollLimit', 'msScrollLimitXMin', 'msScrollLimitYMin', 'msScrollLimitXMax', 'msScrollLimitYMax', 'msScrollRails', 'msScrollSnapPointsX', 'msScrollSnapPointsY', 'msScrollSnapType', 'msScrollSnapX', 'msScrollSnapY', 'msScrollTranslation', 'msFlexbox', 'msFlex', 'msFlexOrder', 'msTextCombineHorizontal' ],

		/**
		 * initializes some basic overrides
		 */
		init: function() {
			
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
			    	var ret = UTILS.fixIEDate(s);
			    	return UTILS.toSGT(ret);
			    }
			}
			else{
			    Date.fromISO= function(s){
			    	var ret = new Date(s);
			    	if (!UTILS.isValidDate(ret)) ret = UTILS.fixIEDate(s);
			        return toSGT(ret);
			    }
			}
			
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
                scriptCharset: "utf-8" , 
                contentType: 'application/json; charset=UTF-8',           	
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
    	 * don't allow inputs with selector to input anything except numbers
    	 */
    	numericOnly: function(selector) {
    		
		    $(selector).keydown(function (e) {
		        // Allow: backspace, delete, tab, escape, enter and .
		        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
		             // Allow: Ctrl+A
		            (e.keyCode == 65 && e.ctrlKey === true) || 
		             // Allow: home, end, left, right
		            (e.keyCode >= 35 && e.keyCode <= 39)) {
		                 // let it happen, don't do anything
		                 return;
		        }
		        // Ensure that it is a number and stop the keypress
		        if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
		            e.preventDefault();
		        }
		    });
    		
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
    	}
		
	}

	UTILS.init();
	return UTILS;
	
});