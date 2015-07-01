define(["jquery", "wmsi/page", "wmsi/utils", "knockout",  "text!client/data/glossary.json", "text!client/templates/tooltip.html", "knockout-amd-helpers", "text", "jquery-ui", "colorbox"], function($, PAGEIMPL, UTIL, KO, GLOSSARY, TOOLTIP) {
	
	/** change the default template path */
	KO.amdTemplateEngine.defaultPath = "client/templates";

	KO.extenders.withPrevious = function (target) {
	    // Define new properties for previous value and whether it's changed
	    target.previous = KO.observable();
	    target.changed = KO.computed(function () { return target() !== target.previous(); });

	    // Subscribe to observable to update previous, before change.
	    target.subscribe(function (v) {
	        target.previous(v);
	    }, null, 'beforeChange');

	    // Return modified observable
	    return target;
	}
	
	KO.bindingHandlers.enterkey = {
		init: function (element, valueAccessor, allBindings, viewModel) {
			var callback = valueAccessor();
			$(element).keypress(function (event) {
				var keyCode = (event.which ? event.which : event.keyCode);
				if (keyCode === 13) {
					callback.call(viewModel);
					return false;
				}
				return true;
			});
		}
	};
	
	KO.bindingHandlers.format = {
		update: function(element, valueAccessor, allBindings) {
	        return KO.bindingHandlers.text.update(element,function(){
	            return PAGE.getFormatted(KO.unwrap(valueAccessor()), allBindings().text);
	        });
				
		}
	}

	KO.bindingHandlers.prepend = {
			update: function(element, valueAccessor, allBindings) {
		        return KO.bindingHandlers.text.update(element,function(){
		        	var value = parseInt(allBindings().text);
		            var prep = KO.unwrap(valueAccessor());
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
		        	viewModel.updatesMin(vm.buckets[ui.values[0]].from);
		        	viewModel.updatesMax(vm.buckets[ui.values[1]].to);
		        },
		        stop: function(event, ui) {
		        	var template = $(event.target).closest(".criteria");
		        	var vm = $(template).data();
		        	viewModel.min(vm.buckets[ui.values[0]].from);
		        	viewModel.max(vm.buckets[ui.values[1]].to);
		        },
		        step: 1
			});
		}
	};
	
	KO.bindingHandlers.tooltip = {
		init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var id = KO.unwrap(valueAccessor());
			$.each(PAGE.glossary.terms, function(idx, term) {
				if (term.id == id) {
					$(element).attr("tooltip-copy", term.definition);
				}
			});
			
			$(element).hover(PAGE.tooltips.open, PAGE.tooltips.close);
		}
	};
	
	PAGE = {

		//fqdn : "http://54.254.221.141", /** PROD */
		fqdn : "http://ec2-107-23-250-19.compute-1.amazonaws.com", /** QA */
		
		pqdn : "http://sgx-pdf.wealthmsi.com/pdfx/",
			
		gaClientId: "UA-50238919-1",
		
		glossary: JSON.parse(GLOSSARY),
		
		tooltipHTML: TOOLTIP,
		
		currentFormats: null, 
		
        "numberFormats-SGD": {
        	millions: { header: "in S$ mm", decimals: 1, format: "S$ $VALUE mm" },
        	volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
        	dollars: { header: "in S$", decimals: 3 },
        	percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { decimals: 1 }
        },
		
		init: function(child) {
			
			this.currentFormats = PAGE["numberFormats-SGD"];
			
			// extend parent
			$.extend(true, this, PAGEIMPL);

			// extend child
			$.extend(true, this, child);
			
			// initialize the core object
			this.initPage();
			
			return this;
			
		},
		
        getFormatted: function(fmt, value) {
        	
        	if (typeof fmt === "undefined" || fmt == "string" || fmt == "lookup") return;
        	
    		var val = value;
    		if (val === "" || val === "-") return;
    		
    		var formatter = this.currentFormats.hasOwnProperty(fmt) ? this.currentFormats[fmt] : {};
    		
    		if (fmt.indexOf("number") != -1 || fmt == "millions" || fmt == "percent" || fmt =="dollars" || fmt == "volume") {

    			// round
    			val = parseFloat(val).toFixed(formatter.decimals).replace(/(\.\d*[1-9])0+$/,'$1').replace(/\.0*$/,'');

    			// give some commas
    			var parts = val.split(".");
    		    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    		    if (parts.length > 1 && parseInt(parts[1], 10) > 0) val = parts.join(".");
    		    else val = parts[0];

    		    // negative numbers
    		    if (val.indexOf("-") == 0) val = "(" + val.substring(1) + ")";
    		    
    		    // make it pretty
    		    if (formatter.hasOwnProperty("format")) val = formatter.format.replace(new RegExp("\\$VALUE","gm"), val);
    		    
    		}
    		else if (fmt == "date") {
    			val = $.datepicker.formatDate("dd/M/yy", Date.fromISO(val));
    		}
    		else {
    			console.log(fmt);
    		}
        	
    		return val;
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
            	$(settings.content).appendTo($(".content", container));
            	$(container).addClass(settings.type);
            	
            	var cboxSettings = {
            		html: $(container),
            		overlayClose: false,
            		transition: 'none',
            		maxWidth: settings.hasOwnProperty("maxWidth") ? settings.maxWidth : 550,
            		onComplete: function() {
            			if (settings.hasOwnProperty("postLoad")) settings.postLoad(settings)
            			if (PAGE.getParentURL() != null) {
            				//PAGE.resizeIframe(PAGE.pageHeight, 10); TODO make work
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
            	
            	return container;
            }
        },
        
        dropdowns: {
        	
            open: function(selector) {
            	$('.dropdown').closest(".button-dropdown").removeClass("open");
            	$(selector).addClass("open");
            	$("html").bind("click", PAGE.dropdowns.close);
            },

            close: function(selector) {
            	$("html").unbind("click", PAGE.dropdowns.close);
            	$('.dropdown').closest(".button-dropdown").removeClass("open");
            },
            
            init: function(selector, finished) {
            	
            	$('.button-dropdown', selector).unbind("click");
            	
            	$('.button-dropdown', selector).click(function(e) {

                	e.preventDefault();
                	e.stopPropagation()

            		var isOpen = $(this).hasClass("open");

            		if (isOpen) {
            			PAGE.dropdowns.close('.button-dropdown');
            			return;
            		}
            		
            		PAGE.dropdowns.open(this);
            		
            	});
            	
                $('.button-dropdown li', selector).click(function(e) {
                	
                	e.preventDefault();
                	e.stopPropagation()
                	
                	var dd = $(this).closest(".button-dropdown");
                	var def = typeof $(dd).attr("data-label") !== "undefined" ? $(dd).attr("data-label") : "";
                	var text = $(this).text();
                	var dataName = typeof $(this).attr("data-name") !== "undefined" ? $(this).attr("data-name") : $(this).closest(".criteria, .additional-criteria").attr("data-name"); 
                	var viewModel = KO.dataFor($(dd).closest("tr")[0]);
                	
                	if ($("ul li", dd).first().text() != def && text != def) {
                		$("ul", dd).prepend($("<li />").text(def)).click(function(e) {
                        	e.preventDefault();
                        	e.stopPropagation()
                			$(dd).find(".copy").text($(e.target).text());
                        	viewModel.val(undefined);
                			$(e.target).remove();
                			PAGE.dropdowns.close();
                			if (typeof finished !== "undefined") finished();
                		});
                	}
                	
                	$(".copy", dd).text(text);
                	viewModel.val(text);
                	
                	if (typeof finished !== "undefined") finished();
                	
                	dd.click();
                	
                });
                
            }
        },
        
        tooltips: {
        	
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