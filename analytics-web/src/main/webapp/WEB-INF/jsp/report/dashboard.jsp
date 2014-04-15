<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.List" %>
<%@page import="java.text.DecimalFormat" %>
<%@page import="org.fastcatgroup.analytics.db.vo.*" %>
<%@page import="org.fastcatgroup.analytics.analysis.SearchStatisticsProperties" %>
<%

List<SearchHitVO> currentWeek = (List<SearchHitVO>) request.getAttribute("currentWeekData");
List<SearchHitVO> lastWeek = (List<SearchHitVO>) request.getAttribute("lastWeekData");
String timeText = (String) request.getAttribute("timeText");
int totalCurrentWeek = 0;
int totalLastWeek = 0;

DecimalFormat format = new DecimalFormat("#,###");
%>
<c:set var="ROOT_PATH" value=".." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
	$(document).ready(function() {

			// Sample Data
			var d1 = [ 
				<% for(int inx=0;inx<currentWeek.size();inx++) { %>
					<% 
					SearchHitVO vo = currentWeek.get(inx);
					if(inx > 0) { 
					%>,<%
					}
					totalCurrentWeek += vo.getHit();
					%>
					[<%=inx%>, <%=vo.getHit() %>]
				<% } %>
			];
			var d2 = [ 
				<% for(int inx=0;inx<lastWeek.size();inx++) { %>
					<% 
					SearchHitVO vo = lastWeek.get(inx);
					if(inx > 0) { 
					%>,<%
					}
					totalLastWeek += vo.getHit();
					%>
					[<%=inx%>, <%=vo.getHit() %>]
				<% } %>
			];
			
			var data = [ {
				label : "Current",
				data : d1,
				color : '#eb8544'
			}, {
				label : "Previous",
				data : d2,
				color : '#487FF3'
			} ];

			$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins
				.getFlotDefaults(),
				{
					xaxis : {
						ticks : [ [0,"Mon"], [1,"Tue"], [2,"Wed"], 
							[3,"Thu"], [4,"Fri"], [5,"Sat"], [6,"Sun"] ]
					}, series : {
						lines : {
							fill : false,
							lineWidth : 1.5
						}, points : {
							show : true,
							radius : 2.5,
							lineWidth : 1.1
						}, grow : {
							active : true,
							growings : [ {
								stepMode : "maximum"
							} ]
						}
					}, grid : {
						hoverable : true,
						clickable : true
					}, tooltip : true,
					tooltipOpts : { content : '%s: %y' }
				}));

			
			var ctr1 = [ [ 1262304000000, 1300 ], [ 1264982400000, 700 ], [ 1267401600000, 1000 ],
				[ 1270080000000, 3500 ], [ 1272672000000, 2000 ],
				[ 1275350400000, 1500 ], [ 1277942400000, 1200 ] ];

			var ctr2 = [ [ 1262304000000, 700 ],[ 1264982400000, 400 ],[ 1267401600000, 600 ],
				[ 1270080000000, 2500 ], [ 1272672000000, 1300 ],
				[ 1275350400000, 700 ], [ 1277942400000, 600 ] ];
			
			var ctr3 = [ [ 1262304000000, 60 ],[ 1264982400000, 50 ],[ 1267401600000, 60 ],
							[ 1270080000000, 55 ], [ 1272672000000, 55 ],
							[ 1275350400000, 70 ], [ 1277942400000, 60 ] ];
			
			
			var ctr_data = [ {
				label : "검색PV",
				data : ctr1,
				color : '#eb8544'
			}, {
				label : "통합검색",
				data : ctr2,
				color : '#487FF3'
			}, {
				label : "유입률",
				data : ctr3,
				color : '#999',
				yaxis: 2
			} ];
			$.plot("#chart_dashboard_ctr", ctr_data, $.extend(true, {}, Plugins
					.getFlotDefaults(),
					{
						xaxis : {
							min : (new Date(2009, 12, 1)).getTime(),
							max : (new Date(2010, 6, 2)).getTime(),
							mode : "time",
							tickSize : [ 1, "month" ],
							monthNames : [ "Sun", "Mon", "Tue", "Wed", "Thu",
											"Fri", "Sat" ],
							tickLength : 0
						},
						yaxes: [
						  {
							  
						  },{
							position: "right"
						  }      
						],
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
			
			
			// 서비스별 Data
			<%
			List<SearchTypeHitVO>[] typeListArray = (List<SearchTypeHitVO>[])request.getAttribute("typeListArray");
			for(int inx=0;inx<typeListArray.length; inx++) {
				List<SearchTypeHitVO>typeList = typeListArray[inx];
				if(typeList.size() > 0) {
					String typeId = typeList.get(0).getTypeId();
					%>
					var <%=typeId%>_rate_data = [
						<%
						for(int typeInx=0;typeInx < typeList.size(); typeInx++) {
							SearchTypeHitVO vo = typeList.get(typeInx);
							%>
							<% if(typeInx > 0) { %>, <% } %>
							{ label:"<%=vo.getDtype()%>", data: <%=vo.getHit()%> }
							
						<%
						}
						%>
					];
					$.plot("#chart_<%=typeId%>_rate", <%=typeId%>_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
						series: {
							pie: {
								show: true,
								radius: 1,
								label: {
									show: true
								}
							}
						}, grid: {
							hoverable: true
						}, tooltip: true,
						tooltipOpts: {
							content: '%p.0%, %s', // show percentages, rounding to 2 decimal places
							shifts: { x: 20, y: 0 }
						}
					}));
					<%	
				}
			}
			%>
// 			// 정렬별 Data
// 			var login_rate_data = [];
// 			login_rate_data[0] = { label: "인기도순", data: 50 };
// 			login_rate_data[1] = { label: "정확도순", data: 30 };
// 			login_rate_data[1] = { label: "낮은가격순", data: 20 };
// 			$.plot("#chart_login_rate", login_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
// 				series: {
// 					pie: {
// 						show: true,
// 						radius: 1,
// 						label: {
// 							show: true
// 						}
// 					}
// 				},
// 				grid: {
// 					hoverable: true
// 				},
// 				tooltip: true,
// 				tooltipOpts: {
// 					content: '%p.0%, %s', // show percentages, rounding to 2 decimal places
// 					shifts: {
// 						x: 20,
// 						y: 0
// 					}
// 				}
// 			}));
// 			// 연령별 Data
// 			var age_rate_data = [];
// 			age_rate_data[0] = { label: "10대이전", data: 10 };
// 			age_rate_data[1] = { label: "20대", data: 25 };
// 			age_rate_data[2] = { label: "30대", data: 40 };
// 			age_rate_data[3] = { label: "40대", data: 15 };
// 			age_rate_data[3] = { label: "50대이후", data: 10 };
// 			$.plot("#chart_age_rate", age_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
// 				series: {
// 					pie: {
// 						show: true,
// 						radius: 1,
// 						label: {
// 							show: true
// 						}
// 					}
// 				},
// 				grid: {
// 					hoverable: true
// 				},
// 				tooltip: true,
// 				tooltipOpts: {
// 					content: '%p.0%, %s', // show percentages, rounding to 2 decimal places
// 					shifts: {
// 						x: 20,
// 						y: 0
// 					}
// 				}
// 			}));
		});
	
	
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp" >
			<c:param name="lcat" value="dashboard" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Dashboard</a></li>
					</ul>
					<ul class="crumb-buttons">
						<li class="range">
							<!-- <a href="#"> <i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
						</a> -->
						</li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Dashboard</h3>
						<span>Overview statistics</span>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : ${timeText}
								</h4>
							</div>
						</div>

					</div>
				</div>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Hit Progress</h4>
							</div>
							<div class="widget-content">
								<div id="chart_dashboard_main" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong><%=format.format(totalCurrentWeek) %></strong> <small>Total Count</small></li>
									<li class="light"><strong><%=format.format(totalLastWeek) %></strong> <small>Last
											Period</small></li>
									<%
									double rate = 0;
									if(totalCurrentWeek > 0) {
										rate = 10000.0 * (totalCurrentWeek - totalLastWeek) / totalLastWeek;
									}
									rate = Math.round(rate) / 100;
									
									String classStr = "text-primary";
									if(rate < 0) {
										classStr = "text-danger";
									}
									%>
									<li><strong class="<%=classStr%>"><%=rate > 0? "+" + rate:rate %>%</strong> <small>Change</small></li>
								</ul>
							</div>
						</div>
					</div>
					<!-- /.col-md-12 -->
				</div>
				
				<%
				List<RankKeywordVO> popularKeywordList = (List<RankKeywordVO>)request.getAttribute("popularKeywordList");
				List<RankKeywordVO> hotKeywordList = (List<RankKeywordVO>)request.getAttribute("hotKeywordList");
				List<RankKeywordVO> newKeywordList = (List<RankKeywordVO>)request.getAttribute("newKeywordList");
				%>
				<div class="row">
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>Popular Keyword TOP10</h4>
							</div>
							<div class="widget-content">
								<table class="table table-condensed">
									<tr>
										<th>#</th>
										<th>KEYWORD</th>
										<th>COUNT</th>
										<th>CHANGE</th>
									</tr>
									<% 
									for(int inx=0;inx < popularKeywordList.size(); inx++) { 
										RankKeywordVO vo = popularKeywordList.get(inx);
										int diff = vo.getCountDiff();
										String diffStr = "";
										if(diff > 0) {
											diffStr = "+"+diff;
										} else if(diff < 0) {
											diffStr = "-"+diff;
										}
									%>
									<tr>
										<td><%=inx + 1 %></td>
										<td><%=vo.getKeyword() %></td>
										<td><%=vo.getCount() %></td>
										<td><%=diffStr%></td>
									</tr>
									<% 
									} 
									%>
								</table>
							</div>
						</div>
					</div>
					
					<!--  -->
					
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>Hot Keyword TOP10</h4>
							</div>
							<div class="widget-content">
								<table class="table table-condensed">
									<tr>
										<th>#</th>
										<th>KEYWORD</th>
										<th>COUNT</th>
										<th>CHANGE</th>
									</tr>
									<% 
									for(int inx=0;inx < hotKeywordList.size(); inx++) { 
										RankKeywordVO vo = hotKeywordList.get(inx);
										int diff = vo.getCountDiff();
										String diffStr = "";
										if(diff > 0) {
											diffStr = "+"+diff;
										} else if(diff < 0) {
											diffStr = "-"+diff;
										}
									%>
									<tr>
										<td><%=inx + 1 %></td>
										<td><%=vo.getKeyword() %></td>
										<td><%=vo.getCount() %></td>
										<td><%=diffStr%></td>
									</tr>
									<% 
									} 
									%>
								</table>
							</div>
						</div>
					</div>					
					<!--  -->
					
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>New Keyword TOP10</h4>
							</div>
							<div class="widget-content">
								<table class="table table-condensed">
									<tr>
										<th>#</th>
										<th>KEYWORD</th>
										<th>COUNT</th>
										<th>CHANGE</th>
									</tr>
									<% 
									for(int inx=0;inx < newKeywordList.size(); inx++) { 
										RankKeywordVO vo = newKeywordList.get(inx);
										int diff = vo.getCountDiff();
										String diffStr = "";
										if(diff > 0) {
											diffStr = "+"+diff;
										} else if(diff < 0) {
											diffStr = "-"+diff;
										}
									%>
									<tr>
										<td><%=inx + 1 %></td>
										<td><%=vo.getKeyword() %></td>
										<td><%=vo.getCount() %></td>
										<td><%=diffStr%></td>
									</tr>
									<% 
									} 
									%>
								</table>
							</div>
						</div>
					</div>
				</div>
				<!-- - -->
				<div class="row">
				<% 
				for(int inx = 0;inx < typeListArray.length; inx++) {
					List<SearchTypeHitVO> typeList = typeListArray[inx];
					if(typeList.size() > 0) {
						String typeId = typeList.get(0).getTypeId();
					%>
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4><%=typeId %> Ratio</h4>
							</div>
							<div class="widget-content">
								<div id="chart_<%=typeId %>_rate" class="chart"></div>
							</div>
						</div>
					</div>
					<% 
					}
				}
				%>
				</div>
				
				<!--  -->
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Click Through Rate</h4>
							</div>
							<div class="widget-content">
								<div id="chart_dashboard_ctr" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong>172,055</strong> <small>검색PV</small></li>
									<li class="text-success"><strong>86,372</strong> <small>유입건</small></li>
									<li class="text-primary"><strong>50.20%</strong> <small>유입률</small></li>
								</ul>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li class="light"><strong>76,086</strong> <small>상품블로그</small></li>
									<li class="light"><strong>7,257</strong> <small>사러가기</small></li>
									<li class="light"><strong>3,029</strong> <small>상품리스트</small></li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>