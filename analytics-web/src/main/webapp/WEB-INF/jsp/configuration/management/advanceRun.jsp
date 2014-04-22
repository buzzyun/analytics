<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.fastcatgroup.analytics.analysis.vo.*"%>
<%@page import="org.fastcatgroup.analytics.analysis.StatisticsUtils" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.*"%>
<%
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
		mode: "range",
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
			<c:param name="mcat" value="advanceRun" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Configuration</a></li>
						<li><a href="#">Management</a></li>
						<li><a href="#">Advance Run Statistics</a></li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title page-title-sm">
						<h3>Advance Run Statistics</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row ">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>Search Statistics</h4>
							</div>
							<div class="widget-content no-padding">
								<form class="form-inline" method="post" >
									<input class="form-control fcol2-1 " size="16" type="text" name="date" autocomplete="off" value="<%=date %>" >
									<input type="submit" class="btn btn-primary" value="Run">
								</form>
							</div>
						</div>
					</div>
				</div>
				<div class="alert alert-warning">
					This doesn't make week, month and year statistics on daily tasks, but only make them at the end of the period. 
				</div>
			</div>
		</div>
	</div>
</body>
</html>
