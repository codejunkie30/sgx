define([ "wmsi/utils", "knockout", "client/modules/tearsheet" ], function(UTIL, ko, TS) {

  //source knockout docs
  ko.bindingHandlers.slideVisible = {
    update: function(element, valueAccessor, allBindings) {
        // First get the latest data that we're bound to
        var value = valueAccessor();

        // Next, whether or not the supplied model property is observable, get its current value
        var valueUnwrapped = ko.unwrap(value);

        // Grab some more data from another binding property
        var duration = allBindings.get('slideDuration') || 300; 
        
        // Now manipulate the DOM element
        if (valueUnwrapped == true)
            $(element)
              .slideDown(duration) 
              .prev()
              .removeClass('bottom-space');

        else
            $(element)
              .slideUp(duration)  
              .prev()
              .addClass('bottom-space');
      }
  };

  var modalContent = [
    {title:'What\'s the Moving Average Convergence Divergence?', text:'The MACD combines two moving averages into a single indicator. This offers you a potentially simpler way to read the trends suggested by the simple moving averages. It also offers clues about how much momentum might have built up to sustain the prevailing trend.'},
    {title:'What Does The Histogram Show?', text:'Accompanying the MACD indicator and signal line is a histogram that represents the difference between the two. The columns of the histogram extend above or below zero. The longer the column at any point, the greater the momentum in that direction. Positive numbers are bullish and negative, bearish.'},
    {title:'What Does The Signal Line Show?', text: 'The MACD signal line shows how quickly the MACD itself is changing, and in which direction. When the MACD line crosses the signal line on an upswing, it creates a bullish signal (on a downswing, bearish).'},
    {title:'Why I Should Look at This Chart?', text:'The MACD indicator is the difference between short-term and medium-term moving averages of the share price. An MACD reading above zero occurs when a price might have been rising more quickly in recent days than it had been some weeks earlier (below zero, the reverse).'}
  ]

  var TechnicalCharting = {

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

    //reference to chart class/function
    indicators_chart: null,

    financials_chart: null,

    chartSeriesConfig: null,

    activeTab: ko.observable('indicators'),

    controlBoxVars: {
      simpleMA: ko.observable(true),
      divergenceMA: ko.observable(true)
    },

    toggleControlBox: function(data) {
      var val = this.controlBoxVars[ data ]();
      val = !val;
      this.controlBoxVars[ data ]( val );

    },


    /* Modal related functionality */
    modalTitle: ko.observable(''),
    modalText: ko.observable(''),
    modalVisible: ko.observable(false),
    modalContent: modalContent,
    modalActiveContentNumber: ko.observable(0),
    overlay: $('<div>').addClass('grayout'),
    contentArray: function() {
      var len = modalContent.length;
      var arr = [];
      for(var i=0; i<len; i++) {
        arr.push(i);
      }
      return arr;
    },
    modalNext: function(){
      var len = modalContent.length;
      var current = this.modalActiveContentNumber();
      if( current == len-1 ) return;
      this.modalActiveContentNumber(++current);
    },
    modalPrev: function() {
      var len = modalContent.length;
      var current = this.modalActiveContentNumber();
      if(current == 0) return;
      this.modalActiveContentNumber(--current);
    },

    viewModal: function(num){
      
      $('body').append(this.overlay);
      this.modalActiveContentNumber(num);
      this.modalVisible(true);

    },
    destroyModal: function() {
      this.modalVisible(false);
      this.overlay.remove();
    },
    /* End modal related code */


    changeTab: function(tabName){

      if( tabName == this.activeTab ) return;
      this.activeTab(tabName);
    },

    init_nonPremium: function() {

      $('.technical-charting').remove();
      $('.technical-charting-alternative').show();
      ko.applyBindings(this, $("body")[0]);
      setTimeout(function(){ PAGE.resizeIframeSimple(); }, 50);

    },

    init_premium: function() {

      var self = this;
      self.activeTab.subscribe(function(data){
        if(data == 'financials' && self.financials_chart == null) {
          self.financials_chart = new HS_Chart('#financials-chart-container');
        }
      }, self);

      self.indicators_chart = new HS_Chart('#technical-chart-container');
      self.indicators_chart.chartReady.subscribe(function(){

        //temporarily data call is stored here
        self.chartSeriesConfig = {
            "15day":{
              getData: function(){
                return self.indicators_chart.fifteenDayData;
              }
            },
            "50day": {
              getData: function(){
                return self.indicators_chart.fiftyDayData;
              }
            },
            "MACD": {
              getData: function(){
                return self.indicators_chart.macdData;
              }
            },
            "Histogram": {
              getData: function() {
                return self.indicators_chart.histogramData;
              }
            }
          }
          setTimeout(function(){ PAGE.resizeIframeSimple(); }, 50);
      });

      ko.applyBindings(this, $("body")[0]);

    },

    initPage: function() {

      var self = this;
      $.extend(true, this, TS);
      this.init();  //this is tearsheet.js init
      PAGE.checkStatus();

      self.premiumUser.subscribe(function(data){

        if(data == true)
          self.init_premium();
        else 
          self.init_nonPremium();
        
      });

    },

    //for keeping track of checkbox choices
    control:{
      simpleAvg: ko.observableArray([]),
      convDivg: ko.observableArray([])
    },



    processCheckbox: function(data, event) {

      event.target.disabled = true;
      var dataVal = event.target.value.split('.');
      var addSeriesBool = event.target.checked;
      var contextArray = this.control[ dataVal[0] ];
      var contextValue = dataVal[1];

      //need secondary axis when going from 0 -> 1, need to remove it when going from >0 to 0 for convDivg
      var initialLength = this.control.convDivg().length;
      (addSeriesBool)? contextArray.push(contextValue): contextArray.remove(contextValue);
      var finalLength = this.control.convDivg().length;

      var axisAction = (initialLength > 0 && finalLength == 0)? false: (initialLength == 0 && finalLength > 0)? true: null;
      this.modifyIndicatorsChart( contextValue, addSeriesBool, event, axisAction );
      return true;

    },



    modifyIndicatorsChart: function(value, addSeriesBool, event, axisAction) {

      var self = this, seriesObj;

      if( axisAction !== null ){
        (axisAction)? this.indicators_chart.addSecondaryAxis(): this.indicators_chart.removeSecondaryAxis();  //add or remove secondary axis as needed. NOTE: index:2
      }


      var defaultSeriesConfig = {
        //simple Moving Area
        '15day': { type: 'line', id: value, name: '15-day SMA', dataGrouping: { units: self.indicators_chart.groupingUnits} },
        '50day': { type: 'line', id: value, name: '50-day SMA', dataGrouping: { units: self.indicators_chart.groupingUnits} },
        //Moving avg convg
        'MACD': { type: 'line', id: value, name: 'MACD', yAxis:2, dataGrouping: { units: self.indicators_chart.groupingUnits} },
        'Histogram': { type: 'column', id: value, name: 'Histogram', yAxis:2, dataGrouping: { units: self.indicators_chart.groupingUnits} },
        'SignalLine': { type: 'line', id: value, name: 'Signal line', yAxis:2, dataGrouping: { units: self.indicators_chart.groupingUnits} }
      };

      if(addSeriesBool) {

        var seriesData = this.chartSeriesConfig[ value ].getData();
        var seriesObj = defaultSeriesConfig[ value ];
        seriesObj.data = seriesData;
        this.indicators_chart.addSeries(seriesObj);

      }
      else {

        this.indicators_chart.removeSeries(value);

      }

      this.indicators_chart.redraw();
      event.target.disabled = false;

    }

  }

  var HS_Chart = function(chartId) {
    var self=this;

    this.chartElement = null;                 //ref to chart element (not chart options)
    this.dataReady = ko.observable(false);  //subscriber for when data returns from ajax call
    this.chartReady = ko.observable(false);   //subscriber for when chart rendering is complete

    // Data containers  :mock data api | will be removed later
    this.defaultData = [];
    this.fifteenDayData = [];
    this.fiftyDayData = [];

    this.macdData = [];
    this.histogramData = []

    this.initiateData();
    var optionObj = this.getGenericChartObj();

    this.dataReady.subscribe(function(data){
      optionObj.series.push(
        {
          type: 'line',
          name: 'AAPL',
          data: self.defaultData,
          yAxis:0,
          dataGrouping: {
            units: self.groupingUnits
          }
        });

      $(chartId).highcharts('StockChart', optionObj);
      self.chartElement = $(chartId).highcharts();
      self.chartReady(true);
    });

  }

  HS_Chart.prototype.initiateData = function(){
    var self=this;

      $.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=aapl-ohlcv.json&callback=?', function (data) {
      //$.getJSON('http://192.168.1.37:8000/fakedata?callback=?', function (data) {

          // split the data set into ohlc and volume
           var dataLength = data.length;
             // set the allowed units for data grouping
              self.groupingUnits = [[
                  'week',                         // unit name
                  [1]                             // allowed multiples
              ], [
                  'month',
                  [1, 2, 3, 4, 6]
              ]],
              i = 0;

              //self.groupingUnits = null;

          for (i; i < dataLength; i += 1) {
              self.defaultData.push([
                  data[i][0],   // the date
                  data[i][1],   // open
                  data[i][2],   // high
                  data[i][3],   // low
                  data[i][4]    // close
              ]);

              self.fifteenDayData.push([
                  data[i][0], 
                  data[i][1]-5, 
                  data[i][2]-5, 
                  data[i][3]-16, 
                  data[i][4]-5 
              ]);

              self.fiftyDayData.push([
                  data[i][0], 
                  data[i][1]+5, 
                  data[i][2]+5, 
                  data[i][3]+20, 
                  data[i][4]+10 
              ]);

              self.macdData.push([
                  data[i][0],   // the date
                  data[i][5]    // the volume
              ]);

              self.histogramData.push([
                  data[i][0],   // the date
                  data[i][5]    // the volume
              ]);
          }
          self.dataReady(true);

        });

  };

  HS_Chart.prototype.redraw = function() {

    this.chartElement.redraw();

  }


  HS_Chart.prototype.addSeries = function(optionObject) {

    this.chartElement.addSeries(optionObject, false);

  }

  HS_Chart.prototype.removeSeries = function(seriesId) {

    var series = this.chartElement.get(seriesId);
    if( !series ) return;
    this.chartElement.get(seriesId).remove(false);

  }


  HS_Chart.prototype.addSecondaryAxis = function() {

    this.chartElement.yAxis[0].update({
      height:'60%'
    }, false);

    this.chartElement.addAxis({
          id:'secondary-axis',
          labels: {
              align: 'right',
              x: -3
          },
          title: {
              text: 'Volume'
          },
          top: '65%',
          height: '35%',
          offset: 0,
          opposite:true,
          lineWidth: 2
    }, false, false);

  }

  HS_Chart.prototype.removeSecondaryAxis = function() { 

    this.chartElement.get('secondary-axis').remove(false);
    this.chartElement.yAxis[0].update({
      height:'100%'
    }, false); 

  }

  HS_Chart.prototype.getGenericChartObj = function() {
    var options = 
      {
        rangeSelector: {
          selected: 1,
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
        yAxis: [{
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

      series: []
      };

    return options;
  }

return TechnicalCharting;


});