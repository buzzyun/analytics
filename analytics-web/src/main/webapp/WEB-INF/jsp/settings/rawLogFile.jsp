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
//List<List<TaskResultVO>> monthlyTaskResult = (List) request.getAttribute("taskResult");
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
							<a data-toggle="modal" data-target="#rawLogFileDataModal">팝업테스트</a>
							
							
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
											<th class="">Week</th>
										</tr>
									</thead>
									<tbody>
									<% 
									for ( int dateInx=0; localCalendar.getTimeInMillis() <= nextCalendar.getTimeInMillis() ; ) {
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
											
											
										%>
											<%
											localCalendar.add(localCalendar.DATE, 1);
											
											if(localCalendar.get(Calendar.MONTH) != month){
												classStr += " text-muted"; 
											}
											
											%>
											<th class="<%=classStr%>"><%=dateFormat.format(localCalendar.getTime()) %></th>
										<%
										}
										%>
											<th>&nbsp;</th>
										</tr>
										<tr>
											<%
											for ( int weekInx=0; weekInx < 7; weekInx++, dateInx++ ) {
												bodyCalendar.add(bodyCalendar.DATE, 1);
											%>
												<%
												//List<TaskResultVO> taskResult = monthlyTaskResult.get(dateInx);
												boolean isSuccess = true;
												/* for (int taskInx=0;taskResult!=null && taskInx < taskResult.size(); taskInx++) {
													if(!"SUCCESS".equals(taskResult.get(taskInx).getResultStatus())) {
														isSuccess = false;
														break;
													}
												} */
												String classStr = "";
												if(!isSuccess) {
													classStr = "danger";
												}
												if(bodyCalendar.get(Calendar.MONTH) != month){
													classStr += " text-muted"; 
												}
												
												
												%>
												<td class="<%=classStr %>" style="height:50px;">
												<%-- <a data-toggle="modal" data-target="#taskResultModal_<%=dateInx%>" class="a-no-decoration" style="cursor: pointer;">
												<%
												for (int taskInx=0;taskResult!=null && taskInx < taskResult.size(); taskInx++) {
												%>
													<%TaskResultVO task = taskResult.get(taskInx); %>
													<%if ("SUCCESS".equals(task.getResultStatus())) { %>
														<span class="text-success glyphicon glyphicon-ok-sign"></span>
														<%=task.getTaskId() %><br/>
													<% } else { %>
														<span class="text-danger glyphicon glyphicon-remove-sign"></span>
														<%=task.getTaskId() %><br/>
													<% } %>
												<%
												}
												%>
												</a>  --%>
												
												- <a href="#" class="a-no-decoration">raw.log (318kb)</a><br>
												- <a href="#" class="a-no-decoration">type_raw.log (523kb)</a><br>
												- <a href="#" class="a-no-decoration">click_raw.log (10kb)</a>
												</td>
											<%
											}
											%>
											<td style="height:50px;">
												- <a href="#" class="a-no-decoration">raw.log (318kb)</a><br>
												- <a href="#" class="a-no-decoration">type_raw.log (523kb)</a><br>
												- <a href="#" class="a-no-decoration">click_raw.log (10kb)</a>
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
						<div class="col-md-12 bottom-space">
							<table class="table table-bordered table-header table-highlight-head">
								<thead>
									<tr>
										<th class="">Month</th>
										<th class="">Year</th>
									</tr>
								<tbody>
									<tr>
										<td>
										- <a href="#" class="a-no-decoration">raw.log (918kb)</a><br>
										- <a href="#" class="a-no-decoration">type_raw.log (823kb)</a><br>
										- <a href="#" class="a-no-decoration">click_raw.log (110kb)</a>
										</td>
										<td>
										- <a href="#" class="a-no-decoration">raw.log (7.5mb)</a><br>
										- <a href="#" class="a-no-decoration">type_raw.log (5.2mb)</a><br>
										- <a href="#" class="a-no-decoration">click_raw.log (1.1mb)</a>
										</td>
									</tr>
								</tbody>
							</table>
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
							<p><b>Path : /Application/analytics/statistics/www/date/Y2014/M04/D25/data/raw.log</b></p>
							<p><b>File: raw.log (318kb)</b></p> 
						</div>
						<div class="col-md-12">
							<textarea style="width:100%; height:600px">
01:27	cat2	TYAN	iptime 무선공유기	281748	2001	total
01:28	cat2	커튼장식	미스트	49451	562	autocomplete
01:28	_root	부가장비 기타	애니데이 팬티라이너	380624	3363	main
01:28	cat2	참숯 기타	남성 여성	1538065	187	catelist
01:28	cat2	ide to sata	케이블 기타	793236	2977	total
01:28	cat2	제균	cpu 쿨러	1830270	1529	main
01:28	cat1	RGB(D-SUB)	카메라배낭형가방	0	366	total
01:28	cat1	맥북파우치	파노라마 모니터	1932653	1637	catelist
01:29	_root	2차클렌징	센터 서라운드	0	1943	autocomplete
01:29	cat2	사회학	ips236v	1133502	661	catelist
01:29	_root	잠금장치	아이나비 블랙박스	0	693	autocomplete
01:29	_root	냉동고(가정용)	마운틴하드웨어	1679162	1486	main
01:29	cat2	유나이티드워커스	돗자리매트	0	2665	total
01:29	cat1	탁구공	충전기 배터리	209939	3023	autocomplete
01:29	cat1	속사케이스	선반	490073	1997	total
01:30	cat2	42lm6400	스테이츠맨	283691	2809	total
01:30	cat2	w700	선풍기	0	1117	autocomplete
01:30	cat2	골프티 볼마커	흑백 레이저	264338	2751	main
01:30	cat2	알루미늄 스티커	르꼬끄신발	513608	2428	main
01:30	_root	장갑	슬리퍼	49446	2844	autocomplete
01:30	cat2	제습제	조립컴퓨터본체	1435035	2046	main
01:30	cat1	손수레	황사 마스크	849657	2289	main
01:31	_root	교육 카드	루어낚시	0	713	autocomplete
01:31	cat2	매거진	브래지어	1963991	428	autocomplete
01:31	cat2	문화	앰블럼	861776	864	catelist
01:31	cat1	엑스캔버스	기아	1293701	3820	total
01:31	_root	베이직+by코데즈컴바인	닭 오리 기타	111127	3353	total
01:31	cat2	텐트모기장	남아용	1183300	3355	total
01:31	cat1	헬기 비행기	pdp tv	0	2118	autocomplete
01:32	cat2	터치스크린	동파방지	201302	286	main
01:32	cat2	팩스복합기	델	897924	3566	main
01:32	_root	쪼리	기능성샤워기	1530694	1637	main
01:32	cat1	남성가방	라빠레뜨가방	1137245	1871	main
01:32	cat1	nt355v5c-s75j	퍼프소매 벌룬	102900	3082	total
01:32	cat1	아로마오일	키모니테니스그립	114366	2901	autocomplete
01:32	cat1	3단 선반	무료	752595	2465	catelist
01:33	cat1	청림아쿠아청소기	노트북 거치대	565024	1564	total
01:33	cat1	탁구라켓	메가박스	0	194	total
01:33	cat1	궁중비책	corsair	1639846	1994	autocomplete
01:33	_root	조립컴퓨터본체	부츠컷 나팔	591487	1896	catelist
01:33	_root	진공청소기	메인보드 2011	699938	1703	autocomplete
01:33	_root	조율기	야외용	0	2866	total
01:33	cat2	린넨암막커튼	아기치즈	1201194	2894	autocomplete
01:34	cat2	구명조끼	건강측정기	828930	939	autocomplete
01:34	cat2	슈즈액세서리	ㅗㅇㅇ	0	1877	autocomplete
01:34	cat1	브라운관TV	기타 테마의상	223416	3689	autocomplete
01:34	_root	도서 DVD	h160-gv3wk	1399969	1258	catelist							
							
							
							</textarea>
							<br><br>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
