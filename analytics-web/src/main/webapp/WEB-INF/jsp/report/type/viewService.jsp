<%@page import="java.util.Random"%>
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
	
	//서비스별 Data
	var service_rate_data = [];
	service_rate_data[0] = { label: "통합검색", data: 50 };
	service_rate_data[1] = { label: "인기검색어", data: 25 };
	service_rate_data[2] = { label: "자동완성", data: 20 };
	service_rate_data[3] = { label: "추천어", data: 5 };
	$.plot("#chart_service_rate", service_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
		series: {
			pie: {
				show: true,
				radius: 1,
				label: {
					show: true
				}
			}
		},
		grid: {
			hoverable: true
		},
		tooltip: true,
		tooltipOpts: {
			content: '%p.0%, %s', // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			}
		}
	}));

});


</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="typeProgress" />
			<c:param name="mcat" value="page" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">유형별검색추이</a></li>
						<li><a href="#">서비스별검색추이</a></li>
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
						<h3>서비스별검색추이</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" role="form">
							<select id="select_category" class="select_flat select_flat-sm fcol2"></select>
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
								<h4>서비스사용비율</h4>
							</div>
							<div class="widget-content">
								<div id="chart_service_rate" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<div>
									<table class="table table-striped table-bordered table-condensed">
										<thead>
											<tr>
												<th>TIME</th>
												<th>통합검색</th>
												<th>인기검색어</th>
												<th>자동완성</th>
												<th>추천어</th>
											</tr>
										</thead>
										<tbody>
											<%
											int at = 0;
											int bt = 0;
											int ct = 0;
											int dt = 0;
											for(int i =1;i <= 15; i++){
												Random r = new Random();
												int a = r.nextInt(5000) + 100;
												int b = r.nextInt(5000) + 100;
												int c = r.nextInt(5000) + 100;
												int d = r.nextInt(5000) + 100;
												at += a;
												bt += b;
												ct += c;
												dt += d;
											%>
											<tr>
												<td>2013.10.<%=i < 10 ? "0" + i : "" + i %></td>
												<td><%=a %></td>
												<td><%=b %></td>
												<td><%=c %></td>
												<td><%=d %></td>
											</tr>
											<%
											}
											%>
											<tr>
												<td>Summary</td>
												<td><%=at %></td>
												<td><%=bt %></td>
												<td><%=ct %></td>
												<td><%=dt %></td>
											</tr>
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
