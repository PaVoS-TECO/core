define(['jquery', 'appState', 'initializer', 'leafletUtil', 'geoJsonUtil', 'storageUtil', 'grid', 'recursiveRectangleGrid',
        'bounds', 'dimension', 'util', 'utcDateTime', 'requestor', 'dynamicHtmlBuilder'],
function($, AppState, Initializer, LeafletUtil, GeoJsonUtil, StorageUtil, Grid, RecursiveRectangleGrid, Bounds, Dimension,
         Util, UTCDateTime, Requestor, DynamicHtmlBuilder) {

    function App(appState) {
        this.appState = appState;
    }

    App.prototype.setAppState = function(appState) {
        this.appState = appState;
    }

    App.prototype.getAppState = function() {
        return this.appState;
    }

    App.prototype.init = function() {
        Requestor.startLoadAnimation(3500);

        // Keep reference to local scope
        var app = this;

        /**
         * Wait for document to be ready for manipulation.
         */
        $(document).ready(function() {
            Initializer.init(app);
        });
    }

    App.prototype.run = function() {

    }

    return App;
});