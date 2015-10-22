define([ "wmsi/utils", "knockout", "knockout-validate", "client/modules/tearsheet", "client/modules/technical-chart/technical-chart-algorithms", "client/modules/technical-chart/technical-chart", "highstock" ], function(UTIL, ko, Validation, TS, Algorithms, HS_Chart) {

  ko.validation = Validation;
  ko.validation.init({insertMessages:false});

  ko.validation.rules.between = {
      validator: function(value, params) {
          var min = params[0];
          var max = params[1];

          value = parseInt(value, 10);

          if (!isNaN(value)) {
              return value >= min && value <= max;
          }
          return false;   
      },
      message: 'Value must be between {0} and {1}'
  };

  ko.validation.registerExtenders();


  ko.bindingHandlers.slideVisible = {
    update: function(element, valueAccessor, allBindings) {
        // First get the latest data that we're bound to
        var value = valueAccessor();

        // Next, whether or not the supplied model property is observable, get its current value
        var valueUnwrapped = ko.unwrap(value);

        // Grab some more data from another binding property
        var duration = allBindings.get('slideDuration') || 300; // 400ms is default duration unless otherwise specified
        
        // Now manipulate the DOM element
        if (valueUnwrapped == true)
            $(element).slideDown(duration); // Make the element visible

        else
            $(element).slideUp(duration);   // Make the element invisible

      }
  };



  var modalContent = {

    simpleMA: [
      {title:'what\'s the Simple Moving Average?', text:'The SMA smooths out the peaks and valleys of daily price volatility. When looked at for any given day, it tells you the average price for some period in the immediate past (usually the preceding 15 days or 50 days). When looked at over time (as in our graphs), the 15-day SMA suggests the shapes of short-term trading trends and the 50-day, medium-term trends.' },
      {title:'Why I Should Look at This Chart?', text:'Stock price moving averages are typically looked at in relation to the price of the stock itself. When a share price is rising consistently, it tends to stay above its SMA on any given day. When it starts to fall consistently, it eventually falls below its SMA. Being above the SMA is generally seen as a bullish signal, below, a bearish signal.'},
    ],
    divergenceMA:[
      {title:'What\'s the Moving Average Convergence Divergence?', text:'The MACD combines two moving averages into a single indicator. This offers you a potentially simpler way to read the trends suggested by the simple moving averages. It also offers clues about how much momentum might have built up to sustain the prevailing trend.'},
      {title:'What Does The Histogram Show?', text:'Accompanying the MACD indicator and signal line is a histogram that represents the difference between the two. The columns of the histogram extend above or below zero. The longer the column at any point, the greater the momentum in that direction. Positive numbers are bullish, and negative, bearish.'},
      {title:'What Does The Signal Line Show?', text: 'The MACD signal line shows how quickly the MACD itself is changing, and in which direction. When the MACD line crosses the signal line on an upswing, it creates a bullish signal (on a downswing, bearish).'},
      {title:'Why I Should Look at This Chart?', text:'The MACD indicator is the difference between short-term and medium-term moving averages of the share price. An MACD reading above zero occurs when a price might have been rising more quickly in recent days than it had been some weeks earlier (below zero, the reverse).'}
    ],
    RSI: [
      {title:'What\'s the Relative Strength Index?', text:'The Relative Strength Index (RSI) can help an investor understand whether a stock may have become overvalued or undervalued after recent trading. A reading of zero in this indicator shows that every day\'s closing price change has been negative in the recent past. A reading of 100 shows that every price change has been positive, and a reading of 50 indicates that the ups and downs cancelled each other out.'},
      {title:'Why I Should Look at This Chart?', text: 'Normal readings are typically 25 to 70. Readings above 70 suggest strong upward momentum and the possibility a stock has become too highly valued for the current market, a condition called overbought. Readings below 25 may be the result of strong downward momentum, which could have depressed the value of the stock excessively. That condition is called oversold.'}
    ]

  }


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
      divergenceMA: ko.observable(true),
      RSI: ko.observable(true)
    },

    rsiInput: {

      overBought: ko.observable(70).extend({
        digit:{message:'Please enter a valid number.'},
        between:[0,100]
      }),

      overSold: ko.observable(30).extend({
        digit:{message:'Please enter a valid number.'},
        between:[0,100]
      })

    },

    editEnableRSI: ko.observable(false),

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
    modalActiveArrayName: ko.observable(''),
    modalActiveArray: ko.observableArray([]),
    modalActiveContentNumber: ko.observable(0),
    overlay: $('<div>').addClass('grayout'),

    initModal: function() {
      this.modalActiveArrayName.subscribe(function(data){
        this.modalActiveArray( this.modalContent[data] );
      }, this);
    },
    contentArray: function() {
      var len = this.modalActiveArray().length;
      var arr = [];
      for(var i=0; i<len; i++) {
        arr.push(i);
      }
      return arr;
    },
    modalNext: function(){
      var len = this.modalActiveArray().length;
      var current = this.modalActiveContentNumber();
      if( current == len-1 ) return;
      this.modalActiveContentNumber(++current);
    },
    modalPrev: function() {
      var current = this.modalActiveContentNumber();
      if(current == 0) return;
      this.modalActiveContentNumber(--current);
    },

    viewModal: function(name){
      
      $('body').append(this.overlay);
      this.modalActiveArrayName(name);
      this.modalVisible(true);

    },
    destroyModal: function() {
      this.modalVisible(false);
      this.modalActiveContentNumber(0);
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
      this.initModal();
      this.activeTab.subscribe(function(data){
        if(data == 'financials' && this.financials_chart == null) {
          this.financials_chart = new HS_Chart('#financials-chart-container');
        }
      }, this);

      //this.trackPage("SGX Company Financials - " + this.companyInfo.companyName);
      
      var endpoint = this.fqdn + "/sgx/company/priceHistory";
      var postType = 'GET';
      var params = { id: this.ticker };

      UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { self.initIndicatorsChart(data)  }, undefined, undefined);

      this.editEnableRSI.subscribe(function(data) {
        if(data)
          this.addSeriesRSI();
        else {
          this.removeSeriesRSI();
          this.rsiInput.overBought(70);  //back to default
          this.rsiInput.overSold(30);
        }
      }, this);

      ko.applyBindings(this, $("body")[0]);

    },

    initIndicatorsChart: function(data) {

      this.indicators_chart = new HS_Chart('#technical-chart-container', this.ticker);
      this.indicators_chart.chartReady.subscribe(function(){
        setTimeout(function(){ PAGE.resizeIframeSimple(); }, 500);
      });
      this.indicators_chart.initData(data);
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

    //for keeping track of total checkbox choices
    control:{
      simpleAvg: ko.observableArray([]),
      convDivg: ko.observableArray([])
    },

    //keep track of individual checkbox
    checkBoxes: {'fifteen': ko.observable(), 'fifty': ko.observable(), 'MACD': ko.observable(),
                'Histogram': ko.observable(), 'SignalLine': ko.observable(), 'RSI': ko.observable()},



    processCheckbox: function(data, event) {
      

      event.target.disabled = true;
      var dataVal = event.target.value.split('.');
      var addSeriesBool = event.target.checked;
      var contextArray = this.control[ dataVal[0] ];
      var contextValue = dataVal[1];

      (this.checkBoxes[ contextValue ] == undefined)? this.checkBoxes[ contextValue ] = ko.observable(addSeriesBool): this.checkBoxes[ contextValue ](addSeriesBool);

      //need secondary axis when going from 0 -> 1, need to remove it when going from >0 to 0 for convDivg
      var initialLength = this.control.convDivg().length;
      (addSeriesBool)? contextArray.push(contextValue): contextArray.remove(contextValue);
      var finalLength = this.control.convDivg().length;

      var axisAction = (initialLength > 0 && finalLength == 0)? false: (initialLength == 0 && finalLength > 0)? true: null;
      this.modifyIndicatorsChart( contextValue, addSeriesBool, event, axisAction );
      return true;

    },


    addSeriesRSI: function() {

      var newSeries = { type: 'line', id:'RSI', name:'RSI', showInLegend:true, algorithm:'RSI', periods:14, color:'#434348' };
      var xData = this.indicators_chart.xData.slice(0);
      var yData = this.indicators_chart.dataRSI.slice(0);

      newSeries.data = Algorithms['RSI'](xData, yData);

      this.indicators_chart.addAxisRSI( this.rsiInput.overBought(), this.rsiInput.overSold());
      this.indicators_chart.addSeries(newSeries, true);
      this.indicators_chart.redraw();
      this.postChartRedraw();

      this.rsiInput.overBought.subscribe(function(data) {

        this.indicators_chart.modifyPlotline(data, 'overBought-plotline');

      }, this);

      this.rsiInput.overSold.subscribe(function(data){

        this.indicators_chart.modifyPlotline(data, 'overSold-plotline');

      }, this);

    },

    removeSeriesRSI: function() {

      this.indicators_chart.removeAxis(true);
      this.indicators_chart.redraw();
      this.postChartRedraw();


    },

    modifyIndicatorsChart: function(value, addSeriesBool, event, axisAction) {

      var self = this;

      var defaultSeriesConfig = {
        //simple Moving Area
        'fifteen': { type: 'line', id: value, name: '15-day SMA', yAxis:0, showInLegend:true, algorithm:'SMA', periods: 15 },
        'fifty': { type: 'line', id: value, name: '50-day SMA', yAxis:0, showInLegend:true, algorithm:'SMA', periods: 40 },
        //Moving avg con40
        'MACD': { type: 'line', id: value, name: 'MACD', showInLegend:true, algorithm:'MACD' },
        'Histogram': { type: 'column', id: value, name: 'Histogram', showInLegend:true, algorithm:'histogram' },
        'SignalLine': { type: 'line', id: value, name: 'Signal line', showInLegend:true, algorithm:'signalLine'  }
      };

      if( axisAction !== null ){
        (axisAction)? this.indicators_chart.addAxisSecondary(): this.indicators_chart.removeAxis();  //add or remove secondary axis as needed.
        if(!axisAction) {
          //this.removeSeriesRSI();  //redudant to fix parts of chart getting cut off on resize.
          //this.addSeriesRSI();
        }
      }


      if(addSeriesBool) {

        var newSeries = defaultSeriesConfig[ value ];
        var xData = this.indicators_chart.xData.slice(0);
        var yData = this.indicators_chart.yData.slice(0);
        var algorithm = newSeries.algorithm;

        var seriesData = Algorithms[ algorithm ]( xData, yData, newSeries.periods );
        newSeries.data = seriesData;
        this.indicators_chart.addSeries(newSeries);

      }
      else if(!addSeriesBool && axisAction != false) {

        this.indicators_chart.removeSeries(value);

      }

      this.indicators_chart.redraw();
      this.postChartRedraw();

      event.target.disabled = false;

    },

    postChartRedraw: function() {
      var scroll;
      var section = $('.technical-charting');
      if(this.indicators_chart.chartElement.yAxis.length ==4 ) {
        section.animate({ 'paddingBottom':20 }, 1000);
      }else {
        section.animate({ 'paddingBottom':120 }, 1000);
      }
    }

  }













return TechnicalCharting;

});