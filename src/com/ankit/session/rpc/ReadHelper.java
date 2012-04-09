package com.ankit.session.rpc;

import com.ankit.session.model.RPCRequest;
import com.ankit.session.model.RPCResponse;
import com.ankit.session.model.SessionCookie;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionStateTable;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.ServerContext;
import com.ankit.session.servlet.SessionData;
import com.ankit.session.servlet.SessionNotFoundException;
import com.ankit.session.util.MyUtil;

public class ReadHelper {


	public SessionData readSessionData() throws SessionNotFoundException{
		SessionCookie cookie = ServerContext.getInstance().getSessionCookie();
		SessionVersion svn = cookie.getSessionVersion();
		SessionID sid = cookie.getSessionID();
		ServerContext context = ServerContext.getInstance();
		boolean primary = MyUtil.getMyIPP().equals(svn.getPrimary()) ;
		boolean backup = MyUtil.getMyIPP().equals(svn.getBackup());
		SessionData sd = null;
		if(primary || backup){
			//Read from local sstbl
			sd = handleLocalSession(sid, svn);

			if(sd != null && svn.getBackup()== null){
				WriteHelper writer = new WriteHelper();
				sd =  writer.storeSessionData(sid, sd);
			}
		}
		//FIXME
//		else if(context.getCacheTable().get(sid, svn) != null){
//			//if cache exists
//			sd = context.getCacheTable().get(sid, svn);
//		}
		else{
			RPCResponse response = handleRemoteSession(sid, svn);

			if(response != null && response.getSessionData() != null){
				//FIXME
				if(response.getSessionData().getSessionVersion().getBackup() == null){
					ServerContext.getInstance().getSessionStateTable().addSession(sid, response.getSessionData()); 
				}else{
//					context.getCacheTable().add(sid, response.getSessionData());
				}
				sd = response.getSessionData();
			}
		}
		if(sd == null){
			throw new SessionNotFoundException();
		}
		else{
			return sd;
		}
	}
	private SessionData  handleLocalSession(SessionID sid,SessionVersion svn){
		SessionStateTable sstbl = ServerContext.getInstance().getSessionStateTable();
		SessionData sessionData = sstbl.getSession(sid);
		return sessionData;
	}
	private RPCResponse getResponse(RPCRequest readRequest){
		RPCResponse readResponse = null;
		RPCClient rpcClient = new RPCClient();
		readResponse = rpcClient.sendRequest(readRequest);
		if (readResponse !=null) {
			if (readResponse.getCallID() == readRequest.getCallID()
					&& readResponse.getOpCode() == RPCResponse.READ_SUCCESS) {
				return readResponse;
			}
		}
		return null;
	}
	private RPCResponse handleRemoteSession(SessionID sid,SessionVersion svn){
		RPCRequest readRequest = new RPCRequest();
		readRequest.setCallID(MyUtil.getCallID());
		readRequest.setSessionID(sid);
		readRequest.setChangeCount(svn.getChangeCount());
		readRequest.setDestIPP(svn.getPrimary());
		RPCResponse response = getResponse(readRequest);
		if(response == null && svn.getBackup() != null){
			readRequest.setCallID(MyUtil.getCallID());
			readRequest.setDestIPP(svn.getBackup());
			response = getResponse(readRequest);
		}
		return response;
	}
}
