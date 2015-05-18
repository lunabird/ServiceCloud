package rgscenter.domain;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rgscenter.wsdl.ServiceInfo;
import rgscenter.wsdl.WSDLSet;

import java.io.FileNotFoundException;  
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yy.ConnMySQL;
import com.yy.ServiceHeaper;



















import Message.Message;
import Message.MigrateData;
import Message.MsgType;
/**
 * ע�����Ľڵ�
 * @author park_wh
 *
 */
public class ServiceRegistryNode{
	
	private List<ServiceInfo> services;
	private List<ServerNodeInfo> servers;
	private ArrayList<ResponseThread> responseThreads = new ArrayList<ServiceRegistryNode.ResponseThread>();
	/**������*/
	private int serviceAmount = 3;
	private Log log=LogFactory.getLog("ServiceRegistryNode");
	
	
	public static File file = new File("conf/IP.txt");
	
	//8-15����
	public static Hashtable<String,NodeInfo> hashtable = new Hashtable<String,NodeInfo>();
//	/**ע�����Ľ��и��������߳�*/
//	class DeployServicesCopy extends Thread{
//		public DeployServicesCopy(){
//			
//		}
//	}
	public static String unifiedIP = Config.newInstance().getAttribute("unifiedIP");
	
	/**
	 * ����������������������
	 * @author park_wh
	 *
	 */
	class ListenClientHeart extends Thread
	{
		private boolean flag = true;
		private Socket socket = null;
		private ServerSocket serverSocket = null;
		public boolean isFlag() {
			return flag;
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
		public ListenClientHeart() throws IOException
		{
			//���������˿�
			serverSocket = new ServerSocket(7001);
			this.start();
		}
		
		public void run()
		{
			System.out.println("��ʼ��������������������7001...");
			while(flag)
			{
				try {
					socket = serverSocket.accept();
					String ip = socket.getInetAddress().toString().substring(1);
//					System.out.println("�д�����������"+socket.getRemoteSocketAddress());
					log.info("�д�����������"+socket.getRemoteSocketAddress());
					new ResponseClientHeart(socket);
					//responseHeart.put(ip,new ResponseClientHeart(socket));
					//responseThreads.put(responseThread.getIdString(), responseThread);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	/*
	 * ��Ӧ����������������
	 */
	class ResponseClientHeart extends Thread
	{
		private boolean flag = true;
		
		private boolean connFlag = false;//�����ж��Ƿ��������Ͽ�����
		private Socket socket = null;
		private String idString;
		public boolean isConnFlag() {
			return connFlag;
		}
		public void setConnFlag(boolean connFlag) {
			this.connFlag = connFlag;
		}
		public String getIdString()
		{
			return idString;
		}
		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}
		public ResponseClientHeart(Socket socket)
		{
			this.socket = socket;
			this.start();
		}
		public void close()
		{
			this.setConnFlag(true);
			if(socket != null && !socket.isClosed())
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		public void run()
		{
//			while(flag)
//			{
				try{
					System.out.println("��������");
					String theip = socket.getInetAddress().toString().substring(1);
					ArrayList<String> theips = ServiceHeaper.getLegalIPs();
					//���Բ��Ϸ���������
					if(theips.contains(theip))
					{
						ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
						Message msg = (Message) in.readObject();
						
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						if(msg != null)
						{
							if(msg.getType() ==MsgType.WSDLSET)
						
							{
								System.out.println("��Ϣ��ʽƥ��");
								//List<String> body��
//								System.out.println("get heart msg ip:" +theip);
								log.info("get heart msg ip:" +theip);
								out.writeObject(new Message(MsgType.WSDLSET,"have receive heart msg"));
								out.flush();
								ArrayList<Object> al = (ArrayList<Object>) msg.getBody();
								Object[] nodeInfo = (Object[]) al.get(1);
								nodeInfo[0] = socket.getInetAddress().toString().substring(1);
								
								//����hashtable
								String[] st = new String[4];
								st[0] = nodeInfo[0].toString();
								st[1] = nodeInfo[1].toString();
								st[2] = nodeInfo[2].toString();
								st[3] = nodeInfo[3].toString();
								if(hashtable.containsKey(nodeInfo[0].toString()))
								{
								hashtable.get(nodeInfo[0].toString()).setLastHeartTime(System.currentTimeMillis());
								hashtable.get(nodeInfo[0].toString()).setNodeRealTimeInfo(st);
								}
	//							else
	//							{
	//								hashtable.put(nodeInfo[0].toString(), new NodeInfo());
	//							}
								//��ʵʱ��Ϣ�������ݿ�
								ServiceHeaper.insertNodeRealTimeInfo(nodeInfo);
								
								//���¿��з���״̬
								Map<String,String> serstatus = (Map<String, String>) al.get(2);
								ServiceHeaper.updateServicesStatus(serstatus, theip);
								Object[] wsdlset = (Object[]) al.get(0);
								//������ز���
							  
						    		for(Object s:wsdlset)
						    		{
						    			System.out.println(s.toString());
						    		}
						    		
						    	if(wsdlset.length==1)
						    	{
						    			//�������ε�����û�б仯		
						    			System.out.println("�����б��ޱ仯!");			
						    	}
						    	else
						    	{
						    			System.out.println("�������ݿ�");
						    			String serviceName = null;
						    			String serviceLocation = null;
						    			String serviceid = null;
						    			for(int i=0;i<wsdlset.length;)
						    			{
						    				//����wsdl��ַ��ȡ����������Ϣ
						    				System.out.println("3");
						    				
						    				
						    				if(wsdlset[i].toString().equals("ADD"))
						    				{
						    					UseUserTable u = new UseUserTable();
						    					serviceName = wsdlset[i+1].toString();
						    					serviceid = ServiceHeaper.getServiceIDByFileName(serviceName);
						    					serviceLocation = wsdlset[i+2].toString();
						    					System.out.println("filename:"+serviceName);
						    		//			String serviceType = getServiceType(serviceName);
						    					String serviceType = u.SelectServiceType(serviceName);
						    					System.out.println("getServiceType:"+serviceType);
						    					//���¸����� ����ServerInfo�и���ServiceNum
						    					System.out.println("abc:"+socket.getInetAddress().toString().substring(1));
						    					//������bu����ʱ
						    					if(serviceType != null)
						    						addAService(socket.getInetAddress().toString().substring(1),serviceid,serviceLocation,serviceType);
						    					
						    					i += 3;
						    				}
						    				else if(wsdlset[i].toString().equals("DELETE")){	
						    					//�޸����ݿ�
						    					//�Ӹ�������ɾ����¼ ,������ServerInfo����ServiceNum
						    					System.out.println("DELETE...");
						    					serviceName = wsdlset[i+1].toString();
						    					serviceid = ServiceHeaper.getServiceIDByFileName(serviceName);
						    					deleteAService(socket.getInetAddress().toString().substring(1),serviceid);
						    					i +=2;
						    				}
						    				else
						    				{
						    					i++;
						    					continue;
						    				}
						    				//ά�ָ�������
						    				ArrayList<String> list = ServiceHeaper.assignCopyByServiceName(serviceid);
						    				ServiceHeaper.dealWithCopyedIps(list,serviceName);
						    			}
						    	}
						    		
						    	
						 
							}
							else
							{
								//System.out.println("3");
								System.out.println("the msg is not matched");
								out.writeObject(new Message(MsgType.WSDLSET,"the msg is not matched"));
								flag = false;
								//�ڵ㲻���� ������ز���
	//							String ip = socket.getInetAddress().toString().substring(1);
	//							ServiceHeaper.keepSomeServiceCopy(dealUnavailableNode(ip));
							}
						}
						else
						{
							System.out.println("��Ϣ��ʽ��ƥ��");
							flag = false;
							System.out.println("readObject() failed");
							out.writeObject(new Message(MsgType.WSDLSET,"can't get msg heart"));
							//�ڵ㲻���� ������ز���
	//						String ip = socket.getInetAddress().toString().substring(1);
	//						ServiceHeaper.keepSomeServiceCopy(dealUnavailableNode(ip));
						}
						
						System.out.println("�����������");
						//out.flush();
					}
					//Thread.sleep(15000);
					
//				}
//				catch(Exception e)
//				{  	
//					flag = false;
////					String ip = socket.getInetAddress().toString().substring(1);
////					if(connFlag == false)
////					{
////						System.out.println("�����ڵ㲻����");
////						
////						//�ڵ㲻���� ������ز���
////						
////						try {
////							ServiceHeaper.keepSomeServiceCopy(dealUnavailableNode(ip));
////						} catch (Exception e2) {
////							// TODO Auto-generated catch block
////							e2.printStackTrace();
////						}
////						
////					}
////					else
////					{
////						System.out.println("�����ɹ��Ͽ�����");
////						
////						//�ڵ������Ͽ�����          ������ز���
////						//��node���н���Ӧ������״̬����Ϊdisconnect״̬�������з���Ҳ����Ϊdisconnect״̬
////						//������
////						try {
////							ServiceHeaper.dealwithTheDisConnNode(ip);
////						} catch (SQLException e1) {
////							// TODO Auto-generated catch block
////							e1.printStackTrace();
////						}
////						
////					}
////					
////					//�����̴߳�responseHeart����ɾ��
////						responseHeart.remove(ip);
//					if(socket != null && !socket.isClosed())
//					{
//						try {
//							System.out.println("�Ͽ�socket");
//							socket.close();
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//					}
//					e.printStackTrace();
//					
////				}
//				
//			}
		
				}catch(Exception e)
				{
					//e.printStackTrace();
					log.error(e.getMessage());
				}
				finally{
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}
	}
	class ScanHashtable extends Thread
	{
		public void run()
		{
			while(true)
			{

				long nowTime = System.currentTimeMillis();
				ArrayList<String> removeKeys = new ArrayList<String>();
				for(String key:hashtable.keySet())
				{
					NodeInfo value = hashtable.get(key);
					if(value.getStatus().equals("connect"))
					{
						if(nowTime - value.getLastHeartTime() > 180000)//��ʱ
						{
							value.setStatus("unreach");
						}
						else//����
						{
							//���ý����κβ���������lashtime��noderealtime�������н���
						}
					}
					else if(value.getStatus().equals("disconnect"))
					{
						//
					}
					else//����������
					{
						removeKeys.add(key);
						//��ʱ�ر�
						try {
							ServiceHeaper.keepSomeServiceCopy(dealUnavailableNode(key));
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					
					}
//
				}
				for(String key:removeKeys)
				{
					hashtable.remove(key);
				}
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	//��һ�������ڵ㲻����ʱ����������ɾ���������ڵ�����з���ͷ���ڵ���Ϣ
	public ArrayList<String> dealUnavailableNode(String ip) throws SQLException
	{
		ArrayList<String> list = new ArrayList<String>();
		DBConnectionManager co = DBConnectionManager.getInstance();
		Statement stat = co.getConnection().createStatement();
		ResultSet resultSet  = null;
		String sql3 = "select ServiceID from duplicate where IPAddress='";
//		ResultSet resultSet = co.query(sql3, ip);
		resultSet = stat.executeQuery(sql3+ip+"'");
		try{
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String sql = "delete from node where HostIP=? ";
		String sql2 = "delete from duplicate where IPAddress=?";
		//co.update("lock tables node read");
		//co.update("lock tables node write");
		co.update(sql,ip);
		//co.update("unlock tables");
		//co.update("lock tables duplicate read");
		//co.update("lock tables duplicate write");
		co.update(sql2, ip);
		//co.update("unlock tables");
		co.close(resultSet);
		co.close(stat);
		return list;
	}
	//ɾ��ָ��ip�����ϵ�ĳ���񣬲�����ServerInfo��serviceNum
	public static void deleteAService(String ip,String name)
	{
		DBConnectionManager co = DBConnectionManager.getInstance();
		co.getConnection();
		String sql = "update node set ServiceNumber=ServiceNumber-1 where HostIP=?";
		String sql2 = "delete from duplicate where IPAddress=? and ServiceID=?";
		
		//co.update("lock tables node write");
		co.update(sql,ip);
		//co.update("unlock tables");
		
		//co.update("lock tables duplicate write");
		co.update(sql2, ip,name);
		//co.update("unlock tables");
		co.close();
	}
	public static void deleteAService(String wsdl) throws SQLException
	{
		String IPAddress = null;
		DBConnectionManager co = DBConnectionManager.getInstance();
		Statement stat = co.getConnection().createStatement();
		ResultSet resultset = null;
		String sql0 = "select IPAddress from duplicate where WSDL='";
//		ResultSet resultset = co.query(sql0, wsdl);
		resultset = stat.executeQuery(sql0+wsdl+"'");
		try{
			if(resultset.next())
			{
				IPAddress = resultset.getString(1);
			}
			else
			{
				co.close();
				return;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		String sql = "update host set ServiceNumber=ServiceNumber-1 where HostIP=?";
		String sql2 = "delete from duplicate where WSDL=?";
		//co.update("lock tables host read");
		//co.update("lock tables host write");
		co.update(sql,IPAddress);
		//co.update("unlock tables");
		//co.update("lock tables duplicate read");
		//co.update("lock tables duplicate write");
		co.update(sql2, wsdl);
		//co.update("unlock tables");
		co.close(resultset);
		co.close(stat);
	}
	//ָ�����������ĳ���񣬲�����ServiceInfo��serviceNum
	public static  void addAService(String ip,String name,String location,String serviceType) throws SQLException, UnknownHostException, IOException
	{
		System.out.println("���¸������node��");
		DBConnectionManager co = DBConnectionManager.getInstance();
		Statement stat = co.getConnection().createStatement();
		ResultSet rs = null;
//		int flowNum = 0;
		String status = "start";
		if(serviceType.equalsIgnoreCase("Flow")){
			status = "stop";
//			co.update("lock tables duplicate read");
//			co.update("lock tables duplicate write");
//			String sql2 = "select count(*) from duplicate where ServiceName=? and ServiceStatus='start'";
//			ResultSet resultSet = (ResultSet) co.query(sql2, name);
//			if(resultSet.next())
//				flowNum = resultSet.getInt(1);
//			co.update("unlock tables");
//			if(flowNum==1) status = "stop";
//			 else if(flowNum==0){
//			Socket socket = new Socket(ip,8000);
//			Message outMes = new Message(MsgType.StartFlow, name);
//			 ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
//		        oos.writeObject(outMes);
//	 		    oos.flush();
//	 		    socket.close();
//			 }
		
		}
		// �жϸü�¼�Ƿ���duplicate��
		boolean flag = false;
		String sql0 = "select * from duplicate where ServiceID='"+name+"' and IPAddress='"+ip+"'";
//		ResultSet rs = co.query(sql0, name,ip);
		
		rs = stat.executeQuery(sql0);
		if(rs.next())
		{
			flag = true;
		}
		if(flag)
		{
			System.out.print(name+","+ip+"�Ѿ����ڣ�");
			String sql3 = "update duplicate set WSDL=?,Flag=1,ServiceType=? where ServiceID=? and IPAddress=?";
			String sql = "update node set ServiceNumber=ServiceNumber+1 where HostIP=?";
			co.update(sql3, location,serviceType,name,ip);
			co.update(sql, ip);
		}
		else{
			String sql = "update node set ServiceNumber=ServiceNumber+1 where HostIP=?";
			String sql2 = "insert into duplicate values(?,?,?,?,?,0)";
			
			//co.update("lock tables node write");
			co.update(sql,ip);
			//co.update("unlock tables");
			
			//co.update("lock tables duplicate write");
			co.update(sql2, name,ip,location,serviceType,status);
			System.out.println("insert into duplicate");
			//co.update("unlock tables");
			
		}
		co.close(rs);
		co.close(stat);
	}
	
	
	
	
	/** ע�����ļ���7000�߳�*/
	class ListenServerNodeThread extends Thread{
	    private static final int PORT = 7000;
	    private Socket socket;
	    private ServerSocket serverSocket;
	    private int id = 1;
	    
	    public ListenServerNodeThread()  throws IOException {
	        serverSocket = new ServerSocket(PORT);
	        start();
	    }
	    public void run()
	    {
	    	System.out.println("ע�����ķ�������ʼ��������ڵ�7000...");
	    	 while (true) {
	             try {
					socket = serverSocket.accept();
					System.out.println("�з���ڵ����"+socket.getRemoteSocketAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             responseThreads.add(new ResponseThread(socket, id++));
	         }
	    }
	}
	
	/** ע��������Ӧ�߳�*/
    class ResponseThread extends Thread {
        private Socket socket;
        private int id = 0;
        
        public ResponseThread(Socket socket,int id) {
        	this.id = id;
        	this.socket = socket;
            start();
        }
        public void run() {
            try {
                // ��ȡ���������������������Ϣ����
            	
	            	ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
	                
	                // msg�д�ŵ��Ƿ���ڵ㷢�͵ķ����б���Ϣ
	                //��Ϣ��Ϊwsdlset	                
	                Message msg = (Message) is.readObject();
	                Message Mesout = null;
	                
	               if(msg.getType() == MsgType.Host){
	                	//����������������������������
	                	System.out.println("���������");
	                	ArrayList<String> al = new ArrayList<String>();
	                	al = (ArrayList<String>)msg.getBody();
	                	al.set(0, socket.getInetAddress().toString().substring(1));
	                	//��ӵ�hashtable��
	                	hashtable.put(socket.getInetAddress().toString().substring(1), new NodeInfo());
	                	
	                	UseUserTable u = new UseUserTable();
	                	Boolean flag = u.AddHost(al);
	                	Mesout = new Message(MsgType.HostResult, flag);
	                	
	                	ObjectOutputStream os=new ObjectOutputStream(socket.getOutputStream());
	      		        os.writeObject(Mesout);
	      	 		    os.flush();
	      	 		    
	      	 		    //�����������ͱ仯�������б�
	      	 		   
//	      	 		    BufferedReader reader = new BufferedReader(new FileReader(file));
//	      	 		    String MonitorIP = reader.readLine();
//	      	 		    Socket socket1 = new Socket(MonitorIP,7001);
//	      	 		    ObjectOutputStream oos=new ObjectOutputStream(socket1.getOutputStream());
//	      	 		    ArrayList<String> list = new ArrayList<String>();
//	  	      		    list=u.GetIP();
//	  	      		    Mesout = new Message(MsgType.AllHost, list);
//	      	 		    os.writeObject(Mesout);
//	      	 		    os.flush();
	      	 		  //  socket1.close();
	      	 		    socket.close();
	                }//8.15����������������
	               else if(msg.getType() == MsgType.DisconnectRequest)
	                {
	            	   
	                	
	            	   String ip = socket.getInetAddress().toString().substring(1);
	            	   if(hashtable.containsKey(ip))
	            		   hashtable.get(ip).setStatus("disconnect");
	            	  //�������������� ״̬
	            	   try {
							ServiceHeaper.dealwithTheDisConnNode(ip);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	            	   System.out.println("�Ͽ����� "+ip);
	            	   socket.close();
	                }
	               else if(msg.getType() == MsgType.ConnectRequest)
	                {
	                	
	            	   String ip = socket.getInetAddress().toString().substring(1);
	            	   
	            	   //��������״̬
	            	   if(hashtable.containsKey(ip))
						{
	            		   hashtable.get(ip).setStatus("connect");
		            	   hashtable.get(ip).setLastHeartTime(System.currentTimeMillis());
						}
						else
						{
							hashtable.put(ip, new NodeInfo());
						}
	            	  
	            	   
	            	 //��node���н���״̬����Ϊconnect ������������ÿ�������״̬����Ϊ�����ݿ��о�����������״̬��
	            	   ServiceHeaper.setServiceStatusByIP(ip);
	            	   System.out.println("�ٴ��������� "+ip);
	            	   socket.close();
	                }
	               else if(msg.getType() == MsgType.CallService)
					{
	            	   //��������������÷���
	            	    System.out.println("CallService");
						ArrayList<String> body = (ArrayList<String>) msg.getBody();
						ArrayList<String> res = new ArrayList<String>();
						String assignWSDL = null;
						if(body.get(0)==null)
						{
							res = ServiceHeaper.getServicesByServiceRouter(body.get(1));
							
							if(res!=null){
							assignWSDL = res.get(1);
							if(assignWSDL == null)
								res.set(1, "error") ;
							}
						
						}
						else
						{
							//ɾ��wsdl��Ӧ�ķ��񸱱�
							deleteAService(body.get(0));
							//�ٴη���wsdl��ַ
							res = ServiceHeaper.getServicesByServiceRouter(body.get(1));
							
							if(res!=null){
							assignWSDL = res.get(1);
							if(assignWSDL == null)
								res.set(1, "error") ;
							}
						}
						
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(new Message(MsgType.CallService,res));
						oos.flush();
						socket.close();
					}
	               else if(msg.getType() == MsgType.CallService1)
					{
	            	   //��������������÷���
	            	    System.out.println("CallService1");
						ArrayList<String> body = (ArrayList<String>) msg.getBody();
						String assignWSDL = null;
						ArrayList<String> res = null;
						if(body.get(0).equalsIgnoreCase("null"))
						{
							res = ServiceHeaper.getServicesByServiceRouter(body.get(1));
							
							if(res!=null){
							assignWSDL = res.get(1);
							if(assignWSDL == null)
								res.set(1, "error") ;
							}
						
						}
						else
						{
//							//ɾ��wsdl��Ӧ�ķ��񸱱�
//							deleteAService(body.get(0));
//							//�ٴη���wsdl��ַ
//							res = ServiceHeaper.getServicesByServiceRouter(body.get(1));
//							
//							if(res!=null){
//							assignWSDL = res.get(1);
//							if(assignWSDL == null)
//								res.set(1, "error") ;
//							}
						}
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(new Message(MsgType.CallService1,res));
						oos.flush();
						socket.close();
					}
	               else if(msg.getType().equals(MsgType.CallInfo)){
						//����������������������������Ϣ
						System.out.println("CallInfo");
						ArrayList<String> al = new ArrayList<String>();
						al = (ArrayList<String>)msg.getBody();
						UseUserTable u = new UseUserTable();
						u.AddCallInfo(al);
					}
	               else if(msg.getType().equals(MsgType.CallInfo1)){
						//����������������������������Ϣ
						System.out.println("CallInfo1");
						ArrayList<String> al = new ArrayList<String>();
						al = (ArrayList<String>)msg.getBody();
						UseUserTable u = new UseUserTable();
						u.AddCallInfo1(al);
					}
	               else if(msg.getType().equals(MsgType.DependInfo)){
						//�����������������������IP��������
						System.out.println("DependInfo");
						String IP = new String();
						String name = (String)msg.getBody();
						UseUserTable u = new UseUserTable();
						String serviceID = ServiceHeaper.getServiceIDByFileName(name);
						IP = u.SelectServiceIP(serviceID);
						System.out.println(name+"  "+IP+serviceID);
						
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(new Message(MsgType.DependInfoResult,IP));
						oos.flush();
						socket.close();
					}
//	                try{
//	        			Thread.sleep(10000);
//	        		}
//	        		catch(InterruptedException e){
//	        			e.printStackTrace();
//	        		}

	                // �����ⲿ��ĺ���
//	                addService(wsdlset.GetServiceInfosFromLocations());
//	                System.out.println("ע����������״̬��");
//	                // �����ⲿ��ĺ���
//	                showStatus();
//	                
//	             // ��ͻ���1������������ļ�������
//	               
//	                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//	                
//	                //MigraData data=new MigraData(serviceName,serviceIp,port);
//	                MigrateData data = new MigrateData("Test","127.0.0.1",7001);
//	                msg = new Message(MsgType.REPLICATE, data);
//	                oos.writeObject(msg);
//	                oos.flush();
              
	                
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
        
        /**���и�������*/
        public void deployServiceCopies(){
        	ArrayList<String> allServices = new ArrayList<String>();
        	ArrayList<String> allIPAddress = new ArrayList<String>();
        	UseUserTable u = new UseUserTable();
        	try {
				allServices = u.clientGetServiceList();
				
				for(int i=0;i<allServices.size();i++){
	        		String serviceName = allServices.get(i);
	        		int amount = u.getServiceCopyAmount(serviceName);
	        		if(amount < 3){
	        			//���������С��3, ��ȡ������С�Ľڵ�
	        			String copyIPAddress = u.getMiniumIPAddress(serviceName);	//��Ҫ�����ķ���ڵ�ĵ�ַ
	        			String copiedIPAddress = u.getIPFromCopyTable(serviceName);	//�Ӹ������л�ȡ�÷������ڵ�IP��ַ
	        			
	        			//��copyIPAddress����ڵ㷢����Ϣ�������copiedIPAdress�����������ļ�
	        			
	        		}
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	
        }
        
        /**
    	 * ������delete�����ķ���ڵ�������ڵ㿽������
    	 * @param wsdllocation ������delete�����ķ���ڵ��ַ
    	 * @throws UnknownHostException
    	 * @throws ClassNotFoundException
    	 * @throws IOException
    	 * @throws SQLException 
    	 */
    	public void copyFromOtherNode(String wsdllocation) throws UnknownHostException, ClassNotFoundException, IOException, SQLException
    	{	
    		UseUserTable u = new UseUserTable();
    		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    		
    		Services ser = new Services();
    		try {
    			ser.getServices(wsdllocation);
    		} catch (UnknownHostException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}		
    		String serviceName=ser.getName(wsdllocation);
    		String ip = u.selectIPFromServicename(serviceName);
    		
    		MigrateData data = new MigrateData(serviceName,ip,7001);
    		Message msg = new Message(MsgType.REPLICATE, data);
            oos.writeObject(msg);
            oos.flush();
        	
    	}
    	/**
    	 * ����������ڵ�ӱ��ڵ�����������,��ע�������������ڵ㷢���Ŀ�������
    	 * @param ip ���ڵ��ip
    	 * @param serName Ҫ�����ķ�����
    	 * @throws IOException
    	 * @throws SQLException 
    	 */
        public void copyFromThisNode(String ip,String serName) throws IOException, SQLException{    
        	
    		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
   				
        		MigrateData data = new MigrateData(serName,ip,7001);
        		Message msg = new Message(MsgType.REPLICATE, data);
                oos.writeObject(msg);
                oos.flush();        	
        }
        /**
    	 * ��ȡ���������б�
    	 * @throws ParseException 
    	 * @throws SQLException 
    	 * @throws IOException 
    	 * @throws ClassNotFoundException 
    	 * @throws UnknownHostException 
    	 */
    	public void getHeartBeat(WSDLSet wsdlset) throws ParseException, SQLException, UnknownHostException, ClassNotFoundException, IOException{
    		UseUserTable u = new UseUserTable();
    		List<String> wsdlLocation = wsdlset.getWsdllocations();
    		
    		if(wsdlLocation.size()==1){
    			//�������ε�����û�б仯		
    			System.out.println("�����б��ޱ仯!");			
    		}
    		else{
    			System.out.println("�������ݿ�");
    			for(int i=0;i<wsdlLocation.size();i+=2){
    				if(wsdlLocation.get(i).equals("ADD")){
    					//���¸�����
    					u.insert_Table(wsdlLocation.get(i+1));   					
    					//����allServerNodes��
        				u.insertAllServerNodesTable(wsdlLocation.get(i+1));
    					
    					Services ser = new Services();
    					try {
    						ser.getServices(wsdlLocation.get(i+1));
    					} catch (UnknownHostException e1) {
    						// TODO Auto-generated catch block
    						e1.printStackTrace();
    					}	
    					String serName=ser.getName(wsdlLocation.get(i+1));
    					String ipadress=ser.getIpAddress(wsdlLocation.get(i+1));
 					
    					//�˴�Ӧ������������ڵ㷢�������˷��������
//    					if(u.getServiceAmount(wsdlLocation.get(i+1)) < serviceAmount){   						
//    						ArrayList<String> iplist = u.getOtherServernodes(wsdlLocation.get(i+1));
//    						for(int j=0;j<iplist.size();j++){
//    							//�������ڵ㷢������Ӵ˴���������������
//    							copyFromThisNode(ipadress,serName);
//    						} 						
//    					}
    				}
    				else if(wsdlLocation.get(i).equals("DELETE")){	
    					//�޸����ݿ�
    					//�Ӹ�������ɾ����¼ ,����������м�¼��Ϊ0����ɾ���ø�����,��ɾ��services�еļ�¼
    					u.deleteServiceFromCopy(wsdlLocation.get(i+1));
//    					u.updateServiceFromServices(wsdlLocation.get(i+1));	
    					
//    					if(u.getServiceAmount(wsdlLocation.get(i+1))<3){
//    						//�����������ڵ㿽������������
//    						copyFromOtherNode(wsdlLocation.get(i+1));
//    					}
    				}
    			}
    		}
    	}
    	
    }	
    	
       
	
    /** ע�����ļ��� �ͻ����� �߳�*/
   	class ListenClientAgentThread extends Thread{
   	    private static final int CLIENTAGENTPORT = 5000;
   	    private Socket socket_c;
   	    private ServerSocket serverSocket_c;
   	    
   	    public ListenClientAgentThread()  throws IOException {
   	        serverSocket_c = new ServerSocket(CLIENTAGENTPORT);
   	        start();
   	    }
   	    public void run()
   	    {
   	    	System.out.println("ע�����ķ�������ʼ�����ͻ��˴���5000...");
			javax.swing.JOptionPane.showMessageDialog(null,"ע�����Ŀ�ʼ������");
   	    	 while (true) {   	    		
   	             try {
   					socket_c = serverSocket_c.accept();
   					System.out.println("�пͻ��˽���"+socket_c.getRemoteSocketAddress());
   				} catch (IOException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}  	             
   	             new ResponseClientAgent(socket_c);
   	         }
   	    }
   	}
    
    
    
   	/** ע��������Ӧ �ͻ����� �߳�*/
    class ResponseClientAgent extends Thread {
     private Socket socket_c;
     
     public ResponseClientAgent(Socket socket_c) {
         this.socket_c = socket_c;
         start();
     }
     public void saveFile(String name,String length,String dir) throws IOException{
  		//�ж��ļ��Ƿ���ڣ����ھ�ɾ����
  		File file = new File(dir+File.separator+name);
  		if(file.exists()){
  			file.delete();
  		}
  		FileOutputStream fos = new FileOutputStream(file);
  		InputStream is = socket_c.getInputStream();
  		
  		int bufferSize = 8192;
  		byte[] buf = new byte[bufferSize];
  		long passedlen = 0;
  		long len = Long.valueOf(length);
  		
  		
  	    System.out.println("�ļ��ĳ���Ϊ:" + len + "  B");
  		System.out.println("��ʼ�����ļ�!");

  		// ��ȡ�ļ�
  		boolean f = true;
  		while (f) {
  			int read = 0;
  			if (is != null) {
  				read = is.read(buf);
  			}
  			passedlen += read;
  			if (read == -1) {
  				break;
  			}
  			System.out.println("�ļ�������" + (passedlen * 100 / len) + "%");
  			
  			fos.write(buf, 0, read);
  			
  			if((passedlen * 100 / len) == 100){
  				f = false;
  			}
  		}
  		
  		System.out.println("������ɣ��ļ���Ϊ" + dir+File.separator+name);
  		fos.close();//�ر��ļ���
  	}
     public void run(){  
 		try {
 //			BufferedReader ois = new BufferedReader(new InputStreamReader(socket_c.getInputStream(),"GB2312"));
     		ObjectInputStream ois = new ObjectInputStream(socket_c.getInputStream());
 			Message inMes = (Message)ois.readObject();
 			Message outMes = null;
 		    if(inMes.getType().equals(MsgType.LOGIN)){		    	
 		        //����ͻ��˷�������Ϣ�ǿͻ���¼����
 		    	System.out.println("LOGIN");
 		        String[] info = (String[]) inMes.getBody();
 		        outMes = login(info);
 		        
 		       ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
 		        oos.writeObject(outMes);
 	 		    oos.flush();
 	 		    socket_c.close();
 		        
 		    }else if(inMes.getType().equals(MsgType.SERVICELIST)){//�Ѿ�����
 		        //�ͻ��˷�������������ʾ�����б�
 		    	System.out.println("SERVICELIST");
 		    	UseUserTable u = new UseUserTable();
 	      		ArrayList<String[]> list = new ArrayList<String[]>();
 		    	list=u.ListServices();
 		    	outMes = new Message(MsgType.SERVICELISTRESULT, list);
 		    	
 		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
 		   
 		    }
 		   else if(inMes.getType().equals(MsgType.HOSTINFO)){//�Ѿ�����
		        //�ͻ��˷�������������ʾָ��ipʵʱ��������Ϣ
		    	System.out.println("HOSTINFO");
		    	String ip = (String) inMes.getBody();
		    	outMes = new Message(MsgType.HOSTINFO, ServiceHeaper.getHostCPUInfo(ip));
		    	
		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
 		        oos.writeObject(outMes);
 	 		    oos.flush();
 	 		    socket_c.close();
		   
		    }
 		  else if(inMes.getType().equals(MsgType.HOSTHARDDISK)){//�Ѿ�����
		        //�ͻ��˷�������������ʾָ��ipʵʱ��������Ϣ
		    	System.out.println("HOSTHARDDISK");
		    	String ip = (String) inMes.getBody();
		    	outMes = new Message(MsgType.HOSTHARDDISK, ServiceHeaper.getHostDiskInfo(ip));
		    	
		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
	 		    socket_c.close();
		   
		    }
 		   else if(inMes.getType().equals(MsgType.UNDEPLOYEDSERVICE)){//�Ѿ�����
		        //�ͻ��˷�������������ʾ�����б�
		    	System.out.println("UNDEPLOYEDSERVICE");
		    	UseUserTable u = new UseUserTable();
	      		ArrayList<String[]> list = new ArrayList<String[]>();
		    	list=u.getUndeployedService();
		    	outMes = new Message(MsgType.UNDEPLOYEDSERVICE, list);
		    	
		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
 		        oos.writeObject(outMes);
 	 		    oos.flush();
 	 		    socket_c.close();
		   
		    }
 		    else if(inMes.getType().equals(MsgType.HOSTLIST)){//�Ѿ�����
 		    	 //�ͻ��˷�������������ʾָ���������б��CPU���ڴ桢Ӳ�̡�������������Ϣ��servlet��������������ɸѡ
 		    	System.out.println("HOSTLIST");
 		    	UseUserTable u = new UseUserTable();
 		    	String body = (String) inMes.getBody();
 		    	ArrayList<String[]> allinfo = new ArrayList<String[]>();
 	      		ArrayList<String[]> list = new ArrayList<String[]>();
 	      		ArrayList<String[]> server = new ArrayList<String[]>();
 	      		list=u.ListHost(body);
 	      		server = ServiceHeaper.getServerInfo3();
 	      		for(String[] hostinfo : list){
 	      			for(String[] serverinfo : server){
 	      				if(serverinfo[0].equalsIgnoreCase(hostinfo[0])){
 	      					hostinfo[2] = serverinfo[1];
 	      					hostinfo[3] = serverinfo[2];
 	      					hostinfo[4] = serverinfo[3];
 	      					allinfo.add(hostinfo);
 	      					break ;
 	      				}
 	      			}
 	      		}
 	      		outMes = new Message(MsgType.HOSTLISTRESULT, allinfo);
 	      	 	      		
 	      		ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  		        oos.flush();
  	 		    socket_c.close();
 		     }else if(inMes.getType().equals(MsgType.SETCOPYNUM)){//�Ѳ��ԣ�Flow����δ���ԣ�δ����������
 		    	 //�ͻ��˷������������޸ķ��񸱱�����
 		    	 System.out.println("SETCOPYNUM");
 		    	 UseUserTable u = new UseUserTable();
 		    	 ArrayList<String> list = new ArrayList<String>();
 		    	 list=(ArrayList<String>) inMes.getBody();
 		    	 Boolean flag = u.SetDuplicateNum(list);
 		    	 ArrayList<String> ip = new ArrayList<String>();
 		    	 ip = ServiceHeaper.assignCopyByServiceName(list.get(0));
 		    	 ServiceHeaper.dealWithCopyedIps(ip,ServiceHeaper.getFileNameByServiceID(list.get(0)));
 		    	 outMes = new Message(MsgType.SETCOPYNUMRESULT, flag?"true":"false");
 		    	 
 		    	
 		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
 		     } else if(inMes.getType().equals(MsgType.SERVICECOPYLIST)){//�Ѳ���
  		        //�ͻ�������������ķ����̷����б�
  		    	System.out.println("SERVICECOPYLIST");
  		    	ArrayList<String[]> al = new ArrayList<String[]>();
  		    	UseUserTable u = new UseUserTable();
  		    	al = (ArrayList<String[]>)u.GetAllServices();
  		    	outMes = new Message(MsgType.SERVICECOPYLISTRESULT, al);
  		    	
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 	    	socket_c.close();
  		    }else if(inMes.getType().equals(MsgType.FLOWCOPYLIST)){
  		    	 //�ͻ�����������������̷����б�
  		    	System.out.println("FLOWCOPYLIST");
  		    	ArrayList<String[]> al = new ArrayList<String[]>();
  		    	UseUserTable u = new UseUserTable();
  		    	al = (ArrayList<String[]>)u.GetAllFlows();
  		    	outMes = new Message(MsgType.FLOWCOPYLISTRESULT, al);
  		    	
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 	    	socket_c.close();
  		    }else if(inMes.getType().equals(MsgType.SERVICESTATUS)){
  		    	//�ͻ��������޸ķ���״̬
  		    	System.out.println("SERVICESTATUS");
  		    	ArrayList<String> al = (ArrayList<String>)inMes.getBody();
  		    	UseUserTable u = new UseUserTable();
  		    	//�����ļ�����
  		    	String servicename = ServiceHeaper.getFileNameByServiceID(al.get(0));
  		    	//���ø���״̬���Ż����еĸ���ip
  		    	ArrayList<String> list = u.SetServiceStatus(al);
  		    	outMes = new Message(MsgType.SERVICESTATUSRESULT, list);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		    
  	 		    //��exe���⴦��
  	 		    String type = ServiceHeaper.getServiceTypeByServiceID(al.get(0));
  	 		    System.out.println("serviceType:"+type);
  	 		    System.out.println("filename:"+servicename);
  	 		    if(type.equalsIgnoreCase("exe"))
  	 		    {
  	 		    	//��ÿ��ip���ͷ�����ָͣ��
  	 		    	System.out.println("exe stop/start!");
  	 		    	ArrayList<String> body = new ArrayList<String>();
  	 		    	Message inMSG = null;
  	 		    	body.add(servicename);
  	 		    	body.add(al.get(1));
  	 		    	body.add(type);
  	 		    	for(String ip:list)
  	 		    	{
  	 		    		Socket socket = new Socket(ip,6000);
  	 		    		Message outMes1 = new Message(MsgType.changeServiceStatus,body);
  	 		    		ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
  	 		    		oos1.writeObject(outMes1);
  	 		    		oos1.flush();
  	 		    		
  	 		    		ObjectInputStream ois1 = new ObjectInputStream(socket.getInputStream());
  	 		    		inMSG = (Message) ois1.readObject();
  	 		    		//��������
  	 		    		socket.close();
  	 		    		
  	 		    	}
  	 		    }
  	 		    else if(type.equalsIgnoreCase("restful"))
  	 		    {
  	 		    	System.out.println("restful stop/start!");
  	 		    	ArrayList<String> body = new ArrayList<String>();
  	 		    	Message inMSG = null;
  	 		    	body.add(servicename);
  	 		    	body.add(al.get(1));
  	 		    	body.add(type);
  	 		    	for(int i=0;i<list.size();i++)
  	 		    	{	//�ҵ�ÿ��������Ӧ��rui
  	 		    		String uri = u.getRestfulURI(al.get(0), list.get(i));
  	 		    		body.set(0, uri);
  	 		    		
  	 		    		
  	 		    		Socket socket = new Socket(list.get(i),6000);
  	 		    		Message outMes1 = new Message(MsgType.changeServiceStatus,body);
  	 		    		ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
  	 		    		oos1.writeObject(outMes1);
  	 		    		oos1.flush();
  	 		    		
  	 		    		ObjectInputStream ois1 = new ObjectInputStream(socket.getInputStream());
  	 		    		inMSG = (Message) ois1.readObject();
  	 		    		//��������
  	 		    		socket.close();
  	 		    		
  	 		    	}
  	 		    }
  	 		    
  		    }else if(inMes.getType().equals(MsgType.GETSERVICESTATUS)){
  		    	System.out.println("GETSERVICESTATUS");
  		    	String serviceid = (String) inMes.getBody();
  		    	UseUserTable u = new UseUserTable();
  		    	String list = u.getServiceStatus(serviceid);
  		    	outMes = new Message(MsgType.SETCCRNUMRESULT, list);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }
  		    else if(inMes.getType().equals(MsgType.SETCCRNUM)){//�Ѳ���
  		    	//�ͻ��������޸ķ���״̬
  		    	System.out.println("SETCCRNUM");
  		    	ArrayList<String> al = (ArrayList<String>)inMes.getBody();
  		    	UseUserTable u = new UseUserTable();
  		    	Boolean list = u.SetServiceCCRNum(al);
  		    	outMes = new Message(MsgType.SETCCRNUMRESULT, list?"true":"false");
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }
  		    else if(inMes.getType().equals(MsgType.STRGLIST)){
  		    	//�ͻ�������չʾ���з���Ҫ��Ĳ���
  		    	System.out.println("STRGLIST");
  		    	String input = (String)inMes.getBody();
  		    	ArrayList<String[]> al = new ArrayList<String[]>();
  		    	UseUserTable u = new UseUserTable();
  		    	al = u.ListStrategy(input);
  		    	outMes = new Message(MsgType.STRGLISTRESULT, al);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }else if(inMes.getType().equals(MsgType.SELECTSTRG)){
  		    	//�ͻ���ѡ��ʹ��ĳ����
  		    	System.out.println("SELECTSTRG");
  		    	String input = (String)inMes.getBody();
  		    	UseUserTable u = new UseUserTable();
  		    	Boolean flag = u.SelectStrategy(input);
  		    	outMes = new Message(MsgType.SELECTSTRGRESULT, flag);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }else if (inMes.getType().equals(MsgType.SETSTRGPARA)){
  		    	//�ͻ���ѡ�����ò���CPU���ڴ����
  		    	System.out.println("SETSTRGPARA");
  		    	ArrayList<String> al = new ArrayList<String>();
  		    	al = (ArrayList<String>)inMes.getBody();
  		    	UseUserTable u = new UseUserTable();
  		    	Boolean flag = u.SetStrategyPara(al);
  		    	outMes = new Message(MsgType.SETSTRGPARARESULT, flag);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }else if(inMes.getType().equals(MsgType.SERVICEDEPLOY)){//�Ѳ���
 		    	//����ͻ��������Ƿ�������
 		    	System.out.println("SERVICEDEPLOY");
 		    	ArrayList<String> serviceInfo = (ArrayList<String>)inMes.getBody();
 		    	outMes = publishService(serviceInfo);

 		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 	    	
  	 	    	//�����̣߳���ѯ���������Ƿ����м�¼���鵽֮����ȫ�ֵ�ַ�����ͷ��������Ϣ
  	 	    	ServiceFeedback s = new ServiceFeedback(serviceInfo.get(0));
  	 	    	socket_c.close();
 		    }
  		    else if(inMes.getType().equals(MsgType.SERVICEDIRECTDEPLOY)){//�Ѳ���
		    	//�ֶ���������
		    	System.out.println("SERVICEDIRECTDEPLOY");
		    	ArrayList<String> serviceInfo = (ArrayList<String>)inMes.getBody();
		    	publishService2(serviceInfo);

//		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
//		        oos.writeObject(outMes);
//	 		    oos.flush();
	 	    	
	 	    	//�����̣߳���ѯ���������Ƿ����м�¼���鵽֮����ȫ�ֵ�ַ�����ͷ��������Ϣ
	 	    	ServiceFeedback s = new ServiceFeedback(serviceInfo.get(0));
	 	    	socket_c.close();
		    }
  		  else if(inMes.getType().equals(MsgType.SERVICEDEPLOYFLAG)){//�Ѳ���
		    	//��֤���������Ƿ�ɹ�
		    	System.out.println("SERVICEDEPLOYFLAG");
		    	ArrayList<String> deployInfo = (ArrayList<String>)inMes.getBody();
		    	checkDeployInfo(deployInfo);
	 	    	
	 	    	//�����̣߳���ѯ���������Ƿ����м�¼���鵽֮����ȫ�ֵ�ַ�����ͷ��������Ϣ
//	 	    	ServiceFeedback s = new ServiceFeedback(serviceInfo.get(0));
	 	    	socket_c.close();
	 	    	
		    }
  		    else if (inMes.getType().equals(MsgType.FEEDBACKREQUEST)){
 		    	//ǰ��������������Ϣ
 		    	System.out.println("FEEDBACKREQUEST");
 		    	String sname = (String)inMes.getBody();
 		    	UseUserTable u = new UseUserTable();
 		    	String back[] = new String[3];
 		    	back = u.FeedBack(sname);
 		    	outMes = new Message(MsgType.FEEDBACK, back);
 		    	
 		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 	    	socket_c.close();
  	 	    	
 		    }else if(inMes.getType().equals(MsgType.SERVICEMESSAGE)){
 		       //�ͻ����󷢲�������Ϣ��Ϣ
 		    	System.out.println("SERVICEMESSAGE");
 		    	ArrayList<String[]> al = new ArrayList<String[]>();
 		    	al = (ArrayList<String[]>)inMes.getBody();
 		    	UseUserTable u = new UseUserTable();
 		    	u.AddServiceMessage(al);
 		    	socket_c.close();
 		    	//��������������Ϣ��
 		    }else if(inMes.getType().equals(MsgType.MESSAGELIST)){
 		    	//�ͻ�����չʾ������Ϣ��Ϣ
 		    	System.out.println("MESSAGELIST");
 		    	ArrayList<String[]> al = new ArrayList<String[]>();
 		    	UseUserTable u = new UseUserTable();
 		    	al = u.ListMessage();
 		    	outMes = new Message(MsgType.MESSAGELISTRESULT,al);
 		    	
 		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
   		        oos.writeObject(outMes);
   	 		    oos.flush();
   	 		    socket_c.close();
 		    }else if(inMes.getType().equals(MsgType.SERVICEDELETE)){
 		    	//����ͻ�������ɾ������
 		    	System.out.println("SERVICEDELETE");
 		    	String serviceName = (String)inMes.getBody();
 		    	outMes = deleteService(serviceName);
 		    	
 		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
 		    }else if(inMes.getType().equals(MsgType.FLOWLIST)) {
 		    	//����ͻ��������г������б�(���̱��Ź���)
 		    	System.out.println("FLOWLIST");
 		    	UseUserTable u = new UseUserTable();
 		    	ArrayList<String[]> al = u.ListFlow();
 		    	outMes = new Message(MsgType.FLOWLISTRESULT, al);
 		    	
 		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
   		        oos.writeObject(outMes);
   	 		    oos.flush();
   	 		    socket_c.close();
 		     }else if(inMes.getType().equals(MsgType.FLOWLIST2)) {
  		    	//����ͻ��������г������б�(���������)
  		    	System.out.println("FLOWLIST2");
  		    	UseUserTable u = new UseUserTable();
  		    	ArrayList<String[]> al = u.ListFlow2();
  		    	outMes = new Message(MsgType.FLOWLIST2RESULT, al);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
    		        oos.writeObject(outMes);
    	 		    oos.flush();
    	 		    socket_c.close();
  		     }else if(inMes.getType().equals(MsgType.SERVFLOWDEPLOY)){
   		    	//�ͻ�������������̲���
 		    	 System.out.println("SERVFLOWDEPLOY");
 		    	ArrayList<String> flowInfo = (ArrayList<String>)inMes.getBody();
 		    	outMes = deployFlow(flowInfo);

 		        ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if (inMes.getType().equals(MsgType.FLOWEXECUTE)){
  	 			 //�ͻ�����������ִ��(������ҵ�����̺ͷ������̣�Ҫִ�б����Ȳ���)
  	 			 System.out.println("FLOWEXECUTE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String> al =u.SetFlowStatus((String)inMes.getBody(),"start");
  	 			 outMes = new Message(MsgType.FLOWEXECUTERESULT, al.get(0));
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.FLOWSTATUS)){
  	 			 //�ͻ��������޸�����״̬
  	 			 System.out.println("FLOWSTATUS");
  	 			 UseUserTable u = new UseUserTable();
  	 			 String name =  ((ArrayList<String>) inMes.getBody()).get(0);
  	 			 String status = ((ArrayList<String>) inMes.getBody()).get(1);
  	 			 ArrayList<String> ip = u.SetFlowStatus(name,status);
  	 			 outMes = new Message(MsgType.FLOWSTATUSRESULT, ip);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.FLOWTRANS)){
  	 			 //�ͻ��������������ת����ҵ�����̵��������̣�
  	 			 System.out.println("FLOWTRANS");
  	 			 String business = (String)inMes.getBody();
  	 			 String service = transformFlow(business);
  	 			 outMes = new Message(MsgType.FLOWTRANSRESULT, service);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.BSNSFLOWDEPLOY)){
  	 			 //�ͻ����������ҵ�����̲���
  	 			 ArrayList<String> al = new ArrayList<String>();
  	 			 al = (ArrayList<String>)inMes.getBody();
  	 			 System.out.println("��������Ϊ"+al.get(4));
  	 			 if(al.get(4).equals("0")) {
  	 				 al.set(4, transformFlow(al.get(3)));
  	 				 System.out.println("��������ת��");
  	 			 }
  	 			 outMes = deployFlow(al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.SERVICEEXIST)){
  	 			//����ƥ�����/�������Ƿ����
  	 			 System.out.println("SERVICEEXIST");
  	 			 UseUserTable u = new UseUserTable();
  	 			 Boolean flag = null;
  	 			 String name = (String)inMes.getBody();
  	 			 flag = u.isNameExist(name);
  	 			 outMes = new Message(MsgType.SERVICEEXISTRESULT, flag?"true":"false");
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }
  	 		else if(inMes.getType().equals(MsgType.SERVICEDETAIL)){
  	 			
  	 			 System.out.println("SERVICEDETAIL");
  	 			 ArrayList<String> al = ServiceHeaper.getServiceDetail();
  	 			 outMes = new Message(MsgType.SERVICEDETAIL, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }
  	 		else if(inMes.getType().equals(MsgType.NODEDETAIL)){
  	 			
 	 			 System.out.println("NODEDETAIL");
 	 			ArrayList<String> al = ServiceHeaper.getNodeDetail();
 	 			 outMes = new Message(MsgType.NODEDETAIL, al);
 	 			 
 	 			 
 	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
 		        oos.writeObject(outMes);
 	 		    oos.flush();
 	 		    socket_c.close();
 	 		 }else if(inMes.getType().equals(MsgType.FILTER)){
  	 			//ɸѡ�����
  	 			 System.out.println("FILTER");
  	 			 UseUserTable u = new UseUserTable();
  	 			 HashSet<String> hs = new HashSet<String>();
  	 			 String type = (String)inMes.getBody();
  	 			 hs = u.FilterService(type);
  	 			 outMes = new Message(MsgType.FILTERRESULT, hs);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.FILTERSERVICE)){
  	 			//ģ����ѯ�����
  	 			 System.out.println("FILTERSERVICE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String[]> al = new ArrayList<String[]>();
  	 			 ArrayList<String> in= (ArrayList<String>)inMes.getBody();
  	 			 al = u.CFilterService(in);
  	 			 outMes = new Message(MsgType.FILTERSERVICERESULT, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.NODELIST)){
  	 			//�ͻ�������ڵ���Ϣչʾ
  	 			 System.out.println("NODELIST");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String[]> al = new ArrayList<String[]>();
  	 			 al = u.ListNodeInfo();
  	 			 outMes = new Message(MsgType.NODELISTRESULT, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.NODEDELETE)){
  	 			//�ͻ�������ɾ���ڵ���Ϣ
  	 			 boolean flag=false;
  	 			 System.out.println("NODEDELETE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 String ipaddress = (String)inMes.getBody();
  	 			 if(u.nodeExist(ipaddress));
  	 			 {
  	 				
  	 			 	//u.DeleteNode(ipaddress);
  	 			 	flag = true;
  	 			 	//��ڵ����kill����
  	 			 	Socket socket1 = new Socket(ipaddress,8888);
  	 			 	Message mes = new Message(MsgType.KILL,"");
  	 			 	ObjectOutputStream oos = new ObjectOutputStream(socket1.getOutputStream());
  	 			 	oos.writeObject(mes);
  	 			 	oos.flush();
  	 			 	socket1.close();
  	 			 }
  	 			 outMes = new Message(MsgType.NODEDELETERESULT,flag?"true":"false");
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.NODESET)){
  	 			//�ͻ��������޸Ľڵ���Ϣ
  	 			 System.out.println("NODESET");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String> info = (ArrayList<String>)inMes.getBody();
  	 			 u.SetNodeInfo(info);
  	 			 outMes = new Message(MsgType.NODESETRESULT, true);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.NODEINSERT)){
  	 			//�ͻ�������½ڵ���Ϣ
  	 			 System.out.println("NODEINSERT");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String> info = (ArrayList<String>)inMes.getBody();
  	 			 u.AddNodeInfo(info);
  	 			 outMes = new Message(MsgType.NODEINSERTRESULT, true);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.FILTERNODE)){
  	 			//ɸѡ�ڵ��
  	 			 System.out.println("FILTERNODE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 HashSet<String> hs = new HashSet<String>();
  	 			 String type = (String)inMes.getBody();
  	 			 hs = u.FilterNode(type);
  	 			 outMes = new Message(MsgType.FILTERNODERESULT, hs);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.CFILTERNODE)){
  	 			//ģ����ѯ�ڵ��
  	 			 System.out.println("CFILTERNODE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String[]> al = new ArrayList<String[]>();
  	 			 ArrayList<String> in= (ArrayList<String>)inMes.getBody();
  	 			 al = u.CFilterNode(in);
  	 			 outMes = new Message(MsgType.CFILTERNODERESULT, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.FILTERSTRATEGY)){
  	 			//ɸѡ���Ա�
  	 			 System.out.println("FILTERSTRATEGY");
  	 			 UseUserTable u = new UseUserTable();
  	 			 HashSet<String> hs = new HashSet<String>();
  	 			 ArrayList<String> in = (ArrayList<String>)inMes.getBody();
  	 			 hs = u.FilterStrategy(in);
  	 			 outMes = new Message(MsgType.FILTERSTRATEGYRESULT, hs);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.CFILTERSTRATEGY)){
  	 			//ģ����ѯ���Ա�
  	 			 System.out.println("CFILTERSTRATEGY");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String[]> al = new ArrayList<String[]>();
  	 			 ArrayList<String> in= (ArrayList<String>)inMes.getBody();
  	 			 al = u.CFilterStrategy(in);
  	 			 outMes = new Message(MsgType.CFILTERSTRATEGYRESULT, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }
 		    //�����������ݣ�callinfo)
  	 		 else if(inMes.getType().equals(MsgType.batinsert))
  	 		 {
  	 			 System.out.println("batinsert");
  	 			 ArrayList<String[]> al = (ArrayList<String[]>) inMes.getBody();
  	 			ServiceHeaper.insertCallinfo(al);
  	 			socket_c.close();
  	 		 }
 		    //���Ժ(���ַ�����ռ����)
  	 		 else if(inMes.getType().equals(MsgType.dkyserviceinfo))
  	 		 {
  	 			 System.out.println("dkyserviceinfo");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<Object> al ;
  	 			 al = u.getDKYServiceInfo();
  	 			 outMes = new Message(MsgType.dkyserviceinfo, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }//���з���״̬
  	 		 else if(inMes.getType().equals(MsgType.dkyservicelist))
  	 		 {
  	 			 System.out.println("dkyservicelist");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String[]> al ;
  	 			 al = u.getDKYServiceList();
  	 			 outMes = new Message(MsgType.dkyservicelist, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }//ͳ�Ƶ��ô���
  	 		else if(inMes.getType().equals(MsgType.ServiceCallStatistics))
 	 		 {
 	 			 System.out.println("ServiceCallStatistics");
 	 			 String param = (String) inMes.getBody();
 	 			 UseUserTable u = new UseUserTable();
 	 			 ArrayList<String[]> al ;
 	 			 al = u.ServiceCallStatistics(param);
 	 			 outMes = new Message(MsgType.ServiceCallStatistics, al);
 	 			 
 	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
 		        oos.writeObject(outMes);
 	 		    oos.flush();
 	 		    socket_c.close();
 	 		 }//ͳ�Ƶ�����ʱ
  	 		else if(inMes.getType().equals(MsgType.ServiceDelayStatistics))
	 		 {
	 			 System.out.println("ServiceDelayStatistics");
	 			
	 			 UseUserTable u = new UseUserTable();
	 			ArrayList<String[]> al ;
	 			 al = u.ServiceDelayStatistics();
	 			 outMes = new Message(MsgType.ServiceDelayStatistics, al);
	 			 
	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
	 		    socket_c.close();
	 		 }
 		    //10.15
  	 		else if(inMes.getType() == MsgType.AddServiceComp)
			{
				ArrayList<Object> obj = (ArrayList<Object>)inMes.getBody();
				String serviceId = (String) obj.get(0);
				
				//�������ݿ��е�interface������ID
				System.out.println("�������ݿ��е�interface������ID");
				DBConnectionManager db = DBConnectionManager.getInstance();
				db.getConnection();
				String[] param = {serviceId};
				String sql = "select ServiceName,InterfaceName from interface where ServiceID=?";
				ResultSet rs = db.query(sql,param);
				String serviceName = null,
						interfaceName = null;
//						serviceType = null;
				Service_Interface si = new Service_Interface();
				
				while(rs.next()){
					serviceName = rs.getString(1);
					interfaceName = rs.getString(2);
//					serviceType = rs.getString(3);
					//"web service"��Ҫ���õ�
					boolean flag = si.generateClass(serviceName, interfaceName, "web service");
					//���ɶ�Ӧ��class�ļ���������Ľӿڵ�����Ϣ���interfacecall��Ϣ���
					if(flag == true){
						sql = "insert into interfacecall values(?,?,?)";
						param = new String[]{serviceName,interfaceName,"<component doc:name=\"Java\" class=\""+serviceName+"_"+interfaceName+"\"/>"};
						db.update(sql, param);
					}
				}
				
				//�ٷ�����Ϣ���������������ϣ���java�������ŵ�Mule.jar��
				System.out.println("�ٷ�����Ϣ���������������ϣ���serviceName/src�µ�java�������ŵ�Mule.jar��");
				UseUserTable u = new UseUserTable();
				ArrayList<String> al = u.getMuleContainerIPs();
				for(String ip:al)
	 		    	{
						System.out.println("send file");
						File f = new File(serviceName+File.separator+"src");
		 		   		if(!f.exists())
		 		   		{
		 		   			System.out.println("file not exist");
		 		   			System.exit(-1);
		 		   		}
		 		   		System.out.println(f.getName());
		 		   		System.out.println(f.length());
		 		   		ArrayList<String> al1 = new ArrayList<String>();
		 	
		 		   		al1.add(f.getName());
		 		   		al1.add(String.valueOf(f.length()));
	 		   		
	 		   		
	 		    		Socket socket = new Socket(ip,6000);
	 		    		Message outMes1 = new Message(MsgType.sendConverFile,al1);
	 		    		ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
	 		    		oos1.writeObject(outMes1);
	 		    		oos1.flush();
	 		    	
	 		    		
	 		   		
	 		   		
	 		   		
	 		   		//�����ļ�
	 		   		OutputStream os = socket.getOutputStream();
	 		   		FileInputStream fis = new FileInputStream(f);

	 		   		// ��������С
	 		   		int bufferSize = 8192;
	 		   		// ������
	 		   		byte[] buf = new byte[bufferSize];
	 		   		// �����ļ�
	 		   		while (true) {
	 		   			int read = 0;
	 		   			if (fis != null) {
	 		   				read = fis.read(buf);
	 		   				
	 		   			}
	 		   		if (read == -1) {
	 		   				break;
	 		   		}
	 		   			os.write(buf, 0, read);
	 		   			
	 		   		}
	 		   		os.flush();
	 		   		socket.close();
	 		    		
	 		    		
	 		    		//��������
	 		    		socket.close();
	 		    		
	 		    	}
				
				socket_c.close();
			}
			else if(inMes.getType() == MsgType.AddTransformer)
			{
				//����Transformer��Ӧ��java�ļ�,transformer/src��

	 			 UseUserTable u = new UseUserTable();
				System.out.println("����Transformer��Ӧ��java�ļ�");
				ArrayList<Object> obj = (ArrayList<Object>)inMes.getBody();
				String filename = (String) obj.get(0);
				String length = (String) obj.get(1);
				File dir = new File(System.getProperty("user.dir")+File.separator+"Transformer");
				if(!dir.exists())
				{
					dir.mkdir();
				}
				String classPath = System.getProperty("user.dir")+File.separator+"Transformer"+File.separator+"bin";
				dir = new File(classPath);
				if(!dir.exists())
				{
					dir.mkdir();
				}
				
				saveFile(filename,length,classPath);

				//�ٷ�����Ϣ���������������ϣ���java�������ŵ�Mule.jar��
				System.out.println("Ȼ����Ϣ���͵��������������ϣ������Ž�Mule.jar��");
				//���������Ϣ���õ�����Mule�����������ip��Ȼ��Transformer���java�ļ��������������ϣ���������Mule.jar
				//���Կ�����������б��룬Ȼ�󽫱�����.class�ļ�����
				ArrayList<String> al = u.getMuleContainerIPs();
				for(String ip:al)
	 		    	{
						File f = new File(classPath+File.separator+filename);
		 		   		if(!f.exists())
		 		   		{
		 		   			System.out.println("file not exist");
		 		   			System.exit(-1);
		 		   		}
		 		   		System.out.println(f.getName());
		 		   		System.out.println(f.length());
		 		   		ArrayList<String> al1 = new ArrayList<String>();
		 	
		 		   		al1.add(f.getName());
		 		   		al1.add(String.valueOf(f.length()));
	 		   		
	 		   		
	 		    		Socket socket = new Socket(ip,6000);
	 		    		Message outMes1 = new Message(MsgType.sendConverFile,al1);
	 		    		ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
	 		    		oos1.writeObject(outMes1);
	 		    		oos1.flush();
	 		    	
	 		    		
	 		   		
	 		   		
	 		   		
	 		   		//�����ļ�
	 		   		OutputStream os = socket.getOutputStream();
	 		   		FileInputStream fis = new FileInputStream(f);

	 		   		// ��������С
	 		   		int bufferSize = 8192;
	 		   		// ������
	 		   		byte[] buf = new byte[bufferSize];
	 		   		// �����ļ�
	 		   		while (true) {
	 		   			int read = 0;
	 		   			if (fis != null) {
	 		   				read = fis.read(buf);
	 		   				
	 		   			}
	 		   		if (read == -1) {
	 		   				break;
	 		   		}
	 		   			os.write(buf, 0, read);
	 		   			
	 		   		}
	 		   		os.flush();
	 		   		socket.close();
	 		    		
	 		    		
	 		    		//��������
	 		    		socket.close();
	 		    		
	 		    	}
				socket_c.close();
			}
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     //8.3
  	 		 else if(inMes.getType().equals(MsgType.LOGSIZE))
  	 		 {
  	 			 //log ��Ĵ�С
  	 			 System.out.println("LOGSIZE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 String al = null;
  	 			 al = u.getLogSize();
  	 			 outMes = new Message(MsgType.LOGSIZE, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }
  	 		else if(inMes.getType().equals(MsgType.FILTERLOGSIZE))
 	 		 {
 	 			 //ɸѡ��log��С
  	 			System.out.println("FILTERLOGSIZE");
	 			 UseUserTable u = new UseUserTable();
	 			 ArrayList<String> body = (ArrayList<String>) inMes.getBody();
	 			 String al = null;
	 			 al = u.getFilterLogSize(body);
	 			 outMes = new Message(MsgType.FILTERLOGSIZE, al);
	 			 
	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
	 		    socket_c.close();
 	 		 }
  	 		else if(inMes.getType().equals(MsgType.FILTERWARN)){
  	 			//ģ����ѯ������Ϣ��
  	 			 System.out.println("FILTERWARN");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String[]> al = new ArrayList<String[]>();
  	 			 ArrayList<String> in= (ArrayList<String>)inMes.getBody();
  	 			 al = u.CFilterLog(in);
  	 			 outMes = new Message(MsgType.FILTERWARN, al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }
				else if(inMes.getType().equals(MsgType.serviceCallInfoRequest)){
					System.out.println("serviceCallInfoRequest");
					UseUserTable utable = new UseUserTable();
					Object m=inMes.getBody();
					if (m instanceof String[]){
						String[] mInfo = (String[])inMes.getBody();
						int[] res = utable.monGetServiceCallInfo(mInfo);
						outMes = new Message(MsgType.serviceCallInfo, res);
                	
						ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
						os.writeObject(outMes);
						os.flush();
						socket_c.close();
					}
					else{
						String mInfo = (String)inMes.getBody();
						int[] res = utable.monGetServiceCallInfos(mInfo);
						System.out.println("��ʼ֪��");
						outMes = new Message(MsgType.serviceCallInfo, res);
						for(int i=0;i<10;i++){
							System.out.println(res[i]);
						}
						ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
						os.writeObject(outMes);
						os.flush();
						socket_c.close();
					}
				}
				else if(inMes.getType().equals(MsgType.serviceRunTimeRequest)){
					System.out.println("serviceRunTimeRequest");
					UseUserTable utable = new UseUserTable();
					Object m=inMes.getBody();
					if(m instanceof String[])
					{
						String[] mInfo = (String[])inMes.getBody();
						ArrayList<Object> res = utable.monServiceRunTime(mInfo);
						outMes = new Message(MsgType.serviceRunTime, res);
                	
						ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
						os.writeObject(outMes);
						os.flush();
						socket_c.close();
					}
					else{
						String mInfo = (String)inMes.getBody();
						ArrayList<Object> res = utable.monServiceRunTimes(mInfo);
						outMes = new Message(MsgType.serviceRunTime, res);
                	
						ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
						os.writeObject(outMes);
						os.flush();
						socket_c.close();
					}
				}
				else if(inMes.getType().equals(MsgType.activeServiceRequest)){
					System.out.println("activeServiceRequest");

					UseUserTable utable = new UseUserTable();
					String[] mInfo = (String[])inMes.getBody();
					ArrayList<String[]> res = utable.monActiveService(mInfo);
					outMes = new Message(MsgType.activeService, res);
                	
                	ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
      		        os.writeObject(outMes);
      	 		    os.flush();
      	 		    socket_c.close();
				}
				else if(inMes.getType().equals(MsgType.singleServiceCallTimesRequest)){
					System.out.println("singleServiceCallTimesRequest");
					UseUserTable utable = new UseUserTable();
					String[] mInfo = (String[])inMes.getBody();
					int[] res = utable.monGetSingleServiceCallInfo(mInfo);
					outMes = new Message(MsgType.singleServiceCallTimes, res);
                	
                	ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
      		        os.writeObject(outMes);
      	 		    os.flush();
      	 		    socket_c.close();
				}
				//����ƽ����Ӧʱ��
				else if(inMes.getType().equals(MsgType.singleServiceRunTimeRequest)){
					System.out.println("singleServiceRunTimeRequest");
					UseUserTable utable = new UseUserTable();
					String[] mInfo = (String[])inMes.getBody();
					double[] res = utable.monSingleServiceRunTime(mInfo);
					outMes = new Message(MsgType.singleServiceRunTime, res);
                	
                	ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
      		        os.writeObject(outMes);
      	 		    os.flush();
      	 		    socket_c.close();
				}
				else if(inMes.getType().equals(MsgType.serviceCopyRequest)){
					System.out.println("serviceCopyRequest");
					UseUserTable utable = new UseUserTable();
					String mInfo = (String)inMes.getBody();
					ArrayList<String[]> res = utable.monGetServiceCopy(mInfo);
					outMes = new Message(MsgType.serviceCopy, res);
                	
                	ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
      		        os.writeObject(outMes);
      	 		    os.flush();
      	 		    socket_c.close();
				}
				else if(inMes.getType().equals(MsgType.serviceDetailInfoWithCopyRequest)){
					System.out.println("serviceDetailInfoWithCopyRequest");
					UseUserTable utable = new UseUserTable();
					ArrayList<String[]> res = utable.monGetAllServiceInfoAndCopyInfo();
					outMes = new Message(MsgType.serviceDetailInfoWithCopy, res);
                	
                	ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
      		        os.writeObject(outMes);
      	 		    os.flush();
      	 		    socket_c.close();
				}
				else if(inMes.getType().equals(MsgType.nodeHistoryCPURAMRequest)){
					System.out.println("nodeHistoryCPURAMRequest");					
					UseUserTable utable = new UseUserTable();
					String mInfo = (String)inMes.getBody();
					ArrayList<String[]> res = utable.monGetNodeHistoryCPURAM(mInfo);
					outMes = new Message(MsgType.nodeHistoryCPURAM, res);
                	
                	ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
      		        os.writeObject(outMes);
      	 		    os.flush();
      	 		    socket_c.close();
				}
				else if(inMes.getType().equals(MsgType.alarmListRequest)){
					System.out.println("alarmListRequest");
					UseUserTable utable = new UseUserTable();
					ArrayList<String[]> res = utable.ListAlarmInfo();
					outMes = new Message(MsgType.alarmListResult, res);
                	
                	ObjectOutputStream os=new ObjectOutputStream(socket_c.getOutputStream());
      		        os.writeObject(outMes);
      	 		    os.flush();
      	 		    socket_c.close();
				}
 		     else if(inMes.getType().equals(MsgType.AllServiceStateRequest)){
  		    	//���м������������з���״̬��Ϣ
  		    	System.out.println("AllServiceStateRequest");
  		    	UseUserTable u = new UseUserTable();
 	      		ArrayList<String[]> list = new ArrayList<String[]>();
 	      		list=u.GetServiceStatus();
// 	      		for(String[] a :list){
// 	      			for(String aa :a){
// 	      				System.out.print(aa+" ");
// 	      			}
// 	      			System.out.println();
// 	      		}
 	      		outMes = new Message(MsgType.AllServiceState, list);
 	      	
 	      		ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close(); 
  		    }else if(inMes.getType().equals(MsgType.AllHostRequest)){
  		    	//���м�������������������IP
  		    	System.out.println("AllHostRequest");
  		    	UseUserTable u = new UseUserTable();
 	      		ArrayList<String> list = new ArrayList<String>();
 	      		list=u.GetIP();
 	      		outMes = new Message(MsgType.AllHost, list);
 	      		
 	      		ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }
  		    else if(inMes.getType().equals(MsgType.AlarmStorageRequest)){
  		    	//���м���������洢������Ϣ
  		    	System.out.println("AlarmStorageRequest");
  		    	UseUserTable u = new UseUserTable();
 	      		ArrayList<String> list = new ArrayList<String>();
 	      		list = (ArrayList<String>) inMes.getBody();
 	      		Boolean flag = false;
 	      		flag = u.InsertLog(list);
 	      		outMes = new Message(MsgType.AlarmStorageResult, flag);
 	      		
 	      		ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }
  		  else if(inMes.getType().equals(MsgType.handleAlarmRequest)){
		    	//���м��������޸�
		    	System.out.println("handleAlarmRequest");
		    	UseUserTable u = new UseUserTable();
	      		ArrayList<String> list = new ArrayList<String>();
	      		list = (ArrayList<String>) inMes.getBody();
	      		Boolean flag = false;
	      		flag = u.changeLogStatus(list);
	      		outMes = new Message(MsgType.handleAlarmResult, flag);
	      		
	      		ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
	 		    socket_c.close();
		    }
  		    else if(inMes.getType().equals(MsgType.AlarmListRequest)){
  		    	//���м���������չʾ������Ϣ
  		    	System.out.println("AlarmListRequest");
  		    	UseUserTable u = new UseUserTable();
 	      		ArrayList<String[]> list = new ArrayList<String[]>();
 	      		list = u.ListAlarmInfo();
 	      		outMes = new Message(MsgType.AlarmListResult, list);
 	      		
 	      		ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }else if(inMes.getType().equals(MsgType.ServiceInvokeInfoRequest)){
  		    	//���м���������չʾ���������Ϣ
  		    	System.out.println("ServiceInvokeInfoRequest");
  		    	UseUserTable u = new UseUserTable();
  		    	ArrayList<String[]> al = new ArrayList<String[]>();
  		    	al = u.ListCallInfo();
  		    	outMes = new Message(MsgType.ServiceInvokeInfoResult, al);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }
  		    else if(inMes.getType().equals(MsgType.NODESTATUS))
  		    {
  		    	System.out.println("NODESTATUS");
  		    	ArrayList<String> body = (ArrayList<String>) inMes.getBody();
  		    	UseUserTable u = new UseUserTable();
  		    	boolean flag = u.nodeExist(body.get(0));
  		    	if(flag)
  		    	{
  		    		Message mes = null;
  		    		if(body.get(1).equals("disconnect"))
  		    		{
  		    			mes = new Message(MsgType.STOPRUNNING,"");
  		    		}else if(body.get(1).equals("connect"))
  		    		{
  		    			mes = new Message(MsgType.STARTRUNNING,"");
  		    		}
  		    		Socket socket1 = new Socket(body.get(0),8888);
  		    		ObjectOutputStream oos = new ObjectOutputStream(socket1.getOutputStream());
  		    		oos.writeObject(mes);
  		    		oos.flush();
  		    		System.out.println("��������ͣ��Ϣ��"+body.get(0)+"����"+body.get(1));
  		    		socket1.close();
  		    	}
  		    	
  		    	outMes = new Message(MsgType.NODESTATUS, flag?"true":"false");
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  		    }
  		  else if(inMes.getType().equals(MsgType.NODEEXIST))
		    {
		    	System.out.println("NODEEXIST");
		    	String body = (String) inMes.getBody();
		    	UseUserTable u = new UseUserTable();
		    	boolean flag = u.nodeExist(body);
		    	
		    	outMes = new Message(MsgType.NODEEXIST, flag?"true":"false");
		    	
		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
	 		    socket_c.close();
		    }
  		  else if (inMes.getType().equals(MsgType.SIZENUM))
  		  {
  			System.out.println("SIZENUM");
  			
	    	String body = (String) inMes.getBody();
	    	UseUserTable u = new UseUserTable();
	    	int flag = u.getSizeNum(body);
	    	
	    	outMes = new Message(MsgType.SIZENUM, String.valueOf(flag));
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
 		    oos.flush();
 		    socket_c.close();
  		  }
  		else if (inMes.getType().equals(MsgType.HOSTSIZE))
		  {
			System.out.println("HOSTSIZE");
			
	    	String body = (String) inMes.getBody();
	    	UseUserTable u = new UseUserTable();
	    	int flag = u.getHostSize(body);
	    	
	    	outMes = new Message(MsgType.HOSTSIZE, String.valueOf(flag));
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
		    oos.flush();
		    socket_c.close();
		  }
  		else if (inMes.getType().equals(MsgType.FILTERSIZE))
		  {
			System.out.println("FILTERSIZE");
			
	    	ArrayList<String> body = (ArrayList<String>) inMes.getBody();
	    	UseUserTable u = new UseUserTable();
	    	int flag = u.getFilterSize(body);
	    	
	    	outMes = new Message(MsgType.FILTERSIZE, String.valueOf(flag));
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
		    oos.flush();
		    socket_c.close();
		  }
  		else if (inMes.getType().equals(MsgType.FILTERLIST))
		  {
			System.out.println("FILTERLIST");
			
	    	ArrayList<String> body = (ArrayList<String>) inMes.getBody();
	    	UseUserTable u = new UseUserTable();
	    	ArrayList<String[]> al = u.getFilterList(body);
	    	
	    	outMes = new Message(MsgType.FILTERLIST,al);
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
		    oos.flush();
		    socket_c.close();
		  }
  		else if (inMes.getType().equals(MsgType.FILTERLISTSIZE))
		  {
			System.out.println("FILTERLISTSIZE");
			
	    	ArrayList<String> body = (ArrayList<String>) inMes.getBody();
	    	UseUserTable u = new UseUserTable();
	    	long al = u.getFilterListSize(body);
	    	
	    	outMes = new Message(MsgType.FILTERLISTSIZE,String.valueOf(al));
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
		    oos.flush();
		    socket_c.close();
		  }
  		else if (inMes.getType().equals(MsgType.SERVICEMAP))
		  {//�������˿���Ϣ
			System.out.println("SERVICEMAP");
			
	    	
	    	UseUserTable u = new UseUserTable();
	    	HashMap<String,String> hm = u.getServiceMap();
	    	
	    	outMes = new Message(MsgType.SERVICEMAP,hm);
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
		    oos.flush();
		    socket_c.close();
		  }
  		else if (inMes.getType().equals(MsgType.SERVICEADDRESSINFO))
		  {//�������˿���Ϣ
			System.out.println("SERVICEADDRESSINFO");
			
	    	
	    	UseUserTable u = new UseUserTable();
	    	String serviceid = (String) inMes.getBody();
	    	ArrayList<String> al = u.getServiceAddressInfo(serviceid);
	    	outMes = new Message(MsgType.SERVICEADDRESSINFO,al);
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
		    oos.flush();
		    socket_c.close();
		  }
  		else if (inMes.getType().equals(MsgType.SERVICEADAPTERINFO))
		  {//���󽫷���������Ϣ���
			System.out.println("SERVICEADAPTERINFO");
			
	    	
	    	UseUserTable u = new UseUserTable();
	    	ArrayList<String> serviceadapter = (ArrayList<String>) inMes.getBody();
	    	boolean flag = u.addServiceAdapter(serviceadapter);
	    	outMes = new Message(MsgType.SERVICEADAPTERINFO,flag);
	    	
	    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
	        oos.writeObject(outMes);
		    oos.flush();
		    socket_c.close();
		  }
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		    
 		   else if(inMes.getType().equals(MsgType.SERVICEREQUEST)){		    	
		        //���ݿͻ��˵Ĺؼ���ƥ����ʵķ�����
		    	System.out.println("SERVICEREQUEST");
		        String keyword = (String)inMes.getBody();
		        outMes = getMatchedServicesList(keyword);
		        
		       ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
		        
		     }else if(inMes.getType().equals(MsgType.SERVICEINFO)){		    	 
 		        //����ͻ��˷�������Ϣ������ĳ���������Ϣ
 		    	 System.out.println("SERVICEINFO");
 		        String serviceName = (String)inMes.getBody();
 		        outMes = getMatchedServiceInfo(serviceName);
 		        
 		       ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
 		        
 		    }else if(inMes.getType().equals(MsgType.ServiceManagement)){
 		    	//����ͻ�ѡ����������Ҫ��ѯ�Լ������������ķ����б�
 		    	System.out.println("ServiceManagement");
 		    	String userName = (String)inMes.getBody();
 		    	outMes = serviceManage(userName);   	
 		    	
 		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
 		    }else if (inMes.getType().equals(MsgType.ADMINSERVICE)){
 		    	System.out.println("AdminService");
 		    	String userName = (String)inMes.getBody();
 		    	outMes = AdminService(userName);   	
 		    	
 		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
 		    }else if (inMes.getType()==MsgType.GETUSERINFO){                
              //�����û���Ϣ�������룬���ΪArrayList�û���Ϣ            
      		  UseUserTable u = new UseUserTable();
      		  ArrayList<String[]> list = null;
      		  try {
					list = u.getUserInfo();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			   } 
      		  ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
      		  oos.writeObject(list);
				  oos.flush();
      	  
      	   }else if (inMes.getType()==MsgType.DELETEUSERINFO){
      		   //ɾ���û���Ϣ
	        	  UseUserTable u = new UseUserTable();
	        	  String a = (String) inMes.getBody();
	        	  try {
					u.DeleteUserInfo(a);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       }else if (inMes.getType()==MsgType.UPDATEUSERINFO){
	    	 //�����û�Ȩ��
	        	  UseUserTable u = new UseUserTable();
	        	  ArrayList<String> al = (ArrayList) inMes.getBody();
	        	  String name = al.get(0);
	        	  String privilege = al.get(1);
	        	  try {
					u.UpdateUserInfo(name, privilege);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       }else if (inMes.getType()==MsgType.INSERTUSERINFO){
	    	 //�����û���Ϣ����
	        	  UseUserTable u = new UseUserTable();
	        	  ArrayList<String> al = (ArrayList) inMes.getBody();
	        	  String name = al.get(0);
	        	  String code = al.get(1);
	        	  String privilege = al.get(2);
	        	  try {
					u.InsertUserInfo(name, code, privilege);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          }else if (inMes.getType()==MsgType.UPDATEUSERPASSWORD){
	        	  UseUserTable u = new UseUserTable();
	        	  ArrayList<String> al = (ArrayList) inMes.getBody();
	        	  String name = al.get(0);
	        	  String password = al.get(1);
	        	  try {
					u.UpdateUserPassword(name, password);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          }
// 		    ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
// 		    if (!(outMes.getBody().equals(null)))
// 		    {oos.writeObject(outMes);
// 		    oos.flush();
// 		    }
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (ClassNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
 		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
 		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
 		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
      }

	    
    }
     
     
     private Message login(String[] info){
    	 ArrayList<String> flag = new ArrayList<String>();
    	 String name = info[0];
    	 String password = info[1];
    	 //�����û����������ѯ���ݿ⣬�жϸ��û��Ƿ��ǺϷ��û�
    	 UseUserTable u = new UseUserTable();
    	 try {
    		 //����ֵflag��Ȩ�޺���Ƭ�洢·����client����admin
			flag = u.confirmUser(name, password);
//			System.out.println(flag);
			if(flag != null){
				System.out.println("�û�"+name+"��¼");
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
    	 Message mes = new Message(MsgType.LOGIN, flag);
    	 return mes;
     }
     
     public void checkDeployInfo(ArrayList<String> deployInfo) {
		// TODO Auto-generated method stub
    	int flag = -1;
		if(deployInfo.get(2).equalsIgnoreCase("true"))
		{
			flag = 1;
		}
		else if(deployInfo.get(2).equalsIgnoreCase("false"))
		{
			flag = 2;
		}
		if(flag == -1)
		{
			System.out.println("������֤��ʽ����");
			return ;
		}
		String sql = "update duplicate set Flag=1 where ServiceID=? and IPAddress=?";
		String sql2 = "delete from duplicate where ServiceID=? and IPAddress=?";
		DBConnectionManager co = DBConnectionManager.getInstance();
		if(flag == 1)
		{
			//co.update(sql, deployInfo.get(0),deployInfo.get(1));
		}
		else
		{
			co.update(sql2);
		}
	}
	private void publishService2(ArrayList<String> serviceInfo) {
		// TODO Auto-generated method stub
    	 String ipAddress = null;
    	 Message outMes = null;
    	 String serviceID = serviceInfo.get(0);	//������
    	 System.out.println("serviceID��"+serviceID);
	     //�ж���������Ƿ��Ѿ�����
	     boolean flag = isServiceExisted(serviceID);
	     if(flag == true){
	    	 //����÷����Ѿ�����
	    	 ipAddress = null;
	    	 System.out.println("�÷����Ѵ���");
	    	 //outMes = new Message(MsgType.SERVICEDEPLOYRESULT, ipAddress);
	     }else{
	    	 //����÷��񲻴���
	    	 System.out.println("�÷��񲻴��ڣ�������ӷ���");
//	    	 ipAddress = addNewService(serviceInfo);
//	    	 outMes = new Message(MsgType.SERVICEDEPLOYRESULT, ipAddress);
	    	 addService(serviceInfo);
	     }
    	    	 
    	 
	}
     private void addService(ArrayList<String> serviceInfo)
     {
    	 UseUserTable u  = new UseUserTable();
    	 String ips = serviceInfo.get(serviceInfo.size()-1);
    	//������Ϣ���
    	 try {
			u.addNewServiceItem(serviceInfo, "");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 String serviceid = serviceInfo.get(0);
    	 String wsdl ="nothing";
    	 String type =serviceInfo.get(7);
    	 String status = "start";
    	//���븱����
    	 String[] ip= ips.split(";");
    	 DBConnectionManager co = DBConnectionManager.getInstance();
    	 String sql = "insert into duplicate values(?,?,?,?,?,2)";
    	 for(int i=0;i<ip.length;i++)
    	 {
    		 co.update(sql, serviceid,ip[i],wsdl,type,status);
    	 }
    	 System.out.println("duplicate �����¼�ɹ�!");
     }
	private Message getMatchedServicesList(String keyword) throws IOException, ClassNotFoundException{
          	//1.���յ��ͻ�������������з������ֵ���Ϣ����Ҫ����Ϣ�����ַ��ظ��ͻ�����
          	
          	//2.�����ݿ��ѯ���еķ�����
          	UseUserTable u = new UseUserTable();
      		ArrayList<String> allServiceNames = null;
      		//���ȵõ����еķ������б�
			try {
				allServiceNames = u.clientGetServiceList();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//3.����keyword�ҳ�ƥ��ķ�����
      		ArrayList<String[]> matchService = new ArrayList<String[]>();
      		String serviceName = null;
			for(int i=0;i<allServiceNames.size();i++){
				if(allServiceNames.get(i).toString().toLowerCase().contains(keyword.toString().toLowerCase())){
					serviceName = allServiceNames.get(i);
					try {
						matchService.add(u.clientGetServiceList2(serviceName));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			Message mes = new Message(MsgType.MATCHSERVICEs, matchService);	
			return mes;
		}
     
     private Message getMatchedServiceInfo(String serviceName) throws IOException, ClassNotFoundException{
        //���ͻ������ĳ�������Ϣ���ظ��ͻ���
//        String _serviceName = serviceName;
//      System.out.println(serviceName);
         	
        //�����ݿ��ѯ�ͻ�����Ҫ�����wsdl��ַ
        UseUserTable u = new UseUserTable();
     	ArrayList<String> serviceInfo = null;
			try {
				serviceInfo = u.getClientReqServiceWSDL(serviceName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
//         	
         Message mes=new Message(MsgType.SERVICEINFO,serviceInfo);
         return mes;
     }
     /**��������
     */
     private Message publishService(ArrayList<String> serviceInfo) throws UnsupportedEncodingException{
    	 String ipAddress = null;
    	 Message outMes = null;
    	 String serviceID = serviceInfo.get(0);	//������
    	 System.out.println("serviceID��"+serviceID);
	     //�ж���������Ƿ��Ѿ�����
	     boolean flag = isServiceExisted(serviceID);
	     if(flag == true){
	    	 //����÷����Ѿ�����
	    	 ipAddress = null;
	    	 System.out.println("�÷����Ѵ���");
	    	 outMes = new Message(MsgType.SERVICEDEPLOYRESULT, "service exist!");
	     }else{
	    	 //����÷��񲻴���
	    	 System.out.println("�÷��񲻴��ڣ�������ӷ���");
	    	 ipAddress = addNewService(serviceInfo);
	    	 outMes = new Message(MsgType.SERVICEDEPLOYRESULT, ipAddress!=null?ipAddress:"�����ڿ��ô��������ڵ㡣");
	    	 
	     }
    	    	 
    	 return outMes;
     }
    
     /**
      * ��������
      * @param flowInfo
      * @return
      */
     private Message deployFlow(ArrayList<String> flowInfo){
    	 String ipAddress = null;
    	 Message outMes = null;
    	 String flowName = flowInfo.get(0);	//������
    	 String serviceFlow = flowInfo.get(4);
    	 System.out.println("flowName��"+flowName);
    	 ArrayList<String> al = new ArrayList<String>();
	     //�ж���������Ƿ��Ѿ�����
	     boolean flag = isFlowExisted(flowName);
	     if(flag == true){
	    	 //����������Ѿ�����
	    	 ipAddress = null;
	    	 al.add(flowName);
	    	 al.add(ipAddress);
	    	 al.add(serviceFlow);
	    	 System.out.println("�������Ѵ���");
	    	 outMes = new Message(MsgType.FLOWDEPLOYRESULT, al);
	     }else{
	    	 //��������̲�����
	    	 System.out.println("�����̲����ڣ������������");
	    	 ipAddress = addNewFlow(flowInfo);
	    	 al.add(flowName);
	    	 al.add(ipAddress);
	    	 al.add(serviceFlow);
	    	 outMes = new Message(MsgType.FLOWDEPLOYRESULT, al);
	    	 
	     }
    	    	 
    	 return outMes;
     }
     /**�жϸ÷����Ƿ��Ѿ�����*/
//     private boolean isServiceExisted(String serviceName){   	
//    	 UseUserTable u = new UseUserTable();
//    	 ArrayList<String> allServiceNames = null;
//    	 
//    	 try {
//			allServiceNames = u.clientGetServiceList();
//    	 } catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//    	 }
//    	    	
//    	 boolean flag = allServiceNames.contains(serviceName);    	 
//    	 System.out.println(flag);
//    	 return flag;
//     }
     private boolean isServiceExisted(String serviceID){   	
    	 UseUserTable u = new UseUserTable();
    	 ArrayList<String> allServiceNames = null;
    	 
    	 try {
			allServiceNames = u.clientGetServiceList();
    	 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	 }
    	    	
    	 boolean flag = allServiceNames.contains(serviceID);    	 
    	 System.out.println(flag);
    	 return flag;
     }
     /**�жϸ������Ƿ��Ѿ�����*/
     private boolean isFlowExisted(String flowName){   	
    	 UseUserTable u = new UseUserTable();
    	 ArrayList<String> allFlowNames = null;
    	 
    	 try {
			allFlowNames = u.ListFlowName();
    	 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	 }
    	    	
    	 boolean flag = allFlowNames.contains(flowName);    	 
//    	 System.out.println(flag);
    	 return flag;
     }
     /**�����ݿ�������µķ����¼���������µķ�����䶼��ip��ַ
     * */
     private String addNewService(ArrayList<String> serviceInfo) throws UnsupportedEncodingException{
     	UseUserTable u = new UseUserTable();
     	String ipAddress = null;
     	try {
//     		if (serviceInfo.size()==9) ipAddress = serviceInfo.get(8);
//     		else{
//     		String type = serviceInfo.get(5);
//     		ipAddress = u.getIpAddress(type);
//     		}
     		String type = serviceInfo.get(7);
     		ipAddress = u.getIpAddress(type);
//     		//���·������Ϣ��ӵ�services���Լ�allservernodes���У�ͬʱ�½�һ��������
     		if(serviceInfo.get(7).equalsIgnoreCase("flow"))
     		{
     			
     		}
     		else
     		{
     			u.addNewServiceItem(serviceInfo, ipAddress);
     		}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	return ipAddress;
     }
     /**�����ݿ�������µ����̼�¼���������µ����̷��䶼��ip��ַ*/
     private String addNewFlow(ArrayList<String> flowInfo){
      	UseUserTable u = new UseUserTable();
      	String ipAddress = null;
      	
      	try {
      		if (flowInfo.size()==8) ipAddress = flowInfo.get(7);
      		else{
      		String type = "Flow";
      		ipAddress = u.getIpAddress(type);
      		}
      		//�������̵���Ϣ��ӵ�flow���У����޸�duplicate���ȴ������޸�
 			u.addNewFlowItem(flowInfo);			
 		} catch (SQLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
      	return ipAddress;
      }
     
     /**���������ѯĳ�û��������ķ����б�*/
     private Message serviceManage(String userName){
    	
    	 Message outMes = null;
    	//���û������ķ����б�
 		ArrayList<String[]> pubServicesByUser = new ArrayList<String[]>();
 		
 		UseUserTable u = new UseUserTable();		
 		//��ȡ���з����б�
 		ArrayList<String[]> allServices = null;
 		try {
 			allServices = u.clientGetServiceList1();
 		} catch (SQLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
 		//�����з�����ɾѡƥ��ķ���
 		for(int i=0;i<allServices.size();i++){
 			String[] serviceInfo = allServices.get(i);
 			//�жϸ÷���ķ������Ƿ��ǵ�¼���û�
 			if((userName).equals(serviceInfo[2])){
 				pubServicesByUser.add(serviceInfo);			
 			}
 		}
 		outMes = new Message(MsgType.ServiceManagement, pubServicesByUser);
    	return outMes;
     }
     
     /**
      * ҵ������ת����������
      * @param business
      * @return
      */
     private String transformFlow(String business){
    	 BusinessToServiceFlow b = new BusinessToServiceFlow();
    	 String service = new String();
    	 service = b.flowTransBtoS(business);
    	 
    	 return service;
     }
     
     private Message AdminService(String userName){
    	
    	 Message outMes = null;
    	//�����û������ķ����б�
// 		ArrayList<String[]> pubServices = new ArrayList<String[]>();
 		
 		UseUserTable u = new UseUserTable();		
 		//��ȡ���з����б�
 		ArrayList<String[]> allServices = null;
 		try {
 			allServices = u.clientGetServiceList1();
 			
 		} catch (SQLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 	
 		outMes = new Message(MsgType.ADMINSERVICE, allServices);
    	return outMes;
     }
     
     /**ɾ�����񣬷���ĳ�������и�����IP��ַ*/
     private Message deleteService(String serviceName){
    	 Message outMes = null;
    	 UseUserTable u = new UseUserTable();
    	 ArrayList<String> ipAddress = new ArrayList<String>();
    	 
    	 try {
    		//��ȡĳ����������и�����ŵ�IP��ַ
			ipAddress = u.getAllIpAddressOfService(serviceName);//servicetype+filename+ips
			//ɾ���÷���ļ�¼���޸ĸ����� ������������
			String serviceType = ipAddress.get(0);

			
			//�����еĸ���ִ��ɾ������ָ��
			if(serviceType.equalsIgnoreCase("restful"))
			{
				ArrayList<String> body = new ArrayList<String>();
				body.add(serviceType);
				body.add(ipAddress.get(1));
				if(ipAddress.size()>2){
					for(int i=2;i<ipAddress.size();i++)
					{
						String uri = u.getRestfulURI(serviceName,ipAddress.get(i));
						u.deleteDuplicateItem(serviceName,ipAddress.get(i));
						body.set(1, ipAddress.get(1)+"&&"+uri);
						
						Socket socket = null;
						try{
							socket = new Socket(ipAddress.get(i),6000);
	  	 		    		Message outMes1 = new Message(MsgType.DeleteService,body);
	  	 		    		ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
	  	 		    		oos1.writeObject(outMes1);
	  	 		    		oos1.flush();
	  	 		    		
	  	 		    		ObjectInputStream ois1 = new ObjectInputStream(socket.getInputStream());
	  	 		    		Message inMSG = (Message) ois1.readObject();
	  	 		    		//��������
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						finally{
  	 		    		try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
					}
				}
				
			}
			
			u.deleteAllInfoOfService(serviceName,serviceType);
			
			if(serviceType.equalsIgnoreCase("exe"))
			{
				ArrayList<String> body = new ArrayList<String>();
				body.add(serviceType);
				body.add(ipAddress.get(1));
				if(ipAddress.size()>2){
					for(int i=2;i<ipAddress.size();i++)
					{
						Socket socket = null;
						try{
							socket = new Socket(ipAddress.get(i),6000);
	  	 		    		Message outMes1 = new Message(MsgType.DeleteService,body);
	  	 		    		ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
	  	 		    		oos1.writeObject(outMes1);
	  	 		    		oos1.flush();
	  	 		    		
	  	 		    		ObjectInputStream ois1 = new ObjectInputStream(socket.getInputStream());
	  	 		    		Message inMSG = (Message) ois1.readObject();
	  	 		    		//��������
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						finally{
  	 		    		try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
					}
			}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	     	 
    	 outMes = new Message(MsgType.SERVICEDELETERESULT, ipAddress);
    	 return outMes;
     }
    ServiceRegistryNode()
	{
		services = new ArrayList<ServiceInfo>();
		servers = new ArrayList<ServerNodeInfo>();
	}
	

	/** ��ʾע�����ĵ�ǰ״̬*/
	public void showStatus()
	{
		Iterator iter = services.iterator();
		while(iter.hasNext())
		{
			ServiceInfo serviceInfo = (ServiceInfo)iter.next();
			serviceInfo.show();
		}
		iter = servers.iterator();
		while(iter.hasNext())
		{
			ServerNodeInfo serverNode = (ServerNodeInfo)iter.next();
			serverNode.show();
		}
	}
	
	/** ��ӷ���*/
	public void addService(List<ServiceInfo> slist)
	{
		services.addAll(slist);
	}
	
	
	
	/** ���������߳�*/
	public void startListening()
	{
		try {
			new ListenServerNodeThread();	//��������ڵ��̣߳��˿�Ϊ7000
			new ListenClientAgentThread();	//�����ͻ��˴����̣߳��˿�Ϊ5000
			new ListenClientHeart();        //�����������ڵ��������˿�Ϊ7001
			new ScanHashtable().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws InterruptedException, IOException
	{   
//		try {  
//            //�ļ�����·��  
//            PrintStream rs=new PrintStream("D:\\Registry.txt");  
//            System.setOut(rs);  
//        } catch (FileNotFoundException e) {  
//            e.printStackTrace();  
//        }  
		ServiceRegistryNode registryNode = new ServiceRegistryNode();
		registryNode.startListening();
		//System.out.println("Hello");
		
		
//		String busFlowString="";
//		StringBuffer sBuffer=new StringBuffer();
//		File filename = new File("D:\\businessFlow.xml"); // Ҫ��ȡ����·����input.txt�ļ�  
//        InputStreamReader reader;
//        reader = new InputStreamReader(new FileInputStream(filename));
//		BufferedReader br = new BufferedReader(reader);
//        busFlowString = br.readLine();
//        while (busFlowString!=null) {
//			sBuffer.append(busFlowString);
//			sBuffer.append("\n");
//			busFlowString = br.readLine();
//			
//		}
//        reader.close();
//        busFlowString=sBuffer.toString();
//        String a = new String();
//        a = registryNode.transformFlow(busFlowString);
//        System.out.println(a);
		
	}
}
