define(['knockout', 'text!./premium-preview.html'], function(ko, htmlString) {

  function ComponentViewModel(params) {
    this.sectionName = params.sectionName;
    this.userStatus = params.userStatus();
  }

  return {viewModel: ComponentViewModel, template: htmlString };

});