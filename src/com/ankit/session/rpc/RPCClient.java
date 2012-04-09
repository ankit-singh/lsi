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

import com.ankit.session.model.RPCRequest;
import com.ankit.session.model.RPCResponse;
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
			clientSocket.setSoTimeout(10000);
			clientSocket.receive(receivePacket);
			log.info("***********************************response received");
			clientSocket.close();
			response = ByteToResponse(receiveData);
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
			sb.append(request.getSessionID().getString()).append("_");
		}
		
		if(opCode == RPCRequest.WRITE){
			log.info("WRRRRRRRRRRRITTTTTTTTTTTTTTTT");
			sb.append(request.getSessionData().toString());
		}else if(opCode == RPCRequest.READ){
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
			if(opCode == RPCResponse.ALIVE){
				response = new RPCResponse(opCode);
				response.setCallID(respCallId);
			}
			else if(opCode == RPCResponse.READ_SUCCESS){
				DateFormat df = DateFormat.getDateInstance();
				try {
					response  = new RPCResponse(opCode,new SessionData(Integer.parseInt(respArr[0]),respArr[1],df.parse(respArr[2])));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else if(opCode == RPCResponse.WRITE_SUCCESS){
				response = new RPCResponse(opCode);
			}
		return response;
	}
}
