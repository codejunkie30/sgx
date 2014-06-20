var express = require('express');
var nodePhantom = require('node-phantom');
var app = express();
var phantoms = [], running = [], maxProcs = 10;

/**
 * start getting requests
 */
app.get("/", function(req, res) {

        console.log("URL: " + req.query.site);

        // initialize (need to pass render first time)
        getPhantom(req.query.site, res);

});

//start listening
app.listen(3000);

/**
 * initialize phantom
 * SGX servers are having issues pooling phantom (and even reusing instances)
 * need to return a new one each time and restart regularly
 * @returns
 */
function getPhantom(site, res) {
    nodePhantom.create(function(err,ph) {
    	ph.createPage(function(err, page) { drawPage(site, res, err, page, ph); });
    });
}

/**
* handle rendering
* @param url
* @param response
* @param err
* @param page
*/
function drawPage(site, response, err, page, ph) {

	page.open(site, function(pErr,status) {
		var myInterval = setInterval(
			function() {
				page.evaluate(
						function() {
							if (typeof document["pdf-name"] === "undefined") return false;
                            var pageProps = new Object();
                            pageProps.outputName = $("body").attr("pdf-name");
                            pageProps.headerHTML = $(".header-content").html();
                            pageProps.headerHeight = $(".header-content").outerHeight() + "px";
                            pageProps.footerHTML = $(".footer-content").html();
                            pageProps.footerHeight = $(".footer-content").outerHeight() + "px";
                            pageProps.paperHeight = $("body").attr("paper-height");
                            pageProps.paperWidth = $("body").attr("paper-width");
                            pageProps.paperMargin = $.parseJSON($("body").attr("paper-margin"));
                            $(".footer-content").hide();
                            return pageProps;
                        },
                        function(err, pageProps) {
                        	 if (!pageProps) return;
                             clearInterval(myInterval);
                             var paperSize = {
                                 height: pageProps.paperHeight,
                                     width: pageProps.paperWidth,
                                     margin: {
                                             top: pageProps.paperMargin[0],
                                             bottom: pageProps.paperMargin[1],
                                             left: pageProps.paperMargin[2],
                                             right: pageProps.paperMargin[3]
                                     }
                             };

                             if (typeof pageProps.headerHTML !== 'undefined') {
                                     paperSize.header = {
                                             height: pageProps.headerHeight,
                                             contents: "function(pageNum, numPages) { return decodeURI(\"" + encodeURI(pageProps.headerHTML) + "\"); }"
                                     };
                             }

                             if (typeof pageProps.footerHTML !== 'undefined') {
                                     paperSize.footer = {
                                             height: pageProps.footerHeight,
                                             contents: "function(pageNum, numPages) { return decodeURI(\"" + encodeURI(pageProps.footerHTML) + "\"); }"
                                     };
                             }

                             page.set('paperSize', paperSize, function(result) {
                        	 page.render("pdfcache/" + pageProps.outputName, function(err, data) {
                             response.download("pdfcache/" + pageProps.outputName);
                             page.close();
                             ph.exit();
                         });
                     });
                 });
             },
             500
         );
	});

	/** handle logging */
	page.onConsoleMessage = function(msg) {
	 console.log(msg);
	};

}