<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ROOT_PATH" value=".." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
$(document).ready(function(){

	// Sample Data
	var data_max_time = [];
	var data_avg_time = [];

	// Random data for "Server load"
	for (var x = 0; x < 30; x++) {
		var y = Math.floor( 100 + Math.random() * 30 );
		data_max_time.push([x, y]);
		y = Math.floor( 10 + Math.random() * 30 );
		data_avg_time.push([x, y]);
	}

	

	var series_multiple = [
		{
			label: "Average Time",
			data: data_avg_time,
			color: App.getLayoutColorCode('green'),
			lines: {
				fill: true
			},
			points: {
				show: false
			}
		},{
			label: "Max Time",
			data: data_max_time,
			color: App.getLayoutColorCode('red')
		}
	];

	// Initialize flot
	var plot = $.plot("#chart_response_time", series_multiple, $.extend(true, {}, Plugins.getFlotDefaults(), {
		series: {
			lines: { show: true },
			points: { show: true },
			grow: { active: true }
		},
		grid: {
			hoverable: true,
			clickable: true
		},
		tooltip: true,
		tooltipOpts: {
			content: '%s: %y'
		}
	}));

});
	
	
</script>

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
						<li><a href="#">검색추이</a></li>
						<li><a href="#">응답시간</a></li>
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
						<h3>응답시간</h3>
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
							
							<input type="button" class="btn btn-sm btn-primary" value="Submit">
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
								<h4>Total Count</h4>
							</div>
							<div class="widget-content">
								<div id="chart_response_time" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
							
								<table class="table table-striped table-bordered table-hover">
									<thead>
										<tr>
											<th>TIME</th>
											<th>MAX</th>
											<th>AVERAGE</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>2013.10.01</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.02</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.03</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.04</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.05</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.06</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.07</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.08</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.09</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.10</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.11</td>
											<td>2000ms</td>
											<td>300ms</td>
										</tr>
										<tr>
											<td>2013.10.12</td>
											<td>2000ms</td>
											<td>300ms</td>
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