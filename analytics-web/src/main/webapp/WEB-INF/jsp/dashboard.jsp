<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:import url="inc/common.jsp" />
<html>
<head>
<c:import url="inc/header.jsp" />

<script>
	$(document).ready(function() {
		/* "use strict";

		App.init(); // Init layout and core plugins
		Plugins.init(); // Init all plugins
		FormComponents.init(); // Init all form-specific plugins */
		
		
		
		// We use an inline data source in the example, usually data would be fetched from a server
		var data1 = [], data2 = [], data3 = [], data4 = [], totalPoints = 120;
		

		function getRandomData(data) {
			if (data.length > 0)
				data.shift();
			// do a random walk
			while (data.length < totalPoints) {
				var prev = data.length > 0 ? data[data.length - 1] : 50;
				var y = prev + Math.random() * 10 - 5;
				if (y < 0)
				y = 0;
				if (y > 100)
				y = 100;
				data.push(y);
			}
			// zip the generated y values with the x values
			var res = [];
			for (var i = 0; i < data.length; ++i){
				res.push([i, data[i]])
			}
			
			return res;
		}

		function getRandomDataList() {
			getRandomData(data1)
			return [getRandomData(data1), getRandomData(data2), getRandomData(data3), getRandomData(data4)];
		}
		var series_multiple = [
			{
				label: "Sample",
				data: getRandomData(data1),
			},{
				label: "Vol1",
				data: getRandomData(data2),
			},{
				label: "Vol2",
				data: getRandomData(data3),
			},{
				label: "Vol3",
				data: getRandomData(data4),
			}
		];

		// Initialize flot
		var plot = $.plot("#chart_multiple", series_multiple, $.extend(true, {}, Plugins.getFlotDefaults(), {
			series: {
				lines: { lineWidth: 1.5, show: true },
				points: { show: false },
				grow: { active: false }
			},
			grid: {
				hoverable: true,
				clickable: true
			},
			tooltip: true,
			tooltipOpts: {
				content: '%y'
			}
		}));
		
		function update() {
			plot.setData(getRandomDataList());
			// since the axes don't change, we don't need to call plot.setupGrid()
			plot.draw();
			setTimeout(update, 1000);
		}

		update();
	});
</script>
</head>
<body>
<c:import url="inc/mainMenu.jsp" />
<div id="container" class="sidebar-closed">
		<div id="content">
			<div class="container">
				<!-- Breadcrumbs line -->
				<div class="crumbs">
					<ul id="breadcrumbs" class="breadcrumb">
						<li><i class="icon-home"></i> <a href="javascript:void(0);">Dashboard</a>
						</li>
					</ul>

				</div>
				<!-- /Breadcrumbs line -->

				<!--=== Page Header ===-->
				<div class="page-header">
					<div class="page-title">
						<h3>Dashboard</h3>
					</div>
				</div>
				<!-- /Page Header -->

				<!--=== Page Content ===-->
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4><i class="icon-reorder"></i> Realtime Query Request</h4>
								<div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs widget-collapse"><i class="icon-angle-down"></i></span>
									</div>
								</div>
							</div>
							<div class="widget-content">
								<div id="chart_multiple" class="chart chart-medium"></div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="widget box">
							<div class="widget-header">
								<h4><i class="icon-reorder"></i> Collections</h4>
								<div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs"><i class="icon-external-link"></i></span>
										<span class="btn btn-xs widget-collapse"><i class="icon-angle-down"></i></span>
									</div>
								</div>
							</div>
							<div class="widget-content no-padding">
								<table class="table table-bordered table-hover">
									<thead>
										<tr>
											<th>Collection</th>
											<th>Shard</th>
											<th>Document Size</th>
											<th>Disk Size</th>
											<th>Update Time</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Sample</td>
											<td>Sample</td>
											<td>1500</td>
											<td>128.5MB</td>
											<td>2013-09-10 12:00:05</td>
										</tr>
										<tr>
											<td rowspan="4">VOL</td>
											<td>VOL1</td>
											<td>45000</td>
											<td>3128.5MB</td>
											<td>2013-09-11 16:00:05</td>
										</tr>
										<tr>
											<td>VOL2</td>
											<td>45000</td>
											<td>3128.5MB</td>
											<td>2013-09-11 16:00:05</td>
										</tr>
										<tr>
											<td>VOL2011</td>
											<td>45000</td>
											<td>3128.5MB</td>
											<td>2013-09-11 16:00:05</td>
										</tr>
										<tr>
											<td>VOL2012</td>
											<td>45000</td>
											<td>3128.5MB</td>
											<td>2013-09-11 16:00:05</td>
										</tr>
									</tbody>
								</table>
							</div> <!-- /.widget-content -->
						</div> <!-- /.widget -->
					</div>
					
					
					<div class="col-md-6">
						<div class="widget box">
							<div class="widget-header">
								<h4><i class="icon-reorder"></i> Indexing History</h4>
								<div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs"><i class="icon-external-link"></i></span>
										<span class="btn btn-xs widget-collapse"><i class="icon-angle-down"></i></span>
									</div>
								</div>
							</div>
							<div class="widget-content no-padding">
								<table class="table table-hover table-bordered">
									<thead>
										<tr>
											<th>Shard</th>
											<th>Status</th>
											<th>Insert Size</th>
											<th>Finish</th>
											<th>Duration</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>*<strong>VOL</strong></td>
											<td><span class="label label-success"><i class="glyphicon glyphicon-ok"></i> Sucess</span></td>
											<td>1500</td>
											<td>20 mins ago</td>
											<td>1h 20m 20s</td>
										</tr>
										<tr>
											<td>VOL1</td>
											<td><span class="label label-success"><i class="glyphicon glyphicon-ok"></i> Sucess</span></td>
											<td>1500</td>
											<td>20 mins ago</td>
											<td>1h 20m 20s</td>
										</tr>
										<tr>
											<td>VOL2</td>
											<td><span class="label label-danger"><i class="glyphicon glyphicon-warning-sign"></i> Fail</span></td>
											<td>1500</td>
											<td>20 mins ago</td>
											<td>1h 20m 20s</td>
										</tr>
										<tr>
											<td>VOL2011</td>
											<td><span class="label label-success"><i class="glyphicon glyphicon-ok"></i> Sucess</span></td>
											<td>1500</td>
											<td>20 mins ago</td>
											<td>1h 20m 20s</td>
										</tr>
										<tr>
											<td>VOL2012</td>
											<td><span class="label label-success"><i class="glyphicon glyphicon-ok"></i> Sucess</span></td>
											<td>1500</td>
											<td>20 mins ago</td>
											<td>1h 20m 20s</td>
										</tr>
									</tbody>
								</table>
							</div> <!-- /.widget-content -->
						</div> <!-- /.widget -->
					</div>
					
				</div>
				
				<div class="row">
					<div class="col-md-6">
						<div class="widget box">
							<div class="widget-header">
								<h4><i class="icon-reorder"></i> Server Status</h4>
								<div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs"><i class="icon-external-link"></i></span>
										<span class="btn btn-xs widget-collapse"><i class="icon-angle-down"></i></span>
									</div>
								</div>
							</div>
							<div class="widget-content no-padding">
								<table class="table table-bordered table-checkable table-hover">
									<thead>
										<tr>
											<th>#</th>
											<th>Server Name</th>
											<th>Status</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>1</td>
											<td>Node1</td>
											<td>Alive</td>
										</tr>
									</tbody>
								</table>
							</div> <!-- /.widget-content -->
						</div> <!-- /.widget -->
					</div>
					
					
					<div class="col-md-6">
						<div class="widget box">
							<div class="widget-header">
								<h4><i class="icon-reorder"></i> Notifications</h4>
								<div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs"><i class="icon-external-link"></i></span>
										<span class="btn btn-xs widget-collapse"><i class="icon-angle-down"></i></span>
									</div>
								</div>
							</div>
							<div class="widget-content ">
								<ul class="feeds clearfix">
									<li>
										<div class="col1">
											<div class="content">
												<div class="content-col1">
													<div class="label label-info">
														<i class="icon-bell"></i>
													</div>
												</div>
												<div class="content-col2">
													<div class="desc">[Node1] [FC-100] Index is failed.</div>
												</div>
											</div>
										</div> <!-- /.col1 -->
										<div class="col2">
											<div class="date">
												Just now
											</div>
										</div> <!-- /.col2 -->
									</li>
									<li>
										<div class="col1">
											<div class="content">
												<div class="content-col1">
													<div class="label label-danger"><i class="icon-warning-sign"></i></div>
												</div>
												<div class="content-col2">
													<div class="desc">Collection [sample] is not indexed.</div>
												</div>
											</div>
										</div> <!-- /.col1 -->
										<div class="col2">
											<div class="date">20 mins ago</div>
										</div> <!-- /.col2 -->
									</li>
									<li class="hoverable">
										<a href="javascript:void(0);">
											<div class="col1">
												<div class="content">
													<div class="content-col1">
														<div class="label label-danger"><i class="icon-warning-sign"></i></div>
													</div>
													<div class="content-col2">
														<div class="desc">Collection [vol] is not indexed.</div>
													</div>
												</div>
											</div> <!-- /.col1 -->
											<div class="col2">
												<div class="date">25 mins ago</div>
											</div> <!-- /.col2 -->
										</a>
									</li>
									<li>
										<div class="col1">
											<div class="content">
												<div class="content-col1">
													<div class="label label-info">
														<i class="icon-bell"></i>
													</div>
												</div>
												<div class="content-col2">
													<div class="desc">[Node1] [FC-100] You have 2 puzzles to solve.</div>
												</div>
											</div>
										</div> <!-- /.col1 -->
										<div class="col2">
											<div class="date">
												Just now
											</div>
										</div> <!-- /.col2 -->
									</li>
									<li>
										<div class="col1">
											<div class="content">
												<div class="content-col1">
													<div class="label label-info">
														<i class="icon-bell"></i>
													</div>
												</div>
												<div class="content-col2">
													<div class="desc">[Node1] [FC-100] You have 2 puzzles to solve.</div>
												</div>
											</div>
										</div> <!-- /.col1 -->
										<div class="col2">
											<div class="date">
												Just now
											</div>
										</div> <!-- /.col2 -->
									</li>
								</ul>
							</div> <!-- /.widget-content -->
						</div> <!-- /.widget -->
					</div>
					
				</div>
				
				<div class="row">
					<div class="col-md-12">
						<div class="widget box">
							<div class="widget-header">
								<h4><i class="icon-reorder"></i> Running Tasks</h4>
								<div class="toolbar no-padding">
									<div class="btn-group">
										<span class="btn btn-xs widget-collapse"><i class="icon-angle-down"></i></span>
									</div>
								</div>
							</div>
							<div class="widget-content no-padding">
								<table class="table table-bordered table-checkable table-hover">
									<thead>
										<tr>
											<th>#</th>
											<th>Task Name</th>
											<th>Status</th>
											<th>Start Time</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>1</td>
											<td>Indexing</td>
											<td>[Sample] collection full indexing manually 420000 documents ..</td>
											<td>4.5 hours ago</td>
										</tr>
										<tr>
											<td>2</td>
											<td>Indexing</td>
											<td>[Vol] collection add indexing manually 20000 documents ..</td>
											<td>1.5 hours ago</td>
										</tr>
									</tbody>
								</table>
							</div> <!-- /.widget-content -->
						</div> <!-- /.widget -->
					</div>
				
				</div>
				<!-- /Page Content -->
			</div>
			<!-- /.container -->

		</div>
</div>
</body>
</html>







