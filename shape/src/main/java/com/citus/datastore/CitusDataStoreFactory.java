package com.citus.datastore;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.geotools.data.DataStore;
import org.geotools.data.DataAccessFactory.Param;
import org.geotools.data.mysql.MySQLDialectBasic;
import org.geotools.data.mysql.MySQLDialectPrepared;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.jdbc.SQLDialect;

import org.locationtech.jts.geom.Geometry;

public class CitusDataStoreFactory extends JDBCDataStoreFactory  
{
    /** parameter for database type */
    public static final Param DRIVER = new Param("driver", String.class, "JDBC driver class name", true, "com.mysql.jdbc.Driver");
    public static final Param SRID   = new Param("srid",  Integer.class, "SRID", true, 900913);
    public static final Param URL    = new Param("url",  String.class, "JDBC connect string", true, "jdbc:mysql://host:3306/schema");
    protected String driverClassName;
    protected Integer srid = (Integer)SRID.sample;
    static JDBCDataStore defaultStore;
    
    public CitusDataStoreFactory()
    {
    	
    }

    public static synchronized JDBCDataStore getDefaultStore(Map params) throws IOException
    {
    	if (defaultStore == null)
    	{
    		defaultStore = new CitusDataStoreFactory().createDataStore(params);
    		
    		// map blob type to Geometry
    		Map<String, Class<?>> map = defaultStore.getSqlTypeNameToClassMappings();
    		map.put("BLOB", Geometry.class);
    	}
    	
    	return defaultStore;
    }
    
    @Override
    public BasicDataSource createDataSource(Map params) throws IOException 
    {
        this.driverClassName = DRIVER.lookUp(params).toString();
        
    	return super.createDataSource(params);
    }
    
    protected SQLDialect createSQLDialect(JDBCDataStore dataStore) 
    {
        return new CitusDialectPrepared(dataStore);
    }

    public String getDisplayName() {
        return "Citus";
    }
    
    
    protected String getDriverClassName() {
        return this.driverClassName;
    }

    protected String getJDBCUrl(Map params) throws IOException 
    {
    	return URL.lookUp(params).toString();
    }

    public String getDescription() {
        return "Citus Database";
    }

    @Override
    protected void setupParameters(Map parameters) {
        super.setupParameters(parameters);
        parameters.put(DRIVER.key, DRIVER);
        parameters.put(SRID.key, SRID);
        parameters.put(URL.key, URL);
    
        parameters.remove(DBTYPE.key);
        parameters.remove(HOST.key);
        parameters.remove(PORT.key);
        parameters.remove(DATABASE.key);
        parameters.remove(SCHEMA.key);
    }
        
    @Override
    protected JDBCDataStore createDataStoreInternal(JDBCDataStore dataStore, Map params) throws IOException {
        CitusDialectPrepared dialect = (CitusDialectPrepared)dataStore.getSQLDialect();
        // dialect.setStorageEngine(storageEngine)
        
        return dataStore;
    }
    
    @Override
    protected String getValidationQuery() {
        return null;
    }

	public Integer getSRID() 
	{
		return this.srid;
	}

	@Override
	protected String getDatabaseID() {
		return null;
	}
}
