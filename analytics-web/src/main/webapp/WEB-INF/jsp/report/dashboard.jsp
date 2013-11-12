<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ROOT_PATH" value=".."/>

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
</head>
<body>
<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

<div id="container">
	<c:import url="${ROOT_PATH}/report/sideMenu.jsp" />
	<div id="content">
	<div class="container">
		<!-- Breadcrumbs line -->
		<div class="crumbs">
			<ul id="breadcrumbs" class="breadcrumb">
				<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
				<li><a href="#">Dashboard</a></li>
			</ul>
			<ul class="crumb-buttons">
				<li class="range"><a href="#">
					<i class="icon-calendar"></i>
					<span></span>
					<i class="icon-angle-down"></i>
				</a></li>
			</ul>
		</div>
		<!-- /Breadcrumbs line -->

		<!--=== Page Header ===-->
		<div class="page-header">
			<div class="page-title">
				<h3>Dashboard</h3>
			</div>
		</div>
		<!-- /Page Header -->
	</div>
</div>
</div>
</body>
</html>