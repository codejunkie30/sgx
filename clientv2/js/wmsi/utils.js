
define(["jquery", "moment"], function($, moment) {
	
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
		
		saveRefreshNavigation: function(fromNavigation) {
            
            store.set('fromNavigation', {value: fromNavigation});
        },


        retrieveRefreshNavigation: function() {
            var fromNavigation = store.get('fromNavigation');
            if(!fromNavigation) 
            	return;
            return fromNavigation.value;
        },

        //X-AUTH-TOKEN
        saveAuthToken: function(token) {
            if(!token) return;
            //hopefully store is in at this time
            store.set('token', {value: token, expiration: moment().add(1,'hours').valueOf()});
        },


        retrieveAuthToken: function() {
            var token = store.get('token');
            if(token && moment().valueOf() < token.expiration ) {
                return token.value;
            }else {
                return false;
            }


        },
        
        
        saveCurrencyFlag: function(flag) {
           store.set('currenyFlag', flag);
        },


        retrieveCurrencyFlag: function() {
            var flag = store.get('currenyFlag');
            return flag;


        },
        
        saveEnvType:function(envType){
            store.set('envType',envType);
        },
        
        getEnvType:function(){
          return store.get('envType');  
        },

        deleteAuthToken: function() {
            store.remove('token')  
        },
	
		
		saveCurrency: function(currency) {
            if(!currency) return;
            //hopefully store is in at this time
            store.set('currency', {value: currency});
        },


        retrieveCurrency: function() {
			var currency = store.get('currency');
			if(currency != undefined ) {
                return currency.value;
            }else {
                return 'sgd';
            }
        },
		
		 deleteCurrency: function() {
            store.remove('currency')  
        },
		
		saveState: function(initialLoad) {
            if(!initialLoad) return;
            //hopefully store is in at this time
            store.set('initialLoad', {value: initialLoad});
        },

        retrieveState: function() {
			var initialLoad = store.get('initialLoad');
			if(initialLoad != undefined ) {
                return initialLoad.value.toLowerCase();
            }else {
                return false;
            }
        },
		deleteState: function() {
            store.remove('initialLoad')  
        },
		
		/**
		 * handle an ajax request
		 * @param endpoint the URL to make the request to
		 * @param postType the method used to request the server - GET or POST 
		 * @param data the request parameters sent to the server
		 * @param successFN a function to call on success (data is passed in as argument)
		 * @param errorFN a function to call on error (data, status and error message passed as arguments in that order)
		 * @param jsonp is used to override the callback function name in a JSONP request
		 * @param jsonpCallback the function name for JSONP request
		 */
        
		handleAjaxRequest: function(endpoint, postType, data, jsonp, successFN, errorFN, jsonpCallback) {

            UTILS.handleAjaxRequestJSON(endpoint, postType, data, successFN, errorFN);
        	
        },
		
		handleAjaxRequestJSON: function(endpoint, postType, data, successFN, errorFN) {
        	var config = {
                url: endpoint,
                type: postType,
                dataType: 'json',
                scriptCharset: "utf-8",
                contentType: 'application/json',
                success: typeof successFN !== "undefined" ? successFN : this.genericAjaxSuccess,
                error: typeof errorFN !== "undefined" ? errorFN : this.genericAjaxError
        	};
        	
			
			
            var token = UTILS.retrieveAuthToken();
            var currency = UTILS.retrieveCurrency();
			var initialLoad = UTILS.retrieveState();

            config.beforeSend=function(request) {
            	//make sure responses are not cached
                request.setRequestHeader('cache-control', 'no-cache');

				if (token !== false) {
                	request.setRequestHeader('x-auth-token', token);
				}
				if (currency !== false) {
					request.setRequestHeader('currency', currency);
				}
				if ( location.pathname.split("/")[1] == "print.html" && UTILS.getParameterByName("currency")!= undefined){
					request.setRequestHeader('currency', UTILS.getParameterByName("currency"));
				}
				
				if (initialLoad !== false) {
					request.setRequestHeader('initial-load', initialLoad);
				}
            }
            
			
			
        	// add data request
        	if (typeof data !== "undefined") {
        		config.data = JSON.stringify(data);
                //config.data = JSON.stringify(data);
        	}
			
        	$.ajax(config);
        	
        },
		
		
		// Handles logout - Used since no data or type is needed
		handleAjaxRequestLogout: function(endpoint, successFN, errorFN) {        	
        	var config = {
                url: endpoint,
                success: typeof successFN !== "undefined" ? successFN : this.genericAjaxSuccess,
                error: typeof errorFN !== "undefined" ? errorFN : this.genericAjaxError
        	};
        	var token = UTILS.retrieveAuthToken();
            config.beforeSend=function(request) {
            	if (token !== false) {
                	request.setRequestHeader('x-auth-token', token);
				}
				
            }
        	$.ajax(config);        	
        },
        
        handleRefreshAjaxRequestLogout: function(endpoint, successFN, errorFN) {        	
        	var config = {
                url: endpoint,
                async: false,
                type: 'POST',
                dataType: 'json',
                success: typeof successFN !== "undefined" ? successFN : this.genericAjaxSuccess,
                error: typeof errorFN !== "undefined" ? errorFN : this.genericAjaxError
        	};
        	var token = UTILS.retrieveAuthToken();
            config.beforeSend=function(request) {
            	if (token !== false) {
                	request.setRequestHeader('x-auth-token', token);
				}
				
            }
        	$.ajax(config);        	
        },
        handleSynchronousRequest: function(endpoint, postType, data, successFN, errorFN) {        	
    	var config = {
            url: endpoint,
            async: false,
            type: postType,
            dataType: 'json',
            success: typeof successFN !== "undefined" ? successFN : this.genericAjaxSuccess,
            error: typeof errorFN !== "undefined" ? errorFN : this.genericAjaxError
    	};
    	var token = UTILS.retrieveAuthToken();
        config.beforeSend=function(request) {
        	if (token !== false) {
            	request.setRequestHeader('x-auth-token', token);
				}
				
        }
    	$.ajax(config);        	
    },

		
		
		
        /**
         * used when no success function passed to handleAjaxRequest
         * @param data the data returned from the server
         */
        genericAjaxSuccess: function(data) {
        	alert("NO success method provided");
        },
        
        /**
         * used when no error function passed to handleAjaxRequest
         * @param data the data returned from the server
         * @param status the status code returned from the server
         * @param er the error message return from the server
         */
        genericAjaxError: function(data, status, er) {
        	
            	if(data.responseText.indexOf('Invalid Token')>=0||jqXHR.status=="401") {
        		UTILS.saveAuthToken("");
        		top.location.href = PAGE.getPage(PAGE.pageData.getPage('logout'));
        	}
        	console.log("NO error method provided");
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
    	},
    	
    	isEmpty: function(val){
    	    return (val === undefined || val == null || val.length <= 0) ? true : false;
    	}
    	
	}

	UTILS.init();
	return UTILS;
	
});