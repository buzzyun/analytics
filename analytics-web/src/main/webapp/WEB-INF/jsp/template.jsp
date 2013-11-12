<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="inc/common.jsp" />
<html>
<head>
<c:import url="inc/header.jsp" />
</head>
<body>
<c:import url="inc/mainMenu.jsp" />
<div id="container">
	<c:import url="inc/sideMenu.jsp" />
	<div id="content">
	<div class="container">
		<!-- Breadcrumbs line -->
		<div class="crumbs">
			<ul id="breadcrumbs" class="breadcrumb">
				<li><i class="icon-home"></i> <a href="javascript:void(0);">User</a>
				</li>
				<li class="current"><a href="javascript:void(0);" title="">Analysis</a>
				</li>
				<li class="current"><a href="javascript:void(0);" title="">Korean</a>
				</li>
			</ul>

		</div>
		<!-- /Breadcrumbs line -->

		<!--=== Page Header ===-->
		<div class="page-header">
			<div class="page-title">
				<h3>Analysis Plugin</h3>
			</div>
		</div>
		<!-- /Page Header -->
	</div>
</div>
</div>
</body>
</html>