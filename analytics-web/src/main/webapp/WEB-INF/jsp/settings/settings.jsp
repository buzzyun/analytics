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
						<li class=""><a href="javascript:{}"><span class="icon-plus-sign"></span> New Site </a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="tab_3_1">
							<div class="col-md-12">
								<div class="widget box">
									<div class="widget-content no-padding">
										<div class="dataTables_header clearfix">
											<div class="input-group col-md-12">
												<button class="btn btn-sm" data-toggle="modal" data-target="#userNew" data-backdrop="static">
												 <span class="icon-plus-sign"></span> New Category
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

	<div class="modal" id="userNew">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">New User</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="new-user-form">
						<input type="hidden" name="mode" value=""/>
						<input type="hidden" name="id" value="-1"/>
						<div class="form-group">
							<label for="name" class="col-sm-3 control-label">Name</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="name" name="name" placeholder="Name" minlength="3">
							</div>
						</div>
						<div class="form-group">
							<label for="userId" class="col-sm-3 control-label">User Id</label>
							<div class="col-sm-9">
								<input type="text" class="form-control required" id="userId" name="userId" placeholder="User Id" minlength="4">
							</div>
						</div>
						<div class="form-group">
							<label for="password" class="col-sm-3 control-label">Password</label>
							<div class="col-sm-4">
								<input type="password" class="form-control required" id="password" name="password" placeholder="Password" minlength="4">
							</div>
							<div class=" col-sm-4">
								<input type="password" class="form-control required" id="confirmPassword" name="confirmPassword" placeholder="Confirm Password" minlength="4" equalTo="[name='password']">
							</div>
						</div>
						
						<div class="form-group">
							<label for="email" class="col-sm-3 control-label">E-mail</label>
							<div class="col-sm-9">
								<input type="text" class="form-control email" id="email" name="email" placeholder="E-mail">
							</div>
						</div>
						<div class="form-group">
							<label for="sms" class="col-sm-3 control-label">SMS</label>
							<div class="col-sm-9">
								<input type="text" class="form-control number" id="sms" name="sms" placeholder="SMS">
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