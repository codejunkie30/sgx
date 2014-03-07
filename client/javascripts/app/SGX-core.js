// This is the modular wrapper for any page
define(['jquery', 'jquicore', 'jquiwidget', 'accordion', 'slider', 'tabs', 'debug'], function($, SGX) {

    // Nested namespace uses initial caps for AMD module references, lowercased for namespaced objects within AMD modules
    // Instead of console.log() use Paul Irish's debug.log()
    SGX = {
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
            SGX.modal.init();
            SGX.dropdowns();
            SGX.tabs();
            SGX.slider();
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
        dropdowns: function() {
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
        },
        modal: {
            close: function() {
                $('#modal').find('.close-button').on('click', function() {
                    $('#modal').fadeOut();
                });
            },
            init: function() {
                SGX.modal.close();
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
                        var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                            rightPt = $(this).find('.ui-slider-handle').last().position().left;


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
        }

    };
    SGX.core();













});