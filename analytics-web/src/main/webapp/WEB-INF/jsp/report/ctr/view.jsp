<%@page import="java.util.Random, java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
List<String> clickTypeList = (List<String>) request.getAttribute("clickTypeList");
List<Integer> searchPvList = (List<Integer>) request.getAttribute("searchPvList");
List<Integer> hitList = (List<Integer>) request.getAttribute("hitList");
String timeText = (String) request.getAttribute("timeText");
if(timeText == null ) {
	timeText = ""; 
}
%>
<c:set var="ROOT_PATH" value="../.." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />

<script>
$(document).ready(function() {
	
	var pickmenup_options = {
		calendars: 3,
		mode: 'range',
		format: 'Y.m.d',
		first_day: 1,
		position: 'bottom',
		hide_on_select	: true 
	};
	$("#timeText").pickmeup(pickmenup_options);
		
		
	var ctr1 = [ [ 1262304000000, 1300 ], [ 1264982400000, 700 ], [ 1267401600000, 1000 ],
				[ 1270080000000, 3500 ], [ 1272672000000, 2000 ],
				[ 1275350400000, 1500 ], [ 1277942400000, 1200 ] ];

	var ctr2 = [ [ 1262304000000, 700 ],[ 1264982400000, 400 ],[ 1267401600000, 600 ],
				[ 1270080000000, 2500 ], [ 1272672000000, 1300 ],
				[ 1275350400000, 700 ], [ 1277942400000, 600 ] ];
		
	var ctr3 = [ [ 1262304000000, 60 ],[ 1264982400000, 50 ],[ 1267401600000, 60 ],
				[ 1270080000000, 55 ], [ 1272672000000, 55 ],
				[ 1275350400000, 70 ], [ 1277942400000, 60 ] ];
	
	var ctr_data = [ 
{
	label : "유입률",
	data : ctr3,
	color : '#eee',
	lines: { show: false},
	bars: {
		show: true,
		barWidth: 30 * 60 * 60 * 1000 *4,
		align:'center'
	},
	yaxis: 2
},                 
	{
		label : "검색PV",
		data : ctr1,
		lines: {
            show: true
        },
        points:{
            show:true
        }
	}, {
		label : "통합검색",
		data : ctr2,
		lines: {
            show: true
        },
        points:{
            show:true
        }
	} ];
	$.plot("#chart_ctr_total", ctr_data, $.extend(true, {}, Plugins
			.getFlotDefaults(),
			{
				xaxis : {
					min : (new Date(2009, 12, 1)).getTime(),
					max : (new Date(2010, 6, 2)).getTime(),
					mode : "time",
					tickSize : [ 1, "month" ],
					monthNames : [ "Sun", "Mon", "Tue", "Wed", "Thu",
									"Fri", "Sat" ],
					tickLength : 0
				},
				yaxes: [
				  {
					  
				  },{
					position: "right"
				  }      
				],
				/* series : {
					lines : {
						fill : false,
						lineWidth : 1.5
					},
					points : {
						show : true,
						radius : 2.5,
						lineWidth : 1.1
					},
					grow : {
						active : true,
						growings : [ {
							stepMode : "maximum"
						} ]
					}
				}, */
				grid : {
					hoverable : true,
					clickable : true
				},
				tooltip : true,
				tooltipOpts : {
					content : '%s: %y'
				}
			}));

	
	
	
	
	
	var ctr_detail1 = [ [ 1262304000000, 800 ], [ 1264982400000, 700 ], [ 1267401600000, 900 ],
					[ 1270080000000, 900 ], [ 1272672000000, 950 ],
					[ 1275350400000, 1050 ], [ 1277942400000, 800 ] ];

	var ctr_detail2 = [ [ 1262304000000, 700 ],[ 1264982400000, 500 ],[ 1267401600000, 600 ],
				[ 1270080000000, 750 ], [ 1272672000000, 650 ],
				[ 1275350400000, 700 ], [ 1277942400000, 600 ] ];
		
	var ctr_detail3 = [ [ 1262304000000, 460 ],[ 1264982400000, 450 ],[ 1267401600000, 460 ],
				[ 1270080000000, 455 ], [ 1272672000000, 455 ],
				[ 1275350400000, 470 ], [ 1277942400000, 460 ] ];
	var ctr_detail4 = [ [ 1262304000000, 60 ],[ 1264982400000, 50 ],[ 1267401600000, 60 ],
	    				[ 1270080000000, 55 ], [ 1272672000000, 54 ],
	    				[ 1275350400000, 65 ], [ 1277942400000, 64 ] ];
	
	var ctr_detail_data = [ 
{
	label : "유입률",
	data : ctr_detail4,
	color : '#eee',
	lines: { show: false},
	bars: {
		show: true,
		barWidth: 30 * 60 * 60 * 1000 *4,
		align:'center'
	},
	yaxis: 2
},                       
	 {
		label : "상품블로그",
		data : ctr_detail1,
		lines: {
            show: true
        },
        points:{
            show:true
        }
	}, {
		label : "사러가기",
		data : ctr_detail2,
		lines: {
            show: true
        },
        points:{
            show:true
        }
	}, {
		label : "상품리스트",
		data : ctr_detail3,
		lines: {
            show: true,
            fill : false,
			lineWidth : 1.5
        },
        points:{
        	show : true,
			radius : 2.5,
			lineWidth : 1.1
        }
	} ];
	$.plot("#chart_ctr_detail", ctr_detail_data, $.extend(true, {}, Plugins
			.getFlotDefaults(),
			{
				xaxis : {
					min : (new Date(2009, 12, 1)).getTime(),
					max : (new Date(2010, 6, 2)).getTime(),
					mode : "time",
					tickSize : [ 1, "month" ],
					monthNames : [ "Sun", "Mon", "Tue", "Wed", "Thu",
									"Fri", "Sat" ],
					tickLength : 0
				},
				yaxes: [
				  {
					  
				  },{
					position: "right"
				  }      
				],
				/* series : {
					lines : {
						fill : false,
						lineWidth : 1.5
					},
					points : {
						show : true,
						radius : 2.5,
						lineWidth : 1.1
					},
					grow : {
						active : true,
						growings : [ {
							stepMode : "maximum"
						} ]
					}
				}, */
				grid : {
					hoverable : true,
					clickable : true
				},
				tooltip : true,
				tooltipOpts : {
					content : '%s: %y'
				}
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
					<!-- <ul class="crumb-buttons">
						<li class="range">
							<a href="#"> <i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
						</a>
						</li>
					</ul> -->
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
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : 2014.01 - 2014.04
								</h4>
								<!-- <div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs"><i class="icos-word-document"></i></span>
									</div>
								</div> -->
							</div>
						</div>

					</div>
				</div>
				
				<%
					for(int i=0;i<hitList.size(); i++){
						Integer pv = searchPvList.get(i);
						Integer a = hitList.get(i);
						out.println(pv + " : " + a);
						out.println("<br>");
					}
				%>
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Total Click-through</h4>
							</div>
							<div class="widget-content">
								<div id="chart_ctr_total" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<li><strong>172,055</strong> <small>Search PV</small></li>
									<li class="text-success"><strong>86,372</strong> <small>Click-through Count</small></li>
									<li class="text-primary"><strong>50.20%</strong> <small>Click-through Rate</small></li>
								</ul>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>Detail Click-through</h4>
							</div>
							<div class="widget-content">
								<div id="chart_ctr_detail" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<li class="text-success"><strong>86,372</strong> <small>Click-through Count</small></li>
									<li><strong>76,086</strong> <small>상품블로그</small> </li>
									<li><strong>7,257</strong> <small>사러가기</small></li>
									<li><strong>3,029</strong> <small>상품리스트</small></li>
									
								</ul>
							</div>
							
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-12">	
						<div class="widget box">
						<div class="widget-header">
							<h4>Search-through Count</h4>
						</div>
						<div class="widget-content">
							<ul class="stats">
								<li class="text-success"><strong>172,055</strong> <small>Search Count</small></li>
								<li><strong>6,086</strong> <small>연관검색</small></li>
								<li><strong>3,257</strong> <small>자동완성</small></li>
								<li><strong>1,029</strong> <small>추천검색</small></li>
								<li><strong>1,000</strong> <small>바로가기</small></li>
								<li><strong>8,000</strong> <small>인기검색</small></li>
								<li><strong>1,200</strong> <small>ETC</small></li>
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
