define(function() {
    /**
      * Encapsulates width and height.
      * 
      * @param {*} width the width
      * @param {*} height the height
      */
    function Dimension(width, height) {
        this.width = width;
        this.height = height;
    }

    /**
      * Set the width.
      * 
      * @param {*} width the width
      */
    Dimension.prototype.setWidth = function(width) {
        this.width = width;
    }
    /**
      * Get the width.
      */
    Dimension.prototype.getWidth = function() {
        return this.width;
    }
    /**
      * Set the height.
      * 
      * @param {*} height the height
      */
    Dimension.prototype.setHeight = function(height) {
        this.height = height;
    }
    /**
      * Get the height.
      */
    Dimension.prototype.getHeight = function() {
        return this.height;
    }

    return Dimension;
});