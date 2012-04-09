package com.ankit.session.model;

public class IPP {

	private int udpPortID ;
	private String serverIP;
	
	
	public IPP(String serverIP, int udpPortID){
		this.serverIP = serverIP;
		this.udpPortID = udpPortID;
	}
	public IPP(String serverIP,String udpPortID){
		this.serverIP = serverIP;
		this.udpPortID = Integer.parseInt(udpPortID);
		
	}
	public IPP(){
		
	}
	public IPP(String ippStr) {
		String[] strArr = ippStr.split("_");
		this.serverIP = strArr[0];
		this.udpPortID = Integer.parseInt(strArr[1]);
	}
	public String getServerIP() {
		return serverIP;
	}
	
	public int getUdpPortID() {
		return udpPortID;
	}
	public String getString(){
		return serverIP+"_"+udpPortID;
	}
//	@Override
//	public boolean equals(Object obj) {
//		IPP newIPP = (IPP) obj;
//		return (this.serverIP.equals(newIPP.getServerIP())) &&
//				(this.udpPortID == newIPP.getUdpPortID());
//	}
	public void setServerIP(String value) {
		serverIP = value;
		
	}
	public void setPort(int parseInt) {
		udpPortID = parseInt;
	}
	@Override
	public String toString() {
		return getString();
	}
//	@Override
//	public int hashCode() {
//		return this.toString().hashCode();
//	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serverIP == null) ? 0 : serverIP.hashCode());
		result = prime * result + udpPortID;
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
		if (!(obj instanceof IPP)) {
			return false;
		}
		IPP other = (IPP) obj;
		if (serverIP == null) {
			if (other.serverIP != null) {
				return false;
			}
		} else if (!serverIP.equals(other.serverIP)) {
			return false;
		}
		if (udpPortID != other.udpPortID) {
			return false;
		}
		return true;
	}
}
