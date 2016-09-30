
var ko = require('knockout');
var pikaday = require('pikaday');

var moment = require('moment');

ko.bindingHandlers.pikaday = {

  init: function(element, valueAccessor) {
    var self = this;
    var picker;
    var value = valueAccessor();

    picker = new pikaday({
        field: element,
        format: 'MM-DD-YYYY',
        onSelect: function(date) {
          value(this.getMoment().format('MM-DD-YYYY'));
        },
        onOpen: function() {
          if(value() != null)
          this.setDate( moment(value(),'MM-DD-YYYY').toDate());
        }
    });

    picker.setDate(value());

    ko.utils.domNodeDisposal.addDisposeCallback(element, function(){
      picker.destroy();
  });

  },

  update: function(element, valueAccessor) {
    var value = valueAccessor();
  }

}