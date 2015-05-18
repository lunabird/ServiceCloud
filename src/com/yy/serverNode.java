package com.yy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.FileNotFoundException;  
import java.io.PrintStream;  

import Message.Message;
import Message.MsgType;
public class serverNode {
	//Ĭ�Ϸ���Ĵ��λ��
	private String savedPath = "E:\\";
	
	//����ڵ�����˿�
	 private static final int serverNodeListenPort = 7001;
	
    
    /** ����ڵ�Ự�����߳�*/ 
    class RequestThread extends Thread
    {
    	Socket socket;
    }
    
    
    class ListenThread extends Thread{
	   
	    private Socket socket;
	    private ServerSocket serverSocket;
	    
	    public ListenThread()  throws IOException {
	        serverSocket = new ServerSocket(serverNodeListenPort);
	        start();
	    }
	    public void run()
	    {
	    	System.out.println("����ڵ��������ʼ�����˿�7001...");
			javax.swing.JOptionPane.showMessageDialog(null,"����ڵ㿪ʼ������");
	    	 while (true) {
	             try {
					socket = serverSocket.accept();
					System.out.println("�пͻ���/�����ڵ����: "+socket.getInetAddress().getHostAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             new ResponseThread(socket);
	         }
	    }
	}
    
    /** ����ڵ���Ӧ�߳�*/
    class ResponseThread extends Thread
    {
    	private Socket socket;
    	public ResponseThread(Socket socket)
    	{
    		this.socket = socket;
    		start();
    	}
    	public void run()
    	{
    		try {  				
    			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    			Message msg = (Message)ois.readObject();
    			Message outMes = null;
    			

    			if(msg.getType().equals(MsgType.ClientUploadService)){
    				System.out.println("�ͻ����ϴ�����");
    				//����ͻ��˼����򱾷���ڵ㷢�ͷ����ļ������ý���׼��
    				String fileName = (String)msg.getBody();
    				String fullPath = savedPath+fileName;
    				outMes = uploadService(fullPath);
    				
    			}
    			//����
    			ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
     		    oos.writeObject(outMes);
     		    oos.flush();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    	}
    	
    	private Message uploadService(String fullPath) throws IOException{
    		Message outMes = null;
    		// ��������С
			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];
			int passedlen = 0;
			long len = 0;
			DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fullPath)));
				
			// ����socket�������������ļ�
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				
			// ��ȡ�ļ�����
			len = inputStream.readLong();
//			System.out.println("�ļ��ĳ���Ϊ:" + len + "  B");
			System.out.println("��ʼ�����ļ�!");

			// ��ȡ�ļ�
			boolean f = true;
			while (f) {
				int read = 0;
				if (inputStream != null) {
					read = inputStream.read(buf);
				}
				passedlen += read;
				if (read == -1) {
					break;
				}
				System.out.println("�ļ�������" + (passedlen * 100 / len) + "%");
				fileOut.write(buf, 0, read);
				if((passedlen * 100 / len) == 100){
					f = false;
				}
			}
			System.out.println("������ɣ��ļ���Ϊ" + fullPath);
			fileOut.close();
			
			String flag = "fail";
			if(passedlen * 100 / len == 100){
				flag = "success";
			}
			outMes = new Message(MsgType.ClientUploadService, flag);
    		return outMes;
    	}
    	
    	
    }
    
    public serverNode() throws UnknownHostException, IOException, ClassNotFoundException {   	
    	/**�����˿�7001*/
        new ListenThread();
        
        /**��ע�����ķ���������Ϣ*/
 //      sendHeartToRegistryNode();
    }
    
   
    public static void main(String[] args) {
//    	 try {  
//             //�ļ�����·��  
//             PrintStream ss=new PrintStream("D:\\Server.txt");  
//             System.setOut(ss);  
//         } catch (FileNotFoundException e) {  
//             e.printStackTrace();  
//         }  
    	try {
			new serverNode();			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}