package com.ankit.session.rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.ankit.session.model.IPP;
import com.ankit.session.model.RPCRequest;
import com.ankit.session.model.RPCResponse;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.SessionData;

public class RPCClient   {
	private int currentCallID =-1; 
	DatagramSocket clientSocket ;
	private static final Log log = LogFactoryImpl.getLog(RPCClient.class);
	public  RPCResponse sendRequest(RPCRequest request) {
		RPCResponse response = null;
		try {
			log.info("*************************************START");
			clientSocket = new DatagramSocket();
			currentCallID = request.getCallID();
			InetAddress serverIP = InetAddress.getByName(request.getIPP().getServerIP());
			byte[] sendData = RequestToByte(request);
			log.info("**************************RPCClient.sendRequest() Server :"+request.getIPP().getServerIP());
			log.info("************************RPCClient.sendRequest() Port : "+request.getIPP().getUdpPortID());
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIP, request.getIPP().getUdpPortID());
			log.info("************************Client Socket Port :"+sendPacket.getPort());
			clientSocket.send(sendPacket);
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.setSoTimeout(5000);
			clientSocket.receive(receivePacket);
			log.info("***********************************response received");
			response = ByteToResponse(receiveData);
			clientSocket.close();
			if(response != null){
				log.info("***************Response to client Call ID :"+response.getCallID());
				log.info("***************Response to client Opcode : "+response.getCallID());
			}
		}
		catch (SocketException e) {
			log.info("****************RPCClient.sendRequest() Socket Exception");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			log.info("*****************RPCClient.sendRequest() UnknownHost Exception");
			e.printStackTrace();
		} catch (IOException e) {
			log.info("******************RPCClient.sendRequest() IOException");
			e.printStackTrace();
		}
		return response;

	}
	
	private byte[] RequestToByte(RPCRequest request){
		byte[] retVal = new byte[1024];
		int opCode = request.getOpCode();
		StringBuffer sb = new StringBuffer();
		sb.append(currentCallID).append("_");
		sb.append(opCode);
		if(opCode != RPCRequest.NOOP){
			sb.append("_");
			sb.append(request.getSessionID().getString());
		}
		
		if(opCode == RPCRequest.WRITE){
			log.info("WRRRRRRRRRRRITTTTTTTTTTTTTTTT");
			sb.append("_");
			sb.append(request.getSessionData().toString());
		}else if(opCode == RPCRequest.READ || opCode == RPCRequest.DEL){
			sb.append("_");
			sb.append(request.getChangeCount());
		}
		retVal = sb.toString().getBytes();
		log.info("sending string :"+sb.toString());
		return retVal;
	}
	private RPCResponse ByteToResponse(byte[] resp){
		
		RPCResponse response = null;
		String respStr = new String(resp).trim();
		log.info("**************************Response String ; "+respStr);
		String[] respArr = respStr.split("_");
		int respCallId =Integer.parseInt(respArr[0]);
		
			int opCode = Integer.parseInt(respArr[1]);
			response = new RPCResponse(opCode);
			response.setCallID(respCallId);
			 if(opCode == RPCResponse.READ_SUCCESS){
				DateFormat df = DateFormat.getDateInstance();
				try {
					IPP newIPP = new IPP(respArr[3],respArr[4]);
					response.setSessionID(new SessionID(respArr[2],newIPP));
					IPP pIPP = new IPP(respArr[6],respArr[7]);
					IPP bIPP = new IPP(respArr[8],respArr[9]);
					SessionVersion svn = new SessionVersion(Integer.parseInt(respArr[5]), pIPP, bIPP);
					SessionData data = new SessionData(svn,respArr[10],respArr[11]);
					response.setSessionData(data);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			 }
		return response;
	}
}
