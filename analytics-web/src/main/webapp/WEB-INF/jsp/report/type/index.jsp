<%@page import="java.util.*"%>
<%@ page import="org.fastcatgroup.analytics.db.vo.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String categoryId = (String) request.getAttribute("categoryId");
List<SearchTypeHitVO> list = (List<SearchTypeHitVO>) request.getAttribute("list");

String typeId = (String) request.getAttribute("typeId");
String timeFrom = (String) request.getAttribute("timeFrom");
String timeTo = (String) request.getAttribute("timeTo");

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
	if(list != null){
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
	
	
	$('#date1').DatePicker({
		flat: true,
		date: new Date(),
		calendars: 3,
		mode: 'range',
		view: 'days',
		//lselect: 'days',
		starts: 1,
		onChange: function(formated) {
			//$('#select_days').text(formated[0] +' ~ ' + formated[1]);
		}
	});

});


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
					
					<div class="col-md-12">
						<form class="form-inline" method="get">
							<div class="col-md-5 bottom-space">
								<div class="form-inline bottom-space">
								<%
								if(!typeId.equals("category")){
								%>
									<select id="select_category" name="categoryId" class="select_flat select_flat-sm fcol2"></select>
								<%
								}
								%>
								<span>
								<input class="form-control fcol1-2 " size="16" type="text" name="timeFrom" value="<%=timeFrom %>" >
								- <input class="form-control fcol1-2 " size="16" type="text" name="timeTo" value="<%=timeTo %>" >
								</span>
								</div>
								
								<div class="btn-group bottom-space">
									<button type="button" class="btn btn-primary">Daily</button>
									<button type="button" class="btn btn-default">Weekly</button>
									<button type="button" class="btn btn-default">Monthly</button>
									<button type="button" class="btn btn-default">Yearly</button>
								</div>
							
								<div class="form-inline">
									<input type="submit" class="btn btn-primary" value="Submit">
								</div>
							</div>
							<div class="col-md-7 bottom-space">
								<div id="date1"></div>
							</div>
						</form>
					</div>
				</div>
				
				<%
				if(list != null){
				%>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : <%=timeFrom %> - <%=timeTo %>
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
								<h4>${typeId} ratio</h4>
							</div>
							<div class="widget-content">
								<div id="chart_category_rate" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<div>
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
											<td><%=vo.getHit() %></td>
											<td><%=String.format("%.1f", ratio) %>%</td>
											</tr>
											<%
											}
											%>
											<tr>
												<td>Summary</td>
												<td></td>
												<td><%=totalCount %></td>
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
</body>
</html>
