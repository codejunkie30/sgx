var page = require('webpage').create();
var system = require('system');


function waitFor(testFx, onReady, timeOutMillis) {
    var maxtimeOutMillis = timeOutMillis ? timeOutMillis : 5000, //< Default Max Timout is 5s
        start = new Date().getTime(),
        condition = false,
        interval = setInterval(function () {
            if ((new Date().getTime() - start < maxtimeOutMillis) && !condition) {
                // If not time-out yet and condition not yet fulfilled
                condition = (typeof (testFx) === "string" ? eval(testFx) : testFx()); //< defensive code
            } else {
                if (!condition) {
                    // If condition still not fulfilled (timeout but condition is 'false')
                    //console.log("'waitFor()' timeout");
                    typeof (onReady) === "string" ? eval(onReady) : onReady();
                    clearInterval(interval);
                    //phantom.exit(1);
                } else {
                    // Condition fulfilled (timeout and/or condition is 'true')
                    console.log("'waitFor()' finished in " + (new Date().getTime() - start) + "ms.");
                    typeof (onReady) === "string" ? eval(onReady) : onReady(); //< Do what it's supposed to do once the condition is fulfilled
                    clearInterval(interval); //< Stop this interval
                }
            }
        }, 500); //< repeat check every 500ms
};
 
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

        	
            waitFor(function () { return page.evaluate(function () { alert(document.title); return document.title.indexOf("PRINT") != -1; });
                    },
                    function () {
                        page.render('page2.pdf');
                        phantom.exit();
                    }, 20000);        	
        	
            

            /**
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
*/

        }
    });

}

