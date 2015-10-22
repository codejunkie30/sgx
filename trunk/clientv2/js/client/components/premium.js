define(['knockout', 'text!./premium.html'], function(ko, htmlString) {

  function ComponentViewModel(params) { }

  return {viewModel: ComponentViewModel, template: htmlString };

});