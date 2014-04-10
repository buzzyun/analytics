<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="
java.util.*,
org.fastcatgroup.analytics.db.vo.*,
org.fastcatgroup.analytics.env.Settings
" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	String menuId = "configuration";
	Iterator<Object> configKeys = (Iterator<Object>)request.getAttribute("configKeys");
	Settings configProperties = (Settings)request.getAttribute("configProperties");
%>
<c:set var="ROOT_PATH" value="../.." scope="request"/>
<c:import url="../inc/common.jsp" />
<html>
<head>
<c:import url="../inc/header.jsp" />

<script type="text/javascript">
$(document).ready(function(){
});

</script>
</head>
<body>
<c:import url="../inc/mainMenu.jsp" />
<div id="container">
		<c:import url="${ROOT_PATH}/settings/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="configuration" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Settings</a>
						</li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->
				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>System Configuration</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<!--=== Page Content ===-->
				<div class="tabbable tabbable-custom tabbable-full-width">
					<div class="tab-content">
						<div class="tab-pane active" id="tab_3_1">
							<div class="col-md-12">
								<div class="widget box">
									<div class="widget-content no-padding">
										<div class="dataTables_header clearfix">
											<div class="input-group col-md-12">
												<button class="btn btn-sm" data-toggle="modal" data-target="#userNew" data-backdrop="static">
												 <span class="icon-plus"></span> Add Property
												 </button>
												<button class="btn btn-sm" data-toggle="modal" data-target="#userNew" data-backdrop="static">
												 <span class="icon-play-circle"></span> Save & Restart
												 </button>
											</div>
										</div>
										<table class="table table-bordered">
											<thead>
												<tr>
													<th>Key</th>
													<th>Value</th>
												</tr>
											</thead>	
											<tbody>
											<%
 											for(int propInx=0;configKeys.hasNext();propInx++) {
 												String key = String.valueOf(configKeys.next());
 												String value = configProperties.getString(key,"");
											%>
												<tr>
													<td>
															<input type="text" class="form-control" value="<%=key %>"/>
													</td>
													<td>
															<input type="text" class="form-control" value="<%=value %>"/>
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
				</div>
				<!-- /Page Content -->
			</div>
			<!-- /.container -->
		</div>
	</div>
</body>
</html>