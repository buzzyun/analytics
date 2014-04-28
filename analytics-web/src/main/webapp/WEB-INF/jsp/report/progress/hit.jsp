<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="org.fastcatsearch.analytics.db.vo.*" %>
<%@ page import="org.fastcatsearch.analytics.analysis.StatisticsUtils" %>
<%
String categoryId = request.getParameter("categoryId");
List<SearchHitVO> list = (List<SearchHitVO>) request.getAttribute("list");
String keyword = request.getParameter("keyword");
String timeText = (String) request.getAttribute("timeText");
String timeViewType = (String) request.getAttribute("timeViewType");

if(keyword == null){
	keyword = "";
}
DecimalFormat format = new DecimalFormat("#,###");
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
	int delta = list.size() / 10;
	if(delta == 0) { delta = 1; }
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
  		[ <%=i %>, <%=vo.getAvgTime() %> ]
  		<%
  		}
  		%>
  	];
	
	var ticks = [
		<%
		//tick 의 갯수는 항상 10 개 이하로 유지하도록 한다.
		for(int i=0;i<list.size();i++){
			SearchHitVO vo = list.get(i);
			if(i > 0){
			%>,<%
			}
		%>
		[ <%=i %>, '<%=(i%delta==0)?vo.getTimeId():"" %>' ]
		<%
		}
		%>
	];

	
	var data = [
		{data : d1, color : '#eb8544', label: 'Hit count'},
		{data : avgTime, color : 'rgba(66,139,202,0.3)', yaxis: 2, label: 'Response time', lines: { show: false}, bars: {
			show: true,
			barWidth: 0.2 ,
			align:"center"
		}
		}
	];

	function yFormatter(v, axis) {
		return Math.ceil(v) + " ms";
	}
	
	$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins.getFlotDefaults(), 
		{
			xaxis: { ticks :ticks },
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
	
	
	var pickmeupOptions = {
		calendars: 3,
		mode: "range",
		format: "Y.m.d",
		first_day: 1,
		position: "bottom",
		hide_on_select	: true,
		change : function(date) {
			var options = $(this).data("pickmeup-options");
			var timeViewType = options.timeViewType;
			var dateStr1 = "";
			var dateStr2 = "";
			
			if(timeViewType == "H") {
				date = /([0-9]{4}[.][0-9]{2}[.][0-9]{2})/.exec(date)[0];
				dateStr1 = dateStr2 = date;
			} else {
				dateStr1 = date[0];
				dateStr2 = date[1];
			}
			var dateObj1 = options.parseDate(dateStr1);
			var dateObj2 = options.parseDate(dateStr2);
			
			var prevDate = $(this).attr("prev-date");
			
			if(timeViewType == "H") {
				console.log(dateStr1+":"+dateStr2);
			} else if(timeViewType == "D") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					$(this).attr("prev-date",null);
				};
			} else if(timeViewType == "W") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = options.firstDayOfWeek(dateObj1);
					dateObj2 = options.cloneDate(dateObj1);
					dateObj2.setDate( dateObj2.getDate() + 6 );
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = options.firstDayOfWeek(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = options.firstDayOfWeek(dateObj1);
						dateObj2 = options.parseDate(prevDate);
					}
					
					dateObj2.setDate( dateObj2.getDate() + 6 );
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
			} else if(timeViewType == "M") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = options.firstDayOfMonth(dateObj1);
					dateObj2 = options.lastDayOfMonth(dateObj1);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = options.firstDayOfMonth(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = options.firstDayOfMonth(dateObj1);
						dateObj2 = options.parseDate(prevDate);
					}
					
					dateObj2 = options.lastDayOfMonth(dateObj2);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
				
			} else if(timeViewType == "Y") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = options.firstDayOfYear(dateObj1);
					dateObj2 = options.lastDayOfYear(dateObj1);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = options.firstDayOfYear(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = options.firstDayOfYear(dateObj1);
						dateObj2 = options.parseDate(prevDate);
					}
					
					dateObj2 = options.lastDayOfYear(dateObj2);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
			}
			
			console.log("PICKUP : "+dateStr1+" ~ "+dateStr2+" / "+$(this).attr("prev-date"));
			if(timeViewType != "H") {
				$(this).val(dateStr1+" - "+dateStr2);
				$(this).pickmeup("set_date",new Array(dateStr1,dateStr2));
			} else {
				$(this).val(dateStr1);
				$(this).pickmeup("set_date",dateStr1);
			};
		}, cloneDate:function(date) {
			return new Date(date.getFullYear(), date.getMonth(), date.getDate());
		}, firstDayOfWeek:function(date) {
			var newDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
			newDate.setDate( newDate.getDate() - ( ( newDate.getDay() + 6 ) % 7 ) );
			return newDate;
		}, firstDayOfMonth:function(date) {
			return new Date(date.getFullYear(), date.getMonth(), 1);
		}, lastDayOfMonth:function(date) {
			return new Date(date.getFullYear(), date.getMonth() + 1, 0);
		}, firstDayOfYear:function(date) {
			return new Date(date.getFullYear(), 0, 1);
		}, lastDayOfYear:function(date) {
			return new Date(date.getFullYear(), 11, 31);
		}, parseDate:function(dateStr) {
			if(!$.isArray(dateStr)) {
				src = dateStr.split("."); src = dateStr.split(".");
				return new Date(src[0], src[1] - 1, src[2]);
			};
		}, formatDate:function(dateObj, failValue) {
			if(dateObj==null) {
				dateObj = failValue;
			}
			var year = dateObj.getFullYear();
			var month = (dateObj.getMonth() + 1);
			var date = dateObj.getDate();
			if(month < 10) { month = "0"+month; }
			if(date < 10) { date = "0"+date; }
			return year+"."+month+"."+date;
		}, timeViewType:"${timeViewType}"
	};
	$("#timeText").pickmeup(pickmeupOptions);
	
	$("#timeViewTypeList button").on("click", function(){
		var timeElement = $("#timeText");
		var options = timeElement.data("pickmeup-options");
		
		$(this).addClass("btn-primary");
		$(this).removeClass("btn-default");
		
		$(this).siblings().addClass("btn-default");
		$(this).siblings().removeClass("btn-primary");
		
		var timeViewType = $(this).text().charAt(0);
		$(this).parents("div").find("input[name=timeViewType]").val(timeViewType);
		options.timeViewType = timeViewType;
		
		var dates = timeElement.val().split(" - ");
		dates[0] = options.parseDate(dates[0]);
		if(timeViewType != "H") {
			dates[1] = dates[1]?options.parseDate(dates[1]):dates[0];
		}
		
		if(timeViewType == "H") {
			timeElement.val(options.formatDate(dates[0]));
			options.mode="single";
		} else {
			var fdate = null;
			if(timeViewType == "D") {
				fdate = dates[0];
				tdate = dates[1];
			} else if(timeViewType == "W") {
				fdate = options.firstDayOfWeek(dates[0]);
				tdate = options.firstDayOfWeek(dates[1]);
				tdate.setDate(tdate.getDate() + 6);
			} else if(timeViewType == "M") {
				fdate = options.firstDayOfMonth(dates[0]);
				tdate = options.lastDayOfMonth(dates[1]);
			} else if(timeViewType == "Y") {
				fdate = options.firstDayOfYear(dates[0]);
				tdate = options.lastDayOfYear(dates[1]);
			}
			timeElement.val(options.formatDate(fdate)+" - "+options.formatDate(tdate));
			options.mode="range";
		};
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
											<th>Max Time</th>
											<th>Averate Time</th>
										</tr>
									</thead>
									<tbody>
										<%
											for(int i = 0;i < list.size(); i++){
												SearchHitVO vo = list.get(i);
										%>
										<tr>
											<td><%=vo.getTimeId() %></td>
											<td><%=format.format(vo.getHit()) %></td>
											<td><%=format.format(vo.getMaxTime()) %> ms</td>
											<td><%=format.format(vo.getAvgTime()) %> ms</td>
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
