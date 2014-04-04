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
	
	var avgTime = [
  		<%
  		for(int i=0;i<list.size();i++){
  			SearchHitVO vo = list.get(i);
  			if(i > 0){
  			%>,<%
  			}
  		%>
  		[ <%=i %>, <%=vo.getAverageTime() %> ]
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

	
	var data = [
		{data : d1, color : '#eb8544', label: 'Hit count'},
		{data : avgTime, color : 'blue', yaxis: 2, label: 'Response time'}
	];

	function yFormatter(v, axis) {
		return v + " ms";
	}
	
	$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins.getFlotDefaults(), 
		{
			xaxis: {
				ticks :ticks
			},
			/* yaxis: {
				ticks: 20,
				min: 0,
			}, */
			yaxes: [ { min: 0 }, {
				alignTicksWithAxis: 1,
				position: 'right',
				min: 0,
				ticks: 1,
				tickFormatter: yFormatter
			} ],
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
		mode: "single",
		format: "Y.m.d",
		first_day: 1,
		position: "bottom",
		hide_on_select	: false,
		change : function(date) {
			console.log(date);
			console.log(date.length);
// 			var dateStr1 = date[0];
// 			var dateStr2 = date[1];
// 			var dateSrc1 = date[0].split(".");
// 			var dateSrc2 = date[1].split(".");
// 			var dateObj1 = new Date(dateSrc1[0], dateSrc1[1] - 1, dateSrc1[2]);
// 			var dateObj2 = new Date(dateSrc2[0], dateSrc2[1] - 1, dateSrc2[2]);
			
// 			var timeViewType = $(this).data("pickmeup-options").timeViewType;
			
			
// 			var tmp = $(this).pickmeup("get_date");
// 			console.log("TMP:"+tmp[0].getFullYear()+"-"+(tmp[0].getMonth()+1)+"-"+tmp[0].getDate());
			
// 			if(timeViewType == "D") {
// 				console.log(date[0]+":"+date[1]);
// 			} else if(timeViewType == "W") {
// 				console.log(date[0]+":"+date[1]);
// 				dateObj1.setDate( dateObj1.getDate() - ( ( dateObj1.getDay() + 6 ) % 7 ) );
// 				dateObj2 = new Date(dateObj1.getFullYear(), dateObj1.getMonth(), dateObj1.getDate());
// 				dateObj2.setDate( dateObj2.getDate() + 6 );
// 				dateStr1 = dateObj1.getFullYear()+"."+(dateObj1.getMonth()+1)+"."+dateObj1.getDate();
// 				dateStr2 = dateObj2.getFullYear()+"."+(dateObj2.getMonth()+1)+"."+dateObj2.getDate();
// 			} else if(timeViewType == "M") {
// 				console.log(date[0]+":"+date[1]);
// 			} else if(timeViewType == "Y") {
// 				console.log(date[0]+":"+date[1]);
// 			}
			
// 			console.log("PICKUP : "+dateStr1+" ~ "+dateStr2);
			
// 			$(this).pickmeup("set_date",new Array(dateStr1,dateStr2));
			
			
			
// 			//console.log(date[0]+"-"+date[1]);
// 			//var datechar = date[0].split(".");
// 			//console.log(datechar[0]+"-"+datechar[1]+"-"+datechar[2]);
// 			//var dateobj = new Date(datechar[0], datechar[1] - 1, datechar[2]);//Date.parse(datechar[0]+"-"+datechar[1]+"-"+datechar[2]);
// 			//console.log(dateobj.getDay());
// 			//var dates = date.split(",");
// 			//$(this).pickmeup("set_date",new Array("2014.04.10","2014.05.10"));
// 			//$("#timeText").pickmeup("set_date","2014.04.10,2014.05.10");
		}//, 
		//timeViewType:"D"
		
	};
	$("#timeText").pickmeup(pickmenup_options);
	
	$("#timeViewTypeList button").on("click", function(){
		$(this).addClass("btn-primary");
		$(this).removeClass("btn-default");
		
		$(this).siblings().addClass("btn-default");
		$(this).siblings().removeClass("btn-primary");
		
		var timeViewType = $(this).text().charAt(0);
		$("#timeViewTypeList input[name=timeViewType]").val(timeViewType);
		$("#timeText").data('pickmeup-options').timeViewType = timeViewType;
		
		//TODO 달력의 날짜를 확인하여, 주,월,년의 경우 시작/끝 날짜를 조정해준다.
	});
	
});
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="searchProgress" />
			<c:param name="mcat" value="hitCount" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Search Progress</a></li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title page-title-sm">
						<h3>Search Progress</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					<form method="get" autocomplete="off">
						<div class="col-md-12">
							<div class="form-inline">
								<select id="select_category" name="categoryId" class="select_flat fcol2"></select>
								<input class="form-control fcol2-1 " type="text" name="timeText" id="timeText" value="<%=timeText %>" >
								<div id="timeViewTypeList" class="btn-group">
									<button type="button" class="btn <%="H".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Hourly</button>
									<button type="button" class="btn <%="D".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Daily</button>
									<button type="button" class="btn <%="W".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Weekly</button>
									<button type="button" class="btn <%="M".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Monthly</button>
									<button type="button" class="btn <%="Y".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Yearly</button>
									<input type="hidden" name="timeViewType" value="<%=timeViewType %>">
								</div>
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
