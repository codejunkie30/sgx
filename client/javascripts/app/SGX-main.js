define(['jquery', 'jquicore', 'jquiwidget', 'accordion', 'slider', 'tabs'], function($) {
    jQuery(function($) {
        console.log('test2');
        $('.tabbed-content').tabs({
            active: 0
        });
        $('body').on('click', function(e) {
            console.log(e.toElement);
            if (!$(e.toElement).parents().hasClass("button-dropdown")) {
                $(document).find('.dropdown').removeClass('open');
            }
            // $(document).find('.dropdown').removeClass('open');
        });

        $(".module-accordion").accordion({
            active: 1,
            animated: 'easeOutExpo',
            autoHeight: false,
            collapsible: true,
            event: 'click',
        });

        $('.checkbox').on('click', function() {
            var $this = $(this),
                checked = $this.hasClass("checked");
            if (checked === true) {
                console.log('checked');
                $this.removeClass("checked");
            } else {
                console.log('not checked');
                $this.addClass("checked");
            }
        });
        $('.button-dropdown').find('> .trigger:not(.open)').on('click', function() {
            var $this = $(this),
                $button = $this.parents('.button-dropdown');
            $dropdown = $button.find('.dropdown');

            if ($dropdown.hasClass("open")) {
                $dropdown.removeClass("open");
            } else {
                $dropdown.addClass("open");
            }

        });
        $('.module-stock-slider').each(function() {
            // $('.bar').slider();


            $('.slider-bar').slider({
                range: true,
                min: 0,
                max: 300,
                values: [75, 200],
                create: function(event, ui) {},
                slide: function (event, ui) {
                    /*var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                        rightPt = $(this).find('.ui-slider-handle').last().position().left;
                    $(event.target).parents('.module-stock-slider').find('.stock-bar').filter(function(idx, elem) {
                        console.log($(elem).css("left"));
                        console.log(leftPt);
                        var elemLeft = $(elem).css("left").replace("px", "");

                        if ((elemLeft < leftPt) || (elemLeft > rightPt)) {
                            console.log(idx);
                            console.log(elem);
                            $(elem).css({
                                'background': 'gray'
                            });
                        } else {
                            $(elem).css({
                                'background': 'blue'
                            });
                        }
                    });*/
                },
                change: function(event, ui) {
                    // console.log(event.target);
                    // console.log(ui);
                    // $("#amount").val("$" + ui.values[0] + " - $" + ui.values[1]);
                    var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                        rightPt = $(this).find('.ui-slider-handle').last().position().left;


                    // console.log(leftPt);
                    // console.log($(event.target).parents('.module-stock-slider').find('.stock-bar').first().css("left"));
                    $(event.target).parents('.module-stock-slider').find('.stock-bar').filter(function(idx, elem) {
                        // console.log($(elem).css("left"));
                        // console.log(leftPt);
                        var elemLeft = $(elem).css("left").replace("px", "");

                        if ((elemLeft < leftPt) || (elemLeft > rightPt)) {
                            // console.log(idx);
                            // console.log(elem);
                            $(elem).css({
                                'background': 'gray'
                            });
                        } else {
                            $(elem).css({
                                'background': 'blue'
                            });
                        }
                    });

                    /*$(this).parents('.module-stock-slider').find('.stock-bar').each(function () {
                        var $this = $(this);
                        var left = $this.attr("left");

                        console.log($this);
                        console.log($this.attr("left"));
                        if ($this.attr('left') > leftPt) {
                            $this.css({'background':'red'});
                        } else {
                            $this.css({'background':'green'});
                        }
                    });*/

                    /*$stockContainer.find('.stock-bar').filter(function(e) {
                        return () || ($stockContainer.find('.stock-bar').attr('left') > rightPt);
                    });*/
                    /*$stocks.addClass("test");*/
                }
            });
            var leftPt = $(this).find('.ui-slider-handle').first().position().left,
                rightPt = $(this).find('.ui-slider-handle').last().position().left;

            // console.log(leftPt + ' ' + rightPt);

            for (var i = 0; i < 49; i++) {
                if (((6 * i) < leftPt) || ((6 * i) > rightPt)) {
                    $('.stock-bar-container').append('<div class="stock-bar" style="background: gray; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />');
                } else {
                    $('.stock-bar-container').append('<div class="stock-bar" style="background: blue; height:' + Math.floor(100 * Math.random()) + '%;left:' + 2 * i + '%;" />');
                }

            }
        });


    });

});