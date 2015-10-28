define([ "wmsi/utils", "knockout", "text!client/data/estimates.json", "client/modules/tearsheet", "highstock" ], function(UTIL, ko, ESTIMATES, TS) {

    ko.components.register('premium-preview', { require: 'client/components/premium-preview'});

    //Estimates specific table header handling
    ko.bindingHandlers.tblHeader = {
        init: function(element, valueAccessor, allBindings) {
            var value = allBindings().text;
            var val = valueAccessor();
            return ko.bindingHandlers.text.update(element, function(){
                return value.slice(0, -4)+' '+value.slice(-4);
            });
        }
    }

	var CF = {
			
		quarterlyEst: null,
		annualEst: null,
		estimates: null,
        dataExists:ko.observable(),

        sectionName:'Estimates',

		currency: ko.observable(""),
		series: ko.observable([]),
		legendItems: ko.computed(function() {}),
		premiumUser: ko.observable(),	
		premiumUserEmail: ko.observable(),
		premiumUserAccntInfo: ko.observable(),
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(null),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(),
		currentDay: ko.observable(),
		quarterlyTab: ko.observable(true),
	    annualTab: ko.observable(false),

        summaryData: null,
		
		initPage: function() {
			
            var me = this;
			// extend tearsheet
			$.extend(true, this, TS);
            this.init();  //this is tearsheet init
			// set up some basics
			this.quarterlyEst = JSON.parse(ESTIMATES).quarterly;
			this.annualEst = JSON.parse(ESTIMATES).annual;
			this.series([]);
			this.estimates = {
                quarterly:[],
                annually: []
            };
			this.currency("");

			this.legendItems = ko.computed(function() {
				if (this.series().length == 0) return [];
				var chart = $('#bar-chart').highcharts(), ret = [];
				$.each(chart.series, function(idx, series) { ret.push(series.userOptions) });
				return ret;
			}, this);

            this.quarterlyTab.subscribe(function(data) {
                if(data) 
                    setTimeout(function() { me.resizeIframeSimple(); }, 100);
            });
            this.annualTab.subscribe(function(data) {
                if(data)
                    setTimeout(function() { me.resizeIframeSimple(); }, 100);
            });
            this.series.subscribe(function(data) {
                if(data.length == 0) {
                    setTimeout(function() { me.resizeIframeSimple(); }, 100);
                }
            });


			
			var self = this;
			
			PAGE.checkStatus();
            
            var waitForDataToInit = ko.computed({
                read:function(){

                    var companyData = this.gotCompanyData();
                    var userStatus = this.userStatus();

                    if( companyData && userStatus ) {

                        if ( userStatus == 'UNAUTHORIZED' || userStatus == 'EXPIRED' ) {
                            this.dataExists(false);
                            this.init_nonPremium();
                        }else {
                          this.init(function() { self.finish(self); });
                        }
                    }
                },
                owner:this
            });
            
		},
		
		finish: function(me) {

			
			me.trackPage("SGX Company Estimates - " + me.companyInfo.companyName);
			
			var endpoint = me.fqdn + "/sgx/company/estimates";
			var postType = 'GET';
    		var params = { id: me.ticker };
    		UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { me.initFinancials(me, data);  }, undefined, undefined);
    		
		},

        init_nonPremium: function() {
            $('#estimates-content-alternative').show();
            ko.applyBindings(this, $("body")[0]);
        },
		
		initFinancials: function(me, data) {

            this.dataExists(data.estimates.length);
            this.summaryData = data.estimates[0];  //index 0 is summaryData


    		var combinedData = data.estimates.slice(1); //use data from index 1
    		var currency = null;

            for( var i = 0, len = combinedData.length; i < len; i++ ) {
                var val = combinedData[i];
                if (/FY/.test(val.period)){
                    this.estimates.annually.push(val);
                }else {
                    this.estimates.quarterly.push(val);
                }
            }

            //table col widths 
            this.quarterlyTableColWidth = '15%';
            this.annualTableColWidth = '15%';
            //sort quarterly data and get th width
            if(this.estimates.quarterly.length > 0) {
                this.estimates.quarterly.sort(function(a, b){
                    return a.periodDate > b.periodDate;
                });

                this.quarterlyTableColWidth = 70/this.estimates.quarterly.length+'%';
            }

            if(this.estimates.annually.length > 0) {

                this.annualTableColWidth = 70/this.estimates.annually.length+'%';
            }


        	
        	//if (estimates.length == 5) return estimates;

    		// we need to decide whether to use the latest year end
    		// or quarter data

    		//var isQ4 = combinedData[combinedData.length - 1].period.indexOf("LTM4") != -1;
    		//estimates.splice(isQ4 ? estimates.length - 1 : 0, 1);

            if(this.dataExists())
			 this.currency(combinedData[0].filingCurrency);
			
			// initialize the chart without a series
			this.initChart(me);

            ko.applyBindings(this, $("body")[0]);
			
    		// resize
			
			
		},		
		initChart: function(me) {

    		var chart = $('#bar-chart').highcharts({
    			chart: {
    				alignThresholds: true,
    				width: me.getChartWidth(1),
    				height: me.getChartHeight()
    			},
    			plotOptions: {
    				allowPointSelect: true,
    				line: {
        				marker: {
                            enabled: true,
        					states: {
        						hover: {
        							enabled: false
        						},
        						select: {
        							enabled: false
                                }
        					}
        				}
    				}
    			},
    			legend: {
    				enabled: false
    			},
    			title: undefined,
    			credits: {
    	            enabled: false
    	        },
				tooltip: {
					useHTML: true
				},
                xAxis: {
                    categories: [],
                    plotBands: {
                        color: '#e2e2e2',
                        from: 0
                    },
    	        	labels: {
    	        		useHTML: true,
    	        		fontSize: '10px'
    	        	}
                },
                yAxis: {
                	title: null
                }
    		});
            setTimeout(function() { me.resizeIframeSimple(); }, 300);
    		
		},
		
		handleClick: function(model, data, event) {
			var el = $(".checkbox", $(event.currentTarget).closest("tr"));
			model.chartData(model, el);
		},
		
		canUncheck: function(model, name) {
			var ret = false;
			$.each(model.series(), function(idx, series) { if ($(".trigger", series).data().name == name) { ret = true; } });
			return ret;
		},
		
		chartData: function(model, el) {
			
			// already checked
			if ($(el).hasClass("checked")) {
				model.removeSeries(model, el);
				return;
			}
			
			// block it
    		if ($(".checked").length >= 5) {
                model.modal.open({ type: 'alert',  content: '<h4>Chart Company Estimates <span>(Select up to 5)</h4><p>Only five data points can be charted at a time. Remove a data point before selecting a new one.</p>' });
    			return;
    		}

    		// check for data in the row
    		if ($(el).siblings().filter(function() { return $(this).text() == "-"; }).length == $(el).siblings().length -1 ) {
				model.modal.open({ type: "alert", content: "<p>No data available for this series.</p>" });
    			return;
    		}
    		
			// render the chart 
			this.renderChart(model, el);
			
		},
		
    	removeSeries: function(model, el) {
    		
    		var name = $(".trigger", el).attr("data-name");
    		var chart = $('#bar-chart').highcharts();

    		// remove the series
    		$(el).removeClass("checked");

    		// remove the series from the chart
    		chart.get(name).remove();
    		chart.setSize(this.getChartWidth(chart.series.length - 1), this.getChartHeight(), true);
    		
    		// latest series
    		this.series($(".checked"));
    		
    	},
    	
		renderChart: function(me, el) {
			
    		// check the box
    		$(el).addClass("checked");
    		
    		// now update the chart
			var chart = $('#bar-chart').highcharts();
			var trigger = $(".trigger", el);
			var data = $(trigger).data();
			var name = $(trigger).text().trim();

            var categories = [];
             $.each($(".data-point-container thead .col-title"), function(key, val) {
                var txt = "";
                $("span", $(this)).each(function(i, e) {
                    if (i > 0) txt += "<br />";
                    txt += $(this).text();
                });
                categories.push(txt); 
             });

            chart.xAxis[0].setCategories(categories);

			// create series data
			var eventsConfig = { mouseOver: function() { this.series.yAxis.update({ title: { style: { fontWeight: "bold" } }, labels: { style: { fontWeight: "bold" } } }); }, 
                                    mouseOut: function() { this.series.yAxis.update({ title: { style: { fontWeight: "normal" } }, labels: { style: { fontWeight: "normal" } } }); } };
			var seriesData = [];
			$(el).siblings().not(".uncheck").each(function(idx, td) {
				var val = typeof $(td).attr("data-value") === "undefined" ? 0 : parseFloat($(td).attr("data-value"));
				seriesData.push({ y: val, events: eventsConfig });
			});
			
			//axis info
			chart.addAxis({
		    	id: data.name,
		    	title: { text: name },
		    	opposite: me.hasLeftYAxis(),
				labels: {
                    formatter: function() {
                    	var fmt = data.hasOwnProperty("format") ? data.format : ""; 
                    	if (fmt == "cash") return Highcharts.numberFormat(this.value);
                    	else if (fmt == "percent") return this.value + "%";
                        return Highcharts.numberFormat(this.value, 3);
                    }
				}
			});
			
			var sData = {
                name: name,
                id: data.name + "-series",
				type: me.getSeriesType(data.group),
                data: seriesData,
                yAxis: data.name,
                zIndex: me.getSeriesType(data.group) == "line" ? 50 : 1,
                parentName: $(".section", $(trigger).closest("tbody").prev()).text().trim(),
                color: me.getColor(chart.series)
			};
				
			// add the series data
			chart.addSeries(sData);

            //adjusting min/max for y-axis so that the grid lines line up
            // remove if it's causing problems
            var seriesLen = this.series().length;
            if(seriesLen != 0){
                yAxis0Extremes = chart.yAxis[1].getExtremes();
                yAxisMaxMinRatio = yAxis0Extremes.max / yAxis0Extremes.min;
                yAxis1Extremes = chart.yAxis[seriesLen+1].getExtremes();
                yAxis1Min = (yAxis1Extremes.max / yAxisMaxMinRatio).toFixed(0);
                chart.yAxis[seriesLen+1].setExtremes(yAxis1Min, yAxis1Extremes.max);

            }

			chart.setSize(me.getChartWidth(chart.series.length), me.getChartHeight(), true);
    		setTimeout(function() { me.resizeIframe(me.getTrueContentHeight(), $('#bar-chart').position().top); }, 100); 
    		
    		// latest series
    		this.series($(".checked"));

		},
		
    	hasLeftYAxis: function() {
    		var chart = $('#bar-chart').highcharts(), ret = false;
    		if (chart.hasOwnProperty("yAxis")) { $.each(chart.yAxis, function(idx, axis) { if (typeof axis.opposite !== "undefined" && !axis.opposite) { ret = true; } }); }
    		return ret;
    	},
    	
    	getColor: function(series) {
    		var colors = [ '#565a5c', '#1e2171', '#BED600', '#0094B3', '#BF0052' ];
    		$.each(series, function(idx, data) {  colors = $.grep(colors, function(val) { return data.color != val; }); });
    		return colors[0];    		
    	},
            	
    	getChartWidth: function(seriesCount) {
    		var div = $("#bar-chart");
    		var width = (seriesCount * parseInt($(div).attr("series-increment"))) + parseInt($(div).attr("default-width"));
    		return width;
    	},
    	
    	getChartHeight: function() {
    		var height = parseInt($("#bar-chart").attr("default-height"));
    		return height;
    	},
    	
    	getSeriesType: function(group) {
    		var groups = [];
    		groups["pure"] = "column";
    		groups["per"] = "line";
    		groups["ratio"] = "column";
    		return groups[group];
    	},
		showEstQuarterly: function(model, el){
			$('#Annual .checkbox').each( function(){
				if ($(this).hasClass('checked')) { ;
					model.removeSeries(model, this);
				}		            
			});
			CF.quarterlyTab(true);
			CF.annualTab(false);
			$('.quarterly a').addClass('active');
			$('.annual a').removeClass('active');
		},
		showEstAnnual: function(model, el){
			$('#Quarterly .checkbox').each( function(){
				if ($(this).hasClass('checked')) { 
					model.removeSeries(model, this);
				}		            
			});
			CF.quarterlyTab(false);
			CF.annualTab(true);	
			$('.annual a').addClass('active');
			$('.quarterly a').removeClass('active');
		} 

	};

    /**
     * Experimental Highcharts plugin to implement chart.alignThreshold option.
     * Author: Torstein Hønsi
     * Last revision: 2013-12-02
     */
    (function (H) {
        var each = H.each;
        H.wrap(H.Chart.prototype, 'adjustTickAmounts', function (proceed) {
            var ticksBelowThreshold = 0,
                ticksAboveThreshold = 0;
            if (this.options.chart.alignThresholds) {
                each(this.yAxis, function (axis) {
                    var threshold = axis.series[0] && axis.series[0].options.threshold || 0,
                        index = axis.tickPositions && $.inArray(threshold, axis.tickPositions);

                    if (index !== undefined && index !== -1) {
                        axis.ticksBelowThreshold = index;
                        axis.ticksAboveThreshold = axis.tickPositions.length - index;
                        ticksBelowThreshold = Math.max(ticksBelowThreshold, index);
                        ticksAboveThreshold = Math.max(ticksAboveThreshold, axis.ticksAboveThreshold);
                    }
                });

                each(this.yAxis, function (axis) {
                    
                    var tickPositions = axis.tickPositions;

                    if (tickPositions) {

                        if (axis.ticksAboveThreshold < ticksAboveThreshold) {
                            while (axis.ticksAboveThreshold < ticksAboveThreshold) {
                                tickPositions.push(
                                    tickPositions[tickPositions.length - 1] + axis.tickInterval
                                );
                                axis.ticksAboveThreshold++;
                            }
                        }

                        if (axis.ticksBelowThreshold < ticksBelowThreshold) {
                            while (axis.ticksBelowThreshold < ticksBelowThreshold) {
                                tickPositions.unshift(
                                    tickPositions[0] - axis.tickInterval
                                );
                                axis.ticksBelowThreshold++;
                            }

                        }
                        axis.min = tickPositions[0];
                        axis.max = tickPositions[tickPositions.length - 1];
                    }
                });
            } else {
                proceed.call(this);
            }

        })
    }(Highcharts)); 
	
	return CF;
	
});