<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.util.*" %>
<%@page import="java.text.DecimalFormat" %>
<%@page import="org.fastcatsearch.analytics.db.vo.*" %>
<%@page import="org.fastcatsearch.analytics.analysis.StatisticsUtils" %>
<%@page import="org.fastcatsearch.analytics.util.ListableCounter"%>
<%@page import="org.fastcatsearch.analytics.analysis.config.StatisticsSettings.*"%>
<%

List<SearchHitVO> currentWeek = (List<SearchHitVO>) request.getAttribute("currentWeekData");
List<SearchHitVO> lastWeek = (List<SearchHitVO>) request.getAttribute("lastWeekData");

List<ClickTypeSetting> clickTypeList = (List<ClickTypeSetting>) request.getAttribute("clickTypeList");
List<Integer> searchPvList = (List<Integer>) request.getAttribute("searchPvList");
Map<String, ListableCounter> clickHitList = (Map<String,ListableCounter>) request.getAttribute("clickHitList");
List<String> labelList = (List<String>) request.getAttribute("labelList");

List<SearchTypeHitVO>[] typeHitListArray = (List<SearchTypeHitVO>[])request.getAttribute("typeHitListArray");
List<TypeSetting> primeTypeList  = (List<TypeSetting>)request.getAttribute("primeTypeList");

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
			
			var data = [ 
			{
				label : "Previous",
				data : d2,
				color : '#487FF3'
			},
			{
				label : "Current",
				data : d1,
				color : '#eb8544'
			}
			];

			$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins
				.getFlotDefaults(),
				{
					xaxis : {
						ticks : [ [0,"Mon"], [1,"Tue"], [2,"Wed"], 
							[3,"Thu"], [4,"Fri"], [5,"Sat"], [6,"Sun"] ]
					}, series : {
						lines : {
							fill : true,
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
			
		var ctr1 = [];
		var ctr2 = [];
		var ctr3 = [];
		var ticks=[];
			
		<%	
		int totalPv = 0;
		int totalClickHit = 0;
		float totalClickRate = 0f;
		for(int i=0;i<searchPvList.size(); i++){
			Integer pv = searchPvList.get(i);
			if(pv==null) { pv = 0; }
			int clickHit = 0;
			for(ClickTypeSetting clickType : clickTypeList) {
				Integer value = clickHitList.get(clickType.getId()).list().get(i);
				if(value!=null) {
					clickHit+=value;
				}
			}
			float clickThroughRate=0f;
			if(pv>0) {
				clickThroughRate = Math.round(clickHit * 10000f / pv) / 100f;
			}
			totalPv += pv;
			totalClickHit += clickHit;
			%>
			ctr1[<%=i%>]=[<%=i%>,<%=pv%>];
			ctr2[<%=i%>]=[<%=i%>,<%=clickHit%>];
			ctr3[<%=i%>]=[<%=i%>,<%=clickThroughRate%>];
			ticks[<%=i%>]=[<%=i%>,<%=labelList.get(i)%>];
			<%
		}
		if(totalPv > 0) {
			totalClickRate = Math.round(totalClickHit * 10000f / totalPv) / 100f;
		}
		%>
			var ctr_data = [ {
				label : "Search PV",
				data : ctr1,
				color : "#000",
				lines: { show: true },
				points:{ show:true }
			}, {
				label : "Click-through Count",
				data : ctr2,
				color : "#468847",
				lines: { show: true },
				points:{ show:true }
			}, {
				label : "Click-through Rate",
				data : ctr3,
				color : "rgba(66,139,202,0.3)",
				lines: { show: false},
				bars: {
					show: true,
					barWidth: 0.2 ,
					align:"center"
				},
				points:{ show:true },
				yaxis: 2
			} ];
			
			function yFormatter(v, axis) {
				return Math.ceil(v) + " %";
			}
			
			$.plot("#chart_dashboard_ctr", ctr_data, $.extend(true, {}, Plugins
				.getFlotDefaults(), {
					xaxis : {
						min : 0,
						tickSize : [ 1, "month" ],
						ticks : ticks,
						tickLength : 0
					}, yaxes: [
					  { min:"0" },{ min:"0", position: "right", tickFormatter: yFormatter }
					], grid : {
						hoverable : true,
						clickable : true
					}, tooltip : true,
					tooltipOpts : { content : '%s: %y' },
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
					}
				})
			);
			// 서비스별 Data
			<%
			//TODO 
			for(int inx = 0; inx < primeTypeList.size(); inx++) {
				TypeSetting typeSetting = primeTypeList.get(inx);
				String typeId = typeSetting.getId();
				String typeName = typeSetting.getName();
				
				List<SearchTypeHitVO> typeHitList = typeHitListArray[inx];
				%>
				var <%=typeId%>_rate_data = [
					<%
					for(int typeInx=0;typeInx < typeHitList.size(); typeInx++) {
						SearchTypeHitVO vo = typeHitList.get(typeInx);
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
			%>
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
						<li class="range"></li>
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
									<li><strong><%=format.format(totalCurrentWeek) %></strong> <small>Search PV</small></li>
									<li class="light"><strong><%=format.format(totalLastWeek) %></strong> <small>Last
											Period Search PV</small></li>
									<%
									double rate = 0;
									if(totalLastWeek > 0) {
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
				for(int inx = 0; inx < primeTypeList.size(); inx++) {
					TypeSetting typeSetting = primeTypeList.get(inx);
					String typeId = typeSetting.getId();
					String typeName = typeSetting.getName();
					%>
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4><%=typeName %></h4>
							</div>
							<div class="widget-content">
								<div id="chart_<%=typeId %>_rate" class="chart"></div>
							</div>
						</div>
					</div>	
				<%
				}
				%>
				</div>
				
				<!--  -->
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Click-through Rate</h4>
							</div>
							<div class="widget-content">
								<div id="chart_dashboard_ctr" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong><%=format.format(totalPv) %></strong> <small>Search PV</small></li>
									<li class="text-success"><strong><%=format.format(totalClickHit) %></strong> <small>Click-through Count</small></li>
									<li class="text-primary"><strong><%=totalClickRate%>%</strong> <small>Click-through Rate</small></li>
								</ul>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<%
									for(ClickTypeSetting clickType : clickTypeList) {
									%>
									<li class="light"><strong><%=format.format(clickHitList.get(clickType.getId()).value()) %></strong> <small><%=clickType.getName() %></small></li>
									<%
									}
									%>
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