define(['jquery', 'underscore', 'debug'], function($, _, debug, jQuery) {
    var SGX = SGX || {};
    var dev = {
        dev: {
            init: function() {
                debug.log('SGX.Dev - jQuery v' + $.fn.jquery);
                // This is for reloading the page on the fly when a build file changes without needing a refresh.
                function includeJS(incFile) {
                    $('head').prepend('<script type="text/javascript" src="' + incFile + '"></scr' + 'ipt>');
                }
                if (document.domain == 'localhost') {
                    includeJS('/socket.io/socket.io.js');
                    includeJS('/javascripts/reload-client.js');
                }
            }
        }
    };
    _.extend(SGX, dev);
    SGX.dev.init();
});