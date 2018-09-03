define(['appManager', 'leafletUtil', 'leaflet'], function(AppManager, LeafletUtil) {
    var layerArray = [];
    var currentLayer = null;
    var currentIndex = 0;

    updateLayerArray = function(geoJsonArray) {
        currentIndex = 0;
        layerArray = [];
        for (i = 0; i < geoJsonArray.length; i++) {
            layerArray.push(LeafletUtil.createLayerFromGeoJson(geoJsonArray[i], applyColorGradient));
        }
    };

    displayLayer = function(index) {
        if (currentLayer != null) {
            AppManager.MAP.removeLayer(currentLayer);
        }
        currentIndex = index;
        currentLayer = layerArray[currentIndex];
        currentLayer.addTo(AppManager.MAP);
    };

    displayNextLayer = function() {
        if (currentLayer != null) {
            AppManager.MAP.removeLayer(currentLayer);
        }
        currentIndex = (currentIndex + 1) % layerArray.length;
        currentLayer = layerArray[currentIndex];
        currentLayer.addTo(AppManager.MAP);
    }

    applyColorGradient = function(feature, layer) {
        layer.setStyle(
            LeafletUtil.getStyle(
                AppManager.COLOR_GRADIENTS_RANGE[AppManager.APP_STATE.getSelectedSensortype()][0], 
                AppManager.COLOR_GRADIENTS_RANGE[AppManager.APP_STATE.getSelectedSensortype()][1], 
                Number(feature['properties']['value']), 
                AppManager.COLOR_GRADIENTS[AppManager.APP_STATE.getSelectedSensortype()], 
                AppManager.FILL_COLOR_OPACITY, 
                AppManager.BORDER_COLOR_OPACITY, 
                AppManager.BORDER_WEIGHT));
    };

    return {
        updateLayerArray,
        displayLayer,
        displayNextLayer,
        applyColorGradient
    };
});