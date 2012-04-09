package com.ankit.session.rpc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

import com.ankit.session.model.IPP;
import com.ankit.session.model.RPCRequest;
import com.ankit.session.model.RPCResponse;
import com.ankit.session.model.SessionID;
import com.ankit.session.model.SessionVersion;
import com.ankit.session.servlet.ServerContext;
import com.ankit.session.servlet.SessionData;
import com.ankit.session.util.MyUtil;

public class RPCServerThread extends Thread {
	DatagramSocket serverSocket;
	public final Log log = LogFactoryImpl.getLog(RPCServerThread.class);
	@Override
	public void run() {
		startServer();
	}
	public void startServer(){
		
		try {
			serverSocket = MyUtil.getSocket();
			log.info(MyUtil.getMyIPAddress());
			log.info(MyUtil.getPort());
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			while(true)
			{ 	
				log.info("RPCServer.main() Running");
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				log.info("============================================Packet Received from :"+IPAddress.getHostAddress());
				log.info("=============================================Packet Recieved from Port:"+port);
				sendData = proccessRequest(receiveData);
				log.info("=============================================Sending Response :"+new String(sendData));
				DatagramPacket sendPacket =	new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			} 
		}catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * @param request
	 * @return
	 */
	private byte[] RequestToResponse(RPCRequest request){
		RPCResponse response = getResponse(request);
		return ResponseToBytes(response, request);
	}
	/**
	 * @param request
	 * @return
	 */
	private RPCResponse getResponse(RPCRequest request){
		log.info("Request call id :"+request.getCallID());
		log.info("Request Opcode :"+request.getOpCode());
		RPCResponse response = null;
		if(request.getOpCode() == RPCRequest.READ){
			SessionData sessionData = ServerContext.getInstance().getSessionStateTable().getSession(request.getSessionID());
			if(sessionData != null){
				response = new RPCResponse(RPCResponse.READ_SUCCESS,sessionData);
			}
		}else if(request.getOpCode() == RPCRequest.WRITE){
			ServerContext.getInstance().getSessionStateTable().addSession(request.getSessionID(), request.getSessionData());
			response = new RPCResponse(RPCResponse.WRITE_SUCCESS);
		}else if(request.getOpCode() == RPCRequest.NOOP){
			response = new RPCResponse(RPCResponse.ALIVE);
			response.setCallID(request.getCallID());
		}else if(request.getOpCode() == RPCRequest.DEL){
			SessionData data = ServerContext.getInstance().getSessionStateTable().getSession(request.getSessionID());
			if(data.getSessionVersion().getChangeCount() <= request.getChangeCount()){
				ServerContext.getInstance().getSessionStateTable().removeSession(request.getSessionID());
			}
		}
		return response;
	}
	/**
	 * @param response
	 * @return
	 */
	private byte[] ResponseToBytes(RPCResponse response,RPCRequest request){
		byte[] retVal = new byte[1024];
		int opCode = request.getOpCode();
		StringBuffer sb = new StringBuffer();
		sb.append(request.getCallID()).append("_");
		sb.append(response.getOpCode());
		if(opCode == RPCRequest.READ){
			sb.append("_");
			sb.append(request.getSessionID().toString());
			sb.append(request.getSessionData().toString());
		}
		retVal = sb.toString().getBytes();
		return retVal;
	}
	/**
	 * @param req
	 * @return
	 */
	private byte[] proccessRequest(byte[] req){
		RPCRequest rpcReq = ByteToRequest(req);
		return RequestToResponse(rpcReq);
	}
	/**
	 * @param req
	 * @return
	 */
	private RPCRequest  ByteToRequest(byte[] req){
		RPCRequest request = new RPCRequest();
		String reqStr = new String(req).trim();
		log.info("Request String : "+reqStr);
		String[] reqArr = reqStr.split("_");
		log.info("0->"+reqArr[0]);
		log.info("1->"+reqArr[1]);
		request.setCallID(Integer.parseInt(reqArr[0]));
		int opCode = Integer.parseInt(reqArr[1]);
		request.setOpCode(opCode);
		if(opCode == RPCRequest.WRITE){
			try {
				IPP newIPP = new IPP(reqArr[3],reqArr[4]);
				request.setSessionID(new SessionID(reqArr[2],newIPP));
				IPP pIPP = new IPP(reqArr[6],reqArr[7]);
				IPP bIPP = new IPP(reqArr[8],reqArr[9]);
				SessionVersion svn = new SessionVersion(Integer.parseInt(reqArr[5]), pIPP, bIPP);
				SessionData data = new SessionData(svn,reqArr[10],reqArr[11]);
				request.setSessionData(data);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}else if(opCode == RPCRequest.READ || opCode == RPCRequest.DEL){
			IPP newIPP = new IPP(reqArr[3],reqArr[4]);
			request.setSessionID(new SessionID(reqArr[2],newIPP));
			request.setChangeCount(Integer.parseInt(reqArr[5]));
		}else if(opCode == RPCRequest.NOOP){
			//TODO
		}
		return request;
	}


}

