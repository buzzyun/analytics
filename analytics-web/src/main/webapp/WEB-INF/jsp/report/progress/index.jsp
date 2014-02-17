<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<%@ page import="org.fastcatgroup.analytics.db.vo.*" %>
<%@ page import="org.fastcatgroup.analytics.analysis.SearchStatisticsProperties" %>
<%
String categoryId = request.getParameter("categoryId");
List<SearchHitVO> list = (List<SearchHitVO>) request.getAttribute("list");

if(list != null && list.size() > 0){
	Calendar timeStart = SearchStatisticsProperties.parseTimeId(list.get(0).getTimeId());
	Calendar timeFinish = SearchStatisticsProperties.parseTimeId(list.get(list.size()-1).getTimeId());
	Calendar timeCurrent = null;
	SearchHitVO vo = null;
	
	
	for(int timeInx=0;timeStart.getTimeInMillis() <= timeFinish.getTimeInMillis(); timeInx++){
		String timeId = SearchStatisticsProperties.getTimeId(timeStart, Calendar.DATE);
		int hit = 0;
		
		if(timeCurrent == null) {
			vo = list.get(timeInx);
			timeCurrent = SearchStatisticsProperties.parseTimeId(vo.getTimeId()); 
		}
		
		if(timeCurrent != null) {
			timeStart.add(Calendar.DATE, 1);
			long timeStartMillis = timeStart.getTimeInMillis();
			long timeCurrentMillis = timeCurrent.getTimeInMillis();
			if(timeStartMillis == timeCurrentMillis) {
				hit = vo.getHit();
				timeCurrent = null;
			} else {
				SearchHitVO newVO = new SearchHitVO();
				newVO.setTimeId(timeId);
				newVO.setHit(hit);
				if(timeStartMillis < timeCurrentMillis) {
					//목적일보다 작으므로 앞에 더해준다.
					list.add(timeInx, newVO);
				} else {
					//목적일보타 크므로 뒤에 더해준다.
					list.add(timeInx+1, newVO);
				}
			}
		}
	}
}

String timeFrom = request.getParameter("timeFrom");
String timeTo = request.getParameter("timeTo");
if(timeFrom == null){
	timeFrom = "";
}
if(timeTo == null){
	timeTo = "";
}
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
		var d1 = [
			<%
			if(list != null){
				for(int i=0;i<list.size();i++){
					SearchHitVO vo = list.get(i);
					if(i > 0){
					%>,<%
					}
				%>
				[ <%=i %>, <%=vo.getHit() %> ]
				<%
				}
			}
			%>
		];
		
		var ticks = [
			<%
			if(list != null){
				for(int i=0;i<list.size();i++){
					SearchHitVO vo = list.get(i);
					if(i > 0){
					%>,<%
					}
				%>
				[ <%=i %>, '<%=vo.getTimeId() %>' ]
				<%
				}
			}
			%>
		];

		var data = [ {
			data : d1,
			color : '#eb8544'
		}];

		$.plot("#chart_dashboard_main", data,
			{
				xaxis: {
					ticks :ticks
				},
				yaxis: {
					ticks: 20,
					min: 0,
				},
				series : {
					lines : {
						show: true,
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
			});
			
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
								<select id="select_category" name="categoryId" class="select_flat select_flat-sm fcol2"></select>
								<!-- <input type="button" class="btn btn-sm btn-warning" value="DAY"> 
								<input type="button" class="btn btn-sm btn-default" value="WEEK">
								<input type="button" class="btn btn-sm btn-default" value="MONTH">
								<input type="button" class="btn btn-sm btn-default" value="YEAR"> -->
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