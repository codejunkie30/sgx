define([ "wmsi/utils", "knockout", "knockout-validate", "text!client/data/messages.json","client/modules/performance-compareChart-config","highstock", "jquery-placeholder" ], function(UTIL, ko, validation, MESSAGES, PER_CHART_CONFIG) {
	
	ko.components.register('premium-preview', { require: 'client/components/premium-preview'});
	(function($, window, document) {

		  'use strict';

		  var Paginator = function(element, options) {
		    this.el = $(element);
		    this.options = $.extend({}, $.fn.paginathing.defaults, options);

		    this.startPage = 1;
		    this.currentPage = this.options.currentPage;
		    this.totalItems = this.el.children().length;
		    this.totalPages = Math.ceil(this.totalItems / this.options.perPage);
		    this.container = $('<nav></nav>').addClass(this.options.containerClass);
		    this.ul = $('<ul></ul>').addClass(this.options.ulClass);
		    this.currentPage = this.totalPages < this.currentPage ? this.currentPage -1 : this.currentPage;
		    if(this.totalItems > this.options.perPage ){
		    	this.show(this.currentPage);
		    }
		    return this;
		  }

		  Paginator.prototype = {

		    pagination: function(type, page) {
		      var _self = this;
		      var li = $('<li></li>');
		      if(type === 'number'){
		    	  var inputBox = $('<input type="text" style="width: 25px; text-align: center"/>').attr('id', 'pagingTextBox').attr('value', _self.currentPage);
		    	  li.data('pagination-type', type);
		    	  li.append("<b>&nbsp;&nbsp;Page&nbsp;&nbsp;<b>");
		    	  li.append(inputBox);
		    	  li.append("<b>&nbsp;&nbsp;of&nbsp;&nbsp;"+_self.totalPages+"&nbsp;&nbsp; </b>");
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
		        		 VALUATION.addSaveTransactions();
				         if(!VALUATION.hasFieldErrors){
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
					          PAGE.resizeIframeSimple($('#transTableContainer').offset().top);
				         }else{
				        	 $('#pagingTextBox').val(_self.currentPage);
				         }
		        	 });
		         }else{

			        _li.click(function(e) {
			        	e.preventDefault();
			        	VALUATION.addSaveTransactions();
			        	if(!VALUATION.hasFieldErrors){
			        		var page = _li.data('page');
				          	_self.currentPage = page;
				          	_self.show(page);
				          	PAGE.resizeIframeSimple($('#transTableContainer').offset().top);
			        	}
			        });
		         }
		         location.href = '#transactionSave';
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
		    currentPage: 1,
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
		    insertAfter: '#transItemsIdTable'
		  }

		}(jQuery, window, document));
	
	// Input field to only allow numeric values
	ko.bindingHandlers.valuationNumeric = {
	    init: function (element) {
	        $(element).on("keydown", function (event) {
	            // Allow: backspace, delete, tab, escape, and enter
	            if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 ||
	                // Allow: Ctrl+A
	                (event.keyCode == 65 && event.ctrlKey === true) ||
	                // Allow: . ,
	                (event.keyCode == 190 || event.keyCode == 110) ||
	                // Allow: home, end, left, right
	                (event.keyCode >= 35 && event.keyCode <= 39)) {
	                // let it happen, don't do anything
	                return;
	            }
	            else {
	                // Ensure that it is a number and stop the keypress
	                if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105)) {
	                    event.preventDefault();
	                }
	            }
	        });
	        $(element).on("change", function (event) {
	        	event.preventDefault();
	        	var textBox = event.target;
	        	
	        	textBox.value = textBox.value.replace("$","");
	        	textBox.value = textBox.value.replace(/,/gi,"");
	        	
	        	var max = 99999999.999;
	        	if(textBox.value > max){
	        		textBox.value = "";
	        		textBox.focus();
	        	}else{
	        		textBox.value = Number(parseFloat(textBox.value).toString().match(/^\d+(?:\.\d{0,3})?/));
	        	}
	        	
	        	if( (parseFloat(textBox.value) === parseFloat("0")) || (textBox.value==="")){
	        		textBox.value = "";
	        		textBox.focus();
	        		$('#'+textBox.id).css({"borderColor":"red"});
	        		PAGE.modal.open({ type: 'alert',  content: '<p>Please correct errors highlighted in red.</p>', width: 400 });
	        		VALUATION.hasFieldErrors =  true;
	        	}else{
	        		VALUATION.hasFieldErrors =  false;
	        		$('#'+textBox.id).css({"borderColor":""});
	        	}
	        });
	    }    
	};
	
	ko.bindingHandlers.companiesOnchange = {
	    init: function (element) {
	        $(element).on("change", function (event) {
	        	event.preventDefault();
	        	var textBox = event.target;
	        	if(textBox.value){
	        		$('#'+textBox.id).css({"borderColor":""});
	        	}
	        });
	    }    
	};
	
	ko.bindingHandlers.datepicker = {
		    init: function (element, valueAccessor, allBindingsAccessor) {
		        var options = allBindingsAccessor().datepickerOptions || {};
		        $(element).datepicker({
		        	maxDate: new Date(),
		        	dateFormat: 'yy/mm/dd',
		        	constrainInput: false,
		        	yearRange: "-100:+0",
		        	changeMonth: true,
			        changeYear: true,
					beforeShow: function() {
						setTimeout(function(){
			            	$('.ui-datepicker').css('z-index', 999);
			        	}, 0);
					} 
        		});

		        //handle disposal (if KO removes by the template binding)
		        ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
		            $(element).datepicker("destroy");
		        });
		        
		        if($(element).val()){
			        var value = parseInt($(element).val());
			        var date = $.datepicker.formatDate("yy/mm/dd", Date.fromISO(value));
			        $(element).val(date);
		        }
		        
	    	    $(element).on("change", function (event) {
		        	event.preventDefault();
		        	var textBox = event.target;
		        	var flag = false;
		        	var maxYear = (new Date()).getFullYear();
		        	var maxDate = new Date();
		        	
		        	if(textBox.value){
		        		var re=/^(\d{4})\/(\d{1,2})\/(\d{1,2})$/;
		        		if( regs = textBox.value.match(re) ){
		        			if( regs[1] > maxYear || regs[2] < 1 || regs[2] > 12 ||
		        					regs[3] < 1 || regs[3] > 31 || new Date(textBox.value) > maxDate ){
		        				flag = true;
		        			}else{
		        				var lastDayOfMonth = VALUATION.daysInMonth(regs[2], regs[1]);
		        				if(regs[3] > lastDayOfMonth){
		        					flag = true;
		        				}
		        			}
		        		}else{
		        			flag = true;
		        		}
		        	}else{
		        		flag = true;
		        	}
		        	
		        	if(flag){
		        		textBox.value = "";
		        		$('#'+textBox.id).css({"borderColor":"red"});
		        		PAGE.modal.open({ type: 'alert',  content: '<p>Please correct errors highlighted in red.</p>', width: 400 });
		        		VALUATION.hasFieldErrors =  true;
		        		VALUATION.dateValidatorFlag = true;
		        	}else{
		        		$('#'+textBox.id).css({"borderColor":""});
		        		VALUATION.hasFieldErrors =  false;
		        		VALUATION.dateValidatorFlag = false;
		        	}
		        	
		        });
		    }
		};
	

	ko.bindingHandlers.currency = {
	    symbol: ko.observable('$'),
	    init: function(element, valueAccessor, allBindingsAccessor){
	        return ko.bindingHandlers.text.update(element,function(){
	            var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0),
                symbol = ko.utils.unwrapObservable(allBindingsAccessor().symbol === undefined ? allBindingsAccessor().symbol : ko.bindingHandlers.currency.symbol);
            	var returnValue = symbol + value;
	            return returnValue === "$0.000" ? "-" : returnValue;
	        });
	    }
	};
	
	ko.bindingHandlers.currencyInput = {
		symbol: ko.observable('$'),
	    init: function (element, valueAccessor, allBindingsAccessor) {
	    	var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0),
            symbol = ko.utils.unwrapObservable(allBindingsAccessor().symbol === undefined ? allBindingsAccessor().symbol : ko.bindingHandlers.currencyInput.symbol);
    		$(element).val(symbol + value.toFixed(3).replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    }
	};
	
	ko.bindingHandlers.largeNumber = {
	    init: function (element, valueAccessor, allBindingsAccessor) {
	    	var value = +(ko.utils.unwrapObservable(valueAccessor()) || 0);
    		$(element).val(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','));
	    }
	};

	var VALUATION = {
		sectionName:'Valuation',
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
		chartPoints:[],
		seriesColors:ko.observableArray(['#5273cf','#db12be','#e527fd','#5dcc68','#ed9db4','#4c995f','#86b9b5','#f41901','#395b47','#cefb68','#f75900','#a4506c','#0d359b','#7cb5ec','#434348','#90ed7d','#f7a35c','#8085e9','#f15c80','#e4d354','#8d4653','#91e8e1','#60a114','#9866f4','#a401ad']),
		tickerColors:ko.observableArray(),

		totalCurrentValue: 0.00,
		userEnteredPurchasedPrice: 0.00,
    	userEnteredSellPrice: 0.00,
    	
    	hasFieldErrors : false,
    	record_modified: false,
    	
    	validatedCompanies: [],
    	validateFlag: true,
    	
    	dateValidatorFlag: false,
    	
		initPage: function() {
			var me = this;
			
			ko.applyBindings(me, $("body")[0]);
			
			//To get the user status premium/non
			PAGE.checkStatus();
			
			//To get the select box for the currency change in the login bar
			PAGE.libCurrency(true);
			
			me.getWatchListData(me); 
			me.trackPage("SGX - StockList Valuation Performance");
		},
		
		renderChart: function(me, responseData){
			VALUATION.transactionTickers = [];
			me.tickerColors.removeAll();
			VALUATION.chartPoints = [];
			$.each(responseData.companiesPriceHistory, function(i, data){
				VALUATION.transactionTickers.push(data.tickerCode);
				VALUATION.chartPoints[data.tickerCode] = {}
				
				var allData = data.priceHistory;
				var priceData = me.toHighCharts(allData.price);
				VALUATION.chartPoints[data.tickerCode].priceData = priceData;
				VALUATION.chartPoints[data.tickerCode].color = me.seriesColors()[i];

				me.tickerColors()[data.tickerCode] = me.seriesColors()[i];
				me.seriesOptions[i] = {
		                name: data.tickerCode,
		                data: priceData,
		                threshold : null,
						turboThreshold : 0,
						color:me.seriesColors()[i]
	            };
				
			});	

			me.performanceChartRenderer(me.seriesOptions);
			
			//get the transaction data
			me.getTransactionsData(me);
			
		},
		retrieveDateOnlyInMilliseconds: function(milliseconds){
			var date = new Date(milliseconds);
			date.setHours(0,0,0,0);
			return date.getTime();
		},
		toHighCharts : function(data) {
			var ret = [];
			$.each(data, function(idx, row) {
				ret.push({
					x : VALUATION.retrieveDateOnlyInMilliseconds(Date.fromISO(row.date).getTime()),
					y : row.value
				});
			});
			ret.sort(function(a, b) {
				return a.x - b.x;
			});
			return ret;
		},
		
		performanceChartRenderer:function(seriesOptions, newOptions){
			var perfChart;
			var baseChart = PER_CHART_CONFIG;
			
			baseChart.series = seriesOptions;
			// set the zoom
			Highcharts.setOptions({ lang: { rangeSelectorZoom: "", thousandsSep: "," }, global: {useUTC: false}});
			if(newOptions){
				baseChart.rangeSelector.selected = newOptions.rangeSelector.selected;
			}
			
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
    	    var params = { "id" : VALUATION.watchlistId };
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
    	    var params = { "message" : VALUATION.watchlistId };
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){	
						me.transItems.removeAll();
						me.displayTransactions.removeAll();
						me.displayTransCompanies.removeAll();
						if(UTIL.isEmpty(VALUATION.transactionTickers)){
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
							var tickersData = VALUATION.transactionTickers;
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
								me.displayTransCompanies.sort(function(a, b){
									  var a = a.companyName.toLowerCase();
									  var b = b.companyName.toLowerCase(); 
									  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
								});
								me.computeSelectAllTrans(me);
							}
							$(".pagination-container").remove();
						}
						PAGE.hideLoading();
						setTimeout(function(){ PAGE.resizeIframeSimple(100) }, 500);
					}, 
					PAGE.customSGXError);
		},
		
		handleIndividualCheckbox:function(item){
			PAGE.showLoading();
			var me = this;
			VALUATION.showChange(false);
			var value = item.selectedTransaction();
			var showChart = false;
			var displayChart = false;
			var evaluateData = me.displayTransactions();

			ko.utils.arrayForEach(evaluateData, function (aitem) {
            	showChart = aitem.selectedTransaction() || showChart;
             });

			for(var i=0;i<$('input[type="checkbox"]').length;i++) {
				if($('input[type="checkbox"]')[i].checked) {
					displayChart = true;
					break;
				}
			}
			if((typeof event != 'undefined' && typeof event.target != 'undefined' && event.target.checked) || displayChart) {
			    $('#performance-chart-content').show();
        	    $('#performance-chart-header').show();
			    me.singleChartUnchart(me, item.tickerCode, value);
			}
			else{
				$('#performance-chart-content').hide();
            	$('#performance-chart-header').hide();
				me.multiChartUnchart(me, false);
			}
			PAGE.hideLoading();
			
            return true;
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
	                });
	                $('#selectAllId').prop('checked', selectAllTransaction);	                
	                VALUATION.showChange(false);
	                return selectAllTransaction;
	            },
	            write: function (value) {
	            	PAGE.showLoading();
	                ko.utils.arrayForEach(evaluateData, function (item) {
	                    if (value) item.selectedTransaction(true);
	                    else item.selectedTransaction(false);
	                });

	                $('#selectAllId').prop('checked', value);
	                if(value){
	                	$('#performance-chart-content').show()
	                	$('#performance-chart-header').show();
	                	me.multiChartUnchart(me, true);
	                }else{
	                	$('#performance-chart-content').hide();
	                	$('#performance-chart-header').hide();	
	                	me.multiChartUnchart(me, false);                	
	                }
	                PAGE.hideLoading();
	                
	            }
		    });
		},
		
		singleChartUnchart: function(me, seriesName, value){
			var chart = $('#performance-chart-content').highcharts();
			var seriesOptions = new Array();
			var index = 0;

			if(!UTIL.isEmpty(chart)){
				var visibleSeries = new Array();
				var seriesLength = chart.series.length;
				var selectedRange = chart.rangeSelector.selected;
				
				for(var i = seriesLength -1; i > -1; i--) {
					if(chart.series[i].visible){		        		
						visibleSeries.push(chart.series[i].name);
					}
				} 
				for(var seriesName1 in VALUATION.chartPoints) {
					for(var j =0; j < visibleSeries.length; j++){
						if(visibleSeries[j] == seriesName1 && seriesName1 !=seriesName){
							seriesOptions[index++]={
				                name: seriesName1,
				                data: VALUATION.chartPoints[seriesName1].priceData,
				                //chartData: me.chartData,
				                threshold : null,
								turboThreshold : 0,
								color:VALUATION.chartPoints[seriesName1].color
		        			};
						}
					}
				}
				if(value){
					seriesOptions[index++]={
		                name: seriesName,
		                data: VALUATION.chartPoints[seriesName].priceData,
		                threshold : null,
						turboThreshold : 0,
						color:VALUATION.chartPoints[seriesName].color
	            };
        		}        		

				chart.destroy();
				var newOptions = {
						rangeSelector: {							
							selected : selectedRange
						}
				};
				
				me.performanceChartRenderer(seriesOptions, newOptions);
			}
		},
		
		multiChartUnchart: function(me, value){
			var chart = $('#performance-chart-content').highcharts();
			var seriesOptions = new Array();
			var index = 0;
			if(!UTIL.isEmpty(chart)){				
				var seriesLength = chart.series.length;
				var selectedRange = chart.rangeSelector.selected;
				if(value){
					for(var seriesName in VALUATION.chartPoints) {
								seriesOptions[index++]={
					                name: seriesName,
					                data: VALUATION.chartPoints[seriesName].priceData,
					                threshold : null,
									turboThreshold : 0,
									color:VALUATION.chartPoints[seriesName].color
			        			};
					}
					chart.destroy();
					var newOptions = {
							rangeSelector: {							
								selected : selectedRange
							}
					};
					
					me.performanceChartRenderer(seriesOptions, newOptions);
				}else{
					for(var i = seriesLength -1; i > -1; i--) {					
			        	chart.series[i].setVisible(value,false);					
					}
					chart.redraw();
				}
			}
		},
		
		changeTab: function(tabName){
			VALUATION.showChange(false);
			var me = this;
			
			if( tabName == me.activeTab ) return;
		
			$(".header-bar > ul > li.downArrow").removeClass("downArrow");
			
			if(tabName=='performance'){
				$(".header-bar > ul > li:first").addClass("downArrow");
			}else{
				$(".header-bar > ul > li:last").addClass("downArrow");
			}
			setTimeout(function(){ PAGE.resizeIframeSimple(100) }, 100);
			if(tabName === "performance"){
				VALUATION.hasFieldErrors = false;
				VALUATION.record_modified = false;
				me.clearFieldData();
				me.setSortingToDefault();
				me.getTransactionsData(me);
				$(".pagination-container").remove();
				$('#transItemsId').paginathing();
				$('#performance-chart-content').show()
            	$('#performance-chart-header').show();
            	me.multiChartUnchart(me, true);
            	
            	me.trackPage("SGX - StockList Valuation Performance");
			}
			else {
				me.displayTransactions.sort(function(a, b){
		    		var a = !UTIL.isEmpty(a.companyName) ? a.companyName.toLowerCase() : "";
		    		var b = !UTIL.isEmpty(b.companyName) ? b.companyName.toLowerCase() : ""; 
		    		return ((a < b) ? -1 : ((a > b) ? 1 : 0));
				});
				$('#perCompanyName').addClass('asc');
		    	$('#perCompanyName').removeClass('desc');
		    	$('#perNumOfShares').removeClass('shareasc');
		    	$('#perNumOfShares').removeClass('sharedesc');
		    	$('#perLastClosePrice').removeClass('closepasc');
		    	$('#perLastClosePrice').removeClass('closepdesc');
		    	$('#perCurPrice').removeClass('currrentpasc');
		    	$('#perCurPrice').removeClass('currrentpdesc');
		    	me.trackPage("SGX - StockList Valuation Transactions");
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
					if(!data.watchlists){
						PAGE.hideLoading();
			    	    console.log('Watchlists unavailable');
			    	    return;
			    	}
					VALUATION.finalWL(data.watchlists.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
					PAGE.hideLoading();
					
					me.selectedValue(UTIL.getParameterByName("code"));
					VALUATION.watchlistId = UTIL.getParameterByName("code");
					
					var watchlists = me.finalWL();
					for(var i = 0, len = watchlists.length; i < len; i++) {
						var wl = watchlists[i];
						if( wl.id == VALUATION.watchlistId) {
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
			
			VALUATION.editWLName
			.extend({
				minLength: { params: 2, message: displayMessage.watchlist.error },
				maxLength: { params: 40, message: displayMessage.watchlist.error }
			});

			this.editWLNameError = ko.validation.group(VALUATION.editWLName);  //grouping error for editWLName only

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
			
			VALUATION.watchlistId = data.selectedValue();
			
			var chart = $('#performance-chart-content').highcharts();
			if(!UTIL.isEmpty(chart)){
				me.seriesOptions = [];
				me.performanceChartRenderer(me.seriesOptions);
			}
			if(!UTIL.isEmpty(chart)){
				$('#performance-chart-content').show();
				$('#performance-chart-header').show();
			}
			var watchlists = me.finalWL();
			for(var i = 0, len = watchlists.length; i < len; i++) {
				var wl = watchlists[i];
				if( wl.id == VALUATION.watchlistId) {
					me.clearWatchListErrors();
					me.editWLName(wl.name);	
					me.populateWatchlistCompanies(wl, me);
					break;
				}
			}
			VALUATION.showChange(false);
			//get the performance chart data
			me.getChartData(me);
			
			me.setSortingToDefault();
			me.clearFieldData();
			VALUATION.hasFieldErrors = false;
			VALUATION.record_modified = false;
		},
				
		addWatchlist: function(){
			VALUATION.showChange(false);
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
					PAGE.hideLoading();
					VALUATION.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
					
					$.each(data, function(i,data){
						if (data.name == newWLNameLC){
							VALUATION.selectedValue(data.id);
							
							VALUATION.watchlistId = data.id;
							VALUATION.clearWatchListErrors();
							VALUATION.editWLName(data.name);	
							
							var chart = $('#performance-chart-content').highcharts();
							if(!UTIL.isEmpty(chart)){
								me.seriesOptions = [];
								me.performanceChartRenderer(me.seriesOptions);
							}
							
							//get the performance chart data
							me.getChartData(me);
						}						
					});
					
				}, 
				PAGE.customSGXError);	
			
			VALUATION.showChange(false);
			//Clears add WL after submit
			VALUATION.newWLName(null);
		},

		editWLNameSubmit: function(){
			var me=this;
			if(me.editWLNameError().length != 0) return;
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
					VALUATION.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}));
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
			var stockListName = $("#stockListSelect option:selected").text();
			PAGE.modal.open({ content: '<p>Are you sure you want to delete ' + stockListName +'?</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {	
				 VALUATION.showChange(false);
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
					PAGE.hideLoading();
					VALUATION.finalWL(data.sort(function(a, b){
						  var a = a.name.toLowerCase();
						  var b = b.name.toLowerCase(); 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					}))
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
		    		me.watchlistCompanies.sort(function(a, b){
						  var a = !UTIL.isEmpty(a.companyName) ? a.companyName.toLowerCase() : "";
						  var b = !UTIL.isEmpty(b.companyName) ? b.companyName.toLowerCase() : ""; 
						  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
					})
		    	}
		    	PAGE.hideLoading();
				setTimeout(function(){ PAGE.resizeIframeSimple(100) }, 500);

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
		isFieldsEmpty: function(tradeDate, numberOfShares, costAtPurchase, tickerCode){
			var flag = false;
			if( tradeDate && numberOfShares && costAtPurchase && tickerCode ){
				flag = true;
			}else{
				!tickerCode && (tradeDate || costAtPurchase || numberOfShares) ? $('#watchlistCompaniesSelect').css({"borderColor":"red"}) : $('#watchlistCompaniesSelect').css({"borderColor":""});
				!tradeDate && (tickerCode || costAtPurchase || numberOfShares) ? $('#tradeDate').css({"borderColor":"red"}) : $('#tradeDate').css({"borderColor":""});
				!numberOfShares && (tradeDate || costAtPurchase || tickerCode) ? $('#initialNumberOfShares').css({"borderColor":"red"}) : $('#initialNumberOfShares').css({"borderColor":""}) ;
				!costAtPurchase && (tradeDate || numberOfShares || tickerCode) ? $('#initialCostAtPurchase').css({"borderColor":"red"}) : $('#initialCostAtPurchase').css({"borderColor":""}) ;
				if( (!tickerCode && (tradeDate || costAtPurchase || numberOfShares)) || 
						(!tradeDate && (tickerCode || costAtPurchase || numberOfShares)) || 
							(!numberOfShares && (tradeDate || costAtPurchase || tickerCode)) ||
								(!costAtPurchase && (tradeDate || numberOfShares || tickerCode)) ){
					PAGE.modal.open({ type: 'alert',  content: '<p>Please correct errors highlighted in red.</p>', width: 400 });
				}
			}
			return flag;
		},
		
		clearTransaction: function(){
			var me = this;
			if(me.initialCostAtPurchase() || me.initialNumberOfShares() || me.initialTradeDate() || me.selectedCompanyValue() ) {
				VALUATION.hasFieldErrors = false;
			}
			me.clearFieldData();
		},
		
		addSaveTransactions: function() {
			var me = this;
			VALUATION.showChange(false);
			var transItemModel = null;
			var tickerCode = me.selectedCompanyValue();
			var transactionType = me.selectedAvailableType();
			var tradeDate = me.initialTradeDate();
			var numberOfShares = me.initialNumberOfShares();
			var costAtPurchase = me.initialCostAtPurchase();
			var isFieldsNotEmpty = me.isFieldsEmpty(tradeDate, numberOfShares, costAtPurchase, tickerCode);
			if( (isFieldsNotEmpty || (VALUATION.record_modified &&
					UTIL.isEmpty(tradeDate) && UTIL.isEmpty(numberOfShares) && UTIL.isEmpty(costAtPurchase) && UTIL.isEmpty(tickerCode))) &&
					!VALUATION.hasFieldErrors ){
				if(isFieldsNotEmpty){
					me.convertTickerAndClosePrice(tickerCode, me);
					transItemModel = new insertTrans(me.disCompanyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, me.liveClosingPrice, "");
					me.transItems.push(transItemModel);
				}
				
				if( me.buySellValidate() ){
					me.insertTransactionRecords();
					me.clearFieldData();
					VALUATION.hasFieldErrors = false;
				}else{
					VALUATION.hasFieldErrors = true;
					if(transItemModel!=null)me.transItems.remove(transItemModel);
				}
				
				//dirty flag settings
				VALUATION.record_modified = false;
				
				//validation related attributes
				me.validatedCompanies = [];
		    	me.validateFlag = true;
			}
	    },
	    
	    insertTransactionRecords: function(){
	    	var me = this;
	    	var endpoint = PAGE.fqdn + "/sgx/watchlist/addTransaction";
			var postType = 'POST';
    	    var params = {'id' : VALUATION.watchlistId,'transactions' : me.mapTransDataToSend()};
    	    PAGE.showLoading();
			UTIL.handleAjaxRequestJSON(
					endpoint,
					postType,
					params,
					function(data, textStatus, jqXHR){					
						$('.save').remove();
						if($('#valuation-save').length){
							$('#valuation-save').remove();
						}
						$('<div id="valuation-save" class="valuation-save">Your changes have been saved.</div>').insertBefore('#transItemsIdTable').delay(7000).fadeOut(function() {$(this).remove();});
						me.getTransactionsData(me);
						PAGE.hideLoading();
						PAGE.resizeIframeSimple();
					}, 
					PAGE.customSGXError);
			
			me.setSortingToDefault();
	    },
	    
	    setSortingToDefault: function() {
	    	$('#transType').removeClass('typeasc');
	    	$('#transType').removeClass('typedesc');
	    	$('#transTradeDate').removeClass('dateasc');
	    	$('#transTradeDate').removeClass('datedesc');
	    	$('#transNumShare').removeClass('shareasc');
	    	$('#transNumShare').removeClass('sharedesc');
	    	$('#transPrice').removeClass('priceasc');
	    	$('#transPrice').removeClass('pricedesc');
	    	$('#transLastPrice').removeClass('lastpriceasc');
	    	$('#transLastPrice').removeClass('lastpricedesc');
	    	$('#transCompanyNameColumn').removeClass('desc');
	    	$('#transCompanyNameColumn').addClass('asc');
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
	    	me.selectedAvailableType('BUY');
	    	me.initialTradeDate(null);
	    	me.initialNumberOfShares(null);
	    	me.initialCostAtPurchase(null);
	    	$('#watchlistCompaniesSelect').css({"borderColor":""});
			$('#tradeDate').css({"borderColor":""});
			$('#initialNumberOfShares').css({"borderColor":""});
			$('#initialCostAtPurchase').css({"borderColor":""});
	    },
	    
	    calcCurrentValue: function(item, me){
	    	var numberOfShares = parseFloat(item.numberOfShares);
	    	var liveClosingPrice = parseFloat(me.liveClosingPrice);
	    	return (numberOfShares * liveClosingPrice).toFixed(3);
	    },
	    
	    calcMultiCompCurrentValue: function(noOfShares, me){
	    	var numberOfShares = parseFloat(noOfShares);
	    	var liveClosingPrice = parseFloat(me.liveClosingPrice);
	    	return (numberOfShares * liveClosingPrice).toFixed(3);
	    },
	    
	    calcTotalInvested: function(item, me){
    		if(item.transactionType === "BUY"){
    			me.userEnteredPurchasedPrice = parseFloat( parseFloat(me.userEnteredPurchasedPrice) + ( item.numberOfShares * item.costAtPurchase) ).toFixed(3);
    		}else{
    			me.userEnteredSellPrice = parseFloat( parseFloat(me.userEnteredSellPrice) + (item.numberOfShares * item.costAtPurchase) ).toFixed(3);
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
	    	noOfShares = (parseFloat(buyShare) - parseFloat(sellShare)).toFixed(3);
	    	return noOfShares;
	    },
	    
	    displayPerformanceTransactions: function(data){
	    	var me = this;
	    	var tickersData = VALUATION.transactionTickers.slice();
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
					me.getMultiCompData(data[i], transItemModel, me);
					me.displayTransactions.push(transItemModel);
	    		}else{
	    			item = data[i][0];
	    			me.convertTickerAndClosePrice(item.tickerCode, me);
//	    			me.calcTotalInvested(item, me);
	    			var currentValue  = me.calcCurrentValue(item, me);
	    			me.totalCurrentValue = me.totalCurrentValue + parseFloat(currentValue);
					transItemModel =  new insertPerTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate, parseFloat(item.numberOfShares).toFixed(3), 
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
	    		me.convertTickerAndClosePrice(tickerCode, me);
	    		var transItemCompModel =  new insertPerTrans(me.convertTickerCodeToCompany(tickerCode, me), tickerCode,"","", "", "", me.liveClosingPrice, "", "", "");
				me.displayTransactions.push(transItemCompModel);
	    	}
	    	
	    	me.displayTransactions.sort(function(a, b){
	    		var a = !UTIL.isEmpty(a.companyName) ? a.companyName.toLowerCase() : "";
	    		var b = !UTIL.isEmpty(b.companyName) ? b.companyName.toLowerCase() : ""; 
	    		return ((a < b) ? -1 : ((a > b) ? 1 : 0));
			});
	    	
	    	me.totalCalculation(me);
	    },
	    
	    totalCalculation: function(me){
	    	/*var totalInvested = parseFloat(me.userEnteredPurchasedPrice - me.userEnteredSellPrice).toFixed(3);
	    	var totalCurrentValue = parseFloat(me.totalCurrentValue).toFixed(3); 
	    	var percentageChangeVal = (((totalCurrentValue - totalInvested) / totalInvested) * 100).toFixed(3);
	    	if (percentageChangeVal == Number.POSITIVE_INFINITY || percentageChangeVal == Number.NEGATIVE_INFINITY) {
	    		percentageChangeVal = "0.000";
	    	}
	    	var percentageChange = isNaN(percentageChangeVal)? "0.000" :percentageChangeVal;
	    	
	    	$('#totalInvested').html("$" + totalInvested.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    	$('#totalCurrentValue').html("$" + totalCurrentValue.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
	    	
	    	if(percentageChange < 0.00){
	    		$('#percentageChange').html(percentageChange+"%").addClass('negativePerChange');
	    	}
	    	else {
	    		$('#percentageChange').html(percentageChange+"%")
	    	}	    	
    	
	    	me.userEnteredPurchasedPrice = 0.00;
	    	me.userEnteredSellPrice = 0.00;
	    	me.totalCurrentValue = 0.00;*/
	    	var totalCurrentValue = parseFloat(me.totalCurrentValue).toFixed(3);
	    	$('#totalCurrentValue').html("$" + totalCurrentValue.replace(/(\d)(?=(\d{3})+\.)/g, "$1,"));
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
//	    		me.calcTotalInvested(item, me);
	    		var muiltiCompTransModel = new insertMultiPerTrans(item.tickerCode, item.transactionType, item.tradeDate, parseFloat(item.numberOfShares).toFixed(3), item.costAtPurchase, item.id);
	    		model.multiCompData.push(muiltiCompTransModel);
	    	}
	    	
	    	model.multiCompData.sort(function(a, b){
	    		var a = new Date(a.tradeDateForSort);
	    		var b = new Date(b.tradeDateForSort);
	    		return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	});
		    model.tradeDate = model.multiCompData()[model.multiCompData().length-1].intTradeDate;
		    model.tradeDateForSort = model.multiCompData()[model.multiCompData().length-1].tradeDateForSort;
	    },
	    
	    displayAddTransactions: function(data){
	    	var me = this;
	    	var serverTransData = me.refractTransData(data);
	    	ko.utils.arrayMap(serverTransData, function(item) {
	    		me.convertTickerAndClosePrice(item.tickerCode, me);
	    		var transItemModel =  new insertDisplayTrans(me.disCompanyName, item.tickerCode, item.transactionType, item.tradeDate,
	    														item.numberOfShares, item.costAtPurchase, me.liveClosingPrice, item.id);
	    		me.transItems.push(transItemModel);
	    	});
	    	me.transItems.sort(function(a, b){
				  var a = !UTIL.isEmpty(a.companyName()) ? a.companyName().toLowerCase() : "";
				  var b = !UTIL.isEmpty(b.companyName()) ? b.companyName().toLowerCase() : ""; 
				  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
			});
	    	var currentPage = 1;
			if($(".pagination-container").length){
				currentPage = isNaN($('#pagingTextBox').val()) ? 1 : parseInt($('#pagingTextBox').val());
			    $(".pagination-container").remove();
			}
			$('#transItemsId').paginathing({
				currentPage: currentPage
			});
		    $('#transItemsId input').change(function() { 
		    	VALUATION.record_modified = true; 
		    }); 
		    $('#transItemsId select').change(function() { 
		    	VALUATION.record_modified = true; 
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
	    
	    isDeleteValid: function(transactionType, tickerCode, numberOfShares,date){
	    	var flag = true;
	    	numberOfShares = numberOfShares.toString().replace(/,/gi,"");
	    	if(transactionType === "BUY"){
	    		 var me = this;
		    	 var bought = 0.00;
		    	 var sell = 0.00;
		    	 var tempDate = null;
		    	 ko.utils.arrayForEach(me.transItems(), function (item) {
		    		
		    		 if(item.tickerCode() === tickerCode){
	    				 var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
		    			 if(item.transactionType() === "SELL") {
		    				 if(tempDate == null) {
		    					 if(item.tradeDate() >= new Date(date).setHours(0,0,0,0)) {
		    						 tempDate = item.tradeDate();
			    				 }
		    				 }
		    				 else {
		    					 if(item.tradeDate() >= new Date(date).setHours(0,0,0,0) && tempDate >= item.tradeDate()) {
		    						 tempDate = item.tradeDate();
			    				 }
		    				 }
		    				 
		    			 }
		    		 }
		    	 });
		    	 
		    	 ko.utils.arrayForEach(me.transItems(), function (item) {
		    		 if(item.tickerCode() === tickerCode){
	    				 var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
		    			 if(item.transactionType() === "BUY"  && (item.tradeDate() <= new Date(date).setHours(0,0,0,0) || item.tradeDate() <= tempDate)){
		    				 bought = parseFloat( parseFloat(bought) + parseFloat(numberOfShares) ).toFixed(3);
		    			 }else if(item.transactionType() === "SELL"  && (item.tradeDate() >= new Date(date).setHours(0,0,0,0) || item.tradeDate() <= tempDate)) {
		    				 sell = parseFloat( parseFloat(sell) + parseFloat(numberOfShares) ).toFixed(3);
		    			 }
		    		 }
		    	 });
		    	 if(transactionType === "BUY") {
		    		 bought=bought-numberOfShares;
		    	 }
		    	 if(parseFloat(sell) != 0 && parseFloat(sell) > parseFloat(bought)){
		    		 PAGE.modal.open({ type: 'alert',  content: '<p>You cannot delete this transaction as it would create a negative position for this security.</p>', width: 400 });
		    		 flag = false;
		    	 }
	    	}
	    	
	    	return flag;
	    },
	    
	    removeItem: function(item) {
	    	var me = this;
	    	var buySellValidateFlag = true;
	    	
    		buySellValidateFlag = me.buySellValidate() && me.isDeleteValid( item.transactionType(), item.tickerCode(), item.numberOfShares(),item.tradeDate() );
    		//validation related attributes
			me.validatedCompanies = [];
	    	me.validateFlag = true;
		    	
	    	if( buySellValidateFlag ){
		    	PAGE.modal.open({ content: '<p>This will delete this transaction. Click Delete to delete this transaction. This will not remove the company from your StockList.</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
				
				 $('.confirm-delete').click(function(e) {				
					 if(item.id()!=""){
					    	var endpoint = PAGE.fqdn + "/sgx/watchlist/deleteTransaction";
							var postType = 'POST';
				    	    var params = {"id": VALUATION.watchlistId, "transactionId" : item.id()};
				    	    PAGE.showLoading();
							UTIL.handleAjaxRequestJSON(
									endpoint,
									postType,
									params,
									function(data, textStatus, jqXHR){					
										console.log(data);
										me.transItems.remove(item);
										me.getTransactionsData(me);
										VALUATION.hasFieldErrors = false;
										VALUATION.record_modified = false;
										PAGE.hideLoading();
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
	    	VALUATION.showChange(false);
	    	var me = this;
			PAGE.modal.open({ content: '<p>This will delete this transaction. Click Delete to delete this transaction. This will not remove the company from your StockList.</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
			
			 $('.confirm-delete').click(function(e) {				
				 if(item.id!=""){
				    	var endpoint = PAGE.fqdn + "/sgx/watchlist/deleteTransaction";
						var postType = 'POST';
			    	    var params = {"id": VALUATION.watchlistId, "transactionId" : item.id};
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
									me.multiChartUnchart(me, true);
									$('#performance-chart-content').show();
				                	$('#performance-chart-header').show();
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
    		var buySellValidateFlag = true;
	    	if(item.intTransactionType === "BUY") {
	    		buySellValidateFlag = me.buySellValidate() && me.isDeleteValid(item.intTransactionType, item.intTickerCode, item.intNumberOfShares,item.tradeDateForSort );
	    		//validation related attributes
				me.validatedCompanies = [];
		    	me.validateFlag = true;
	    	}
	    	if( buySellValidateFlag ){	
				PAGE.modal.open({ content: '<p>This will delete this transaction. Click Delete to delete this transaction. This will not remove the company from your StockList.</p> <div class="button-wrapper deleteTran"><span class="confirm-delete button floatLeft">Delete</span> <span class="cancel button ml5p ">Cancel</span></div>', width: 400 }); 
				 $('.confirm-delete').click(function(e) {				
			    	var endpoint = PAGE.fqdn + "/sgx/watchlist/deleteTransaction";
					var postType = 'POST';
		    	    var params = {"id": VALUATION.watchlistId, "transactionId" : item.intId};
		    	    PAGE.showLoading();
					UTIL.handleAjaxRequestJSON(
						endpoint,
						postType,
						params,
						function(data, textStatus, jqXHR){					
							console.log(data);
							PAGE.hideLoading();
							me.getTransactionsData(me);
							me.multiChartUnchart(me, true);
							$('#performance-chart-content').show();
		                	$('#performance-chart-header').show();
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
	    	var isChrome = !!window.chrome && !!window.chrome.webstore;
	    	var isFirefox = typeof InstallTrigger !== 'undefined';
	    	
	       	$('#plus_'+id).hide();
	    	$('#minus_'+id).show();
	    	
	    	$.each(data.multiCompData(), function (index, item) {
	    		var intTransType = item.intTransactionType === "BUY" ? "Transaction Type <b>BOUGHT</b>" : "Transaction Type <b>SOLD</b>";
	    		if($('#intMultiComp'+id).text().length > 38){
	    			$('#comptd'+id).append("<div id='intcompdiv"+item.intId+"' style='padding-left: 22px;padding-top: inherit;font: normal 12px/12px Arial, Helvetica, sans-serif;'>" + intTransType + "</div>");
	    			if(isChrome){
	    				$('#datetd'+id).append("<div id='intdatediv"+item.intId+"' style='padding-top: 15px;height: 10px;font: normal 12px/12px Arial, Helvetica, sans-serif;'><b>" + item.intTradeDate + "</b></div>");
			    		$('#sharetd'+id).append("<div id='intsharediv"+item.intId+"' style='padding-top:15px;height: 10px;font: normal 12px/16px Arial, Helvetica, sans-serif;'>" + item.intNumberOfShares+ "</div>");
	    			}else{
	    				$('#datetd'+id).append("<div id='intdatediv"+item.intId+"' style='padding-top: 15px;height: 10.4px;font: normal 12px/12px Arial, Helvetica, sans-serif;'><b>" + item.intTradeDate + "</b></div>");
			    		$('#sharetd'+id).append("<div id='intsharediv"+item.intId+"' style='padding-top:15px;height: 10.4px;font: normal 12px/16px Arial, Helvetica, sans-serif;'>" + item.intNumberOfShares+ "</div>");
	    			}
		    		$('#multiCompData'+id).css({"padding-top":"15px"});
		    		if(isFirefox){
			    		$(".imgIntCenterAllign").css("padding-bottom", "5.7px");
			    	}
	    		}else{
	    			$('#comptd'+id).append("<div id='intcompdiv"+item.intId+"' style='padding-left: 22px;padding-top: inherit;font: normal 12px/12px Arial, Helvetica, sans-serif;'>" + intTransType + "</div>");
	    			if(isChrome){
	    				$('#datetd'+id).append("<div id='intdatediv"+item.intId+"' style='height: 25px;font: normal 12px/12px Arial, Helvetica, sans-serif;'><b>" + item.intTradeDate + "</b></div>");
			    		$('#sharetd'+id).append("<div id='intsharediv"+item.intId+"' style='height: 25px;font: normal 12px/16px Arial, Helvetica, sans-serif;'>" + item.intNumberOfShares+ "</div>");
	    			}else{
	    				$('#datetd'+id).append("<div id='intdatediv"+item.intId+"' style='height: 25.3px;font: normal 12px/12px Arial, Helvetica, sans-serif;'><b>" + item.intTradeDate + "</b></div>");
			    		$('#sharetd'+id).append("<div id='intsharediv"+item.intId+"' style='height: 25.3px;font: normal 12px/16px Arial, Helvetica, sans-serif;'>" + item.intNumberOfShares+ "</div>");
	    			}
	    			if(isFirefox){
	    	    		$(".imgIntCenterAllign").css("padding-bottom", "5.6px");
	    	    	}
	    		}
	    	});
	    	
	    	$('#multiCompData'+id).show();
	    	
	    	$('#tr'+id).addClass('panel');

	    	PAGE.resizeIframeSimple(100);
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
	    	
	    	PAGE.resizeIframeSimple(100);
	    },
	    
	    toogleCompanyLink: function(id, data){	 
	    	
	       	var me = this;
	    	if(($('#minus_'+id).is(':visible'))) {
	    		me.toogleCompanyMinus(id,data);
	    	}
	    	else {
	    		me.toogleCompanyPlus(id,data)
	    	}
	    },
	    
	    transSortByCompanyName: function(data, event){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#'+event.target.id).hasClass('asc')){
	    		$('#'+event.target.id).removeClass('asc').addClass('desc');
	    		me.transItems.sort(function(a, b){
	  			  var a = a.companyName().toLowerCase();
	  			  var b = b.companyName().toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#'+event.target.id).removeClass('desc').addClass('asc');
	    		me.transItems.sort(function(a, b){
	  			  var a = a.companyName().toLowerCase();
	  			  var b = b.companyName().toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
	    	}
	    	$('#transType').removeClass('typeasc');
	    	$('#transType').removeClass('typedesc');
	    	$('#transTradeDate').removeClass('dateasc');
	    	$('#transTradeDate').removeClass('datedesc');
	    	$('#transNumShare').removeClass('shareasc');
	    	$('#transNumShare').removeClass('sharedesc');
	    	$('#transPrice').removeClass('priceasc');
	    	$('#transPrice').removeClass('pricedesc');
	    	$('#transLastPrice').removeClass('lastpriceasc');
	    	$('#transLastPrice').removeClass('lastpricedesc');
	    	$(".pagination-container").remove();
	    	$('#transItemsId').paginathing();
	    	setTimeout(function(){ PAGE.resizeIframeSimple(100) });
	    },
	    
	    transSortbyType: function(){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#transType').hasClass('typeasc')){
	    		$('#transType').removeClass('typeasc').addClass('typedesc');
	    		me.transItems.sort(function(a, b){
	    	    	var a = a.transactionType().toLowerCase();
    	    		var b = b.transactionType().toLowerCase(); 
    	    		return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#transType').removeClass('typedesc').addClass('typeasc');
	    		me.transItems.sort(function(a, b){
	    	    	var a = a.transactionType().toLowerCase();
    	    		var b = b.transactionType().toLowerCase(); 
	  			 	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
	    	}
	    	$('#transCompanyNameColumn').removeClass('asc');
	    	$('#transCompanyNameColumn').removeClass('desc');
	    	$('#transTradeDate').removeClass('dateasc');
	    	$('#transTradeDate').removeClass('datedesc');
	    	$('#transNumShare').removeClass('shareasc');
	    	$('#transNumShare').removeClass('sharedesc');
	    	$('#transPrice').removeClass('priceasc');
	    	$('#transPrice').removeClass('pricedesc');
	    	$('#transLastPrice').removeClass('lastpriceasc');
	    	$('#transLastPrice').removeClass('lastpricedesc');	    	
	    	$(".pagination-container").remove();
	    	$('#transItemsId').paginathing();
	    	setTimeout(function(){ PAGE.resizeIframeSimple(100) });
	    },
	    
	    transSortbyTradeDate: function(){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#transTradeDate').hasClass('dateasc')){
	    		$('#transTradeDate').removeClass('dateasc').addClass('datedesc');
	    		me.transItems.sort(function(a, b){
	    	    	var a = new Date(a.tradeDate()).getTime();
    	    		var b = new Date(b.tradeDate()).getTime(); 
    	    		return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#transTradeDate').removeClass('datedesc').addClass('dateasc');
	    		me.transItems.sort(function(a, b){
	    	    	var a = new Date(a.tradeDate()).getTime();
    	    		var b = new Date(b.tradeDate()).getTime();
	  			 	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
	    	}
	    	$('#transCompanyNameColumn').removeClass('asc');
	    	$('#transCompanyNameColumn').removeClass('desc');
	    	$('#transType').removeClass('typeasc');
	    	$('#transType').removeClass('typedesc');
	    	$('#transNumShare').removeClass('shareasc');
	    	$('#transNumShare').removeClass('sharedesc');
	    	$('#transPrice').removeClass('priceasc');
	    	$('#transPrice').removeClass('pricedesc');
	    	$('#transLastPrice').removeClass('lastpriceasc');
	    	$('#transLastPrice').removeClass('lastpricedesc');
	    	$(".pagination-container").remove();
	    	$('#transItemsId').paginathing();
	    	setTimeout(function(){ PAGE.resizeIframeSimple(100) });
	    },
	    
	    transSortbyNumberShare: function(data, event){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#transNumShare').hasClass('shareasc')){
	    		$('#transNumShare').removeClass('shareasc').addClass('sharedesc')
	    		me.transItems.sort(function(a, b){
	    	    	var a = parseFloat(a.numberOfShares().toString().replace(/,/gi,""));
			    	var b = parseFloat(b.numberOfShares().toString().replace(/,/gi,"")); 
			  	  	return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#transNumShare').removeClass('sharedesc').addClass('shareasc')
	    		me.transItems.sort(function(a, b){
	    	    	var a = parseFloat(a.numberOfShares().toString().replace(/,/gi,""));
			    	var b = parseFloat(b.numberOfShares().toString().replace(/,/gi,""));
	  			  	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
	    	}
	    	$('#transCompanyNameColumn').removeClass('asc');
	    	$('#transCompanyNameColumn').removeClass('desc');
	    	$('#transType').removeClass('typeasc');
	    	$('#transType').removeClass('typedesc');
	    	$('#transTradeDate').removeClass('dateasc');
	    	$('#transTradeDate').removeClass('datedesc');
	    	$('#transPrice').removeClass('priceasc');
	    	$('#transPrice').removeClass('pricedesc');
	    	$('#transLastPrice').removeClass('lastpriceasc');
	    	$('#transLastPrice').removeClass('lastpricedesc');
	    	$(".pagination-container").remove();
	    	$('#transItemsId').paginathing();
	    	setTimeout(function(){ PAGE.resizeIframeSimple(100) });
	    },
	    
	    transSortbyPrice: function(data, event){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#transPrice').hasClass('priceasc')){
	    		$('#transPrice').removeClass('priceasc').addClass('pricedesc');
	    		me.transItems.sort(function(a, b){
	    	       var a = a.costAtPurchase().toString().replace(/,/gi,"");
	    	       var b = b.costAtPurchase().toString().replace(/,/gi,"");
	    	       a = parseFloat(a.replace("$",""));
		  		   b = parseFloat(b.replace("$",""));
			  	   return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#transPrice').removeClass('pricedesc').addClass('priceasc');
	    		me.transItems.sort(function(a, b){
	    	       var a = a.costAtPurchase().toString().replace(/,/gi,"");
		    	   var b = b.costAtPurchase().toString().replace(/,/gi,"");
		    	   a = parseFloat(a.replace("$",""));
			  	   b = parseFloat(b.replace("$",""));
			  	   return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
	    	}
	    	$('#transCompanyNameColumn').removeClass('asc');
	    	$('#transCompanyNameColumn').removeClass('desc');
	    	$('#transType').removeClass('typeasc');
	    	$('#transType').removeClass('typedesc');
	    	$('#transTradeDate').removeClass('dateasc');
	    	$('#transTradeDate').removeClass('datedesc');
	    	$('#transNumShare').removeClass('shareasc');
	    	$('#transNumShare').removeClass('sharedesc');
	    	$('#transLastPrice').removeClass('lastpriceasc');
	    	$('#transLastPrice').removeClass('lastpricedesc');
	    	$(".pagination-container").remove();
	    	$('#transItemsId').paginathing();
	    	setTimeout(function(){ PAGE.resizeIframeSimple(100) });
	    },
	    
	    transSortbyLastPrice: function(data, event){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#transLastPrice').hasClass('lastpriceasc')){
	    		$('#transLastPrice').removeClass('lastpriceasc').addClass('lastpricedesc')
	    		me.transItems.sort(function(a, b){
	    	    	var a = a.currentPrice().toString().replace(/,/gi,"");
	    	    	var b = b.currentPrice().toString().replace(/,/gi,"");
	    	    	a = parseFloat(a.replace("$",""));
		  			b = parseFloat(b.replace("$",""));  
			  	  	return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#transLastPrice').removeClass('lastpricedesc').addClass('lastpriceasc')
	    		me.transItems.sort(function(a, b){
	    	    	var a = a.currentPrice().toString().replace(/,/gi,"");
	    	    	var b = b.currentPrice().toString().replace(/,/gi,"");
	    	    	a = parseFloat(a.replace("$",""));
		  			b = parseFloat(b.replace("$","")); 
			  	  	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
	    	}
	    	$('#transCompanyNameColumn').removeClass('asc');
	    	$('#transCompanyNameColumn').removeClass('desc');
	    	$('#transType').removeClass('typeasc');
	    	$('#transType').removeClass('typedesc');
	    	$('#transTradeDate').removeClass('dateasc');
	    	$('#transTradeDate').removeClass('datedesc');
	    	$('#transNumShare').removeClass('shareasc');
	    	$('#transNumShare').removeClass('sharedesc');
	    	$('#transPrice').removeClass('priceasc');
	    	$('#transPrice').removeClass('pricedesc');
	    	$(".pagination-container").remove();
	    	$('#transItemsId').paginathing();
	    	setTimeout(function(){ PAGE.resizeIframeSimple(100) });
	    },
	    
	    sortColumnByAsc: function(data, event){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#'+event.target.id).hasClass('asc')){
	    		$('#'+event.target.id).removeClass('asc').addClass('desc');
	    		me.displayTransCompanies.sort(function(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#'+event.target.id).removeClass('desc').addClass('asc');
	    		me.displayTransCompanies.sort(function(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
	    	}
	    },  
	    
	    perSortByCompanyName: function(data, event){
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#perCompanyName').hasClass('asc')){
	    		$('#perCompanyName').removeClass('asc').addClass('desc');
	    		me.displayTransactions.sort(function(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#perCompanyName').removeClass('desc').addClass('asc');
	    		me.displayTransactions.sort(function(a, b){
	  			  var a = a.companyName.toLowerCase();
	  			  var b = b.companyName.toLowerCase(); 
	  			  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
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
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#perTradeDate').hasClass('dateasc')){
	    		$('#perTradeDate').removeClass('dateasc').addClass('datedesc');
	    		me.displayTransactions.sort(function(a, b){
	    	    	var a = !UTIL.isEmpty(a.tradeDateForSort) ? new Date(a.tradeDateForSort).getTime() : "";
    	    		var b = !UTIL.isEmpty(b.tradeDateForSort) ? new Date(b.tradeDateForSort).getTime() : ""; 
    	    		if (a == "" && b){
	    	    	    return 1;
	    	    	}
	    	    	if (b == "" && a){
	    	    	    return -1;
	    	    	}
    	    		return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#perTradeDate').removeClass('datedesc').addClass('dateasc');
	    		me.displayTransactions.sort(function(a, b){
    	    		var a = !UTIL.isEmpty(a.tradeDateForSort) ? new Date(a.tradeDateForSort).getTime() : "";
    	    		var b = !UTIL.isEmpty(b.tradeDateForSort) ? new Date(b.tradeDateForSort).getTime() : "";
	  			 	if (a == "" && b){
	    	    	    return 1;
	    	    	}
	    	    	if (b == "" && a){
	    	    	    return -1;
	    	    	}
	  			 	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
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
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#perNumOfShares').hasClass('shareasc')){
	    		$('#perNumOfShares').removeClass('shareasc').addClass('sharedesc')
	    		me.displayTransactions.sort(function(a, b){
	    	    	var a = parseFloat(a.numberOfShares.toString().replace(/,/gi,""));
			    	var b = parseFloat(b.numberOfShares.toString().replace(/,/gi,"")); 
			    	if ((!$.isNumeric(a) || isNaN(a)) && ($.isNumeric(b) && !isNaN(b))){
	    	    	    return 1;
		  			}
			    	if ((!$.isNumeric(b) || isNaN(b)) && ($.isNumeric(a) && !isNaN(a))){
	    	    	    return -1;
			  	  	}
			  	  	return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#perNumOfShares').removeClass('sharedesc').addClass('shareasc')
	    		me.displayTransactions.sort(function(a, b){
	    	    	var a = parseFloat(a.numberOfShares.toString().replace(/,/gi,""));
			    	var b = parseFloat(b.numberOfShares.toString().replace(/,/gi,""));
			    	if ((!$.isNumeric(a)  || isNaN(a)) && ($.isNumeric(b) && !isNaN(b))){
	    	    	    return 1;
		  			}
			    	if ((!$.isNumeric(b) || isNaN(b)) && ($.isNumeric(a) && !isNaN(a))){
	    	    	    return -1;
			  	  	}
	  			  	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
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
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#perLastClosePrice').hasClass('closepasc')){
	    		$('#perLastClosePrice').removeClass('closepasc').addClass('closepdesc')
	    		me.displayTransactions.sort(function(a, b){
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
	    	    });
	    	}else{
	    		$('#perLastClosePrice').removeClass('closepdesc').addClass('closepasc')
	    		me.displayTransactions.sort(function(a, b){
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
	    	    });
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
	    	VALUATION.showChange(false);
	    	var me = this;
	    	if($('#perCurPrice').hasClass('currrentpasc')){
	    		$('#perCurPrice').removeClass('currrentpasc').addClass('currrentpdesc')
	    		me.displayTransactions.sort(function(a, b){
	    	    	var a = a.currentValue.toString().replace(/,/gi,"");
	    	    	var b = b.currentValue.toString().replace(/,/gi,"");
	    	    	a = parseFloat(a.replace("$",""));
		  			b = parseFloat(b.replace("$",""));  
		  			if ((!$.isNumeric(a) || isNaN(a)) && ($.isNumeric(b) && !isNaN(b))){
	    	    	    return 1;
		  			}
		  			if ((!$.isNumeric(b) || isNaN(b)) && ($.isNumeric(a) && !isNaN(a))){
	    	    	    return -1;
			  	  	}
			  	  	return ((a < b) ? 1 : ((a > b) ? -1 : 0));
	    	    });
	    	}else{
	    		$('#perCurPrice').removeClass('currrentpdesc').addClass('currrentpasc')
	    		me.displayTransactions.sort(function(a, b){
	    	    	var a = a.currentValue.toString().replace(/,/gi,"");
	    	    	var b = b.currentValue.toString().replace(/,/gi,"");
	    	    	a = parseFloat(a.replace("$",""));
		  			b = parseFloat(b.replace("$","")); 
		  			if ((!$.isNumeric(a)  || isNaN(a)) && ($.isNumeric(b) && !isNaN(b))){
	    	    	    return 1;
		  			}
		  			if ((!$.isNumeric(b) || isNaN(b)) && ($.isNumeric(a) && !isNaN(a))){
	    	    	    return -1;
			  	  	}
			  	  	return ((a < b) ? -1 : ((a > b) ? 1 : 0));
	    	    });
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
	    
	    validateInitialBuySell: function(data, event){
	    	var me = this;
	    	if( data.initialCostAtPurchase() && data.initialNumberOfShares() && data.initialTradeDate() && !VALUATION.dateValidatorFlag){
	    		
	    		/*var dateArr = data.initialTradeDate().split("/");
				var dateStr = dateArr[0]+" "+dateArr[1]+" "+dateArr[2];
				var date = new Date(dateStr);*/
	    		if( me.validateNumberOfShares(data.selectedAvailableType(), data.initialNumberOfShares(), data.transItems(), data.selectedCompanyValue(), data.initialTradeDate()) ){
	    			$('#tradeDate').css({"borderColor":"red"}) ;
	    			$('#initialNumberOfShares').css({"borderColor":"red"}) ;
	    		}else{
	    			$('#tradeDate').css({"borderColor":""}) ;
					$('#initialNumberOfShares').css({"borderColor":""}) ;
	    		}
	    	}
	    },
	    
	    validateBuySell: function(data, event){
	    	if( data.costAtPurchase() && data.numberOfShares() && data.tradeDate() && !VALUATION.dateValidatorFlag ){
	    		if( VALUATION.validateNumberOfShares(data.transactionType(), 0.00, VALUATION.transItems(), data.tickerCode(), data.tradeDate()) ){
    				$('#date'+data.id()).css({"borderColor":"red"});
    				$('#share'+data.id()).css({"borderColor":"red"});
	    		}else{
	    			$('#date'+data.id()).css({"borderColor":""});
    				$('#share'+data.id()).css({"borderColor":""});
	    		}
	    	}
	    },
	    
	    validateNumberOfShares: function(type, shares, transItems, ticker, date){
	    	var bought = 0.00;
	    	var sell = 0.00;
	    	var flag = false;
	    	var isError = false;
	    	
	    	if(type === "SELL"){
	    		sell = shares;
	    		ko.utils.arrayForEach(transItems, function (item) {
					if(ticker === item.tickerCode()){
						var dateInMilliSeconds = isNaN(date) ? new Date(date).setHours(0,0,0,0) : date;
						var tradeDate = isNaN(item.tradeDate()) ? new Date(item.tradeDate()).setHours(0,0,0,0) : item.tradeDate();
						if( tradeDate < dateInMilliSeconds || ( tradeDate == dateInMilliSeconds )){
							var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
							if(item.transactionType() === "BUY"){
								bought = parseFloat( parseFloat(bought) + parseFloat(numberOfShares) ).toFixed(3);
							}else{
								sell = parseFloat( parseFloat(sell) + parseFloat(numberOfShares) ).toFixed(3);
							}
						}
					}
				});
	    		
	    		if( parseFloat(sell) > parseFloat(bought) ){
					 isError = true;
				}else{
					bought = 0.00;
			    	sell = shares;
					ko.utils.arrayForEach(transItems, function (item) {
						if(ticker === item.tickerCode()){
							var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
							if(item.transactionType() === "BUY"){
								bought = parseFloat( parseFloat(bought) + parseFloat(numberOfShares) ).toFixed(3);
							}else{
								sell = parseFloat( parseFloat(sell) + parseFloat(numberOfShares) ).toFixed(3);
							}
						}
					});
					if( parseFloat(sell) > parseFloat(bought) ){
						isError = true;
					}
				}
	    	}
	    	
	    	if( isError ){
				 PAGE.modal.open({ type: 'alert',  content: '<p>You are trying to sell more shares that you have bought or are attempting to sell a quantity before all shares were purchased. Please correct and try again.</p>', width: 500 });
				 flag = true;
				 VALUATION.hasFieldErrors = true;
			}else {
				VALUATION.hasFieldErrors = false;
	    	}
	    	
	    	return flag;
	    },
	
		buySellValidate: function(){
			var me = this;
			ko.utils.arrayForEach(me.transItems(), function (item) {
				if(UTIL.isEmpty(item.numberOfShares()) || UTIL.isEmpty(item.costAtPurchase()) || UTIL.isEmpty(item.tradeDate()) ){
					me.validateFlag = false;
					VALUATION.hasFieldErrors = true;
					return false;
				}else{
					if(item.transactionType() === "SELL"){
						me.transactionValidator(item);
					}
				}
		 	});
			
			return me.validateFlag;
		},
	
		transactionValidator: function(sentItem){
	    	var me= this;
	    	var loopFlag = true;
	    	var bought = 0.00;
	    	var sell = 0.00;
	    	var sellDates = [];
	    	
	    	$.each(me.validatedCompanies, function(i, data){
	    		if(data === sentItem.tickerCode()){
	    			loopFlag = false;
	    		}
	    	});
	    		
	    	if(loopFlag){
	    		me.validatedCompanies.push(sentItem.tickerCode());
	    		ko.utils.arrayForEach(me.transItems(), function (item) {
	   	    		 if(sentItem.tickerCode() === item.tickerCode()){
	   	    			 if(item.transactionType() === "SELL"){
	   	    				 sellDates.push(item.tradeDate());
	   	    			 }
	   	    		 }
	    	 	});
	    		
				$.each(sellDates, function(i, selldate){
					ko.utils.arrayForEach(me.transItems(), function (item) {
						if(sentItem.tickerCode() === item.tickerCode()){
							var sellDateInMilliSeconds = isNaN(selldate) ? new Date(selldate).setHours(0,0,0,0) : selldate;
							var tradeDate = isNaN(item.tradeDate()) ? new Date(item.tradeDate()).setHours(0,0,0,0) : item.tradeDate();
							if( tradeDate < sellDateInMilliSeconds || ( tradeDate == sellDateInMilliSeconds )){
								var numberOfShares = item.numberOfShares().toString().replace(/,/gi,"");
								if(item.transactionType() === "BUY"){
									bought = parseFloat( parseFloat(bought) + parseFloat(numberOfShares) ).toFixed(3);
								}else{
									sell = parseFloat( parseFloat(sell) + parseFloat(numberOfShares) ).toFixed(3);
								}
							}
						}
					});
					if( (parseFloat(sell) > parseFloat(bought)) || (sell==0.00 && bought==0.00) ){
						 PAGE.modal.open({ type: 'alert',  content: '<p>You are trying to sell more shares that you have bought or are attempting to sell a quantity before all shares were purchased. Please correct and try again.</p>', width: 500 });
						 me.validateFlag = false;
						 VALUATION.hasFieldErrors = true;
					}else{
						VALUATION.hasFieldErrors = false;
					}
					bought = 0.00;
			    	sell = 0.00;
				});
	    	}
	    },
	    
	    daysInMonth: function(month, year) {
	    	var m = [31,28,31,30,31,30,31,31,30,31,30,31];
	    	if (month != 2) return m[month - 1];
	    	if (year%4 != 0) return m[1];
	    	if (year%100 == 0 && year%400 != 0) return m[1];
	    	return m[1] + 1;
    	}
    
	};
	
	function insertTrans(companyName, tickerCode, transactionType, tradeDate, numberOfShares, costAtPurchase, currentPrice, id) {
    	var me = this;
    	me.companyName = ko.observable(companyName);
    	me.tickerCode = ko.observable(tickerCode);
    	me.transactionType = ko.observable(transactionType);
    	me.tradeDate = ko.observable(new Date(tradeDate).setHours(0,0,0,0));
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
    	me.tradeDateForSort = !UTIL.isEmpty(tradeDate) ? tradeDate : "";
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
    	me.tradeDateForSort = !UTIL.isEmpty(tradeDate) ? tradeDate : "";
    	me.intNumberOfShares = !UTIL.isEmpty(numberOfShares) ? numberOfShares.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','): "" ;
    	me.intCostAtPurchase = !UTIL.isEmpty(costAtPurchase) ? "$" + costAtPurchase.toFixed(3).replace(/(\d)(?=(\d{3})+\.)/g, "$1,"): "";
    	me.intId = id;
    }
	
	return VALUATION;
	
});