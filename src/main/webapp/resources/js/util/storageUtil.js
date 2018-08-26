define(function() {
    var IDENTIFIERS_ARRAY_KEY = "identifiersArray";

    return {
        store: function(identifier, value) {
            localStorage.setItem(identifier, value);
        },

        get: function(identifier) {
            return localStorage.getItem(identifier);
        },

        delete: function(identifier) {
            localStorage.removeItem(identifier);
        },

        addToIdentifiersArray: function(identifier) {
            var identifiersArray = localStorage.getItem(IDENTIFIERS_ARRAY_KEY);
        
            if (identifiersArray == null) {
                identifiersArray = [identifier];
                this.store(IDENTIFIERS_ARRAY_KEY, JSON.stringify(identifiersArray));
            } else {
                if (!identifiersArray.includes(identifier)) {
                    array = JSON.parse(identifiersArray);
                    if (!array.includes(identifier)) {
                        array.push(identifier);
                    }
                    this.store(IDENTIFIERS_ARRAY_KEY, JSON.stringify(array));
                }
            }
        },

        removeFromIdentifiersArray: function(identifier) {
            var identifiersArray = JSON.parse(this.getIdentifiersArray());

            // Search for identifier in identifiers array
            for(i = 0; i < identifiersArray.length; i++) {
                if(identifiersArray[i] === identifier) {
                   identifiersArray.splice(i, 1);
                   break;
                }
            }

            if (identifiersArray.length == 0) {
                this.delete(IDENTIFIERS_ARRAY_KEY);
            }

            this.store(IDENTIFIERS_ARRAY_KEY, JSON.stringify(identifiersArray));
        },
        
        getIdentifiersArray: function() {
            return this.get(IDENTIFIERS_ARRAY_KEY);
        }
    }
});