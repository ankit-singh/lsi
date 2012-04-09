package com.ankit.session.rpc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.ankit.session.model.IPP;
import com.ankit.session.model.RPCRequest;
import com.ankit.session.model.RPCResponse;
import com.ankit.session.model.SessionCookie;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.ServerContext;
import com.ankit.session.servlet.SessionData;
import com.ankit.session.util.MyUtil;
import com.ankit.ssm.db.SimpleDBManager;

public class WriteHelper {
	private static final Log log = LogFactoryImpl.getLog(WriteHelper.class);


	public SessionData storeSessionData(SessionID sid,SessionData sessionData){

		IPP myIPP = MyUtil.getMyIPP();
		sessionData.getSessionVersion().setPrimary(myIPP);
		sessionData = storeBackUp(sessionData, sid);
		ServerContext.getInstance().getSessionStateTable().addSession(sid, sessionData);
		ServerContext.getInstance().setSessionCookie(new SessionCookie(sid,sessionData.getSessionVersion()));
		
		return sessionData;

	}

	public SessionData writeSessionData(IPP ipp, SessionData sessionData,SessionID sid){
		if(ipp != null){
			RPCRequest writeRequest = new RPCRequest(ipp,RPCRequest.WRITE);
			writeRequest.setSessionData(sessionData);
			writeRequest.setSessionID(sid);
			writeRequest.setCallID(MyUtil.getCallID());
			sessionData.getSessionVersion().setBackup(ipp);
			RPCResponse writeResponse = getResponse(writeRequest);
			if(writeResponse == null){
				sessionData.getSessionVersion().setBackup(null);
			}
		}
		return sessionData;
	}
	private  RPCResponse getResponse(RPCRequest writeRequest){
		RPCResponse writeResponse = null;
		RPCClient rpcClient = new RPCClient();
		writeResponse = rpcClient.sendRequest(writeRequest);
		if (writeResponse !=null) {
			if (writeResponse.getCallID() == writeRequest.getCallID()
					&& writeResponse.getOpCode() == RPCResponse.WRITE_SUCCESS) {
				return writeResponse;
			}
		}
		return null;
}
private SessionData storeBackUp(SessionData data, SessionID sid){
	ArrayList<String> arr = new ArrayList<String>(SimpleDBManager.getInstance().getMemberSet());
	data.getSessionVersion().setBackup(null);
	log.info("MEMBER LIST SIZE :"+arr.size());
	for(String s: arr){
		log.info("SENDING WRITE REQUEST :"+s);
		IPP desIpp = new IPP(s);
		if (!desIpp.equals(MyUtil.getMyIPP())) {
			data = writeSessionData(desIpp, data, sid);
			if (data.getSessionVersion().getBackup() != null) {
				log.info("WRITE SUCCESS");
				break;
			}
		}
	}
	return data;
}
}
