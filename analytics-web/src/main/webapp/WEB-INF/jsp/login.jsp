<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="inc/common.jsp" />
<html>
<head>
<c:import url="inc/header.jsp" />
<link href="${pageContext.request.contextPath}/resources/assets/css/login.css" rel="stylesheet" type="text/css" />
</head>
<body class="login">
	<!-- Logo -->
	<div class="logo">
		<strong>Analytics</strong>
	</div>
	<!-- /Logo -->

	<!-- Login Box -->
	<div class="box">
		<div class="content">
			<!-- Login Formular -->
			<form class="form-vertical login-form" action="<c:url value="/doLogin.html"/>" method="post">
				<!-- Title -->
				<h3 class="form-title">Log In to your Account</h3>

				<!-- Input Fields -->
				<div class="form-group">
					<div class="input-icon">
						<i class="icon-user"></i>
						<input type="text" name="host" class="form-control" placeholder="Address:Port" autofocus="autofocus" />
					</div>
				</div>
				<div class="form-group">
					<div class="input-icon">
						<i class="icon-user"></i>
						<input type="text" name="username" class="form-control" placeholder="Username" />
					</div>
				</div>
				<div class="form-group">
					<div class="input-icon">
						<i class="icon-lock"></i>
						<input type="password" name="password" class="form-control" placeholder="Password" />
					</div>
				</div>
				<!-- /Input Fields -->

				<!-- Form Actions -->
				<div class="form-actions">
					<label class="checkbox pull-left"><input type="checkbox" class="uniform" name="remember"> Remember me</label>
					<button type="submit" class="submit btn btn-primary pull-right">
						Sign In <i class="icon-angle-right"></i>
					</button>
				</div>
			</form>

		</div> <!-- /.content -->
	</div>
	<div class="footer" style="padding-top:100px">
		<div class="copy">
			<p class="address">
			Copyright(c) Fastcat Group. All rights reserved.<br/> 
			<i class="icon-envelope-alt"></i> contact@fastcatgroup.org<br/> 
			<i class="glyphicon glyphicon-phone-alt"></i> +82-2-508-1151<br/>
			2F Samseong-dong 122-30 Gangnam-gu Seoul, Korea<br/>
			</p>
		</div>
	</div>
</body>
</html>