<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*
,java.util.List
,org.fastcatgroup.analytics.analysis.config.StatisticsSettings.CategorySetting
"%>

<%
List<CategorySetting> categoryList = (List<CategorySetting>)request.getAttribute("categoryList");
%>
<c:set var="ROOT_PATH" value="../.." />
<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />

<script type="text/javascript">

$(document).ready(function() {
	
	var tableRefreshFunc = function(tbody) {
		var trFind = tbody.find("tr");
		trFind.each(function() {
			var newIndex = $.inArray(this, trFind);
			$($(this).find("td")[0]).html(newIndex + 1);
			$(this).find("input, select, textarea").each(function() {
				var name = $(this).attr("name");
				var match = /^([a-zA-Z0-9_-]+)[0-9]+/.exec(name);
				var key = match?match[1]:"";
				$(this).attr("name", key + newIndex);
			});
		});
	}
	
	var removeRowFunction = function() {
		if(confirm("Category will remove. Are you OK?")) {
			var tbody = $(this).parents("tbody");
			$(this).parents("tr").remove();
			tableRefreshFunc(tbody);
		}
	}
	
	var addRowFunction = function(){
		var tbody = $(this).parents("tbody");
		var pivotTr = $(this).parents("tr");
		var trTemplate = $("#schema_template tr");
		var newTr = trTemplate.clone();
		if(pivotTr.find("input").length > 0) {
			pivotTr.after(newTr);
		} else {
			pivotTr.after(newTr);
			pivotTr.remove();
		}
		tableRefreshFunc($(this).parents("tbody"));
		newTr.find("span.icon-plus-sign").parent("a.btn").click(addRowFunction);
		newTr.find("span.icon-minus-sign").parent("a.btn").click(removeRowFunction);
	};
	$("form#category-form span.icon-plus-sign").parent("a.btn").click(addRowFunction);
	$("form#category-form span.icon-minus-sign").parent("a.btn").click(removeRowFunction);
	
	$("div.form-actions input.btn-primary").click(function() {
		updateCategory("category-form", "update");
	});
});

function updateCategory(formId, mode) {
	var form = $("#"+formId);
	var valid = false;
	if(mode == "remove") { 
		valid = true; 
	} else {
		valid = form.valid();
	}
	
	var data = {};
	var tr = form.find("table tr");
	for(var inx = 0 ; inx < tr.length ; inx++) {
		var input = $(tr[inx]).find("input, select, textarea");
		for(var inx2 = 0; inx2 < input.length ; inx2++) {
			var name = input[inx2].name;
			if(input[inx2].type=="checkbox") {
				if(input[inx2].checked) {
					data[name] = input[inx2].value;
				}
			} else {
				data[name] = input[inx2].value;
			}
		}
		data["count"] = inx;
	}
	data["mode"] = mode;
	
	if(valid) {
		$.ajax({
			url:"updateCategory.html",
			type:"POST",
			data:data, dataType:"json",
			success:function(response) {
				if(response["success"] == "true") {
		 			noty({text: "update success !", layout:"topRight"});
		 			setTimeout(function() {
						location.href = location.href;
		 			},1000);
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
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />
	<div id="container">
		<c:import url="${ROOT_PATH}/configuration/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="categorySetting" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> Manager</li>
						<li> Settings</li>
						<li class="current"> Category Settings</li>
					</ul>

				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Category Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->

				<div class="widget">
					<div class="widget-content">
						<form class="form-horizontal" role="form" id="category-form">
						<table class="table table-hover table-bordered table-vertical-align-middle">
							<thead>
								<tr>
									<th>#</th>
									<th>Category ID</th>
									<th>Name</th>
									<th>Use Realtime Popular Keyword</th>
									<th>Use Popular Keyword</th>
									<th>Use Relate Keyword</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
							<%
							for(int cateInx=0; cateInx < categoryList.size();cateInx++) {
								CategorySetting categorySetting = categoryList.get(cateInx);
							%>
								<tr>
									<td><%=cateInx + 1 %></td>
									<td><input class="form-control" type="text" name="categoryId<%=cateInx %>" value="<%=categorySetting.getId() %>" <%=cateInx==0?"readonly":"" %>/></td>
									<td><input class="form-control" type="text" name="categoryName<%=cateInx %>" value="<%=categorySetting.getName() %>"/></td>
									<td><input class="form-control" type="checkbox" name="useRealTimePopularKeyword<%=cateInx%>" value="true" <%=categorySetting.isUseRealTimePopularKeyword()?"checked":"" %>/></td>
									<td><input class="form-control" type="checkbox" name="usePopularKeyword<%=cateInx%>" value="true" <%=categorySetting.isUsePopularKeyword()?"checked":"" %>/></td>
									<td><input class="form-control" type="checkbox" name="useRelateKeyword<%=cateInx%>" value="true" <%=categorySetting.isUseRelateKeyword()?"checked":"" %>/></td>
									<td>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-plus-sign" id="btn-add-<%=cateInx%>"></span>
										</a>
										<% 
										if(cateInx > 0) { 
										%>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-minus-sign text-danger" id="btn-remove-<%=cateInx%>"></span>
										</a>
										<%
										}
										%>
									</td>
								</tr>
							<%
							}
							%>
							</tbody>
						</table>
						</form>
						<table class="hidden" id="schema_template">
							<tr>
								<td></td>
								<td><input class="form-control" type="text" name="categoryId0" value=""/></td>
								<td><input class="form-control" type="text" name="categoryName0" value=""/></td>
								<td><input class="form-control" type="checkbox" name="useRealTimePopularKeyword0" value="true"/></td>
								<td><input class="form-control" type="checkbox" name="usePopularKeyword0" value="true"/></td>
								<td><input class="form-control" type="checkbox" name="useRelateKeyword0" value="true"/></td>
								<td>
									<a class="btn btn-sm" href="javascript:{}">
										<span class="icon-plus-sign"></span>
									</a>
									<a class="btn btn-sm" href="javascript:{}">
										<span class="icon-minus-sign text-danger"></span>
									</a>
								</td>
							</tr>
						</table>
					</div>
				</div>
				
				<div class="form-actions">
					<input type="button" value="Update Settings" class="btn btn-primary pull-right">
				</div>
				<!-- /Page Content -->
			</div>
		</div>
	</div>
</body>
</html>