define(['requestor'], function(Requestor) {
    function GridIDFetchRoutine(callback) {
        this.callback = callback;
    }

    GridIDFetchRoutine.prototype.run = function() {
        Requestor.requestGridID(this.handleGridIDRequest.bind(this));
        console.log("START GridIDFetchRoutine");
    };

    GridIDFetchRoutine.prototype.handleGridIDRequest = function(response) {
        console.log("STOP GridIDFetchRoutine");
        this.callback(response);
    };

    return GridIDFetchRoutine;
})