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
				boolean lcatCurrent = "dashboard".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/report/dashboard.html"/>"><i class="icon-dashboard"></i> Dashboard</a>
			</li>
			
			<%
				lcatCurrent = "searchProgress".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/report/progress/hitCount.html"/>"><i class="icon-bar-chart"></i> Search Progress</a>
			</li>
			
			<%
				lcatCurrent = "rank".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="javascript:void(0);"><i class="icon-bar-chart"></i> Keyword Rank</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "realtimeSearchKeyword".equals(mcat)) ? "current" : "" %>">
						<a href="<c:url value="/${siteId}/report/rank/realtimeSearchKeyword.html"/>">
							<i class="icon-angle-right"></i> Realtime Keyword
						</a>
					</li>
					<li class="<%=(lcatCurrent && "searchKeyword".equals(mcat)) ? "current" : "" %>">
						<a href="<c:url value="/${siteId}/report/rank/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> Search Keyword
						</a>
					</li> 
				</ul>
			</li>
			
			
			<%
				lcatCurrent = "typeProgress".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>">
				<a href="<c:url value="/${siteId}/report/type/index.html"/>"> <i class="icon-bar-chart"></i>Type Ratio</a>
			</li>
			
			<%
				lcatCurrent = "ctr".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a
				href="javascript:void(0);"> <i class="icon-bar-chart"></i>Click-through rate</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "overview".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/view.html"/>">
							<i class="icon-angle-right"></i> Overview
					</a></li> 
					<li class="<%=(lcatCurrent && "detail".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/detail.html"/>">
							<i class="icon-angle-right"></i> Detail
					</a></li> 
					<li class="<%=(lcatCurrent && "keyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/keyword.html"/>">
							<i class="icon-angle-right"></i> Keyword
					</a></li> 
				</ul>
			</li>
			
			<%
				lcatCurrent = "keywords".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> Keywords
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "relateKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/keyword/relate/index.html"/>">
							<i class="icon-angle-right"></i> Relate Keyword
					</a></li> 
				</ul>
			</li>
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
