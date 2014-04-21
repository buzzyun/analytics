<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*" %>
<%@ page import="org.fastcatgroup.analytics.db.vo.*" %>
<%@ page import="org.fastcatgroup.analytics.db.vo.RankKeywordVO.RankDiffType" %>
<%@ page import="org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting" %>

<%
String categoryId = (String) request.getAttribute("categoryId");

int start = (Integer) request.getAttribute("start");
int length = (Integer) request.getAttribute("length");
int pageNo = (Integer) request.getAttribute("pageNo");

String timeText = (String) request.getAttribute("timeText");
String timeViewType = (String) request.getAttribute("timeViewType");

int totalCount = (Integer) request.getAttribute("totalCount");
List<RankKeywordVO> list = (List<RankKeywordVO>) request.getAttribute("list");

String keywordType = (String) request.getAttribute("keywordType");

String[] typeArray = (String[]) request.getAttribute("typeArray");

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

function showKeywordTab(keywordType){
	var form = $("#pagenationForm");
	form[0].keywordType.value=keywordType;
	form.submit();
}

$(document).ready(function(){
	fillCategoryList('${siteId}', $("#select_category"), '<%=categoryId %>');
	
	
	var pickmenup_options = {
		calendars: 3,
		mode: 'days',
		format: 'Y.m.d',
		first_day: 1,
		position: 'bottom',
		hide_on_select	: true 
	};
	$("#timeText").pickmeup(pickmenup_options);
	
	$("#timeViewTypeList button").on("click", function(){
		$(this).addClass("btn-primary");
		$(this).removeClass("btn-default");
		
		$(this).siblings().addClass("btn-default");
		$(this).siblings().removeClass("btn-primary");
		
		$("#timeViewTypeList input[name=timeViewType]").val($(this).text().charAt(0));
		
		//TODO 달력의 날짜를 확인하여, 주,월,년의 경우 시작/끝 날짜를 조정해준다.
		
		
	});
});
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="rank" />
			<c:param name="mcat" value="searchKeyword" />
		</c:import>

		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Keyword Rank</a></li>
						<li><a href="#">Search Keyword</a></li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title page-title-sm">
						<h3>Search Keyword</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					<form method="get">
						<div class="col-md-12">
							<div class="form-inline">
								<select id="select_category" name="categoryId" class="select_flat fcol2"></select>
								<input class="form-control fcol1-2 " size="16" type="text" id="timeText" name="timeText" value="<%=timeText %>" >
								<div id="timeViewTypeList" class="btn-group">
									<button type="button" class="btn <%="H".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Hourly</button>
									<button type="button" class="btn <%="D".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Daily</button>
									<button type="button" class="btn <%="W".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Weekly</button>
									<button type="button" class="btn <%="M".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Monthly</button>
									<button type="button" class="btn <%="Y".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Yearly</button>
									<input type="hidden" name="timeViewType" value="<%=timeViewType %>">
								</div>
								<input type="submit" class="btn btn-primary" value="Submit">
							</div>
						</div>
					</form>
				</div>
				
				
				<div class="tabbable tabbable-custom tabbable-full-width" id="schema_tabs">
					<ul class="nav nav-tabs">
					
						<%
						for(int typeInx=0; typeInx < typeArray.length; typeInx++ ) {
						%>
							<li class="<%=keywordType.equals(typeArray[typeInx]) ? "active" : "" %>"><a href="javascript:showKeywordTab('<%=typeArray[typeInx]%>')"><%=typeArray[typeInx].toUpperCase()%></a></li>
						<%
						}
						%>
					</ul>
					<div class="tab-content row">
						
						<!--=== fields tab ===-->
						<div class="tab-pane active">
							<div class="col-md-12">
								<div class="widget">
									<div class="widget-header">
										<h4>
											<i class="icon-calendar"></i> Period : <%=timeText %>
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
		</div>
	</div>
	<form id="pagenationForm">
	<input type="hidden" name="categoryId" value="${categoryId }"/>
	<input type="hidden" name="keywordType" value="${keywordType }"/>
	<input type="hidden" name="timeViewType" value="${timeViewType }"/>
	<input type="hidden" name="timeText" value="${timeText }"/>
	<input type="hidden" name="pageNo" value="${pageNo }"/>
	<input type="hidden" name="start" value="${start }"/>
	<input type="hidden" name="length" value="${length }"/>
	</form>
</body>
</html>
