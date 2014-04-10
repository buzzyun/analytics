<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="
java.util.*,
org.fastcatgroup.analytics.db.vo.*,
org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig,
org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.CategoryConfig
" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
String menuId = "settings";
List<SiteCategoryConfig> siteList = (List<SiteCategoryConfig>)request.getAttribute("siteList");
String currentSiteId = (String)request.getAttribute("currentSiteId");
List<CategoryConfig> categoryList = (List<CategoryConfig>)request.getAttribute("categoryList");
%>
<c:set var="ROOT_PATH" value="../.." scope="request"/>
<c:import url="../inc/common.jsp" />
<html>
<head>
<c:import url="../inc/header.jsp" />

<script type="text/javascript">
$(document).ready(function(){
	$("form#category-form span.icon-minus-sign").click(function() {
		var categoryId = /btn-remove-(.*)/.exec($(this).attr("id"))[1];
		if(confirm("Category will remove. Are you OK?")) {
			removeCategory(categoryId);
		}
	});
});

function showSiteTab(siteId){
	location.href = "settings.html?siteId="+siteId;
}
function removeCategory(categoryId) {
	var form = $("form#category-form");
	console.log(form[0].siteId);
	console.log(form[0].siteId.value);
	console.log("siteId:"+form[0].siteId.value+" / categoryId : "+categoryId);
	$.ajax({
		url:"update-setting.html"
		,type:"POST"
		,data:{
			mode:"remove"
			,siteId:form[0].siteId.value
			,categoryId:categoryId
		}, dataType:"json",
		success:function(response) {
			if(response["success"] == "true") {
				location.href = location.href;
			} else {
	 			noty({text: "update failed !", layout:"topRight", timeout: 5000});
			}
		}, fail:function(response){
		}
	});
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
					location.href = location.href;
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
						<h3>Site Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<!--=== Page Content ===-->
				<div class="tabbable tabbable-custom tabbable-full-width">
					<ul class="nav nav-tabs">
						<%
						for(int siteInx = 0; siteInx < siteList.size() ; siteInx++) {
							SiteCategoryConfig siteConfig = siteList.get(siteInx);
							String siteName = siteConfig.getSiteName();
							String siteId = siteConfig.getSiteId();
						%>
						<li class="<%=siteId.equals(currentSiteId)?"active":""%>"><a href="javascript:showSiteTab('<%=siteId%>')"><%=siteName %></a></li>
						<%
						}
						%>
						<li class=""><a href="javascript:{}" data-toggle="modal" data-target="#siteNew" data-backdrop="static"><span class="icon-plus"></span> New Site </a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="tab_3_1">
							<div class="col-md-12">
								<div class="widget box">
									<div class="widget-content no-padding">
										<div class="dataTables_header clearfix">
											<div class="input-group col-md-12">
												<button class="btn btn-sm" data-toggle="modal" data-target="#categoryNew" data-backdrop="static">
												 <span class="icon-plus"></span> New Category
												 </button>
												<button class="btn btn-sm" data-toggle="modal" data-target="#categoryNew" data-backdrop="static">
												 <span class="icon-ok"></span> Apply Category
												 </button>
											</div>
										</div>
										<form class="form-horizontal" role="form" id="category-form">
										<input type="hidden" name="siteId" value="<%=currentSiteId %>"/>
										<table class="table table-bordered">
											<thead>
												<tr>
													<th>Category Id</th>
													<th>Category Name</th>
													<th></th>
												</tr>
											</thead>	
											<tbody>
											<%
											for( int cateInx = 0; cateInx < categoryList.size() ; cateInx++ ) {
												CategoryConfig category = categoryList.get(cateInx);
												if("_root".equals(category.getId())) {
													continue;
												}
											%>
												<tr>
													<td>
														<input type="hidden" name="categoryIdOrg<%=cateInx %>" value="<%=category.getId() %>"/>
														<input class="form-control" type="text" name="categoryIdGet<%=cateInx %>" value="<%=category.getId() %>"/>
													</td>
													<td>
														<input class="form-control" type="text" name="categoryNameGet<%=cateInx %>" value="<%=category.getName() %>"/>
													</td>
													<td>
														<span class="icon-minus-sign" id="btn-remove-<%=category.getId()%>"></span>
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
	
	<div class="modal" id="categoryNew">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">New Category</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="new-category-form">
						<input type="hidden" name="mode" value=""/>
						<input type="hidden" name="siteId" value="<%=currentSiteId%>"/>
						<div class="form-group">
							<label for="categoryId" class="col-sm-3 control-label">Category Id</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="categoryId" name="categoryId" placeholder="Category Id" minlength="4">
							</div>
						</div>
						<div class="form-group">
							<label for="categoryName" class="col-sm-3 control-label">Category Name</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="categoryName" name="categoryName" placeholder="Category Name" minlength="4">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-primary" onclick="update('new-category-form','add')">Create Category</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
</body>
</html>