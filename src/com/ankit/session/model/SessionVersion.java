package com.ankit.session.model;

public class SessionVersion {
	int changeCount = 0;
	IPP primary, backup;
	public SessionVersion(int changeCount,IPP primary,IPP backup){
		this.changeCount = changeCount;
		this.primary = primary;
		this.backup = backup;
	}
	public void updateChangeCount(){
		changeCount++;
	}
	public int getChangeCount() {
		return changeCount;
	}
	public IPP getPrimary() {
		return primary;
	}
	public IPP getBackup() {
		return backup;
	}
	public String getString(){
		return changeCount+"_"+primary.toString()+"_"+backup.toString();
	}
	public void setPrimary(IPP myIPP) {
		this.primary = myIPP;
		
	}
	@Override
	public boolean equals(Object obj) {
		SessionVersion newSvn = (SessionVersion) obj;
		return (this.changeCount == newSvn.changeCount)
				&&(this.primary.equals(newSvn.primary))
				&&(this.backup.equals(newSvn.getBackup()));
	}
	public void setBackup(IPP ipp) {
		this.backup = ipp;
		
	}
	
	
}
