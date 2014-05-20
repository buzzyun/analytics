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
SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy.MM.dd");
Calendar calendar = (Calendar) request.getAttribute("calendar");
List<List<TaskResultVO>> monthlyTaskResult = (List) request.getAttribute("taskResult");
List<SiteSetting> siteList = (List)request.getAttribute("siteList");
String siteId = request.getParameter("siteId");
%>
<c:set var="ROOT_PATH" value="../.." scope="request"/>
<c:import url="../inc/common.jsp" />
<html>
<head>
<c:import url="../inc/header.jsp" />

<script type="text/javascript">
$(document).ready(function() {
	var form = $("div.tab-content div.tab-pane form");
	
	var selectbox = form.find("div select#select_site");
	
	selectbox.change(function() {
		form[0].siteId.value = this.value;
		form.submit();
	});
	
	
	var btns = form.find("div.input-group span.input-group-btn button");
	
	btns.click(function() {
		var index = $.inArray(this, btns);
		var date = $(btns[index]).attr("value");
		
		form[0].date.value = date;
		form.submit();
	});
});
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
				//달의 첫주는 이전달과 섞여있기 때문에 차주의 달을 택한다.
				int month = localCalendar.get(Calendar.MONTH);
				//현재달과 이전달, 다음달의 출력문자를 생성.
				String monthStr = monthFormat.format(localCalendar.getTime());
				localCalendar.add(Calendar.MONTH, -1);
				String prevMonth = monthFormat.format(localCalendar.getTime());
				localCalendar.add(Calendar.MONTH, 2);
				String nextMonth = monthFormat.format(localCalendar.getTime());
				//다음달 첫날일자 (달력 한계값 구할때 사용)
				Calendar nextCalendar = (Calendar)localCalendar.clone();
				nextCalendar.set(Calendar.DATE, -1);
				//원래 날자로 복원. (당월 첫째주 시작일)
				localCalendar.add(Calendar.MONTH, -1);
				localCalendar.add(Calendar.DATE, -7);
				%>
				
				<div class="tab-content">
					<div class="tab-pane active">
						<form>
						<div class="col-md-12 bottom-space">
							<select id="select_site" name="siteId" class="select_flat fcol2">
								<% for (int inx=0;inx < siteList.size(); inx++) { %>
								<% SiteSetting site = siteList.get(inx); %>
								<option value="<%=site.getId()%>" <%=site.getId().equals(siteId) ? "selected":"" %>><%=site.getName() %></option>
								<% } %>
							</select>
						</div>
						<div class="col-md-12 bottom-space">
							<div class="input-group fcol2">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button" value="<%=prevMonth%>">&laquo;</button>
								</span>
								<input type="text" class="form-control" name="date" value="<%=monthStr%>">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button" value="<%=nextMonth%>">&raquo;</button>
								</span>
							</div>
						</div>
						</form>
						<div class="col-md-12">
							<div class="widget box">
								<div class="widget-header pull-center">
									<h4><%=monthStr%></h4>
								</div>
								<div class="widget-content no-padding">
									<table class="table table-bordered table-header table-highlight-head">
									<colgroup>
										<% for ( int inx=0;inx < 7;inx++) { %>
										<col style="width:<%=100/7 %>%"></col>
										<% } %>
									</colgroup>
									<thead>
										<tr>
											<th>Mon</th>
											<th>Tue</th>
											<th>Wed</th>
											<th>Thu</th>
											<th>Fri</th>
											<th class="text-primary">SAT</th>
											<th class="text-danger">SUN</th>
										</tr>
									</thead>
									<tbody>
									<% 
									for ( int dateInx=0; localCalendar.getTimeInMillis() <= nextCalendar.getTimeInMillis() ; ) {
									%>
										<tr class="active">
										<%
										for ( int headerInx=0; headerInx < 7; headerInx++ ) {
											String classStr = "";
											if(headerInx == 5) {
												 classStr = "class=\"text-primary\"";
											}else if(headerInx == 6) {
												 classStr = "class=\"text-danger\"";
											}
										%>
											<%
											localCalendar.add(localCalendar.DATE, 1);
											%>
											<th <%=localCalendar.get(Calendar.MONTH)==month?"":"class=\"gray\"" %>><%=dateFormat.format(localCalendar.getTime()) %></th>
										<%
										}
										%>
										</tr>
										<tr>
											<%
											for ( int weekInx=0; weekInx < 7; weekInx++, dateInx++ ) {
											%>
												<%
												List<TaskResultVO> taskResult = monthlyTaskResult.get(dateInx);
												boolean isSuccess = true;
												for (int taskInx=0;taskResult!=null && taskInx < taskResult.size(); taskInx++) {
													if(!"SUCCESS".equals(taskResult.get(taskInx).getResultStatus())) {
														isSuccess = false;
													}
												}
												%>
												<td <%=isSuccess?"":"class=\"danger\"" %> style="height:50px;">
												<a data-toggle="modal" data-target="#taskResultModal_<%=dateInx%>" class="a-no-decoration" style="cursor: pointer;">
												<%
												for (int taskInx=0;taskResult!=null && taskInx < taskResult.size(); taskInx++) {
												%>
													<%TaskResultVO task = taskResult.get(taskInx); %>
													<%if ("SUCCESS".equals(task.getResultStatus())) { %>
														<span class="text-success glyphicon glyphicon-ok-sign"></span>
														<%=task.getTaskId() %><br/>
													<% } else { %>
														<span class="text-success glyphicon glyphicon-remove-sign"></span>
														<%=task.getTaskId() %><br/>
													<% } %>
												<%
												}
												%>
												</a> 
												</td>
											<%
											}
											%>
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
				
				<%
				for(int dateInx=0;dateInx<monthlyTaskResult.size();dateInx++) {
				%>
					<%
					List<TaskResultVO>taskResult = monthlyTaskResult.get(dateInx);
					String targetTime = "";
					if(taskResult.size() > 0) {
						targetTime = ymdFormat.format(taskResult.get(0).getStartTime());
					}
					%>
					<div class="modal" id="taskResultModal_<%=dateInx %>" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog" style="width:80%">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
									<h4 class="modal-title">Task Result Detail</h4>
								</div>
								<br/>
								<div class="tab-content">
									<div class="tab-pane active">
										<div class="col-md-12">
											<div class="widget box">
												<div class="widget-header">
												<h4><%=targetTime %> - Task Result Detail</h4>
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
													<% 
													for ( int taskInx=0;taskInx < taskResult.size(); taskInx++) {
													%>
														<%
														TaskResultVO task = taskResult.get(taskInx);
														boolean isSuccess = "SUCCESS".equals(task.getResultStatus());
														
														String startTime = timeFormat.format(task.getStartTime());
														String endTime = timeFormat.format(task.getEndTime());
														%>
														<tr <%=isSuccess?"":"class=\"danger\"" %>>
															<td class="fcol1"><%=taskInx+1 %></td>
															<td class="fcol1-1"><%=startTime %></td>
															<td class="fcol1-1"><%=endTime %></td>
															<td class="fcol1-1"><%=task.getDuration() %></td>
															<td class="fcol2-1">
															<%if(isSuccess) { %>
															<span class="text-success glyphicon glyphicon-ok-sign"></span> 
															<% } else { %>
															<span class="text-danger glyphicon glyphicon-remove-sign"></span> 
															<% } %>
															<%=task.getTaskName() %>
															</td>
															<td style="word-break:break-all">
															<%=task.getDetail().replaceAll("\n", "<br>") %>
															</td>
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
								
								<div class="modal-footer">
									<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
								</div>
								
							</div>
						</div>
					</div>
				<%
				}
				%>
			</div>
			<!-- /.container -->
		</div>
	</div>

	
</body>
</html>
