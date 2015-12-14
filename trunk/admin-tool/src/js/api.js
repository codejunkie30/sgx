

// var baseUrl = 'http://localhost:8000';
// var baseUrl = 'https://sgx-api-us.sharefc.com/sgx';
var baseUrl = "";
var $overlay = $('<div id="customOverlay" class="grayout"><img src="images/ajax-loader.gif" alt="" /></div>');;
var toastr = require('toastr');


var API = {

  paths: {
    login:            baseUrl + '/sgx/login',
    logout:           baseUrl + '/sgx/logout',
    acctInfo:         baseUrl + '/sgx/account/info',
    getTrial:         baseUrl + '/sgx/properties/trialDuration',
    setTrial:         baseUrl + '/sgx/admin/setTrial',
    searchByEmail:    baseUrl + '/sgx/admin/findUser',
    searchByDate:     baseUrl + '/sgx/admin/searchDate',
    deactivate:       baseUrl + '/sgx/admin/deactivate',
    extendExpiration: baseUrl + '/sgx/admin/extension',
    makeAdmin:        baseUrl + '/sgx/admin/setAdmin',
    removeAdmin:      baseUrl + '/sgx/admin/removeAdmin'
  },

  get: function(url, successFN, params) {
    var requestWithFetch;
    if( params === null || params === undefined) {
      requestWithFetch = fetch(url, {credentials:'same-origin'});
    } else {
      var extra='';
      $.each(params, function(key, val) {
        extra+='?';
        extra+= key+'='+encodeURIComponent(val);
      });

      requestWithFetch = fetch(url + extra, {credentials:'same-origin'});
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
    console.log(params);
    fetch( url, {
      method:'post',
      credentials: 'same-origin',
      body: JSON.stringify(params),
      headers: {
        'Content-type':'application/json; charset=utf-8'
      }
    })
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

  verifyUser: function() {
    var self = this;
    this.post( this.paths.acctInfo, successFN, {dummy:'param'});
    this.showLoading();

    function successFN(response) {
      self.hideLoading();
      console.log(response.type);
      if( response.type === 'MASTER' || response.type === 'ADMIN' ) {
        self.hideLoading();
      }else {
        var error = new CustomException(502, 'Unauthorized Access. Redirecting...')
        setTimeout(function(){
          location.href='/';
        },1500);
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
      //console.log('success');
      location.href = '/';
    }
  },

  showLoading: function() {
    $('body').append($overlay);
  },

  hideLoading: function() {
    var overlay = document.getElementById('customOverlay');
    if(overlay) {
      $(overlay).remove();
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
  console.log(jsonResp);
  if(jsonResp == "")
    return {responseCode:0};

  else if( jsonResp.reason == "Full authentication is required to access this resource" ){
    jsonResp.responseCode = 32;
    jsonResp.data = "Full authentication is required to access this resource";
    setTimeout(function() { location.href="/"; }, 1500);
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


