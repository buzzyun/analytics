<%@page import="java.util.*, org.fastcatgroup.analytics.db.vo.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
List<String[]> clickTypeList = (List<String[]>) request.getAttribute("clickTypeList");
String keyword = request.getParameter("keyword");
String timeText = (String) request.getAttribute("timeText");
if(timeText == null ) {
	timeText = ""; 
}
List<ClickKeywordTargetHitVO> targetHitList = (List<ClickKeywordTargetHitVO>) request.getAttribute("targetHitList");
%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>

$(document).ready(function(){
	
	var pickmenup_options = {
		calendars: 3,
		mode: 'days',
		format: 'Y.m.d',
		first_day: 1,
		position: 'bottom',
		hide_on_select	: true 
	};
	$("#timeText").pickmeup(pickmenup_options);
	
});


</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="ctr" />
			<c:param name="mcat" value="keyword" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Click-through Rate</a></li>
						<li><a href="#">Keyword</a></li>
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
						<h3>Keyword Click-through</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12 bottom-space">
						<form class="form-inline" role="form">
							<input class="form-control fcol1-2 " size="16" type="text" id="timeText" name="timeText" value="<%=timeText %>" >
							<input type="text" name="keyword" class="form-control fcol2" placeholder="Keyword">
							<input type="submit" class="btn btn-primary" value="Submit">
						</form>
					</div>
				</div>
				
				
				<%
				if(keyword != null && keyword.length() > 0) {
				%>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : <%=timeText.substring(0, 7) %>
								</h4>
							</div>
						</div>

					</div>
				</div>
				
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-content">
								<ul class="stats">
									<li><strong><%=keyword %></strong> <small>Keyword</small></li>
									<li><strong>${searchPv }</strong> <small>Search PV</small></li>
									<li class="text-success"><strong>${ctCount }</strong> <small>Click-through count</small></li>
									<li class="text-primary"><strong>${ctRate }</strong> <small>Click-through rate</small></li>
									<%
									for(String[] clickType : clickTypeList) {
										String count = (String) request.getAttribute("ctCount_"+clickType[0]);
									%>
									<li><strong><%=count %></strong> <small><%=clickType[]1 %></small></li>
									<%
									}
									%>
								</ul>
							</div>
						</div>
					</div>
					
				</div>
				
				<%
				}
				
				if(targetHitList != null) {
				%>
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Click List</h4>
							</div>
							<div class="widget-content">
							
								<table class="table table-bordered table-condensed table-hovered">
									<thead>
										<tr>
											<th>#</th>
											<th>Click Target</th>
											<%
											for(String[] clickType : clickTypeList) {
											%>
											<th><%=clickType[1] %></th>
											<%
											}
											%>
										</tr>
									</thead>
									<tbody>
										<%
										for(int i = 0; i< targetHitList.size(); i++) {
											ClickKeywordTargetHitVO vo = targetHitList.get(i);
										%>
										<tr>
											<td><%=i+1 %></td>
											<td><a href="#"><%=vo.getClickId() %></a></td>
											<%
											for(String[] clickType : clickTypeList) {
												if(clickType[0].equals(vo.getClickType())){
													%><td><%=vo.getCount() %></td><%
												}else{
													%><td>0</td><%	
												}
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
				<%
				}
				%>


			</div>
		</div>
	</div>
</body>
</html>
