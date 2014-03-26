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

            SGX.data.get();

            // Initialize custom dropdowns
            SGX.dropdowns();

            // Initialize jquery tabs
            SGX.tabs();
            SGX.slider.startup();
            SGX.tooltip.start();
            SGX.financials.init();

            // Initial Search Criteria components
            SGX.search.init();

            // Initialize form components
            SGX.form.init();

            // Do default Screener search and assign event handlers
            $('.search-submit').on('click', function(e) {
                e.preventDefault();
                SGX.search.sendSearch();
            });
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
        data: {
            get: function(url) {
                // Default Data
                $.ajax({
                    url: 'javascripts/app/search_result.json',
                    dataType: 'json',
                    success: function(data) {
                        console.log(data);
                        for (var companies in data.results) {
                            var company = data.results[companies];
                            console.log(company);
                            $('.module-results').find('tbody').remove();
                            $('.module-results').find('table').append('<div class="tempholder" />');
                            for (var i = 0; i < company.length; i++) {
                                // console.log(i);
                                // $('.module-results').find('td.placeholder').parents('tr').remove();

                                $('.module-results').find('.tempholder').append('<tr><td>' + company[i].companyName + '</td><td>' + company[i].code + '</td><td>' + company[i].industry + '</td><td>' + company[i].marketCap + '</td><td>' + company[i].totalRevenue + '</td><td>' + company[i].peRatio + '</td><td>' + company[i].dividendYield + '</td></tr>');
                            }
                        }
                        $('.module-results').find('.pager').html('');
                        var resultsLength = $('.module-results').find('tr').length - 1,
                            resultsRows = $('.module-results').find('.tempholder tr');


                        // Update results
                        $('.module-results').find('.results').html(resultsLength + ' total matches');


                        for (var i = 0, y = 1; i < resultsRows.length; i += 10, y++) {
                            if (y == 1) {
                                resultsRows.slice(i, i + 10).wrapAll('<tbody data-panel-count="' + y + '" class="active" />');
                            } else {
                                resultsRows.slice(i, i + 10).wrapAll('<tbody data-panel-count="' + y + '" />');
                            }
                            // Add to pager
                            $('.module-results').find('.pager').append('<li><a href="#">' + y + '</a></li>');
                        }
                        var $pager = $('.module-results').find('.pager');
                        $pager.prepend('<li><a class="prev" href="#">Prev</a></li>');
                        $pager.append('<li><a class="next" href="#">Next</a></li>');

                        // Pager click handler
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
                        })

                        $('.module-results').find('tbody').unwrap();

                    }
                });
            }
        },
        dropdowns: function() {
            // debug.info('SGX.dropdowns');


            $('.button-dropdown').find('> .trigger:not(.open)').on('click', function() {

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
        },
        error: {
            init: function(content) {
                SGX.modal.open(content, 'modal-error');
            }
        },
        financials: {
            addCriteria: function() {

            },
            removeCriteria: function() {

            },
            chart: function(settings) {
                console.log(settings);

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
        search: {
            addCriteria: function(target, config) {
                // console.log(target);
                // console.log(config);
                var count = $('.search-criteria').find('.criteria').length,
                    max = config.max,
                    min = config.min;

                // console.log('addCriteria');

                // Set values for min max depending on the type
                if ((typeof(config.type) != 'undefined') && (config.type == 'dollar')) {
                    max = 'S$' + max + 'mm';
                    min = 'S$' + min + 'mm';
                } else if ((typeof(config.type) != 'undefined') && (config.type == 'percent')) {
                    max = max + '%';
                    min = min + '%';
                }
                // If there are less than 5 criteria at the moment, the user can add another search criteria
                if ((typeof(config.type) != 'undefined') && (config.type != 'industry')) {
                    $('.search-criteria').find('tbody').append('<tr class="criteria" style="display:none;" data-type="' + config.type + '" data-name="' + config.name + '"><td>' + config.name + '</td><td>' + min + '</td><td class="criteria-slider">Slider</td><td class="max">' + max + '</td></tr>');
                    var sliderTarget = $('.search-criteria').find('tr.criteria').last().find('.criteria-slider');
                    SGX.slider.init(sliderTarget, config);

                } else if ((typeof(config.type) != 'undefined') && (config.type == 'industry')) {
                    // It is the Industry Criteria, so add it accordingly
                    $('.search-criteria').find('tbody').append('<tr class="criteria" data-name="Industry"><td><span class="info">Industry</span></td><td colspan="2"><div class="button-dropdown"><div class="trigger">Select Sector &amp; Industry<span class="arrow"></span></div><div class="dropdown"><ul><li>Basic Materials</li><li>Conglomerates</li><li>Consumer Goods</li><li>Financial</li><li>Healthcare</li><li>Industrial Goods</li><li>Services</li><li>Technology</li><li>Utilities</li></ul></div></div></td><td class="max"></td></tr>');
                    SGX.dropdowns();
                }
                $('.search-criteria').find('tr.criteria').last().fadeIn();
                SGX.search.register.removeClickHandler();
                // console.log(count);
            },
            removeCriteria: function(target) {
                // This is a placeholder function in case other functionality needs to be piggy-backed onto the removal of Search Criteria
                // console.log('remove Criteria');

                $(target).fadeOut(function() {
                    $(target).remove();
                });
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
                                console.log(target);
                                console.log(criteraName);
                                // target.css({'background': 'red'});
                                $('button.confirm').on('click', target, function(e) {
                                    console.log('confirm');
                                    console.log(target);
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
            sendSearch: function() {

                // Iterate through search criteria and create a request for server
                $('.search-criteria').find('tr').each(function() {
                    console.log('search');
                });

                $.ajax({
                    url: 'javascripts/app/data-distributions.json',
                    success: function(data) {
                        SGX.search.parseSearchResponse(data);
                    }
                })
            },
            parseSearchResponse: function(data) {
                console.log('parse Search response');
                console.log(data);
            },
            init: function() {
                $('.reset').on('click', function() {

                });
                $('.editSearchB').find('.trigger').on('click', function(e) {
                    // console.log('add trigger');
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
                SGX.search.register.removeClickHandler();
            }
        },
        slider: {
            init: function(container, settings) {
                $(container).html('<div class="module-stock-slider"><div class="slider-bar"></div><div class="stock-bar-container"></div><div class="bar"></div><div class="matches">0 matches</div></div>');

                // console.log(settings);

                $(container).find('.slider-bar').slider({
                    range: true,
                    min: parseFloat(settings.min, 10),
                    max: parseFloat(settings.max, 10),
                    values: [settings.min, settings.max],
                    create: function(event, ui) {},
                    slide: SGX.slider.slide,
                    change: SGX.slider.change
                });
                var $this = $(container).find('.module-stock-slider');
                console.log($this);
                var leftPt = $this.find('.ui-slider-handle').first().position().left,
                    rightPt = $this.find('.ui-slider-handle').last().position().left;
                var stockBarObj = {
                    content: ''
                };
                for (var i = 0; i < 49; i++) {
                    if (((6 * i) < leftPt) || ((6 * i) > rightPt)) {
                        // $('.stock-bar-container').append('<div class="stock-bar" style="background: #b0b2cd; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />');
                        stockBarObj.content = stockBarObj.content + '<div class="stock-bar" style="background: ##1e2171; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />';
                    } else {
                        // $('.stock-bar-container').append('<div class="stock-bar" style="background: #1e2171; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />');
                        stockBarObj.content = stockBarObj.content + '<div class="stock-bar" style="background: #1e2171; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />';
                    }
                }
                $(container).find('.stock-bar-container').html(stockBarObj.content);
            },
            startup: function() {
                $('.module-stock-slider').each(function(idx) {
                    var max = $(this).parents('tr').attr('data-max'),
                        min = $(this).parents('tr').attr('data-min');
                    if (typeof(min) != 'undefined') {
                        // console.log(min);
                        min;
                    } else {
                        min = 0;
                    }
                    if (typeof(max) != 'undefined') {
                        // console.log(max);
                        max;
                    } else {
                        max = 300;
                    }
                    $('.slider-bar').slider({
                        range: true,
                        min: parseFloat(min, 10),
                        max: parseFloat(max, 10),
                        values: [min, max],
                        create: function(event, ui) {},
                        slide: SGX.slider.slide,
                        change: SGX.slider.change
                    });
                    var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                        rightPt = $(this).find('.ui-slider-handle').last().position().left;
                    var stockBarObj = {
                        idx: idx,
                        content: ''
                    };
                    for (var i = 0; i < 49; i++) {
                        if (((6 * i) < leftPt) || ((6 * i) > rightPt)) {
                            // $('.stock-bar-container').append('<div class="stock-bar" style="background: #b0b2cd; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />');
                            stockBarObj.content = stockBarObj.content + '<div class="stock-bar" style="background: #b0b2cd; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />';
                        } else {
                            // $('.stock-bar-container').append('<div class="stock-bar" style="background: #1e2171; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />');
                            stockBarObj.content = stockBarObj.content + '<div class="stock-bar" style="background: #1e2171; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />';
                        }
                    }
                    $(this).find('.stock-bar-container').html(stockBarObj.content);
                });
            },
            change: function(event, ui) {
                var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                    rightPt = $(this).find('.ui-slider-handle').last().position().left;
                // $(event.target).css({'background':'red'});
                // console.log(event);
                // console.log($(ui.handle).index());
                $(event.target).parents('.module-stock-slider').find('.stock-bar').filter(function(idx, elem) {
                    var elemLeft = $(elem).css("left").replace("px", ""),
                        dataType = $(this).parents('tr').attr('data-label-type');

                    if ((elemLeft < leftPt) || (elemLeft > rightPt)) {
                        $(elem).css({
                            'background': '#b0b2cd'
                        });
                    } else {
                        $(elem).css({
                            'background': '#1e2171'
                        });
                    }
                    if (ui.value == ui.values[0]) {
                        // Min
                        // console.log('min');
                        if (dataType == 'dollar') {
                            $(this).parents('td').prev('td').html('S$' + ui.value + 'mm');
                        } else if (dataType == 'percent') {
                            $(this).parents('td').prev('td').html(ui.value + '%');
                        }

                    } else {
                        // Max
                        // console.log('max');
                        // $(this).parents('td').next('td').html('S$' + ui.value + 'mm');
                        if (dataType == 'dollar') {
                            $(this).parents('td').next('td').html('S$' + ui.value + 'mm');
                        } else if (dataType == 'percent') {
                            $(this).parents('td').next('td').html(ui.value + '%');
                        }
                    }
                });
            },
            slide: function(event, ui) {
                var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                    rightPt = $(this).find('.ui-slider-handle').last().position().left;
                $(event.target).parents('.module-stock-slider').find('.stock-bar').filter(function(idx, elem) {
                    // debug.log($(elem).css("left"));
                    // debug.log(leftPt);
                    var elemLeft = $(elem).css("left").replace("px", ""),
                        dataType = $(this).parents('tr').attr('data-label-type');

                    // console.log(dataType);
                    if ((elemLeft < leftPt) || (elemLeft > rightPt)) {
                        // debug.log(idx);
                        // debug.log(elem);
                        $(elem).css({
                            'background': '#b0b2cd'
                        });
                    } else {
                        $(elem).css({
                            'background': '#1e2171'
                        });
                    }
                    if (ui.value == ui.values[0]) {
                        // Min
                        // console.log('min');
                        if (dataType == 'dollar') {
                            $(this).parents('td').prev('td').html('S$' + ui.value + 'mm');
                        } else if (dataType == 'percent') {
                            $(this).parents('td').prev('td').html(ui.value + '%');
                        }

                    } else {
                        // Max
                        // console.log('max');
                        // $(this).parents('td').next('td').html('S$' + ui.value + 'mm');
                        if (dataType == 'dollar') {
                            $(this).parents('td').next('td').html('S$' + ui.value + 'mm');
                        } else if (dataType == 'percent') {
                            $(this).parents('td').next('td').html(ui.value + '%');
                        }
                    }
                });
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
                    console.log('info mouseenter');

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


                    /*$('body').on('click', function(e) {
                        SGX.tooltip.close();
                    });*/
                });


                // debug.info('SGX.dropdowns');



            }

        }

    };
    SGX.core();









});