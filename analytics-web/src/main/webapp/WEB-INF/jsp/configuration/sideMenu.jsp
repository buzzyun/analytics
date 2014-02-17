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
			boolean  lcatCurrent = "management".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> Management
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "run".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/configuration/management/run.html"/>">
							<i class="icon-angle-right"></i> Run Statistics 
					</a></li> 
				</ul>
			</li>
			
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
