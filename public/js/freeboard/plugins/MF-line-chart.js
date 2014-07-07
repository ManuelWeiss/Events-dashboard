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

    function renderLineChart (element, time_unit) {
        var chart;

        $(element).find('.linechart').highcharts({
            chart: {
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 10,
                height: 150,
                backgroundColor: 'none',
                events: {
                    load: function() {
                        // set up the updating of the chart each second
                        var series = this.series[0];

                        $(element).on('newValues', function (event, value) {
                            var x = (new Date()).getTime(), // current time
                                y = value;
                            series.addPoint([x, y], true, true);
                        });
                    }
                }
            },
            colors: ["#FF9500"],
            xAxis: {
                type: 'datetime',
                tickPixelInterval: 150
            },
            yAxis: {
                title: {
                    text: 'Amount'
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
                name: 'series-1',
                id: 'event1',
                data: (function() {
                    // generate an array of random data
                    var data = [],
                        time = (new Date()).getTime(),
                        i;

                    for (i = -19; i <= 0; i++) {
                        data.push({
                            x: time + i * 1000 * time_unit,
                            y: 0
                        });
                    }

                    return data;
                })()
            }]
        });
    };

    var addValueToChart = function (element, eventAmount) {
        $(element).trigger('newValues', [eventAmount]);
    };

    var lineChartWidget = function (settings) {
        var self = this;
        var eventCount = 0;
        var thisChart;

        var titleElement = $('<h2 class="linechart-title section-title"></h2>');
        var lineChartElement = $('<div class="linechart" style="width: 100%, height: 100%"></div>');

        var updateTimer = function () {
            setInterval($.proxy(function () {
                addValueToChart(thisChart, eventCount);
                eventCount = 0;
            }, this), 1000 * settings.time_unit);
        };

        this.render = function (element) {
            $(element).append(titleElement).append(lineChartElement);

            thisChart = element;
            renderLineChart(element, settings.time_unit);
            updateTimer();
        }

        this.onSettingsChanged = function (newSettings) {
            titleElement.html((_.isUndefined(newSettings.title) ? "" : newSettings.title));
        }

        this.onCalculatedValueChanged = function (settingName, newValue) {
            eventCount += 1;
        }

        this.onDispose = function () {
        }

        this.getHeight = function () {
            return 3;
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
            },
            {
                name: "time_unit",
                display_name: "Time unit (in seconds)",
                default_value: "1",
                type: "number",
                description: "The unit of time used for averaging"
            }
        ],
        newInstance: function (settings, newInstanceCallback) {
            newInstanceCallback(new lineChartWidget(settings));
        }
    });
}());
