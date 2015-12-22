
var store = require('store');
var moment = require('moment');


module.exports = {

  saveAuthToken: function(token) {
    if(!token) return;
    store.set('adminToken', {value: token, expiration: moment().add(1,'hours').valueOf()});
  },

  retrieveAuthToken: function() {
    var token = store.get('adminToken');
    if(token && moment().valueOf() < token.expiration ) {
        return token.value;
    }else {
        return false;
    }
  },

  deleteAuthToken: function() {
      store.remove('adminToken');  
  }

}