define(['color'], function(Color) {
    /**
      * Encapsulates two Colors and enables their interpolation.
      * 
      * @param {*} startColor the start color
      * @param {*} endColor the end color
      */
    function ColorGradient(startColor, endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    /**
      * Set the start color.
      * 
      * @param {*} startColor the start color
      */
    ColorGradient.prototype.setStartColor = function(startColor) {
        this.startColor = startColor;
    }

    /**
      * Get the start color.
      */
    ColorGradient.prototype.getStartColor = function() {
        return this.startColor;
    }

    /**
      * Set the end color.
      * 
      * @param {*} startColor the end color
      */
    ColorGradient.prototype.setEndColor = function(endColor) {
        this.endColor = endColor;
    }

    /**
      * Get the end color.
      */
    ColorGradient.prototype.getEndColor = function() {
        return this.endColor;
    }

    /**
      * Calculates the ratio that the submitted value has in the given range between min and max. Then the 
      * color on the range between start color and end color with the calculated ratio is returned.
      * 
      * @param {*} min the min value
      * @param {*} max the max value
      * @param {*} value the value
      */
    ColorGradient.prototype.getColor = function(min, max, value) {
        if (value <= min) {
            return this.getStartColor();
        } else if (value >= max) {
            return this.getEndColor();
        }

        var ratio = (value - min) / (max - min);

        var startColorRGB = this.getStartColor().getRGB();
        var endColorRGB   = this.getEndColor().getRGB();

        var resultRGB = [];
        for (i = 0; i < 3; i++) {
            resultRGB.push(Math.round(startColorRGB[i] + ratio * (endColorRGB[i] - startColorRGB[i])));
        }

        return new Color(resultRGB);
    }

    return ColorGradient;
})