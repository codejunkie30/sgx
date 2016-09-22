define([ "jquery", "knockout", "wmsi/page", "highstock" ], function( $, ko, PAGES ) {


  function HS_Chart(chartId, companyName) {

    this.chartElement = null;                 //ref to chart element 
    this.optionObj = null;
    this.dataReady = ko.observable(false);    //subscriber for when data returns from ajax call
    this.chartReady = ko.observable(false);
    this.chartId = chartId;
    this.chartHeight = 500;
    this.companyName = companyName || 'Default';

    this.priceData = [];
    this.volumeData = [];

    //this.initData();
    
    this.dataReady.subscribe(function(data){
      this.initChart(companyName);
    }, this);

  }

  HS_Chart.prototype.initChart = function(companyName) {
      
      var self = this;
      var chartOptions = this.getGenericChartObj();
      chartOptions.series[0].name = companyName;
      $(this.chartId).highcharts('StockChart', chartOptions);
      setTimeout(function(){ 
        self.chartElement = $(self.chartId).highcharts(); 
      }, 500);
      this.chartReady(true);
  }

  HS_Chart.prototype.initData = function(data){

    var dataLength;
    var dataLenArray = [];

    var self=this;
    self.xData = [], self.yData = [];
    
    self.priceData = toHighChartsSimple(data.price, true);
    self.lowPrice = toHighChartsSimple(data.lowPrice);
    self.openPrice = toHighChartsSimple(data.openPrice);
    self.highPrice = toHighChartsSimple(data.highPrice);
    self.volumeData = toHighChartsSimple(data.volume);

    dataLength = Math.min.apply(null, dataLenArray);

    //get RSI relevant data
    self.dataRSI = [];
    for(var i=0, len=dataLength; i < len; i++) {
      var arr = [];
      arr.push(self.openPrice[i].y);
      arr.push(self.highPrice[i].y);
      arr.push(self.lowPrice[i].y);
      arr.push(self.priceData[i].y);

      self.dataRSI.push(arr);

    }

    self.dataReady(true);

        //});

    function toHighChartsSimple( data, separateAxes ) {

      dataLenArray.push(data.length);
      var ret = [];
      $.each(data, function(idx, row) {

        var xVal = row.date;
        var yVal = row.value;

        if( separateAxes ){
          self.xData.push( xVal );
          self.yData.push( yVal );
        }
        ret.push({x: xVal, y: yVal});

      });
      return ret;

    }

  };


  HS_Chart.prototype.redraw = function() {

    this.chartElement.redraw();

  }


  HS_Chart.prototype.addSeries = function(seriesObject) {

    this.addAxis(seriesObject);
    this.chartElement.addSeries(seriesObject, false);

  }

  HS_Chart.prototype.removeSeries = function(seriesObject) {

    var series = this.chartElement.get( seriesObject.id );
    series.remove(false);
    var axis = this.chartElement.get( seriesObject.yAxis );
    if( axis.series.length == 0) {
      axis.remove(false);
      this.repositionAxes();
    }


  }


  HS_Chart.prototype.addAxis = function(seriesObject) {

    var axis = seriesObject.yAxis;
    if( this.chartElement.get(axis) ) return;

    var axisObject =  {
        id: axis,
        labels: {
            align: 'right',
            x: -3
        },
        title: {
            text: seriesObject.axisName
        },
        top: '',
        height: '',
        offset: 0,
        opposite:true,
        lineWidth: 2
      };


    if(seriesObject.id == 'RSI') {
      axisObject.
           plotLines = [{
              value: 70,
              id:'overBought-plotline',
              color: 'orange',
              width: 1
           }, {
              value: 30,
              id:'overSold-plotline',
              color: 'orange',
              width: 1
           }];
    }

    this.chartElement.addAxis(axisObject, false, false);
    this.repositionAxes();

  }


  HS_Chart.prototype.removeAxis = function(seriesObject) {

    var axis = this.chartElement.get(seriesObject.yAxis);
    if( axis ) {
      axis.remove(false);
      this.repositionAxes();
    }

  }




  HS_Chart.prototype.repositionAxes = function() {

    var self = this;
    var primaryAxis = this.chartElement.get('primary-axis');
    var secondaryAxis = this.chartElement.get('secondary-axis');

    var allAxes = this.chartElement.yAxis;
    var additionalAxis = this.chartElement.yAxis.length - 3;

    switch (additionalAxis) {

      case 0: 
        primaryAxis.update({height:'68%'}, false);
        secondaryAxis.update({height:'30%', top:'70%'}, false);
        setTimeout(function(){ self.chartElement.setSize(718, 525) }, 500);
      break;

      case 1:
        primaryAxis.update({height:'45%'}, false);
        secondaryAxis.update({height:'20%', top:'50%'}, false);
        allAxes[3].update({height:'20%', top:'75%'}, false);
        $('section.technical-charting').animate({height:931}, 500);
        
        setTimeout(function(){ 
          self.chartElement.setSize(718, 600);
          PAGES.resizeIframeSimple( 100 ); 
        }, 500);
      break;

      case 2:
        primaryAxis.update({height:'40%'}, false);
        secondaryAxis.update({height:'18%', top:'42%'}, false);
        allAxes[3].update({height:'17.5%', top:'62%'}, false);
        allAxes[4].update({height:'18%', top:'82.5%'}, false);
        $('section.technical-charting').animate({height:1031}, 500);
        setTimeout(function(){ 
          self.chartElement.setSize(718, 700);
          PAGES.resizeIframeSimple( 100 );
         }, 500);
    }

  }


  HS_Chart.prototype.modifyPlotline = function(newVal, plotId) {

    var rsiAxis = this.chartElement.get('rsi-axis');
    if(!rsiAxis) return;
    rsiAxis.removePlotLine(plotId);
    rsiAxis.addPlotLine({value: newVal, color:'orange', width:1, id: plotId, zIndex:99});

  }


  HS_Chart.prototype.getGenericChartObj = function() {
    var options =
      {
        chart: {
          width: 718,
          height:525
        },
        rangeSelector: {
          selected: 3,
          inputEnabled:true,
          buttons: [{
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
        enabled:false
      },
      legend: {
        enabled:true
      },
      xAxis: {
      	
	        labels: {
              formatter: function() {
                  return Highcharts.dateFormat("%e. %b", this.value);
              }
	        },
	        minTickInterval: 24 * 3600 * 1000
	        
  	  },
      yAxis: [{
        id:'primary-axis',
        height: '68%',
        labels: {
          align: 'right',
          x: -3,
          formatter: function() {
            if (this.value == 0) return this.value;
            var yAxisVal = Highcharts.numberFormat(this.value,3);
            return PAGE.currentFormats.chart.format + yAxisVal.replace(/\.?0+$/,'');
          }
        },
        title: {
          text: 'PRICE'
        },
        lineWidth: 2
        }, {
          id:'secondary-axis',
          height: '30%',
          top: '70%',
          labels : {
            align:'right',
            x: -3,
            formatter: function() {
              if(this.value == 0) return this.value;
              return this.value + ' mm';
            }
          },
          title: {
            text:'VOLUME'
          },
          lineWidth:2,
          offset:0
        }
      ],
      plotOptions:{
        series: {
          animation:false,
          turboThreshold:2000
        }
      },
      tooltip: {
        formatter: function() {
          var tp = '<span style="font-size:10px">'+Highcharts.dateFormat('%A, %b %e, %Y',this.x)+'</span><br/>';
          $.each(this.points, function() {
			var currencyType;
			(this.series.yAxis.axisTitle.textStr == 'PRICE') ? currencyType = PAGE.currentFormats.chart.format : currencyType = '';
	    if(_round(this.point.y, 3)==0){
		tp += '<span style="color:'+this.series.color+'">\u25CF</span> '+this.series.name+': <b>' + "-"+'</b><br/>'
	    }else{
		tp += '<span style="color:'+this.series.color+'">\u25CF</span> '+this.series.name+': <b>' + currencyType +_round(this.point.y, 3)+'</b><br/>'
	    }
            
          });

          return tp;
        }
      },
      series: [{
          type: 'line',
          name: 'AAPL',
          showInLegend:true,
          color:'#0b236b',
          data: this.priceData,
          yAxis:'primary-axis',
        }, {
          type:'column',
          name:'Volume',
          color:'#D8E719',
          showInLegend: false,
          data:this.volumeData,
          yAxis:'secondary-axis'
        }]
      };

    return options;
  }




  var FS_Chart = function(chartId, companyName) {
    this.chartElement = null;
    this.optionObj = null;
    this.dataReady = ko.observable(false);
    this.chartReady = ko.observable(false);
    this.chartId = chartId;
    this.companyName = 'Default' || companyName;
    this.seriesObj = [];
  }

  FS_Chart.prototype.initChart = function(seriesObj) {
    var self = this;
    var chartOptions = this.getGenericChartObj();
    chartOptions.series.push(seriesObj);
    $(this.chartId).highcharts(chartOptions);
    setTimeout(function(){ 
      self.chartElement = $(self.chartId).highcharts(); 
      self.chartReady(true);
    }, 500);
    
  }

  FS_Chart.prototype.redraw = function() {
    this.chartElement.redraw();
  }

  FS_Chart.prototype.addAxis = function(serviceObj) {

    var axis = this.chartElement.get(serviceObj.serviceName);
    if( axis )
      return;

    var standardAxis = this.chartElement.yAxis.filter(function(item) {
      return item.userOptions.opposite == false;
    });
    var oppositeBool = (standardAxis.length > 0)? true: false;

    this.chartElement.addAxis({
      id:serviceObj.serviceName,
      name: serviceObj.axisName,
      title: {text: serviceObj.axisName },
      opposite:oppositeBool,
      labels: {
        formatter: function(){
          if( this.value == 0) return '0';
          return PAGE.currentFormats.chart.format + this.axis.defaultLabelFormatter.call(this);
        }
      }
    }, false, false);

  }

  FS_Chart.prototype.removeAxis = function(serviceObj) {
    var axis = this.chartElement.get(serviceObj.serviceName);
    axis.remove(false);
  }

  FS_Chart.prototype.addSeries = function(data, serviceObj) {
    var series = {
      data: data,
      id: serviceObj.key,
      color: serviceObj.color,
      name: serviceObj.displayName,
      yAxis: serviceObj.serviceName 
    };
    this.addAxis(serviceObj);
    this.chartElement.addSeries(series, false);

  }

  FS_Chart.prototype.removeSeries = function(serviceObj) {
    var series = this.chartElement.get(serviceObj.key);
    if (series)
      series.remove(false);
    
    var axis = this.chartElement.get(serviceObj.serviceName);
    if(axis.series.length == 0) {
      axis.remove(false);
    }
  }

  FS_Chart.prototype.getGenericChartObj = function() {
    var options =
    {
      chart: {
        width: 950,
        height:500
      },
		lang: {
	      thousandsSep: ','
	    },
      legend: {
        useHTML:true,
        enabled:true,
        labelFormatter: function() {
          var lbl = '<span style="font-size:12px">'+this.yAxis.userOptions.name+'</span>';
          lbl += '<br />';
          lbl += '<span style="font-weight:normal">'+this.name+'</span>';
          return lbl;

        }
      },
      credits: {
        enabled:false
      },
      title: {
        text:''
      },
      xAxis: {
        categories:[]
      },
      yAxis: {
        title: {
          text:'Income'
        },
        labels: {
          formatter: function(){
            if( this.value == 0) return '0';
            return PAGE.currentFormats.chart.format + this.axis.defaultLabelFormatter.call(this);
          }
        },
        id:'income',
        name: 'Income',
        opposite:false
      },
	  tooltip: {
        formatter: function() {
          var tp = '<span style="font-size:10px">'+this.x+'</span><br/>';
          $.each(this.points, function() {
            if(_round(this.point.y, 3)==0){
        	tp += '<span style="font-weight: bold; color:'+ this.series.color +'">\u25CF</span> <span style="font-size: 12px;"> '+this.series.name+': </span><span style="font-size: 12px; font-weight:bold">' + "-" +'</span><br />';
            }
            else{
        	tp += '<span style="font-weight: bold; color:'+ this.series.color +'">\u25CF</span> <span style="font-size: 12px;"> '+this.series.name+': </span><span style="font-size: 12px; font-weight:bold">' + PAGE.currentFormats.chart.format + _round(this.y, 3) +'</span><br />';
            }

          });

          return tp;
        },
        shared:true  
		},
      series: []
    }

    return options;
  }

  //helper function for decimals
  function _round(num, places) {
    var rounder = Math.pow(10, places);
    var roundee = num * rounder;
    return (Math.round(roundee)/rounder)
           .toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  }


  return { HS_Chart: HS_Chart, 
            FS_Chart: FS_Chart};

});