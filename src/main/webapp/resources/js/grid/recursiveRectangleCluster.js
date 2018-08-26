define(['cluster'], function(Cluster) {
    function RecursiveRectangleCluster(clusterID) {
        Cluster.call(this, clusterID);
    }
    RecursiveRectangleCluster.prototype = Object.create(Cluster.prototype);
    RecursiveRectangleCluster.prototype.constructor = RecursiveRectangleCluster;

    RecursiveRectangleCluster.prototype.getRow = function(gridLevel) {
        // 1 <= gridLevel <= this.getGridLevel()
        gridLevel = Math.max(1, Math.min(this.getGridLevel(), gridLevel));

        return this.getClusterID().split(':')[1].split('-')[gridLevel - 1].split('_')[0];
    }

    RecursiveRectangleCluster.prototype.getColumn = function(gridLevel) {
        // 1 <= gridLevel <= this.getGridLevel()
        gridLevel = Math.max(1, Math.min(this.getGridLevel(), gridLevel));

        return this.getClusterID().split(':')[1].split('-')[gridLevel - 1].split('_')[1];
    }

    RecursiveRectangleCluster.prototype.getGridLevel = function() {
        return this.getClusterID().split(':')[1].split('-').length;
    }

    return RecursiveRectangleCluster;
});