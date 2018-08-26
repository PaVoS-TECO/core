define(['grid', 'gridUtil', 'recursiveRectangleCluster', 'bounds', 'dimension', 'mathUtil'],
function(Grid, GridUtil, RecursiveRectangleCluster, Bounds, Dimension, MathUtil) {
    /**
      * A regular grid, consisting of rectangle clusters with equal size.
      * 
      * @param {*} gridID the identifier of this grid
      * @param {*} bounds the bounds the grid should be applied on
      * @param {*} rows the number of rows
      * @param {*} columns the number of columns
      * @param {*} gridLevels the number of gridLevels
      */
    function RecursiveRectangleGrid(bounds, rows, columns, gridLevels) {
        Grid.call(this, bounds, rows, columns, gridLevels);
    }
    RecursiveRectangleGrid.prototype = Object.create(Grid.prototype);
    RecursiveRectangleGrid.prototype.constructor = RecursiveRectangleGrid;

    /**
      * Get the cluster containing the submitted coordinate at the given gridlevel.
      * 
      * @param {*} coordinate the coordinate
      * @param {*} gridLevel the gridlevel
      */
     RecursiveRectangleGrid.prototype.getClusterContainingCoordinate = function(coordinate, gridLevel) {
        // Call this function in superclass
        Grid.prototype.getClusterContainingCoordinate.call(this);

        var rowColumnArray = [];
        for (level = 1; level <= gridLevel; level++) {
            var clusterDimension = GridUtil.calculateDimensionAtGridLevel(this.getBounds().getDimension(), this.getRows(), this.getColumns(), level);
            
            var row = MathUtil.mod(Math.floor(GridUtil.calculateCoordinateRelativeToBounds(this.getBounds(), coordinate)[0] / clusterDimension.getWidth()), this.getRows());
            var column = MathUtil.mod(Math.floor(GridUtil.calculateCoordinateRelativeToBounds(this.getBounds(), coordinate)[1] / clusterDimension.getHeight()), this.getColumns());

            rowColumnArray.push([row, column]);
        }

        return this.createRecursiveRectanlgeCluster(rowColumnArray);
    };

    /**
      * Get the clusters that are completely or partially contained in the submitted bounds.
      * 
      * @param {*} bounds the bounds
      * @param {*} gridLevel the gridlevel
      */
    RecursiveRectangleGrid.prototype.getClustersContainedInBounds = function(bounds, gridLevel) {
        // Call this function in superclass
        Grid.prototype.getClustersContainedInBounds.call(this);

        var lowerLeftCluster  = this.getClusterContainingCoordinate(bounds.getLowerLeft(), gridLevel);
        var upperRightCluster = this.getClusterContainingCoordinate(bounds.getUpperRight(), gridLevel);

        var rowMin = 0;
        var columnMin = 0;
        var rowMax = 0;
        var columnMax = 0;
        for (level = 1; level <= gridLevel; level++) {
            rowMin    = rowMin    + (lowerLeftCluster.getRow(level)     * Math.pow(this.getRows(),    (gridLevel - level)));
            columnMin = columnMin + (lowerLeftCluster.getColumn(level)  * Math.pow(this.getColumns(), (gridLevel - level)));
            rowMax    = rowMax    + (upperRightCluster.getRow(level)    * Math.pow(this.getRows(),    (gridLevel - level)));
            columnMax = columnMax + (upperRightCluster.getColumn(level) * Math.pow(this.getColumns(), (gridLevel - level)));
        }

        var clusterArray = [];
        for (rowOffset = 0; rowOffset <= (rowMax - rowMin); rowOffset++) {
            for (columnOffset = 0; columnOffset <= (columnMax - columnMin); columnOffset++) {
                clusterArray.push(new RecursiveRectangleCluster(this.getGridID() + ":" + this.calculateClusterID((rowMin + rowOffset), (columnMin + columnOffset), gridLevel)));
            }
        }
        return clusterArray;
    }

    RecursiveRectangleGrid.prototype.getGridID = function() {
        return "recursiveRectangleGrid" + "-" + this.getRows() + "_" + this.getColumns() + "_" + this.getGridLevels();
    };

    RecursiveRectangleGrid.prototype.createRecursiveRectanlgeCluster = function(rowColumnArray) {
        var clusterID = this.getGridID() + ":";

        for (i = 0; i < rowColumnArray.length; i++) {
            clusterID = clusterID + rowColumnArray[i][0] + "_" + rowColumnArray[i][1];
            if (i < (rowColumnArray.length - 1)) {
                clusterID = clusterID + "-";
            }
        }

        return new RecursiveRectangleCluster(clusterID);
    };

    RecursiveRectangleGrid.prototype.calculateClusterID = function(rowOfGridLevel, columnOfGridLevel, gridLevel) {
        clusterID = "";

        for (level = 1; level <= gridLevel; level++) {
            if (level < gridLevel) {
                clusterID = clusterID 
                    + MathUtil.mod(Math.floor(rowOfGridLevel / Math.pow(this.getRows(), (gridLevel - level))), this.getRows())
                    + "_" 
                    + MathUtil.mod(Math.floor(columnOfGridLevel / Math.pow(this.getColumns(), (gridLevel - level))), this.getColumns())
                    + "-";
            } else {
                clusterID = clusterID 
                    + (MathUtil.mod(rowOfGridLevel, this.getRows()))
                    + "_" 
                    + (MathUtil.mod(columnOfGridLevel, this.getColumns()));
            }
        }
        return clusterID;
    };

    return RecursiveRectangleGrid;
});