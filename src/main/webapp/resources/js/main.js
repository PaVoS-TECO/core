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
        'initializer': '../../resources/js/app/initializer',
        'grid': '../../resources/js/grid/grid',
        'recursiveRectangleGrid': '../../resources/js/grid/recursiveRectangleGrid',
        'cluster': '../../resources/js/grid/cluster',
        'recursiveRectangleCluster': '../../resources/js/grid/recursiveRectangleCluster',
        'bounds': '../../resources/js/grid/bounds',
        'dimension': '../../resources/js/grid/dimension',
        'requestor': '../../resources/js/requestor/requestor',
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

require(['app', 'appState', 'recursiveRectangleGrid', 'bounds', 'utcDateTime', 'dynamicHtmlBuilder', 'jquery', 
         'bootstrap', 'bootstrapDatetimepicker', 'bootstrapTouchspin', 'leaflet', 'leafletFullscreen', 'leafletCoordinates', 
         'fontAwesome', 'fontAwesomeSolid', 'loadingOverlay'], 
         function(App, AppState, RecursiveRectangleGrid, Bounds, UTCDateTime, DynamicHtmlBuilder) {

    var grid = new RecursiveRectangleGrid(new Bounds([-180, -90], [180, 90]), 10, 10, 5);

    var appState = new AppState("",
                                [grid.getClusterContainingCoordinate([49, 8], 2), grid.getClusterContainingCoordinate([53, 15], 2)],
                                "pollution", 
                                "CSV",
                                [[new UTCDateTime(2018, 1, 1, 0, 0, 0)], [new UTCDateTime(2018, 8, 1, 0, 0, 0)]], 
                                new UTCDateTime(2018, 7, 23, 12, 25, 0),
                                10000,
                                2500,
                                true);
    var app = new App(appState);

    app.init();
    app.run();

    var sensorTypes = ['temperature_celsius', 'pollution', 'airpressure', 'waterflow', 'blub', 'blab'];
    var senorTypeGroudNameExportModal = 'exportModalSensorTypeRadioButtons';
    var sensorTypeGroupNameSensorTypeModal = 'sensorTypeModalSensorTypeRadioButtons';
    DynamicHtmlBuilder.buildRadioButtonGroup('#exportModalSensorTypeRadioButtons', senorTypeGroudNameExportModal, sensorTypes, 'temperature_celsius');
    DynamicHtmlBuilder.buildRadioButtonGroup('#sensorTypeModalRadioButtons', sensorTypeGroupNameSensorTypeModal, sensorTypes, 'temperature_celsius');

    var exportFormats = ['NetCDF', 'CSV'];
    var exportFormatsName = 'exportModalExportFormatRadioButtons';
    DynamicHtmlBuilder.buildRadioButtonGroup('#exportModalExportFormatRadioButtons', exportFormatsName, exportFormats, 'CSV');

    var automaticManual = ['Automatic', 'Manual'];
    var automaticManualName = 'timeSettingsAutomaticManualRefreshRadioButtons';
    DynamicHtmlBuilder.buildRadioButtonGroup('#timeSettingsAutomaticManualRefreshRadioButtons', automaticManualName, automaticManual, 'Automatic');

    // ================================================================================================================= //

    // setInterval(tempMethod, 5000);

    // function tempMethod( )
    // {
    //   console.log(ViewState.toString());
    // }

});