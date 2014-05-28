package com.bikedefend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class Client {
	
private String serverMessage;
private String receivedMessage;
public static String SERVERIP = "92.63.68.3";
public static int SERVERPORT = 12745;

PrintWriter out;
BufferedReader in;

/**
 * Sends the message entered by client to the server
 * @param message text entered by client
 * @throws IOException 
 */
public String sendMessage(String message, String serverIp, int port) throws IOException{
	if(serverIp != null){
		if(!serverIp.equals("")){
			SERVERIP = serverIp;
		}
	}
	if(port != 0){
		SERVERPORT = port;
	}
	Log.i("TCP client", "Message "+message+" IP "+SERVERIP+" port "+SERVERPORT);
    InetAddress serverAddr = InetAddress.getByName(SERVERIP);
    Log.i("TCP client", "Connecting...");
    
    Socket socket = new Socket(serverAddr, SERVERPORT);
    socket.setSoTimeout(30000);
    if(socket.isConnected()){
    	Log.i("TCP client", "Connected");
    
    	try {
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        socket.setSoTimeout(30000);
        while((serverMessage = in.readLine()) != null){
        if ((serverMessage != null)&&(serverMessage.equals("HELLO"))) {
        	receivedMessage = serverMessage;
            Log.i("TCP client", "Server says hello");
            
            Log.i("TCP Client", "Sending...");
            if (out != null && !out.checkError()) {
            	out.println(message);
            	Log.i("TCP Client", "Sent");
            	out.flush();
            }
            socket.setSoTimeout(30000);
            while((serverMessage = in.readLine()) != null){
            	if(serverMessage.equals("BYE")){
            		break;
            	}
            	if(!serverMessage.equals("")){
            		receivedMessage = serverMessage;
            		Log.i("TCP client", "Received Message "+ receivedMessage);
            	}
            }
        }
        }
        Log.i("TCP client", "Server says nothing");
       
	    } catch (Exception e) {
	        Log.e("TCP Client", "Error", e);
	    } finally {
	    	Log.i("TCP Client", "Closing connecting...");
	        socket.close();
	    }
	return receivedMessage;
    }
    else{
    	return "Connection failed";
    }
}

/*
public void run() {
    mRun = true;
    try {
        InetAddress serverAddr = InetAddress.getByName(SERVERIP);
        Log.e("TCP client", "Connecting...");
        Socket socket = new Socket(serverAddr, SERVERPORT);
        try {
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (mRun) {
                serverMessage = in.readLine();
                if (serverMessage != null && mMessageListener != null) {
                    mMessageListener.messageReceived(serverMessage);
                    Log.i("TCP client", "Received Message");
                }
                serverMessage = null;
            }
        } catch (Exception e) {
            Log.e("TCP", "Error", e);
        } finally {
            socket.close();
        }

    } catch (Exception e) {
        Log.e("TCP", "Error", e);
    }
}*/
}