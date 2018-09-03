define(['colorGradient', 'color', 'mathUtil'], function(ColorGradient, Color, MathUtil) {
    /**
      * Encapsulates multiple Colors and enables their interpolation.
      * 
      * @param {*} colors an array of colors
      */
    function MultiColorGradient(colors) {
        this.setColors(colors);
    }

    /**
      * Set the colors.
      * 
      * @param {*} colors the colors
      */
    MultiColorGradient.prototype.setColors = function(colors) {
        if (colors.length == 1) {
            this.colorGradients = [new ColorGradient(colors[0], colors[0])];
        } else {
            var colorGradients = [];
            for (i = 0; i < (colors.length - 1); i++) {
                colorGradients.push(new ColorGradient(colors[i], colors[i + 1]));
            }
            this.colorGradients = colorGradients;
        }
    }

    /**
      * Get the colors.
      */
    MultiColorGradient.prototype.getColors = function() {
        var colors = [];
        for (i = 0; i < this.colorGradients.length; i++) {
            colors.push(this.colorGradients[i].getStartColor());
            if (i == (this.colorGradients.length - 1)) {
                colors.push(this.colorGradients[i].getEndColor());
            }
        }
        return colors;
    }

    /**
      * Calculates the ratio that the submitted value has in the given range between min and max. Then the 
      * color gradient that corresponds to the ratio in regards to its position in this multi color gradient 
      * is looked up. In this color gradient the color on the range between start color and end color with the 
      * calculated ratio is returned.
      * 
      * @param {*} min the min value
      * @param {*} max the max value
      * @param {*} value the value
      */
    MultiColorGradient.prototype.getColor = function(min, max, value) {
        if (value <= min) {
            return this.colorGradients[0].getStartColor();
        } else if (value >= max) {
            return this.colorGradients[this.colorGradients.length - 1].getEndColor();
        }

        var range = (max - min);
        var valueOnRange = (value - min);
        
        var ratio = (valueOnRange / range);
        var index = Math.floor(ratio * this.colorGradients.length);
        var gradient = this.colorGradients[index];

        var rangeOfGradient = (range * (1 / this.colorGradients.length))
        var valueOnRangeOfGradient = MathUtil.mod(valueOnRange, rangeOfGradient);

        return gradient.getColor(0, rangeOfGradient, valueOnRangeOfGradient);
    }

    return MultiColorGradient;
})