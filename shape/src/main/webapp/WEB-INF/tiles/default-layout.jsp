<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"></c:set>

<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
	<meta http-equiv="cache-control" content="no-cache" />
    
	<link rel="stylesheet" type="text/css" href="${contextPath}/js/kendo/styles/kendo.common.min.css" />
	<link rel="stylesheet" type="text/css" href="${contextPath}/js/kendo/styles/kendo.default.min.css" />
	
	<link rel="stylesheet" type="text/css" href="${contextPath}/css/default.css" />
	<link rel="stylesheet" type="text/css" href="${contextPath}/css/default-layout.css" />

	<script src="${contextPath}/js/kendo/jquery.min.js"></script>
	<script src="http://malsup.github.com/jquery.form.js"></script> 
  	<script src="${contextPath}/js/kendo/kendo.web.js"></script>
  	<script src="${contextPath}/js/jquery.cookie.js"></script>
  	<script src="${contextPath}/js/common.js"></script>
  	
	<title>Shape</title>
</head>
<body>
	<div id="wrap">
		<div id="header" class="global-header">
			<tiles:insertAttribute name="header"></tiles:insertAttribute>
		</div>
		<div id="container" class="global-contents">
			<div id="contents">
				<tiles:insertAttribute name="contents"></tiles:insertAttribute>
			</div>
		</div>
	</div>
</body>
</html>

