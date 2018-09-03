define(['appManager', 'mapManager', 'clusterGeoJsonFetchRoutine'], function(AppManager, MapManager, ClusterGeoJsonFetchRoutine) {
    var timer = null;

    run = function() {
        var clusterGeoJsonFetchRoutine = 
            new ClusterGeoJsonFetchRoutine(
                AppManager.GRID.getGridID(),
                AppManager.APP_STATE.getSelectedClusters(),
                AppManager.APP_STATE.getSelectedSensortype(),
                AppManager.APP_STATE.getSelectedTimeframe(),
                AppManager.APP_STATE.HISTORICAL_SNAPSHOT_AMOUNT,
                this.handleFetchRequest
            );

        clusterGeoJsonFetchRoutine.run();
    };

    handleFetchRequest = function(response) {
        console.log("Received response " + response);

        if (AppManager.LIVE_MODE_ENABLED) {
            MapManager.updateLayerArray([JSON.parse(response)]);
            MapManager.displayLayer(0);
        } else {
            MapManager.updateLayerArray(JSON.parse(response));
            MapManager.displayLayer(0);
        }
    };

    start = function() {
        // clearInterval(timer);
        // if (AppManager.LIVE_MODE_ENABLED) {
        //     timer = setInterval(function() {
        //         this.run();
        //     }, AppManager.APP_STATE.getSelectedLiveRefreshInterval());
        // } else {
        //     timer = setInterval(function() {

        //     }, AppManager.APP_STATE.getSelectedHistoricalRefreshInterval());
        // }
    };

    stop = function() {
        clearInterval(timer);
        timer = null;
    };

    return {
        run,
        start,
        stop
    }
})