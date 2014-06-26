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
String timeViewType = (String) request.getAttribute("timeViewType");

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
	var pickmeupOptions = {
		calendars: 3,
		mode: "range",
		format: "Y.m.d",
		first_day: 1,
		position: "bottom",
		hide_on_select	: true,
		change : function(date) {
			var options = $(this).data("pickmeup-options");
			var timeViewType = options.timeViewType;
			var dateStr1 = "";
			var dateStr2 = "";
			
			if(timeViewType == "H") {
				date = /([0-9]{4}[.][0-9]{2}[.][0-9]{2})/.exec(date)[0];
				dateStr1 = dateStr2 = date;
			} else {
				dateStr1 = date[0];
				dateStr2 = date[1];
			}
			var dateObj1 = options.parseDate(dateStr1);
			var dateObj2 = options.parseDate(dateStr2);
			
			var prevDate = $(this).attr("prev-date");
			
			if(timeViewType == "H") {
				console.log(dateStr1+":"+dateStr2);
			} else if(timeViewType == "D") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					$(this).attr("prev-date",null);
				};
			} else if(timeViewType == "W") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = options.firstDayOfWeek(dateObj1);
					dateObj2 = options.cloneDate(dateObj1);
					dateObj2.setDate( dateObj2.getDate() + 6 );
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = options.firstDayOfWeek(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = options.firstDayOfWeek(dateObj1);
						dateObj2 = options.parseDate(prevDate);
					}
					
					dateObj2.setDate( dateObj2.getDate() + 6 );
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
			} else if(timeViewType == "M") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = options.firstDayOfMonth(dateObj1);
					dateObj2 = options.lastDayOfMonth(dateObj1);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = options.firstDayOfMonth(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = options.firstDayOfMonth(dateObj1);
						dateObj2 = options.parseDate(prevDate);
					}
					
					dateObj2 = options.lastDayOfMonth(dateObj2);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
				
			} else if(timeViewType == "Y") {
				console.log(dateStr1+":"+dateStr2);
				if(dateStr1 == dateStr2) {
					dateObj1 = options.firstDayOfYear(dateObj1);
					dateObj2 = options.lastDayOfYear(dateObj1);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					if(prevDate) {
						$(this).attr("prev-date",null);
					} else {
						$(this).attr("prev-date",dateStr1);
					};
				} else {
					if(prevDate == dateStr1) {
						dateObj2 = options.firstDayOfYear(dateObj2);
					} else if(prevDate == dateStr2) {
						dateObj1 = options.firstDayOfYear(dateObj1);
						dateObj2 = options.parseDate(prevDate);
					}
					
					dateObj2 = options.lastDayOfYear(dateObj2);
					dateStr1 = options.formatDate(dateObj1);
					dateStr2 = options.formatDate(dateObj2);
					$(this).attr("prev-date",null);
				};
			}
			
			console.log("PICKUP : "+dateStr1+" ~ "+dateStr2+" / "+$(this).attr("prev-date"));
			if(timeViewType != "H") {
				$(this).val(dateStr1+" - "+dateStr2);
				$(this).pickmeup("set_date",new Array(dateStr1,dateStr2));
			} else {
				$(this).val(dateStr1);
				$(this).pickmeup("set_date",dateStr1);
			};
		}, cloneDate:function(date) {
			return new Date(date.getFullYear(), date.getMonth(), date.getDate());
		}, firstDayOfWeek:function(date) {
			var newDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
			newDate.setDate( newDate.getDate() - ( ( newDate.getDay() + 6 ) % 7 ) );
			return newDate;
		}, firstDayOfMonth:function(date) {
			return new Date(date.getFullYear(), date.getMonth(), 1);
		}, lastDayOfMonth:function(date) {
			return new Date(date.getFullYear(), date.getMonth() + 1, 0);
		}, firstDayOfYear:function(date) {
			return new Date(date.getFullYear(), 0, 1);
		}, lastDayOfYear:function(date) {
			return new Date(date.getFullYear(), 11, 31);
		}, parseDate:function(dateStr) {
			if(!$.isArray(dateStr)) {
				src = dateStr.split("."); src = dateStr.split(".");
				return new Date(src[0], src[1] - 1, src[2]);
			};
		}, formatDate:function(dateObj, failValue) {
			if(dateObj==null) {
				dateObj = failValue;
			}
			var year = dateObj.getFullYear();
			var month = (dateObj.getMonth() + 1);
			var date = dateObj.getDate();
			if(month < 10) { month = "0"+month; }
			if(date < 10) { date = "0"+date; }
			return year+"."+month+"."+date;
		}, timeViewType:"${timeViewType}"
	};
	$("#timeText").pickmeup(pickmeupOptions);
	
	$("#timeViewTypeList button").on("click", function(){
		var timeElement = $("#timeText");
		var options = timeElement.data("pickmeup-options");
		
		$(this).addClass("btn-primary");
		$(this).removeClass("btn-default");
		
		$(this).siblings().addClass("btn-default");
		$(this).siblings().removeClass("btn-primary");
		
		var timeViewType = $(this).text().charAt(0);
		$(this).parents("div").find("input[name=timeViewType]").val(timeViewType);
		options.timeViewType = timeViewType;
		
		var dates = timeElement.val().split(" - ");
		dates[0] = options.parseDate(dates[0]);
		if(timeViewType != "H") {
			dates[1] = dates[1]?options.parseDate(dates[1]):dates[0];
		}
		
		if(timeViewType == "H") {
			timeElement.val(options.formatDate(dates[0]));
			options.mode="single";
		} else {
			var fdate = null;
			if(timeViewType == "D") {
				fdate = dates[0];
				tdate = dates[1];
			} else if(timeViewType == "W") {
				fdate = options.firstDayOfWeek(dates[0]);
				tdate = options.firstDayOfWeek(dates[1]);
				tdate.setDate(tdate.getDate() + 6);
			} else if(timeViewType == "M") {
				fdate = options.firstDayOfMonth(dates[0]);
				tdate = options.lastDayOfMonth(dates[1]);
			} else if(timeViewType == "Y") {
				fdate = options.firstDayOfYear(dates[0]);
				tdate = options.lastDayOfYear(dates[1]);
			}
			timeElement.val(options.formatDate(fdate)+" - "+options.formatDate(tdate));
			options.mode="range";
		};
	});
	
	
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
	int delta = clickHitList.size() / 10;
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
		ticks[<%=i%>]=[<%=i%>,"<%=(i%delta==0)?labelList.get(i):"" %>"];
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
					<form class="form-inline" role="form">
						<div class="col-md-12">
							<div class="form-inline">
								<input class="form-control fcol2-1 " type="text" name="timeText" id="timeText" value="<%=timeText %>" >
								<div id="timeViewTypeList" class="btn-group">
									<button type="button" class="btn <%="D".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Daily</button>
									<button type="button" class="btn <%="W".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Weekly</button>
									<button type="button" class="btn <%="M".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Monthly</button>
									<button type="button" class="btn <%="Y".equals(timeViewType) ? "btn-primary" : "btn-default" %>">Yearly</button>
									<input type="hidden" name="timeViewType" value="<%=timeViewType %>">
								</div>
								<input type="submit" class="btn btn-primary" value="Submit">
							</div>
						</div>
					</form>
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