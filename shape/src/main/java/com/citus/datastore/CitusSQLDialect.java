package com.citus.datastore;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.geotools.data.DataStoreFactorySpi;
import org.geotools.factory.Hints;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.SQLDialect;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;

public class CitusSQLDialect extends SQLDialect
{
    /**
     * mysql spatial types
     */
    protected Integer POINT = new Integer(2001);
    protected Integer LINESTRING = new Integer(2002);
    protected Integer POLYGON = new Integer(2003);
    protected Integer MULTIPOINT = new Integer(2004);
    protected Integer MULTILINESTRING = new Integer(2005);
    protected Integer MULTIPOLYGON = new Integer(2006);
    protected Integer GEOMETRY = new Integer(2007);

    /**
     * the storage engine to use when creating tables, one of MyISAM, InnoDB
     */
    protected String storageEngine;
    
    public CitusSQLDialect(JDBCDataStore dataStore) {
        super(dataStore);
    }

    public void setStorageEngine(String storageEngine) {
        this.storageEngine = storageEngine;
    }
    
    public String getStorageEngine() {
        return storageEngine;
    }
    
    @Override
    public boolean includeTable(String schemaName, String tableName, Connection cx)
            throws SQLException {
        if ("geometry_columns".equalsIgnoreCase(tableName)) {
            return false;
        }
        return super.includeTable(schemaName, tableName, cx);
    }
    
    public String getNameEscape() {
        return "";
    }

    public String getGeometryTypeName(Integer type) {
        if (POINT.equals(type)) {
            return "POINT";
        }

        if (MULTIPOINT.equals(type)) {
            return "MULTIPOINT";
        }

        if (LINESTRING.equals(type)) {
            return "LINESTRING";
        }

        if (MULTILINESTRING.equals(type)) {
            return "MULTILINESTRING";
        }

        if (POLYGON.equals(type)) {
            return "POLYGON";
        }

        if (MULTIPOLYGON.equals(type)) {
            return "MULTIPOLYGON";
        }

        if (GEOMETRY.equals(type)) {
            return "GEOMETRY";
        }

        return super.getGeometryTypeName(type);
    }

    public Integer getGeometrySRID(String schemaName, String tableName, String columnName,
        Connection cx) throws SQLException 
    {
    	Integer srid;
		
    	DataStoreFactorySpi dsfs = this.dataStore.getDataStoreFactory();
    	
		if(dsfs instanceof CitusJndiDataStoreFactory) {
			CitusJndiDataStoreFactory fac = (CitusJndiDataStoreFactory) dsfs;
			srid = fac.getSRID();
		} else {
			CitusDataStoreFactory fac = (CitusDataStoreFactory) dsfs;
			srid = fac.getSRID();
		}
    	
    	return srid;
    }

    @Override
    public void encodeGeometryColumn(GeometryDescriptor gatt, String prefix,
            int srid, Hints hints, StringBuffer sql) {
        //sql.append("asWKB(");
        encodeColumnName(prefix, gatt.getLocalName(), sql);
        //sql.append(")");
    }

    public void encodeGeometryEnvelope(String tableName, String geometryColumn, StringBuffer sql) 
    {
        //sql.append("asWKB(");
        //sql.append("envelope(");
        encodeColumnName(null, geometryColumn, sql);
        //sql.append("))");
    }

    public Envelope decodeGeometryEnvelope(ResultSet rs, int column,
                Connection cx) throws SQLException, IOException {
        //String wkb = rs.getString( column );
        byte[] wkb = rs.getBytes(column);

        try 
        {
            //Polygon polygon = (Polygon) new WKTReader().read(wkb);
            Polygon polygon = (Polygon) new WKBReader().read(wkb);

            return polygon.getEnvelopeInternal();
        } 
        catch (ParseException e) 
        {
            String msg = "Error decoding wkb for envelope";
            throw (IOException) new IOException(msg).initCause(e);
        }
    }

    public Geometry decodeGeometryValue(GeometryDescriptor descriptor, ResultSet rs, String name,
        GeometryFactory factory, Connection cx ) throws IOException, SQLException {
        byte[] bytes = rs.getBytes(name);
        if ( bytes == null ) 
        {
            return null;
        }
        try 
        {
            return new WKBReader(factory).read(bytes);
        } 
        catch (ParseException e) 
        {
            String msg = "Error decoding wkb";
            throw (IOException) new IOException(msg).initCause(e);
        }
    }

    public void registerClassToSqlMappings(Map<Class<?>, Integer> mappings) {
        super.registerClassToSqlMappings(mappings);

        mappings.put(Point.class, POINT);
        mappings.put(LineString.class, LINESTRING);
        mappings.put(Polygon.class, POLYGON);
        mappings.put(MultiPoint.class, MULTIPOINT);
        mappings.put(MultiLineString.class, MULTILINESTRING);
        mappings.put(MultiPolygon.class, MULTIPOLYGON);
        mappings.put(Geometry.class, GEOMETRY);
    }

    public void registerSqlTypeToClassMappings(Map<Integer, Class<?>> mappings) {
        super.registerSqlTypeToClassMappings(mappings);

        mappings.put(POINT, Point.class);
        mappings.put(LINESTRING, LineString.class);
        mappings.put(POLYGON, Polygon.class);
        mappings.put(MULTIPOINT, MultiPoint.class);
        mappings.put(MULTILINESTRING, MultiLineString.class);
        mappings.put(MULTIPOLYGON, MultiPolygon.class);
        mappings.put(GEOMETRY, Geometry.class);
    }

    public void registerSqlTypeNameToClassMappings(Map<String, Class<?>> mappings) {
        super.registerSqlTypeNameToClassMappings(mappings);

        mappings.put("POINT", Point.class);
        mappings.put("LINESTRING", LineString.class);
        mappings.put("POLYGON", Polygon.class);
        mappings.put("MULTIPOINT", MultiPoint.class);
        mappings.put("MULTILINESTRING", MultiLineString.class);
        mappings.put("MULTIPOLYGON", MultiPolygon.class);
        mappings.put("GEOMETRY", Geometry.class);
        mappings.put("GEOMETRYCOLLETION", GeometryCollection.class);
    }

    @Override
    public void registerSqlTypeToSqlTypeNameOverrides(
            Map<Integer, String> overrides) {
        overrides.put( Types.BOOLEAN, "BOOL");
    }
    
    public void encodePostCreateTable(String tableName, StringBuffer sql) 
    {
    }
    
    @Override
    public void encodePostColumnCreateTable(AttributeDescriptor att, StringBuffer sql) {
        //make geometry columns non null in order to be able to index them
        if (att instanceof GeometryDescriptor && !att.isNillable()) {
            sql.append( " NOT NULL");
        }
    }
    
    @Override
    public void postCreateTable(String schemaName, SimpleFeatureType featureType, Connection cx)
            throws SQLException, IOException 
    {
    }

    public void encodePrimaryKey(String column, StringBuffer sql) 
    {
        encodeColumnName(null, column, sql);
    }

    @Override
    public boolean lookupGeneratedValuesPostInsert() 
    {
        return true;
    }
    
    @Override
    public Object getLastAutoGeneratedValue(String schemaName, String tableName, String columnName,
            Connection cx) throws SQLException 
    {
        return null;
    }

    @Override
    public boolean isLimitOffsetSupported() {
        return true;
    }
    
    @Override
    public void applyLimitOffset(StringBuffer sql, int limit, int offset) 
    {
    }

	@Override
	public Geometry decodeGeometryValue(GeometryDescriptor arg0, ResultSet arg1, String arg2, GeometryFactory arg3,
			Connection arg4, Hints arg5) throws IOException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
