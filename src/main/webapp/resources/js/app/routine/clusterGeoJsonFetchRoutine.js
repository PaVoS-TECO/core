define(['requestor'], function(Requestor) {
    function ClusterGeoJsonFetchRoutine(gridID, clusterID, property, time, steps, callback) {
        this.gridID = gridID; 
        this.clusterID = clusterID; 
        this.property = property; 
        this.time = time; 
        this.steps = steps;
        this.callback = callback;
    };

    ClusterGeoJsonFetchRoutine.prototype.run = function() {
        Requestor.requestClusterGeoJson(this.gridID, 
                                        this.clusterID, 
                                        this.property, 
                                        this.time, 
                                        this.steps, 
                                        this.handleClusterGeoJsonRequest.bind(this));
    };

    ClusterGeoJsonFetchRoutine.prototype.handleClusterGeoJsonRequest = function(response) {
        this.callback(response);
    };

    return ClusterGeoJsonFetchRoutine;
})