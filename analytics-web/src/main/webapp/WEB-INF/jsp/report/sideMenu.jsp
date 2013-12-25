<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*"%>

<%
	/* String collectionId = (String) request.getAttribute("collectionId");
	JSONArray collectionList = (JSONArray) request.getAttribute("collectionList");
	JSONArray analysisPluginList = (JSONArray) request.getAttribute("analysisPluginList");
	*/
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
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="<c:url value="/report/dashboard.html"/>"> <i
					class="icon-dashboard"></i> Dashboard 
			</a>
			</li>
			<%
				lcatCurrent = "rank".equals(lcat);
			%>
			<li class="<%=lcatCurrent ? "current" :"" %>"><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> 검색순위 
			</a>
				<ul class="sub-menu">
					<li class="<%=(lcatCurrent && "searchKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/searchKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 검색어순위
					</a></li> 
					<li class="<%=(lcatCurrent && "myKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/myKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 관심어순위
					</a></li> 
					<li class="<%=(lcatCurrent && "newKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/newKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 신규 검색어 순위
					</a></li>
					<li class="<%=(lcatCurrent && "hotKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/hotKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 급상승 검색어 순위
					</a></li> 
					<li class="<%=(lcatCurrent && "coldKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/hotKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 급하강 검색어 순위
					</a></li>
					<li class="<%=(lcatCurrent && "emptyKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/hotKeywordRank.html"/>">
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
						href="<c:url value="/report/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> 검색어
					</a></li> 
					<li class="<%=(lcatCurrent && "myKeyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/myKeyword.html"/>">
							<i class="icon-angle-right"></i> 관심어
					</a></li> 
					<li class="<%=(lcatCurrent && "hitCount".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/hitCount.html"/>">
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
						href="<c:url value="/report/typeViewCategory.html"/>">
							<i class="icon-angle-right"></i> 카테고리별
					</a></li>
					<li class="<%=(lcatCurrent && "page".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 페이지번호별
					</a></li> 
					<li class="<%=(lcatCurrent && "sort".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 정렬별
					</a></li> 
					<li class="<%=(lcatCurrent && "age".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 연령별
					</a></li> 
					<li class="<%=(lcatCurrent && "service".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 서비스별
					</a></li> 
					<li class="<%=(lcatCurrent && "login".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 로그인별
					</a></li> 
					<li class="<%=(lcatCurrent && "sex".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/typeView.html"/>">
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
						href="<c:url value="/report/ctr.html"/>">
							<i class="icon-angle-right"></i> 전체
					</a></li> 
					<li class="<%=(lcatCurrent && "keyword".equals(mcat)) ? "current" : "" %>"><a
						href="<c:url value="/report/ctrKeyword.html"/>">
							<i class="icon-angle-right"></i> 키워드
					</a></li> 
				</ul>
			</li>
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
