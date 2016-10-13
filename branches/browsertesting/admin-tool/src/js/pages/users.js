"use strict";

var ko = require('knockout');
ko.validation = require('knockout.validation');
var API = require('../api');
var toastr = require('toastr');
var moment = require('moment');

require('bootstrap');

//Custom Handler
require('../bindingHandlers/pikadayHandler');  //datepicker
require('../bindingHandlers/modalHandler');    //modal

//GLOBALLY registers all components 
require('../components/register-all-components');

//var data = require('../data');

function ScreenerPage() {
	var self = this;
	this.page = 'users';
	this.adminType = ko.observable();
	this.init = function() {
		API.verifyUser(
			function( ADMIN_TYPE ){ 							//success callback
				self.adminType( ADMIN_TYPE );
			}, 
			function(){  													//failure callback
				API.goToPage('/', 1500);
			}
		);
	}
	this.userEmail = ko.observable().extend({ required: true, email:true });
	this.dateCreated = ko.observable().extend({required: true });
	this.searchResults = ko.observableArray([]);
	
	this.showModal = ko.observable(false);
	this.showSuperAdmin = ko.observable(false);

	//modal togglers
	this.toggleModal = function() { this.showModal(true); };
	this.toggleSuperAdmin = function() { this.showSuperAdmin(true); };

	//test 
	this.test = function() {
		console.log(this.searchResults());
	}

	//email search

	this.searchByEmailTrigger = function() {
		if (this.emailInputError().length > 0 ){
			this.emailInputError.showAllMessages();
			return;
		}
		
		var email = this.userEmail();

		this.searchByEmailAction(email);
		
	};

	this.searchByEmailAction = function(email) {
		API.showLoading();
		API.post( API.paths.searchByEmail , successFN.bind(this), {id: email});

		function successFN(response) {
			API.hideLoading();
			var result = _transformData([response.data]);
			this.searchResults( result );
		}
	}

	// transaction id Search

  this.transactionId = ko.observable().extend({required:true});

  this.searchByTransactionIdTrigger = function() {
    if(this.transactionIdError().length > 0) {
      this.transactionIdError.showAllMessages();
      return;
    }

    var id = this.transactionId();
    this.searchByTransactionIdAction(id);
  };

  this.searchByTransactionIdAction = function(id) {
    API.showLoading();
    API.post( API.paths.searchTranId, successFN.bind(this), {transId: id} );

    function successFN(response) {
      API.hideLoading();
      var result = _transformData([response.data]);
      this.searchResults( result );
    } 

  };

  // date search

	this.searchByDateTrigger = function() {
		if(this.dateInputError().length > 0) {
			this.dateInputError.showAllMessages();
			return;
		}

		var searchDate;
		
		switch(this.dateCreated()) {
			case 'week':
				searchDate = moment().startOf('week');
				break;

			case 'month':
				searchDate = moment().startOf('month');
				break;

			case 'year':
				searchDate = moment().startOf('year');
				break;

			default: 
				searchDate = moment('2014', 'YYYY').valueOf();

		}

		this.searchByDateAction( searchDate.valueOf() );

	};

	this.searchByDateAction = function(searchDate) {
		API.showLoading();
		API.post( API.paths.searchByDate, successFN.bind(this), {dateParam: searchDate });

		function successFN(response) {
			API.hideLoading();
			if( response.data.length == 0) {
				toastr.info('No results found for that time period.');
				$("#searchResultsDiv").css("display","none");
			}
			else {
				$("#searchResultsDiv").css("display","block");
				var transformedData = _transformData(response.data);
				this.searchResults(transformedData);
			}
		}

	}


	this.emailInputError = ko.validation.group(this.userEmail);
	this.dateInputError = ko.validation.group(this.dateCreated);
	this.transactionIdError = ko.validation.group(this.transactionId);

	//information passed to the user modal
	this.userInfo = {
		emailAddress: 'casper@gmail.com',
		showModal: this.showModal
	}


	this.logout = function() {
		API.logout();

	},

	this.goToPage = API.goToPage

}


function _transformData(resultArray) {

	var transformedData = resultArray.map(function(item){
			if (item.status.toUpperCase() == 'PREMIUM') {
				if (item.transId === undefined || item.transId === null) {
					item.transId = 'N/A';
				} 
			}
			item.expiration_date = ko.observable(item.expiration_date);
			item.status = ko.observable(item.status.toUpperCase());
			return item;
		})
		.sort(function(a, b){
			return b.created_date > a.created_date ? 1: -1;
		});
	return transformedData;
}

module.exports = new ScreenerPage();