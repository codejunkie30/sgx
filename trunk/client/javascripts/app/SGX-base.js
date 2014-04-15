// This is the modular wrapper for any page
define(['jquery', 'underscore', 'jquicore', 'jquiwidget', 'jquimouse', 'jquidatepicker', 'accordion', 'slider', 'tabs', 'debug', 'highstock'], function($, _, SGX) {
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
    		
            parentURL: null,
            
            pageHeight: $(document).height(),

            getPage: function(page) {
            	var parentURL = SGX.getParentURL();
            	if (parentURL == null) return page;
            	return page + "#" + SGX.getParentURL();
            },
            
            getParentURL: function() {
            	if (SGX.parentURL != null) return SGX.parentURL;
            	if (typeof document.location.hash !== "undefined" && document.location.hash != "") {
                	SGX.parentURL = document.location.hash.replace(/^#/, '');
            	}
            	return SGX.parentURL;
            },
            
            getCompanyPage: function(code) {
            	return SGX.getPage(SGX.companyPage + "?code=" + code)
            },
            
            getFinancialsPage: function(code) {
            	return SGX.getPage(SGX.financialsPage + "?code=" + code)
            },
            
            resizeIframe: function(height) {
            	var fn = function() {
            		SGX.pageHeight = height;
            		XD.postMessage(height, SGX.getParentURL(), parent); 
            	};
            	setTimeout(fn, 10);
            },
    		
    		screener: {

        		init: function() {
        			
        			$(".editSearchB .checkbox").each(function(idx, el) {
        				
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
        			
        			SGX.screener.initCriteria();
        			
        		},
        		
        		finalize: function(data) {

        			SGX.screener.drawInitialCriteria(data);
        			
                	SGX.dropdowns.init(".search-criteria", SGX.screener.search.criteriaSearch);
                	
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
                	$.each(allFields, function(idx, field) { if (field.selected) data.fields.push(field.id); });
                	
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
            		field.id = $(el).attr("data-name");
            		field.format = $(el).attr("data-type") === "undefined" ? "string" : $(el).attr("data-type");
            		field.name = $(".trigger", el).text();
            		field.selected = $(el).hasClass("checked");
            		if (!field.hasOwnProperty("template")) field.template = typeof $(el).attr("data-template") !== "undefined" ? $(el).attr("data-template") : "number";
            		field.label = typeof $(el).attr("data-label") !== "undefined" ? $(el).attr("data-label") : "";
            		field.order = parseInt($(el).attr("data-order"));
            		field.isDefault = $(el).hasClass("default");
            		field.shortName = $(el).attr("data-short-name");
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
                		
                	});
                	
                	SGX.formatValues(".search-criteria");
                	
                	return runsearch;                	
                },
                
                drawCriteriaChange: function(distribution) {
                	
                	SGX.hideLoading();

                	var template = $("#criteria-templates [data-template='change']").clone(true);

            		// handle % change
        			var html = $(".change-picker").html();
                    SGX.modal.open({
                        content: html,
                        type: 'prompt',
                        postLoad: function(options) {
                        	SGX.dateranges.init($("#modal .date"));
                        },
                        cancel: function(options) {
                        	SGX.screener.search.criteriaSearch();
                        },
                        confirm: function(options) {
                        	
                        	distribution.label = distribution.shortName;
                        	$(template).data(distribution);
                        	$(".info", template).text(distribution.label);

                        	$(".percent-value", template).text($("#modal input").val());
                        	$(".start-date", template).text($.datepicker.formatDate("yy-mm-dd", $("#modal .start").datepicker( "getDate" )));
                        	$(".end-date", template).text($.datepicker.formatDate("yy-mm-dd", $("#modal .end").datepicker( "getDate" )));
                        	
                        	$(".search-criteria tbody").append(template);
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
                	
                	$.each(distribution.buckets, function(idx, bucket) {
                		$(dd).append($("<li />").text(bucket.key));
                	});
                	
                	$(".search-criteria tbody").append(template);
                	
                    return template;
                	
                },
                
                drawCriteriaSlider: function(distribution) {
                	
                	distribution.min = distribution.buckets[0].key;
                	distribution.max = distribution.buckets[distribution.buckets.length - 1].key;
                	var matches = SGX.screener.getDistributionMatches(distribution, distribution.min, distribution.max) + ' matches';
                	var template = $("#criteria-templates [data-template='number']").clone(true);
                	
                	
                	$(template).data(distribution);
                	$(template).attr("data-name", distribution.id).attr("data-min", distribution.min).attr("data-max", distribution.max);
                	$(".info", template).text(distribution.shortName)
                	$(".min", template).text(distribution.min).attr("data-format", distribution.format);
                	$(".max", template).text(distribution.max).attr("data-format", distribution.format);;
                	$(".matches", template).text(matches);
                	
                	$(".search-criteria tbody").append(template);

                    SGX.screener.criteriaSlider.init($('td.criteria-slider', template));
                    
                    return template;
                	
                },
                
                getDistributionMatches: function(distribution, startVal, endVal) {
                	var ret = 0;
                	$.each(distribution.buckets, function(idx, bucket) {
                		if (bucket.key >= startVal && bucket.key <= endVal) ret += bucket.count;
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
                		var hasPercentChange = _.findWhere(data.fields, "percentChange" );
                		if (typeof hasPercentChange !== "undefined") data.fields = _.without(data.fields, hasPercentChange);
                		
                		// change the function
                		if (hasPercentChange) {
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
                    		SGX.screener.drawCriteria(data); 
                    		SGX.hideLoading(); 
                    		if (finished) finished(data);
                    	});
                		
                	}
                	
                },
                
                search: {
                	
                	nameSearch: function(val) {
                		var endpoint = "/sgx/search/name";
                		SGX.handleAjaxRequest(SGX.fqdn + endpoint, { search: val }, SGX.screener.search.scrollSearch, SGX.screener.search.fail);
                	},
                
                	showAll: function() {
                		var endpoint = "/sgx/search/";
                		SGX.handleAjaxRequest(SGX.fqdn + endpoint, { criteria: [] }, SGX.screener.search.scrollSearch, SGX.screener.search.fail);
                	},
                	
                	scrollSearch: function(data) {
                		SGX.screener.search.renderResults(data);
                		$('.screener-page').animate({ scrollTop: $(".module-results").position().top }, 150);
                	},

                	criteriaSearch: function() {
                		
                		var endpoint = "/sgx/search";
                		var params = [];
                		
                		$(".search-criteria .criteria").each(function(idx, el) {
                			
                			var name = $(this).attr("data-name");
                			
                			$(this).attr("data-template")
                			
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
                				var copy = $(".trigger .copy").text();
                				if (dd.attr("data-label") == copy) return;
                				param.value = copy;
                			}
                			else if ($(this).attr("data-template") == "change") {
                				param.value = $(".percent-value", this).text();
                				param.from = $(".start-date", this).text();
                				param.to = $(".end-date", this).text();
                			}
                			
                			params.push(param);
                			
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
                    			
                    			var th = $("<th />").attr("data-name", field.field).addClass(field.field).appendTo(header);
                    			$("<span />").text(field.shortName).appendTo(th);
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
            			
                		$.each(data.companies, function(idx, company) {
                			
                			var tr = $("<tr />").addClass("result").data(company);
                			var cLink = $("<a href='" + SGX.getCompanyPage(company.tickerCode) + "'>" + company.companyName + "</a>");

                			// company name
                			$("<td />").append(cLink).addClass("companyName").appendTo(tr).attr("data-value", company.companyName.toLowerCase()).attr('data-name', 'companyName');
                			
                			$.each(fields, function(fIdx, field) {
                				var val = company.hasOwnProperty(field.field) ? company[field.field] : "-";
                				var td = $("<td />").text(val).appendTo(tr).attr("data-value", val).attr("data-name", field.sortType == "string" ? field.field.toLowerCase() : field.field).attr("data-sort", field.sortType).addClass(field.field);
                				if (field.format != "string") $(td).attr("data-format", "number").addClass("formattable");
                				if (!defaultDisplay.hasOwnProperty(field.field)) td.addClass('hidden');
                			});
                			
                			$(".module-results tbody").append(tr);
                			
                		});
                		
                		
                		SGX.formatValues($(".module-results tbody"));
                		SGX.screener.search.displayRows(sort, direction);
                		
                		return;

                	},
                	
                	getAllColumns: function() {
                		var ret = [ { field: "tickerCode", label: "Code", shortName: "Code", format: "string" }, { field: "industry", label: "Industry", shortName: "Industry", format: "string" } ];
            			$(".editSearchB .checkbox[data-display='true']").each(function(idx, el) { 
            				var dataSort = $(el).attr("data-sort");
            				if (typeof dataSort === "undefined") dataSort = "number";
            				var dataFmt = $(el).attr("data-type");
            				if (typeof dataFmt === "undefined") dataFmt = "string";
            				ret.push({ field: $(el).attr("data-name"), label: $(".trigger", el).text(), sortType: dataSort, shortName: $(el).attr("data-short-name"), format: dataFmt }); 
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
                        		var navItem = $('<li><a class="page" href="#">' + i + '</a></li>');
                        		paging.append(navItem);
                        	}                        	

                        	// prev/next
                            if (page > 1) {
                            	paging.prepend('<li><a class="prev" href="#">Prev</a></li>');
                            	paging.append('<li><a class="next" href="#">Next</a></li>');
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
                    	$(".module-results .table-wrapper tbody tr[data-page='" + page + "']").show(400, function() {
                    	});
                    	
                    	// handle the navigation
                    	var paging = $('.module-results .pager');
                    	$("a", paging).removeClass("inactive").unbind("click");
                    	
                    	// previous
                    	SGX.screener.search.pageButton($("a.prev", paging), page - 1 == 0 ? 1 : page - 1, page);
                    	
                    	// pages
                    	var lastPg = 1;
                    	$("a.page", paging).each(function(idx, el) {
                    		var pg = parseInt($(el).text());
                    		lastPg = pg;
                    		SGX.screener.search.pageButton($(el), pg, page);
                    	});
                    	
                    	// next
                    	SGX.screener.search.pageButton($("a.next", paging), lastPg == page ? page : page + 1, page);

                    	// resize call
                    	//SGX.resizeIframe();

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
                			$('.screener-page').animate({ scrollTop: $(".module-results").position().top }, 150);
                			SGX.screener.search.displayPage(page);
            			});
            			
            		},
            		
            		customizeResults: function() {

            			var currentCols = SGX.screener.search.getDisplayColumns();
            			var container = $("<div />");
            			var table = $("<table />").addClass("customize-display-table").appendTo(container);
            			$("<tr />").append($("<td colspan='2' />").html("<h3>Customize Results Display</h3>")).appendTo(table);
            			
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
                            	$("#modal .checkbox.checked").each(function() { dcs[$(this).attr("data-name")] = true; });
                            	SGX.screener.search.setDisplayColumns(dcs);
                            	SGX.modal.close();
                            },
                            postLoad: function(settings) {
                            	$("#modal .checkbox").click(function(e) {
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
                	$(selector).addClass("open");
                	$('.dropdown', selector).addClass("open");
                },

                close: function(selector) {
                	$(selector).removeClass("open");
                	$('.dropdown', selector).removeClass("open");
                },
                
                
                init: function(selector, finished) {
                	
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
            		var settings = $(".confirm", modal).data();
                	$("#modal").fadeOut(100, function() { $(this).removeAttr("class"); if (settings.hasOwnProperty("cancel")) { settings.cancel(); } });
                },
                
                open: function(settings) {
                
                	var modal = $("#modal");
                	$(modal).addClass(settings.type);
                	$(".modal-content .copy", modal).html(settings.content);
                	$(".close, .cancel, .modal-close", modal).click(function(e) { SGX.modal.close(); });

                	// clean up
                	var confirm = $(".confirm", modal);
            		confirm.removeData();
            		confirm.unbind();

                	if (settings.type == "prompt") {
                		confirm.data(settings);
                		$(confirm).click(function(e) { settings.confirm($(this).data()); });
                	}
                	
                	if (settings.hasOwnProperty("postLoad")) {
                		settings.postLoad(settings);
                	}
                	
                	$("#modal").fadeIn(100);

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
            
            formatValues: function(selector) {
            	
            	$(".formattable", selector).not("[formatted]").each(function(idx, el) {
            		
            		var fmt = $(el).attr("data-format");
            		var val = $(el).text();
            		
            		if (val == "" || val == "-") return;
            		
            		if (fmt == "simple-number") {
            		    var parts = val.split(".");
            		    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            		    val = parts.join(".");
            		    if (val.indexOf("-") == 0) val = "(" + val.substring(1) + ")";
            		}
            		else if (fmt == "millions") {
            			var tmp = parseInt(val) + "";
            			val = parseFloat(val).toFixed(3);
            			if (tmp.length >= 10) val = (val / 1000000000).toFixed(1).replace(/\.0$/, '') + 'b';
            			else if (tmp.length >= 7) val = (val / 1000000).toFixed(1).replace(/\.0$/, '') + 'mm';
            			else if (tmp.length >= 4) val = (val / 1000).toFixed(1).replace(/\.0$/, '') + 'k';
            			val = "S$" + val;
            		}
            		else if (fmt == "number" && val.indexOf(".") != -1) {
            			val = parseFloat(val).toFixed(3);
            		}
            		else if (fmt == "number") {
            			// do nothing
            		}
            		else if (fmt == "percent") {
            			val = val + "%";
            		}
            		else if (fmt == "date") {
            			val = $.datepicker.formatDate("dd/M/yy", Date.fromISO(val));
            		}
            		else {
            			console.log(fmt);
            		}
            		
            		// we formatted it
            		$(el).attr("formatted", "true").text(val);
            		
            	});
            	
            },
            
            init: function() {
            	var page = location.pathname;
            	if (page.indexOf(SGX.companyPage) != -1) SGX.company.init();
            	else if (page.indexOf(SGX.financialsPage) != -1) SGX.financials.init();
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
            		SGX.handleAjaxRequest(endpoint, params, loadedFN, SGX.company.failed);
            	},
            	
            	failed: function() {
            		window.location = SGX.getPage(SGX.screenerPage);
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
            		
            		SGX.tooltip.init("body");
            		SGX.formatValues("body");
            		SGX.tabs();
            		
            		//SGX.resizeIframe();
            		
            	},
            	
            	initSimple: function(data) {
            		
            		// simple properties
            		$(".property").each(function(idx, el) {
            			var name = $(el).attr("data-name");
            			if (typeof name === "undefined" || !data.company.companyInfo.hasOwnProperty(name)) return;
            			
            			if ($(el).is("a")) $(el).attr("href", data.company.companyInfo[name])
            			else $(el).text(data.company.companyInfo[name]);
            			
            		});
            		
            		// industry tree
            		var tree = [];
            		if (data.company.companyInfo.hasOwnProperty("industry")) tree.push({ type: "industry", value: data.company.companyInfo.industry });
            		if (data.company.companyInfo.hasOwnProperty("industryGroup")) tree.push({ type: "industryGroup", value: data.company.companyInfo.industryGroup });
            		$.each(tree, function(idx, item) {
            			var a = $("<a href='broken.html?action=industry&field=" + item.type + "&value=" + encodeURIComponent(item.value) + "'>" + item.value + "</a>");
            			var li = $("<li />").append(a);
                		$(".breadcrumb-tree").append(li);
            		});

            		// screener
            		$(".screener-link").attr("href", SGX.getPage(SGX.screenerPage));
            		
            		// financials
            		$(".view-financials").click(function(e) { window.location = SGX.getFinancialsPage(data.company.companyInfo.tickerCode); });
            		
            		// company profile
            		$(".back-company-profile").attr("href", SGX.getCompanyPage(data.company.companyInfo.tickerCode));
            		
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
            					shape: 'url(../../img/stock-marker.png)',
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
            			
            			$(".bar-progress", this).addClass("per-" + (factors[name]*20));
            			
            			// set up glossary terms
            			$(".glossary-item", this).attr("glossary-key", name);
            			
            		});
            		
            		
            	},
            	
            	initConsensus: function(data) {
            		
            		var company = data.company.companyInfo;
            		
            		$(".progress-estimate").show();
            		
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
                        	backgroundColor: 'transparent'
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
	            		        animation: false
                            },
                            {
	            		        title: undefined,
                		        top: 250,
                		        height: 60,
                		        offset: 0,
                		        lineWidth: 2,
	            		        animation: false
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
                                    	left: '360px'
                                	}
                        	    },
                        	    {
                                	html: "Volume",
                                	style: {
                                    	top: '190px',
                                    	left: '290px'
                                	}
                        	    }
                        	],
            	        	style: {
            	        		color: "#666",
            	        		fontSize: "12pt",
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
            				if (cur.hasOwnProperty(name)) $(td).text(cur[name]).attr("data-value", cur[name]).addClass("formattable").attr("data-format", "simple-number");
            			});
            			
            		});
            		
            		SGX.formatValues("body");            		
            		
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
            		
            	}
            }
            
    };
    
    SGX.init();    

});