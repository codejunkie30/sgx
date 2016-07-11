define([ "wmsi/utils", "knockout", "text!client/data/fields.json", "text!client/templates/criteria-number.html", "text!client/templates/criteria-select.html", "text!client/templates/criteria-change.html", "jquery-ui" ], function(UTIL, ko, fieldData, nTemplate, sTemplate, cTemplate) {

	/**
	 * allows us to customize particular criteria
	 * assumes working with criteria object for model
	 */
	 
	ko.bindingHandlers.configureAdditionalCriteria = {
	    init: function(element, valueAccessor, allBindings, viewModel, bindingContext) {
	    	var template = ko.utils.unwrapObservable(valueAccessor());
	        var el = $("<div />").appendTo("body");
	    	ko.renderTemplate(
	    		template, 
	    		viewModel,
	    		{
	    			afterRender: function(nodes) {
	    				var settings = viewModel.criteria.additionalConfiguration[template];
	    				settings.content = $(el);
	    				settings.model = viewModel;
	    				var container = viewModel.criteria.screener.modal.open(settings);
	    			}
	    		},
	    		el[0],
	    		"replaceChildren"
	    	);
	    }
	};
	
	var CRITERIA = {
			
		screener: null,
		numberTemplate: null,
		selectTemplate: null,
		changeTemplate: null,
		textDistributions: [ "industryGroup" ],
		firstRun: true,
		exchangeDisplay: false,
		info:[],
		minimum:0,maximum:0,
		fieldItems: null,
		init: function(screener, finalize) {
			
    		// reset the val and the toggle
			$(".searchtoggle .toggle:first").click();
    		$(".searchbar input").val("");
			
			// parse in JSON field configuration
			$.extend(true, this, JSON.parse(fieldData));
			
			this.numberTemplate = nTemplate;
			this.selectTemplate = sTemplate;
			this.changeTemplate = cTemplate;
			this.screener = screener;
			screener.criteria = this;
			
			
			
			if(typeof(UTIL.retrieveTracking()) != "undefined" && UTILS.retrieveTracking().value == "true") {
    			
    			var prevCriteria = UTIL.retrieveScreenerCriteria().value;
    			
    			var prevParams = prevCriteria.criteria;
    			
    				var critCount = 0;
    				var industryFlag = false;
    				var exchangeFlag = false;
    				this.fieldItems = this.fieldGroups;
    				for(var j=0;j<this.fieldGroups.length;j++) {
    					for(var k=0;k<this.fieldGroups[j].fields.length;k++) {
    						this.fieldGroups[j].fields[k].isDefault=false;
    						for(var i=0;i<prevParams.length;i++) {
    							
    							if(prevParams[i].field === this.fieldGroups[j].fields[k].id) {
    								critCount = critCount+1
    								if(prevParams[i].field == "industryGroup") {
    									industryFlag  = true;
    								}
    								else if(prevParams[i].field == "exchange") {
    									exchangeFlag = true;
    									//this.fieldGroups[j].fields[k].label = this.fieldGroups[j].fields[k].itemLabels[prevParams[i].value]
    								}
    								this.fieldGroups[j].fields[k].isDefault=true;
    							}
    							
    						}
    						
    							
    					}
    						
    				}
    				if(!exchangeFlag) {
    					this.fieldGroups[4].fields[0].isDefault=true;
    				}
    				if(!industryFlag) {
    					if(prevCriteria.industryFlag) {
    						this.fieldGroups[3].fields[0].isDefault=true;
    					}
    				}
    				
    			
			}
			
			
			
			// clear the drawn the inputs
			$(".search-criteria tbody").children().remove();
			
			// only apply bindings first time
			if ($(".search-options[data-section='advanced-screener'] .criteria-select[data-init='true']").length == 0) {
				
				// bind
				ko.applyBindings(screener, $(".search-options[data-section='advanced-screener'] .criteria-select")[0]);
				$(".search-options[data-section='advanced-screener'] .criteria-select").attr("data-init", "true");

	    		// industry search
				if (this.firstRun && UTIL.getParameterByName("action") == "isearch") {
					
					// deselect everything but industrygroup
					$.each(this.getSelectedFields().fields, function(idx, val) {
						if (val == "industryGroup") return;
						screener.criteria.clickEvents.uncheckCriteriaItem($(".checkbox[data-id='" + val + "']"));
					});
					
					var tmpFn = finalize;
					
					// select industry
					finalize = function(data) {
						var val = UTIL.getParameterByName("industryGroup");
						screener.criteria.firstRun = false;
						tmpFn(data);
						if (val != "") $(".button-dropdown li:contains('" + val + "')").click();
					};
					
				}
				
				this.getDistributions(this.getSelectedFields(), finalize);
				return;
			}
			
			CRITERIA.reset(finalize);
			
		},

    	reset: function(finished) {
    		CRITERIA.firstRun = true;
        	CRITERIA.exchangeDisplay = false;
			PAGE.showLoading();
			$(".search-criteria tbody").children().remove();
			
			$(".criteria-select .checkbox").each(function(idx, el) { CRITERIA.clickEvents.uncheckCriteriaItem(el); });
			$(".criteria-select [data-default='true']").each(function(idx, el) { CRITERIA.clickEvents.checkCriteriaItem(el); });
			setTimeout(function(){
	        	CRITERIA.getDistributions(CRITERIA.getSelectedFields(), CRITERIA.screener.finalize);
        	},500);
    	},
    	
    	getDistributions: function(data, finished) {

    		var tmpF = finished;
    		var endpoint = this.screener.fqdn + "/sgx/search/distributions";
			var postType = 'POST';
    		var processFields = [], ignoreFields = [];
    		// some fields require additional steps, remove these from this service
    		$.each(data.fields, function(idx, name) {
    			
    			var field = CRITERIA.getFieldById(name);
    			
    			
    			// we can process it in the service
    			if (!field.hasOwnProperty("customDistribution") || !field.customDistribution) {
    				processFields.push(name);
    				return;
    			}
    			
    			// we need to do something else
    			ignoreFields.push({ "field": name, "buckets": [], "template": field.template  });
    			
    		});
    		
    		// reset the field list
    		data.fields = processFields;
    		
    		// change the function
    		if (ignoreFields.length > 0) {
    			tmpF = function(data) { 
    				data.distributions = data.distributions.concat(ignoreFields);
    				if (!data.hasOwnProperty("fieldValues")) data.fieldValues = [];
    				finished(data);
    			};
    		}
    		
    		// do something else
    		if (data.fields.length == 0) { 
    			data = { distributions: [] };
    			tmpF(data);
    			return;
    		}
    		
        	UTIL.handleAjaxRequest(endpoint, postType, data, undefined, tmpF, PAGE.customSGXError, undefined);

    	},
    	
        handleDistributions: function(data) {
        	
        	var textFields = [];
        	if(!data) return;
        	// text ranges
        	$.each(data.distributions, function(idx, dist) {
        		
        		// let's get the fielded data as is (no need to calculate text)
        		var name = dist.field;
        		if ($.grep(data.fieldValues, function(val) { return val.field == name; }).length > 0) return;
        		var fieldData = CRITERIA.getFieldById(name);
        		fieldData.buckets = dist.buckets;
        		textFields.push(fieldData);
        		
        		// hack for industry groups
            	if (name == "industryGroup") fieldData.buckets.push({ "data-name": "industry", key: "Real Estate Investment Trusts (REITs)" });
            	
            	// now sort the values
            	if (fieldData.hasOwnProperty("itemLabels")) {
            		fieldData.buckets.sort(function(a,b) {
            			var aval = fieldData.itemLabels.hasOwnProperty(a.key) ? fieldData.itemLabels[a.key] : a.key;
            			var bval = fieldData.itemLabels.hasOwnProperty(b.key) ? fieldData.itemLabels[b.key] : b.key;
            			return aval.localeCompare(bval); 
            		});
            	}
            	else {
                	fieldData.buckets.sort(function(a,b) { return a.key.localeCompare(b.key); });
            	}
            	
        		
        	});

        	// numeric ranges
        	$.each(data.fieldValues, function(idx, field) {
        		var fieldData = CRITERIA.getFieldById(field.field);
        		
        		// force sort asc
        		field.values.sort(function(a, b) { return a - b; });
        		
        		// get the type and count
        		var type = fieldData.hasOwnProperty("distribution-type") ? fieldData["distribution-type"] : "normal";
        		var bCount = fieldData.hasOwnProperty("distribution-buckets") ? fieldData["distribution-buckets"] : 75;
        		
        		// get the random distributions
        		var buckets = {};
				if (this.field == 'avgBrokerReq'){ 
					$.each(field.values, function(vIdx, val) { var val = Math.round(val * 10) / 10; CRITERIA.randomizeBucket(buckets, val, type, bCount);});
				 } else {
					 $.each(field.values, function(vIdx, val) {CRITERIA.randomizeBucket(buckets, val, type, bCount);});
				 }
				//if(this.field == 'avgBrokerReq' || this.field == 'marketCap')
				//{
					CRITERIA.info[this.field] = field;
				//}
				
        		
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
            	
            	fieldData.buckets = arr;
            	fieldData.min = field.values[0];
            	fieldData.max = field.values[field.values.length-1];
        		
        	});
        	
        	delete data.fieldValues;
        	
        },
        
        randomizeBucket: function(bucket, val, type, cnt) {

        	var idx = (Math.round(val * 100000)%cnt);
        	if (type == "log") idx = (Math.round(Math.log(val * 100000))%cnt);
    		if (!bucket.hasOwnProperty(idx)) bucket[idx] = [];

    		bucket[idx].push(val);
        },
        
        getDistributionMatches: function(field, startVal, endVal) {
        	var ret = 0;
			
        	// primarily for consensus
        	if (!field.hasOwnProperty("values")) {
        		for (i = startVal; i <= endVal; i++) ret += field.buckets[i].count;
        		return ret;
        	}
			
        	var sVal = field.buckets[startVal].from, eVal = field.buckets[endVal].to;
        	$.each(field.values, function(idx, val) {
        		if (val >= sVal && val <= eVal) ret++;
        	});
        	return ret;
        	
        },
        
    	getSelectedFields: function() {
    		var data = { fields: [] };
    		$(".panel .checkbox.checked").each(function(idx, field) {
    			data.fields.push($(field).attr("data-id"));
    		});
    		return data;
    	},
    	
    	getFieldById: function(id) {
    		var ret = null;
    		var order = 0;
    		$(this.fieldGroups).each(function(idx, grp) {
    			$(grp.fields).each(function(f, field) {
					field.order = order;
    				if (field.id == id) ret = field;
    				order++;
    			});
    		});
    		return ret;
    	},
    	
    	maxCriteriaMsg: function() {
    		
            this.screener.modal.open({
                content: '<h4>Search Criteria <span>(select up to 5)</h4><p>You have reached the criteria limit. Please remove criteria before proceeding.</p>',
                type: 'alert'
            });
    		
    	},
    	
        renderInputs: function(data) {
        	
        	this.handleDistributions(data);
        	var criteriaThis = this;
        	
        	if (data.distributions.length > 1) {
            	data.distributions.sort(function(a, b) {
            		return CRITERIA.getFieldById(a.field).order - CRITERIA.getFieldById(b.field).order;
            	});
        	}
        	
        	$.each(data.distributions, function(idx, distribution) {

        		var field = CRITERIA.getFieldById(distribution.field);
        		if (field == null) return;
				
        		var el = $("<tr >").attr("data-id", field.id).html(CRITERIA[field.template + "Template"]).addClass("criteria");
        		
				if (CRITERIA.exchangeDisplay == false) {
					$(el).appendTo(".search-criteria tbody");
				} else {
					$(el).insertBefore(".search-criteria tbody tr:last");
				}
				
        		$(el).data(field);
        		
        		var mdl = {
        			'criteria': CRITERIA, 
        			'field': field,
        			'min': ko.observable(field.min),
        			'max': ko.observable(field.max),
        			'val': ko.observable(field.value),
        			'updatesMin': ko.observable(field.min),
    				'updatesMax': ko.observable(field.max)
        		};
        		
				if (mdl.field.minLabel != undefined && mdl.field.maxLabel != undefined){
					mdl.updatesMin(mdl.field.minLabel);
					mdl.updatesMax(mdl.field.maxLabel);					
				};
				
        		mdl.changes = ko.computed(function() {
        			CRITERIA.minimum = this.min();
        			CRITERIA.maximum = this.max();
        			return this.min() + "-" + this.max() + "-" + this.val();
        		}, mdl);
        		
        		
    			mdl.matches = ko.computed(function() {
    				if (this.changes() != "" && this.field.hasOwnProperty("buckets")) {
        				var slider = $(".search-criteria [data-id='" + this.field.id + "'] .slider-bar");
        				var min = slider.hasClass("ui-slider") ? $(slider).slider("values", 0) : 0;
	        			var max = slider.hasClass("ui-slider") ? $(slider).slider("values", 1) : this.field.buckets.length - 1;

							if(typeof(UTIL.retrieveTracking()) != "undefined" && UTILS.retrieveTracking().value == "true") {
				    			
				    			var prevCriteria = UTIL.retrieveScreenerCriteria().value;
				    			
				    			var prevParams = prevCriteria.criteria;
				    			for(var i=0;i<prevParams.length;i++) {
				    				
				    					if(prevParams[i].field === this.field.id && (prevParams[i].field != "industryGroup" && prevParams[i].field != "exchange" && prevParams[i].field != "percentChange" && prevParams[i].field != "targetPriceNum")) {
				    						
					        				
					        				for(var k=0;k<prevCriteria.position.length;k++) {
				    							if(prevParams[i].field == prevCriteria.position[k]) {

				    								var cnt = prevCriteria.position[k+1][4].upperIndex - prevCriteria.position[k+1][3].lowerIndex;
						    						mdl.updatesMin(CRITERIA.info[prevParams[i].field].values[prevCriteria.position[k+1][3].lowerIndex]);
						    						mdl.updatesMax(CRITERIA.info[prevParams[i].field].values[prevCriteria.position[k+1][4].upperIndex]);
						    						
							        				return cnt+1
				    							}
				    						}
				    						
				    					}
		    				
				    			
				    			}
							
							}
        				return this.criteria.getDistributionMatches(this.field, min, max);
    				}
    				return 0;
    			}, mdl);
    			
        		ko.applyBindings(mdl, $(el)[0]);

					if(typeof(UTIL.retrieveTracking()) != "undefined" && UTILS.retrieveTracking().value == "true") {
		    			
		    			var prevCriteria = UTIL.retrieveScreenerCriteria().value;
		    			
		    			var prevParams = prevCriteria.criteria;
		    			for(var i=0;i<prevParams.length;i++) {
		    						    				
		    					if(prevParams[i].field != "industryGroup" && prevParams[i].field != "exchange" && prevParams[i].field != "percentChange" && prevParams[i].field != "targetPriceNum") {
		    						
		    						for(var k=0;k<prevCriteria.position.length;k++) {
		    							if(prevParams[i].field == prevCriteria.position[k]) {
		    								$("[data-id="+prevParams[i].field+"]").find('.ui-slider-range').attr('style',prevCriteria.position[k+1][0].attr1);
		    								$("[data-id="+prevParams[i].field+"]").find(".ui-slider-handle").eq(0).attr('style',prevCriteria.position[k+1][1].attr2);
		    								$("[data-id="+prevParams[i].field+"]").find(".ui-slider-handle").eq(1).attr('style',prevCriteria.position[k+1][2].attr3);
		    							}
		    						}
		    					}
		    					else if(prevParams[i].field == "industryGroup") {
		    						if($("[data-id="+prevParams[i].field+"]").find('.button-dropdown').find('.copy').eq(0).length != 0) {
		    							//$("[data-id="+prevParams[i].field+"]").find('.dropdown').find('ul').append('<li>Select Industry</li>');
		    							//$("[data-id="+prevParams[i].field+"]").find('.button-dropdown').attr('data-label','Select Industry');
		    							$("[data-id="+prevParams[i].field+"]").find('.button-dropdown').find('.copy').eq(0).val(prevParams[i].value);
			    						$("[data-id="+prevParams[i].field+"]").find('.button-dropdown').find('.copy').eq(1).text(prevParams[i].value);
			    						mdl.field.label=prevParams[i].value;;
			    						mdl.val(prevParams[i].value)
		    						}
		    						
		    					}
		    					else if(prevParams[i].field == "exchange") {
		    						if($("[data-id="+prevParams[i].field+"]").find('.button-dropdown').find('.copy').eq(0).length != 0) {
		    							//$("[data-id="+prevParams[i].field+"]").find('.dropdown').find('ul').append('<li>Select Exchange</li>');
		    							//$("[data-id="+prevParams[i].field+"]").find('.dropdown').attr('data-label','Select Exchange');
		    							$("[data-id="+prevParams[i].field+"]").find('.button-dropdown').find('.copy').eq(0).val(prevParams[i].value);
			    						$("[data-id="+prevParams[i].field+"]").find('.button-dropdown').find('.copy').eq(1).text(CRITERIA.fieldItems[4].fields[0].itemLabels[prevParams[i].value]);
			    						mdl.field.label=CRITERIA.fieldItems[4].fields[0].itemLabels[prevParams[i].value];
			    						mdl.val(prevParams[i].value);
		    						}
		    						
		    					}
		    					
		    			}
		    				
		    			
		    		}
					
        		
        		if (typeof field.customDistribution === "undefined" || !field.customDistribution) {
        			mdl.changes.subscribe(function(val) { mdl.criteria.runSearch(); });
        		}
        		
        	});
        	
        	$(".search-criteria .criteria").removeClass("even");
        	$(".search-criteria .criteria:even").addClass("even");
        	
        	this.screener.dropdowns.init(".search-criteria");
        	
        	if (this.firstRun) this.runSearch(function() { return 0; });
			
            CRITERIA.exchangeDisplay = true;
        },
        
        addCriteria: function(data, finished) {
        	
    		$.each(data.distributions, function(idx, distribution) {
    			CRITERIA.clickEvents.checkCriteriaItem($(".search-criteria [data-id='" + distribution.field + "']"));
    		});
    		
    		CRITERIA.renderInputs(data);
        	
        },
        
    	removeCriteria: function(target, finished) {
			// Prevents user from removing all search criteria
			if ($('.search-criteria .criteria').length == 1) { PAGE.modal.open({ type: 'alert',  content: '<p>At least one search criteria is required.</p>', width: 300 }); return; }
			
    		this.clickEvents.uncheckCriteriaItem(target);
    		this.screener.tooltips.close();
    		
    		$(target).addClass("inactive");

    		var vm = ko.dataFor($(".criteria[data-id='" + $(target).attr("data-id") + "']")[0]);
    		vm.val(null);			
			
    		$(target).remove();
            
        	$(".search-criteria .criteria").removeClass("even");
        	$(".search-criteria .criteria:even").addClass("even");
        	
            if (typeof finished !== "undefined") finished();
            
    	},
    	
    	runSearch: function(scroll) {
    		
    		var endpoint = "/sgx/search";
    		var params = [];
    		var industryFlag = false;
    		var exchangeFlag = false;
    		var exchangeVm = null;
    		var industryVm = null;
    		$(".search-criteria .criteria:not(.inactive)").each(function(idx, el) {

    			var vm = ko.dataFor(el);
    			var name = vm.field.id;
				
    			if(name=="industryGroup") {
    				exchangeVm = vm;
    				industryFlag = true;
    			}
    			else if(name=="exchange") {
    				exchangeFlag = true;
    				industryVm = vm;
    			}
    			param = { 'field': name };
    			
    			if (vm.field.template == "select") {
    				
    				
    				if (typeof vm.val() === "undefined" || vm.val() == null || vm.val() == vm.field.label) {
    					if(name == "exchange") {
    						if((typeof vm.val() === "undefined" || vm.val() == null) && vm.field.label != "Select Exchange" ) {
    							for(var i=0;i<CRITERIA.fieldItems[4].fields[0].buckets.length;i++) {
    								
    								if(CRITERIA.fieldItems[4].fields[0].buckets[i].key == vm.field.label) {
    									param.value = CRITERIA.fieldItems[4].fields[0].buckets[i].key;
    	        						vm.val(CRITERIA.fieldItems[4].fields[0].buckets[i].key);
    	        						param.value = vm.val();
    	        						params.push(param);
    								}
    							}
    							
        						
        					}
    					}
    					else if(name == "industryGroup") {
    						if((typeof vm.val() != "undefined" && vm.val() != null) || vm.field.label != "Select Industry" ) {
    							for(var i=0;i<CRITERIA.fieldItems[3].fields[0].buckets.length;i++) {
    								
    								if(CRITERIA.fieldItems[3].fields[0].buckets[i].key == vm.field.label) {
    									param.value = CRITERIA.fieldItems[3].fields[0].buckets[i].key;
    	        						vm.val(CRITERIA.fieldItems[3].fields[0].buckets[i].key);
    	        						param.value = vm.val();
    	        						params.push(param);
    								}
    							}
    							
        						
        					}
    					}
    					
    					return;
    				}
					//Fix for REITs as it's no longer part of industryGroup
    				(vm.val() != 'Real Estate Investment Trusts (REITs)') ? param.field = vm.field.id : param.field = 'industry';
    				param.value = vm.val();		
    				
    			}
    			else if (vm.field.template == "change") {
    				if (vm.val() == null) return;
    				param.from = vm.min();
    				param.to = vm.max();
    				param.value = vm.val();
    			}
    			else {
    				param.from = vm.min();
    				param.to = vm.max();
    			}
    			// add to search
    			params.push(param);

    			// special case
    			//TODO explain why it was set to 3 before, it just created a lot of problems
    			if (name == "avgBrokerReq") params.push({ field: "targetPriceNum", from: "0" });
    			
    		});
    		
    		// search
    		if(typeof(UTIL.retrieveTracking()) != "undefined" && UTILS.retrieveTracking().value == "true") {
    			
    			var prevCriteria = UTIL.retrieveScreenerCriteria().value;
    			
    			var prevParams = prevCriteria.criteria;
    			for(var i=0;i<prevParams.length;i++) {
    				
    						for(var k=0;k<prevCriteria.position.length;k++) {
    							if(prevParams[i].field == prevCriteria.position[k] && (prevParams[i].field != "industryGroup" && prevParams[i].field != "exchange" && prevParams[i].field != "percentChange" && prevParams[i].field != "targetPriceNum")) {
    								if(typeof(CRITERIA.info[prevParams[i].field]) != "undefined" ) {
    									prevParams[i].from = CRITERIA.info[prevParams[i].field].values[prevCriteria.position[k+1][3].lowerIndex];
    									prevParams[i].to = CRITERIA.info[prevParams[i].field].values[prevCriteria.position[k+1][4].upperIndex];
    								}
    							}
    						}
    						
    			}
    			
    			this.screener.results.retrieve(endpoint, { 'criteria': prevParams }, null, scroll);
    		}
    		else {
    			
    			//var arr = ["marketCap","totalRevenue","peRatio","dividendYield","priceVs52WeekHigh"];
    			var position =[];
    			for(var i=0;i<params.length;i++) {
    				if(params[i].field != "industryGroup" && params[i].field != "exchange" && params[i].field != "percentChange" && params[i].field != "targetPriceNum") {
        					var sliderPosition = [];
        					var styleAttr1 = $("[data-id="+params[i].field+"]").find('.ui-slider-range').attr("style");
        					var styleAttr2 = $("[data-id="+params[i].field+"]").find(".ui-slider-handle").eq(0).attr("style");
        					var styleAttr3 = $("[data-id="+params[i].field+"]").find(".ui-slider-handle").eq(1).attr("style");
        					sliderPosition.push({attr1:styleAttr1});
        					sliderPosition.push({attr2:styleAttr2});
        					sliderPosition.push({attr3:styleAttr3});
        					

        					sliderPosition.push({lowerIndex:CRITERIA.info[params[i].field].values.indexOf(params[i].from)});
        					sliderPosition.push({upperIndex:CRITERIA.info[params[i].field].values.indexOf(params[i].to)});

        					
        					position.push(params[i].field,sliderPosition);
        					
    				}
            		
    			}
    			var screenerCriteria = null;var page = 1;
    			if(UTIL.retrieveScreenerCriteria() == null || UTIL.retrieveScreenerCriteria() == "undefined") {
					var customizeItems = [];
    				screenerCriteria = {
    	    				criteria: params,
    	    				position:position,
    	    				customizeDisplay:customizeItems,
    	    				pagination:customizeItems,
    	    				industryFlag:industryFlag
    	    			}
					
				}
    			else {
    				screenerCriteria = UTIL.retrieveScreenerCriteria().value;
    				screenerCriteria.criteria = params;
    				screenerCriteria.position = position;
    				screenerCriteria.industryFlag=industryFlag;
    				
    			}
    				
    			
    			UTIL.saveScreenerCriteria(screenerCriteria);
    			this.screener.results.retrieve(endpoint, { 'criteria': params }, null, scroll);
    		}
    			
    		
    		
    	},
    	
    	clickEvents: {
    		
    		criteriaClick: function(data, event) {
    			
				var mainEl = $(event.currentTarget);
				
				// remove this field
				if ($(mainEl).hasClass("checked")) {
					event.currentTarget = $(".search-criteria [data-id='" + $(mainEl).attr("data-id") + "'] td.remove img")[0];
					CRITERIA.clickEvents.removeCriteria(data, event);
					return;
				}
								
        		// check if we're maxed out first
        		if ($(".search-criteria tbody tr[data-id != 'exchange']").length >= 5) {
        			CRITERIA.maxCriteriaMsg();
        			return;
        		}
        		
        		CRITERIA.clickEvents.checkCriteriaItem(mainEl);
        		
        		// let's show loading
        		var data = { "fields" : [ $(mainEl).attr("data-id") ] };
        		CRITERIA.getDistributions(data, CRITERIA.addCriteria);
        		
    		},
    		
        	removeCriteria: function(data, event) {
                event.stopPropagation();
                event.preventDefault();
        		CRITERIA.removeCriteria($(event.currentTarget).closest(".criteria"));
        	},
        	
        	checkCriteriaItem: function(criteria) {
        		var name = $(criteria).attr("data-id");
        		var checkEl = $(".criteria-select [data-id='" + name + "']");
                $(checkEl).addClass('checked');
                $(checkEl).find('input[type="checkbox"]').attr("checked", "checked");            		
        	},
        	
        	uncheckCriteriaItem: function(criteria) {
        		var name = $(criteria).attr("data-id");
        		var checkEl = $(".criteria-select [data-id='" + name + "']");
                $(checkEl).removeClass('checked');
                $(checkEl).find('input[type="checkbox"]').removeAttr("checked");            		
        	},
        	
        	reset: function(data, event) {
        		CRITERIA.firstRun = true;
	        	CRITERIA.exchangeDisplay = false;
				PAGE.showLoading();
				$(".search-criteria tbody").children().remove();
				
				$(".criteria-select .checkbox").each(function(idx, el) { CRITERIA.clickEvents.uncheckCriteriaItem(el); });
				$(".criteria-select [data-default='true']").each(function(idx, el) { CRITERIA.clickEvents.checkCriteriaItem(el); });
				setTimeout(function(){
		        	CRITERIA.getDistributions(CRITERIA.getSelectedFields(), CRITERIA.screener.finalize);
	        	},500);

        	}
    		
    	},

    	/**
    	 * settings
    	 */
    	additionalConfiguration: {
    		
    		'criteria-change-configure': {
    			
                type: 'prompt',
                
                maxWidth: 650,
                
                postLoad: function(options) {

                	$(".picker").each(function(idx, el) {
                		var data = $(el).data("config");
                		data.onSelect = function(date) { options.model[$(this).attr("id")]($(this).datepicker("getDate")); };
                		$(el).datepicker(data);
                		options.model[$(el).attr("id")]($(el).datepicker("getDate"));
                	});
                	
                },
                
                cancel: function(options) {
                	CRITERIA.removeCriteria($(".search-criteria [data-id='percentChange']"))
                },
                
                confirm: function(options) {
                	
                	
                	var model = options.model;
                	var percent = parseInt(model.val());
                	
                	if (isNaN(percent) || typeof percent === "undefined" || percent <= 0 || percent >= 100) {
                		alert("Percent Change must be between 1 and 100");
                		model.val("");
                		return;
                	}
                	
                	model.changes.subscribe(function(val) { model.criteria.runSearch(); });
                	model.criteria.runSearch();
                	
                	
                	CRITERIA.clickEvents.checkCriteriaItem($(".criteria-select [data-id='percentChange']"));
                	
                   	CRITERIA.screener.modal.close();
                	
                }
    			
    			
    		}
    		
    		
    	}
		
	};
	
	return CRITERIA;
	
});