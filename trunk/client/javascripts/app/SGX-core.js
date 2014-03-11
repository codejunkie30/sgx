// This is the modular wrapper for any page
define(['jquery', 'jquicore', 'jquiwidget', 'accordion', 'slider', 'tabs', 'debug'], function($, SGX) {

    // Nested namespace uses initial caps for AMD module references, lowercased for namespaced objects within AMD modules
    // Instead of console.log() use Paul Irish's debug.log()
    SGX = {
        startup: function() {
            $('td.max').on('click', function() {
                SGX.modal.open('<p>Are you sure you want to close?</p>');
            });

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
            debug.info('SGX.Core');
            SGX.accordion();
            SGX.modal.init();
            SGX.dropdowns();
            SGX.tabs();
            SGX.slider();
            SGX.tooltip();
            SGX.startup();
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
        data: {
            get: function(url) {
                // JSON call will be made here
                if (typeof(url) !== 'undefined') {
                    $.ajax({
                        url: url,
                        success: function() {
                            SGX.data.success();
                        }
                    });
                }
            },
            success: function() {

            }
        },
        dropdowns: function() {
            // debug.info('SGX.dropdowns');
            $('html').on('click', function(e) {
                if (!$(e.toElement).parents().hasClass("button-dropdown")) {
                    $(document).find('.button-dropdown').removeClass('open');
                }
            });

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
        modal: {
            close: function() {
                $('#modal').fadeOut(function() {
                    $(this).html('<div class="modal-container"><div class="modal-content"><p>Modal is empty.</p></div><div class="bg" /></div>');
                });

            },
            open: function(content, type) {
                var $modal = $('#modal');
                $modal.html('<div class="modal-container"><div class="modal-content" /><div class="bg" /></div>');

                $modal.find('.modal-content').html(content);
                if (type !== 'undefined') {
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
        slider: function() {
            // debug.info('SGX.slider');
            $('.module-stock-slider').each(function(idx) {
                $('.slider-bar').slider({
                    range: true,
                    min: 0,
                    max: 300,
                    values: [75, 200],
                    create: function(event, ui) {},
                    slide: function(event, ui) {
                        var leftPt = $(this).find('.ui-slider-handle').first().position().left,
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
                        });
                    },
                    change: function(event, ui) {
                        // // debug.log(event.target);
                        // // debug.log(ui);
                        // $("#amount").val("$" + ui.values[0] + " - $" + ui.values[1]);
                        var $this = $(this),
                            leftPt = $this.find('.ui-slider-handle').first().position().left,
                            rightPt = $this.find('.ui-slider-handle').last().position().left;


                        debug.info(rightPt);

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
                    }
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
        tabs: function() {
            // debug.info('SGX.tabs');
            $('.tabbed-content').tabs({
                active: 0
            });
        },
        tooltip: function() {
            $('.info').on('click', function() {
                var $this = $(this);
                $this.append('<div class="tooltip"><div class="tooltip-content">This is the tooltip content</div></div>');
                var $tooltip = $(this).find('.tooltip'),
                    height = $tooltip.height();
                $tooltip.css({
                    'top': -height
                });
                // debug.warn(height);
                $tooltip.fadeIn();
            });

            // debug.info('SGX.dropdowns');
            $('body').on('click', function(e) {
                // debug.log(e.toElement);
                if (!$(e.toElement).parents().hasClass("button-dropdown")) {
                    $(document).find('.dropdown').removeClass('open');
                }
                // $(document).find('.dropdown').removeClass('open');
            });
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

    };
    SGX.core();













});