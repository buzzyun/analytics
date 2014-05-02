<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="
java.util.*,
org.fastcatsearch.analytics.db.vo.*,
org.fastcatsearch.analytics.analysis.config.SiteListSetting.SiteSetting,
org.fastcatsearch.analytics.analysis.config.StatisticsSettings.CategorySetting
" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%


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
			<c:param name="mcat" value="systemError" />
		</c:import>
			
		<div id="content">
			<div class="container">
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
									int i = 1;
									%>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
									<tr>
										<td><%=i++ %></td>
										<td>2014.04.02 12:30:00</td>
										<td>[2014-05-02 11:25:53,525 INFO] (CatServer.java:190) CatServer started!
[2014-05-02 11:26:14,911 DEBUG] (MainInterceptor.java:50) REDIRECT >> GET, target = http://localhost:8081/analytics/settings/sites.html</td>
									</tr>
								</tbody>
							
							</table>
						</div>
						
					</div>
					[ page navigation ]
					
				</div>
				<!-- /Page Content -->
			</div>
			<!-- /.container -->
		</div>
	</div>

</body>
</html>