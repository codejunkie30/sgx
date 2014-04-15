(function() {

    require({
        urlArgs: "b=" + ((new Date()).getTime()),
        paths: {
            jquery: 'vendor/jquery-modern/jquery',
            jquicore: 'vendor/jquery.ui/ui/jquery.ui.core',
            jquiwidget: 'vendor/jquery.ui/ui/jquery.ui.widget',
            jquimouse: 'vendor/jquery.ui/ui/jquery.ui.mouse',
            accordion: 'vendor/jquery.ui/ui/jquery.ui.accordion',
            jquidatepicker: 'vendor/jquery.ui/ui/jquery.ui.datepicker',
            slider: 'vendor/jquery.ui/ui/jquery.ui.slider',
            tabs: 'vendor/jquery.ui/ui/jquery.ui.tabs',
            highstock: 'vendor/highstock/highstock',
            debug: 'vendor/ba-debug.min',
            underscore: 'vendor/underscore/underscore'
        },
        waitSeconds: 5,
        shim: {
            "debug": {
                exports: 'debug'
            },
            "jquery": {
                exports: '$'
            },
            "jquicore": {
                deps: ['jquery']
            },
            "jquiwidget": {
                deps: ['jquery', 'jquicore']
            },
            "jquimouse": {
                deps: ['jquery', 'jquicore', 'jquiwidget']
            },
            "jquidatepicker": {
                deps: ['jquery', 'jquicore', 'jquiwidget']
            },
            "accordion": {
                deps: ['jquery', 'jquicore', 'jquiwidget', 'jquimouse']
            },
            "slider": {
                deps: ['jquery', 'jquicore', 'jquiwidget', 'jquimouse']
            },
            "tabs": {
                deps: ['jquery', 'jquicore', 'jquiwidget']
            },
            "highstock": {
                "exports": "StockChart",
                "deps": ["jquery"]
            },
            "underscore": {
                "exports": "_"
            }
        }
    }, [ 'app/SGX-base', 'app/SGX-glossary', 'app/utils' ], function($, SGX, _) {

    });

}).call(this);