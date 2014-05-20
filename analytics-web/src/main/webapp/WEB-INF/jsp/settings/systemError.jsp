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

List<SystemErrorVO> systemErrorList = (List)request.getAttribute("systemErrorList");
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Integer totalSize = (Integer)request.getAttribute("totalSize");
Integer pageSize = (Integer)request.getAttribute("pageSize");
Integer pageNo = (Integer)request.getAttribute("pageNo");
%>
<c:set var="ROOT_PATH" value="../.." scope="request"/>
<c:import url="../inc/common.jsp" />
<html>
<head>
<c:import url="../inc/header.jsp" />

<script type="text/javascript">

function goPage(form,pageNo) {
	var form = $("div#content form");
	form[0].pageNo.value=pageNo;
	form.submit();
}
</script>
</head>
<body>
<c:import url="../inc/mainMenu.jsp" />
<div id="container">
		<c:import url="${ROOT_PATH}/settings/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="systemError" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<form>
					<input type="hidden" name="pageNo" value="${pageNo }"/>
				</form>
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">System Error</a>
						</li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->
				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>System Error</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<!--=== Page Content ===-->
				<div class="col-md-12">
					<div class="widget box">
						<div class="widget-content no-padding">
							<table class="table table-bordered table-highlight-head">
								<thead>
									<tr>
										<th>#</th>
										<th>Time</th>
										<th>Error</th>
									</tr>
								</thead>
								<tbody>
									<%
									for(int inx=0;inx < systemErrorList.size(); inx++) {
									%>
										<%
										SystemErrorVO vo = systemErrorList.get(inx);
										%>
										<tr>
											<td><%=totalSize - (pageSize * (pageNo - 1)) - inx %></td>
											<td><%=dateFormat.format(vo.getTime()) %></td>
											<td><%=vo.getErrorMessage()%></td>
										</tr>
									<%
									}
									%>
								</tbody>
							
							</table>
						</div>
						
					</div>
					<jsp:include page="../inc/pagenation.jsp" >
						<jsp:param name="pageNo" value="${pageNo }"/>
						<jsp:param name="totalSize" value="${totalSize}" />
						<jsp:param name="pageSize" value="${pageSize }" />
						<jsp:param name="width" value="5" />
						<jsp:param name="callback" value="goPage" />
						<jsp:param name="requestURI" value="" />
					 </jsp:include>
				</div>
				<!-- /Page Content -->
			</div>
			<!-- /.container -->
		</div>
	</div>

</body>
</html>