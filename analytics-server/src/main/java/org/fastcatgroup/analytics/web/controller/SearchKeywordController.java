package org.fastcatgroup.analytics.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordValueMapper;
import org.fastcatgroup.analytics.db.vo.RelateKeywordVO;
import org.fastcatgroup.analytics.service.ServiceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/{siteId}/report/keyword")
public class SearchKeywordController extends AbstractController {

	@RequestMapping("/{keywordType}/index")
	public ModelAndView keywordIndex(@PathVariable String siteId, @PathVariable String keywordType) {
		ModelAndView mav = new ModelAndView();
		ServiceManager serviceManager = ServiceManager.getInstance();
		StatisticsService statisticsService = serviceManager.getService(StatisticsService.class);
		mav.addObject("siteConfig", statisticsService.getSiteCategoryListConfig().getList());
		mav.setViewName("report/keyword/index");
		mav.addObject("siteId", siteId);
		mav.addObject("keywordType", keywordType);
		return mav;

	}
	
	private void deleteRelateKeyword(String siteId, String deleteIdList) throws Exception {
		//value 를 먼저 지운 후 keyword 를 지우도록 한다.
		AnalyticsDBService service = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<RelateKeywordMapper> relateMapperSession = null;
		MapperSession<RelateKeywordValueMapper> relateValueMapperSession = null;
		
		try {
			
			relateMapperSession = service.getMapperSession(RelateKeywordMapper.class);
			relateValueMapperSession = service.getMapperSession(RelateKeywordValueMapper.class);
			
			RelateKeywordMapper relateMapper = relateMapperSession.getMapper();
			RelateKeywordValueMapper relateValueMapper = relateValueMapperSession.getMapper();
			
			relateMapper.deleteEntryList(siteId, deleteIdList);
			
			relateMapperSession.commit();
			relateValueMapperSession.commit();
		} finally {
			if(relateValueMapperSession!=null) {
				relateValueMapperSession.closeSession();
			}
			if(relateMapperSession!=null) {
				relateMapperSession.closeSession();
			}
		}
	}

	@RequestMapping("/relate/update")
	@ResponseBody
	public String addUpdateRelateKeyword(
			@PathVariable String siteId, 
			@RequestParam("ID") String id,
			@RequestParam("KEYWORD") String keyword, 
			@RequestParam("VALUE") String value) throws Exception {

		boolean result = false;
		logger.debug("keywordType:{} / siteId:{} / id:{} / keyword:{} / value:{}",
				"relate", siteId, id, keyword, value);
		
		AnalyticsDBService service = ServiceManager.getInstance().getService(AnalyticsDBService.class);
		MapperSession<RelateKeywordMapper> relateMapperSession = null;
		MapperSession<RelateKeywordValueMapper> relateValueMapperSession = null;
		
		try {
			relateMapperSession = service.getMapperSession(RelateKeywordMapper.class);
			relateValueMapperSession = service.getMapperSession(RelateKeywordValueMapper.class);
			
			RelateKeywordMapper relateMapper = relateMapperSession.getMapper();
			RelateKeywordValueMapper relateValueMapper = relateValueMapperSession.getMapper();
			
			result = addKeywordData(siteId, id, keyword, value, relateMapper, relateValueMapper);
		
			relateMapperSession.commit();
			relateValueMapperSession.commit();
			result = true;
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			if(relateValueMapperSession!=null) {
				relateValueMapperSession.closeSession();
			}
			if(relateMapperSession!=null) {
				relateMapperSession.closeSession();
			}
		}
		JSONStringer s = new JSONStringer();
		s.object().key("success").value(result).endObject();
		return s.toString();
	}

	private boolean addKeywordData( String siteId, String id, String keyword, String value
			,RelateKeywordMapper relateMapper ,RelateKeywordValueMapper relateValueMapper 
			) throws Exception {

		boolean result = false;
		// keyword 가 존재하지 않으면 검사하지 않고 key,value 모두 insert
		// keyword가 존재하면, value하나씩 검사하여 없는것만 insert.
		
		try {
			keyword = keyword.trim();
			
			RelateKeywordVO relateEntry = null;
			
			if(id!=null && !"".equals(id)) {
				relateEntry = relateMapper.getEntryById(siteId, id);
			} else {
				relateEntry = relateMapper.getEntry(siteId, keyword);
			}
			
			List<String> valuesList = new ArrayList<String>();
			if(value!=null) {
				String[] valueArray = value.split(",");
				for(String valueString : valueArray) {
					valuesList.add(valueString.trim());
				}
			}
			if(relateEntry!=null && relateEntry.getId()!=0) {
				
				relateEntry.setKeyword(keyword);
				relateEntry.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				
				relateMapper.updateEntry(siteId, relateEntry);
				
				if (id != null && !"".equals(id) && id.equals(String.valueOf(relateEntry.getId()))) {
					//기존값의 수정 이라면, 기존의 값들을 모두 지워 주어야 한다.
					relateValueMapper.deleteValues(siteId, relateEntry.getId());
				} else {
					//그렇지 않다면 같지 않은것들만 입력 하도록 한다.(수동입력 시 입력된 키값이 종전에 있는 값일 경우)
					String entryValue = relateEntry.getValue();
					if (entryValue == null) {
						entryValue = "";
					}
					String[] entryValueArray = entryValue.split(",");
					for (int vinx = 0; vinx < entryValueArray.length; vinx++) {
						String entryValueString = entryValueArray[vinx].trim();
						if(valuesList.contains(entryValueString)) {
							valuesList.remove(entryValueString);
						}
					}
				}
				for(String valueString : valuesList) {
					valueString = valueString.trim();
					relateValueMapper.putEntry(siteId, relateEntry.getId(), valueString);
				}
			} else {
				//아무것도 없다면 둘 다 입력하도록 한다.
				relateEntry = new RelateKeywordVO (keyword, new Timestamp(System.currentTimeMillis()));
				relateMapper.putEntry(siteId, relateEntry);
				
				for(String valueString : valuesList) {
					valueString = valueString.trim();
					relateValueMapper.putEntry(siteId, relateEntry.getId(), valueString);
				}
			}
		
			result = true;
		} catch (Exception e) {
			logger.error("",e);
		} finally {
		}
		return result;
	}

	@RequestMapping("/relate/list")
	public ModelAndView relateKeywordList(@PathVariable String siteId
			, @RequestParam(required = false) String categoryId, @RequestParam(defaultValue = "1") Integer pageNo 
			, @RequestParam(required = false) String keyword , @RequestParam(required = false) Boolean exactMatch
			, @RequestParam(required = false) Boolean isEditable , @RequestParam(required = false) String targetId
			, @RequestParam(required = false) String deleteIdList
			) throws Exception {

		int PAGE_SIZE = 10;

		ModelAndView mav = new ModelAndView();
		MapperSession<RelateKeywordMapper> mapperSession = null;

		//삭제할 것이 있다면 삭제한다.
		if(deleteIdList!=null && !"".equals(deleteIdList)) {
			deleteRelateKeyword(siteId, deleteIdList);
		}
		
		try {
			ServiceManager serviceManager = ServiceManager.getInstance();
			AnalyticsDBService dbService = serviceManager.getService(AnalyticsDBService.class);
			mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			RelateKeywordMapper mapper = mapperSession.getMapper();
			
			int start = 0;

			if (pageNo > 0) {
				start = (pageNo - 1) * PAGE_SIZE;
			}

			String whereCondition = "";
			//TODO whereCondition에 start, end와 검색 keyword 처리.
			
			if(keyword!=null && !"".equals(keyword)) {
				if(exactMatch) {
					whereCondition += "AND (a.keyword='"+keyword+"' OR b.value='"+keyword+"') ";
				} else {
					whereCondition += "AND (a.keyword like '%"+keyword+"%' OR b.value like '%"+keyword+"%') ";
				}
				
				if(!"".equals(whereCondition)) {
					whereCondition = whereCondition.substring(3);
				}
			}
			
			
			int totalSize = mapper.getCount(siteId);
			int filteredSize = mapper.getCountByWhereCondition(siteId, whereCondition);
			List<RelateKeywordVO> entryList = mapper.getEntryListByWhereCondition(siteId, whereCondition, start, PAGE_SIZE);


			mav.addObject("siteId", siteId);
			mav.addObject("categoryId", categoryId);
			mav.addObject("entryList", entryList);
			mav.addObject("start", start);
			mav.addObject("pageNo", pageNo);
			mav.addObject("totalSize", totalSize);
			mav.addObject("filteredSize", filteredSize);
			mav.addObject("pageSize", PAGE_SIZE);
			mav.addObject("targetId", targetId);
			mav.addObject("keyword", keyword);
			mav.addObject("exactMatch", exactMatch);
			if(isEditable != null && isEditable.booleanValue()) {
				mav.setViewName("report/keyword/relateKeywordEdit");
			} else {
				mav.setViewName("report/keyword/relateKeyword");
			}
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		return mav;
	}

	@RequestMapping("/relate/download")
	public void downloadDictionary(HttpServletResponse response, @PathVariable String siteId, 
			@RequestParam(required = false) Boolean forView) throws Exception {

		int PAGE_SIZE = 100;

		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		if (forView != null && forView.booleanValue()) {
			// 다운로드 하지 않고 웹페이지에서 보여준다.
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\"" + siteId+"_relate" + ".txt\"");
		}
		PrintWriter writer = null;
		
		MapperSession<RelateKeywordMapper> mapperSession = null;
		
		int start = 0;
		
		try {
			
			ServiceManager serviceManager = ServiceManager.getInstance();
			AnalyticsDBService dbService = serviceManager.getService(AnalyticsDBService.class);
			mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			
			RelateKeywordMapper mapper = mapperSession.getMapper();
			
			int totalSize = mapper.getCountByWhereCondition(siteId, "");
			
			List<RelateKeywordVO> entryList = mapper.getEntryListByWhereCondition(siteId, "", start, PAGE_SIZE - 1);
			
			writer = response.getWriter();
			for (int rsize=0;rsize<totalSize;) {
				
				int readSize = entryList.size();
				if(readSize == 0) {
					break;
				}
				rsize += readSize;
				for (int i = 0; i < entryList.size(); i++) {
					RelateKeywordVO entry = entryList.get(i);
					
					writer.append(String.valueOf(entry.getKeyword())).append("\t");
					writer.append(String.valueOf(entry.getValue()));
					
					writer.append("\n");
				}
				
				start+=PAGE_SIZE;
				entryList = mapper.getEntryListByWhereCondition(siteId, "", start, PAGE_SIZE);
			}
		} catch (Exception e) {
			logger.error("download error", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
			
			if(mapperSession != null) {
				mapperSession.closeSession();
			}
		}
	}

	@RequestMapping("/relate/upload")
	public void uploadDictionary(HttpSession session, MultipartHttpServletRequest request, HttpServletResponse response
			, @PathVariable String siteId) throws Exception {

		Iterator<String> itr = request.getFileNames();
		String fileName = null;
		try {
			fileName = itr.next();
		} catch (Exception ignore) {
		}
		logger.debug("fileName {}", fileName);
		boolean isSuccess = false;
		String errorMessage = null;
		int totalCount = 0;

		
		if (fileName != null) {
			MultipartFile multipartFile = request.getFile(fileName);
			logger.debug("uploaded {}", multipartFile.getOriginalFilename());
			
			BufferedReader reader = null;
			
			AnalyticsDBService service = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			MapperSession<RelateKeywordMapper> relateMapperSession = null;
			MapperSession<RelateKeywordValueMapper> relateValueMapperSession = null;
			
			try {
				// just temporary save file info into ufile
				logger.debug("len {}", multipartFile.getBytes().length);
				logger.debug("getBytes {}", new String(multipartFile.getBytes()));
				logger.debug("getContentType {}", multipartFile.getContentType());
				logger.debug("getOriginalFilename {}", multipartFile.getOriginalFilename());
				
				relateMapperSession = service.getMapperSession(RelateKeywordMapper.class);
				relateValueMapperSession = service.getMapperSession(RelateKeywordValueMapper.class);
				
				RelateKeywordMapper relateMapper = relateMapperSession.getMapper();
				RelateKeywordValueMapper relateValueMapper = relateValueMapperSession.getMapper();
			
	
				String contentType = multipartFile.getContentType();
				
				if(!contentType.contains("text")){
					
					isSuccess = false;
					errorMessage = "File must be plain text.";
				}else{
					
					int bulkSize = 100;
					
					reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
					int count = 0;
					
					String line = null;
					do {
						while((line = reader.readLine()) != null){
							
							String[] rawLine = line.split("\t");
							
							String keyword = rawLine[0];
							String value = rawLine[1];
							
							boolean ret = addKeywordData(siteId, "", keyword, value, relateMapper, relateValueMapper);
							
							if(!ret) {
								throw new IOException("file 구조가 다릅니다.");
							}
							
							count++;
							if(count == bulkSize){
								break;
							}
						}
					} while(line != null);
						
				}
				
				isSuccess = true;
			} catch (IOException e) {
				isSuccess = false;
				errorMessage = e.getMessage();
			} finally {
				if(reader != null){
					try {
						reader.close();
					} catch (IOException ignore) {
					}
				}
				
				if (relateMapperSession != null) {
					relateMapperSession.closeSession();
				}
				
				if (relateValueMapperSession != null) {
					relateValueMapperSession.closeSession();
				}
			}
		}

		try {
			Writer writer = response.getWriter();
			JSONWriter jsonWriter = new JSONWriter(writer);
			jsonWriter.object().key("success").value(isSuccess).key("count").value(totalCount);

			if (errorMessage != null) {
				jsonWriter.key("errorMessage").value(errorMessage);
			}
			jsonWriter.endObject();
			writer.close();
		} catch (Exception e) {
			logger.error("", e);
		}

	}
}
