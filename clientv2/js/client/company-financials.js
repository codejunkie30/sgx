define([ "wmsi/utils", "knockout", "text!client/data/financials.json", "client/modules/tearsheet", "highstock" ], function(UTIL, ko, FINANCIALS, TS) {

	var CF = {
			
		sections: null,
		dataPoints: ko.observable([]),
		currency: ko.observable(""),
		series: ko.observable([]),
		legendItems: ko.computed(function() {}),
		premiumUser: ko.observable(),	
		premiumUserEmail: ko.observable(),		
		premiumUserAccntInfo: ko.observable(),
		libLoggedIn: ko.observable(),
		libTrialPeriod: ko.observable(),
		libTrialExpired: ko.observable(),
		libSubscribe: ko.observable(),
		libAlerts: ko.observable(),
		libCurrency: ko.observable(),
		currentDay: ko.observable(),
		initPage: function() {
			this.showLoading();
			// extend tearsheet
			$.extend(true, this, TS);

			// set up some basics
			this.sections = JSON.parse(FINANCIALS).financials;
			this.series([]);
			this.dataPoints([]);
			this.currency("");

			this.legendItems = ko.computed(function() {
				if (this.series().length == 0) return [];
				var chart = $('#bar-chart').highcharts(), ret = [];
				$.each(chart.series, function(idx, series) { ret.push(series.userOptions) });
				return ret;
			}, this);
			
			var self = this;
			this.init(function() { self.finish(self); });
			
			PAGE.checkStatus();
			
		},
		
		finish: function(me) {
			
    		// finish other page loading
    		ko.applyBindings(this, $("body")[0]);

			PAGE.hideLoading();

			me.trackPage("SGX Company Financials - " + me.companyInfo.companyName);
			
    		var endpoint = me.fqdn + "/sgx/company/financials";
			var postType = 'POST';
    		var params = { id: me.ticker };
    		UTIL.handleAjaxRequest(endpoint, postType, params, undefined, 
				function(data) { 
					me.initFinancials(me, data);  
				}, 
				PAGE.customSGXError,
				undefined);
	    		
		},
		
		initFinancials: function(me, data) {
			
    		var financials = data.financials.slice();
    		var currency = null;
    		
    		// let's make sure they're sorted
    		financials.sort(function(a, b) {
        		var a = parseInt(a.absPeriod.replace("FY", "").replace("LTM", ""));
        		var b = parseInt(b.absPeriod.replace("FY", "").replace("LTM", ""));
        		return a - b;
        	});          		
        	
        	if (financials.length == 5) return financials;

    		// we need to decide whether to use the latest year end
    		// or quarter data
    		var isQ4 = financials[financials.length - 1].absPeriod.indexOf("LTM4") != -1;
    		financials.splice(isQ4 ? financials.length - 1 : 0, 1);  
			
			this.dataPoints(financials);
			this.currency(financials[0].filingCurrency);
			
			// initialize the chart without a series
			this.initChart(me);
			
    		// resize
			setTimeout(function(){
			me.resizeIframeSimple();
			}, 500);
			
		},		
		initChart: function(me) {
			
			// categories
			var categories = [];
    		$(".data-point-container thead:first th").not(".section, .uncheck").each(function(idx, el) {
    			var txt = "";
    			$("span", $(this)).each(function(i, e) {
    				if (i > 0) txt += "<br />";
    				txt += $(this).text();
    			});
    			categories.push(txt); 
    		});

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
                    categories: categories,
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
                model.modal.open({ type: 'alert',  content: '<h4>Chart Company Financials <span>(Select up to 5)</h4><p>Only five data points can be charted at a time. Remove a data point before selecting a new one.</p>' });
    			return;
    		}

    		// check for data in the row
            var numOfCols = this.dataPoints().length;
            var numOfnulls = $(el).siblings().filter(function() { return $(this).text() == "-"; }).length;
    		if ( numOfCols == numOfnulls ) {
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

			// create series data
			var eventsConfig = { mouseOver: function() { this.series.yAxis.update({ title: { style: { fontWeight: "bold" } }, labels: { style: { fontWeight: "bold" } } }); }, mouseOut: function() { this.series.yAxis.update({ title: { style: { fontWeight: "normal" } }, labels: { style: { fontWeight: "normal" } } }); } };
			var seriesData = [];
			$(el).siblings().not(".uncheck").each(function(idx, td) {
				var val = typeof $(td).attr("data-value") === "undefined" ? 0 : parseFloat($(td).attr("data-value"));
				seriesData.push({ y: val, events: eventsConfig });
			});
			
			// axis info
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
    	
    	getSeriesType: function(group) {
    		var groups = [];
    		groups["pure"] = "column";
    		groups["per"] = "line";
    		groups["ratio"] = "column";
    		return groups[group];
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
    	}

	};

    /**
     * Experimental Highcharts plugin to implement chart.alignThreshold option.
     * Author: Torstein H�nsi
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