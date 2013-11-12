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
					class="icon-edit"></i> Dashboard 
			</a>
			</li>
			<li class=""><a href="javascript:void(0);"> <i
					class="icon-edit"></i> 검색추이 <span
					class="label label-info pull-right">3</span>
			</a>
				<ul class="sub-menu">
					<li class=""><a
						href="<c:url value="/report/dictionary/"/>/index.html">
							<i class="icon-angle-right"></i> Keyword
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/dictionary/"/>/index.html">
							<i class="icon-angle-right"></i> My Keyword
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/dictionary/"/>/index.html">
							<i class="icon-angle-right"></i> Hot Keyword
					</a></li> 
				</ul>
			</li>
			
			<li class=""><a
				href="javascript:void(0);"> <i class="icon-desktop"></i>
					검색비율 <span class="label label-info pull-right">3</span>
			</a>
				<ul class="sub-menu">
					<li class=""><a
						href="<c:url value="/report/dictionary/"/>/index.html">
							<i class="icon-angle-right"></i> 정렬별
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/dictionary/"/>/index.html">
							<i class="icon-angle-right"></i> 연령별
					</a></li> 
					<li class=""><a
						href="<c:url value="/report/dictionary/"/>/index.html">
							<i class="icon-angle-right"></i> 서비스별
					</a></li> 
				</ul>
			</li>
		</ul>

	</div>
	<div id="divider" class="resizeable_del"></div>
</div>
<!-- /Sidebar -->
