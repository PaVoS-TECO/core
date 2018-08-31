package server.core.grid;

import java.awt.geom.Rectangle2D;

import org.junit.Test;

import server.core.grid.config.WorldMapData;

public class GeoGridTest {

	@Test
	public void test() {
		GeoGrid grid = new GeoRecRectangleGrid(new Rectangle2D.Double(
				- WorldMapData.lngRange, - WorldMapData.latRange,WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  
				2, 2, 1);
		
	}

}
