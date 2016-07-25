

var ko = require('knockout');
var moment = require('moment');

ko.bindingHandlers.dateFormat = {
	update: function (element, valueAccessor) {
		var value = ko.unwrap( valueAccessor() );

		var newValueAccessor = function() {
			if( !value ) return '-';
      if(/^[0-9]{2}\-[0-9]{2}\-[0-9]{4}$/.test(value)) { //test if already formatted
        return value;
      }
			return moment(value).format('MM-DD-YYYY');
		};

		ko.bindingHandlers.text.update(element, newValueAccessor);

	}
}