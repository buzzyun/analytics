<%@page import="java.util.Random"%>
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
			<c:param name="lcat" value="ctr" />
			<c:param name="mcat" value="keyword" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">클릭유입률</a></li>
						<li><a href="#">키워드</a></li>
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
						<h3>키워드 클릭유입률</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" role="form">
							
							<button class="btn range">
								<i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
							</button>
							<input type="text" class="form-control fcol3" placeholder="Keyword..">
							<input type="button" class="btn btn-primary" value="Submit">
						</form>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : 2013.10.10 - 2013.10.17
								</h4>
							</div>
						</div>

					</div>
				</div>
				
				
				<div class="row">
					<div class="col-md-2">
						<div class="widget box">
							<div class="widget-header">
								<h4>검색횟수</h4>
							</div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong>1,520,596</strong> <small>검색횟수</small></li>
								</ul>
							</div>
						</div>
					</div>
					<div class="col-md-5">
						<div class="widget box">
							<div class="widget-header">
								<h4>통합검색 유입률</h4>
							</div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong>172,055</strong> <small>검색PV</small></li>
									<li><strong>86,372</strong> <small>유입건</small></li>
									<li><strong>50.20%</strong> <small>유입률</small></li>
								</ul>
							</div>
						</div>
					</div>
					<div class="col-md-5">
						<div class="widget box">
							<div class="widget-header">
								<h4>통합검색 유입건</h4>
							</div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong>76,086</strong> <small>상품블로그</small></li>
									<li><strong>7,257</strong> <small>사러가기</small></li>
									<li><strong>3,029</strong> <small>상품리스트</small></li>
								</ul>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>CRT키워드</h4>
							</div>
							<div class="widget-content">
							
								<table class="table table-bordered table-condensed table-hovered">
									<thead>
										<tr>
											<th>#</th>
											<th>키워드</th>
											<th>검색횟수</th>
											<th>총유입건</th>
											<th>유입률</th>
											<th>블로그</th>
											<th>사러가기</th>
											<th>리스트</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>1</td>
											<td>온수매트</td>
											<td>18,543</td>
											<td>13,688</td>
											<td>41.42</td>
											<td>7,651</td>
											<td>37</td>
											<td>0</td>
										</tr>
										<tr>
											<td>2</td>
											<td>노트북</td>
											<td>48,344</td>
											<td>9,788</td>
											<td>32.32</td>
											<td>8,651</td>
											<td>12</td>
											<td>0</td>
										</tr>
									<%
										for(int i =2;i< 15; i++){
									%>
										<tr>
											<td><%=i+1 %></td>
											<td>마우스</td>
											<td>7,234</td>
											<td>3,234</td>
											<td>23.23</td>
											<td>2,654</td>
											<td>32</td>
											<td>0</td>
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
</body>
</html>
