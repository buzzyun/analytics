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
			
			<li class="<%="siteSetting".equals(mcat) ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/configuration/settings/siteSetting.html"/>">
					<i class="icon-cog"></i> Site Settings
				</a>
			</li>
			<li class="<%="categorySetting".equals(mcat) ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/configuration/settings/categorySetting.html"/>">
					<i class="icon-cog"></i> Category Settings
				</a>
			</li>
			<li class="<%="attributeSetting".equals(mcat) ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/configuration/settings/attributeSetting.html"/>">
					<i class="icon-cog"></i> Attribute Settings
				</a>
			</li>
			<li class="<%="run".equals(mcat) ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/configuration/management/run.html"/>">
					<i class="icon-play"></i> Run Statistics
				</a>
			</li>
			<li class="<%="advanceRun".equals(mcat) ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/configuration/management/advanceRun.html"/>">
					<i class="icon-play"></i> Advance Run Statistics
				</a>
			</li>
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
