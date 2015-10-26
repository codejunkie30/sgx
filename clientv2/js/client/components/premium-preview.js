define(['knockout', 'text!./premium-preview.html'], function(ko, htmlString) {

  function ComponentViewModel(params) {
    this.sectionName = params.sectionName;
  }

  return {viewModel: ComponentViewModel, template: htmlString };

});