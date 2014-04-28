<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.Random, java.util.*" %>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="org.fastcatsearch.analytics.util.ListableCounter"%>
<%@ page import="org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ClickTypeSetting" %>
<%@ page import="org.fastcatsearch.analytics.analysis.config.StatisticsSettings.ServiceSetting" %>
<%
List<ClickTypeSetting> clickTypeSettingList = (List<ClickTypeSetting>) request.getAttribute("clickTypeSettingList");
List<Integer> searchPvList = (List<Integer>) request.getAttribute("searchPvList");
List<Integer> clickHitList = (List<Integer>) request.getAttribute("clickHitList");
List<String> labelList = (List<String>) request.getAttribute("labelList");
List<ServiceSetting> serviceList = (List<ServiceSetting>) request.getAttribute("serviceList");
Map<String, ListableCounter>  searchPathCounter = (Map<String, ListableCounter>) request.getAttribute("searchPathCounter");
String timeText = (String) request.getAttribute("timeText");
if(timeText == null ) {
	timeText = ""; 
}
DecimalFormat format = new DecimalFormat("#,###");

ServiceSetting primeServiceSetting = null; 
for(ServiceSetting serviceSetting : serviceList) {
	if(serviceSetting.isPrime()) {
		primeServiceSetting = serviceSetting;
		break;
	}
}
if(primeServiceSetting == null) {
	primeServiceSetting = new ServiceSetting("","",false);
}
String primeServiceId = primeServiceSetting.getId();
String primeServiceName = primeServiceSetting.getName();
ListableCounter primeSearchCounter = searchPathCounter.get(primeServiceId);
int primeSearchCount = 0;
if(primeSearchCounter != null) {
	primeSearchCount = primeSearchCounter.value();
}
float primeSearchRate = 0f;
%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />

<script>
$(document).ready(function() {
	var pickmeup_options = {
		calendars: 3,
		mode: "range",
		format: "Y.m.d",
		first_day: 1,
		position: "bottom",
		hide_on_select	: true 
	};
	$("#timeText").pickmeup(pickmeup_options);
	
	var searchPvData=[];
	var totalSearchCountData=[];
	var totalSearchRateData=[];
	var clickHitData=[];
	var searchClickRate=[];
	var ticks=[];
	
	<%
	int totalPv = 0;
	int totalClickHit = 0;
	float totalClickRate = 0f;
	for(int i=0;i<clickHitList.size(); i++){
		Integer pv = searchPvList.get(i);
		Integer clickHit = clickHitList.get(i);
		if(pv==null) { pv = 0; }
		if(clickHit==null) { clickHit = 0; }
		totalPv += pv;
		totalClickHit += clickHit;
		float rate = 0f;
		if(pv>0) {
			rate = Math.round(clickHit * 10000f / pv) / 100f;
		}
		
		Integer searchCount = 0;
		if(primeSearchCounter!=null) {
			searchCount = primeSearchCounter.list().get(i);
		}
		if(searchCount==null) { searchCount=0; }
		float searchRate=0f;
		if(pv>0) {
			searchRate = Math.round(searchCount *10000f / pv) / 100f;
		}
	%>
		searchPvData[<%=i%>]=[<%=i%>,<%=pv%>];
		totalSearchCountData[<%=i%>]=[<%=i%>,<%=searchCount%>];
		totalSearchRateData[<%=i%>]=[<%=i%>,<%=searchRate%>];
		clickHitData[<%=i%>]=[<%=i%>,<%=clickHit%>];
		searchClickRate[<%=i%>]=[<%=i%>,<%=rate%>];
		ticks[<%=i%>]=[<%=i%>,<%=labelList.get(i)%>];
	<%
	}
	if(totalPv > 0) {
		totalClickRate = Math.round(totalClickHit * 10000f / totalPv) / 100f;
		primeSearchRate = Math.round(primeSearchCount * 10000f / totalPv) / 100f;
	}
	%>
		
	var search_through_data = [ 
	{
			label : "Search PV",
			data : searchPvData,
			color : "#000",
			lines: { show: true },
			points:{ show:true }
		}, {
		label : "Total Search Count",
		data : totalSearchCountData,
		color : "#468847",
		lines: { show: true },
		points:{ show:true }
	}, {
		label : "Total Search Rate",
		data : totalSearchRateData,
		color : "rgba(66,139,202,0.3)",
		lines: { show: false},
		bars: {
			show: true,
			barWidth: 0.2 ,
			align:"center"
		},
		points:{ show:true },
		yaxis: 2
	}];

	function yFormatter(v, axis) {
		return Math.ceil(v) + " %";
	}
	
	$.plot("#chart_search_rate", search_through_data, $.extend(true, {}, 
		Plugins.getFlotDefaults(), {
			xaxis : {
				min : 0,
				tickSize : [ 1, "month" ],
				ticks : ticks,
				tickLength : 0,
			}, yaxes: [ { min:"0" },{ min:"0", position: "right", tickFormatter: yFormatter }
			], grid : {
				hoverable : true,
				clickable : true
			}, tooltip : true,
			tooltipOpts : { content : "%s: %y" }
		})
	);
	
	var ctr_data = [ 
	{
		label : "Search PV",
		data : searchPvData,
		color : "#000",
		lines: { show: true },
		points:{ show:true }
	}, {
		label : "Click-through Count",
		data : clickHitData,
		color : "#468847",
		lines: { show: true },
		points:{ show:true }
	}, {
		label : "Click-through Rate",
		data : searchClickRate,
		color : "rgba(66,139,202,0.3)",
		lines: { show: false},
		bars: {
			show: true,
			barWidth: 0.2 ,
			align:"center"
		},
		points:{ show:true },
		yaxis: 2
	}];
	
	<%
	List<Integer> typeCountList = new ArrayList<Integer>();
	for(int typeInx=0;typeInx < clickTypeSettingList.size(); typeInx++) {
		ClickTypeSetting clickType = clickTypeSettingList.get(typeInx);
		String clickTypeId = clickType.getId();
		List<Integer> clickList = (List<Integer>) request.getAttribute("clickType_"+clickTypeId);
		int typeCount = 0;
		for(int inx=0; inx < clickList.size(); inx++) { 
			Integer clickCount = clickList.get(inx);
			if(clickCount==null) {
				clickCount = 0;
			}
			typeCount += clickCount;
		}
		typeCountList.add(typeCount);
	}
	%>
	
	
	$.plot("#chart_ctr", ctr_data, $.extend(true, {}, Plugins
	.getFlotDefaults(), {
		xaxis : {
			min : 0,
			tickSize : [ 1, "month" ],
			ticks : ticks,
			tickLength : 0
		},
		yaxes: [ { min:"0" },{ min:"0", position: "right", tickFormatter: yFormatter } ],
		grid : { hoverable : true, clickable : true },
		tooltip : true,
		tooltipOpts : { content : '%s: %y' }
	}));
});

</script>
</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />
	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="ctr" />
			<c:param name="mcat" value="overview" />
		</c:import>
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Click-through Rate</a></li>
						<li><a href="#">Overview</a></li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->
				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title page-title-sm">
						<h3>Overview</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12">
						<form class="form-inline" role="form">
							<input class="form-control fcol2-1" size="16" type="text" id="timeText" name="timeText" value="<%=timeText %>" >
							<input type="button" class="btn  btn-primary" value="Submit">
						</form>
					</div>
				</div>
				<%
				String startTimeLabel = labelList.get(0);
				String endTimeLabel = labelList.get(labelList.size()-1);
				%>
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : <%=timeText%>
								</h4>
							</div>
						</div>

					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Search-through Rate</h4>
							</div>
							<div class="widget-content">
								<div id="chart_search_rate" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<li><strong><%=format.format(totalPv) %></strong> <small>Search PV</small></li>
									<li class="text-success"><strong><%=format.format(primeSearchCount) %></strong> <small><%=primeServiceName %></small></li>
									<li class="text-primary"><strong><%=primeSearchRate %>%</strong> <small><%=primeServiceName %> %</small></li>
								</ul>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<%
									for(ServiceSetting service : serviceList) {
										if("_etc".equals(service.getId()) || service.isPrime()) {
											continue;
										}
									%>
									<li class="light"><strong><%=format.format(searchPathCounter.get(service.getId()).value())%></strong> <small><%=service.getName()%></small></li>
									<%
									}
									%>
									<li class="light"><strong><%=format.format(searchPathCounter.get("_etc").value()) %></strong> <small>ETC</small></li>
								</ul>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Click-through Rate</h4>
							</div>
							<div class="widget-content">
								<div id="chart_ctr" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<li><strong><%=format.format(totalPv) %></strong> <small>Search PV</small></li>
									<li class="text-success"><strong><%=format.format(totalClickHit) %></strong> <small>Click-through Count</small></li>
									<li class="text-primary"><strong><%=totalClickRate %>%</strong> <small>Click-through Rate</small></li>
								</ul>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<% 
									for(int typeInx=0; typeInx < clickTypeSettingList.size(); typeInx++) {
										String clickTypeName = clickTypeSettingList.get(typeInx).getName();
										int typeCount = typeCountList.get(typeInx);
									%>
										<li class="light"><strong><%=format.format(typeCount) %></strong> <small><%=clickTypeName %></small></li>
									<%
									}
									%>
								</ul>
							</div>
							
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>