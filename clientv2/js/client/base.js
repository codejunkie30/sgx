define(["jquery", "wmsi/page", "wmsi/utils", "knockout",  "text!client/data/glossary.json", "text!client/templates/tooltip.html", "text!../../data/pages.jsonp", "highstock", "knockout-amd-helpers", "text", "jquery-ui", "colorbox", "jquery-timeout"], function($, PAGEIMPL, UTIL, KO, GLOSSARY, TOOLTIP, PAGEINFO) {
	
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
	        	if (valueAccessor() == null) return $(element).html(); 
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
	            load: function(event, ui) {
	            	KO.cleanNode(ui.panel[0]);
	            	try { KO.applyBindings(viewModel, ui.panel[0]); } catch(err) {}
	            	PAGE.resizeIframeSimple();
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
			if (id == null) return;
			$.each(PAGE.glossary.terms, function(idx, term) {
				if (term.id == id) {
					$(element).attr("tooltip-copy", term.definition).addClass("tooltip-item");
				}
			});
			
			$(element).hover(PAGE.tooltips.open, PAGE.tooltips.close);
		}
	};
	
	
	KO.bindingHandlers.siteLink = {
		init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
			var vals = KO.unwrap(valueAccessor());
			var url = null;
			if (vals.hasOwnProperty("id")) {
				id = vals.id;
				extra = vals.extra;
				url = PAGE.getPage(PAGE.pageData.getPage(id), extra); 
			}
			else {
				url = PAGE.getPage(PAGE.pageData.getPage(vals));
			}
			$(element).attr("href", url).attr("target", "_parent");
		}
	};
	
	PAGE = {

		//fqdn : "http://54.254.221.141", /** PROD */
		//fqdn : "http://ec2-107-23-250-19.compute-1.amazonaws.com", /** QA */		
		//fqdn : "http://ec2-54-208-120-235.compute-1.amazonaws.com",
		fqdn: "https://sgx-api-us.sharefc.com",
		
		pqdn : window.location.hostname == "sgx.fakemsi.com" ? "http://localhost:3000/?site=" : "http://pdfx.sharefc.com/pdfx/",
			
		gaClientId: "UA-50238919-1",
		
		glossary: JSON.parse(GLOSSARY),
		
		tooltipHTML: TOOLTIP,
		
		currentFormats: null,
		
		pageData: {
			
			pages: {}, 
			
			configuration: function(data) {

				var prnt = this;
				
				$.each(data.pages, function(idx, nm) {
					if (nm == "") return;
					prnt.pages[nm] = {
						file: nm + ".html",
						id: idx
					};
				});
				
			},
			
			getPage: function(nm) {
				return this.pages[nm];
			},
			
            getCompanyPage: function(code, extra) {
            	return PAGE.getPage(this.getPage("company-tearsheet"), "code=" + code + (typeof extra === "undefined" ? "" : extra));
            }
			
		},
		
        "numberFormats-SGD": {
        	lookup: { header: "" },
        	string: { header: "" },
        	millions: { header: "in S$ mm", decimals: 1, format: "S$ $VALUE mm" },
        	volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
        	dollars: { header: "in S$", decimals: 3 },
        	cents: { header: "in S$", decimals: 3, format: "S$ $VALUE" },
        	percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 }
        },
		
		init: function(child) {
			
			this.currentFormats = PAGE["numberFormats-SGD"];

			// set up the page mappings
			eval("this.pageData." + PAGEINFO);
			
			// extend parent
			$.extend(true, this, PAGEIMPL);

			// extend child
			$.extend(true, this, child);
			
			// initialize the core object
			this.initPage();			
			
			return this;
			
		},
		
		hasGlossaryTerm: function(name) {
			return $.grep(this.glossary.terms, function(e, i) { return e.id == name; }).length > 0;
		},
		
        getFormatted: function(fmt, value) {
        	
        	if (typeof fmt === "undefined" || fmt == "string" || fmt == "lookup") return value;
        	if (value === "" || value === "-") return value;
        	
    		var val = value;
    		
    		var formatter = this.currentFormats.hasOwnProperty(fmt) ? this.currentFormats[fmt] : {};
    		
    		if (fmt.indexOf("number") != -1 || fmt == "millions" || fmt == "percent" || fmt =="dollars" || fmt =="cents" || fmt == "volume") {

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
    			try {
    				val = $.datepicker.formatDate("dd/M/yy", Date.fromISO(val));
    			}
    			catch(err) {
    				console.log(err);
    			}
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
					width: settings.hasOwnProperty("width") ? settings.width : 550,
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
                	var viewModel = KO.dataFor($(dd).closest(".criteria")[0]);
                	
                	if ($("ul li", dd).first().text() != def && text != def) {
                		$("ul", dd).prepend($("<li />").text(def)).click(function(e) {
                        	e.preventDefault();
                        	e.stopPropagation()
                        	viewModel.val(undefined);
                        	$(dd).find("span.copy").text($(e.target).text());
                			$(e.target).remove();
                			PAGE.dropdowns.close();
                			if (typeof finished !== "undefined") finished();
                		});
                	}
                	
                	//$(".copy", dd).text(text);
                	viewModel.val(text);
                	$("span.copy", dd).text(text);
                	
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
                
                if ($(el).hasClass("tooltip-left")) {
                	$(template).addClass("tooltip-left");
                	left = $(el).offset().left - width + 25;
                }
                else if ($(el).hasClass("tooltip-right")) {
                	$(template).addClass("tooltip-right");
                	left = $(el).offset().left - 25;
                }
                
                $(template).css({
                    'top': $(el).offset().top - height - 12,
                    'left': left
                });
                
                $(template).fadeIn();
        		
        	},
        
            close: function(filter) {
            	$(".tooltip").remove();
            }
        },
        
		hideLoading: function() {
			$('#loading').hide();
		},
		
		showLoading: function() {
			$('#loading').show();
		},
		checkStatus: function(){
			
			var endpoint = PAGE.fqdn + "/sgx/account/info";
			var postType = 'POST';
			var params = {};
			var jsonp = 'callback';
			var jsonpCallback = 'jsonpCallback';
			
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				jsonp,
				function(data, textStatus, jqXHR){
					if (data.reason == 'Full authentication is required to access this resource'){
						PAGE.premiumUser(false);
					} else {						
						PAGE.premiumUser(true);
						PAGE.premiumUserAccntInfo = data;
						
						if (data.type == 'PREMIUM'){
							PAGE.premiumUserEmail(PAGE.premiumUserAccntInfo.email);
							
							PAGE.libTrialPeriod(true);
							PAGE.libTrialExpired(true);
							PAGE.libSubscribe(true);
						}
						
						if (data.type == 'TRIAL'){
							var start = $.datepicker.formatDate("mm/dd/yy", Date.fromISO(data.startDate));
							var end = $.datepicker.formatDate("mm/dd/yy", Date.fromISO(data.expirationDate));
							var now = $.datepicker.formatDate("mm/dd/yy", Date.fromISO(new Date()));
							
							var trialPeriod = Math.floor(( Date.parse(end) - Date.parse(start) ) / 86400000);
							var daysRemaining = Math.floor(( Date.parse(end) - Date.parse(now) ) / 86400000);

							PAGE.libLoggedIn(true);
							PAGE.libTrialExpired(true);
							PAGE.currentDay(daysRemaining);
						}
						
						if (data.type == 'EXPIRED'){							
							PAGE.libLoggedIn(true);
							PAGE.libTrialPeriod(true);
							PAGE.libAlerts(true);
							PAGE.libCurrency(true);							
						}
						PAGE.timedLogout();
					}
					
				}, 
				function(jqXHR, textStatus, errorThrown){
					console.log(textStatus);
					console.log(errorThrown);
					console.log(jqXHR);
				},jsonpCallback);			
		},
		timedLogout: function(){
			$('body').idleTimeout({
			  idleTimeLimit: 1200,
			  idleCheckHeartbeat: 60,
			   customCallback:    function () {    // define optional custom js function
				   top.location.href = PAGE.getPage(PAGE.pageData.getPage('logout'));
			   },
			  enableDialog: false,
			  activityEvents: 'click keypress scroll wheel mousewheel mousemove',
			  sessionKeepAliveTimer: false
			});	
		}
		
	};
	
	return PAGE;
	
});