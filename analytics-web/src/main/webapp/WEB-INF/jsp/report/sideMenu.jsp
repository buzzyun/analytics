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
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="<c:url value="/${siteId}/report/dashboard.html"/>"> <i
					class="icon-dashboard"></i> Dashboard 
			</a>
			</li>
			
			<%
				lcatCurrent = "keywordProgress".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="<c:url value="/${siteId}/report/progress/searchKeyword.html"/>">
							<i class="icon-bar-chart"></i> Hit Progress
					</a>
				<%-- <ul class="sub-menu">
					<li class="<%=(lcatCurrent && "hitCount".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/progress/hitCount.html"/>">
							<i class="icon-angle-right"></i> 전체
					</a></li> 
					<li class="<%=(lcatCurrent && "searchKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/progress/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> 검색어별
					</a></li> 
				</ul> --%>
			</li>
			
			<%
				lcatCurrent = "rank".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> Keyword Rank 
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "realtimeSearchKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/realtimeSearchKeyword.html"/>">
							<i class="icon-angle-right"></i> Realtime Rank
					</a></li>
					<li class="<%=(lcatCurrent && "all".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/searchKeywordAll.html"/>">
							<i class="icon-angle-right"></i> All Keyword
					</a></li> 
					<li class="<%=(lcatCurrent && "new".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/searchKeywordNew.html"/>">
							<i class="icon-angle-right"></i> New Keyword
					</a></li>
					<li class="<%=(lcatCurrent && "hot".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/searchKeywordHot.html"/>">
							<i class="icon-angle-right"></i> Hot Keyword
					</a></li> 
					<li class="<%=(lcatCurrent && "down".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/searchKeywordDown.html"/>">
							<i class="icon-angle-right"></i> Down Keyword
					</a></li>
					<%-- <li class="<%=(lcatCurrent && "emptyKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/searchKeywordNo.html"/>">
							<i class="icon-angle-right"></i> No Result
					</a></li> --%>
				</ul>
			</li>
			
			
			<%
				lcatCurrent = "typeProgress".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a
				href="javascript:void(0);"> <i class="icon-bar-chart"></i>
					Type Ratio
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "category".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> Category
					</a></li>
					<li class="<%=(lcatCurrent && "page".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> Page Number
					</a></li> 
					<li class="<%=(lcatCurrent && "sort".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> Sort
					</a></li> 
					<li class="<%=(lcatCurrent && "age".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> Age
					</a></li> 
					<li class="<%=(lcatCurrent && "service".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewService.html"/>">
							<i class="icon-angle-right"></i> Service
					</a></li> 
					<li class="<%=(lcatCurrent && "login".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> Login
					</a></li> 
					<li class="<%=(lcatCurrent && "sex".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> Gender
					</a></li>
				</ul>
			</li>
			
			<%
				lcatCurrent = "ctr".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a
				href="javascript:void(0);"> <i class="icon-bar-chart"></i>
					CTR
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "overview".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/view.html"/>">
							<i class="icon-angle-right"></i> All
					</a></li> 
					<li class="<%=(lcatCurrent && "keyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/keyword.html"/>">
							<i class="icon-angle-right"></i> Keyword
					</a></li> 
					<li class="<%=(lcatCurrent && "keyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/ctr/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> [detail..]
					</a></li> 
				</ul>
			</li>
			
			<%
				lcatCurrent = "report".equals(lcat);
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
