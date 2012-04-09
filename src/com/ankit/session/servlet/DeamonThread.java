package com.ankit.session.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.ankit.session.model.SessionStateTable;

public class DeamonThread extends Thread{
	public final Log log = LogFactoryImpl.getLog(DeamonThread.class);
	@Override
	public void run() {
	log.info("Entering run method");

		try {
			log.info("In run Method: currentThread() is"
					+ Thread.currentThread());
			SessionStateTable sstbl = ServerContext.getInstance().getSessionStateTable();
			while (true) {
				try {
					Thread.sleep(1000*30);
				} catch (InterruptedException x) {
					x.printStackTrace();
				}
				sstbl.cleanUp();
				log.info("In run method: woke up again");
			}
		} finally {
			log.info("Leaving run Method");
		}
	}
}
