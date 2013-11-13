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
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp" />
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
					
					<div class="col-md-12 bottom-space">
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
							<input type="button" class="btn btn-sm btn-warning" value="DAY"> 
							<input type="button" class="btn btn-sm btn-default" value="WEEK">
							<input
								type="button" class="btn btn-sm btn-default" value="MONTH">
							<input type="button" class="btn btn-sm btn-default" value="YEAR">
							
							<button class="btn btn-sm range">
								<i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
							</button>
							
						</form>
					</div>
					<div class="col-md-12">
						<form class="form-inline" role="form">
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
								<!-- <div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs"><i class="icos-word-document"></i></span>
									</div>
								</div> -->
							</div>
						</div>

					</div>
				</div>
				
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>클릭유입률</h4>
							</div>
							<div class="widget-content">
									<table class="table table-bordered table-condensed table-hovered">
									<thead>
										<tr>
											<th>검색PV</th>
											<th>유입건</th>
											<th>유입률</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>172,055</td>
											<td>86,372</td>
											<td>50.20%</td>
										</tr>
									</tbody>
									</table>
								
									<table class="table table-bordered table-condensed table-hovered">
									<thead>
										<tr>
											<th>상품블로그</th>
											<th>사러가기</th>
											<th>상품리스트</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>76,086</td>
											<td>7,257</td>
											<td>3,029</td>
										</tr>
									</tbody>
									</table>
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
											<td>7,688</td>
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
											<td>9,234</td>
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
