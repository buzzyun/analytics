<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String categoryId = request.getParameter("categoryId");
%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
	$(document).ready(function() {
		
		fillCategoryList('${siteId}', $("#select_category"), '<%=categoryId %>');

			// Sample Data
			var d1 = [ [ 1262304000000, 2000 ], [ 1264982400000, 500 ],
					[ 1267401600000, 1700 ], [ 1270080000000, 1300 ],
					[ 1272672000000, 2600 ], [ 1275350400000, 1300 ],
					[ 1277942400000, 1700 ], [ 1280620800000, 1300 ],
					[ 1283299200000, 2500 ], [ 1285891200000, 2000 ],
					[ 1288569600000, 1500 ], [ 1291161600000, 1200 ] ];

			var data = [ {
				label : "노트북",
				data : d1,
				color : '#eb8544'
			}];

			$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins
				.getFlotDefaults(),
				{
					xaxis : {
						min : (new Date(2009, 12, 1)).getTime(),
						max : (new Date(2010, 11, 2)).getTime(),
						mode : "time",
						tickSize : [ 1, "month" ],
						monthNames : [ "1", "2", "3", "4",
								"5", "6", "7", "8", "9",
								"10", "11", "12" ],
						tickLength : 0
					},
					series : {
						lines : {
							fill : false,
							lineWidth : 1.5
						},
						points : {
							show : true,
							radius : 2.5,
							lineWidth : 1.1
						},
						grow : {
							active : true,
							growings : [ {
								stepMode : "maximum"
							} ]
						}
					},
					grid : {
						hoverable : true,
						clickable : true
					},
					tooltip : true,
					tooltipOpts : {
						content : '%s: %y'
					}
				}));
			
		});
	
	
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="keywordProgress" />
			<c:param name="mcat" value="hitCount" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">검색추이</a></li>
						<li><a href="#">검색횟수</a></li>
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
						<h3>검색횟수</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" role="form">
							<select id="select_category" class="select_flat select_flat-sm fcol2"></select>
							<input type="button" class="btn btn-sm btn-warning" value="TIME">  
							<input type="button" class="btn btn-sm btn-default" value="DAY"> 
							<input type="button" class="btn btn-sm btn-default" value="WEEK">
							<input
								type="button" class="btn btn-sm btn-default" value="MONTH">
							<input type="button" class="btn btn-sm btn-default" value="YEAR">
							
							<button class="btn btn-sm range">
								<i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
							</button>
							
							<input type="button" class="btn btn-sm btn-primary" value="Submit">
						</form>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : 2013.10.10
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
								<h4>Total Count</h4>
							</div>
							<div class="widget-content">
								<div id="chart_dashboard_main" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
							
								<table class="table table-striped table-bordered table-hover">
									<thead>
										<tr>
											<th>Time</th>
											<th>Count</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>2013.10.01</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.02</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.03</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.04</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.05</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.06</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.07</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.08</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.09</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.10</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.11</td>
											<td>1,000</td>
										</tr>
										<tr>
											<td>2013.10.12</td>
											<td>1,000</td>
										</tr>
									</tbody>
								</table>
								
							</div>
						</div>
					</div>
					<!-- /.col-md-12 -->
				</div>
			
					
				
			</div>
		</div>
	</div>
</body>
</html>