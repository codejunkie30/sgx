// This is the modular wrapper for any page
define(['jquery', 'underscore', 'jquicore', 'jquiwidget', 'jquimouse', 'jquidatepicker', 'accordion', 'slider', 'tabs', 'debug', 'highstock', 'colorbox'], function($, _, SGX) {
    // Nested namespace uses initial caps for AMD module references, lowercased for namespaced objects within AMD modules
    // Instead of console.log() use Paul Irish's debug.log()
	
    SGX = {
    		
    		fqdn : "http://ec2-54-82-16-73.compute-1.amazonaws.com",
    		
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
    		
    		screenerPage: "index.html",
    		
    		companyPage: "company-tearsheet.html",
    		
    		financialsPage: "financials.html",
    		
    		relatedPage: "related.html",
    		
    		alphasPage: "alpha-factor.html",
    		
            parentURL: null,
            
            pageHeight: $(document).height(),
            
            numberFormats: {
            	millions: { header: "in S$ mm", decimals: 1, format: "S$ $VALUE mm" },
            	percent: { header: "in %", decimals:2, format: "$VALUE%" },
            	number: { header: "", decimals: 3 },
            	number1: { decimals: 1 }
            },
            
            getParentURL: function() {
            	if (SGX.parentURL != null) return SGX.parentURL;
            	if (typeof document.location.hash !== "undefined" && document.location.hash != "") {
                	SGX.parentURL = document.location.hash.replace(/^#/, '');
            	}
            	return SGX.parentURL;
            },

            getPage: function(page) {
            	var parentURL = SGX.getParentURL();
            	if (parentURL == null) return page;
            	return page + "#" + SGX.getParentURL();
            },
                        
            getCompanyPage: function(code, extra) {
            	return SGX.getPage(SGX.companyPage + "?code=" + code + (typeof extra === "undefined" ? "" : extra));
            },
            
            getFinancialsPage: function(code, extra) {
            	return SGX.getPage(SGX.financialsPage + "?code=" + code + (typeof extra === "undefined" ? "" : extra));
            },

            getRelatedPage: function(code, extra) {
            	return SGX.getPage(SGX.relatedPage + "?code=" + code + (typeof extra === "undefined" ? "" : extra));
            },
            
            getAlphasPage: function(factor, quintile, extra) {
            	return SGX.getPage(SGX.alphasPage + "?factor=" + factor + "&quintile=" + quintile + (typeof extra === "undefined" ? "" : extra));
            },

            resizeIframe: function(height, scroll) {
            	var fn = function() {
            		SGX.pageHeight = height;
            		var msg = height; //height + "-" + ((typeof scroll === "undefined") ? "0" : scroll);
            		XD.postMessage(msg, SGX.getParentURL(), parent); 
            	};
            	setTimeout(fn, 10);
            },
            
            showTerms: function() {
            	
            	$.get("terms-conditions.html?mike=2", function(data) {
            		try {
                        SGX.modal.open({ content: data, type: 'alert', maxWidth: 1000 });            		
            		}
            		catch(err) {
            			console.log(err);
            		}
            	});
            	
            },
    		
    		screener: {

        		init: function() {
        			
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
        			
        			$(".editSearchB .button-reset").click(function(e) {
        				SGX.screener.criteriaChange.reset();
        			});
        			
        			$(".searchbar input").keypress(function(e) {
        				if (e.which == 13) SGX.screener.criteriaChange.reset(function() { SGX.screener.search.nameSearch($(".searchbar input").val()); });
        			});

        			$(".screener-header .search-submit").click(function(e) {
        				SGX.screener.criteriaChange.reset(function() { SGX.screener.search.nameSearch($(".searchbar input").val()); });
        			});

        			
        			$(".screener-header .button.all-companies").click(function(e) {
        				SGX.screener.criteriaChange.reset(function() { SGX.screener.search.showAll(); });
        			});
        			
            		
            		$(".expand-criteria").click(function(e) {
            			$(".advanced-criteria").show();
            			$(".expand-criteria").hide();
            		});
            		
        			SGX.screener.initCriteria();
        			
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
                	
                	var runsearch = false;
                	
                	if (data.distributions.length > 1) {
                    	data.distributions.sort(function(a, b) {
                    		var a = parseInt($(".editSearchB [data-name='" + a.field + "']").attr("data-order"));
                    		var b = parseInt($(".editSearchB [data-name='" + b.field + "']").attr("data-order"));
                        	if (a < b) return -1;
                        	if (a > b) return 1;
                        	return 0;
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

                            var criteria = $(this).closest(".criteria");
                            var cName = $(".info", criteria).text();
                            
                            SGX.modal.open({
                                content: '<p>Do you want to remove ' + cName + ' from your search?</p>',
                                type: 'prompt',
                                target: criteria,
                                confirm: function(options) {
                                    var target = options.target;
                                    var finished = function() { SGX.modal.close(); }
                                    SGX.screener.criteriaChange.removeCriteria(options.target, finished);
                                }
                            });
                            
                            
                		});
                		
                		SGX.tooltip.init(template);
                		
                	});
                	
                	$(".search-criteria .criteria").removeClass("even");
                	$(".search-criteria .criteria:even").addClass("even");
                	SGX.formatter.formatElements(".search-criteria");
                	
                	return runsearch;                	
                },
                
                drawCriteriaChange: function(distribution) {
                	
                	SGX.hideLoading();

                	var template = $("#criteria-templates [data-template='change']").clone(true);

        			
                	$(".checkbox", template).unbind("click");
        			$(".checkbox", template).click(function(e) {

            			e.preventDefault();
            			e.stopPropagation();        				

                		// handle % change
            			var html = $(".change-picker").html();

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
                            	
                        		SGX.modal.close(); 
                        		
                    			SGX.screener.search.criteriaSearch();
                        		
                            }
                            
                        });            			
        				
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
                	
                	distribution.buckets.sort(function(a, b) {
                		var a = a.key, b = b.key;
                    	if (a < b) return -1;
                    	if (a > b) return 1;
                    	return 0;
                	});
                	
                	$.each(distribution.buckets, function(idx, bucket) {
                		$(dd).append($("<li />").text(bucket.key));
                	});
                	
                	$(".search-criteria tbody").append(template);
                	
                	SGX.dropdowns.init(template, SGX.screener.search.criteriaSearch);
                	
                    return template;
                	
                },
                
                drawCriteriaSlider: function(distribution) {
                	
                	distribution.min = distribution.buckets[0].from;
                	distribution.max = distribution.buckets[distribution.buckets.length - 1].to;
                	var matches = SGX.screener.getDistributionMatches(distribution, distribution.min, distribution.max) + ' matches';
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
                	$.each(distribution.buckets, function(idx, bucket) {
                		if ((startVal >= bucket.from && endVal <= bucket.to) || bucket.from >= startVal) {
                			ret += bucket.count;
                		}
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
                            min: parseFloat(distribution.min, 10),
                            max: parseFloat(distribution.max, 10),
                            values: [ distribution.min, distribution.max ],
                            slide: SGX.screener.criteriaSlider.slide,
                            stop: SGX.screener.search.criteriaSearch,
                            step: 1
                        });
                        
                        /**
                        var steps = parseInt($(".slider-bar", container ).slider( "option", "step" ));
                        var left = $(".ui-slider-handle", container).first().position().left;
                        var right = $(".ui-slider-handle", container).last().position().left;
                        var bWidth = ((right - left) / steps) - 1;

                        for (var i=0; i<steps; i++) {
                        	
                        	var matches = SGX.getDistributionMatches(distribution, i, i + bWidth + 1 );
                        	var result = matches == 0 ? 4 : Math.round((matches / 100) * distribution.max) + 4;
                        	result = (result / 100) * 16;
                        	if (result > 16) result = 16;
                        	var div = $("<span />").css( { "z-index": -100, "border-right": "1px solid #FFF", width: bWidth, height: result, background: "#1e2171", display: "inline-block" });
                        	$('.stock-bar-container', container).append(div);
                        }
                        */
                        
                    },
                    
                    slide: function(event, ui) {
                    	
                    	// get the parent element
                    	var template = $(event.target).closest(".criteria");
                    	var distribution = $(template).data();
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
                	
                	nameSearch: function(val) {
                		var endpoint = SGX.fqdn + "/sgx/search/name";
                		SGX.screener.search.simpleSearch(endpoint, { search: val });
                	},
                
                	showAll: function() {
                		var endpoint = SGX.fqdn + "/sgx/search/";
                		SGX.screener.search.simpleSearch(endpoint, { criteria: [] });
                	},
                	
                	simpleSearch: function(endpoint, params) {
                		SGX.showLoading();
                		$(".advanced-criteria").hide();
                		$(".expand-criteria").show();
                		SGX.handleAjaxRequest(endpoint, params, function(data) { SGX.screener.search.renderResults(data); SGX.hideLoading(); }, SGX.screener.search.fail);
                	},
                	
                	addtlCritSearch: function() {
                		SGX.screener.search.fullSearch(false);
                	},
                	
                	criteriaSearch: function() {
                		SGX.screener.search.fullSearch(true);
                	},
                	
                	fullSearch: function(resetAddtl) {
                		
                		var endpoint = "/sgx/search";
                		var params = [];
                		
                		// reset any refine options
                		if (resetAddtl) SGX.screener.criteriaChange.resetAdditionalCriteria();
                		
                		$(".search-criteria .criteria, .additional-criteria").each(function(idx, el) {
                			
                			var name = $(this).attr("data-name");
                			
                			param = { field: name };
                			
                			// handle slider criteria
                			if ($(".slider-bar", this).length > 0) {
                				var sliderVals = $(".slider-bar", this).slider("values");
                				param.from = sliderVals[0];
                				param.to = sliderVals[1];
                			}
                			// dropdown
                			else if ($(".button-dropdown", this).length > 0) {
                				var dd = $(".button-dropdown", this);
                				var copy = $(".trigger .copy", dd).text();
                				if (dd.attr("data-label") == copy) return;
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
                		debug.log(xhr);
                		debug.log(ajaxOptions);
                		debug.log(thrownErr);
                	},
                	
                	renderResults: function(data) {

                		
                		// reset name input
                		$(".searchbar input").val("");
                		
                		if (data.companies.length == 0) {
                    		$(".no-results-display").show();
                    		$(".results-display").hide();
                		}
                		else {
                    		$(".results-display").show();
                    		$(".no-results-display").hide();
                		}

                		$(".module-results .results").text(data.companies.length + " results");
                		$(".module-results tbody").children().remove();
                		
                		// all other fields
            			var fields = SGX.screener.search.getAllColumns();
            			var defaultDisplay = SGX.screener.search.getDisplayColumns();
            			var sort = "companyName";
            			var direction = "asc";

            			// add headers (if hasn't happened already)
            			if ($(".module-results thead th").length == 0) {
            				
                    		var header = $(".module-results thead");
                    		$("<th data-name='companyName' data-sort='string'><span>Company Name</span></th>").addClass("sort").addClass("asc").addClass("companyName").appendTo(header);
                    		
                    		$.each(fields, function(idx, field) {
                    			var title = field.shortName;
                    			var th = $("<th />").attr("data-name", field.field).addClass(field.field).appendTo(header);
                    			if (field.format != "string" && field.format != "lookup") $(th).attr("data-sort", "number")
                    			$(th).attr("data-format", field.format);
                    			$("<span />").text(title).appendTo(th);
                    			if (SGX.numberFormats.hasOwnProperty(field.format)) $("<span />").addClass("fmt").text(SGX.numberFormats[field.format].header).appendTo(th);
                    			if (!defaultDisplay.hasOwnProperty(field.field)) th.addClass('hidden');
                    		});
                    		
                    		$(".module-results thead th").click(function(e) {
                    			
                				var table = $(".module-results table");
                				$("th", table).removeClass("sort");

                				var sort = $(this).hasClass("asc") ? "asc" : "desc";
                				
                				$("th", table).removeClass("desc").removeClass("asc");
                				
                				if (sort == "desc") sort = "asc";
                				else sort = "desc";
                				
                				$(this).addClass("sort").addClass(sort);

                        		SGX.screener.search.displayRows($(this).attr("data-name"), sort);
                                
                			});

            			}
            			else {
            				sort = $("th.sort").attr("data-name");
            				direction = $("th.sort").hasClass("desc") ? "desc" : "asc";
            			}
            			
            			var tbodyData = "", fmtCache = {};
                		$(".module-results thead th").each(function(idx, el) {
                			var fmt = $(el).attr("data-format") == "millions" ? "number1" : $(el).attr("data-format");
                			fmtCache[$(el).attr("data-name")] = fmt;
                		});
                		
                		$(".module-results").data(data.companies);
                		
                		var industries = [];
                		$.each(data.companies, function(idx, company) {
                			
                			// for IE8 support need to clean up
                			tbodyData += '<tr class="result"><td class="companyName" data-name="companyName" data-value="' + company.companyName.toLowerCase() + '">';
                			tbodyData += '<a href="' + SGX.getCompanyPage(company.tickerCode) + '">' + company.companyName + '</a>';
                			tbodyData += "</td>";
                			
                			$.each(fields, function(fIdx, field) {
                				
                				// the column
                				var val = SGX.screener.search.getColumnValue(company, field);
                				var formatted = SGX.formatter.getFormatted(fmtCache[field.field], val);
                				if (field.sortType == "string") val = val.toLowerCase();
                				else if (field.sortType == "number" && val == "-") val = "-9999999999";
                				tbodyData += '<td class="' + field.field;
                				if (!defaultDisplay.hasOwnProperty(field.field)) tbodyData += ' hidden';
                				tbodyData += '" data-value="' + val + '" data-name="' +  field.field + '">' + (typeof formatted === "undefined" ? val : formatted) + '</td>';
                				
                				// if industry
                				if (field.field == "industry") {
                        			if ($.inArray(val, industries) != -1) return;
                        			industries.push(val);
                				}

                				
                			});
                			
                			tbodyData += '</tr>';
                			
                		});

                		// append the industries
                		industries.sort(function(a, b) { return a.localeCompare(b); });
                    	$.each(industries, function(idx, val) { $("<li />").text(val).appendTo(".module-results .button-dropdown ul");  });

                		
                		$(".module-results tbody").html(tbodyData);

            			SGX.dropdowns.init(".module-results", SGX.screener.search.addtlCritSearch);
                		SGX.screener.search.displayRows(sort, direction);
                		
                		return;
                	},
                	
                	getColumnValue: function(company, field) {
                		
                		var val = company.hasOwnProperty(field.field) ? company[field.field] : null;
                		if (val == null) return "-";
                		 
                		if (field.hasOwnProperty("formatter")) {
                			if (field.formatter.type == "round-list") {
                				val = val | 0;
                				val = field.formatter.values[val];
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
                	
                    displayRows: function(sort, direction) {
                    	
                    	var paging = $('.module-results .pager');
                        paging.empty();
                        
                        var resultRows = $('.module-results tr.result');
                        var page = SGX.screener.search.sortRows($(resultRows).closest("table"), sort, direction);
                        
                        if (resultRows.length > 0) {
                        	
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
                            
                            SGX.screener.search.displayPage(1);
                            
                        }
                        
                    },
                    
                    sortRows: function(table, sort, direction) {
                    	
                    	var rows = $("tbody tr", table);
                    	
                    	if (rows <= 1) return;
                    	
                    	if (typeof direction === "undefined") direction = "asc";
                    	var sortType = $(table).find("th[data-name='" + sort + "']").attr("data-sort");
                    	
                    	// sort
                    	rows.sort(function(a, b) {
                    		
                    		var a1 = $("[data-name='" + sort + "']", "asc" == direction ? a : b).attr("data-value");
                    		var b1 = $("[data-name='" + sort + "']", "asc" == direction ? b : a).attr("data-value");

                    		if ("number" == sortType) {
                    			a1 = parseFloat(a1);
                    			b1 = parseFloat(b1);
                    			return b1-a1;
                    		}
                    		
                    		return a1.localeCompare(b1);
                    		
                    	});
                    	
                    	// break into pages
                    	var i, j, page = 1;
                    	for (i=0, j=rows.length; i<j; i+=SGX.resultSize) {
                    		var tmp = rows.slice(i,i+SGX.resultSize);
                    		if (tmp.length == 0) continue;
                    		$(tmp).attr("data-page", page);
                    		page++;
                    	}

                    	// reappend in order
                    	$(table).children('tbody').append(rows);

                    	return page;
                    },
                    
                    displayPage: function(page) {
                    	
                    	// display the results
                    	$(".module-results .table-wrapper tbody tr").hide();
                    	$(".module-results .table-wrapper tbody").show();
                    	$(".module-results .table-wrapper tbody tr[data-page='" + page + "']").removeClass("even");
                    	$(".module-results .table-wrapper tbody tr[data-page='" + page + "']:even").addClass("even");
                    	$(".module-results .table-wrapper tbody tr[data-page='" + page + "']").show();
                    	
                    	// handle the navigation
                    	var paging = $('.module-results .pager');
                    	$(".action-btn", paging).removeClass("inactive").unbind("click");
                    	
                    	// previous
                    	SGX.screener.search.pageButton($(".action-btn.prev", paging), page - 1 == 0 ? 1 : page - 1, page);
                    	
                    	// pages
                    	var lastPg = 1;
                    	$(".action-btn", paging).not(".prev, .next").each(function(idx, el) {
                    		var pg = parseInt($(el).text());
                    		lastPg = pg;
                    		SGX.screener.search.pageButton($(el), pg, page);
                    	});
                    	
                    	// next
                    	SGX.screener.search.pageButton($(".action-btn.next", paging), lastPg == page ? page : page + 1, page);

                    	// resize
        	            var curHeight = SGX.getTrueContentHeight();
        	            if (SGX.pageHeight !== curHeight) SGX.resizeIframe(curHeight);
        	            
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
                    error: typeof errorFN !== "undefined" ? errorFN : SGX.genericAjaxError,
                    complete: function() {
                    	if (typeof document.location.hash !== "undefined" && document.location.hash != "") {
            	            var curHeight = SGX.getTrueContentHeight();
            	            if (SGX.pageHeight !== curHeight) SGX.resizeIframe(curHeight);
                    	}
                    }
            	});
            	
            },
            
            getTrueContentHeight: function() {
	            var curHeight = 0;
        		$(".container_3:visible:last").each(function(idx) {
        			curHeight += ($(this).prop("scrollHeight")); 
        		});
        		return curHeight + 50;
            },
            
            genericAjaxSuccess: function(data) {
            	alert("NO success method provided");
            	debug.log(data);
            },
            
            genericAjaxError: function(data, status, er) {
            	alert("NO error method provided");
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
                },

                close: function(selector) {
                	$('.dropdown').closest(".button-dropdown").removeClass("open");
                },
                
                init: function(selector, finished) {
                	
                	$('.button-dropdown', selector).unbind("click");
                	$('.button-dropdown', selector).click(function(e) {

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
                    	
                    	if ($("ul li", dd).first().text() != def && text != def) {
                    		$("ul", dd).prepend($("<li />").text(def)).click(function(e) {
                    			$(dd).find(".copy").text($(e.target).text());
                    			$(e.target).remove();
                    			if (typeof finished !== "undefined") finished();
                    		});
                    	}
                    	
                    	$(dd).find(".copy").text(text);
                    	
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
                	
                	$.colorbox({
                		html: $(container),
                		overlayClose: false,
                		transition: 'none',
                		maxWidth: settings.hasOwnProperty("maxWidth") ? settings.maxWidth : 550,
                		onComplete: function() {
                			if (settings.hasOwnProperty("postLoad")) settings.postLoad(settings)
                		},
                		onClose: function() {
                			if (settings.hasOwnProperty("close")) settings.close(settings);
                		}
                	});

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
            		$(".tooltip-content", template).text($(el).attr("glossary-copy"));
            		
                    var height = $(template).height(), width = $(template).width();
                    $(template).css({
                        'top': $(el).offset().top - height - 12,
                        'left': $(el).offset().left - (width/2) + 10
                    });
                    
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
            		if (val == "" || val == "-") return;
            		
            		var formatter = SGX.numberFormats.hasOwnProperty(fmt) ? SGX.numberFormats[fmt] : {};
            		
            		if (fmt.indexOf("number") != -1 || fmt == "millions" || fmt == "percent") {

            			// round
            			val = parseFloat(val).toFixed(formatter.decimals).replace(/(\.\d*[1-9])0+$/,'$1').replace(/\.0*$/,'');

            			// give some commas
            			var parts = val.split(".");
            		    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            		    if (parts.length > 1 && parseInt(parts[1]) > 0) val = parts.join(".");
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
            	
            	$(".terms-conditions").click(function(e) { 
            		e.preventDefault();
            		e.stopPropagation();
            		SGX.showTerms();  
            	});
            	
            	var page = location.pathname;
            	if (page.indexOf(SGX.companyPage) != -1) SGX.company.init();
            	else if (page.indexOf(SGX.financialsPage) != -1) SGX.financials.init();
            	else if (page.indexOf(SGX.relatedPage) != -1) SGX.related.init();
            	else if (page.indexOf(SGX.alphasPage) != -1) SGX.alphas.init();
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
            		
            		SGX.company.initSimple(data);

            		// news
            		var newsData = SGX.company.initNews(data);

            		// init charts
            		var endpoint = SGX.fqdn + "/sgx/company/priceHistory";
            		var params = { id: data.company.companyInfo.tickerCode };
            		SGX.handleAjaxRequest(endpoint, params, function(sData) { SGX.company.initStockCharts(sData, newsData); });
            		
            		// all other sections
            		SGX.company.initHolders(data);
            		SGX.company.initConsensus(data);
            		SGX.company.initAlphaFactors(data);
            		
            		// hide/show
            		if (!data.company.companyInfo.hasOwnProperty("businessDescription")) $(".businessDescription").hide();
            		
            		SGX.tooltip.init("body");
            		SGX.formatter.formatElements("body");
            		SGX.tabs();
            		
            	},
            	
            	initSimple: function(data) {
            		
            		// simple properties
            		$(".property").each(function(idx, el) {
            			var name = $(el).attr("data-name");
            			if (typeof name === "undefined" || !data.company.companyInfo.hasOwnProperty(name)) return;
            			
            			if ($(el).is("a")) $(el).attr("href", "http://" + data.company.companyInfo[name])
            			else $(el).text(data.company.companyInfo[name]);
            			
            		});
            		
            		// industry tree
            		var tree = [];
            		if (data.company.companyInfo.hasOwnProperty("industry")) tree.push({ type: "industry", value: data.company.companyInfo.industry });
            		if (data.company.companyInfo.hasOwnProperty("industryGroup")) tree.push({ type: "industryGroup", value: data.company.companyInfo.industryGroup });
            		$.each(tree, function(idx, item) {
            			var a = $("<a href='" + SGX.getRelatedPage(data.company.companyInfo.tickerCode, "&action=industry&field=" + item.type + "&value=" + encodeURIComponent(item.value)) + "'>" + item.value + "</a>");
            			if (idx > 0) $(".breadcrumb-tree .dynamic").append($("<span />").html("&nbsp;>&nbsp;"));
            			$(".breadcrumb-tree .dynamic").append(a);
            		});

            		// screener
            		$(".screener-link").attr("href", SGX.getPage(SGX.screenerPage));
            		
            		// financials
            		$(".view-financials").click(function(e) { window.location = SGX.getFinancialsPage(data.company.companyInfo.tickerCode); });
            		
            		// company profile
            		$(".back-company-profile").attr("href", SGX.getCompanyPage(data.company.companyInfo.tickerCode));
            		
            		// comparable 
            		$(".comparable-button").click(function(e) {  
            			window.location = SGX.getRelatedPage(data.company.companyInfo.tickerCode, "&action=related");
            		});
            		
            		// init pricing
            		var endpoint = SGX.fqdn + "/sgx/price";
            		var params = { id: data.company.companyInfo.tickerCode };
            		SGX.handleAjaxRequest(endpoint, params, SGX.company.initPrice);
            		
            	},
            	
            	initPrice: function(data) {
            		
            		var date = Date.fromISO(data.price.currentDate);
            		var price = data.price.hasOwnProperty("lastPrice") ? data.price.lastPrice : data.price.closePrice;
            		
            		$(".stock-price .change").text(data.price.change);
            		$(".stock-price .lastPrice").text(price);
            		$(".stock-price .last-updated .day").text($.datepicker.formatDate( "dd/M/yy", date));
            		$(".stock-price .last-updated .time").text(date.getHours() + ":" + String("00" + date.getMinutes()).slice(-2) + " SGT");
            		
            		$(".stock-price").show();
            		
            		$(".stock-price span").removeAttr("formatted");
            		SGX.formatter.formatElements(".stock-price");


            	},
            	
            	initNews: function(data) {

        			var ret = [];

            		if (data.hasOwnProperty("keyDevs") && data.keyDevs.length > 0) {
            			
            			data.keyDevs.sort(function(a, b) {
                    		a = a.date, b = b.date
                        	if (a < b) return -1;
                        	if (a > b) return 1;
                        	return 0;            				
            			});
            			
            			$.each(data.keyDevs, function(idx, keyDev) {

            				// sidebar display
            				var letter = SGX.letters.substring(idx, idx+1);
            				var nId = 'keyDev-' + letter;
            				var icon = $("<div />").addClass("icon").text(letter); 
            				var link = $("<span />").text(keyDev.headline).attr("data-name", keyDev.date).attr("data-content", keyDev.situation).attr("data-name", nId);
            				$("<li />").append(icon).append(link).appendTo(".stock-events ul");
            				$(link).click(function(e) {
            					var copy = "<h4>" + $(this).text() + "</h4><div class='news'>" + $(this).attr("data-content") + "</div>";
                                SGX.modal.open({ content: copy, type: 'alert' });
            				});
            				
            				// for chart
            				var chartData = {
            					x: Date.fromISO(keyDev.date),
            					title: letter,
            					text: keyDev.headline,
            					shape: 'url(img/stock-marker.png)',
            					id: nId
            				};
            				ret.push(chartData);
            				
            			});
            			
            			$(".stock-events").show();

            		}

            		return ret;
            	},
            	
            	initAlphaFactors: function(data) {
            		
            		if (!data.hasOwnProperty("alphaFactors") || data.alphaFactors == null) {
            			$(".alpha-factors").hide();
            			return;
            		}
            		
            		var factors = data.alphaFactors;
            		
            		$(".alpha-factors .slider").each(function(idx, el) {
            			
            			var name = $(this).attr("data-name");
            			
            			// no data
            			if (!factors.hasOwnProperty(name)) {
            				$(this).hide();
            				return;
            			}
            			
            			$(this).click(function(el) {
            				window.location = SGX.getAlphasPage(name, factors[name]);
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
            		
            		$(".theme-three-bubble-progress").addClass("opt-" + (company.avgBrokerReq | 0));
            		
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
            			
            			$(".panel .owners tr:first").hide();
            			var percent = 0;
            			
             			$.each(data.holders.holders, function(idx, owner) {
            				var tr = $("<tr />").prependTo(".panel .owners");
            				if (idx%2 == 0) $(tr).addClass("even");
            				$("<td />").text(owner.name).addClass("property").appendTo(tr);
            				$("<td />").text(owner.shares).addClass("property").appendTo(tr);
            				percent += owner.percent;
            			});
             			
             			$("[data-name='percentCommonStock']").text(percent);
            			
            		}
            		
            		
            	},
            	
            	initStockCharts: function(data, newsData) {
            		
            		var priceData = SGX.company.toHighCharts(data.price);
            		var volumeData = SGX.company.toHighCharts(data.volume);
            		
            		Highcharts.setOptions({ lang: { rangeSelectorZoom: "" }});
            		
                    $('#area-chart').highcharts('StockChart', {
                    	
                        colors: [ '#363473', '#BFCE00' ],
                        
                        chart: {
                        	backgroundColor:'rgba(255, 255, 255, 0.1)'
                        },
                        
            		    rangeSelector: {
            				inputEnabled: false,
            		        selected: 3,
                            buttons: [{
                                type: 'day',
                                count: 1,
                                text: '1d'
                            }, {
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
            	        	enabled: false
            	        },

                        yAxis: [
                            {
	            		        title: undefined,
	            		        height: 170,
	            		        lineWidth: 2,
	            		        animation: false,
	            		        labels: {
		                            formatter: function() {
		                                return "S$ " + Highcharts.numberFormat(this.value, 2);
		                            }
	            		        }
                            },
                            {
	            		        title: undefined,
                		        top: 250,
                		        height: 60,
                		        offset: 0,
                		        lineWidth: 2,
	            		        animation: false,
	            		        labels: {
		                            formatter: function() {
		                                return Highcharts.numberFormat(this.value, 2) + " mm";
		                            }
	            		        }
                            }
                        ],
                        
                        series: [
                            {
                            	data: priceData,
                            	type: 'area',
                            	id: 'priceData'
                            },
                            {
                            	data: volumeData,
                            	type: 'column',
            		        	yAxis: 1
                            },
                            
                            {
                            	type: 'flags',
                                data: newsData,
                                onSeries: 'priceData',
                                shape: 'circlepin',
                                y: -24,
                                width: 16,
                                style: { 
                                	color: 'black',
                                	cursor: 'pointer'
                                },
                                events: {
                                	click: function(e) {
                                		$(".stock-events [data-name='" + e.point.id + "']").click();
                                	},
                                },

                            }

                                 
                        ],
                        
                        title: undefined,
                        
                        labels: {
                        	items: [
                        	    {
                                	html: "Price",
                                	style: {
                                    	top: '-22px',
                                    	left: '550px'
                                	}
                        	    },
                        	    {
                                	html: "Volume",
                                	style: {
                                    	top: '190px',
                                    	left: '550px'
                                	}
                        	    }
                        	],
            	        	style: {
            	        		color: "#666",
            	        		fontWeight: "bold"
            	        	}
                        }

                        
                    });
            		
            	},
            	
                toHighCharts: function(data) {
                	var ret = [];
                	$.each(data, function(idx, row) {
                		ret.push([ Date.fromISO(row.date).getTime(), row.value ]);
                	});
                	return ret;
                }
            	
            	
            },
            
            financials: {
            	
            	init: function() {
            		var code = SGX.getParameterByName("code");
            		var company = SGX.company.getCompany(code, SGX.financials.loadedCompany);
            	},
            	
            	loadedCompany: function(data) {

            		SGX.company.initSimple(data);
            		
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
            				$(this).text(financials[idx].absPeriod);
            				$(".currency").text(financials[idx].filingCurrency);
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

            		SGX.formatter.formatElements("body");
            		
            	},
            	
            	cleanFinancials: function(financials) {

            		// let's make sure they're sorted
                	financials.sort(function(a, b) {
                		var a = parseInt(a.absPeriod.replace("FY", "").replace("LTM", ""));
                		var b = parseInt(b.absPeriod.replace("FY", "").replace("LTM", ""));
                    	if (a < b) return -1;
                    	if (a > b) return 1;
                    	return 0;
                	});          		

            		// we need to decide whether to use the latest year end
            		// or quarter data
            		var isQ4 = financials[financials.length - 1].absPeriod.indexOf("LTM4") != -1;
            		financials.splice(isQ4 ? financials.length - 1 : 0, 1);  
            		
            		return financials;
            		
            	},
            	
            	addSeries: function(el) {
            		
            		if ($(".checked").length >= 2) {
            			
                        SGX.modal.open({
                            content: '<h4>Chart Company Financials <span>(Select up to 2)</h4><p>Only two data points can be charted at a time. Remove a data point before selecting a new one.</p>',
                            type: 'alert'
                        });
                        
            			return;
            		}
            		
        			$(el).addClass("checked");
        			
        			$(el).closest("tr").find(".unchart").text("[ UNCHART ]").click(function() {
        				$(this).closest("tr").find(".checkbox").click();
        			});
        			
        			// get the series data
        			var seriesData = [];
        			$(el).closest("td").siblings("[data-value]").each(function() {
        				seriesData.push(parseFloat($(this).attr("data-value"))); 
        			});
        			
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
    				    	opposite: !chart.yAxis[0].opposite,
    				    	color: '#565b5c' == chart.yAxis[0].color ? '#1e2070' : '#565b5c',
        					labels: {
	                            formatter: function() {
	                            	if ($(".trigger", el).attr("data-format") == "cash") {
	                            		return "S$" + this.value;
	                            	}
	                            	else if ($(".trigger", el).attr("data-format") == "percent") {
	                            		return this.value + "%";
	                            	}
	                                return this.value;
	                            }
        					}    				    			
            			});
            			
            			chart.addSeries({
    	                    name: $(".trigger", el).text(),
    	                    data: seriesData,
    	                    yAxis: name
            			});

        			}

        			// legend formatting
        			var chart = $('#large-bar-chart').highcharts();
        			
        			$(".legend-note .item").hide();
        			$(".legend-note .item").each(function(idx, item) {
        				if (idx >= chart.series.length) return;
        				$(".color", this).css({ "background-color": chart.series[idx].color  });
        				$(".label", this).html(chart.series[idx].name + " <span class='parent'>[ " + $(".financials-section [data-name='" + chart.series[idx].yAxis.userOptions.id + "']").closest("tbody").prev("thead").find("h4").text() + " ]</span>");
        				$(this).show();
        			})
        			
                	// resize
    	            var curHeight = SGX.getTrueContentHeight();
    	            if (SGX.pageHeight !== curHeight) SGX.resizeIframe(curHeight);
            		
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
            			return;
            		}
            
            		// remove the series
            		chart.get(name).remove();
            		
            	},
            	
            	
            	initChart: function(el, seriesData) {
            		
        			var categories = [];
            		$(".financials-viewport thead:first th").not(".title").each(function() { categories.push($(this).text()); });

            		$(".chart-row").show();
            		
            		var chart = $('#large-bar-chart').highcharts({
            			
            			colors: [ '#565b5c', '#1e2070' ], 
            			
            			chart: {
            				type: 'column'
            			},
            			legend: {
            				enabled: false
            			},
            			title: undefined,
            			credits: {
            	            enabled: false
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
		                            		return "S$" + this.value;
		                            	}
		                            	else if ($(".trigger", el).attr("data-format") == "percent") {
		                            		return this.value + "%";
		                            	}
		                                return this.value;
		                            }
            					}
            				}
            			],
            			series: [
            			    {
            			    	id: $(".trigger", el).attr("data-name"),
                                name: $(".trigger", el).text(),
                                data: seriesData
                            }
                        ],
                        xAxis: {
                            gridLineColor: 'none',
                            categories: categories,
                            plotBands: {
                                color: '#e2e2e2',
                                from: 0
                            }
                        }
            		});
            			
            		return chart;
            	}
            	
            },
            
            related: {
            	
            	init: function() {
            		var code = SGX.getParameterByName("code");
            		var company = SGX.company.getCompany(code, SGX.related.loadedCompany);
            	},
            	
            	loadedCompany: function(data) {

            		SGX.company.initSimple(data);
            		
            		var action = SGX.getParameterByName("action");
            		
            		if (action == "") {
            			SGX.failed();
            			return;
            		}
            		
            		// show the elements for the page
            		$("." + action).show();

            		// handle the search
            		if (action == "related") SGX.related.handleRelated(data.company.companyInfo.tickerCode);
            		else if (action == "industry") SGX.related.handleIndustry();
            		
            	},
            	
            	handleRelated: function(ticker) {
            		var endpoint = SGX.fqdn + "/sgx/company/relatedCompanies";
            		var params = { id: ticker };
            		$(".pager").hide();
            		SGX.handleAjaxRequest(endpoint, params, SGX.screener.search.renderResults);            		
            	},
            	
            	handleIndustry: function() {
            		
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
            		
            		var endpoint = "/sgx/search";
            		var qs = {};
            		qs.criteria = params;
            		SGX.handleAjaxRequest(SGX.fqdn + endpoint, qs, SGX.screener.search.renderResults, SGX.screener.search.fail);
            		
            	}
            	
            },
            
            alphas: {
            	
            	init: function() {
            		
            		var factor = SGX.getParameterByName("factor");
            		var quintile = parseInt(SGX.getParameterByName("quintile"));
            		var doSearch = false;
            		
            		$(".alpha-factor .slider").each(function(idx, el) {
            			
            			var name = $(this).attr("data-name");
            			
            			// select the current one
            			if (name == factor && !isNaN(quintile) && quintile > 0 && quintile <= 5) {
            				$(".bar-progress", this).addClass("per-" + (quintile*20)).attr("data-class", "per-" + (quintile*20));
            				doSearch = true;
            			}
        				
        				// add in spans
        				$.each(new Array(5), function(quint) {
        					
        					var span = $("<span />").attr("data-quintile", quint + 1).appendTo(".bar-progress", this);
        					
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
        						window.location = SGX.getAlphasPage($(this).closest(".slider").attr("data-name"), $(this).attr("data-quintile"));
        					});
        					
        				});
        				
            			// set up glossary terms
            			$(".glossary-item", this).attr("glossary-key", name);
            			
            		});

        			SGX.tooltip.init("body");
        			
        			if (doSearch) {
                		var endpoint = "/sgx/search/alphaFactors";
                		var params = {};
                		params[factor] = quintile;
                		SGX.handleAjaxRequest(SGX.fqdn + endpoint, params, SGX.screener.search.renderResults, SGX.screener.search.fail);
        			}
        			else {
        	            var curHeight = SGX.getTrueContentHeight();
        	            if (SGX.pageHeight !== curHeight) SGX.resizeIframe(curHeight);
        			}

            	}
            	
            	
            },
            
        	failed: function() {
        		window.location = SGX.getPage(SGX.screenerPage);
        	},
        	

            
    };
    
    SGX.init();    

});