define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json","client/modules/performance-chart-config","highstock", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, PER_CHART_CONFIG) {

	
	ko.bindingHandlers.datepicker = {
		    init: function (element, valueAccessor, allBindingsAccessor) {
		        var options = allBindingsAccessor().datepickerOptions || {};
		        $(element).datepicker({  maxDate: new Date() });

		        //handle the field changing
		       /* ko.utils.registerEventHandler(element, "change", function () {
		            var observable = valueAccessor();
		            observable($(element).datepicker("getDate"));
		        });*/

		        //handle disposal (if KO removes by the template binding)
		        ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
		            $(element).datepicker("destroy");
		        });
		        
		        var value = parseInt($(element).val());
		        var date = $.datepicker.formatDate("mm/dd/yy", Date.fromISO(value));
		        $(element).val(date);
		    }
		};
	

	ko.bindingHandlers.currency = {
	    symbol: ko.observable('$'),
	    init: function(element, valueAccessor, allBindingsAccessor){
	        return ko.bindingHandlers.text.update(element,function(){
	            var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0),
                symbol = ko.utils.unwrapObservable(allBindingsAccessor().symbol === undefined ? allBindingsAccessor().symbol : ko.bindingHandlers.currency.symbol);
            	var returnValue = symbol + value;
	            return returnValue === "$0.00" ? "-" : returnValue;
	        });
	    }
	};
	
	ko.bindingHandlers.currencyInput = {
		symbol: ko.observable('$'),
	    init: function (element, valueAccessor, allBindingsAccessor) {
	    	var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0),
            symbol = ko.utils.unwrapObservable(allBindingsAccessor().symbol === undefined ? allBindingsAccessor().symbol : ko.bindingHandlers.currencyInput.symbol);
    		$(element).val(symbol + value.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    }
	};
	
	ko.bindingHandlers.largeNumber = {
	    init: function (element, valueAccessor, allBindingsAccessor) {
	    	var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0);
    		$(element).val(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','));
	    }
	};

	var VALUATION = {
		finalWL: ko.observableArray(),
		showChange: ko.observable(false),
		editWLName: ko.observable(),
		newWLName: ko.observable(),
		premiumUser: ko.observable(),
		selectedValue: ko.observable(),
		addWatchlistName: ko.observableArray(),
		messages: JSON.parse(MESSAGES),
		activeTab: ko.observable('performance'),
		displayTransactions: ko.observableArray([]),
		displayTransCompanies: ko.observableArray(),
		selectAllTransaction: ko.computed(function() {}),
		companyNameAndTickerList : [],
		
		watchlistCompanies: ko.observableArray(),
		availableTypes: ko.observableArray(['BUY', 'SELL']),
		transItems: ko.observableArray([]),
		selectedCompanyValue: ko.observable(),
		selectedAvailableType: ko.observable(),
		initialNumberOfShares: ko.observable(),
		initialCostAtPurchase: ko.observable(),
		initialTradeDate: ko.observable(),
		
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(),
		premiumUserEmail: ko.observable(),
		currentDay: ko.observable(),
		
		watchlistId: null,
		disCompanyName: null,
		liveClosingPrice: null,
		
		seriesOptions: [],
		transactionTickers: [],
		chartData: [],
		
		transactionNoOfShares: 0.00,
		userEnteredPurchasedPrice: 0.00,
		userEnteredSellPrice: 0.00,
		
		companiesNoOfShares: 0.00,
		sumOfLastClosePrice: 0.00,
		
		initPage: function() {
			var me = this;
			
			ko.applyBindings(me, $("body")[0]);
			
			//To get the user status premium/non
			PAGE.checkStatus();
			
			//To get the select box for the currency change in the login bar
			PAGE.libCurrency(true);
			
			me.getWatchListData(me); 
			
			$('#valuationSection').show();
			
		},
		
		renderChart: function(me, responseData){
			me.transactionTickers = [];
			$.each(responseData.companiesPriceHistory, function(i, data){
				me.transactionTickers.push(data.tickerCode);
				
				var allData = data.priceHistory;
				var priceData = me.toHighCharts(allData.price);
				var lowPrice = me.toHighCharts(allData.lowPrice);
				var openPrice = me.toHighCharts(allData.openPrice);
				var highPrice = me.toHighCharts(allData.highPrice);
				
				$.each(priceData, function(k, record) {
					var key = Highcharts.dateFormat("%e/%b/%Y", new Date(record.x));
					
			        me.chartData[key] = {}
			        me.chartData[key].close = record.y;
					 if( lowPrice[k])
						 me.chartData[key].low = lowPrice[k].y;
					 if( openPrice[k])
						 me.chartData[key].open = openPrice[k].y;
					 if( highPrice[k])
						 me.chartData[key].high = highPrice[k].y;
				});
				
				me.seriesOptions[i] = {
		                name: data.tickerCode,
		                data: priceData,
		                chartData: me.chartData,
		                threshold : null,
						turboThreshold : 0
	            };
				
			});	
			
			me.performanceChartRenderer(me);
			
			//get the transaction data
			me.getTransactionsData(me);
		},
		
		toHighCharts : function(data) {
			var ret = [];
			$.each(data, function(idx, row) {
				ret.push({
					x : Date.fromISO(row.date).getTime(),
					y : row.value
				});
			});
			ret.sort(function(a, b) {
				return a.x - b.x;
			});
			return ret;
		},
		
		performanceChartRenderer:function(me){
			var baseChart = PER_CHART_CONFIG;
			
			baseChart.series = me.seriesOptions;
			
			baseChart.tooltip.formatter = function() {
				if (!this.hasOwnProperty("point")) return;
				
				var key = Highcharts.dateFormat("%e/%b/%Y", this.key);
				var userOption =  this.series.userOptions.chartData[key];
		    	var priceData = me.cloneDataAndFormat( userOption, me );
				
		    	var ret = "<b>" + key+" "+ "("+this.series.userOptions.name+")" + "</b>";

		    	// not a trading day
		    	if (priceData == undefined) {
		    		ret += "<br />";
		    		ret += "No trading data available.";
		    		return ret;
		    	}
		    	
				var openVal = Highcharts.numberFormat(priceData.open,3);
				if(parseFloat(openVal) == parseFloat("0.000")){
					openVal = "-"
				}else{
					openVal = "$" + openVal.replace(/\.?0+$/,'');
				}
				
				var closeVal = Highcharts.numberFormat(priceData.close,3);
				if(parseFloat(closeVal) == parseFloat("0.000")){
					closeVal="-";
				}else{
					closeVal="$" + closeVal.replace(/\.?0+$/,'');
				}
				
				var lowVal = Highcharts.numberFormat(priceData.low,3);
				if(parseFloat(lowVal) == parseFloat("0.000")){
					lowVal="-";
				}else{
					lowVal="$" + lowVal.replace(/\.?0+$/,'');
				}
				
				var highVal = Highcharts.numberFormat(priceData.high,3);
				if(parseFloat(highVal) == parseFloat("0.000")){
					highVal="-";
				}else{
					highVal="$" + highVal.replace(/\.?0+$/,'');
				}
				
		    	// is a trading day
		    	ret += "<span class='chart-mouseover'>";
		    	ret += "<br />";
		    	ret += "<span>Open</span>: " + openVal;
		    	ret += "<br />";
		    	ret += "<span>Close</span>: " + closeVal;
		    	ret += "<br />";
		    	ret += "<span>Low</span>: " + lowVal;
		    	ret += "<br />";
		    	ret += "<span>High</span>: " + highVal;
		    	ret += "</span>";
		    	
		    	return ret;
			};
			
			$('#performance-chart-content').highcharts('StockChart', baseChart);
		},
		
		cloneDataAndFormat: function(obj, me) {
		      var returnObj = {};
		      $.each(obj, function(key, val) {
		    	  returnObj[key] = me.roundMe(val, 3);
		      });

		      return returnObj;
	    },
	    
	    roundMe: function(val, precision) {
	    	  var roundingMultiplier = Math.pow(10, precision);
	    	  var valAsNum = isNaN(val)? 0 : parseFloat(+val);
	    	  var returnVal = Math.round( valAsNum*roundingMultiplier) / roundingMultiplier;

	    	  return returnVal;
    	},
		
		getChartData(me){
			PAGE.showLoading();
			var endpoint = PAGE.fqdn + "/sgx/company/stockListpriceHistory";
			var postType = 'POST';
    	    var params = { "id" : me.watchlistId };
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){					
						//Render Chart 
						me.renderChart(me, data);
						PAGE.hideLoading();
					}, 
					PAGE.customSGXError);
		},
		
		getTransactionsData: function(me){
			PAGE.showLoading();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/transactions";
			var postType = 'POST';
    	    var params = { "message" : me.watchlistId };
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){	
						me.transItems([]);
						me.displayTransactions([]);
						me.displayTransCompanies([]);
						if(!$.isEmptyObject(data)){
							me.displayAddTransactions(data);
							me.displayPerformanceTransactions(data);
							me.computeSelectAllTrans(me);
						}else{
							var tickersData = me.transactionTickers;
							if(!UTIL.isEmpty(tickersData)){
								var jsonObj = [];
								for(i=0 ;i<tickersData.length; i++){
									var item = {};
									item ["tickerCode"] = tickersData[i];
									item ["companyName"] = me.convertTickerCodeToCompany(tickersData[i], me);
									item ["selectedTransaction"] = ko.observable(true);
									jsonObj.push(item);
								}
								me.displayTransCompanies(jsonObj);
								me.displayTransCompanies.sort(sortByName);
						    	function sortByName(a, b){
									  var a = a.companyName.toLowerCase();
									  var b = b.companyName.toLowerCase(); 
									  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
								}
								me.computeSelectAllTrans(me);
							}
						}
						PAGE.hideLoading();
						me.showDatePicker();
						setTimeout(function(){ PAGE.resizeIframeSimple(window.parent.$('body').scrollTop()-200) }, 500);
					}, 
					PAGE.customSGXError);
		},
		
		showDatePicker: function(){
			$( "#tradeDate" ).datepicker({  maxDate: new Date() });
		},
		
		computeSelectAllTrans: function(me){
			var evaluateData = me.displayTransactions();
			if(UTIL.isEmpty(evaluateData)){
				evaluateData = me.displayTransCompanies();
			}
			me.selectAllTransaction = ko.computed({
				read: function () {
	                var selectAllTransaction = true;
	                ko.utils.arrayForEach(evaluateData, function (item) {
	                	selectAllTransaction = selectAllTransaction && item.selectedTransaction();
	                	//single chart transaction
	                	me.singleChartUnchart(me, item.tickerCode, item.selectedTransaction());
	                });
	                $('#selectAllId').prop('checked', selectAllTransaction);
	                return selectAllTransaction;
	            },
	            write: function (value) {
	                ko.utils.arrayForEach(evaluateData, function (item) {
	                    if (value) item.selectedTransaction(true);
	                    else item.selectedTransaction(false);
	                });
	                //Multi Chart Transaction
	                me.multiChartUnchart(me, value);
	            }
		    });
		},
		
		singleChartUnchart: function(me, seriesName, value){
			var chart = $('#performance-chart-content').highcharts();
			if(!UTIL.isEmpty(chart)){
				var seriesLength = chart.series.length;
				for(var i = seriesLength -1; i > -1; i--) {
					if(chart.series[i].name == seriesName){
		        		chart.series[i].setVisible(value);
					}
				}
			}
		},
		
		multiChartUnchart: function(me, value){
			var chart = $('#performance-chart-content').highcharts();
			if(!UTIL.isEmpty(chart)){
				chart.series.visible = value;
			}
		},
		
		changeTab: function(tabName){
			var me = this;
			
			if( tabName == me.activeTab ) return;
		
			$(".header-bar > ul > li.downArrow").removeClass("downArrow");
			
			if(tabName=='performance'){
				$(".header-bar > ul > li:first").addClass("downArrow");
			}else{
				$(".header-bar > ul > li:last").addClass("downArrow");
			}
			setTimeout(function(){ PAGE.resizeIframeSimple(window.parent.$('body').scrollTop()-200) }, 100);
			me.activeTab(tabName);
	    },

		getWatchListData: function(me) {
			PAGE.showLoading();
			
			var displayMessage = VALUATION.messages.messages[0];
			var endpoint = me.fqdn + "/sgx/watchlist/get";
			var postType = 'POST';
			var params = {};
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params, 
				function(data, textStatus, jqXHR){
					VALUATION.finalWL(data.watchlists.sort(sortByName));
					function sortByName(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					VALUATION.selectedValue(UTIL.getParameterByName("code"));
					PAGE.hideLoading();
					var arr = data.removed;					
					var removedTicker = arr.join(', ');
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your StockLists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
					}
				},PAGE.customSGXError
			);
			
			me.selectedValue.subscribe(function(data){
				me.watchlistId = data;
				
				var chart = $('#performance-chart-content').highcharts();
				if(!UTIL.isEmpty(chart)){
					me.seriesOptions = [];
					me.performanceChartRenderer(me);
				}
				
				//get the performance chart data
				me.getChartData(me);
				
				var watchlists = me.finalWL();
				for(var i = 0, len = watchlists.length; i < len; i++) {
					var wl = watchlists[i];
					if( wl.id == data) {
						me.clearWatchListErrors();
						me.editWLName(wl.name);	
						me.populateWatchlistCompanies(wl, me);
						break;
					}
				}				
				
				PAGE.resizeIframeSimple();
			}, me);

			ko.validation.init({insertMessages: false});
			VALUATION.newWLName.extend({
					minLength: { params: 2, message: displayMessage.watchlist.error },
					maxLength: { params: 40, message: displayMessage.watchlist.error }
			});

			me.wlNameError = ko.validation.group(VALUATION.newWLName);  //grouping error for wlName only
			me.errors = ko.validation.group(me);			
			me.errors.subscribe(function () {
				PAGE.resizeIframeSimple();
			});
			
			return me;
		},
				
		addWatchlist: function(){
			var me = this;
			var newWLNameLC = VALUATION.newWLName();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/create";
			var postType = 'POST';
    	    var params = { "message": newWLNameLC };
			var wlLength = VALUATION.finalWL().length;
			
			VALUATION.addWatchlistName([]);
			$.each(VALUATION.finalWL(), function(i, data){
				VALUATION.addWatchlistName.push(data.name.toLowerCase());
			});	
			
			if (me.wlNameError().length != 0) return;
			if (newWLNameLC.trim()==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watchlist name is empty.</p>', width: 600 }); return; }
			if ($.inArray( newWLNameLC.toLowerCase().trim(), VALUATION.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return; }
			if (wlLength >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 StockLists.</p>', width: 600 }); return; }
					
			
			PAGE.showLoading();
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){					
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					PAGE.hideLoading();
					VALUATION.finalWL(data.sort(sortByName));
					
					$.each(data, function(i,data){
						if (data.name == newWLNameLC){
							VALUATION.selectedValue(data.id);
						}						
					});
				}, 
				PAGE.customSGXError);	
			
			
			//Clears add WL after submit
			VALUATION.newWLName(null);
		},

		editWLNameSubmit: function(){
			var me=this;
			var editedName = VALUATION.editWLName().trim();
			var endpoint = PAGE.fqdn + "/sgx/watchlist/rename";
			var postType = 'POST';
			var params = { "watchlistName": editedName, "id": VALUATION.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			
			if (editedName ==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watchlist name is empty.</p>', width: 600 }); return; }
			if ($.inArray( editedName.toLowerCase(), VALUATION.addWatchlistName() ) != -1) { PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return;  }
			
			VALUATION.addWatchlistName([]);
			$.each(VALUATION.finalWL(), function(i, data){
				VALUATION.addWatchlistName.push(data.name.toLowerCase());
			});	
			PAGE.showLoading();
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					VALUATION.finalWL(data.sort(sortByName));
					PAGE.hideLoading();
				}, 
				PAGE.customSGXError,
				jsonpCallback
			);
			
				//Clears add WL after submit
			VALUATION.newWLName(null);
			VALUATION.showChange(false);
		},
		
		confirmDelete: function(){
			var deleteName = VALUATION.editWLName();
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + deleteName +'?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button floatRight ">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				VALUATION.deleteWatchlist();
				$('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });		
		},		
		
		deleteWatchlist: function(){			
			var endpoint = PAGE.fqdn + "/sgx/watchlist/delete";
			var postType = 'POST';
			var params = { "message": VALUATION.selectedValue()};
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
			
			PAGE.showLoading();
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					function sortByName(a, b){
					  var a = a.name.toLowerCase();
					  var b = b.name.toLowerCase(); 
					  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}
					PAGE.hideLoading();
					VALUATION.finalWL(data.sort(sortByName));
				}, 
				PAGE.customSGXError,
				undefined
			);			
		},
		
		populateWatchlistCompanies:function(watchlistObject, me){
		    // JSON Call for populating the companies for the selected watchlist.
			if (Object.prototype.toString.call(watchlistObject.companies) == '[object Array]' && watchlistObject.companies.length == 0){ 
			    $('#watchlistCompaniesSelect').empty()
				return;
			}
			PAGE.showLoading();
		    var endpoint = PAGE.fqdn+"/sgx/price/companyPrices";
		    var params = { "companies": watchlistObject.companies };
		    var postType = 'POST';
		    $.getJSON(endpoint+"?callback=?", { 'json': JSON.stringify(params) }).done(function(data){
		    	if(!$.isEmptyObject(data)){
		    		me.watchlistCompanies(data.companyPrice);
		    	}
		    	PAGE.hideLoading();
				setTimeout(function(){ PAGE.resizeIframeSimple(window.parent.$('body').scrollTop()-200) }, 500);

			}).fail(function(jqXHR, textStatus, errorThrown){
				console.log('error making service call');
			});

		},
		
		clearWatchListErrors: function() {
			$('.error-messages').empty();
		},
		
		_hideLoading: function() {
			$('#loading-alerts').remove();
			$('#grayout').remove();
		},
		
		_showLoading: function() {
			var container = $('.wl-companies');
			container.prepend($('<div id="grayout" class="grayout"></div>'));
			$('.wl-companies').prepend($('<div id="loading-alerts"><div class="loading-text"><img src="img/ajax-loader.gif"></div></div>'));
		},
		
		
		
		//-------------Transaction functionality starts--------------
		addTrans: function() {
			var me = this;
			var tickerCode = me.selectedCompanyValue();
			var transactionType = me.selectedAvailableType();
			var tradeDate = $.datepicker.formatDate("yy-mm-dd", Date.fromISO(me.initialTradeDate()));
			var numberOfShares = me.initialNumberOfShares();
			var costAtPurchase = me.initialCostAtPurchase();
			if(!UTIL.isEmpty(numberOfShares) && !UTIL.isEmpty(costAtPurchase) && !UTIL.isEmpty(tickerCode)){
				me.convertTickerAndClosePrice(tickerCode, me);
				var transItemModel = new insertTrans(me.disCompanyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, me.liveClosingPrice, "");
				me.transItems.push(transItemModel);
				me.clearFieldData();
				me.addTransaction(transItemModel);
			}
	    },
	    
	    saveTrans: function(){
	    	var me = this;
	    	var endpoint = PAGE.fqdn + "/sgx/watchlist/addTransaction";
			var postType = 'POST';
    	    var params = {'id' : me.watchlistId,'transactions' : me.mapTransDataToSend()};
    	    PAGE.showLoading();
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){					
						console.log(data);
						PAGE.hideLoading();
						me.transItems([]);
						me.getTransactionsData(me);
					}, 
					PAGE.customSGXError);
	    },
	    
	    mapTransDataToSend: function(){
	    	var me= this;
	    	var items = ko.toJS(me.transItems());
	    	return ko.utils.arrayMap(items, function(item) {
	    		var formattedDate = $.datepicker.formatDate("yy-mm-dd", Date.fromISO(item.tradeDate));
	    		var costAtPurchase = item.costAtPurchase.toString().replace(/,/gi,"");
	    		var numberOfShares = item.numberOfShares.toString().replace(/,/gi,"");
	    		item.tradeDate = formattedDate;
	    		item.costAtPurchase = costAtPurchase.replace("$","");
	    		item.numberOfShares = numberOfShares;
	            delete item.companyName;
	            return item;
	        });
	    },
	    
	    clearFieldData: function(){
	    	var me = this;
	    	me.initialTradeDate(null);
	    	me.initialNumberOfShares(null);
	    	me.initialCostAtPurchase(null);
	    },
	    
	    addTransaction: function(model){
	    	var me = this;
	    	var endpoint = PAGE.fqdn + "/sgx/watchlist/addTransaction";
			var postType = 'POST';
    	    var params = ko.toJS(me.mapDataToSend(model));
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){					
						console.log(data);
						me.transItems([]);
						me.getTransactionsData(me);
					}, 
					PAGE.customSGXError);
	    },
	    
	    mapDataToSend: function(item){
	            return {
	            		id : this.watchlistId,
	            		transactions : [
		                    {
		                    	id 			   : "",
				            	tickerCode     : item.tickerCode,
				            	transactionType: item.transactionType,
				            	tradeDate      : item.tradeDate,
				            	numberOfShares : item.numberOfShares,
				            	costAtPurchase : item.costAtPurchase,	
				            	currentPrice   : item.currentPrice
		                    }
		                  ]
            		  };
	    },
	    
	    calcCurrentValue: function(item, me){
	    	var numberOfShares = parseFloat(item.numberOfShares);
	    	var liveClosingPrice = parseFloat(me.liveClosingPrice);
	    	return (numberOfShares * liveClosingPrice).toFixed(2);
	    },
	    
	    calcMultiCompCurrentValue: function(noOfShares, me){
	    	var numberOfShares = parseFloat(noOfShares);
	    	var liveClosingPrice = parseFloat(me.liveClosingPrice);
	    	return (numberOfShares * liveClosingPrice).toFixed(2);
	    },
	    
	    calcTotalInvested: function(item, me){
	    	me.transactionNoOfShares += parseFloat(item.numberOfShares) ;
    		if(item.transactionType === "BUY"){
    			me.userEnteredPurchasedPrice += parseFloat(item.costAtPurchase);
    		}else{
    			me.userEnteredSellPrice += parseFloat(item.costAtPurchase);
    		}
	    },
	    
	    calcSingleCompNoOfShares: function(item, me){
	    	me.companiesNoOfShares += parseFloat(item.numberOfShares);
	    },
	    
	    calcMultiCompNoOfShares: function(data, me){
	    	var buyShare = 0;
    		var sellShare = 0;
    		var noOfShares = 0;
	    	for(i=0; i<data.length; i++){
	    		var item = data[i];
	    		if(item.transactionType === "BUY"){
	    			buyShare += parseFloat(item.numberOfShares);
	    		}else{
	    			sellShare += parseFloat(item.numberOfShares);
	    		}
	    	}
	    	noOfShares = (parseFloat(buyShare) - parseFloat(sellShare)).toFixed(2);
	    	me.companiesNoOfShares += parseFloat(noOfShares);
	    	return noOfShares;
	    },
	    
	    displayPerformanceTransactions: function(data){
	    	var me = this;
	    	var tickersData = me.transactionTickers.slice();
	    	var availTicker = [];
	    	
	    	for(i in data){
	    		availTicker.push(i);
	    		var item = null;
	    		var transItemModel = null;
	    		var muiltiCompTransModel = null;
	    		if(data[i].length > 1){
	    			item = data[i][0];
	    			me.convertTickerAndClosePrice(item.tickerCode, me);
	    			me.sumOfLastClosePrice += parseFloat(me.liveClosingPrice);
	    			var noOfShares = me.calcMultiCompNoOfShares(data[i], me);
	    			var currentValue  = me.calcMultiCompCurrentValue(noOfShares, me);
					transItemModel =  new insertPerTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate, noOfShares, 
																item.costAtPurchase, me.liveClosingPrice, "", true, currentValue);
					me.displayTransactions.push(transItemModel);
					me.getMultiCompData(data[i], transItemModel, me);
	    		}else{
	    			item = data[i][0];
	    			me.convertTickerAndClosePrice(item.tickerCode, me);
	    			me.sumOfLastClosePrice += parseFloat(me.liveClosingPrice);
	    			me.calcTotalInvested(item, me);
	    			me.calcSingleCompNoOfShares(item, me);
	    			var currentValue  = me.calcCurrentValue(item, me);
					transItemModel =  new insertPerTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate, item.numberOfShares, 
																item.costAtPurchase, me.liveClosingPrice, item.id, false, currentValue);
					me.displayTransactions.push(transItemModel);
	    		}
	    	}
	    	
	    	for(var i = 0; i<availTicker.length; i++){
	    		var loc = tickersData.indexOf(availTicker[i]);
	    		if(loc != -1) {
	    			tickersData.splice(loc, 1);
	    		}
	    	}
	    	
	    	for(var i = 0; i<tickersData.length; i++){
	    		var tickerCode = tickersData[i];
	    		var transItemCompModel =  new insertPerTrans(me.convertTickerCodeToCompany(tickerCode, me), tickerCode,"","", "", "", "", "", "", "");
				me.displayTransactions.push(transItemCompModel);
	    	}
	    	
	    	me.displayTransactions.sort(sortByName);
	    	function sortByName(a, b){
				  var a = a.companyName.toLowerCase();
				  var b = b.companyName.toLowerCase(); 
				  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
			}
	    	
	    	me.totalCalculation(me);
	    },
	    
	    totalCalculation: function(me){
			var totalInvested = ((me.transactionNoOfShares * me.userEnteredPurchasedPrice) - (me.transactionNoOfShares * me.userEnteredSellPrice)).toFixed(2);
	    	var totalCurrentValue = (me.companiesNoOfShares * me.sumOfLastClosePrice).toFixed(2);
	    	var percentageChange = ((totalInvested - totalCurrentValue) / (totalInvested * 100)).toFixed(2); 
	    	
	    	$('#totalInvested').html("$" + totalInvested.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    	$('#totalCurrentValue').html("$" + totalCurrentValue.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    	$('#percentageChange').html(percentageChange+"%");
	    	
	    	me.transactionNoOfShares = 0.00;
	    	me.userEnteredPurchasedPrice = 0.00;
	    	me.userEnteredSellPrice = 0.00;
	    	me.companiesNoOfShares = 0.00;
	    	me.sumOfLastClosePrice = 0.00;
	    },
	    
	    convertTickerCodeToCompany: function(tickerCode, me){
	    	var company = "";
	    	$.each(me.watchlistCompanies(), function (index, record) {
				  if(tickerCode === record.ticker){
					  company = record.companyName;
					  return false;
				  }
	    	});
	    	return company;
		},
		
		convertTickerAndClosePrice: function(tickerCode, me){
			me.disCompanyName = null,
			me.liveClosingPrice = null,
	    	$.each(me.watchlistCompanies(), function (index, record) {
				  if(tickerCode === record.ticker){
					  me.disCompanyName = record.companyName;
					  me.liveClosingPrice = record.price;
					  return false;
				  }
	    	});
		},
	    
	    getMultiCompData: function(data, model, me){
	    	for(i =0; i<data.length; i++){
	    		var item = data[i];
	    		me.calcTotalInvested(item, me);
	    		var muiltiCompTransModel = new insertMultiPerTrans(item.tickerCode, item.transactionType, item.tradeDate, item.numberOfShares, item.costAtPurchase, item.id);
	    		model.multiCompData.push(muiltiCompTransModel);
	    	}
	    },
	    
	    displayAddTransactions: function(data){
	    	var me = this;
	    	me.showDatePicker();
	    	var serverTransData = me.refractTransData(data);
	    	ko.utils.arrayMap(serverTransData, function(item) {
	    		me.convertTickerAndClosePrice(item.tickerCode, me);
	    		var transItemModel =  new insertDisplayTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate,
	    														item.numberOfShares, item.costAtPurchase, me.liveClosingPrice, item.id);
	    		me.transItems.push(transItemModel);
	    	});
	    	me.transItems.sort(sortByName);
	    	function sortByName(a, b){
				  var a = a.companyName().toLowerCase();
				  var b = b.companyName().toLowerCase(); 
				  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
			}
	    },
	    
	    refractTransData: function(data){
	    	var codes = [];
	    	for(i in data){
	    		for(j=0; j<data[i].length; j++){
	    			codes.push(data[i][j]);
	    		}
	    	}
	    	return codes;
	    },
	    
	    removeItem: function(item) {
	    	var me = this;
	    	PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button floatRight ">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				 if(item.id()!=""){
				    	var endpoint = PAGE.fqdn + "/sgx/watchlist/deleteTransaction";
						var postType = 'POST';
			    	    var params = {"id": me.watchlistId, "transactionId" : item.id()};
			    	    PAGE.showLoading();
						UTIL.handleAjaxRequestJSON(
								endpoint,
								postType,
								params,
								function(data, textStatus, jqXHR){					
									console.log(data);
									me.transItems.remove(item);
									PAGE.hideLoading();
									me.transItems([]);
									me.getTransactionsData(me);
								}, 
								PAGE.customSGXError);
			    	}else{
			    		me.transItems.remove(item);
			    	}
				$('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });	
			 
	    },
	    
	    removePerformanceItem: function(item) {
	    	var me = this;
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button floatRight ">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				 if(item.id!=""){
				    	var endpoint = PAGE.fqdn + "/sgx/watchlist/deleteTransaction";
						var postType = 'POST';
			    	    var params = {"id": me.watchlistId, "transactionId" : item.id};
			    	    PAGE.showLoading();
						UTIL.handleAjaxRequestJSON(
								endpoint,
								postType,
								params,
								function(data, textStatus, jqXHR){					
									console.log(data);
									me.displayTransactions.remove(item);
									PAGE.hideLoading();
									me.getTransactionsData(me);
								}, 
								PAGE.customSGXError);
			    	}else{
			    		me.displayTransactions.remove(item);
			    	}
				$('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });	
	    	
	    },
	    
	    removeIntPerItem: function(item){
	    	var me = this;
			PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button floatRight ">Cancel</span></div>', width: 400 }); 
			 $('.confirm-delete').click(function(e) {				
		    	var endpoint = PAGE.fqdn + "/sgx/watchlist/deleteTransaction";
				var postType = 'POST';
	    	    var params = {"id": me.watchlistId, "transactionId" : item.intId};
	    	    PAGE.showLoading();
				UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){					
						console.log(data);
						PAGE.hideLoading();
						me.getTransactionsData(me);
					}, 
				PAGE.customSGXError);
				$('.cboxWrapper').colorbox.close();
	        });
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });	
	    },
	    
	    toogleCompanyPlus: function(id, data){	 
	       	$('#plus_'+id).hide();
	    	$('#minus_'+id).show();
	    	
	    	$.each(data.multiCompData(), function (index, item) {
	    		var intTransType = item.intTransactionType === "BUY" ? "Transaction Type <b>BOUGHT</b>" : "Transaction Type <b>SOLD</b>";
	    		$('#comptd'+id).append("<div id='intcompdiv"+item.intId+"' style='padding-left: 22px;padding-top: inherit;font: normal 12px/12px Arial, Helvetica, sans-serif;'>" + intTransType + "</div>");
	    		$('#datetd'+id).append("<div id='intdatediv"+item.intId+"' style='padding-bottom: 5px;font: normal 12px/12px Arial, Helvetica, sans-serif;'><b>" + item.intTradeDate + "</b></div>");
	    		$('#sharetd'+id).append("<div id='intsharediv"+item.intId+"' style='padding-bottom: 5px;font: normal 12px/16px Arial, Helvetica, sans-serif;'>" + item.intNumberOfShares+ "</div>");
	    	});
	    	
	    	$('#multiCompData'+id).show();
	    	
	    	$('#tr'+id).addClass('panel');

	    	PAGE.resizeIframeSimple(window.parent.$('body').scrollTop()-200);
	    	//setTimeout(function(){ PAGE.resizeIframeSimple(); }, 500);
	    },
	    
	    toogleCompanyMinus: function(id, data){
	    	$('#multiCompData'+id).hide();
	    	$('#minus_'+id).hide();
	    	$('#plus_'+id).show();
	    	
	    	$.each(data.multiCompData(), function (index, item) {
	    		$('#intcompdiv'+item.intId).remove();
	    		$('#intdatediv'+item.intId).remove();
	    		$('#intsharediv'+item.intId).remove();
	    	});
	    	
	    	$('#tr'+id).removeClass('panel');
	    	
	    	/*$('#tr'+id).each(function(){  
    	    alert($(this).find('td'));
	    });*/
	    	
	    	PAGE.resizeIframeSimple(window.parent.$('body').scrollTop()-200);
	    },
	    
	    sortColumn: function(data, event){
	    	var me = this;
	    	if($('#'+event.target.id).hasClass('asc')){
	    		$('#'+event.target.id).removeClass('asc').addClass('desc')
	    		me.transItems.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName().toLowerCase();
	  			  var b = b.companyName().toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#'+event.target.id).removeClass('desc').addClass('asc')
	    		me.transItems.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName().toLowerCase();
	  			  var b = b.companyName().toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    },
	    
	    sortColumnAsc: function(data, event){
	    	var me = this;
	    	if($('#'+event.target.id).hasClass('asc')){
	    		$('#'+event.target.id).removeClass('asc').addClass('desc')
	    		me.displayTransactions.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#'+event.target.id).removeClass('desc').addClass('asc')
	    		me.displayTransactions.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    },
	    
	    sortColumnByAsc: function(data, event){
	    	var me = this;
	    	if($('#'+event.target.id).hasClass('asc')){
	    		$('#'+event.target.id).removeClass('asc').addClass('desc')
	    		me.displayTransCompanies.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#'+event.target.id).removeClass('desc').addClass('asc')
	    		me.displayTransCompanies.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    },  
	    
	
	};
	
	function insertTrans(companyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, currentPrice, id) {
    	var me = this;
    	me.companyName = ko.observable(companyName);
    	me.tickerCode = ko.observable(tickerCode);
    	me.transactionType = ko.observable(transactionType);
    	me.tradeDate = ko.observable(tradeDate);
    	me.numberOfShares = ko.observable(numberOfShares);
    	me.costAtPurchase = ko.observable(costAtPurchase);
    	me.currentPrice = ko.observable(currentPrice);
    	me.id = ko.observable(id);
    }
    
    function insertDisplayTrans(companyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, currentPrice, id) {
    	var me = this;
    	me.companyName = ko.observable(companyName);
    	me.tickerCode = ko.observable(tickerCode);
    	me.transactionType = ko.observable(transactionType);
    	me.tradeDate = ko.observable(tradeDate);
    	me.numberOfShares = ko.observable(numberOfShares);
    	me.costAtPurchase = ko.observable(costAtPurchase);
    	me.currentPrice = ko.observable(currentPrice);
    	me.id = ko.observable(id);
    }
	
	function insertPerTrans(companyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, lastCloseLivePrice, id, multiFlag, currentValue) {
    	var me = this;
    	me.companyName = companyName;
    	me.tickerCode = tickerCode;
    	me.transactionType = transactionType;
    	me.tradeDate = !UTIL.isEmpty(tradeDate) ? $.datepicker.formatDate("mm/dd/yy", Date.fromISO(tradeDate)) : "";
    	me.numberOfShares = !UTIL.isEmpty(numberOfShares) ? numberOfShares.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','): "" ;
    	me.costAtPurchase = costAtPurchase;
    	me.lastClosePrice = !UTIL.isEmpty(lastCloseLivePrice) ? "$" + lastCloseLivePrice: "";
    	me.id = id;
    	me.selectedTransaction = ko.observable(true);
    	me.isMultiTrans = multiFlag ;
    	me.multiCompData = ko.observableArray([]);
    	me.currentValue = !UTIL.isEmpty(currentValue) ? "$" + currentValue.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"): "";
    }
	
	function insertMultiPerTrans(tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, id) {
    	var me = this;
    	me.intTickerCode = tickerCode;
    	me.intTransactionType = transactionType;
    	me.intTradeDate = !UTIL.isEmpty(tradeDate) ? $.datepicker.formatDate("mm/dd/yy", Date.fromISO(tradeDate)) : "";
    	me.intNumberOfShares = !UTIL.isEmpty(numberOfShares) ? numberOfShares.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','): "" ;
    	me.intCostAtPurchase = !UTIL.isEmpty(costAtPurchase) ? "$" + costAtPurchase.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,"): "";
    	me.intId = id;
    }
	
	return VALUATION;
	
});