package org.fastcatgroup.analytics.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/keyword")
public class SearchKeywordController extends AbstractController {
	
	
	@RequestMapping("/{dictionaryType}/list")
	public ModelAndView listDictionary(HttpSession session, @PathVariable String analysisId, @PathVariable String dictionaryType
			, @RequestParam String dictionaryId
			, @RequestParam(defaultValue = "1") Integer pageNo
			, @RequestParam(required = false) String keyword
			, @RequestParam(required = false) String searchColumn
			, @RequestParam(required = false) Boolean exactMatch
			, @RequestParam(required = false) Boolean isEditable
			, @RequestParam String targetId, @RequestParam(required = false) String deleteIdList) throws Exception {
		
		JSONObject jsonObj = null;
		Integer deletedSize = 0; 
//		logger.debug("deleteIdList >> {}", deleteIdList);
		if(deleteIdList != null && deleteIdList.length() > 0){
			String requestUrl = "/management/dictionary/delete.json";
			
			
			deletedSize = jsonObj.getInt("result");
		}
		
		
		String requestUrl = "/keyword/list.json";
		String dictionaryPrefix = dictionaryType;
		String dictionaryOption = null;
		int PAGE_SIZE = 10;
		int start = 0;
		
		if(pageNo > 0){
			start = (pageNo - 1) * PAGE_SIZE + 1;
		}
		
		String searchKeyword = null;
		if(exactMatch){
			searchKeyword = keyword;
		}else{
			if(keyword != null && keyword.length() > 0){
				searchKeyword = "%" + keyword + "%";
			}
		}
		if(searchColumn.equals("_ALL")){
			searchColumn = null;
		}
		////
		
		ModelAndView mav = new ModelAndView();
		dictionaryPrefix = dictionaryPrefix.toLowerCase();
		if(isEditable != null && isEditable.booleanValue()){
			mav.setViewName("manager/dictionary/" + dictionaryPrefix + "DictionaryEdit");
		}else{
			mav.setViewName("manager/dictionary/" + dictionaryPrefix + "Dictionary");
		}
		mav.addObject("analysisId", analysisId);
		mav.addObject("dictionaryId", dictionaryId);
		mav.addObject("dictionaryType", dictionaryType);
		mav.addObject("dictionaryOption", dictionaryOption);
		mav.addObject("list", jsonObj);
		mav.addObject("start", start);
		mav.addObject("pageNo", pageNo);
		mav.addObject("pageSize", PAGE_SIZE);
		mav.addObject("keyword", keyword);
		mav.addObject("searchColumn", searchColumn);
		mav.addObject("exactMatch", exactMatch);
		mav.addObject("targetId", targetId);
		mav.addObject("deletedSize", deletedSize);
		
		return mav;
	}
	
	
	
	@RequestMapping("/{dictionaryType}/download")
	public void downloadDictionary(HttpSession session, HttpServletResponse response, @PathVariable String analysisId, @PathVariable String dictionaryType
			, @RequestParam String dictionaryId, @RequestParam(required = false) Boolean forView) throws Exception {
		
		JSONObject jsonObj = null;
		
		String requestUrl = "/management/dictionary/list.json";
		
		int totalReadSize = 0;
		int PAGE_SIZE = 100;
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		if(forView != null && forView.booleanValue()){
			//다운로드 하지 않고 웹페이지에서 보여준다.
		}else{
			logger.debug("dictionaryId > {}", dictionaryId);
			response.setHeader("Content-disposition", "attachment; filename=\""+dictionaryId+".txt\"");
		}
		PrintWriter writer = null;
		try{
			writer = response.getWriter();
			int pageNo = 1;
			while(true){
				int start = 0;
				if(pageNo > 0){
					start = (pageNo - 1) * PAGE_SIZE + 1;
				}
				
				JSONArray columnList = jsonObj.getJSONArray("columnList");
				JSONArray array = jsonObj.getJSONArray(dictionaryId);
				int readSize = array.length();
				totalReadSize += readSize;
				
				for(int i =0; i<array.length(); i++){
					JSONObject obj = array.getJSONObject(i);
					for(int j =0; j<columnList.length(); j++){
						String columnName = columnList.getString(j);
						String value = String.valueOf(obj.get(columnName));
						writer.append(value);
						if(j<columnList.length() - 1){
							//컬럼끼리 구분자는 탭이다.
							writer.append("\t");
						}
					}
					writer.append("\n");
					
				}
			
				int totalSize = jsonObj.getInt("totalSize");
				if(totalReadSize >= totalSize){
					break;
				}
				pageNo++;
			}
		}catch(IOException e){
			logger.error("download error", e);
		} finally {
			if(writer != null){
				writer.close();
			}
		}
	}
	
	@RequestMapping("/{dictionaryType}/upload")
	public void uploadDictionary(HttpSession session, MultipartHttpServletRequest request, HttpServletResponse response, @PathVariable String analysisId, @PathVariable String dictionaryType
			, @RequestParam String dictionaryId) throws Exception {
		
		Iterator<String> itr = request.getFileNames();
		String fileName = null;
		try{
			fileName = itr.next();
		}catch(Exception ignore){
		}
		logger.debug("fileName {}", fileName);
		
		boolean isSuccess = false;
		String errorMessage = null;
		int totalCount = 0;
		
		
		
		try{
			Writer writer = response.getWriter();
			JSONWriter jsonWriter = new JSONWriter(writer);
			jsonWriter.object()
				.key("success").value(isSuccess)
				.key("count").value(totalCount);
			
			if(errorMessage != null){
				jsonWriter.key("errorMessage").value(errorMessage);
			}
			jsonWriter.endObject();
			writer.close();
		}catch(Exception e){
			logger.error("", e);
		}
	
	}
}
