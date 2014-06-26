<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.fastcatsearch.analytics.analysis.vo.*"%>
<%@page import="org.fastcatsearch.analytics.analysis.StatisticsUtils" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.*"%>
<%
String date = (String) request.getAttribute("date");

%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
$(document).ready(function(){
	
	var pickmeupOptions = {
		calendars: 3,
		format: "Y.m.d",
		first_day: 1,
		position: "bottom",
		hide_on_select	: false,
		change : function(date) {
		}, cloneDate:function(date) {
		}, timeViewType:"D"
	};
	$("input[name=date]").pickmeup(pickmeupOptions);
	
	
	$("#runForm").on("submit", function(e){
		var count = $(this).find("input[type=checkbox]:checked").length;
		console.log(count);
		if(count == 0) {
			noty({text: "Please select types.", type: "error", layout:"topRight", timeout: 3000});
		}else{
			$(this).ajaxSubmit();
		}
		e.preventDefault();
	});
});

function selectAll() {
	$.each($("#select_table input[type=checkbox]"), function(i, obj){
		$(obj).attr("checked", true);
		console.log($(obj), $(obj).attr("checked"));
	});
}
function deselectAll() {
	$.each($("#select_table input[type=checkbox]"), function(i, obj){
		$(obj).removeAttr("checked");
		console.log($(obj), $(obj).attr("checked"));
	});
}
</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/configuration/sideMenu.jsp">
			<c:param name="lcat" value="management" />
			<c:param name="mcat" value="run" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Configuration</a></li>
						<li><a href="#">Management</a></li>
						<li><a href="#">Run Statistics</a></li>
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
						<h3>Run Statistics</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row ">
					<!-- .row-bg -->
					
						
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-content">
								<form method="post" id="runForm" action="doRun.html">
									<div>
										<table class="table table-hover table-checkable table-bordered table-vertical-align-middle">
											<thead>
											<tr>
												<th rowspan="2">Operation</th><th class="checkbox-column" colspan="5"><input type="checkbox" checked="checked"> Check All</th>
											</tr>
											<tr>
												<th class="fcol1-2">Hour</th>
												<th class="fcol1-2">Day</th>
												<th class="fcol1-2">Week</th>
												<th class="fcol1-2">Month</th>
												<th class="fcol1-2">Year</th>
											</tr>
											</thead>
											<tbody>
											<tr>
												<td>Search Progress</td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="hour_sp"></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
											</tr>
											<tr>
												<td>Search Progress &amp; Keyword Rank</td>
												<td></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="day_sp"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="week_sp"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="month_sp"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="year_sp"></td>
											</tr>
											<tr>
												<td>Search Type Rate</td>
												<td></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="day_type"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="week_type"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="month_type"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="year_type"></td>
											</tr>
											<tr>
												<td>Relate Keyword</td>
												<td></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="day_relate"></td>
												<td></td>
												<td></td>
												<td></td>
											</tr>
											<tr>
												<td>Click-through Rate</td>
												<td></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="day_ctr"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="week_ctr"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="month_ctr"></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="year_ctr"></td>
											</tr>
											<tr>
												<td>Click-through Rate File</td>
												<td></td>
												<td class="checkbox-column"><input type="checkbox" checked="checked" name="taskType" value="day_ctr_file"></td>
												<td></td>
												<td></td>
												<td></td>
											</tr>
											</tbody>
										</table>
										
										
									</div>
									<div class="form-inline">
										<input class="form-control fcol1-2 " size="16" type="text" name="date" autocomplete="off" value="<%=date %>" >
										<input type="submit" class="btn btn-primary" value="Run">
									</div>
								</form>
							</div>
						</div>
					</div>
				
				</div> 
			</div>
		</div>
	</div>
</body>
</html>
