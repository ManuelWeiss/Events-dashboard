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
        $(element).find('.funnel').highcharts({
            chart: {
                type: 'funnel',
                animation: Highcharts.svg,
                marginRight: 100,
                backgroundColor: 'none',
                height: 320,
                events: {
                    load: function() {
                        // set up the updating of the chart each second
                        var series = this.series[0];
                        var dataMap = {
                            pageLoad: 0,
                            registration: 1,
                            buildAny: 2,
                            trialStarted: 3,
                            planUpgrade: 4
                        };

                        $(element).on('newEvent', function (event, eventName) {
                            var eventPosition = dataMap[eventName];
                            var point = series.data[eventPosition];
                            if (point) {
                                var currentValue = point.y;
                                point.update({x: eventPosition, y: currentValue + 1}, true, true);
                            }
                        });
                    }
                }
            },
            plotOptions: {
                series: {
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>',
                        color: '#FFFFFF',
                        softConnector: true
                    },
                    neckWidth: '30%',
                    neckHeight: '25%'

                    //-- Other available options
                    // height: pixels or percent
                    // width: pixels or percent
                }
            },
            legend: {
                enabled: false
            },
            series: [{
                name: 'Unique users',
                data: [
                    ['Website visits',   0],
                    ['Registrations',       0],
                    ['Websites built', 0],
                    ['Trials started',    0],
                    ['Plan upgrades',    0]
                ]
            }]
        });
    };

    var addValueToChart = function (element, eventName) {
        $(element).trigger('newEvent', [eventName]);
    };

    var lineChartWidget = function (settings) {
        var self = this;
        var thisChart;

        var titleElement = $('<h2 class="funnel-title section-title"></h2>');
        var lineChartElement = $('<div class="funnel" style="width: 100%, height: 100%"></div>');

        this.render = function (element) {
            $(element).append(titleElement).append(lineChartElement);

            thisChart = element;
            renderLineChart(element);
        }

        this.onSettingsChanged = function (newSettings) {
            titleElement.html((_.isUndefined(newSettings.title) ? "" : newSettings.title));
        }

        this.onCalculatedValueChanged = function (settingName, newValue) {
            addValueToChart(thisChart, newValue.stream);
        }

        this.onDispose = function () {
        }

        this.getHeight = function () {
            return 6;
        }

        this.onSettingsChanged(settings);
    };

    freeboard.loadWidgetPlugin({
        type_name: "funnel",
        display_name: "Funnel",
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
