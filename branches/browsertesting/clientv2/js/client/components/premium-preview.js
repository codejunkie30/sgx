define(['knockout', "wmsi/page", 'text!./premium-preview.html', "text!client/data/messages.json"], function(ko, page, htmlString, MESSAGES) {
	
	var messages= JSON.parse(MESSAGES);
	var displayMessage = messages.messages[0];
	
  function ComponentViewModel(params) {
    this.sectionName = params.sectionName;
    this.userStatus = params.userStatus();
	this.price = displayMessage.premium.price;
	this.content = displayMessage.premium.content;
	trackPromoGA(params.sectionName);
  }
  
  function trackPromoGA(secName){
	  var title = "SGX Plus Promo -"+secName;
	  page.trackPage(title);
  }

  return {viewModel: ComponentViewModel, template: htmlString};

});