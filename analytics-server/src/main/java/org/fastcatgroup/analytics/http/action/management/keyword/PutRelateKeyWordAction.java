package org.fastcatgroup.analytics.http.action.management.keyword;

import java.io.Writer;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.apache.ibatis.plugin.Plugin;
import org.fastcatgroup.analytics.http.ActionAuthority;
import org.fastcatgroup.analytics.http.ActionAuthorityLevel;
import org.fastcatgroup.analytics.http.ActionMapping;
import org.fastcatgroup.analytics.http.action.ActionRequest;
import org.fastcatgroup.analytics.http.action.ActionResponse;
import org.fastcatgroup.analytics.http.action.AuthAction;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.fastcatgroup.analytics.util.ResponseWriter;

@ActionMapping(value="/management/keyword/put", authority=ActionAuthority.Keyword, authorityLevel=ActionAuthorityLevel.WRITABLE)
public class PutRelateKeyWordAction extends AuthAction {

	@Override
	public void doAuthAction(ActionRequest request, ActionResponse response) throws Exception {
		
		String categoryId = request.getParameter("category");

		String pluginId = request.getParameter("pluginId");
		String dictionaryId = request.getParameter("dictionaryId");
/*
		PluginService pluginService = ServiceManager.getInstance().getService(PluginService.class);
		Plugin plugin = pluginService.getPlugin(pluginId);
		AnalysisPlugin analysisPlugin = (AnalysisPlugin) plugin;

		DictionaryDAO dictionaryDAO = analysisPlugin.getDictionaryDAO(dictionaryId);

		List<ColumnSetting> columnSettingList = dictionaryDAO.columnSettingList();

		int count = 0;
		String errorMessage = null;
		if (columnSettingList != null && columnSettingList.size() > 0) {
			String[] columns = new String[columnSettingList.size()];
			Object[] values = new Object[columnSettingList.size()];
			for (int i = 0; i < columnSettingList.size(); i++) {
				ColumnSetting columnSetting = columnSettingList.get(i);
				String name = columnSetting.getName().toUpperCase();
				String type = columnSetting.getType();
				columns[i] = name;
				String value = request.getParameter(name);

				if (type.startsWith("int") || type.startsWith("INT")) {
					int intValue = 0;
					try {
						intValue = Integer.parseInt(value);
					} catch (Exception ignore) {
					}
					values[i] = intValue;
				} else if (type.contains("char") || type.contains("CHAR")) {
					values[i] = value;
				} else {
					values[i] = value;
				}
			}
			try{
				count = dictionaryDAO.putEntry(columns, values);
			}catch(Exception e){
				if(e.getCause() instanceof SQLIntegrityConstraintViolationException){
					errorMessage = "Duplicate word exist.";
				}else{
					errorMessage = e.getCause().toString();
				}
			}
		}

		if(count > 0){
			analysisPlugin.dictionaryStatusDAO().updateUpdateTime(dictionaryId);
		}
		
		Writer writer = response.getWriter();
		ResponseWriter resultWriter = getDefaultResponseWriter(writer);
		resultWriter.object().key("success").value(count > 0);
		if(errorMessage != null){
			resultWriter.key("errorMessage").value(errorMessage);
		}
		resultWriter.endObject();
		resultWriter.done();
*/
	}

}
