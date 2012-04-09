package com.ankit.ssm.db;

import com.ankit.ssm.exceptions.SSMException;

public class MembershipProbeThread extends Thread
{
	private static int threadWaitTime = 10000;
	private MembershipManager probeManager;
	public MembershipProbeThread() {
		probeManager = new MembershipManager();
	} 
	
	
	@Override
	public void run() {
		while(true){
			try {
				probeManager.refresh();
				sleep(threadWaitTime);
			} catch (SSMException e) {
				System.out.println("MembershipProbeThread.run() Scan Failed");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("MembershipProbeThread.run() Scan Interuppted");
				e.printStackTrace();
			}
			
		}

	}
}

