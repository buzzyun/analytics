<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ROOT_PATH" value=".." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />

<script>
	
$(document).ready(
		function() {
	// Sample Data
	var d11 = [];
	for (var i = 0; i < 10; i++){
		d11.push([i, parseInt((10-i)*100) + Math.random() * 100]);
	}

	var ds1 = new Array();

	ds1.push({
		label: "Current",
		data: d11,
		bars: {
			show: true,
			barWidth: 0.2,
			order: 1,
			align:'center'
		}
	});

	// Initialize Chart
	$.plot("#chart_keyword_rank1", ds1, $.extend(true, {}, Plugins.getFlotDefaults(), {
		legend: {
			show: false
		},
		series: {
			lines: { show: false },
			points: { show: false },
			bars: {
				fillColor: { colors: [ { opacity: 1 }, { opacity: 0.7 } ] },
				
			}
		},
		xaxis: {ticks: [[0,'노트북'],[1,'CPU'],[2,'메모리'],[3,'마우스'],[4,'울트라롱핸드폰']
				,[5,'모바일'],[6,'마우스패드'],[7,'울트라북'],[8,'청바지'],[9,'핸드폰케이스']]},
		grid:{
			hoverable: true
		},
		tooltip: true,
		tooltipOpts: {
			content: '%y'
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
						<li><a href="#">검색순위</a></li>
						<li><a href="#">신규검색어</a></li>
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
						<h3>신규검색어순위</h3>
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
								<h4>TOP 10</h4>
							</div>
							<div class="widget-content">
								<div id="chart_keyword_rank1" class="chart"></div>
							</div>
							<div class="divider"></div>
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
												<td>노트북</td>
												<td><%=i*1000+ i*7 + (i*100 % 13) %></td>
												<td>+1</td>
												<td>+<%=(30 - i) * 10 %></td>
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
