<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ROOT_PATH" value=".." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
	$(document).ready(function() {

			// Sample Data
			var d1 = [ [ 1262304000000, 0 ], [ 1264982400000, 500 ],
					[ 1267401600000, 700 ], [ 1270080000000, 1300 ],
					[ 1272672000000, 2600 ], [ 1275350400000, 1300 ],
					[ 1277942400000, 1700 ]];
			var d2 = [ [ 1262304000000, 10 ], [ 1264982400000, 400 ],
					[ 1267401600000, 300 ], [ 1270080000000, 900 ],
					[ 1272672000000, 2000 ], [ 1275350400000, 1500 ],
					[ 1277942400000, 1200 ]];
			
			var data = [ {
				label : "Current",
				data : d1,
				color : '#eb8544'
			}, {
				label : "Previous",
				data : d2,
				color : '#487FF3'
			} ];

			$.plot("#chart_dashboard_main", data, $.extend(true, {}, Plugins
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
					series : {
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
					},
					grid : {
						hoverable : true,
						clickable : true
					},
					tooltip : true,
					tooltipOpts : {
						content : '%s: %y'
					}
				}));

			
			var ctr1 = [ [ 1262304000000, 1300 ], [ 1264982400000, 700 ], [ 1267401600000, 1000 ],
				[ 1270080000000, 3500 ], [ 1272672000000, 2000 ],
				[ 1275350400000, 1500 ], [ 1277942400000, 1200 ] ];

			var ctr2 = [ [ 1262304000000, 700 ],[ 1264982400000, 400 ],[ 1267401600000, 600 ],
				[ 1270080000000, 2500 ], [ 1272672000000, 1300 ],
				[ 1275350400000, 700 ], [ 1277942400000, 600 ] ];
			
			var ctr3 = [ [ 1262304000000, 60 ],[ 1264982400000, 50 ],[ 1267401600000, 60 ],
							[ 1270080000000, 55 ], [ 1272672000000, 55 ],
							[ 1275350400000, 70 ], [ 1277942400000, 60 ] ];
			
			
			var ctr_data = [ {
				label : "검색PV",
				data : ctr1,
				color : '#eb8544'
			}, {
				label : "통합검색",
				data : ctr2,
				color : '#487FF3'
			}, {
				label : "유입률",
				data : ctr3,
				color : '#999',
				yaxis: 2
			} ];
			$.plot("#chart_dashboard_ctr", ctr_data, $.extend(true, {}, Plugins
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
						series : {
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
						},
						grid : {
							hoverable : true,
							clickable : true
						},
						tooltip : true,
						tooltipOpts : {
							content : '%s: %y'
						}
					}));
			
			
			// 서비스별 Data
			var service_rate_data = [];
			service_rate_data[0] = { label: "컴퓨터", data: 50 };
			service_rate_data[1] = { label: "노트북", data: 25 };
			service_rate_data[2] = { label: "가전", data: 20 };
			service_rate_data[3] = { label: "주변기기", data: 5 };
			$.plot("#chart_category_rate", service_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
				series: {
					pie: {
						show: true,
						radius: 1,
						label: {
							show: true
						}
					}
				},
				grid: {
					hoverable: true
				},
				tooltip: true,
				tooltipOpts: {
					content: '%p.0%, %s', // show percentages, rounding to 2 decimal places
					shifts: {
						x: 20,
						y: 0
					}
				}
			}));
			// 정렬별 Data
			var login_rate_data = [];
			login_rate_data[0] = { label: "인기도순", data: 50 };
			login_rate_data[1] = { label: "정확도순", data: 30 };
			login_rate_data[1] = { label: "낮은가격순", data: 20 };
			$.plot("#chart_login_rate", login_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
				series: {
					pie: {
						show: true,
						radius: 1,
						label: {
							show: true
						}
					}
				},
				grid: {
					hoverable: true
				},
				tooltip: true,
				tooltipOpts: {
					content: '%p.0%, %s', // show percentages, rounding to 2 decimal places
					shifts: {
						x: 20,
						y: 0
					}
				}
			}));
			// 연령별 Data
			var age_rate_data = [];
			age_rate_data[0] = { label: "10대이전", data: 10 };
			age_rate_data[1] = { label: "20대", data: 25 };
			age_rate_data[2] = { label: "30대", data: 40 };
			age_rate_data[3] = { label: "40대", data: 15 };
			age_rate_data[3] = { label: "50대이후", data: 10 };
			$.plot("#chart_age_rate", age_rate_data, $.extend(true, {}, Plugins.getFlotDefaults(), {
				series: {
					pie: {
						show: true,
						radius: 1,
						label: {
							show: true
						}
					}
				},
				grid: {
					hoverable: true
				},
				tooltip: true,
				tooltipOpts: {
					content: '%p.0%, %s', // show percentages, rounding to 2 decimal places
					shifts: {
						x: 20,
						y: 0
					}
				}
			}));
		});
	
	
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp" />
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">Dashboard</a></li>
					</ul>
					<ul class="crumb-buttons">
						<li class="range">
							<!-- <a href="#"> <i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
						</a> -->
						</li>
					</ul>
				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Dashboard</h3>
						<span>Overview statistics</span>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					<div class="col-md-12">
						<form class="form-inline" role="form">
							<select class="select_flat select_flat-sm">
								<option>ALL</option>
								<option>통합검색</option>
								<option>모바일</option>
							</select> <input type="button" class="btn btn-sm btn-warning"
								value="Today"> <input type="button"
								class="btn btn-sm btn-default" value="Yesterday"> <input
								type="button" class="btn btn-sm btn-default" value="Week">
							<input type="button" class="btn btn-sm btn-default" value="Month">
						</form>
					</div>

				</div>
				<div class="row">
					<div class="col-md-12">
						<div class="widget">
							<div class="widget-header">
								<h4>
									<i class="icon-calendar"></i> Period : 2013.10.10 - 2013.10.17
								</h4>
							</div>
						</div>

					</div>
				</div>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>검색횟수</h4>
							</div>
							<div class="widget-content">
								<div id="chart_dashboard_main" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong>4,853</strong> <small>Total Count</small></li>
									<li class="light"><strong>3,500</strong> <small>Last
											Period</small></li>
									<li><strong class="text-primary">+30%</strong> <small>Change</small></li>
								</ul>
							</div>
						</div>
					</div>
					<!-- /.col-md-12 -->
				</div>


				<div class="row">
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>검색어 TOP10</h4>
							</div>
							<div class="widget-content">
								<table class="table table-condensed">
									<tr>
										<th>#</th>
										<th>KEYWORD</th>
										<th>COUNT</th>
										<th>CHANGE</th>
									</tr>
									<tr>
										<td>1</td>
										<td>노트북</td>
										<td>1,203</td>
										<td>+30</td>
									</tr>
									<tr>
										<td>2</td>
										<td>핸드폰</td>
										<td>1,005</td>
										<td>+20</td>
									</tr>
									<tr>
										<td>3</td>
										<td>아이폰</td>
										<td>910</td>
										<td>+27</td>
									</tr>
									<tr>
										<td>4</td>
										<td>CPU</td>
										<td>850</td>
										<td>+13</td>
									</tr>
									<tr>
										<td>5</td>
										<td>메모리</td>
										<td>810</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>6</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>7</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>8</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>9</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>10</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
								</table>
							</div>
						</div>
					</div>
					
					<!--  -->
					
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>관심어 TOP10</h4>
							</div>
							<div class="widget-content">
								<table class="table table-condensed">
									<tr>
										<th>#</th>
										<th>KEYWORD</th>
										<th>COUNT</th>
										<th>CHANGE</th>
									</tr>
									<tr>
										<td>1</td>
										<td>노트북</td>
										<td>1,203</td>
										<td>+30</td>
									</tr>
									<tr>
										<td>2</td>
										<td>핸드폰</td>
										<td>1,005</td>
										<td>+20</td>
									</tr>
									<tr>
										<td>3</td>
										<td>아이폰</td>
										<td>910</td>
										<td>+27</td>
									</tr>
									<tr>
										<td>4</td>
										<td>CPU</td>
										<td>850</td>
										<td>+13</td>
									</tr>
									<tr>
										<td>5</td>
										<td>메모리</td>
										<td>810</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>6</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>7</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>8</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>9</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>10</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
								</table>
							</div>
						</div>
					</div>					
					<!--  -->
					
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>급상승키워드 TOP10</h4>
							</div>
							<div class="widget-content">
								<table class="table table-condensed">
									<tr>
										<th>#</th>
										<th>KEYWORD</th>
										<th>COUNT</th>
										<th>CHANGE</th>
									</tr>
									<tr>
										<td>1</td>
										<td>노트북</td>
										<td>1,203</td>
										<td>+30</td>
									</tr>
									<tr>
										<td>2</td>
										<td>핸드폰</td>
										<td>1,005</td>
										<td>+20</td>
									</tr>
									<tr>
										<td>3</td>
										<td>아이폰</td>
										<td>910</td>
										<td>+27</td>
									</tr>
									<tr>
										<td>4</td>
										<td>CPU</td>
										<td>850</td>
										<td>+13</td>
									</tr>
									<tr>
										<td>5</td>
										<td>메모리</td>
										<td>810</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>6</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>7</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>8</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>9</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
									<tr>
										<td>10</td>
										<td>메모리</td>
										<td>550</td>
										<td>+45</td>
									</tr>
								</table>
							</div>
						</div>
					</div>
					
					
				</div>
				
				<!-- - -->
				
				
				<div class="row">
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>카테고리별 비율</h4>
							</div>
							<div class="widget-content">
								<div id="chart_category_rate" class="chart"></div>
							</div>
						</div>
					</div>
					<!--  -->
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>로그인별 비율</h4>
							</div>
							<div class="widget-content">
								<div id="chart_login_rate" class="chart"></div>
							</div>
						</div>
					</div>
					<!--  -->
					<div class="col-md-4">
						<div class="widget box">
							<div class="widget-header">
								<h4>연령별 비율</h4>
							</div>
							<div class="widget-content">
								<div id="chart_age_rate" class="chart"></div>
							</div>
						</div>
					</div>
					
				</div>
				
				
				<!--  -->
				
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>클릭유입률</h4>
							</div>
							<div class="widget-content">
								<div id="chart_dashboard_ctr" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li><strong>172,055</strong> <small>검색PV</small></li>
									<li class="text-success"><strong>86,372</strong> <small>유입건</small></li>
									<li class="text-primary"><strong>50.20%</strong> <small>유입률</small></li>
								</ul>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<ul class="stats">
									<!-- .no-dividers -->
									<li class="light"><strong>76,086</strong> <small>상품블로그</small></li>
									<li class="light"><strong>7,257</strong> <small>사러가기</small></li>
									<li class="light"><strong>3,029</strong> <small>상품리스트</small></li>
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