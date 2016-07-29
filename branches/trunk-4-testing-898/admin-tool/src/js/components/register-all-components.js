
var ko = require('knockout');


/*
 * This looks a little weired because we're referencing the files in the same folder with ../
 * this is because this file is required in pages/users, at which point it actually executes the registrations. 
 * The file references are relative to that file 
*/
ko.components.register('modal-component', {
  viewModel: require('../components/screenerModal'),
  template: require('../components/screenerModalTmpl.html')
});

ko.components.register('super-admin', {
  viewModel: require('../components/superAdmin'),
  template: require('../components/superAdminTmpl.html')
});

ko.components.register('common-header', {
  viewModel: require('../components/header'),
  template: require('../components/headerTmpl.html')
});

ko.components.register('display-users', {
  viewModel: require('../components/displayUsers'),
  template: require('../components/displayUsersTmpl.html')
});