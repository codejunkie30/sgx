// This is the modular wrapper for any page
define(['jquery', 'underscore', 'jquicore', 'jquiwidget', 'jquimouse', 'jquidatepicker', 'accordion', 'slider', 'tabs', 'debug'], function($, _, SGX) {
    // Nested namespace uses initial caps for AMD module references, lowercased for namespaced objects within AMD modules
    // Instead of console.log() use Paul Irish's debug.log()
	
    SGX = {
    		
    		fqdn : "http://ec2-54-82-16-73.compute-1.amazonaws.com",
    		
    		resultSize: 25,
    		
    		displayColumns: {
    			companyName: true,
    			tickerCode: true,
    			industry: true,
    			marketCap: true,
    			totalRevenue: true,
    			peRatio: true,
    			dividendYield: true
    		},
    		
            parentURL: decodeURIComponent(document.location.hash.replace(/^#/, '')),
    		
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
                			var cLink = $("<a href='company-tearsheet.html?code=" + company.tickerCode +"'>" + company.companyName + "</a>");

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
                    	
                    	XD.postMessage($(document).height(), decodeURIComponent(document.location.hash.replace(/^#/, '')), parent);

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
            	});
            	
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
                	
                	if (settings.type == "prompt") {
                		var confirm = $(".confirm", modal);
                		confirm.data(settings);
                		$(".confirm", modal).click(function(e) {
                			settings.confirm($(this).data());
                		})
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
            		
            		if (fmt == "millions") {
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
            		else {
            			console.log(fmt);
            		}
            		
            		// we formatted it
            		$(el).attr("formatted", "true").text(val);
            		
            	});
            	
            }
    		
    };
    
    
    SGX.screener.init();

});