// # MF stream plugin
//
// A freeboard plugin that allows subscribing to a stream of MF events
//
// -------------------

// Best to encapsulate your plugin in a closure, although not required.
(function () {
    "use strict";

    var moonfruitDataSource = function (settings, updateCallback) {
        var self = this;
        var currentSettings = settings;

        function getData() {
            var chatFeed = new EventSource(currentSettings.source_url);
            chatFeed.addEventListener("message", addMsg, false);
        };

        function addMsg(msg) {
            updateCallback(msg.data);
        };

        // **onSettingsChanged(newSettings)** (required) : A public function we must implement that will be called when a user makes a change to the settings.
        self.onSettingsChanged = function (newSettings) {
            currentSettings = newSettings;
        };

        // **updateNow()** (required) : A public function we must implement that will be called when the user wants to manually refresh the datasource
        self.updateNow = function () {};

        // **onDispose()** (required) : A public function we must implement that will be called when this instance of this plugin is no longer needed. Do anything you need to cleanup after yourself here.
        self.onDispose = function () {};

        getData();
    };

    freeboard.loadDatasourcePlugin({
        type_name: "MF_SSE_stream_plugin",
        display_name: "Moonfruit SSE stream plugin",
        description: "This plugin allows subscribing to a SSE stream",
        settings: [
            {
                name: "source_url",
                display_name: "Stream source URL",
                type: "text",
                default_value: "http://localhost:9000/feed/registration",
                description: "Enter the source URL of your stream"
            },
        ],
        newInstance: function (settings, newInstanceCallback, updateCallback) {
            newInstanceCallback(new moonfruitDataSource(settings, updateCallback));
        }
    });
}());
