define(['dimension'], function(Dimension) {
    return {
        /**
          * Divides the submitted dimension into equal rectangles using the given rows and columns 
          * per gridlevel and returns the dimension of the resulting object.
          */
        calculateDimensionAtGridLevel: function(dimension, rows, columns, gridLevel) {
            width = (dimension.getWidth()) / Math.pow(rows, gridLevel);
            height = (dimension.getHeight()) / Math.pow(columns, gridLevel);
            return new Dimension(width, height);
        },

        /**
          * Calculate what the coordinates values would be if the bounds lower left corner was the point (0, 0).
          */
        calculateCoordinateRelativeToBounds: function(bounds, coordinate) {
            return [(coordinate[0] - bounds.getLowerLeft()[0]), (coordinate[1] - bounds.getLowerLeft()[1])];
        }
    }
});