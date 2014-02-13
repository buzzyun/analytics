<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*"%>
<%
	JSONArray keywordList = (JSONArray) request.getAttribute("list");
%>


<c:set var="ROOT_PATH" value="../.." scope="request" />
<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />

<script>
$(document).ready(function(){
	//load keyword tab contents
	$('#keyword_tab a').on('show.bs.tab', function (e) {
		var targetId = e.target.hash;
		
		if(targetId == "#tab_keyword_overview"){
			loadToTab("overview.html", null, targetId);
		}else{
			var aObj = $(e.target);
			var keywordId = aObj.attr("_id");
			loadKeywordTab(keywordId, keywordId, 1, null, null, false, false, targetId);
		}
	});
	
	loadToTab("list.html", {targetId:"#tab_keyword_overview"}, "#tab_keyword_overview");
});

</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />
	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="report" />
			<c:param name="mcat" value="relateKeyword" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> Manager</li>
						<li class="current"> Keyword</li>
						<li class="current"> ${keywordType}</li>
					</ul>

				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>${keywordType }</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div id="tab_keyword_overview"></div>
						
				<!-- /Page Content -->
			</div>
		</div>
	</div>
</body>
</html>