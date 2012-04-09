package com.ankit.session.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ankit.session.model.CacheTable;
import com.ankit.session.model.SessionCookie;
import com.ankit.session.model.SessionStateTable;

/**The SessionStateManger is responsible for holding the single refrence of the
 * Session table.
 *  
 * 
 * @author ankitsingh
 *
 */
public class ServerContext {
	
	
	private ServerContext(){}
	
	private static HttpServletRequest currentRequest;
	
	private static HttpServletResponse currentResponse;
	
	private static SessionCookie sessionCookie;
	
	private String responseHTML ;
	
	public String getResponseHTML(){
		return responseHTML;
	}
	public void setResponseHTML(String s){
		responseHTML = s;
	}
	public  SessionCookie getSessionCookie() {
		return sessionCookie;
	}
	public  void setSessionCookie(SessionCookie sc) {
		sessionCookie = sc;
	}
//	public  int getCallID(){
//		return ++callID;''
//	}
	private static int maxSessionAge = 1;
	/**
	 * The instance variable for the session state manager
	 */
	private static ServerContext _instance;
	
	private static SessionStateTable sstbl = new SessionStateTable();
	
	private static CacheTable cctbl = new CacheTable();
		
	public SessionStateTable getSessionStateTable(){
		return sstbl;
	}
	public   CacheTable getCacheTable(){
		return cctbl;
	}
	/**
	 * Returns the instance of SessionStateManger
	 * @return
	 * 		Instance of SessionStateManger	
	 */
	public static ServerContext getInstance(){
		if(_instance == null){
			_instance = new ServerContext();
		}
		return _instance;
		
	}
	public void setCurrentRequest(HttpServletRequest request){
		currentRequest = request;
	}
	public HttpServletRequest getCurrentRequest(){
		return currentRequest;
	}
	public HttpServletResponse getCurrentResponse(){
		return currentResponse;
	}
	public void setCurrentResponse(HttpServletResponse res){
		currentResponse = res;
	}
	
	/**
	 * @return
	 */
	public  int getMaxSessionAge() {
		return maxSessionAge;
	}

	public  void setMaxSessionAge(int maxSessionAge) {
		ServerContext.maxSessionAge = maxSessionAge;
	}
	
	
}
