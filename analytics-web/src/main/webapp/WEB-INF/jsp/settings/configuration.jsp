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
	$("#config-form").attr("method","post");
	
	$("#btn-apply-property").click(function() {
		var form = $("#config-form");
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
												<!--
												<button class="btn btn-sm" id="btn-add-property" data-toggle="modal" data-target="#propertyNew" data-backdrop="static">
												<span class="icon-plus"></span> Add Property
												</button>
												-->
												<button class="btn btn-sm" id="btn-apply-property">
												<span class="icon-ok"></span> Save
												</button>
												<button class="btn btn-sm" id="btn-restart">
												<span class="icon-play-circle"></span> Restart
												</button>
											</div>
										</div>
										<form class="form-horizontal" id="config-form" role="form">
										<input type="hidden" name="mode" value="update"/>
										<table class="table table-bordered">
											<thead>
												<tr>
													<th>Key</th>
													<th>Value</th>
													<!--
													<th></th>
													-->
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
														<input type="hidden" name="key<%=propInx%>" value="<%=key %>"/>
														<label><%=key%></label>
													</td>
													<td>
														<input type="text" class="form-control" name="value<%=propInx%>" value="<%=value %>"/>
													</td>
													<!--
													<td>
														<button class="btn btn-sm">
														<span class="icon-minus-sign"></span>
														</button>
													</td>
													-->
												</tr>
											<%
											}
											%>
											</tbody>
										</table>
										</form>
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
	<div class="modal" id="propertyNew">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">New Property</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="new-prop-form">
						<input type="hidden" name="mode" value=""/>
						<input type="hidden" name="id" value="-1"/>
						<div class="form-group">
							<label for="key" class="col-sm-3 control-label">Key</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="propertyKey" name="propertyKey" placeholder="Property Key" minlength="1">
							</div>
						</div>
						<div class="form-group">
							<label for="key" class="col-sm-3 control-label">Value</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="propertyValue" name="propertyValue" placeholder="Property Value" minlength="1">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-primary" onclick="update('new-user-form','update')">Create User</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
</body>
</html>