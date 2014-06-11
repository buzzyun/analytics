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
List<List<String[]>> dailyFileInfoList = (List) request.getAttribute("dailyFileInfoList");
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

function showLogFile(siteId, date, timeViewType, fileName, fileSize) {
	$.ajax({
		url : "rawLogFileData.html",
		data : {
			siteId : siteId,
			date : date,
			timeViewType : timeViewType,
			fileName : fileName
		},
		dataType : "text",
		type : "POST",
		success : function(response) {
			$('#rawLogFileDataModal').modal({show : true});
			$("#dateLabel").text(date);
			$("#fileInfo").text(fileName + "("+fileSize+")");
			$("#fileContent").val(response);
		}
	});
}

</script>
</head>
<body>
<c:import url="../inc/mainMenu.jsp" />
<div id="container">
		<c:import url="${ROOT_PATH}/settings/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="rawLogFile" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Raw Log File</a>
						</li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->
				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Raw Log File</h3>
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
										<% for ( int inx=0;inx < 8;inx++) { %>
										<col style="width:<%=100/8 %>%"></col>
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
									int dateInx=0;
									for ( int k=0; localCalendar.getTimeInMillis() <= nextCalendar.getTimeInMillis(); k++) {
									%>
										<tr class="active">
										<%
										
										Calendar bodyCalendar = (Calendar) localCalendar.clone();
										for ( int headerInx=0; headerInx < 7; headerInx++ ) {
											String classStr = "";
											if(headerInx == 5) {
												 classStr = "text-primary";
											}else if(headerInx == 6) {
												 classStr = "text-danger";
											}
											
											localCalendar.add(localCalendar.DATE, 1);
											
											if(localCalendar.get(Calendar.MONTH) != month){
												classStr += " text-muted"; 
											}
											
											%>
											<th class="<%=classStr%>"><%=dateFormat.format(localCalendar.getTime()) %></th>
										<%
										}
										%>
										</tr>
										<tr>
											<%
											for ( int weekInx=0; weekInx < 7; weekInx++, dateInx++ ) {
												bodyCalendar.add(bodyCalendar.DATE, 1);
											%>
												<%
												List<String[]> fileInfoList = dailyFileInfoList.get(dateInx);
												String classStr = "";
												if(bodyCalendar.get(Calendar.MONTH) != month){
													classStr += " text-muted"; 
												}
												
												%>
												<td class="<%=classStr %>" style="height:50px;">
												
												<%
												for (int i=0; fileInfoList!=null && i < fileInfoList.size(); i++) {
													String[] fileInfo = fileInfoList.get(i);
													%>
													- <a href="javascript:showLogFile('<%=siteId %>', '<%=ymdFormat.format(bodyCalendar.getTime()) %>', 'D', '<%=fileInfo[0] %>', '<%=fileInfo[1] %>')" class="a-no-decoration"><%=fileInfo[0] %> (<%=fileInfo[1] %>)</a>
												<%
													if(i < fileInfoList.size() - 1) {
														%><br><%
													}
												}
												%>
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
				
				
			</div>
			<!-- /.container -->
		</div>
	</div>

	<div class="modal" id="rawLogFileDataModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content" style="width:800px">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Raw Log File</h4>
				</div>
				<br/>
				<div class="tab-content">
					<div class="tab-pane active">
						<div class="col-md-12">
							<p><b>Date : <span id="dateLabel"></span></b></p>
							<p><b>File: <span id="fileInfo"></span></b></p> 
						</div>
						<div class="col-md-12">
							<textarea id="fileContent" style="width:100%; height:600px"></textarea>
							<br><br>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
