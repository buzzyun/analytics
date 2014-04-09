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
				boolean lcatCurrent = "user".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/settings/index.html"/>"><i class="icon-user"></i> User Setting</a>
			</li>
			
			<%
				lcatCurrent = "".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/settings/configuration.html"/>"><i class="icon-cog"></i> Engine Configuration</a>
			</li>
			
			<%
				lcatCurrent = "".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/settings/settings.html"/>"><i class="icon-list"></i> Site Settings</a>
			</li>
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
