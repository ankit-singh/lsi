package com.ankit.session.servlet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	public static final DateFormat formatter = new SimpleDateFormat("yyyyMMddHmmss");
	
		
	/**
	 * @param age
	 */
	public SessionData(String message) {
		this.message= "Hello User";
		refresh(discardAge);
	}
	public SessionData(SessionVersion svn,String msg,Date date){
		this.message = msg;
		discardTime = date;
		this.svn = svn;
		refresh();
	}
	public SessionData(SessionVersion svn,String msg,String date){
		this.message = msg;
		discardTime = string2Date(date);
		this.svn = svn;
		refresh();
	}
	 private static Date string2Date(String discardTime){
	        Date date = null;
	        try {
	            date = (Date)formatter.parse(discardTime);
	        } catch (ParseException e) {
	           e.printStackTrace();
	        }  
	        return date;
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
		return svn.getString()+"_"+message+"_"+formatter.format(discardTime);
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
