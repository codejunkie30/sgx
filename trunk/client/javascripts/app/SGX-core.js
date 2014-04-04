// This is the modular wrapper for any page
define(['jquery', 'underscore', 'jquicore', 'jquiwidget', 'jquimouse', 'accordion', 'slider', 'tabs', 'debug'], function($, _, SGX) {
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
        companyProfile: {
            getAlphaFactor: function(value, type) {
                if ((typeof(value) != 'undefined') && (typeof(type) != 'undefined')) {
                    var requestValue = 0;
                    if (value == 'per-80') {
                        requestValue = 4;
                    } else if (value == 'per-60') {
                        requestValue = 3;
                    } else if (value == 'per-40') {
                        requestValue = 2;
                    } else if (value == 'per-20') {
                        requestValue = 1;
                    }
                    console.log(value);

                    var request = {};
                    request[type] = requestValue;
                    console.log(request);

                    $.ajax({
                        url: 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/search/alphaFactors',
                        type: 'GET',
                        dataType: 'jsonp',
                        jsonpCallback: 'jsonp',
                        data: {
                            'json': JSON.stringify(request)
                        },
                        contentType: 'application/json; charset=UTF-8',
                        success: function(data) {
                            console.log(data);
                        },
                        error: function(data, status, er) {
                            console.log(data);
                        }
                    });
                } else {
                    // Value and type are not defined
                }

            },
            getChart: function(id) {
                console.log(id);
                // SGX.companyProfile.getHolders('G07');
                $.ajax({
                    async: false,
                    url: 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/priceHistory',
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify({
                            "id": id
                        })
                    },
                    contentType: 'application/json; charset=UTF-8',
                    error: function(data) {
                        console.error('getChart error');

                        if (data.status == 500) {
                            // Server side error
                            console.warn('500 error');
                        } else if (data.status == 404) {
                            // Not found
                            console.warn('404 error');
                        } else {
                            console.error(data.status);
                        }
                    },
                    success: function(data) {
                        console.log('getChart w/ ID: ' + id);
                        console.log(data);
                        var priceArry = [],
                            volArry = [];

                        for (var i = 0; i < data.price.length; i++) {
                            if (i > 0) {
                                var prev = i - 1;
                                if (data.price[i].date < data.price[prev].date) {
                                    // console.log(i + ' this one is not larger than the previous date');
                                }

                            }

                            // console.log(data.price[i].date);
                            var pricePt = [data.price[i].date, data.price[i].value];
                            priceArry.push(pricePt);

                        };
                        for (var i = 0; i < data.volume.length; i++) {
                            if (i > 0) {
                                var prev = i - 1;
                                if (data.volume[i].date < data.volume[prev].date) {
                                    // console.log(i + ' this Volume one is not larger than the previous date');
                                }

                            }


                            var volPt = [data.volume[i].date, data.volume[i].value];
                            volArry.push(volPt);

                        };
                        var sortedPriceArry = _.sortBy(priceArry, function(obj) {
                            return obj[0];
                        });
                        var sortedVolArry = _.sortBy(volArry, function(obj) {
                            return obj[0];
                        });

                        console.log(volArry);



                        SGX.stocks.areaGraph(sortedPriceArry);
                        SGX.stocks.volGraph(sortedVolArry);
                        // SGX.stocks.areaGraph(newArry);
                        // SGX.companyProfile.getHolders(id);
                        SGX.companyProfile.getCompany(id);
                    }
                });
            },
            getCompany: function(id) {
                // http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company


                $.ajax({
                    url: 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company',
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify({
                            "id": id
                        })
                    },
                    contentType: 'application/json; charset=UTF-8',
                    error: function(data) {

                        if (data.status == 500) {
                            // Server side error
                            console.warn('500 error');
                        } else if (data.status == 404) {
                            // Not found
                            console.warn('404 error');
                        } else {
                            // console.info('\n\n' + 'services error');
                            console.info(data.status + url + '\n\n');
                        }
                    },
                    success: function(data) {
                        console.log('getCompany w/ ID: ' + id);
                        console.log(data);

                        // News
                        if ((typeof(data) != 'undefined') && (typeof(data.keyDevs) != 'undefined')) {

                            $('.stock-events').find('ul').empty();
                            for (var i = 0; i < data.keyDevs.length; i++) {
                                var letter = parseFloat(i + 1, 10);
                                // console.log(letter)
                                // console.log(toLetters(letter));
                                $('.stock-events').find('ul').append('<li><div class="icon">' + toLetters(letter) + '</div><a href="#">' + data.keyDevs[i].headline + '</a></li>');
                            };

                            function toLetters(num) {
                                "use strict";
                                var mod = num % 26,
                                    pow = num / 26 | 0,
                                    out = mod ? String.fromCharCode(64 + mod) : (--pow, 'Z');
                                return pow ? toLetters(pow) + out : out;
                            }
                        }
                        // Company Info
                        $.each(data.company.companyInfo, function(index, value) {
                            // console.log(index);
                            if ($('.' + index).length) {
                                if (!index == 'companyWebsite') {
                                    $('.' + index).html(value);
                                } else if (index == 'companyName') {
                                    if ((value.length > 37) && (value.length < 54)) {
                                        $('.' + index).css({
                                            'font-size': '28px'
                                        });
                                    } else if (value > 54) {
                                        $('.' + index).css({
                                            'font-size': '20px'
                                        });
                                    }
                                    $('.' + index).css({
                                        'opacity': 0
                                    });
                                    $('.' + index).text(value);
                                    $('.' + index).animate({
                                        'opacity': 1
                                    }, 500, function() {
                                        // $('.' + index).removeAttr("style");
                                    });
                                } else if (index == 'companyWebsite') {
                                    $('.' + index).attr({
                                        'href': 'http://' + value,
                                        'alt': value
                                    });
                                } else if (index == 'fiscalYearEnd') {
                                    var monthNames = ["January", "February", "March", "April", "May", "June",
                                        "July", "August", "September", "October", "November", "December"
                                    ];


                                    var d = new Date(value);
                                    var year = d.getUTCFullYear(),
                                        month = monthNames[d.getMonth()],
                                        day = d.getUTCDay();
                                    $('.' + index).html(day + ' ' + month + ' ' + year);

                                } else {
                                    $('.' + index).css({
                                        'opacity': 0
                                    });
                                    $('.' + index).html(value);
                                    $('.' + index).animate({
                                        'opacity': 1
                                    }, 500, function() {
                                        // $('.' + index).removeAttr("style");
                                    });
                                }

                            } else {
                                console.log('could not find ' + index);
                            }

                        });

                        // Holders
                        var holders = '';

                        $('body').find('.topHolders').empty();
                        for (var i = 0; i < data.holders.holders.length; i++) {
                            console.log(i);
                            if (i == 1) {
                                holders = data.holders.holders[i].name.toString();
                            } else {
                                holders = holders + ', ' + data.holders.holders[i].name.toString();
                            }
                        }
                        $('body').find('.topHolders').append(holders);

                        //////////////////

                        $('body').find('.numberSharesHeld').empty();
                        for (var i = 0; i < data.holders.holders.length; i++) {
                            if (i == 1) {
                                holders = data.holders.holders[i].shares.toString();
                            } else {
                                holders = holders + ', ' + data.holders.holders[i].shares.toString();
                            }
                        }
                        $('body').find('.numberSharesHeld').append(holders);

                        //////////////////

                        $('body').find('.percentCommonStock').empty();
                        for (var i = 0; i < data.holders.holders.length; i++) {
                            if (i == 1) {
                                holders = data.holders.holders[i].percent.toString();
                            } else {
                                holders = holders + ', ' + data.holders.holders[i].percent.toString();
                            }
                        }
                        $('body').find('.percentCommonStock').append(holders);

                        //////////////////

                        SGX.companyProfile.getPrice(id);
                    }
                });
            },
            getPrice: function(id) {
                $.ajax({
                    url: 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/price',
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify({
                            "id": id
                        })
                    },
                    contentType: 'application/json; charset=UTF-8',
                    error: function(data) {

                        if (data.status == 500) {
                            // Server side error
                            console.warn('500 error');
                        } else if (data.status == 404) {
                            // Not found
                            console.warn('404 error');
                        } else {
                            // console.info('\n\n' + 'services error');
                            console.info(data.status + '\n\n');
                        }
                    },
                    success: function(data) {
                        console.log('getPrice');
                        console.log(data);

                        var change = data.price.change,
                            price = data.price.lastPrice,
                            date = new Date(data.price.currentDate),
                            year = date.getUTCFullYear(),
                            monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "OCt", "Nov", "Dec"],
                            month = monthNames[date.getMonth()],
                            day = date.getUTCDay(),
                            dateFormatted = day + '/' + month + '/' + year;

                        console.log(change + ' ' + price + ' ' + dateFormatted);
                    }
                });
            },
            getTest: function(url, id) {
                console.log('getTest');
                // http://ec2-54-82-16-73.compute-1.amazonaws.com/company
                $.ajax({
                    url: url,
                    type: 'POST',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify({
                            "id": id
                        })
                    },
                    contentType: 'application/json; charset=UTF-8',
                    error: function(data) {

                        if (data.status == 500) {
                            // Server side error
                            console.warn('500 error');
                        } else if (data.status == 404) {
                            // Not found
                            console.warn('404 error');
                        } else {
                            // console.info('\n\n' + 'services error');
                            console.info(data.status + url + '\n\n');
                        }
                    },
                    success: function(data) {
                        // console.log('getTest w/ ID: A7S');
                        // console.log(data);
                        console.log(url + ' success')
                    }
                });
            },
            startup: function(id) {

                var intraDayPrice = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/price',
                    companyInfo = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/info',
                    companyHolders = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/holders',
                    keyDevelopments = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/keyDevs',
                    company = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company',
                    priceVolume = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/priceHistory',

                    // These Services are not working locally for me
                    alphaFactor = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/search/alphaFactors',
                    companyFinancials = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/financials',
                    searchScreener = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/search/screener',
                    relatedCompanies = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/relatedCompanies';


                // This is working
                if (typeof(id) != 'undefined') {
                    SGX.companyProfile.getChart(id);
                } else {
                    SGX.companyProfile.getChart("A7S");
                }


                // SGX.companyProfile.getNews("G07");
                // SGX.companyProfile.getAlphaFactor();

                // Alpha factor structure
                /*{
              "analystExpectations": int,
                "capitalEfficiency": int,
                "earningsQuality": int,
                "historicalGrowth": int,
                "priceMomentum": int,
                "size": int,
                "valuation": int,
                "volatility": int
}*/

                /*var id = {
                    "id": "G07"
                };
                $.ajax({
                    url: "http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/priceHistory",
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify(id)
                    },
                    contentType: 'application/json; charset=UTF-8',
                    success: function(data) {
                        console.log(data);
                    },
                    error: function(data, status, er) {
                        console.log(data);
                    }
                });*/

                // SGX.companyProfile.getInfo('G07');


                var sortObjectByKey = function(obj) {
                    var keys = [];
                    var sorted_obj = {};

                    for (var key in obj) {
                        if (obj.hasOwnProperty(key)) {
                            keys.push(key);
                        }
                    }

                    // sort keys
                    keys.sort();

                    // create new array based on Sorted Keys
                    jQuery.each(keys, function(i, key) {
                        sorted_obj[key] = obj[key];
                    });

                    return sorted_obj;
                };

                /*$.ajax({
                    url: companyInfo,
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify(data)
                    },
                    contentType: 'application/json; charset=UTF-8',
                    error: function(data) {
                        alert('error');
                    },
                    success: function(data) {
                        console.log('company info');
                        console.log(data.companyInfo);
                        $.each(data.companyInfo, function(index, value) {
                            // console.log(index);
                            if ($('.' + index).length) {
                                $('.' + index).html(value);
                            } else {
                                console.log('could not find ' + index);
                            }

                        });
                        for(i = 0; i < data.companies.length; i++) {
                            // console.log(data)
                            // $('.tabbed-content').find('')
                        }

                        // $('.open-price')

                    }
                });*/

                var companyHolders = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/company/holders',
                    intraDayPrice = 'http://ec2-54-82-16-73.compute-1.amazonaws.com/sgx/price';
                var data = {
                    "id": "SK6U"
                };
                /**/

                // This is for the recent news
                // This is giving a 400 error, but that is how I was supposed to request data


                // This is giving a 400 error, but that is how I was supposed to request data
                // Draw stock chart
                /*$.ajax({
                    url: intraDayPrice,
                    type: 'GET',
                    dataType: 'jsonp',
                    jsonpCallback: 'jsonp',
                    data: {
                        'json': JSON.stringify(data)
                    },
                    contentType: 'application/json; charset=UTF-8',
                    error: function(data) {
                        console.log('error');
                    },
                    success: function(data) {
                        console.log(data);
                        

                    }
                });*/

            }
        },
        routing: function() {
            /*
            N9LU - This one should make avgbrokerReq and targetPriceNum show up for the number of company employees and Capital Consensus Estimates
            S30
            */
            /*if() {

            }*/
            debug.info('SGX.routing');

            $('a').on('click', function(e) {
                // alert('test');
                e.preventDefault();
                var companyTickerID = $(this).data("company-code"),
                    url = $(this).attr('href');


                console.log(companyTickerID);

                var storageObject = {
                    id: companyTickerID
                };
                localStorage.setItem('SGXclient', JSON.stringify(storageObject));
                console.log('storageObject: ', storageObject);
                console.log(window.location);
                if(url.indexOf("html") != -1) {
                    console.log(window.location)
                } else {
                    console.log('no html');
                    console.log(window.location + '.html');
                    url = url + '.html';
                    console.log(url);
                    window.location = url;
                }
                // window.location = url;
            });

            // Put the object into storage
            // localStorage.setItem('SGXclient', JSON.stringify(storageObject));

            // Retrieve the object from storage
            // var storageObject = localStorage.getItem('storageObject');


        },
        core: function(flag) {

            SGX.routing();

            SGX.startup();
            debug.info('SGX.Core');
            SGX.closeAll();
            SGX.accordion();
            SGX.modal.init();

            // Initialize custom dropdowns
            SGX.dropdowns.init();

            // Initialize jquery tabs
            SGX.tabs();
            SGX.slider.startup();
            SGX.tooltip.start();
            

            // Initial Search Criteria components
            SGX.search.init();

            // Initialize form components
            SGX.form.init();

            // SGX.stocks.init();
            // Get Company Profile Info
            if ($('.company-tearsheet-page').length) {
                console.log('company-tearsheet');
                var storageObject = JSON.parse(localStorage.getItem('SGXclient'));
                // console.log(JSON.stringify);
                console.warn(storageObject.id);
                if (typeof(storageObject.id) !== 'undefined') {
                    // console.log(storageObject.id);
                    SGX.companyProfile.startup(storageObject.id);
                }
            }
            if ($('.financials-page').length) {
                SGX.financials.init();
            }

            $('.alpha-factors').find('.slider').on('click', function() {
                var value = $(this).find('.bar-progress').attr("class"),
                    value = value.replace("bar-progress ", ""),
                    type = $(this).data('factor-type');

                SGX.companyProfile.getAlphaFactor(value, type);
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
                    console.log('dropdown click');
                    var text = $(this).text();
                    $(this).parents('.button-dropdown').find('.trigger').html(text + '<span class="arrow"></span>');
                    $(document).find('.button-dropdown').removeClass('open');
                    $(this).siblings().removeAttr('class');
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


                    /*var count = $('.search-criteria').find('.criteria').length;
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
                    }*/
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
                    type = modalSettings.type,
                    target = modalSettings.target;

                $modal.html('<div class="modal-container"><div class="modal-content" /><div class="modal-close"></div><div class="bg" /></div>');
                var $modalContent = $modal.find('.modal-content');
                if (typeof(content) !== 'undefined') {
                    $modal.find('.modal-content').html(content);
                }

                if ((typeof(type) !== 'undefined') && (type == 'prompt')) {

                    // If prompt modal, we need to add buttons for confirming the user's action
                    debug.info('Prompt');
                    $modal.addClass(type);
                    $modalContent.append('<div class="button confirm">Confirm</div><div class="button cancel">Cancel</div>').end();
                    // console.log(modalSettings.target);
                    modalSettings.confirm({
                        target: target
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
            industryDropdown: {
                populate: function(data) {
                    var resultIndustries = [];
                    $('.secondary-search-dropdown').find('ul').empty().append('<li data-industry="all-industries">All Industries</li>');
                    for (var i = data.companies.length - 1; i >= 0; i--) {
                        var industry = data.companies[i].industry;

                        // console.log(industry);
                        if ($.inArray(industry, resultIndustries) == -1) {
                            resultIndustries.push(industry);
                            // $('.secondary-search-dropdown').find('ul').append('<li data-industry="' + trimIndustry + '">' + data.companies[i].industry + '</li>');
                        }
                    };
                    // console.log(resultIndustries);
                    var sortedIndustries = _.sortBy(resultIndustries, function(obj) {
                        return obj[0];
                    });

                    // console.log(sortedIndustries);
                    for (var indus = 0; indus < sortedIndustries.length; indus++) {

                        var trimIndustry = sortedIndustries[indus].replace(/ /g, '').replace(',', '').toLowerCase().toString();
                        $('.secondary-search-dropdown').find('ul').append('<li data-industry="' + trimIndustry + '">' + sortedIndustries[indus] + '</li>');

                        // $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('ul').append('<li>' + sortedIndustries[indus].key + '</li>');
                    }
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
            },
            init: function() {
                var data = {
                    "fields": ["marketCap", "totalRevenue", "peRatio", "dividendYield", "industryGroup"]
                };
                // Default load
                if ($('.search-criteria').length) {
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
                }

                // Register Submit form handler for Search
                SGX.search.submitClick();


            }, // end it
            addCriteria: function(data, settings) {

                if (typeof(data) == 'undefined') {
                    var data = {
                        "fields": ["marketCap", "totalRevenue", "peRatio", "dividendYield", "industryGroup"]
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
                            // console.log(data.distributions[i]);
                            var $relCheckbox = $('.editSearchB').find('.checkbox[data-name="' + data.distributions[i].field + '"]'),
                                friendlyName = $relCheckbox.find('.trigger').text(),
                                type = $relCheckbox.data('type'),
                                matches = data.distributions[i].buckets.length + ' matches',
                                min = data.distributions[i].buckets[0].key,
                                max = data.distributions[i].buckets[data.distributions[i].buckets.length - 1].key;

                            if (data.distributions[i].field != 'industryGroup') {
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
                            } else if (data.distributions[i].field == 'industryGroup') {

                                // console.log('industry');
                                // console.log(data.distributions[i]);
                                console.log(data.distributions[i].buckets);
                                var industriesArry = [];

                                if (!$('tr.criteria[data-name="' + data.distributions[i].field + '"]').length) {
                                    $('table.search-criteria').find('tbody').append('<tr class="criteria" data-name="' + data.distributions[i].field + '" data-type="' + data.distributions[i].field + '"><td>Industry</td><td colspan="2"><div class="button-dropdown"><div class="trigger">Select Sector &amp; Industry<span class="arrow"></span></div><div class="dropdown"><ul></ul></div></div></td><td class="max"></td></tr>');
                                    for (var b = 0; b < data.distributions[i].buckets.length; b++) {
                                        // console.log(data.distributions[i].buckets[b]);
                                        var industryString = {
                                            key: data.distributions[i].buckets[b].key
                                        };
                                        industriesArry.push(industryString);
                                        // console.log(industryRow);
                                        // $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('ul').append('test');

                                    };
                                } else {
                                    console.log('else');
                                }

                                var sortedIndustries = _.sortBy(industriesArry, function(obj) {
                                    return obj.key;
                                });

                                console.log(sortedIndustries);
                                $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('ul').append('<li>All Industries</li>');
                                for (var indus = 0; indus < sortedIndustries.length; indus++) {
                                    $('tr.criteria[data-name="' + data.distributions[i].field + '"]').find('ul').append('<li>' + sortedIndustries[indus].key + '</li>');
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
                                $('.button.confirm').on('click', target, function(e) {
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

                    var result = '<tr class="result" data-industry="' + trimIndustry + '" data-company-code="' + data.companies[i].tickerCode + '"><td><a href="company-tearsheet" data-company-code="' + data.companies[i].tickerCode + '"> ' + data.companies[i].companyName + '</td><td>' + data.companies[i].tickerCode + '</td><td class="industry">' + data.companies[i].industry + '</td><td>' + data.companies[i].marketCap + '</td><td>' + data.companies[i].totalRevenue + '</td><td>' + data.companies[i].priceToBookRatio + '</td><td>' + data.companies[i].dividendYield + '</td></tr>';
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
                SGX.routing();

            },
            removeResults: function() {
                $('.module-results').find('tbody').remove();
            },
            resetResults: function() {
                $('.module-results').find('tbody').remove().end().find('thead').after(SGX.search.resultsDOM);
                // console.log(SGX.search.resultsDOM);
            },
            submitClick: function() {
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
                        if ($(this).data('name') != 'industryGroup') {
                            data.criteria.push({
                                "field": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').attr('data-name'),
                                "from": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').attr('data-min'),
                                "to": $('table.search-criteria').find('tr.criteria:eq(' + idx + ')').attr('data-max')
                            });
                        } else if ($(this).data('name') == 'industryGroup') {
                            console.log('todo');
                            if (($('table.search-criteria').find('tr.criteria:eq(' + idx + ')').find('li.open').length) && ($('table.search-criteria').find('tr.criteria:eq(' + idx + ')').find('li.open').index() != 0)) {
                                console.log();
                                console.log($('table.search-criteria').find('tr.criteria:eq(' + idx + ')').find('li.open').text());
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
                    });
                });
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
        stocks: {
            areaGraph: function(dataNew) {
                console.log('areaGraph');
                if ($('#area-chart').length) {
                    // debug.log('exists');
                    if (typeof(dataNew) != 'undefined') {
                        data = dataNew;
                    }

                    $('#area-chart').highcharts('StockChart', {
                        colors: [
                            '#363473',
                            '#363473',
                            '#8bbc21',
                            '#910000',
                            '#1aadce',
                            '#492970',
                            '#f28f43',
                            '#77a1e5',
                            '#c42525',
                            '#a6c96a'
                        ],
                        chart: {
                            resetZoomButton: {
                                relativeTo: 'chart'
                            }
                        },
                        xAxis: {
                            gridLineWidth: 0,
                            range: 6 * 30 * 24 * 3600 * 1000
                        },

                        rangeSelector: {
                            buttonSpacing: 2,
                            buttonTheme: { // styles for the buttons
                                fill: '#fff',
                                stroke: '#babbbd',
                                'stroke-width': 1,
                                style: {
                                    color: '#1e2171',
                                    fontWeight: 'bold',
                                },
                                states: {
                                    hover: {
                                        fill: '#fff',
                                        style: {
                                            color: '#1e2171'
                                        }
                                    },
                                    select: {
                                        fill: '#fff',
                                        style: {
                                            color: '#1e2171'
                                        }
                                    }
                                }
                            },
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
                            }],
                            inputBoxBorderColor: 'transparent',
                            inputBoxWidth: 100,
                            inputBoxHeight: 18,
                            inputStyle: {
                                color: 'transparent',
                                fontWeight: 'bold'
                            },
                            labelStyle: {
                                color: 'transparent',
                                fontWeight: 'bold'
                            },
                            selected: 8
                        },

                        title: {
                            text: 'Price',
                            style: {
                                color: '#2e2e2e',
                                'font-size': '16px'
                            }
                        },

                        tooltip: {
                            enabled: false,
                            style: {
                                width: '200px'
                            },
                            valueDecimals: 4
                        },
                        navigator: {
                            enabled: false
                        },
                        scrollbar: {
                            enabled: false
                        },
                        yAxis: {
                            gridLineWidth: 2,
                            range: 90
                        },
                        series: [{
                                name: 'Price',
                                data: data,
                                id: 'dataseries',
                                type: 'area'
                            },
                            // the event marker flags
                            /*{
                                    type: 'flags',
                                    data: [{
                                        x: Date.UTC(2008, 11, 9),
                                        title: 'B',
                                        text: 'EURUSD: Bearish Trend Change on Tap?',
                                        shape: 'url(../../img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2008, 12, 6),
                                        title: 'C',
                                        text: 'US Dollar: Is This the Long-Awaited Recovery or a Temporary Bounce?',
                                        shape: 'url(../../img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2009, 7, 25),
                                        title: 'H',
                                        text: 'Euro Contained by Channel Resistance',
                                        shape: 'url(../../img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2010, 8, 28),
                                        title: 'G',
                                        text: 'EURUSD: Bulls Clear Path to 1.50 Figure',
                                        shape: 'url(../../img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 6, 4),
                                        title: 'F',
                                        text: 'EURUSD: Rate Decision to End Standstill',
                                        shape: 'url(../../img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 7, 5),
                                        title: 'E',
                                        text: 'EURUSD: Enter Short on Channel Break',
                                        shape: 'url(../../img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 9, 6),
                                        title: 'D',
                                        text: 'Forex: U.S. Non-Farm Payrolls Expand 244K, U.S. Dollar Rally Cut Short By Risk Appetite',
                                        shape: 'url(../../img/stock-marker.png)'
                                    }],
                                    onSeries: 'dataseries',
                                    shape: 'circlepin',
                                    y: -24,
                                    width: 16,
                                    style: { // text style
                                        color: 'black',
                                    },
                                }*/
                        ]
                    });


                }
            },
            volGraph: function(dataNew) {
                if ($('#bar-chart').length) {
                    if (typeof(dataNew) != 'undefined') {
                        data = dataNew;
                    }

                    $('#bar-chart').highcharts('StockChart', {
                        colors: [
                            '#b5cf34',
                            '#363473',
                            '#8bbc21',
                            '#910000',
                            '#1aadce',
                            '#492970',
                            '#f28f43',
                            '#77a1e5',
                            '#c42525',
                            '#a6c96a'
                        ],
                        chart: {
                            height: 200
                        },
                        plotOptions: {
                            pointPadding: 0
                        },
                        rangeSelector: {
                            selected: 5,
                            enabled: false
                        },

                        title: {
                            text: 'Volume',
                            style: {
                                color: '#2e2e2e',
                                'font-size': '16px'
                            }
                        },

                        tooltip: {
                            enabled: false,
                            style: {
                                width: '200px'
                            },
                            valueDecimals: 4
                        },
                        xAxis: {
                            gridLineWidth: 0
                        },
                        navigatior: {
                            height: 30
                        },
                        yAxis: {
                            height: 100
                        },
                        series: [{
                                name: 'Volume',
                                data: data,
                                id: 'dataseries',
                                type: 'column'
                            },
                            // the event marker flags
                            /*{
                                    type: 'flags',
                                    data: [{
                                        x: Date.UTC(2010, 7, 25),
                                        title: 'H',
                                        text: 'Euro Contained by Channel Resistance',
                                        shape: 'url(http://localhost:5000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2010, 8, 28),
                                        title: 'G',
                                        text: 'EURUSD: Bulls Clear Path to 1.50 Figure',
                                        shape: 'url(http://localhost:5000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 6, 4),
                                        title: 'F',
                                        text: 'EURUSD: Rate Decision to End Standstill',
                                        shape: 'url(http://localhost:5000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 7, 5),
                                        title: 'E',
                                        text: 'EURUSD: Enter Short on Channel Break',
                                        shape: 'url(http://localhost:5000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 9, 6),
                                        title: 'D',
                                        text: 'Forex: U.S. Non-Farm Payrolls Expand 244K, U.S. Dollar Rally Cut Short By Risk Appetite',
                                        shape: 'url(http://localhost:5000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 10, 6),
                                        title: 'C',
                                        text: 'US Dollar: Is This the Long-Awaited Recovery or a Temporary Bounce?',
                                        shape: 'url(http://localhost:5000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 11, 9),
                                        title: 'B',
                                        text: 'EURUSD: Bearish Trend Change on Tap?',
                                        shape: 'url(http://localhost:5000/img/stock-marker.png)'
                                    }],
                                    onSeries: 'dataseries',
                                    shape: 'circlepin',
                                    y: -24,
                                    width: 16,
                                    style: { // text style
                                        color: 'black',
                                        'line-height': '10px',
                                        'vertical-align': 'top'
                                    },
                                }*/
                        ]
                    });
                }
            },
            init: function() {
                debug.log('SGX.Stocks');
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