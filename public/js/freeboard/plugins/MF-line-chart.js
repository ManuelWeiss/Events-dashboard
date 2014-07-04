// # MF stream plugin
//
// A freeboard plugin that allows subscribing to a stream of MF events
//
// -------------------

// Best to encapsulate your plugin in a closure, although not required.
(function () {
    "use strict";

    freeboard.addStyle('.highcharts-title', "display:none;");
    freeboard.addStyle('.section-title.linechart-title', "margin-bottom: 10px;");
    freeboard.addStyle('.highcharts-container > svg text[text-anchor=end]', "display: none !important;");

    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });

    function renderLineChart (element) {
        var chart;

        $(element).find('.linechart').highcharts({
            chart: {
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 10,
                height: 180,
                backgroundColor: 'none',
                events: {
                    load: function() {
                        // set up the updating of the chart each second
                        var series = this.series[0];
                        setInterval(function() {
                            var x = (new Date()).getTime(), // current time
                                y = Math.random();
                            series.addPoint([x, y], true, true);
                        }, 1000);
                    }
                }
            },
            colors: ["#FF9500"],
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150,
                lineColor: '#B88F51'
            },
            yAxis: {
                title: {
                    text: 'Number of registrations'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#CCCCCC'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+
                        Highcharts.numberFormat(this.y, 2);
                }
            },
            legend: {
                enabled: false
            },
            exporting: {
                enabled: false
            },
            series: [{
                name: 'Random data',
                data: (function() {
                    // generate an array of random data
                    var data = [],
                        time = (new Date()).getTime(),
                        i;

                    for (i = -19; i <= 0; i++) {
                        data.push({
                            x: time + i * 1000,
                            y: Math.random()
                        });
                    }
                    return data;
                })()
            }]
        });
    };

    var lineChartWidget = function (settings) {
        var self = this;

        var titleElement = $('<h2 class="linechart-title section-title"></h2>');
        var lineChartElement = $('<div class="linechart" style="width: 100%, height: 100%"></div>');

        this.render = function (element) {
            $(element).append(titleElement).append(lineChartElement);

            renderLineChart(element);
        }

        this.onSettingsChanged = function (newSettings) {
            titleElement.html((_.isUndefined(newSettings.title) ? "" : newSettings.title));
        }

        this.onCalculatedValueChanged = function (settingName, newValue) {
            // addValueToSparkline(lineChartElement, newValue);
        }

        this.onDispose = function () {
        }

        this.getHeight = function () {
            return 3.5;
        }

        this.onSettingsChanged(settings);
    };

    freeboard.loadWidgetPlugin({
        type_name: "line_chart",
        display_name: "Line chart",
        settings: [
            {
                name: "title",
                display_name: "Title",
                type: "text"
            },
            {
                name: "value",
                display_name: "Value",
                type: "calculated"
            }
        ],
        newInstance: function (settings, newInstanceCallback) {
            newInstanceCallback(new lineChartWidget(settings));
        }
    });
}());
