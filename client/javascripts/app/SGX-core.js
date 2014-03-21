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
                active: 1,
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
            SGX.dropdowns();
            SGX.tabs();
            SGX.slider.init();
            SGX.tooltip.start();
            

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
        data: {
            get: function(url) {

                // Default Data
                $.ajax({
                    url: 'javascripts/app/search_result.json',
                    dataType: 'json',
                    success: function(data) {
                        // console.log(data);
                        for (var companies in data.results) {
                            var company = data.results[companies];
                            // console.log(company);
                            for (var i = 0; i < company.length; i++) {
                                $('.module-results').find('tbody').append('<tr><td>' + company[i].companyName + '</td><td>' + company[i].code + '</td><td>' + company[i].industry + '</td><td>' + company[i].marketCap + '</td><td>' + company[i].totalRevenue + '</td><td>' + company[i].peRatio + '</td><td>' + company[i].dividendYield + '</div>');
                            }
                        }
                    }
                });

                // JSON call will be made here
                if (typeof(url) !== 'undefined') {
                    $.ajax({
                        url: url,
                        success: function() {
                            SGX.data.success();
                            console.log(data);
                            for (var i = data.length - 1; i >= 0; i--) {
                                data[i]
                            };
                        }
                    });
                }
            },
            success: function() {

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
        },
        error: {
            init: function(content) {
                SGX.modal.open(content, 'modal-error');
            }
        },
        form: {
            checkbox: {
                check: function() {

                },
                init: function() {
                    // debug.info('SGX.form.checkbox.init');
                    $('.checkbox').find('.trigger').on('click', function() {
                        var $this = $(this),
                            $checkbox = $this.parents('.checkbox'),
                            $input = $checkbox.find('input[type="checkbox"]'),
                            checkboxChecked = $checkbox.hasClass("checked");
                        if (checkboxChecked) {
                            $this.parents('.checkbox').removeClass('checked');
                            $input.removeAttr("checked");
                        } else {
                            $this.parents('.checkbox').addClass('checked');
                            $input.attr("checked", "checked");
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
                    console.log(modalSettings.target);
                    modalSettings.confirm({
                        target: modalSettings.target
                    });



                } else if (typeof(type) !== 'undefined') {
                    // Catch all to help with debugging
                    $modal.addClass(type);
                }
                $modal.fadeIn();
            },
            init: function() {
                SGX.modal.close();
                $('#modal').find('.close-button').on('click', function() {

                });
                $('#modal').on('click', function() {
                    SGX.modal.close();
                });
            }
        },
        search: {
            removeCriteria: function(target) {
                // This is a placeholder function in case other functionality needs to be piggy-backed onto the removal of Search Criteria
                // console.log('remove Criteria');
                // console.log(target);
                $(target).fadeOut(function() {
                    $(target).remove();
                });

            },
            init: function() {
                $('td.max').on('click', function(e) {
                    e.stopPropagation();
                    var $target = $(this).parents('tr'),
                        criteraName = $target.find('td').first().text();

                    SGX.modal.open({
                        content: '<p>Do you want to remove ' + criteraName + ' from your search?</p>',
                        type: 'prompt',
                        target: $target,
                        confirm: function(options) {

                            var target = options.target;
                            console.log(target);
                            // target.css({'background': 'red'});
                            $('button.confirm').on('click', target, function(e) {
                                console.log('confirm');
                                console.log(target);
                                SGX.search.removeCriteria(target);
                                console.log('confirmed');
                            });

                        }

                    });
                });
            }
        },
        slider: {
            init: function() {
                $('.module-stock-slider').each(function(idx) {
                    $('.slider-bar').slider({
                        range: true,
                        min: 0,
                        max: 300,
                        values: [75, 200],
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
                // // debug.log(event.target);
                // // debug.log(ui);
                // $("#amount").val("$" + ui.values[0] + " - $" + ui.values[1]);
                // console.log(event.val);
                // $(this).next('.matches').html('')
                var $this = $(this),
                    leftPt = $this.find('.ui-slider-handle').first().position().left,
                    rightPt = $this.find('.ui-slider-handle').last().position().left;


                // debug.info(rightPt);

                // // debug.log(leftPt);
                // // debug.log($(event.target).parents('.module-stock-slider').find('.stock-bar').first().css("left"));
                $(event.target).parents('.module-stock-slider').find('.stock-bar').filter(function(idx, elem) {
                    // // debug.log($(elem).css("left"));
                    // // debug.log(leftPt);
                    var elemLeft = $(elem).css("left").replace("px", "");

                    if ((elemLeft < leftPt) || (elemLeft > rightPt)) {
                        // // debug.log(idx);
                        // // debug.log(elem);
                        $(elem).css({
                            'background': '#b0b2cd'
                        });
                    } else {
                        $(elem).css({
                            'background': '#1e2171'
                        });
                    }

                    // Update max value
                    $next = $this.parents('td').next('td').html('S$' + Math.floor(rightPt * 100) / 100 + 'mm');

                    // Update min value
                    $prev = $this.parents('td').prev('td').html('S$' + Math.floor(leftPt * 100) / 100 + 'mm');
                });
            },
            slide: function(event, ui) {
                var $this = $(this),
                    leftPt = $(this).find('.ui-slider-handle').first().position().left,
                    rightPt = $(this).find('.ui-slider-handle').last().position().left;
                $(event.target).parents('.module-stock-slider').find('.stock-bar').filter(function(idx, elem) {
                    // debug.log($(elem).css("left"));
                    // debug.log(leftPt);
                    var elemLeft = $(elem).css("left").replace("px", "");

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
                    // Update max value
                    $next = $this.parents('td').next('td').html('S$' + Math.floor(rightPt * 100) / 100 + 'mm');

                    // Update min value
                    $prev = $this.parents('td').prev('td').html('S$' + Math.floor(leftPt * 100) / 100 + 'mm');
                });
            }

        },
        tabs: function() {
            // debug.info('SGX.tabs');
            $('.tabbed-content').tabs({
                active: 0
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
                $('.info').on('click', function() {
                    console.log('info click');

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

                $('.button-dropdown').find('> .trigger:not(.open)').on('click', function() {
                    var $this = $(this),
                        $button = $this.parents('.button-dropdown');
                    $dropdown = $button.find('.dropdown');

                    if ($button.hasClass("open")) {
                        $button.removeClass("open");
                    } else {
                        $button.addClass("open");
                    }
                });

            }

        }

    };
    SGX.core();









});