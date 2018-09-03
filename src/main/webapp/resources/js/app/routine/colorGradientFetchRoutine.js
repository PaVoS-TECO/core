define(['requestor'], function(Requestor) {
    function ColorGradientFetchRoutine(callback) {
        this.callback = callback;
    };

    ColorGradientFetchRoutine.prototype.run = function() {
        Requestor.requestColorGradients(this.handleColorGradientsRequest.bind(this));
        console.log("START ColorGradientFetchRoutine");
    };

    ColorGradientFetchRoutine.prototype.handleColorGradientsRequest = function(response) {
        console.log("STOP ColorGradientFetchRoutine");
        this.callback(JSON.parse(response));
    }

    return ColorGradientFetchRoutine;
})