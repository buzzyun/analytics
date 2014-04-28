<%@page import="org.fastcatsearch.analytics.db.vo.RelateKeywordVO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="org.json.*"%>
<%
	List<RelateKeywordVO> entryList = (List<RelateKeywordVO>)request.getAttribute("entryList");
	int totalSize = (Integer) request.getAttribute("totalSize");
	int filteredSize = (Integer) request.getAttribute("filteredSize");
	int start = (Integer) request.getAttribute("start");
	String targetId = (String) request.getAttribute("targetId");
	
	boolean exactMatch = (Boolean) request.getAttribute("exactMatch");
	
	String keyword = (String) request.getAttribute("keyword");
	
	String siteId = request.getParameter("siteId");
	
	JSONArray searchableColumnList = new JSONArray();
	String searchColumn = (String) request.getAttribute("searchColumn");
%>
<script>

var wordInputObj;
var valueInputObj;
var wordInputResultObj;
var searchInputObj;
var searchColumnObj;
var exactMatchObj;

$(document).ready(function(){
	
	wordInputObj = $("#word_input_relate");
	valueInputObj = $("#value_input_relate");
	wordInputResultObj = $("#word_input_result_relate");
	searchInputObj = $("#search_input_relate");
	searchColumnObj = $("#relateSearchColumn");
	exactMatchObj = $("#relateExactMatch");
	
	searchInputObj.keydown(function (e) {
		if(e.keyCode == 13){
			var keyword = toSafeString($(this).val());
			loadKeywordTab('relate', 1, keyword, searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>');
			return;
		}
	});
	searchInputObj.focus();
	
	searchColumnObj.on("change", function(){
		var keyword = toSafeString(searchInputObj.val());
		if(keyword != ""){
			loadKeywordTab('relate', 1, keyword, searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>');
		}
	});
	exactMatchObj.on("change", function(){
		var keyword = toSafeString(searchInputObj.val());
		if(keyword != ""){
			loadKeywordTab('relate', 1, keyword, searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>');
		}
	});
	
	//단어추가상자PUT버튼.
	$("#word_input_button_relate").on("click", function(e){
		relateValueInsert();
	});
	//단어추가상자 엔터키. 
	wordInputObj.keydown(function (e) {
		if(e.keyCode == 13){
			relateValueInsert();
		}
	});
	valueInputObj.keydown(function (e) {
		if(e.keyCode == 13){
			relateValueInsert();
		}
	});
	
	$("#relateWordInsertModal").on("hidden.bs.modal", function(){
		relateLoadList();
		searchInputObj.focus();
	});
	
	$("#relateWordInsertModal").on("shown.bs.modal", function(){
		wordInputObj.focus();
	});
	
	if($("._table_relate")){
		checkableTable("._table_relate");
	}
	
	//사전 업로드.
	var fileInputObj = $("#relate_file_upload");
	
	fileInputObj.on("change", function(){
		console.log("val=","["+$(this).val()+"]");
		if($(this).val() != ""){
			fileInputObj.parents("form:first").ajaxSubmit({
				dataType:  "json", 
				success: function(resp){
					console.log("upload response ", resp);
					if(resp.success){
						noty({text: "File upload success", type: "success", layout:"topRight", timeout: 3000});
						$("#relateWordInsertModal").modal("hide");
					}else{
						noty({text: "File upload fail. "+resp.errorMessage, type: "error", layout:"topRight", timeout: 5000});
					}
				}
				, error: function(a){
					noty({text: "File upload error!", type: "error", layout:"topRight", timeout: 5000});
				}
				, complete: function(){
					//지워준다.
					$("#relate_file_upload").val("");
				}
			});
		}
	});
});
function relateTruncate(){
	if(confirm("Clean all data including invisible entries.")){
		truncateKeyword('${analysisId}', 'relate', relateLoadList);
	}
}
function relateLoadList(){
	var keyword = toSafeString(searchInputObj.val());
	loadKeywordTab('relate', 1, keyword, searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>');
}
function relateValueInsert(){
	var keyword = toSafeString(wordInputObj.val());
	wordInputObj.val(keyword);
	var value = toSafeString(valueInputObj.val());
	valueInputObj.val(value);
	
	if(keyword == ""){
		wordInputResultObj.text("Keyword is required.");
		return;
	}
	if(value == ""){
		wordInputResultObj.text("Value is required.");
		return;
	}
	
	$.ajax({
		url:'update.html',
		type:"POST",
		data:{
			siteId: '${siteId}',
			keywordId: 'relate',
			ID:"",
			KEYWORD: keyword,
			VALUE: value },
		dataType:"json",
		success:function(response) {
			if(response.success){
				wordInputObj.val("");
				valueInputObj.val("");
				if(keyword != ""){
					wordInputResultObj.text("\""+keyword+" > "+value+"\" Inserted.");
				}else{
					wordInputResultObj.text("\"" + value+"\" Inserted.");
				}
				wordInputResultObj.removeClass("text-danger-imp");
				wordInputResultObj.addClass("text-success-imp");
				wordInputObj.focus();
			}else{
				var message = "\""+keyword+" > "+value+"\" Insert failed.";
				if(response.errorMessage){
					message = message + " Reason = "+response.errorMessage;
				}
				wordInputResultObj.text(message);
				wordInputResultObj.addClass("text-danger-imp");
				wordInputResultObj.removeClass("text-success-imp");
			} },
		fail:function(response){
			wordInputResultObj.text("\""+keyword+"\" Insert error.");
			wordInputResultObj.addClass("text-danger-imp");
			wordInputResultObj.removeClass("text-success-imp");
		} 
	});
}

function relateWordUpdate(id){
	
	var trObj = $("#_relate-"+id);
	//console.log("update", id, trObj);
	
	var data = { 
		site: '${siteId}',
		keywordId: 'relate'
	};
	
	trObj.find("input[type=text],input[type=hidden]").each(function() {
		var name = $(this).attr("name");
		var value = toSafeString($(this).val());
		if(name != ""){
			data[name] = value;
		}
	});
	//console.log("data ",data);
	
	if(data.KEYWORD == ""){
		noty({text: "Keyword is required.", type: "warning", layout:"topRight", timeout: 2000});
		return;
	}
	
	if(data.VALUE == ""){
		noty({text: "Value is required.", type: "warning", layout:"topRight", timeout: 2000});
		return;
	}
	
	$.ajax({
		url:'update.html',
		type:"POST",
		data:data,
		dataType:"json",
		success:function(response) {
			if(response.success){
				noty({text: "Update Success", type: "success", layout:"topRight", timeout: 1000});
			}else{
				noty({text: "Update Fail", type: "error", layout:"topRight", timeout: 2000});
			}
		}, fail:function(response){
			noty({text: "Update Error", type: "error", layout:"topRight", timeout: 2000});
		}
	});
}
function gorelateKeywordPage(uri, pageNo){
	loadKeywordTab('relate', pageNo, '${keyword}', searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>');
}
function gorelateViewablePage(pageNo){
	loadKeywordTab('relate', pageNo, '${keyword}', searchColumnObj.val(), exactMatchObj.is(":checked"), false, '<%=targetId%>');	
}
function relatedeleteOneWord(deleteId){
	if(confirm("Are you sure to delete?")){
		loadKeywordTab('relate', '${pageNo}', '${keyword}', searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>', deleteId);
	}
}
function relatedeleteSelectWord(){
	var idList = new Array();
	$("._table_relate").find('tr.checked').each(function() {
		var id = $(this).find("td input[name=ID]").val();
		idList.push(id);
	});
	if(idList.length == 0){
		alert("Please select words.");
		return;
	}
	if(! confirm("Delete "+idList.length+" word?")){
		return;
	}
	var deleteIdList = idList.join(",");
	loadKeywordTab('relate', '${pageNo}', '${keyword}', searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>', deleteIdList);	
}
</script>

<div class="col-md-12">
<div class="widget box">
	<div class="widget-content no-padding">
		<div class="dataTables_header clearfix">
			
			<div class="form-inline col-md-7">
				<div class="form-group" style="width:240px">
					<div class="input-group" >
						<span class="input-group-addon"><i class="icon-search"></i></span>
						<input type="text" class="form-control" placeholder="Search" id="search_input_relate" value="${keyword}">
					</div>
				</div>
				<div class="form-group">
					&nbsp;
					<div class="checkbox">
					<label>
						<input type="checkbox" id="relateExactMatch" <c:if test="${exactMatch}">checked</c:if>> Exact Match
					</label>
					</div>
				</div>
			</div>
			
			<div class="col-md-5">
				<div class="pull-right">
					<a href="javascript:relateTruncate();"  class="btn btn-danger btn-sm">
						<span class="glyphicon glyphicon-ban-circle"></span> Clean
					</a>
					&nbsp;
					<div class="btn-group">
						<a href="#relateWordInsertModal" role="button" data-toggle="modal" class="btn btn-sm" rel="tooltip"><i class="icon-plus"></i></a>
						<a href="javascript:relatedeleteSelectWord()" class="btn btn-sm" rel="tooltip"><i class="icon-minus"></i></a>
						<a href="javascript:gorelateKeywordPage('', '${pageNo}');" class="btn btn-sm" rel="tooltip"><i class="icon-refresh"></i></a>
					</div>
					&nbsp;
					<a href="javascript:gorelateViewablePage('${pageNo}');"  class="btn btn-default btn-sm">
						<span class="glyphicon glyphicon-eye-open"></span> View
					</a>
				</div>
			</div>
		</div>
		
		<%
		if(entryList.size() > 0){
		%>
		<div class="col-md-12" style="overflow:auto">
		
			<table class="_table_relate table table-hover table-bordered table-checkable table-condensed">
				<thead>
					<tr>
						<th class="checkbox-column">
							<input type="checkbox">
						</th>
						<th>Keyword</th>
						<th>Value</th>
						<th>Action</th>
					</tr>
				</thead>
				<tbody>
				<%
				for(int i=0; i < entryList.size(); i++){
					 RelateKeywordVO relateKeyword = entryList.get(i);
				%>
					<tr id="_relate-<%=relateKeyword.getId() %>">
						<td class="checkbox-column">
							<input type="checkbox" class="edit">
							<input type="hidden" name="ID" value="<%=relateKeyword.getId() %>"/>
						</td>
						<td class="col-md-2">
							<input type="text" name="KEYWORD" value="<%=relateKeyword.getKeyword() %>" class="form-control"/>
						</td>
						<td><input type="text" name="VALUE" value="<%=relateKeyword.getValue() %>" class="form-control"/></td>
						<td class="col-md-2"><a href="javascript:relateWordUpdate(<%=relateKeyword.getId() %>);" class="btn btn-sm"><i class="glyphicon glyphicon-saved"></i></a>
						<a href="javascript:relatedeleteOneWord(<%=relateKeyword.getId() %>);" class="btn btn-sm"><i class="glyphicon glyphicon-remove"></i></a></td>
					</tr>
				<%
				}
				%>
				</tbody>
			</table>
		</div>
		<%
		}
		%>
		<div class="table-footer">
			<div class="col-md-12">
			Rows 
			<% if(entryList.size() > 0) { %>
			<%=start %> - <%=start + entryList.size() - 1 %> of <%=filteredSize %> <% if(filteredSize != totalSize) {%> (filtered from <%=totalSize %> total entries)<% } %>
			<% } else { %>
			Empty
			<% } %>
			
			<jsp:include page="../../inc/pagenation.jsp" >
			 	<jsp:param name="pageNo" value="${pageNo }"/>
			 	<jsp:param name="totalSize" value="<%=filteredSize %>" />
				<jsp:param name="pageSize" value="${pageSize }" />
				<jsp:param name="width" value="5" />
				<jsp:param name="callback" value="gorelateKeywordPage" />
				<jsp:param name="requestURI" value="" />
			 </jsp:include>
			</div>
		</div>	
	</div>
</div>
</div>

<div class="modal" id="relateWordInsertModal" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Relate Word Insert</h4>
			</div>
			<div class="modal-body">
				<div class="form-inline">
					<div class="form-group">
						<input type="text" id="word_input_relate" class="form-control" placeholder="Keyword">
					</div>
					<div class="form-group" style="width:370px">
						<div class="input-group" >
							<input type="text" id="value_input_relate" class="form-control" placeholder="Value">
							<span class="input-group-btn">
								<button class="btn btn-default" type="button" id="word_input_button_relate">Put</button>
							</span>
						</div>
					</div>
				</div>
				<label id="word_input_result_relate" for="word_input" class="help-block" style="word-wrap: break-word;"></label>
			</div>
			<div class="modal-footer">
				<form action="upload.html" method="POST" enctype="multipart/form-data" style="display: inline;">
					<input type="hidden" name="keywordId" value="relate"/>
					<span class="fileContainer btn btn-primary"><span class="icon icon-upload"></span> File Upload ...<input type="file" name="filename" id="relate_file_upload"></span>
				</form>
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		  	</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal-dialog -->
</div>
						