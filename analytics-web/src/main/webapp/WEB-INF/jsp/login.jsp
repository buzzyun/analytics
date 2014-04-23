<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="inc/common.jsp" />
<%
String redirect = request.getParameter("redirect");
if(redirect == null){
	redirect = "";
}

%>
<html>
<head>
<c:import url="inc/header.jsp" />
<link href="${pageContext.request.contextPath}/resources/assets/css/login.css" rel="stylesheet" type="text/css" />
<script>

$(document).ready(function(){
	$("#login-form").validate();
});

</script>
</head>
<body class="login">
	<!-- Logo -->
	<div class="logo">
		<strong>Fastcat</strong> Analytics
	</div>
	<!-- /Logo -->

	<!-- Login Box -->
	<div class="box">
		<div class="content">
			<!-- Login Formular -->

			<table>
				<tr>
					<td class="left"></td>
					<td class="right">
						<form class="form-vertical login-form" id="login-form"
							action="<c:url value="/doLogin.html"/>" method="post">
							<input type="hidden" name="redirect" value="<%=redirect %>" />
							<!-- Input Fields -->
							<div class="form-group">
								<div class="input-icon">
									<i class="icon-user"></i> <input type="text" name="userId"
										class="form-control required" placeholder="Username" />
								</div>
							</div>
							<div class="form-group">
								<div class="input-icon">
									<i class="icon-lock"></i> <input type="password"
										name="password" class="form-control required" placeholder="Password" />
								</div>
							</div>
							<!-- /Input Fields -->

							<!-- Form Actions -->
							<div class="form-actions">
								<!-- <label class="checkbox pull-left"><input type="checkbox"
									class="uniform" name="remember"> Remember me</label> -->
								<button type="submit" class="submit btn btn-primary pull-right">
									Sign In <i class="icon-angle-right"></i>
								</button>
							</div>
						</form>
					</td>
				</tr>
			</table>

		</div>
	</div>
	<!-- /.content -->
	<div class="footer">
		<div class="copy">
			<p class="address">
				Copyright(c) Fastcatsearch. All rights reserved.<br /> <i
					class="icon-envelope-alt"></i> contact@fastcatsearch.org<br /> <i
					class="glyphicon glyphicon-phone-alt"></i> +82-2-508-1151<br /> 2F
				Samseong-dong 122-30 Gangnam-gu Seoul, Korea<br />
			</p>
		</div>
	</div>
</body>
</html>