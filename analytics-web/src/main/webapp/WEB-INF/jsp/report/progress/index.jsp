<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<%@ page import="org.fastcatgroup.analytics.db.vo.*" %>
<%
String categoryId = request.getParameter("categoryId");
List<SearchHitVO> list = (List<SearchHitVO>) request.getAttribute("list");
String timeFrom = request.getParameter("timeFrom");
String timeTo = request.getParameter("timeTo");
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
			var d1 = [ [ "d1", 2000 ], [ "d2", 500 ],
					[ "d3", 1700 ], [ "d4", 1300 ],
					[ "d5", 2600 ], [ "d6", 1300 ]
					];

			var data = [ {
				label : "노트북",
				data : d1,
				color : '#eb8544'
			}];

			$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins
				.getFlotDefaults(),
				{
					xaxis : {
						/* min : (new Date(2009, 12, 1)).getTime(),
						max : (new Date(2010, 11, 2)).getTime(), */
						//mode : "time",
						//tickSize : [ 1, "month" ],
						monthNames : [ "1", "2", "3", "4",
								"5", "6", "7", "8", "9",
								"10", "11", "12" ],
						//tickLength : 0
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
			<c:param name="lcat" value="hitProgress" />
			<c:param name="mcat" value="all" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Hit Progress</a></li>
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
						<h3>Hit Progress</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					<form method="get">
						<div class="col-md-12 bottom-space">
							<div class="form-inline">
								<select id="select_category" class="select_flat select_flat-sm fcol2"></select>
								<input type="button" class="btn btn-sm btn-warning" value="DAY"> 
								<input type="button" class="btn btn-sm btn-default" value="WEEK">
								<input type="button" class="btn btn-sm btn-default" value="MONTH">
								<input type="button" class="btn btn-sm btn-default" value="YEAR">
								<select name="timeType" class="select_flat select_flat-sm fcol1">
									<option value="D">Day</option>
									<option value="W">Week</option>
									<option value="M">Month</option>
									<option value="Y">Year</option>
								</select>
								<input class="form-control fcol1-2 " size="16" type="text" name="timeFrom" value="<%=timeFrom %>" >
								- <input class="form-control fcol1-2 " size="16" type="text" name="timeTo" value="<%=timeTo %>" >
							</div>
						</div>
						<div class="col-md-12">
							<div class="form-inline">
								<input type="submit" class="btn btn-primary" value="Submit">
							</div>
						</div>
					</form>
				</div>
				
				
				<%
				
				if(list != null){
					if(list.size() > 0){
				%>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : <%=timeFrom %> - <%=timeTo %>
								</h4>
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
										<%
											for(int i = 0;i < list.size(); i++){
												SearchHitVO vo = list.get(i);
										%>
										<tr>
											<td><%=vo.getTimeId() %></td>
											<td><%=vo.getHit() %></td>
										</tr>
										<%
											}
										%>
									</tbody>
								</table>
								
							</div>
						</div>
					</div>
					<!-- /.col-md-12 -->
				</div>
				<%
					}else{
						%>
						<div class="row">
							<div class="col-md-12">
							No data
							</div>
						</div>
						<%
					}
				}
				%>
					
				
			</div>
		</div>
	</div>
</body>
</html>