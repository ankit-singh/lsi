package com.ankit.session.model;

import com.ankit.session.servlet.SessionData;

public class RPCRequest extends IMessageCodes{
	IPP ipp;
	int opCode;
	Object data;
	int callId;
	SessionID sessionID;
	SessionData sessionData;
//	private SessionVersion svn;
	
	public RPCRequest(IPP ipp, int opCode){
		this.ipp = ipp;
		this.opCode = opCode;
	}
	public RPCRequest(){
		
	}
	public void setCallID(int id){
		this.callId = id;
	}
	public IPP getIPP(){
		return ipp;
	}
	public int getOpCode(){
		return opCode;
	}
	public SessionID getSessionID(){
		return sessionID;
	}
	public SessionData getSessionData(){
		return sessionData;
	}
	public void setDestIPP(IPP destIPP){
		this.ipp = destIPP;
	}
	public void setOpCode(int parseInt) {
		this.opCode = parseInt;
	}
	public void setSessionData(SessionData sessionData) {
		this.sessionData = sessionData;
	}
	public void setSessionID(SessionID sessionID) {
		this.sessionID = sessionID;
	}
	public int getCallID() {
		return this.callId;
	}
	public void setChangeCount(int parseInt) {
		this.changeCount = parseInt;
	}
	private int changeCount;
	
	public int getChangeCount(){
		return this.changeCount;
	}
}
