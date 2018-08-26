define(['dimension'], function(Dimension) {
    /**
      * Encapsulates two points and provides the corner coordinates and the dimension 
      * of the rectangle they create.
      * 
      * @param {*} point1 the first point
      * @param {*} point2 the second point
      */
    function Bounds(point1, point2) {
        var x1 = point1[0];
        var y1 = point1[1];
        var x2 = point2[0];
        var y2 = point2[1];

        this.minX = Math.min(x1, x2);
        this.maxX = Math.max(x1, x2);
        this.minY = Math.min(y1, y2);
        this.maxY = Math.max(y1, y2);
    }

    /**
      * Get the coordinates of the lower left corner.
      */
    Bounds.prototype.getLowerLeft = function() {
        return [this.minX, this.minY];
    }
    /**
      * Get the coordinates of the upper left corner.
      */
    Bounds.prototype.getUpperLeft = function() {
        return [this.minX, this.maxY];
    }
    /**
      * Get the coordinates of the lower right corner.
      */
    Bounds.prototype.getLowerRight = function() {
        return [this.maxX, this.minY];
    }
    /**
      * Get the coordinates of the upper right corner.
      */
    Bounds.prototype.getUpperRight = function() {
        return [this.maxX, this.maxY];
    }

    /**
      * Get the dimension.
      */
    Bounds.prototype.getDimension = function() {
        return new Dimension((this.maxX - this.minX), (this.maxY - this.minY));
    }

    return Bounds;
});