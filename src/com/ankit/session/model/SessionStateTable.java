package com.ankit.session.model;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.ankit.session.servlet.SessionData;

public class SessionStateTable {
	/**
	 * SessionState table used to store the list of session in this server
	 * Used ConcurrentHashMap to obtain bucket level locking
	 * currently assuming the value of concurrent request to be 100
	 */
	private static ConcurrentHashMap<SessionID, SessionData> sstbl = new ConcurrentHashMap<SessionID, SessionData>(100);


	/**
	 * Insert or update the SessionTable
	 * @param sessionId - uniques session id
	 * @param state @see SessionState.java
	 */
	public void addSession(SessionID sessionId,SessionData state){
		System.out.println("SessionStateTable.addSession()");
		synchronized (sessionId) {
			sstbl.put(sessionId, state);
		}
		System.out.println("SessionStateTable.addSession() SID :"+sessionId);
		printTable();
	}
	private void printTable(){
		for(SessionID key : sstbl.keySet()){
			System.out.println("Table Entry -->:"+key);
		}
	}
	/**
	 * This method is used to retrieve the session state	
	 * @param sessionID: valid sessionID string 
	 * @return SessionState object
	 */	
	public SessionData getSession(SessionID sessionId){
		System.out.println("SessionStateTable.getSession() SID : "+sessionId);
		printTable();
		synchronized (sessionId) {
			System.out.println("SessionStateTable.getSession() "+sstbl.containsKey(sessionId));
			SessionData data = sstbl.get(sessionId);
			System.out.println("SessionStateTable.getSession() Data :"+data);
			if(sstbl.get(sessionId) != null){
			sstbl.get(sessionId).refresh();
			}
			return sstbl.get(sessionId);
		}
	}
	/**Remove the session from the session table
	 * @param sessionId
	 */
	public void removeSession(SessionID sessionId){
		synchronized (sessionId) {

			sstbl.remove(sessionId);
		}
		printTable();
	}

	
	public void cleanUp(){
		printTable();
		Date currentTime = Calendar.getInstance().getTime();
		for(SessionID key : sstbl.keySet()){
			System.out.println("SessionStateManager.cleanUp() Checking Session :"+key);
			if(currentTime.compareTo(sstbl.get(key).getDiscardTime()) > 0){
				System.out.println("SessionStateManager.cleanUp() Session Expired Key:"+key);
				System.out
				.println("SessionStateManager.cleanUp() Expiry time : "
						+ sstbl.get(key).getDiscardTime());
				;				System.out.println("SessionStateManager.cleanUp() Current Time :"+currentTime);
				sstbl.remove(key);
			}

		}

	}




}
