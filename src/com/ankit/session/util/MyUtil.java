package com.ankit.session.util;

import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.ankit.session.model.IPP;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.SessionData;
import com.ankit.ssm.db.SimpleDBManager;

public class MyUtil {
	private static DatagramSocket dgSocket = null;
	private static int port = -1;
	private static int callID = -1;
	/**
	 * @return
	 */
	public static  String getLogoutPage(String heading, String Message){
		HtmlCreater logOutPage = new HtmlCreater(heading);
		logOutPage.addHeading(Message, 2);
		return logOutPage.getHtml();
	}
	public static IPP getMyIPP(){
		return new IPP(getMyIPAddress(),getPort());
	}
	/**
	 * @param newMessage
	 * @param expiryTime
	 * @return
	 */
	public static String getHtmlPage(SessionID sid, SessionData sd){
		HtmlCreater htmlPage = new HtmlCreater("Session Management");
		htmlPage.addHeading(sd.getMessage(), 3);
		htmlPage.startForm("GET", "1b");
		htmlPage.addInputText("newMessage", 40, 512);
		htmlPage.addSubmitButton("cmdReplace","replace");
		htmlPage.endForm();
		htmlPage.startForm("GET", "1b");
		htmlPage.addSubmitButton("cmdRefresh","refresh");
		htmlPage.addSubmitButton("cmdLogout", "logout");
		htmlPage.addSubmitButton("cmdRefMEM","refreshMemberList");
		htmlPage.addSubmitButton("cmdKILL", "Kill Server");
		htmlPage.endForm();
		htmlPage.addText("<br> Session Created/Refreshed Time : "+MyUtil.getCurrentTimestamp()+"</br>");
		htmlPage.addText("<br>Session Expiry Time : "+sd.getDiscardTime().toString()+"</br>");
		//
		htmlPage.addText(htmlLine("==========================================="));
		htmlPage.addText(htmlLine("Session ID "+sid.getString()));
		htmlPage.addText(htmlLine("SessionData Version : "+sd.getSessionVersion().getChangeCount()));
		htmlPage.addText(htmlLine("MY IPP : "+getMyIPP().getString()));
		htmlPage.addText(htmlLine("Primary IPP : "+sd.getSessionVersion().getPrimary().getString()));
		if(sd.getSessionVersion().getBackup() != null){
		htmlPage.addText(htmlLine("Backup IPP :"+sd.getSessionVersion().getBackup().getString()));
		}else{
			htmlPage.addText(htmlLine("Backup IPP : NULL"));
		}
		ArrayList<String> arr = new ArrayList<String>(SimpleDBManager.getInstance().getMemberSet());
		Iterator<String> iterator  = arr.iterator();
		String s = new String();
		while(iterator.hasNext()){
			s +='\n';
			s +=iterator.next();

		}
		htmlPage.addText(htmlLine("Member List: "+s));
		return htmlPage.getHtml();
	}
	private static String htmlLine(String s){
		return "<br>"+s+"</br>";
	}
	/**
	 * @return
	 */
	public static  String getCurrentTimestamp() {
		return formatedTime(Calendar.getInstance().getTime());
	}
	/**
	 * @return
	 */
	private static int ssno = 0;
	public static String generateSessionNum() {
		System.out.println("MyUtil.generateSessionId()");
		String uidServer = new java.rmi.server.UID().toString(); // guaranteed unique
		try {
			return URLEncoder.encode(uidServer,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return uidServer;
		} // encode any special chars
		
	}
	/**
	 * @param time
	 * @return
	 */
	public static String formatedTime(Date time){
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM, yyyy hh:mm:ss a z");
		return sdf.format(time);
	}
	/**
	 * @param value
	 * @return
	 */
	public static String extractSessionId(String value)
	{	String[] strArr = value.split("_");
	return strArr[0];

	}
	public static String generateCookieValue(SessionID sid, SessionVersion svn){
		StringBuffer sb = new StringBuffer();
		//sessionNumber_serverIP_udpPort_changeCount_primaryIP_primaryPort_backupIP_backupPort
		sb.append(sid.getSessionNumber()).append("_");
		sb.append(sid.getServerID().getServerIP()).append("_");
		sb.append(sid.getServerID().getUdpPortID()).append("_");
		sb.append(svn.getChangeCount()).append("_");
		sb.append(svn.getPrimary().getServerIP()).append("_");
		sb.append(svn.getPrimary().getUdpPortID()).append("_");
		sb.append(svn.getBackup().getServerIP()).append("_");
		sb.append(svn.getBackup().getUdpPortID()).append("_");
		return sb.toString();
		
	}
	public static String getMyIPAddress(){
		String ipadd = null;
		try {
		    InetAddress addr = InetAddress.getLocalHost();
		    ipadd = addr.getHostAddress();
		} catch (UnknownHostException e) {
		}
		return ipadd;
	}
	public static DatagramSocket getSocket(){
		if(dgSocket == null){
			try {
				dgSocket = new DatagramSocket();
				port = dgSocket.getLocalPort();
			} catch (SocketException e) {
				System.out.println("MyUtil.getSocket() Socket Creation Failed");
				e.printStackTrace();
			}
		}
		return dgSocket;
	}
	public static int getPort(){
		if(port == -1){
			getSocket().getLocalPort();
		}
		return port;
	}
	public static int getCallID(){
		if(callID == -1){
			callID = 1000*getPort();
			
		}
		return ++callID;
	}
}
