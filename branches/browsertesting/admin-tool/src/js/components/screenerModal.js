"use strict";

var $ = require('jquery');
var ko = require('knockout');
var toastr = require('toastr');
var API = require('../api');
var moment = require('moment');


ko.validation.rules['mustBeGreaterDate'] = {
  validator: function(val) {
    var today = moment().startOf('day').subtract(1, 'minutes');
    var newDay = moment(val, 'MM-DD-YYYY');
    return newDay.isAfter(today);
  },  
  message: 'Expiration date must be greater than current date'
};

ko.validation.registerExtenders();

function modalVM( params ) {

	$.extend(this, params.data);

  this.adminType = params.adminType();

  this.accountExpiration = ko.observable().extend({mustBeGreaterDate: true});

  this.dateError = ko.validation.group(this.accountExpiration);

  this.displayText = ko.observable('');
  this.accountType = ko.observable('');
  this.test = function(){
    console.log(this.accountExpiration());
  }

  this.userData.subscribe(function(data) {

    var expirationDate;
    if(data.username == '') return;

    //set expiration date on modal
    if( data.expiration_date !== null ) {
      expirationDate = data.expiration_date();
      this.accountExpiration(expirationDate);
    }else {
      this.accountExpiration(null);
    }

    //set display text on modal
    var intro = this.getDisplayText( data );
    this.displayText(intro);
    this.dateError.showAllMessages(false);

  }, this)


  this.deactivateAcount = function() {
    
    var user = this.userData().username;
    var params = { id: user }
    API.showLoading();
    API.post( API.paths.deactivate, successFN.bind(this), params );

    function successFN(response) {
      var userObj = response.data;
      toastr.success('Your changes have been saved.');
      this.refreshUserData( userObj );
      API.hideLoading();
      this.showModal(false);
    }
  }


  this.turnToAdmin = function() {
    var user = this.userData().username;
    var params = {id: user};

    API.showLoading();
    API.post( API.paths.makeAdmin, successFN.bind(this), params );

    function successFN(response) {
      var userObj = response.data;
      API.hideLoading();
      toastr.success('Account successfully made admin.');
      this.refreshUserData( userObj );
      this.showModal(false);
    }

  }

  this.removeAdmin = function() {
    var user = this.userData().username;
    var params = {id: user};

    API.showLoading();
    API.post( API.paths.removeAdmin, successFN.bind(this), params );
    
    function successFN(response) {
      var userObj = response.data;
      API.hideLoading();
      toastr.success('Admin rights successfully removed.');
      this.refreshUserData( userObj );
      this.showModal(false);
    }

  }

  this.saveChanges = function() {

    var user = this.userData().username;
    var newExpirationDate = moment( this.accountExpiration(), 'MM-DD-YYYY').valueOf();
    var today = moment().startOf('day').valueOf();

    if(newExpirationDate === today) { //if expiration date same as today. Expire the account immediately.
      this.deactivateAcount();
      return;
    }
    
    var params = { id: user, dateParam: newExpirationDate };

    API.showLoading();
    API.post( API.paths.extendExpiration , successFN.bind(this), params );

    function successFN(response) {
      var userObj = response.data;
      this.refreshUserData( userObj );
      this.showModal(false);
      API.hideLoading();
      toastr.success('Your changes have been saved.');
    }

  }

  this.refreshUserData = function( userObj ) {
    this.userData().expiration_date( userObj.expiration_date );
    this.userData().status( userObj.status.toUpperCase() );
  }

  this.getDisplayText = function(user){

    switch(ko.unwrap(user.status)) {
      case 'TRIAL':
    	var a = moment(user.expiration_date(),'MM-DD-YYYY');
        var b = moment().startOf('day');
        var daysLeft = a.diff(b, 'days');
        this.accountType('Trial');
        return 'This account has '+daysLeft+' days left in trial.';
      case 'PREMIUM':
        this.accountType('Premium');
        return 'This account is active and expires on '+moment(user.expiration_date()).format('MM/DD/YYYY');
      case 'EXPIRED':
        this.accountType('Expired');
        return 'This account has expired.';
      case 'ADMIN':
        return 'This is an admin account.';
      case 'MASTER':
        return 'This is a master administrator account.';
      default:
        return 'Check again';
    }
  }

}

module.exports = modalVM;
