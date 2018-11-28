package com.citus.controller;
 
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.geometry.BoundingBox;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.citus.datastore.CitusDataStoreFactory;
import com.citus.datastore.CitusJndiDataStoreFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.operation.union.UnaryUnionOp;

import egovframework.rte.fdl.property.EgovPropertyService;

@Controller
public class ShapeController 
{
	Logger logger = Logger.getLogger(ShapeController.class);

	@Resource(name = "propertyService")
    protected EgovPropertyService propertyService;

	Integer srid = 5186;
	
	Map<String, String> labelPropertyMaps = new HashMap<String, String>();
	
	public ShapeController() {
		// properties to be selected from ORACLE 
		/*labelPropertyMaps.put("WTL_PIPE_LM.SA001", "NVL(a.FTR_STR, \' \') as FTR_STR"); 
		labelPropertyMaps.put("WTL_SPLY_LS.SA002", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_STPI_PS.SA003", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_MANH_PS.SA100", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_HEAD_PS.SA110", "NVL(a.FTR_STR, \' \') as FTR_STR, NVL(a.HEA_NAM, \' \') as HEA_NAM");
		labelPropertyMaps.put("WTL_GAIN_PS.SA112", "NVL(a.FTR_STR, \' \') as FTR_STR, NVL(a.GAI_NAM, \' \') as GAI_NAM");
		labelPropertyMaps.put("WTL_PURI_AS.SA113", "NVL(a.FTR_STR, \' \') as FTR_STR, NVL(a.PUR_NAM, \' \') as PUR_NAM");
		labelPropertyMaps.put("WTL_SERV_PS.SA114", "NVL(a.FTR_STR, \' \') as FTR_STR, NVL(a.SRV_NAM, \' \') as SRV_NAM");
		labelPropertyMaps.put("WTL_FLOW_PS.SA117", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_FIRE_PS.SA118", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_FIRE_PS.SA119", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_RSRV_PS.SA120", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_PRGA_PS.SA121", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_META_PS.SA122", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_WTQT_PS.SA123", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA200", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA201", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA202", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA203", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA204", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA205", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_PRES_PS.SA206", "NVL(a.FTR_STR, \' \') as FTR_STR, NVL(a.PRS_NAM, \' \') as PRS_NAM");
		labelPropertyMaps.put("WTL_LEAK_PS.SA300", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_PIPE_PS.SA900", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_RDCG_PS.SA224", "NVL(a.FTR_STR, \' \') as FTR_STR, NVL(a.RDG_NAM, \' \') as RDG_NAM");
		labelPropertyMaps.put("WTL_MANH_PS.SA991", "NVL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_CTPN_PS.SA992", "NVL(a.FTR_STR, \' \') as FTR_STR, NVL(a.CTP_NAM, \' \') as CTP_NAM");
		labelPropertyMaps.put("WTL_BLOK_AS.SA993", "NVL(a.BLK_NAM, \' \') as BLK_NAM");*/
		
		// properties to be selected from MYSQL		
		labelPropertyMaps.put("WTL_PIPE_LM.SA001", "IFNULL(a.FTR_STR, \' \') as FTR_STR"); 
		labelPropertyMaps.put("WTL_SPLY_LS.SA002", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_STPI_PS.SA003", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_MANH_PS.SA100", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_HEAD_PS.SA110", "IFNULL(a.FTR_STR, \' \') as FTR_STR, IFNULL(a.HEA_NAM, \' \') as HEA_NAM");
		labelPropertyMaps.put("WTL_GAIN_PS.SA112", "IFNULL(a.FTR_STR, \' \') as FTR_STR, IFNULL(a.GAI_NAM, \' \') as GAI_NAM");
		labelPropertyMaps.put("WTL_PURI_AS.SA113", "IFNULL(a.FTR_STR, \' \') as FTR_STR, IFNULL(a.PUR_NAM, \' \') as PUR_NAM");
		labelPropertyMaps.put("WTL_SERV_PS.SA114", "IFNULL(a.FTR_STR, \' \') as FTR_STR, IFNULL(a.SRV_NAM, \' \') as SRV_NAM");
		labelPropertyMaps.put("WTL_FLOW_PS.SA117", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_FIRE_PS.SA118", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_FIRE_PS.SA119", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_RSRV_PS.SA120", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_PRGA_PS.SA121", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_META_PS.SA122", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_WTQT_PS.SA123", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA200", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA201", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA202", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA203", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA204", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_VALV_PS.SA205", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_PRES_PS.SA206", "IFNULL(a.FTR_STR, \' \') as FTR_STR, IFNULL(a.PRS_NAM, \' \') as PRS_NAM");
		labelPropertyMaps.put("WTL_LEAK_PS.SA300", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_PIPE_PS.SA900", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_RDCG_PS.SA224", "IFNULL(a.FTR_STR, \' \') as FTR_STR, IFNULL(a.RDG_NAM, \' \') as RDG_NAM");
		labelPropertyMaps.put("WTL_MANH_PS.SA991", "IFNULL(a.FTR_STR, \' \') as FTR_STR");
		labelPropertyMaps.put("WTL_CTPN_PS.SA992", "IFNULL(a.FTR_STR, \' \') as FTR_STR, IFNULL(a.CTP_NAM, \' \') as CTP_NAM");
		labelPropertyMaps.put("WTL_BLOK_AS.SA993", "IFNULL(a.BLK_NAM, \' \') as BLK_NAM");
		
	}
	
	public String dbType() {
		String drvier = propertyService.getString("Global.shape.driverClassName");
		if (drvier.contains((CharSequence)"mysql")) {
			return "MySQL";
		}
		return "Oracle";
	}
	
	@RequestMapping(value="/shapeImport.do")
	public String shapeImport()
	{
		return "shapeImport.tiles";
	}

	
	@RequestMapping(value="/doShapeImport.do", method=RequestMethod.POST)
	public void shapeImport(Map<String, Object> commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		File workDir = null;
		String result = "success";
		String message = "OK";
		
		Map<String, Object> retObjects = new HashMap<String, Object>();		
		
		try
		{
			// 임시 작업 디렉토리를 설정한다
			String rootDir = propertyService.getString("Global.shape.workDir"); 
			workDir = new File(String.format("%s%s%s", rootDir, File.separator, UUID.randomUUID().toString()));
			
			
			// 파일을 저장한다
			List<File> shapeList = saveShapeFile(workDir, request);
			
			// 임포트를 시작한다
			importShape(shapeList, srid);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			logger.debug(ex);
			message = (ex.getCause() != null) ? ex.getCause().getMessage() : "Unknown";   
			result = "fail";
		}
		finally
		{
			deleteDirectory(workDir);
		}
		
		retObjects.put("result", result);
		retObjects.put("message", message);
		response.setContentType("text/json");
		GeoJSONUtil2.encode(retObjects, response.getOutputStream());
		
	}

	private void importShape(List<File> shapeList, Integer srid) throws Exception 
	{
		// JDBC data store를 얻어온다
		JDBCDataStore store = this.getJDBCDataStore();
		// Transaction tran = new DefaultTransaction("import");
		SimpleFeatureIterator it = null;
		
		try
		{
			for (File file : shapeList)
			{
				String fileName  = file.getName();
				String tableName = fileName.substring(0, fileName.length() - FilenameUtils.getExtension(fileName).length() - 1);
				if (this.dbType().equals("MySQL")) {
					tableName = tableName.toLowerCase();
				}
				String geometryTableName = this.getGeometryTableName(tableName);
				
				// attribute table과 geometry table을 확인한다
				verifyTables(store, tableName);
				
				JDBCFeatureStore attrStore = (JDBCFeatureStore) store.getFeatureSource(tableName);
				JDBCFeatureStore geomStore = (JDBCFeatureStore) store.getFeatureSource(geometryTableName);
				
				// commented out because of long-time-taken transaction
				//attrStore.setTransaction(tran);
				//geomStore.setTransaction(tran);
	
				// read from shape
				FileDataStore fileStore = this.getFileDataStore(file);
				// FileDataStoreFinder.getDataStore(file);
				SimpleFeatureSource fileFeatureSource = fileStore.getFeatureSource();
	
				SimpleFeatureCollection fileCols = fileFeatureSource.getFeatures();
				List<SimpleFeature> features = new ArrayList<SimpleFeature>();
				
				it = fileCols.features();
	
				// import attributes
				while (it.hasNext())
				{
					SimpleFeature f = it.next();
					String ftr_idn;
					if(f.getAttribute(IDN_COLUMN_NAME) instanceof Double) {
						ftr_idn = String.valueOf(((Double)f.getAttribute(IDN_COLUMN_NAME)).longValue());
					} else {
						ftr_idn = f.getAttribute(IDN_COLUMN_NAME).toString();
					}
					String ftr_cde = f.getAttribute(CDE_COLUMN_NAME).toString();
					String compositeKey;
					if (tableName.equals("WTL_BLOK_AS")) {
						compositeKey = ftr_idn;
					}
					else {
						compositeKey = ftr_cde + "." + ftr_idn;
					}
					FeatureIdImpl fid = (FeatureIdImpl)f.getIdentifier();
					fid.setID(compositeKey);
					
					f.getUserData().put(Hints.USE_PROVIDED_FID, true);
					f.getUserData().put(Hints.PROVIDED_FID, compositeKey);
					features.add(f);
				}

				logger.debug("features importing");

				attrStore.addFeatures(features);
				
				logger.debug("features imported");
	
				// import geometry
				SimpleFeatureType featureType = geomStore.getSchema();
				SimpleFeatureBuilder fb = new SimpleFeatureBuilder(featureType);
				
				List<SimpleFeature> geometries = new ArrayList<SimpleFeature>();
				for (SimpleFeature f : features)
				{
					Geometry g  = (Geometry)f.getDefaultGeometry();
					String   id = f.getID();
					String   fid;
					if (tableName.equals("WTL_BLOK_AS")) {
						fid = id;
					}
					else {
						String[] split = id.split( "\\." );
						fid = split[1];
					}
					logger.debug("column id : " + fid);

					if (g != null)
					{
						BoundingBox bb = f.getBounds();
						fb.set("GEOMETRY", f.getDefaultGeometry());
						fb.set("MINX", Math.floor(bb.getMinX()));
						fb.set("MINY", Math.floor(bb.getMinY()));
						fb.set("MAXX", Math.ceil(bb.getMaxX()));
						fb.set("MAXY", Math.ceil(bb.getMaxY()));
						g.setSRID(srid);
					}
					else {
						logger.debug("Geometry == null");
					}
					
					SimpleFeature newFeature = fb.buildFeature(fid);
					newFeature.getUserData().put(Hints.USE_PROVIDED_FID, true);
					newFeature.getUserData().put(Hints.PROVIDED_FID, fid);
					geometries.add(newFeature);
				}
				
				geomStore.addFeatures(geometries);
			}
			
			//tran.commit();
			logger.info("--------------------- successfully imported -------------------");
		}
		catch (Exception ex)
		{
			//tran.rollback();
			logger.debug("exception in importShape() : " + ex.getMessage());
			throw ex;
		}
		finally
		{
			if (it != null) { 
				it.close(); 
			}
			//tran.close();
		}
	}

	static final String GEOMETRY_TABLE_PREFIX = "G_";
	static final String IDN_COLUMN_NAME = "FTR_IDN";
	static final String CDE_COLUMN_NAME = "FTR_CDE";
	
	String getGeometryTableName(String tableName)
	{
		return GEOMETRY_TABLE_PREFIX + tableName;
	}
	
	
	ContentFeatureSource getFeatureSource(JDBCDataStore store, String tableName)
	{
		try
		{
			logger.debug("db schema = " + store.getDatabaseSchema());
			logger.debug("DB name : " + tableName);
			return store.getFeatureSource(tableName);
		}
		catch (Exception ex) 
		{
			logger.debug("JDBCDataStore.getFeatureSource() throws exception with message - " + ex.getMessage());
			return null;
		}
	}
	
	private void verifyTables(JDBCDataStore store, String tableName) throws Exception 
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			// check attirubute table
			if (getFeatureSource(store, tableName) == null) {
				throw new Exception("No attribute table exists!!! - " + tableName);
			}
		}
		catch (Exception ex)
		{
			store.closeSafe(stmt);
			if(!conn.isClosed()) store.closeSafe(conn);
			throw ex;
		}
			
		try
		{
			// check geometry table
			String geometryTableName = this.getGeometryTableName(tableName);
			if (getFeatureSource(store, geometryTableName)==null)
			{
				conn = store.getConnection(Transaction.AUTO_COMMIT);
				String sql0 = "";
				String sql = "";		
				
				sql0 = String.format("SELECT * FROM Information_schema.tables WHERE table_name = '%s'", geometryTableName);
				
				PreparedStatement stmt0 = conn.prepareStatement(sql0);
				ResultSet  rs0 = stmt0.executeQuery();
				
				if(!rs0.next()) {
					// create geometry table
					if (this.dbType().equals("MySQL")) {
						sql = String.format("create table %s (" +
								"FTR_IDN  int    not null auto_increment," +
								"GEOMETRY BLOB   null," +
								"MINX     FLOAT  null," +
								"MINY     FLOAT  null," +
								"MAXX     FLOAT  null," +
								"MAXY     FLOAT  null," +
								"primary key(FTR_IDN))", geometryTableName);
					}
					else if (this.dbType().equals("Oracle")) {
						sql = String.format("create table %s (" +
								"FTR_IDN  integer not null," +
								"GEOMETRY BLOB    null," +
								"MINX     FLOAT   null," +
								"MINY     FLOAT   null," +
								"MAXX     FLOAT   null," +
								"MAXY     FLOAT   null," +
								"primary key(FTR_IDN))", geometryTableName);
					}
					
					stmt = conn.prepareStatement(sql);
					stmt.execute();
					store.closeSafe(stmt);

					// create index
					// sql = String.format("alter table %s add index idx_minx_%s(MINX)", geometryTableName, geometryTableName);
					sql = String.format("create index idx_minx_%s on %s(MINX)", geometryTableName, geometryTableName);
					stmt = conn.prepareStatement(sql);
					stmt.execute();
					store.closeSafe(stmt);
					
					//sql = String.format("alter table %s add index idx_miny_%s(MINY)", geometryTableName, geometryTableName);
					sql = String.format("create index idx_miny_%s on %s(MINY)", geometryTableName, geometryTableName);
					stmt = conn.prepareStatement(sql);
					stmt.execute();
					store.closeSafe(stmt);
					
					sql = String.format("create index idx_maxx_%s on %s(MAXX)", geometryTableName, geometryTableName);
					stmt = conn.prepareStatement(sql);
					stmt.execute();
					store.closeSafe(stmt);
					
					sql = String.format("create index idx_maxy_%s on %s(MAXY)", geometryTableName, geometryTableName);
					stmt = conn.prepareStatement(sql);
					stmt.execute();
					store.closeSafe(stmt);
				}
			}
		}
		catch (Exception ex)
		{
			logger.debug("geometry table : " + ex.getMessage());
			throw ex;
		} finally {
			if (store != null)
			{
				store.closeSafe(stmt);
				if(conn!=null && !conn.isClosed()) store.closeSafe(conn);
			}
		}
	}

	private FileDataStore getFileDataStore(File file) throws Exception 
	{
		Properties prop = new Properties();
		prop.put( "url", file.toURI().toURL());
		prop.put( "charset", "EUC-KR");
	
		FileDataStore dataStore = (FileDataStore) DataStoreFinder.getDataStore(prop);
		return dataStore;
	}
	
	// database store
	private JDBCDataStore getJDBCDataStore() throws IOException
	{
		String connectionType = propertyService.getString("Global.shape.connectionType");	
		
		if(connectionType.equals("JNDI")) {
			String jndiName = propertyService.getString("Global.shape.jndiName");
			
			Map map = new HashMap();
			map.put( "dbtype", "oracle");
			map.put( "jndiReferenceName", jndiName);
			map.put( "srid", srid);
			map.put( "Expose primary keys", true);
			
			return CitusJndiDataStoreFactory.getDefaultStore(map);
		} else {

			String driver = propertyService.getString("Global.shape.driverClassName");
			String dbURL = propertyService.getString("Global.shape.url");
			String user = propertyService.getString("Global.shape.username");
			String passwd = propertyService.getString("Global.shape.password");
			String maxConn = propertyService.getString("Global.shape.maxConnection");
			String minConn = propertyService.getString("Global.shape.minConnection");
			String validateConnections = propertyService.getString("Global.shape.validateConnections");
			
			Properties prop = new Properties();
			prop.put( "driver", driver );
			prop.put( "srid", srid);
			prop.put( "url", dbURL);
			prop.put( "user", user);
			prop.put( "passwd", passwd);
			prop.put( "validate connections", validateConnections);
			prop.put( "max connections", maxConn);
			prop.put( "min connections", minConn);
			
			prop.put( "Expose primary keys", true);
			return CitusDataStoreFactory.getDefaultStore(prop);
		}
	}

	private void deleteDirectory(File dir)
	{
		try 
		{ 
			if (dir == null || !dir.exists())
				return;
			FileUtils.deleteDirectory(dir); 
		} 
		catch (Exception ex)
		{
			logger.debug(ex);
		}
	}
	
	private List<File> saveShapeFile(File workDir, HttpServletRequest request) throws Exception 
	{
		if (!workDir.mkdirs()) 
			throw new Exception("Failed to create working folder");
		if (!AbstractMultipartHttpServletRequest.class.isInstance(request))
			throw new Exception("Request must be a multipart");
	
		DefaultMultipartHttpServletRequest multipartRequest = (DefaultMultipartHttpServletRequest)request;
		MultiValueMap<String, MultipartFile> fileMap = multipartRequest.getMultiFileMap();
		if (fileMap==null || fileMap.size() == 0)
			throw new Exception("No files to update");

		Set<String> keys = fileMap.keySet();
		List<File>  shapeList = new ArrayList<File>();
		
		for (String key : keys)
		{
			List<MultipartFile> fileList = fileMap.get(key);
			for (MultipartFile multipartFile : fileList)
			{
				if (multipartFile.getSize() <= 0)
					continue;
				
				String orgName  = multipartFile.getOriginalFilename();
				File   dest     = new File(String.format("%s%s%s", workDir.getAbsolutePath(), File.separator, orgName));
				
				logger.debug(orgName);
				multipartFile.transferTo(dest);
				
				String ext = FilenameUtils.getExtension(orgName);
				if ("SHP".equalsIgnoreCase(ext))
					shapeList.add(dest);
			}
		}
		
		return shapeList;
	}
	
	@RequestMapping(value="/doShapeExport.do")
	public void exportShape(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		String rootDir = propertyService.getString("Global.shape.workDir"); 
		File workDirFile = null;
		
		JDBCDataStore store = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			String tableName = commandMap.get("table").toString();				// ex "WTL_FIRE_PS"
			String geometryTableName = this.getGeometryTableName(tableName);
			String condition = commandMap.get("condition").toString();			// ex 'layer.FTR_CDE="SA119"'
			condition = HtmlEntities.decode(condition);
			String tableGeometryType = commandMap.get("type").toString();		// ex "path", "point", "polygon"
			String epsg = commandMap.get("epsg").toString();					// ex "EPSG:5186"
			
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String baseFileName = tableName + "_" + dateFormat.format(new Date());  
			
			// 읽어들일 테이블 데이타스토어
			store = this.getJDBCDataStore();
			JDBCFeatureStore attrStore = (JDBCFeatureStore) store.getFeatureSource(tableName);
			
			SimpleFeatureTypeBuilder ftBuilder = new SimpleFeatureTypeBuilder(); 
			//set global state
			ftBuilder.setName(geometryTableName);
			ftBuilder.setNamespaceURI("http://www.citus.co.kr/");
			ftBuilder.setSRS("EPSG:5186");

			List<AttributeDescriptor> ads =  attrStore.getSchema().getAttributeDescriptors();
			
			ftBuilder.addAll(ads);	// 기존 DB 스키마 추가.
			
			// 추가로 필요한 것을 add(). 아마도 geometry.
			ftBuilder.setDefaultGeometry( "the_geom" );
			if (tableGeometryType.equals("path")) {
				ftBuilder.add("the_geom", LineString.class);
			}
			else if (tableGeometryType.equals("point")) {
				ftBuilder.add( "the_geom", Point.class );
			}
			else if (tableGeometryType.equals("polygon")) {
				ftBuilder.add( "the_geom", Polygon.class );
			}

			SimpleFeatureType fType = ftBuilder.buildFeatureType();

			List<SimpleFeature> featureList = new ArrayList<SimpleFeature>();
			conn = store.getConnection(Transaction.AUTO_COMMIT);
			
			String sql = String.format("select layer.*, geom.geometry from %s layer,  %s geom where layer.%s=geom.%s ",
					tableName, geometryTableName, IDN_COLUMN_NAME, IDN_COLUMN_NAME);
			
			if (!condition.equals("*")) {
				sql += " and (" + condition + ")";
			}
			
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				try {
					featureList.add(buildFeatureFromResultSet(rs, fType, tableName));
				}
				catch(Exception e) {
					e.printStackTrace();
					logger.debug(e);
					
					continue;
				}
			}
			
			// write할 shapefile 생성
			workDirFile = new File(String.format("%s%s%s", rootDir, File.separator, baseFileName));
			workDirFile.mkdir();
			
			File shapeFile = File.createTempFile(baseFileName + "_", ".shp", workDirFile);
			
			ShapefileDataStoreFactory shapeStoreFactory = new ShapefileDataStoreFactory();

	        Map<String, Serializable> params = new HashMap<String, Serializable>();
	        params.put(ShapefileDataStoreFactory.URLP.key, shapeFile.toURI().toURL());
	        params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, Boolean.TRUE);
	        params.put(ShapefileDataStoreFactory.DBFCHARSET.key, "EUC-KR");
	        
	        ShapefileDataStore shapefileStore = (ShapefileDataStore) shapeStoreFactory.createNewDataStore(params);
	        shapefileStore.createSchema(fType);
	        
	        Transaction transaction = new DefaultTransaction("create");

	        String typeName = shapefileStore.getTypeNames()[0];
	        SimpleFeatureSource featureSource = shapefileStore.getFeatureSource(typeName);

	        if (featureSource instanceof SimpleFeatureStore) {
	            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
	            SimpleFeatureCollection collection = new ListFeatureCollection(fType, featureList);
	            featureStore.setTransaction(transaction);
	            try {
	                featureStore.addFeatures(collection);
	                transaction.commit();
	            } catch (Exception e) {
	                e.printStackTrace();
	                logger.debug(e);
	                transaction.rollback();
	            } finally {
	                transaction.close();
	            }
	        } else {
	            logger.debug(typeName + " does not support read/write access");
	        }

			String zipName = workDirFile.getName();
			int pos = zipName.lastIndexOf(".");
			if (pos > 0) {
			    zipName = zipName.substring(0, pos);
			}
			zipName += ".zip";
			
			String zipPath = String.format("%s%s%s", rootDir, File.separator, zipName);		
			
	        ZipFiles zipFiles = new ZipFiles();
	        zipFiles.zipDirectory(workDirFile, zipPath);
			
			Map<String, Object> resultJsonMap = new HashMap<String, Object>();
			resultJsonMap.put("result", "ok");
			resultJsonMap.put("zip", zipName);
			
			response.setContentType("text/json");
			GeoJSONUtil2.encode(resultJsonMap, response.getOutputStream());
		}
		catch (IOException ioe) {
			logger.debug("exception in exportShape() : " + ioe.getMessage());
			logger.debug(ioe);
			throw ioe;
		}
		catch (Exception e)
		{
			logger.debug("exception in exportShape() : " + e.getMessage());
			logger.debug(e);
			throw e;
		}
		finally
		{
			if (store != null)
			{
				store.closeSafe(rs);
				store.closeSafe(stmt);
				if(!conn.isClosed()) store.closeSafe(conn);
			}
			//deleteDirectory(workDirFile);
		}
	}

	/**
	 * SimpleFeatureType Data 구성
	 * @param rs
	 * @param type
	 * @param table
	 * @return
	 * @throws Exception
	 */
	private SimpleFeature buildFeatureFromResultSet(ResultSet rs, SimpleFeatureType type, String table) throws Exception {
		List<Object> values = new ArrayList<Object>();
		Geometry defaultGeometry = null;
		
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		
		for (int i = 1; i <= columnCount; i++) {
			String column = meta.getColumnName(i);
			String className = meta.getColumnClassName(i);
			int columnType = meta.getColumnType(i);
			String columnTypeName = meta.getColumnTypeName(i);
			int index = type.indexOf(column);
			Object value = rs.getObject(i);
			if(columnTypeName.endsWith("BLOB")) {
				if (value != null) {
					defaultGeometry = new WKBReader().read(rs.getBytes(i));	//Geometry geometry = new WKBReader().read((byte[])value);
					int the_geom_index = type.indexOf("the_geom");
					values.add(the_geom_index, defaultGeometry);
				}
			} else	if (columnType>=2 && columnType<=8) {//NUMERIC, NUMBER(2), DECIMAL(3), INTEGER(4), SMALLINT(5), FLOAT(6), REAL(7), DOUBLE(8)
				if (value == null) {
					value = -1;
				}
				values.add(index, value);

			} else {
				values.add(index, value);
			}
		}

		String ftr_idn = String.valueOf(rs.getLong(IDN_COLUMN_NAME));
		String compositeKey;
		if (table.equals("WTL_BLOK_AS")) {
			compositeKey = ftr_idn;
		}
		else {
			String ftr_cde = rs.getString(CDE_COLUMN_NAME);
			compositeKey = ftr_cde + "." + ftr_idn;
		}
		SimpleFeatureImpl feature = new SimpleFeatureImpl(values, type, new FeatureIdImpl(compositeKey));

		feature.getUserData().put(Hints.USE_PROVIDED_FID, true);
		feature.getUserData().put(Hints.PROVIDED_FID, compositeKey);

		feature.setDefaultGeometry(defaultGeometry);

		return feature;
	}
	
	
	private String getColumnsSelected(String table) 
	{
		String default_columns = "a.FTR_IDN, a.FTR_CDE, b.geometry, ";
		String columns = labelPropertyMaps.get(table);
		
		return default_columns + columns;
	}
	
	@RequestMapping(value="/areaQuery.json.do", method=RequestMethod.POST)
	public void areaQuery(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		ret.put("type", "FeatureCollection");
		ret.put("features", list);
		
		JDBCDataStore store = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			store = this.getJDBCDataStore();
			
			String tableName = commandMap.get("table").toString();
			String featureCode = commandMap.get("code").toString(); 
			String geometryTableName = this.getGeometryTableName(tableName);
			String columnsSelected = this.getColumnsSelected(tableName + "." + featureCode);
			
			Integer minx = NumberUtils.parseNumber(commandMap.get("MINX").toString(), Integer.class);
			Integer miny = NumberUtils.parseNumber(commandMap.get("MINY").toString(), Integer.class);
			Integer maxx = NumberUtils.parseNumber(commandMap.get("MAXX").toString(), Integer.class);
			Integer maxy = NumberUtils.parseNumber(commandMap.get("MAXY").toString(), Integer.class);
			
			//conn = store.getConnection(Transaction.AUTO_COMMIT);
			
			conn = store.getDataSource().getConnection();
			
			String sql = String.format("select %s from %s a,  %s b where a.%s=b.%s and " +
					" a.%s = '%s' and " +
					"(" +
						"((b.minx between %d and %d or b.maxx between %d and %d) or (b.miny between %d and %d or b.maxy between %d and %d))" +
						" or " +
						"((%d between b.minx and b.maxx or %d between b.minx and b.maxx) or (%d between b.miny and b.maxy or %d between b.miny and b.maxy))" + 
					")"
					,
					columnsSelected,
					tableName, geometryTableName, IDN_COLUMN_NAME, IDN_COLUMN_NAME,
					"FTR_CDE", featureCode, 
					minx, maxx, minx, maxx, 
					miny, maxy, miny, maxy, 
					minx, maxx, miny, maxy);
			stmt = conn.prepareStatement(sql);
			logger.debug("::sql::" + sql);
			rs = stmt.executeQuery();
			
			GeometryJSON2 json = new GeometryJSON2();
			
			logger.debug("::row::" + rs.getRow());
			
			while (rs.next())
			{
				list.add(prepareRecord(json, rs));
			}
			
			/*response.setContentType("text/json");
			response.setCharacterEncoding("UTF-8");*/
			GeoJSONUtil2.encode(ret, response.getOutputStream());
		}
		catch (SocketException e) {
			logger.debug("::areaQuery::SocketException is occurred. but ignored!!!");
		}		
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (store != null)
			{
				store.closeSafe(rs);
				store.closeSafe(stmt);
				if(!conn.isClosed()) store.closeSafe(conn);
			}
		}
	}	
	
	private HashMap<String, Object> prepareRecord(GeometryJSON2 json, ResultSet rs) throws Exception 
	{
		HashMap<String, Object> row = new HashMap<String, Object>();
		row.put("type", "Feature");
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		row.put("properties", properties);
		
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		
		//String dbCharset = propertyService.getString("Global.shape.dbCharset", "UTF-8");
		
		for (int i=1; i<=columnCount; i++)
		{
			String typeName = meta.getColumnTypeName(i);
			
			String name = meta.getColumnName(i);
			Object value = rs.getObject(i);			
			if (value != null && typeName.endsWith("BLOB"))
			{
				value = rs.getBytes(i);
				
				Geometry g = new WKBReader().read((byte[])value);
				Object geometry = json.create(g);
				row.put("geometry", geometry);
			}
			else
			{
				
				/*CharBuffer cbuffer = CharBuffer.wrap((new String(rs.getBytes(i), "MS949")).toCharArray());
				Charset utf8charset = Charset.forName("UTF-8");
				ByteBuffer bbuffer = utf8charset.encode(cbuffer);

				//변환된 UTF-8 문자열
				value = new String(bbuffer.array());*/
				
				properties.put(name, value);

			}
		}
		
		return row;
	}

	@RequestMapping(value="/searchProxy.json.do")
	public void searchProxy(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append("http://map.vworld.kr/search.do");
		
		Set<Map.Entry<String, Object>> set = commandMap.entrySet();
		int count = 0;
		for (Map.Entry<String, Object> entry : set)
		{
			sb.append(count==0?"?":"&");
			sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
			count++;
		}
		
		URL url = new URL(sb.toString());
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		
		InputStream input = con.getInputStream();
		OutputStream output = response.getOutputStream();
		
		response.setContentType("text/json");
		IOUtils.copy(input, output);
	}
	
	@RequestMapping(value="/ListInterest.json.do", method=RequestMethod.POST)
	public void listInterest(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		ret.put("type", "Extent");
		ret.put("interests", list);
		
		JDBCDataStore store = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String userId = commandMap.get("userId").toString();
			
			// JDBC data store를 얻어온다
			store = this.getJDBCDataStore();
			
			conn = store.getDataSource().getConnection();
			
			String sql = String.format("SELECT INT_IDN, USR_IDN, INT_NAM, REG_YMD, MIN_X, MIN_Y, MAX_X, MAX_Y FROM WTT_INTR_ET WHERE USR_IDN = '%s'",
					userId
					);
			stmt = conn.prepareStatement(sql);
			logger.debug("::sql::" + sql);
			rs = stmt.executeQuery();
			
			GeometryJSON2 json = new GeometryJSON2();
			while(rs.next()) {
				list.add(prepareRecord(json, rs));
			}
			
			GeoJSONUtil2.encode(ret, response.getOutputStream());
		}
		catch (SocketException e) {
			logger.debug("::listInterest::SocketException is occurred. but ignored!!!");
		}		
		catch (Exception e)
		{
			logger.debug("exception in listInterest() : " + e.getMessage());
		}
		finally
		{
			if (store != null)
			{
				store.closeSafe(rs);
				store.closeSafe(stmt);
				if(!conn.isClosed()) store.closeSafe(conn);
			}
		}
	}	
	
	@RequestMapping(value="/AddInterest.json.do", method=RequestMethod.POST)
	public void newInterest(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		response.setContentType("text/json");
		PrintWriter writer = response.getWriter();
		String result = "{result:'OK'}";
		
		JDBCDataStore store = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			String userId = commandMap.get("userId").toString();
			String interstName = commandMap.get("interstName").toString();
			String minx = commandMap.get("minx").toString();
			String miny = commandMap.get("miny").toString();
			String maxx = commandMap.get("maxx").toString();
			String maxy = commandMap.get("maxy").toString();
			
			// JDBC data store를 얻어온다
			store = this.getJDBCDataStore();
			
			conn = store.getDataSource().getConnection();
			
			String sql = String.format("INSERT INTO WTT_INTR_ET(INT_IDN, USR_IDN, INT_NAM, REG_YMD, MIN_X, MIN_Y, MAX_X, MAX_Y) VALUES ("
					+ "SEQUENCE_WTT_INTR_ET.NEXTVAL, '%s', '%s', TO_CHAR(SYSDATE, 'YYYYMMDD'), %s, %s, %s, %s "
					+ ")",
					userId,
					interstName,
					minx,
					miny,
					maxx,
					maxy
					);
			stmt = conn.prepareStatement(sql);
			logger.debug("::sql::" + sql);
			int re = stmt.executeUpdate();
			if(re==1) {
				result = "{result:'OK'}";
			}
		}
		catch (Exception ex)
		{
			logger.debug("exception in newInterest() : " + ex.fillInStackTrace());
			result = "{result:'Fail', message: '" + ex.fillInStackTrace() + "'}";
			throw ex;
		} finally {
			if (store != null)
			{
				store.closeSafe(stmt);
				if(!conn.isClosed()) store.closeSafe(conn);
			}			
		}
		writer.print(result);
	}	
	
	@RequestMapping(value="/DelInterest.json.do", method=RequestMethod.POST)
	public void delInterest(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		response.setContentType("text/json");
		PrintWriter writer = response.getWriter();
		String result = "{result:'OK'}";
		
		JDBCDataStore store = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			String userId = commandMap.get("userId").toString();
			String intIdn = commandMap.get("intIdn").toString();
			
			// JDBC data store를 얻어온다
			store = this.getJDBCDataStore();
			
			conn = store.getDataSource().getConnection();
			
			String sql = String.format("DELETE WTT_INTR_ET WHERE USR_IDN = '%s' AND INT_IDN = %s",
					userId,
					intIdn
					);
			stmt = conn.prepareStatement(sql);
			logger.debug("::sql::" + sql);
			int re = stmt.executeUpdate();
			if(re==1) {
				result = "{result:'OK'}";
			}
		}
		catch (Exception ex)
		{
			logger.debug("exception in newInterest() : " + ex.fillInStackTrace());
			result = "{result:'Fail', message: '" + ex.fillInStackTrace() + "'}";
			throw ex;
		} finally {
			if (store != null)
			{
				store.closeSafe(stmt);
				if(!conn.isClosed()) store.closeSafe(conn);
			}			
		}
		writer.print(result);
	}		
	
	@RequestMapping(value="/ModGeometry.json.do", method=RequestMethod.POST)
	public void updateGeometry(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		String str = commandMap.get("featureCollection").toString();
		str = str.replace("&quot;", "\"");
		
		String tableName = commandMap.get("tableName").toString();
		if (this.dbType().equals("MySQL")) {
			tableName = tableName.toLowerCase();
		}
		String geometryTableName = this.getGeometryTableName(tableName);
		
		FeatureJSON json = new FeatureJSON();
		DefaultFeatureCollection fc = (DefaultFeatureCollection)json.readFeatureCollection(str);
		SimpleFeatureIterator e = fc.features();
		
		// JDBC data store를 얻어온다
		JDBCDataStore store = this.getJDBCDataStore();
		JDBCFeatureStore geomStore = (JDBCFeatureStore) store.getFeatureSource(geometryTableName);
		
		List<String> errList = new ArrayList<String>();
		int errCount = 0;
		
		Transaction transaction = new DefaultTransaction("delGeometry");
		geomStore.setTransaction(transaction);
		
		try {
			while (e.hasNext())
			{
				SimpleFeature f = e.next();
				/*
				 * 크기 회전하기시  multiple로 오는 경우가 있다.
				 * 이때 feature의 data가 존재하는 것만 대상으로 한다.
				 * 2015.03.06. By lispee
				 */
				Object idn_column_name = f.getAttribute(IDN_COLUMN_NAME);
				if(idn_column_name==null) continue;
				
				String pk = f.getAttribute(IDN_COLUMN_NAME).toString();
				Filter filter = CQL.toFilter(IDN_COLUMN_NAME + "=" + pk);
				Geometry g = (Geometry)f.getDefaultGeometry();
				logger.info(pk + ", " + g);
				
				try {
					geomStore.modifyFeatures("GEOMETRY", g, filter);

					BoundingBox bb = f.getBounds();
					geomStore.modifyFeatures("MINX", Math.floor(bb.getMinX()), filter);
					geomStore.modifyFeatures("MINY", Math.floor(bb.getMinY()), filter);
					geomStore.modifyFeatures("MAXX", Math.ceil(bb.getMaxX()), filter);
					geomStore.modifyFeatures("MAXY", Math.ceil(bb.getMaxY()), filter);
					g.setSRID(srid);
				}
				catch (Exception ee) {
					errCount++;
					errList.add(pk);
				} 
			}
			if(errCount>0) throw new Exception();
			else transaction.commit();
		} catch(Exception ee) {
			transaction.rollback();
		} finally {
			transaction.close();
		}
		
		response.setContentType("text/json");
		PrintWriter writer = response.getWriter();
		if (errCount > 0 ) {
			writer.print("{result:'Fail', FTR_IDN: [ ");
			writer.print(StringUtils.join(errList, ','));
			writer.print(" ]}");
		}
		else {
			writer.print("{result:'OK'}");
		}
	}
	
	@RequestMapping(value="/AddGeometry.json.do", method=RequestMethod.POST)
	public void newGeometry(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		response.setContentType("text/json");
		PrintWriter writer = response.getWriter();
		String result = "{result:'OK'}";
		
		try {
			String tableName = commandMap.get("tableName").toString();
			if (this.dbType().equals("MySQL")) {
				tableName = tableName.toLowerCase();
			}
			String geometryTableName = this.getGeometryTableName(tableName);
			
			// JDBC data store를 얻어온다
			JDBCDataStore store = this.getJDBCDataStore();
			
			/*
			 * AddGeometry시에는 굳이 검증할 필요 없음.
			 */
			verifyTables(store, tableName);
			
			JDBCFeatureStore geomStore = (JDBCFeatureStore) store.getFeatureSource(geometryTableName);
	
			// add attributes
			String str = commandMap.get("featureCollection").toString();
			str = str.replace("&quot;", "\"");
			FeatureJSON json = new FeatureJSON();			
			DefaultFeatureCollection fc = (DefaultFeatureCollection)json.readFeatureCollection(str);

			// add geometries
			SimpleFeatureType featureType = geomStore.getSchema();
			SimpleFeatureBuilder fb = new SimpleFeatureBuilder(featureType);
			
			List<SimpleFeature> geometries = new ArrayList<SimpleFeature>();

			SimpleFeatureIterator it = fc.features();
			while(it.hasNext()) {
				SimpleFeature f = it.next();
				String primaryKey = f.getAttribute(IDN_COLUMN_NAME).toString();
				//Filter filter = CQL.toFilter(IDN_COLUMN_NAME + "=" + primaryKey);
				Geometry g = (Geometry)f.getDefaultGeometry();
				
				if(g instanceof MultiLineString) {
					g = JtsGeometryUtil.convertMultiLineString2LineString((MultiLineString)g);
				}
				
				logger.info(primaryKey + ", " + g);
	
				if (g != null)
				{
					BoundingBox bb = f.getBounds();
					fb.set("GEOMETRY", g);
					fb.set("MINX", Math.floor(bb.getMinX()));
					fb.set("MINY", Math.floor(bb.getMinY()));
					fb.set("MAXX", Math.ceil(bb.getMaxX()));
					fb.set("MAXY", Math.ceil(bb.getMaxY()));
					g.setSRID(srid);
				}
				else {
					logger.debug("Geometry == null");
				}
				
				SimpleFeature newFeature = fb.buildFeature(primaryKey);
				newFeature.getUserData().put(Hints.USE_PROVIDED_FID, true);
				newFeature.getUserData().put(Hints.PROVIDED_FID, primaryKey);
				geometries.add(newFeature);
			}
			
			Transaction transaction = new DefaultTransaction("newGeometry");
			geomStore.setTransaction(transaction);
			
			try {
				geomStore.addFeatures(geometries);
		        transaction.commit();
		        logger.info("--------------------- successfully added -------------------");
		    }
		    catch( Exception eek){
		        transaction.rollback();
				throw eek;
		    } finally {
		    	transaction.close();
		    }
		}
		catch (Exception ex)
		{
			logger.debug("exception in newGeometry() : " + ex.fillInStackTrace());
			result = "{result:'Fail', message: '" + ex.fillInStackTrace() + "'}";
			throw ex;
		}
		writer.print(result);
	}	


	@RequestMapping(value="/DelGeometry.json.do", method=RequestMethod.POST)
	public void delGeometry(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		String str = commandMap.get("featureCollection").toString();
		str = str.replace("&quot;", "\"");
		
		String tableName = commandMap.get("tableName").toString();
		if (this.dbType().equals("MySQL")) {
			tableName = tableName.toLowerCase();
		}
		String geometryTableName = this.getGeometryTableName(tableName);
		
		FeatureJSON json = new FeatureJSON();
		DefaultFeatureCollection fc = (DefaultFeatureCollection)json.readFeatureCollection(str);
		SimpleFeatureIterator it = fc.features();
		
		// JDBC data store를 얻어온다
		JDBCDataStore store = this.getJDBCDataStore();
		JDBCFeatureStore geomStore = (JDBCFeatureStore) store.getFeatureSource(geometryTableName);
		
		//SimpleFeatureType schema = geomStore.getSchema();
		List<String> errList = new ArrayList<String>();
		int errCount = 0;
		
		Transaction transaction = new DefaultTransaction("delGeometry");
		geomStore.setTransaction(transaction);
		
		try {
			while (it.hasNext())
			{
				SimpleFeature f = it.next();
				String pk = f.getAttribute(IDN_COLUMN_NAME).toString();
				Filter filter = CQL.toFilter(IDN_COLUMN_NAME + "=" + pk);
				Geometry g = (Geometry)f.getDefaultGeometry();
				logger.info(pk + ", " + g);
				
				try {
					geomStore.removeFeatures(filter);
				}
				catch (Exception ee) {
					errCount++;
					errList.add(pk);
					
				}
			}
			if(errCount>0) throw new Exception();
			else {
				transaction.commit();
				logger.info("--------------------- successfully deleted -------------------");
			}
		} catch (Exception e) {
			transaction.rollback();
	    } finally{
	    	transaction.close();
	    }
		
		response.setContentType("text/json");
		PrintWriter writer = response.getWriter();
		if (errCount > 0 ) {
			writer.print("{result:'Fail', FTR_IDN: [ ");
			writer.print(StringUtils.join(errList, ','));
			writer.print(" ]}");
		}
		else {
			writer.print("{result:'OK'}");
		}
	}

	private WKTReader wktReader = new WKTReader();

	public Geometry readWKT(String geoStr) 
	{
		Geometry geo = null;
		try {
			geo = wktReader.read(geoStr); 
		}
		catch(ParseException pe) {
			logger.debug("exception in readWKT() : " + pe.getMessage());
			geo = null;
		}
		
		return geo;
	}
	

	@RequestMapping(value="/SplitGeometry.json.do", method=RequestMethod.POST)
	public void splitGeometry(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		
		try {
			String geoString = commandMap.get("geo").toString();
			String cutString = commandMap.get("cut").toString();
			
			/*
			String tableName = commandMap.get("tableName").toString();
			if (this.dbType().equals("MySQL")) {
				tableName = tableName.toLowerCase();
			}
			String geometryTableName = this.getGeometryTableName(tableName);
			*/
			
			GeometryJSON2 json = new GeometryJSON2();
			List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			ret.put("type", "GeometryCollection");
			ret.put("geometries", list);
			
			Geometry geoObj = readWKT(geoString);
			
			if (geoObj instanceof Point  || geoObj instanceof MultiPoint){
				throw new Exception("포인트 객체는 분할하기가 지원되지 않습니다.");
			} else if (geoObj instanceof LineString || geoObj instanceof MultiLineString) {
				// split line
				if(geoObj instanceof MultiLineString) {
					geoObj = JtsGeometryUtil.convertMultiLineString2LineString((MultiLineString)geoObj);
				}
				
				Geometry cutPoint = geoObj.intersection(readWKT(cutString));
				Coordinate coordinate = cutPoint.getCoordinate(); 
				List<LineString> lines = JtsGeometryUtil.splitLineString((LineString)geoObj, coordinate);
				
				for (Iterator<LineString> it = lines.iterator(); it.hasNext();) {
					HashMap<String, Object> row = new HashMap<String, Object>();
					Geometry line = (Geometry)it.next();
					Map<String, Object> geometries = json.create(line);
					
					for( String key : geometries.keySet() ){
			            row.put(key,  geometries.get(key));
			        }
					list.add(row);
					
				}
			}
			else if (geoObj instanceof Polygon || geoObj instanceof MultiPolygon) {
				// split polygon
				LineMerger lineMerger = new LineMerger();
				lineMerger.add(geoObj);
				lineMerger.add(readWKT(cutString));
				Collection mergedLines = lineMerger.getMergedLineStrings();
				
				Geometry unioned = UnaryUnionOp.union(mergedLines);
				
				Polygonizer polygonizer = new Polygonizer();
				polygonizer.add(unioned);
				Collection polys = polygonizer.getPolygons();
		
				for (Iterator it = polys.iterator(); it.hasNext();) {
					HashMap<String, Object> row = new HashMap<String, Object>(); 
					Geometry poly = (Geometry) it.next();
					Map<String, Object> geometries = json.create(poly);
					
					for( String key : geometries.keySet() ){
			            row.put(key,  geometries.get(key));
			        }
					list.add(row);
				}
			}
		}
		catch (Exception e) {
			ret.put("error", e.getMessage());
			logger.debug("exception in splitGeometry() : " + e.getMessage() + ", but irgnored!!!");
			//throw e;
		} finally {
			response.setContentType("text/json");
			GeoJSONUtil2.encode(ret, response.getOutputStream());
		}
	}

	@RequestMapping(value="/MergeGeometry.json.do", method=RequestMethod.POST)
	public void mergeGeometry(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			String geoString = commandMap.get("geo").toString();
			
			/*
			String tableName = commandMap.get("tableName").toString();
			if (this.dbType().equals("MySQL")) {
				tableName = tableName.toLowerCase();
			}
			String geometryTableName = this.getGeometryTableName(tableName);
			*/
			
			GeometryJSON2 json = new GeometryJSON2();
			List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			ret.put("type", "GeometryCollection");
			ret.put("geometries", list);

			GeometryCollection geoObj = (GeometryCollection)readWKT(geoString);
			// merge polygon
			/*
			 *  Point 형의 GeometryCollection은 허용안함.
			 */
			int N = geoObj.getNumGeometries();
			if(N>0) {
				Geometry prevGeoObjInner = null;
				for(int i=0;i<N;i++) {
					Geometry geoObjInner = geoObj.getGeometryN(i);
					if (geoObjInner instanceof Point) {
						throw new Exception("포인트 객체는 병합하기가 지원되지 않습니다.");
					} else if (geoObjInner instanceof LineString || geoObjInner instanceof MultiLineString) {
						if(N>2) {
							throw new Exception("2개 이상의 라인 객체는 병합하기가 지원되지 않습니다.");
						}
						if(prevGeoObjInner!=null && prevGeoObjInner.intersects(geoObjInner) && !prevGeoObjInner.touches(geoObjInner)) {
							throw new Exception("서로 교차하는 두 라인 객체는 병합하기가 지원되지 않습니다.");
						}
					} else if (geoObjInner instanceof Polygon || geoObjInner instanceof MultiPolygon) {
						if(prevGeoObjInner!=null && prevGeoObjInner.disjoint(geoObjInner)) {
							throw new Exception("공통된 영역이 없는 폴리곤 객체들은 병합하기가 지원되지 않습니다.");
						}
					}
					prevGeoObjInner = geoObjInner;
				}
			}
			
			Geometry union = geoObj.union();
			
			/*
			 *  MultiLineString 형의 원 객체는 LineString형으로 취해줌.
			 */
			if(union instanceof MultiLineString) {
				union = JtsGeometryUtil.convertMultiLineString2LineString((MultiLineString)union);
			}

			HashMap<String, Object> row = new HashMap<String, Object>();
			Map<String, Object> geometries = json.create(union);
				
			for( String key : geometries.keySet() ){
	            row.put(key,  geometries.get(key));
	        }
			list.add(row);
		}
		catch (Exception e) {
			ret.put("error", e.getMessage());
			logger.debug("exception in mergeGeometry() : " + e.getMessage() + ", but irgnored!!!");
		} finally {
			response.setContentType("text/json");
			GeoJSONUtil2.encode(ret, response.getOutputStream());
		}
	}

	
	@RequestMapping(value="/FindFeature.json.do", method=RequestMethod.POST)
	public void findFeature(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		Map<String, Object> ret = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		ret.put("type", "FeatureCollection");
		ret.put("features", list);
		
		JDBCDataStore store = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try
		{
			store = this.getJDBCDataStore();
			
			String tableName = commandMap.get("table").toString();
			String featureIndex = commandMap.get("index").toString();
			String featureCode = commandMap.get("code").toString(); 
			String geometryTableName = this.getGeometryTableName(tableName);
			String columnsSelected = this.getColumnsSelected(tableName + "." + featureCode);
			
			//conn = store.getConnection(Transaction.AUTO_COMMIT);
			
			conn = store.getDataSource().getConnection();
			
			String sql = String.format(
				"select %s from %s a, %s b where a.%s = b.%s and a.%s = '%s' and a.%s = %s ",
				columnsSelected,
				tableName, geometryTableName, IDN_COLUMN_NAME, IDN_COLUMN_NAME,
				"FTR_CDE", featureCode,
				"FTR_IDN", featureIndex
			);
			
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			GeometryJSON2 json = new GeometryJSON2();
			while (rs.next())
			{
				list.add(prepareRecord(json, rs));
			}
			
			/*response.setContentType("text/json");
			response.setCharacterEncoding("UTF-8");*/
			GeoJSONUtil2.encode(ret, response.getOutputStream());
		}
		catch (SocketException e) {
			logger.debug("::findFeature::SocketException is occurred. but ignored!!!");
		}		
		catch (Exception e)
		{
			logger.debug("exception in findFeature() : " + e.getMessage());
		}
		finally
		{
			if (store != null)
			{
				store.closeSafe(rs);
				store.closeSafe(stmt);
				if(!conn.isClosed()) store.closeSafe(conn);
			}
		}
	}	

	@RequestMapping(value="/ArcizeGeometry.json.do", method=RequestMethod.POST)
	public void arcizeGeometry(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		double radius = 10.0;
		try {
			Map<String, Object> ret = new HashMap<String, Object>();
			
			Geometry arctizedGeometry = null;
			Map<String, Object> nthGeometries = null;

			String geoString = commandMap.get("geo").toString();
			String cutString = commandMap.get("cut").toString();
			String radiusString = commandMap.get("radius").toString();
			radius = Double.parseDouble(radiusString.replaceAll(" ", "."));
			
			GeometryJSON2 json = new GeometryJSON2();
			List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			ret.put("type", "GeometryCollection");
			ret.put("geometries", list);
			
			Geometry geoObj = readWKT(geoString);
			Geometry cutObj = (LineString)readWKT(cutString);
			
			GeometryFactory factory = geoObj.getFactory();
			
			if ((geoObj instanceof LineString || geoObj instanceof MultiLineString) && cutObj instanceof LineString) {
				// point <-- intersection of geo & cut
				Geometry centerPoints = geoObj.intersection(cutObj);
				if(centerPoints==null) {
					arctizedGeometry = cutObj;
				} else {
					Coordinate center = null;
					if(centerPoints instanceof MultiPoint) {
						Vector<Geometry> vecGeom = new Vector();
						Geometry tmpCutObj = cutObj;
						int idx = 0;
						for(int i=0;i<centerPoints.getNumGeometries();i++) {
							if(i==idx) {
								center = centerPoints.getGeometryN(idx).getCoordinate();
								nthGeometries = getNthGeometry(geoObj, tmpCutObj, center, radius);
								
								Geometry firstLine = (Geometry)nthGeometries.get("firstLine");
								Geometry semiCircle = (Geometry)nthGeometries.get("semiCircle");
								Geometry secondLine = (Geometry)nthGeometries.get("secondLine");
								Boolean plus1center = (Boolean)nthGeometries.get("plus1center");
								
								if(firstLine!=null) vecGeom.add(firstLine);
								if(semiCircle!=null) vecGeom.add(semiCircle);
								if(secondLine!=null) {
									if(i<centerPoints.getNumGeometries()-1) {
										tmpCutObj = secondLine;
										if(plus1center!=null&&plus1center) {
											idx = idx + 2;
										} else {
											idx++;
										}
									} else {
										vecGeom.add(secondLine);
									}
								} 
							}
						}
						
						Geometry prevGeom = null;
						for(int i=0;i<vecGeom.size();i++){
							Geometry geom = (Geometry)vecGeom.get(i);
							if(prevGeom==null) prevGeom = geom;
							else prevGeom = prevGeom.union(geom);
						}
						
						arctizedGeometry = prevGeom;
						
						if(arctizedGeometry instanceof MultiLineString) {
							arctizedGeometry = JtsGeometryUtil.convertMultiLineString2LineString((MultiLineString)arctizedGeometry);
						}
					} else {
						center = centerPoints.getCoordinate();
						nthGeometries = getNthGeometry(geoObj, cutObj, center, radius);
						
						Geometry firstLine = (Geometry)nthGeometries.get("firstLine");
						Geometry semiCircle = (Geometry)nthGeometries.get("semiCircle");
						Geometry secondLine = (Geometry)nthGeometries.get("secondLine");
						
						if(firstLine!=null) arctizedGeometry = firstLine;
						if(semiCircle!=null) arctizedGeometry = arctizedGeometry.union(semiCircle);
						if(secondLine!=null) arctizedGeometry = arctizedGeometry.union(secondLine);
						
						if(arctizedGeometry instanceof MultiLineString) {
							arctizedGeometry = JtsGeometryUtil.convertMultiLineString2LineString((MultiLineString)arctizedGeometry);
						}
					}
				}

				HashMap<String, Object> row = new HashMap<String, Object>();
				Map<String, Object> geometries = json.create(arctizedGeometry);
					
				for (String key : geometries.keySet()) {
		            row.put(key,  geometries.get(key));
		        }
				list.add(row);
			}
			else {
				throw new Exception("Unsupported Geomety Type");
			}
			
			response.setContentType("text/json");
			GeoJSONUtil2.encode(ret, response.getWriter());
		}
		catch (Exception e) {
			logger.debug("exception in arcizeGeometry() : " + e.fillInStackTrace());
			throw e;
		}
	}
	
	private Map<String, Object> getNthGeometry(Geometry geoObj, Geometry cutObj, Coordinate center, double radius) throws Exception {
		
		Map<String, Object> retObjects = new HashMap<String, Object>();
		
		Geometry nthGeometry = null;
		
		try{
			Geometry bufferCircle = JtsGeometryUtil.createCircle(center, radius);
			
			if(cutObj.within(bufferCircle)) {
				retObjects.put("firstLine", cutObj);
			} else {
				Geometry intersectionCut = cutObj.intersection(bufferCircle); // 리턴되는 순서 보장 안됨
				if(intersectionCut==null) {
					retObjects.put("firstLine", cutObj);
				}
				else {
					Coordinate[] intersectionCutCoords = intersectionCut.getCoordinates();
					
				    if(!(intersectionCut.crosses(geoObj)||intersectionCut.intersects(geoObj))) {
				    	retObjects.put("firstLine", cutObj);
				    }
				    else {
				    	Geometry intersectionGeo = geoObj.intersection(bufferCircle);
				    	
				    	Coordinate[] intersectionGeoCoords = intersectionGeo.getCoordinates();
				    	
				    	Geometry geoCircle0 = JtsGeometryUtil.createArc(center, intersectionGeoCoords[0], intersectionGeoCoords[intersectionGeoCoords.length-1], radius, true);
				    	Geometry geoCircle1 = JtsGeometryUtil.createArc(center, intersectionGeoCoords[intersectionGeoCoords.length-1], intersectionGeoCoords[0], radius, true);

				    	boolean crossesGeoCircle0 = cutObj.crosses(geoCircle0);
				    	boolean crossesGeoCircle1 = cutObj.crosses(geoCircle1);
				    	
				    	if(!crossesGeoCircle0||!crossesGeoCircle1) {
							if(crossesGeoCircle0) {
								Geometry nthIntersection = cutObj.intersection(geoCircle0);
								Coordinate[] coords = nthIntersection.getCoordinates();
								
								Coordinate splitCoord = coords[coords.length-1];
								List<LineString> lines = null;
								lines = JtsGeometryUtil.splitLineString((LineString)cutObj, splitCoord);
								retObjects.put("firstLine", lines.get(0));
								if(lines.size()==2) {
									retObjects.put("secondLine", lines.get(1));
									retObjects.put("plus1center", true);
								}
							} else if(crossesGeoCircle1) {
								Geometry nthIntersection = cutObj.intersection(geoCircle1);
								Coordinate[] coords = nthIntersection.getCoordinates();
								
								Coordinate splitCoord = coords[coords.length-1];
								List<LineString> lines = null;
								lines = JtsGeometryUtil.splitLineString((LineString)cutObj, splitCoord);
								retObjects.put("firstLine", lines.get(0));
								if(lines.size()==2) {
									retObjects.put("secondLine", lines.get(1));
									retObjects.put("plus1center", true);
								}
							} else {
								retObjects.put("firstLine", cutObj);
							}
						} else {
							boolean endPointCross = JtsGeometryUtil.createPoint(center).touches(geoObj);
							
							if(endPointCross){
								List<LineString> lines = null;
								lines = JtsGeometryUtil.splitLineString((LineString)cutObj, center);
								retObjects.put("firstLine", lines.get(0));
								if(lines.size()==2) {
									retObjects.put("secondLine", lines.get(1));
								}
							} else {
								Geometry geoCircle0Intersection = cutObj.intersection(geoCircle0);
								Geometry geoCircle1Intersection = cutObj.intersection(geoCircle1);
								
						    	Geometry firstPtr = null;
						    	Geometry secondPtr = null;
					    		boolean firstPtrWithinGeoCircle0 = false;
					    		boolean firstPtrWithinGeoCircle1 = false;
					    		
						    	for(Coordinate c:intersectionCutCoords) {
						    		Geometry p = JtsGeometryUtil.createPoint(c);
						    		//buffer값은 특히 민감한 부분 => 0.1 or 0.01
						    		boolean pWithinGeoCircle0 = p.within(geoCircle0Intersection.buffer(0.1));
						    		boolean pWithinGeoCircle1 = p.within(geoCircle1Intersection.buffer(0.1));
						    		
						    		if(pWithinGeoCircle0||pWithinGeoCircle1) {
						    			if(firstPtr == null) {
						    				if(pWithinGeoCircle0) {
						    					firstPtr = geoCircle0Intersection;
						    					firstPtrWithinGeoCircle0 = true;
						    				}
						    				else if(pWithinGeoCircle1) {
						    					firstPtr = geoCircle1Intersection;
						    					firstPtrWithinGeoCircle1 = true;
						    				}
						    				//firstPtr = p;
						    				//firstPtrWithinGeoCircle0 = firstPtr.within(geoCircle0Intersection.buffer(0.1));
						    				//firstPtrWithinGeoCircle1 = firstPtr.within(geoCircle1Intersection.buffer(0.1));
						    			}
						    			else{
						    				if((firstPtrWithinGeoCircle0&&pWithinGeoCircle1)
						    				||(firstPtrWithinGeoCircle1&&pWithinGeoCircle0)) {
						    					if(pWithinGeoCircle0) {
						    						secondPtr = geoCircle0Intersection;
							    				}
							    				else if(pWithinGeoCircle1) {
							    					secondPtr = geoCircle1Intersection;
							    				}
						    					//secondPtr = p;
						    					break;
						    				}
						    			}
						    		}
						    	}
						    	if(firstPtr==null||secondPtr==null) {
						    		retObjects.put("firstLine", cutObj);
						    	} else {
									Geometry semiCircle = JtsGeometryUtil.createArc(center, firstPtr.getCoordinate(), secondPtr.getCoordinate(), radius, true);
									retObjects.put("semiCircle", semiCircle);
									
									// split cut line to two lines  <-- twoPoints
									Geometry startPtr = ((LineString)cutObj).getStartPoint();
									Geometry endPtr = ((LineString)cutObj).getEndPoint();
									Geometry firstLine = null;
									Geometry secondLine = null;
									
									MultiLineString lines = null;
									Geometry tmpLine = null;
									MultiLineString tmpLines = null;
									
									//lines = JtsGeometryUtil.splitLineString((LineString)cutObj, firstPtr.getCoordinate());
									if(firstPtrWithinGeoCircle0) lines = (MultiLineString)(((LineString)cutObj).difference(geoCircle0));
									else if(firstPtrWithinGeoCircle1) lines = (MultiLineString)(((LineString)cutObj).difference(geoCircle1));

									for(int i=0;i<lines.getNumGeometries();i++) {
										LineString l0 = (LineString)lines.getGeometryN(i);
										if(l0.touches(startPtr)) {
											firstLine = l0;
											retObjects.put("firstLine", firstLine);
										} else {
											tmpLine = l0;
											//tmpLines = JtsGeometryUtil.splitLineString((LineString)cutObj, secondPtr.getCoordinate());
											if(firstPtrWithinGeoCircle0) tmpLines = (MultiLineString)(((LineString)cutObj).difference(geoCircle1));
											else if(firstPtrWithinGeoCircle1) tmpLines = (MultiLineString)(((LineString)cutObj).difference(geoCircle0));
											
											for(int j=0;j<tmpLines.getNumGeometries();j++) {
												LineString l1 = (LineString)tmpLines.getGeometryN(j);
												if(l1.touches(endPtr)) {
													secondLine = l1;
													retObjects.put("secondLine", secondLine);
												} 
											}	
										}
									}
					    		}
							}
						}
				    }		
				}
			}
		} catch (Exception e) {
			logger.debug("exception in getNthGeometry() : " + e.fillInStackTrace());
			throw e;
		}
		
		return retObjects;
	}
	
	@RequestMapping(value="/FindIntersectionsGeometry.json.do", method=RequestMethod.POST)
	public void findIntersectionsGeometry(Map<String, Object> commandMap, HttpServletResponse response) throws Exception
	{
		Map<String, Object> retObjects = new HashMap<String, Object>();

		
		List<Map<String, Object>> intersections = new ArrayList<Map<String, Object>>();
		
		try {
			String geoString = commandMap.get("geo").toString().replace("&quot;", "\"");
			String cutString = commandMap.get("cut").toString().replace("&quot;", "\"");
			
			DefaultFeatureCollection fc = (DefaultFeatureCollection)new FeatureJSON().readFeatureCollection(geoString);
			
			SimpleFeature cutFeature = new FeatureJSON().readFeature(cutString);
			LineString cutLineString = (LineString)cutFeature.getDefaultGeometry();
			Point cutStartPoint = cutLineString.getStartPoint();
			TreeMap<Double, SimpleFeature> sortedFeatures = new TreeMap<Double, SimpleFeature>();

			/*
			Map<String, Double> retStart = new HashMap<String, Double>();
			retStart.put("x", cutStartPoint.getX());
			retStart.put("y",  cutStartPoint.getY());
			retObject.put("start", retStart);
			*/

			SimpleFeatureIterator it = fc.features();
			while (it.hasNext()) {
				SimpleFeature f = it.next();
				try {
					Geometry intersectionPoint = ((Geometry)f.getDefaultGeometry()).intersection(cutLineString);
					Coordinate[] intersectionCoordinates = intersectionPoint.getCoordinates();
					
					f.setAttribute("intersectionX", intersectionCoordinates[0].x);
					f.setAttribute("intersectionY", intersectionCoordinates[0].y);
					double dist = cutStartPoint.distance(intersectionPoint);
					f.setAttribute("distance", dist);
					sortedFeatures.put(new Double(dist), f);
				}
				catch(TopologyException te) {
					continue;
				}
				catch(IllegalArgumentException iae) {
					continue;
				}
				catch(Exception e) {
					continue;
				}
			}

			for (Double key : sortedFeatures.keySet()) {
				SimpleFeature f = sortedFeatures.get(key);
				Map<String, Object> intersection = new HashMap<String, Object>();
				Map<String, Double> pointMap = new HashMap<String, Double>(); 
				
				pointMap.put("x", (Double)f.getAttribute("intersectionX"));
				pointMap.put("y", (Double)f.getAttribute("intersectionY"));
				intersection.put("point", pointMap);
				intersection.put("FTR_CDE", (String)f.getAttribute("FTR_CDE"));
				intersection.put("FTR_IDN", (Long)f.getAttribute("FTR_IDN"));
				
	            intersections.add(intersection);
	        }
			
			retObjects.put("intersections", intersections);
			
		}
		catch (Exception e) {
			logger.debug("exception in findIntersectionsGeometry() : " + e.getMessage());
			throw e;
		}		

		response.setContentType("text/json");
		GeoJSONUtil2.encode(retObjects, response.getOutputStream());
	}
}




