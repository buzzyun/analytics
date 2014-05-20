<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="
java.util.*,
java.text.SimpleDateFormat,
org.fastcatsearch.analytics.db.vo.*,
org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting,
org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting
" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy.MM");
SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
Calendar calendar = (Calendar) request.getAttribute("calendar");
%>
<c:set var="ROOT_PATH" value="../.." scope="request"/>
<c:import url="../inc/common.jsp" />
<html>
<head>
<c:import url="../inc/header.jsp" />

<script type="text/javascript">

</script>
</head>
<body>
<c:import url="../inc/mainMenu.jsp" />
<div id="container">
		<c:import url="${ROOT_PATH}/settings/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="taskResult" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Task Result</a>
						</li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->
				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Task Result</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<!--=== Page Content ===-->
				
				
				<%
				Calendar localCalendar = (Calendar)calendar.clone();
				localCalendar.add(Calendar.DATE, 7);
				int month = localCalendar.get(Calendar.MONTH);
				String monthStr = monthFormat.format(localCalendar.getTime());
				
				localCalendar.add(Calendar.MONTH, -1);
				String prevMonth = monthFormat.format(localCalendar.getTime());
				localCalendar.add(Calendar.MONTH, 2);
				String nextMonth = monthFormat.format(localCalendar.getTime());
				localCalendar.add(Calendar.MONTH, -1);
				
				localCalendar.add(Calendar.DATE, -7);
				%>
				
				<div class="tab-content">
					<div class="tab-pane active">
						<div class="col-md-12 bottom-space">
							<select id="select_site" name="siteId" class="select_flat fcol2">
								<option>localhost.com</option>
							</select>
						</div>
						<div class="col-md-12 bottom-space">
							<div class="input-group fcol2">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button">&laquo;</button>
								</span>
								<input type="text" class="form-control" value="<%=monthStr%>">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button">&raquo;</button>
								</span>
							</div>
						</div>
									
						<div class="col-md-12">
							<div class="widget box">
								<div class="widget-header pull-center">
									<h4><%=monthStr%></h4>
								</div>
								<div class="widget-content no-padding">
									<table class="table table-bordered table-header table-highlight-head">
									<thead>
										<tr>
											<th>Mon</th>
											<th>Tue</th>
											<th>Wed</th>
											<th>Thu</th>
											<th>Fri</th>
											<th>SAT</th>
											<th>SUN</th>
										</tr>
									</thead>
									<tbody>
									<% 
									for ( int weekInx=0; localCalendar.get(Calendar.MONTH) <= month; weekInx++) {
									%>
										<tr class="active">
										<%
										for ( int dateInx=0; dateInx < 7; dateInx++ ) {
										%>
											<%
											localCalendar.add(Calendar.DATE, 1);
											%>
											<th><%=dateFormat.format(localCalendar.getTime()) %></th>
										<%
										}
										%>
										</tr>
										<tr>
											<%
											for ( int dateInx=0; dateInx < 7; dateInx++ ) {
											%>
											<td class="danger"><a href="#" class="a-no-decoration">
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-danger glyphicon glyphicon-remove-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
											</a>
											</td>
											<%
											}
											%>
											<!--
											<td>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-danger glyphicon glyphicon-remove-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
												<span class="text-success glyphicon glyphicon-ok-sign"></span> DAILY_SP<br>
											</td>
											-->
										</tr>
									<%
									}
									%>
									</tbody>
									</table>
								</div>
							</div>
							
						</div>
					</div>
				</div>
				
				
				<div class="tab-content">
					<div class="tab-pane active">
						<div class="col-md-12">
							<div class="widget box">
								<div class="widget-header">
								<h4>2014.03.31 - Task Result Detail</h4>
								</div>
								<div class="widget-content no-padding">
									<table class="table table-bordered">
									<thead>
										<tr>
											<th>#</th>
											<th>Start</th>
											<th>End</th>
											<th>Duration</th>
											<th>Task</th>
											<th class="fcol2">Explain</th>
										</tr>
									</thead>
									<tbody>
									<tr>
										<td>1</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-success glyphicon glyphicon-ok-sign"></span> Daily Search Progress Task</td>
										<td></td>
									</tr>
									<tr class="danger">
										<td>2</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-danger glyphicon glyphicon-remove-sign"></span> Weekly Search Progress Task</td>
										<td>(Calculator.java:74) Process Drop. cause=Drop process due to file is empty. file=/Users/swsong/TEST_HOME/danawa1022/analytics-1.14.2/statistics/www/rt/data/cate3/key-count.log
[2014-05-02 10:35:04,979 DEBUG] (AbstractLogAggregator.java:89) ##aggregate count 0
[2014-05-02 10:35:04,980 DEBUG] (Calculator.java:65) #### calculate category RealtimePopularKeywordCalculator > www:cate4:CategoryProcess</td>
									</tr>
									<tr>
										<td>3</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-success glyphicon glyphicon-ok-sign"></span> Weekly Search Progress Task</td>
										<td></td>
									</tr>
									<tr>
										<td>4</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-success glyphicon glyphicon-ok-sign"></span> Weekly Search Progress Task</td>
										<td></td>
									</tr>
									<tr>
										<td>5</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-success glyphicon glyphicon-ok-sign"></span> Weekly Search Progress Task</td>
										<td></td>
									</tr>
									<tr>
										<td>6</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-success glyphicon glyphicon-ok-sign"></span> Weekly Search Progress Task</td>
										<td></td>
									</tr>
									<tr>
										<td>7</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-success glyphicon glyphicon-ok-sign"></span> Weekly Search Progress Task</td>
										<td></td>
									</tr>
									<tr>
										<td>8</td>
										<td>00:20:00</td>
										<td>00:20:50</td>
										<td>50s</td>
										<td><span class="text-success glyphicon glyphicon-ok-sign"></span> Weekly Search Progress Task</td>
										<td></td>
									</tr>
									</tbody>
									</table>
								</div>
							</div>
							
						</div>
					</div>
				</div>
			</div>
			<!-- /.container -->
		</div>
	</div>

	
</body>
</html>