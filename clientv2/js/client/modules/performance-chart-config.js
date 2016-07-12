define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {

	var defaults = {
		title : undefined,

		chart : {
			backgroundColor : 'rgba(255, 255, 255, 0.1)',
			height : 445,
			width : 700
		},

		plotOptions : {
			series : {
				animation : false
			},
			line: {
	            
                events: {
                    legendItemClick: function () {
                        return false;
                    }
                },
                showInLegend: true
			}
		},

		rangeSelector : {
			inputEnabled : false,
			selected : 4,
			buttons : [ {
				type : 'day',
				count : 5,
				text : '5d'
			}, {
				type : 'month',
				count : 1,
				text : '1m'
			}, {
				type : 'month',
				count : 3,
				text : '3m'
			}, {
				type : 'month',
				count : 6,
				text : '6m'
			}, {
				type : 'year',
				count : 1,
				text : '1y'
			}, {
				type : 'year',
				count : 3,
				text : '3y'
			}, {
				type : 'year',
				count : 5,
				text : '5y'
			}, {
				type : 'all',
				text : 'All'
			} ]
		},

		scrollbar : {
			enabled : false
		},

		navigator : {
			enabled : false
		},

		credits : {
			enabled : false
		},

		tooltip : {
			enabled : true,
			useHTML : true,
			crosshairs : [ true, true ],
			shared : false
		},

		legend: {
			enabled : false           
        },

		xAxis : {

			labels : {
				formatter : function() {
					return Highcharts.dateFormat("%e/%b/%Y", this.value);
				},
				style: {
                	color: "#000000",
                	fontWeight: "bold"
                },
				x: 0
			}

		},

		yAxis : [
				{
					lineWidth : 2,
					animation : false,
					minRange : .001,
					labels : {
						formatter : function() {
							if (this.value == 0)
								return;
							return "S$ "
									+ _round(this.value, 3);
						},
						style : {
							color : "#000000",
							fontWeight : "bold"
						},
						align : 'right',
						y : -3,
						x : -10
					},
					title: {
						style : {
							color : "#000000",
							fontWeight : "bold"
						},
				          text: 'PRICE'
			        }
				}]

	};

	return defaults
	// helper function for decimals
	function _round(num, places) {
		var rounder = Math.pow(10, places);
		var roundee = num * rounder;
		return _numberWithCommas(Math.round(roundee) / rounder);
	}
	function _numberWithCommas(x) {
		return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	}
});