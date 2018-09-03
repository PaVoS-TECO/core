define(function() {
    /**
      * Encapsulates color in hexadecimal and rgb format.
      * 
      * @param {*} params a hex string or an rgb array
      */
    function Color(params) {
        if (params.constructor !== Array) {
            this.setHex(params);
        } else {
            this.setRGB(params);
        }
    }

    /**
      * Update this color with a hexadecimal color value.
      * 
      * @param {*} hex the hex
      */
    Color.prototype.setHex = function(hex) {
        if (String(hex).match('#([0-9a-f]{6})') == null) {
            throw new Error('Invalid hexadecimal color');
        }

        this.hex = hex;
    }
    /**
      * Get the hexadecimal value of this color.
      */
    Color.prototype.getHex = function() {
        return this.hex;
    }
    /**
      * Update this color with a rgb color value.
      * 
      * @param {*} params the rgb array
      */
    Color.prototype.setRGB = function(params) {
        if ((params.length != 3)
            || (params[0] < 0) || (params[0] > 255)
            || (params[1] < 0) || (params[1] > 255)
            || (params[2] < 0) || (params[2] > 255)) {

            throw new Error("Invalid RGB color array");

        }

        var result = '#';

        var temp;
        for (i = 0; i < 3; i++) {
            temp = params[i].toString(16);
            if (temp.length == 1) {
                temp = '0' + temp;
            }
            result = result + temp;
        }

        this.hex = result;
    }
    /**
      * Get the rgb values for this color as an array.
      */
    Color.prototype.getRGB = function () {
        var temp = this.hex;
        if (this.hex.charAt(0) == '#') {
            temp = this.hex.substring(1, 7);
        }

        var red = parseInt(temp.substring(0, 2), 16);
        var green = parseInt(temp.substring(2, 4), 16);
        var blue = parseInt(temp.substring(4, 6), 16);

        return [red, green, blue];
    }

    return Color;
})