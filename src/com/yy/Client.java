package com.yy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Message.Message;
import Message.MsgType;

public class Client {

	private String serverLocation ="127.0.0.1";
	private int heartPort = 7000;
	private Socket sendHeartSocket;
	public Client()
	{
		try {
			sendHeartSocket = new Socket(serverLocation,heartPort);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new SendHeartThread().start();
	}
	class SendHeartThread extends Thread
	{
		private boolean flag = true;
		public void run()
		{
			while(flag)
			{
				try{
					ObjectOutputStream out = new ObjectOutputStream(sendHeartSocket.getOutputStream());
					out.writeObject(new Message(MsgType.WSDLSET,"heart"));
					out.flush();
					
					ObjectInputStream in = new ObjectInputStream(sendHeartSocket.getInputStream());
					Message msg = (Message) in.readObject();
					if(msg != null)
					{
						if(msg.getType() == MsgType.WSDLSET)
						{
							System.out.println("server:"+msg.getBody().toString());
						}
						else
						{
							flag = false;
							System.out.println("local:can't get the matched msg from server");
						}
					}
					else
					{
						flag = false;
						System.out.println("local:fail read object");
					}
					Thread.sleep(15000);
				}
				catch(Exception e)
				{
					flag = false;
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args)
	{
		new Client();
	}
}
