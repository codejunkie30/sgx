// This is the modular wrapper for any page
define(['jquery', 'underscore', 'jquicore', 'jquiwidget', 'jquimouse', 'accordion', 'slider', 'tabs', 'debug'], function($, _, SGX) {
    // Nested namespace uses initial caps for AMD module references, lowercased for namespaced objects within AMD modules
    // Instead of console.log() use Paul Irish's debug.log()
	
    SGX = {
    		
    		fqdn : "http://ec2-54-82-16-73.compute-1.amazonaws.com",
    		
    		screener: {

        		init: function() {
        			
        			$(".editSearchB .checkbox").each(function(idx, el) {
        				
        				$(this).attr("data-order", idx);
        				$(this).click(function(e) {
        					
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
                    		
                    		// otherwise we're good
                    		SGX.showLoading();
        					
        					// add in the field
        					var data = { "fields" : [ $(this).attr("data-name") ] };
        					SGX.screener.criteriaChange.getDistributions(data, SGX.screener.criteriaChange.addCriteria);
        					
        				});
        				
        			});
        			
        			SGX.screener.initCriteria();
        			SGX.accordion();
        			SGX.hideLoading();
        			
        		},
        		
                initCriteria: function() {
                	
                	// load up all fields for distributions
                	var allFields = SGX.screener.getAllCriteria();
                	var data = { "fields" : [] };
                	$.each(allFields, function(idx, field) { if (field.selected) data.fields.push(field.id); });
                	
                	SGX.screener.criteriaChange.getDistributions(data, SGX.screener.drawInitialCriteria);
                	
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
            		field.format = $(el).attr("data-type");
            		field.name = $(".trigger", el).text();
            		field.selected = $(el).hasClass("checked");
            		field.template =  typeof $(el).attr("data-template") !== "undefined" ? $(el).attr("data-template") : "number";
            		field.label = typeof $(el).attr("data-label") !== "undefined" ? $(el).attr("data-label") : "";
            		field.order = parseInt($(el).attr("data-order"));
            		return field;
                },
                
                drawInitialCriteria: function(data) {
                	$(".search-criteria tbody").children().remove();
                	
                	data.distributions.sort(function(a, b) {
                		var a = parseInt($(".editSearchB [data-name='" + a.field + "']").attr("data-order"));
                		var b = parseInt($(".editSearchB [data-name='" + b.field + "']").attr("data-order"));
                    	if (a < b) return -1;
                    	if (a > b) return 1;
                    	return 0;
                	});
                	
                	SGX.screener.drawCriteria(data);
                	
                	SGX.tooltip.init("body");
                	
                },
                
                drawCriteria: function(data) {
                	
                	$.each(data.distributions, function(idx, distribution) {

                		// populate with label, other info from HTML
                		var el = $(".editSearchB [data-name='" + distribution.field + "']");
                		SGX.screener.populateField(distribution, el);
                		
                		var template;
                		
                		// draw the appropriate input
                		if (distribution.template == "select") template = SGX.screener.drawCriteriaSelect(distribution);
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
                	
                },
                
                drawCriteriaSelect: function(distribution) {
                	
                	var template = $("#criteria-templates [data-template='select']").clone(true);
                	var dd = $(".dropdown ul", template);

                	$(template).data(distribution);
                	$(".button-dropdown", template).attr("data-label", distribution.label);
                	$(".info", template).text(distribution.name);
                	$(".trigger .copy", template).text(distribution.label);
                	
                	$.each(distribution.buckets, function(idx, bucket) {
                		$(dd).append($("<li />").text(bucket.key));
                	});
                	
                	$(".search-criteria tbody").append(template);

                	SGX.dropdowns.init();

                    return template;
                	
                },
                
                drawCriteriaSlider: function(distribution) {
                    
                	distribution.min = distribution.buckets[0].key;
                	distribution.max = distribution.buckets[distribution.buckets.length - 1].key;
                	var matches = SGX.screener.getDistributionMatches(distribution, distribution.min, distribution.max) + ' matches';
                	var template = $("#criteria-templates [data-template='number']").clone(true);
                	
                	
                	$(template).data(distribution);
                	$(template).attr("data-name", distribution.id).attr("data-min", distribution.min).attr("data-max", distribution.max);
                	$(".info", template).text(distribution.name)
                	$(".min", template).text(distribution.min);
                	$(".max", template).text(distribution.max);
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
                            step: (distribution.max / 50 > 0 ? 50 : 1)
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
                		SGX.screener.drawCriteria(data);
                		$.each(data.distributions, function(idx, distribution) {
                			SGX.screener.criteriaChange.checkCriteriaItem($(".editSearchB [data-name='" + distribution.field + "']"));
                		});
                		SGX.hideLoading();
                	},
                	
                	removeCriteria: function(target, finished) {
                		SGX.screener.criteriaChange.uncheckCriteriaItem(target);
                        $(target).remove();
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
                		var endpoint = SGX.fqdn + "/sgx/search/distributions";
                    	SGX.handleAjaxRequest(endpoint, data, finished, undefined);
                	},
                	
                	maxCriteriaMsg: function() {
                        SGX.modal.open({
                            content: '<h4>Search Criteria <span>(select up to 5)</h4><p>You have reached the criteria limit. Please remove criteria before proceeding.</p>',
                            type: 'alert'
                        });
                	}
                	
                }
                
    		},
    		
            
    		hideLoading: function() {
    			$('#loading').delay(250).fadeOut();
    		},
    		
    		showLoading: function() {
    			$('#loading').delay(250).fadeIn();
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
                    jsonpCallback: 'jsonp',
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
            
            dropdowns: {
            	
                open: function(selector) {
                	$(selector).addClass("open");
                	$('.dropdown', selector).addClass("open");
                },

                close: function(selector) {
                	$(selector).removeClass("open");
                	$('.dropdown', selector).removeClass("open");
                },
                
                
                init: function() {
                	
                	$('.button-dropdown').click(function(e) {

                		var isOpen = $(this).hasClass("open");

                		if (isOpen) {
                			SGX.dropdowns.close('.button-dropdown');
                			return;
                		}
                		
                		SGX.dropdowns.open(this);
                		
                	});
                	
                    $('.button-dropdown li').click(function(e) {
                    	
                    	e.preventDefault();
                    	e.stopPropagation()
                    	
                    	var dd = $(this).closest(".button-dropdown");
                    	var def = typeof $(dd).attr("data-label") !== "undefined" ? $(dd).attr("data-label") : "";
                    	var text = $(this).text();
                    	
                    	if ($("ul li", dd).first().text() != def && text != def) {
                    		$("ul", dd).prepend($("<li />").text(def)).click(function(e) {
                    			$(dd).find(".copy").text($(e.target).text());
                    			$(e.target).remove();
                    		});
                    	}
                    	
                    	
                    	
                    	$(dd).find(".copy").text(text);
                    	dd.click();
                    });

                }
            },
            
            modal: {
                close: function() {
                	$("#modal").fadeOut(100, function() { $(this).removeAttr("class"); });
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
            }
    		
    };
    
    
    SGX.screener.init();

});