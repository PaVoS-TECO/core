define(function() {
    separatedToArray = function(input, separator) {
        var output = [];
        if (input != undefined) {
            array = String(input).split(separator);
            for (i = 0; i < array.length; i++) {
                output.push(array[i]);
            }
        }
        return output;
    }

    arrayStringToArray = function(input) {
        var output = [];
        try {
            output = JSON.parse(input);
        } catch {
            throw new Error(input + " isn't valid json");
        }
        return output;
    }

    jsonStringToObject = function(input) {
        var output = {};
        try {
            output = JSON.parse(input);
        } catch {
            throw new Error(input + " isn't valid json")
        }
        return output;
    }

    return {
        commaSeparatedToArray,
        arrayStringToArray,
        jsonStringToObject
    }
})