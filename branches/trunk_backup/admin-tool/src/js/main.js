
require('../sass/main.scss');

var ko = require('knockout');

var UsersPage = require('./pages/users');
var LoginPage = require('./pages/login');
var GeneralPage = require('./pages/general');


/* existing pages: corresponding viewModel (javascript page) */
/* key corresponds to container id in html page */
var pages = {
  'login': LoginPage,
  'users': UsersPage,
  'general': GeneralPage
}

var pagesArray =  Object.keys(pages);



for( var i = 0, len = pagesArray.length; i < len; i++) {

  var container = document.getElementById(pagesArray[i])
  if(container) {
    console.log('starting page '+pagesArray[i]);
    var VM = pages[ pagesArray[i] ];
    ko.applyBindings(VM, container);
    if (VM.init && typeof VM.init === 'function') {
      VM.init();
    }
    break;
  }

}



