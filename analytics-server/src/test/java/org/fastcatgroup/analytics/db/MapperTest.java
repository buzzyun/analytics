package org.fastcatgroup.analytics.db;

import org.fastcatgroup.analytics.db.mapper.AnalyticsMapper;
import org.fastcatgroup.analytics.db.mapper.SearchHitMapper;
import org.junit.Test;

public class MapperTest {

	@Test
	public void test() {
		TestMapper testMapper = new TestMapper(); 
		
		Class<?> mapperClass = TestMapper.class;
		
		boolean result = SearchHitMapper.class.isAssignableFrom(AnalyticsMapper.class);
		System.out.println(result);
		
		result = AnalyticsMapper.class.isAssignableFrom(SearchHitMapper.class);
		System.out.println(result);
		
		result = AnalyticsMapper.class.isAssignableFrom(mapperClass);
		System.out.println(result);

		
	}

	class TestMapper implements AnalyticsMapper {

		@Override
		public void createTable(String siteId) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void createIndex(String siteId) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void validateTable(String siteId) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dropTable(String siteId) throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int truncate(String siteId) throws Exception {
			// TODO Auto-generated method stub
			return 0;
		}

		
		
	}
}
