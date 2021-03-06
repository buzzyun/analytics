<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.fastcatsearch.analytics.analysis.vo.*"%>
<%@page import="java.util.*"%>
<%
List<RankKeyword> rankList = (List<RankKeyword>) request.getAttribute("rankList");
String categoryId = request.getParameter("categoryId");
%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
$(document).ready(function(){
	fillCategoryList('${siteId}', $("#select_category"), '<%=categoryId %>');
});
</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="rank" />
			<c:param name="mcat" value="realtimeSearchKeyword" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Keyword Rank</a></li>
						<li><a href="#">Realtime</a></li>
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
						<h3>Realtime Keyword Rank</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" role="form" method="post" >
							<select id="select_category" class="select_flat select_flat-sm fcol2" name="categoryId"></select> 
							
							<input type="submit" class="btn btn-sm btn-primary" value="Submit">
						</form>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-content">
								<div>
									<table class="table table-striped table-bordered table-condensed">
										<thead>
											<tr>
												<th>#</th>
												<th>KEYWORD</th>
												<th>COUNT</th>
												<th>RANK CHANGE</th>
												<th>COUNT CHANGE</th>
											</tr>
										</thead>
										<tbody>
											<%
											if(rankList != null){
												for(RankKeyword rankKeyword : rankList){
												%>
												<tr>
													<td><%=rankKeyword.getRank() %></td>
													<td><%=rankKeyword.getKeyword() %></td>
													<td><%=rankKeyword.getCount()%></td>
													<td><%=rankKeyword.getRankDiffType()%> <%=rankKeyword.getRankDiff()%></td>
													<td><%=rankKeyword.getCountDiff() %></td>
												</tr>
												<%
												}
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
	</div>
</body>
</html>
