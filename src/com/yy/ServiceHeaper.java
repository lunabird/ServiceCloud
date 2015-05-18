package com.yy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.mysql.jdbc.PreparedStatement;

import rgscenter.domain.DBConnectionManager;
import rgscenter.domain.NodeInfo;
import rgscenter.domain.ServiceRegistryNode;
import rgscenter.domain.UseUserTable;
import Message.Message;
import Message.MsgType;


public class ServiceHeaper {

	//���ݲ��������Լ�map��key��ѡ��num��ip��
public static ArrayList<String> getService(String name,Map<String,Float> cpumaps,float cpukey,Map<String,Float> memmaps,float memkey,int num) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException
{
	Class class1 = null;  
    class1 = Class.forName(name);  
    Object obj = class1.newInstance(); 
    Method[] method = class1.getMethods();
    //for(int i=0;i<method.length;i++)
    	//System.out.println(method[i].getName());
    //��˼�������
    //Method method = class1.getMethod("getTargetService");
    Object o = method[0].invoke(obj, cpumaps,cpukey,memmaps,memkey,num);
	if(o != null)
		return (ArrayList<String>) o;
	return null;
}
//��Ĭ�ϵ��ò��� ����·�ɷ��� 
//flagΪ����cpu�����ڴ�(CPUUse or MEMUse)
//maps ���յ�������ip��cpu���ڴ�ļ�ֵ��
public static ArrayList<String> getServicesByServiceRouter(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, UnknownHostException, SQLException, IOException
{
	//1.�жϸ÷����Ƿ����
		if(!haveTheService(name))
			return null;
	//2�ҵ�Ĭ�ϵķ���·�ɲ�����
	String strategyName = null;
	DBConnectionManager co = DBConnectionManager.getInstance();
	co.getConnection();
	String sql = "select StrategyClassName from strategy where StrategyType='����·�ɲ���' and InUse='1' ";

	//ResultSet resultSet = co.query(sql);
	Statement stat = null;
	ResultSet resultSet = null;
	try{
		stat = co.getConnection().createStatement();
		resultSet = stat.executeQuery(sql);
		if(resultSet.next())
		{
			strategyName = resultSet.getString(1);
		}
		else
		{
			System.out.println("û��Ĭ�ϵĲ�����");
			return null;
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally{
		if(resultSet != null)
			try {
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(stat != null)
			try {
				stat.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	return getServicesByServiceRouter(name,strategyName);
}

public static ArrayList<String> getServicesByServiceRouter(String name,String strategyName) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, UnknownHostException, IOException
{
	ArrayList<String> res = new ArrayList<String>();
		//1.�жϸ÷����Ƿ����
			if(strategyName == null)
			{
				System.out.println("��������Ϊ��");
				return null;
			}
			if(!haveTheService(name)){
				res.add("���񲻴���");
				return res;
			}
				
		//2�ҵ�ָ������·�ɲ��Ե���ֵ
		float cpuKey = 0;
		float memKey = 0;
		DBConnectionManager co = DBConnectionManager.getInstance();
		co.getConnection();
		String sql = "select CPUUse,MEMUse from strategy where StrategyType='����·�ɲ���' and StrategyClassName='";
		Statement stat = co.getConnection().createStatement();
		ResultSet resultSet = stat.executeQuery(sql+strategyName+"'");
		//ResultSet resultSet = co.query(sql,strategyName);
		if(resultSet.next())
		{
			
			cpuKey = resultSet.getFloat(1);
			
			memKey = resultSet.getFloat(2);
			
		}
		else
		{
			
			System.out.println("û��Ĭ�ϵĲ�����");
			res.add("û��Ĭ�ϵĲ�����");
			return res;
			
		}
		co.close(resultSet);
		System.out.println("��������"+strategyName+"	cpu������ֵ��"+cpuKey+" 	mem������ֵ��"+memKey);
		//3.�������Բ���
		//���ݷ������ҵ����񸱱�������ip	
		ArrayList<String > list = new ArrayList<String>();
		String sql2 = "select IPAddress from duplicate where ServiceID='"+name+"' and ServiceStatus='start'";
		resultSet = stat.executeQuery(sql2);
		//resultSet = co.query(sql2, name);
		boolean f = false;
		while(resultSet.next())
		{
			f = true;
			list.add(resultSet.getString(1));
		}
		if(f == false)
		{
			//�����ڸ÷���ĸ���
		
			//co.close();
			System.out.println("�����ڸ÷���ĸ���");
			res.add("û�п��ø���");
			return res;
		}
		co.close(resultSet);
		//co.close();
		//��ȡcpumaps��memmaps��Ϣ
				ArrayList<String[]> serverInfo = getServerInfo3();
				if(serverInfo == null)
				{
					System.out.println("��ȡ������Ϣʧ��");
				}
				HashMap<String,Float> cpumaps = new HashMap<String,Float>();
				HashMap<String,Float> memmaps = new HashMap<String,Float>();
				for(int i=0;i<serverInfo.size();i++)
				{
					String ip = serverInfo.get(i)[0];
					
					Float cpu = Float.valueOf(serverInfo.get(i)[1].substring(0, serverInfo.get(i)[1].length()-1));
					
					Float mem = Float.valueOf(serverInfo.get(i)[2].substring(0, serverInfo.get(i)[2].length()-1));
					cpumaps.put(ip, cpu);
					memmaps.put(ip, mem);
				}
		//��maps ɸѡ��Ҫ����������Ϣ��
		HashMap<String,Float> targetMaps1 = new HashMap<String,Float>();
		for(Map.Entry<String, Float> map :cpumaps.entrySet() )
		{
			String ip = map.getKey();
			if(list.contains(ip))
				targetMaps1.put(ip, map.getValue());
		}
		System.out.println("cpuɸѡ�ķ��ϼ�ֵ�ԣ�");
		for(Map.Entry<String, Float> map :targetMaps1.entrySet() )
		{
			System.out.println(map.getKey()+"--"+map.getValue().floatValue());
		}
		HashMap<String,Float> targetMaps2 = new HashMap<String,Float>();
		for(Map.Entry<String, Float> map :memmaps.entrySet() )
		{
			String ip = map.getKey();
			if(list.contains(ip))
				targetMaps2.put(ip, map.getValue());
		}
		System.out.println("memɸѡ�ķ��ϼ�ֵ�ԣ�");
		for(Map.Entry<String, Float> map :targetMaps2.entrySet() )
		{
			System.out.println(map.getKey()+"--"+map.getValue().floatValue());
		}
		//4�������ؽ��
		ArrayList<String> result = getService(strategyName,targetMaps1,cpuKey,targetMaps2,memKey,1);
		System.out.println("result size:"+result.size());
		if(result != null && result.size()>0)
		{
			
			res.add(result.get(0));
			System.out.println("ѡ����õ�����IP��"+res.get(0));
			String re = null;
	//		DBConnectionManager conn = DBConnectionManager.getInstance();
//			conn.getConnection();
			String sql6 = "select WSDL from duplicate where ServiceID='"+name+"' and IPAddress='"+result.get(0)+"'";
			//resultSet = conn.query(sql6, name,result.get(0));
			resultSet = stat.executeQuery(sql6);
			if(resultSet.next())
			{
				re = resultSet.getString(1);
				res.add(re);
				
			}
			co.close(resultSet);
			co.close(stat);
			return res;
		}
		return null;
}
public static ArrayList<String> assignCopyByServiceName(String name) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, UnknownHostException, IOException
{
		//1.�жϸ÷����Ƿ����
		if(getTheServiceType(name) == null)
			return null;
		//2.�ҵ�Ĭ�ϵĸ����������
		String strategyName = null;
		DBConnectionManager co = DBConnectionManager.getInstance();
		Statement stat = co.getConnection().createStatement();
		String sql = "select StrategyClassName from strategy where StrategyType='���ط������' and InUse=1 ";

		ResultSet resultSet = stat.executeQuery(sql);
		if(resultSet.next())
		{
			strategyName = resultSet.getString(1);
		}
		else
		{
			System.out.println("û��Ĭ�ϵĲ�����");
			return null;
		}
		co.close(resultSet);
		co.close(stat);
		return assignCopyByServiceName(name,strategyName);
}

public static ArrayList<String> assignCopyByServiceName(String name,String strategyName) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, UnknownHostException, IOException
{
		//1.�жϸ÷����Ƿ����
		String result = "result";
		if(strategyName == null)
			return null;
		if((result = getTheServiceType(name) )== null)
			return null;
		//result Ҫ����service Ҫ����flow
		//2.�ҵ�ָ���ĸ���������Ե���ֵ
		float cpuKey = 0;
		float memKey = 0;
		DBConnectionManager co = DBConnectionManager.getInstance();
		Statement stat = co.getConnection().createStatement();
		ResultSet resultSet = null;
		String sql = "select CPUUse,MEMUse from strategy where StrategyType='���ط������' and StrategyClassName='";
		resultSet = stat.executeQuery(sql+strategyName+"'");
		//resultSet = co.query(sql,strategyName);
		if(resultSet.next())
		{
			
			
				cpuKey = resultSet.getFloat(1);
			
				memKey = resultSet.getFloat(2);
			
		}
		else
		{
			
			System.out.println("û�иò�����");
			return null;
		}
		System.out.println("Ĭ�ϵĲ�������"+strategyName+" 	cpu������ֵ��"+cpuKey+"   mem������ֵ��"+memKey);
		//3.�ҵ�����need��
		int serviceNum = -1;
		int nowNum = -1;
		String serviceType = null;
		//��ȡָ���������ĸ�������
		if(result.equals("service"))
		{
			sql = "select CopyNumber,ServiceType from services where ServiceID='";
			//resultSet = (ResultSet) co.query(sql,name);
			co.close(resultSet);
			resultSet = stat.executeQuery(sql+name+"'");
			if(!resultSet.next())
			{
				
				System.out.println("the service is not exist");
				return null;
			}
			serviceNum = resultSet.getInt(1);
			serviceType = resultSet.getString(2);
			
		}
		else//flow
		{
			sql = "select CopyNumber from flow where FlowID='";
			//resultSet = (ResultSet) co.query(sql,name);
			co.close(resultSet);
			resultSet = stat.executeQuery(sql+name+"'");
			if(!resultSet.next())
			{
				
				System.out.println("the service is not exist");
				return null;
			}
			serviceNum = resultSet.getInt(1);
			serviceType = "Flow";
			
		}
		//��ȡ��ǰ������
		String sql2 =null;
//		if(result.equals("service"))
//			sql2 = "select count(*) from duplicate where ServiceName=? and ServiceStatus='start'";
//		else
			sql2 = "select count(*) from duplicate where ServiceID='";
			co.close(resultSet);
//		resultSet = (ResultSet) co.query(sql2, name);
			resultSet = stat.executeQuery(sql2+name+"'");
		if(resultSet.next())
			nowNum = resultSet.getInt(1);
		else
			nowNum = 0;
		
		
		System.out.println("serviceNum is "+serviceNum+"	nowNum is "+nowNum+"	serviceType: "+serviceType);
		int needNum=serviceNum - nowNum;
		//4�������������ݸ����Է���
		String ipcontainService = null;
		//��������Ҫ�������
		ArrayList<String> l = new ArrayList<String>();
		if(needNum == 0 || nowNum==0)
		{
			
			return null;
		}
		else if(needNum > 0)
		{
			//��ȡ�Ѿ����еĸ�����ip
			String sql5 = "select IPAddress from duplicate where ServiceID ='"+name+"' limit 1";
//			resultSet = (ResultSet) co.query(sql5, name);
			co.close(resultSet);
			resultSet = stat.executeQuery(sql5);
			if(resultSet.next())
			{
				ipcontainService = resultSet.getString(1);
				//break;
			}
			else
			{
				//�����ڸ���
				return null;
				
			}
			
			
			//�Ӳ����ø����Һ��и÷�������������Ⱥ�Ϸ�������
			String sql3 =null;
			if(getContainerName(serviceType).equalsIgnoreCase("exe"))
			{
				sql3 = "select HostIP from node where HostIp not in(select IPAddress from duplicate where ServiceID='"+name+"' or Flag<>0)";
//				resultSet = (ResultSet) co.query(sql3, name);
				co.close(resultSet);
				resultSet = stat.executeQuery(sql3);
			}
			else//��exe���� 
			{
				sql3 = "select HostIP from node where HostIp not in(select IPAddress from duplicate where ServiceID='"+name+"' or Flag<>0) and ContainerName like '%"+getContainerName(serviceType)+"%'";
//				resultSet = (ResultSet) co.query(sql3, name,"%"+getContainerName(serviceType)+"%");
				co.close(resultSet);
				resultSet = stat.executeQuery(sql3);
			}
			
			//System.out.println(getContainerName(serviceType));
			while(resultSet.next())
			{
				l.add(resultSet.getString(1));
			}
			
		}
		else //��ǰ����������
		{
			String sql4 = "select IPAddress from duplicate where ServiceID='"+name+"' and Flag=0";
//			resultSet = co.query(sql4, name);
			co.close(resultSet);
			resultSet = stat.executeQuery(sql4);
			while(resultSet.next())
			{
				l.add(resultSet.getString(1));
			}
			
		}
		co.close(resultSet);
		co.close(stat);
		//��ȡcpumaps��memmaps��Ϣ
		ArrayList<String[]> serverInfo = getServerInfo3();
		if(serverInfo == null)
		{
			System.out.println("��ȡ������Ϣʧ��");
		}
		else{
			for(String[] a : serverInfo){
				for(String s :a){
					System.out.println(s);
				}
			}
			
		}
		HashMap<String,Float> cpumaps = new HashMap<String,Float>();
		HashMap<String,Float> memmaps = new HashMap<String,Float>();
		/*
		 * �ֹ�����
		 */
//		cpumaps.put("192.168.0.1", Float.valueOf((float) 23));
//		cpumaps.put("192.168.0.23", Float.valueOf((float)43));
//		cpumaps.put("192.168.0.7", Float.valueOf((float) 63));
//		cpumaps.put("192.168.0.8", Float.valueOf((float) 33));
//		memmaps.put("192.168.0.1", Float.valueOf((float) 23));
//		memmaps.put("192.168.0.23", Float.valueOf((float)33));
//		memmaps.put("192.168.0.7", Float.valueOf((float) 63));
//		memmaps.put("192.168.0.8", Float.valueOf((float) 33));
		for(int i=0;i<serverInfo.size();i++)
		{
			String ip = serverInfo.get(i)[0];
			Float cpu = Float.valueOf(serverInfo.get(i)[1].substring(0, serverInfo.get(i)[1].length()-1));
			Float mem = Float.valueOf(serverInfo.get(i)[2].substring(0, serverInfo.get(i)[2].length()-1));
			cpumaps.put(ip, cpu);
			memmaps.put(ip, mem);
		}
		//ɸѡ���ʵļ�ֵ��
		HashMap<String,Float> targetMaps1 = new HashMap<String,Float>();
		for(Map.Entry<String, Float> map :cpumaps.entrySet() )
		{
			String ip = map.getKey();
			if(l.contains(ip))
				targetMaps1.put(ip, map.getValue());
		}
		HashMap<String,Float> targetMaps2 = new HashMap<String,Float>();
		for(Map.Entry<String, Float> map :memmaps.entrySet() )
		{
			String ip = map.getKey();
			if(l.contains(ip))
				targetMaps2.put(ip, map.getValue());
		}
		System.out.println("targetMaps1.size:"+targetMaps1.size()+"targetMaps2.size:"+targetMaps2.size());
		ArrayList<String> assignIp = getService(strategyName,targetMaps1,cpuKey,targetMaps2,memKey,needNum);
		System.out.println("Ŀ������Ϊ��");
		for(String tstr:assignIp)
		{
			System.out.print(tstr+"	");
		}
		//5�������ؽ��
		ArrayList<String> list = new ArrayList<String>();
		if(result !=null && assignIp!=null && assignIp.size()>0)
		{	
			if(needNum > 0)
			{
				list.add("copy");
				list.add(ipcontainService);
				list.add(serviceType);
				for(int i=0;i<assignIp.size();i++)
					list.add(assignIp.get(i));
			}
			else if(needNum < 0)
			{
				list.add("delete");
				list.add(serviceType);
				for(int i=0;i<assignIp.size();i++)
					list.add(assignIp.get(i));
			}
		}
		for(String s:list)
		{
			System.out.print(s+"||");
		}
		System.out.println();
		return list;
}
public static void dealwithTheDisConnNode(String ip) throws SQLException
{
	DBConnectionManager co = DBConnectionManager.getInstance();
	//co.getConnection();
	//����host��������״̬
	//co.locktable("node");
	String sql = "update node set Status=?  where HostIP=? ";
	co.update(sql, "disconnect",ip);
	//co.unlocktable("node");
	//���ø���������״̬
	//co.locktable("duplicate");
	String sql1 = "update duplicate set ServiceStatus=?  where IPAddress=? ";
	co.update(sql1, "disconnect",ip);
	//co.unlocktable("duplicate");
}
public static void setServiceStatusByIP(String ip) throws SQLException
{
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet res = null;
	//����host��������״̬
	
	String sql = "update node set Status=?  where HostIP=? ";
	co.update(sql, "connect",ip);
	
	//��ȡ���������з���
//	ArrayList<String> services = new ArrayList<String>();
//	String sql1 = "select ServiceID from duplicate where IPAddress='";
//
//	res = stat.executeQuery(sql1+ip+"'");
//	
//	while(res.next())
//	{
//		services.add(res.getString(1));
//	}
//
//	co.close(res);
//	co.close(stat);
//	//��ÿ����������״̬
//	for(int i=0;i<services.size();i++)
//	{
//		try {
//			setServiceStatus(services.get(i));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
private static void setServiceStatus(String serviceName) throws SQLException
{
	int numOfStart =0,numOfStop =0;
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet res = null;
	//��ȡstart����������stop��������
	//co.locktable("duplicate");
	String sql1 = "select count(*) from duplicate where ServiceID='"+serviceName+"' and ServiceStatus='";
//	ResultSet res = co.query(sql1,serviceName,"start");
	res = stat.executeQuery(sql1+"start'");
	
	if(res.next())
		numOfStart = res.getInt(1);
	co.close(res);
//	res = co.query(sql1,serviceName,"stop");
	res = stat.executeQuery(sql1+"stop'");
	if(res.next())
		numOfStop = res.getInt(1);
	co.close(res);
	//��������״̬�����������÷���״̬
	System.out.println("start num:"+numOfStart+"stop num:"+numOfStop);
	
	String sql2 = "update duplicate set ServiceStatus=?  where ServiceID=?";
	if(numOfStart > numOfStop)
		co.update(sql2, "start",serviceName);
	else
		co.update(sql2, "stop",serviceName);
	//co.unlocktable("duplicate");
	co.close(res);
	co.close(stat);
	
}

public static String getContainerName(String serviceType)
{
	if(serviceType.equalsIgnoreCase("Web Service"))
		return "Axis2";
	else if(serviceType.equalsIgnoreCase("Restful"))
		return "Tomcat";
	else if(serviceType.equalsIgnoreCase("Flow"))
		return "Mule";
	else if(serviceType.equalsIgnoreCase("Web Site"))
		return "IIS";
	return serviceType;//exe
}

public static String getTheServiceType(String name) throws SQLException
{
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet resultSet =null;
	boolean flag1=false,flag2=false;
	String sql = "select * from services where ServiceID='";
	String sql2 = "select * from flow where FlowID='";
	//co.locktable("services");
//	ResultSet resultSet = co.query(sql, name);
	resultSet = stat.executeQuery(sql+name+"'");
	
	try{
		if(resultSet.next())
		{
			flag1 = true;
		
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	//co.unlocktable("services");
	co.close(resultSet);
	//co.locktable("flow");
//	resultSet = co.query(sql2, name);
	resultSet = stat.executeQuery(sql2+name+"'");
	
	try{
		if(resultSet.next())
		{
			flag2 = true;
		
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	//co.unlocktable("flow");
	co.close(resultSet);
	co.close(stat);
	//����flag��ֵ���ؽ��
	if(flag1)
		return "service";
	if(flag2)
		return "flow";
	return null;
}
public static boolean haveTheService(String name) throws SQLException
{
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet resultSet = null;
	String sql = "select * from services where ServiceID='";
//	ResultSet resultSet = co.query(sql, name);
	resultSet = stat.executeQuery(sql+name+"'");
	try{
		if(resultSet.next())
		{
			co.close(resultSet);
			co.close(stat);
			return true;
		}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	
	co.close(resultSet);
	co.close(stat);
	return false;
}
public static String assignServiceCopy(String name)
{
	return null;
}

//��ȡ������ʵʱ��Ϣ
public static ArrayList<String[]> getServerInfo()throws UnknownHostException, IOException, ClassNotFoundException
{
	BufferedReader reader = new BufferedReader(new FileReader(ServiceRegistryNode.file));
	  String MonitorIP = reader.readLine();
	    Socket socket = new Socket(MonitorIP,7001);
	ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	oos.writeObject(new Message(MsgType.CurrentInfo,null));
	oos.flush();
	
	ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	Message inMsg = (Message) ois.readObject();
	socket.close();
	if(inMsg.getType() == MsgType.CurrentInfoResult)
	{	
		return (ArrayList<String[]>) inMsg.getBody();
	}
	return null;
}
public static String getServiceTypeByServiceID(String serviceid) throws SQLException
{
	String type = null;
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet rs =null;
	String sql = "select ServiceType from services where ServiceID='";
	rs = stat.executeQuery(sql+serviceid+"'");
	if(rs.next())
		type = rs.getString(1);
	else
		type = "Flow";
	co.close(rs);
	co.close(stat);
	return type;
}
//��ȡ������ʵʱ��Ϣ ���ݿ��л�ȡ
public static ArrayList<String[]> getServerInfo2() throws SQLException
{
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date timeBeforeModify = new Date();
    Calendar c = Calendar.getInstance();
    c.setTime(timeBeforeModify);
    c.set(Calendar.SECOND, c.get(Calendar.SECOND)-28);
    Date timeAfterModify = c.getTime();
    
    ArrayList<String[]> al = new ArrayList<String[]>();
    HashSet hs = new HashSet();//����ȥ�ظ�
    DBConnectionManager co = DBConnectionManager.getInstance();
    Statement stat = co.getConnection().createStatement();
    ResultSet  rs = null;
    String sql = "select IP,CPU,MEM from noderealtimeinfo where Time between '"+sf.format(timeAfterModify)+"' and '"+sf.format(timeBeforeModify)+"'";
//    ResultSet rs = co.query(sql, sf.format(timeAfterModify),sf.format(timeBeforeModify));
    rs = stat.executeQuery(sql);
    while(rs.next())
    {
    	boolean flag = hs.add(rs.getString(1));
    	if(flag){
	    	String[] str = new String[3];
	    	str[0] = rs.getString(1);
	    	str[1] = rs.getString(2);
	    	str[2] = rs.getString(3);
	    	str[1] = str[1].substring(0, str[1].indexOf("%"));
	    	str[2] = str[2].substring(0, str[2].indexOf("%"));
	    	al.add(str);
    	}
    }
    co.close(rs);
    co.close(stat);
	if(al.size() == 0)
		return null;
	return al;
}
//��ȡ������ʵʱ��Ϣ���ڴ��л�ȡ
public static ArrayList<String[]> getServerInfo3() throws SQLException
{
  
  ArrayList<String[]> al = new ArrayList<String[]>();
  for(String key:ServiceRegistryNode.hashtable.keySet())
  {
	  NodeInfo value = ServiceRegistryNode.hashtable.get(key);
	  if(value.getStatus().equals("connect"))
	  {
		  al.add(value.getNodeRealTimeInfo());
	  }
  }
  if(al.size() == 0)
	return null;
  return al;
}
//�ļ���Ψһ
public static String getServiceIDByFileName(String filename) throws SQLException
{
	String serviceid = null;
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet rs = null;
	String sql = "select ServiceID from services where FileName='";
//	ResultSet rs = co.query(sql, filename);
	//co.locktable("services");
	
	rs = stat.executeQuery(sql+filename+"'");
	if(rs.next())
	{
		serviceid = rs.getString(1);
		co.close(rs);
		//co.unlocktable("services");
	}
	else
	{
		co.close(rs);	
		//co.unlocktable("services");
		//co.locktable("flow");
		sql = "select FlowID from flow where FileName='";
		
//		rs = co.query(sql, filename);
		rs = stat.executeQuery(sql+filename+"'");
		if(rs.next())
		{
			serviceid = rs.getString(1);
		}
		co.close(rs);
		//co.unlocktable("flow");
	}
	
	co.close(stat);
	return serviceid;
}

//�������������Ϣ
public static void dealWithCopyedIps(ArrayList<String> list,String name)
{
	if(list == null||list.size()==0)
		return ;
	if(list.get(0).equals("delete"))
	{
		
		//����socket���� ��ÿ����Ҫɾ������������������
		//�����ʽ MsgType.DeleteService body name
		//����
		for(int i=2;i<list.size();i++)
		{
			try{
				ArrayList<String> delMsg = new ArrayList<String>();
				delMsg.add(name);
				delMsg.add(list.get(1));
				if(list.get(1).equalsIgnoreCase("restful")){
					UseUserTable u = new UseUserTable();
					String uri = u.getRestfulURI(getServiceIDByFileName(name),list.get(i));
					
					delMsg.set(0, name+"&&"+uri);
				}
				String ip = list.get(i);
				System.out.println("Ҫɾ�����������ip�ǣ�"+ip);
				Socket socket = new Socket(ip,8000);
				Message outMsg = new Message(MsgType.DeleteService,delMsg);
				ObjectOutputStream  oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(outMsg);
				oos.flush();
				
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				ois.readObject();
				socket.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	else if(list.get(0).equals("copy"))
	{
		//����Ҫ�����ķ�������� ���͸�ĳ�����и÷��������
		//����message
		ArrayList<String> l = new ArrayList<String>();
		l.add(name);
		for(int i=2;i<list.size();i++)
		{
			l.add(list.get(i));
			
		}
		//���Ҫ����ip
		for(int i=2;i<l.size();i++)
		{
			System.out.println("Ҫ��������ip��"+l.get(i));
		}
		try{
			String ip = list.get(1);
			Socket socket = new Socket(ip,8000);
			Message outMsg = new Message(MsgType.CopyAllocation,l);
			ObjectOutputStream  oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(outMsg);
			oos.flush();
			//δ�õ�
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Message inMeg = (Message) ois.readObject();
			if(inMeg.getType() == MsgType.CopyAllocationResult)
				System.out.println(inMeg.getBody().toString());
			socket.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
//ά�ּ�������ĸ�������
public static void keepSomeServiceCopy(ArrayList<String> list) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, UnknownHostException, IOException
{
	for(int i=0;i<list.size();i++)
	{
		dealWithCopyedIps(assignCopyByServiceName(list.get(i)),getFileNameByServiceID(list.get(i)));
	}
}
public static String getFileNameByServiceID(String serviceid) throws SQLException
{
	String filename = null;
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet rs = null;
	String sql = "select FileName from services where ServiceID='";
//	ResultSet rs = co.query(sql, serviceid);
	rs = stat.executeQuery(sql+serviceid+"'");
	if(rs.next())
	{
		filename = rs.getString(1);
	}
	else
	{
		sql = "select FileName from flow where FlowID='";
		co.close(rs);
//		rs = co.query(sql, serviceid);
		rs = stat.executeQuery(sql+serviceid+"'");
		if(rs.next())
		{
			filename = rs.getString(1);
		}
	}
	co.close(rs);
	co.close(stat);
	return filename;
}
public static ArrayList<String[]> getHostCPUInfo(String ip) throws SQLException
{
	int number = 10;
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet rs = null;
	String sql = "select CPU,MEM,Time from noderealtimeinfo where HostIP='"+ip+"' order by Time desc";
	ArrayList<String[]> al = new ArrayList<String[]>();
    try{
    //co.locktable("noderealtimeinfo");
//    ResultSet rs = co.query(sql, ip);
    rs = stat.executeQuery(sql);
    while(rs.next() && number > 0)
    {
    	String[] str = new String[3];
    	str[0] = rs.getString(1);
    	str[1] = rs.getString(2);
    	str[2] = rs.getString(3);
    	str[2] = str[2].substring(0,str[2].length()-2);
    	al.add(str);
    	number--;
    }
   
    //co.unlocktable("noderealtimeinfo");
    co.close(rs);
    co.close(stat);
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
    return al;
	
}
public static ArrayList<String[]> getHostDiskInfo(String ip) throws SQLException
{
	int number = 10;
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet rs = null;
	String sql = "select DiskInfo,Time from noderealtimeinfo where HostIP='"+ip+"' order by Time desc";
	ArrayList<String[]> al = new ArrayList<String[]>();
    try{
    //co.locktable("noderealtimeinfo");
//    ResultSet rs = co.query(sql, ip);
    rs = stat.executeQuery(sql);
    while(rs.next() && number >0)
    {
    	String[] str = new String[2];
    	str[0] = rs.getString(1);
    	str[1] = rs.getString(2);
    	str[1] = str[1].substring(0,str[1].length()-2);
    	al.add(str);
    	number--;
    }
    //co.unlocktable("noderealtimeinfo");
    co.close(rs);
    co.close(stat);
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
    return al;
	
}
public static void insertNodeRealTimeInfo(Object[] o)
{
	DBConnectionManager co = DBConnectionManager.getInstance();
	String sql = "insert into noderealtimeinfo values(?,?,?,?,?)";
	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
    String time = df1.format(new Date());
    try{
   //co.locktable("noderealtimeinfo");
    co.update(sql, o[0].toString(),o[1].toString(),o[2].toString(),o[3].toString(),time);
    //co.unlocktable("noderealtimeinfo");
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
    co.close();
	
}
public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, SQLException
{
	/*String serviceName = "AddService";
	Map<String,Float> maps = new HashMap<String,Float>();
	maps.put("192.168.0.1", Float.valueOf((float) 23));
	maps.put("192.168.0.23", Float.valueOf((float)43));
	maps.put("192.168.0.7", Float.valueOf((float) 63));
	maps.put("192.168.0.8", Float.valueOf((float) 33));
	
	Map<String,Float> maps2 = new HashMap<String,Float>();
	maps2.put("192.168.0.1", Float.valueOf((float) 23));
	maps2.put("192.168.0.23", Float.valueOf((float)33));
	maps2.put("192.168.0.7", Float.valueOf((float) 63));
	maps2.put("192.168.0.8", Float.valueOf((float) 33));
	System.out.println("Ĭ�Ϸ���·�ɷ����ipΪ��");
	String list = getServicesByServiceRouter(serviceName,maps,maps2);
	System.out.println(list);
	System.out.println("****************");
	System.out.println("ָ������·�ɷ����ipΪ��");
	list = getServicesByServiceRouter(serviceName,maps,maps2,"com.yy.C");
	System.out.println(list);*/
	/*String serviceName = "AddService";
	Map<String,Float> maps = new HashMap<String,Float>();
	maps.put("192.168.0.1", Float.valueOf((float) 23));
	maps.put("192.168.0.23", Float.valueOf((float)43));
	maps.put("192.168.0.7", Float.valueOf((float) 63));
	maps.put("192.168.0.8", Float.valueOf((float) 33));
	
	Map<String,Float> maps2 = new HashMap<String,Float>();
	maps2.put("192.168.0.1", Float.valueOf((float) 23));
	maps2.put("192.168.0.23", Float.valueOf((float)43));
	maps2.put("192.168.0.7", Float.valueOf((float) 63));
	maps2.put("192.168.0.8", Float.valueOf((float) 33));
	//System.out.println("Ĭ�ϸ��������ipΪ��");
	ArrayList<String> list =assignCopyByServiceName(serviceName,maps,maps2);
	if(list!=null)
		for(int i=0;i<list.size();i++)
			System.out.println(list.get(i));*/
	ArrayList<String> al = getServiceDetail();
	for(String s : al)
	{
		System.out.println(s);
	}
}
public static ArrayList<String> getServiceDetail() throws SQLException {
	// TODO Auto-generated method stub
	ArrayList<String> al = new ArrayList<String>();
	int num=0,startNum=0,stopNum=0,discNum=0;
	String str = "";
	String str1 ="";
	String str2 = "";
	String str3 = "";
	ArrayList<String> businesstype = new ArrayList<String>();
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet rs = null;
//	co.locktable("services");
//	ResultSet rs = co.query("select count(*) from services");
	rs = stat.executeQuery("select count(*) from services");
	if(rs.next())
		num = rs.getInt(1);
	co.close(rs);
	rs  = stat.executeQuery("select BusinessType,count(BusinessType) from services group by BusinessType");
	
	while(rs.next())
	{
		str =str+rs.getString(2)+",";
		businesstype.add(rs.getString(1));
	}
	if(str.length() != 0)
		str = str.substring(0, str.length()-1);
//	co.unlocktable("services");
	co.close(rs);
	//start ״̬ �� ����ҵ�����͵ķ�������
	//co.locktable("duplicate write, services ");
	String sql = "select  count(distinct ServiceID) from duplicate where ServiceStatus=? and ServiceType<>'Flow'";
	String sql2 = "select BusinessType,count(distinct duplicate.ServiceID) from duplicate,services where duplicate.ServiceID=services.ServiceID and ServiceStatus=? and BusinessType =? group by BusinessType";
//	rs = co.query(sql, "start");
	rs = stat.executeQuery("select  count(distinct ServiceID) from duplicate where ServiceStatus='start' and ServiceType<>'Flow'");
	if(rs.next())
		startNum = rs.getInt(1);
	co.close(rs);
	for(int i=0;i<businesstype.size();i++)
	{
//		rs = co.query(sql2, "start",businesstype.get(i));
		rs = stat.executeQuery("select BusinessType,count(distinct duplicate.ServiceID) from duplicate,services where duplicate.ServiceID=services.ServiceID and ServiceStatus='start' and BusinessType ='"+businesstype.get(i)+"' group by BusinessType");
		if(rs.next())
			str1 = str1+ rs.getString(2)+",";
		else
			str1 = str1+"0,";
		co.close(rs);
	}
	if(str1.length() != 0)
		str1 = str1.substring(0, str1.length()-1);
	
	//stop
//	rs = co.query(sql, "stop");
	rs = stat.executeQuery("select  count(distinct ServiceID) from duplicate where ServiceStatus='stop' and ServiceType<>'Flow'");
	if(rs.next())
		stopNum = rs.getInt(1);
	co.close(rs);
	for(int i=0;i<businesstype.size();i++)
	{
//		rs = co.query(sql2, "stop",businesstype.get(i));
		rs = stat.executeQuery("select BusinessType,count(distinct duplicate.ServiceID) from duplicate,services where duplicate.ServiceID=services.ServiceID and ServiceStatus='stop' and BusinessType ='"+businesstype.get(i)+"' group by BusinessType");
		if(rs.next())
			str2 = str2+ rs.getString(2)+",";
		else
			str2 = str2+"0,";
		co.close(rs);
	}
	if(str2.length() != 0)
		str2 = str2.substring(0, str2.length()-1);
	//disconnect
//	rs = co.query(sql, "disconnect");
	rs = stat.executeQuery("select  count(distinct ServiceID) from duplicate where ServiceStatus='disconnect' and ServiceType<>'Flow'");
	if(rs.next())
		discNum = rs.getInt(1);
	co.close(rs);
	for(int i=0;i<businesstype.size();i++)
	{
//		rs = co.query(sql2, "disconnect",businesstype.get(i));
		rs = stat.executeQuery("select BusinessType,count(distinct duplicate.ServiceID) from duplicate,services where duplicate.ServiceID=services.ServiceID and ServiceStatus='disconnect' and BusinessType ='"+businesstype.get(i)+"' group by BusinessType");
		if(rs.next())
			str3 = str3+ rs.getString(2)+",";
		else
			str3 = str3+"0,";
		co.close(rs);
	}
	co.close(stat);
	if(str3.length() != 0)
		str3 = str3.substring(0, str3.length()-1);
	//co.unlocktable("duplicate");
	al.add(num+"@"+str);
	al.add(startNum+"@"+str1);
	al.add(stopNum+"@"+str2);
	al.add(discNum+"@"+str3);
	return al;
}
public static ArrayList<String> getNodeDetail() throws SQLException {
	// TODO Auto-generated method stub

	// TODO Auto-generated method stub
	ArrayList<String> al = new ArrayList<String>();
	int num=0,conNum=0,discNum=0;
	String str = "";
	String str1 = "";
	String str2 = "";
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stat = co.getConnection().createStatement();
	ResultSet rs = null;
	//co.locktable("node");
//	ResultSet rs = co.query("select count(*) from node");
	rs = stat.executeQuery("select count(*) from node");
	if(rs.next())
		num = rs.getInt(1);
	co.close(rs);
//	rs  = co.query("select ServiceNumber from node");
	rs = stat.executeQuery("select ServiceNumber from node");
	while(rs.next())
	{
		str =str+rs.getString(1)+",";
	}
	co.close(rs);
	if(str.length() != 0)
		str = str.substring(0, str.length()-1);
	
	
	
	String sql = "select  count(*) from node where Status=?";
	String sql2 = "select ServiceNumber from node where Status=?";
//	rs = co.query(sql, "connect");
	rs = stat.executeQuery("select  count(*) from node where Status='connect'");
	if(rs.next())
		conNum = rs.getInt(1);
	co.close(rs);
//	rs = co.query(sql2, "connect");
	rs = stat.executeQuery("select ServiceNumber from node where Status='connect'");
	while(rs.next())
	{
		str1  = str1 + rs.getInt(1)+",";
	}
	co.close(rs);
	if(str1.length() != 0)
		str1 = str1.substring(0, str1.length()-1);
	
//	rs = co.query(sql, "disconnect");
	rs = stat.executeQuery("select  count(*) from node where Status='disconnect'");
	if(rs.next())
		discNum = rs.getInt(1);
	co.close(rs);
//	rs = co.query(sql2, "disconnect");
	rs = stat.executeQuery("select ServiceNumber from node where Status='disconnect'");
	while(rs.next())
	{
		str2  = str2 + rs.getInt(1)+",";
	}
	if(str2.length() != 0)
		str2 = str2.substring(0, str2.length()-1);
	//co.unlocktable("node");
	co.close(rs);
	co.close(stat);
	al.add(num+"@"+str);
	al.add(conNum+"@"+str1);
	al.add(discNum+"@"+str2);
	return al;

}
public static void updateServicesStatus(Map<String,String> map,String ip)
{
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stmt=null;
	try {
		stmt = co.getConnection().createStatement();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	ResultSet rs = null;
	String sql0  = "select ServiceStatus from duplicate where ServiceID='";
	String sql = "update duplicate set ServiceStatus=? where ServiceID=? and IPAddress=?";
	String serviceid = null;
	String nowValue = "";
	for(Map.Entry<String, String> temp:map.entrySet())
	{
		String key = temp.getKey();
		
		try {
			serviceid = getServiceIDByFileName(key);//id
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(serviceid == null)
			return;
		try{
	
		rs = stmt.executeQuery(sql0+serviceid+"' and IPAddress='"+ip+"'");
		if(rs.next())
			nowValue = rs.getString(1);//��ǰ״̬
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		String value = temp.getValue();//status
		if(value != nowValue && nowValue !="")
			co.update(sql, value,serviceid,ip);
		//�쳣�������
		if(!value.equals("disconnect") && value !=nowValue)
		{
			;
		}
	}
}
public static ArrayList<String> getLegalIPs() throws SQLException
{
	ArrayList<String> al = new ArrayList<String>();
	DBConnectionManager co = DBConnectionManager.getInstance();
	Statement stmt = null;
	ResultSet rs = null;
	stmt =co.getConnection().createStatement();
	//co.locktable("node");
	String sql = "select HostIP from node where Status<>'check'";
	rs = stmt.executeQuery(sql);
	while(rs.next())
	{
		al.add(rs.getString(1));
	}
	//co.unlocktable("node");
	co.close(rs);
	co.close(stmt);
	return al;
}
//������������
public static int insertCallinfo(ArrayList<String[]> al)
{
	Connection conn = DBConnectionManager.getInstance().getConnection();
	try{
	conn.setAutoCommit(false);
	String sql = "insert into callinfo(No,Time,EndTime,ServiceID,ServiceIP,ServiceType,BusinessType,Status) values(NULL,?,?,?,?,?,?,?)";
	PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
	for(String[] strs:al)
	{
		//set sql
		pst.setDouble(1, Double.valueOf(strs[0]));
		pst.setDouble(2, Double.valueOf(strs[1]));
		pst.setString(3, strs[2]);
		pst.setString(4, strs[3]);
		pst.setString(5, strs[4]);
		pst.setString(6, strs[5]);
		pst.setString(7, strs[6]);
		pst.addBatch();
	}
	//��������
	pst.executeBatch();
	conn.commit();
	return 0;
	}catch(Exception e)
	{
		e.printStackTrace();
		try {
			conn.rollback();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return -1;
	}
}
}
