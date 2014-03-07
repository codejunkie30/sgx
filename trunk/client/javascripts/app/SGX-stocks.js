define(['jquery', 'highstock', 'debug'], function($, StockChart) {

    var SGX = SGX || {};
    var stocks = {
        stocks: {
            init: function() {
                debug.log('SGX.Stocks');
                if ($('#container').length) {
                    $('#container').highcharts({
                        chart: {
                            type: 'column'
                        },
                        legend: {
                            align: 'right',
                            enabled: false
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
                            colors: ['#565b5c']

                        }, {
                            name: 'Payout Ratio',
                            data: [12000, 13000, 16250, 13800, 13000],
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


                if ($('#containerR').length) {
                    // debug.log('exists');
                    $.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=usdeur.json&callback=?', function(data) {
                        // debug.log('success');
                        $('#containerR').highcharts('StockChart', {
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
                            yAxis: {},
                            series: [{
                                    name: 'Price',
                                    data: data,
                                    id: 'dataseries',
                                    type: 'area'
                                },
                                // the event marker flags
                                {
                                    type: 'flags',
                                    data: [{
                                        x: Date.UTC(2008, 11, 9),
                                        title: 'B',
                                        text: 'EURUSD: Bearish Trend Change on Tap?',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2008, 12, 6),
                                        title: 'C',
                                        text: 'US Dollar: Is This the Long-Awaited Recovery or a Temporary Bounce?',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 9, 6),
                                        title: 'D',
                                        text: 'Forex: U.S. Non-Farm Payrolls Expand 244K, U.S. Dollar Rally Cut Short By Risk Appetite',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 7, 5),
                                        title: 'E',
                                        text: 'EURUSD: Enter Short on Channel Break',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 6, 4),
                                        title: 'F',
                                        text: 'EURUSD: Rate Decision to End Standstill',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2010, 8, 28),
                                        title: 'G',
                                        text: 'EURUSD: Bulls Clear Path to 1.50 Figure',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2009, 7, 25),
                                        title: 'H',
                                        text: 'Euro Contained by Channel Resistance',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }],
                                    onSeries: 'dataseries',
                                    shape: 'circlepin',
                                    y: -24,
                                    width: 16,
                                    style: { // text style
                                        color: 'black',
                                    },
                                }
                            ]
                        });

                    });


                }
                if ($('#containerRR').length) {
                    $.getJSON('http://www.highcharts.com/samples/data/jsonp.php?filename=usdeur.json&callback=?', function(data) {
                        // debug.log('success');
                        $('#containerRR').highcharts('StockChart', {

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
                            yAxis: {},
                            series: [{
                                    name: 'Volume',
                                    data: data,
                                    id: 'dataseries',
                                    type: 'column'
                                },
                                // the event marker flags
                                {
                                    type: 'flags',
                                    data: [{
                                        x: Date.UTC(2010, 7, 25),
                                        title: 'H',
                                        text: 'Euro Contained by Channel Resistance',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2010, 8, 28),
                                        title: 'G',
                                        text: 'EURUSD: Bulls Clear Path to 1.50 Figure',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 6, 4),
                                        title: 'F',
                                        text: 'EURUSD: Rate Decision to End Standstill',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2011, 7, 5),
                                        title: 'E',
                                        text: 'EURUSD: Enter Short on Channel Break',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 9, 6),
                                        title: 'D',
                                        text: 'Forex: U.S. Non-Farm Payrolls Expand 244K, U.S. Dollar Rally Cut Short By Risk Appetite',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 10, 6),
                                        title: 'C',
                                        text: 'US Dollar: Is This the Long-Awaited Recovery or a Temporary Bounce?',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
                                    }, {
                                        x: Date.UTC(2012, 11, 9),
                                        title: 'B',
                                        text: 'EURUSD: Bearish Trend Change on Tap?',
                                        shape: 'url(http://localhost:3000/img/stock-marker.png)'
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
                                }
                            ]
                        });

                    });
                }
            }
        }
    };
    _.extend(SGX, stocks);
    SGX.stocks.init();
});