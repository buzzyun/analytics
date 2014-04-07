<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.fastcatgroup.analytics.analysis.vo.*"%>
<%@page import="org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.*"%>
<%@page import="org.fastcatgroup.analytics.analysis.SearchStatisticsProperties" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.*"%>
<%
List<SiteCategoryConfig> siteCategoryList = (List<SiteCategoryConfig>) request.getAttribute("siteCategoryList");
List<RankKeyword> rankList = (List<RankKeyword>) request.getAttribute("rankList");
String categoryId = request.getParameter("categoryId");
String date = (String) request.getAttribute("date");

%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
$(document).ready(function(){
	
	var pickmeupOptions = {
		calendars: 3,
		format: "Y.m.d",
		first_day: 1,
		position: "bottom",
		hide_on_select	: false,
		change : function(date) {
		}, cloneDate:function(date) {
		}, timeViewType:"D"
	};
	$("input[name=date]").pickmeup(pickmeupOptions);
});
</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/configuration/sideMenu.jsp">
			<c:param name="lcat" value="management" />
			<c:param name="mcat" value="run" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Configuration</a></li>
						<li><a href="#">Management</a></li>
						<li><a href="#">Run Statistics</a></li>
					</ul>
					<!-- <ul class="crumb-buttons">
						<li class="range">
							<a href="#"> <i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
						</a>
						</li>
					</ul> -->
				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title page-title-sm">
						<h3>Run Statistics</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" method="get" >
							<select name="taskType" class="select_flat select_flat-sm fcol2-1">
								<option value="searchStatictics">Search Statistics</option>
								<option value="relateKeyword">Relate Keyword</option>
								<option value="realtimeKeyword">Realtime Popular Keyword</option>
								<!-- <option value="W">Week</option>
								<option value="M">Month</option>
								<option value="Y">Year</option> -->
							</select>
							<input class="form-control fcol1-2 " size="16" type="text" name="date" autocomplete="off" value="<%=date %>" >
							<input type="submit" class="btn btn-sm btn-primary" value="Run">
						</form>
					</div>
				</div>
				
				
			</div>
		</div>
	</div>
</body>
</html>
