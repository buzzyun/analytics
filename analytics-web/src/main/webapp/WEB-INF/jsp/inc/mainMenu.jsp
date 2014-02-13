<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<script>
$(document).ready(function(){
	
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
		<a href="#" class="toggle-sidebar bs-tooltip" data-placement="bottom"
				data-original-title="Toggle navigation"> <i class="icon-reorder"></i>
			</a> 
		<!-- /Sidebar Toggler -->

		<!-- Top Left Menu -->
		<ul class="nav navbar-nav navbar-left">
			<li class="active"><a href="<c:url value="/${siteId}/report/index.html"/>"> Report </a></li>
			<li><a href="<c:url value="/${siteId}/configuration/index.html"/>"> Configuration </a></li>
		</ul>
		<!-- /Top Left Menu -->


		<!-- Top Right Menu -->
		<ul class="nav navbar-nav navbar-right">
		
			<li class="dropdown user"><a href="#" class="dropdown-toggle"
				data-toggle="dropdown"> <!--<img alt="" src="assets/img/avatar1_small.jpg" />-->
					<i class="icon-globe"></i> <span class="username">${_siteName}</span> <i
					class="icon-caret-down small"></i>
			</a>
				<ul class="dropdown-menu">
					<%
					List<String[]> siteList = (List<String[]>) request.getAttribute("_siteList");
					if(siteList != null){
						for(String[] siteIdName : siteList){
						%>
						<li><a href="<c:url value="/"/>/<%=siteIdName[0] %>/report/index.html"><i class="icon-globe"></i><%=siteIdName[1] %></a></li>
						<%
						}
					}
					%>
				</ul>
			</li>
			
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


