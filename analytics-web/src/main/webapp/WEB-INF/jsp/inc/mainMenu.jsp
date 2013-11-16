<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
$(document).ready(function(){
	/* $('#indexing_tab a[href!="#tab_indexing_run"]').click(function() {
		stopPollingIndexTaskState();
		console.log("stop polling ${collectionId}");
	});
	$('#indexing_tab a[href="#tab_indexing_run"]').click(function() {
		startPollingIndexTaskState('${collectionId}');
		console.log("start polling ${collectionId}");
	}); */
	
	$('#running_tasks_dropdown').on('show.bs.dropdown', function () {
		startPollingAllTaskStateForTaskBar();
	});
	$('#running_tasks_dropdown').on('hide.bs.dropdown', function () {
		stopPollingAllTaskStateForTaskBar();
	});
});
</script>
<!-- Header -->
<header class="header navbar navbar-fixed-top" role="banner">
	<!-- Top Navigation Bar -->
	<div class="container">

		<!-- Only visible on smartphones, menu toggle -->
		<!-- <ul class="nav navbar-nav">
			<li class="nav-toggle"><a href="javascript:void();" title=""><i
					class="icon-reorder"></i></a></li>
		</ul> -->

		<!-- Logo -->
		<a class="navbar-brand" href="<c:url value="/start.html" />"> <strong>Fastcat</strong> Analytics
		</a>
		<!-- /logo -->

		<!-- Sidebar Toggler -->
		<!-- <a href="#" class="toggle-sidebar bs-tooltip" data-placement="bottom"
				data-original-title="Toggle navigation"> <i class="icon-reorder"></i>
			</a> -->
		<!-- /Sidebar Toggler -->

		<!-- Top Left Menu -->
		<ul class="nav navbar-nav navbar-left">
				<li class="active"><a href="<c:url value="/report/index.html"/>"> Report </a></li>
				<li><a href="<c:url value="/configuration/index.html"/>"> Configuration </a></li>
			</ul>
		<!-- /Top Left Menu -->


		<!-- Top Right Menu -->
		<ul class="nav navbar-nav navbar-right">
			<!-- .row .row-bg Toggler -->
			<li><a href="<c:url value="/account/index.html"/>" class="dropdown-toggle"> <i class="icon-cog"></i>
			</a></li>

			<!-- User Login Dropdown -->
			<li class="dropdown user"><a href="#" class="dropdown-toggle"
				data-toggle="dropdown"> <!--<img alt="" src="assets/img/avatar1_small.jpg" />-->
					<i class="icon-male"></i> <span class="username">Sang Song</span> <i
					class="icon-caret-down small"></i>
			</a>
				<ul class="dropdown-menu">
					<li><a href="<c:url value="/main/profile.html"/>"><i class="icon-user"></i>
							My Profile</a></li>
					<li><a href="<c:url value="/main/logout.html" />"><i class="icon-key"></i> Log Out</a></li>
				</ul>
			</li>
			<!-- /user login dropdown -->
		</ul>
		<!-- /Top Right Menu -->
	</div>
	<!-- /top navigation bar -->

</header>
<!-- /.header -->


