<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<%@ page import="org.fastcatgroup.analytics.db.vo.*" %>
<%@ page import="org.fastcatgroup.analytics.db.vo.RankKeywordVO.RankDiffType" %>

<%
String categoryId = (String) request.getAttribute("categoryId");

int start = (Integer) request.getAttribute("start");
int length = (Integer) request.getAttribute("length");
int pageNo = (Integer) request.getAttribute("pageNo");

String timeId = (String) request.getAttribute("timeId");
int totalCount = (Integer) request.getAttribute("totalCount");
List<RankKeywordVO> list = (List<RankKeywordVO>) request.getAttribute("list");

String menuId = (String) request.getAttribute("menuId");
String pageTitle = "";
if(menuId.equals("all")){
	pageTitle = "All Keyword Rank";
}else if(menuId.equals("new")){
	pageTitle = "New Keyword Rank";
}else if(menuId.equals("hot")){
	pageTitle = "Hot Keyword Rank";
}else if(menuId.equals("down")){
	pageTitle = "Down Keyword Rank";
}
%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>

function goPage(uri, pageNo){
	var form = $("#pagenationForm");
	form[0].pageNo.value=pageNo;
	form.submit();
}
	
$(document).ready(function(){
	fillCategoryList('${siteId}', $("#select_category"), '<%=categoryId %>');
	
	
	$('#date1').DatePicker({
		flat: true,
		date: new Date(),
		calendars: 1,
		//mode: 'range',
		view: 'days',
		//lselect: 'days',
		starts: 1,
		onChange: function(formated) {
			$('#select_days').text(formated[0] +' ~ ' + formated[1]);
		}
	});
});
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="rank" />
			<c:param name="mcat" value="${menuId}" />
		</c:import>

		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Keyword Rank</a></li>
						<li><a href="#">
						<%=pageTitle %>
						
						</a></li>
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
						<h3><%=pageTitle %></h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					<form method="get">
						<div class="col-md-12">
							<div class="form-inline">
								<select id="select_category" name="categoryId" class="select_flat fcol2"></select>
								<!-- <input type="button" class="btn btn-sm btn-warning" value="DAY"> 
								<input type="button" class="btn btn-sm btn-default" value="WEEK">
								<input type="button" class="btn btn-sm btn-default" value="MONTH">
								<input type="button" class="btn btn-sm btn-default" value="YEAR"> -->
								<input class="form-control fcol1-2 " size="16" type="text" name="timeId" value="<%=timeId %>" >
								<input type="submit" class="btn btn-primary" value="Submit">
							</div>
						</div>
						<!-- <div class="bottom-space"></div>
						<div class="col-md-12">
							<div class="form-inline">
								<input type="text" class="form-control fcol3" placeholder="Keyword..">
								<input type="submit" class="btn btn-primary" value="Submit">
							</div>
						</div> -->
						<div id="date1" class="pull-right"></div>
						<div id="select_days" style="text-align:left;font-size:16px;"></div>
					</form>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : <%=timeId %>
								</h4>
							</div>
							<div class="widget-content">
							Total: <%=totalCount %>
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
										for(int i = 0;i < list.size(); i++){
											RankKeywordVO vo = list.get(i);
										%>
										<tr>
											<td><%=(pageNo -1) * length + i + 1 %></td>
											<td><%=vo.getKeyword() %></td>
											<td><%=vo.getCount() %></td>
											<td><%=vo.getRankDiffType() %>
											
											<%=vo.getRankDiffType() == RankDiffType.NEW ? "" : vo.getRankDiff() %></td>
											<td><%=vo.getCountDiff() %></td>
										</tr>
										<%
										}
										%>
									</tbody>
								</table>
								<%
								
								request.setAttribute("pageSize", 10);
								
								%>
								<div class="table-footer">
									<div class="col-md-12">
									Rows 
									<% if(list.size() > 0) { %>
									<%=start + 1 %> - <%=start + list.size() %> of <%=totalCount %> 
									<% } else { %>
									Empty
									<% } %>
									
									<jsp:include page="../../inc/pagenation.jsp" >
									 	<jsp:param name="pageNo" value="${pageNo }"/>
									 	<jsp:param name="totalSize" value="<%=totalCount %>" />
										<jsp:param name="pageSize" value="${pageSize }" />
										<jsp:param name="width" value="5" />
										<jsp:param name="callback" value="goPage" />
										<jsp:param name="requestURI" value="" />
									 </jsp:include>
									</div>
								</div>	
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<form id="pagenationForm">
	<input type="hidden" name="categoryId" value="${categoryId }"/>
	<input type="hidden" name="timeId" value="${timeId }"/>
	<input type="hidden" name="pageNo" value="${pageNo }"/>
	<input type="hidden" name="start" value="${start }"/>
	<input type="hidden" name="length" value="${length }"/>
	</form>
</body>
</html>
