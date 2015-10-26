define([ "jquery", "knockout", "highstock" ], function( $, ko ) {



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
      this.initChart();
    }, this);

  }

  HS_Chart.prototype.initChart = function() {
      
      var self = this;
      var chartOptions = this.getGenericChartObj();
      $(this.chartId).highcharts('StockChart', chartOptions);
      setTimeout(function(){ 
        self.chartElement = $(self.chartId).highcharts(); 
      }, 500);
      this.chartReady(true);
  }

  HS_Chart.prototype.initData = function(data){

    var self=this;
    self.xData = [], self.yData = [];
      //$.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=aapl-ohlcv.json&callback=?', function (data) {
    //$.getJSON('http://localhost:8001/fakedata?callback=?', function (data) {
    //$.getJSON('http://192.168.1.37:8001/fakedata?callback=?', function (data) {
      //console.log(data);
          self.priceData = toHighChartsSimple(data.price, true);
          self.lowPrice = toHighChartsSimple(data.lowPrice);
          self.openPrice = toHighChartsSimple(data.openPrice);
          self.highPrice = toHighChartsSimple(data.highPrice);
          
          //self.volumeData = toHighChartsSimple(data.volume);
          //get RSI relevant data
          self.dataRSI = [];
          for(var i=0, len=self.priceData.length; i < len; i++) {
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

      var ret = [];
      $.each(data, function(idx, row) {

        if( separateAxes ){
          self.xData.push(row.date);
          self.yData.push(row.value);
        }
        ret.push({x: row.date, y:row.value});

      });
      return ret;

    }

  };


  HS_Chart.prototype.redraw = function() {

    this.chartElement.redraw();

  }


  HS_Chart.prototype.addSeries = function(seriesObject, isRSI) {

    if( seriesObject.yAxis == undefined ){
      var axisId = (!isRSI)? 'secondary-axis': 'rsi-axis';
      var otherAxis = this.chartElement.get( axisId );
      var index = otherAxis.userOptions.index;
      seriesObject.yAxis = index;
    }

    this.chartElement.addSeries(seriesObject, false);

  }

  HS_Chart.prototype.removeSeries = function(seriesId) {

    var series = this.chartElement.get(seriesId);
    if( !series ) return;
    this.chartElement.get(seriesId).remove(false);

  }

  HS_Chart.prototype.repositionAxis = function() {

    var primaryAxis = this.chartElement.get('primary-axis');
    var secondaryAxis = this.chartElement.get('secondary-axis');
    var rsiAxis = this.chartElement.get('rsi-axis');

    if( secondaryAxis && rsiAxis ){

      primaryAxis.update({ height: '45%'}, false);
      secondaryAxis.update({ height: '20%', top: '50%'}, false);
      rsiAxis.update({ height: '20%', top: '75%'}, false);

    }
    else if( !rsiAxis && secondaryAxis ) {

      primaryAxis.update({ height: '60%'}, false);
      secondaryAxis.update({ height: '30%', top: '65%'}, false);

    }
    else if( rsiAxis && !secondaryAxis) {

      primaryAxis.update({ height: '60%'}, false);
      rsiAxis.update({ height: '30%', top: '65%'}, false);

    }
    else {

      primaryAxis.update({ height: '100%'}, false);

    }

  }

  HS_Chart.prototype.resizeChart = function() {
    var self = this;
    setTimeout(function(){ 
      var axisNumber = self.chartElement.yAxis.length;
      if(axisNumber == 4) {
        self.chartElement.setSize(718, 600);
      }else {
        if(self.chartElement.chartHeight == self.chartHeight) return;
        self.chartElement.setSize(718, self.chartHeight);
      }
    }, 500);
  }

  HS_Chart.prototype.resizeChartForced = function() {

    this.chartElement.setSize(718, 550, false);

  }

  HS_Chart.prototype.addAxisSecondary = function() {
    this.chartElement.addAxis({
          id: 'secondary-axis',
          labels: {
              align: 'right',
              x: -3
          },
          title: {
              text: 'MACD'
          },
          top: '',
          height: '',
          offset: 0,
          opposite:true,
          lineWidth: 2
    }, false, false);

    this.cleanupChart();

  }

  HS_Chart.prototype.addAxisRSI = function(overBought, overSold) {

    /*Note: redundant call that forces the chart to calculate height 2 times
            to prevent part of rsi chart from dissapearing*/
    if(this.chartElement.yAxis.length == 3){
      this.chartElement.setSize(718, 550, false);
    }

    this.chartElement.addAxis({
          id:'rsi-axis',
          labels: {
              align: 'right',
              x: -3
          },
          title: {
              text: 'RSI'
          },
          min:0,
          max:100,
          tickInterval:25,
           plotLines: [{
              value: overBought,
              id:'overBought-plotline',
              color: 'orange',
              width: 1
           }, {
              value: overSold,
              id:'overSold-plotline',
              color: 'orange',
              width: 1
           }],
          top: '',
          height: '',
          offset: 0,
          opposite:true,
          lineWidth: 2
    }, false, false);

    this.cleanupChart();

  }

  HS_Chart.prototype.removeAxis = function(isRSI) {

    var axisId = (isRSI)? 'rsi-axis': 'secondary-axis';
    this.chartElement.get(axisId).remove(false);
    this.cleanupChart();

  }

  HS_Chart.prototype.modifyPlotline = function(newVal, plotId) {

    var rsiAxis = this.chartElement.get('rsi-axis');
    if(!rsiAxis) return;
    rsiAxis.removePlotLine(plotId);
    rsiAxis.addPlotLine({value: newVal, color:'orange', width:1, id: plotId, zIndex:99});

  }

  //this is a weird bug that doesn't always show but the secondary axis(MACD/Histogram) MUST be at the very end
  //in the yAxis Array list or else you'll get wierd highchart errors. 
  HS_Chart.prototype.repositionSecondaryAxis = function () {

    var secondaryAxis = this.chartElement.get('secondary-axis');
    var rsiAxis = this.chartElement.get('rsi-axis');

    if( secondaryAxis && rsiAxis){
      this.chartElement.yAxis.splice(2);
      rsiAxis.userOptions.index = 2;
      secondaryAxis.userOptions.index = 3;
      this.chartElement.yAxis.push(rsiAxis);
      this.chartElement.yAxis.push(secondaryAxis);
    }
  }

  HS_Chart.prototype.cleanupChart = function(){

    this.repositionSecondaryAxis(); //bugFix @todo: remove this or prevent it from running on ie8...
    this.repositionAxis();
    this.resizeChart();

  }

  HS_Chart.prototype.getGenericChartObj = function() {
    var options =
      {
        chart: {
          width: 718,
          height:500
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
      yAxis: [{
        id:'primary-axis',
        labels: {
          align: 'right',
          x: -3
        },
        title: {
          text: 'PRICE'
        },
        lineWidth: 2
        }
      ],
      plotOptions:{
        series: {
          animation:false,
          turboThreshold:2000
        }
      },
      tooltip: {
        valueDecimals: 3
      },
      series: [{
          type: 'line',
          name: this.companyName+' Stock Price',
          showInLegend:true,
          data: this.priceData,
          yAxis:0,
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

  FS_Chart.prototype.getGenericChartObj = function() {
    var options =
    {
      legend: {
        useHTML:true,
        enabled:true,
        labelFormatter: function() {
          var lbl = '<span style="font-weight:bold">'+this.yAxis.userOptions.name+'</span>';
          lbl += '<br />';
          lbl += '<span style="font-weight:normal">'+this.name+'</span>';
          return lbl;

        }
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
        id:'income',
        name: 'Income'
      },
      series: []
    }

    return options;
  }


  return { HS_Chart: HS_Chart, 
            FS_Chart: FS_Chart};

});