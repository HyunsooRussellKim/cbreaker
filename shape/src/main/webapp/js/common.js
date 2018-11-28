// common.js

var _defaultSelectText = '--- 선택 ---';

$(function(){
	if (typeof Array.prototype.indexOf != 'function') 
	{
		Array.prototype.indexOf = function(obj) {
			var index = -1;
			$.each(this, function(i, e){
				if (e == obj) {
					index = i;
					return false;
				}
			});
			
			return index;
		};
	}

	// trim fix
	if (typeof String.prototype.trim != 'function') 
	{
		String.prototype.trim = function() 
		{
			return this.replace(/^\s+|\s+$/g, '');
		};
	}	
	
	// placeholder fix
	var input = document.createElement("input");
    if(('placeholder' in input)==false) 
    { 
		$('[placeholder]').focus(function() {
			var i = $(this);
			if(i.val() == i.attr('placeholder')) 
			{
				i.val('').removeClass('placeholder');
				if(i.hasClass('password')) 
				{
					i.removeClass('password');
					this.type='password';
				}			
			}
		}).blur(function() {
			var i = $(this);	
			if(i.val() == '' || i.val() == i.attr('placeholder')) 
			{
				if(this.type=='password') 
				{
					i.addClass('password');
					this.type='text';
				}
				i.addClass('placeholder').val(i.attr('placeholder'));
			}
		}).blur().parents('form').submit(function() {
			$(this).find('[placeholder]').each(function() {
				var i = $(this);
				if(i.val() == i.attr('placeholder'))
					i.val('');
			});
		});
	}	
    
	var padder = function(a,b){return(1e15+a+"").slice(-b);};
    // date formatter
    Date.prototype.formatShortDate = function() {
    	return this.getFullYear() + padder(this.getMonth() + 1, 2) + padder(this.getDate(), 2);
    };
    Date.prototype.formatDate = function() {
    	return this.getFullYear() + "-" + padder(this.getMonth() + 1, 2) + "-" + padder(this.getDate(), 2);
    };
    Date.prototype.formatDateTime = function() {
    	return this.formatDate() + " " + padder(this.getHours(), 2) + ":" + padder(this.getMinutes(), 2) + ":" + padder(this.getSeconds(), 2);
    };

    Date.parseDateTime = function(str) {
    	if (str == null) return null;
    	
    	var arr = str.split(' ');
    	var yyyy=0, MM=0, dd=0, hh=0, mm=0, ss=0;
    	if (arr[0].length == 8) {
    		yyyy = parseInt(arr[0].substr(0, 4), 10);
    		MM   = parseInt(arr[0].substr(4, 2), 10) - 1;
    		dd   = parseInt(arr[0].substr(6, 2), 10);
    	}
    	else if (arr[0].length == 10) {
    		yyyy = parseInt(arr[0].substr(0, 4), 10);
    		MM   = parseInt(arr[0].substr(5, 2), 10) - 1;
    		dd   = parseInt(arr[0].substr(8, 2), 10);
    	}
    	else { return null; }
    		
    	if (arr.length > 1) {
	    	if (arr[1].length == 4) {
	    		hh = parseInt(arr[1].substr(0, 2), 10);
	    		mm = parseInt(arr[1].substr(2, 2), 10);
	    	}
	    	if (arr[1].length == 6) {
	    		hh = parseInt(arr[1].substr(0, 2), 10);
	    		mm = parseInt(arr[1].substr(2, 2), 10);
	    		mm = parseInt(arr[1].substr(4, 2), 10);
	    	}
	    	else {
	    		var ta = arr[1].split(':');
	    		if (ta.length > 0) hh = parseInt(ta[0], 10);
	    		if (ta.length > 1) mm = parseInt(ta[1], 10);
	    		if (ta.length > 2) ss = parseFloat(ta[2], 10);
	    	}
    	}
    	
    	return new Date(yyyy, MM, dd,  hh, mm, ss);
    }
    
});

function getObjectWithName(object, name) {
	var ret = null;
	$.each(object, function(key, value){
		if (name == key) {
			ret = value;
			return false;
		}
	});
	 
	return ret;
};

function transferResultToString(code) {
	if (code == 'R') return '전송 대기';
	if (code == 'T') return '전송 시작';
	if (code == 'S') return '성공';
	if (code == 'F') return '실패';
	
	return '';
}

function canTransfer(code) {
	if (code == 'R') return false;//'전송 대기';
	if (code == 'T') return false;//'전송 시작';
	if (code == 'S') return false;//'성공';
	if (code == 'F') return true;//'실패';
	return true;
}

function clearSelection() {
    if(document.selection && document.selection.empty) {
        document.selection.empty();
    } 
    else if(window.getSelection) {
        var sel = window.getSelection();
        sel.removeAllRanges();
    }
}
function getBrowser() {
    var s = navigator.userAgent.toLowerCase();
    var match = /(webkit)[ \/](\w.]+)/.exec(s) ||
                /(opera)(?:.*version)?[ \/](\w.]+)/.exec(s) ||
                /(msie) ([\w.]+)/.exec(s) ||
               !/compatible/.test(s) && /(mozilla)(?:.*? rv:([\w.]+))?/.exec(s) ||
               [];
    return { name: match[1] || "", version: match[2] || "0" };
}

function getWindowHeight() {
	if (document.documentElement && document.documentElement.clientHeight)
		return document.documentElement.clientHeight;
	return 0;
}

function fileTemplate(e) {
	return makeFileTemplate(e, false);
}

function fileTemplateForDelete(e) {
	return makeFileTemplate(e, true);
}

function makeFileTemplate(e, addDelete) {
	var url = '/molit/getFile.do?thumbnail=true&name=' + e.filePath;
	var fullurl = '/molit/getFile.do?name=' + e.filePath;
	var del = "";
	if (addDelete == true)
		del = "<img class='file_delete' filetype='" +e.fileType+ "' fileid='"+e.fileId+"' src='/molit/js/orgTreeView/close3.gif'/>";
	if (e.fileType == 'i')
		return "<div class='file'>"+del+"<a target='_blank' href='" + fullurl + "'><img src='"+url+"'/></a></div>";
	if (e.fileType == 'p')
		return "<div class='file'>"+del+"<a target='_blank' href='" + fullurl + "'><img src='/molit/images/icon_pdf.jpg'/></a></div>";
	if (e.fileType == 'v')
		return "<div class='file'>"+del+"<a target='_blank' href='" + fullurl + "'><img src='/molit/images/icon_play.png'/></a></div>";
	
	return;
	if (getBrowser().name == "msie") {
		return '<div class="file"><object ' +
			'type="video/x-ms-asf" url="'+ fullurl +'" data="'+ fullurl +'"' +
			'classid="CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6">' +
			'<param name="url" value="'+ url +'">' +
			'<param name="autostart" value="0">' +
			'<param name="uiMode" value="full">' +
			'<param name="autosize" value="0">' +
			'<param name="playcount" value="1">  ' +                            
			'<embed type="application/x-mplayer2" src="'+ url +'" width="100%" height="100%" autostart="false" showcontrols="true" pluginspage="http://www.microsoft.com/Windows/MediaPlayer/"></embed>' +
		'</object></div>';	
	}
	
	return  '<div class="file">' +
		'<video controls>' +
  			'<source src="'+ fullurl +'" type="video/ogg">' +
  			'<source src="'+ fullurl +'" type="video/mp4">' +
  			'<object data="'+ fullurl +'" width="100" height="100">' +
  				'<embed width="100" height="100" src="'+ fullurl +'">' +
  			'</object>' +
  		'</video>' +
  		'</div>';
}

function inputText(obj) {
	var val = '';
	if (obj!=null) {
		val = $.trim(obj.val());
		if (val == obj.attr('placeholder')) val = '';
	}
	
	return val;
}

function isEmpty(obj) {
	if (obj==null || $.trim(obj).length == 0)
		return true;
	return false;
}

function isNotEmpty(obj) {
	return !isEmpty(obj);
}

function cloneJSON(json) {
	return JSON.parse(JSON.stringify(json));
}

function occurence(str, ch) {
	var count = 0;
	str = jstr(str);
	for (var i=0; i <str.length; i++) 
		if (ch == str[i]) count++;
	return count;
}

function jstr(obj) {
	return JSON.stringify(obj, null, 2);
}

function sjstr(obj) {
	return JSON.stringify(obj).replace(/\"([^(\")"]+)\"/g,"$1");
}

function jstrlog(json) {
	safelog(jstr(json));
}

function safelog(msg) {
	if (window.console != null) 
		window.console.log(msg);
}

/////////////////////////////////////////////////////////////////////////////
function checkServerResponse(data) {
	if (data.result.success == false) {
		alert(data.result.msg);
	}
	
	return data.result.success;
}

function checkServerResponseWithFormData(data) {
	var data = JSON.parse(data);
	return checkServerResponse(data);
}

function gridLocalizedPageableResource() {
	var pagable = {
   	 messages: {
   		 display: "{0} - {1} 전체 : {2}개",
   		 empty: "데이터 없음",
   		 first:"처음", previous:"이전", next:"다음", last:"마지막", refresh:"갱신"
   	 } 
	};
   	 
   	 return pagable;
}
	
function fetchGridData(options, url, data) {
	$.ajax( {
		url: url,
		type: "POST",
	 	contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		data: data, 
		dataType: "json",
		success: function(recv) {
			if (checkServerResponse(recv) == false) 
				options.error();
			else {
				options.success(recv);
			}
		}
	});
}

/////////////////////////////////////////////////////////////////////////////
function getCodeData(groupArray, success, fail) {
	$.ajax( {
		url: "/molit/getCodeInfo.json.do",
		type: "POST",
		async: false,
	 	contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		data: {codeList:groupArray}, 
		dataType: "json",
		success: function(recv) {
			if (checkServerResponse(recv) == false) {
				if (fail != null) fail(recv);
			}
			else { 
				success(recv);
			}
		}
	});
}

function getCodeName(codeList, group, mwType) {
	var name = '';
	if (mwType != null) {
		$.each(codeList, function(i, e){
			if (e.code == group) {
				$.each(e.childs, function(ci, ce){
					if (mwType == ce.mwType) {
						name = ce.mwName;
						return false;
					}
				});
				if (name != '') return false;
			}
		});
	}
	return name;
}

function fetchDropDownCodeData(options, codeArray, selectText) {
	getCodeData(codeArray, 
		function(recv){
			
			var data = [];
			if (recv.data.length != 0) {
				if (selectText != null)
					data.push({mwName:selectText, mwType:''});
				$.each(recv.data[0].childs, function(i, e){data.push(e);});
			}
			options.success(data);
		}, 
		function(recv){
			options.error();
		}
	);
}

function getCodeDropDownListParam(codeList, selectText, options) {
	var param = {		
		dataTextField:'mwName',
		dataValueField:'mwType',
		animation:false,
		dataSource: {
			transport: {
				read:function(options) {
					fetchDropDownCodeData(options, codeList, selectText);
				}
			}
		}
	};
	
	if (options != null) {
		$.extend(param, options);
	}
	
	return param;
}

////////////////////////////////////////////////////////////
function getData(url, reqData, success, fail) {
	$.ajax( {
		url: url,
		type: "POST",
		data: reqData,
		async: false,
	 	contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		dataType: "json",
		success: function(recv) {
			//safelog(jstr(recv));
			if (checkServerResponse(recv) == false) {
				if (fail != null) fail(recv);
			}
			else { 
				success(recv);
			}
		}
	});
}

function fetchDropDownData(options, url, reqData, selectText) {
	getData(url, 
		reqData,
		function(recv){
			var data = [];
			if (selectText != null) {
				var sel = '{"' + options.dataTextField + '":"'+selectText+'", "' + options.dataValueField + '":""}';
				data.push(JSON.parse(sel));
			}
			$.each(recv.data, function(i, e){data.push(e);});
			options.success(data);
		}, 
		function(recv){
			options.error();
		}
	);
}

function getDropDownListParam(url, textField, valueField, insertSelect) {
	var param = {
			dataTextField:textField,
			dataValueField:valueField,
			animation:false,
			dataSource: {
				transport: {
					read:function(options) {
						options.dataTextField = textField;
						options.dataValueField = valueField;
						fetchDropDownData(options, url, insertSelect);
					}
				}
			}
	}
	
	return param;
}

//////////////////////////////////////////////////////////////////////////////////////
function safestr(obj) {
	return (obj != null ? obj : '');
}

function abbstr(obj, len) {
	var str = safestr(obj).substr(0,len);
	if (str.length < safestr(obj).length)
		str += '...';
	return str;
}

function newline2br(str) {
	return safestr(str).replace(/\n/g, '<br>');
}

/////////////////////////////////////////////////////////////////////////////
function popupWindow(url, title, w, h, resize) {
	var wLeft = window.screenLeft ? window.screenLeft : window.screenX;
    var wTop = window.screenTop ? window.screenTop : window.screenY;

    var left = wLeft + (window.innerWidth / 2) - (w / 2);
    var top = wTop + (window.innerHeight / 2) - (h / 2);
    var shape = 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars='+ ((resize==true)?'yes':'no')+',';
    shape += 'resizable=' + ((resize==true)?'yes':'no') + ','; 
    shape += 'width=' + w + ', height=' + h + ', top=' + top + ', left=' + left;
    var popup = window.open(url, title, shape);
    popup.focus();
    return popup;
}

function formSubmit(frm, act, tar){
    frm.action = act;
    frm.target = tar;
    frm.submit();
    frm.target = "_self";
}

function createImageUpload(name) {
	$(name).kendoUpload({select:imageVideoFilter, 
		remove:removeHandlerForUpload,
		maxImage:4, maxVideo:0,	maxPdf:0, 
		imageCount:0, videoCount:0, pdfCount:0});
}
function createImageVideoUpload(name) {
	$(name).kendoUpload({select:imageVideoFilter, 
		remove:removeHandlerForUpload,
		maxImage:3, maxVideo:1,	maxPdf:0, 
		imageCount:0, videoCount:0, pdfCount:0});
}
function createDocumentUpload(name) {
	$(name).kendoUpload({select:documentFilter,
		remove:removeHandlerForUpload,
		maxImage:0, maxVideo:0, maxPdf:1, 
		imageCount:0, videoCount:0, pdfCount:0});
}

function updateAttachCount(upload, fileList) {
	$.each(fileList, function(index, e){
		if (e.fileType == 'i') upload.options.imageCount++;
		else if (e.fileType == 'v') upload.options.videoCount++;
		else if (e.fileType == 'p') upload.options.pdfCount++;
	});
}

function fileExtensionFilter(e, accept, no_match_message)
{
	var upload = $('#upload').data('kendoUpload');
	var options = cloneJSON(upload.options);
	var success = true;
	$.each(e.files, function(i, f){
		var ext = f.extension.toLowerCase();
		if (accept.indexOf(ext) == -1) {
			if (no_match_message == null)
				no_match_message = '허용되지 않는 파일입니다';
			alert(no_match_message);
			success = false;
			return false;
		}
		
		if (f.size > 10*1024*1024) {
			alert("첨부파일 크기는 10MB 이하입니다");
			success = false;
			return false;
		}
		
		if ((isImage(ext) && ++options.imageCount > options.maxImage) ||
			(isVideo(ext) && ++options.videoCount > options.maxVideo) ||
			(isPdf(ext)   && ++options.pdfCount   > options.maxPdf)) {
			alert('첨부 개수를 초과했습니다');
			success = false;
			return false;
		}
	});
	
	if (success == false)  {
		e.preventDefault();
		return;
	}
	upload.options.imageCount = options.imageCount;
	upload.options.videoCount = options.videoCount;
	upload.options.pdfCount   = options.pdfCount;
	
	//safelog(jstr(upload.options));
}

function removeHandlerForUpload(e) {
	var upload = $('#upload').data('kendoUpload');
	
	$.each(e.files, function(i, f){
		var ext = f.extension.toLowerCase();
		if (isImage(ext))      upload.options.imageCount--;
		else if (isVideo(ext)) upload.options.videoCount--;
		else if (isPdf(ext))   upload.options.pdfCount--;
	});
}

function isImage(ext) {
	if (ext == '.jpg' || ext=='.png' || ext=='.gif')
		return true;
	return false;
}
function isVideo(ext) {
	if (ext == '.mp4' || ext=='.m4v' || ext=='.avi' || ext=='.wmv')
		return true;
	return false;
}
function isPdf(ext) {
	if (ext == '.pdf')
		return true;
	return false;
}
function imageVideoFilter(e)
{
	var accept = ['.jpg', '.png', '.gif', '.mp4', '.avi', '.wmv', '.m4v'];
	fileExtensionFilter(e, accept, '이미지 또는 비디오 파일을 선택 해 주십시요');
}

function documentFilter(e)
{
	var accept = ['.pdf'];
	fileExtensionFilter(e, accept, 'PDF 파일을 선택 해 주십시요');
}


function deleteAttach(e)
{
	var that = this;
	var fileId = $(this).attr('fileid');
	var fileType = $(this).attr('filetype');
	
	if (!confirm("첨부파일은 삭제후 복원되지 않습니다\r\n그래도 삭제할까요?"))
		return;
	
	$.ajax( {
		url: "/molit/deleteAttach.json.do",
		type: "POST",
	 	contentType:"application/x-www-form-urlencoded; charset=UTF-8",
		data: {fileId:fileId}, 
		dataType: "json",
		success: function(recv) {
			if (checkServerResponse(recv) == true) { 
				$(that).parent().remove();
				
				var upload = $('#upload').data('kendoUpload');
				if (upload != null) {
					if (fileType=='i')      upload.options.imageCount--;
					else if (fileType=='v') upload.options.videoCount--;
					else if (fileType=='p') upload.options.pdfCount--;
				}
			}
		},
		error: function() { alert('서버연결에 실패했습니다'); }
	});
}

/////////////////////////////////////////////////////////////////////////////
(function($) {
	$.fn.scrollMinimal = function(smooth) {
		  var cTop = this.offset().top;
		  var cHeight = this.outerHeight(true);
		  var windowTop = $(window).scrollTop();
		  var visibleHeight = $(window).height();

		  if (cTop < windowTop) {
		    if (smooth) {
		      $('body').animate({'scrollTop': cTop}, 'slow', 'swing');
		    } 
		    else {
		      $(window).scrollTop(cTop);
		    }
		  } 
		  else if (cTop + cHeight > windowTop + visibleHeight) {
		    if (smooth) {
		      $('body').animate({'scrollTop': cTop - visibleHeight + cHeight}, 'slow', 'swing');
		    } 
		    else {
		      $(window).scrollTop(cTop - visibleHeight + cHeight);
		    }
		  }
		  return this;
	};
	
})(jQuery);
