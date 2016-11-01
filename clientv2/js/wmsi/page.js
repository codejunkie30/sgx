define(["jquery", "wmsi/utils", "wmsi/XD", ], function($, UTIL) {

	PAGE = {
			
		/**
		 * client name used for analytics and page titles
		 */
		parentURL: null,
		pageHeight: null,
		language: 'en-us',
		localData: null,

		/**
		 * initialize the page object
		 * @param config the page config object
		 */
		initPage: function() {
			// initialize analytics
			(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o), m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m) })(window,document,'script','//www.google-analytics.com/analytics.js','_gaTracker'); _gaTracker('create', PAGE.gaClientId, { 'cookieDomain': 'none' });

			// set the default page height
			this.pageHeight = $(document).height();

			return this;
		},

		
		/**
		 * get the page title
		 */
		getPageTitle: function(title) {
			return title;
		},

		/**
		 * track a page view to analytics
		 * @param title the page title logged in analytics
		 */
		trackPage: function(title) {
        		PAGE.trackEnvironment();
        		var newTitle = this.getPageTitle(title);
                	window.document.title = newTitle;
                	setTimeout(function(){this.trackView("pageview", newTitle); }, 100),
		},
		
		trackEnvironment :function(){
		    _gaTracker('set', {
			  'dimension1': PAGE.getEnvironmentType()
			});
		},
		
		/**
		 * track a page view to analytics
		 * @param type the type of view
		 * @param title the page title logged in analytics
		 */
		trackView: function(type, title) {
        	window.document.title = this.getPageTitle(title);
        	//console.log(window.document.title);
        	_gaTracker('send', type, { 'title': title });
		},
		
		/**
		 * default iframe
		 */
		resizeIframeSimple: function(scroll) {
            var scroll = scroll || 0;
			PAGE.resizeIframe(PAGE.getTrueContentHeight(), scroll);
		},


		
		/**
		 * send resize event to parent 
		 * @param height the new height of frame
		 * @param scroll the scroll position of the page (e.g. top for new pages, in page for page change events)
		 */
        resizeIframe: function(height, scroll) {
        	if (this.getParentURL() == null) return;
        	var curPage = this;
        	var fn = function() {
        		var msg = height + "-" + ((typeof scroll === "undefined") ? "0" : scroll);
        		var url = curPage.getParentURL();
        		curPage.pageHeight = height;
        		XD.postMessage(msg, url, parent); 
        	};
        	setTimeout(fn, 50);
        },

        /**
         * used by XD to know where to send the resize message to
         */
        getParentURL: function() {
        	if (this.parentURL != null) return this.parentURL;
         	if (typeof document.location.hash !== "undefined" && document.location.hash != "") {
            	this.parentURL = decodeURIComponent(document.location.hash.replace(/^#/, ''));
            	this.parentURL = this.parentURL.split("?")[0].split("#")[0];
        	}
         	else {
         		this.parentURL = window.location.href;
         	}
        	return this.parentURL;
        },
        
        /**
         * attempts to get the true content height of page
         * does something different for early versions of IE
         */
        getTrueContentHeight: function() {
        	return navigator.userAgent.toLowerCase().indexOf('msie') != -1 ? document.body.offsetHeight : $("body:first,html:first").innerHeight();
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
         * get the property value from a js object
         * @param obj the object to pull the value from
         * @param prop a period "." separated tree to traverse (e.g. model.title.value)
         */
        getPropertyValue: function(obj, prop) {
            if (typeof prop === "string") prop = prop.split(".");
            if (typeof prop === "undefined") return undefined;
            if (prop.length > 1) {
                var e = prop.shift();
                return this.getPropertyValue(obj[e] = Object.prototype.toString.call(obj[e]) === "[object Object]" ? obj[e] : {}, prop);
            } 
            return obj[prop[0]]	;
        },
        
        /**
         * Ajax call to get EnvironmentType for GoogleAnalytics
         */
        getEnvironmentType:function(){
		var endpoint = PAGE.fqdn + "/sgx/environmentType";
		var postType = 'POST';
		var params = {};
		if(!UTIL.isEmpty(UTIL.getEnvType())){
		    return UTIL.getEnvType();
		}
		UTIL.handleAjaxRequestJSON(
			endpoint,
			postType,
			params,
			function(data, textStatus, jqXHR){
				if(!UTIL.isEmpty(data)){
				    UTIL.saveEnvType(data);
				}
				return UTIL.getEnvType();
				    
			}, 
			PAGE.customSGXError);  
        },

        /**
         * assign a value to the json object by string reference
         * @param obj the core object
         * @param prop the property to set (using . notation if nested path)
         * @param value the value to set
         */
        setPropertyValue: function(obj, prop, value) {
            if (typeof prop === "string") prop = prop.split(".");
            if (prop.length > 1) {
                var e = prop.shift();
                this.setPropertyValue(obj[e] = Object.prototype.toString.call(obj[e]) === "[object Object]" ? obj[e] : {}, prop, value);
                return;
            }
            obj[prop[0]] = value;
        },
        
        /**
         * iframe version of the page
         */
        getPage: function(pgObj, extra) {
        	return this.getPageURL(pgObj, this.getParentURL(), extra);
        },
        
        /**
         * iframe version of the page
         */
        getPageURL: function(pgObj, parentURL, extra) {
        	var url = pgObj.file;
        	if (parentURL != null) url = parentURL + "?page=" + pgObj.id;
        	if (typeof extra !== "undefined") {
        		url += url.indexOf("?") == -1 ? "?" : "&";
        		url += extra;
        	}
        	// check language
        	if (url.indexOf("lang=") == -1) {
        		url += url.indexOf("?") == -1 ? "?" : "&";
        		url += "lang=" + this.language;
        	}
        	if (typeof pgObj.postProcess === "function") url = pgObj.postProcess(url);
        	return url;
        }
			
	}
	
	return PAGE;
	
});

