define(function() {
    return {
        /**
          * The modulo operator which turns negative results into their positive equivalent.
          * 
          * For Example: -2 mod 5 = -2 -> -2 mod 5 = 3
          * 
          * @param {*} value the value
          * @param {*} mod the modulo
          */
        mod(value, mod) {
            return (((value % mod) + mod) % mod);
        }
    }
});