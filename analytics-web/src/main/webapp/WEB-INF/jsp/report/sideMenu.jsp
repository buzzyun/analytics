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
				lcatCurrent = "report".equals(lcat);
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
					<li class="<%=(lcatCurrent && "searchKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> 검색어순위
					</a></li> 
					<li class="<%=(lcatCurrent && "myKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/myKeyword.html"/>">
							<i class="icon-angle-right"></i> 관심어순위
					</a></li> 
					<li class="<%=(lcatCurrent && "newKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/newKeyword.html"/>">
							<i class="icon-angle-right"></i> 신규 검색어 순위
					</a></li>
					<li class="<%=(lcatCurrent && "hotKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/hotKeyword.html"/>">
							<i class="icon-angle-right"></i> 급상승 검색어 순위
					</a></li> 
					<li class="<%=(lcatCurrent && "coldKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/hotKeyword.html"/>">
							<i class="icon-angle-right"></i> 급하강 검색어 순위
					</a></li>
					<li class="<%=(lcatCurrent && "emptyKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/rank/hotKeyword.html"/>">
							<i class="icon-angle-right"></i> 결과부족 검색어 순위
					</a></li>
				</ul>
			</li>
			
			<%
				lcatCurrent = "keywordProgress".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> 검색추이 
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "searchKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/progress/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> 검색어
					</a></li> 
					<li class="<%=(lcatCurrent && "myKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/progress/myKeyword.html"/>">
							<i class="icon-angle-right"></i> 관심어
					</a></li> 
					<li class="<%=(lcatCurrent && "hitCount".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/progress/hitCount.html"/>">
							<i class="icon-angle-right"></i> 검색횟수
					</a></li> 
				</ul>
			</li>
			<%
				lcatCurrent = "typeProgress".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a
				href="javascript:void(0);"> <i class="icon-bar-chart"></i>
					유형별검색추이
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "category".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> 카테고리별
					</a></li>
					<li class="<%=(lcatCurrent && "page".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> 페이지번호별
					</a></li> 
					<li class="<%=(lcatCurrent && "sort".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> 정렬별
					</a></li> 
					<li class="<%=(lcatCurrent && "age".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> 연령별
					</a></li> 
					<li class="<%=(lcatCurrent && "service".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewService.html"/>">
							<i class="icon-angle-right"></i> 서비스별
					</a></li> 
					<li class="<%=(lcatCurrent && "login".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> 로그인별
					</a></li> 
					<li class="<%=(lcatCurrent && "sex".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/${siteId}/report/type/viewCategory.html"/>">
							<i class="icon-angle-right"></i> 성별
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
