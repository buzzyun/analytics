package org.fastcatgroup.analytics.db;

import org.apache.ibatis.session.SqlSession;

public class MapperSession<T> {
	private SqlSession session;
	private T mapper;
	
	public SqlSession getSession() {
		return session;
	}
	
	public MapperSession(SqlSession session, T mapper){
		this.session = session;
		this.mapper = mapper;
	}
	
	public T getMapper(){
		return mapper;
	}
	
	public void commit(){
		if(session != null){
			session.commit();
		}
	}
	
	public void rollback(){
		if(session != null){
			session.rollback();
		}
	}
	public void closeSession(){
		if(session != null){
			session.commit();
			session.close();
		}
	}
}
