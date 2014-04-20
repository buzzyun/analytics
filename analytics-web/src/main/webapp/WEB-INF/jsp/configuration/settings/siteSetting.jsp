<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.json.*
,org.fastcatgroup.analytics.analysis.config.StatisticsSettings.PopularKeywordSetting
,org.fastcatgroup.analytics.analysis.config.StatisticsSettings.RealTimePopularKeywordSetting
,org.fastcatgroup.analytics.analysis.config.StatisticsSettings.RelateKeywordSetting
"%>
<%
String banWords = (String)request.getAttribute("banWords");
String fileEncoding = (String)request.getAttribute("fileEncoding");
PopularKeywordSetting popularKeywords = (PopularKeywordSetting) request.getAttribute("popularKeywordSetting");
RelateKeywordSetting relateKeywords = (RelateKeywordSetting) request.getAttribute("relateKeywordSetting");
RealTimePopularKeywordSetting realTimeKeywords = (RealTimePopularKeywordSetting) request.getAttribute("realTimePopularKeywordSetting");

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
						<h4>Common Settings</h4>
					</div>
					<div class="widget-content">
						<div class="row">
							<div class="col-md-12 form-horizontal">
								<div class="form-group">
									<label class="col-md-2 control-label">Banwords:</label>
									<div class="col-md-10"><textarea class="form-control" placeholder="word#1, word#2, ..." style="width:100%"><%=banWords %></textarea></div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">File Encoding:</label>
									<div class="col-md-10"><input class="form-control fcol2" name="fileEncoding" value="<%=fileEncoding%>"/></div>
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
									<div class="col-md-10"><input type="text" name="" class="form-control digits required fcol1-1" value="<%=realTimeKeywords.getMinimumHitCount()%>">
									<p class="help-block">If keyword hit count is smaller than this, it's ignored.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">Recent Log Using Size:</label>
									<div class="col-md-10"><input type="text" name="" class="form-control digits required fcol1-1" value="<%=realTimeKeywords.getRecentCount()%>">
									<p class="help-block">When aggregating keyword count with previos logs, this value set how many previous logs envolved.</p>
									</div>
									
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">TopN Store Size:</label>
									<div class="col-md-10"><input type="text" name="" class="form-control digits required fcol1-1" value="<%=realTimeKeywords.getTopCount()%>">
									<p class="help-block">How many top keywords to store.</p>
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
									<div class="col-md-10"><input type="text" name="" class="form-control digits required fcol1-1" value="<%=popularKeywords.getMinimumHitCount()%>">
									<p class="help-block">If keyword hit count is smaller than this, it's ignored.</p>
									</div>
								</div>
								<div class="form-group">
									<label class="col-md-2 control-label">TopN Store Size:</label>
									<div class="col-md-10"><input type="text" name="" class="form-control digits required fcol1-1" value="<%=popularKeywords.getTopCount()%>">
									<p class="help-block">How many top keywords to store.</p>
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
									<div class="col-md-10"><input type="text" name="" class="form-control digits required fcol1-1" value="<%=relateKeywords.getMinimumHitCount()%>">
									<p class="help-block">If keyword hit count is smaller than this, it's ignored.</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				</form>
				
				<div class="form-actions">
					<input type="submit" value="Update Settings" class="btn btn-primary pull-right">
				</div>
				<!-- /Page Content -->
			</div>
		</div>
	</div>
</body>
</html>