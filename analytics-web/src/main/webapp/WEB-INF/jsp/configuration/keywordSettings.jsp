<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*"%>

<%
	//JSONArray nodeList = (JSONArray) request.getAttribute("nodeList");
%>
<c:set var="ROOT_PATH" value="../.." />
<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />
	<div id="container">
		<c:import url="${ROOT_PATH}/manager/sideMenu.jsp">
			<c:param name="lcat" value="keyword" />
			<c:param name="mcat" value="settings" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> Manager</li>
						<li> Keyword Service</li>
						<li class="current"> Settings</li>
					</ul>

				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->

				<div class="widget">
					<div class="widget-header">
						<h4>Service Settings</h4>
					</div>
					
					<div class="widget-content">
						<table class="table table-hover table-bordered table-vertical-align-middle">
							<thead>
								<tr>
									<th>#</th>
									<th>Category ID</th>
									<th>Name</th>
									<th>Realtime<br>Popular Keyword Service</th>
									<th colspan="2">Popular Keyword Service</th>
									<th>Relate Keyword Service</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>1</td>
									<td>ROOT</td>
									<td>ROOT</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
									<td>
										<input type="text" class="form-control fcol2" value="3D, 1W"/>
									</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
								</tr>
								<tr>
									<td>2</td>
									<td>total</td>
									<td>통합검색</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
									<td>
										<input type="text" class="form-control fcol2" value="3D, 1W"/>
									</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
								</tr>
								<tr>
									<td>3</td>
									<td>mobile</td>
									<td>모바일 검색</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
									<td>
										<input type="text" class="form-control fcol2" value="3D, 1W"/>
									</td>
									<td>
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true"> Yes
										</label>
									</td>
								</tr>
							</tbody>
						</table>
						
						<!-- <div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-3 control-label">Keyword Suggestions Service:</label>
									<div class="col-md-9">
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true" /> Yes
										</label>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-3 control-label">AD Keyword Service:</label>
									<div class="col-md-9">
										<label class="checkbox">
											<input type="checkbox" name="enable" class="form-control" value="true" /> Yes
										</label>
									</div>
								</div>
							</div>
						</div> -->
					</div>
				</div>
				
				<div class="widget">
					<div class="widget-header">
						<h4>Common Settings</h4>
					</div>
					<div class="widget-content">
						<div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Service Node List :</label>
									<div class="col-md-5"><input type="text" name="serviceNodeList" class="form-control required" value="node1"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- <div class="widget">
					<div class="widget-header">
						<h4>Keyword Suggestions</h4>
					</div>
					<div class="widget-content">
						
					</div>
				</div>
				
				<div class="widget">
					<div class="widget-header">
						<h4>AD Keyword</h4>
					</div>
					<div class="widget-content">
						
					</div>
				</div> -->
				
				<div class="form-actions">
					<input type="submit" value="Update Settings" class="btn btn-primary pull-right">
				</div>
				<!-- /Page Content -->
			</div>
		</div>
	</div>
</body>
</html>