package org.fastcatgroup.analytics.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fastcatgroup.analytics.analysis.StatisticsService;
import org.fastcatgroup.analytics.db.AnalyticsDBService;
import org.fastcatgroup.analytics.db.MapperSession;
import org.fastcatgroup.analytics.db.mapper.RelateKeywordMapper;
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

	@RequestMapping("/relate/delete")
	@ResponseBody
	public String deleteRelateKeyword(@PathVariable String siteId, @RequestParam(required = false) String deleteIdList) throws Exception {
		boolean result = false;
		
		JSONStringer s = new JSONStringer();
		s.object().key("result").value(result).endObject();
		return s.toString();
	}

	@RequestMapping("/relate/update")
	@ResponseBody
	public String addUpdateRelateKeyword(
			@PathVariable String siteId, 
			@RequestParam String site,
			@RequestParam String category,
			@RequestParam("ID") String id,
			@RequestParam("KEYWORD") String keyword, 
			@RequestParam("VALUE") String value) throws Exception {

		boolean result = false;
		// keyword 가 존재하지 않으면 검사하지 않고 key,value 모두 insert
		// keyword가 존재하면, value하나씩 검사하여 없는것만 insert.
		
		logger.debug("keywordType:{} / siteId:{} / categoryId:{} / id:{} / keyword:{} / value:{}",
				"relate", site, category, id, keyword, value);
		
		
		
		
		JSONStringer s = new JSONStringer();
		s.object().key("result").value(result).endObject();
		return s.toString();
	}


	@RequestMapping("/relate/list")
	public ModelAndView relateKeywordList(@PathVariable String siteId
			, @RequestParam(required = false) String categoryId, @RequestParam(defaultValue = "1") Integer pageNo 
			, @RequestParam(required = false) String keyword , @RequestParam(required = false) Boolean exactMatch
			, @RequestParam(required = false) Boolean isEditable , @RequestParam(required = false) String targetId
			) throws Exception {

		int PAGE_SIZE = 10;

		ModelAndView mav = new ModelAndView();
		MapperSession<RelateKeywordMapper> mapperSession = null;

		try {
			ServiceManager serviceManager = ServiceManager.getInstance();
			AnalyticsDBService dbService = serviceManager.getService(AnalyticsDBService.class);
			StatisticsService statisticsService = serviceManager.getService(StatisticsService.class);
			mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			RelateKeywordMapper mapper = mapperSession.getMapper();
			
			int start = 0;
			//int end = 0;

			if (pageNo > 0) {
				start = (pageNo - 1) * PAGE_SIZE;
			}
			//end = start + PAGE_SIZE;

			String whereCondition = "";
			//TODO whereCondition에 start, end와 검색 keyword 처리.
			
			
			
			
			
			int totalSize = mapper.getCount(siteId);
			int filteredSize = mapper.getCountByWhereCondition(siteId, whereCondition);
			List<RelateKeywordVO> entryList = mapper.getEntryListByWhereCondition(siteId, whereCondition, start, PAGE_SIZE);


			mav.addObject("siteId", siteId);
			mav.addObject("categoryId", categoryId);
			mav.addObject("entryList", entryList);
			mav.addObject("start", 1);
			mav.addObject("pageNo", pageNo);
			mav.addObject("totalSize", totalSize);
			mav.addObject("filteredSize", filteredSize);
			mav.addObject("pageSize", PAGE_SIZE);
			mav.addObject("targetId", targetId);
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

		JSONObject jsonObj = null;

		int totalReadSize = 0;
		int PAGE_SIZE = 100;

		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		if (forView != null && forView.booleanValue()) {
			// 다운로드 하지 않고 웹페이지에서 보여준다.
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\"" + siteId+"_relate" + ".txt\"");
		}
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			int pageNo = 1;
			while (true) {
				int start = 0;
				if (pageNo > 0) {
					start = (pageNo - 1) * PAGE_SIZE + 1;
				}

				JSONArray columnList = jsonObj.getJSONArray("columnList");
				JSONArray array = jsonObj.getJSONArray("item");
				int readSize = array.length();
				totalReadSize += readSize;

				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					for (int j = 0; j < columnList.length(); j++) {
						String columnName = columnList.getString(j);
						String value = String.valueOf(obj.get(columnName));
						writer.append(value);
						if (j < columnList.length() - 1) {
							// 컬럼끼리 구분자는 탭이다.
							writer.append("\t");
						}
					}
					writer.append("\n");

				}

				int totalSize = jsonObj.getInt("totalSize");
				if (totalReadSize >= totalSize) {
					break;
				}
				pageNo++;
			}
		} catch (IOException e) {
			logger.error("download error", e);
		} finally {
			if (writer != null) {
				writer.close();
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
