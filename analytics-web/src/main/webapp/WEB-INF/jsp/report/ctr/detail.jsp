<%@page import="java.util.*, org.fastcatsearch.analytics.db.vo.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
List<String[]> clickTypeList = (List<String[]>) request.getAttribute("clickTypeList");
List<ClickKeywordHitVO> keywordList = (List<ClickKeywordHitVO>) request.getAttribute("keywordList");
List<Map<String, String>> typeCountMapList = (List<Map<String, String>>) request.getAttribute("typeCountMapList");
List<String> keywordSearchPvList = (List<String>) request.getAttribute("keywordSearchPvList");
List<String> kewordCtrList = (List<String>) request.getAttribute("kewordCtrList");

String timeText = (String) request.getAttribute("timeText");
if(timeText == null ) {
	timeText = ""; 
}
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

function goDetail(keyword){
	submitForm("keyword.html", {"keyword" : keyword}, "post");
}

</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="ctr" />
			<c:param name="mcat" value="detail" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Click-through Rate</a></li>
						<li><a href="#">Detail</a></li>
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
						<h3>Detail Click-through</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" role="form">
							<input class="form-control fcol1-2 " size="16" type="text" id="timeText" name="timeText" value="<%=timeText %>" >
							<input type="submit" class="btn btn-primary" value="Submit">
						</form>
					</div>
				</div>
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
					
					<div class="col-md-6">
						<div class="widget box">
							<div class="widget-header">
								<h4>Click-through Rate</h4>
							</div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong>${searchPv}</strong> <small>Search PV</small></li>
									<li class="text-success"><strong>${ctCount }</strong> <small>Click-through count</small></li>
									<li class="text-primary"><strong>${ctRate }</strong> <small>Click-through rate</small></li>
								</ul>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="widget box">
							<div class="widget-header">
								<h4>Detail Click-through Count</h4>
							</div>
							<div class="widget-content">
								<ul class="stats">
									<%
									for(String[] clickType : clickTypeList) {
										String ctCount = "ctCount_" + clickType[0];
									%>
									<li><strong><%=request.getAttribute(ctCount) %></strong> <small><%=clickType[1] %></small></li>
									<%
									}
									%>
								</ul>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Keyword List</h4>
							</div>
							<div class="widget-content">
							
								<table class="table table-bordered table-condensed table-hovered">
									<thead>
										<tr>
											<th>#</th>
											<th>Keyword</th>
											<th>Search count</th>
											<th>Click-through count</th>
											<th>Click-through rate</th>
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
									for(int i = 0; i < keywordList.size(); i++) {
										ClickKeywordHitVO vo = keywordList.get(i);
										Map<String, String> typeCountMap = typeCountMapList.get(i);
									%>
										<tr>
											<td><%=i+1 %></td>
											<td><a href="javascript:goDetail('<%=vo.getKeyword() %>')"><%=vo.getKeyword() %></a></td>
											<td><%=keywordSearchPvList.get(i) %></td>
											<td><%=vo.getCount() %></td>
											<td><%=kewordCtrList.get(i) %></td>
											<%
											for(String[] clickType : clickTypeList) {
											%>
											<td><%=typeCountMap.get(clickType[0]) %></td>
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
	</div>
</body>
</html>
