package com.citus.service;

import java.util.Map;

public interface ShapeService 
{
	void createGeometryTable(Map<String, Object> commandMap);
}
