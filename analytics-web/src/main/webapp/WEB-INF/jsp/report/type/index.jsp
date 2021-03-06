<%@page import="java.util.*"%>
<%@page import="org.fastcatsearch.analytics.db.vo.*" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.text.DecimalFormat" %>
<%@page import="org.fastcatsearch.analytics.analysis.StatisticsUtils" %>
<%@page import="org.fastcatsearch.analytics.analysis.config.StatisticsSettings.TypeSetting" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String categoryId = (String) request.getAttribute("categoryId");
List<SearchTypeHitVO> list = (List<SearchTypeHitVO>) request.getAttribute("list");
String typeId = (String) request.getAttribute("typeId");
String timeText = (String) request.getAttribute("timeText");
String timeViewType = (String) request.getAttribute("timeViewType");
List<TypeSetting> typeArray = (List<TypeSetting>) request.getAttribute("typeArray");
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
	
	//서비스별 Data
	var service_rate_data = [];
	
	<%	
	int totalCount = 0;
	if(list != null && list.size() > 0){
		for(int i=0;i<list.size(); i++){
			SearchTypeHitVO vo = list.get(i);
			totalCount += vo.getHit();
		}
		for(int i=0;i<list.size(); i++){
			SearchTypeHitVO vo = list.get(i);
			float ratio = (((float)vo.getHit() / (float)totalCount) * 100.0f);
			String ratioString = String.format("%.1f", ratio);
			%>
			service_rate_data[<%=i%>] = { label: "<%=vo.getDtype() %>", data: <%=ratioString %> };
			<%
		}
		%>
		
		$.plot("#chart_category_rate", service_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
			series: {
				pie: {
					show: true,
					radius: 1,
					label: {
						show: true,
					}
				}
			},
			grid: {
				hoverable: true
			},
			tooltip: true,
			tooltipOpts: {
				content: '%s: %p.0%', // show percentages, rounding to 2 decimal places
				shifts: {
					x: 20,
					y: 0
				}
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
			var dateObj1 = parseDate(dateStr1);
			var dateObj2 = parseDate(dateStr2);
			
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
					dateObj1 = getFirstDayOfWeek(dateObj1);
					dateObj2 = cloneDate(dateObj1);
					dateObj2.setDate( dateObj2.getDate() + 6 );
					dateStr1 = formatDate(dateObj1);
					dateStr2 = formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = getFirstDayOfWeek(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = getFirstDayOfWeek(dateObj1);
						dateObj2 = parseDate(prevDate);
					}
					
					dateObj2.setDate( dateObj2.getDate() + 6 );
					dateStr1 = formatDate(dateObj1);
					dateStr2 = formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
			} else if(timeViewType == "M") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = getFirstDayOfMonth(dateObj1);
					dateObj2 = getLastDayOfMonth(dateObj1);
					dateStr1 = formatDate(dateObj1);
					dateStr2 = formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = getFirstDayOfMonth(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = getFirstDayOfMonth(dateObj1);
						dateObj2 = parseDate(prevDate);
					}
					
					dateObj2 = getLastDayOfMonth(dateObj2);
					dateStr1 = formatDate(dateObj1);
					dateStr2 = formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
				
			} else if(timeViewType == "Y") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = getFirstDayOfYear(dateObj1);
					dateObj2 = getLastDayOfYear(dateObj1);
					dateStr1 = formatDate(dateObj1);
					dateStr2 = formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = getFirstDayOfYear(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = getFirstDayOfYear(dateObj1);
						dateObj2 = parseDate(prevDate);
					}
					
					dateObj2 = getLastDayOfYear(dateObj2);
					dateStr1 = formatDate(dateObj1);
					dateStr2 = formatDate(dateObj2);
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
		dates[0] = parseDate("${today}");
		var fdate = null;
		if(timeViewType == "D") {
			fdate = dates[0];
			tdate = dates[0];
		} else if(timeViewType == "W") {
			fdate = getFirstDayOfWeek(dates[0]);
			tdate = getLastDayOfWeek(dates[0]);
		} else if(timeViewType == "M") {
			fdate = getFirstDayOfMonth(dates[0]);
			tdate = getLastDayOfMonth(dates[0]);
		} else if(timeViewType == "Y") {
			fdate = getFirstDayOfYear(dates[0]);
			tdate = getLastDayOfYear(dates[0]);
		}
		timeElement.val(formatDate(fdate)+" - "+formatDate(tdate));
		options.mode="range";
	});
});

function showRatioTab(typeId){
	$("#tabForm input[name=typeId]").val(typeId);
	$("#tabForm").submit();
}
</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="typeProgress" />
			<c:param name="mcat" value="${typeId}" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Type Ratio</a></li>
						<li><a href="#">${typeId }</a></li>
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
						<h3>Type Ratio ${typeId}</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					<form method="get">
						<div class="col-md-12">
							<div class="form-inline">
							<%
							if(!typeId.equals("category")){
							%>
								<select id="select_category" name="categoryId" class="select_flat fcol2" disabled></select>
							<%
							}else{
							%>
								<select name="categoryId" class="select_flat fcol2" disabled></select>
							<%
							}
							%>
								<input class="form-control fcol2-1 " type="text" name="timeText" id="timeText" value="<%=timeText %>" >
								<div id="timeViewTypeList" class="btn-group">
									<button type="button" class="btn <%="D".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Daily</button>
									<button type="button" class="btn <%="W".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Weekly</button>
									<button type="button" class="btn <%="M".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Monthly</button>
									<button type="button" class="btn <%="Y".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Yearly</button>
									<input type="hidden" name="timeViewType" value="<%=timeViewType %>">
								</div>
								<input type="hidden" name="typeId" value="<%=typeId %>">
								<input type="submit" class="btn btn-primary" value="Submit">
							</div>
						</div>
					</form>
				</div>
				
				<%
				if(list != null){
				%>
				
				<div class="tabbable tabbable-custom tabbable-full-width" id="schema_tabs">
					<ul class="nav nav-tabs">
						<% 
						for ( int typeInx=0; typeInx < typeArray.size(); typeInx ++ ) { 
						%>
							<li class="<%=typeId.equals(typeArray.get(typeInx).getId()) ? "active" : "" %>"><a href="javascript:showRatioTab('<%=typeArray.get(typeInx).getId()%>')"><%=typeArray.get(typeInx).getName() %></a></li>
						<% 
						} 
						%>
					</ul>
					<div class="tab-content row">
						
						<!--=== fields tab ===-->
						<div class="tab-pane active">
					
							<div class="col-md-12">
								<div id="chart_category_rate" class="chart"></div>
								<div>
									<br/>
									<table class="table table-striped table-bordered table-condensed">
										<thead>
											<tr>
												<th>Rank</th>
												<th>Type</th>
												<th>Hit Count</th>
												<th>Ratio</th>
											</tr>
										</thead>
										<tbody>
											<%
											for(int i=0;i<list.size(); i++){
												SearchTypeHitVO vo = list.get(i);
												float ratio = (((float)vo.getHit() / (float)totalCount) * 100.0f);
											%>
											<tr>
											<td><%=i + 1 %></td>
											<td><%=vo.getDtype() %></td>
											<td><%=format.format(vo.getHit()) %></td>
											<td><%=String.format("%.1f", ratio) %>%</td>
											</tr>
											<%
											}
											%>
											<tr>
												<td>Summary</td>
												<td></td>
												<td><%=format.format(totalCount) %></td>
												<td>100.0%</td>
											</tr>
										</tbody>
									</table>
								</div>
									
							</div>
								
						</div>
					</div>
				</div>
				
				<%
				}
				%>
				
			</div>
		</div>
	</div>
	
	<form id="tabForm">
	<input type="hidden" name="categoryId" value="${categoryId }"/>
	<input type="hidden" name="typeId" value="${typeId }"/>
	<input type="hidden" name="timeViewType" value="${timeViewType }"/>
	<input type="hidden" name="timeText" value="${timeText }"/>
	</form>
</body>
</html>
