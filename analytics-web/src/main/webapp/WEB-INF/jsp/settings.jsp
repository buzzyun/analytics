<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="inc/common.jsp" />
<html>
<head>
<c:import url="inc/header.jsp" />
</head>
<body>
<c:import url="inc/mainMenu.jsp" />
<div id="container" class="sidebar-closed">
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
						<h3>Admin Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->

				<!--=== Page Content ===-->
				
				<div class="col-md-12">
					<div class="widget">
						<div class="widget-header">
							<h4>Account</h4>
						</div>
						<div class="widget-content no-padding">
							<div class="dataTables_header clearfix">
									<div class="input-group col-md-12">
									<a href="javascript:void(0);" class="btn btn-sm"><span
										class="glyphicon glyphicon-plus-sign"></span> Add User</a>
										&nbsp;
									<a href="javascript:void(0);" class="btn btn-sm"><span
										class="glyphicon glyphicon-minus-sign"></span> Remove User</a>
										&nbsp;
								</div>
							</div>
							
							<table class="table table-hover table-bordered table-checkable">
								<thead>
									<tr>
										<th class="checkbox-column">
											<input type="checkbox" class="uniform">
										</th>
										<th>ID</th>
										<th>Name</th>
										<th>Level</th>
										<th>E-Mail</th>
										<th>SMS</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="checkbox-column">
											<input type="checkbox" class="uniform">
										</td>
										<td><strong>admin</strong></td>
										<td>Smith Black</td>
										<td>Admin</td>
										<td>admin@gmail.com</td>
										<td>010-9876-9876</td>
									</tr>
									<tr>
										<td class="checkbox-column">
											<input type="checkbox" class="uniform">
										</td>
										<td><strong>user1</strong></td>
										<td>John Doe</td>
										<td>Operator</td>
										<td>john@gmail.com</td>
										<td>010-9876-9876</td>
									</tr>
									<tr>
										<td class="checkbox-column">
											<input type="checkbox" class="uniform">
										</td>
										<td><strong>user2</strong></td>
										<td>Jane aire</td>
										<td>Operator</td>
										<td>jane@gmail.com</td>
										<td>010-9876-9876</td>
									</tr>
								</tbody>
							</table>
						</div> <!-- /.widget-content -->
						</div> <!-- /.widget -->
					<!-- <div class="form-actions">
						<input type="submit" value="Update Settings" class="btn btn-primary pull-right">
					</div> -->
						
					
				</div>
				
				
				<div class="col-md-12">
					<div class="widget">
						<div class="widget-header">
							<h4>SMS</h4>
						</div>
						<div class="widget-content no-padding">
							
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Call URL :</label>
									<div class="col-md-10">
									<div class="input-group">
										<input type="text" name="regular" class="form-control">
										<span class="input-group-btn">
											<button class="btn btn-default" type="button">Update</button>
										</span>
									</div>
									<span class="help-block">(Automatically append parameters like "?time=2013-01-01 12:00:00&message=This is a sample message")</span>
									</div>
								</div>
							</div>
							
						</div> <!-- /.widget-content -->
						</div> <!-- /.widget -->
					
				</div>

				<!-- /Page Content -->
			</div>
			<!-- /.container -->

		</div>
</div>
</body>
</html>