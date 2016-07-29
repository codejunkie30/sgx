(function() {

    require({
        urlArgs: "v=2.17",
        paths: {
            "jquery": 'vendor/jquery/jquery.1.11.3.min',
            "jquery-ui": 'vendor/jquery/jquery-ui-1.11.4/jquery-ui.min',
            "jquery-placeholder": 'vendor/jquery/jquery.placeholder',
            "jquery-validate": 'vendor/jquery/jquery.validate.min',
            "knockout": "vendor/knockout/knockout-3.3.0-min",
            "knockout-amd-helpers": "vendor/knockout/knockout-amd-helpers",
            "knockout-repeat": "vendor/knockout/knockout-repeat",
			"knockout-validate": "vendor/knockout/knockout.validation",			
	    	"text": "vendor/text",
            "highstock": 'vendor/highstock/highstock',
            "underscore": 'vendor/underscore',
            "colorbox": 'vendor/colorbox',
            "json2": 'vendor/json2',
            "jquery-timeout": 'vendor/jquery/jquery.idleTimeout.min',
			'jquery-store': 'vendor/store',
            'moment': 'vendor/moment'
        },

        waitSeconds: 180,

        shim: {
            "jquery": {
                exports: '$'
            },
            "knockout": {
            	exports: "ko"
            },
            "jquery-ui": {
                deps: ['jquery']
            },
            "jquery-placeholder": {
                deps: ['jquery']
            },
            "jquery-validate": {
                deps: ['jquery']
            },
            "highstock": {
                "exports": "StockChart",
                "deps": ["jquery"]
            },
            "underscore": {
                "exports": "_"
            },
            "colorbox": {
            	deps: ['jquery']
            },
            'json2': { 
            	exports: 'JSON' 
            }, 
			'jquery-store': {
                deps: ['jquery']
			}, 
			'jquery-timeout': {
                deps: ['jquery']				
			}
        }
        
    });
    
    loadPage();

}).call(this);