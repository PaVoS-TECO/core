define(function() {
    function Cluster(clusterID) {
        this.clusterID = clusterID;
    }

    Cluster.prototype.setClusterID = function(clusterID) {
        this.clusterID = clusterID;
    }

    Cluster.prototype.getClusterID = function() {
        return this.clusterID;
    }

    return Cluster;
});