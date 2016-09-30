define(["jquery", "wmsi/page", "wmsi/utils", "knockout",  "text!client/data/glossary.json", "text!client/templates/tooltip.html", "text!../../data/pages.jsonp", "text!client/templates/add-watchlist.html", "moment", "text!client/data/currency.json","text!client/data/messages.json", "knockout-amd-helpers", "text", "jquery-ui", "colorbox", "jquery-timeout"], function($, PAGEIMPL, UTIL, KO, GLOSSARY, TOOLTIP, PAGEINFO, addWatchlist, moment, CUR, MESSAGES) {

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
	        	//console.log(allBindings().text);
	            return PAGE.getFormatted(KO.unwrap(valueAccessor()), allBindings().text);
	        });
				
		}
	};
	
	KO.bindingHandlers.formatVolume = {
			update: function(element, valueAccessor, allBindings) {
		        return KO.bindingHandlers.text.update(element,function() {
		        	if (valueAccessor() == null) return "-";
		        	if(parseFloat(allBindings().text) == parseFloat("0.000")) {
		        		return "-";
		        	}
		        	else {
		        		return allBindings().text;
		        	}
		        });
					
			}
		};
	
	KO.bindingHandlers.formatNonZeroValue = {
		update: function(element, valueAccessor, allBindings) {
	        return KO.bindingHandlers.text.update(element,function(){
	        	if (valueAccessor() == null) return $(element).html(); 
	        	//console.log(allBindings().text);
	            return PAGE.formatZeroValue(KO.unwrap(valueAccessor()), allBindings().text);
	        });
				
		}
	};

	KO.bindingHandlers.precision = {
		update: function(element, valueAccessor, allBindings) {
			var precision = valueAccessor();
			var value = allBindings().text;
      //console.log(allBindings);
      var replacement = allBindings.get('ifnoval');
            var postFix = allBindings().postFix;
            var formatNum = allBindings().formatNum;

			return KO.bindingHandlers.text.update(element, function(){
				if(value == null || value == '-') {
          if (replacement !== undefined) {
            return replacement;
          }else {
            return '-';
          }
        }
                var roundingMultiplier = Math.pow(10, precision);
                var newValueAsNum = isNaN(value) ? 0 : parseFloat(+value);
                valueToWrite = Math.round(newValueAsNum * roundingMultiplier) / roundingMultiplier;
                //ensure trailing .00;
                valueToWrite = valueToWrite.toString();
                if(valueToWrite.indexOf('.') != -1) {
                	if(valueToWrite.split('.')[1].length != precision) {
                		valueToWrite += getZeroes(precision - valueToWrite.split('.')[1].length);
                	}
                } else {

                	valueToWrite += '.'+getZeroes(precision);
                }
                if(precision == 0){
                  valueToWrite =  Math.round(value);
                  valueToWrite+="";
                }
                if(formatNum) {
                  valueToWrite = valueToWrite.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                }
                if(valueToWrite==0.00)return "-";
                if(postFix) {
                  if( postFix != '%')
                    valueToWrite += ' ';
                  valueToWrite += postFix;
                }
                return valueToWrite;

              });

		}

	};

    function getZeroes(len) {
        var retVal = '';
        if (!(len > 0) ) return;
        for (var i = 0; i < len; i++) {
            retVal+= '0';
        }
        return retVal;
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


    KO.bindingHandlers.prependIf = {
        update: function(element, valueAccessor, allBindings) {
            return KO.bindingHandlers.text.update(element,function(){
                var value = allBindings().text;
                var prep = KO.unwrap(valueAccessor());
                var conditional = KO.unwrap(allBindings().conditional);
                return conditional ? prep + value : value;
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
	            activate: function(event, ui) {
	            	var headerHeight = 200;
	            	PAGE.resizeIframeSimple(600);
	            },
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
					// Fix for Consensus Recomendation
		        	var tID = $(template[0]).attr('data-id');
					if (tID != 'avgBrokerReq'){
			        	viewModel.updatesMin(vm.buckets[ui.values[0]].from);
			        	viewModel.updatesMax(vm.buckets[ui.values[1]].to);
					}
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
				if(extra!=null&&extra.indexOf('code')>-1) {
				    if(extra.indexOf('=')>-1)
					extra='code='+encodeURIComponent(extra.substr(extra.indexOf('=')+1,extra.len));
				}
				url = PAGE.getPage(PAGE.pageData.getPage(id), extra); 
			}
			else {
				url = PAGE.getPage(PAGE.pageData.getPage(vals));
			}
			$(element).attr("href", url).attr("target", "_parent");
		}
	};
	
	KO.bindingHandlers.stockListSiteLink = {
			update: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
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
	
	// Input field to only allow numeric values
	KO.bindingHandlers.numeric = {
	    init: function (element) {
	        $(element).on("keydown", function (event) {
	            // Allow: backspace, delete, tab, escape, and enter
	            if (event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 ||
	                // Allow: Ctrl+A
	                (event.keyCode == 65 && event.ctrlKey === true) ||
	                // Allow: . ,
	                (event.keyCode == 190 || event.keyCode == 110) ||
	                // Allow: home, end, left, right
	                (event.keyCode >= 35 && event.keyCode <= 39)) {
	                // let it happen, don't do anything
	                return;
	            }
	            else {
	                // Ensure that it is a number and stop the keypress
	                if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105)) {
	                    event.preventDefault();
	                }
	            }
	        });
	    }    
	};

  // hide until data is ready
  KO.bindingHandlers.showHidden = {
    update: function (element, valueAccessor) {
      var val = !!KO.unwrap(valueAccessor());
      if (val === true) {
        $(element).removeClass('hidden');
      } else {
        $(element).addClass('hidden');
      }
    }
  }
	
	
	PAGE = {
		fqdn: "",
		//fqdn: "https://sgx-api-us.sharefc.com",
		
		pqdn : window.location.hostname == "localhost" || window.location.hostname == "sgx.fakemsi.com" ? "http://localhost:3000/?site=" : "http://pdfx.sharefc.com/pdfx/",
			
		gaClientId: "UA-50238919-1",
		
		glossary: JSON.parse(GLOSSARY),
		
		tooltipHTML: TOOLTIP,
		
		currentFormats: null,
		
		newWLName: KO.observable(),
		
        userStatus: KO.observable(''),
		
		finalWL: KO.observableArray(),
		
		selectedValue: KO.observable(),	
		
		currentCompanyName: KO.observable(),	
		
		currentTicker: KO.observable(),		
		
		newWLName: KO.observable(),		
		
		addWatchlistName: KO.observableArray(),
		
		currencyDD: JSON.parse(CUR),
		
		getCurrencies: KO.observableArray(),
		
		selectedCurrency: KO.observable(),
		
		changedCurrency: KO.observable(false),
		
		premiumUser: KO.observable(false),
		
		libTrialExpired: KO.observable(true),
		
		libLoggedIn: KO.observable(true),
		
		libTrialPeriod: KO.observable(true),
		
		libAlerts: KO.observable(true),
		
		libCurrency: KO.observable(true),
		
		libCurrSelect: KO.observable(true),	
		
		currentDay: KO.observable(),
		
		//Make this value true on actions where the page reloads and if that is valid navigatin
		//and if you don't want the singed-in user to not logout
		validNavigation: KO.observable(false),
		
		TIMEOUT_SECONDS:1000,
		
		messages: JSON.parse(MESSAGES),
		
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
		
		"numberFormats-hkd": {
        	lookup: { header: "" },
        	string: { header: "" },
			millions: { header: "in HK$ mm", decimals: 2, format: "HK$ $VALUE mm" },
			volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
			dollars: { header: "in HK$", decimals: 3, format: "HK$ $VALUE" },
			cents: { header: "in HK$", decimals: 3, format: "HK$ $VALUE" },
			percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "HK$ "}
		},
		"numberFormats-idr": {
        	lookup: { header: "" },
        	string: { header: "" },
			millions: { header: "in Rp mm", decimals: 2, format: "Rp $VALUE mm" },
			volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
			dollars: { header: "in Rp", decimals: 3, format: "Rp $VALUE" },
			cents: { header: "in Rp", decimals: 3, format: "Rp $VALUE" },
			percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "Rp "}
		},
		"numberFormats-myr": {
        	lookup: { header: "" },
        	string: { header: "" },
			millions: { header: "in RM mm", decimals: 2, format: "RM $VALUE mm" },
			volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
			dollars: { header: "in RM", decimals: 3, format: "RM $VALUE" },
			cents: { header: "in RM", decimals: 3, format: "RM $VALUE" },
			percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "RM "}
		},
		"numberFormats-php": {
        	lookup: { header: "" },
        	string: { header: "" },
			millions: { header: "in ₱ mm", decimals: 2, format: "₱ $VALUE mm" },
			volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
			dollars: { header: "in ₱", decimals: 3, format: "₱ $VALUE" },
			cents: { header: "in ₱", decimals: 3, format: "₱ $VALUE" },
			percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "₱ "}
		},
		"numberFormats-thb": {
        	lookup: { header: "" },
        	string: { header: "" },
			millions: { header: "in ฿ mm", decimals: 2, format: "฿ $VALUE mm" },
			volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
			dollars: { header: "in ฿", decimals: 3, format: "฿ $VALUE" },
			cents: { header: "in ฿", decimals: 3, format: "฿ $VALUE" },
			percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "฿ "}
		},
		"numberFormats-twd": {
        	lookup: { header: "" },
        	string: { header: "" },
			millions: { header: "in NT$ mm", decimals: 2, format: "NT$ $VALUE mm" },
			volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
			dollars: { header: "in NT$", decimals: 3, format: "NT$ $VALUE" },
			cents: { header: "in NT$", decimals: 3, format: "NT$ $VALUE" },
			percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "NT$ "}
		},
		"numberFormats-usd": {	
        	lookup: { header: "" },
        	string: { header: "" },
			millions: { header: "in US$ mm", decimals: 2, format: "US$ $VALUE mm" },
			volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
			dollars: { header: "in US$", decimals: 3, format: "US$ $VALUE" },
			cents: { header: "in US$", decimals: 3, format: "US$ $VALUE" },
			percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "US$ "}
		},		
        "numberFormats-sgd": {
        	lookup: { header: "" },
        	string: { header: "" },
        	millions: { header: "in S$ mm", decimals: 2, format: "S$ $VALUE mm" },
        	volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
        	dollars: { header: "in S$", decimals: 3, format: "S$ $VALUE" },
        	cents: { header: "in S$", decimals: 3, format: "S$ $VALUE" },
        	percent: { header: "in %", decimals:2, format: "$VALUE%" },
        	number: { header: "", decimals: 3 },
        	number1: { header: "", decimals: 1 },
			chart: {format: "S$ "}
        },
		
		init: function(child) {
			UTIL.retrieveRefreshNavigation("");
			if ( location.pathname.split("/")[1] == "print.html" && UTIL.getParameterByName("currency")!= undefined){
				this.currentFormats = PAGE["numberFormats-"+UTIL.getParameterByName("currency")];
			}
			else
      		this.currentFormats = PAGE["numberFormats-"+UTIL.retrieveCurrency()];
			
			// set up the page mappings
			eval("this.pageData." + PAGEINFO);
			
			// extend parent
			$.extend(true, this, PAGEIMPL);
			
			$(window).on('click', function(e) {
		    	if(e.target.nodeName.toLowerCase() === "a" || e.target.nodeName.toLowerCase() === "img" || e.target.nodeName.toLowerCase() === "span" || e.target.nodeName.toLowerCase() === "select"){
		    		console.log("anchor tag cliked");
		    		PAGE.validNavigation(true);
		    	}
		    	else {
		    		console.log("not an anchor tag cliked");
		    		PAGE.validNavigation(false);
		    	}
	            
	        });
			
			$(window).on('popstate', function (e) {
			    PAGE.validNavigation(true);
			});
			
			$(window).on('beforeunload', function(e){
				if(!PAGE.validNavigation() && UTILS.retrieveAuthToken()!=false) {
						PAGE.validNavigation(true);
						var endpoint = PAGE.fqdn + "/sgx/logout";
						
						var params = {};
						
						UTIL.handleRefreshAjaxRequestLogout(
							endpoint,
							function(data, textStatus, jqXHR){
							console.log("logout");
							}, 
							PAGE.customSGXError);
						UTIL.saveAuthToken("");
						UTIL.deleteAuthToken();
						UTIL.deleteCurrency();
						UTIL.deleteState();
						UTIL.saveRefreshNavigation("refresh");
				}
			}); 
			
			// initialize the core object (something broke somewhere and this stopped being called)
			PAGEIMPL.initPage();
			
			// extend child
			$.extend(true, this, child);
			
			// initialize the core object
			this.initPage();
			if(UTIL.getParameterByName("page") != 14 && UTIL.retrieveRefreshNavigation() === "refresh"){
				top.location.href = PAGE.getPage(PAGE.pageData.getPage('sign-in'));
			}
			return this;
			
		},

		hasGlossaryTerm: function(name) {
			return $.grep(this.glossary.terms, function(e, i) { return e.id == name; }).length > 0;
		},

	getFormatter:function(fmt){
	    return this.currentFormats.hasOwnProperty(fmt) ? this.currentFormats[fmt] : {};
	},
	
        getFormatted: function(fmt, value, decimals) {
        	if (typeof fmt === "undefined" || fmt == "string" || fmt == "lookup") return value;
        	if (value === "" || value === "-") return value;
          if (value == null) return '-';
        	
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
    	
        formatZeroValue: function(fmt, value) {
    	       if (typeof fmt === "undefined" || fmt == "string" || fmt == "lookup") return value;
    	       if (value === "" || value === "-") return value;
    	       if (value == null) return '-';
    	
		var val = value;
		
		var formatter = PAGE.getFormatter(fmt);
		
		if (fmt.indexOf("number") != -1 || fmt == "millions" || fmt == "percent" || fmt =="dollars" || fmt =="cents" || fmt == "volume") {

			// round
			val = parseFloat(val).toFixed(formatter.decimals).replace(/(\.\d*[1-9])0+$/,'$1').replace(/\.0*$/,'');

			// give some commas
			var parts = val.split(".");
    		    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    		    if (parts.length > 1 && parseInt(parts[1], 10) > 0) val = parts.join(".");
    		    else val = parts[0];

    		 // negative numbers
		if (val.indexOf("-") == 0 && val==0) {
			return "-";
		};
				
				
		if(val==0){
		    return "-";
				}
		// negative numbers
		if (val.indexOf("-") == 0) val = "(" + val.substring(1) + ")";
				
    		    // make it pretty
    		    if (formatter.hasOwnProperty("format")) val = formatter.format.replace(new RegExp("\\$VALUE","gm"), val);

    		    
		    
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
					height: settings.hasOwnProperty("height") ? settings.height : false,
					width: settings.hasOwnProperty("width") ? settings.width : 550,
            		maxWidth: settings.hasOwnProperty("maxWidth") ? settings.maxWidth : 550,
            		maxHeight: settings.hasOwnProperty("maxHeight") ? settings.maxHeight : false,
					scrolling: settings.hasOwnProperty("scrolling") ? settings.scrolling : false,
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
                	var text = $(this).attr("data-value");
                	var dataName = typeof $(this).attr("data-name") !== "undefined" ? $(this).attr("data-name") : $(this).closest(".criteria, .additional-criteria").attr("data-name");
                	var viewModel = KO.dataFor($(dd).closest(".criteria")[0]);
					var criteriaRow = $(this).closest('tr').attr('data-id');
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
                	
                	viewModel.val(text);
					// Fix for exchange drop-down copy
					if (criteriaRow == 'exchange'){
						$("span.copy", dd).text(viewModel.criteria.fieldGroups[4].fields[0].itemLabels[text]);
					} else {
                		$("span.copy", dd).text(text);
					}
                	
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
			$('#loading').remove();
		},
		
		showLoading: function() {
			$('body').prepend($('<div id="loading"><div class="loading-text"><img src="img/ajax-loader.gif"></div></div>'));
		},
		checkStatus: function(){
      		var endpoint = PAGE.fqdn + "/sgx/account/info";
		      // var endpoint = 'https://localhost:2443' + "/sgx/account/info";
		      // var endpoint = 'https://sgx.mymsi.com:3443/sgx/account/info';
			var postType = 'POST';
			var params = {};
			UTIL.handleAjaxRequestJSON(
				endpoint,
				postType,
				params,
				function(data, textStatus, jqXHR){
					if (data.reason == 'Full authentication is required to access this resource' || data.reason == 'Invalid username or password' || data.reason == 'Authentication token not Valid'){
						PAGE.premiumUser(false);
	           		    PAGE.userStatus('UNAUTHORIZED');
						
						
			            var currentPg = PAGE.getParameterByName('page');
			            if (currentPg == 18) {
			              top.location.href = PAGE.getPage(PAGE.pageData.getPage('sign-in'));
			              return;
			            }
					} else {
					
						PAGE.premiumUser(true);
						PAGE.premiumUserAccntInfo = data;
						//data.type = 'EXIRED';
						
						if (data.type == 'PREMIUM'){
							PAGE.premiumUserEmail(PAGE.premiumUserAccntInfo.email);
							PAGE.userStatus('PREMIUM');
							PAGE.libTrialPeriod(true);
							PAGE.libTrialExpired(false);
							PAGE.libSubscribe(true);
							
						}
						
						if (data.type == 'TRIAL'){
				            var start = moment(data.startDate);
				            var end = moment(data.expirationDate);
				            var daysRemaining = data.daysRemaining;
							if (daysRemaining >= 0) {
							    	if(daysRemaining==0){
							    	    var displayMessage = PAGE.messages.messages[0];
							    	    $('#introTrialLastDay').html('&nbsp;'+displayMessage.accountSettings.introPremiumLastDay);
							    	}
								PAGE.userStatus('TRIAL');
								PAGE.libLoggedIn(true);
								PAGE.libTrialExpired(false);
								PAGE.currentDay(daysRemaining);
							}else {
								PAGE.userStatus('EXPIRED');
								PAGE.libTrialExpired(true);						
								PAGE.libLoggedIn(true);
								PAGE.libTrialPeriod(true);
								PAGE.libAlerts(true);
								PAGE.libCurrency(true);	
								//PAGE.libCurrSelect(true);	
							}
						
						}
						
						if (data.type == 'EXPIRED'){
				            PAGE.userStatus('EXPIRED');
				            PAGE.libTrialExpired(true);						
							PAGE.libLoggedIn(true);
							PAGE.libTrialPeriod(true);
							PAGE.libAlerts(true);
							PAGE.libCurrency(true);							
						}
						
						PAGE.timedLogout();
						setTimeout(function(){ PAGE.callout(); }, PAGE.TIMEOUT_SECONDS);
						PAGE.TIMEOUT_SECONDS=100000000;//No need to call again!!
						if (data.type == 'PREMIUM' || data.type == 'TRIAL') {

						    //898
							var endpoint = PAGE.fqdn + "/sgx/currencyList";
							var postType = 'POST';
							var params = {};
							UTIL.handleAjaxRequestJSON(
								endpoint,
								postType,
								params,
								function(data, textStatus, jqXHR){
									
									PAGE.getCurrencies(data);
								              PAGE.selectedCurrency(UTILS.retrieveCurrency().toLowerCase());

								              PAGE.selectedCurrency.subscribe(function(newValue) {
								                if (newValue != UTILS.retrieveCurrency()){                
								                  UTILS.saveCurrency(newValue.toLowerCase());
								                  setTimeout(function(){
								                    location.reload();
								                  }, 100);
								                }

								              });
								}, 
								PAGE.customSGXError);
							//PAGE.getCurrencies(PAGE.currencyDD.currencyList);


					
						}
					}
				}, 
				PAGE.customSGXError);			
		},
		timedLogout: function(){
			$('body').idleTimeout({
			  idleTimeLimit: 900,
			  idleCheckHeartbeat: 60,
			   customCallback:    function () {    // define optional custom js function
				   top.location.href = PAGE.getPage(PAGE.pageData.getPage('logout'));
			   },
			  enableDialog: false,
			  activityEvents: 'click keypress scroll wheel mousewheel mousemove',
			  sessionKeepAliveTimer: false
			});	
		},
		//something similar for renewing the token.call to ajax 
		
		
		    callout : function () {
			var set_delay = 660000;
			//var set_delay = 200000; //dev testing the code;call for every 3mins
			var endpoint = PAGE.fqdn + "/sgx/reqNewTxToken";
			var params = {};
			var jsonp = 'jsonp';
			var postType = 'POST';
			var jsonpCallback = 'jsonpCallback';
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params,
				jsonp,
				function(data, textStatus, jqXHR){
				    //console.log(textStatus);
				    UTIL.saveAuthToken(data.token);
				},PAGE.customSGXError,jsonpCallback
				);
				setTimeout(PAGE.callout, set_delay);//call every 12mins!!
		    },
		addWatchlist: function(me) {
			me.currentCompanyName(me.company.companyInfo.companyName);
			me.currentTicker(me.company.companyInfo.tickerCode);
			PAGE.newWLName('');
			var settings = {	    			
				maxWidth: 500,
				height: 300,				
				content: addWatchlist,				
				viewModel: this.viewModel,				
				type: 'content',				
				postLoad: function(settings) {
					
					var ticker = me.company.companyInfo.tickerCode;					
					
					PAGE.selectedValue.subscribe(function(data){});
					
					var endpoint = PAGE.fqdn + "/sgx/watchlist/get";
					var postType = 'POST';
					var params = {};
					var jsonp = 'jsonp';
					var jsonpCallback = 'jsonpCallback';
					UTIL.handleAjaxRequest(
						endpoint,
						postType,
						params, 
						undefined, 
						function(data, textStatus, jqXHR){
							function sortByName(a, b){
							  var a = a.name.toLowerCase();
							  var b = b.name.toLowerCase(); 
							  return ((a < b) ? -1 : ((a > b) ? 1 : 0));
							}
							PAGE.finalWL(data.watchlists.sort(sortByName));
						}, 
						PAGE.customSGXError,
						jsonpCallback);					
					
					$('.button.add-wl').click(function(e){
						PAGE.showLoading();
						if (PAGE.selectedValue() == undefined){
							PAGE.addNewCompany();
						} else {
							PAGE.addCompanyExisting();	
						}
					});					
					
					KO.applyBindings(settings.viewModel, $(".modal-container")[0]);
					
				}
			};
			
			PAGE.modal.open(settings);
			PAGE.resizeIframeSimple();
		},
		
		addCompanyExisting: function(){
			var ticker = PAGE.currentTicker();
			$.each(PAGE.finalWL(), function(idx, wl){
				if (PAGE.selectedValue() ==  wl.id){
					
					var companies = wl.companies;
					
					if (companies.length >= 25) { alert("You have added 25 companies to this StockList. Please choose another."); PAGE.hideLoading(); return; }
					
					if ($.inArray( ticker, companies ) != -1) { alert("This company already exists in this StockList."); PAGE.hideLoading(); return; }
					
					wl.companies = KO.observableArray(companies);
					
					companies.push(ticker);	
					
					var endpoint = PAGE.fqdn + "/sgx/watchlist/addCompanies";
					var postType = 'POST';
					var params = {"id": PAGE.selectedValue(), "companies": companies};
					var jsonp = 'jsonp';
					var jsonpCallback = 'jsonpCallback';
					UTIL.handleAjaxRequest(
						endpoint,
						postType,
						params, 
						undefined, 
						function(data, textStatus, jqXHR){
              PAGE.hideLoading();	
							if (data.message == 'success'){
								$(".modal-container").colorbox.close();
							}
						}, 
						PAGE.customSGXError,
						jsonpCallback);								
				}
			});
		},
		addNewCompany: function(){
			var ticker = PAGE.currentTicker();
			var wlLength = PAGE.finalWL().length;
			
			$.each(PAGE.finalWL(), function(i, data){
				PAGE.addWatchlistName.push(data.name.toLowerCase());
			});			
						
			var newWLNameLC = PAGE.newWLName();
			
			if (newWLNameLC.trim()==="" ) {  alert("Stocklist name is empty."); PAGE.hideLoading(); return; }
			if (newWLNameLC.trim().length < 2 || newWLNameLC.trim().length > 40) {  alert("Stocklist must be between 1 and 40 characters."); PAGE.hideLoading(); return; }
			
			if ($.inArray( newWLNameLC.toLowerCase().trim(), PAGE.addWatchlistName() ) != -1) {  alert("Stocklist name already exists."); PAGE.hideLoading(); return; }
			if (wlLength >= 10) { alert("You can create up to 10 StockLists."); PAGE.hideLoading(); return; }
			
			var endpoint = PAGE.fqdn + "/sgx/watchlist/create";
			var postType = 'POST';
    		var params = { "message": PAGE.newWLName() };
			var jsonp = 'jsonp';
			var jsonpCallback = 'jsonpCallback';
						
			UTIL.handleAjaxRequest(
				endpoint,
				postType,
				params, 
				undefined, 
				function(data, textStatus, jqXHR){
					PAGE.finalWL(data);
					$.each(PAGE.finalWL(), function(idx, wl){
						if (wl.name == PAGE.newWLName()){
							var companies = wl.companies;
				
							if (companies.length >= 25) { alert("You have added 25 companies to this StockList. Please choose another."); PAGE.hideLoading(); return; }
							
							if ($.inArray( ticker, companies ) != -1) { alert("This company already exists in this StockList."); PAGE.hideLoading(); return; }
							
							wl.companies = KO.observableArray(companies);
							
							companies.push(ticker);	
							
							var endpoint = PAGE.fqdn + "/sgx/watchlist/addCompanies";
							var postType = 'POST';
							var params = {"id": wl.id, "companies": companies};
							var jsonp = 'jsonp';
							var jsonpCallback = 'jsonpCallback';
							UTIL.handleAjaxRequest(
								endpoint,
								postType,
								params, 
								undefined, 
								function(data, textStatus, jqXHR){
                  PAGE.hideLoading();	
									if (data.message == 'success'){
										$(".modal-container").colorbox.close();
									}			
									
								}, 
								function(jqXHR, textStatus, errorThrown){
                  PAGE.hideLoading();
									PAGE.customErrorMessage();
								},jsonpCallback);
						}
					});
				}, 
				PAGE.customSGXError,
				jsonpCallback);
		},
		/**
         * used for custom error function passed to handleAjaxRequest & handleAjaxRequestJSON
         * @param customMessage will be the message passed in to let the user know what the error is
         */
		customSGXError: function(jqXHR, textStatus, errorThrown) {
			if(jqXHR.responseText.indexOf('Invalid Token')>=0) {
				top.location.href = PAGE.getPage(PAGE.pageData.getPage('logout'));
        	}
        	
		    console.log(textStatus+"   "+errorThrown + "  "+jqXHR);
		    PAGE.hideLoading();
			//Developer to use in case of custom error handling
        }
		
	};
	
	return PAGE;
	
});