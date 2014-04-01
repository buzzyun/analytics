<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<%@ page import="org.fastcatgroup.analytics.db.vo.*" %>
<%@ page import="org.fastcatgroup.analytics.analysis.SearchStatisticsProperties" %>
<%
String categoryId = request.getParameter("categoryId");
List<SearchHitVO> list = (List<SearchHitVO>) request.getAttribute("list");
String keyword = request.getParameter("keyword");
String timeText = (String) request.getAttribute("timeText");
String timeViewType = (String) request.getAttribute("timeViewType");

if(keyword == null){
	keyword = "";
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

	<%
	if(list != null && list.size() > 0){
	%>
	// Sample Data
	var d1 = [
		<%
		for(int i=0;i<list.size();i++){
			SearchHitVO vo = list.get(i);
			if(i > 0){
			%>,<%
			}
		%>
		[ <%=i %>, <%=vo.getHit() %> ]
		<%
		}
		%>
	];
	
	var ticks = [
		<%
		for(int i=0;i<list.size();i++){
			SearchHitVO vo = list.get(i);
			if(i > 0){
			%>,<%
			}
		%>
		[ <%=i %>, '<%=vo.getTimeId() %>' ]
		<%
		}
		%>
	];

	
	var data = [ {
		data : d1,
		color : '#eb8544'
	}];

	$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins.getFlotDefaults(), 
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
	}));
	<%
	}
	%>
	
	
	var pickmenup_options = {
		calendars: 3,
		mode: 'range',
		format: 'Y.m.d',
		first_day: 1,
		position: 'right',
		hide_on_select	: false
	};
	$("#timeText").pickmeup(pickmenup_options);
	
	$("#timeViewTypeList button").on("click", function(){
		$(this).addClass("btn-primary");
		$(this).removeClass("btn-default");
		
		$(this).siblings().addClass("btn-default");
		$(this).siblings().removeClass("btn-primary");
		
		$("#timeViewTypeList input[name=timeViewType]").val($(this).text().charAt(0));
		
		//TODO 달력의 날짜를 확인하여, 주,월,년의 경우 시작/끝 날짜를 조정해준다.
		
		
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
					<form method="get" autocomplete="off">
						<div class="col-md-12 bottom-space">
							<div class="form-inline bottom-space">
								<select id="select_category" name="categoryId" class="select_flat fcol2"></select>
								<input class="form-control fcol2-1 " type="text" name="timeText" id="timeText" value="<%=timeText %>" >
							</div>
							
							<div id="timeViewTypeList" class="btn-group bottom-space">
								<button type="button" class="btn <%="H".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Hourly</button>
								<button type="button" class="btn <%="D".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Daily</button>
								<button type="button" class="btn <%="W".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Weekly</button>
								<button type="button" class="btn <%="M".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Monthly</button>
								<button type="button" class="btn <%="Y".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Yearly</button>
								<input type="hidden" name="timeViewType" value="<%=timeViewType %>">
							</div>
							
							
							<div class="form-inline">
								<input type="text" name="keyword" class="form-control fcol2" placeholder="Keyword" value="<%=keyword %>">
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
									<i class="icon-calendar"></i> Period : <%=timeText %>
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
