package com.ankit.session.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.ankit.session.model.SessionCookie;
import com.ankit.session.rpc.CreateHelper;
import com.ankit.session.rpc.DeleteHelper;
import com.ankit.session.rpc.RPCServerThread;
import com.ankit.session.rpc.ReadHelper;
import com.ankit.session.rpc.UpdateHelper;
import com.ankit.session.util.MyUtil;
import com.ankit.ssm.db.MembershipProbeThread;
import com.ankit.ssm.db.SimpleDBManager;
import com.ankit.ssm.exceptions.SSMException;

/**
 * @author ankitsingh
 *
 */
@SuppressWarnings("serial")
@WebServlet("/1b")
public class AppServer extends HttpServlet{
	/**
	 * The constant cookieName
	 */
	private  final static String cookieName = "CS5300PROJECT1SESSIONAS2536";
	private int sessionAge = 15;
	private int cookieAge =sessionAge;
	private static final Log log = LogFactoryImpl.getLog(AppServer.class);
	
	@Override
	public void init() throws ServletException {
		log.info("Starting Deamon Thread");
		DeamonThread deamonThread = new DeamonThread();
		deamonThread.setDaemon(true);
		startThread(deamonThread);
		log.info("starting The RPC server thread");
		RPCServerThread rpcST = new RPCServerThread();
		startThread(rpcST);
		log.info("Starting the random probe thread");
		MembershipProbeThread mpt = new MembershipProbeThread();
		startThread(mpt);
		super.init();
	}
	private void startThread(Thread t){
		if(!t.isAlive()){
			t.start();
		}
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		Cookie currentCookie = getCookie(request.getCookies());
		ServerContext.getInstance().setCurrentRequest(request);
		ServerContext.getInstance().setCurrentResponse(response);
		if(currentCookie != null){
			SessionCookie sc = new SessionCookie(currentCookie);
			ServerContext.getInstance().setSessionCookie(sc);
		}
		if(currentCookie == null){
			createSession();
		}else{
			if (request.getParameter("cmdRefresh")!=null){
				processRead();
			}
			else if(request.getParameter("cmdLogout")!=null){
				processLogout();
			}
			else if(request.getParameter("cmdReplace") != null){
				processUpdate();
			}else if(request.getParameter("cmdRefMEM") != null){
				try {
					SimpleDBManager.getInstance().refresh();
				} catch (SSMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				processRead();
			}else if(request.getParameter("cmdKILL") != null){
				System.exit(0);
			}	
			else{
				processRead();
			}
		}

		//1. Get the response output stream 
		//2. send the new message and the new expiry time
		response = ServerContext.getInstance().getCurrentResponse();
		PrintWriter out = response.getWriter();
		log.info("AppServer.doGet() HTML"+ServerContext.getInstance().getResponseHTML());
		out.println(ServerContext.getInstance().getResponseHTML());


	}
	private void createSession(){
		log.info("CREATE NEW SESSION");
		CreateHelper creater = new CreateHelper();
		SessionData data =creater.createNewSession();
		buildResponse(data);
	}
	private void processUpdate(){
		log.info("UPDATE SESSION");
		try{
			UpdateHelper updater = new UpdateHelper();
			SessionData data = updater.updateSessionData();
			buildResponse(data);
		}catch (SessionNotFoundException e) {
			//session not found
			//display the expired message
			buildExpiredHTML();
		}
	}
	private void  processLogout(){
		log.info("LOGOUT SESSION");
		DeleteHelper deleteHelper = new DeleteHelper();
		SessionCookie sc= ServerContext.getInstance().getSessionCookie();
		deleteHelper.deletSessionData(sc.getSessionID(), sc.getSessionVersion());
		buildLogoutHTML();
	}
	
	private void processRead(){
		log.info("READ SESSION");
		try{
			ReadHelper reader = new ReadHelper();
			SessionData data = reader.readSessionData();
			buildResponse(data);
		}catch (SessionNotFoundException e) {
			//session not found
			//display the expired message
			buildExpiredHTML();
		}
	}
	private void buildResponse(SessionData sessionData){
		SessionCookie sc = ServerContext.getInstance().getSessionCookie();
		log.info("AppServer.buildResponse() SC :"+sc);
		sc.setSessionVerion(sessionData.getSessionVersion());
		Cookie c = sc.getCookie();
		c.setMaxAge(cookieAge*60);
		ServerContext.getInstance().getCurrentResponse().addCookie(c);
		ServerContext.getInstance().setResponseHTML((MyUtil.getHtmlPage(sc.getSessionID(), sessionData)));

	}
	private void buildExpiredHTML(){
		buildExitHTML("Session Expired", "Opps! Your Session can not be found or has expired");
	}
	private void buildLogoutHTML(){
		buildExitHTML("Session Ended", "You have been logged out");
	}
	private void buildExitHTML(String heading,String message){
		String s = MyUtil.getLogoutPage(heading, message);
		SessionCookie sc = ServerContext.getInstance().getSessionCookie();
		Cookie c = sc.getCookie();
		c.setMaxAge(0);
		ServerContext.getInstance().getCurrentResponse().addCookie(c);
		ServerContext.getInstance().setResponseHTML(s);
	}


	/**
	 * @param cookies
	 * @return
	 */
	private Cookie getCookie(Cookie[] cookies){
		Cookie retVal = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(cookieName)) {
					retVal = cookies[i];
					break;
				}
			}
		}
		log.info("SessionStateServlet.getCookie() Cookie Value : "+retVal);
		return retVal;
	}


}
