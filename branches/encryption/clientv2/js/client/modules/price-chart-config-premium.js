define([ "wmsi/utils", "knockout" ], function(UTIL, ko) {

	var defaults = {
        	
		title: undefined,
			
		colors: [ 'rgb(206, 217, 236)', '#BFCE00' ],
            
		chart: {
			backgroundColor:'rgba(255, 255, 255, 0.1)',
			height: 445
		},
            
		plotOptions: {
			series: {
				animation: false
			},
			area: {
				lineColor: 'rgb(10, 63, 160)'
			}
		},
            
		rangeSelector: {
			inputEnabled: false,
			selected: 5,
			buttons: [{
				type: 'day',
				count: 1,
				text: '1d'
            },{
				type: 'day',
				count: 5,
				text: '5d'
            }, {
                type: 'month',
                count: 1,
                text: '1m'
            }, {
                type: 'month',
                count: 3,
                text: '3m'
            }, {
                type: 'month',
                count: 6,
                text: '6m'
            }, {
                type: 'year',
                count: 1,
                text: '1y'
            }, {
                type: 'year',
                count: 3,
                text: '3y'
            }, {
                type: 'year',
                count: 5,
                text: '5y'
            }, {
                type: 'all',
                text: 'All'
            }]            		        
	    },
		    
		credits: {
            enabled: false
        },
	        
        tooltip: {
        	enabled: true,
            useHTML: true,
            crosshairs: [ true, true ],
            shared: true
        },
        
        xAxis: {
        	
	        labels: {
                formatter: function() {
                    return Highcharts.dateFormat("%e. %b", this.value);
                }
	        }
	        
    	},

        yAxis: [
                {
    		        title: undefined,
    		        height: 170,
    		        lineWidth: 2,
    		        animation: false,
    		        minRange: .001,
    		        labels: {
                        formatter: function() {
                        	if (this.value == 0) return;
                            return PAGE.currentFormats.chart.format + _round(this.value, 3);
                        },
                        style: {
                        	color: "#000000",
                        	fontWeight: "bold"
                        },
                        align: 'right',
                        y: -3,
                        x: -10
    		        }
                },
                {
    		        title: undefined,
    		        top: 250,
    		        height: 60,
    		        offset: 0,
    		        lineWidth: 2,
    		        animation: false,
    		        minRange: .01,
    		        labels: {
                        formatter: function() {
                            return this.chart.yAxis[1].max + " mm";
                        },
                        style: {
                        	color: "#000000",
                        	fontWeight: "bold"
                        },
                        y: -45,
                        x: -10
    		        }
                }
         ],
         

         labels: {
         	items: [
         	    {
                 	html: "Price",
                 	style: {
                     	top: '-22px',
                     	left: '596px'
                 	}
         	    },
         	    {
                 	html: "Volume",
                 	style: {
                     	top: '185px',
                     	left: '582px'
                 	}
         	    }
         	],
	        	style: {
	        		color: "#666",
	        		fontWeight: "bold"
	        	}
         }
    	
	};
	
	return defaults
//helper function for decimals
  function _round(num, places) {
    var rounder = Math.pow(10, places);
    var roundee = num * rounder;
    return _numberWithCommas(Math.round(roundee)/rounder);
  }
function _numberWithCommas(x) {
      return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  }
});