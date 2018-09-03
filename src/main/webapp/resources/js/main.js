require.config({
    'paths': {
        'jquery': '../../vendors/jquery/3.3.1/jquery.min',
        'bootstrap': '../../vendors/bootstrap/3.3.7/js/bootstrap.min',
        'bootstrapDatetimepicker': '../../vendors/bootstrap-plugins/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.min',
        'bootstrapDatepicker': '../../vendors/bootstrap-plugins/bootstrap-datepicker/1.4.1/js/bootstrap-datepicker.min',
        'bootstrapTouchspin': '../../vendors/bootstrap-plugins/bootstrap-touchspin-master/src/jquery.bootstrap-touchspin',
        'leaflet': '../../vendors/leaflet/leaflet',
        'leafletFullscreen': '../../vendors/leaflet-plugins/leaflet.fullscreen-master/Control.FullScreen',
        'leafletCoordinates': '../../vendors/leaflet-plugins/Leaflet.Coordinates-master/src/Control.Coordinates',
        'leafletNumberFormatter': '../../vendors/leaflet-plugins/Leaflet.Coordinates-master/src/util/NumberFormatter',
        'fontAwesome': '../../vendors/fontawesome/fontawesome-free-5.2.0-web/js/fontawesome.min',
        'fontAwesomeSolid': '../../vendors/fontawesome/fontawesome-free-5.2.0-web/js/solid.min',
        'loadingOverlay': '../../vendors/jquery-loading-overlay/jquery-loading-overlay-master/src/loadingoverlay',

        'app': '../../resources/js/app/app',
        'appState': '../../resources/js/app/appState',
        'appManager': '../../resources/js/app/appManager',
        'initializer': '../../resources/js/app/initializer',
        'routine': '../../resources/js/app/routine',
        'requestor': '../../resources/js/app/requestor',
        'requestHandler': '../../resources/js/app/requestHandler',
        'mapManager': '../../resources/js/app/mapManager',

        'initializationRoutine': '../../resources/js/app/routine/initializationRoutine',
        'fetchRoutine': '../../resources/js/app/routine/fetchRoutine',
        'exportRoutine': '../../resources/js/app/routine/exportRoutine',
        'exportFormatFetchRoutine': '../../resources/js/app/routine/exportFormatFetchRoutine',
        'clusterGeoJsonFetchRoutine': '../../resources/js/app/routine/clusterGeoJsonFetchRoutine',
        'colorGradientFetchRoutine': '../../resources/js/app/routine/colorGradientFetchRoutine',
        'sensorGeoJsonFetchRoutine': '../../resources/js/app/routine/sensorGeoJsonFetchRoutine',
        'sensorReportRoutine': '../../resources/js/app/routine/sensorReportRoutine',
        'sensorTypeFetchRoutine': '../../resources/js/app/routine/sensorTypeFetchRoutine',
        'gridIDFetchRoutine': '../../resources/js/app/routine/gridIDFetchRoutine',

        'grid': '../../resources/js/grid/grid',
        'recursiveRectangleGrid': '../../resources/js/grid/recursiveRectangleGrid',
        'cluster': '../../resources/js/grid/cluster',
        'recursiveRectangleCluster': '../../resources/js/grid/recursiveRectangleCluster',
        'bounds': '../../resources/js/grid/bounds',
        'dimension': '../../resources/js/grid/dimension',

        'color': '../../resources/js/visualization/color',
        'colorGradient': '../../resources/js/visualization/colorGradient',
        'multiColorGradient': '../../resources/js/visualization/multiColorGradient',

        'parser': '../../resources/js/util/parser',
        'dateTime': '../../resources/js/util/dateTime',
        'dynamicHtmlBuilder': '../../resources/js/util/dynamicHtmlBuilder',
        'utcDateTime': '../../resources/js/util/utcDateTime',
        'leafletUtil': '../../resources/js/util/leafletUtil',
        'geoJsonUtil': '../../resources/js/util/geoJsonUtil',
        'gridUtil': '../../resources/js/util/gridUtil',
        'storageUtil': '../../resources/js/util/storageUtil',
        'mathUtil': '../../resources/js/util/mathUtil',
        'util': '../../resources/js/util/util'
    },
    'shim': {
        'bootstrap': {
            'deps': ['jquery']
        },
        'bootstrapDatepicker': {
            'deps': ['jquery', 'bootstrap']
        },
        'bootstrapTouchspin': {
            'deps': ['jquery', 'bootstrap']
        },
        'leaflet': {
            'exports': 'L'
        },
        'leafletFullscreen': {
            'deps': ['leaflet']
        },
        'leafletCoordinates': {
            'deps': ['leaflet', 'leafletNumberFormatter']
        },
        'leafletNumberFormatter': {
            'deps': ['leaflet']
        },
        'fontAwesomeSolid': {
            'deps': ['fontAwesome']
        },
        'loadingOverlay': {
            'deps': ['jquery']
        }
    }
});

require(['app', 
         'jquery', 
         'bootstrap', 'bootstrapDatetimepicker', 'bootstrapTouchspin', 
         'leaflet', 'leafletFullscreen', 'leafletCoordinates', 
         'fontAwesome', 'fontAwesomeSolid', 
         'loadingOverlay'], 
         function(App) {

    var app = new App();

    app.run();

});