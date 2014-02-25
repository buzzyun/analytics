<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="
java.util.Map,
java.util.HashMap,
java.util.List,
org.fastcatgroup.analytics.db.vo.UserAccountVO
" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	String menuId = "user";
	List<UserAccountVO> userList = (List<UserAccountVO>)request.getAttribute("userList");
%>
<c:set var="ROOT_PATH" value="../.." scope="request"/>
<c:import url="../inc/common.jsp" />
<html>
<head>
<c:import url="../inc/header.jsp" />

<script type="text/javascript">
$(document).ready(function(){
	$("#new-user-form").validate();
	$("#update-user-form").validate();
});

function showUpdateUserModal(id){
	$.ajax({
		url:"get-user.html",
		type:"POST",
		data:{
			id: id},
		dataType:"json",
		success:function(response) {
			var id = response["id"];
			var userName = response["name"];
			var userId = response["userId"];
			var email = response["email"];
			var sms = response["sms"];
			console.log("response>", response);
 			$("div#userEdit input[name|=name]").val(userName);
 			$("div#userEdit input[name|=id]").val(id);
 			$("div#userEdit input[name|=userId]").val(userId);
 			$("div#userEdit input[name|=email]").val(email);
 			$("div#userEdit input[name|=sms]").val(sms);
			$("#userEdit").modal({show: true, backdrop: 'static'});
		}, fail:function(response){
 			noty({text: "Can't submit data error : ["+response+"] error", layout:"topRight", timeout: 5000});
		}
	});
}

function update(formId, mode) {
	
	var form = $("#"+formId)[0];
	
	$.ajax({
		url:"update-user.html",
		type:"POST",
		data:{
			mode:mode
			,id:form.id.value
			,name:form.name.value
			,userId:form.userId.value
			,email:form.email.value
			,sms:form.sms.value
			,password:form.password.value
		}, dataType:"json",
		success:function(response) {
			if(response["success"] == "true") {
	 			noty({text: "update successed", layout:"topRight", timeout: 2000});
	 			setTimeout(function() {
					location.href = location.href;
	 			},2000);
			} else {
	 			noty({text: "update failed !", layout:"topRight", timeout: 5000});
			}
		}, fail:function(response){
		}
	});
}

</script>
</head>
<body>
<c:import url="../inc/mainMenu.jsp" />
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
						<h3>Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<!--=== Page Content ===-->
				<div class="tabbable tabbable-custom tabs-left">
					<div class="tab-content">
						<div class="tab-pane active" id="tab_3_1">
							<div class="col-md-12">
								<div class="widget box">
									<div class="widget-content no-padding">
										<div class="dataTables_header clearfix">
											<div class="input-group col-md-12">
												<button class="btn btn-sm" data-toggle="modal" data-target="#userNew" data-backdrop="static">
												 <span class="icon-user"></span> New User
												 </button>
											</div>
										</div>
										<table class="table table-bordered">
											<thead>
												<tr>
													<th>User Name</th>
													<th>User Id</th>
													<th>Email</th>
													<th>Sms</th>
													<th></th>
												</tr>
											</thead>	
											<tbody>
											<%
 											for(int userInx=0;userInx<userList.size();userInx++) {
 												
 												UserAccountVO vo = userList.get(userInx);
 												
												int id = vo.id;
												String userId = vo.userId;
												String userName = vo.name;
												String email = vo.email;
												String sms = vo.sms;
												
											%>
												<tr>
													<td><strong><%=userName %></strong></td>
													<td><%=userId %></td>
													<td><%=email %></td>
													<td><%=sms %></td>
													<td><a href="javascript:showUpdateUserModal('<%=id%>')">Edit</a></td>
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

	<div class="modal" id="userEdit">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Edit User</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal" role="form" id="update-user-form">
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
								<input type="text" class="form-control required" id="userId" name="userId" placeholder="User Id" minlength="4" readonly>
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
						<div class="form-group">
							<label for="password" class="col-sm-3 control-label">Password</label>
							<div class="col-sm-4">
								<input type="password" class="form-control" id="password2" name="password" placeholder="Password" minlength="4">
							</div>
							<div class="col-sm-4">
								<input type="password" class="form-control" id="confirmPassword2" name="confirmPassword" placeholder="Confirm Password" equalTo="[id='password2']" minlength="4">
							</div>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-danger pull-left" onclick="update('update-user-form','delete')">Remove</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button type="button" class="btn btn-primary" onclick="update('update-user-form','update')">Save changes</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
</body>
</html>