<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*
,java.util.List
,org.fastcatgroup.analytics.analysis.config.StatisticsSettings.ClickTypeSetting
,org.fastcatgroup.analytics.analysis.config.StatisticsSettings.TypeSetting
,org.fastcatgroup.analytics.analysis.config.StatisticsSettings.ServiceSetting
"%>
<%
List<ClickTypeSetting> clickTypeList = (List<ClickTypeSetting>) request.getAttribute("clickTypeList");
List<ServiceSetting> serviceList = (List<ServiceSetting>) request.getAttribute("serviceList");
List<TypeSetting> typeList = (List<TypeSetting>) request.getAttribute("typeList");
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
				if(name=="servicePrimeIndex") {
					$(this).attr("name", name);
					$(this).attr("value", newIndex);
				} else {
					var match = /^([a-zA-Z0-9_-]+)[0-9]+/.exec(name);
					var key = match?match[1]:"";
					$(this).attr("name", key + newIndex);
				}
			});
		});
	}
	
	var removeRowFunction = function() {
		if(confirm("Attribute will remove. Are you OK?")) {
			var tbody = $(this).parents("tbody");
			$(this).parents("tr").remove();
			tableRefreshFunc(tbody);
		}
	}
	
	var addRowFunction = function(){
		var tableId = $(this).parents("table").attr("id");
		var pivotTr = $(this).parents("tr");
		var trTemplate = $("#schema_template tr#"+tableId);
		var newTr = trTemplate.clone();
		newTr.removeAttr("id");
		if(pivotTr.find("input, select, textarea").length > 0) {
			pivotTr.after(newTr);
		} else {
			pivotTr.after(newTr);
			pivotTr.remove();
		}
		tableRefreshFunc($(this).parents("tbody"));
		newTr.find("span.icon-plus-sign").parent("a.btn").click(addRowFunction);
		newTr.find("span.icon-minus-sign").parent("a.btn").click(removeRowFunction);
	};
	$("form#attribute-form span.icon-plus-sign").parent("a.btn").click(addRowFunction);
	$("form#attribute-form span.icon-minus-sign").parent("a.btn").click(removeRowFunction);
	
	$("div.form-actions input.btn-primary").click(function() {
		updateAttribute("attribute-form", "update");
	});
});

function updateAttribute(formId, mode) {
	
	if(confirm("DANGER !! All Previous Data Should Erase.\n"+
			"Cause Attribute Sequence Not Match. Are you OK?")) {
		if(confirm("Are you Really OK?")) {
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
					if(input[inx2].type=="checkbox" || input[inx2].type=="radio") {
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
					url:"updateAttribute.html",
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
	}
}
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />
	<div id="container">
		<c:import url="${ROOT_PATH}/configuration/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="attributeSetting" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> Manager</li>
						<li> Statistics</li>
						<li class="current"> Settings</li>
					</ul>

				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Attribute Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->
				
				<form class="form-horizontal" role="form" id="attribute-form">
				<div class="widget">
					<div class="widget-content">
						<div class="bottom-space-sm"><h4>Type Attributes</h4></div>
						<table id="type-setting" class="table table-hover table-bordered table-vertical-align-middle">
							<thead>
								<tr>
									<th>#</th>
									<th>Click ID</th>
									<th>Name</th>
									<th>Is Prime</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
							<%
							if(typeList.size() > 0) {
							%>
								<%
								for(int attrInx=0; attrInx < typeList.size();attrInx++) {
									TypeSetting typeSetting = typeList.get(attrInx);
								%>
								<tr>
									<td><%=attrInx + 1 %></td>
									<td><input class="form-control" type="text" name="typeId<%=attrInx %>" value="<%=typeSetting.getId() %>"/></td>
									<td><input class="form-control" type="text" name="typeName<%=attrInx %>" value="<%=typeSetting.getName() %>"/></td>
									<td><input class="form-control" type="checkbox" name="typePrime<%=attrInx%>" value="true" <%=typeSetting.isPrime()?"checked":""%>/></td>
									<td>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-plus-sign" id="btn-add-<%=attrInx%>"></span>
										</a>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-minus-sign text-danger" id="btn-remove-<%=attrInx%>"></span>
										</a>
									</td>
								</tr>
								<%
								}
								%>
							<%
							} else {
							%>
								<tr>
									<td colspan="5">
									<a class="btn btn-sm" href="javascript:{}">
										<span class="icon-plus-sign"></span>Add New
									</a>
									</td>
								</tr>
							<%
							}
							%>
							</tbody>
						</table>
					</div>
				</div>
				
				<div class="widget">
					<div class="widget-content">
						<div class="bottom-space-sm"><h4>Service Attributes</h4></div>
						<table id="service-setting" class="table table-hover table-bordered table-vertical-align-middle">
							<thead>
								<tr>
									<th>#</th>
									<th>Click ID</th>
									<th>Name</th>
									<th>Is Prime</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
							<%
							if(serviceList.size() > 0) {
							%>
								<%
								for(int attrInx=0; attrInx < serviceList.size();attrInx++) {
									ServiceSetting serviceSetting = serviceList.get(attrInx);
								%>
								<tr>
									<td><%=attrInx + 1 %></td>
									<td><input class="form-control" type="text" name="serviceId<%=attrInx %>" value="<%=serviceSetting.getId() %>"/></td>
									<td><input class="form-control" type="text" name="serviceName<%=attrInx %>" value="<%=serviceSetting.getName() %>"/></td>
									<td><input class="form-control" type="radio" name="servicePrimeIndex" value="<%=attrInx%>" <%=serviceSetting.isPrime()?"checked":""%>/></td>
									<td>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-plus-sign" id="btn-add-<%=attrInx%>"></span>
										</a>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-minus-sign text-danger" id="btn-remove-<%=attrInx%>"></span>
										</a>
									</td>
								</tr>
								<%
								}
								%>
							<%
							} else {
							%>
								<tr>
									<td colspan="5">
									<a class="btn btn-sm" href="javascript:{}">
										<span class="icon-plus-sign"></span>Add New
									</a>
									</td>
								</tr>
							<%
							}
							%>
							</tbody>
						</table>
					</div>
				</div>
				
				<div class="widget">
					<div class="widget-content">
						<div class="bottom-space-sm"><h4>Click Type Attributes</h4></div>
						<table id="click-type-setting" class="table table-hover table-bordered table-vertical-align-middle">
							<thead>
								<tr>
									<th>#</th>
									<th>Click ID</th>
									<th>Name</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
							<%
							if(clickTypeList.size() > 0) {
							%>
								<%
								for(int attrInx=0; attrInx < clickTypeList.size();attrInx++) {
									ClickTypeSetting clickTypeSetting = clickTypeList.get(attrInx);
								%>
								<tr>
									<td><%=attrInx + 1 %></td>
									<td><input class="form-control" type="text" name="clickTypeId<%=attrInx %>" value="<%=clickTypeSetting.getId() %>"/></td>
									<td><input class="form-control" type="text" name="clickTypeName<%=attrInx %>" value="<%=clickTypeSetting.getName() %>"/></td>
									<td>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-plus-sign" id="btn-add-<%=attrInx%>"></span>
										</a>
										<a class="btn btn-sm" href="javascript:{}">
											<span class="icon-minus-sign text-danger" id="btn-remove-<%=attrInx%>"></span>
										</a>
									</td>
								</tr>
								<%
								}
								%>
							<%
							} else {
							%>
								<tr>
									<td colspan="4">
									<a class="btn btn-sm" href="javascript:{}">
										<span class="icon-plus-sign"></span>Add New
									</a>
									</td>
								</tr>
							<%
							}
							%>
							</tbody>
						</table>
					</div>
				</div>
				</form>
				
				<table class="hidden" id="schema_template">
					<tr id="type-setting">
						<td></td>
						<td><input class="form-control" type="text" name="typeId0" value=""/></td>
						<td><input class="form-control" type="text" name="typeName0" value=""/></td>
						<td><input class="form-control" type="checkbox" name="typePrime0" value="true"/></td>
						<td>
							<a class="btn btn-sm" href="javascript:{}">
								<span class="icon-plus-sign" id="btn-add-0"></span>
							</a>
							<a class="btn btn-sm" href="javascript:{}">
								<span class="icon-minus-sign text-danger" id="btn-remove-0"></span>
							</a>
						</td>
					</tr>
					<tr id="service-setting">
						<td></td>
						<td><input class="form-control" type="text" name="serviceId0" value=""/></td>
						<td><input class="form-control" type="text" name="serviceName0" value=""/></td>
						<td><input class="form-control" type="radio" name="servicePrimeIndex" value=""/></td>
						<td>
							<a class="btn btn-sm" href="javascript:{}">
								<span class="icon-plus-sign" id="btn-add-0"></span>
							</a>
							<a class="btn btn-sm" href="javascript:{}">
								<span class="icon-minus-sign text-danger" id="btn-remove-0"></span>
							</a>
						</td>
					</tr>
					<tr id="click-type-setting">
						<td></td>
						<td><input class="form-control" type="text" name="clickTypeId0" value=""/></td>
						<td><input class="form-control" type="text" name="clickTypeName0" value=""/></td>
						<td>
							<a class="btn btn-sm" href="javascript:{}">
								<span class="icon-plus-sign" id="btn-add-0"></span>
							</a>
							<a class="btn btn-sm" href="javascript:{}">
								<span class="icon-minus-sign text-danger" id="btn-remove-0"></span>
							</a>
						</td>
					</tr>
				</table>
				<div class="form-actions">
					<input type="submit" value="Update Settings" class="btn btn-primary pull-right">
				</div>
				<!-- /Page Content -->
			</div>
		</div>
	</div>
</body>
</html>