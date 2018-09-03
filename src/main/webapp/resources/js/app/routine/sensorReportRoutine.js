define(['requestor'], function(Requestor) {
    function SensorReportRoutine(sensorID, reason) {
        this.sensorID = sensorID;
        this.reason = reason;
    };

    SensorReportRoutine.prototype.run = function() {
        Requestor.requestSensorReport(this.sensorID,
                                      this.reason,
                                      this.handleSensorReportRequest.bind(this));
        console.log(this);
    };

    SensorReportRoutine.prototype.handleSensorReportRequest = function(response) {
        console.log(response);
    };

    return SensorReportRoutine;
})