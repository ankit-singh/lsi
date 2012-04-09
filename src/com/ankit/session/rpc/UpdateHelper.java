package com.ankit.session.rpc;

import com.ankit.session.model.SessionCookie;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.ServerContext;
import com.ankit.session.servlet.SessionData;
import com.ankit.session.servlet.SessionNotFoundException;
import com.ankit.session.util.MyUtil;

public class UpdateHelper {

	public SessionData updateSessionData() throws SessionNotFoundException{
		SessionCookie cookie = ServerContext.getInstance().getSessionCookie();
		SessionID sid = cookie.getSessionID();
		ReadHelper reader = new ReadHelper();
		SessionData sessionData = reader.readSessionData();
		String message = ServerContext.getInstance().getCurrentRequest().getParameter("newMessage");
//
		garbageCollection(sid, sessionData.getSessionVersion());
		//
		sessionData.updateMessage(message);
		WriteHelper writer = new WriteHelper();
		sessionData = writer.storeSessionData(sid, sessionData);
		return sessionData;
	}
	private void garbageCollection(SessionID sid, SessionVersion svn){
		DeleteHelper deleter = new DeleteHelper();
		if(!svn.getPrimary().equals(MyUtil.getMyIPP())){
			deleter.forceRemoteDelete(sid, svn.getPrimary(),svn.getChangeCount());
		}else if(svn.getBackup() != null && !svn.getBackup().equals(MyUtil.getMyIPP())){
			deleter.forceRemoteDelete(sid, svn.getBackup(),svn.getChangeCount());
		}
	}
}
