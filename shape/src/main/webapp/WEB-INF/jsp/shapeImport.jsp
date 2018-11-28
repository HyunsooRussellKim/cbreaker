<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"></c:set>

<script>
	$(document).ready(function(){
		// import shape
		$("#files").kendoUpload();
		$("#importShape").click(importShape);
		
		// area query
 		$("#MINX").kendoNumericTextBox()
 		$("#MINY").kendoNumericTextBox()
 		$("#MAXX").kendoNumericTextBox()
 		$("#MAXY").kendoNumericTextBox()
 		
 		$("#areaQuery").click(areaQuery);
 		$("#areaQuery2").click(areaQuery2);
 		
 		$("#searchProxy").click(searchProxy);
 		$("#updateGeometry").click(updateGeometry);
 		$("#newGeometry").click(newGeometry);
        $("#delGeometry").click(delGeometry);
        $("#exportShape").click(exportShape);
 		
	});
	
	function importShape() {
		if (!confirm('선택한 파일을 갱신할까요?'))
			return;
		
		import_form.action ="${contextPath}/doShapeImport.do";
		import_form.submit();
	}
	
	var a;
	function areaQuery() {
		var data = {table:$("#table").val(),
				code: $("#code").val(),
				MINX:$("#MINX").val(), 
				MINY:$("#MINY").val(), 
				MAXX:$("#MAXX").val(), 
				MAXY:$("#MAXY").val()};
		
		$.ajax({
			type : "POST",  
			contentType:"application/x-www-form-urlencoded; charset=UTF-8", 
			url : "${contextPath}/areaQuery.json.do", 
			data : data,
			success : function(data) {
				a = data;
				$("#query-result").val(JSON.stringify(data, null, 2));
			} 
		});	
	}
	
	function areaQuery2() {
		var data = {table:$("#table").val(), 
				MINX:$("#MINX").val(), 
				MINY:$("#MINY").val(), 
				MAXX:$("#MAXX").val(), 
				MAXY:$("#MAXY").val()};
		
		$.ajax({
			type : "POST",  
			contentType:"application/x-www-form-urlencoded; charset=UTF-8", 
			url : "${contextPath}/areaQuery2.json.do", 
			data : data,
			success : function(data) {
				a = data;
				$("#query-result").val(JSON.stringify(data, null, 2));
			} 
		});	
	}
	
	function searchProxy() {
		var data = {q:$("#q").val(), 
				category:$("#category").val(),
				pageUnit:$("#pageUnit").val(),
				pageIndex:$("#pageIndex").val(),
				output:$("#output").val(),
				apiKey:$("#apiKey").val()};
		
		$.ajax({
			type : "POST",  
			contentType:"application/x-www-form-urlencoded; charset=UTF-8", 
			url : "${contextPath}/searchProxy.json.do", 
			data : data,
			success : function(data) {
				a = data;
				$("#search-result").val(JSON.stringify(data, null, 2));
			} 
		});	
	}
	
	function updateGeometry() {
		var data = {	
						tableName:$("#update-table").val(),
						featureCollection:$("#update-json").val()
				   };
		
		$.ajax({
			type : "POST",  
			contentType:"application/x-www-form-urlencoded; charset=UTF-8", 
			url : "${contextPath}/ModGeometry.json.do", 
			data : data,
			success : function(data) {
				$("#update-result").val(JSON.stringify(data, null, 2));
			} 
		});	
	}
	
    function newGeometry() {
        var data = {    
                        tableName:$("#new-table").val(),
                        featureCollection:$("#new-json").val()
                   };
        
        $.ajax({
            type : "POST",  
            contentType:"application/x-www-form-urlencoded; charset=UTF-8", 
            url : "${contextPath}/AddGeometry.json.do", 
            data : data,
            success : function(data) {
                $("#new-result").val(JSON.stringify(data, null, 2));
            } 
        }); 
    }

    function delGeometry() {
        var data = {    
                        tableName:$("#del-table").val(),
                        featureCollection:$("#del-json").val()
                   };
        
        $.ajax({
            type : "POST",  
            contentType:"application/x-www-form-urlencoded; charset=UTF-8", 
            url : "${contextPath}/delGeometry.json.do", 
            data : data,
            success : function(data) {
                $("#del-result").val(JSON.stringify(data, null, 2));
            } 
        }); 
    }
    
    
    function exportShape() {
    	var data = {
    		table : $('#export-table').val(),
    	    code : $('#export-code').val(),
    	    type : $('#geom-type').val(),
    	    epsg : 'EPSG:5186',
    	    condition: $('#sql-subcondition').val()
    	};

        $.ajax({
            type : "POST",  
            contentType:"application/x-www-form-urlencoded; charset=UTF-8", 
            url : "${contextPath}/doShapeExport.do", 
            data : data,
            success : function(data) {
                $("#export-result").val(JSON.stringify(data, null, 2));
            } 
        }); 
    	
    }
    
    
	
</script>
<div class="demo-section">
	<div>1. SHP 파일을 서버로 업로드 하고 데이터베이스를 갱신합니다. by 시터스 <br><br></div>
	<form name="import_form" enctype="multipart/form-data" method="post" action="${contextPath}/doShapeImport.do">
		<input name="files" id="files" type="file"/><br><br>
	</form>
	<input id="importShape" value="Submit" class="k-button" />
</div>
<br>
<div class="demo-section">
	<div>2. 영역 쿼리를 수행합니다 <br><br></div>
	Table  <input id="table" type="text" value="WTL_FIRE_PS"/>
	CODE  <input id="code" type="text" value="SA119"/><br>
	MIN_X  <input id="MINX" type="number" value="234173"/>
	MIN_Y  <input id="MINY" type="number" value="385807"/><br>
	MAX_X  <input id="MAXX" type="number" value="234428"/>
	MAX_Y  <input id="MAXY" type="number" value="386035"/><br>
	<textarea id="query-result" style="width:100%;height:100px"></textarea>
	<input id="areaQuery" value="Submit" class="k-button" /><br>
	<input id="areaQuery2" value="Submit" class="k-button" /><br>
</div>
<br>
<div class="demo-section">
	<div>3. vWorld search proxy <br><br></div>
   	<input type="text" id="q" value = "시터스">
   	<input type="text" id="category" value = "Poi">
   	<input type="text" id="pageUnit" value = "10">
   	<input type="text" id="pageIndex" value = "1">
   	<input type="text" id="output" value = "json">
   	<input type="text" id="apiKey" value = "4FAC4DA4-D9B1-3CE5-891C-CE3664473E79">
   	<br>    
   	<input id="searchProxy" value = "Submit" class="k-button">
	<textarea id="search-result" style="width:100%;height:100px"></textarea>
</div>
<br>
<div class="demo-section">
	<div>4. Update Geometry <br><br></div>
	Table  <input id="update-table" type="text" value="WTL_FIRE_PS"/><br>
	<textarea id="update-json" style="width:100%;height:50px">
	   {
	       "features":[
	           {"properties":{"FTR_IDN":225},"type":"Feature","geometry":{"type":"Point","coordinates":[234327.78,385986.015]}},
	           {"properties":{"FTR_IDN":227},"type":"Feature","geometry":{"type":"Point","coordinates":[234320.176,385857.19]}}
           ],
           "type":"FeatureCollection"
        }
    </textarea>
   	<br>    
   	<input id="updateGeometry" value = "Submit" class="k-button"> 
   	<input type="text" id="update-result">
</div>

<div class="demo-section">
    <div>5. New Geometry <br><br></div>
    Table  <input id="new-table" type="text" value="WTL_FIRE_PS"/><br>
    <textarea id="new-json" style="width:100%;height:50px">
        {
            "features":[
                {"properties":{"FTR_IDN":225},"type":"Feature","geometry":{"type":"Point","coordinates":[234329.78,385986.015]}},
                {"properties":{"FTR_IDN":225},"type":"Feature","geometry":{"type":"Point","coordinates":[234323.176,385857.19]}}
            ],
            "type":"FeatureCollection"
        }
    </textarea>
    <br>    
    <input id="newGeometry" value = "Submit" class="k-button"> 
    <input type="text" id="new-result">
</div>

<div class="demo-section">
    <div>6. Del Geometry <br><br></div>
    Table  <input id="del-table" type="text" value="WTL_FIRE_PS"/><br>
    <textarea id="del-json" style="width:100%;height:50px">
        {
            "features":[
                {"properties":{"FTR_IDN":225},"type":"Feature"}
            ],
            "type":"FeatureCollection"
        }
    </textarea>
    <br>    
    <input id="delGeometry" value = "Submit" class="k-button"> 
    <input type="text" id="del-result">
</div>

<div class="demo-section">
    <div>7. Export shapefile<br><br></div>
    Table : <input id="export-table" type="text" value="WTL_FIRE_PS"/>   
    Code : <input id="export-code" type="text" value="SA119"/>
    Type : <input id="geom-type" type="text" value="point"/><br>
    "layer.FTR_IDN=117" or "*"<br>
    <input type="text" id="sql-subcondition" style="width:100%;height:50px" value="*"/><br>    
    <input id="exportShape" value = "Submit" class="k-button"/>
    <input type="text" id="export-result" style="width:100%"></input>
</div>



