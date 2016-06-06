
var ko = require('knockout');
var moment = require('moment');
/*Dev stuff for mockup*/
// var Firebase = require('firebase');
// var ref = new Firebase('https://gaotestsgx.firebaseio.com/');
// console.log((new Date()).getTime());
// console.log(moment().utc().valueOf());

// console.log(moment().month(1).format("YYYY-MM-DD"));
// console.log(moment().startOf('month').format('MM-DD-YYYY'));
// console.log(moment().startOf('month').valueOf());


function superAdminVM(params) {

  this.showSuperAdmin = params.data;
  //cleanup
  this.showSuperAdmin.subscribe(function(data){
    if( !data ) {
      this.account.email('');
      this.account.password('');
      this.accountErrors.showAllMessages(false);
    }
  }, this);

  this.account = {};
  this.account.email = ko.observable().extend({  required:true, email:true });
  this.account.password = ko.observable().extend({ required: true });

  this.accountErrors = ko.validation.group(this.account);


  this.createNewAdmin = function() {
    // console.log(this.accountErrors());
    // this.accountErrors.showAllMessages();
    var usersRef = ref.child("users");
    usersRef.set({
      Pravin: {
        date_of_birth: "December 20, 1996",
        full_name: "Pravin Rai"
      }
    });
    // usersRef.once('value', function(data) {
    //   console.log(data.val());
    // });
  }

}

module.exports = superAdminVM;