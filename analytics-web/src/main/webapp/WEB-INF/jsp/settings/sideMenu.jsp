<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*"%>

<%
	String lcat = request.getParameter("lcat");
	String mcat = request.getParameter("mcat");
%>


<div id="sidebar" class="sidebar-fixed">
	<div id="sidebar-content">
		<!--=== Navigation ===-->
		<ul id="nav">
			<%
				boolean lcatCurrent = "user".equals(mcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/settings/index.html"/>"><i class="icon-user"></i> User Setting</a>
			</li>
			
			<%
				lcatCurrent = "configuration".equals(mcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/settings/system.html"/>"><i class="icon-cog"></i> System Configuration</a>
			</li>
			
			<%
				lcatCurrent = "settings".equals(mcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/settings/sites.html"/>"><i class="icon-list"></i> Sites Settings</a>
			</li>
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
