package org.fastcatgroup.analytics.http.action.service.keyword;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.ServiceAction;
import org.fastcatgroup.analytics.util.ResponseWriter;
import static org.fastcatgroup.analytics.analysis.calculator.KeywordHitAndRankConstants.*;

@ActionMapping("/test/upgrade-log")
@Deprecated
public class UpgradeLogAction extends ServiceAction {

	@Override
	public void doAction(ActionRequest request, ActionResponse response) throws Exception {
		
		File file = environment.filePaths().getStatisticsRoot().file("search", "date" );
		
		final Pattern ptn = Pattern.compile("^[0-9]{2}[:][0-9]{2}	");
		
		file.listFiles(new FilenameFilter() {
			@Override public boolean accept(File dir, String name) {
				if(name.startsWith("Y")) {
					logger.debug("year : {} / name:{}", dir, name);
					File file = new File(dir, name);
					file.listFiles(new FilenameFilter() {
						@Override public boolean accept(File dir, String name) {
							if(name.startsWith("M")) {
								logger.debug("month : {} / name:{}", dir, name);
								File file = new File(dir, name);
								file.listFiles(new FilenameFilter() {
									@Override public boolean accept(File dir, String name) {
										if(name.startsWith("D")) {
											logger.debug("day : {} / name:{}", dir, name);
											File file = new File(dir, name);
											file = new File(file,"data");
											file.listFiles(new FilenameFilter() {
												@Override public boolean accept(File dir, String name) {
													
													File basedir = new File(dir, name);

													File rawlog = new File(basedir, RAW_LOG_FILENAME);
													File typelog = new File(basedir, TYPE_RAW_FILENAME);

													File rawlogNew = new File(basedir, "raw.new");
													File typelogNew = new File(basedir, "type_raw.new");

													BufferedReader reader = null;
													BufferedWriter writer = null;
													try {
														reader = new BufferedReader(new FileReader(rawlog));
														writer = new BufferedWriter(new FileWriter(rawlogNew));
														for(String rline="";(rline=reader.readLine())!=null;) {
															Matcher m = ptn.matcher(rline);
															String[] frags = rline.split("\t");
															if(m.find() && frags.length >= 6) {
																writer.append(rline).append("\n");
															} else {
																writer.append("00:00	")
																.append(frags[0]).append("\t")
																.append(frags[1]).append("\t")
																.append(frags[2]).append("\t")
																.append("0").append("\t")
																.append(frags[3]).append("\n");
															}
														}
														writer.close();
														reader.close();
														rawlog.delete();
														rawlogNew.renameTo(rawlog);

														reader = new BufferedReader(new FileReader(typelog));
														writer = new BufferedWriter(new FileWriter(typelogNew));
														for(String rline="";(rline=reader.readLine())!=null;) {
															Matcher m = ptn.matcher(rline);
															String[] frags = rline.split("\t");
															if(m.find() && frags.length >= 10) {
																writer.append(rline).append("\n");
															} else {
																writer.append("00:00	")
																.append(rline).append("\n");
															}
														}
														writer.close();
														reader.close();
														typelog.delete();
														typelogNew.renameTo(typelog);
													} catch (IOException e) {
														logger.debug("error : {}",e);
													} finally {
														if(writer!=null) try {
															writer.close();
														} catch (IOException ignore) { }
														if(reader!=null) try {
															reader.close();
														} catch (IOException ignore) { }
													}
											
											
													return false;
												}
											});
										}
										return false;
									}
								});
							}
							return false;
						}
					});
				}
				return false;
			}
		});
		
		Writer writer = response.getWriter();
		writeHeader(response);
		
		ResponseWriter responseWriter = getDefaultResponseWriter(writer);
		responseWriter.object()
		.key("success").value(true)
		.key("lastCount").value(0)
		.endObject();
		responseWriter.done();
	}

}
