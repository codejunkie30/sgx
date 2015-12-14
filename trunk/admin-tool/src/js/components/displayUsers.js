
var ko = require('knockout');

require('../bindingHandlers/dateFormat');    //modal


function DisplayUsers(params) {
	
	this.users = ko.observableArray([]);
	this.currentPage = ko.observable(1);
	this.showModal = ko.observable(false);

	//replica of existing user model, that'll eventually be replaced sent to modal
	this.currentUserData = ko.observable({  
		created_date:'',
		expiration_date: '',
		status: function(){},
		username:''
	});
	this.currentUserIndex = ko.observable();

	this.modalData = {
		showModal: this.showModal,
		userData: this.currentUserData,
		currentUserIndex: this.currentUserIndex
	}

	this.test = function() {
		var curIndex = this.modalData.currentUserIndex();
		console.log()
		console.log(curIndex);
		console.log(this.users()[curIndex]);
	}

	var perPage = 10;

	this.paginationArray = ko.observableArray([]);
	this.setCurrentPage = function(data) {
		if(data == this.currentPage())
			return;
		this.currentPage(data);
	}

	this.showUserModal = function(data, index) {
		this.currentUserIndex( ko.unwrap(index) + perPage*(this.currentPage()-1) );
		this.currentUserData(data);
		this.showModal(true);
	}

	this.getPageDisplay = function(recordIndex, currentPage) {

		var displayIndex = ko.unwrap(recordIndex) + 1;
		var adjustmentForPage = (currentPage - 1)*perPage;
		var num = displayIndex + adjustmentForPage;
		return num;
	}


	this.userDisplayCtrl = ko.computed(function(){
		var start = this.currentPage() - 1;
		var records = params.data();
		var begin = perPage*start;
		var end = begin + perPage;
		var filteredRecords = records.slice(begin, end);

		if(filteredRecords.length > 0) {
			var totalPages = Math.ceil(records.length/perPage);
			this.paginationArray( ko.utils.range(1, totalPages) );
			this.users(filteredRecords);
		}
	}, this);

}



module.exports = DisplayUsers;