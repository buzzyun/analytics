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
String menuId = "settings";
List<SiteSetting> siteList = (List<SiteSetting>)request.getAttribute("siteList");
String currentSiteId = (String)request.getAttribute("currentSiteId");
List<CategorySetting> categoryList = (List<CategorySetting>)request.getAttribute("categoryList");
%>
<c:set var="ROOT_PATH" value="../.." scope="request"/>
<c:import url="../inc/common.jsp" />
<html>
<head>
<c:import url="../inc/header.jsp" />

<script type="text/javascript">
$(document).ready(function(){
	$("form#site-form span.icon-edit").parent("a.btn").click(function() {
		var siteId = $(this).find("span").attr("id").substring(8);
		var siteName = $(this).find("input#siteName").val();
		var editForm = $("form#edit-site-form");
		editForm[0].siteId.value=siteId;
		editForm[0].siteName.value=siteName;
	});
	$("form#site-form span.icon-minus-sign").parent("a.btn").click(function() {
		var siteId = $(this).find("span").attr("id").substring(11);
		removeSite(siteId);
	});
});

function removeSite(siteId) {
	if(confirm("DANGER! This site and category will remove. Are you OK ?")) {
		if(confirm("Are you sure ?")) {
			var form = $("form#site-form");
			$.ajax({
				url:"update-setting.html"
				,type:"POST"
				,data:{
					mode:"removeSite"
					,siteId:siteId
				}, dataType:"json",
				success:function(response) {
					if(response["success"] == "true") {
						location.href = "sites.html";
					} else {
			 			noty({text: "update failed !", layout:"topRight", timeout: 5000});
					}
				}, fail:function(response){
				}
			});
		}
	}
}

function update(formId, mode) {
	var form = $("#"+formId);
	var valid = false;
	if(mode == "remove") { 
		valid = true; 
	} else {
		valid = form.valid();
	}
	form = form[0];
	var elements = form.elements;
	var data = {};
	for(var inx = 0; inx < elements.length; inx++) {
		data[elements[inx].name] = elements[inx].value;
	}
	data["mode"] = mode;
	
	if(valid) {
		$.ajax({
			url:"update-setting.html",
			type:"POST",
			data:data, dataType:"json",
			success:function(response) {
				if(response["success"] == "true") {
					if(mode=="updateSite") {
						location.href = "sites.html?siteId="+data["siteIdNew"];
					} else {
						location.href = location.href;
					}
				} else {
		 			noty({text: "update failed !", layout:"topRight", timeout: 5000});
				}
			}, fail:function(response){
			}
		});
	}
}
</script>
</head>
<body>
<c:import url="../inc/mainMenu.jsp" />
<div id="container">
		<c:import url="${ROOT_PATH}/settings/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="settings" />
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
						<h3>Sites Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<!--=== Page Content ===-->
				<div class="tabbable tabbable-custom tabbable-full-width">
					<ul class="nav nav-tabs"></ul>
					<div class="tab-content">
						<div class="tab-pane active" id="tab_3_1">
							<div class="col-md-12">
								<div class="widget box">
									<div class="widget-content no-padding">
										<form class="form-horizontal" role="form" id="site-form">
										<input type="hidden" name="siteId" value="<%=currentSiteId %>"/>
										<div class="dataTables_header clearfix">
											<div class="input-group col-md-12">
												<button class="btn btn-sm" data-toggle="modal" data-target="#siteNew" data-backdrop="static"">
													<span class="icon-plus-sign"></span> Add New Site 
												</button>
											</div>
										</div>
										<table class="table table-bordered">
											<thead>
												<tr>
													<th>Site Id</th>
													<th>Site Name</th>
													<th></th>
												</tr>
											</thead>	
											<tbody>
											<%
											
											for(int siteInx = 0; siteInx < siteList.size() ; siteInx++) {
												SiteSetting siteConfig = siteList.get(siteInx);
												String siteName = siteConfig.getName();
												String siteId = siteConfig.getId();
												%>
													<tr>
														<td> 
															<div>
															<%=siteId%> 
															</div>
														</td>
														<td> 
															<div>
															<%=siteName %> 
															</div>
														</td>
														<td>
															<div>
															<a class="btn btn-sm" href="javascript:{}" data-toggle="modal" data-target="#siteEdit" data-backdrop="static">
																<span class="icon-edit" id="btn-add-<%=siteId%>"></span>
																<input type="hidden" id="siteName" value="<%=siteName%>"/>
															</a>
															<a class="btn btn-sm" href="javascript:{}">
																<span class="icon-minus-sign text-danger" id="btn-remove-<%=siteId%>"></span>
															</a>
															</div>
														</td>
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

	<div class="modal" id="siteNew">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">New Site</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="new-site-form">
						<input type="hidden" name="mode" value=""/>
						<div class="form-group">
							<label for="siteId" class="col-sm-3 control-label">Site Id</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="siteId" name="siteId" placeholder="Site Id">
							</div>
						</div>
						<div class="form-group">
							<label for="siteName" class="col-sm-3 control-label">Site Name</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="siteName" name="siteName" placeholder="Site Name">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-primary" onclick="update('new-site-form','addSite')">Create Site</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	
	<div class="modal" id="siteEdit">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Edit Site</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="edit-site-form">
						<input type="hidden" name="siteId" value="${currentSiteId}"/>
						<input type="hidden" name="mode" value=""/>
						<div class="form-group">
							<label for="siteId" class="col-sm-3 control-label">Site Id</label>
							<div class="col-sm-9">
								<label> ${currentSiteId} </label>
							</div>
						</div>
						<div class="form-group">
							<label for="siteName" class="col-sm-3 control-label">Site Name</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="siteName" name="siteName" value="${currentSiteName}" placeholder="Site Name">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-primary" onclick="update('edit-site-form','updateSite')">Update Site</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
</body>
</html>