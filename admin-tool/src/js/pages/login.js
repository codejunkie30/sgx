"use strict";

var ko = require('knockout');
ko.validation = require('knockout.validation');
var toastr = require('toastr');
var API = require('../api');

ko.validation.init({ insertMessages: false });

function LoginPage() {
  this.page = 'login';
  this.datetest = ko.observable().extend({ date: true }),
  this.account = {
    email: ko.observable().extend({ required:true, email:true }),
    password: ko.observable().extend({ required:true })
  }

  this.loginTrigger = function() {
    
    if( this.acctErrors.length > 0 ) {
      this.acctErrors.showAllMessages();
    }
  
    var params = { username: this.account.email(), password: this.account.password() };
    this.loginAction(params);
  }


  this.loginAction = function(params) {

    API.showLoading();
    API.post( API.paths.login, successFN.bind(this), params );
    function successFN(data) {
      API.verifyUser(function() {
        API.goToPage('users');
      });
    }

  }

  this.test = function() {
    console.log(LoginPage.acctErrors());
    console.log(this.datetest());
  }

  this.acctErrors = ko.validation.group(this.account);

}



module.exports = new LoginPage();