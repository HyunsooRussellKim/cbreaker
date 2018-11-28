package com.citus.service.impl;

import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.citus.dao.ShapeDao;
import com.citus.service.ShapeService;

import egovframework.rte.fdl.cmmn.AbstractServiceImpl;

@Service("shapeService")
public class ShapeServiceImpl extends AbstractServiceImpl  implements ShapeService
{
	@Resource(name="shapeDao")
	public ShapeDao shapeDao;

	@Override
	public void createGeometryTable(Map<String, Object> commandMap) 
	{
		shapeDao.createGeometryTable(commandMap);
	}
}
