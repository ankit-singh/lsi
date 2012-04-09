package com.ankit.session.rpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.ankit.session.model.IPP;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.SessionData;
import com.ankit.session.util.MyUtil;

public class CreateHelper {
	private static final Log log = LogFactoryImpl.getLog(CreateHelper.class);
	public SessionData createNewSession() {
		IPP myIPP = MyUtil.getMyIPP();
		String sessionNum  = MyUtil.generateSessionNum();
		log.info("Session Num : "+sessionNum);
		log.info("My IPP : "+myIPP.toString());
		SessionID sid  = new SessionID(sessionNum, myIPP);
		SessionData sessionData = new SessionData("Hello User");
		sessionData.setSessionVersion(new SessionVersion(0,myIPP,null));
		logSessionVersion("Before Write", sessionData.getSessionVersion());
		WriteHelper writer = new WriteHelper();
		sessionData =writer.storeSessionData(sid, sessionData);
		logSessionVersion("After Write", sessionData.getSessionVersion());

		log.info("End");
		return sessionData;

	}
	private void logSessionVersion(String msg,SessionVersion svn){
		log.info(msg);
		log.info("Change Count : "+svn.getChangeCount());
		if(svn.getPrimary() != null){
			log.info("Primay IPP : "+svn.getPrimary().toString());
		}
		else{
			log.info("Primary NULL");
		}
		if(svn.getBackup() != null){
			log.info("Backup IPP : "+svn.getBackup().toString());
		}
		else{
			log.info("Backup NULL");
		}
	}

}
