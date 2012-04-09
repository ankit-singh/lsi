package com.ankit.session.model;


public class SessionID {
	private String sessionNumber;
	private IPP serverID;
	public String getSessionNumber() {
		return sessionNumber;
	}
	public IPP getServerID() {
		return serverID;
	}
	public SessionID(String sessionNum, IPP serverID){
		this.sessionNumber = sessionNum;
		this.serverID = serverID;
	}
	public String getSessionNum(){
		return sessionNumber;
	}
	public String getString(){
		StringBuffer sb = new StringBuffer(sessionNumber).append("_");
		sb.append(serverID.getString());
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getString();
	} //
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serverID == null) ? 0 : serverID.hashCode());
		result = prime * result
				+ ((sessionNumber == null) ? 0 : sessionNumber.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SessionID)) {
			return false;
		}
		SessionID other = (SessionID) obj;
		if (serverID == null) {
			if (other.serverID != null) {
				return false;
			}
		} else if (!serverID.equals(other.serverID)) {
			return false;
		}
		if (sessionNumber == null) {
			if (other.sessionNumber != null) {
				return false;
			}
		} else if (!sessionNumber.equals(other.sessionNumber)) {
			return false;
		}
		return true;
	}
}
