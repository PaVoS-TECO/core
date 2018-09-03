define(['jquery', 'appManager', 'initializer', 'gridIDFetchRoutine', 'exportFormatFetchRoutine', 'sensorTypeFetchRoutine', 'colorGradientFetchRoutine', 'grid', 'gridUtil'],
function($, AppManager, Initializer, GridIDFetchRoutine, ExportFormatFetchRoutine, SensorTypeFetchRoutine, ColorGradientFetchRoutine, Grid, GridUtil) {
    function InitializationRoutine(callback) {
        this.gridIDFetched = false;
        this.exportFormatFetched = false;
        this.sensorTypeFetched = false;
        this.colorGradientFetched = false;
        this.callback = callback;
    };

    InitializationRoutine.prototype.run = function() {
        console.log("START InitializationRoutine");
        var gridIDRoutine = new GridIDFetchRoutine(this.gridIDFetchNotify.bind(this));
        gridIDRoutine.run();
        var exportFormatRoutine  = new ExportFormatFetchRoutine(this.exportFormatFetchNotify.bind(this));
        exportFormatRoutine.run();
        var colorGradientRoutine = new ColorGradientFetchRoutine(this.colorGradientFetchNotify.bind(this));
        colorGradientRoutine.run();
    };

    InitializationRoutine.prototype.gridIDFetchNotify = function(gridID) {
        if (gridID != null) {
            AppManager.GRID = GridUtil.parseGridID(AppManager.MAP_BOUNDS, gridID);
        }
        this.gridIDFetched = true;

        var sensorTypeRoutine = new SensorTypeFetchRoutine(AppManager.GRID.getGridID(), this.sensorTypeFetchNotify.bind(this));
        sensorTypeRoutine.run();
    };

    InitializationRoutine.prototype.exportFormatFetchNotify = function(exportFormatArray) {
        AppManager.EXPORTFORMATS_ARRAY = exportFormatArray;
        this.exportFormatFetched = true;

        this.continueIfFinished();
    };

    InitializationRoutine.prototype.sensorTypeFetchNotify = function(sensorTypeArray) {
        AppManager.SENSORTYPES_ARRAY = sensorTypeArray;
        this.sensorTypeFetched = true;
        
        this.continueIfFinished();
    };

    InitializationRoutine.prototype.colorGradientFetchNotify = function(colorGradientJson) {
        console.log(colorGradientJson);
        this.colorGradientFetched = true;
        
        this.continueIfFinished();
    };

    InitializationRoutine.prototype.continueIfFinished = function() {
        if (this.gridIDFetched
            && this.exportFormatFetched
            && this.sensorTypeFetched
            && this.colorGradientFetched) {
                console.log("STOP InitializationRoutine");
                $(document).ready(function() {
                    Initializer.start();
                });
                this.callback();
            }
    };

    return InitializationRoutine;
})