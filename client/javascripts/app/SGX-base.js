var deps = ['jquery', 'underscore', 'jquicore', 'jquiwidget', 'jquimouse', 'jquidatepicker', 'accordion', 'slider', 'tabs', 'highstock', 'colorbox', 'placeholder'];
if (location.pathname.indexOf("print.html") != -1) deps = ['jquery', 'underscore', 'jquicore', 'jquiwidget', 'jquidatepicker', 'highstock', 'colorbox'];

define(deps, function($, _, SGX) {
	
    SGX = {
    		
    		fqdn : "http://54.254.221.141", /** PROD */
    		//fqdn : "http://ec2-107-23-250-19.compute-1.amazonaws.com", /** QA */
    		
    		pqdn : "http://sgx-pdf.wealthmsi.com/pdfx/",
    		
    		resultSize: 25,
    		
    		letters: "ABCDEFGHIJSKLMNOPQRSTUVWXYZ",
    		
    		displayColumns: {
    			companyName: true,
    			tickerCode: true,
    			industry: true,
    			marketCap: true,
    			totalRevenue: true,
    			peRatio: true,
    			dividendYield: true
    		},
    		
    		screenerPage: { id: "0", file: "index.html" },
    		
    		companyPage: { id: "1", file: "company-tearsheet.html" },
    		
    		financialsPage: { id: "2", file: "financials.html" },
    		
    		relatedPage: { id: "3", file: "related.html" },
    		
    		alphasPage: { id: "0", file: "index.html" },

    		printPage: { id: "5", file: "print.html" },
    		
    		tradePage: { id: "6", file: "trade.html" },
    		
    		termsPage: { id: "7", file: "terms-conditions.html" },

            parentURL: null,
            
            pageHeight: $(document).height(),
            
            trackPage: function(title) {
            	window.document.title = title;
            	_gaTracker('send', 'pageview', { 'title': title });
            },
            
            numberFormats: {
            	millions: { header: "in S$ mm", decimals: 1, format: "S$ $VALUE mm" },
            	volume: { header: "in mm", decimals: 2, format: "$VALUE mm" },
            	dollars: { header: "in S$", decimals: 3 },
            	percent: { header: "in %", decimals:2, format: "$VALUE%" },
            	number: { header: "", decimals: 3 },
            	number1: { decimals: 1 }
            },
            
            getParentURL: function() {
            	if (SGX.parentURL != null) return SGX.parentURL;
             	if (typeof document.location.hash !== "undefined" && document.location.hash != "") {
                	SGX.parentURL = decodeURIComponent(document.location.hash.replace(/^#/, ''));
                	SGX.parentURL = SGX.parentURL.split("?")[0].split("#")[0];
            	}
             	else {
             		SGX.parentURL = window.location.pathname;
             	}
            	return SGX.parentURL;
            },

            getPage: function(page) {
            	var parentURL = SGX.getParentURL();
            	if (parentURL == null) return page;
            	return parentURL + "?page=" + page;
            },
                        
            getCompanyPage: function(code, extra) {
            	return SGX.getPage(SGX.companyPage.id + "&code=" + code + (typeof extra === "undefined" ? "" : extra));
            },
            
            getFinancialsPage: function(code, extra) {
            	return SGX.getPage(SGX.financialsPage.id + "&code=" + code + (typeof extra === "undefined" ? "" : extra));
            },

            getRelatedPage: function(code, extra) {
            	return SGX.getPage(SGX.relatedPage.id + "&code=" + code + (typeof extra === "undefined" ? "" : extra));
            },
            
            getTradePage: function(code, extra) {
            	return SGX.getPage(SGX.tradePage.id + "&code=" + code + (typeof extra === "undefined" ? "" : extra));
            },

            getAlphasPage: function(factor, quintile, extra) {
            	return SGX.getPage(SGX.alphasPage.id + "&type=alpha-factors&factor=" + factor + "&quintile=" + quintile + (typeof extra === "undefined" ? "" : extra));
            },
            
            getPrintPage: function(code, extra) {
            	return location.protocol + "//" + window.location.hostname + "/" +  SGX.printPage.file + "?code=" + code + (typeof extra === "undefined" ? "" : extra);
            },

            getTermsPage: function(extra) {
            	return SGX.getPage(SGX.termsPage.id + (typeof extra === "undefined" ? "" : extra));
            },

            resizeIframe: function(height, scroll) {
            	if (SGX.getParentURL() == null) return;
            	var fn = function() {
            		var msg = height + "-" + ((typeof scroll === "undefined") ? "0" : scroll);
            		SGX.pageHeight = height;
            		XD.postMessage(msg, SGX.getParentURL(), parent); 
            	};
            	setTimeout(fn, 10);
            },
            
            showUserGuide: function() {
            	window.open("http://www.sgx.com/wps/wcm/connect/f912408c-fc27-4cfa-ac02-90970dc6fd30/StockFacts-User-Guide.pdf?MOD=AJPERES");
            },
            
            showTerms: function() {
            	window.top.location.href = SGX.getTermsPage();
            },
    		
    		screener: {

    			changeSearchToggle: function(name) {

    				// remove all classes and add current class to results table
    				$(".screener-toggles .button").each(function(idx, el) { $(".module-results").removeClass($(this).attr("data-name") + "-clz"); });
    				$(".module-results").addClass(name + "-clz");

    				// toggle tabs
    				$(".screener-toggles .button, .screener-toggles .arrow").removeClass("selected");
    				$(".screener-toggles span[data-name='" + name + "']").addClass("selected");
    				
    				// hide/show options
    				$(".search-options").hide();
    				$("[data-section='" + name + "']").show();
    				
        			SGX.trackPage("SGX - Screener (" + $(".screener-toggles span[data-name='" + name + "']").text() + ")");
    				
    			},
    			
    			loadMarketingCopy: function() {
    				
    				$.getJSON( "data/homepage.json?time=" + new Date().getMilliseconds(), function(data) {
                        	var promo = Math.floor(Math.random() * data.promos.length) + 1;
                        	promo = data.promos[promo-1];
                        	$(".screener-header .message .intro-headline").html(promo.title);
                        	$(".screener-header .message .copy").html(promo.copy);
    				});
    				
    				
    			},

        		init: function() {

        			// some base variables
        			var searchType = SGX.getParameterByName("type") == "" ? "advanced-screener" : SGX.getParameterByName("type");
            		var factor = SGX.getParameterByName("factor");
            		var quintile = parseInt(SGX.getParameterByName("quintile"));
            		
            		SGX.screener.loadMarketingCopy();
            		
            		// handle toggle
            		SGX.screener.changeSearchToggle(searchType);
            		
        			// init general criteria
        			$(".editSearchB .checkbox").each(function(idx, el) {
        				

        				// set up glossary terms
        				$(".trigger", this).addClass("glossary-item");
        				$(".trigger", this).attr("glossary-key", $(this).attr("data-name"));
        				
        				$(this).attr("data-order", idx);
        				$(this).click(function(e) {
        					
        					var mainEl = $(this)
        					
        					// remove this field
        					if ($(this).hasClass("checked")) {
        						var target = $(".search-criteria [data-name='" + $(this).attr("data-name") + "']");
        						$("td.remove", target).click();
        						return;
        					}
        					
        					
                    		// check if we're maxed out first
                    		if ($(".search-criteria .criteria").length >= 5) {
                    			SGX.screener.criteriaChange.maxCriteriaMsg();
                    			return;
                    		} 
                    		
        					// add in the field
                    		SGX.showLoading();
        					var data = { "fields" : [ $(this).attr("data-name") ] };
        					SGX.screener.criteriaChange.getDistributions(data, SGX.screener.criteriaChange.addCriteria);
        					
        				});
        				
        			});
        			
        			// init alpha factors
            		$(".alpha-factor .slider").each(function(idx, el) {
            			
            			var name = $(this).attr("data-name");
            			
        				// add in spans
        				$.each(new Array(5), function(quint) {
        					
        					var span = $("<span />").attr("data-quintile", quint + 1).appendTo($(".bar-progress", el));
        					
        					$(span).hover(
        							function(e) {
        								var mine = $(this).attr("data-quintile");
        								$(this).parent().removeClass(function(pos, css) { return (css.match(/(^|\s)per-\S+/g) || []).join(' '); });
        								$(this).parent().addClass("per-" + (mine*20));
        							},
        							function(e) {
        								var mine = $(this).attr("data-quintile");
        								$(this).parent().removeClass(function(pos, css) { return (css.match(/(^|\s)per-\S+/g) || []).join(' '); });
        								if (typeof $(this).parent().attr("data-class") !== "undefined") $(this).parent().addClass($(this).parent().attr("data-class"));
        					});
        					
        					$(span).click(function(e) {
								var mine = $(this).attr("data-quintile");
								$(this).closest(".bar-progress").addClass("per-" + (mine*20)).attr("data-class", "per-" + (mine*20));
								$(this).closest(".slider").attr("data-value", mine);
								$(".slider-right-label", $(this).closest(".slider")).addClass("selected")
								SGX.showLoading();
								SGX.screener.search.alphaSearch();
        					});
        					
        				});
        				
            			// set up glossary terms
            			$(".glossary-item", this).attr("glossary-key", name);

						// select the current one
						if (name == factor && !isNaN(quintile) && quintile > 0 && quintile <= 5) {
							$(".bar-progress", el).addClass("per-" + (quintile*20)).attr("data-class", "per-" + (quintile*20));
							$(this).attr("data-value", quintile);
							$(".slider-right-label", this).addClass("selected")
						}
						
						// clicking the high label turns this off
						$(".slider-right-label", this).click(function() {
							if (!$(this).hasClass("selected")) return;
							SGX.showLoading();
							$(this).removeClass("selected");
							var prg = $(".bar-progress", el);
							$(prg).removeClass($(prg).attr("data-class")).removeAttr("data-class")
							$(el).removeAttr("data-value");
							SGX.screener.search.alphaSearch();
						});
						
            		});
        			
        			$(".editSearchB .button-reset").click(function(e) {
        				SGX.screener.criteriaChange.reset(SGX.screener.search.criteriaSearch);
        			});

        			$(".searchbar input").keypress(function(e) {
        				if (e.which == 13) SGX.screener.criteriaChange.reset(function() { SGX.screener.search.nameSearch($(".searchbar input").val()); });
        			});
        			
        			$(".searchtoggle .toggle, .searchtoggle .arrow").click(function() {
        				$(".searchtoggle .toggle").removeClass("selected");
        				$(".searchtoggle .s" + $(this).attr("data-name")).addClass("selected");
        				$(".searchbar input").attr("placeholder", $(this).attr("data-placeholder"));
        				$('.searchbar input').placeholder();
        				$('.searchbar input').val("");
        			});
        			
        			$(".searchtoggle .toggle:first").click();

        			$(".screener-header .search-submit").click(function(e) {
        				var fn = function() { SGX.screener.search.nameSearch($(".searchbar input").val()); };
        				if ($.trim($(".searchbar input").val()) == "") fn = function() { SGX.screener.search.showAll(); };
        				SGX.screener.criteriaChange.reset(fn);
        			});
        			
        			$(".screener-toggles .button[data-name='advanced-screener']").click(function(e) {
            			SGX.screener.criteriaChange.reset(SGX.screener.search.criteriaSearch);
        				SGX.screener.changeSearchToggle("advanced-screener");
            		});
        			
        			$(".screener-toggles .button[data-name='alpha-factors']").click(function(e) {
        				SGX.screener.changeSearchToggle("alpha-factors");
        				SGX.screener.search.criteriaSearch();
        			});

        			$(".screener-toggles .button[data-name='all-companies']").click(function(e) {
        				SGX.screener.changeSearchToggle("all-companies");
        				SGX.screener.search.criteriaSearch();
        			});
        			
        			SGX.screener.initCriteria();
        			
        		},
        		
        		isValidTicker: function(str) {
        			if (/^\w+$/.test(str) && str.indexOf("_") == -1) return null;
        			return "Only letters and numbers allowed in stock code search.";
        		},
        		
        		isValidName: function(str) {
        			return null;
        		},
        		
        		finalize: function(data) {

        			SGX.screener.drawInitialCriteria(data);
        			
                	SGX.tooltip.init("body");
        			SGX.accordion();
        			
        			SGX.screener.search.criteriaSearch();
        			
        			$(".button-customize-display").click(function() { SGX.screener.search.customizeResults(); });

        			SGX.hideLoading();

        			        			
        		},
        		
                initCriteria: function() {
                	
                	// load up all fields for distributions
                	var allFields = SGX.screener.getAllCriteria();
                	var data = { "fields" : [] };
                	$.each(allFields, function(idx, field) { 
                		if (field.selected) data.fields.push(field.id); 
                	});

                	SGX.screener.criteriaChange.getDistributions(data, SGX.screener.finalize);
                	
                },

                getAllCriteria: function() {
                	var ret = [];
                	$(".editSearchB [data-name]").each(function(idx, el) { 
                		if ($(el).attr("data-name") == "") return;
                		var field =  {};
                		ret.push(SGX.screener.populateField(field, el)); 
                	});
                	return ret;
                },
                
                populateField: function(field, el) {
                	if (!field.hasOwnProperty("field")) field.field = $(el).attr("data-name");
            		field.id = $(el).attr("data-name");
            		field.format = $(el).attr("data-type") === "undefined" ? "string" : $(el).attr("data-type");
            		field.name = $(".trigger", el).text();
            		field.selected = $(el).hasClass("checked");
            		if (!field.hasOwnProperty("template")) field.template = typeof $(el).attr("data-template") !== "undefined" ? $(el).attr("data-template") : "number";
            		field.label = typeof $(el).attr("data-label") !== "undefined" ? $(el).attr("data-label") : "";
            		field.order = parseInt($(el).attr("data-order"));
            		field.isDefault = $(el).hasClass("default");
            		field.shortName = $(el).attr("data-short-name");
            		field.minLabel = typeof $(el).attr("data-min") !== "undefined" ? $(el).attr("data-min") : "";
            		field.maxLabel = typeof $(el).attr("data-max") !== "undefined" ? $(el).attr("data-max") : "";
            		if (typeof $(el).attr("data-sort") !== "undefined" && field.format != "lookup") field.dataSort = $(el).attr("data-sort");
            		else if (field.format == "lookup") field.dataSort = "string";
            		else field.dataSort == "number";
            		if (field.format == "lookup") field.formatter = $.parseJSON($(el).attr("data-formatter"));
            		return field;
                },
                
                drawInitialCriteria: function(data) {
                	$(".search-criteria tbody").children().remove();
                	SGX.screener.drawCriteria(data);
                },
                
                drawCriteria: function(data) {

                	SGX.screener.handleDistributions(data);
                	
                	var runsearch = true;
                	
                	if (data.distributions.length > 1) {
                    	data.distributions.sort(function(a, b) {
                    		var a = parseInt($(".editSearchB [data-name='" + a.field + "']").attr("data-order"));
                    		var b = parseInt($(".editSearchB [data-name='" + b.field + "']").attr("data-order"));
                    		return a - b;
                    	});
                	}
                	
                	$.each(data.distributions, function(idx, distribution) {
                		
                		if ($(".search-criteria [data-name='" + distribution.field + "']").length > 0) return;
                		
                		// populate with label, other info from HTML
                		var el = $(".editSearchB [data-name='" + distribution.field + "']");
                		SGX.screener.populateField(distribution, el);
                		
                		var template;
                		
                		// draw the appropriate input
                		if (distribution.template == "select") template = SGX.screener.drawCriteriaSelect(distribution);
                		else if (distribution.template == "change") {
                			template = SGX.screener.drawCriteriaChange(distribution);
                			runsearch = false;
                		}
                		else template = SGX.screener.drawCriteriaSlider(distribution);
                		
                    	$(".label .glossary-item", template).attr("glossary-key", distribution.id);
                    	$(template).attr("data-name", distribution.id);

                		$("td.remove", template).click(function(e) {
                            e.stopPropagation();
                            e.preventDefault();
                            SGX.tooltip.close();
                            SGX.screener.criteriaChange.removeCriteria($(this).closest(".criteria"), function() {});
                		});
                		
                		SGX.tooltip.init(template);
                		
                	});
                	
                	$(".search-criteria .criteria").removeClass("even");
                	$(".search-criteria .criteria:even").addClass("even");
                	SGX.formatter.formatElements(".search-criteria");
                	
                	return runsearch;                	
                },
                
                handleDistributions: function(data) {
                	
                	if (typeof data.fieldValues === "undefined") return;
                	
                	$.each(data.fieldValues, function(idx, field) {
                		
                		// force sort asc
                		field.values.sort(function(a, b) { return a - b; });

                		// type of algorithm for bucketing
                		var type = $(".editSearchB [data-name='" + field.field + "']");
                		if (typeof type.attr("distribution-type") !== "undefined") type = type.attr("distribution-type");
                		else type = "normal";
                		
                		// number of buckets
                		var bCount = $(".editSearchB [data-name='" + field.field + "']");
                		if (typeof bCount.attr("distribution-buckets") !== "undefined") bCount = parseInt(bCount.attr("distribution-buckets"));
                		else bCount = 75;

                		// get the random distributions
                		var buckets = {};
                		$.each(field.values, function(vIdx, val) { SGX.screener.randomizeBucket(buckets, val, type, bCount); });
                		
                		// build the collection
                		var arr = [], start = 0;
                    	for (var prop in buckets) {
                    		if (!buckets.hasOwnProperty(prop)) continue;
                    		var bucket = {};
                    		bucket.count = buckets[prop].length;
                    		bucket.from = field.values[start];
                    		bucket.to = field.values[(buckets[prop].length-1) + start];
                    		arr.push(bucket);
                    		start += buckets[prop].length;
                    	}
                    	
                    	// replace the existing bucket
                    	SGX.screener.replaceBucket(data, field.field, arr, field.values, type);
                		
                	});
                	
                	delete data.fieldValues;
                	
                },
                
                randomizeBucket: function(bucket, val, type, cnt) {
                	var idx = (Math.round(val * 100000)%cnt);
                	if (type == "log") idx = (Math.round(Math.log(val * 100000))%cnt);
            		if (!bucket.hasOwnProperty(idx)) bucket[idx] = [];
            		bucket[idx].push(val);
                },
                
                replaceBucket: function(data, name, arr, vals, type) {
                	if (type == "histogram") return;
                	$.each(data.distributions, function(idx, dist) {
                		if (dist.field == name) {
                			dist.buckets = arr;
                			dist.values = vals;
                		}
                	});
                },
                
                drawCriteriaChange: function(distribution) {
                	
                	SGX.hideLoading();

                	var template = $("#criteria-templates [data-template='change']").clone(true);

            		// handle % change
        			var html = $(".change-picker").html();
        			
        			//SGX.resizeIframe(SGX.pageHeight, 0);

                    SGX.modal.open({
                        content: html,
                        type: 'prompt',
                        maxWidth: 600,
                        postLoad: function(options) {
                        	SGX.dateranges.init($(".modal-container .date"));
                        },
                        cancel: function(options) {
                        	$(".editSearchB [data-name='" + distribution.field + "']").removeClass("checked");
                        },
                        confirm: function(options) {
                        	
                        	if (parseInt($(".modal-container input").val()) <= 0 || parseInt($(".modal-container input").val()) > 100) {
                        		alert("Percent Change must be between 1 and 100");
                        		return;
                        	} 
                        	
                        	$(".editSearchB [data-name='" + distribution.field + "']").addClass("checked");
                        	
                        	distribution.label = distribution.shortName;
                        	$(template).data(distribution);
                        	$(".info", template).text(distribution.name);

                        	$(".percent-value", template).text($(".modal-container input").val());
                        	
                        	$(".start-date", template).text($.datepicker.formatDate("dd/M/yy", $(".modal-container .start").datepicker( "getDate" )));
                        	$(".start-date", template).attr("data-value", $.datepicker.formatDate("yy-mm-dd", $(".modal-container .start").datepicker( "getDate" )))
                        	$(".end-date", template).text($.datepicker.formatDate("dd/M/yy", $(".modal-container .end").datepicker( "getDate" )));
                        	$(".end-date", template).attr("data-value", $.datepicker.formatDate("yy-mm-dd", $(".modal-container .end").datepicker( "getDate" )))
                        	
                        	$(".search-criteria tbody").append(template);
                        	
                        	SGX.tooltip.init(template);

                        	$(".search-criteria .criteria").removeClass("even");
                        	$(".search-criteria .criteria:even").addClass("even");

                    		SGX.modal.close(); 
                    		
                			SGX.screener.search.criteriaSearch();
                			
                        }
                        
                    });            			
                	
                	return template;
                },
                
                drawCriteriaSelect: function(distribution) {
                	
                	var template = $("#criteria-templates [data-template='select']").clone(true);
                	var dd = $(".dropdown ul", template);

                	$(template).data(distribution);
                	$(".button-dropdown", template).attr("data-label", distribution.label);
                	$(".info", template).text(distribution.shortName);
                	$(".trigger .copy", template).text(distribution.label);

                	// hack for industryGroup
                	if (distribution.field == "industryGroup") {
                		distribution.buckets.push({ "data-name": "industry", count: 0, key: "Real Estate Investment Trusts (REITs)" });
                	}

                	distribution.buckets.sort(function(a, b) { return a.key - b.key; });
                	
                	$.each(distribution.buckets, function(idx, bucket) {
                		var li = $("<li />").text(bucket.key).appendTo(dd);
                		if (bucket.hasOwnProperty("data-name")) li.attr("data-name", bucket["data-name"]);
                	});
                	
                	$(".search-criteria tbody").append(template);
                	
                	SGX.dropdowns.init(template, SGX.screener.search.criteriaSearch);
                	
                    return template;
                	
                },
                
                drawCriteriaSlider: function(distribution) {
                	
                	distribution.min = distribution.buckets[0].from;
                	distribution.max = distribution.buckets[distribution.buckets.length - 1].to;
                	var matches = SGX.screener.getDistributionMatches(distribution, 0, distribution.buckets.length - 1) + ' matches';
                	var template = $("#criteria-templates [data-template='number']").clone(true);
                	
                	
                	$(template).data(distribution);
                	$(template).attr("data-name", distribution.id).attr("data-min", distribution.min).attr("data-max", distribution.max);
                	$(".info", template).text(distribution.name)
                	$(".min", template).text(distribution.minLabel != "" ? distribution.minLabel : distribution.min).attr("data-format", distribution.format);
                	$(".max", template).text(distribution.minLabel != "" ? distribution.maxLabel : distribution.max).attr("data-format", distribution.format);;
                	$(".matches", template).text(matches);
                	
                	$(".search-criteria tbody").append(template);

                    SGX.screener.criteriaSlider.init($('td.criteria-slider', template));
                    
                    return template;
                	
                },
                
                getDistributionMatches: function(distribution, startVal, endVal) {
                	var ret = 0;
                	
                	// primarily for consensus
                	if (!distribution.hasOwnProperty("values")) {
                		for (i = startVal; i <= endVal; i++) ret += distribution.buckets[i].count;
                		return ret;
                	}
                	
                	var sVal = distribution.buckets[startVal].from, eVal = distribution.buckets[endVal].to;
                	$.each(distribution.values, function(idx, val) {
                		if (val >= sVal && val <= eVal) ret++;
                	});
                	return ret;
                	
                },
                
                getDistributionValues: function(distribution) {
                	var ret = [];
                	$.each(distribution.buckets, function(idx, bucket) {
                		ret.push(bucket.count);
                	});
                	return ret;
                },
                
                criteriaSlider: {
                	
                    init: function(container) {
                    	
                    	var distribution = $(container).closest(".criteria").data();

                        $(container).find('.slider-bar').slider({
                            range: true,
                            min: 0,
                            max: distribution.buckets.length - 1,
                            values: [ 0, distribution.buckets.length - 1 ],
                            slide: SGX.screener.criteriaSlider.slide,
                            stop: SGX.screener.search.criteriaSearch,
                            step: 1
                        });
                                                
                    },
                    
                    slide: function(event, ui) {
                    	
                    	var template = $(event.target).closest(".criteria");
                    	var distribution = $(template).data();
                    	
                    	var min = distribution.buckets[ui.values[0]].from, max = distribution.buckets[ui.values[1]].to;
                    	
                    	$(".min", template).text(SGX.formatter.getFormatted($(".min", template).attr("data-format"), min));
                    	$(".max", template).text(SGX.formatter.getFormatted($(".max", template).attr("data-format"), max));
                    	
                    	var matches = SGX.screener.getDistributionMatches(distribution, ui.values[0], ui.values[1]) + ' matches';
                    	$(".matches", template).text(matches);

                    }

                },
                
                criteriaChange: {

                	addCriteria: function(data) {
                		var run = SGX.screener.drawCriteria(data);
                		$.each(data.distributions, function(idx, distribution) {
                			SGX.screener.criteriaChange.checkCriteriaItem($(".editSearchB [data-name='" + distribution.field + "']"));
                		});
                		if (run) SGX.screener.search.criteriaSearch();
                		SGX.hideLoading();
                	},
                	
                	resetAdditionalCriteria: function() {
                		
                		$(".additional-criteria").each(function() {
                			if ($(this).has(".button-dropdown")) {
                				$(".button-dropdown ul li", this).remove();
                				$(".button-dropdown .trigger .copy", this).text($(".button-dropdown", this).attr("data-label"));
                			}
                		});
                		
                	},
                	
                	removeCriteria: function(target, finished) {
                		SGX.screener.criteriaChange.uncheckCriteriaItem(target);
                        $(target).remove();
                        SGX.screener.search.criteriaSearch();
                    	$(".search-criteria .criteria").removeClass("even");
                    	$(".search-criteria .criteria:even").addClass("even");
                        if (typeof finished !== "undefined") finished();
                	},
                	
                	checkCriteriaItem: function(criteria) {
                		var name = $(criteria).attr("data-name");
                		var checkEl = $(".editSearchB [data-name='" + name + "']");
                        $(checkEl).addClass('checked');
                        $(checkEl).find('input[type="checkbox"]').attr("checked", "checked");            		
                	},
                	
                	uncheckCriteriaItem: function(criteria) {
                		var name = $(criteria).attr("data-name");
                		var checkEl = $(".editSearchB [data-name='" + name + "']");
                        $(checkEl).removeClass('checked');
                        $(checkEl).find('input[type="checkbox"]').removeAttr("checked");            		
                	},
                	
                	getDistributions: function(data, finished) {
                		
                		var tmpF = finished;
                		var endpoint = SGX.fqdn + "/sgx/search/distributions";
                		var hasPercentChange = $.inArray("percentChange", data.fields);
                		if (hasPercentChange != -1) data.fields.splice(hasPercentChange, 1);
                		
                		// change the function
                		if (hasPercentChange != -1) {
                			tmpF = function(data) { 
                				data.distributions.push({ "field": "percentChange", buckets: [], template: "change" }); 
                				finished(data);
                			};
                		}
                		
                		// do something else
                		if (data.fields.length == 0) { 
                			data = { distributions: [] }; 
                			tmpF(data);
                			return;
                		}
                		
                    	SGX.handleAjaxRequest(endpoint, data, tmpF, undefined);
                	},
                	
                	maxCriteriaMsg: function() {
                        SGX.modal.open({
                            content: '<h4>Search Criteria <span>(select up to 5)</h4><p>You have reached the criteria limit. Please remove criteria before proceeding.</p>',
                            type: 'alert'
                        });
                	},
                	
                	reset: function(finished) {
                		
                    	$(".search-criteria tbody").children().remove();
                    	
                    	var sorter = $(".module-results thead th.companyName");
                    	if (!$(sorter).hasClass("sort") && !$(sorter).hasClass("asc")) $(sorter).click();
                        
                    	// load up all fields for distributions
                    	var allFields = SGX.screener.getAllCriteria();
                    	var data = { "fields" : [] };
                    	$.each(allFields, function(idx, field) { 
                    		if (field.isDefault) data.fields.push(field.id); 
                    	});
                    	
                    	SGX.showLoading();
                    	
                    	SGX.screener.criteriaChange.getDistributions(data, function(data) { 
                    		$(".editSearchB .checkbox").removeClass("checked");
                    		$(".editSearchB .checkbox.default").addClass("checked");
                    		SGX.screener.drawCriteria(data); 
                    		SGX.hideLoading(); 
                    		if (finished) finished(data);
                    	});
                		
                	}
                	
                },
                
                search: {
                	
                	nameSearch: function(val, params) {
                		
                		val = $.trim(val);
                		var msg = null;

                		SGX.screener.changeSearchToggle("all-companies");

                		// nothing typed if, show all
                		if (val === "undefined" || val.length == 0) {
                			SGX.screener.search.showAll();
                			return;
                		}
                		
                		// validate string input
        				var msg = executeFunctionByName($(".searchtoggle .toggle.selected").attr("data-validator"), SGX, val);
        				if (msg != null) {
        					SGX.modal.open({ content: "<p>" + msg + "</p>", type: 'alert', maxWidth: 1000 });
        					return;
        				}
        				
        				// set the keyword search
                		if (typeof params === "undefined") params = { criteria: [] };
        				
                		var endpoint = "/sgx/search";
                		var cName = "companyName";
                		if ($(".searchtoggle .selected").attr("data-name") == "code") cName = "tickerCode";
                		params.criteria.push({ field: cName, value: val });
                		
                		SGX.trackPage("SGX Keyword Search by " + $(".searchtoggle .selected").attr("data-name") + " - " + val);
                		
                		SGX.screener.search.simpleSearch(SGX.fqdn + endpoint, params, val);
                		
                	},
                
                	showAll: function(params) {
                		if (typeof params === "undefined") params = { criteria: [] };
                		var endpoint = SGX.fqdn + "/sgx/search";
                		if (!$(".module-results thead th:first").hasClass("asc")) $(".module-results thead th:first").click();
                		SGX.screener.search.simpleSearch(endpoint, params);
                	},
                	
                	simpleSearch: function(endpoint, params, keyword) {
                		SGX.showLoading();
                		SGX.screener.criteriaChange.resetAdditionalCriteria();
                		SGX.handleAjaxRequest(endpoint, params, function(data) { if (typeof keyword !== "undefined") data.keywords = keyword; SGX.screener.search.renderResults(data); SGX.hideLoading(); }, SGX.screener.search.fail);
                	},
                	
                	addtlCritSearch: function() {
                		
                		// it's all search/name search
                		if ($(".expand-criteria").is(":visible")) {
                			
                			var crit = $(".additional-criteria .button-dropdown");
                			var ind = $(".copy", crit).text();
                			if ($(crit).attr("data-label") == ind) ind = null;
                			var sData = $(".module-results").data();

                			// additional crit
                			var params = {};
                			if (ind != null) params.criteria = [ { field: "industry", value: ind } ];

                			if (sData.hasOwnProperty("keywords")) SGX.screener.search.nameSearch(sData.keywords, params);
                			else SGX.screener.search.showAll(params);
                			
                			return;
                		}
                		
                		// normal search
                		SGX.screener.search.fullSearch(false);
                		
                	},
                	
                	criteriaSearch: function() {
                		
                		var type = $(".screener-toggles .button.selected").attr("data-name");
                		
                		if (type == "alpha-factors") SGX.screener.search.alphaSearch();
                		else if (type == "all-companies") SGX.screener.search.showAll();
                		else SGX.screener.search.fullSearch(true);
                		
                	},
                	
                	alphaSearch: function() {

                   		var endpoint = "/sgx/search/alphaFactors", params = {}, hasCrit = false;
                   		
                   		$(".alpha-factor .slider[data-value]").each(function(idx, el) {
                   			hasCrit = true;
                   			params[$(this).attr("data-name")] = parseInt($(this).attr("data-value"));
                   		});
                   		
                   		var done = function(data) { SGX.hideLoading(); SGX.screener.search.renderResults(data); };
                   		
                   		if (hasCrit) SGX.handleAjaxRequest(SGX.fqdn + endpoint, params, done, SGX.screener.search.fail); 
                   		else SGX.screener.search.showAll();

                		
                	},
                	
                	fullSearch: function(resetAddtl, finalize) {
                		
                		var endpoint = "/sgx/search";
                		var params = [];
                		
                		// reset any refine options
                		if (resetAddtl) SGX.screener.criteriaChange.resetAdditionalCriteria();
                		
                		$(".search-criteria .criteria, .additional-criteria").each(function(idx, el) {
                			
                			var name = $(this).attr("data-name");
                			
                			param = { field: name };
                			
                			// handle slider criteria
                			if ($(".slider-bar", this).length > 0) {
                				var distribution = $(this).data();
                				var sliderVals = $(".slider-bar", this).slider("values");
                				param.from = distribution.buckets[sliderVals[0]].from;
                				param.to = distribution.buckets[sliderVals[1]].to;
                			}
                			// dropdown
                			else if ($(".button-dropdown", this).length > 0) {
                				var dd = $(".button-dropdown", this);
                				var copy = $(".trigger .copy", dd).text();
                				if (dd.attr("data-label") == copy) return;
                				param.field = $(".trigger .copy", dd).attr("data-name");
                				param.value = copy;
                			}
                			// percent change
                			else if ($(this).attr("data-template") == "change") {
                				param.value = $(".percent-value", this).text();
                				param.from = $(".start-date", this).attr("data-value");
                				param.to = $(".end-date", this).attr("data-value");
                			}

                			// add to search
                			params.push(param);

                			// special case
                			if (name == "avgBrokerReq") {
                				params.push({ field: "targetPriceNum", from: "3" });
                			}
                			
                		});
                		
                		var qs = {};
                		qs.criteria = params;
                		SGX.handleAjaxRequest(SGX.fqdn + endpoint, qs, SGX.screener.search.renderResults, SGX.screener.search.fail);
                		
                	},
                
                	fail: function(xhr, ajaxOptions, thrownErr) {
                		console.log(xhr);
                		console.log(ajaxOptions);
                		console.log(thrownErr);
                	},
                	
                	renderResults: function(data) {

                		// one match, redirect
                        if (data.companies.length == 1) {
                        	window.top.location.href = SGX.getCompanyPage(data.companies[0].tickerCode);
                        }
                        
                		// reset name input
                		$(".searchbar input").val("");
                		
                		if (data.companies.length == 0) {
                    		$(".no-results-display").show();
                    		$(".module-results .results").show();
                    		$(".results-display").hide();
                		}
                		else {
                    		$(".results-display").show();
                    		$(".no-results-display").hide();
                		}
                		
                		var resultsCopy = data.companies.length + " results";
                		if (data.hasOwnProperty("keywords")) resultsCopy += " for \"" + data.keywords + "\"";
                		$(".module-results .label").text(resultsCopy);
                		$(".module-results tbody").children().remove();
                		
                		// all other fields
            			var fields = SGX.screener.search.getAllColumns();
            			var defaultDisplay = SGX.screener.search.getDisplayColumns();
            			var sort = "companyName";
            			var direction = "asc";

            			// add headers (if hasn't happened already)
            			if ($(".module-results thead th").length == 0) {
            				
                    		var header = $(".module-results thead");
                    		var thName = $("<th data-name='companyName' data-sort='string'><span>Company Name</span></th>").addClass("companyName").appendTo(header);
                    		
                    		// default sort, unless keyword search than use natural search ranking
                    		if (!data.hasOwnProperty("keywords")) $(thName).addClass("sort").addClass("asc");
                    		
                    		$.each(fields, function(idx, field) {
                    			var title = field.shortName;
                    			var th = $("<th />").attr("data-name", field.field).addClass(field.field).appendTo(header);
                    			if (field.format != "string" && field.format != "lookup") $(th).attr("data-sort", "number");
                    			else if (field.format == "lookup") $(th).attr("data-sort", "lookup")
                    			$(th).attr("data-format", field.format);
                    			$("<span />").text(title).appendTo(th);
                    			if (SGX.numberFormats.hasOwnProperty(field.format)) $("<span />").addClass("fmt").text(SGX.numberFormats[field.format].header).appendTo(th);
                    			if (!defaultDisplay.hasOwnProperty(field.field)) th.addClass('hidden');
                    		});
                    		
                    		$(".module-results thead th").click(function(e) {
                    			
                    			// remove keywords if they exist
                    			var tData = $(".module-results").data();
                    			delete tData.keywords;
                    			
                				var table = $(".module-results table");
                				$("th", table).removeClass("sort");

                				var sort = $(this).hasClass("asc") ? "asc" : "desc";
                				
                				$("th", table).removeClass("desc").removeClass("asc");
                				
                				if (sort == "desc") sort = "asc";
                				else sort = "desc";
                				
                				$(this).addClass("sort").addClass(sort);

                        		SGX.screener.search.displayRows(1, $(this).attr("data-name"), sort);
                                
                			});

            			}
            			else if (!data.hasOwnProperty("keywords")) {
            				sort = $("th.sort").attr("data-name");
            				direction = $("th.sort").hasClass("desc") ? "desc" : "asc";
            			}
            			else if (data.hasOwnProperty("keywords")) {
            				var table = $(".module-results table");
            				$("th", table).removeClass("sort").removeClass("asc").removeClass("desc");
            			}
            			
                		// add to the element
                		$(".module-results").data(data);
                		
                		// append the industries
                		var industries = [];
                		$.each(data.companies, function(idx, company) {
                			if (!company.hasOwnProperty("industry")) return;
                			var val = company.industry;
                			if ($.inArray(val, industries) != -1) return;
                			industries.push(val);
                		});
                		industries.sort(function(a, b) { return a.localeCompare(b); });
                		$(".module-results .button-dropdown ul li").remove();
                		if ($(".module-results .button-dropdown .copy").text() != $(".module-results .button-dropdown").attr("data-label")) {
                    		$("<li />").text($(".module-results .button-dropdown").attr("data-label")).appendTo(".module-results .button-dropdown ul"); 
                		}
                		$.each(industries, function(idx, val) { $("<li />").text(val).appendTo(".module-results .button-dropdown ul");  });
            			SGX.dropdowns.init(".module-results", SGX.screener.search.addtlCritSearch);

            			// display the rows
                		SGX.screener.search.displayRows(1, sort, direction);
                		
                	},
                	
                	getColumnValue: function(company, field) {
                		
                		var val = company.hasOwnProperty(field.field) ? company[field.field] : null;
                		if (val == null) return "-";
                		 
                		if (field.hasOwnProperty("formatter")) {
                			if (field.formatter.type == "round-list") {
                				val = val | 0;
                				val = field.formatter.values[val];
                			}

                			// needs a minimum value
                			if (field.formatter.hasOwnProperty("minField") && company.hasOwnProperty(field.formatter.minField)) {
                				if (company[field.formatter.minField] < field.formatter.minValue) val = null;
                			}
                			
                		}
                		
                		return val == null ? "-" : val;
                	},
                	
                	
                	getAllColumns: function() {
                		var ret = [ { field: "tickerCode", label: "Code", shortName: "Code", format: "string" }, { field: "industry", label: "Industry", shortName: "Industry", format: "string" } ];
            			$(".editSearchB .checkbox[data-display='true']").each(function(idx, el) { 
            				var field = {};
            				SGX.screener.populateField(field, el);
            				ret.push(field); 
            			});
                		return ret;
                	},
                	
                	
                	getDisplayColumns: function() {
                		return SGX.displayColumns;
                	},
                	
                	setDisplayColumns: function(columns) {
                		
                		SGX.displayColumns = columns;
                		SGX.displayColumns["companyName"] = true;
                		SGX.displayColumns["tickerCode"] = true;
                		SGX.displayColumns["industry"] = true;
                		
            			$(".module-results th, .module-results td").hide();
                		$(".module-results th, .module-results td").each(function() {
                			if (SGX.displayColumns.hasOwnProperty($(this).attr("data-name"))) $(this).show();
                		});
                		
                        SGX.screener.search.displayPage(1);
                	},
                	
                    displayRows: function(curPage, sort, direction) {

                    	var data = $(".module-results").data();
                    	var page = SGX.screener.search.sortRows(data, sort, direction);
                    	var paging = $('.module-results .pager');
                    	
                        paging.empty();
                        
                        if (data.companies.length > 0) {
                        	
                        	// page navigation
                        	for (var i=1; i<page; i++) {
                        		var navItem = $('<span class="action-btn">' + i + '</span>');
                        		paging.append(navItem);
                        	}                        	

                        	// prev/next
                            if (page > 1) {
                            	paging.prepend('<span class="action-btn prev">Prev</span>');
                            	paging.append('<span class="action-btn next">Next</span>');
                            }
                            
                            SGX.screener.search.displayPage(curPage);
                            
                        }
                        
                    },
                    
                    sortRows: function(data, sort, direction) {
                    	
                    	if (typeof direction === "undefined") direction = "asc";
                    	var sortType = $(".module-results").find("th[data-name='" + sort + "']").attr("data-sort");
                    	
                    	var field = null;
                    	if ($(".editSearchB .checkbox[data-name='" + sort + "']").length > 0) {
                        	field = {};
                        	SGX.screener.populateField(field, $(".editSearchB .checkbox[data-name='" + sort + "']"));
                    	}
                    	
                    	if (!data.hasOwnProperty("keywords")) {

                        	// sort
                        	data.companies.sort(function(a, b) {
                        		
                        		var a1 = "asc" == direction ? a[sort] : b[sort];
                        		var b1 = "asc" == direction ? b[sort] : a[sort];
                        		if ("number" == sortType) {
                        			if (typeof b1 === "undefined") b1 = "-99999999999999";
                        			if (typeof a1 === "undefined") a1 = "-99999999999999";
                        			return b1-a1;
                        		}
                        		else if ("lookup" == sortType && field != null) {
                    				a1 = "asc" == direction ? SGX.screener.search.getColumnValue(a, field).toLowerCase() : SGX.screener.search.getColumnValue(b, field).toLowerCase();
                    				b1 = "asc" == direction ? SGX.screener.search.getColumnValue(b, field).toLowerCase() : SGX.screener.search.getColumnValue(a, field).toLowerCase();
                        		}
                        		
                        		return a1.localeCompare(b1);
                        	});

                    	}
                    	
                    	
                    	// break into pages
                    	var i, j, page = 1;
                    	for (i=0, j=data.companies.length; i<j; i+=SGX.resultSize) {
                    		var tmp = data.companies.slice(i,i+SGX.resultSize);
                    		if (tmp.length == 0) continue;
                    		$.each(tmp, function(idx, obj) { obj.page = page;   });
                    		page++;
                    	}

                    	return page;
                    },
                    
                    displayPage: function(page) {

                    	var isFirst = (typeof $(".module-results tbody").attr("rendered") === "undefined") ? true : false;
                    	var data = $(".module-results").data();

                    	$(".module-results tbody").attr("rendered", "true");
                    	$(".module-results tbody").children().remove();
                    	$(".module-results .table-wrapper tbody").show();
                    	
            			var fields = SGX.screener.search.getAllColumns();
            			var defaultDisplay = SGX.screener.search.getDisplayColumns();
            			var tbodyData = "", fmtCache = {};

            			$(".module-results thead th").each(function(idx, el) {
                			var fmt = $(el).attr("data-format") == "millions" ? "number1" : $(el).attr("data-format");
                			fmtCache[$(el).attr("data-name")] = fmt;
                		});

            			// append the rows
                		$.each(data.companies, function(idx, company) {
                			
                			if (company.page != page) return;
                			
                			// the row
                			var tr = $("<tr />").addClass("result");
                			if (idx%2 == 0) $(tr).addClass("even");
                			
                			// company td
                			var td = $("<td />").addClass("companyName").attr("data-name", "companyName").appendTo(tr);
                			$(td).append('<a target="_parent" href="' + SGX.getCompanyPage(company.tickerCode) + '">' + company.companyName + '</a>');
                			
                			// rest of the rows
                			$.each(fields, function(fIdx, field) {
                				var val = SGX.screener.search.getColumnValue(company, field);
                				var formatted = SGX.formatter.getFormatted(fmtCache[field.field], val);
                				td = $("<td />").addClass(field.field).attr("data-name", field.field).text(typeof formatted === "undefined" ? val : formatted.replace("%", ""));
                				if (!defaultDisplay.hasOwnProperty(field.field)) $(td).addClass("hidden");
                				$(td).appendTo(tr);
                			});
                			
                			$(".module-results tbody").append(tr);
                			
                		});
                    	
                    	// handle the navigation
                    	var paging = $('.module-results .pager');
                    	$(".action-btn", paging).removeClass("inactive").unbind("click");
                    	
                    	// previous
                    	SGX.screener.search.pageButton($(".action-btn.prev", paging), page - 1 == 0 ? 1 : page - 1, page);
                    	
                    	// pages
                    	$(".action-btn", paging).not(".prev, .next").each(function(idx, el) { SGX.screener.search.pageButton($(el), parseInt($(el).text()), page); });

                    	// next
                    	var lastPg = parseInt($(".action-btn:not(.prev,.next):last", paging).text());
                    	SGX.screener.search.pageButton($(".action-btn.next", paging), lastPg == page ? page : page + 1, page);

                    	// resize, so many different ways to handle scroll
                    	var scroll = -1;
                    	
                    	// first load (scroll to top)
                    	if (isFirst) scroll = 0;
                    	// by indsutry
                    	else if ($(".related-page").length > 0) scroll = 30;
                    	// screener with advanced criteria being displayed
                    	else if ($(".screener-page").length > 0) scroll = 0;
                    	
        	            SGX.resizeIframe(SGX.getTrueContentHeight(), scroll);
        	            
                    },
                    
               		pageButton: function(el, page, cur) {
               			
            			$(el).attr('data-page', page);
            			
            			if (cur == page) {
            				$(el).addClass("inactive");
            			}
            			
            			$(el).click(function(e) {
                			e.preventDefault();
                			e.stopPropagation();
                			if (cur == page) return;
                			SGX.screener.search.displayPage(page);
            			});
            			
            		},
            		
            		customizeResults: function() {

            			var currentCols = SGX.screener.search.getDisplayColumns();
            			var container = $("<div />");
            			var table = $("<table />").addClass("customize-display-table").appendTo(container);
            			$("<tr />").append($("<td colspan='2' />").html("<h4>Customize Results Display</h4>")).appendTo(table);
            			
            			var tr = $("<tr />").appendTo(table), column, header = 0;
            			
            			$(".module-accordion").children().each(function(idx, el) {
            				
            				if ($(el).is("h3")) {
        						if ($(this).attr("data-display") != "true") return;
            					if (header%2 == 0) column = $("<td />").appendTo(tr);
            					$("<div class='heading' />").text($(el).text()).appendTo(column);
            					header++;
            					return;
            				}
            					
        					$(".checkbox", el).each(function() { 

        						if ($(this).attr("data-display") != "true") return;

                				// some fields can only display if it's being searched on
                				if (typeof $(this).attr("data-search-required") !== "undefined" && $(this).attr("data-search-required") == "true") {
                					if ($(".search-criteria [data-name='" + $(this).attr("data-name") + "']").length == 0) return;
                				}
        						
    							var chk = $(this).clone();
    							$(chk).removeClass("checked");
    							if (currentCols.hasOwnProperty($(chk).attr("data-name"))) $(chk).addClass("checked");
    							$(chk).appendTo(column);
    							
        					});

            			});

                        SGX.modal.open({
                            content: $(container).html(),
                            type: 'prompt',
                            confirm: function(settings) {
                            	var dcs = {};
                            	$(".modal-container .checkbox.checked").each(function() { dcs[$(this).attr("data-name")] = true; });
                            	SGX.screener.search.setDisplayColumns(dcs);
                            	SGX.modal.close();
                            },
                            postLoad: function(settings) {
                            	$(".modal-container .checkbox").click(function(e) {
        							if ($(this).is(".checked")) { $(this).removeClass("checked"); return; }
                            		if ($(this).closest("table").find(".checked").length >= 4) { alert("Please remove a column before adidng a new one."); return; }
                            		$(this).addClass("checked");
                            	});
                            }
                            
                        });

            		}
            		
                }
                
                

                
    		},   		
            
    		hideLoading: function() {
    			$('#loading').hide();
    		},
    		
    		showLoading: function() {
    			$('#loading').show();
    		},

    		accordion: function() {
                $(".module-accordion").accordion({
                    active: 0,
                    animated: 'easeOutExpo',
                    autoHeight: false,
                    collapsible: true,
                    event: 'click',
                });
            },
            
            handleAjaxRequest: function(endpoint, data, successFN, errorFN) {
            	
            	$.ajax({
                    url: endpoint,
                    type: 'GET',
                    dataType: 'jsonp',
                    data: { 'json': JSON.stringify(data) },
                    scriptCharset: "utf-8" , 
                    contentType: 'application/json; charset=UTF-8',           	
                    success: typeof successFN !== "undefined" ? successFN : SGX.genericAjaxSuccess,
                    error: typeof errorFN !== "undefined" ? errorFN : SGX.genericAjaxError
            	});
            	
            },
            
            getTrueContentHeight: function() {
            	return isAnyIE() ? getPropIE('Height') : $("body:first,html:first").height();
            },
            
            genericAjaxSuccess: function(data) {
            	alert("NO success method provided");
            	debug.log(data);
            },
            
            genericAjaxError: function(data, status, er) {
            	//alert("NO error method provided");
            	debug.log(status);
            	debug.log(data);
            	debug.log(er);
            },
            
            dateranges: {
            	
            	init: function(selector) {
            		
            		$(selector).each(function(idx, el) {
            			
                    	$(this).datepicker({
                    		minDate: 0,
                    		numberOfMonths: 1,
                    		minDate: $(this).attr("min-date"),
                    		maxDate: $(this).attr("max-date"),
                    		defaultDate: $(this).attr("default-date")
                    	});
            			
            		});


            	}
            	

            	
            },
            
            dropdowns: {
            	
                open: function(selector) {
                	$('.dropdown').closest(".button-dropdown").removeClass("open");
                	$(selector).addClass("open");
                	$("html").bind("click", SGX.dropdowns.close);
                },

                close: function(selector) {
                	$("html").unbind("click", SGX.dropdowns.close);
                	$('.dropdown').closest(".button-dropdown").removeClass("open");
                },
                
                init: function(selector, finished) {
                	
                	$('.button-dropdown', selector).unbind("click");
                	
                	$('.button-dropdown', selector).click(function(e) {

                    	e.preventDefault();
                    	e.stopPropagation()

                		var isOpen = $(this).hasClass("open");

                		if (isOpen) {
                			SGX.dropdowns.close('.button-dropdown');
                			return;
                		}
                		
                		SGX.dropdowns.open(this);
                		
                	});
                	
                    $('.button-dropdown li', selector).click(function(e) {
                    	
                    	e.preventDefault();
                    	e.stopPropagation()
                    	
                    	var dd = $(this).closest(".button-dropdown");
                    	var def = typeof $(dd).attr("data-label") !== "undefined" ? $(dd).attr("data-label") : "";
                    	var text = $(this).text();
                    	var dataName = typeof $(this).attr("data-name") !== "undefined" ? $(this).attr("data-name") : $(this).closest(".criteria, .additional-criteria").attr("data-name"); 
                    	
                    	if ($("ul li", dd).first().text() != def && text != def) {
                    		$("ul", dd).prepend($("<li />").text(def)).click(function(e) {
                            	e.preventDefault();
                            	e.stopPropagation()
                    			$(dd).find(".copy").text($(e.target).text());
                    			$(e.target).remove();
                    			SGX.dropdowns.close();
                    			if (typeof finished !== "undefined") finished();
                    		});
                    	}
                    	
                    	$(".copy", dd).text(text).attr("data-name", dataName);
                    	
                    	if (typeof finished !== "undefined") finished();
                    	
                    	dd.click();
                    	
                    });
                    
                }
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

                	var container = SGX.modal.container();
                	$(".content", container).html(settings.content);
                	$(container).addClass(settings.type);
                	
                	var cboxSettings = {
                		html: $(container),
                		overlayClose: false,
                		transition: 'none',
                		maxWidth: settings.hasOwnProperty("maxWidth") ? settings.maxWidth : 550,
                		onComplete: function() {
                			if (settings.hasOwnProperty("postLoad")) settings.postLoad(settings)
                			if (SGX.getParentURL() != null) {
                				SGX.resizeIframe(SGX.pageHeight, 10);
                				$("#colorbox").position();
                			}
                		},
                		onClose: function() {
                			if (settings.hasOwnProperty("close")) settings.close(settings);
                		}
                	};
                	
                	if (SGX.getParentURL() != null) cboxSettings.top = 100;
                	
                	$.colorbox(cboxSettings);

                	// cancel/close
                	$(".cancel, .close").click(function(e) {
                		if ($(this).hasClass("cancel") && settings.hasOwnProperty("cancel")) settings.cancel(settings);
                		SGX.modal.close(); 
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
            
            tooltip: {
            	
            	init: function(selector) {
            		
            		$(".glossary-item", selector).each(function(idx, el) {
            			
            			var id = $(el).attr("glossary-key");
            			
            			if (typeof id === "undefined") return;
            			
            			$.each(GLOSSARY, function(idx, term) {
            				if (term.id == id) {
            					$(el).attr("glossary-copy", term.definition);
            				}
            			});
            			
    					$(el).hover(SGX.tooltip.open, SGX.tooltip.close);
            			
            		});
            		
            	},
            	
            	open: function(event) {
            		
            		var el = $(event.target);
            		
            		var template = $(".tooltip").clone(true);
            		$(template).addClass("current");
            		$(template).appendTo("body");
            		$(".tooltip-content", template).html($(el).attr("glossary-copy"));
            		
                    var height = $(template).height(), width = $(template).width();
                    var left = $(el).offset().left - (width/2) + 10;
                    
                    if ($(el).hasClass("glossary-left")) left = $(el).offset().left - width + 25;
                    
                    $(template).css({
                        'top': $(el).offset().top - height - 12,
                        'left': left
                    });
                    
                    if ($(el).hasClass("glossary-left")) $(template).addClass("tooltip-left");
                    
                    $(template).fadeIn();
            		
            	},
            
                close: function(filter) {
                	$(".tooltip.current").remove();
                }
            },
            
            formatter: {
            	
                formatElements: function(selector) {
                	$(".formattable", selector).not("[formatted]").each(function(idx, el) { 
                		SGX.formatter.formatValue(el, $(el).attr("data-format")); 
                	});
                },

                formatValue: function(el, fmt) {
            		var val = SGX.formatter.getFormatted(fmt, $(el).text());
            		$(el).attr("formatted", "true").text(val);
                },

                getFormatted: function(fmt, value) {

                	if (typeof fmt === "undefined" || fmt == "string" || fmt == "lookup") return;
                	
            		var val = value;
            		if (val === "" || val === "-") return;
            		
            		var formatter = SGX.numberFormats.hasOwnProperty(fmt) ? SGX.numberFormats[fmt] : {};
            		
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
                }

            },
            
            
            init: function() {
            	
            	// google analytics
          	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
          		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
          		  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
          		  })(window,document,'script','//www.google-analytics.com/analytics.js','_gaTracker');
          		  _gaTracker('create', 'UA-50238919-1', { 'cookieDomain': 'none' });

          		// terms and conditions
            	$(".terms-conditions").click(function(e) { 
            		e.preventDefault();
            		e.stopPropagation();
            		SGX.showTerms();  
            	});
            	
            	// user guide
            	$(".user-guide").click(function(e) {
            		e.preventDefault();
            		e.stopPropagation();
            		SGX.showUserGuide();
            	});
            	
        		// screener
        		$(".screener-link").attr("target", "_parent").attr("href", SGX.getPage(SGX.screenerPage.id));
            	
            	// the page
            	var page = location.pathname;
            	if (page.indexOf(SGX.companyPage.file) != -1) SGX.company.init();
            	else if (page.indexOf(SGX.financialsPage.file) != -1) SGX.financials.init();
            	else if (page.indexOf(SGX.relatedPage.file) != -1) SGX.related.init();
            	else if (page.indexOf(SGX.printPage.file) != -1) SGX.print.init();
            	else if (page.indexOf(SGX.tradePage.file) != -1) SGX.trade.init();
            	else if (page.indexOf(SGX.termsPage.file) != -1) SGX.resizeIframe(1000, 0);
            	else SGX.screener.init();
            	
            },
            

            getParameterByName: function(name) {
                name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
                var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
                    results = regex.exec(location.search);
                return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
            },
            
            tabs: function() {
                $('.tabbed-content').tabs({
                    active: 0,
                    show: {
                        effect: "blind",
                        duration: 800
                    }
                });
            },
            
            company: {
            	
            	init: function() {
            		var code = SGX.getParameterByName("code");
            		var company = SGX.company.getCompany(code, SGX.company.loaded);
            	},
            	
            	getCompany: function(code, loadedFN) {
            		var endpoint = SGX.fqdn + "/sgx/company";
            		var params = { id: code };
            		SGX.handleAjaxRequest(endpoint, params, loadedFN, SGX.failed);
            	},
            	
            	loaded: function(data) {
            		
        			SGX.trackPage("SGX Company Profile - " + data.company.companyInfo.companyName);
            		
            		SGX.company.initSimple(data, true);

            		// init charts
            		var endpoint = SGX.fqdn + "/sgx/company/priceHistory";
            		var params = { id: data.company.companyInfo.tickerCode };
            		SGX.handleAjaxRequest(endpoint, params, function(sData) { SGX.company.initStockCharts(sData, SGX.company.defaultResize); });
            		
            		// all other sections
            		SGX.company.initHolders(data);
            		SGX.company.initConsensus(data);
            		SGX.company.initAlphaFactors(data);
            		SGX.company.initGTI(data);
            		
            		// hide/show
            		if (!data.company.companyInfo.hasOwnProperty("businessDescription")) $(".businessDescription").hide();
            		
            		// hide/show
            		if (!data.company.companyInfo.hasOwnProperty("companyWebsite")) $(".companyWebsite").hide();
            		
            		SGX.tooltip.init("body");
            		SGX.formatter.formatElements("body");
            		SGX.tabs();
            		
            		// resize/scroll
            		SGX.resizeIframe(SGX.getTrueContentHeight() + 50, 0);
            		
            	},
            	
            	defaultResize: function() {
            		SGX.resizeIframe(SGX.getTrueContentHeight() + 50, 0);
            	},
            	
            	installCompanyHeader: function() {
            		
            		var companyHeader="";
            		companyHeader += "<div class=\"row\">";
            		companyHeader += "<div class=\"grid_3\">";
            		companyHeader += "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"company-header\">";
            		companyHeader += "<tr>";
            		companyHeader += "<td class=\"page-title\">";
            		companyHeader += "<span class=\"property\" data-name=\"companyName\"><\/span> (<span class=\"property\" data-name=\"tickerCode\"><\/span>)";
            		companyHeader += "<\/td>";
            		companyHeader += "<td valign=\"bottom\">";
            		companyHeader += "<span class=\"stock-price\">";
            		companyHeader += "<div class=\"main\">";
            		companyHeader += "<span class=\"currency\"></span> <span class=\"lastPrice formattable\" data-format=\"number\">--<\/span>";
            		companyHeader += "&nbsp;";
            		companyHeader += "<span class=\"change\">--<\/span>";
            		companyHeader += "<\/div>";
            		companyHeader += "<div class=\"last-updated\">Last Price:&nbsp;";
            		companyHeader += "<span class=\"date\">&nbsp;";
            		companyHeader += "<span class=\"day\">--<\/span>&nbsp;";
            		companyHeader += "<span class=\"time\">--<\/span> SGT";
            		companyHeader += "<\/span>";
            		companyHeader += "<\/div>";
            		companyHeader += "<\/span>";
            		companyHeader += "<\/td>";
            		companyHeader += "<\/tr>";
            		companyHeader += "<\/table>";
            		companyHeader += "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"company-header\">";
            		companyHeader += "<tr>";
            		companyHeader += "<td class=\"breadcrumb\">";
            		companyHeader += "<span class=\"button back-button\"><a href=\"index.html\" class=\"screener-link\">Back to StockFacts<\/a><\/span>";
            		companyHeader += "<\/td>";
            		companyHeader += "<td class=\"breadcrumb right\">";
            		companyHeader += "<div class=\"print-button\">Download/Print<\/div>";
            		companyHeader += "<div class=\"button comparable-button\">Find comparable companies<\/div>";
            		companyHeader += "<\/td>";
            		companyHeader += "<\/tr>";
            		companyHeader += "<\/table>";
            		companyHeader += "<\/div>";
            		companyHeader += "<\/div>";
            		companyHeader += "<div class=\"row\">";
            		companyHeader += "<div class=\"grid_3\">";
            		companyHeader += "<span class=\"column\">";
            		companyHeader += "<p class=\"breadcrumb-tree\">";
            		companyHeader += "<span>Industry Tree:<\/span> <span class=\"dynamic\"><\/span>";
            		companyHeader += "<\/p>";
            		companyHeader += "<\/span>";
            		companyHeader += "<a class=\"back-company-profile\" href=\"#\">&laquo; Back to Company Profile<\/a>";
            		companyHeader += "<\/div>";
            		companyHeader += "<\/div>";
            		
            		$(".container_3:first").prepend($(companyHeader));
            		
            		// screener
            		$(".screener-link").attr("target", "_parent").attr("href", SGX.getPage(SGX.screenerPage.id));
            		
            	},
            	
            	initSimple: function(data, addHeader) {
            		
            		if (addHeader) SGX.company.installCompanyHeader();
            		
            		// simple properties
            		$(".property").each(function(idx, el) {
            			var name = $(el).attr("data-name");
            			if (typeof name === "undefined" || !data.company.companyInfo.hasOwnProperty(name)) return;
            			
            			if ($(el).is("a")) $(el).attr("href", "http://" + data.company.companyInfo[name])
            			else $(el).text(data.company.companyInfo[name]);
            			
            		});
            		
            		// industry tree
            		var tree = [];
            		if (data.company.companyInfo.hasOwnProperty("industryGroup")) tree.push({ type: "industryGroup", value: data.company.companyInfo.industryGroup });
            		if (data.company.companyInfo.hasOwnProperty("industry")) tree.push({ type: "industry", value: data.company.companyInfo.industry });
            		$.each(tree, function(idx, item) {
            			var a = $("<a target='_parent' href='" + SGX.getRelatedPage(data.company.companyInfo.tickerCode, "&action=industry&field=" + item.type + "&value=" + encodeURIComponent(item.value)) + "'>" + item.value + "</a>");
            			if (idx > 0) $(".breadcrumb-tree .dynamic").append($("<span />").html("&nbsp;>&nbsp;"));
            			$(".breadcrumb-tree .dynamic").append(a);
            		});

            		// financials
            		$(".view-financials").click(function(e) { 
            			window.top.location.href = SGX.getFinancialsPage(data.company.companyInfo.tickerCode);
            		});
            		
            		// company profile
            		$(".back-company-profile").attr("target", "_parent").attr("href", SGX.getCompanyPage(data.company.companyInfo.tickerCode));
            		
            		// comparable 
            		$(".comparable-button").click(function(e) {  
            			window.top.location.href = SGX.getRelatedPage(data.company.companyInfo.tickerCode, "&action=related");
            		});

            		// comparable 
            		$(".trade-button").click(function(e) {  
            			window.top.location.href = SGX.getTradePage(data.company.companyInfo.tickerCode);
            		});

            		// print
            		$(".print-button").click(function(e) {
            			var page = SGX.getPrintPage(data.company.companyInfo.tickerCode);
            			window.open(SGX.pqdn + encodeURIComponent(page));
            		});
            		
            		// init pricing
            		var endpoint = SGX.fqdn + "/sgx/price";
            		var params = { id: data.company.companyInfo.tickerCode };
            		SGX.handleAjaxRequest(endpoint, params, SGX.company.initPrice);
            		
            	},
            	
            	initPrice: function(data) {
            		
            		var dateField = data.price.hasOwnProperty("lastTradeTimestamp") ? data.price.lastTradeTimestamp : data.price.previousDate;
            		var date = Date.fromISO(dateField);
            		var price = data.price.hasOwnProperty("lastPrice") ? data.price.lastPrice : data.price.closePrice;
            		
            		$(".stock-price .currency").text(data.price.tradingCurrency);
            		$(".stock-price .change").text(data.price.change);
            		$(".stock-price .lastPrice").text(price);
            		$(".stock-price .last-updated .day").text($.datepicker.formatDate( "dd/M/yy", date));
            		$(".stock-price .last-updated .time").text(date.getHours() + ":" + String("00" + date.getMinutes()).slice(-2) + "");
            		
            		$(".stock-price").show();
            		
            		$(".stock-price span").removeAttr("formatted");
            		SGX.formatter.formatElements(".stock-price");


            	},
            	
            	initNews: function(finished) {
            		
            		SGX.company.loadNews(finished);

            	},
            	
            	loadNews: function(finished) {
            		
            		var chart = $('#area-chart').highcharts();
            		var start = new Date(chart.xAxis[0].min);
            		var end = new Date(chart.xAxis[0].max);
            		var div = $(".stock-events");
            		
            		var curStart = typeof $(div).attr("start-dt") === "undefined" ? new Date() : $(div).attr("start-dt");
            		var curEnd = typeof $(div).attr("end-dt") === "undefined" ? new Date() : $(div).attr("end-dt");
            		
            		if (start == curStart && end == curEnd) return;
            		
            		$(div).attr("start-dt", start).attr("end-dt", end);
            		var endpoint = SGX.fqdn + "/sgx/search/keydevs";
            		var params = { tickerCode: $("[data-name='tickerCode']").text(), from: Highcharts.dateFormat("%Y-%m-%e", start), to: Highcharts.dateFormat("%Y-%m-%e", end) };
            		
            		SGX.handleAjaxRequest(endpoint, params, function(data) { SGX.company.displayNews(chart, data, finished); });
            		
            		
            	},
            	
            	displayNews: function(chart, data, finished) {
            		
            		// just make it an empty array
            		if (!data.hasOwnProperty("keyDevs")) data.keyDevs = [];
            		
           			data.keyDevs = data.keyDevs.sort(function(a, b) { return Date.fromISO(b.date) - Date.fromISO(a.date); });
           			var seriesData = [];
           			
           			$(".stock-events ul li").remove();

           			// handle the HTML
           			$.each(data.keyDevs, function(idx, keyDev) {
           				
           				if (idx >= 10) return;
            				
        				// sidebar display
        				var letter = SGX.letters.substring(idx, idx+1);
        				var nId = 'keyDev-' + letter;
        				var icon = $("<div />").addClass("icon").text(letter); 
        				var link = $("<span />").text(keyDev.headline).attr("data-name", keyDev.date).attr("data-content", keyDev.situation).attr("data-name", nId).attr("data-dt", SGX.formatter.getFormatted("date", keyDev.date));
        				$("<li />").append(icon).append(link).appendTo(".stock-events ul");
        				$(link).click(function(e) {
        					var copy = "<h4>" + $(this).text() + "</h4><p class='bold'>From " + $(this).attr("data-dt") + "</p><div class='news'>" + $(this).attr("data-content") + "</div>";
                            SGX.modal.open({ content: copy, type: 'alert' });
        				});
        				
        				// for chart
        				var point = {
        					x: Date.fromISO(keyDev.date),
        					title: letter,
        					text: keyDev.headline,
        					shape: 'url(img/stock-marker.png)',
        					id: nId
        				};
        				seriesData.push(point);
        				
        			});

           			// now the chart
            		seriesData.sort(function(a, b) { return a.x - b.x;  });
            		chart.series[2].update({ data: seriesData });
            	
            		$(".stock-events").show();
            		
            		if (seriesData.length == 0) {
            			$("<li />").html($(".stock-events .no-data").html()).appendTo($(".stock-events ul")); 
            			
            		}
            		
            		
            		if (typeof finished !== "undefined") finished();
            		
            	},
            	
            	initAlphaFactors: function(data) {
            		
            		if (!data.hasOwnProperty("alphaFactors") || data.alphaFactors == null) {
            			$(".alpha-factors .no-factors").show();
            			return;
            		}
            		
        			$(".alpha-factors .has-factors").show();
            		
            		var factors = data.alphaFactors;
            		
            		$(".alpha-factors .slider").each(function(idx, el) {
            			
            			var name = $(this).attr("data-name");
            			
            			// no data
            			if (!factors.hasOwnProperty(name) || factors[name] == 0) {
            				$(this).hide();
            				return;
            			}
            			
            			$(this).click(function(el) {
            				window.top.location.href = SGX.getAlphasPage(name, factors[name], "&code=" + data.company.companyInfo.tickerCode);
            			});
            			
            			$(".bar-progress", this).addClass("per-" + (factors[name]*20));
            			
            			// set up glossary terms
            			$(".glossary-item", this).attr("glossary-key", name);
            			
            		});
            		
            		$(".alpha-factors").show();
            		
            		
            	},
            	
            	initConsensus: function(data) {
            		
            		var company = data.company.companyInfo;
            		
            		$(".progress-estimate").show();
            		
            		$(".theme-three-bubble-progress").addClass("opt-" + Math.round(company.avgBrokerReq));
            		
            		// no estimate to display
            		if (!company.hasOwnProperty("targetPriceNum")  || company.targetPriceNum < 3) {
            			$(".progress-estimate .no-estimate").show();
            			return;
            		}
            		
            		$(".progress-estimate .price").text(company.targetPrice);
            		$(".progress-estimate .has-estimate").show();
            		
            	},
            	
            	initHolders: function(data) {
            		
            		// holders
            		if (data.hasOwnProperty("holders") && data.holders.hasOwnProperty("holders") && data.holders.holders.length > 0) {
            			
            			data.holders.holders.sort(function(a, b) { return a.shares - b.shares; });
            			
            			$(".panel .owners tr:first").hide();
            			var percent = 0;
            			
             			$.each(data.holders.holders, function(idx, owner) {
            				var tr = $("<tr />").prependTo(".panel .owners");
            				if (idx%2 == 0) $(tr).addClass("even");
            				$("<td />").text(owner.name).addClass("property left").appendTo(tr);
            				$("<td />").text(owner.shares).addClass("property formattable right").attr("data-format", "number").appendTo(tr);
            				$("<td />").text(owner.percent).addClass("property formattable right").attr("data-format", "percent").appendTo(tr);
            				percent += owner.percent;
            			});
             			
             			$("[data-name='percentCommonStock']").text(percent);
            			
            		}
            		
            		$(".panel tr").each(function(idx, el) {
            			$("td:last", this).css("border", "0");
            		});
            		
            		
            	},
            	
            	initGTI: function(data) {
            		
            		var length = 0;
            		try { length = data.gtis.gtis.length; } catch(err) {}
            		
            		if (length == 0) {
            			$(".gti-block").hide();
            			return;
            		}
            		
            		$.each(data.gtis.gtis, function(idx, gti) {
            			var div = $("<div />").addClass("row");
            			if (idx%2 == 0) $(div).addClass("odd");
            			$("<span />").text(gti.issue.substring(3, 7)).appendTo(div);
            			$("<span />").text(gti.rank).appendTo(div);
            			$("<span />").text(gti.totalScore).appendTo(div);
            			$(div).appendTo(".gti-table");
            		});
            		
            	},
            	
            	chartData: [],
            	
            	initStockCharts: function(data, finishedDrawing) {
            		
            		// let's get all the price data set up
            		var priceData = SGX.company.toHighCharts(data.price);
            		var lowPrice = SGX.company.toHighCharts(data.lowPrice);
            		var openPrice = SGX.company.toHighCharts(data.openPrice);
            		var highPrice = SGX.company.toHighCharts(data.highPrice);
            		$.each(priceData, function(idx, point) {
            			var key = Highcharts.dateFormat("%e/%b/%Y", new Date(point.x));
            			SGX.company.chartData[key] = {}
            			SGX.company.chartData[key].close = point.y;
            			SGX.company.chartData[key].low = lowPrice[idx].y;
            			SGX.company.chartData[key].open = openPrice[idx].y;
            			SGX.company.chartData[key].high = highPrice[idx].y;
            		});
            		
            		// all the volume data
            		var volumeData = SGX.company.toHighCharts(data.volume);
            		
            		Highcharts.setOptions({ lang: { rangeSelectorZoom: "" }});
            		
                    $('#area-chart').highcharts('StockChart', {
                    	
                        colors: [ 'rgb(206, 217, 236)', '#BFCE00' ],
                        
                        chart: {
                        	backgroundColor:'rgba(255, 255, 255, 0.1)'
                        },
                        
                        plotOptions: {
                            series: {
                                animation: false
                            },
                            area: {
                            	lineColor: 'rgb(10, 63, 160)'
                            }
                        },
                        
            		    rangeSelector: {
            				inputEnabled: false,
            		        selected: 4,
                            buttons: [{
                                type: 'day',
                                count: 5,
                                text: '5d'
                            }, {
                                type: 'month',
                                count: 1,
                                text: '1m'
                            }, {
                                type: 'month',
                                count: 3,
                                text: '3m'
                            }, {
                                type: 'month',
                                count: 6,
                                text: '6m'
                            }, {
                                type: 'year',
                                count: 1,
                                text: '1y'
                            }, {
                                type: 'year',
                                count: 3,
                                text: '3y'
                            }, {
                                type: 'year',
                                count: 5,
                                text: '5y'
                            }, {
                                type: 'all',
                                text: 'All'
                            }]            		        
            		    },
            		    
            			credits: {
            	            enabled: false
            	        },
            	        
            	        tooltip: {
            	        	enabled: true,
            	        	formatter: SGX.company.getPointHTML,
                            useHTML: true,
                            crosshairs: [ true, true ],
                            shared: true
            	        },
            	        
            	        xAxis: {
            	        	
	        		        labels: {
	                            formatter: function() {
	                                return Highcharts.dateFormat("%e. %b", this.value);
	                            }
	        		        },
		    		        events: {
		    		        	afterSetExtremes: function(e) {
				                	SGX.company.loadNews(SGX.company.defaultResize);
		    		        	}
		    		        }
	        		        
                    	},
                    	
                        yAxis: [
                            {
	            		        title: undefined,
	            		        height: 170,
	            		        lineWidth: 2,
	            		        animation: false,
	            		        minRange: .001,
	            		        labels: {
		                            formatter: function() {
		                            	if (this.value == 0) return;
		                                return "S$ " + this.value;
		                            },
		                            style: {
		                            	color: "#000000",
		                            	fontWeight: "bold"
		                            },
		                            align: 'right',
		                            y: -3,
		                            x: -10
	            		        }
                            },
                            {
	            		        title: undefined,
                		        top: 250,
                		        height: 60,
                		        offset: 0,
                		        lineWidth: 2,
	            		        animation: false,
	            		        minRange: .01,
	            		        labels: {
		                            formatter: function() {
		                                return this.chart.yAxis[1].max + " mm";
		                            },
		                            style: {
		                            	color: "#000000",
		                            	fontWeight: "bold"
		                            },
		                            y: -45,
		                            x: -10
	            		        }
                            }
                        ],
                        
                        series: [
                            {
                            	name: 'Price',
                            	data: priceData,
                            	type: 'area',
                            	id: 'priceData',
                            	threshold: null,
                            	turboThreshold: 5000
                            	
                            },
                            {
                            	name: 'Volume',
                            	data: volumeData,
                            	type: 'column',
            		        	yAxis: 1,
                            	turboThreshold: 5000
                            },
                            {
                            	type: 'flags',
                                style: { 
                                	color: 'black',
                                	cursor: 'pointer'
                                },
                                events: {
                                	click: function(e) {
                                		$(".stock-events [data-name='" + e.point.id + "']").click();
                                	},
                                },
                                onSeries: 'priceData',
                                shape: 'circlepin',
                                width: 16,
                                y: -26,
                                data: []
                            }    
                        ],
                        
                        title: undefined,
                        
                        labels: {
                        	items: [
                        	    {
                                	html: "Price",
                                	style: {
                                    	top: '-22px',
                                    	left: '596px'
                                	}
                        	    },
                        	    {
                                	html: "Volume",
                                	style: {
                                    	top: '185px',
                                    	left: '582px'
                                	}
                        	    }
                        	],
            	        	style: {
            	        		color: "#666",
            	        		fontWeight: "bold"
            	        	}
                        }
                        
                    }, 
                    function() {
                    	SGX.company.initNews(finishedDrawing);
                    }
                    );
                    
            	},
            	
                toHighCharts: function(data) {
                	var ret = [];
                	$.each(data, function(idx, row) {
                		ret.push({ x: Date.fromISO(row.date).getTime(), y: row.value });
                	});
                	ret.sort(function(a, b) { return a.x - b.x; });
                	return ret;
                },
                
                getPointHTML: function() {
                	
                	if (!this.hasOwnProperty("points")) return;
                	
                	var key = Highcharts.dateFormat("%e/%b/%Y", this.points[0].x);
                	var point = SGX.company.chartData[key];
                	
                	var ret = "<b>" + Highcharts.dateFormat("%e/%b/%Y", this.points[0].x) + "</b>";

                	// not a trading day
                	if (point == undefined) {
                		ret += "<br />";
                		ret += "No trading data available.";
                		return ret;
                	}
                	
                	// is a trading day
                	ret += "<span class='chart-mouseover'>";
                	ret += "<br />";
                	ret += "<span>Open</span>: S$ " + point.open;
                	ret += "<br />";
                	ret += "<span>Close</span>: S$ " + point.close;
                	ret += "<br />";
                	ret += "<span>Low</span>: S$ " + point.low;
                	ret += "<br />";
                	ret += "<span>High</span>: S$ " + point.high;
                	ret += "<br />";

                	// no volume for this period
                	if (this.points.length <= 1) return ret;
 
                	// has volume too
                	ret += "<span>Volume</span>: " + this.points[1].y.toFixed(3) + " mm";
                	ret += "</span>";
                	
                	return ret;
                }
            	
            	
            },
            
            financials: {
            	
            	init: function() {
            		SGX.financials.registerPlugin();
            		var code = SGX.getParameterByName("code");
            		var company = SGX.company.getCompany(code, SGX.financials.loadedCompany);
            	},
            	
            	registerPlugin: function() {
            		
            	    /**
            	     * Experimental Highcharts plugin to implement chart.alignThreshold option.
            	     * Author: Torstein Hønsi
            	     * Last revision: 2013-12-02
            	     */
            	    (function (H) {
            	        var each = H.each;
            	        H.wrap(H.Chart.prototype, 'adjustTickAmounts', function (proceed) {
            	            var ticksBelowThreshold = 0,
            	                ticksAboveThreshold = 0;
            	            if (this.options.chart.alignThresholds) {
            	                each(this.yAxis, function (axis) {
            	                    var threshold = axis.series[0] && axis.series[0].options.threshold || 0,
            	                        index = axis.tickPositions && $.inArray(threshold, axis.tickPositions);

            	                    if (index !== undefined && index !== -1) {
            	                        axis.ticksBelowThreshold = index;
            	                        axis.ticksAboveThreshold = axis.tickPositions.length - index;
            	                        ticksBelowThreshold = Math.max(ticksBelowThreshold, index);
            	                        ticksAboveThreshold = Math.max(ticksAboveThreshold, axis.ticksAboveThreshold);
            	                    }
            	                });

            	                each(this.yAxis, function (axis) {
            	                    
            	                    var tickPositions = axis.tickPositions;

            	                    if (tickPositions) {

            	                        if (axis.ticksAboveThreshold < ticksAboveThreshold) {
            	                            while (axis.ticksAboveThreshold < ticksAboveThreshold) {
            	                                tickPositions.push(
            	                                    tickPositions[tickPositions.length - 1] + axis.tickInterval
            	                                );
            	                                axis.ticksAboveThreshold++;
            	                            }
            	                        }

            	                        if (axis.ticksBelowThreshold < ticksBelowThreshold) {
            	                            while (axis.ticksBelowThreshold < ticksBelowThreshold) {
            	                                tickPositions.unshift(
            	                                    tickPositions[0] - axis.tickInterval
            	                                );
            	                                axis.ticksBelowThreshold++;
            	                            }

            	                        }
            	                        axis.min = tickPositions[0];
            	                        axis.max = tickPositions[tickPositions.length - 1];
            	                    }
            	                });
            	            } else {
            	                proceed.call(this);
            	            }

            	        })
            	    }(Highcharts));            		
            		
            	},
            	
            	loadedCompany: function(data) {
            		
        			SGX.trackPage("SGX Company Profile - " + data.company.companyInfo.companyName);
            		
            		SGX.company.initSimple(data, true);
            		
            		$(".back-company-profile").show();
            		
            		$(".financials-viewport tbody").each(function() { $("tr:even", this).addClass("even"); }); 

            		var endpoint = SGX.fqdn + "/sgx/company/financials";
            		var params = { id: data.company.companyInfo.tickerCode };
            		SGX.handleAjaxRequest(endpoint, params, SGX.financials.loadedFinancials);
            		
            	},
            	
            	loadedFinancials: function(data) {
            		
            		if (!data.hasOwnProperty("financials")  || data.financials.length == 0) return;
            		
            		var financials = SGX.financials.cleanFinancials(data.financials); 
            		
            		// headings
            		$(".financials-viewport thead").each(function() {
            			
            			$("th", this).not(".unchart").not(":first").each(function(idx, item) {
            				if (idx >= financials.length) return;
            				var txt = financials[idx].absPeriod.indexOf("LTM") != -1 ? "LTM Ending" : financials[idx].absPeriod;
            				$(this).html(txt + "<br />" + $.datepicker.formatDate("dd/M/yy", Date.fromISO(financials[idx].periodDate)));
            				$(".data-point-container .currency").text(financials[idx].filingCurrency);
            			});
            			
            		});
            		
            		// loop through the td's
            		$(".financials-viewport td").has(".trigger").each(function(idx, el) {
            			
            			var name = $(".trigger", el).attr("data-name");

            			if (typeof name === "undefined") {
            				console.log($(".trigger", el).text());
            				return;
            			}
            			
            			// now get the siblings
            			$(el).siblings().not(".unchart").each(function(idx, td) {
               				if (idx >= financials.length) return;
               				var cur = financials[idx];
            				if (cur.hasOwnProperty(name)) $(td).text(cur[name]).attr("data-value", cur[name]).addClass("formattable").attr("data-format", "number");
            			});
            			
            		});
            		
            		$(".checkbox").click(function(e) {
            			
            			if ($(this).hasClass("checked")) {
            				SGX.financials.removeSeries(this);
            				return;
            			}
            			
        				SGX.financials.addSeries(this);
            			
            		});

        			SGX.tooltip.init("body");
            		SGX.formatter.formatElements("body");
            		SGX.resizeIframe(1760, 0);
            		
            	},
            	
            	cleanFinancials: function(financials) {
            		
            		var nancials = financials.slice();
            		
            		// let's make sure they're sorted
            		nancials.sort(function(a, b) {
                		var a = parseInt(a.absPeriod.replace("FY", "").replace("LTM", ""));
                		var b = parseInt(b.absPeriod.replace("FY", "").replace("LTM", ""));
                		return a - b;
                	});          		
                	
                	if (nancials.length == 5) return nancials;

            		// we need to decide whether to use the latest year end
            		// or quarter data
            		var isQ4 = nancials[nancials.length - 1].absPeriod.indexOf("LTM4") != -1;
            		nancials.splice(isQ4 ? nancials.length - 1 : 0, 1);  
            		
            		financials = nancials;
            		
            		return nancials;
            		
            	},
            	
            	addSeries: function(el) {
            		
            		if ($(".checked").length >= 5) {
            			
                        SGX.modal.open({
                            content: '<h4>Chart Company Financials <span>(Select up to 5)</h4><p>Only five data points can be charted at a time. Remove a data point before selecting a new one.</p>',
                            type: 'alert'
                        });
                        
            			return;
            		}
            		
        			$(el).addClass("checked");
        			
        			// get the series data
        			var seriesData = [], empty = 0;
        			var eventsConfig = { 
        					mouseOver: function() {
        						this.series.yAxis.update({ title: { style: { fontWeight: "bold" } }, labels: { style: { fontWeight: "bold" } } });
        					}, 
        					mouseOut: function() {
        						this.series.yAxis.update({ title: { style: { fontWeight: "normal" } }, labels: { style: { fontWeight: "normal" } } });
        					} 
        			};
        			$(el).closest("tr").children().not(".unchart").each(function(idx, td) {
        				if (idx == 0) return;
        				if (typeof $(td).attr("data-value") === "undefined") empty++;
        				var val = typeof $(td).attr("data-value") === "undefined" ? 0 : parseFloat($(td).attr("data-value"));
        				seriesData.push({ y: val, events: eventsConfig });
        			});
        			
        			// no data to plot
        			if (empty == seriesData.length) {
        				SGX.modal.open({
        					type: "alert",
        					content: "<p>No data available for this series.</p>"
        				});
                        $(el).removeClass("checked");
        				return;
        			}

        			$(el).closest("tr").find(".unchart").text("[ UNCHART ]").click(function() { $(this).closest("tr").find(".checkbox").click(); });
        			
        			var name = $(".trigger", el).attr("data-name");

        			// need to initialize
        			if ($(".chart-row").is(":hidden")) {
        				SGX.financials.initChart(el, seriesData);
        			}
        			else {
        				
            			// otherwise need to manipulate a bit
            			var chart = $('#large-bar-chart').highcharts();
            			
            			chart.addAxis({
    				    	id: name,
    				    	title: {
    				    		text: $(".trigger", el).text()
    				    	},
    				    	opposite: SGX.financials.hasLeftYAxis(),
        					labels: {
	                            formatter: function() {
	                            	if ($(".trigger", el).attr("data-format") == "cash") {
	                            		return Highcharts.numberFormat(this.value);
	                            	}
	                            	else if ($(".trigger", el).attr("data-format") == "percent") {
	                            		return this.value + "%";
	                            	}
	                                return Highcharts.numberFormat(this.value, 3);
	                            }
        					}
            			});

            			SGX.financials.getNextColor(chart.series);
            			
            			chart.addSeries({
    	                    name: $(".trigger", el).text(),
    	                    id: $(".trigger", el).attr("data-name") + "-series",
            				type: SGX.financials.getSeriesType($(".trigger", el).attr("data-group")),
    	                    data: seriesData,
    				    	color: SGX.financials.getNextColor(chart.series),
    	                    yAxis: name,
                            zIndex: SGX.financials.getSeriesType($(".trigger", el).attr("data-group")) == "line" ? 50 : 1
            			});
            			
                		chart.setSize(SGX.financials.getChartWidth(chart.series.length), SGX.financials.getChartHeight(), true);
                		
        			}
        			
        			// draw legend
        			SGX.financials.drawLegend();
        			
                	// resize
    	            var curHeight = SGX.getTrueContentHeight();
    	            SGX.resizeIframe(curHeight, 10);
            		
            	},
            	
            	getNextColor: function(series) {
            		var colors = [ '#565a5c', '#1e2171', '#BED600', '#0094B3', '#BF0052' ];
            		$.each(series, function(idx, s) {
            			colors = _.without(colors, s.color);
            		});
            		return colors[0];
            	},
            	
            	drawLegend: function() {

        			var chart = $('#large-bar-chart').highcharts();

        			$(".legend-note .items .item").remove();
        			
            		$.each(chart.series, function(idx, s) {
            			
            			var div = $("<span />").addClass("item");
            			$("<span />").addClass("color").css({ "background-color": chart.series[idx].color }).appendTo(div);
            			$("<span />").addClass("label").html(chart.series[idx].name).appendTo(div);
            			$("<span />").addClass("parent").html($(".financials-section [data-name='" + chart.series[idx].yAxis.userOptions.id + "']").closest("tbody").prev("thead").find("h4").text()).appendTo(div);
            			$(div).appendTo(".legend-note .items");

            		});
            		
            	},
            	
            	removeSeries: function(el) {
            		
            		var name = $(".trigger", el).attr("data-name");
            		var chart = $('#large-bar-chart').highcharts();
            		
            		$(el).removeClass("checked");
            		$(el).closest("tr").find(".unchart").text("");

            		// remove the entire chart
            		if (chart.series.length == 1) {

            			chart.destroy();
            			$(".chart-row").hide();
            			
                    	// resize
            			SGX.resizeIframe(1730, 0);
            			
            			return;
            		}
            		
            		// remove the series
            		chart.get(name).remove();
            		chart.setSize(SGX.financials.getChartWidth(chart.series.length - 1), SGX.financials.getChartHeight(), true);

        			// draw legend
        			SGX.financials.drawLegend();

            		
            	},
            	
            	initChart: function(el, seriesData) {
            		
        			var categories = [];
            		$(".financials-viewport thead:first th").not(".title").each(function() { 
            			categories.push($(this).html()); 
            		});

            		$(".chart-row").show();
            		
            		var chart = $('#large-bar-chart').highcharts({
            			
            			chart: {
            				alignThresholds: true,
            				width: SGX.financials.getChartWidth(1),
            				height: SGX.financials.getChartHeight()
            			},
            			plotOptions: {
            				allowPointSelect: true,
            				line: {
                				marker: {
                                    enabled: true,
                					states: {
                						hover: {
                							enabled: false
                						},
                						select: {
                							enabled: false
                                        }
                					}
                				}
            				}
            			},
            			legend: {
            				enabled: false
            			},
            			title: undefined,
            			credits: {
            	            enabled: false
            	        },
    					tooltip: {
    						useHTML: true
    					},
            			yAxis: [
            			    {
        						id: $(".trigger", el).attr("data-name"),
            					title: {
            						text: $(".trigger", el).text()
            					}, 
            					labels: {
		                            formatter: function() {
		                            	if ($(".trigger", el).attr("data-format") == "cash") {
		                            		return Highcharts.numberFormat(this.value);
		                            	}
		                            	else if ($(".trigger", el).attr("data-format") == "percent") {
		                            		return this.value + "%";
		                            	}
		                                return Highcharts.numberFormat(this.value, 3);
		                            }
            					}
            				}
            			],
            			series: [
            			    {
                				type: SGX.financials.getSeriesType($(".trigger", el).attr("data-group")),
            			    	id: $(".trigger", el).attr("data-name"),
                                name: $(".trigger", el).text(),
            					color: SGX.financials.getNextColor([]),
                                data: seriesData,
                                zIndex: SGX.financials.getSeriesType($(".trigger", el).attr("data-group")) == "line" ? 50 : 1
                            }
                        ],
                        xAxis: {
                        	id: $(".trigger", el).attr("data-name") + "-axis",
                            gridLineColor: 'none',
                            categories: categories,
                            plotBands: {
                                color: '#e2e2e2',
                                from: 0
                            },
            	        	labels: {
            	        		useHTML: true
            	        	}
                        }
            		});
            			
            		return chart;
            	},
            	
            	getChartWidth: function(seriesCount) {
            		var div = $("#large-bar-chart");
            		var width = (seriesCount * parseInt($(div).attr("series-increment"))) + parseInt($(div).attr("default-width"));
            		return width;
            	},
            	
            	getChartHeight: function() {
            		var height = parseInt($("#large-bar-chart").attr("default-height"));
            		return height;
            	},
            	
            	getSeriesType: function(group) {
            		var groups = [];
            		groups["pure"] = "column";
            		groups["per"] = "line";
            		groups["ratio"] = "column";
            		return groups[group];
            	},
            	
            	hasLeftYAxis: function() {
            		var chart = $('#large-bar-chart').highcharts();
            		if (!chart.hasOwnProperty("yAxis")) return false;
            		var ret = false;
            		$.each(chart.yAxis, function(idx, axis) {
            			if (!axis.opposite) ret = true;
            		});
            		return ret;
            	}
            	
            },
            
            related: {
            	
            	init: function() {
            		var code = SGX.getParameterByName("code");
            		var company = SGX.company.getCompany(code, SGX.related.loadedCompany);
            	},
            	
            	loadedCompany: function(data) {
            		
            		SGX.company.initSimple(data, true);
            		
            		$(".back-company-profile").show();
            		
            		var action = SGX.getParameterByName("action");
            		
            		if (action == "") {
            			SGX.failed();
            			return;
            		}
            		
            		// show the elements for the page
            		$("." + action).show();

            		var pageTitle = "Related Companies";

            		// handle the search
            		if (action == "related") SGX.related.handleRelated(data.company.companyInfo.tickerCode, data);
            		else if (action == "industry") SGX.related.handleIndustry(data);

            	},
            	
            	handleRelated: function(ticker, data) {
        			SGX.trackPage("SGX Related Companies (Comparative) - " + data.company.companyInfo.companyName);
            		var endpoint = SGX.fqdn + "/sgx/company/relatedCompanies";
            		var params = { id: ticker };
            		$(".pager").hide();
            		
            		SGX.handleAjaxRequest(endpoint, params, function(data) { SGX.screener.search.renderResults(data); SGX.resizeIframe(SGX.getTrueContentHeight(), 0);   });            		
            	},
            	
            	handleIndustry: function(data) {

        			SGX.trackPage("SGX Related Industries (Industry) - " + data.company.companyInfo.companyName);

            		var field = SGX.getParameterByName("field");
            		var value = SGX.getParameterByName("value");
            		
            		if (field == "" || value == "") {
            			SGX.failed();
            			return;
            		}
            		
            		// dynamic copy
            		$(".page-subtitle.industry").text($(".page-subtitle.industry").text() + " \"" + value + "\"");
            		
            		var param = { "field": field, "value": value };
            		var params = [ param ];
            		
            		var endpoint = SGX.fqdn + "/sgx/search";
            		var qs = {};
            		qs.criteria = params;
            		SGX.handleAjaxRequest(endpoint, qs, function(data) { SGX.screener.search.renderResults(data); SGX.resizeIframe(SGX.getTrueContentHeight(), 0);   }, SGX.screener.search.fail);
            		
            	}
            	
            },
            
        	failed: function() {
        		window.top.location.href = SGX.getPage(SGX.screenerPage);
        	},
        	
            trade: {
            	
            	init: function() {
            		var code = SGX.getParameterByName("code");
        			SGX.company.getCompany(code, SGX.trade.companyLoaded);
            	},
            	
            	companyLoaded: function(data) {

        			SGX.trackPage("SGX Go To Trade (" + data.company.companyInfo.companyName + ")");
            		$(".company-row").show();
            		SGX.company.initSimple(data, true);
            		$(".back-company-profile").show();            		
            		SGX.resizeIframe(620, 0);
            		
            	}
            	
            },        	
        	
        	print: {
        		
        		init: function() {
        			$("body").attr("loaded-items", 0)
            		var code = SGX.getParameterByName("code");
            		var company = SGX.company.getCompany(code, SGX.print.loadedCompany);        			
        		},
        	
        		loadedCompany: function(data) {
        			
        			// basic company data
        			SGX.company.initSimple(data, false);

            		// init charts
            		var endpoint = SGX.fqdn + "/sgx/company/priceHistory";
            		var params = { id: data.company.companyInfo.tickerCode };
            		SGX.handleAjaxRequest(endpoint, params, function(sData) {  SGX.company.initStockCharts(sData, function() { SGX.print.loadedItems(data); }); });
        			
            		// all other sections
            		SGX.company.initHolders(data);
            		SGX.company.initConsensus(data);
            		SGX.company.initAlphaFactors(data);
            		SGX.company.initGTI(data);
            		
            		// hide/show
            		if (!data.company.companyInfo.hasOwnProperty("businessDescription")) $(".businessDescription").hide();
            		
            		// hide/show
            		if (!data.company.companyInfo.hasOwnProperty("companyWebsite")) $(".companyWebsite").hide();

            		// handle financials
            		var endpoint = SGX.fqdn + "/sgx/company/financials";
            		var params = { id: data.company.companyInfo.tickerCode };
            		SGX.handleAjaxRequest(endpoint, params, function(fData) { SGX.print.loadFinancials(fData, data); });
            		
            		// handle TOS
            		$(".terms").load("terms-conditions.html .grid_3", function(x, y, z) {
            			SGX.print.loadedItems(data);
            		})
        			
        		},
        		
        		loadFinancials: function(data, cData) {

            		if (!data.hasOwnProperty("financials")  || data.financials.length == 0) {
                		SGX.print.loadedItems(cData);
            			return;
            		}

            		var financials = SGX.financials.cleanFinancials(data.financials); 
            		
            		// headings
            		$(".financials-section thead").each(function() {
            			
            			$("th", this).not(".unchart").not(":first").each(function(idx, item) {
            				if (idx >= financials.length) return;            				
            				var txt = financials[idx].absPeriod.indexOf("LTM") != -1 ? "LTM Ending<br />" + $.datepicker.formatDate("dd/M/yy", Date.fromISO(financials[idx].periodDate)) : financials[idx].absPeriod.replace("FY", "FY  ");
            				$(this).html(txt);
            				$(".financials-section .currency").text(financials[idx].filingCurrency);
            			});
            			
            		});
            		
            		// loop through the td's
            		$(".financials-section tbody tr").each(function(idx, el) {
            			
            			var name = $("td:first", el).attr("data-name");
            			
            			if (typeof name === "undefined") return;
            			
            			$("td", el).not(":first").each(function(i, td) {
            				if (i >= financials.length) return;            				
            				var cur = financials[i];
            				if (cur.hasOwnProperty(name)) $(td).text(cur[name]).attr("data-value", cur[name]).addClass("formattable").attr("data-format", "number");
            			});
            			
            		});
        			
            		$(".financials-section .statistics tbody tr:even").addClass("even");
            		$(".panel tbody tr").removeClass("even");
            		$(".panel tbody tr:even").addClass("even");
            		
            		SGX.print.loadedItems(cData);
        			
        		},
        		
        		loadedItems: function(cData) {
        			
        			$("body").attr("loaded-items", parseInt($("body").attr("loaded-items")) + 1);
        			if ($("body").attr("loaded-items") != 3) return;
        			
            		SGX.formatter.formatElements("body");
        			
            		$("body").attr("pdf-name", cData.company.companyInfo.tickerCode + "-" + new Date().getTime() + ".pdf");
        			SGX.trackPage("SGX Print Company Profile - " + cData.company.companyInfo.companyName);

        			setTimeout(function() { document["pdf-name"] = $("body").attr("pdf-name"); }, 100);
        			
        		}
        		
        	}
        	

            
    };    
    
    SGX.init();    

});