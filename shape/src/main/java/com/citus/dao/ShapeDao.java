package com.citus.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapExecutor;

import egovframework.rte.psl.dataaccess.EgovAbstractDAO;

@Repository("shapeDao")
public class ShapeDao extends EgovAbstractDAO 
{

	public void createGeometryTable(Map<String, Object> commandMap) 
	{

	}
}