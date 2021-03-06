<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*
,org.fastcatsearch.analytics.analysis.config.StatisticsSettings.*
"%>
<%
SiteProperties siteProperties = (SiteProperties) request.getAttribute("siteProperties");
PopularKeywordSetting popularKeywords = (PopularKeywordSetting) request.getAttribute("popularKeywordSetting");
RelateKeywordSetting relateKeywords = (RelateKeywordSetting) request.getAttribute("relateKeywordSetting");
RealTimePopularKeywordSetting realTimeKeywords = (RealTimePopularKeywordSetting) request.getAttribute("realTimePopularKeywordSetting");
CTRSetting ctrSetting = (CTRSetting) request.getAttribute("ctrSetting");

%>
<c:set var="ROOT_PATH" value="../.." />
<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script type="text/javascript">
$(document).ready(function() {
	$("div.form-actions input.btn").click(function() {
		var form=$("form#setting-form");
		valid = form.valid();
		if(valid) {
			var data = {};
			var input = form.find("input, select, textarea");
			for(var inx = 0; inx < input.length ; inx++) {
				var name = input[inx].name;
				if(input[inx].type=="checkbox") {
					if(input[inx].checked) {
						data[name] = input[inx].value;
					}
				} else {
					data[name] = input[inx].value;
				}
				data["mode"]="update";
			}
			$.ajax({
				url:"updateSetting.html",
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
	});
});
</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />
	<div id="container">
		<c:import url="${ROOT_PATH}/configuration/sideMenu.jsp">
			<c:param name="lcat" value="settings" />
			<c:param name="mcat" value="siteSetting" />
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
						<h3>Site Settings</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<form class="form-horizontal" role="form" id="setting-form">
				<div class="widget">
					<div class="widget-header">
						<h4>Common Properties</h4>
					</div>
					<div class="widget-content">
						<div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Banwords:</label>
									<div class="col-md-10"><textarea class="form-control" name="banwords" placeholder="" style="width:100%"><%=siteProperties.getBanwords() %></textarea>
									<p class="help-block">One by one each line</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Max Keyword Length:</label>
									<div class="col-md-10"><input class="form-control fcol2 digits required" name="maxKeywordLength" value="<%=siteProperties.getMaxKeywordLength() %>"/>
									<p class="help-block">If keyword length is larger than this, it's ignored.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Daily Schedule Time:</label>
									<div class="col-md-10"><input class="form-control fcol2 digits required" name="dailyScheduleTime" value="<%=siteProperties.getDailyScheduleTime() %>"/>
									<p class="help-block">At this time, run daily schedule.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Schedule Delay in seconds:</label>
									<div class="col-md-10"><input class="form-control fcol2 digits required" name="scheduleDelayInSeconds" value="<%=siteProperties.getScheduleDelayInSeconds() %>"/>
									<p class="help-block">When running a scheduled task, wait for this time.</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="widget">
					<div class="widget-header">
						<h4>Realtime Popular Keyword</h4>
					</div>
					<div class="widget-content">
						<div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Minimum Hit Count:</label>
									<div class="col-md-10"><input type="text" name="realTimeKeywordMinimumHit" class="form-control digits required fcol1-1" value="<%=realTimeKeywords.getMinimumHitCount()%>">
									<p class="help-block">If keyword hit count is smaller than this, it's ignored.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Recent Log Using Size:</label>
									<div class="col-md-10"><input type="text" name="realTimeKeywordRecentLog" class="form-control digits required fcol1-1" value="<%=realTimeKeywords.getRecentCount()%>">
									<p class="help-block">When aggregating keyword count with previos logs, this value set how many previous logs envolved.</p>
									</div>
									
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">View Size:</label>
									<div class="col-md-10"><input type="text" name="realTimeKeywordTopSize" class="form-control digits required fcol1-1" value="<%=realTimeKeywords.getTopCount()%>">
									<p class="help-block">How many keywords to show.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Period in seconds:</label>
									<div class="col-md-10"><input type="text" name="realTimeKeywordPeriod" class="form-control digits required fcol1-1" value="<%=realTimeKeywords.getPeriodInSeconds()%>">
									<p class="help-block">Keyword making period in seconds.</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="widget">
					<div class="widget-header">
						<h4>Popular Keyword</h4>
					</div>
					<div class="widget-content">
						<div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Minimum Hit Count:</label>
									<div class="col-md-10"><input type="text" name="popularKeywordMinimumHit" class="form-control digits required fcol1-1" value="<%=popularKeywords.getMinimumHitCount()%>">
									<p class="help-block">If keyword hit count is smaller than this, it's ignored.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">View Size:</label>
									<div class="col-md-10"><input type="text" name="popularKeywordTopSize" class="form-control digits required fcol1-1" value="<%=popularKeywords.getTopCount()%>">
									<p class="help-block">How many keywords to show.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Root Store Size:</label>
									<div class="col-md-10"><input type="text" name="rootStoreCount" class="form-control digits required fcol1-1" value="<%=popularKeywords.getRootStoreCount()%>">
									<p class="help-block">How many root keywords to store in database.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Category Store Size:</label>
									<div class="col-md-10"><input type="text" name="categoryStoreCount" class="form-control digits required fcol1-1" value="<%=popularKeywords.getCategoryStoreCount()%>">
									<p class="help-block">How many category keywords to store in database.</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				
				<div class="widget">
					<div class="widget-header">
						<h4>Relate Keyword</h4>
					</div>
					<div class="widget-content">
						<div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Minimum Hit Count:</label>
									<div class="col-md-10"><input type="text" name="relateKeywordMinimumHit" class="form-control digits required fcol1-1" value="<%=relateKeywords.getMinimumHitCount()%>">
									<p class="help-block">If keyword hit count is smaller than this, it's ignored.</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="widget">
					<div class="widget-header">
						<h4>Click-through Rate</h4>
					</div>
					<div class="widget-content">
						<div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Dump-file Day Size:</label>
									<div class="col-md-10"><input type="text" name="dumpFileDaySize" class="form-control digits required fcol1-1" value="<%=ctrSetting.getDumpFileDaySize()%>">
									<p class="help-block">Merge click log files within N days from now</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Target-file Path:</label>
									<div class="col-md-5"><input type="text" name="targetFilePath" class="form-control" value="<%=ctrSetting.getTargetFilePath()%>">
									<p class="help-block">File Path for Click-log Statistics Result</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Minimum Click Count:</label>
									<div class="col-md-10"><input type="text" name="minimumClickCount" class="form-control digits required fcol1-1" value="<%=ctrSetting.getMinimumClickCount() %>">
									<p class="help-block">If keyword click count is smaller than this, it's ignored.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">File Daily Decay-factor:</label>
									<div class="col-md-10"><input type="text" name="fileDailyDecayFactor" class="form-control number required fcol1-1" value="<%=ctrSetting.getFileDailyDecayFactor()%>">
									<p class="help-block">Daily click count decay factor for file.</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				</form>
				
				<div class="form-actions">
					<input type="submit" value="Update Settings" class="btn btn-primary pull-right"/>
				</div>
				<!-- /Page Content -->
			</div>
		</div>
	</div>
</body>
</html>