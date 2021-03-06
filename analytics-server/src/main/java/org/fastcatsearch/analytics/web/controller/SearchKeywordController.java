package org.fastcatsearch.analytics.web.controller;

import org.fastcatsearch.analytics.analysis.StatisticsService;
import org.fastcatsearch.analytics.db.AnalyticsDBService;
import org.fastcatsearch.analytics.db.MapperSession;
import org.fastcatsearch.analytics.db.mapper.RelateKeywordMapper;
import org.fastcatsearch.analytics.db.mapper.RelateKeywordValueMapper;
import org.fastcatsearch.analytics.db.vo.RelateKeywordVO;
import org.fastcatsearch.analytics.env.Settings;
import org.fastcatsearch.analytics.service.ServiceManager;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/{siteId}/report/keyword")
public class SearchKeywordController extends AbstractController {

	@RequestMapping("/relate/index")
	public ModelAndView keywordIndex(@PathVariable String siteId) {
		ModelAndView mav = new ModelAndView();
		ServiceManager serviceManager = ServiceManager.getInstance();
		StatisticsService statisticsService = serviceManager.getService(StatisticsService.class);
		mav.addObject("siteConfig", statisticsService.getSiteListSetting().getSiteList());
		mav.setViewName("report/keyword/relateKeyword");
		return mav;

	}
	
	private void deleteRelateKeyword(@PathVariable String siteId, String deleteIdList) throws Exception {
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
	public ModelAndView addUpdateRelateKeyword(
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
		ModelAndView mav = new ModelAndView();
		mav.addObject("content", s.toString());
		mav.setViewName("text");
		return mav;
	}
	
	@RequestMapping("/relate/apply")
	public ModelAndView applyRelateKeyword(@PathVariable String siteId) {
		boolean result = false;
		
		ServiceManager serviceManager = ServiceManager.getInstance();
		StatisticsService statisticsService = serviceManager.getService(StatisticsService.class);
		AnalyticsDBService service = serviceManager.getService(AnalyticsDBService.class);
		MapperSession<RelateKeywordMapper> relateMapperSession = null;
		
		int PAGE_SIZE=100;
		
		int start = 0;

		try {
			
			relateMapperSession = service.getMapperSession(RelateKeywordMapper.class);
			
			RelateKeywordMapper relateMapper = relateMapperSession.getMapper();
			
			int totalSize = relateMapper.getCount(siteId);

			Map<String, List<String>>keywordMap = new HashMap<String, List<String>>();
			
			while(start < totalSize) {

				List<RelateKeywordVO> entryList = relateMapper.getEntryList(siteId, start, PAGE_SIZE);

				for (int i = 0; i < entryList.size(); i++) {
					RelateKeywordVO entry = entryList.get(i);
					String valueString = entry.getValue();
					if(valueString!=null) {
						String[] values = valueString.split(",");
						if(values.length > 0) {
							List<String> keywordList = new ArrayList<String>(values.length);
							for (String v : values) {
								keywordList.add(v);
							}
							keywordMap.put(entry.getKeyword(), keywordList);
							logger.trace("keyword:{} / values:{}", entry.getKeyword(), keywordList);
						}
					}
				}
				
				start+=PAGE_SIZE;
			}
			statisticsService.updateRelativeKeywordMap(siteId, keywordMap);
			result = true;
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			if(relateMapperSession!=null) {
				relateMapperSession.closeSession();
			}
		}
		
		JSONStringer s = new JSONStringer();
		s.object().key("success").value(result).endObject();
		ModelAndView mav = new ModelAndView();
		mav.addObject("content", s.toString());
		mav.setViewName("text");
		return mav;
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
			, @RequestParam(defaultValue = "1") Integer pageNo 
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
			
			//whereCondition에 start, end와 검색 keyword 처리.
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
			//int filteredSize = mapper.getCount(siteId);//mapper.getCountByKeyword(siteId, exactMatch, keyword);
			//int filteredSize = mapper.getCountByKeyword(siteId, exactMatch, keyword);
			int filteredSize = mapper.getCountByWhereCondition(siteId, whereCondition);
			//List<RelateKeywordVO> entryList = mapper.getEntryListByKeyword(siteId, exactMatch, keyword, start, PAGE_SIZE);
			List<RelateKeywordVO> entryList = mapper.getEntryListByWhereCondition(siteId, whereCondition, start, PAGE_SIZE);

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
				mav.setViewName("report/keyword/relateKeywordList");
			}
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		return mav;
	}

	@RequestMapping("/relate/download")
	public void downloadRelateKeyword(HttpServletResponse response, @PathVariable String siteId, 
			@RequestParam(required = false) Boolean forView) throws Exception {

		int PAGE_SIZE = 100;
		
		Settings settings = environment.settingManager().getSystemSettings();
		String charEncoding = settings.getString("download.characterEncoding", "utf-8");
		String fileExt = settings.getString("download.fileExt", "txt");
		String delimiter = settings.getString("download.delimiter", "\t");

		response.setContentType("text/plain");
		response.setCharacterEncoding(charEncoding);
		if (forView != null && forView.booleanValue()) {
			// 다운로드 하지 않고 웹페이지에서 보여준다.
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\"" + siteId+"_relate" + "."+fileExt+"\"");
		}
		PrintWriter writer = null;
		
		MapperSession<RelateKeywordMapper> mapperSession = null;
		
		int start = 0;
		
		try {
			
			ServiceManager serviceManager = ServiceManager.getInstance();
			AnalyticsDBService dbService = serviceManager.getService(AnalyticsDBService.class);
			mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			
			RelateKeywordMapper mapper = mapperSession.getMapper();
			
			int totalSize = mapper.getCount(siteId);
			
			List<RelateKeywordVO> entryList = mapper.getEntryList(siteId, start, PAGE_SIZE - 1);
			
			writer = response.getWriter();
			for (int rsize=0;rsize<totalSize;) {
				
				int readSize = entryList.size();
				if(readSize == 0) {
					break;
				}
				rsize += readSize;
				for (int i = 0; i < entryList.size(); i++) {
					RelateKeywordVO entry = entryList.get(i);
					
					String keywordStr = entry.getKeyword().replaceAll(delimiter, "\\"+delimiter);
					writer.append(keywordStr).append(delimiter);
					writer.append(String.valueOf(entry.getValue()));
					
					writer.append("\n");
				}
				
				start+=PAGE_SIZE;
				entryList = mapper.getEntryList(siteId, start, PAGE_SIZE);
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
	public void uploadRelateKeyword(HttpSession session, MultipartHttpServletRequest request, HttpServletResponse response
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

		Writer writer = null;
		try {
			writer = response.getWriter();
			JSONWriter jsonWriter = new JSONWriter(writer);
			jsonWriter.object().key("success").value(isSuccess).key("count").value(totalCount);

			if (errorMessage != null) {
				jsonWriter.key("errorMessage").value(errorMessage);
			}
			jsonWriter.endObject();
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			if(writer!=null) try {
				writer.close();
			} catch (Exception ignore) { }
		}

	}
}
