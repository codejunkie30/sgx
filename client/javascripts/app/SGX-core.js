// This is the modular wrapper for any page
define(['jquery', 'jquicore', 'jquiwidget', 'jquimouse', 'accordion', 'slider', 'tabs', 'debug'], function($, SGX) {
    // Nested namespace uses initial caps for AMD module references, lowercased for namespaced objects within AMD modules
    // Instead of console.log() use Paul Irish's debug.log()
    SGX = {
        startup: function() {
            $('#loading').delay(250).fadeOut();
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
        core: function(flag) {
            SGX.startup();
            debug.info('SGX.Core');
            SGX.closeAll();
            SGX.accordion();
            SGX.modal.init();

            // SGX.data.get();

            // Initialize custom dropdowns
            SGX.dropdowns.init();

            // Initialize jquery tabs
            SGX.tabs();
            SGX.slider.startup();
            SGX.tooltip.start();
            SGX.financials.init();

            // Initial Search Criteria components
            SGX.search.init();

            // Initialize form components
            SGX.form.init();
        },
        checkboxes: function() {
            $('.checkbox').on('click', function() {
                var $this = $(this),
                    checked = $this.hasClass("checked");
                if (checked === true) {
                    // debug.log('checked');
                    $this.removeClass("checked");
                } else {
                    // debug.log('not checked');
                    $this.addClass("checked");
                }
            });
        },
        closeAll: function() {
            // Close tooltips and dropdowns on body click
            $('html').on('click', function(e) {
                if (!$(e.toElement).parents().hasClass("button-dropdown")) {
                    $(document).find('.button-dropdown').removeClass('open');
                }
                // If isn't info button, hide tooltips
                if (!$(e.toElement).hasClass("info")) {
                    SGX.tooltip.close();
                }
            });
        },
        dropdowns: {
            close: function(target) {
                // $(target)
                $(document).find('.button-dropdown').removeClass('open');
                $(document).find('.trigger').removeClass('open');
            },
            init: function() {
                $('.button-dropdown').find('> .trigger:not(.open)').on('click', function() {
                    console.log('todo cleanup');
                    $(document).find('.button-dropdown').removeClass('open');
                    var $this = $(this),

                        $button = $this.parents('.button-dropdown'),
                        $dropdown = $button.find('.dropdown');

                    if ($this.hasClass("open")) {
                        $this.removeClass("open");
                    } else {
                        $this.addClass("open");
                    }
                });

                $('.button-dropdown').find('> .trigger:not(.open)').on('click', function() {
                    var $button = $(this).parents('.button-dropdown'),
                        $dropdown = $button.find('.dropdown');

                    if ($button.hasClass("open")) {
                        $button.removeClass("open");
                    } else {
                        $button.addClass("open");
                    }
                });
                $('.button-dropdown').find('li').on('click', function() {
                    console.log('click');
                    var text = $(this).text();
                    $(this).parents('.button-dropdown').find('.trigger').html(text + '<span class="arrow"></span>');
                    $(document).find('.button-dropdown').removeClass('open');
                    var $this = $(this),

                        $button = $this.parents('.button-dropdown'),
                        $dropdown = $button.find('.dropdown');

                    if ($this.hasClass("open")) {
                        $this.removeClass("open");
                    } else {
                        $this.addClass("open");
                    }
                });
            }
        },
        error: {
            init: function(content) {
                SGX.modal.open(content, 'modal-error');
            }
        },
        financials: {
            chart: function(settings) {
                if ($('#large-bar-chart').length) {
                    $('#large-bar-chart').highcharts({
                        chart: {
                            type: 'column'
                        },
                        legend: {
                            enabled: true,
                            borderColor: '',
                            borderWidth: 0,
                            align: 'right',
                            verticalAlign: 'bottom',
                            backgroundColor: 'white',
                            symbolPadding: 10,
                            symbolWidth: 16,
                            symbolHeight: 16,
                            symbolRadius: 0,
                            maxHeight: 200,
                            padding: 10,
                            x: 10,
                            y: 10,
                            itemMarginTop: 5,
                            itemMarginBottom: 35,
                            itemStyle: {
                                cursor: 'pointer',
                                color: ['#565b5c', '#1e2070'],
                                fontSize: '12px',
                                height: 20
                            },
                            width: 210,
                            lineHeight: 20
                        },
                        plotOptions: {
                            column: {
                                pointRange: 1,
                                pointPadding: 0,
                                colorByPoint: true,
                                borderWidth: 0,
                                width: 29
                            },
                            dataLabels: {
                                enabled: true,
                                formatter: function() {
                                    return this.y + '°C';
                                }
                            }
                        },
                        series: [{
                            name: 'Total Revenue',
                            data: [10000, 12000, 12000, 14500, 15000],
                            color: ['#565b5c'],
                            colors: ['#565b5c']

                        }, {
                            name: 'Payout Ratio',
                            data: [12000, 13000, 16250, 13800, 13000],
                            color: ['#1e2070'],
                            colors: ['#1e2070']
                        }],
                        subtitle: {
                            floating: true,
                            align: 'right',
                            text: ''
                        },
                        title: {
                            floating: false,
                            align: 'right',
                            text: '',
                            enabled: false
                        },
                        tooltip: {
                            enabled: false,
                            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y:,.0f}</b></td></tr>',
                            footerFormat: '</table>',
                            shared: true,
                            useHTML: true
                        },

                        xAxis: {
                            gridLineColor: 'none',
                            categories: [
                                'FY2010 30-Jun-2010',
                                'FY2011 30-Jun-2011',
                                'FY2012 30-Jun-2012',
                                'FY2013 30-Jun-2013',
                                'LTM Ending 31-Dec-2013'
                            ],
                            plotBands: {
                                color: '#e2e2e2',
                                from: 0
                            }
                        },
                        yAxis: {
                            min: 0,
                            labels: {
                                formatter: function() {
                                    return '$' + this.value;
                                }
                            },
                            gridLineColor: '#e2e2e2',
                            gridLineWidth: 30,
                            title: {
                                text: ''
                            },
                            tickInterval: 1000
                        }
                    });
                }
            },
            init: function() {
                $('.financials-viewport').find('.trigger').on('click', function(e) {
                    console.log('add trigger');
                    var $this = $(this),
                        $checkbox = $this.parents('.checkbox'),
                        $input = $checkbox.find('input[type="checkbox"]'),
                        checkboxChecked = $checkbox.hasClass("checked");
                    var settings = {
                        name: $checkbox.attr('data-name'),
                        type: $checkbox.attr('data-label-type'),
                        min: $checkbox.attr('data-min'),
                        max: $checkbox.attr('data-max')
                    };


                    var count = $('.search-criteria').find('.criteria').length;
                    if (count < 5) {
                        if (checkboxChecked) {
                            SGX.form.checkbox.uncheck($checkbox);
                            var $target = $('tr.criteria[data-name="' + $checkbox.attr('data-name') + '"]');
                            console.log($target);
                            SGX.search.removeCriteria($target);
                            console.log('should be uncheck');
                        } else {
                            SGX.form.checkbox.check($checkbox);
                            SGX.search.addCriteria($(this), settings);
                        }
                    } else {
                        // There are 5 criteria at the moment, prompt user to remove a criteria
                        if (checkboxChecked) {
                            // User is trying to remove criteria using checkbox
                            SGX.form.checkbox.uncheck($checkbox);
                            var $target = $('tr.criteria[data-name="' + $checkbox.attr('data-name') + '"]');
                            console.log($target);
                            SGX.search.removeCriteria($target);
                            console.log('should be uncheck');
                        } else {
                            console.log('count is more than 5');
                            SGX.modal.open({
                                content: 'Please remove a search criteria'
                            });
                        }
                    }
                });
            }
        },
        form: {
            checkbox: {
                check: function(target) {
                    $(target).addClass('checked');
                    $(target).find('input[type="checkbox"]').attr("checked", "checked");
                },
                uncheck: function(target) {
                    $(target).removeClass('checked');
                    $(target).find('input[type="checkbox"]').removeAttr("checked");
                },
                init: function() {

                    // If checkboxes need functionality other than default trigger, remove the trigger-sgx class from them and create custom functionality
                    // debug.info('SGX.form.checkbox.init');
                    $('.checkbox').find('.trigger').on('click', '.trigger-sgx', function() {
                        var $this = $(this),
                            $checkbox = $this.parents('.checkbox'),
                            $input = $checkbox.find('input[type="checkbox"]'),
                            checkboxChecked = $checkbox.hasClass("checked");
                        if (checkboxChecked) {
                            SGX.form.checkbox.uncheck($checkbox);
                        } else {
                            SGX.form.checkbox.check($checkbox);
                        }
                    });
                }
            },
            init: function() {
                SGX.form.checkbox.init();
            }
        },
        modal: {
            close: function() {
                var $modal = $('#modal');
                $modal.fadeOut(function() {
                    $modal.removeAttr("class style");
                    $(this).html('<div class="modal-container"><div class="modal-content"><p>Modal is empty.</p></div><div class="bg" /></div>');
                });

            },
            open: function(modalSettings) {
                var $modal = $('#modal'),
                    content = modalSettings.content,
                    type = modalSettings.type;

                $modal.html('<div class="modal-container"><div class="modal-content" /><div class="bg" /></div>');
                var $modalContent = $modal.find('.modal-content');
                if (typeof(content) !== 'undefined') {
                    $modal.find('.modal-content').html(content);
                }

                if ((typeof(type) !== 'undefined') && (type == 'prompt')) {

                    // If prompt modal, we need to add buttons for confirming the user's action
                    debug.info('Prompt');
                    $modal.addClass(type);
                    $modalContent.append('<button class="confirm">Confirm</button><button class="cancel">Cancel</button>').end();
                    // console.log(modalSettings.target);
                    modalSettings.confirm({
                        target: modalSettings.target
                    });

                } else if (typeof(type) !== 'undefined') {
                    // Catch all to help with debugging
                    $modal.addClass(type);
                }
                $modal.fadeIn();
            },
            init: function(modalSettings) {
                SGX.modal.close();
                $('#modal').find('.close-button').on('click', function() {

                });
                $('#modal').on('click', function(modalSettings) {
                    SGX.modal.close();
                });
            }
        },
        pagination: {
            reset: function() {
                console.log('pagination reset');
                var $pager = $('.module-results').find('.pager');
                $pager.empty();
                var resultsRows = $('.module-results').find('tr.result');
                if (resultsRows.parents('tbody').length) {
                    resultsRows.unwrap();
                }

                for (var i = 0, y = 1; i < resultsRows.length; i += 10, y++) {
                    if (y == 1) {
                        resultsRows.slice(i, i + 10).wrapAll('<tbody data-panel-count="' + y + '" class="active" />');
                    } else {
                        resultsRows.slice(i, i + 10).wrapAll('<tbody data-panel-count="' + y + '" />');
                    }
                    // Add to pager
                    $('.module-results').find('.pager').append('<li><a href="#">' + y + '</a></li>');
                }
                if ($pager.find('li').length > 1) {
                    $pager.prepend('<li><a class="prev" href="#">Prev</a></li>');
                    $pager.append('<li><a class="next" href="#">Next</a></li>');
                }
                SGX.pagination.registerClickHandlers();
            },
            registerClickHandlers: function() {
                // Pager click handler
                var $pager = $('.module-results').find('.pager');
                $pager.find('a').on('click', function(e) {
                    e.preventDefault();
                    var $this = $(this),
                        target = $this.parent().index() - 1;

                    if (!$this.hasClass("prev") && !$this.hasClass("next")) {
                        // console.log(target);
                        $('.module-results').find('tbody:not(:eq(' + target + '))').removeClass("active");
                        $('.module-results').find('tbody:eq(' + target + ')').addClass("active");
                    } else if ($this.hasClass("next")) {
                        if ($('.module-results').find('tbody.active').next('tbody').length > 0) {
                            // console.log('next');
                            $('.module-results').find('tbody.active').next('tbody').addClass("active");
                            $('.module-results').find('tbody.active').first().removeClass("active");
                        } else {
                            $('.module-results').find('tbody').removeClass("active");
                            $('.module-results').find('tbody').first().addClass("active");
                        }

                    } else if ($this.hasClass("prev")) {
                        if ($('.module-results').find('tbody.active').prev('tbody').length > 0) {
                            // console.log('prev');
                            $('.module-results').find('tbody.active').prev('tbody').addClass("active");
                            $('.module-results').find('tbody.active').last().removeClass("active");
                        } else {
                            $('.module-results').find('tbody').removeClass("active");
                            $('.module-results').find('tbody').last().addClass("active");
                        }
                    }
                    console.log($('.module-results').find('tbody.active').index());
                });
            },
        },
        search: {
            criteriaObject: {},
            resultsObject: {},
            resultsDOM: '',
            init: function() {
                var data = {
                    "fields": ["marketCap", "totalRevenue", "peRatio", "dividendYield", "industry"]
                };
                // Default load
                SGX.search.addCriteria(data);

                $('.button-reset').on('click', function() {
                    $('.search-criteria').find('tr.criteria').each(function() {
                        SGX.search.removeCriteria($(this));
                    });

                });
                $('.editSearchB').find('.trigger').on('click', function(e) {
                    // console.log('add trigger');
                    var $checkbox = $(this).parents('.checkbox'),
                        $input = $checkbox.find('input[type="checkbox"]'),
                        checkboxChecked = $checkbox.hasClass("checked");
                    var settings = {
                        name: $checkbox.data('name'),
                        type: $checkbox.data('type'),
                        min: $checkbox.data('min'),
                        max: $checkbox.data('max'),
                        servername: $checkbox.data('name')
                    };
                    var count = $('.search-criteria').find('.criteria').length;
                    if (count < 5) {
                        if (checkboxChecked) {
                            SGX.form.checkbox.uncheck($checkbox);
                            var $target = $('tr.criteria[data-name="' + $checkbox.attr('data-name') + '"]');
                            SGX.search.removeCriteria($target);
                        } else {
                            // Checkbox is not checked and less than 5, so add Criteria
                            SGX.form.checkbox.check($checkbox);
                            var field = $checkbox.attr('data-name').toString();
                            SGX.search.addCriteria({
                                "fields": [field]
                            });
                        }
                    } else {
                        // There are 5 criteria at the moment, prompt user to remove a criteria
                        if (checkboxChecked) {
                            // User is trying to remove criteria using checkbox
                            SGX.form.checkbox.uncheck($checkbox);
                            var $target = $('tr.criteria[data-name="' + $checkbox.attr('data-name') + '"]');
                            SGX.search.removeCriteria($target);
                        } else {

                            SGX.modal.open({
                                content: 'Please remove a search criteria'
                            });
                        }
                    }
                });
                $('.search-submit').on('click', function(e) {
                    e.preventDefault();
                    var data = {
                        "criteria": [
                            /*{
                            "field": "marketCap",
                            "from": "5.0",
                            "to": "2500.0"
                        }, {
                            "field": "percentChange",
                            "from": "2013-04-01",
                            "to": "2014-03-12",
                            "value": 2
                        }*/
                        ]
                    };
                    var criteria = $('table.search-criteria').find('tr.criteria');

                    $('table.search-criteria').find('tr.criteria').each(function(idx) {
                        console.log(idx);
                        if ($(this).data('name') != 'industry') {
                            data.criteria.push({
                                "field": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').attr('data-name'),
                                "from": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').attr('data-min'),
                                "to": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').attr('data-max')
                            });
                        } else if ($(this).data('name') == 'industry') {
                            console.log('todo');
                            if ($('table.search-criteria').find('tr.criteria:eq(' + idx + ')').find('li.open').length) {
                                data.criteria.push({
                                    "field": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').data('name'),
                                    "value": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').find('li.open').text()
                                });
                            }

                        }
                    });
                    console.log('\n\n\n');
                    console.log(data);
                    console.log('\n\n\n');
                    $.ajax({
                        url: 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/search',
                        type: 'POST',
                        dataType: 'jsonp',
                        jsonpCallback: 'jsonp',
                        data: {
                            'json': JSON.stringify(data)
                        },
                        contentType: 'application/json; charset=UTF-8',
                        success: function(data) {
                            console.log(data);
                            if (data.companies.length != 0) {
                                SGX.search.removeResults();
                                SGX.search.addResults(data);
                                // SGX.search.pagination.reset()
                            } else {

                            }
                        }
                    })
                });
                $('.button-customize-display').find('.trigger').on('click', function() {
                    // SGX.form.checkbox.check()
                    var checked = $(this).parents('.checkbox').hasClass("checked");
                    if (checked === true) {
                        // debug.log('checked');
                        $(this).parents('.checkbox').removeClass("checked");
                    } else {
                        // debug.log('not checked');
                        $(this).parents('.checkbox').addClass("checked");
                    }
                    console.log(SGX.search.resultsObject);
                });
            }, // end it
            addCriteria: function(data, settings) {
                if (typeof(data) == 'undefined') {
                    var data = {
                        "fields": ["marketCap", "totalRevenue", "peRatio", "dividendYield", "industry"]
                    };
                }
                $.ajax({
                    url: "http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/search/distributions",
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify(data)
                    },
                    contentType: 'application/json; charset=UTF-8',
                    success: function(data) {
                        console.log(data.distributions);
                        for (var i = data.distributions.length - 1; i >= 0; i--) {
                            console.log(data.distributions[i]);
                            var $relCheckbox = $('.editSearchB').find('.checkbox[data-name="' + data.distributions[i].field + '"]'),
                                friendlyName = $relCheckbox.find('.trigger').text(),
                                type = $relCheckbox.data('type'),
                                matches = data.distributions[i].buckets.length + ' matches',
                                min = data.distributions[i].buckets[0].key,
                                max = data.distributions[i].buckets[data.distributions[i].buckets.length - 1].key;

                            if (data.distributions[i].field != 'industry') {
                                if (!$('tr.criteria[data-name="' + data.distributions[i].field + '"]').length) {
                                    $('table.search-criteria').find('tbody').append('<tr class="criteria" style="display:none;" data-name="' + data.distributions[i].field + '" data-type="' + type + '" data-max="' + max + '" data-min="' + min + '"><td>' + friendlyName + '</td><td>S$' + min + 'mm</td><td class="criteria-slider"><div class="module-stock-slider"><div class="slider-bar"></div><div class="stock-bar-container"></div><div class="bar"></div><div class="matches"></div></div></td><td class="max">S$' + max + 'mm</td></tr>');
                                } else {
                                    if ((typeof(type) != 'undefined') && (type == 'dollar')) {
                                        $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('td.min').html('S$' + min + 'mm');
                                        $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('td.max').html('S$' + max + 'mm');
                                    } else if ((typeof(type) != 'undefined') && (type == 'percent')) {
                                        $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('td.min').html(min + '%');
                                        $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('td.max').html(max + '%');
                                    }
                                    $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('.matches').html(matches);
                                }
                                var sliderSettings = {
                                    min: parseFloat(min, 10),
                                    max: parseFloat(max, 10)
                                }
                                SGX.slider.init($('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('td.criteria-slider'), sliderSettings);
                            } else if (data.distributions[i].field == 'industry') {
                                console.log('industry');
                                console.log(data.distributions[i].buckets);
                                if (!$('tr.criteria[data-name="' + data.distributions[i].field + '"]').length) {
                                    $('table.search-criteria').find('tbody').append('<tr class="criteria" data-name="' + data.distributions[i].field + '" data-type="' + data.distributions[i].field + '"><td>Industry</td><td colspan="2"><div class="button-dropdown"><div class="trigger">Select Sector &amp; Industry<span class="arrow"></span></div><div class="dropdown"><ul></ul></div></div></td><td class="max"></td></tr>');
                                    for (var b = data.distributions[i].buckets.length - 1; b >= 0; b--) {
                                        // console.log(data.distributions[i].buckets[b]);

                                        // console.log(industryRow);
                                        // $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('ul').append('test');
                                        $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('ul').append('<li>' + data.distributions[i].buckets[b].key + '</li>')
                                    };
                                } else {
                                    console.log('else');

                                }
                                SGX.dropdowns.init();

                            }
                        };
                        $('.search-criteria').find('tr.criteria').fadeIn();
                        SGX.search.register.removeClickHandler();
                    }, // end success
                    error: function(data, status, er) {
                        console.log(data);
                    }
                }); //end ajax


            },
            register: {
                removeClickHandler: function() {
                    $('td.max').on('click', function(e) {
                        e.stopPropagation();
                        var $target = $(this).parents('tr'),
                            criteraName = $target.data('name');

                        SGX.modal.open({
                            content: '<p>Do you want to remove ' + criteraName + ' from your search?</p>',
                            type: 'prompt',
                            target: $target,
                            confirm: function(options) {

                                var target = options.target;
                                // target.css({'background': 'red'});
                                $('button.confirm').on('click', target, function(e) {

                                    SGX.search.removeCriteria(target);
                                    criteraName = $.trim(criteraName);
                                    // .css({'background':'red'});
                                    SGX.form.checkbox.uncheck($('.editSearchB').find('.checkbox[data-name="' + criteraName + '"]'));
                                    console.log('confirmed');
                                });

                            }

                        });
                    });
                }
            },
            removeCriteria: function(target) {
                // This is a placeholder function in case other functionality needs to be piggy-backed onto the removal of Search Criteria
                // console.log('remove Criteria');
                // console.log();
                $(target).fadeOut(function() {
                    $(target).remove();
                    SGX.form.checkbox.uncheck($('.editSearchB').find('.checkbox[data-name="' + $(target).data('name') + '"]'));
                });
            },
            addResults: function(data) {
                $('.module-results').find('.results').html(data.companies.length + ' results')

                // Clear results DOM store
                SGX.search.resultsDOM = '';
                SGX.search.resultsObject = data.companies;
                // console.log(data.companies);
                for (var i = data.companies.length - 1; i >= 0; i--) {

                    if (typeof(data.companies[i].dividendYield) == 'undefined') {
                        data.companies[i].dividendYield = '-'
                    }
                    var trimIndustry = data.companies[i].industry.replace(/ /g, '').replace(',', '').toLowerCase().toString();

                    var result = '<tr class="result" data-industry="' + trimIndustry + '"><td>' + data.companies[i].companyName + '</td><td>' + data.companies[i].tickerCode + '</td><td class="industry">' + data.companies[i].industry + '</td><td>' + data.companies[i].marketCap + '</td><td>' + data.companies[i].totalRevenue + '</td><td>' + data.companies[i].priceToBookRatio + '</td><td>' + data.companies[i].dividendYield + '</td></tr>';
                    if ($('.module-results').find('tbody').length) {
                        $('.module-results').find('tbody').append(result);
                    } else {
                        $('.module-results').find('table').append('<tbody>' + result + '</tbody>');
                    }



                    // Add results to cache
                    SGX.search.resultsDOM = SGX.search.resultsDOM + result;
                };
                SGX.search.industryDropdown.populate(data);
                SGX.pagination.reset();

            },
            removeResults: function() {
                $('.module-results').find('tbody').remove();
            },
            resetResults: function() {
                $('.module-results').find('tbody').remove().end().find('thead').after(SGX.search.resultsDOM);
                // console.log(SGX.search.resultsDOM);
            },
            industryDropdown: {
                populate: function(data) {
                    var resultIndustries = [];
                    $('.secondary-search-dropdown').find('ul').empty().append('<li data-industry="all-industries">All Industries</li>');
                    for (var i = data.companies.length - 1; i >= 0; i--) {
                        var industry = data.companies[i].industry,
                            trimIndustry = data.companies[i].industry.replace(/ /g, '').replace(',', '').toLowerCase().toString();

                        if ($.inArray(trimIndustry, resultIndustries) == -1) {
                            resultIndustries.push(trimIndustry);
                            $('.secondary-search-dropdown').find('ul').append('<li data-industry="' + trimIndustry + '">' + data.companies[i].industry + '</li>');
                        }
                    };
                    SGX.search.industryDropdown.click();
                },
                click: function() {
                    $('.secondary-search-dropdown').find('li').on('click', function() {
                        // Filter Results
                        var industry = $(this).data('industry'),
                            text = $(this).text();


                        if ((typeof(industry) !== 'undefined') && (industry != 'all-industries')) {
                            console.log(industry);
                            SGX.search.resetResults();
                            $(this).parents('.button-dropdown').find('.trigger').html(text + '<span class="arrow"></span>');
                            SGX.dropdowns.close();
                            SGX.dropdowns.init();
                            $('.module-results').find('tr.result:not([data-industry="' + industry + '"])').remove();
                            $('.module-results').find('tr.result[data-industry="' + industry + '"]').show();
                            SGX.pagination.reset();
                        } else {
                            console.log('pagination all');
                            SGX.search.resetResults();
                            $(this).parents('.button-dropdown').find('.trigger').html('All Industries <span class="arrow"></span>');
                            SGX.dropdowns.close();
                            SGX.dropdowns.init();
                            SGX.pagination.reset();
                        }
                    });
                    $('.button-customize-display').on('click', function() {
                        console.log(SGX.search.resultsObject);
                    });
                }
            }
        },
        slider: {
            init: function(container, settings) {
                console.log('sliderinit');
                // $(container).html('<div class="module-stock-slider"><div class="slider-bar"></div><div class="stock-bar-container"></div><div class="bar"></div><div class="matches">' + settings.matches + '</div></div>');


                $(container).html('<div class="module-stock-slider"><div class="slider-bar"></div><div class="stock-bar-container"></div><div class="bar"></div><div class="matches"></div></div>');
                // console.log(container);
                if (settings.min < 0) {
                    settings.min = 0;
                }
                // console.log(settings);
                $(container).find('.slider-bar').slider({
                    range: true,
                    min: parseFloat(settings.min, 10),
                    max: parseFloat(settings.max, 10),
                    values: [settings.min, settings.max],
                    slide: SGX.slider.slide
                });
                /*
                var $this = $(container).find('.module-stock-slider'),
                    leftPt = $this.find('.ui-slider-handle').first().position().left,
                    rightPt = $this.find('.ui-slider-handle').last().position().left,
                    stockBarObj = {
                        content: ''
                    };
                var barWidth = 300 / SGX.search.criteriaObject[settings.servername].distributions[0].buckets.length;
                console.log(barWidth);
                for (var i = 0; i < SGX.search.criteriaObject[settings.servername].distributions[0].buckets.length; i++) {


                    if ((i < leftPt) || (i > rightPt)) {
                        stockBarObj.content = stockBarObj.content + '<div class="stock-bar" style="background: ##1e2171; height:' + Math.floor(100 * Math.random()) + '%;width:' + barWidth + 'px;left:' + (i + 1) + 'px;" />';
                    } else {
                        stockBarObj.content = stockBarObj.content + '<div class="stock-bar" style="background: #1e2171; height:' + Math.floor(100 * Math.random()) + '%;width:' + barWidth + 'px;left:' + (i + 1) + 'px;" />';
                    }
                }
                $(container).find('.stock-bar-container').html(stockBarObj.content);*/
            },
            startup: function() {

            },
            slide: function(event, ui) {
                var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                    rightPt = $(this).find('.ui-slider-handle').last().position().left,
                    dataType = $(this).parents('tr').data('type');

                if (ui.value == ui.values[0]) {

                    if (dataType == 'dollar') {
                        $(this).parents('td').prev('td').html('S$' + ui.value + 'mm');
                    } else if (dataType == 'percent') {
                        $(this).parents('td').prev('td').html(ui.value + '%');
                    }
                    $(this).parents('tr.criteria').attr({
                        'data-min': ui.value
                    });
                } else {
                    if (dataType == 'dollar') {
                        $(this).parents('td').next('td').html('S$' + ui.value + 'mm');
                    } else if (dataType == 'percent') {
                        $(this).parents('td').next('td').html(ui.value + '%');
                    }
                    $(this).parents('tr.criteria').attr({
                        'data-max': ui.value
                    });
                }
                console.log('todo');
                /*
                $(event.target).parents('.module-stock-slider').find('.stock-bar').filter(function(idx, elem) {
                    var elemLeft = $(elem).css("left").replace("px", "");
                    if ((elemLeft < leftPt) || (elemLeft > rightPt)) {
                        $(elem).css({
                            'background': '#b0b2cd'
                        });
                    } else {
                        $(elem).css({
                            'background': '#1e2171'
                        });
                    }

                });*/
            }

        },
        tabs: function() {
            // debug.info('SGX.tabs');
            $('.tabbed-content').tabs({
                active: 0,
                show: {
                    effect: "blind",
                    duration: 800
                }
            });
        },
        tooltip: {
            close: function(filter) {
                // Close other tooltips or prep currently active tooltips to be faded out
                $('body').find('.tooltip').removeClass("tooltip-active");
                $('body').find('.tooltip:not(.tooltip-active)').fadeOut(function(filter) {
                    $('body').find('.tooltip:not(.tooltip-active)').remove();
                });
            },
            open: function(modalSettings) {},
            start: function() {
                debug.info('SGX.tooltip.start');
                $('.info').on('mouseenter', function() {
                    // console.log('info mouseenter');

                    var $this = $(this);

                    SGX.tooltip.close();
                    if (!$this.find('.tooltip').length) {

                        $this.append('<div class="tooltip tooltip-active"><div class="tooltip-content">This is the tooltip content</div><div class="tooltip-arrow"></div></div>');
                        var $tooltip = $(this).find('.tooltip'),
                            height = $tooltip.height();
                        $tooltip.css({
                            'top': -height - 10,
                            'right': -$tooltip.width() / 2 + 10
                        });
                        // debug.warn(height);
                        $tooltip.fadeIn();
                    } else {
                        SGX.tooltip.close();
                    }
                });
            }
        }
    };
    SGX.core();
});