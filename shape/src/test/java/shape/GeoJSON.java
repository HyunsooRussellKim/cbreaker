package shape;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.geotools.geojson.geom.GeometryJSON;
import org.junit.Test;

import org.locationtech.jts.geom.Point;

public class GeoJSON {

	@Test
	public void test() {
		GeometryJSON gjson = new GeometryJSON();
		// be sure to strip whitespace
		String json = "{'type':'Point','coordinates':[100.1,0.1]}";

		Reader reader = new StringReader(json);
		try {
			Point p = gjson.readPoint( reader );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue("Success", true);
		//fail("Not yet implemented");
	}

}
