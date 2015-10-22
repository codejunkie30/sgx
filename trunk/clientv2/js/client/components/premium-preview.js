define(['knockout', 'text!./premium-preview.html'], function(ko, htmlString) {

  function ComponentViewModel(params) { }

  return {viewModel: ComponentViewModel, template: htmlString };

});