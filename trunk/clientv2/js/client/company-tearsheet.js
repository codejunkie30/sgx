define(
	[ "wmsi/utils", "knockout", "client/modules/price-chart",
		"text!client/data/factors.json", "client/modules/tearsheet",
		"client/base", "text!client/data/socialsentimentcompanies.json" ],
	function(UTIL, ko, PRICE_CHART, FACTORS, TS, BASE,
		SOCIALSENTIMENTCOMPANIES) {
	    /**
	     * no op
	     */
	    ko.bindingHandlers.createFactor = {};

	    var CP = {

		letters : "ABCDEFGHIJSKLMNOPQRSTUVWXYZ",
		factors : ko.observable([]),
		premiumUser : ko.observable(),
		premiumUserEmail : ko.observable(),
		premiumUserAccntInfo : ko.observable(),
		libLoggedIn : ko.observable(),
		libTrialPeriod : ko.observable(),
		libTrialExpired : ko.observable(),
		libSubscribe : ko.observable(),
		libAlerts : ko.observable(),
		libCurrency : ko.observable(false),
		currentDay : ko.observable(),

		cpUserStatus : ko.observable(),
		currentTicker : ko.observable(),

		ajaxInAction : ko.observableArray([]),
		socialSentimentCompanies : JSON.parse(SOCIALSENTIMENTCOMPANIES),
		twitterFeeds : ko.observable([]),
		sentimentImagePath : ko.observable(),
		buzzImagePath : ko.observable(),
		sentimentIndicators : ko.observable(true),
		buzzIndicators:ko.observable(true),

		initPage : function(me) {
		    // extend tearsheet
		    $.extend(true, this, TS);

		    var self = this;
		    PAGE.ajaxInAction.push('initPage');

		    this.init(function() {
			self.finish(self, function() {
			    PAGE.ajaxInAction.remove('initPage')
			    CP.fetchSocialAlphaTwitterFeed();
			    CP.fetchSocialAlphaAnalytics();

			});
		    }); // call for social alpha Analytics

		    // call for social alpha twitter feed

		    PAGE.checkStatus();

		    PAGE.ajaxInAction.push('checkStatus');

		    var cpUserStatus;

		    PAGE.userStatus.subscribe(function(data) {
			CP.cpUserStatus = data;
			if (data) {
			    PAGE.ajaxInAction.remove('checkStatus');
			}
		    });

		},
		// call for social alpha Analytics
		fetchSocialAlphaAnalytics : function() {
		    var me = this;
		    var tickerId = PAGE.companyInfo.tickerCode;
		    var params = {
		    };
		    var postType = 'GET';
		    var endpoint ="/indicators/sentiment/range";
		    this.getDataFromSocialAlpha(endpoint, postType, params,
			    function(data) {
				console.log(data);
			    }, PAGE.customSGXErrors);
		   var tickerCodes = ["NS8U"];
		    for(ticker in tickerCodes){
		    var endpoint ="/indicators/sentiment/SGX:"+tickerId ;
		    params = {};
		    
		    this.getDataFromSocialAlpha(endpoint, postType, params,
			    function(data) {
			 var val = data.indicators.value;
			 if(val===undefined||val=='undefined'){
			     	CP.sentimentIndicators(false);
			     return;
			 }
			    if (val <= -1) {
				CP.sentimentImagePath('/img/social/sentiment_gauge_1.png');
			    } else if (val >= -1.0
				    || val <= 0.75) {
				CP.sentimentImagePath('/img/social/sentiment_gauge_2.png');
			    } else if (val >= -0.75
				    || val <= -0.5) {
				CP.sentimentImagePath('/img/social/sentiment_gauge_3.png');
			    } else if (val >= -0.5 || val <= 0.5) {
				CP.sentimentImagePath('/img/social/sentiment_gauge_4.png');
			    }else if (val >= 0.5 || val <= 0.75) {
				CP.sentimentImagePath('/img/social/sentiment_gauge_5.png');
			    }else if (val >= 0.75 || val <= 1) {
				CP.sentimentImagePath('/img/social/sentiment_gauge_6.png');
			    }else if (val >= 1) {
				CP.sentimentImagePath('/img/social/sentiment_gauge_7.png');
			    }

			    }, PAGE.customSGXErrors);
		    }
		    var endpoint ="/indicators/volatility/SGX:"+tickerCodes;
		    this.getDataFromSocialAlpha(endpoint, postType, params,
			    function(data) {
			    	  console.log(data);
			    	  var val = data.indicators.value;
			    	  if(val==null||null===undefined){
			    	      CP.buzzIndicators(false);
			    	  return;
			    	  }
				    if (val < 1.0) {
					CP.buzzImagePath('/img/social/buzz_bar_1.png');
				    } else if (val >= 1.0
					    || val <= 2.0) {
					CP.buzzImagePath('/img/social/buzz_bar_2.png');
				    } else if (val >= 2.0
					    || val <= 3.0) {
					CP.buzzImagePath('/img/social/buzz_bar_3.png');
				    } else if (val >= 3.0) {
					CP.buzzImagePath('/img/social/buzz_bar_4.png');
				    }

			    }, PAGE.customSGXErrors);
		},
		// call for social alpha twitter feed
		fetchSocialAlphaTwitterFeed : function() {
		    var me = this;
		    var tickerId = PAGE.companyInfo.tickerCode;
		    var fromDate = new Date();
		    var days=7; // Days you want to subtract
		    var date = new Date();
		    fromDate = new Date(date.getTime() - (days * 24 * 60 * 60 * 1000));
		    var params = {
			from: fromDate.toISOString(),
			to: new Date().toISOString()
		    };
		    //var tickerCodes = ["C61U","U14","Z74","E5H","T39","BN4","BS6","U11","MC0","U96","C31","NS8U","C09","CC3","S68","C52","S59","C07","S58","C38U","G13","A17U","F34","D05","S63","J36","Y92","H78","C6L","O39"]
		    //var tickerCodes = ["NS8U"];
		    var postType = 'GET';
//		    for(ticker in tickerCodes){
			    var endpoint = "/tweets/by-ticker/SGX:" +tickerId;
			    this.getDataFromSocialAlpha(endpoint, postType, params,
				    function(data) {
				// display in panel
				console.log(data);
				PAGE.twitterFeeds(data.tweets.sort(function(a,b){
				    return b.sourceTimestamp.localeCompare(a.sourceTimestamp);
				}));
				//TODO Display on UI
				    }, PAGE.customSGXErrors);
			
//		    }

		},
		// check if SocialSentiment be visible
		isSocialSentimentVisibleForCompany : function() {
		    var me = this;
		    var tickerId = PAGE.companyInfo.tickerCode;
		    return $
			    .grep(
				    this.socialSentimentCompanies.socialSentimentCompanies,
				    function(e, i) {
					return e.id === tickerId;
				    }).length > 0 && (PAGE.userStatus()== 'TRIAL'||PAGE.userStatus()== 'PREMIUM');
		},
		getDataFromSocialAlpha : function(endPoint,postType,data, successFN, errorFN) {
		    var date = new Date();
		    date = date.toISOString();
		    var type = postType;
		    var acceptType = "application/json, text/plain, */*";
		    //TODO read from FILE
		    var APIKEY = "JBMMSWSTFBXPVMMIFFXB"
		    var secretKey = "2uAMASOLmIlcYbkIvR3WaPlWEPB4Xs3l3EjWp8o5";
		    
		     var nonce = Math.floor((Math.random() * 1000) + 1);
		   
		    var message = type + ":"
			    + acceptType + ":" + date
			    + ":" + endPoint + ":" + nonce ;
		    var crypto=CryptoJS.HmacSHA1(message,secretKey).toString(CryptoJS.enc.Base64);
		    //as this third party service provider invocation ,we need not use UTILS.handleAjaxRequestJSON and also token mechanisim to maintain the communication
		    $.ajax({
			url : "https://api.social-alpha.com"+endPoint,
			type : type,
			data : data,
			beforeSend : function(xhr) {
			    xhr.setRequestHeader('Accept',
				    acceptType);
			    xhr.setRequestHeader('Authorization',
				    'SA-HMAC-SHA1 Credential='+APIKEY+' Nonce='+nonce+' Signature='
					    + crypto);
			    xhr.setRequestHeader('X-SA-Date', date);
			},
	                success: typeof successFN !== "undefined" ? successFN : UTIL.genericAjaxSuccess,
	                error: typeof errorFN !== "undefined" ? errorFN : UTIL.genericAjaxError});
		},
		displayTime:function(sourceTimestamp){
		    var srcDate = new Date(sourceTimestamp).getTime();
		    var currDate = new Date(new Date().toISOString()).getTime();
		    var diff = Math.abs((currDate-srcDate)/(1000*60*60));
		    return diff<=24?Math.round(diff)+" h":Highcharts.dateFormat("%b %e", new Date(sourceTimestamp).getTime());
		},
		isSocialFeedSentimentVisible:function(){
		    return CP.sentimentIndicators();
		},
		isBuzzIndicatorVisible:function(){
		   return  CP.buzzIndicators()
		},
		finish : function(me, cb) {

		    // finish other page loading
		    ko.applyBindings(this, $("body")[0]);

		    if (cb && typeof cb === 'function') {
			cb();
		    }

		    // track the view
		    me.trackPage("SGX Company Profile - "
			    + me.companyInfo.companyName);

		    // alpha factors
		    var tmp = JSON.parse(FACTORS);
		    $.each(tmp.factors, function(idx, field) {
			var item = {
			    "minLabel" : "High",
			    "maxLabel" : "Low"
			};
			$.extend(item, field);
			tmp.factors[idx] = item;
		    });
		    this.factors(tmp.factors);

		    CP.currentTicker(me.ticker);

		    PAGE.ajaxInAction.push('priceHistoryCall');
		    var cb = function() {
			PAGE.ajaxInAction.remove('priceHistoryCall');
		    }

		    // init charts
		    var params = {
			id : me.ticker
		    };
		    var postType = 'POST';
		    var endpoint = me.fqdn + "/sgx/company/priceHistory";
		    UTIL.handleAjaxRequest(endpoint, postType, params,
			    undefined, function(data) {
				me.initPriceChart(me, data, me, cb);
			    }, PAGE.customSGXError, undefined);

		    return this;

		},
		initPriceChart : function(parent, data, me, cb) {

		    if (cb && typeof cb === 'function') {
			cb();
		    }
		    var finished = function() {
			parent.resizeIframeSimple();
			var myFin = function() {
			    parent.resizeIframeSimple();
			};
			parent.initNews(parent, data, myFin);
		    };
		    var update = function() {
			parent.initNews(parent, data);
		    };
		    // Pushes params to chart for use
		    if (data.price.length > 0) {
			if (UTIL.retrieveCurrency().toLowerCase() === "sgd") {
			    // To add the real time pricing to the chart
			    endpoint = PAGE.fqdn + "/sgx/price";
			    var params = {
				id : me.ticker
			    };
			    var postType = 'POST';
			    UTIL
				    .handleAjaxRequest(
					    endpoint,
					    postType,
					    params,
					    undefined,
					    function(priceData) {
						if (priceData
							&& priceData
								.hasOwnProperty("price")
							&& priceData.price.tradingCurrency
								.toLowerCase() === "sgd") {
						    priceData = priceData.price;
						    // Adding the same data
						    // point twice to handle 1d
						    // option being active
						    // always
						    for (i = 0; i < 2; i++) {
							var date = priceData
								.hasOwnProperty("lastTradeTimestamp")
								&& priceData.lastTradeTimestamp != null ? priceData.lastTradeTimestamp
								: priceData.previousDate;
							var price = priceData
								.hasOwnProperty("lastPrice")
								&& priceData.lastPrice != null ? priceData.lastPrice
								: priceData.closePrice;

							if (priceData.closePrice)
							    data.price.push({
								date : date,
								value : price
							    });
							if (priceData.lowPrice)
							    data.lowPrice
								    .push({
									date : date,
									value : priceData.lowPrice
								    });
							if (priceData.openPrice)
							    data.openPrice
								    .push({
									date : date,
									value : priceData.openPrice
								    });
							if (priceData.highPrice)
							    data.highPrice
								    .push({
									date : date,
									value : priceData.highPrice
								    });
							var volume = priceData.volume;
							if (volume) {
							    volume = priceData.volume / 1000000.0;
							}
							data.volume.push({
							    date : date,
							    value : volume
							});
						    }
						}
						PRICE_CHART.init(
							"#price-volume", data,
							finished, update,
							me.ticker,
							CP.cpUserStatus);
					    }, PAGE.customSGXError, undefined);
			} else {
			    PRICE_CHART.init("#price-volume", data, finished,
				    update, me.ticker, CP.cpUserStatus);
			}
		    } else {
			parent.resizeIframeSimple();
		    }

		},

		initNews : function(parent, data, finishedDrawing) {

		    var chart = $('#price-volume').highcharts();
		    var start = new Date(chart.xAxis[0].min);
		    var end = new Date(chart.xAxis[0].max);
		    var div = $(".stock-events");

		    var curStart = typeof $(div).attr("start-dt") === "undefined" ? new Date()
			    : $(div).attr("start-dt");
		    var curEnd = typeof $(div).attr("end-dt") === "undefined" ? new Date()
			    : $(div).attr("end-dt");

		    if (start == curStart && end == curEnd)
			return;

		    $(div).attr("start-dt", start).attr("end-dt", end);

		    PAGE.ajaxInAction.push('keydevs');
		    var cb = function() {
			PAGE.ajaxInAction.remove('keydevs');
		    }

		    var endpoint = parent.fqdn + "/sgx/search/keydevs";
		    var postType = 'POST';
		    var params = {
			tickerCode : parent.ticker,
			from : Highcharts.dateFormat("%Y-%m-%e", start),
			to : Highcharts.dateFormat("%Y-%m-%e", end)
		    };

		    UTIL.handleAjaxRequest(endpoint, postType, params,
			    undefined, function(data) {

				// just make it an empty array
				if (!data.hasOwnProperty("keyDevs"))
				    data.keyDevs = [];

				// sort it
				data.keyDevs.sort(function(a, b) {
				    return Date.fromISO(b.date)
					    - Date.fromISO(a.date);
				});

				// restrict to no more then 10
				data.keyDevs = data.keyDevs.slice(0, 10);

				// display in panel
				parent.keyDevs(data.keyDevs);

				// add to chart
				var seriesData = [];
				$.each(parent.keyDevs(), function(idx, keyDev) {
				    var point = {
					x : Date.fromISO(keyDev.date),
					title : parent.getKeyDevLetter(idx),
					shape : 'url(img/stock-marker.png)',
					id : parent.getKeyDevID(idx)
				    };
				    seriesData.push(point);
				});
				seriesData.sort(function(a, b) {
				    return a.x - b.x;
				});
				chart.series[2].update({
				    data : seriesData
				});

				// everything is done
				if (typeof finishedDrawing !== "undefined")
				    finishedDrawing();

				if (cb && typeof cb === 'function') {
				    cb();
				}

			    }, PAGE.customSGXError, undefined);

		},

		getKeyDevLetter : function(idx) {
		    return this.letters.substring(idx, idx + 1);
		},

		getKeyDevID : function(idx) {
		    return "keyDev-" + this.getKeyDevLetter(idx);
		},

		keyDevClick : function(model, data, event) {
		    var source;
		    if (data.source != null) {
			source = data.source
		    } else {
			source = '-'
		    }
		    var copy = "<h4>" + data.headline + "</h4>"
			    + "<p class='bold'>" + "Source: " + source
			    + "<br />" + "Type: " + data.type + "<br />"
			    + "From: " + model.getFormatted("date", data.date)
			    + "</p>" + "<div class='news'>" + data.situation
			    + "</div>";

		    model.modal.open({
			content : copy,
			type : 'alert'
		    });

		},

		hasGTIs : function(model) {
		    var ret = false;
		    try {
			ret = model.gtis.gtis.length > 0;
		    } catch (err) {
		    }
		    return ret;
		},

		handleFactor : function(tearsheet, elements, data) {

		    // nothing to do
		    if (typeof tearsheet.alphaFactors === "undefined"
			    || tearsheet.alphaFactors == null)
			return;

		    // id and matching value
		    var id = $("[data-id]", elements[0]).attr("data-id");
		    var factor = typeof id !== "undefined" && id != "" ? tearsheet.alphaFactors[id]
			    : undefined;

		    if (typeof factor === "undefined" || factor == 0) {
			$(elements[0]).hide();
			return;
		    }

		    // handle the click
		    var pg = tearsheet.getPage(tearsheet.pageData
			    .getPage("index"), "type=alpha-factors&factor="
			    + id + "&quintile=" + factor);
		    $(".quintiles", elements[0]).addClass(
			    "per-" + (factor * 20)).click(function() {
			window.top.location.href = pg;
		    });

		}

	    };
	    CP.ajaxOngoingCalls = ko.computed(function() {
		return CP.ajaxInAction().length > 0;
	    }).extend({
		throttle : 250
	    });

	    CP.ajaxOngoingCalls.subscribe(function(data) {
		if (data) {
		    if (document.getElementById('loading'))
			return;
		    BASE.showLoading();
		} else {
		    BASE.hideLoading();
		}
	    })

	    return CP;

	});