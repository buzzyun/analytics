if (typeof console == "undefined") var console = { log: function() {} };

function nl2br(str){
	return $.trim(str).replace(/\n/g,"<br>");
}

function br2nl(str){
	return str.replace(/(<br>)|(<br \/>)|(<p>)|(<\/p>)/g,"\n");
}

function toSafeString(str){
	return $.trim(str).replace(/'/gi, "").replace(/"/gi, "").replace(/\\/gi, "");
}

function submitGet(url, data){
	submitForm(url, data, "GET");
}
function submitPost(url, data){
	submitForm(url, data, "POST");
}
//가상의 폼을 만들어서 sumit한다.
function submitForm(url, data, method){
	if($("#jQueryPostItForm")){
		$("#jQueryPostItForm").remove();
	}
	$('body').append($('<form/>', {
		id : 'jQueryPostItForm',
		method : method,
		action : url
	}));
	
	for ( var i in data) {
		$('#jQueryPostItForm').append($('<input/>', {
			type : 'hidden',
			name : i,
			value : data[i]
		}));
	}

	$('#jQueryPostItForm').submit();
	
}

function loadToTab(url, data, id){
	//console.log(url, data, id);
	$.ajax({
		url : url,
		data : data,
		type : "POST",
		success : function(response) {
			$(id).html(response);
		}
	});
} 

var CONTEXT = /(\/[^\/]+)\//.exec(window.location.pathname)[1];
var PROXY_REQUEST_URI = window.location.protocol + "//" + window.location.host
		+ CONTEXT+"/main/request.html";

function requestProxy(methodType, data, resultType, successCallback, failCallback, completeCallback){
	
	$.ajax({
		url : PROXY_REQUEST_URI,
		type : methodType,
		data : data,
		dataType : resultType,
		success: successCallback,
		fail: failCallback,
		complete: completeCallback 
	});
}

function runIndexing(collectionId, indexingType) {
	if(!indexingStatePollingFlag){
		startPollingIndexTaskState(collectionId);
	}
	
	$.ajax({
		url : PROXY_REQUEST_URI,
		type : "POST",
		data : {
			uri : "/indexing/"+indexingType+"/run",
			collectionId : collectionId
		},
		dataType : "json"

	}).done(function(msg) {
		noty({text: collectionId+ " " + indexingType + " Indexing Running.", type: "success", layout:"topRight", timeout: 3000});
	}).fail(function(jqXHR, textStatus, error) {
		alert("Request failed: " + jqXHR.responseText);
	}).always(function() {
		// alert("complete");
	});
}
function runFullIndexing(collectionId) {
	runIndexing(collectionId, "full");
}
function runAddIndexing(collectionId) {
	runIndexing(collectionId, "add");
}
function runDocumentFullIndexing(collectionId) {
	runIndexing(collectionId, "full/document-store");
}

function runIndexBuildFullIndexing(collectionId) {
	runIndexing(collectionId, "full/index-build");
}
function stopIndexing(collectionId) {
	$.ajax({
		url : PROXY_REQUEST_URI,
		type : "POST",
		data : {
			uri : "/indexing/stop",
			collectionId : collectionId
		},
		dataType : "json"

	}).done(function(result) {
		if(result.success){
			noty({text: collectionId+ " Indexing Stop Requested.", type: "success", layout:"topRight", timeout: 3000});
		}else{
			noty({text: collectionId+ " indexing job not running.", type: "error", layout:"topRight", timeout: 5000});
		}
	}).fail(function(jqXHR, textStatus, error) {
		alert("Request failed: " + jqXHR.responseText);
	}).always(function() {
		// alert("complete");
	});
}


var indexingStatePollingFlag = false;
function startPollingIndexTaskState(collectionId){
	startPollingIndexTaskState(collectionId, true);
}
function startPollingIndexTaskState(collectionId, keepPollingFlag){
	indexingStatePollingFlag = keepPollingFlag;
	(function poll() {
		$.ajax({
			url: PROXY_REQUEST_URI,
			type: "GET",
			data : {
				uri : "/indexing/task-state",
				collectionId : collectionId
			},
			dataType: "json",
			complete: function() { if(indexingStatePollingFlag) {setTimeout(function() {poll();}, 2000); } },
			timeout: 2000,
			success: function(data) {
				console.log("polling ", data, data.indexingState);
				if($.isEmptyObject(data.indexingState)){
					$("#indexing_type").text("");
					$("#indexing_state").text("");
					$("#indexing_document_count").text("");
					$("#indexing_scheduled").text("");
					$("#indexing_start_time").text("");
					$("#indexing_elapsed").text("");
//					stopPollingIndexTaskState();
				} else {
					$("#indexing_type").text(data.indexingState.indexingType);
					$("#indexing_state").html("<i class=\"icon-spinner icon-spin icon-large\"></i> " + data.indexingState.state);
					$("#indexing_document_count").text(data.indexingState.count);
					$("#indexing_scheduled").text(data.indexingState.isScheduled);
					$("#indexing_start_time").text(data.indexingState.startTime);
					$("#indexing_elapsed").text(data.indexingState.elapsed);
					
				}
			}
		});
	})();
}


function stopPollingIndexTaskState(){
	indexingStatePollingFlag = false;
}


var pollingTimeout = 3000;
var pollingInterval = 1000;
var pollingAllTaskURI = "/management/common/all-task-state.json";
////////////task polling for tasks page

function startPollingAllTaskState(){
	(function poll() {
		$.ajax({
			url: PROXY_REQUEST_URI,
			type: "GET",
			data : {
				uri : pollingAllTaskURI
			},
			dataType: "json",
			complete: function() { setTimeout(function() {poll();}, pollingInterval);},
			timeout: pollingTimeout,
			success: function(data) {
				console.log("polling task for page", data, data.taskState);
				//$("#running_tasks_top").find(".count").text(data.taskState.length);
				
				//task페이지가 존재하면.
				if($("#_logs_tasks_table").length > 0){
					$("#_logs_tasks_table").find("tbody tr").remove();
				}
				
				if(data.taskState.length > 0){
					
					for(var i = 0; i < data.taskState.length; i++){
						
						if($("#_logs_tasks_table").length > 0){
							
							var $task = $("<tr><td class=\"_task_num\"></td><td><span class=\"task\"><span class=\"desc\"></span> <span class=\"percent\"></span></span>"
								+"<div class=\"progress progress-small progress-striped active\"><div style=\"width: 20%;\" class=\"progress-bar progress-bar-info\"></div></div>"
								+"</td><td class=\"_task_eclapsed\"></td><td class=\"_task_startTime\">2013-09-10 12:35:00</td></tr>");
							$task.find("._task_num").text(i+1);
							$task.find(".desc").text(data.taskState[i].summary);
							$task.find("._task_eclapsed").text(data.taskState[i].elapsed);
							$task.find("._task_startTime").text(data.taskState[i].startTime);
							if(data.taskState[i].progress != -1){
								$task.find(".percent").text(data.taskState[i].progress+"%");
								$task.find(".progress-bar").css("width", data.taskState[i].progress+"%");
							}else{
								$task.find(".progress-bar").css("width", "50%");
							}
							$("#_logs_tasks_table").append($task);
						}
						
					}
					
				}
			}
		});
	})();
}


////////////task polling for taskbar

var pollingAllTaskStateFlagForTaskBar = false;

function startPollingAllTaskStateForTaskBar(){
	if(pollingAllTaskStateFlagForTaskBar){
		return;
	}
	pollingAllTaskStateFlagForTaskBar = true;
	
	(function poll() {
		$.ajax({
			url: PROXY_REQUEST_URI,
			type: "GET",
			data : {
				uri : pollingAllTaskURI
			},
			dataType: "json",
			complete: function() { if(pollingAllTaskStateFlagForTaskBar) {setTimeout(function() {poll();}, pollingInterval); } },
			timeout: pollingTimeout,
			success: function(data) {
//				console.log("polling task for taskbar", data, data.taskState);
				
				$("#running_tasks_top").find("li").not(".title").remove();
				$("#running_tasks_top").find(".count").text(data.taskState.length);
				
				if(data.taskState.length > 0){
					
					for(var i = 0; i < data.taskState.length; i++){
						
						//상단 task 요약.
						var $task = $("<li><a href=\"javascript:void(0);\"><span class=\"task\"><span class=\"desc\">11</span><span class=\"percent\"></span></span>"
								+"<div class=\"progress progress-small progress-striped active\"><div style=\"width: 1%;\" class=\"progress-bar progress-bar-info\"></div>"
								+"</div></a></li>");
						$task.find(".desc").text(data.taskState[i].summary);
						if(data.taskState[i].progress != -1){
							$task.find(".percent").text(data.taskState[i].progress+"%");
							$task.find(".progress-bar").css("width", data.taskState[i].progress+"%");
						}else{
							$task.find(".progress-bar").css("width", "50%");
						}
						$("#running_tasks_top").append($task);
					}
					
				}
			}
		});
	})();
}


function stopPollingAllTaskStateForTaskBar(){
	pollingAllTaskStateFlagForTaskBar = false;
}



/////////////////// keyword
function loadKeywordTab(keywordId, pageNo, keyword, searchColumn, exactMatch, isEditable, targetId, deleteIdList){
	console.log("loadKeywordTab", keywordId, pageNo, escape(keyword), searchColumn, exactMatch, isEditable, targetId, deleteIdList);
	loadToTab('list.html', {keywordId: keywordId, pageNo: pageNo, keyword: keyword, searchColumn: searchColumn, exactMatch: exactMatch, isEditable: isEditable, targetId: targetId, deleteIdList: deleteIdList}, targetId);
}


function truncateKeyword(analysisId, keywordId, callback){
	requestProxy("POST", { 
		uri: '/management/keyword/truncate.json',
		pluginId: analysisId,
		keywordId: keywordId
	},
	"json",
	function(response) {
		if(response.success){
			noty({text: "Clean data success.", type: "success", layout:"topRight", timeout: 3000});
			callback();
		}else{
			var message = "Clean data error.";
			if(response.errorMessage){
				message = message + " Reason = "+response.errorMessage;
			}
			noty({text: message, type: "error", layout:"topRight", timeout: 3000});
		}
	},
	function(response){
		noty({text: "Clean data error.", type: "error", layout:"topRight", timeout: 3000});
	});	
}



function checkableTable(tableId) {
	console.log("checkabel ", $(tableId).find( 'thead th.checkbox-column :checkbox' ));
//	$(tableId).find(':checkbox').each(function(j, cb_self) {
//		$(cb_self).uniform();
//		$.uniform.update($(cb_self));
//	});
	$(tableId).find( 'thead th.checkbox-column :checkbox' ).on('change', function() {
		var checked = $( this ).prop( 'checked' );
		$( this ).parents('table').children('tbody').each(function(i, tbody) {
			$(tbody).find('.checkbox-column').each(function(j, cb) {
				var cb_self = $( ':checkbox', $(cb) ).prop( "checked", checked ).trigger('change');
//				if (cb_self.hasClass('uniform')) {
//					$.uniform.update(cb_self);
//				}

				$(cb).closest('tr').toggleClass( 'checked', checked );
			});
		});
	});
	$(tableId).find( 'tbody tr td.checkbox-column :checkbox' ).on('change', function() {
		var checked = $( this ).prop( 'checked' );
		$( this ).closest('tr').toggleClass( 'checked', checked );
	});
}


function downloadKeyword(keywordId){
	console.log("keywordId" , keywordId);
	submitGet("download.html", {keywordId : keywordId});
}


function applySelectKeyword(analysisId){
	var idList = new Array();
	$("._table_keyword_list").find('tr.checked').each(function() {
		var id = $(this).find("td input[name=ID]").val();
		idList.push(id);
	});
	if(idList.length == 0){
		alert("Please select keyword.");
		return;
	}
	
	var keywordIdList = idList.join(",");
	
	if(!confirm("Apply selected ["+keywordIdList+"] "+idList.length+" keyword?")){
		return;	
	}
	//applykeyword("${analysisId }", keywordIdList);
	console.log("apply dict ", analysisId, keywordIdList);
	
	showModalSpinner();
	
	$.ajax({
		url : PROXY_REQUEST_URI,
		type : "POST",
		data : {
			uri : "/management/keyword/apply.json",
			pluginId : analysisId,
			keywordId: keywordIdList
		},
		dataType : "json"

	}).success(function(msg) {
		console.log(msg);
		noty({text: "Keyword apply success", type: "success", layout:"topRight", timeout: 3000});
	}).fail(function(jqXHR, textStatus, error) {
		noty({text: "Keyword apply error.", type: "error", layout:"topRight", timeout: 3000});
	}).done(function(){
		loadToTab("overview.html", null, "#tab_keyword_overview");
		hideModalSpinner();
	});
}



///////////////////spinner

function showModalSpinner(){
	var spinner_opts = {
		lines: 11, // The number of lines to draw
		length: 21, // The length of each line
		width : 10, // The line thickness
		radius : 32, // The radius of the inner circle
		corners : 1, // Corner roundness (0..1)
		rotate : 0, // The rotation offset
		direction : 1, // 1: clockwise, -1: counterclockwise
		color : '#fff', // #rgb or #rrggbb or array of colors
		speed : 1, // Rounds per second
		trail : 60, // Afterglow percentage
		shadow : false, // Whether to render a shadow
		hwaccel : false, // Whether to use hardware acceleration
		className : 'spinner', // The CSS class to assign to the spinner
		zIndex : 2e9, // The z-index (defaults to 2000000000)
		top : 'auto', // Top position relative to parent in px
		left : 'auto' // Left position relative to parent in px
	};
	var spinObj = $('<div id="spin_modal_overlay" style="background-color: rgba(0, 0, 0, 0.6); width:100%; height:100%; position:fixed; top:0px; left:0px; z-index:10000"/>');
	$('body').append(spinObj);
	var spinner = new Spinner(spinner_opts).spin(spinObj[0]);
}

function hideModalSpinner(){
	if($('#spin_modal_overlay')){
		$('#spin_modal_overlay').remove();
	}
}



/////////////////////////// collection data 
function loadDataRawTab(collectionId, pageNo, targetId){
	console.log("loadDataRawTab", collectionId, pageNo, targetId);
	loadToTab('dataRaw.html', {collectionId: collectionId, pageNo: pageNo, targetId: targetId}, targetId);
}

function loadDataAnalyzedTab(collectionId, pageNo, targetId){
	console.log("loadDataAnalyzedTab", collectionId, pageNo, targetId);
	loadToTab('dataAnalyzed.html', {collectionId: collectionId, pageNo: pageNo, targetId: targetId}, targetId);
}

function loadDataSearchTab(collectionId, pageNo, targetId){
	console.log("loadDataRawTab", collectionId, pageNo, targetId);
	loadToTab('dataRaw.html', {collectionId: collectionId, pageNo: pageNo, targetId: targetId}, targetId);
}

/////////////////////////// account setting

function updateUsingProxy(formName, mode) {
	var form = $("form#"+formName+"");
	if(!form.valid()){
		return;
	}
	
	form[0].mode.value=mode;
	console.log("updateUsingProxy > ", form);
	form.submit(function(e) {
		var postData = $(this).serializeArray();
		$.ajax({
				url : PROXY_REQUEST_URI,
				type: "POST",
				data : postData,
				dataType : "json",
				success:function(data, textStatus, jqXHR) {
					try {
						if(data["success"]=="true") {
							location.href = location.href;
						}
					} catch (e) { 
						alert("error occured for update");
					}
					
				}, error: function(jqXHR, textStatus, errorThrown) {
					alert("ERROR" + textStatus + " : " + errorThrown);
				}
		});
		e.preventDefault(); //STOP default action
	});
	form.submit();
}


/////////////////////// test > search

function loadSearchTestTab(queryString, targetId){
	console.log("loadSearchTestTab", queryString, targetId);
	loadToTab('searchResult.html', {queryString: queryString, targetId: targetId}, targetId);
}


/////////////////// cookie

function setCookie(cookieName, value, expireDays) {
	var expireDate = new Date();
	expireDate.setDate(expireDate.getDate() + expireDays);
	var cookieValue = escape(value)
			+ ((expireDays == null) ? "" : "; expires=" + expireDate.toUTCString());
	document.cookie = cookieName + "=" + cookieValue;
}

function getCookie(cookieName) {
	var cookieValue = document.cookie;
	var startIndex = cookieValue.indexOf(" " + cookieName + "=");
	if (startIndex == -1) {
		startIndex = cookieValue.indexOf(cookieName + "=");
	}
	if (startIndex == -1) {
		cookieValue = null;
	} else {
		startIndex = cookieValue.indexOf("=", startIndex) + 1;
		var endIndex = cookieValue.indexOf(";", startIndex);
		if (endIndex == -1) {
			endIndex = cookieValue.length;
		}
		cookieValue = unescape(cookieValue.substring(startIndex, endIndex));
	}
	return cookieValue;
}

function deleteCookie(cookieName) {
	var expireDate = new Date();

	// 어제 날짜를 쿠키 소멸 날짜로 설정한다.
	expireDate.setDate(expireDate.getDate() - 1);
	document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
}

function storeQueryHistory(host, queryString){
	
	
}

function getQueryHistory(host){
	
}


/////////////////////////// logs data 
function loadNotificationTab(pageNo, targetId){
	loadToTab('notificationsDataRaw.html', {pageNo: pageNo}, targetId);
}

function loadExceptionTab(pageNo, targetId){
	loadToTab('exceptionsDataRaw.html', {pageNo: pageNo}, targetId);
}

/////
function startCollection(collectionId){
	operateCollection(collectionId, "Start");
}
function stopCollection(collectionId){
	operateCollection(collectionId, "Stop");
}

function operateCollection(collectionId, command){
	
	if(confirm(command+" collection " + collectionId + "?")){
		requestProxy("POST", {uri:"/management/collections/operate", collectionId: collectionId, command: command}, "json"
			, function(){
				location.href = location.href;
			}, function(){
				noty({text: "Cannot "+command+" collection "+collectionId+" : " + response["errorMessage"], type: "error", layout:"topRight", timeout: 5000});
			});
	}
}



///////////////////// category select list
function triggerSiteCategoryList(siteObject, categoryOject, defaultSiteId, defaultCategoryId){
	
	$.ajax({
		url : CONTEXT+"/report/siteList.html",
		type: "POST",
		dataType : "json",
		success:function(data, textStatus, jqXHR) {
			try {
				siteObject.children().remove();
				
				siteObject.append("<option value='_root' "+(defaultSiteId == "_root" ? "selected" : "")+">전체</option>");
				$.each(data, function() {
					$.each(this, function(k, v) {
						siteObject.append("<option value='"+k+"' "+(defaultSiteId == k ? "selected" : "")+">"+v+"</option>");
					});
				});
				fillCategoryList(defaultSiteId, categoryOject, defaultCategoryId);
			} catch (e) { 
				alert("error occured for update");
			}
			
		}, error: function(jqXHR, textStatus, errorThrown) {
			alert("ERROR" + textStatus + " : " + errorThrown);
		}
	});
	
	siteObject.on("change", function(){
		var siteId = $(this).find(':selected').attr('value');
		fillCategoryList(siteId, categoryOject, defaultCategoryId);
	});


}

function fillCategoryList(siteId, categoryOject, defaultCategoryId){
	
	$.ajax({
		url : CONTEXT+"/"+siteId+"/report/categoryList.html",
		type: "POST",
		data: { siteId: siteId },
		dataType : "json",
		success:function(data, textStatus, jqXHR) {
			try {
				categoryOject.children().remove();
				if(data.length > 0){
					$.each(data, function() {
						$.each(this, function(k, v) {
							categoryOject.append("<option value='"+k+"' "+(defaultCategoryId == k ? "selected" : "")+">"+v+"</option>");
						});
					});
					categoryOject.attr('disabled', false);
				}else{
					categoryOject.attr('disabled', true);
				}
			} catch (e) { 
				alert("error occured for update");
			}
			
		}, error: function(jqXHR, textStatus, errorThrown) {
			alert("ERROR" + textStatus + " : " + errorThrown);
		}
	});
}


function getFirstDayOfWeek (date) {
	var newDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
	newDate.setDate( newDate.getDate() - ( ( newDate.getDay() + 6 ) % 7 ) );
	return newDate;
}

function getLastDayOfWeek (date) {
	var newDate = getFirstDayOfWeek(date);
	newDate.setDate( newDate.getDate() + 6 );
	return newDate;
}

function getFirstDayOfMonth (date) {
	return new Date(date.getFullYear(), date.getMonth(), 1);
}

function getLastDayOfMonth (date) {
	return new Date(date.getFullYear(), date.getMonth() + 1, 0);
}

function getFirstDayOfYear (date) {
	return new Date(date.getFullYear(), 0, 1);
}

function getLastDayOfYear (date) {
	return new Date(date.getFullYear(), 11, 31);
}

function parseDate(dateStr) {
	if(!$.isArray(dateStr)) {
		src = dateStr.split("."); src = dateStr.split(".");
		return new Date(src[0], src[1] - 1, src[2]);
	};
}

function cloneDate(date) {
	return new Date(date.getFullYear(), date.getMonth(), date.getDate());
}

function formatDate(dateObj, failValue) {
	if(dateObj==null) {
		dateObj = parseDate(failValue);
	}
	var year = dateObj.getFullYear();
	var month = (dateObj.getMonth() + 1);
	var date = dateObj.getDate();
	if(month < 10) { month = "0"+month; }
	if(date < 10) { date = "0"+date; }
	return year+"."+month+"."+date;
}