<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*"%>

<%
	/* String collectionId = (String) request.getAttribute("collectionId");
	JSONArray collectionList = (JSONArray) request.getAttribute("collectionList");
	JSONArray analysisPluginList = (JSONArray) request.getAttribute("analysisPluginList");
	String lcat = request.getParameter("lcat");
	String mcat = request.getParameter("mcat");
	String scat = request.getParameter("scat"); */
%>


<div id="sidebar" class="sidebar-fixed">
	<div id="sidebar-content">
		<form class="sidebar-search">
			<div class="input-box">
				<button type="submit" class="submit">
					<i class="icon-search"></i>
				</button>
				<span>
					<input type="text" placeholder="Search...">
				</span>
			</div>
		</form>
		<!--=== Navigation ===-->
		<ul id="nav">
			<li class=""><a href="<c:url value="/report/dashboard.html"/>"> <i
					class="icon-dashboard"></i> Dashboard 
			</a>
			</li>
			<li class=""><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> 검색순위 
			</a>
				<ul class="sub-menu">
					<li class=""><a
						href="<c:url value="/report/searchKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 검색어순위
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/myKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 관심어순위
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/hotKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 급상승 검색어 순위
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/hotKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 급하강 검색어 순위
					</a></li>
					<li class=""><a
						href="<c:url value="/report/hotKeywordRank.html"/>">
							<i class="icon-angle-right"></i> 결과부족 검색어 순위
					</a></li>
				</ul>
			</li>
			<li class=""><a href="javascript:void(0);"> <i
					class="icon-bar-chart"></i> 검색추이 
			</a>
				<ul class="sub-menu">
					<li class=""><a
						href="<c:url value="/report/searchKeyword.html"/>">
							<i class="icon-angle-right"></i> 검색어
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/myKeyword.html"/>">
							<i class="icon-angle-right"></i> 관심어
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/hitCount.html"/>">
							<i class="icon-angle-right"></i> 검색횟수
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/responseTime.html"/>">
							<i class="icon-angle-right"></i> 응답시간
					</a></li> 
				</ul>
			</li>
			
			<li class=""><a
				href="javascript:void(0);"> <i class="icon-bar-chart"></i>
					유형별검색추이
			</a>
				<ul class="sub-menu">
					<li class=""><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 정렬별
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 연령별
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 서비스별
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 로그인별
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/typeView.html"/>">
							<i class="icon-angle-right"></i> 성별
					</a></li>
				</ul>
			</li>
			
			<li class=""><a
				href="javascript:void(0);"> <i class="icon-bar-chart"></i>
					클릭유입률
			</a>
				<ul class="sub-menu">
					<li class=""><a
						href="<c:url value="/report/ctr.html"/>">
							<i class="icon-angle-right"></i> 전체
					</a></li> 
					<li class=""><a
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
