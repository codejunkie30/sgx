var page = require('webpage').create();
page.settings.resourceTimeout = 10000;

var system = require('system');

 
 /**
  * From PhantomJS documentation:
  * This callback is invoked when there is a JavaScript console. The callback may accept up to three arguments: 
  * the string for the message, the line number, and the source identifier.
  */
 page.onConsoleMessage = function (msg, line, source) {
     console.log('console> ' + msg);
 };
 
 /**
  * From PhantomJS documentation:
  * This callback is invoked when there is a JavaScript alert. The only argument passed to the callback is the string for the message.
  */
 page.onAlert = function (msg) {
     console.log('alert!!> ' + msg);
 };
 
if (system.args.length < 2) {
    console.log('Usage: renderpdf.js URL');
    phantom.exit(1);
}
else {

    var address = system.args[1];
    
    page.open(address, function (status) {
	
        if (status !== 'success') {
            console.log('Unable to load the address!');
        } 
		else {

			var pageProps = page.evaluate(function() {
				var pageProps = new Object();
				pageProps.footerHTML = $(".footer").html();
				pageProps.footerHeight = $(".footer").outerHeight() + "px";
				$(".footer").hide();
				return pageProps;
			});
			
			page.paperSize = {
					height: "11in",
					width: "8.5in",
					margin: {
						top: ".34in",
						bottom: ".34in",
						left: ".55in",
						right: ".55in"
					},
					footer: {
						height: pageProps.footerHeight,
						contents: typeof pageProps.footerHTML == 'undefined' ? "" : phantom.callback(function(pageNum, numPages) { return pageProps.footerHTML; })
					}
			};

			pageProps.outputPath = "Fake.pdf";
			
		    page.render(pageProps.outputPath);
			
			console.log("Rendering: " + pageProps.outputPath);

		    phantom.exit();

        }
    });

}

