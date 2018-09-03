define(['dimension', 'recursiveRectangleGrid'], function(Dimension, RecursiveRectangleGrid) {
    /**
     * Divides the submitted dimension into equal rectangles using the given rows and columns 
     * per gridlevel and returns the dimension of the resulting object.
     */
    calculateDimensionAtGridLevel = function(dimension, rows, columns, gridLevel) {
        width = (dimension.getWidth()) / Math.pow(rows, gridLevel);
        height = (dimension.getHeight()) / Math.pow(columns, gridLevel);
        return new Dimension(width, height);
    };

    /**
     * Calculate what the coordinates values would be if the bounds lower left corner was the point (0, 0).
     */
    calculateCoordinateRelativeToBounds = function(bounds, coordinate) {
        return [(coordinate[0] - bounds.getLowerLeft()[0]), (coordinate[1] - bounds.getLowerLeft()[1])];
    };

    /**
     * Turns an array of clusters into an array of their clusterIDs.
     */
    clusterArrayToStringArray = function(array) {
        var stringArray = [];
        for (i = 0; i < array.length; i++) {
            stringArray.push(array[i].getClusterID());
        }
        return stringArray;
    };

    /**
     * Parses the submitted grid id and returns its corresponding grid object.
     */
    parseGridID = function(bounds, gridID) {
        gridType = gridID.split('-')[0];
        gridParameters = gridID.split('-')[1].split('_');

        if (gridType == 'recursiveRectangleGrid') {
            return new RecursiveRectangleGrid(bounds, gridParameters[0], gridParameters[1], gridParameters[2]);
        }
    };
    
    return {
        calculateDimensionAtGridLevel,
        calculateCoordinateRelativeToBounds,
        clusterArrayToStringArray,
        parseGridID,
    };
});