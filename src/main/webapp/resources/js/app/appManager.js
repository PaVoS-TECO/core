define(['bounds', 'recursiveRectangleGrid'], function(Bounds, RecursiveRectangleGrid) {
    return {
        AVAILABLE_SENSORTYPES: ['temperature_celsius', 'pollution', 'airpressure', 'waterflow', 'blub', 'blab'],
        AVAILABLE_EXPORTFORMATS: ['NetCDF', 'CSV'],
        AVAILABLE_REFRESH_STATES: ['Automatic', 'Manual'],
        MAP: null,
        GRID: new RecursiveRectangleGrid(new Bounds([-90, -180], [90, 180]), 10, 10, 5),
        BOUNDS: [[0, 0], [10, 10]],
        CONTENT_TABLE: [['id', 'temperature_celsius'], ['recursiveRectangleGrid-10_10_5:6_5-3_4', '', 'recursiveRectangleGrid-10_10_5:6_5-3_5', '21.3']]
    }
});