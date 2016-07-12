define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json","client/modules/performance-chart-config","highstock", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, PER_CHART_CONFIG) {
	
	(function($, window, document) {

		  'use strict';

		  var Paginator = function(element, options) {
		    this.el = $(element);
		    this.options = $.extend({}, $.fn.paginathing.defaults, options);

		    this.startPage = 1;
		    this.currentPage = 1;
		    this.totalItems = this.el.children().length;
		    this.totalPages = Math.ceil(this.totalItems / this.options.perPage);
		    this.container = $('<nav></nav>').addClass(this.options.containerClass);
		    this.ul = $('<ul></ul>').addClass(this.options.ulClass);

		    if(this.totalItems > this.options.perPage ){
		    	this.show(this.startPage);	
		    }
		    return this;
		  }

		  Paginator.prototype = {

		    pagination: function(type, page) {
		      var _self = this;
		      var li = $('<li></li>');
		      if(type === 'number'){
		    	  var inputBox = $('<input type="text" style="width: 18px; text-align: center"/>').attr('id', 'pagingTextBox').attr('value', _self.currentPage);
		    	  li.data('pagination-type', type);
		    	  li.append("<b>&nbsp;&nbsp;Page&nbsp;&nbsp;<b>");
		    	  li.append(inputBox);
		    	  li.append("&nbsp;&nbsp;of&nbsp;&nbsp;<b>"+_self.totalPages+"&nbsp;&nbsp; </b>");
		      }else{
			      var span = $('<span class="action-btn"></span>');
			      var cssClass = type;
			      var text = type === 'number' ? page : _self.paginationText(type);
	
			      li.addClass(cssClass);
			      li.data('pagination-type', type);
			      li.data('page', page);
			      li.append(span.html(text));
		      }
		      return li;
		    },

		    paginationText: function(type) {
		      return this.options[type + 'Text'];
		    },

		    buildPagination: function() {
		      var _self = this;
		      var pagination = [];
		      var prev = _self.currentPage - 1 < _self.startPage ? _self.startPage : _self.currentPage - 1; 
		      var next = _self.currentPage + 1 > _self.totalPages ? _self.totalPages : _self.currentPage + 1; 

		      var start, end;
		      var limit = _self.options.limitPagination;
		      var interval = 2;

		      if(limit) {
		        if(_self.currentPage <= Math.ceil(limit / 2) + 1) {
		          start = 1;
		          end = limit;
		        } else if(_self.currentPage + Math.floor(limit / 2) >= _self.totalPages) {
		          start = _self.totalPages - limit;
		          end = _self.totalPages;
		        } else {
		          start = _self.currentPage - Math.ceil(limit / 2);
		          end = _self.currentPage + Math.floor(limit / 2);
		        }
		      } else {
		        start = _self.startPage;
		        end = _self.totalPages;
		      }

		      // "First" button
		      if(_self.options.firstLast) {
		        pagination.push(_self.pagination('first', _self.startPage));
		      }
		      
		      // "Prev" button
		      if(_self.options.prevNext) {
		        pagination.push(_self.pagination('prev', prev));
		      }
		      
		      // Pagination
		      pagination.push(_self.pagination('number'));

		      // "Next" button
		      if(_self.options.prevNext) {
		        pagination.push(_self.pagination('next', next));
		      }

		      // "Last" button
		      if(_self.options.firstLast) {
		        pagination.push(_self.pagination('last', _self.totalPages));
		      }

		      return pagination;
		    },

		    render: function(page) {
		      var _self = this;
		      var options = _self.options;
		      var pagination = _self.buildPagination();

		      // Remove children before re-render (prevent duplicate)
		      _self.ul.children().remove();
		      _self.ul.append(pagination);

		      // Manage active DOM
		      var startAt = page === 1 ? 0 : (page - 1) * options.perPage;
		      var endAt = page * options.perPage;

		      _self.el.children().hide();
		      _self.el.children().slice(startAt, endAt).show();
		      
		      // Manage active state
		      _self.ul.children().each(function() {
		        var _li = $(this);
		        var type = _li.data('pagination-type');

		        switch (type) {
		          case 'number':
		            if(_li.data('page') === page) {
		              _li.addClass(options.activeClass);
		            }
		            break;
		          case 'first':
		            page === _self.startPage && _li.toggleClass(options.disabledClass);
		            break;
		          case 'last':
		            page === _self.totalPages && _li.toggleClass(options.disabledClass);
		            break;
		          case 'prev':
		            (page - 1) < _self.startPage && _li.toggleClass(options.disabledClass);
		            break;
		          case 'next':
		            (page + 1) > _self.totalPages && _li.toggleClass(options.disabledClass);
		            break;
		          default:
		            break;
		        }
		      });

		      // If insertAfter is defined
		      if(options.insertAfter) {
		        _self.container
		          .append(_self.ul)
		          .insertAfter($(options.insertAfter));
		      } else {
		        _self.el
		          .after(_self.container.append(_self.ul));
		      }
		    },

		    handle: function() {
		      var _self = this;
		      _self.container.find('li').each(function(){
		        var _li = $(this);
		        var type = _li.data('pagination-type');
		        if(type === 'number'){
		        	 $('#pagingTextBox').change(function(e) {
		        		 e.preventDefault();
		        		 var pageNumber = _self.currentPage;
		        		 try{
		        			 pageNumber = parseInt($('#pagingTextBox').val());
		        			 if(pageNumber > _self.totalPages || pageNumber < 1 || isNaN(pageNumber)){
		        				 pageNumber = _self.currentPage;
		        				 $('#pagingTextBox').val(_self.currentPage);
		        			 }
		        		 }catch(e){
		        			 $('#pagingTextBox').val(_self.currentPage);
		        		 }
		        		 _self.currentPage = pageNumber;
				          _self.show(pageNumber);
		        	 });
		         }else{

			        _li.click(function(e) {
			        	e.preventDefault();
			          	var page = _li.data('page');
			          	_self.currentPage = page;
			          	_self.show(page);
			        });
		         }
		        });
		      },

		    show: function(page) {
		      var _self = this;

		      _self.render(page);
		      _self.handle();
		    }
		  }

		  $.fn.paginathing = function(options) {
		    var _self = this;
		    var settings = (typeof options === 'object') ? options : {};

		    return _self.each(function(){
		      var paginate = new Paginator(this, options);
		      return paginate;
		    });
		  };

		  $.fn.paginathing.defaults = {
		    perPage: 25,
		    limitPagination: false,
		    prevNext: true,
		    firstLast: false,
		    prevText: '&lt;',
		    nextText: '&gt;',
		    firstText: 'First',
		    lastText: 'Last',
		    containerClass: 'pagination-container',
		    ulClass: 'pagination',
		    liClass: 'page',
		    activeClass: 'active',
		    disabledClass: 'disabled',
		    insertAfter: null
		  }

		}(jQuery, window, document));
	
	ko.bindingHandlers.datepicker = {
		    init: function (element, valueAccessor, allBindingsAccessor) {
		        var options = allBindingsAccessor().datepickerOptions || {};
		        $(element).datepicker({  maxDate: new Date(), dateFormat: 'dd/M/yy' });

		        //handle disposal (if KO removes by the template binding)
		        ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
		            $(element).datepicker("destroy");
		        });
		        
		        var value = parseInt($(element).val());
		        var date = $.datepicker.formatDate("dd/M/yy", Date.fromISO(value));
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
		seriesColors:ko.observableArray(['#5273cf','#db12be','#e527fd','#5dcc68','#ed9db4','#4c995f','#86b9b5','#f41901','#395b47','#cefb68','#f75900','#a4506c','#0d359b','#7cb5ec','#434348','#90ed7d','#f7a35c','#8085e9','#f15c80','#e4d354','#8d4653','#91e8e1','#60a114','#9866f4','#a401ad']),
		tickerColors:ko.observableArray(),

		totalCurrentValue: 0.00,
		userEnteredPurchasedPrice: 0.00,
    	userEnteredSellPrice: 0.00,
    	
		initPage: function() {
			var me = this;
			
			ko.applyBindings(me, $("body")[0]);
			
			//To get the user status premium/non
			PAGE.checkStatus();
			
			//To get the select box for the currency change in the login bar
			PAGE.libCurrency(true);
			
			me.getWatchListData(me); 
			
		},
		
		renderChart: function(me, responseData){
			me.transactionTickers = [];
			me.tickerColors.removeAll();
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

				me.tickerColors()[data.tickerCode] = me.seriesColors()[i];
				me.seriesOptions[i] = {
		                name: data.tickerCode,
		                data: priceData,
		                chartData: me.chartData,
		                threshold : null,
						turboThreshold : 0,
						color:me.seriesColors()[i]
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
			var perfChart;
			var baseChart = PER_CHART_CONFIG;
			
			baseChart.series = me.seriesOptions;
			// set the zoom
			Highcharts.setOptions({ lang: { rangeSelectorZoom: "", thousandsSep: "," }});
			
			//tooltip custom positioning
			baseChart.tooltip.positioner= function (labelWidth, labelHeight, point) {
                var tooltipX, tooltipY;
                if (point.plotX + labelWidth > perfChart.plotWidth) {
                    tooltipX = point.plotX + perfChart.plotLeft - labelWidth - 20;
                } else {
                    tooltipX = point.plotX + perfChart.plotLeft + 20;
                }
                tooltipY = point.plotY + perfChart.plotTop - 20;
                return {
                    x: tooltipX,
                    y: tooltipY
                };
            }
			
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
			
			perfChart = $('#performance-chart-content').highcharts('StockChart', baseChart).highcharts();
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
		
		getChartData: function(me){
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
						me.transItems.removeAll();
						me.displayTransactions.removeAll();
						me.displayTransCompanies.removeAll();
						if(UTIL.isEmpty(me.transactionTickers)){
							$('#valutionNoCompaniesTextDiv').show();
				    		$('#valuationContentDiv').hide();	
						}else{
							$('#valutionNoCompaniesTextDiv').hide();
				    		$('#valuationContentDiv').show();
						}
						
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
			$( "#tradeDate" ).datepicker({  maxDate: new Date(), dateFormat: 'dd/M/yy' });
		},
		
		computeSelectAllTrans: function(me){
			var evaluateData = me.displayTransactions();
			if(UTIL.isEmpty(evaluateData)){
				evaluateData = me.displayTransCompanies();
			}
			me.selectAllTransaction = ko.computed({
				read: function () {
					PAGE.showLoading();
	                var selectAllTransaction = true;
	                var showChart = false;
	                ko.utils.arrayForEach(evaluateData, function (item) {
	                	selectAllTransaction = selectAllTransaction && item.selectedTransaction();
	                	showChart = item.selectedTransaction() || showChart;
	                	//single chart transaction
	                	me.singleChartUnchart(me, item.tickerCode, item.selectedTransaction());
	                });
	                $('#selectAllId').prop('checked', selectAllTransaction);
	                if(showChart){
	                	$('#performance-chart-content').show()
	                	$('#performance-chart-header').show();	
	                	PAGE.resizeIframeSimple();
	                }else{
	                	$('#performance-chart-content').hide();
	                	$('#performance-chart-header').hide();	
	                	PAGE.resizeIframeSimple();
	                }
	                PAGE.hideLoading();
	                return selectAllTransaction;
	            },
	            write: function (value) {
	            	PAGE.showLoading();
	                ko.utils.arrayForEach(evaluateData, function (item) {
	                    if (value) item.selectedTransaction(true);
	                    else item.selectedTransaction(false);
	                });
	                //Multi Chart Transaction
	                me.multiChartUnchart(me, value);
	                PAGE.hideLoading(); 
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
					PAGE.hideLoading();
					
					me.selectedValue(UTIL.getParameterByName("code"));
					me.watchlistId = UTIL.getParameterByName("code");
					
					var watchlists = me.finalWL();
					for(var i = 0, len = watchlists.length; i < len; i++) {
						var wl = watchlists[i];
						if( wl.id == me.watchlistId) {
							me.clearWatchListErrors();
							me.editWLName(wl.name);	
							me.populateWatchlistCompanies(wl, me);
							break;
						}
					}
					
					//get the performance chart data
					me.getChartData(me);
					
					PAGE.resizeIframeSimple();
					
					var arr = data.removed;					
					var removedTicker = arr.join(', ');
					if (arr.length > 0) {
						$('<div class="save">The companies below have been removed from one or more of your StockLists. No data is available at this time.<br>'+removedTicker+'</div>').insertBefore('header.header');
					}
				},PAGE.customSGXError
			);
			
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
		
		watchListChange: function(data, event){
			var me = this;
			me.transItems.removeAll();
			me.displayTransactions.removeAll();
			me.displayTransCompanies.removeAll();
			me.watchlistCompanies.removeAll();
			
			me.watchlistId = data.selectedValue();
			
			$('.pagination-container').remove();
			
			var chart = $('#performance-chart-content').highcharts();
			if(!UTIL.isEmpty(chart)){
				me.seriesOptions = [];
				me.performanceChartRenderer(me);
			}
			
			var watchlists = me.finalWL();
			for(var i = 0, len = watchlists.length; i < len; i++) {
				var wl = watchlists[i];
				if( wl.id == me.watchlistId) {
					me.clearWatchListErrors();
					me.editWLName(wl.name);	
					me.populateWatchlistCompanies(wl, me);
					break;
				}
			}
			
			//get the performance chart data
			me.getChartData(me);
			
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
			if (newWLNameLC.trim()==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name is empty.</p>', width: 600 }); return; }
			if ($.inArray( newWLNameLC.toLowerCase().trim(), VALUATION.addWatchlistName() ) != -1) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name already exists.</p>', width: 600 }); return; }
			if (wlLength >= 10) { PAGE.modal.open({ type: 'alert',  content: '<p>You can create up to 10 StockLists.</p>', width: 600 }); return; }
					
			
			PAGE.showLoading();
			
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){
					me.transItems.removeAll();
					me.displayTransactions.removeAll();
					me.displayTransCompanies.removeAll();
					me.watchlistCompanies.removeAll();
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
							
							me.watchlistId = data.id;
							
							$('.pagination-container').remove();
							
							var chart = $('#performance-chart-content').highcharts();
							if(!UTIL.isEmpty(chart)){
								me.seriesOptions = [];
								me.performanceChartRenderer(me);
							}
							
							//get the performance chart data
							me.getChartData(me);
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
			
			VALUATION.addWatchlistName([]);
			$.each(VALUATION.finalWL(), function(i, data){
				VALUATION.addWatchlistName.push(data.name.toLowerCase());
			});	
			
			if (editedName ==="" ) {  PAGE.modal.open({ type: 'alert',  content: '<p>StockList name is empty.</p>', width: 600 }); return; }
			if ($.inArray( editedName.toLowerCase(), VALUATION.addWatchlistName() ) != -1) { PAGE.modal.open({ type: 'alert',  content: '<p>StockList name already exists.</p>', width: 600 }); return;  }
					
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
			
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + deleteName +'?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
			
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
			    $('#watchlistCompaniesSelect').val(null);
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
			var transItemModel;
			var tickerCode = me.selectedCompanyValue();
			var transactionType = me.selectedAvailableType();
			var tradeDate = $.datepicker.formatDate("dd/M/yy", Date.fromISO(me.initialTradeDate()));
			var numberOfShares = me.initialNumberOfShares();
			var costAtPurchase = me.initialCostAtPurchase();
			if(!UTIL.isEmpty(numberOfShares) && !UTIL.isEmpty(costAtPurchase) && !UTIL.isEmpty(tickerCode)){
				me.convertTickerAndClosePrice(tickerCode, me);
				transItemModel = new insertTrans(me.disCompanyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, me.liveClosingPrice, "");
				me.transItems.push(transItemModel);
				me.clearFieldData();
			}
			if(me.transItems().length){
				me.buySellValidate() ? me.saveTrans() : me.transItems.remove(transItemModel) ;
			}
	    },
	    
	    saveTrans: function(){
	    	$('.pagination-container').remove();
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
						$('.save').remove();
						$('<div class="save">Your changes have been saved.</div>').insertBefore('header.header').delay(4000).fadeOut(function() {$(this).remove();});
						PAGE.hideLoading();
						me.transItems([]);
						me.getTransactionsData(me);
					}, 
					PAGE.customSGXError);
					//me.clearFieldData();
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
	    	me.selectedCompanyValue(null);
	    	me.initialTradeDate(null);
	    	me.initialNumberOfShares(null);
	    	me.initialCostAtPurchase(null);
	    },
	    
	    /*addTransaction: function(model){
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
	    },*/
	    
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
    		if(item.transactionType === "BUY"){
    			me.userEnteredPurchasedPrice = parseFloat( parseFloat(me.userEnteredPurchasedPrice) + ( item.numberOfShares * item.costAtPurchase) ).toFixed(2);
    		}else{
    			me.userEnteredSellPrice = parseFloat( parseFloat(me.userEnteredSellPrice) + (item.numberOfShares * item.costAtPurchase) ).toFixed(2);
    		}
	    },
	    
	    calcMultiCompNoOfShares: function(data, me){
	    	var buyShare = 0.00;
    		var sellShare = 0.00;
    		var noOfShares = 0.00;
	    	for(i=0; i<data.length; i++){
	    		var item = data[i];
	    		if(item.transactionType === "BUY"){
	    			buyShare += parseFloat(item.numberOfShares);
	    		}else{
	    			sellShare += parseFloat(item.numberOfShares);
	    		}
	    	}
	    	noOfShares = (parseFloat(buyShare) - parseFloat(sellShare)).toFixed(2);
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
	    			var noOfShares = me.calcMultiCompNoOfShares(data[i], me);
	    			var currentValue  = me.calcMultiCompCurrentValue(noOfShares, me);
	    			me.totalCurrentValue = me.totalCurrentValue + parseFloat(currentValue);
					transItemModel =  new insertPerTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate, noOfShares, 
																item.costAtPurchase, me.liveClosingPrice, "", true, currentValue);
					me.displayTransactions.push(transItemModel);
					me.getMultiCompData(data[i], transItemModel, me);
	    		}else{
	    			item = data[i][0];
	    			me.convertTickerAndClosePrice(item.tickerCode, me);
	    			me.calcTotalInvested(item, me);
	    			var currentValue  = me.calcCurrentValue(item, me);
	    			me.totalCurrentValue = me.totalCurrentValue + parseFloat(currentValue);
					transItemModel =  new insertPerTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate, parseFloat(item.numberOfShares).toFixed(2), 
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
	    	var totalInvested = parseFloat(me.userEnteredPurchasedPrice - me.userEnteredSellPrice).toFixed(2);
	    	var totalCurrentValue = parseFloat(me.totalCurrentValue).toFixed(2); 
	    	var percentageChange = (((totalCurrentValue - totalInvested) / totalInvested) * 100).toFixed(2); 
	    	
	    	$('#totalInvested').html("$" + totalInvested.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    	$('#totalCurrentValue').html("$" + totalCurrentValue.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    	$('#percentageChange').html(percentageChange+"%");
	    	
	    	me.userEnteredPurchasedPrice = 0.00;
	    	me.userEnteredSellPrice = 0.00;
	    	me.totalCurrentValue = 0.00;
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
	    		var muiltiCompTransModel = new insertMultiPerTrans(item.tickerCode, item.transactionType, item.tradeDate, parseFloat(item.numberOfShares).toFixed(2), item.costAtPurchase, item.id);
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
	    	
    		$('#transItemsId').paginathing({
			    insertAfter: '#transItemsId'
			});
	    	
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
	    
	    isDeleteValid: function(transactionType, tickerCode, numberOfShares){
	    	var flag = true;
	    	numberOfShares = numberOfShares.toString().replace(/,/gi,"");
	    	if(transactionType === "BUY"){
	    		 var me = this;
		    	 var bought = 0.00;
		    	 var sell = 0.00;
		    	 ko.utils.arrayForEach(me.transItems(), function (item) {
		    		 if(item.tickerCode() === tickerCode){
		 	    		 var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
		    			 if(item.transactionType() === "BUY"){
		    				 bought = parseFloat( parseFloat(bought) + parseFloat(numberOfShares) ).toFixed(2);
		    			 }else{
		    				 sell = parseFloat( parseFloat(sell) + parseFloat(numberOfShares) ).toFixed(2);
		    			 }
		    		 }
		    	 });
		    	 bought = parseFloat( parseFloat(bought) - parseFloat(numberOfShares) ).toFixed(2);
		    	 if(parseFloat(sell) > parseFloat(bought)){
		    		 PAGE.modal.open({ type: 'alert',  content: '<p>You cannot delete this transaction as it would create a negative position for this security.</p>', width: 400 });
		    		 flag = false;
		    	 }
	    	}
	    	
	    	return flag;
	    },
	    
	    removeItem: function(item) {
	    	var me = this;
	    	if( me.isDeleteValid(item.transactionType(), item.tickerCode(), item.numberOfShares()) ){
	    		$('.pagination-container').remove();
		    	PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
				
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
	    	}
			 
	    },
	    
	    removePerformanceItem: function(item) {
	    	var me = this;
	    	$('.pagination-container').remove();
			PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
			
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
	    	if( me.isDeleteValid(item.intTransactionType, item.intTickerCode, item.intNumberOfShares) ){
		    	$('.pagination-container').remove();
				PAGE.modal.open({ content: '<p>Are you sure you want to delete the transaction ?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
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
	    	}
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
	    		$('#'+event.target.id).removeClass('asc').addClass('desc');
	    		me.transItems.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName().toLowerCase();
	  			  var b = b.companyName().toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#'+event.target.id).removeClass('desc').addClass('asc');
	    		me.transItems.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName().toLowerCase();
	  			  var b = b.companyName().toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    	$(".pagination-container").remove();
	    	$('#transItemsId').paginathing({
			    insertAfter: '#transItemsId'
			});
	    },
	    
	    sortColumnByAsc: function(data, event){
	    	var me = this;
	    	if($('#'+event.target.id).hasClass('asc')){
	    		$('#'+event.target.id).removeClass('asc').addClass('desc');
	    		me.displayTransCompanies.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#'+event.target.id).removeClass('desc').addClass('asc');
	    		me.displayTransCompanies.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    },  
	    
	    perSortByCompanyName: function(data, event){
	    	var me = this;
	    	if($('#perCompanyName').hasClass('asc')){
	    		$('#perCompanyName').removeClass('asc').addClass('desc');
	    		me.displayTransactions.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#perCompanyName').removeClass('desc').addClass('asc');
	    		me.displayTransactions.sort(sortByName);
	    	    function sortByName(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    	$('#perTradeDate').removeClass('dateasc');
	    	$('#perTradeDate').removeClass('datedesc');
	    	$('#perNumOfShares').removeClass('shareasc');
	    	$('#perNumOfShares').removeClass('sharedesc');
	    	$('#perLastClosePrice').removeClass('closepasc');
	    	$('#perLastClosePrice').removeClass('closepdesc');
	    	$('#perCurPrice').removeClass('currrentpasc');
	    	$('#perCurPrice').removeClass('currrentpdesc');
	    },
	    
	    perSortbyTradeDate: function(data, event){
	    	var me = this;
	    	if($('#perTradeDate').hasClass('dateasc')){
	    		$('#perTradeDate').removeClass('dateasc').addClass('datedesc');
	    		me.displayTransactions.sort(sortByTradeDate);
	    	    function sortByTradeDate(a, b){
	    	    	var a = !UTIL.isEmpty(a.tradeDate) ? new Date(a.tradeDate).getTime() : "";
    	    		var b = !UTIL.isEmpty(b.tradeDate) ? new Date(b.tradeDate).getTime() : ""; 
    	    		if (a == "" && b){
	    	    	    return 1;
	    	    	}
	    	    	if (b == "" && a){
	    	    	    return -1;
	    	    	}
    	    		return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#perTradeDate').removeClass('datedesc').addClass('dateasc');
	    		me.displayTransactions.sort(sortByTradeDate);
	    	    function sortByTradeDate(a, b){
    	    		var a = !UTIL.isEmpty(a.tradeDate) ? new Date(a.tradeDate).getTime() : "";
    	    		var b = !UTIL.isEmpty(b.tradeDate) ? new Date(b.tradeDate).getTime() : "";
	  			 	if (a == "" && b){
	    	    	    return 1;
	    	    	}
	    	    	if (b == "" && a){
	    	    	    return -1;
	    	    	}
	  			 	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    	$('#perCompanyName').removeClass('asc');
	    	$('#perCompanyName').removeClass('desc');
	    	$('#perNumOfShares').removeClass('shareasc');
	    	$('#perNumOfShares').removeClass('sharedesc');
	    	$('#perLastClosePrice').removeClass('closepasc');
	    	$('#perLastClosePrice').removeClass('closepdesc');
	    	$('#perCurPrice').removeClass('currrentpasc');
	    	$('#perCurPrice').removeClass('currrentpdesc');
	    	
	    },
	    
	    perSortbyNumShares: function(data, event){
	    	var me = this;
	    	if($('#perNumOfShares').hasClass('shareasc')){
	    		$('#perNumOfShares').removeClass('shareasc').addClass('sharedesc')
	    		me.displayTransactions.sort(sortByNumShares);
	    	    function sortByNumShares(a, b){
	    	    	var a = parseFloat(a.numberOfShares);
	    	    	var b = parseFloat(b.numberOfShares); 
		  			if (!$.isNumeric(a) && b){
	    	    	    return 1;
		  			}
			  	  	if (!$.isNumeric(b) && a){
	    	    	    return -1;
			  	  	}
			  	  	return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#perNumOfShares').removeClass('sharedesc').addClass('shareasc')
	    		me.displayTransactions.sort(sortByNumShares);
	    	    function sortByNumShares(a, b){
	  			  	var a = parseFloat(a.numberOfShares);
	  			  	var b = parseFloat(b.numberOfShares);
	  			  	if (!$.isNumeric(a) && b){
	    	    	    return 1;
		  			}
			  	  	if (!$.isNumeric(b) && a){
	    	    	    return -1;
			  	  	}
	  			  	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    	$('#perCompanyName').removeClass('asc');
	    	$('#perCompanyName').removeClass('desc');
	    	$('#perTradeDate').removeClass('dateasc');
	    	$('#perTradeDate').removeClass('datedesc');
	    	$('#perLastClosePrice').removeClass('closepasc');
	    	$('#perLastClosePrice').removeClass('closepdesc');
	    	$('#perCurPrice').removeClass('currrentpasc');
	    	$('#perCurPrice').removeClass('currrentpdesc');
	    },
	    
	    perSortLastClsPrice: function(data, event){
	    	var me = this;
	    	if($('#perLastClosePrice').hasClass('closepasc')){
	    		$('#perLastClosePrice').removeClass('closepasc').addClass('closepdesc')
	    		me.displayTransactions.sort(sortByClsPrice);
	    	    function sortByClsPrice(a, b){
	    	       var a = a.lastClosePrice.toString().replace(/,/gi,"");
	    	       var b = b.lastClosePrice.toString().replace(/,/gi,"");
	    	       a = parseFloat(a.replace("$",""));
		  		   b = parseFloat(b.replace("$",""));
		  		   if (!$.isNumeric(a) && b){
	    	    	    return 1;
			  	   }
			  	   if (!$.isNumeric(b) && a){
	    	    	    return -1;
			  	   }
			  	   return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#perLastClosePrice').removeClass('closepdesc').addClass('closepasc')
	    		me.displayTransactions.sort(sortByClsPrice);
	    	    function sortByClsPrice(a, b){
	    	       var a = a.lastClosePrice.toString().replace(/,/gi,"");
		    	   var b = b.lastClosePrice.toString().replace(/,/gi,"");
		    	   a = parseFloat(a.replace("$",""));
			  	   b = parseFloat(b.replace("$",""));
			  	   if (!$.isNumeric(a) && b){
	    	    	    return 1;
			  	   }
			  	   if (!$.isNumeric(b) && a){
	    	    	    return -1;
			  	   }
			  	   return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    	$('#perCompanyName').removeClass('asc');
	    	$('#perCompanyName').removeClass('desc');
	    	$('#perTradeDate').removeClass('dateasc');
	    	$('#perTradeDate').removeClass('datedesc');
	    	$('#perNumOfShares').removeClass('shareasc');
	    	$('#perNumOfShares').removeClass('sharedesc');
	    	$('#perCurPrice').removeClass('currrentpasc');
	    	$('#perCurPrice').removeClass('currrentpdesc');
	    },
	    
	    perSortCurrentValue: function(data, event){
	    	var me = this;
	    	if($('#perCurPrice').hasClass('currrentpasc')){
	    		$('#perCurPrice').removeClass('currrentpasc').addClass('currrentpdesc')
	    		me.displayTransactions.sort(sortByCurrentVal);
	    	    function sortByCurrentVal(a, b){
	    	    	var a = a.currentValue.toString().replace(/,/gi,"");
	    	    	var b = b.currentValue.toString().replace(/,/gi,"");
	    	    	a = parseFloat(a.replace("$",""));
		  			b = parseFloat(b.replace("$",""));  
		  			if (!$.isNumeric(a) && b){
	    	    	    return 1;
		  			}
			  	  	if (!$.isNumeric(b) && a){
	    	    	    return -1;
			  	  	}
			  	  	return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    }
	    	}else{
	    		$('#perCurPrice').removeClass('currrentpdesc').addClass('currrentpasc')
	    		me.displayTransactions.sort(sortByCurrentVal);
	    	    function sortByCurrentVal(a, b){
	    	    	var a = a.currentValue.toString().replace(/,/gi,"");
	    	    	var b = b.currentValue.toString().replace(/,/gi,"");
	    	    	a = parseFloat(a.replace("$",""));
		  			b = parseFloat(b.replace("$","")); 
		  			if (!$.isNumeric(a) && b){
	    	    	    return 1;
		  			}
			  	  	if (!$.isNumeric(b) && a){
	    	    	    return -1;
			  	  	}
			  	  	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    }
	    	}
	    	$('#perCompanyName').removeClass('asc');
	    	$('#perCompanyName').removeClass('desc');
	    	$('#perTradeDate').removeClass('dateasc');
	    	$('#perTradeDate').removeClass('datedesc');
	    	$('#perNumOfShares').removeClass('shareasc');
	    	$('#perNumOfShares').removeClass('sharedesc');
	    	$('#perLastClosePrice').removeClass('closepasc');
	    	$('#perLastClosePrice').removeClass('closepdesc');
	    },
	    
	    buySellValidate: function(){
	    	var me = this;
	    	var flag = true;
	    	var bought = 0.00;
	    	var sell = 0.00;
	    	var tickers = me.transactionTickers;
    		
    		$.each(tickers, function(i, data){
    			ko.utils.arrayForEach(me.transItems(), function (item) {
	   	    		 if(item.tickerCode() === data){
	   	 	    		 var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
	   	    			 if(item.transactionType() === "BUY"){
	   	    				 bought = parseFloat( parseFloat(bought) + parseFloat(numberOfShares) ).toFixed(2);
	   	    			 }else{
	   	    				 sell = parseFloat( parseFloat(sell) + parseFloat(numberOfShares) ).toFixed(2);
	   	    			 }
	   	    		 }
   	    	 	});
    			if( parseFloat(sell) > parseFloat(bought) ){
    				PAGE.modal.open({ type: 'alert',  content: '<p>You are trying to sell more shares that you have purchased. Please correct and try again.</p>', width: 400 });
    				flag = false;
    				return false;
   	    	 	}
    			bought = 0.00;
    	    	sell = 0.00;
			});	
    		
    		return flag;
	    },
	    
	    /*buySellInitialValidate: function(data, event){
	    	var me = this;
	    	var bought = 0.00;
	    	var sell = 0.00;
	    	var share = 0.00;
	    	
	    	if(data.transItems().length){
	    		var tickerCode = data.selectedCompanyValue();
		    	 ko.utils.arrayForEach(data.transItems(), function (item) {
		    		 if(item.tickerCode() === tickerCode){
		 	    		 var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
		    			 if(item.transactionType() === "BUY"){
		    				 bought = parseFloat( parseFloat(bought) + parseFloat(numberOfShares) ).toFixed(2);
		    			 }else{
		    				 sell = parseFloat( parseFloat(sell) + parseFloat(numberOfShares) ).toFixed(2);
		    			 }
		    		 }
		    	 });
	    	}
	    	
	    	share = !UTIL.isEmpty(data.initialNumberOfShares()) ? data.initialNumberOfShares() : 0.00;
    		if(data.selectedAvailableType() === "BUY"){
			 	bought = parseFloat( parseFloat(bought) + parseFloat(share) ).toFixed(2);
		 	}else{
		 		sell = parseFloat( parseFloat(sell) + parseFloat(share) ).toFixed(2);
		 	}
	    	
	    	if(parseFloat(sell) > parseFloat(bought)){
	    		me.buySellValidateFlag = false;
	    		 PAGE.modal.open({ type: 'alert',  content: '<p>You are trying to sell more shares that you have purchased. Please correct and try again.</p>', width: 400 });
	    		 return;
	    	 }
	    }*/
	
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
    	me.tradeDate = !UTIL.isEmpty(tradeDate) ? $.datepicker.formatDate("dd/M/yy", Date.fromISO(tradeDate)) : "";
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
    	me.intTradeDate = !UTIL.isEmpty(tradeDate) ? $.datepicker.formatDate("dd/M/yy", Date.fromISO(tradeDate)) : "";
    	me.intNumberOfShares = !UTIL.isEmpty(numberOfShares) ? numberOfShares.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','): "" ;
    	me.intCostAtPurchase = !UTIL.isEmpty(costAtPurchase) ? "$" + costAtPurchase.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,"): "";
    	me.intId = id;
    }
	
	return VALUATION;
	
});