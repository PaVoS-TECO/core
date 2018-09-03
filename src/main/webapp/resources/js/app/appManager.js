define(["appState", "color", "multiColorGradient", "bounds", "recursiveRectangleGrid", "utcDateTime"], function(AppState, Color, MultiColorGradient, Bounds, RecursiveRectangleGrid, UTCDateTime) {
    // Latitude - Longitude
    var KARLSRUHE = [49.007, 8.404];
    var KARLSRUHE_TECO = [49.013, 8.424];

    var LEAFLET_MAP_CONTAINER = "mapContainer";
    var BASEMAP_URL = "http://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png";

    var BASEMAP_ATTRIBUTION = { attribution: "Positron", minZoom: 2, maxZoom: 15 };
    var INITIAL_COORDINATES = KARLSRUHE;
    var INITIAL_ZOOMLEVEL = 10;
    var IS_FULLSCREEN_AVAILABLE = true;
    var IS_MOUSE_COORDINATES_VISIBLE = true;

    var AVAILABLE_SENSORTYPES = ["temperature_celsius", "pollution", "airpressure", "waterflow", "blub", "blab"];
    var AVAILABLE_EXPORTFORMATS = ["NetCDF", "CSV"];
    var AVAILABLE_REFRESH_STATES = ["Automatic", "Manual"];
    var MAP = null;
    var MAP_BOUNDS = new Bounds([-85.0, -180.0], [85.0, 180.0]);
    var GRID = new RecursiveRectangleGrid(MAP_BOUNDS, 2, 2, 3);
    var BOUNDS = [[0, 0], [10, 10]];
    var CONTENT_TABLE = [
                         [
                          "id", "temperature_celsius"
                         ],
                         [
                          "recursiveRectangleGrid-10_10_5:6_5-3_2", "20.1",
                          "recursiveRectangleGrid-10_10_5:6_5-3_3", "", 
                          "recursiveRectangleGrid-10_10_5:6_5-3_4", "", 
                          "recursiveRectangleGrid-10_10_5:6_5-3_5", "21.3"
                         ]
                        ];
    var APP_STATE = new AppState(
        "",
        [GRID.getClusterContainingCoordinate([49, 8], 2), GRID.getClusterContainingCoordinate([53, 15], 2)],
        "temperature_celsius", 
        "CSV",
        [[new UTCDateTime(2018, 1, 1, 0, 0, 0)], [new UTCDateTime(2018, 8, 1, 0, 0, 0)]], 
        new UTCDateTime(2018, 7, 23, 12, 25, 0),
        10000,
        2500,
        true);
    var GEOJSON_ARRAY = [];
    var HISTORICAL_SNAPSHOT_AMOUNT = 20;
    var LIVE_MODE_ENABLED = false;
    var BOUNDS = null;
    var GRID_LEVEL = 1;
    var SENSORTYPES_ARRAY;
    var EXPORTFORMATS_ARRAY;
    var COLOR_GRADIENTS = {
        "temperature_celsius": new MultiColorGradient([new Color("#0000ff"), new Color("#00ff00"), new Color("#ff0000")])
    };
    var COLOR_GRADIENTS_RANGE = {
        "temperature_celsius": [-20, 50]
    };
    var FILL_COLOR_OPACITY = 0.2;
    var BORDER_COLOR_OPACITY = 0.6;
    var BORDER_WEIGHT = 0.5;
    var EXPORT_TIMEOUT = 10000;
    var EXPORT_STATUS_TIMEOUT = 500;
    var HTTP_REQUEST_TIMEOUT = 5000;
    
    return {
        KARLSRUHE,
        KARLSRUHE_TECO,
        LEAFLET_MAP_CONTAINER,
        BASEMAP_URL,
        BASEMAP_ATTRIBUTION,
        INITIAL_COORDINATES,
        INITIAL_ZOOMLEVEL,
        IS_FULLSCREEN_AVAILABLE,
        IS_MOUSE_COORDINATES_VISIBLE,

        AVAILABLE_SENSORTYPES,
        AVAILABLE_EXPORTFORMATS,
        AVAILABLE_REFRESH_STATES,
        MAP,
        MAP_BOUNDS,
        GRID,
        BOUNDS,
        CONTENT_TABLE,
        APP_STATE,
        GEOJSON_ARRAY,
        HISTORICAL_SNAPSHOT_AMOUNT,
        LIVE_MODE_ENABLED,
        BOUNDS,
        GRID_LEVEL,
        SENSORTYPES_ARRAY,
        EXPORTFORMATS_ARRAY,
        COLOR_GRADIENTS,
        COLOR_GRADIENTS_RANGE,
        FILL_COLOR_OPACITY,
        BORDER_COLOR_OPACITY,
        BORDER_WEIGHT,
        EXPORT_TIMEOUT,
        EXPORT_STATUS_TIMEOUT,
        HTTP_REQUEST_TIMEOUT
    }
});