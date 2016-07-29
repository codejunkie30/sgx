
var moment = require('moment');

var store = {

  set: function(key, value) {
    var jsonVal = JSON.stringify(value);
    sessionStorage.setItem(key, jsonVal);
  },

  get: function(key) {
    var data = sessionStorage.getItem(key);
    if( data === null || data === "null") {
      return false;
    }

    return (JSON.parse(data));
  },

  remove: function(key) {
    sessionStorage.setItem(key, 'null');
  }

};

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