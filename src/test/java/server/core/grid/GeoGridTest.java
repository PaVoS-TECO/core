package server.core.grid;

import java.awt.geom.Point2D;

import org.junit.Test;

import server.core.grid.config.WorldMapData;

public class GeoGridTest {

	@Test
	public void test() {
		GeoGrid grid = new GeoRectangleGrid(new Point2D.Double(WorldMapData.lngRange * 2, WorldMapData.latRange * 2),  2, 2, 1, "testGrid");
		
	}

}
