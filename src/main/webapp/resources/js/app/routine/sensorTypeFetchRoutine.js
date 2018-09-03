define(['requestor', 'parser'], function(Requestor, Parser) {
    function SensorTypeFetchRoutine(gridID, callback) {
        this.gridID = gridID;
        this.callback = callback;
    }

    SensorTypeFetchRoutine.prototype.run = function() {
        Requestor.requestSensortypes(this.gridID,
                                     this.handleSensorTypesRequest.bind(this));
                                     console.log(this.gridID);
        console.log("START SensorTypeFetchRoutine");
    }

    SensorTypeFetchRoutine.prototype.handleSensorTypesRequest = function(response) {
        console.log("STOP SensorTypeFetchRoutine");
        this.callback(Parser.arrayStringToArray(response));
    }

    return SensorTypeFetchRoutine;
})