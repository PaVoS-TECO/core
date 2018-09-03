define(['requestor'], function(Requestor) {
    function SensorGeoJsonFetchRoutine(gridID, clusterID, property) {
        this.gridID = gridID; 
        this.clusterID = clusterID; 
        this.property = property;
    };

    SensorGeoJsonFetchRoutine.prototype.run = function() {
        Requestor.requestSensorGeoJson(this.gridID, 
                                       this.clusterID, 
                                       this.property,
                                       this.handleSensorGeoJsonRequest.bind(this));
    };

    SensorGeoJsonFetchRoutine.prototype.handleSensorGeoJsonRequest = function(response) {
        console.log(response);
    };

    return SensorGeoJsonFetchRoutine;
})