package com.ankit.session.model;

import com.ankit.session.servlet.SessionData;

public class RPCResponse extends IMessageCodes {
	
	SessionData data;
	int opCode;
	SessionID sid;
	public RPCResponse(int opCode){
		
		this.opCode = opCode;
	}
	public RPCResponse(int type, SessionData data){
		this.data = data;
		this.opCode = type;
	}
	public SessionData getSessionData(){
		return data;
	}
	public int getOpCode() {
		return opCode;
	}
	public void setCallID(int respCallId) {
		callID = respCallId;
		
	}
	public int getCallID() {
		return callID;
	}
	private int callID;

	public void setSessionID(SessionID sessionID) {
		this.sid = sessionID;
	}
	public void setSessionData(SessionData data2) {
		// TODO Auto-generated method stub
		this.data = data2;
	}
}
