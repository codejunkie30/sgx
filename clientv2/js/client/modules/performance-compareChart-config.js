define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {

	var defaults = {
			title : undefined,

			chart : {
				backgroundColor : 'rgba(255, 255, 255, 0.1)',
				height : 445,
				width : 1000
			},
			
			rangeSelector: {
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
    		
    		xAxis : {

    			labels : {
    				formatter : function() {
    					return Highcharts.dateFormat("%e/%b/%Y", this.value);
    				},
    				style: {
                    	color: "#000000",
                    	fontWeight: "bold"
                    },
    				x: 10
    			}

    		},

    		yAxis : [{
				/*lineWidth : 2,
				animation : false,
				minRange : .001,*/
				labels : {
					formatter: function () {
						return (this.value > 0 ? ' + ' : '') + this.value + '%';
					},
					style : {
						color : "#000000",
						fontWeight : "bold"
					}
				},
				/*title: {
					style : {
						color : "#000000",
						fontWeight : "bold"
					},
			          text: 'PRICE'
		        }*/
			}],

            plotOptions: {
                series: {
                    compare: 'percent'
                }
            },

            tooltip: {
                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.change}%)<br/>',
                valueDecimals: 2
            }

	};

	return defaults
});