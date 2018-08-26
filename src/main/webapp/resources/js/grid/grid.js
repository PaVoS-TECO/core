define(['bounds', 'dimension'], function() {
    /**
      * A regular grid, consisting of clusters with equal shape and size and the possibility to be identified 
      * by the row and column they're in.
      * 
      * @param {*} bounds the bounds the grid should be applied on
      * @param {*} rows the number of rows
      * @param {*} columns the number of columns
      * @param {*} gridLevels the number of gridlevels
      */
    function Grid(bounds, rows, columns, gridLevels) {
        this.bounds = bounds;
        this.rows = rows;
        this.columns = columns;
        this.gridLevels = gridLevels;
    }

    /**
      * Set the bounds of this grid.
      * 
      * @param {*} bounds the bounds
      */
    Grid.prototype.setBounds = function(bounds) {
        this.bounds = bounds;
    }
    /**
      * Get the bounds of this grid.
      */
    Grid.prototype.getBounds = function() {
        return this.bounds;
    }
    /**
      * Set the number of rows.
      * 
      * @param {*} rows the rows
      */
    Grid.prototype.setRows = function(rows) {
        this.rows = rows;
    }
    /**
      * Get the number of rows.
      */
    Grid.prototype.getRows = function() {
        return this.rows;
    }
    /**
      * Set the number of columns.
      * 
      * @param {*} columns the columns
      */
    Grid.prototype.setColumns = function(columns) {
        this.columns = columns;
    }
    /**
      * Get the number of columns.
      */
    Grid.prototype.getColumns = function() {
        return this.columns;
    }
    /**
      * Set the number of gridlevels.
      * 
      * @param {*} gridLevels the gridlevels
      */
    Grid.prototype.setGridLevels = function(gridLevels) {
        this.gridLevels = gridLevels;
    }
    /**
      * Get the number of gridlevels.
      */
    Grid.prototype.getGridLevels = function() {
        return this.gridLevels;
    }

    /**
      * Get the identifier of this grid.
      */
    Grid.prototype.getGridID = function() { }

    /**
      * Get the cluster containing the submitted coordinate at the given gridlevel.
      * 
      * @param {*} coordinate the coordinate
      * @param {*} gridLevel the gridlevel
      */
    Grid.prototype.getClusterContainingCoordinate = function(coordinate, gridLevel) { }

    /**
      * Get the clusters that are completely or partially contained in the submitted bounds.
      * 
      * @param {*} bounds the bounds
      * @param {*} gridLevel the gridlevel
      */
    Grid.prototype.getClustersContainedInBounds = function(bounds, gridLevel) { }

    return Grid;
});