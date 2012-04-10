package com.ankit.session.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.ankit.session.servlet.SessionData;

public class CacheTable {
	public static final Log log = LogFactoryImpl.getLog(CacheTable.class);
	SessionStateTable ssChace = new SessionStateTable();

	public void add(SessionID sid, SessionData data){
		log.info("Add->Cache Session ID : "+sid.getString());
		log.info("Add->Cache Session Data :"+data.getString());
		ssChace.addSession(sid, data);
	}
	public SessionData get(SessionID sid,SessionVersion svn){
		log.info("GET->Cache Session ID : "+sid.getString());
		log.info("GET->Cache Session Version :"+svn.getString());
		SessionData sessionData= ssChace.getSession(sid);
		if( sessionData !=null && sessionData.getSessionVersion().getChangeCount() == svn.getChangeCount()){
			return sessionData;
		}else{
			ssChace.removeSession(sid);
			return null;
		}
	}
	
}
