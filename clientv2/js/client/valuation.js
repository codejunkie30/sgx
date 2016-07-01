define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json","client/modules/performance-chart-config","highstock", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, PER_CHART_CONFIG) {

	
	ko.bindingHandlers.datepicker = {
		    init: function (element, valueAccessor, allBindingsAccessor) {
		        var options = allBindingsAccessor().datepickerOptions || {};
		        $(element).datepicker(options);

		        //handle the field changing
		       /* ko.utils.registerEventHandler(element, "change", function () {
		            var observable = valueAccessor();
		            observable($(element).datepicker("getDate"));
		        });*/

		        //handle disposal (if KO removes by the template binding)
		        ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
		            $(element).datepicker("destroy");
		        });
		    },
		    update: function(element, valueAccessor, allBindingsAccessor, viewModel) {
		        var value = parseInt($(element).val());
		        var date = $.datepicker.formatDate("mm/dd/yy", Date.fromISO(value));
		        $(element).val(date);
		    }
		};
	

	ko.bindingHandlers.currency = {
	    symbol: ko.observable('$'),
	    update: function(element, valueAccessor, allBindingsAccessor){
	        return ko.bindingHandlers.text.update(element,function(){
	            var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0),
                symbol = ko.utils.unwrapObservable(allBindingsAccessor().symbol === undefined ? allBindingsAccessor().symbol : ko.bindingHandlers.currency.symbol);
            	var returnValue = symbol + value.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,");
	            return returnValue === "$0.00" ? "-" : returnValue;
	        });
	    }
	};
	
	ko.bindingHandlers.currencyInput = {
		symbol: ko.observable('$'),
	    update: function (element, valueAccessor, allBindingsAccessor) {
	    	var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0),
            symbol = ko.utils.unwrapObservable(allBindingsAccessor().symbol === undefined ? allBindingsAccessor().symbol : ko.bindingHandlers.currencyInput.symbol);
	    	if(!isNaN(value)){
	    		$(element).val(symbol + value.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    	}else{
	    		$(element).val($(element).val().replace(/[^\w\s]/gi, ''));
	    	}
	    }
	};
	
	ko.bindingHandlers.largeNumber = {
	    update: function (element, valueAccessor, allBindingsAccessor) {
	    	var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0);
	    	if(!isNaN(value)){
	    		$(element).val(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','));
	    	}else{
	    		$(element).val($(element).val().replace(/[^\w\s]/gi, ''));
	    	}
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
		
		chartData : [],
		seriesOptions: [],
		transactionTickers: [],
		watchlistId: null,
		disCompanyName: null,
		liveClosingPrice: null,
		
		volumeData : [],
		closePrice : [],
		priceData : [],
		priceHistoryData : [],
		
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
			me.chartData = responseData;
			me.transactionTickers = [];
			$.each(responseData.companiesPriceHistory, function(i, data){
				var priceData = me.toHighCharts(data.priceHistory.price);
				me.transactionTickers.push(data.tickerCode);
				me.seriesOptions[i] = {
		                name: data.tickerCode,
		                data: priceData,
		                threshold : null,
						turboThreshold : 0
	            };

            	me.performanceChartRenderer(me);
			});	
			
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
			$('#performance-chart-content').highcharts('StockChart', baseChart);
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
							PAGE.resizeIframeSimple();
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
							PAGE.resizeIframeSimple();
						}
						PAGE.hideLoading();
						me.showDatePicker();
					}, 
					PAGE.customSGXError);
		},
		
		showDatePicker: function(){
			$( "#tradeDate" ).datepicker();
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
					PAGE.hideLoading();
					var arr = data.removed;					
					var removedTicker = arr.join(', ');
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your Watch Lists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
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
			
			if (me.wlNameError().length != 0) return;
			if (newWLNameLC.trim()==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watchlist name is empty.</p>', width: 600 }); return; }
			if ($.inArray( newWLNameLC.toLowerCase().trim(), VALUATION.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>Watch list name already exists.</p>', width: 600 }); return; }
			if (wlLength >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 Watch Lists.</p>', width: 600 }); return; }
			
			VALUATION.addWatchlistName([]);
			$.each(VALUATION.finalWL(), function(i, data){
				VALUATION.addWatchlistName.push(data.name.toLowerCase());
			});			
			
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
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + deleteName +'?</p> <div class="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
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
				setTimeout(function(){ PAGE.resizeIframeSimple() }, 500);

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
				PAGE.resizeIframeSimple();
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
	    		var costAtPurchase = item.costAtPurchase;
	    		var numberOfShares = item.numberOfShares;
	    		item.tradeDate = formattedDate;
	    		item.costAtPurchase = isNaN(costAtPurchase) ? costAtPurchase.replace(/[^\w\s]/gi, '') : costAtPurchase;
	    		item.numberOfShares = isNaN(numberOfShares) ? numberOfShares.replace(/[^\w\s]/gi, '') : numberOfShares;
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
					transItemModel =  new insertPerTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate, item.numberOfShares, 
																item.costAtPurchase, me.liveClosingPrice, item.id, true);
					me.displayTransactions.push(transItemModel);
					me.getMultiCompData(data[i], transItemModel, me);
					PAGE.resizeIframeSimple();
	    		}else{
	    			item = data[i][0];
	    			me.convertTickerAndClosePrice(item.tickerCode, me);
					transItemModel =  new insertPerTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate, item.numberOfShares, 
																item.costAtPurchase, me.liveClosingPrice, item.id, false);
					me.displayTransactions.push(transItemModel);
					PAGE.resizeIframeSimple();
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
	    		var transItemCompModel =  new insertPerTrans(me.convertTickerCodeToCompany(tickerCode, me), tickerCode,"","", "", "", "", "", "");
				me.displayTransactions.push(transItemCompModel);
				PAGE.resizeIframeSimple();
	    	}
	    	
	    	me.displayTransactions.sort(sortByName);
	    	function sortByName(a, b){
				  var a = a.companyName.toLowerCase();
				  var b = b.companyName.toLowerCase(); 
				  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
			}
			
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
	    		if(i!=0){
		    		var item = data[i];
		    		var muiltiCompTransModel = new insertMultiPerTrans(item.tickerCode, item.transactionType, item.tradeDate, item.numberOfShares, item.costAtPurchase, item.id);
		    		model.multiCompData.push(muiltiCompTransModel);
	    		}
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
	    		PAGE.resizeIframeSimple();
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
	    	PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
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
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper"><span class="confirm-delete button">Delete</span> <span class="cancel button">Cancel</span></div>', width: 400 }); 
			
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
			    		me.transItems.remove(item);
			    	}
				$('.cboxWrapper').colorbox.close();
	        });
			
			 $('.cancel').click(function(e) {
				$('.cboxWrapper').colorbox.close();
	        });	
	    	
	    },
	    
	    
	    toogleCompanyPlus: function(id, item){
	    	$('#toogle_'+id).show();
	    	$('#plus_'+id).hide();
	    	$('#minus_'+id).show();
	    	$('#internaltd_'+id).removeClass();
	    	PAGE.resizeIframeSimple();
	    	//setTimeout(function(){ PAGE.resizeIframeSimple(); }, 500);
	    },
	    
	    toogleCompanyMinus: function(id, item){
	    	$('#toogle_'+id).hide();
	    	$('#minus_'+id).hide();
	    	$('#plus_'+id).show();
	    	PAGE.resizeIframeSimple();
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
	
	function insertPerTrans(companyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, currentPrice, id, multiFlag) {
    	var me = this;
    	me.companyName = companyName;
    	me.tickerCode = tickerCode;
    	me.transactionType = transactionType;
    	me.tradeDate = tradeDate;
    	me.numberOfShares = numberOfShares;
    	me.costAtPurchase = costAtPurchase;
    	me.currentPrice = currentPrice;
    	me.id = id;
    	me.selectedTransaction = ko.observable(true);
    	me.isMultiTrans = multiFlag ;
    	me.multiCompData = ko.observableArray([]);
    }
	
	function insertMultiPerTrans(tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, id) {
    	var me = this;
    	me.intTickerCode = tickerCode;
    	me.intTransactionType = transactionType;
    	me.intTradeDate = tradeDate;
    	me.intNumberOfShares = numberOfShares;
    	me.intCostAtPurchase = costAtPurchase;
    	me.intId = id;
    }
	
	return VALUATION;
	
});