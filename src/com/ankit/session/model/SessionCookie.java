package com.ankit.session.model;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

public class SessionCookie {
	private  final static String cookieName = "CS5300PROJECT1SESSIONAS2536";

	private SessionID sid;
	private SessionVersion svn;
	private Cookie c;
	private static final Log log = LogFactoryImpl.getLog(SessionCookie.class);
	public SessionCookie(SessionID sid,SessionVersion svn){
		this.sid = sid;
		this.svn = svn;
		
	}
	public SessionCookie(Cookie c){
		this.c = c;
		try{
		String[] strArr = c.getValue().split("_");
		sid = new SessionID(strArr[0], new IPP(strArr[1],Integer.parseInt(strArr[2])));
		svn = new SessionVersion(Integer.parseInt(strArr[3]),new IPP(strArr[4],Integer.parseInt(strArr[5])),
				new IPP(strArr[6],Integer.parseInt(strArr[7])));
		if(svn.getBackup().getServerIP().equals("0.0.0.0"))
		{
			svn.setBackup(null);
		}
		log.info("Session Cookie :"+this.getString());
		}catch(ArrayIndexOutOfBoundsException e){
			log.error(" Array Index out of bounds");
			e.printStackTrace();
			//TODO do we need to handle such scenario
		}
	}
	
	public SessionVersion getSessionVersion(){
		return svn;
	}
	public SessionID getSessionID(){
		return sid;
	}
	public Cookie getCookie(){
		if(c == null){
			c = new Cookie(cookieName, this.getString());
		}
		c.setValue(this.getString());
		return c;
	}
	public String getString() {
		StringBuffer sb = new StringBuffer();
		//sessionNumber_serverIP_udpPort_changeCount_primaryIP_primaryPort_backupIP_backupPort
		sb.append(sid.getSessionNumber()).append("_");
		sb.append(sid.getServerID().getServerIP()).append("_");
		sb.append(sid.getServerID().getUdpPortID()).append("_");
		sb.append(svn.getChangeCount()).append("_");
		sb.append(svn.getPrimary().getServerIP()).append("_");
		sb.append(svn.getPrimary().getUdpPortID()).append("_");
		if(svn.getBackup() == null){
			sb.append("0.0.0.0_");
			sb.append("0");
		}else{
		sb.append(svn.getBackup().getServerIP()).append("_");
		sb.append(svn.getBackup().getUdpPortID()).append("");
		}
		return sb.toString();
	}

	public void setSessionVerion(SessionVersion sessionVersion) {
		this.svn = sessionVersion;
		
	}
	
}
