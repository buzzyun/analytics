package org.fastcatgroup.analytics.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
@RequestMapping("/report/keyword")
public class SearchKeywordController extends AbstractController {

	@RequestMapping("/{keywordId}/index")
	public ModelAndView keywordIndex(@PathVariable String keywordId) {
		ModelAndView mav = new ModelAndView();

		mav.setViewName("report/keyword/index");
		mav.addObject("keywordId", keywordId);
		return mav;

	}

	@RequestMapping("/{keywordId}/delete")
	@ResponseBody
	public String deleteRelateKeyword(@PathVariable String keywordId, @RequestParam(required = false) String deleteIdList) throws Exception {
		boolean result = false;
		
		JSONStringer s = new JSONStringer();
		s.object().key("result").value(result).endObject();
		return s.toString();
	}

	@RequestMapping("/{keywordId}/update")
	@ResponseBody
	public String addUpdateRelateKeyword(@PathVariable String keywordId, String keyword, @RequestParam List<String> valueList) throws Exception {

		boolean result = false;
		// keyword 가 존재하지 않으면 검사하지 않고 key,value 모두 insert
		
		// keyword가 존재하면, value하나씩 검사하여 없는것만 insert.
		
		
		JSONStringer s = new JSONStringer();
		s.object().key("result").value(result).endObject();
		return s.toString();
	}

	@RequestMapping("/{keywordId}/list")
	public ModelAndView relateKeywordList(@PathVariable String keywordId, @RequestParam(defaultValue = "1") Integer pageNo
			, @RequestParam(required = false) String category, @RequestParam(required = false) String keyword
			, @RequestParam(required = false) Boolean exactMatch, @RequestParam(required = false) Boolean isEditable)
			throws Exception {

		JSONStringer stringer = null;
		int PAGE_SIZE = 10;

		ModelAndView mav = new ModelAndView();
		MapperSession<RelateKeywordMapper> mapperSession = null;

		try {
			stringer = new JSONStringer();
			AnalyticsDBService dbService = ServiceManager.getInstance().getService(AnalyticsDBService.class);
			mapperSession = dbService.getMapperSession(RelateKeywordMapper.class);
			RelateKeywordMapper mapper = mapperSession.getMapper();

			int start = 0;
			int end = 0;

			if (pageNo > 0) {
				start = (pageNo - 1) * PAGE_SIZE + 1;
			}
			end = start + PAGE_SIZE;

			String whereCondition = "";

			int totalSize = mapper.getCount(category);
			int filteredSize = mapper.getCountByWhereCondition(category, whereCondition);
			List<RelateKeywordVO> entryList = mapper.getEntryListByWhereCondition(category, whereCondition, start, end);

			stringer.object().key("totalSize").value(totalSize).key("filteredSize").value(filteredSize).key(keywordId).array();
			for (RelateKeywordVO vo : entryList) {
				stringer.object().key("KEYWORD").value(vo.getKeyword()).key("VALUE").value(vo.getValue()).endObject();
			}
			stringer.endArray().endObject();
			mav.addObject("keywordId", keywordId);
			mav.addObject("list", new JSONObject(stringer.toString()));
			mav.addObject("start", 1);
			mav.addObject("pageNo", pageNo);
			mav.addObject("totalSize", totalSize);
			mav.addObject("pageSize", PAGE_SIZE);
			mav.setViewName("report/keyword/" + keywordId + "Keyword");
		} finally {
			if (mapperSession != null) {
				mapperSession.closeSession();
			}
		}
		return mav;
	}

	@RequestMapping("/{keywordId}/download")
	public void downloadDictionary(HttpSession session, HttpServletResponse response, @PathVariable String analysisId, @PathVariable String dictionaryType,
			@RequestParam String keywordId, @RequestParam(required = false) Boolean forView) throws Exception {

		JSONObject jsonObj = null;

		String requestUrl = "/management/dictionary/list.json";

		int totalReadSize = 0;
		int PAGE_SIZE = 100;

		response.setContentType("text/plain");
		response.setCharacterEncoding("utf-8");
		if (forView != null && forView.booleanValue()) {
			// 다운로드 하지 않고 웹페이지에서 보여준다.
		} else {
			logger.debug("dictionaryId > {}", keywordId);
			response.setHeader("Content-disposition", "attachment; filename=\"" + keywordId + ".txt\"");
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
				JSONArray array = jsonObj.getJSONArray(keywordId);
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

	@RequestMapping("/{keywordId}/upload")
	public void uploadDictionary(HttpSession session, MultipartHttpServletRequest request, HttpServletResponse response, @PathVariable String analysisId,
			@PathVariable String dictionaryType, @RequestParam String keywordId) throws Exception {

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
