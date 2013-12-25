<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="ROOT_PATH" value=".." />

<c:import url="${ROOT_PATH}/inc/common.jsp" />
<html>
<head>
<c:import url="${ROOT_PATH}/inc/header.jsp" />
<script>
	
$(document).ready(function(){
		// Sample Data
		var d11 = [];
		var d22 = [];
		for (var i = 0; i < 10; i++){
			d11.push([i, parseInt((10-i)*100) + Math.random() * 100]);
			d22.push([i, parseInt((10-i)*100) + Math.random() * 100]);
		}

		var ds1 = new Array();

		ds1.push({
			label: "Current",
			data: d11,
			bars: {
				show: true,
				barWidth: 0.2,
				order: 1
			}
		});
		ds1.push({
			label: "Previous",
			data: d22,
			bars: {
				show: true,
				barWidth: 0.2,
				order: 2
			}
		});

		// Initialize Chart
		$.plot("#chart_keyword_rank1", ds1, $.extend(true, {}, Plugins.getFlotDefaults(), {
			series: {
				lines: { show: false },
				points: { show: false },
				bars: {
					fillColor: { colors: [ { opacity: 1 }, { opacity: 0.7 } ] },
					
				}
			},
			xaxis: {ticks: [[0,'노트북'],[1,'CPU'],[2,'메모리'],[3,'마우스'],[4,'울트라롱핸드폰']
					,[5,'모바일'],[6,'마우스패드'],[7,'울트라북'],[8,'청바지'],[9,'핸드폰케이스']]},
			grid:{
				hoverable: true
			},
			tooltip: true,
			tooltipOpts: {
				content: '%y'
			}
			
		}));
		
		var dp = $("#datepicking").datepicker({
			format: "yyyy.mm.dd"
		}).on('changeDate', function(ev) {
			dp.datepicker('hide');		
		});
		var dp2 = $("#datepicking2").datepicker({
			format: "yyyy.mm",
			viewMode: 1,
			minViewMode: 1
		}).on('changeDate', function(ev) {
			dp2.datepicker('hide');		
		});
		var dp3 = $("#datepicking3").datepicker({
			format: "yyyy.mm",
			viewMode: 2,
			minViewMode: 2
		}).on('changeDate', function(ev) {
			dp3.datepicker('hide');		
		});
		
		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
	
		var checkin = $('#dpd1').datepicker({
			format : "yyyy.mm.dd"
		}).on('changeDate', function(ev) {
			if (ev.date.valueOf() > checkout.date.valueOf()) {
				var newDate = new Date(ev.date)
				newDate.setDate(newDate.getDate() + 1);
				checkout.setValue(newDate);
			}
			checkin.hide();
			$('#dpd2')[0].focus();
		}).data('datepicker');

		var checkout = $('#dpd2').datepicker({
			format : "yyyy.mm.dd"
		}).on('changeDate', function(ev) {
			checkout.hide();
		}).data('datepicker');
	});
</script>

</head>
<body>
	<c:import url="${ROOT_PATH}/inc/mainMenu.jsp" />

	<div id="container">
		<c:import url="${ROOT_PATH}/report/sideMenu.jsp">
			<c:param name="lcat" value="rank" />
			<c:param name="mcat" value="searchKeyword" />
		</c:import>
			
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Report</a></li>
						<li><a href="#">검색순위</a></li>
						<li><a href="#">검색어</a></li>
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
						<h3>검색어순위</h3>
					</div>
				</div>
				<!-- /Page Header -->
				<div class="row row-bg row-bg-sm">
					<!-- .row-bg -->
					
					<div class="col-md-12 bottom-space">
						<form class="form-inline" role="form">
							<select class="select_flat select_flat-sm">
								<option>:: SITE ::</option>
								<option>통합검색</option>
								<option>모바일</option>
							</select> 
							<select class="select_flat select_flat-sm fcol2">
								<option>:: CATEGORY ::</option>
								<option>PC</option>
								<option>가전</option>
							</select> 
							<input type="button" class="btn btn-sm btn-warning" value="DAY"> 
							<input type="button" class="btn btn-sm btn-default" value="WEEK">
							<input
								type="button" class="btn btn-sm btn-default" value="MONTH">
							<input type="button" class="btn btn-sm btn-default" value="YEAR">
							
							<!-- <button class="btn btn-sm range">
								<i class="icon-calendar"></i>
								<span></span> <i class="icon-angle-down"></i>
							</button> -->
							
							<input class="form-control fcol1-2 input-sm" id="datepicking" size="16" type="text" value="2013.12.25" >
							<input class="form-control fcol1-2 input-sm" id="datepicking2" size="16" type="text" value="2013.12" >
							<input class="form-control fcol1-2 input-sm" id="datepicking3" size="16" type="text" value="" >
							
							<input type="text" class="form-control fcol1-2 input-sm" value="" id="dpd1">~<input type="text" class="form-control fcol1-2 input-sm" value="" id="dpd2">
							
						</form>
					</div>
					<div class="col-md-12">
						<form class="form-inline" role="form">
						<input type="text" class="form-control fcol3" placeholder="Keyword..">
						<input type="button" class="btn btn-primary" value="Submit">
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
								<!-- <div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs"><i class="icos-word-document"></i></span>
									</div>
								</div> -->
							</div>
						</div>

					</div>
				</div>
				
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4>TOP 10</h4>
							</div>
							<div class="widget-content">
								<div id="chart_keyword_rank1" class="chart"></div>
							</div>
							<div class="divider"></div>
							<div class="widget-content">
								<div>
									<table class="table table-striped table-bordered table-condensed">
										<thead>
											<tr>
												<th>#</th>
												<th>KEYWORD</th>
												<th>COUNT</th>
												<th>RANK CHANGE</th>
												<th>COUNT CHANGE</th>
											</tr>
										</thead>
										<tbody>
											<%
											for(int i =0;i < 15; i++){
											%>
											<tr>
												<td><%=i+1 %></td>
												<td>노트북</td>
												<td><%=i*1000+ i*7 + (i*100 % 13) %></td>
												<td>+1</td>
												<td>+100</td>
											</tr>
											<%
											}
											%>
										</tbody>
									</table>
								</div>
							
							</div>
						</div>
					
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
