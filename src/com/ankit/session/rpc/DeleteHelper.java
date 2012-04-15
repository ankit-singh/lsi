package com.ankit.session.rpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ankit.session.model.IPP;
import com.ankit.session.model.RPCRequest;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.ServerContext;
import com.ankit.session.util.MyUtil;

public class DeleteHelper {
	private static final Log log = LogFactory.getLog(DeleteHelper.class);
	
	public void forceRemoteDelete(SessionID sid, IPP destIPP, int changeCount){
		if (destIPP != null) {
			log.info("start");
			log.info("SessionID : " + sid);
			log.info("Destination IPP : " + destIPP);
			log.info("Change count :" + changeCount);
			int callId = MyUtil.getCallID();
			log.info("Call ID :" + callId);
			RPCRequest deleteRequest = new RPCRequest(destIPP, RPCRequest.DEL);
			deleteRequest.setCallID(callId);
			deleteRequest.setSessionID(sid);
			deleteRequest.setChangeCount(changeCount);
			RPCClient rpcClient = new RPCClient();
			rpcClient.sendRequest(deleteRequest);
			log.info("end");
		}
	}
	public void deletSessionData(SessionID sid, SessionVersion svn){
	
		deleteSessionData(sid, svn.getPrimary(), svn.getChangeCount());
		deleteSessionData(sid, svn.getBackup(), svn.getChangeCount());
	}
	public void deleteSessionData(SessionID sid, IPP destIPP, int changeCount){
		log.info("start");
		log.info("SessionID : "+sid );
		log.info("Destination IPP : "+destIPP);
		log.info("Change count :"+changeCount);
		if(destIPP != null && destIPP.equals(MyUtil.getMyIPP())){
			ServerContext.getInstance().getSessionStateTable().removeSession(sid);
		}else{
			forceRemoteDelete(sid, destIPP, changeCount);
			
		}
		log.info("end");
	}
}
