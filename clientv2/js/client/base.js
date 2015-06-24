define(["jquery", "wmsi/page", "wmsi/utils", "knockout",  "text!client/data/glossary.json", "text!client/templates/tooltip.html", "knockout-amd-helpers", "text", "jquery-ui", "colorbox"], function($, PAGEIMPL, UTIL, KO, GLOSSARY, TOOLTIP) {
	
	/** change the default template path */
	KO.amdTemplateEngine.defaultPath = "client/templates";
	
	KO.bindingHandlers.precision = {
		update: function(element, valueAccessor, allBindings) {
	        return ko.bindingHandlers.text.update(element,function(){
	        	var value = parseInt(allBindings().text);
	            var decimals = +(ko.unwrap(valueAccessor()) || 0);
	            return decimals == 0 ? value||0 : value.toFixed(decimals);
	        });
				
		}
	};

	KO.bindingHandlers.prepend = {
			update: function(element, valueAccessor, allBindings) {
		        return ko.bindingHandlers.text.update(element,function(){
		        	var value = parseInt(allBindings().text);
		            var prep = ko.unwrap(valueAccessor());
		            return typeof prep !== "undefined" ? prep + value : value;
		        });
			}
	};
	
	KO.bindingHandlers.accordian = {
	    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
            $(element).accordion({
                active: 0,
                animated: 'easeOutExpo',
                autoHeight: false,
                collapsible: true,
                event: 'click',
            });
	    }
	};
	
	KO.bindingHandlers.tabs = {
		init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			$(element).tabs({
	            active: 0,
	            show: {
	                effect: "blind",
	                duration: 800
	            }
			});
		}
	};
	
	KO.bindingHandlers.slider = {
		init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			
			$(element).slider({
		        range: true,
		        min: 0,
		        max: viewModel.field.buckets.length - 1,
		        values: [ 0, viewModel.field.buckets.length - 1 ],
		        slide: function(event, ui) {
		        	var template = $(event.target).closest(".criteria");
		        	var vm = $(template).data();
		        	viewModel.min(vm.buckets[ui.values[0]].from);
		        	viewModel.max(vm.buckets[ui.values[1]].to);
		        },
		        //stop: SGX.screener.search.criteriaSearch,
		        step: 1
			});
		}
	};
	
	PAGE = {

		//fqdn : "http://54.254.221.141", /** PROD */
		fqdn : "http://ec2-107-23-250-19.compute-1.amazonaws.com", /** QA */
		
		pqdn : "http://sgx-pdf.wealthmsi.com/pdfx/",
			
		gaClientId: "UA-50238919-1",
		
		glossary: JSON.parse(GLOSSARY),
		
		tooltipHTML: TOOLTIP,
		
		init: function(child) {
			
			// extend parent
			$.extend(true, this, PAGEIMPL);

			// extend child
			$.extend(true, this, child);
			
			// initialize the core object
			this.initPage();
			
			return this;
			
		},
    	
        modal: {
        	
            close: function(settings) {
            	$.colorbox.close();
            },
            
            container: function() {
            	var container = $("<div />").addClass("modal-container");
            	$(container).append($("<div />").addClass("content"));
            	$(container).append($("<div />").addClass("nav"));
            	$(".nav", container).append('<span class="action-btn confirm prompt-display">Confirm</span><span class="action-btn cancel prompt-display">Cancel</span><span class="action-btn cancel alert-display">Close</span>');
            	return container;
            },
            
            open: function(settings) {

            	var container = this.container();
            	$(".content", container).html(settings.content);
            	$(container).addClass(settings.type);
            	
            	var cboxSettings = {
            		html: $(container),
            		overlayClose: false,
            		transition: 'none',
            		maxWidth: settings.hasOwnProperty("maxWidth") ? settings.maxWidth : 550,
            		onComplete: function() {
            			if (settings.hasOwnProperty("postLoad")) settings.postLoad(settings)
            			if (PAGE.getParentURL() != null) {
            				PAGE.resizeIframe(PAGE.pageHeight, 10);
            				$("#colorbox").position();
            			}
            		},
            		onClose: function() {
            			if (settings.hasOwnProperty("close")) settings.close(settings);
            		}
            	};
            	
            	if (PAGE.getParentURL() != null) cboxSettings.top = 100;
            	
            	$.colorbox(cboxSettings);

            	// cancel/close
            	$(".cancel, .close").click(function(e) {
            		if ($(this).hasClass("cancel") && settings.hasOwnProperty("cancel")) settings.cancel(settings);
            		PAGE.modal.close(); 
            	});
            	
            	// confirm
            	var confirm = $(".confirm", container);
        		confirm.removeData();
        		confirm.unbind();
            	if (settings.type == "prompt") {
            		confirm.data(settings);
            		$(confirm).click(function(e) { settings.confirm($(this).data()); });
            	}

            }
        },
        
        tooltips: {
        	
        	init: function(selector) {
        		
        		$(".tooltip-item", selector).each(function(idx, el) {
        			
        			var id = $(el).attr("tooltip-key");
        			$.each(PAGE.glossary.terms, function(idx, term) {
        				
        				if (term.id == id) {
        					$(el).attr("tooltip-copy", term.definition);
        				}
        			});
        			
					$(el).hover(PAGE.tooltips.open, PAGE.tooltips.close);
        			
        		});
        		
        	},
        	
        	open: function(event) {
        		
        		var el = $(event.currentTarget);
        		
        		var template = $(PAGE.tooltipHTML);
        		$(template).addClass("current");
        		$(template).appendTo("body");
        		$(".tooltip-content", template).html($(el).attr("tooltip-copy"));
        		
                var height = $(template).height(), width = $(template).width();
                var left = $(el).offset().left - (width/2) + 10;
                
                if ($(el).hasClass("tooltip-left")) left = $(el).offset().left - width + 25;
                
                else if ($(el).hasClass("tooltip-right")) left = $(el).offset().left - 25;
                
                $(template).css({
                    'top': $(el).offset().top - height - 12,
                    'left': left
                });
                
                $(template).fadeIn();
        		
        	},
        
            close: function(filter) {
            	$(".tooltip").remove();
            }
        }
		
	};
	
	return PAGE;
	
});