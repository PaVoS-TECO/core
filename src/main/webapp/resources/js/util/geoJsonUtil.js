define(function() {
    return {
        createStyling: function(geoJsonFeature, color, opacity, lineColor, lineWidth, lineOpacity) {
            geoJsonFeature['properties']['fill']           = { color };
            geoJsonFeature['properties']['fill-opacity']   = { opacity };
            geoJsonFeature['properties']['stroke']         = { lineColor };
            geoJsonFeature['properties']['stroke-width']   = { lineWidth };
            geoJsonFeature['properties']['stroke-opacity'] = { lineOpacity };
        }
    }
});