package com.ankit.ssm.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.ankit.session.model.IPP;
import com.ankit.session.model.RPCRequest;
import com.ankit.session.model.RPCResponse;
import com.ankit.session.rpc.RPCClient;
import com.ankit.session.util.MyUtil;
import com.ankit.ssm.exceptions.SSMException;



public class SimpleDBManager {

	private final static Log log = LogFactoryImpl
			.getLog(SimpleDBManager.class);
	public static final String domainName = "CS5300PROJECT1BSDBMbrList";
	public static final String attributeName = "ipps";
	public static final String itemName = "members";
	public static HashSet<String> mbrSet = new HashSet<String>();
	public static SimpleDBManager instance;
	private static IPP myIPP;
	private static String myServerIP;
	static int myPort = -1;
	/**
	 * The Amazon Simple DB object
	 */

	private AmazonSimpleDB simpleDB = null;
	public static SimpleDBManager getInstance(){
		if(instance == null){
			instance = new SimpleDBManager();
			instance.init();
		}
		return instance;
	}
	public void init(){
		if (simpleDB == null) {
			//		log.info("SimpleDBManager.init() "+this.getClass());
			log.info("start");
			FileInputStream inputStream;
			try {
				//				log.info("AWS Credential file : "+this.getClass().getResource("../AwsCredentials.properties")
				//							.getPath());
				log.info("SimpleDBManager.init()" + this.getClass());
				log.info("SimpleDBManager.init() 2"
						+ this.getClass().getResource(
								"/AwsCredentials.properties"));
				String filePath = this.getClass()
						.getResource("/AwsCredentials.properties").getPath();
				log.info("SimpleDBManager.init() 3");
				inputStream = new FileInputStream(new File(filePath));
				log.info("Opening simple db");
				simpleDB = new AmazonSimpleDBClient(new PropertiesCredentials(
						inputStream));

				if (myServerIP == null) {
					try {
						myServerIP = InetAddress.getLocalHost().getHostAddress();
					} catch (UnknownHostException e) {
						System.out.println("MembershipManager.init() Unknown Host Exception");
						e.printStackTrace();
					}
				}
				if (myPort <= 0) {
					myPort = MyUtil.getPort();
				}
				myIPP = new IPP(myServerIP, myPort);
				try {
					createDomain();
					refresh();
				} catch (SSMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (FileNotFoundException e1) {
				log.error("File not found");
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				log.error("io exception");
			}
			log.info("end");
		}
	}
	private void createDomain(){
		try {
			simpleDB.createDomain(new CreateDomainRequest(
					SimpleDBManager.domainName));
			if (getSMbrSet().equals("")) {
				List<ReplaceableAttribute> att = new ArrayList<ReplaceableAttribute>();
				att.add(new ReplaceableAttribute(SimpleDBManager.attributeName, "", true));
				simpleDB.putAttributes(new PutAttributesRequest(SimpleDBManager.domainName, SimpleDBManager.itemName, att));
			}
		} catch (AmazonClientException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			init();
			createDomain();
		}
	}
	public String getSMbrSet(){
		try {
			String selectIPPs = "SELECT `" + SimpleDBManager.attributeName
					+ "` FROM `" + SimpleDBManager.domainName + "`";
			return simpleDB.select(new SelectRequest(selectIPPs))
					.getItems().get(0).getAttributes().get(0).getValue();
		} catch (Exception e) {
			return "";
		}
	}


	public void storeMemberList(String newMemberString,String oldMemberString){
		List<ReplaceableAttribute> newMbrList = new ArrayList<ReplaceableAttribute>();
		newMbrList.add(new ReplaceableAttribute(
				SimpleDBManager.attributeName, newMemberString, true));

		simpleDB.putAttributes(new PutAttributesRequest(
				SimpleDBManager.domainName, SimpleDBManager.itemName,
				newMbrList));
	}
	public void refresh() throws SSMException{
		init();
		log.info("START REFRESH");
		log.info("Clean Member Set");
		mbrSet.clear();
		log.info("Get SMbrSet from Simple DB");
		String sMbrList = getSMbrSet();
		log.info("sMbrList : "+sMbrList);
		ConcurrentSkipListSet<String>	sMbrListSet;
		log.info("Empty : "+sMbrList.trim().equals(""));
		if (sMbrList != null && !sMbrList.trim().equals("")) { 
			sMbrListSet = new ConcurrentSkipListSet<String>(Arrays.asList(sMbrList
					.split("--")));
		}
		else { 
			sMbrListSet = new ConcurrentSkipListSet<String>();
			sMbrListSet.add(myIPP.getString());
		}
		log.info("My IPP ; "+myIPP);
		log.info("Sending NOOP to all memsbers");
		Iterator<String> iterator = sMbrListSet.iterator();
		String memberString = "";
		while (iterator.hasNext()) {
			String ippStr = iterator.next();
			IPP mIPP = null;
			try {
				log.info("IPP String : "+ippStr);
				mIPP = new IPP(ippStr);
			} catch (NumberFormatException e) {
				continue;
			}
			if (!mIPP.equals(myIPP)) {
				log.info("Sending Probe Request");
				int noOfTries = 2;
				RPCRequest probeRequest = new RPCRequest(mIPP,RPCRequest.NOOP);
				probeRequest.setCallID(MyUtil.getCallID());
				RPCClient rpcClient = new RPCClient();
				while(noOfTries > 0){
					log.info("Sending to :"+mIPP);
					log.info("Try :"+noOfTries);
					RPCResponse probeResponse = rpcClient.sendRequest(probeRequest);
					noOfTries--;
					if (probeResponse !=null) {
						if (probeResponse.getCallID() == probeRequest.getCallID()
								&& probeResponse.getOpCode() == RPCResponse.ALIVE) {
							mbrSet.add(mIPP.getString());
							memberString += mIPP.getString();
							memberString +="--";
							break;
						}
					}
				}
			}
		}
		log.info("Adding Self to memebr list");
		memberString +=myIPP.toString();
		mbrSet.add(myIPP.toString());
		storeMemberList(memberString, sMbrList);
	} 
	@SuppressWarnings("unchecked")
	public HashSet<String> getMemberSet(){
		return (HashSet<String>) mbrSet.clone();
	}

}
