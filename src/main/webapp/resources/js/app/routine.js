define(['appManager', 'requestor'], function(AppManager, Requestor) {
    var timer = null;

    start = function() {
        if (AppManager.APP_STATE.getAutomaticRefreshEnabled) {
            if (AppManager.LIVE_MODE_ENABLED) {
                timer = setInterval(liveRoutine, AppManager.APP_STATE.getSelectedLiveRefreshInterval());
            } else {
                timer = setInterval(historicalRoutine, AppManager.APP_STATE.getSelectedHistoricalRefreshInterval());
            }
        } else {
            this.stop();
        }
    }

    stop = function() {
        clearInterval(timer);
        timer = null;
    }

    liveRoutine = function() {
        AppManager.GEOJSON_ARRAY = [Requestor.requestClusterGeoJson(
            AppManager.GRID.getGridID(), 
            AppManager.GRID.getClustersContainedInBounds(AppManager.BOUNDS, AppManager.GRID_LEVEL), 
            AppManager.APP_STATE.getSelectedSensortype(), 
            null, 
            null)];
        
        console.log(AppManager.GEOJSON_ARRAY);
    }

    historicalRoutine = function() {
        AppManager.GEOJSON_ARRAY = Requestor.requestClusterGeoJson(
            AppManager.GRID.getGridID(),
            AppManager.GRID.getClustersContainedInBounds(AppManager.BOUNDS, AppManager.GRID_LEVEL),
            AppManager.APP_STATE.getSelectedSensortype(),
            AppManager.APP_STATE.getSelectedTimeframe(),
            AppManager.HISTORICAL_SNAPSHOT_AMOUNT).split(',');

        console.log(AppManager.GEOJSON_ARRAY);
    }

    startExportRoutine = function(appState) {
        Requestor.startLoadAnimation();

        Requestor.stopLoadAnimation();
    }

    exportInit = function() {

    }

    checkExportStatus = function(response) {

    }

    download = function(response) {

    }

    return {
        start,
        stop,
        startExportRoutine
    }
});