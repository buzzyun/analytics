<%@page import="org.fastcatsearch.analytics.db.vo.RelateKeywordVO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="org.json.*"%>
<%
	String keywordId = (String) request.getAttribute("keywordId");
	List<RelateKeywordVO> entryList = (List<RelateKeywordVO>)request.getAttribute("entryList");
	int totalSize = (Integer) request.getAttribute("totalSize");
	int filteredSize = (Integer) request.getAttribute("filteredSize");
	
	int start = (Integer) request.getAttribute("start");
	String targetId = (String) request.getAttribute("targetId");
	
	String siteId = request.getParameter("siteId");
%>
<script>

var searchInputObj;
var searchColumnObj;
var exactMatchObj;

$(document).ready(function(){
	
	searchInputObj = $("#search_input_relate");
	searchColumnObj = $("#relateSearchColumn");
	exactMatchObj = $("#relateExactMatch");

	searchInputObj.keydown(function (e) {
		if(e.keyCode == 13){
			var keyword = toSafeString($(this).val());
			loadKeywordTab('relate', 1, keyword, searchColumnObj.val(), exactMatchObj.is(":checked"), false, '<%=targetId%>');
			return;
		}
	});
	searchInputObj.focus();
	
	searchColumnObj.on("change", function(){
		var keyword = toSafeString(searchInputObj.val());
		if(keyword != ""){
			loadKeywordTab('relate', 1, keyword, searchColumnObj.val(), exactMatchObj.is(":checked"), false, '<%=targetId%>');
		}
	});
	exactMatchObj.on("change", function(){
		var keyword = toSafeString(searchInputObj.val());
		if(keyword != ""){
			loadKeywordTab('relate', 1, keyword, searchColumnObj.val(), exactMatchObj.is(":checked"), false, '<%=targetId%>');
		}
	});
	
});

function gorelateKeywordPage(uri, pageNo){
	loadKeywordTab('relate', pageNo, '${keyword}', searchColumnObj.val(), exactMatchObj.is(":checked"), false, '<%=targetId%>');	
}
function gorelateEditablePage(pageNo){
	loadKeywordTab('relate', pageNo, '${keyword}', searchColumnObj.val(), exactMatchObj.is(":checked"), true, '<%=targetId%>');	
}

function gorelateApplyPage(pageNo){
	$.ajax({
		url:'apply.html',
		type:"POST",
		data:{ siteId: '${siteId}' },
		dataType:"json",
		success:function(response) {
			if(response.success){
				noty({text: "Apply Success", type: "success", layout:"topRight", timeout: 1000});
			}else{
				noty({text: "Apply Fail", type: "error", layout:"topRight", timeout: 2000});
			} },
		fail:function(response){
			noty({text: "Apply Fail", type: "error", layout:"topRight", timeout: 2000});
		} 
	});
}
</script>

<div class="col-md-12">
<div class="widget box">
	<div class="widget-content no-padding">
		<div class="dataTables_header clearfix">
		
			<div class="form-inline col-md-7">
			
				<div class="form-group " style="width:240px">
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
					<a href="javascript:downloadKeyword('relate')"  class="btn btn-default btn-sm">
						<span class="icon icon-download"></span> Download
					</a>
					&nbsp;
					<div class="btn-group">
						<a href="javascript:gorelateKeywordPage('', '${pageNo}');" class="btn btn-sm" rel="tooltip"><i class="icon-refresh"></i></a>
					</div>
					&nbsp;
					<a href="javascript:gorelateEditablePage('${pageNo}');"  class="btn btn-default btn-sm">
						<span class="glyphicon glyphicon-edit"></span> Edit
					</a>
					&nbsp;
					<a href="javascript:gorelateApplyPage('${pageNo}');"  class="btn btn-default btn-sm">
						<span class="glyphicon glyphicon-ok"></span> Apply
					</a>
				</div>
			</div>
				
		</div>
		
		<%
		if(entryList.size() > 0){
		%>
		<div class="col-md-12" style="overflow:auto">
		
			<table class="table table-hover table-bordered">
				<thead>
					<tr>
						<th>Keyword</th>
						<th>Value</th>
					</tr>
				</thead>
				<tbody>
					
				<%
				for(int i=0; i < entryList.size(); i++){
					RelateKeywordVO relateKeyword = entryList.get(i);
				%>
					<tr>
						<td class="col-md-2"><%=relateKeyword.getKeyword() %></td>
						<td>
						<%=relateKeyword.getValue() %>
						</td>
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
			<%=start + 1 %> - <%=start + entryList.size() %> of <%=filteredSize %> <% if(filteredSize != totalSize) {%> (filtered from <%=totalSize %> total entries)<% } %>
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