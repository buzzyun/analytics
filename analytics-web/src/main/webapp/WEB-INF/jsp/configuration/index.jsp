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
	<c:import url="${ROOT_PATH}/configuration/sideMenu.jsp" />
	<div id="content">
	<div class="container">
		<!-- Breadcrumbs line -->
		<div class="crumbs">
			<ul id="breadcrumbs" class="breadcrumb">
				<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a>
				</li>
			</ul>

		</div>
		<!-- /Breadcrumbs line -->

		<!--=== Page Header ===-->
		<div class="page-header">
			<div class="page-title">
				<h3>Configuration</h3>
			</div>
		</div>
		<!-- /Page Header -->
	</div>
</div>
</div>
</body>
</html>