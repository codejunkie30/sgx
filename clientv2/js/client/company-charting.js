define([ "wmsi/utils", "knockout", "knockout-validate", "client/modules/tearsheet", "client/modules/technical-chart/technical-chart-algorithms", "client/modules/technical-chart/technical-chart", "text!client/data/technicalCharts.json", "highstock" ], function(UTIL, ko, Validation, TS, Algorithms, Chart, Static_Content) {
  //here

  initializeCustomParts(); //custom bindings/ validation etc


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

      income: financialsDropdowns.income,

      balanceSheets: financialsDropdowns.balanceSheets,

      cashFlow: financialsDropdowns.cashFlow,

      ratios: financialsDropdowns.ratios,

      growth: financialsDropdowns.growth
    },

    dropdownChoices: ko.observableArray(),

    aggregateDropdownChoices:{},

    choiceNumTracker: {
      income: ko.observable(0),
      balanceSheets: ko.observable(0),
      cashFlow: ko.observable(0),
      ratios: ko.observable(0),
      growth: ko.observable(0)
    },

    trackChoiceNum: function(key, action) {
      var serviceName = this.aggregateDropdownChoices[ key ].serviceName
      var currentDropdown = this.choiceNumTracker[ serviceName ];
      var choiceNum = currentDropdown();
      if( action == 'add' ) {
        choiceNum++;
      } else {
        choiceNum--;
      }
      currentDropdown( choiceNum ) ;
    },

    flattenDropdownsObject: function() {
      var self = this;
      $.each(financialsDropdowns, function(key, val) {
        $.each(val, function(idx, obj) {
          self.aggregateDropdownChoices[obj.key] = obj;
        });
      });
    },

    //This takes caare of all the logic for dropdown interactions
    //All interaction has dropdownChoice observable array as an entry point
    //additions subtraction etc, to the array fire up related events
    setFinancialsDropdowns: function() {

      var self = this;
      self.dropdownChoices.subscribe(function(data) {

        var localScope = this; //this really is local scope for this function
        localScope.oldData = this.oldData || []; //save old data in this function's scope
        localScope.bypassNextNotification = localScope.bypassNextNotification || false; //need to bypass the rest of the code sometimes

        if (localScope.bypassNextNotification) {
          localScope.bypassNextNotification = false;
          return;
        }

        var diff, action;
        if(data.length > 5) {
          self.modal.open({ type: 'alert',  content: '<h4>Chart Financials <span>(Select up to 5)</h4><p>Only five data points can be charted at a time. Remove a data point before selecting a new one.</p>' });
          localScope.bypassNextNotification = true;
          self.dropdownChoices( data.slice(0,5));
          return;
        }
        if( data.length == 0) {
          localScope.bypassNextNotification = true;
          self.dropdownChoices.push(localScope.oldData[0]);
          return;
        }


        if(localScope.oldData.length < data.length) { //add series Call
          action = 'add';
          diff = ko.utils.arrayFilter( data, function(item) {
            return localScope.oldData.indexOf(item) < 0;
          });
        }else { //subtract series Call
          action = 'subtract';
          diff = localScope.oldData.filter(function(item) {
            return data.indexOf(item) < 0;
          });
        }

        self.trackChoiceNum( diff[0], action);

        self.makeDataCall(diff[0], action);

        this.oldData = data.slice(0);
      });

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
        if(data == 'financials') {
          //this.financials_chart = new Chart.FS_Chart('#financials-chart-container');
          $('.technical-charting').css('height', '700px');
          setTimeout(function(){ PAGE.resizeIframeSimple(); }, 100);
        }else {
          $('.technical-charting').css('height', '920px');
          setTimeout(function(){ PAGE.resizeIframeSimple(); }, 100);
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

      this.financials_chart = new Chart.FS_Chart('#financials-chart-container');
      this.flattenDropdownsObject();
      this.setFinancialsDropdowns();
      this.dropdownChoices.push('ebitda');

      ko.applyBindings(this, $("body")[0]);

    },

    financialsDataCache: {},

    makeDataCall: function(dropDownKey, action) {

      var self = this;
      var serviceObj = this.aggregateDropdownChoices[ dropDownKey ];
      //if data exists in cache, simply use it
      var serviceName = serviceObj.serviceName;
      if (this.financialsDataCache[ serviceName ]) {
        this.financialsHandler(this.financialsDataCache[ serviceName ], serviceObj, action);
        return;
      }


      var endpoint = this.fqdn + '/sgx/company/techCharts/'+serviceObj.serviceName;
      //var endpoint = 'http://192.168.1.37:8001/techCharts/'+serviceObj.serviceName;
      var postType = 'GET';
      var params = { id: self.ticker };
      UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { self.financialsHandler(data, serviceObj, action, self);  }, undefined, undefined);

    },




    financialsHandler: function(data, serviceObj, action, me) {

      var self = me || this;
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

      var seriesColor = getSeriesColor( serviceKey );
      serviceObj.color = seriesColor;

      if (self.financials_chart.chartElement == null) {

            self.financials_chart.initChart({data:seriesData,
                                              id: serviceKey,
                                              color: seriesColor,
                                              name: serviceObj.displayName});

            self.financials_chart.chartReady.subscribe(function(data) {
              self.financials_chart.chartElement.xAxis[0].setCategories(arrayCategories);
            });

      } else {

        if (action == 'add') {
          self.financials_chart.addSeries(seriesData, serviceObj);
          self.financials_chart.redraw();

        } else {

          self.financials_chart.removeSeries(serviceObj);
          self.financials_chart.redraw();

        }

      }


    },


    makeDataCall: function(dropDownKey, action) {

      var self = this;
      var serviceObj = this.aggregateDropdownChoices[ dropDownKey ];
      //if data exists in cache, simply use it
      var serviceName = serviceObj.serviceName;
      if (this.financialsDataCache[ serviceName ]) {
        this.financialsHandler(this.financialsDataCache[ serviceName ], serviceObj, action);
        return;
      }

      var endpoint = this.fqdn + '/sgx/company/techCharts/'+serviceObj.serviceName;
      //var endpoint = 'http://192.168.1.37:8001/techCharts/'+serviceObj.serviceName;
      var postType = 'GET';
      var params = { id: self.ticker };

      UTIL.handleAjaxRequest(endpoint, postType, params, undefined, function(data) { self.financialsHandler(data, serviceObj, action, self);  }, undefined, undefined);

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
        
      var waitForDataToInit = ko.computed({
          read:function(){

              var companyData = this.gotCompanyData();
              var userStatus = this.userStatus();

              if( companyData && userStatus ) {

                  if ( userStatus == 'UNAUTHORIZED' || userStatus == 'EXPIRED' ) {
                    this.init_nonPremium();
                    //this.init_premium();
                  }else {
                    this.init_premium();
                  }
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

      var newSeries = { type: 'line', id:'RSI', name:'RSI', showInLegend:true, algorithm:'RSI', periods:14, color:'#333333' };
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
        'fifteen': { type: 'line', id: value, name: '15-day SMA', yAxis:0, showInLegend:true, algorithm:'SMA', periods: 15, color:'#5f6062' },
        'fifty': { type: 'line', id: value, name: '50-day SMA', yAxis:0, showInLegend:true, algorithm:'SMA', periods: 40, color:'#ffcc00' },
        //Moving avg con40
        'MACD': { type: 'line', id: value, name: 'MACD', showInLegend:true, algorithm:'MACD', color:'#0094b3' },
        'Histogram': { type: 'column', id: value, name: 'Histogram', showInLegend:true, algorithm:'histogram', color:'#bdd831' },
        'SignalLine': { type: 'line', id: value, name: 'Signal line', showInLegend:true, algorithm:'signalLine', color:'#791e75' }
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
      return;
      var scroll;
      var section = $('.technical-charting');
      if(this.indicators_chart.chartElement.yAxis.length ==4 ) {
        section.animate({ 'paddingBottom':20 }, 1000);
        //$('.indicators-description').animate({'margin-top', 130});
      }else {
        section.animate({ 'paddingBottom':120 }, 1000);
      }
    }

  }





return TechnicalCharting;










  function initializeCustomParts() {

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

    ko.bindingHandlers.classToggler = {
      update:function(element, valueAccessor, allBindings) {
        var timer;
        var className = valueAccessor();
        $(element).on('click', function(event){

          if(event.target == this) {

            $(this).toggleClass(className);
          }

          event.stopPropagation();
        })
        .on('mouseenter', function(){
          if(timer)
            clearTimeout(timer);
        })
        .on('mouseleave', function(){
          var self = this;
          timer = setTimeout(function(){
             $(self).removeClass(className);
          }, 500)
        });
      }
    }

  }

    //helper for financials series color
    function getSeriesColor( key ){
      this.colors = this.colors || ['#0b236b', '#bdd831', '#0094b3', '#791e75', '#5f6062'];
      this.memo = this.memo || {};

      if ( this.memo[ key ] ) {
        this.colors.push(this.memo[ key ]);
        delete this.memo[ key ];
        return;
      }
      var color = this.colors.shift();
      this.memo[key] = color;
      return color;

    }

});