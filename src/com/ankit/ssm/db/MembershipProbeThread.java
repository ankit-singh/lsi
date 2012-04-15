package com.ankit.ssm.db;

import com.ankit.ssm.exceptions.SSMException;

public class MembershipProbeThread extends Thread
{
	private static int threadWaitTime = 6000;
	//TODO random waitime
	@Override
	public void run() {
		while(true){
			try {
				SimpleDBManager.getInstance().refresh();
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

