

//var baseUrl = 'https://192.168.1.34:8000';
// var baseUrl = 'https:localhost:3443';
var baseUrl = "";
var $overlay = $('<div id="customOverlay" class="grayout"><img src="images/ajax-loader.gif" alt="" /></div>');;
var toastr = require('toastr');
var tokenHandler = require('./helpers/tokenHandler');

var API = {

  paths: {
    login:            baseUrl + '/sgx/login',
    logout:           baseUrl + '/sgx/logout',
    acctInfo:         baseUrl + '/sgx/admin/info',
    getTrial:         baseUrl + '/sgx/properties/trialDuration',
    setTrial:         baseUrl + '/sgx/admin/setTrial',
    searchByEmail:    baseUrl + '/sgx/admin/findUser',
    searchByDate:     baseUrl + '/sgx/admin/searchDate',
    deactivate:       baseUrl + '/sgx/admin/deactivate',
    extendExpiration: baseUrl + '/sgx/admin/extension',
    makeAdmin:        baseUrl + '/sgx/admin/setAdmin',
    removeAdmin:      baseUrl + '/sgx/admin/removeAdmin',
    searchTranId:     baseUrl + '/sgx/admin/transId'
  },


  getOptions: function(){
    var options = {
      method:'get',
      credentials:'same-origin'
    };
    var adminToken = tokenHandler.retrieveAuthToken();
    if( adminToken !== false ) {
      options.headers = {
        'x-auth-token': adminToken
      }
    }

    return options;
  },

  postOptions: function(params) {
    var options = {
      method:'post',
      credentials: 'same-origin',
      body: JSON.stringify(params),
      headers: {
        'Content-type':'application/json; charset=utf-8'
        }
      }

    var adminToken = tokenHandler.retrieveAuthToken();
    if( adminToken !== false ) {
      options.headers['x-auth-token'] =  adminToken;
    }
    return options;
  },


  get: function(url, successFN, params) {
    var requestWithFetch;
    if( params === null || params === undefined) {
      requestWithFetch = fetch(url, this.getOptions());
    } else {
      var extra='';
      $.each(params, function(key, val) {
        extra+='?';
        extra+= key+'='+encodeURIComponent(val);
      });

      requestWithFetch = fetch(url + extra, this.getOptions());
    }

    requestWithFetch
      .then(checkStatus)
      .then(parseJSON)
      .then(supplementBackend)
      .then(checkResponseCode)
      .then(successFN)
      .catch(function(error) {
        if( !error instanceof CustomException)
          alert('unknown error');
        handleError(error);
      });

  },


  post: function(url, successFN, params) {
    fetch( url, this.postOptions(params))
      .then(checkStatus)
      .then(parseJSON)
      .then(supplementBackend)
      .then(checkResponseCode)
      .then(successFN)
      .catch(function(error) {
        if(!error instanceof CustomException) 
          alert('unknown error');
        handleError(error);
      });
  },


  verifyUser: function(successCb, failureCb) {
    //if token not present go into failureCB immediately
    if( !tokenHandler.retrieveAuthToken() && failureCb) {
      failureCb();
      return;
    }

    var self = this;
    this.post( this.paths.acctInfo, successFN, {dummy:'param'});
    this.showLoading();

    function successFN(response) {
      self.hideLoading();
      if( response.type === 'MASTER' || response.type === 'ADMIN' ) {
        if(successCb && typeof successCb === 'function') {
          successCb(response.type);
        }
      }else {
        var error = new CustomException(502, 'This email address is not registered as an admin');
        if(failureCb && typeof failureCb === 'function') {
          failureCb();
        }
        throw error;
      }
    }
  },


  logout: function() {
    API.showLoading();
    fetch( API.paths.logout, {method:'POST'} )
      .then(function(response) {
        if(response.status !== 200) {
          throw new Error('Error in logout');
        }
      })
      .then(successFN)
      .catch(function(err) {
        console.log(err);
      });

    function successFN(response) {
      tokenHandler.deleteAuthToken();
      API.goToPage('/');
    }
  },


  showLoading: function() {
    // if (document.getElementById('customOverlay')) return;
    $('body').append($overlay);
  },


  hideLoading: function() {
    var overlay = document.getElementById('customOverlay');
    if(overlay) {
      $(overlay).remove();
    }
  },


  goToPage: function(dest, delay) {
    //add pathname for development
    var isDev = (location.href.indexOf('fakemsi') !== -1 || location.hostname === 'localhost') ? true: false;
    var newDest;

    if ( !isDev ){
      if( dest == '/')
        newDest = '/3rdss/';
      else 
        newDest = '/3rdss/'+dest+'.html';
    } else {
      if ( dest == '/')
        newDest = '/';
      else 
        newDest = dest+'.html';
    }

    if( delay ) 
      setTimeout(function() { location.href = newDest; }, delay);
    else {
      location.href = newDest;
    }
  }

}

module.exports = API;


function CustomException(errorCode, message) {
  this.errorCode = errorCode;
  this.message = message;
  this.name = "CustomException";
}

function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response
  } else {
    var error = new CustomException(response.status, response.statusText)
    throw error;
  }
}

function checkResponseCode(jsonResp) {

    if(jsonResp.responseCode != 0) {
      var error = new CustomException(jsonResp.responseCode, jsonResp.data);
      throw error;
    }else {
      return jsonResp;
    }
}

function parseJSON(response) {
  return response.clone().json().catch(function() {
      return response.text();
    });
}

function supplementBackend(jsonResp) {
  if(typeof jsonResp == 'string'){
    return {responseCode:0, data: jsonResp};
  }
  else if( jsonResp.reason == "Invalid Token" ){
	  API.logout();
  }

  else if( jsonResp.reason == "Full authentication is required to access this resource" ){
    jsonResp.responseCode = 32;
    jsonResp.data = "Full authentication is required to access this resource";
    API.goToPage('/', 1500);
  }

  else if ( jsonResp.reason == 'Authentication token not Valid') {
    jsonResp.responseCode = 35;
    jsonResp.data = "Authentication token not Valid.";
    API.goToPage('/', 2000);
  }

  else if( jsonResp.reason == "Invalid username or password" ){
    jsonResp.responseCode = 31;
    jsonResp.data = "Invalid username or password";
  }

  else if( jsonResp.trialDays || jsonResp.email) {
    jsonResp.responseCode = 0;
  }
  
  return jsonResp;

}


function handleError(error) {
  toastr.error(error.message);
  API.hideLoading();
}


