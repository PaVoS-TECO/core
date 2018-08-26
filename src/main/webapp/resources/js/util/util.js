define(function() {
    return {
        /**
          * Returns whether the submitted array contains the given value or not.
          * 
          * @param {*} array the array
          * @param {*} value the value
          */
        contains: function(array, value) {
            for (i = 0; i < array.length; i++) {
                if (array[i] === value) {
                    return true;
                }
            }
            return false;
        },

        /**
          * Add the char to the submitted string until the demanded string length is met.
          * 
          * @param {*} string the string that the char is added to
          * @param {*} char the char
          * @param {*} demandedStringLength the demanded string length
          * @param {*} after a boolean that states whether the char should be added before or after the string
          */
        addString: function(string, toAdd, demandedStringLength, after) {
            var toAddLength = String(toAdd).length;
            var leftToAdd = (demandedStringLength - String(string).length);
            
            var toAddResult = '';
            while(toAddLength <= leftToAdd) {
                toAddResult = toAddResult + toAdd;
                leftToAdd = leftToAdd - toAddLength;
            }
            if (leftToAdd > 0) {
                toAddResult = toAdd.substring(0, (leftToAddLength - 1));
            }
            
            var result;
            if (after) {
                result = string + toAddResult;
            } else {
                result = toAddResult + string;
            }
            return result;
        },
        /**
         * Convert the submitted component to a hexadecimal number.
         * 
         * @param {*} c the submitted component
         */
        componentToHex: function(c) {
            var hex = c.toString(16);
            return hex.length == 1 ? "0" + hex : hex;
        },

        /**
         * Convert the (r, g, b) color format into hex format.
         * 
         * @param {*} r the amount of red
         * @param {*} g the amount of green
         * @param {*} b the amount of blue
         */
        rgbToHex: function(r, g, b) {
            return "#" + this.componentToHex(r) + this.componentToHex(g) + this.componentToHex(b);
        },

        /**
         * Returns a random integer between min (inclusive) and max (inclusive)
         * Using Math.round() will give you a non-uniform distribution!
         * 
         * @param {*} min the smallest possible number
         * @param {*} max the biggest possible number
         */
        getRandomInt: function(min, max) {
            return Math.floor(Math.random() * (max - min + 1)) + min;
        }
    }
});