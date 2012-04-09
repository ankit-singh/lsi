package com.ankit.session.servlet;

import java.util.Calendar;
import java.util.Date;

import com.ankit.session.model.SessionVersion;

/**
 * @author ankitsingh
 *
 */
public class SessionData {
	/**
	 * The version number 
	 */
	SessionVersion svn;
	/**
	 * The message
	 */
	String message;
	/**
	 * The session age in minutes
	 */
	//TODO Change sessionAge
	int discardAge = 10;
	/**
	 * 
	 */
	private Date discardTime;
	
	
		
	/**
	 * @param age
	 */
	public SessionData() {
		message= "Hello User";
		refresh(discardAge);
	}
	public SessionData(int age) {
		message= "Hello User";
		discardAge = age;
		refresh(discardAge);
	}
	public SessionData(int version, String message,Date expiry){
		this.message = message;
		discardTime = expiry;
		refresh();
	}
	
	/**
	 * @param age
	 */
	private void refresh(int age){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, discardAge);
		discardTime = cal.getTime();
		
	}
	/**
	 * Refresh the session expiry time
	 */
	public void refresh(){
		refresh(discardAge);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getString();
	}
	public String getString(){
		System.out.println("SessionData.getString() Discardtime :"+discardTime);
		return svn.getString()+"_"+message+"_"+discardTime.toString();
	}
	public void setSessionVersion(SessionVersion svn){
		this.svn = svn;
	}
	/**
	 * @param newMessage
	 */
	public void updateMessage(String newMessage){
		if(newMessage != null && !newMessage.equals(message)){
		message = newMessage;
		svn.updateChangeCount();
		}
	}
	/**
	 * @return
	 */
	public int getVersionNo() {
		return svn.getChangeCount();
	}
	public SessionVersion getSessionVersion(){
		return this.svn;
	}
	/**
	 * @return
	 */
	public Date getDiscardTime(){
		return discardTime;
	}
	/**
	 * @return
	 */
	public String getMessage(){
		return message;
	}
}
