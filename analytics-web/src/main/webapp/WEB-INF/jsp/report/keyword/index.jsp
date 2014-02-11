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
	
	loadToTab("list.html", null, "#tab_keyword_overview");
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
						<li class="current"> ${keywordId}</li>
					</ul>

				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>${keywordId }</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="tabbable tabbable-custom tabbable-full-width">
					<ul id="keyword_tab" class="nav nav-tabs">
						<li class="active"><a href="#tab_keyword_overview" data-toggle="tab">Overview</a></li>
					</ul>
					<div class="tab-content row">

						<!--=== Overview ===-->
						<div class="tab-pane active" id="tab_keyword_overview"></div>
						<div class="tab-pane" id="tab_keyword_relate"></div>
						<!-- //tab field -->
					</div>
					<!-- /.tab-content -->
				</div>
						
				<!-- /Page Content -->
			</div>
		</div>
	</div>
</body>
</html>