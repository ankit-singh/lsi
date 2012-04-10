package com.ankit.session.rpc;

import com.ankit.session.model.IPP;
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
		boolean primary = MyUtil.getMyIPP().equals(svn.getPrimary()) ;
		boolean backup = MyUtil.getMyIPP().equals(svn.getBackup());
		SessionData sd = null;
		if(primary || backup){
			//Read from local sstbl
			sd = handleLocalSession(sid, svn);

			if(sd != null && svn.getBackup()== null){
				WriteHelper writer = new WriteHelper();
				sd =  writer.storeSessionData(sid, sd);
			}else{
				
			}
		}
		else if(ServerContext.getInstance().getCacheTable().get(sid, svn) != null){
			//if cache exists
			sd = ServerContext.getInstance().getCacheTable().get(sid, svn);
		}
		else{
			RPCResponse response = getFromRemote(svn.getPrimary(), sid, svn.getChangeCount());
			if(response == null && svn.getBackup() != null){
				getFromRemote(svn.getBackup(), sid, svn.getChangeCount());
			}
			if(response != null && response.getSessionData() != null){
				sd = response.getSessionData();
				if(response.getSessionData().getSessionVersion().getBackup() == null){
					ServerContext.getInstance().getSessionStateTable().addSession(sid, response.getSessionData()); 
					WriteHelper writer = new WriteHelper();
					sd = writer.writeSessionData(svn.getPrimary(), sd,sid);
				}else{
					ServerContext.getInstance().getCacheTable().add(sid, response.getSessionData());
				}
				
			}
		}
		if(sd == null){
			throw new SessionNotFoundException();
		}
		else{
			return sd;
		}
	}
	private RPCResponse getFromRemote(IPP destIPP, SessionID sid,int chanegeCount){
		RPCRequest readRequest = new RPCRequest();
		readRequest.setCallID(MyUtil.getCallID());
		readRequest.setSessionID(sid);
		readRequest.setChangeCount(chanegeCount);
		readRequest.setDestIPP(destIPP);
		RPCResponse response = getResponse(readRequest);
		return response;
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
}
