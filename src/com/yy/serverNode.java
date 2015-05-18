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
	//默认服务的存放位置
	private String savedPath = "E:\\";
	
	//服务节点监听端口
	 private static final int serverNodeListenPort = 7001;
	
    
    /** 服务节点会话发起线程*/ 
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
	    	System.out.println("服务节点服务器开始监听端口7001...");
			javax.swing.JOptionPane.showMessageDialog(null,"服务节点开始监听！");
	    	 while (true) {
	             try {
					socket = serverSocket.accept();
					System.out.println("有客户端/其他节点接入: "+socket.getInetAddress().getHostAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             new ResponseThread(socket);
	         }
	    }
	}
    
    /** 服务节点响应线程*/
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
    				System.out.println("客户端上传服务");
    				//如果客户端即将向本服务节点发送服务文件，做好接收准备
    				String fileName = (String)msg.getBody();
    				String fullPath = savedPath+fileName;
    				outMes = uploadService(fullPath);
    				
    			}
    			//返回
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
    		// 缓冲区大小
			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];
			int passedlen = 0;
			long len = 0;
			DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fullPath)));
				
			// 建立socket输入流，接收文件
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				
			// 获取文件长度
			len = inputStream.readLong();
//			System.out.println("文件的长度为:" + len + "  B");
			System.out.println("开始接收文件!");

			// 获取文件
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
				System.out.println("文件接收了" + (passedlen * 100 / len) + "%");
				fileOut.write(buf, 0, read);
				if((passedlen * 100 / len) == 100){
					f = false;
				}
			}
			System.out.println("接收完成，文件存为" + fullPath);
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
    	/**监听端口7001*/
        new ListenThread();
        
        /**向注册中心发送心跳消息*/
 //      sendHeartToRegistryNode();
    }
    
   
    public static void main(String[] args) {
//    	 try {  
//             //文件生成路径  
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