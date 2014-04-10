<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="
java.util.*,
org.fastcatgroup.analytics.db.vo.*,
org.fastcatgroup.analytics.analysis.config.SiteCategoryListConfig.SiteCategoryConfig
" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
String menuId = "settings";
List<SiteCategoryConfig> siteList = (List<SiteCategoryConfig>)request.getAttribute("siteList");
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
						<li class="active"><a href="javascript:showSiteTab('<%=siteId%>')"><%=siteName %></a></li>
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
											</div>
										</div>
										<table class="table table-bordered">
											<thead>
												<tr>
													<th>Category Id</th>
													<th>Category Name</th>
													<th></th>
												</tr>
											</thead>	
											<tbody>
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
						<input type="hidden" name="id" value="-1"/>
						<div class="form-group">
							<label for="siteId" class="col-sm-3 control-label">Site Id</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="siteId" name="siteId" placeholder="Site Id" minlength="4">
							</div>
						</div>
						<div class="form-group">
							<label for="siteName" class="col-sm-3 control-label">Site Name</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="siteName" name="siteName" placeholder="Site Name" minlength="4">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-primary" onclick="update('new-site-form','update')">Create Site</button>
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
						<input type="hidden" name="id" value="-1"/>
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
					<button type="button" class="btn btn-primary" onclick="update('new-category-form','update')">Create Category</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
</body>
</html>