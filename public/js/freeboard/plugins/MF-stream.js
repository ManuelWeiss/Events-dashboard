// # MF stream plugin
//
// A freeboard plugin that allows subscribing to a stream of MF events
//
// -------------------

// Best to encapsulate your plugin in a closure, although not required.
(function()
{
    var myDatasourcePlugin = function(settings, updateCallback)
    {
        var self = this;

        var currentSettings = settings;

        function getData()
        {
            var chatFeed = new EventSource(currentSettings.source_url);
            chatFeed.addEventListener("message", addMsg, false);
        };

        function addMsg (msg) {
            updateCallback(msg);
        };

        // **onSettingsChanged(newSettings)** (required) : A public function we must implement that will be called when a user makes a change to the settings.
        self.onSettingsChanged = function(newSettings)
        {
            currentSettings = newSettings;
        };

        // **updateNow()** (required) : A public function we must implement that will be called when the user wants to manually refresh the datasource
        self.updateNow = function()
        {
        };

        // **onDispose()** (required) : A public function we must implement that will be called when this instance of this plugin is no longer needed. Do anything you need to cleanup after yourself here.
        self.onDispose = function()
        {
        };

        getData();
    };

    // **freeboard.loadDatasourcePlugin(definition)** tells freeboard that we are giving it a datasource plugin. It expects an object with the following:
    freeboard.loadDatasourcePlugin({
        // **type_name** (required) : A unique name for this plugin. This name should be as unique as possible to avoid collisions with other plugins, and should follow naming conventions for javascript variable and function declarations.
        "type_name"   : "MF_SSE_stream_plugin",
        // **display_name** : The pretty name that will be used for display purposes for this plugin. If the name is not defined, type_name will be used instead.
        "display_name": "Moonfruit SSE stream plugin",
        // **description** : A description of the plugin. This description will be displayed when the plugin is selected or within search results (in the future). The description may contain HTML if needed.
        "description" : "This plugin allows subscribing to a SSE stream",
        // **external_scripts** : Any external scripts that should be loaded before the plugin instance is created.
        "external_scripts" : [
        ],
        // **settings** : An array of settings that will be displayed for this plugin when the user adds it.
        "settings"    : [
            {
                // **name** (required) : The name of the setting. This value will be used in your code to retrieve the value specified by the user. This should follow naming conventions for javascript variable and function declarations.
                "name"         : "source_url",
                // **display_name** : The pretty name that will be shown to the user when they adjust this setting.
                "display_name" : "Stream source URL",
                // **type** (required) : The type of input expected for this setting. "text" will display a single text box input. Examples of other types will follow in this documentation.
                "type"         : "text",
                // **default_value** : A default value for this setting.
                "default_value": "http://localhost:9000/feed/registration",
                // **description** : Text that will be displayed below the setting to give the user any extra information.
                "description"  : "Enter the source URL of your stream"
            },
        ],
        // **newInstance(settings, newInstanceCallback, updateCallback)** (required) : A function that will be called when a new instance of this plugin is requested.
        // * **settings** : A javascript object with the initial settings set by the user. The names of the properties in the object will correspond to the setting names defined above.
        // * **newInstanceCallback** : A callback function that you'll call when the new instance of the plugin is ready. This function expects a single argument, which is the new instance of your plugin object.
        // * **updateCallback** : A callback function that you'll call if and when your datasource has an update for freeboard to recalculate. This function expects a single parameter which is a javascript object with the new, updated data. You should hold on to this reference and call it when needed.
        newInstance   : function(settings, newInstanceCallback, updateCallback)
        {
            // myDatasourcePlugin is defined below.
            newInstanceCallback(new myDatasourcePlugin(settings, updateCallback));
        }
    });

}());
