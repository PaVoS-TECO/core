package web.view.map;

import web.grid.Grid;

/**
 * A map layer that can be displayed on an AbstractMap.
 */
public class MapLayer {

    /**
     * Default constructor
     */
    public MapLayer() {
    }


    /**
     * 
     */
    protected AbstractMap layers;

    /**
     * Set the grid of this MapLayer.
     * @param grid
     */
    public void setGrid(Grid grid) {
        // TODO implement here
    }

    /**
     * Get the Grid of this MapLayer.
     * @return the Grid of this MapLayer.
     */
    public Grid getGrid() {
        // TODO implement here
        return null;
    }

}