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
			boolean  lcatCurrent = "report".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> 검색어관리
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "relateKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/keyword/relate/index.html"/>">
							<i class="icon-angle-right"></i> 연관검색어
					</a></li> 
				</ul>
			</li>
			<%
				lcatCurrent = "rank".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> 검색순위 
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "realtimeSearchKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/realtimeSearchKeyword.html"/>">
							<i class="icon-angle-right"></i> 실시간검색어순위
					</a></li>
				</ul>
			</li>
			<%
				lcatCurrent = "ctr".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a
				href="javascript:void(0);"> <i class="icon-bar-chart"></i>
					클릭유입률
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "overview".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/view.html"/>">
							<i class="icon-angle-right"></i> 전체
					</a></li> 
					<li class="<%=(lcatCurrent && "keyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/keyword.html"/>">
							<i class="icon-angle-right"></i> 키워드
					</a></li> 
					<li class="<%=(lcatCurrent && "keyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> 검색키워드
					</a></li> 
				</ul>
			</li>
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
