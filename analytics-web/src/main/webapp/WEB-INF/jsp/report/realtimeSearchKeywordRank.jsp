<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ROOT_PATH" value=".." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="rank" />
			<c:param name="mcat" value="realtimeSearchKeyword" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">검색순위</a></li>
						<li><a href="#">검색어</a></li>
					</ul>
					<!-- <ul class="crumb-buttons">
						<li class="range">
							<a href="#"> <i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
						</a>
						</li>
					</ul> -->
				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title page-title-sm">
						<h3>실시간검색어순위</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" role="form">
							<select class="select_flat select_flat-sm">
								<option>:: SITE ::</option>
								<option>통합검색</option>
								<option>모바일</option>
							</select> 
							<select class="select_flat select_flat-sm fcol2">
								<option>:: CATEGORY ::</option>
								<option>PC</option>
								<option>가전</option>
							</select> 
						</form>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : 현재시각
								</h4>
							</div>
						</div>

					</div>
				</div>
				
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>TOP 10</h4>
							</div>
							<div class="widget-content">
								<div>
									<table class="table table-striped table-bordered table-condensed">
										<thead>
											<tr>
												<th>#</th>
												<th>KEYWORD</th>
												<th>COUNT</th>
												<th>RANK CHANGE</th>
												<th>COUNT CHANGE</th>
											</tr>
										</thead>
										<tbody>
											<%
											for(int i =0;i < 15; i++){
											%>
											<tr>
												<td><%=i+1 %></td>
												<td>노트북<%=i %></td>
												<td><%=1000+(50-i*3)%></td>
												<td>&lt;UP&gt; <%=(20-i)*7 %></td>
												<td>+100</td>
											</tr>
											<%
											}
											%>
										</tbody>
									</table>
								</div>
							
							</div>
						</div>
					
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
