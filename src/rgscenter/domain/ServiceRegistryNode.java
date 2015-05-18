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
 * 注册中心节点
 * @author park_wh
 *
 */
public class ServiceRegistryNode{
	
	private List<ServiceInfo> services;
	private List<ServerNodeInfo> servers;
	private ArrayList<ResponseThread> responseThreads = new ArrayList<ServiceRegistryNode.ResponseThread>();
	/**副本数*/
	private int serviceAmount = 3;
	private Log log=LogFactory.getLog("ServiceRegistryNode");
	
	
	public static File file = new File("conf/IP.txt");
	
	//8-15新增
	public static Hashtable<String,NodeInfo> hashtable = new Hashtable<String,NodeInfo>();
//	/**注册中心进行副本分配线程*/
//	class DeployServicesCopy extends Thread{
//		public DeployServicesCopy(){
//			
//		}
//	}
	public static String unifiedIP = Config.newInstance().getAttribute("unifiedIP");
	
	/**
	 * 监听代理容器发的心跳包
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
			//心跳监听端口
			serverSocket = new ServerSocket(7001);
			this.start();
		}
		
		public void run()
		{
			System.out.println("开始监听代理容器的心跳包7001...");
			while(flag)
			{
				try {
					socket = serverSocket.accept();
					String ip = socket.getInetAddress().toString().substring(1);
//					System.out.println("有代理容器接入"+socket.getRemoteSocketAddress());
					log.info("有代理容器接入"+socket.getRemoteSocketAddress());
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
	 * 响应代理容器的心跳包
	 */
	class ResponseClientHeart extends Thread
	{
		private boolean flag = true;
		
		private boolean connFlag = false;//用于判断是否是主动断开连接
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
					System.out.println("心跳接入");
					String theip = socket.getInetAddress().toString().substring(1);
					ArrayList<String> theips = ServiceHeaper.getLegalIPs();
					//忽略不合法代理心跳
					if(theips.contains(theip))
					{
						ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
						Message msg = (Message) in.readObject();
						
						ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
						if(msg != null)
						{
							if(msg.getType() ==MsgType.WSDLSET)
						
							{
								System.out.println("消息格式匹配");
								//List<String> body体
//								System.out.println("get heart msg ip:" +theip);
								log.info("get heart msg ip:" +theip);
								out.writeObject(new Message(MsgType.WSDLSET,"have receive heart msg"));
								out.flush();
								ArrayList<Object> al = (ArrayList<Object>) msg.getBody();
								Object[] nodeInfo = (Object[]) al.get(1);
								nodeInfo[0] = socket.getInetAddress().toString().substring(1);
								
								//更新hashtable
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
								//将实时信息加入数据库
								ServiceHeaper.insertNodeRealTimeInfo(nodeInfo);
								
								//更新库中服务状态
								Map<String,String> serstatus = (Map<String, String>) al.get(2);
								ServiceHeaper.updateServicesStatus(serstatus, theip);
								Object[] wsdlset = (Object[]) al.get(0);
								//进行相关操作
							  
						    		for(Object s:wsdlset)
						    		{
						    			System.out.println(s.toString());
						    		}
						    		
						    	if(wsdlset.length==1)
						    	{
						    			//相临两次的心跳没有变化		
						    			System.out.println("服务列表无变化!");			
						    	}
						    	else
						    	{
						    			System.out.println("更新数据库");
						    			String serviceName = null;
						    			String serviceLocation = null;
						    			String serviceid = null;
						    			for(int i=0;i<wsdlset.length;)
						    			{
						    				//解析wsdl地址获取服务所需信息
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
						    					//更新副本表 并在ServerInfo中更新ServiceNum
						    					System.out.println("abc:"+socket.getInetAddress().toString().substring(1));
						    					//服务名bu存在时
						    					if(serviceType != null)
						    						addAService(socket.getInetAddress().toString().substring(1),serviceid,serviceLocation,serviceType);
						    					
						    					i += 3;
						    				}
						    				else if(wsdlset[i].toString().equals("DELETE")){	
						    					//修改数据库
						    					//从副本表中删除记录 ,并更新ServerInfo表中ServiceNum
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
						    				//维持副本因子
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
								//节点不可用 进行相关操作
	//							String ip = socket.getInetAddress().toString().substring(1);
	//							ServiceHeaper.keepSomeServiceCopy(dealUnavailableNode(ip));
							}
						}
						else
						{
							System.out.println("消息格式不匹配");
							flag = false;
							System.out.println("readObject() failed");
							out.writeObject(new Message(MsgType.WSDLSET,"can't get msg heart"));
							//节点不可用 进行相关操作
	//						String ip = socket.getInetAddress().toString().substring(1);
	//						ServiceHeaper.keepSomeServiceCopy(dealUnavailableNode(ip));
						}
						
						System.out.println("心跳处理完毕");
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
////						System.out.println("主机节点不可用");
////						
////						//节点不可用 进行相关操作
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
////						System.out.println("主机成功断开连接");
////						
////						//节点主动断开连接          进行相关操作
////						//在node表中将对应主机的状态设置为disconnect状态并将所有服务也设置为disconnect状态
////						//待补充
////						try {
////							ServiceHeaper.dealwithTheDisConnNode(ip);
////						} catch (SQLException e1) {
////							// TODO Auto-generated catch block
////							e1.printStackTrace();
////						}
////						
////					}
////					
////					//将本线程从responseHeart表中删掉
////						responseHeart.remove(ip);
//					if(socket != null && !socket.isClosed())
//					{
//						try {
//							System.out.println("断开socket");
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
						if(nowTime - value.getLastHeartTime() > 180000)//超时
						{
							value.setStatus("unreach");
						}
						else//正常
						{
							//不用进行任何操作，更新lashtime和noderealtime在心跳中进行
						}
					}
					else if(value.getStatus().equals("disconnect"))
					{
						//
					}
					else//主机不可用
					{
						removeKeys.add(key);
						//暂时关闭
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
	
	//当一个主机节点不可用时，从数据中删除该主机节点的所有服务和服务节点信息
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
	//删除指定ip主机上的某服务，并更新ServerInfo的serviceNum
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
	//指定主机上添加某服务，并更新ServiceInfo的serviceNum
	public static  void addAService(String ip,String name,String location,String serviceType) throws SQLException, UnknownHostException, IOException
	{
		System.out.println("更新副本表和node表");
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
		// 判断该记录是否在duplicate中
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
			System.out.print(name+","+ip+"已经存在！");
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
	
	
	
	
	/** 注册中心监听7000线程*/
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
	    	System.out.println("注册中心服务器开始监听服务节点7000...");
	    	 while (true) {
	             try {
					socket = serverSocket.accept();
					System.out.println("有服务节点接入"+socket.getRemoteSocketAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             responseThreads.add(new ResponseThread(socket, id++));
	         }
	    }
	}
	
	/** 注册中心响应线程*/
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
                // 读取容器代理软件发送来的消息对象
            	
	            	ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
	                
	                // msg中存放的是服务节点发送的服务列表信息
	                //消息体为wsdlset	                
	                Message msg = (Message) is.readObject();
	                Message Mesout = null;
	                
	               if(msg.getType() == MsgType.Host){
	                	//服务容器代理软件请求新添加主机
	                	System.out.println("新添加主机");
	                	ArrayList<String> al = new ArrayList<String>();
	                	al = (ArrayList<String>)msg.getBody();
	                	al.set(0, socket.getInetAddress().toString().substring(1));
	                	//添加到hashtable中
	                	hashtable.put(socket.getInetAddress().toString().substring(1), new NodeInfo());
	                	
	                	UseUserTable u = new UseUserTable();
	                	Boolean flag = u.AddHost(al);
	                	Mesout = new Message(MsgType.HostResult, flag);
	                	
	                	ObjectOutputStream os=new ObjectOutputStream(socket.getOutputStream());
	      		        os.writeObject(Mesout);
	      	 		    os.flush();
	      	 		    
	      	 		    //给监控软件发送变化的主机列表
	      	 		   
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
	                }//8.15新增代理容器请求
	               else if(msg.getType() == MsgType.DisconnectRequest)
	                {
	            	   
	                	
	            	   String ip = socket.getInetAddress().toString().substring(1);
	            	   if(hashtable.containsKey(ip))
	            		   hashtable.get(ip).setStatus("disconnect");
	            	  //设置主机，服务 状态
	            	   try {
							ServiceHeaper.dealwithTheDisConnNode(ip);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	            	   System.out.println("断开请求 "+ip);
	            	   socket.close();
	                }
	               else if(msg.getType() == MsgType.ConnectRequest)
	                {
	                	
	            	   String ip = socket.getInetAddress().toString().substring(1);
	            	   
	            	   //设置主机状态
	            	   if(hashtable.containsKey(ip))
						{
	            		   hashtable.get(ip).setStatus("connect");
		            	   hashtable.get(ip).setLastHeartTime(System.currentTimeMillis());
						}
						else
						{
							hashtable.put(ip, new NodeInfo());
						}
	            	  
	            	   
	            	 //在node表中将其状态设置为connect 并将它所含的每个服务的状态设置为（数据库中绝大多数服务的状态）
	            	   ServiceHeaper.setServiceStatusByIP(ip);
	            	   System.out.println("再次连接请求 "+ip);
	            	   socket.close();
	                }
	               else if(msg.getType() == MsgType.CallService)
					{
	            	   //代理容器请求调用服务
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
							//删除wsdl对应的服务副本
							deleteAService(body.get(0));
							//再次分配wsdl地址
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
	            	   //代理容器请求调用服务
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
//							//删除wsdl对应的服务副本
//							deleteAService(body.get(0));
//							//再次分配wsdl地址
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
						//服务容器代理软件发来服务调用信息
						System.out.println("CallInfo");
						ArrayList<String> al = new ArrayList<String>();
						al = (ArrayList<String>)msg.getBody();
						UseUserTable u = new UseUserTable();
						u.AddCallInfo(al);
					}
	               else if(msg.getType().equals(MsgType.CallInfo1)){
						//服务容器代理软件发来服务调用信息
						System.out.println("CallInfo1");
						ArrayList<String> al = new ArrayList<String>();
						al = (ArrayList<String>)msg.getBody();
						UseUserTable u = new UseUserTable();
						u.AddCallInfo1(al);
					}
	               else if(msg.getType().equals(MsgType.DependInfo)){
						//服务容器代理软件发来依赖IP查找请求
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

	                // 调用外部类的函数
//	                addService(wsdlset.GetServiceInfosFromLocations());
//	                System.out.println("注册中心最新状态：");
//	                // 调用外部类的函数
//	                showStatus();
//	                
//	             // 向客户端1发出移入服务文件的命令
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
        
        /**进行副本分配*/
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
	        			//如果副本数小于3, 获取负载最小的节点
	        			String copyIPAddress = u.getMiniumIPAddress(serviceName);	//需要拷贝的服务节点的地址
	        			String copiedIPAddress = u.getIPFromCopyTable(serviceName);	//从副本表中获取该服务所在的IP地址
	        			
	        			//向copyIPAddress服务节点发送消息，让其从copiedIPAdress处拷贝服务文件
	        			
	        		}
	        	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	
        }
        
        /**
    	 * 发送来delete心跳的服务节点从其他节点拷贝服务
    	 * @param wsdllocation 发送来delete心跳的服务节点地址
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
    	 * 让其他服务节点从本节点来拷贝服务,是注册中心向其他节点发出的拷贝命令
    	 * @param ip 本节点的ip
    	 * @param serName 要拷贝的服务名
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
    	 * 获取心跳服务列表
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
    			//相临两次的心跳没有变化		
    			System.out.println("服务列表无变化!");			
    		}
    		else{
    			System.out.println("更新数据库");
    			for(int i=0;i<wsdlLocation.size();i+=2){
    				if(wsdlLocation.get(i).equals("ADD")){
    					//更新副本表
    					u.insert_Table(wsdlLocation.get(i+1));   					
    					//更新allServerNodes表
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
 					
    					//此处应该向其他服务节点发出拷贝此服务的命令
//    					if(u.getServiceAmount(wsdlLocation.get(i+1)) < serviceAmount){   						
//    						ArrayList<String> iplist = u.getOtherServernodes(wsdlLocation.get(i+1));
//    						for(int j=0;j<iplist.size();j++){
//    							//向其他节点发出让其从此处拷贝副本的命令
//    							copyFromThisNode(ipadress,serName);
//    						} 						
//    					}
    				}
    				else if(wsdlLocation.get(i).equals("DELETE")){	
    					//修改数据库
    					//从副本表中删除记录 ,如果副本表中记录数为0，则删除该副本表,并删除services中的记录
    					u.deleteServiceFromCopy(wsdlLocation.get(i+1));
//    					u.updateServiceFromServices(wsdlLocation.get(i+1));	
    					
//    					if(u.getServiceAmount(wsdlLocation.get(i+1))<3){
//    						//发出从其他节点拷贝副本的命令
//    						copyFromOtherNode(wsdlLocation.get(i+1));
//    					}
    				}
    			}
    		}
    	}
    	
    }	
    	
       
	
    /** 注册中心监听 客户代理 线程*/
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
   	    	System.out.println("注册中心服务器开始监听客户端代理5000...");
			javax.swing.JOptionPane.showMessageDialog(null,"注册中心开始监听！");
   	    	 while (true) {   	    		
   	             try {
   					socket_c = serverSocket_c.accept();
   					System.out.println("有客户端接入"+socket_c.getRemoteSocketAddress());
   				} catch (IOException e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}  	             
   	             new ResponseClientAgent(socket_c);
   	         }
   	    }
   	}
    
    
    
   	/** 注册中心响应 客户代理 线程*/
    class ResponseClientAgent extends Thread {
     private Socket socket_c;
     
     public ResponseClientAgent(Socket socket_c) {
         this.socket_c = socket_c;
         start();
     }
     public void saveFile(String name,String length,String dir) throws IOException{
  		//判断文件是否存在，存在就删除掉
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
  		
  		
  	    System.out.println("文件的长度为:" + len + "  B");
  		System.out.println("开始接收文件!");

  		// 获取文件
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
  			System.out.println("文件接收了" + (passedlen * 100 / len) + "%");
  			
  			fos.write(buf, 0, read);
  			
  			if((passedlen * 100 / len) == 100){
  				f = false;
  			}
  		}
  		
  		System.out.println("接收完成，文件存为" + dir+File.separator+name);
  		fos.close();//关闭文件流
  	}
     public void run(){  
 		try {
 //			BufferedReader ois = new BufferedReader(new InputStreamReader(socket_c.getInputStream(),"GB2312"));
     		ObjectInputStream ois = new ObjectInputStream(socket_c.getInputStream());
 			Message inMes = (Message)ois.readObject();
 			Message outMes = null;
 		    if(inMes.getType().equals(MsgType.LOGIN)){		    	
 		        //如果客户端发来的消息是客户登录请求
 		    	System.out.println("LOGIN");
 		        String[] info = (String[]) inMes.getBody();
 		        outMes = login(info);
 		        
 		       ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
 		        oos.writeObject(outMes);
 	 		    oos.flush();
 	 		    socket_c.close();
 		        
 		    }else if(inMes.getType().equals(MsgType.SERVICELIST)){//已经测试
 		        //客户端发来的请求是显示服务列表
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
 		   else if(inMes.getType().equals(MsgType.HOSTINFO)){//已经测试
		        //客户端发来的请求是显示指定ip实时的主机信息
		    	System.out.println("HOSTINFO");
		    	String ip = (String) inMes.getBody();
		    	outMes = new Message(MsgType.HOSTINFO, ServiceHeaper.getHostCPUInfo(ip));
		    	
		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
 		        oos.writeObject(outMes);
 	 		    oos.flush();
 	 		    socket_c.close();
		   
		    }
 		  else if(inMes.getType().equals(MsgType.HOSTHARDDISK)){//已经测试
		        //客户端发来的请求是显示指定ip实时的主机信息
		    	System.out.println("HOSTHARDDISK");
		    	String ip = (String) inMes.getBody();
		    	outMes = new Message(MsgType.HOSTHARDDISK, ServiceHeaper.getHostDiskInfo(ip));
		    	
		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
	 		    socket_c.close();
		   
		    }
 		   else if(inMes.getType().equals(MsgType.UNDEPLOYEDSERVICE)){//已经测试
		        //客户端发来的请求是显示服务列表
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
 		    else if(inMes.getType().equals(MsgType.HOSTLIST)){//已经测试
 		    	 //客户端发来的请求是显示指所有主机列表和CPU、内存、硬盘、服务数量等信息，servlet根据容器名进行筛选
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
 		     }else if(inMes.getType().equals(MsgType.SETCOPYNUM)){//已测试（Flow类型未测试）未处理副本分配
 		    	 //客户端发来的请求是修改服务副本因子
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
 		     } else if(inMes.getType().equals(MsgType.SERVICECOPYLIST)){//已测试
  		        //客户端请求带副本的非流程服务列表
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
  		    	 //客户端请求带副本的流程服务列表
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
  		    	//客户端请求修改服务状态
  		    	System.out.println("SERVICESTATUS");
  		    	ArrayList<String> al = (ArrayList<String>)inMes.getBody();
  		    	UseUserTable u = new UseUserTable();
  		    	//服务文件名称
  		    	String servicename = ServiceHeaper.getFileNameByServiceID(al.get(0));
  		    	//设置副本状态并放回所有的副本ip
  		    	ArrayList<String> list = u.SetServiceStatus(al);
  		    	outMes = new Message(MsgType.SERVICESTATUSRESULT, list);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		    
  	 		    //对exe特殊处理
  	 		    String type = ServiceHeaper.getServiceTypeByServiceID(al.get(0));
  	 		    System.out.println("serviceType:"+type);
  	 		    System.out.println("filename:"+servicename);
  	 		    if(type.equalsIgnoreCase("exe"))
  	 		    {
  	 		    	//给每个ip发送服务启停指令
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
  	 		    		//后续处理
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
  	 		    	{	//找到每个副本对应的rui
  	 		    		String uri = u.getRestfulURI(al.get(0), list.get(i));
  	 		    		body.set(0, uri);
  	 		    		
  	 		    		
  	 		    		Socket socket = new Socket(list.get(i),6000);
  	 		    		Message outMes1 = new Message(MsgType.changeServiceStatus,body);
  	 		    		ObjectOutputStream oos1 = new ObjectOutputStream(socket.getOutputStream());
  	 		    		oos1.writeObject(outMes1);
  	 		    		oos1.flush();
  	 		    		
  	 		    		ObjectInputStream ois1 = new ObjectInputStream(socket.getInputStream());
  	 		    		inMSG = (Message) ois1.readObject();
  	 		    		//后续处理
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
  		    else if(inMes.getType().equals(MsgType.SETCCRNUM)){//已测试
  		    	//客户端请求修改服务状态
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
  		    	//客户端请求展示所有符合要求的策略
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
  		    	//客户端选择使用某策略
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
  		    	//客户端选择设置策略CPU、内存参数
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
  		    }else if(inMes.getType().equals(MsgType.SERVICEDEPLOY)){//已测试
 		    	//如果客户的请求是发布服务
 		    	System.out.println("SERVICEDEPLOY");
 		    	ArrayList<String> serviceInfo = (ArrayList<String>)inMes.getBody();
 		    	outMes = publishService(serviceInfo);

 		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 	    	
  	 	    	//启动线程，查询副本表中是否已有记录，查到之后向全局地址服务发送服务回填信息
  	 	    	ServiceFeedback s = new ServiceFeedback(serviceInfo.get(0));
  	 	    	socket_c.close();
 		    }
  		    else if(inMes.getType().equals(MsgType.SERVICEDIRECTDEPLOY)){//已测试
		    	//手动发布服务
		    	System.out.println("SERVICEDIRECTDEPLOY");
		    	ArrayList<String> serviceInfo = (ArrayList<String>)inMes.getBody();
		    	publishService2(serviceInfo);

//		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
//		        oos.writeObject(outMes);
//	 		    oos.flush();
	 	    	
	 	    	//启动线程，查询副本表中是否已有记录，查到之后向全局地址服务发送服务回填信息
	 	    	ServiceFeedback s = new ServiceFeedback(serviceInfo.get(0));
	 	    	socket_c.close();
		    }
  		  else if(inMes.getType().equals(MsgType.SERVICEDEPLOYFLAG)){//已测试
		    	//验证主机部署是否成功
		    	System.out.println("SERVICEDEPLOYFLAG");
		    	ArrayList<String> deployInfo = (ArrayList<String>)inMes.getBody();
		    	checkDeployInfo(deployInfo);
	 	    	
	 	    	//启动线程，查询副本表中是否已有记录，查到之后向全局地址服务发送服务回填信息
//	 	    	ServiceFeedback s = new ServiceFeedback(serviceInfo.get(0));
	 	    	socket_c.close();
	 	    	
		    }
  		    else if (inMes.getType().equals(MsgType.FEEDBACKREQUEST)){
 		    	//前端请求回填服务信息
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
 		       //客户请求发布服务消息信息
 		    	System.out.println("SERVICEMESSAGE");
 		    	ArrayList<String[]> al = new ArrayList<String[]>();
 		    	al = (ArrayList<String[]>)inMes.getBody();
 		    	UseUserTable u = new UseUserTable();
 		    	u.AddServiceMessage(al);
 		    	socket_c.close();
 		    	//入库结束，不回消息了
 		    }else if(inMes.getType().equals(MsgType.MESSAGELIST)){
 		    	//客户请求展示服务消息信息
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
 		    	//如果客户端请求删除服务
 		    	System.out.println("SERVICEDELETE");
 		    	String serviceName = (String)inMes.getBody();
 		    	outMes = deleteService(serviceName);
 		    	
 		    	 ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
 		    }else if(inMes.getType().equals(MsgType.FLOWLIST)) {
 		    	//如果客户端请求列出流程列表(流程编排功能)
 		    	System.out.println("FLOWLIST");
 		    	UseUserTable u = new UseUserTable();
 		    	ArrayList<String[]> al = u.ListFlow();
 		    	outMes = new Message(MsgType.FLOWLISTRESULT, al);
 		    	
 		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
   		        oos.writeObject(outMes);
   	 		    oos.flush();
   	 		    socket_c.close();
 		     }else if(inMes.getType().equals(MsgType.FLOWLIST2)) {
  		    	//如果客户端请求列出流程列表(服务管理功能)
  		    	System.out.println("FLOWLIST2");
  		    	UseUserTable u = new UseUserTable();
  		    	ArrayList<String[]> al = u.ListFlow2();
  		    	outMes = new Message(MsgType.FLOWLIST2RESULT, al);
  		    	
  		    	ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
    		        oos.writeObject(outMes);
    	 		    oos.flush();
    	 		    socket_c.close();
  		     }else if(inMes.getType().equals(MsgType.SERVFLOWDEPLOY)){
   		    	//客户端请求服务流程部署
 		    	 System.out.println("SERVFLOWDEPLOY");
 		    	ArrayList<String> flowInfo = (ArrayList<String>)inMes.getBody();
 		    	outMes = deployFlow(flowInfo);

 		        ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if (inMes.getType().equals(MsgType.FLOWEXECUTE)){
  	 			 //客户端请求流程执行(不区分业务流程和服务流程，要执行必须先部署)
  	 			 System.out.println("FLOWEXECUTE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 ArrayList<String> al =u.SetFlowStatus((String)inMes.getBody(),"start");
  	 			 outMes = new Message(MsgType.FLOWEXECUTERESULT, al.get(0));
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.FLOWSTATUS)){
  	 			 //客户端请求修改流程状态
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
  	 			 //客户端请求进行流程转换（业务流程到服务流程）
  	 			 System.out.println("FLOWTRANS");
  	 			 String business = (String)inMes.getBody();
  	 			 String service = transformFlow(business);
  	 			 outMes = new Message(MsgType.FLOWTRANSRESULT, service);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.BSNSFLOWDEPLOY)){
  	 			 //客户端请求进行业务流程部署
  	 			 ArrayList<String> al = new ArrayList<String>();
  	 			 al = (ArrayList<String>)inMes.getBody();
  	 			 System.out.println("服务流程为"+al.get(4));
  	 			 if(al.get(4).equals("0")) {
  	 				 al.set(4, transformFlow(al.get(3)));
  	 				 System.out.println("进行流程转换");
  	 			 }
  	 			 outMes = deployFlow(al);
  	 			 
  	 			ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
  		        oos.writeObject(outMes);
  	 		    oos.flush();
  	 		    socket_c.close();
  	 		 }else if(inMes.getType().equals(MsgType.SERVICEEXIST)){
  	 			//请求匹配服务/流程名是否存在
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
  	 			//筛选服务表
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
  	 			//模糊查询服务表
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
  	 			//客户端请求节点信息展示
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
  	 			//客户端请求删除节点信息
  	 			 boolean flag=false;
  	 			 System.out.println("NODEDELETE");
  	 			 UseUserTable u = new UseUserTable();
  	 			 String ipaddress = (String)inMes.getBody();
  	 			 if(u.nodeExist(ipaddress));
  	 			 {
  	 				
  	 			 	//u.DeleteNode(ipaddress);
  	 			 	flag = true;
  	 			 	//向节点放送kill命令
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
  	 			//客户端请求修改节点信息
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
  	 			//客户端添加新节点信息
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
  	 			//筛选节点表
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
  	 			//模糊查询节点表
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
  	 			//筛选策略表
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
  	 			//模糊查询策略表
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
 		    //批量插入数据（callinfo)
  	 		 else if(inMes.getType().equals(MsgType.batinsert))
  	 		 {
  	 			 System.out.println("batinsert");
  	 			 ArrayList<String[]> al = (ArrayList<String[]>) inMes.getBody();
  	 			ServiceHeaper.insertCallinfo(al);
  	 			socket_c.close();
  	 		 }
 		    //电科院(各种服务所占比例)
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
  	 		 }//所有服务状态
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
  	 		 }//统计调用次数
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
 	 		 }//统计调用延时
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
				
				//查找数据库中的interface表，服务ID
				System.out.println("查找数据库中的interface表，服务ID");
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
					//"web service"需要查库得到
					boolean flag = si.generateClass(serviceName, interfaceName, "web service");
					//生成对应的class文件，将服务的接口调用信息入库interfacecall信息入库
					if(flag == true){
						sql = "insert into interfacecall values(?,?,?)";
						param = new String[]{serviceName,interfaceName,"<component doc:name=\"Java\" class=\""+serviceName+"_"+interfaceName+"\"/>"};
						db.update(sql, param);
					}
				}
				
				//再发送消息到各个代理主机上，将java代码编译放到Mule.jar里
				System.out.println("再发送消息到各个代理主机上，将serviceName/src下的java代码编译放到Mule.jar里");
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
	 		    	
	 		    		
	 		   		
	 		   		
	 		   		
	 		   		//发送文件
	 		   		OutputStream os = socket.getOutputStream();
	 		   		FileInputStream fis = new FileInputStream(f);

	 		   		// 缓冲区大小
	 		   		int bufferSize = 8192;
	 		   		// 缓冲区
	 		   		byte[] buf = new byte[bufferSize];
	 		   		// 传输文件
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
	 		    		
	 		    		
	 		    		//后续处理
	 		    		socket.close();
	 		    		
	 		    	}
				
				socket_c.close();
			}
			else if(inMes.getType() == MsgType.AddTransformer)
			{
				//接收Transformer对应的java文件,transformer/src下

	 			 UseUserTable u = new UseUserTable();
				System.out.println("接收Transformer对应的java文件");
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

				//再发送消息到各个代理主机上，将java代码编译放到Mule.jar里
				System.out.println("然后将消息发送到各个代理主机上，编译后放进Mule.jar里");
				//向代理发送消息，得到含有Mule服务的主机的ip，然后将Transformer里的java文件传到各个代理上，编译打包进Mule.jar
				//可以考虑在这里进行编译，然后将编译后的.class文件传送
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
	 		    	
	 		    		
	 		   		
	 		   		
	 		   		
	 		   		//发送文件
	 		   		OutputStream os = socket.getOutputStream();
	 		   		FileInputStream fis = new FileInputStream(f);

	 		   		// 缓冲区大小
	 		   		int bufferSize = 8192;
	 		   		// 缓冲区
	 		   		byte[] buf = new byte[bufferSize];
	 		   		// 传输文件
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
	 		    		
	 		    		
	 		    		//后续处理
	 		    		socket.close();
	 		    		
	 		    	}
				socket_c.close();
			}
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     
 		     //8.3
  	 		 else if(inMes.getType().equals(MsgType.LOGSIZE))
  	 		 {
  	 			 //log 表的大小
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
 	 			 //筛选后log大小
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
  	 			//模糊查询警报信息表
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
						System.out.println("开始知心");
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
				//服务平均响应时间
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
  		    	//运行监控软件请求所有服务状态信息
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
  		    	//运行监控软件请求所有主机的IP
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
  		    	//运行监控软件请求存储警报信息
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
		    	//运行监控软件请修改
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
  		    	//运行监控软件请求展示警报信息
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
  		    	//运行监控软件请求展示服务调用信息
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
  		    		System.out.println("代理发送启停消息："+body.get(0)+"类型"+body.get(1));
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
		  {//请求服务端口信息
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
		  {//请求服务端口信息
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
		  {//请求将服务适配信息入库
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
		        //根据客户端的关键字匹配合适的服务名
		    	System.out.println("SERVICEREQUEST");
		        String keyword = (String)inMes.getBody();
		        outMes = getMatchedServicesList(keyword);
		        
		       ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
		        
		     }else if(inMes.getType().equals(MsgType.SERVICEINFO)){		    	 
 		        //如果客户端发来的消息是请求某个服务的信息
 		    	 System.out.println("SERVICEINFO");
 		        String serviceName = (String)inMes.getBody();
 		        outMes = getMatchedServiceInfo(serviceName);
 		        
 		       ObjectOutputStream oos=new ObjectOutputStream(socket_c.getOutputStream());
		        oos.writeObject(outMes);
	 		    oos.flush();
 		        
 		    }else if(inMes.getType().equals(MsgType.ServiceManagement)){
 		    	//如果客户选择服务管理，需要查询自己曾经发布过的服务列表
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
              //查找用户信息，零输入，输出为ArrayList用户信息            
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
      		   //删除用户信息
	        	  UseUserTable u = new UseUserTable();
	        	  String a = (String) inMes.getBody();
	        	  try {
					u.DeleteUserInfo(a);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	       }else if (inMes.getType()==MsgType.UPDATEUSERINFO){
	    	 //更改用户权限
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
	    	 //增加用户信息表项
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
    	 //根据用户名和密码查询数据库，判断该用户是否是合法用户
    	 UseUserTable u = new UseUserTable();
    	 try {
    		 //返回值flag是权限和照片存储路径，client或者admin
			flag = u.confirmUser(name, password);
//			System.out.println(flag);
			if(flag != null){
				System.out.println("用户"+name+"登录");
				
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
			System.out.println("服务验证格式不对");
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
    	 String serviceID = serviceInfo.get(0);	//服务名
    	 System.out.println("serviceID："+serviceID);
	     //判断这个服务是否已经存在
	     boolean flag = isServiceExisted(serviceID);
	     if(flag == true){
	    	 //如果该服务已经存在
	    	 ipAddress = null;
	    	 System.out.println("该服务已存在");
	    	 //outMes = new Message(MsgType.SERVICEDEPLOYRESULT, ipAddress);
	     }else{
	    	 //如果该服务不存在
	    	 System.out.println("该服务不存在，可以添加服务");
//	    	 ipAddress = addNewService(serviceInfo);
//	    	 outMes = new Message(MsgType.SERVICEDEPLOYRESULT, ipAddress);
	    	 addService(serviceInfo);
	     }
    	    	 
    	 
	}
     private void addService(ArrayList<String> serviceInfo)
     {
    	 UseUserTable u  = new UseUserTable();
    	 String ips = serviceInfo.get(serviceInfo.size()-1);
    	//服务信息入库
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
    	//插入副本表
    	 String[] ip= ips.split(";");
    	 DBConnectionManager co = DBConnectionManager.getInstance();
    	 String sql = "insert into duplicate values(?,?,?,?,?,2)";
    	 for(int i=0;i<ip.length;i++)
    	 {
    		 co.update(sql, serviceid,ip[i],wsdl,type,status);
    	 }
    	 System.out.println("duplicate 插入记录成功!");
     }
	private Message getMatchedServicesList(String keyword) throws IOException, ClassNotFoundException{
          	//1.接收到客户代理的请求所有服务名字的消息，需要将消息的名字返回给客户代理
          	
          	//2.从数据库查询所有的服务名
          	UseUserTable u = new UseUserTable();
      		ArrayList<String> allServiceNames = null;
      		//首先得到所有的服务名列表
			try {
				allServiceNames = u.clientGetServiceList();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//3.根据keyword找出匹配的服务名
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
        //将客户请求的某服务的信息返回给客户端
//        String _serviceName = serviceName;
//      System.out.println(serviceName);
         	
        //从数据库查询客户端需要服务的wsdl地址
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
     /**发布服务
     */
     private Message publishService(ArrayList<String> serviceInfo) throws UnsupportedEncodingException{
    	 String ipAddress = null;
    	 Message outMes = null;
    	 String serviceID = serviceInfo.get(0);	//服务名
    	 System.out.println("serviceID："+serviceID);
	     //判断这个服务是否已经存在
	     boolean flag = isServiceExisted(serviceID);
	     if(flag == true){
	    	 //如果该服务已经存在
	    	 ipAddress = null;
	    	 System.out.println("该服务已存在");
	    	 outMes = new Message(MsgType.SERVICEDEPLOYRESULT, "service exist!");
	     }else{
	    	 //如果该服务不存在
	    	 System.out.println("该服务不存在，可以添加服务");
	    	 ipAddress = addNewService(serviceInfo);
	    	 outMes = new Message(MsgType.SERVICEDEPLOYRESULT, ipAddress!=null?ipAddress:"不存在可用代理容器节点。");
	    	 
	     }
    	    	 
    	 return outMes;
     }
    
     /**
      * 部署流程
      * @param flowInfo
      * @return
      */
     private Message deployFlow(ArrayList<String> flowInfo){
    	 String ipAddress = null;
    	 Message outMes = null;
    	 String flowName = flowInfo.get(0);	//流程名
    	 String serviceFlow = flowInfo.get(4);
    	 System.out.println("flowName："+flowName);
    	 ArrayList<String> al = new ArrayList<String>();
	     //判断这个流程是否已经存在
	     boolean flag = isFlowExisted(flowName);
	     if(flag == true){
	    	 //如果该流程已经存在
	    	 ipAddress = null;
	    	 al.add(flowName);
	    	 al.add(ipAddress);
	    	 al.add(serviceFlow);
	    	 System.out.println("该流程已存在");
	    	 outMes = new Message(MsgType.FLOWDEPLOYRESULT, al);
	     }else{
	    	 //如果该流程不存在
	    	 System.out.println("该流程不存在，可以添加流程");
	    	 ipAddress = addNewFlow(flowInfo);
	    	 al.add(flowName);
	    	 al.add(ipAddress);
	    	 al.add(serviceFlow);
	    	 outMes = new Message(MsgType.FLOWDEPLOYRESULT, al);
	    	 
	     }
    	    	 
    	 return outMes;
     }
     /**判断该服务是否已经存在*/
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
     /**判断该流程是否已经存在*/
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
     /**向数据库中添加新的服务记录，并返回新的服务分配都的ip地址
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
//     		//将新服务的信息添加到services、以及allservernodes表中，同时新建一个副本表
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
     /**向数据库中添加新的流程记录，并返回新的流程分配都的ip地址*/
     private String addNewFlow(ArrayList<String> flowInfo){
      	UseUserTable u = new UseUserTable();
      	String ipAddress = null;
      	
      	try {
      		if (flowInfo.size()==8) ipAddress = flowInfo.get(7);
      		else{
      		String type = "Flow";
      		ipAddress = u.getIpAddress(type);
      		}
      		//将新流程的信息添加到flow表中，不修改duplicate表，等待心跳修改
 			u.addNewFlowItem(flowInfo);			
 		} catch (SQLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
      	return ipAddress;
      }
     
     /**服务管理：查询某用户发布过的服务列表*/
     private Message serviceManage(String userName){
    	
    	 Message outMes = null;
    	//该用户发布的服务列表
 		ArrayList<String[]> pubServicesByUser = new ArrayList<String[]>();
 		
 		UseUserTable u = new UseUserTable();		
 		//获取所有服务列表
 		ArrayList<String[]> allServices = null;
 		try {
 			allServices = u.clientGetServiceList1();
 		} catch (SQLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
 		//从所有服务中删选匹配的服务
 		for(int i=0;i<allServices.size();i++){
 			String[] serviceInfo = allServices.get(i);
 			//判断该服务的发布者是否是登录的用户
 			if((userName).equals(serviceInfo[2])){
 				pubServicesByUser.add(serviceInfo);			
 			}
 		}
 		outMes = new Message(MsgType.ServiceManagement, pubServicesByUser);
    	return outMes;
     }
     
     /**
      * 业务流程转换服务流程
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
    	//所有用户发布的服务列表
// 		ArrayList<String[]> pubServices = new ArrayList<String[]>();
 		
 		UseUserTable u = new UseUserTable();		
 		//获取所有服务列表
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
     
     /**删除服务，返回某服务所有副本的IP地址*/
     private Message deleteService(String serviceName){
    	 Message outMes = null;
    	 UseUserTable u = new UseUserTable();
    	 ArrayList<String> ipAddress = new ArrayList<String>();
    	 
    	 try {
    		//获取某个服务的所有副本存放的IP地址
			ipAddress = u.getAllIpAddressOfService(serviceName);//servicetype+filename+ips
			//删除该服务的记录，修改副本表 不包括主机表
			String serviceType = ipAddress.get(0);

			
			//对所有的副本执行删除服务指令
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
	  	 		    		//后续处理
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
	  	 		    		//后续处理
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
	

	/** 显示注册中心当前状态*/
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
	
	/** 添加服务*/
	public void addService(List<ServiceInfo> slist)
	{
		services.addAll(slist);
	}
	
	
	
	/** 启动服务线程*/
	public void startListening()
	{
		try {
			new ListenServerNodeThread();	//监听服务节点线程，端口为7000
			new ListenClientAgentThread();	//监听客户端代理线程，端口为5000
			new ListenClientHeart();        //监听服务代理节点心跳，端口为7001
			new ScanHashtable().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws InterruptedException, IOException
	{   
//		try {  
//            //文件生成路径  
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
//		File filename = new File("D:\\businessFlow.xml"); // 要读取以上路径的input.txt文件  
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
