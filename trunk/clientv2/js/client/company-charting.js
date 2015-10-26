define([ "wmsi/utils", "knockout", "knockout-validate", "client/modules/tearsheet", "client/modules/technical-chart/technical-chart-algorithms", "client/modules/technical-chart/technical-chart", "text!client/data/technicalCharts.json", "highstock" ], function(UTIL, ko, Validation, TS, Algorithms, Chart, Static_Content) {
  //here
  ko.validation = Validation;
  ko.validation.init({insertMessages:false});

  ko.components.register('premium-preview', { require: 'client/components/premium-preview'});

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


  var modalContent = JSON.parse(Static_Content).modalContent;
  var financialsDropdowns = JSON.parse(Static_Content).financialsDropdowns;


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

    sectionName: 'Technical Charts',
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

    financialsDropdowns: {

      selectedChoices: {
        income: ko.observable(),
        balanceSheets: ko.observable(),
        cashFlow: ko.observable(),
        ratios: ko.observable(),
        growth: ko.observable()
      },

      income: financialsDropdowns.income,

      balanceSheets: financialsDropdowns.balanceSheets,

      cashFlow: financialsDropdowns.cashFlow,

      ratios: financialsDropdowns.ratios,

      growth: financialsDropdowns.growth
    },

    init_nonPremium: function() {

      $('.technical-charting').remove();
      $('.technical-charting-alternative').show();
      ko.applyBindings(this, $("body")[0]);
      //setTimeout(function(){ PAGE.resizeIframeSimple(); }, 100);
    },

    init_premium: function() {

      var self = this;
      this.initModal();
      this.activeTab.subscribe(function(data){
        if(data == 'financials' && this.financials_chart == null) {
          this.financials_chart = new Chart.FS_Chart('#financials-chart-container');
          this.financialsDropdowns.selectedChoices.income({displayName: 'Net Income', serviceName: 'income', key: 'netIncome', axisName:'Income'});
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

      this.financialsDataCache = {};
      this.financialsPayload = ko.computed({
        read: function() {
          var selectedObj = ko.toJS(this.financialsDropdowns.selectedChoices);
          $.each(selectedObj, function(key, val) {
            if (val) {
              if( self.financialsDataCache[key] ) {
                var cachedData = self.financialsDataCache[key];
                self.financialsHandler( cachedData, val, self);
              }else {
                self.makeDataCall(val, self.financialsHandler);
              } 
              
            }
          });

        },
        owner:this
      }),

      ko.applyBindings(this, $("body")[0]);

    },

    financialsDataCache: {},
    getFinancialsColor: function(key){
      var colors = { 'income':'#565a5c', 
                      'balanceSheets': '#1e2171', 
                      'cashFlow': '#BED600', 
                      'ratios': '#0094B3', 
                      'growth': '#BF0052' 
                    };
      return colors[key];
    },



    financialsHandler: function(data, serviceObj, self) {

      var self = self;
      var serviceName = serviceObj.serviceName;
      var serviceKey = serviceObj.key;
      if( !self.financialsDataCache[ serviceName ] ) {
        self.financialsDataCache[ serviceName ] = data;
      }

      var seriesData = [];
      var arrayCategories = [];
      $.each(data, function(key, val) {

        for (var i=0, len=val.length; i < len; i++) {
          seriesData.push(val[i][serviceKey]);
          arrayCategories.push(val[i]['absPeriod']);
        }
      });

      if (self.financials_chart.chartElement == null) {
        self.financials_chart.initChart({data:seriesData,
                                          color:self.getFinancialsColor(serviceName),
                                          id: serviceName+'-series',
                                          name: serviceObj.displayName});

        self.financials_chart.chartReady.subscribe(function(data) {
          self.financials_chart.chartElement.xAxis[0].setCategories(arrayCategories);
          PAGE.resizeIframeSimple()
        });

      } else {

        var needNewAxis = true;

        if( self.financials_chart.chartElement.get( serviceName+'-series')) {
          var prevSeries = self.financials_chart.chartElement.get( serviceName+'-series');
          if(prevSeries) prevSeries.remove();
          needNewAxis = false;
        }

        if(needNewAxis) {
          self.financials_chart.chartElement
                      .addAxis({id: serviceName,
                                name: serviceObj.axisName,
                                title: {text:serviceObj.axisName},
                                opposite:true});
        }

        self.financials_chart.chartElement
                      .addSeries({data: seriesData,
                                  color:self.getFinancialsColor(serviceName),
                                  id:serviceName+'-series',
                                  name: serviceObj.displayName,
                                  yAxis:serviceName});
      }

    },

    makeDataCall: function(serviceObj, callback) {
      
      var self = this;
      if(!serviceObj || !callback) {
        return;
      }

      var endpoint = this.fqdn + '/sgx/company/techCharts/'+serviceObj.serviceName;
      var postType = 'GET';
      var params = { id: self.ticker };

      UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { callback(data, serviceObj, self);  }, undefined, undefined);

    },

    initIndicatorsChart: function(data) {

      this.indicators_chart = new Chart.HS_Chart('#technical-chart-container', this.ticker);
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

      //   if(data == true)
      //     self.init_premium();
      //   else 
      //     self.init_nonPremium();
        
      // });

      var waitForDataToInit = ko.computed({
          read:function(){
              var userData = this.premiumUser();
              var companyData = this.gotCompanyData();

              if(userData && companyData) {
                  self.init_premium();
              }else if(userData == false && companyData == true) {
                  self.init_nonPremium();
              }
          },
          owner:this
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