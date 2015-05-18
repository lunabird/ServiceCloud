package rgscenter.domain;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yy.ServiceHeaper;


public class UseUserTable {
	private URLDecoder ud = new URLDecoder();  
	private DBConnectionManager connMgr;
	private Connection con;
	/*
	 * ���캯��
	 */
	public UseUserTable() {
		connMgr = DBConnectionManager.getInstance();
		con = connMgr.getConnection();
		if (con == null) {
            System.out.println("Can't get connection");
            return;
        }
	}
//	/**
//	 * ��ȡ���������б�
//	 * @throws ParseException 
//	 * @throws SQLException 
//	 * @throws IOException 
//	 * @throws ClassNotFoundException 
//	 * @throws UnknownHostException 
//	 */
//	public void getHeartBeat(WSDLSet wsdlset) throws ParseException, SQLException, UnknownHostException, ClassNotFoundException, IOException{
//		List<String> lst1 = wsdlset.getWsdllocations();
//		
//		if(lst1.size()==1){
//			//�������ε�����û�б仯		
//			System.out.println("same HeartBeat!");			
//		}
//		else{
//			System.out.println("�������ݿ�");
//			for(int i=0;i<lst1.size();i+=2){
//				if(lst1.get(i).equals("ADD")){
//					insert_Table("services",lst1.get(i+1));
//				}
//				else if(lst1.get(i).equals("DELETE")){	
//					//�޸����ݿ�
//					deleteServiceFromCopy(lst1.get(i+1));
//					updateServiceFromServices(lst1.get(i+1));				
//					//�������ڵ㿽������
//					copyFromOtherNode(lst1.get(i+1));
//					
//					
//				}
//				else{
//					
//				}
//			}
//		}
//	}
	
	/** ���»򴴽�������
	 * @param tableName
	 * @param WSDLlocation
	 * @throws ParseException
	 */
	public void insert_Table(String WSDLlocation) throws ParseException{
		
		Services ser = new Services();
		try {
			ser.getServices(WSDLlocation);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		String name=ser.getName(WSDLlocation);
		String ipAddress=ser.getIpAddress(WSDLlocation);
//		int serviceAmount=ser.getServiceAmount();
		String wsdllocation=WSDLlocation;
		
		Statement statement;
		try {
			statement = con.createStatement();
//			ResultSet rs = null;
			//���븱����
			System.out.println("Ŀǰ����ı��ǣ�"+name);
			if(isTabExists(name)){
				String sql1 = "INSERT INTO "+name+" values ('" + name +"','"+ipAddress+"','"+wsdllocation+"')";
//				System.out.println("��������ڲ���"+sql1);
				statement.executeUpdate(sql1);
				System.out.println("�ɹ������¼��");
			}
			else{
				System.out.println("�½�������"+name);
				createTab(name);
				String sql1 = "INSERT INTO "+name+" values ('" + name +"','"+ipAddress+"','"+wsdllocation+"')";
//				System.out.println("���������ڲ���"+sql1);
				statement.executeUpdate(sql1);
			}
			//���¸�����
//			String sql = "SELECT Count(*) from "+name;
//			System.out.println("��ȡ������ļ�¼��Ŀ��������������"+sql);
//			try {
//				rs = statement.executeQuery(sql);	
//				rs.next();
//				int f= rs.getInt(1);				
//				serviceAmount = f;
//			}catch(SQLException e){
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			//����services��
//			if(isNameExists(name)){
//					updateServices(name,serviceAmount);
//			}
//			else{
//					insertServices(name);
//			}
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**�ж�services�����Ƿ����ĳ������
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public boolean isNameExists(String ServicesName) throws SQLException{
		Statement statement;statement = con.createStatement();
		ResultSet rs=null;
		String sqlm ="SELECT name FROM services";
		rs=statement.executeQuery(sqlm);
		ArrayList<String> AL1 = new ArrayList<String>();
		while(rs.next())
		{		
			String svsName = rs.getString(1);
			AL1.add(svsName);
		}		
		if(AL1.contains(ServicesName)){
			System.out.println("name already exists");
			return true;
		}
		else{
			System.out.println("name not exists");
			return false;
		}		
	}
	/**������Ŀ����services��
	 * @throws SQLException 
	 * 
	 */
	public void insertServices(String ServicesName) throws SQLException{
		Statement statement;
		statement = con.createStatement();		
		int serviceAmount=1;
		String sqlx = "INSERT INTO services values ("+"'"+ServicesName+"'"+","+ serviceAmount+")";
		System.out.println("���¼�¼����services��"+sqlx);
		statement.executeUpdate(sqlx);
	}
	/**����services��ĸ�����
	 * @throws SQLException 
	 * 
	 */
	public void updateServices(String ServicesName,int ServiceAmount) throws SQLException{
		Statement statement;
		statement = con.createStatement();
		String sql = "UPDATE services SET serviceamount="+ServiceAmount+" WHERE name="+"'"+ServicesName+"'";
		System.out.println("����services���serviceamount "+sql);
		statement.executeUpdate(sql);
	}
	
	/**����������
	 * @param tableName
	 * @return
	 */
	public void createTab(String tableName){
		Statement stmt = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = "CREATE table "+ tableName+" (name varchar(50),IPAddress varchar(15)," +
				"wsdllocation varchar(255),PRIMARY key (wsdllocation))";
//		System.out.println(sql);
		try {
			stmt.execute(sql);
//			System.out.println("�ɹ�����������"+tableName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**�жϸ������Ƿ����
	 * @param tableName
	 * @return
	 */
	public boolean isTabExists(String tableName){
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql = "SELECT Count(table_name) from information_schema.columns WHERE table_name LIKE "+"'"+tableName+"'";
//		System.out.println(sql);
		try {
			rs = stmt.executeQuery(sql);	
			rs.next();
			int f= rs.getInt(1);
			
			if(f>0){
				System.out.println("�������Ѵ��ڣ�");
				return true;
			}
			else {
				System.out.println("���������ڣ�");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;		
	}
	/**
	 * �Ӹ�������ɾ����¼
	 * @param wsdlLocation
	 * @throws SQLException 
	 */
	public void deleteServiceFromCopy(String wsdlLocation) throws SQLException{
		Statement stmt = null;
		Services ser = new Services();
		try {
			ser.getServices(wsdlLocation);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		String name=ser.getName(wsdlLocation);
		String ipAddress=ser.getIpAddress(wsdlLocation);
//		int serviceAmount=ser.getServiceAmount();
//		String wsdllocation=wsdlLocation;
		
		String sql1 = "DELETE FROM "+name+" WHERE wsdllocation ="+"'"+wsdlLocation+"'";
		String sql2 = "DELETE FROM allservernodes WHERE IPAddress = '"+ipAddress+"' AND services = '"+name+"'";
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stmt.execute(sql1);
		System.out.println("�ɹ��Ӹ�����"+name+"��ɾ����¼��");
		stmt.execute(sql2);
		System.out.println("�ɹ���allservernodes����ɾ����¼��");		
		
		//����������м�¼��Ϊ0����ɾ���ø�����
		String sql3 = "SELECT COUNT(*) from "+name;
		ResultSet rs = stmt.executeQuery(sql3);	
		rs.next();
		int serviceAmount = rs.getInt(1);				
		if(serviceAmount == 0){
			String sql4 = "DROP TABLE "+name;
			stmt.execute(sql4);
			
			String sql5 = "DELETE FROM services WHERE name='"+name+"'";
			stmt.execute(sql5);
			System.out.println("������Ϊ0��ɾ���������services���еļ�¼");
		}
	}
	/**
	 * ����services��ĸ�����
	 * @param wsdlLocation
	 * @throws SQLException
	 */
	public void updateServiceFromServices(String wsdlLocation) throws SQLException{
		Statement stmt = null;
		Services ser = new Services();
		try {
			ser.getServices(wsdlLocation);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		String name=ser.getName(wsdlLocation);
		int serviceAmount=getServiceAmount(wsdlLocation);

		stmt = con.createStatement();
		String sql2 = "UPDATE services SET serviceamount="+(serviceAmount-1)+" WHERE name = "+"'"+name+"'";
		stmt.execute(sql2);
		System.out.println("�ɹ�����services��ĸ�������");
	}
	/**
	 * �ѷ���ڵ��ip��ַ��������Ʋ���allServerNodes��
	 * @param wsdlLocation
	 * @throws SQLException
	 */
	public void insertAllServerNodesTable(String wsdlLocation) throws SQLException{
		Statement stmt = null;
		stmt = con.createStatement();
		Services ser = new Services();
		try {
			ser.getServices(wsdlLocation);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		String ipaddress = ser.getIpAddress(wsdlLocation);
		String servicename = ser.getName(wsdlLocation);
		String sql = "INSERT INTO allServerNodes values ("+"'"+ipaddress+"','"+servicename+"')";
//		System.out.println(sql);
		stmt.execute(sql);
		System.out.println("�ɹ�����allServerNodes��");
	}
	/**
	 * ��allservernodes�����ó�thiswsdl���������ip��ַ������
	 * ����������ʱ������Щ�ڵ�����������
	 * @return
	 * @throws SQLException
	 * @throws UnknownHostException 
	 */
	public ArrayList<String> getOtherServernodes(String thiswsdl) throws SQLException, UnknownHostException{
		Statement stmt = null;
		ResultSet rs =null;
		ArrayList<String> iplist = new ArrayList<String>();
		stmt = con.createStatement();
		String sql = "SELECT DISTINCT IPAddress FROM allservernodes";
		rs = stmt.executeQuery(sql);	
		while(rs.next()){
			iplist.add(rs.getString(1));
		}
		Services ser = new Services();
		ser.getServices(thiswsdl);
		String ipaddress = ser.getIpAddress(thiswsdl);
		iplist.remove(ipaddress);
		return iplist;
	}
	
	/**
	 * ��allservernodes������ӷ���
	 * @param wsdllocation
	 * @throws SQLException
	 */
//	public void appendText(String wsdllocation) throws SQLException{
//		Statement stmt = null;
//		stmt = con.createStatement();
//		Services ser = new Services();
//		try {
//			ser.getServices(wsdllocation);
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}	
//		String ipaddress = ser.getIpAddress(wsdllocation);
//		String servicename = ser.getName(wsdllocation);
//		String sql = "INSERT INTO allServerNodes SET services = CONCAT (services,'"+ servicename+",') where IPAddress="+"'"+ipaddress+"'";
////		System.out.println(sql);
//		stmt.execute(sql);
//		System.out.println("����allServerNodes��ɹ���");
//	}
	/**
	 * �жϸ�ip��ַ�Ƿ��Ѿ�����
	 * @param ipaddress
	 * @return
	 * @throws SQLException
	 */
	boolean isINAllServerNodesTable(String ipaddress) throws SQLException{
		Statement stmt = null;
		stmt = con.createStatement();
		String sql = "SELECT IPAddress FROM allServerNodes WHERE IPAddress="+"'"+ipaddress+"'";
		boolean b=stmt.execute(sql);
//		System.out.println(b);
		return b;
	}
	
	/**
	 * ��ȡĳ����ĸ�����
	 * @param wsdlLocation
	 * @return
	 * @throws SQLException
	 */
	public int getServiceAmount(String wsdlLocation) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		Services ser = new Services();
		try {
			ser.getServices(wsdlLocation);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		String name=ser.getName(wsdlLocation);		
		String sql = "SELECT COUNT(*) FROM "+name;
		
		stmt = con.createStatement();
		rs = stmt.executeQuery(sql);	
		rs.next();
		int serviceAmount= rs.getInt(1);
		System.out.println("��ѯ����"+name+"�ĸ�����Ϊ"+serviceAmount);
		return serviceAmount;
	}
	
//	/**
//	 * �������ڵ㿽������
//	 * @param serviceName
//	 * @param ip
//	 * @throws UnknownHostException
//	 * @throws ClassNotFoundException
//	 * @throws IOException
//	 * @throws SQLException 
//	 */
//	public void copyFromOtherNode(String wsdllocation) throws UnknownHostException, ClassNotFoundException, IOException, SQLException
//	{	
//		
////		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//		
//		Services ser = new Services();
//		try {
//			ser.getServices(wsdllocation);
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}		
//		String serviceName=ser.getName(wsdllocation);
//		String ip = selectIPFromServicename(serviceName);
//		
//		MigrateData data = new MigrateData(serviceName,ip,7001);
//		Message msg = new Message(MsgType.REPLICATE, data);
//        oos.writeObject(msg);
//        oos.flush();
//    	
//	}
	
	/**
	 * �õ�ĳ����ĸ������ڵ�IP��ַ
	 * @param serviceName
	 * @return
	 * @throws SQLException
	 */
	public String selectIPFromServicename(String serviceName) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT IPAddress FROM "+serviceName;
		rs = stmt.executeQuery(sql);	
		rs.next();
		String nodeIP =  rs.getString(1);
		return nodeIP;
	}
	
	/**
	 * �����ݿ��ѯ���еķ�����
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String> clientGetServiceList() throws SQLException{
		ArrayList<String> serviceNames = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT ServiceID FROM services";
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			serviceNames.add(rs.getString(1));
		}
		return serviceNames;
	}
	/**
	 * �����ݿ��ѯ�������ϸ��Ϣ�����ڿͻ����÷���
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> clientGetServiceList1() throws SQLException{
		ArrayList<String[]> serviceItem = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT * FROM services";
		rs = stmt.executeQuery(sql);
		while(rs.next()){			
			String name = rs.getString(1);
//			String serviceamount = String.valueOf(rs.getInt(2));
			String description = rs.getString(2);
			String publisher = rs.getString(3);
			String parameter = rs.getString(4);
			String paramAmount = String.valueOf(rs.getInt(5));
//			String[] str_row = {name, serviceamount,description,publisher,parameter,paramAmount}; 
			String[] str_row = {name,description,publisher,parameter,paramAmount};
			serviceItem.add(str_row);
		}
		return serviceItem;
	}
	
	
	/**�����ݿ��в�ѯƥ��ķ����б�*/
	public ArrayList<String[]> clientGetValueServiceList(String keyword) throws SQLException, NullPointerException{
		ArrayList<String[]> allServiceItem = new ArrayList<String[]>();
		ArrayList<String[]> valueServiceItem = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT * FROM services";
		rs = stmt.executeQuery(sql);
		while(rs.next()){			
			String name = rs.getString(1);
			String serviceamount = String.valueOf(rs.getInt(2));
			String description = rs.getString(3);
			String publisher = rs.getString(4);
			String parameter = rs.getString(5);
			String paramAmount = String.valueOf(rs.getInt(6));
			String[] str_row = {name, serviceamount,description,publisher,parameter,paramAmount}; 		
			allServiceItem.add(str_row);
		}
		
		for(int i=0;i<allServiceItem.size();i++){
			String[] str = allServiceItem.get(i);
			if(str[0].toString().toLowerCase().contains(keyword.toString().toLowerCase())){
				valueServiceItem.add(str);
			}
		}
		
		return valueServiceItem;
	}

	
	
	/**
	 * �����ݿ��ѯ�ͻ�����Ҫ�����wsdl��ַ
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String> getClientReqServiceWSDL(String name) throws SQLException{
		ArrayList<String> serviceInfo = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT wsdllocation FROM "+name;
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			serviceInfo.add(rs.getString(1));
		}
		return serviceInfo;
	}
	
    /**
     * �ж��û���¼
     * @param username
     * @param pwd
     * @param auth
     * @return
     * @throws SQLException
     */
	public ArrayList<String> confirmUser(String username,String pwd) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		ArrayList<String> al = new ArrayList<String>();
		String sql = "SELECT Password FROM user where UserName = "+"'"+username+"'";
		rs = stmt.executeQuery(sql);	
		rs.next();
		String password =  rs.getString(1);
		if(password.equals(pwd)){
			ResultSet rs1 =null;
			String sql1 = "SELECT Authority,ImagePath FROM user WHERE username = "+"'"+username+"'";
			rs1 = stmt.executeQuery(sql1);
			rs1.next();
			String auth = rs1.getString(1);
			String path = rs1.getString(2);
			al.add(auth);
			al.add(path);
			return al;
		}
		else
			return null;		
	}
	/**
	 * ��ѯĳ�ڵ�ĸ���
	 * @param ip
	 * @return
	 * @throws SQLException
	 */
	public int queryNodeLoad(String ip) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT COUNT(*) FROM allservernodes WHERE IPAddress="+"'"+ip+"'";
		rs = stmt.executeQuery(sql);	
		rs.next();
		int load = rs.getInt(1);
		return load;
	}
	/**
	 * �û���ѯ�Լ������ķ���
	 * @param userName
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String> queryPublishedService(String userName) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT name FROM services WHERE publisher="+"'"+userName+"'";
		rs = stmt.executeQuery(sql);	
		ArrayList<String> pub_services = new ArrayList<String>();
		while(rs.next()){
			pub_services.add(rs.getString(1));
		}
		return pub_services;
	}
	/**
	 * ��ѯ��Ҫ���õķ���Ĳ�������
	 * @param serviceName
	 * @return
	 * @throws SQLException
	 */
	public int queryParamAmount(String serviceName) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT paramAmount FROM services WHERE name="+"'"+serviceName+"'";
		rs = stmt.executeQuery(sql);	
		rs.next();
		int amount = rs.getInt(1);
		return amount;
	}
	/**
	 * �����ݿ��ȡ�����б�
	 * @param serviceName
	 * @return
	 * @throws SQLException
	 */
	public String[] getParamList(String serviceName) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT parameter FROM services WHERE name="+"'"+serviceName+"'";
		rs = stmt.executeQuery(sql);	
		rs.next();
		String param = rs.getString(1);
		if(!param.isEmpty()){
			String[] paramList=param.split(",");
			return paramList;
		}
		return null;
	}
	/**
	 * �û�������ѯ�����û�����Ϣ������ʾ
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> getUserInfo() throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		ArrayList<String[]> userlist = new ArrayList<String[]>();
		stmt = con.createStatement();
		String sql = "SELECT * FROM user";
		rs = stmt.executeQuery(sql);	
		while(rs.next()){
			String username = rs.getString("username");
			String pswd = rs.getString("password");
			String auth = rs.getString("authority");
			String[] row_str = {username,pswd,auth};
			userlist.add(row_str);
		}
		return userlist;
	}
	
	/**
	 * �����ݿ��ѯ�������ϸ��Ϣ�����ڿͻ��ùؼ���keyword���÷���
	 * @return
	 * @throws SQLException
	 */
	public String[] clientGetServiceList2(String servicename) throws SQLException{
		String[] serviceItem = new String[6];
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT * FROM services WHERE name="+"'"+servicename+"'";
		rs = stmt.executeQuery(sql);
		while(rs.next()){			
			serviceItem[0]= rs.getString(1);
//			serviceItem[1] = String.valueOf(rs.getInt(2));
			serviceItem[1] = rs.getString(2);
			serviceItem[2] = rs.getString(3);
			serviceItem[3] = rs.getString(4);
			serviceItem[4] = String.valueOf(rs.getInt(5));
		}
		return serviceItem;
	}
	
	
	/**��ȡĳ������ĸ�����
	 * @throws SQLException */
	public int getServiceCopyAmount(String serviceName) throws SQLException{
		int amount = 0;
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT COUNT(*) FROM "+serviceName;
		rs = stmt.executeQuery(sql);	
		rs.next();
		amount = rs.getInt(1);
		return amount;
		
	}
	
	/**Ϊ������и������䣬��ȡ������С�Ҳ����÷����IP��ַ
	 * @throws SQLException */
	public String getMiniumIPAddress(String serviceName) throws SQLException{
		String ipAddress = null;
		Statement stmt = null;
		ResultSet rs =null;
		stmt = con.createStatement();
		String sql = "SELECT IPAddress FROM allservernodes WHERE services <> "+"'"+serviceName+"' GROUP BY IPAddress ORDER BY count(IPAddress) ASC";
		rs = stmt.executeQuery(sql);
		rs.next();
		ipAddress = rs.getString(1);
		return ipAddress;
	}
	
	/**�Ӹ������л�ȡ�÷������ڵ�ĳһ��IP��ַ
	 * @throws SQLException */
	public String getIPFromCopyTable(String serviceName) throws SQLException{
		String ipAddress = null;
		Statement stmt = null;
		ResultSet rs = null;
		stmt = con.createStatement();
		String sql = "SELECT IPAddress From "+serviceName;
		rs = stmt.executeQuery(sql);
		rs.next();
		ipAddress = rs.getString(1);
		return ipAddress;
	}
	
	/**
	 * ������û��������û���
	 * @param name
	 * @param code
	 * @param privilege
	 * @throws SQLException
	 */
	public void InsertUserInfo(String name,String code,String privilege)throws SQLException{
		Statement stmt = null;
		stmt = con.createStatement();
		String sql = "INSERT INTO user values ("+"'"+name+"',"+"'"+code+"',"+"'"+privilege+"'"+")";
		stmt.execute(sql);
		System.out.println("�ɹ�����"+name+"�û���");
	
	}
	
	/**
	 * ɾ���û�����
	 * @param name
	 * @throws SQLException
	 */
	public void DeleteUserInfo(String name)throws SQLException{
		Statement stmt = null;
		stmt = con.createStatement();
		String sql = "DELETE FROM user WHERE username ="+"'"+name+"'";
		stmt.execute(sql);
		System.out.println("�ɹ�ɾ��"+name+"�û���");
	}
	
	
	/**
	 * �޸��û�Ȩ��
	 * @param name
	 * @param privilege
	 * @throws SQLException
	 */
	public void UpdateUserInfo(String name,String privilege)throws SQLException{
		Statement stmt = null;
		stmt = con.createStatement();
		String sql = "UPDATE user SET authority='"+privilege+"' WHERE username="+"'"+name+"'";
		stmt.execute(sql);
		System.out.println("�ɹ��޸�"+name+"�û�Ȩ��");

	}

	
	/**
	 * �޸��û������
	 * @param name
	 * @param password
	 * @throws SQLException
	 */
	public void UpdateUserPassword(String name, String password) throws SQLException {
		Statement stmt = null;
		stmt = con.createStatement();
		String sql = "UPDATE user SET password='"+password+"' WHERE username="+"'"+name+"'";
		stmt.execute(sql);
		System.out.println("�ɹ��޸�"+name+"�û�����");
		
	}
	
	
	/**
	 * ��ArrayList<String[]>��ʽ���ص�ǰ���з�����Ϣ
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> ListServices() throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "SELECT * FROM services";
        String sql1 = "lock tables services write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[11];
			temp[0]=rs.getString(1);//id
			temp[1]=rs.getString(2);//serviceName
			//temp[2]=String.valueOf(rs.getFloat(3));//version
			temp[2] = rs.getString(3);
			temp[3]=rs.getString(4);//description
			temp[4]=rs.getString(8);//serviceType
			temp[5]=rs.getString(9);//businessType
			temp[6]=String.valueOf(rs.getInt(10));//copynumber
			temp[7]=String.valueOf(rs.getInt(11));//maxrunningnumber
			temp[8]=rs.getString(13);//ownersystem
			temp[10]=ServiceRegistryNode.unifiedIP+rs.getLong("Port")+"/";
			al.add(temp);
		}
		//stmt.execute(sql2);
		//stmt.execute("lock tables node write");
		String sql3 = "select count(*) from node where Status='connect' and ContainerName like '%";
		for(int i=0;i<al.size();i++)
		{
			rs = stmt.executeQuery(sql3+getContainerName(al.get(i)[4])+"%'");
			if(rs.next())
				al.get(i)[9] = rs.getString(1);
			else
				al.get(i)[9] = "0";
		}
		//stmt.execute("unlock tables");
		return al;
	}
	public  String getContainerName(String serviceType)
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
	/**
	 * ��ArrayList<String[]>���ص�ǰ����������Ϣ
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> ListHost() throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "SELECT HostIP,HostName,ServiceNumber FROM node";
        String sql1 = "lock tables node write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[6];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(2);
			temp[5]=rs.getString(3);
			al.add(temp);
		}
		//stmt.execute(sql2);
		return al;
		
	}
	public ArrayList<String[]> ListHost(String str) throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String containerName = getContainerName(str);
		String sql = "SELECT HostIP,HostName,ServiceNumber FROM node where Status='connect' and ContainerName like '%"+containerName+"%'";
        String sql1 = "lock tables node write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[6];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(2);
			temp[5]=rs.getString(3);
			al.add(temp);
		}
		//stmt.execute(sql2);
		return al;
		
	}
	/**
	 * ���÷��񸱱�����
	 * @param al
	 * @return
	 * @throws SQLException
	 */
    public Boolean SetDuplicateNum(ArrayList<String> al) throws SQLException{
    	Statement stmt = null;
    	ResultSet rs = null;
		stmt=con.createStatement();
		Boolean flag = false;
		String sql = null,sql1 = null,sql2 = "unlock tables";;
		String sql3 = "select ServiceType from duplicate where ServiceID="+"'"+al.get(0)+"'";
       // stmt.execute("lock tables duplicate write");
        rs = stmt.executeQuery(sql3);
        rs.next();
        String serviceType = rs.getString(1);
        //stmt.execute(sql2);
        if(serviceType.equalsIgnoreCase("Flow")){
        	sql = "UPDATE flow SET CopyNumber="+al.get(1)+" WHERE FlowID="+"'"+al.get(0)+"'";
        	sql1 = "lock tables flow write";
        }
        else{
        	 sql = "UPDATE services SET CopyNumber="+al.get(1)+" WHERE ServiceID="+"'"+al.get(0)+"'";
             sql1 = "lock tables services write";           
        }
         
        System.out.println(sql1);
        //stmt.execute(sql1);
	    stmt.execute(sql);
		//stmt.execute(sql2);
		flag = true;
		System.out.println("set dupicate number");
		return flag;
		
    }
    
    /**ɾ��ĳ�����������ݿ����ܱ����Ϣ�����޸ĸ���������������������޸�
   	 * @throws SQLException */
       /**
        * ���޸ģ�
        * @param serviceID
        * @param serviceType
        * @throws SQLException
        */
   	public void deleteAllInfoOfService(String serviceID,String serviceType) throws SQLException{
   		Statement stmt = null;
   		String sql1 = new String();
   		String sql2 = new String();
   		String out = new String();
   		stmt = con.createStatement();
   		if(serviceType.equalsIgnoreCase("Flow")){
   			sql1 = "DELETE FROM flow WHERE FlowName="+"'"+serviceID+"'";
   			sql2 = "lock tables flow write";
   			out = "ɾ��flow���еļ�¼�ɹ�";
   			}
   		else {
//   		String sql = "select count(*) from services where ServiceName="+"'"+serviceName+"'";
   		//ɾ��services���и÷��������ڵļ�¼
   		sql1 = "DELETE FROM services WHERE ServiceID="+"'"+serviceID+"'";
   		sql2 = "lock tables services write";
   		out = "ɾ��services���еļ�¼�ɹ�";
   		}
   		String sql3 = "delete from servicemessage where ServiceID="+"'"+serviceID+"'";
   		//ɾ��duplicate���и÷������ڵļ�¼
//   		String sql2 = "DELETE FROM duplicate WHERE ServiceName="+"'"+serviceName+"'";
   		//ɾ���÷���ĸ�����
   		//stmt.execute(sql2);
   		stmt.execute(sql1);
   		//stmt.execute("lock tables servicemessage write");
   		stmt.execute(sql3);
   		//stmt.execute("unlock tables");
   		System.out.println(out);
   		System.out.println("ɾ��servicemessage���еļ�¼�ɹ�");
   		String sql4 = "delete from duplicate where ServiceID="+"'"+serviceID+"'";
   		stmt.execute(sql4);
//   		stmt.execute(sql4);
//   		stmt.execute(sql2);
//   		System.out.println("ɾ��duplicate���м�¼�ɹ�");
//   		stmt.execute(sql5);
//   		stmt.execute("lock tables host write");
//   		for(int i=0;i<al.size();i++){
//   			String ipAddress = al.get(0);
//   			String s =  "UPDATE host SET ServiceNumber = ServiceNumber-1 WHERE HostIP="+"'"+ipAddress+"'";
//   			stmt.execute(s);
//   		}
//   		System.out.println("����host��ɹ�");
//   		stmt.execute(sql5);
//   		System.out.println("�ɹ�ɾ������");
   	}
   	
   	
   	/**��ȡĳ������ķ������ͣ��ļ����Լ����и�����IP��ַ
   	 * @throws SQLException */
   	/**
   	 * ���޸ģ�
   	 * @param serviceID
   	 * @return
   	 * @throws SQLException
   	 */
   	public ArrayList<String> getAllIpAddressOfService(String serviceID) throws SQLException{
   		ArrayList<String> ipAddress = new ArrayList<String>();
   		Statement stmt = null;
   		ResultSet rs = null;
   		stmt = con.createStatement();
   		String sql = "SELECT IPAddress From duplicate WHERE ServiceID="+"'"+serviceID+"' and ServiceStatus<>'disconnect'";
   		String sql1 = "lock tables duplicate write";
   		String sql2 = "unlock tables";
   		String sql3 = "SELECT ServiceType From duplicate WHERE ServiceID="+"'"+serviceID+"'";
   		
   		//stmt.execute(sql1);
   		rs = stmt.executeQuery(sql3);
   		rs.next();
   		ipAddress.add(rs.getString(1));
   		if(ipAddress.get(0).equalsIgnoreCase("flow"))
   		{
   			//flow 
   		}
   		else 
   		{
   			rs = stmt.executeQuery("select FileName from services where ServiceID='"+serviceID+"'");
   			rs.next();
   			ipAddress.add(rs.getString(1));
   		}
   		rs = stmt.executeQuery(sql);
   		while(rs.next()){
   			ipAddress.add(rs.getString(1));
   			System.out.println(rs.getString(1));
   		}
   	    //stmt.execute(sql2);
   		return ipAddress;
   	}
   	
	/**
	 * ��ArrayList<String[]>��ʽ���ط������и�����״̬��IP���������͡�����ϵͳ����Ϣ
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> GetServiceStatus() throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "SELECT * FROM duplicate  ";
        String sql1 = "lock tables duplicate write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String temp[] = new String[5];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(2);
			temp[2]=rs.getString(5);
			al.add(temp);
		}
		//stmt.execute(sql2);
		sql = "SELECT * FROM services";
		//stmt.execute("lock tables services write");
		rs = null;
		rs = stmt.executeQuery(sql);
		ArrayList<String[]> list = new ArrayList<String[]>();
		while(rs.next()){
			String temp[] = new String[3];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(6);
			temp[2]=rs.getString(7);
			list.add(temp);
		}
		//stmt.execute(sql2);		
		sql = "SELECT * FROM flow";
		//stmt.execute("lock tables flow write");
		rs = null;
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String temp[] = new String[3];
			temp[0]=rs.getString(1);
			temp[1]="Flow";
			temp[2]=rs.getString(6);
			list.add(temp);
		}
		//stmt.execute(sql2);
		for(String[] a :al){
			for(String[] b :list){
				
				if(b[0].equals(a[0])){
	//				System.out.println(b[0]+"="+a[0]);
					a[3] = b[1];
					a[4] = b[2];
					break;
				}
			}
		}
		return al;
		
	}
	
	/**
	 * ��ArrayList��ʽ���ط��񸱱��ķ�������IP������״̬
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> GetAllServices() throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "SELECT * FROM duplicate where ServiceType !='Flow'";
        String sql1 = "lock tables duplicate write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String temp[] = new String[3];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(2);
			temp[2]=rs.getString(5);
			al.add(temp);
		}
		//stmt.execute(sql2);
		return al;
		
	}
	
	public ArrayList<String[]> GetAllFlows() throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "SELECT * FROM duplicate where ServiceType ='Flow'";
        String sql1 = "lock tables duplicate write";
        String sql2 = "unlock tables";
       // stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String temp[] = new String[3];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(2);
			temp[2]=rs.getString(5);
			al.add(temp);
		}
		//stmt.execute(sql2);
		return al;
		
	}
	/**
	 * �����м������ṩ����������IP
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String> GetIP() throws SQLException{
		ArrayList<String> al = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "SELECT HostIP FROM host";
        String sql1 = "lock tables host write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			al.add(rs.getString(1));
		}
		//stmt.execute(sql2);
		return al;
	}
	
	/**
	 * �޸�ָ�����񸱱���״̬,����״̬��ʱ��ģ�
	 * @param al
	 * @return
	 * @throws SQLException
	 */
	
	 public ArrayList<String> SetServiceStatus(ArrayList<String> al) throws SQLException{
	    	Statement stmt = null;
			stmt=con.createStatement();
			ResultSet rs = null;
			ArrayList<String> list = new ArrayList<String>();
			String sql = "UPDATE duplicate SET ServiceStatus='"+al.get(1)+"' WHERE ServiceID="+"'"+al.get(0)+"' and ServiceStatus <>'disconnect' and Flag <>'2'";
	        String sql1 = "lock tables duplicate write";
	        String sql2 = "unlock tables";
	        String sql3 = "select IPAddress from duplicate WHERE ServiceID='"+al.get(0)+"' and ServiceStatus <>'disconnect' and Flag <>'2'";
	        System.out.println(sql);
	       // stmt.execute(sql1);
		    stmt.execute(sql);
		    rs = stmt.executeQuery(sql3);
		    while(rs.next()){
		    	list.add(rs.getString(1));
		    	System.out.println(rs.getString(1));
		    }
			//stmt.execute(sql2);
			System.out.println("set service status");
			return list;
			
	    }
	 public Boolean SetServiceCCRNum(ArrayList<String> al) throws SQLException{
		 Statement stmt = null;
	    	ResultSet rs = null;
			stmt=con.createStatement();
			Boolean flag = false;
			String sql = null,sql1 = null,sql2 = "unlock tables";;
			String sql3 = "select ServiceType from duplicate where ServiceID="+"'"+al.get(0)+"'";
	        //stmt.execute("lock tables duplicate write");
	        rs = stmt.executeQuery(sql3);
	        rs.next();
	        String serviceType = rs.getString(1);
	        //stmt.execute(sql2);
	        if(serviceType.equalsIgnoreCase("Flow")){
	        	sql = "UPDATE flow SET MaxRunningNumber="+al.get(1)+" WHERE FlowID="+"'"+al.get(0)+"'";
	        	sql1 = "lock tables flow write";
	        }
	        else{
	        	 sql = "UPDATE services SET MaxRunningNumber="+al.get(1)+" WHERE ServiceID="+"'"+al.get(0)+"'";
	             sql1 = "lock tables services write";           
	        }
	         
	        System.out.println(sql1);
	        //stmt.execute(sql1);
		    stmt.execute(sql);
			//stmt.execute(sql2);
			flag = true;
			System.out.println("set dupicate number");
			return flag;
			
	    }
	 /**
	  * ���ݷ��������������󣬷��ز��Ա���Ϣ
	  * @param input
	  * @return
	  * @throws SQLException
	  */
	 public ArrayList<String[]> ListStrategy(String type) throws SQLException{
		    ArrayList<String[]> al = new ArrayList<String[]>();
		    
		    Statement stmt = null;
		    stmt=con.createStatement();
		    ResultSet rs =null;
			String sql = "SELECT * FROM strategy WHERE StrategyType="+"'"+type+"'";
	        String sql1 = "lock tables strategy write";
	        String sql2 = "unlock tables";
	        //stmt.execute(sql1);
	        rs = stmt.executeQuery(sql);
			while(rs.next()){
				String[] temp = new String[5];
				temp[0]=rs.getString(1);
				temp[1]=rs.getString(3);
				temp[2]=String.valueOf(rs.getInt(5));
				temp[3]=String.valueOf(rs.getInt(6));
				temp[4]=rs.getString(7);
				al.add(temp);
			}
			//stmt.execute(sql2);
			return al;
	 }
	
	 /**
	  * �����û�ѡ��Ĳ������޸ĸ�����Ե�inuseѡ��
	  * @param name
	  * @return
	  * @throws SQLException
	  */
	 public Boolean SelectStrategy(String name) throws SQLException{
	        Boolean flag = false;
	        Statement stmt = null;
	        stmt=con.createStatement();
		    ResultSet rs =null;
			String sql = "SELECT StrategyType FROM strategy WHERE StrategyName="+"'"+name+"'";
	        String sql1 = "lock tables strategy write";
	        String sql2 = "unlock tables";
	        //stmt.execute(sql1);
	        rs = stmt.executeQuery(sql);
	        rs.next();
	        String type = rs.getString(1);
	        String sql3 = "UPDATE strategy SET InUse='0' WHERE StrategyType="+"'"+type+"'";
	        String sql4 = "UPDATE strategy SET InUse='1' WHERE StrategyName="+"'"+name+"'";
	        stmt.execute(sql3);
	        stmt.execute(sql4);
	        //stmt.execute(sql2);
	        flag = true;
	        return flag;	 
	  }
	 
	 /**
	  * ���ò��Ա����
	  * @param al
	  * @return
	  * @throws SQLException
	  */
	 public Boolean SetStrategyPara(ArrayList<String> al) throws SQLException{
		 Boolean flag = false;
		 Statement stmt = null;
	     stmt=con.createStatement();
		
		 String name = al.get(0);
		 int cpu = Integer.parseInt(al.get(1));
		 int mem = Integer.parseInt(al.get(2));
		 String sql = "UPDATE strategy SET CPUUse="+cpu+" WHERE StrategyName="+"'"+name+"'";
		 String sql3 = "UPDATE strategy SET MEMUse="+mem+" WHERE StrategyName="+"'"+name+"'";
	     String sql1 = "lock tables strategy write";
	     String sql2 = "unlock tables";
	    // stmt.execute(sql1);
	     stmt.execute(sql);
	     stmt.execute(sql3);
	     //stmt.execute(sql2);
	     flag = true;
		 return flag;
	 }
	 
	 /**���µķ������ip��ַ,��Ҫƥ��������ͺ�����������exe����������
		 * @throws SQLException */
		public String getIpAddress(String type) throws SQLException{
			String ipAddress = null;
			Statement stmt = null;
			ResultSet rs =null;
			stmt = con.createStatement();
			String sql = new String();
			if(!type.equalsIgnoreCase("exe")){
			
			 if(type.equalsIgnoreCase("Web Service")) type = "Axis2";
			  else if(type.equalsIgnoreCase("Flow")) type = "Mule";
			  else if(type.equalsIgnoreCase("Restful")) type = "Tomcat";
			  else if(type.equalsIgnoreCase("Web Site")) type = "IIS";
			  sql = "SELECT HostIP FROM node WHERE ContainerName LIKE '%"+type+"%' and Status='connect'  ORDER BY ServiceNumber ASC";
			
			}
			else sql = "SELECT HostIP FROM node where Status='connect' ORDER BY ServiceNumber ASC";
			System.out.println(sql);
//			String sql1 = "lock tables host write";
//		    String sql2 = "unlock tables";
//		    stmt.execute(sql1);
			rs = stmt.executeQuery(sql);
			if(rs.next())
				ipAddress = rs.getString(1);
//			stmt.execute(sql2);
			
			System.out.println("ipAddress:"+ipAddress);
			return ipAddress;
		}
		
		
		/**��services�������һ����¼,���޸�duplicate���host���Ȳ����֮��������������������
		  * @throws SQLException  */
		/**
		 * ���޸ģ�
		 * @param serviceInfo
		 * @param ipAddress
		 * @throws SQLException
		 * @throws UnsupportedEncodingException
		 */
		public void addNewServiceItem(ArrayList<String> serviceInfo, String ipAddress) throws SQLException, UnsupportedEncodingException{
			

			String servcieid = serviceInfo.get(0);
			System.out.println("serviceid:"+servcieid);
			String serviceName = serviceInfo.get(1);
			System.out.println("servicename:"+serviceName);
			String version = serviceInfo.get(2);
			System.out.println("version:"+version);
			String serviceDescription = serviceInfo.get(3);
			System.out.println("serviceDescription:"+serviceDescription);
			String servicePublisher = serviceInfo.get(4);
			System.out.println("deployer:"+servicePublisher);
			int paramAmount = Integer.parseInt(serviceInfo.get(5));
			System.out.println("paramAmount:"+paramAmount);
//			String param = serviceInfo.get(6);
//			System.out.println("param:"+param);
			String paramType = serviceInfo.get(6);
			System.out.println("paramtype:"+paramType);
			String serviceType = serviceInfo.get(7);
			System.out.println("serviceType:"+serviceType);
			String businessType = serviceInfo.get(8);
			System.out.println("businessType:"+businessType);
			int CopyNumber = Integer.parseInt(serviceInfo.get(9));
			System.out.println("copynum:"+CopyNumber);
			int cnumber = Integer.parseInt(serviceInfo.get(10));
			System.out.println("cnumber:"+cnumber);
			String filename = serviceInfo.get(11);
			System.out.println("filename:"+filename);
			String ownerSystem = serviceInfo.get(12);
			System.out.println("ownerSystem:"+ownerSystem);
			String keywords = serviceInfo.get(13);
			System.out.println("keywords:"+keywords);
			String devlanauage = serviceInfo.get(14);
			System.out.println("devlanauage:"+devlanauage);
			String developer = serviceInfo.get(15);
			System.out.println("developer:"+developer);
			String Icon = serviceInfo.get(16);
			System.out.println("icon:"+Icon);
			String release =serviceInfo.get(17);
			System.out.println("release:"+release);
			long port = getport();
			Statement stmt = null;
			stmt = con.createStatement();
			//��������Ϣ��ӵ�services����
			String sql1 = "INSERT INTO services values ("+"'"+servcieid+"','"+serviceName+"','"+version+"','"+serviceDescription+"','"+
						servicePublisher+"',"+paramAmount+",'"+paramType+"','"+serviceType+"','"+businessType+"',"+CopyNumber+","+cnumber+",'"+filename+"','"+ownerSystem+"','"+keywords+"','"+devlanauage+"','"+developer+"','"+Icon+"',"+port+",'"+release+"')";
			System.out.println(sql1);
			//����������IP��ַ��ӵ�duplicate����
//			String wsdl = "http://"+ipAddress+":8080/axis2/services/"+serviceName+"?wsdl";
//			String sql2 = "INSERT INTO duplicate values ("+"'"+serviceName+"','"+ipAddress+"','"+wsdl+"','"+paramType+"','start')";
			
			
			//stmt.execute("lock tables services write");
			stmt.execute(sql1);
			//stmt.execute("unlock tables");
//			stmt.execute("lock tables duplicate write");
//			stmt.execute(sql2);
//			stmt.execute("unlock tables");
//			String sql4 = "update host set ServiceNumber = ServiceNumber+1 where HostIP="+"'"+ipAddress+"'";
//			stmt.execute("lock tables host write");
//			stmt.execute(sql4);
//			stmt.execute("unlock tables");
			System.out.println("��services���в����¼�¼�ɹ�");
//			System.out.println("��duplicate���в����¼�¼�ɹ�");
//			System.out.println("����host���������ɹ�");
			
		}
		//��������Ӧ�Ķ˿�
		private long getport() throws SQLException {
			// TODO Auto-generated method stub
			Statement stmt = null;
			ResultSet rs = null;
			HashSet set = new HashSet();
			stmt = con.createStatement();
			String sql = "select * from services";
			rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				long port = rs.getLong("Port");
				set.add(port);
			}
			long start =10000l;
			boolean flag = false;
			while(start < 65535l && !flag)
			{
				start++;
				flag = set.add(start);
				
			}
			return start;
		}
		/**��flow�������һ����¼, ���޸�duplicate���host��
		  * @throws SQLException */
		public void addNewFlowItem(ArrayList<String> flowInfo) throws SQLException{
			String flowName = flowInfo.get(0);
			System.out.println("flowname:"+flowName);
			String flowDescription = flowInfo.get(1);
			System.out.println("flowDescription:"+flowDescription);
			String flowPublisher = flowInfo.get(2);
			System.out.println("publisher:"+flowPublisher);
			String businessFlow = flowInfo.get(3);
			System.out.println("businessFlow:"+businessFlow);
			String serviceFlow = flowInfo.get(4);
			System.out.println("serviceFlow:"+serviceFlow);
			String businessType = flowInfo.get(5);
			int CopyNumber = Integer.parseInt(flowInfo.get(6));
			System.out.println("copynum:"+CopyNumber);
			
		
			Statement stmt = null;
			stmt = con.createStatement();
			//��������Ϣ��ӵ�flow����
			String sql1 = "INSERT INTO flow values ("+"'"+flowName+"','"+flowDescription+"','"+
						flowPublisher+"','"+businessFlow+"','"+serviceFlow+"','"+businessType+"',"+CopyNumber+")";
			if(businessFlow.equals("0"))
				sql1 = "INSERT INTO flow values ("+"'"+flowName+"','"+flowDescription+"','"+
						flowPublisher+"',null,'"+serviceFlow+"','"+businessType+"',"+CopyNumber+")";
			System.out.println(sql1);
			//����������IP��ַ��ӵ�duplicate����
//			String wsdl = serviceFlow;
//			String sql2 = "INSERT INTO duplicate values ("+"'"+flowName+"','"+ipAddress+"','"+wsdl+"','Flow','start')";
			
			
			//stmt.execute("lock tables flow write");
			stmt.execute(sql1);
			//stmt.execute("unlock tables");
//			stmt.execute("lock tables duplicate write");
//			stmt.execute(sql2);
//			stmt.execute("unlock tables");
//			String sql4 = "update host set ServiceNumber = ServiceNumber+1 where HostIP="+"'"+ipAddress+"'";
//			stmt.execute("lock tables host write");
//			stmt.execute(sql4);
//			stmt.execute("unlock tables");
			System.out.println("��flow���в����¼�¼�ɹ�");
//			System.out.println("��duplicate���в����¼�¼�ɹ�");
//			System.out.println("����host���������ɹ�");
			
		}
		
		/**
		 * ���뾯����־��Ϣ
		 * @param al
		 * @return
		 * @throws SQLException
		 */
		public Boolean InsertLog (ArrayList<String> al) throws SQLException{
		
			Statement stmt = null;
			Boolean flag = false;
			ResultSet rs = null;
			stmt = con.createStatement();
			int  num = 0;
			String time = al.get(0);
			String type = al.get(1);
			String content = al.get(2);
			String ip = al.get(3);
			String container = al.get(4);
			if(container.equals("0"))  container=null;
			String sql1 = "select count(*) from log ";
			//stmt.execute("lock tables log write");
			rs = stmt.executeQuery(sql1);
			rs.next();
			num = rs.getInt(1)+1;
			String sql = "INSERT INTO log values ("+"'"+num+"','"+time+"','"+type+"','"+content+"','"+ip+"','"+container+"',0)";
			stmt.execute(sql);
			//stmt.execute("unlock tables");
			flag = true;
			
			return flag;
		}
	 
		/**
		 * �г����̱��Ź�����Ҫ�������б�
		 * @return
		 * @throws SQLException
		 */
		public ArrayList<String[]> ListFlow() throws SQLException{
			ArrayList<String[]> al = new ArrayList<String[]>();
			ResultSet rs =null;
			Statement stmt = null;
			stmt = con.createStatement();
			String sql = "SELECT * FROM flow";
			//stmt.execute("lock tables flow write");
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String[] temp = new String[4];
				temp[0]=rs.getString(1);
				temp[1]=rs.getString(2);
				temp[2]=rs.getString(4);
				temp[3]=rs.getString(5);
				al.add(temp);
			}
			//stmt.execute("unlock tables");
			return al;
		}
		
		/**
		 * �г������������Ҫ�������б�
		 * @return
		 * @throws SQLException
		 */
		public ArrayList<String[]> ListFlow2() throws SQLException{
			ArrayList<String[]> al = new ArrayList<String[]>();
			ResultSet rs =null;
			Statement stmt = null;
			stmt = con.createStatement();
			String sql = "SELECT * FROM flow";
			//stmt.execute("lock tables flow write");
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String[] temp = new String[5];
				temp[0]=rs.getString(1);
				temp[1]="Flow";
				temp[2]=rs.getString(6);
				temp[3]=rs.getString(2);
				temp[4]=rs.getString(7);
				al.add(temp);
			}
			//stmt.execute("unlock tables");
			return al;
		}
		
		/**
		 * �����������б�
		 * @return
		 * @throws SQLException
		 */
		public ArrayList<String> ListFlowName() throws SQLException{
			ArrayList<String> flowNames = new ArrayList<String>();
			Statement stmt = null;
			ResultSet rs =null;
			stmt = con.createStatement();
			String sql = "SELECT FlowName FROM flow";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				flowNames.add(rs.getString(1));
			}
			return flowNames;
		}
		
		/**
		 * ��������������ȡ�������е�����IP
		 * @param name
		 * @return
		 * @throws SQLException
		 */
		public  String GetFlowIP(String name) throws SQLException{
			String IP = new String();
			Statement stmt = null;
			ResultSet rs =null;
			stmt = con.createStatement();
			String sql = "SELECT IPAddress FROM duplicate WHERE ServiceName='"+name+"'";
			rs = stmt.executeQuery(sql);
			rs.next();
			IP = rs.getString(1);
			return IP;
		}
		
		/**
		 * �޸�����״̬,��������״̬���趨����ϸ�����������������̵Ĺرվ��ǰѿ��ŵ���һ���ص�������������ע������ѡ��һ������
		 * @param al
		 * @return
		 * @throws SQLException
		 */
		public ArrayList<String> SetFlowStatus(String name ,String status) throws SQLException{
	    	Statement stmt = null;
	    	ResultSet rs = null;
	    	String sql = null;
	    	String sql3 = null;
	    	ArrayList<String> al = new ArrayList<String>();
			stmt=con.createStatement();
			if(status.equalsIgnoreCase("start"))
				sql = "UPDATE duplicate SET ServiceStatus='start' WHERE ServiceName="+"'"+name+"' limit 1";
			else
			    sql = "UPDATE duplicate SET ServiceStatus='stop' WHERE ServiceName="+"'"+name+"'";
			String sql1 = "lock tables duplicate write";
	        String sql2 = "unlock tables";
	        sql3 = "select IPAddress from duplicate where ServiceStatus='"+status+"' and ServiceName="+"'"+name+"'";
	        //stmt.execute(sql1);
		    stmt.execute(sql);
		    rs = stmt.executeQuery(sql3);
		    while(rs.next()){
		    	al.add(rs.getString(1));
		    }
			//stmt.execute(sql2);
			System.out.println("set flow status");
			return al;
			
	    }
		
		/**
		 * ���������
		 * @param al
		 * @return
		 * @throws SQLException
		 */
		public Boolean AddHost(ArrayList<String> al) throws SQLException{
			Boolean flag = false;
			System.out.println("��node���м�������");
			Statement stmt = null;
			stmt = con.createStatement();
			String ip = al.get(0);
			String name = al.get(1);
			String container = al.get(2);
			int num = Integer.parseInt(al.get(3));
			String mac = al.get(4);
			String system = al.get(5);
			String bit = al.get(6);
			String location = al.get(7);
			String description = al.get(8);
			String info = al.get(9);
			String sql = "INSERT INTO node values ("+"'"+ip+"','"+name+"','"+container+"',"+num+",0,'"+mac+"','"+location+"','"+system+"','"+bit+"','connect','"+description+"','"+info+"')";
			//stmt.execute("lock tables node write");
			System.out.println(sql);
			stmt.execute(sql);
			//stmt.execute("unlock tables");
			flag = true;
			return true;
		}
		
		
		public String SelectServiceType(String name) throws SQLException{
			ResultSet rs =null;
			Statement stmt = null;
			String type = null;
			stmt = con.createStatement();
			//stmt.execute("lock tables services write");
			String sql = "select ServiceType from services where FileName='"+name+"'";
			rs = stmt.executeQuery(sql);
			if(rs.next())
			 type = rs.getString(1);
			else type = "Flow";
            //stmt.execute("unlock tables");
			System.out.println("OK");
			return type;
		}
		
		
		
		//��ѯ���ݿ���е������Ϣ
				public ArrayList<String> queryFlowCompInfo(String name) throws SQLException{
					
					ArrayList<String> mArrayList=new ArrayList<String>();
					Statement stmt = null;
					ResultSet comreSet=null;
					stmt = con.createStatement();
					String sql = "select ServiceName,CompInfo from compinfo where CompName=";
					sql = sql+'"'+name+'"';
					//stmt.execute("lock tables compinfo write");
					comreSet = stmt.executeQuery(sql);
					try {
						while(comreSet.next()){
							mArrayList.add(comreSet.getString("CompInfo"));
							//System.out.println(compInfo);
							mArrayList.add(comreSet.getString("ServiceName"));
							//System.out.println(pre_serviceName);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					//stmt.execute("unlock tables");
					
					return mArrayList;
				}
				
				//��ѯ���ݿ���е���������Ϣ
				public String queryAdapterInfo(String preName,String postName,String flow_content) throws SQLException{
			
					Statement stmt = null;
					ResultSet rs1=null;
					stmt = con.createStatement();
					String sql = "select InputName,OutputName,AdapterInfo from adapterinfo";
					//stmt.execute("lock tables adapterinfo write");
					rs1 = stmt.executeQuery(sql);
					try {
						while(rs1.next()){
							if(preName.equals(rs1.getString("InputName")) && postName.equals(rs1.getString("OutputName"))){
								flow_content = flow_content +"\t\t"+ rs1.getString("AdapterInfo") + "\n\t\t";
								break;
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					//stmt.execute("unlock tables");
					
					return flow_content;
		}
			
		
		/**
		 * ������Ϣ��ȡչʾ		
		 * @return
		 * @throws SQLException
		 */
		public ArrayList<String[]> ListAlarmInfo() throws SQLException{
			Statement stmt = null;
			ResultSet rs=null;
			ArrayList<String[]> al = new ArrayList<String[]>();
			stmt = con.createStatement();
			String sql = "select * from log";
			//stmt.execute("lock tables log write");
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String[] temp = new String[7];
				temp[0]=rs.getString(1);
				temp[1]=rs.getString(2);
				temp[2]=rs.getString(3);
				temp[3]=rs.getString(4);
				temp[4]=rs.getString(5);
				temp[5]=rs.getString(6);
				temp[6]=rs.getString(7);
				al.add(temp);
			}
			//stmt.execute("unlock tables");
           return al;		
		}
		
		/**
		 * ������Ϣ��⣬�ӵ��÷���ķ��������������������Ϣ�����洢��
		 * @param al
		 * @throws SQLException
		 */
		public void AddCallInfo(ArrayList<String> al) throws SQLException{
			String Time = al.get(0);
			String ServiceName = al.get(1);
			String ServiceIP = al.get(2);
			String ServiceType = al.get(3);
			String Status = al.get(4);
			Statement stmt = null;
			ResultSet rs=null;
			stmt = con.createStatement();
			String sql = "select BusinessType from services where ServiceName = '"+ServiceName+"'";
			//stmt.execute("lock tables services write");
			rs = stmt.executeQuery(sql);
			rs.next();
			String BusinessType = rs.getString(1);
			//stmt.execute("unlock tables");
			String sql1 = "select count(*) from callinfo ";
			//stmt.execute("lock tables callinfo write");
			rs = stmt.executeQuery(sql1);
			rs.next();
			int num = rs.getInt(1)+1;
			String sql2 = "INSERT INTO callinfo values ("+""+num+",'"+Time+"','"+
					ServiceName+"','"+ServiceIP+"','"+ServiceType+"','"+BusinessType+"','"+Status+"')";
			stmt.execute(sql2);
			//stmt.execute("unlock tables");
			
		}
		
		/**
		 * �г����������Ϣ�����ظ�������
		 * @return
		 * @throws SQLException
		 */
		public ArrayList<String[]> ListCallInfo() throws SQLException{
			ArrayList<String[]> al = new ArrayList<String[]>();
			Statement stmt = null;
			ResultSet rs=null;
			stmt = con.createStatement();
			String sql = "select * from callinfo";
			//stmt.execute("lock table callinfo write");
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String[] temp = new String[7];
				temp[0]=String.valueOf(rs.getInt(1));
				temp[1]=rs.getString(2);
				temp[2]=rs.getString(3);
				temp[3]=rs.getString(4);
				temp[4]=rs.getString(5);
				temp[5]=rs.getString(6);
				temp[6]=rs.getString(7);
				al.add(temp);
			}
			//stmt.execute("unlock tables");
			return al;
			
		}
		
		/**
		 * �жϷ������Ƿ���ڣ��������̺������������з��񲻿�������
		 * @param name
		 * @return
		 * @throws SQLException
		 */
		public Boolean isNameExist(String name) throws SQLException{
			ArrayList<String> al = new ArrayList<String>();
			Statement stmt = null;
			ResultSet rs=null;
			stmt = con.createStatement();
			String sql = "select * from services where ServiceID='"+name+"'";
//			String sql1 = "select FlowName from flow";
//			//stmt.execute("lock tables services write");
//			rs = stmt.executeQuery(sql);
//			while(rs.next()) al.add(rs.getString(1));
//			//stmt.execute("unlock tables");
//			//stmt.execute("lock tables flow write");
//			rs = stmt.executeQuery(sql1);
//			while(rs.next()) al.add(rs.getString(1));
//			//stmt.execute("unlock tables");
//			if(al.contains(name)) return true;
//			else return false;
			rs = stmt.executeQuery(sql);
			if(rs.next())
				return true;
			return false;
		}
		
		/**
		 * ��������Ϣ���������
		 * @param al
		 * @throws SQLException
		 */
		public void AddServiceMessage(ArrayList<String[]> al) throws SQLException{
			Statement stmt = null;
			stmt = con.createStatement();
			String sql = null;
			//stmt.execute("lock tables servicemessage write");
			for(String[] temp :al){
				String ServiceName = temp[0];
				String MessageName = temp[1];
				String Keyword = temp[2];
				String Description = temp[3];
				String Package = temp[4];
				String Address = temp[5];
				sql = "INSERT INTO servicemessage values ("+"'"+ServiceName+"','"+
						MessageName+"','"+Keyword+"','"+Description+"','"+Package+"','"+Address+"')";
				System.out.println(sql);
				stmt.execute(sql);
			}
			//stmt.execute("unlock tables");
		}
		
		/**
		 * ���޸ģ�
		 * �б���ȫ��������Ϣ��Ϣ
		 * @return
		 * @throws SQLException
		 */
		public ArrayList<String[]> ListMessage() throws SQLException{
			ArrayList<String[]> al = new ArrayList<String[]>();
			Statement stmt = null;
			ResultSet rs=null;
			stmt = con.createStatement();
			String sql = "select * from servicemessage";
			//stmt.execute("lock table servicemessage write");
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String[] temp = new String[2];
				temp[0]=rs.getString(1);
				temp[1]=rs.getString(2)+";"+rs.getString(3)+";"+rs.getString(4)+";"+rs.getString(5)+";"+rs.getString(6);
				
				al.add(temp);
			}
			//stmt.execute("unlock tables");
			return al;
		}
		
		/**
		 * ϵͳ����ͨ���˺����ҵ���ϵͳ��Ҫ������ϵͳ��IP��ַ
		 * @param name
		 * @return
		 * @throws SQLException
		 */
		public String SelectServiceIP(String name) throws SQLException{
			Statement stmt = null;
			ResultSet rs=null;
			stmt = con.createStatement();
			String ip = new String();
			String sql = "select IPAddress from duplicate where ServiceID = '"+name+"'";
			//stmt.execute("lock tables duplicate write");
			rs = stmt.executeQuery(sql);
			
			if(rs.next()){
				ip = rs.getString(1);
				//stmt.execute("unlock tables");
				return ip;
			}
			
			else {
				//stmt.execute("unlock tables");
				return "";
			}
			
		}
		
		/**
		 * ���޸ģ�
		 * ��ȡ���������Ϣ
		 * @param sid
		 * @return
		 * @throws SQLException
		 */
		public String[] FeedBack(String sid) throws SQLException{
			String[] back = new String[2];
			back[0] = sid;
//			StringBuffer sb = new StringBuffer();
//			String[] temp = sname.split("_v");
//			if(temp.length==1){
//				back[0] = temp[0];
//				back[1] = "1.0";
//			}else{
//				for(int i=0;i<temp.length-1;i++){
//					sb.append(temp[i]);
//					}
//				back[0] = sb.toString();
//				back[1] = temp[temp.length-1];
//				}
			Statement stmt = null;
			ResultSet rs=null;
			String cmd = new String();
			stmt = con.createStatement();
//			//stmt.execute("lock tables services write");
//			String gettype = "select ServiceType from services where ServiceID='"+sid+"'";
//			rs = stmt.executeQuery(gettype);
//			rs.next();
//			String type = rs.getString(1);
//			//stmt.execute("unlock tables");
//			if(type.equalsIgnoreCase("RESTful")||type.equalsIgnoreCase("Web Service")){
//				cmd = "select WSDL from duplicate where ServiceID='"+sid+"'";
//				
//			}
//			else{
//				cmd = "select IPAddress from duplicate where ServiceID='"+sid+"'";
//				
//			}
			cmd = "select * from services where ServiceID='"+sid+"'";
			//stmt.execute("lock tables duplicate write");
			System.out.println(cmd);
			rs = stmt.executeQuery(cmd);
			if(rs.next())
				back[1] = ServiceRegistryNode.unifiedIP+rs.getLong("Port")+"/";
			
			else 
				back[1] = "";
			
			//stmt.execute("unlock tables");
			for(String a :back){
				System.out.print(a+"  ");
			}
			
			return back;
			
		}
	
		/**
		 * ���޸ģ�
		 * �жϸ��������Ƿ���ָ������������
		 * @param name
		 * @return
		 * @throws SQLException
		 */
	public boolean isDuplicateExist(String id) throws SQLException{
		boolean flag = true;
		Statement stmt = null;
		ResultSet rs=null;
		stmt = con.createStatement();
		//stmt.execute("lock tables duplicate write");
		String cmd = "select ServiceID from duplicate where ServiceID='"+id+"'";
		rs = stmt.executeQuery(cmd);
		if(rs.next()) {
			flag = false;
			System.out.println("�����ڸ������д�����Ϣ��");
		}else System.out.println("�����ڸ������в����ڣ�");
		return flag;
		
	}
	
	/**
	 * ��д�ģ������ɸѡ
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public HashSet<String> FilterService(String type) throws SQLException{
		HashSet<String> al = new HashSet<String>();
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs=null;
		//stmt.execute("lock tables services write");
		String cmd = "select "+type+" from services";
		System.out.println(cmd);
		rs = stmt.executeQuery(cmd);
		while(rs.next()){
			al.add(rs.getString(1));
		}
		//stmt.execute("unlock tables");	
		return al;
	}
	
	/**
	 * �����ӣ��ڵ��ɸѡ
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public HashSet<String> FilterNode(String type) throws SQLException{
		HashSet<String> al = new HashSet<String>();
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs=null;
		//stmt.execute("lock tables node write");
		String cmd = "select "+type+" from node";
		System.out.println(cmd);
		rs = stmt.executeQuery(cmd);
		while(rs.next()){
			al.add(rs.getString(1));
		}
		//stmt.execute("unlock tables");	
		return al;
	}
	
	/**
	 * ���������Ա�ɸѡ
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public HashSet<String> FilterStrategy(ArrayList<String> in) throws SQLException{
		HashSet<String> al = new HashSet<String>();
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs=null;
		String type = in.get(0);
		String strategy = in.get(1);
		//stmt.execute("lock tables strategy write");
		String cmd = "select "+type+" from strategy where StrategyType='"+strategy+"'";
		System.out.println(cmd);
		rs = stmt.executeQuery(cmd);
		while(rs.next()){
			al.add(rs.getString(1));
		}
		//stmt.execute("unlock tables");	
		return al;
	}
		
	/**
	 * ����Ӵ��룬����ǰ����Ϣ���з����ģ��ɸѡ	
	 * @param in
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> CFilterService(ArrayList<String> in) throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String type = in.get(0);
		String body = in.get(1);
		String sql = "select * from services where "+type+" LIKE '%"+body+"%' ";
		System.out.println(sql);
        String sql1 = "lock tables services write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[11];
			temp[0]=rs.getString(1);//id
			temp[1]=rs.getString(2);//serviceName
			temp[2]=rs.getString(3);//version
			temp[3]=rs.getString(4);//description
			temp[4]=rs.getString(8);//serviceType
			temp[5]=rs.getString(9);//businessType
			temp[6]=String.valueOf(rs.getInt(10));//copynumber
			temp[7]=String.valueOf(rs.getInt(11));//maxrunningnumber
			temp[8]=rs.getString(13);//ownersystem
			temp[10]=ServiceRegistryNode.unifiedIP+rs.getLong("Port")+"/";
			al.add(temp);
		}
		//stmt.execute(sql2);
		//stmt.execute("lock tables node write");
		String sql3 = "select count(*) from node where Status='connect' and ContainerName like '%";
		for(int i=0;i<al.size();i++)
		{
			rs = stmt.executeQuery(sql3+getContainerName(al.get(i)[4])+"%'");
			if(rs.next())
				al.get(i)[9] = rs.getString(1);
			else
				al.get(i)[9] = "0";
		}
		//stmt.execute("unlock tables");
		return al;
	
		
	}
	
	/**
	 * �����ӣ��ڵ��ģ����ѯ
	 * @param in
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> CFilterNode(ArrayList<String> in) throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String type = in.get(0);
		String body = in.get(1);
		String sql = "select * from node where "+type+" LIKE '%"+body+"%' ";
		System.out.println(sql);
        String sql1 = "lock tables node write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[8];
			temp[0]=rs.getString("HostName");//name
			temp[1]=rs.getString("description");//description
			temp[2]=rs.getString("HostIP");//ip
			temp[3]=rs.getString("MAC");//mac
			temp[4]=rs.getString("System")+"-"+rs.getString("Bit");//os+bit
			temp[5]=rs.getString("Location");//locate
			temp[6]=rs.getString("Type");//type
			temp[7]=rs.getString("Status");//status
			al.add(temp);
		}
		//stmt.execute(sql2);
		return al;
	
		
	}
	
	/**
	 * ����������ģ����ѯ
	 * @param in
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> CFilterStrategy(ArrayList<String> in) throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String type = in.get(0);
		String body = in.get(1);
		String strategy = in.get(2);
		String sql = "select * from strategy where "+type+" LIKE '%"+body+"%' and StrategyType = '"+strategy+"'";
		System.out.println(sql);
        String sql1 = "lock tables strategy write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[5];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(3);
			temp[2]=String.valueOf(rs.getFloat(5));
			temp[3]=String.valueOf(rs.getFloat(6));
			temp[4]=rs.getString(7);
			al.add(temp);
		}
		//stmt.execute(sql2);
		return al;
	
		
	}
	
	
	/**
	 * ����ӣ��ڵ���Ϣչʾ
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> ListNodeInfo() throws SQLException{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "select * from node";
		//stmt.execute("lock tables node write");
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[8];
			temp[0]=rs.getString("HostName");//name
			temp[1]=rs.getString("description");//description
			temp[2]=rs.getString("HostIP");//ip
			temp[3]=rs.getString("MAC");//mac
			temp[4]=rs.getString("System")+"-"+rs.getString("Bit");//os+bit
			temp[5]=rs.getString("Location");//locate
			temp[6]=rs.getString("Type");//type
			temp[7]=rs.getString("Status");//status
			al.add(temp);
		}
		//stmt.execute("unlock tables");
		return al;
		
	}
	
	/**
	 * ����ӣ�ɾ���ڵ�������Ϣ
	 * @param ipAddress
	 * @throws SQLException
	 */
	public void DeleteNode(String ipAddress) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "delete from node where HostIP = '"+ipAddress+"'";
		System.out.println(sql);
		//stmt.execute("lock tables node write");
		stmt.execute(sql);
		//stmt.execute("unlock tables");
	}
	public boolean nodeExist(String ip) throws SQLException
	{
		boolean flag = false;
		String sql = "select * from node where HostIP=?";
		ResultSet rs = connMgr.query(sql, ip);
		if(rs.next())
			flag = true;
		return flag;
	}
	/**
	 * ����ӽڵ�
	 * @param al
	 * @throws SQLException
	 */
	public void AddNodeInfo(ArrayList<String> al) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String ip = al.get(0);
		System.out.println("ip: "+ip);
		String name = al.get(1);
		System.out.println("name: "+name);
		String des = al.get(2);
		System.out.println("description: "+des);
		String cname = al.get(3);
		System.out.println("container name: "+cname);
		int cnum = Integer.parseInt(al.get(4));
		System.out.println("container num: "+cnum);
		int snum = 0;
		System.out.println("service num: 0");
		String mac = al.get(5);
		System.out.println("mac: "+mac);
		String loc = al.get(6);
		System.out.println("location: "+loc);
		String sys = al.get(7);
		System.out.println("system: "+sys);
		String bit = al.get(8);
		System.out.println("bit: "+bit);
		String status = "disconnect";
		System.out.println("status: desconnect");
		String type = al.get(9);
		System.out.println("type: "+type);
		//stmt.execute("lock tables node write");
		String sql = "insert into node values("+"'"+ip+"','"+name+"','"+cname+"',"+
						cnum+","+snum+",'"+mac+"','"+loc+"','"+sys+"','"+bit+"','"+status+"','"+des+"','"+type+"')";
		stmt.execute(sql);
		//stmt.execute("unlock tables");
		System.out.println("insert!");
		
		
	}
	
	/**
	 * ����ӣ��ڵ���Ϣ�޸�
	 * @param al
	 * @throws SQLException
	 */
	public void SetNodeInfo(ArrayList<String> al) throws SQLException{
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String name = al.get(0);
		String des = al.get(1);
		String ip = al.get(2);
		String os = al.get(3);
		String bit = al.get(4);
		String loc = al.get(5);
		String type = al.get(6);
		String mac = al.get(7);
		String sql = "update node set HostIP='"+ip+"',HostName='"+name+"',description='"+des+"',Location='"+loc+"',System='"+os+"',Bit='"+bit+"',Type='"+type+"' where MAC='"+mac+"'";
		System.out.println(sql);
		//stmt.execute("lock tables node write");
		stmt.execute(sql);
		//stmt.execute("unlock tables");
	}
		
		
	public static void main(String[] args) throws SQLException {
		UseUserTable u =new UseUserTable();
		ArrayList<String> body = new ArrayList<String>();
		body.add("service");
		body.add("sum");
		ArrayList<String[]> al = u.getFilterList(body);
		if(al.size()==0) System.out.println("null");
		else{
		for(String[] a :al){
			for(String s :a){
				System.out.print(s+"***");
			}
			System.out.println();
		}
		}
//		ArrayList al = u.GetIP();
//		for(int i=0;i<al.size();i++){
//			System.out.println(al.get(i));
//		}
//		ArrayList al = new ArrayList();
//		al.add("A");
//		al.add("10");
//		al.add("20");
//		u.SetStrategyPara(al);
//		System.out.println(u.SetServiceStatus(al));
//		u.SelectStrategy("A");
//		ArrayList al = new ArrayList();
//		al.add("AddService");
//		al.add(3);
//		u.SetDuplicateNum(al);
	}
//����
	public  ArrayList<String[]> getUndeployedService() throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		ResultSet rs=null;
		ArrayList<String[]> al = new ArrayList<String[]>();
		stmt = con.createStatement();
		String sql = "select * from services";
		rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			String[] str = new String[10];
			str[0] = rs.getString("ServiceID");
			str[1] = rs.getString("ServiceName");
			str[2] = rs.getString("Version");
			str[3] = rs.getString("Description");
			str[4] = rs.getString("BusinessType");
			str[5] = rs.getString("OwnerSystem");
			str[6] = rs.getString("KeyWords");
			str[7] = rs.getString("DevLanguage");
			str[8] = rs.getString("Developer");
			str[9] = rs.getString("ServiceType");
			al.add(str);
					
					
		}
		return al;
	}
	/**
	 * �ӿ������ҵ����������ͳ�ƣ��������Ϊ���������Լ���ǰʱ�̣�����ֵΪ��СΪ10�����顣����ÿ���ӵ�������
	 */
	public int[] monGetServiceCallInfo(String[] i) throws SQLException, ParseException {
		int[] mInt=new int[10];
		String bTypeName = i[0];
		String bTime = i[1];
		Statement stmt = null;
		long currentTime = Long.parseLong(bTime);
		long endTime = currentTime-600000;
		stmt = con.createStatement();
		//stmt.execute("lock tables callinfo write");
		String sql = "select * from callinfo where BusinessType ='"+bTypeName+"' and Time between '"+endTime+"' and '"+bTime+"'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			long mTimeString =Long.parseLong(rs.getString("Time"));
		//	System.out.println("..."+mTimeString);
			long temp = currentTime-mTimeString;
			if(temp<60000){
				mInt[0]++;
			}
			else if (temp<120000) {
				mInt[1]++;
			}

			else if (temp<180000) {
				mInt[2]++;
			}
			else if (temp<240000) {
				mInt[3]++;
			}
			else if (temp<300000) {
				mInt[4]++;
			}
			else if (temp<360000) {
				mInt[5]++;
			}
			else if (temp<420000) {
				mInt[6]++;
			}
			else if (temp<480000) {
				mInt[7]++;
			}
			else if(temp<540000){
				mInt[8]++;
			}
			else {
				mInt[9]++;
			}
		}
		
		//stmt.execute("unlock tables");
		return mInt;
		
	}
	/**
	 * �ӿ������ҵ����������ͳ�ƣ��������Ϊ��ǰʱ�̣�����ֵΪ��СΪ10�����顣����ÿ���ӵ�������
	 */
	public int[] monGetServiceCallInfos(String i) throws SQLException, ParseException {
		int[] mInt=new int[10];
		String bTime = i;
		Statement stmt = null;
		long currentTime = Long.parseLong(bTime);
		long endTime = currentTime-600000;
		stmt = con.createStatement();
		//stmt.execute("lock tables callinfo write");
		String sql = "select * from callinfo where Time between '"+endTime+"' and '"+bTime+"'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			long mTimeString =Long.parseLong(rs.getString("Time"));
			//System.out.println("..."+mTimeString);
			long temp = currentTime-mTimeString;
			if(temp<60000){
				mInt[0]++;
			}
			else if (temp<120000) {
				mInt[1]++;
			}

			else if (temp<180000) {
				mInt[2]++;
			}
			else if (temp<240000) {
				mInt[3]++;
			}
			else if (temp<300000) {
				mInt[4]++;
			}
			else if (temp<360000) {
				mInt[5]++;
			}
			else if (temp<420000) {
				mInt[6]++;
			}
			else if (temp<480000) {
				mInt[7]++;
			}
			else if(temp<540000){
				mInt[8]++;
			}
			else {
				mInt[9]++;
			}
		}
		
		//stmt.execute("unlock tables");
		return mInt;
		
	}
	/**
	 * ͳ�ƻ�Ծ��,����10�����Ծ�ķ���
	 * @param i 
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	public ArrayList<String[]> monActiveService(String[] i) throws ParseException, SQLException{
		ArrayList<String[]> mArrayList=new ArrayList<String[]>();
		Map<String,Integer> mMap=new HashMap<String, Integer>();
		String bTypeName = i[0];
		String bTime = i[1];
		Statement stmt = null;
		long currentTime =Long.parseLong(bTime);
		long endTime = currentTime-3600000;//һСʱ���ڣ���λ��ms
		stmt = con.createStatement();
		//stmt.execute("lock tables callinfo write,services write");
		String sql = "select callinfo.ServiceID,ServiceName from callinfo,services where callinfo.ServiceID=services.ServiceID and callinfo.BusinessType ='"+bTypeName+"' and Time between '"+endTime+"' and '"+bTime+"'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			String serviceName = rs.getString("ServiceID")+"_"+rs.getString("ServiceName");;
				if (mMap.containsKey(serviceName)) {
					mMap.put(serviceName, mMap.get(serviceName)+1);
				}
				else {
					mMap.put(serviceName, 1);
				}
			
		}
		//stmt.execute("unlock tables");
		int size = mMap.size();
		//ѡ��10�����
		for (int j = 0; j < size&&j<10; j++) {
			String[] temp = new String[2];
			String serviceName = getActiveServiceName(mMap);
			temp[0]=serviceName;
			temp[1]=mMap.get(serviceName).toString();
			mMap.remove(serviceName);
			mArrayList.add(temp);
		}

		return mArrayList;
	}
	//����ͳ�Ʒ��������ʱ��
	public ArrayList<Object> monServiceRunTime(String[] i) throws ParseException, SQLException{
		int[] count=new int[10];
		String bTypeName = i[0];
		String bTime = i[1];
		Statement stmt = null;
		double small = Double.MAX_VALUE,big = Double.MIN_VALUE,scope=-1;
		long currentTime =Long.parseLong(bTime);
		long endTime = currentTime-3600000;//һСʱ���ڣ���λ��ms
		stmt = con.createStatement();
		//stmt.execute("lock tables callinfo write");
		String sql = "select * from callinfo where BusinessType ='"+bTypeName+"' and Time between '"+endTime+"' and '"+bTime+"'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			double temp = rs.getDouble("EndTime")-rs.getDouble("Time");
			if(small >temp)
				small = temp;
			if(big <temp)
				big = temp;
		}
		rs = null;
		if((scope = big -small) < 0)
			return null;
		
		ArrayList<Object> al = new ArrayList<Object>();
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			double temp = rs.getDouble("EndTime")-rs.getDouble("Time");
			if(temp < small+scope*0.1)
			{
				count[0]++;
			}
			else if(temp < small +scope*0.2)
			{
				count[1]++;
			}
			else if(temp < small +scope*0.3)
			{
				count[2]++;
			}
			else if(temp < small +scope*0.4)
			{
				count[3]++;
			}
			else if(temp < small +scope*0.5)
			{
				count[4]++;
			}
			else if(temp < small +scope*0.6)
			{
				count[5]++;
			}
			else if(temp < small +scope*0.7)
			{
				count[6]++;
			}
			else if(temp < small +scope*0.8)
			{
				count[7]++;
			}
			else if(temp < small +scope*0.9)
			{
				count[8]++;
			}
			else if(temp < small +scope*1)
			{
				count[9]++;
			}
		}
		//stmt.execute("unlock tables");

		al.add(count);
		al.add(String.valueOf(small));
		al.add(String.valueOf(big));
		return al;
	}
	//����ͳ�Ʒ��������ʱ��
		public ArrayList<Object> monServiceRunTimes(String i) throws ParseException, SQLException{
			int[] count=new int[10];
			
			String bTime = i;
			Statement stmt = null;
			double small = Double.MAX_VALUE,big = Double.MIN_VALUE,scope=-1;
			long currentTime =Long.parseLong(bTime);
			long endTime = currentTime-3600000;//һСʱ���ڣ���λ��ms
			stmt = con.createStatement();
			//stmt.execute("lock tables callinfo write");
			String sql = "select * from callinfo where Time between '"+endTime+"' and '"+bTime+"'";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next())
			{
				double temp = rs.getDouble("EndTime")-rs.getDouble("Time");
				if(small >temp)
					small = temp;
				if(big <temp)
					big = temp;
			}
			rs = null;
			if((scope = big -small) < 0)
				return null;
			
			ArrayList<Object> al = new ArrayList<Object>();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				double temp = rs.getDouble("EndTime")-rs.getDouble("Time");
				if(temp < small+scope*0.1)
				{
					count[0]++;
				}
				else if(temp < small +scope*0.2)
				{
					count[1]++;
				}
				else if(temp < small +scope*0.3)
				{
					count[2]++;
				}
				else if(temp < small +scope*0.4)
				{
					count[3]++;
				}
				else if(temp < small +scope*0.5)
				{
					count[4]++;
				}
				else if(temp < small +scope*0.6)
				{
					count[5]++;
				}
				else if(temp < small +scope*0.7)
				{
					count[6]++;
				}
				else if(temp < small +scope*0.8)
				{
					count[7]++;
				}
				else if(temp < small +scope*0.9)
				{
					count[8]++;
				}
				else if(temp < small +scope*1)
				{
					count[9]++;
				}
			}
			//stmt.execute("unlock tables");

			al.add(count);
			al.add(String.valueOf(small));
			al.add(String.valueOf(big));
			return al;
		}
	
	/**
	 * �ӿ������ҵ����������ͳ�ƣ��������Ϊ���������Լ���ǰʱ�̣�����ֵΪ��СΪ10�����顣����ÿ���ӵ�������
	 */
	public int[] monGetSingleServiceCallInfo(String[] i) throws SQLException, ParseException {
		int[] mInt=new int[10];
		String bServiceName = i[0];
		String bTime = i[1];
		Statement stmt = null;
		long currentTime = Long.parseLong(bTime);
		long endTime = currentTime-600000;
		stmt = con.createStatement();
		//stmt.execute("lock tables callinfo write");
		String sql = "select * from callinfo where ServiceID ='"+bServiceName+"' and Time between '"+endTime+"' and '"+bTime+"'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			long mTimeString =Long.parseLong(rs.getString("Time"));
			//System.out.println("..."+mTimeString);
			long temp = currentTime-mTimeString;
			if(temp<60000){
				mInt[0]++;
			}
			else if (temp<120000) {
				mInt[1]++;
			}

			else if (temp<180000) {
				mInt[2]++;
			}
			else if (temp<240000) {
				mInt[3]++;
			}
			else if (temp<300000) {
				mInt[4]++;
			}
			else if (temp<360000) {
				mInt[5]++;
			}
			else if (temp<420000) {
				mInt[6]++;
			}
			else if (temp<480000) {
				mInt[7]++;
			}
			else if(temp<540000){
				mInt[8]++;
			}
			else {
				mInt[9]++;
			}
		}
		
		//stmt.execute("unlock tables");
		return mInt;
		
	}
	
	class SingleServiceData{
		double runTime;
		int count;
	}
	public double[] monSingleServiceRunTime(String[] i) throws ParseException, SQLException{
		ArrayList<SingleServiceData> mArrayList = new ArrayList<SingleServiceData>();
		for(int j=0;j<6;j++){
			SingleServiceData temp = new SingleServiceData();
			mArrayList.add(temp);
		}
		double[] count=new double[6];
		String bServiceName = i[0];
		String bTime = i[1];
		Statement stmt = null;
		long currentTime =Long.parseLong(bTime);
		long endTime = currentTime-3600000;//һСʱ���ڣ���λ��ms
		stmt = con.createStatement();
		//stmt.execute("lock tables callinfo write");
		String sql = "select * from callinfo where ServiceID ='"+bServiceName+"' and Time between '"+endTime+"' and '"+bTime+"'";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			
			
			double tempTime = currentTime-rs.getDouble("Time");
			
			switch ((int)(tempTime/600000)) {
			case 0:
				mArrayList.get(0).count++;
				mArrayList.get(0).runTime+=rs.getDouble("EndTime")-rs.getDouble("Time");
				break;
			case 1:
				mArrayList.get(1).count++;
				mArrayList.get(1).runTime+=rs.getDouble("EndTime")-rs.getDouble("Time");
				break;
			case 2:
				mArrayList.get(2).count++;
				mArrayList.get(2).runTime+=rs.getDouble("EndTime")-rs.getDouble("Time");
				break;
			case 3:
				mArrayList.get(3).count++;
				mArrayList.get(3).runTime+=rs.getDouble("EndTime")-rs.getDouble("Time");
				break;
			case 4:
				mArrayList.get(4).count++;
				mArrayList.get(4).runTime+=rs.getDouble("EndTime")-rs.getDouble("Time");
				break;
			default:
				mArrayList.get(5).count++;
				mArrayList.get(5).runTime+=rs.getDouble("EndTime")-rs.getDouble("Time");
				break;
			}
		}
		//stmt.execute("unlock tables");
		for(int j=0;j<6;j++)
		{
			if(mArrayList.get(j).count==0){
				count[j]=0;
			}
			else{	
				count[j]=mArrayList.get(j).runTime/mArrayList.get(j).count;
			}
		}

		return count;
	}
	public ArrayList<String[]> monGetServiceCopy(String name) throws SQLException{
		ArrayList<String[]> mArrayList = new ArrayList<String[]>();
		Statement stmt = null;
		stmt = con.createStatement();
//		stmt.execute("lock tables services write");
//		String sql = "select ServiceID from services where ServiceName ='"+name+"'";
//		ResultSet rs = stmt.executeQuery(sql);
//		String s=null;
//		if(rs.next())
//			s = rs.getString(1);
//		stmt.execute("unlock tables");
		String sql1="select IPAddress,HostName from duplicate,node where duplicate.IPAddress=node.HostIP and ServiceID =";
		//stmt.execute("lock tables duplicate write,node write");
		ResultSet rs = stmt.executeQuery(sql1+"'"+name+"'");
		
		while(rs.next()){
			String[] str = new String[2];
			str[0] = rs.getString(2);
			str[1] = rs.getString(1);
			mArrayList.add(str);
		}
		//stmt.execute("unlock tables");
		return mArrayList;
	}
	/**
	 * ���ط����丱����Ϣ���˴����Ը���Ϊ׼�������������ĳ�����񣬵��Ǹ�������û�������Ϣ������ʾ�˷���
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> monGetAllServiceInfoAndCopyInfo() throws SQLException{
//		ArrayList<String[]> mArrayList = new ArrayList<String[]>();
//		ArrayList<String[]> serviceArrayList= new ArrayList<String[]>();
//		Statement stmt = null;
//		stmt = con.createStatement();
//		//��ʹ������ ��ѯ����ֹ����
//		stmt.execute("lock tables duplicate write");
//		String sql1="select ServiceID,IPAddress from duplicate";
//		ResultSet rs = stmt.executeQuery(sql1);
//		
//		while(rs.next()){
//			String[] mString = new String[6];
//			//������id�ݴ�����һ��λ�ã�����һ�ű��н����滻
//			mString[5]=rs.getString(1);
//			mString[1]=rs.getString(2);
//			mArrayList.add(mString);
//		}
//		stmt.execute("unlock tables");
//		stmt.execute("lock tables services write");
//		String sql = "select ServiceID,ServiceName,ServiceType,BusinessType,OwnerSystem from services";
//		rs = stmt.executeQuery(sql);
//		while(rs.next()){
//			String[] mString = new String[5];
//			mString[0]=rs.getString(1);
//			mString[1]=rs.getString(2);
//			mString[2]=rs.getString(3);
//			mString[3]=rs.getString(4);
//			mString[4]=rs.getString(5);
//			serviceArrayList.add(mString);
//		}
//		stmt.execute("unlock tables");
//		for(int i=0;i<mArrayList.size();i++){
//			String serID=mArrayList.get(i)[0];
//			for(int j=0;j<serviceArrayList.size();j++){
//				//��ͨ������id�ҵ����ķ�����Ϣ
//				if(serviceArrayList.get(j)[5].equals(serID)){
//					mArrayList.get(i)[0]=serviceArrayList.get(j)[1];
//					mArrayList.get(i)[2]=serviceArrayList.get(j)[2];
//					mArrayList.get(i)[3]=serviceArrayList.get(j)[3];
//					mArrayList.get(i)[4]=serviceArrayList.get(j)[4];
//					break;
//				}
//			}
//		}
//		
//		
//		return mArrayList;
		ArrayList<String[]> mArrayList = new ArrayList<String[]>();
		//ArrayList<String[]> serviceArrayList= new ArrayList<String[]>();
		Statement stmt = null;
		stmt = con.createStatement();
		//��ʹ������ ��ѯ����ֹ����
		String sql = "select services.ServiceName,duplicate.IPAddress,services.ServiceType,services.BusinessType,services.OwnerSystem,services.ServiceID from services,duplicate where services.ServiceID=duplicate.ServiceID";
		
		//stmt.execute("lock tables duplicate write,services write");
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			String[] str = new String[6];
			str[0] = rs.getString(1);
			str[1] = rs.getString(2);
			str[2] = rs.getString(3);
			str[3] = rs.getString(4);
			str[4] = rs.getString(5);
			str[5] = rs.getString(6);
			mArrayList.add(str);
		}
		//stmt.execute("unlock tables");
		return mArrayList;
	}
	/**
	 * ��ȡ��ʷCPU�Լ��ڴ���Ϣ
	 * @param ip ����ip
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String[]> monGetNodeHistoryCPURAM(String ip) throws SQLException{
		ArrayList<String[]> mArrayList=new ArrayList<String[]>();
		Statement stmt = null;
		stmt = con.createStatement();
		//stmt.execute("lock tables noderealtimeinfo write");
		String sql1="select Time,CPU,MEM from noderealtimeinfo where HostIP='"+ip+"' order by Time desc";
		
		ResultSet rs = stmt.executeQuery(sql1);
		int i=0;
		while(rs.next()&&i<10){
			String[] temp = new String[3];
			temp[0]=rs.getString(1);
			temp[1]=rs.getString(2);
			temp[2]=rs.getString(3);
			mArrayList.add(temp);
			i++;
		}
		//stmt.execute("unlock tables");
		return mArrayList;
	}
	/**
	 * ��ȡҵ������3.2.1
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<String> monGetBusinessType() throws SQLException{
		ArrayList<String> mArrayList = new ArrayList<String>();
		Statement stmt = null;
		stmt = con.createStatement();
		//stmt.execute("lock tables services write");
		String sql1="select BusinessType from services group by BusinessType";
		
		ResultSet rs = stmt.executeQuery(sql1);
		while(rs.next()){
			mArrayList.add(rs.getString(1));
		}
		return mArrayList;
	}
	
	private String getActiveServiceName(Map<String, Integer>  mMap) {
		String serviceName="";
		int max=0;
		for(String key:mMap.keySet()){
			if(mMap.get(key)>max){
				serviceName=key;
			}
		}
		return serviceName;
	}

	public Boolean changeLogStatus(ArrayList<String> list) throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		stmt = con.createStatement();
		int lognum = Integer.valueOf(list.get(0));
		String status = list.get(1);
		
		String sql = "update log set Status='"+status+"' where LogNumber="+lognum;
		int count = stmt.executeUpdate(sql);
		if(count == 1)
			return true;
		return false;
	}

	public ArrayList<String[]> CFilterLog(ArrayList<String> in) throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		ArrayList<String[]> al = new ArrayList<String[]>();
		if(in.get(0).equalsIgnoreCase("LogType"))
		{
			//stmt.execute("lock tables log write");
			System.out.println("select * from log where LogType='"+in.get(1)+"'");
			rs = stmt.executeQuery("select * from log where LogType='"+in.get(1)+"'");
			
			while(rs.next())
			{
				String[] str = new String[7];
				str[0] = rs.getString(1);
				str[1] = rs.getString(2);
				str[2] = rs.getString(3);
				str[3] = rs.getString(4);
				str[4] = rs.getString(5);
				str[5] = rs.getString(6);
				str[6] = rs.getString(7);
				al.add(str);
				
			}
			//stmt.execute("unlock tables");
		}
		else
		{
			//stmt.execute("lock tables log write");
			rs = stmt.executeQuery("select * from log where LogContent like '%"+in.get(1)+"%'");
			while(rs.next())
			{
				String[] str = new String[7];
				str[0] = rs.getString(1);
				str[1] = rs.getString(2);
				str[2] = rs.getString(3);
				str[3] = rs.getString(4);
				str[4] = rs.getString(5);
				str[5] = rs.getString(6);
				str[6] = rs.getString(7);
				al.add(str);
				
			}
			//stmt.execute("unlock tables");
		}
		return al;
	}

	public int getSizeNum(String body) throws SQLException {
		// TODO Auto-generated method stub\
		int num = 0;
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		String sql = "select count(*) from ";
		if(body.equalsIgnoreCase("service"))
			sql = sql + "services";
		else
			sql = sql +"node";
		rs = stmt.executeQuery(sql);
		if(rs.next())
			num = rs.getInt(1);
		return num;
	}

	public int getHostSize(String body) throws SQLException {
		// TODO Auto-generated method stub
		int num = 0;
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		String container = getContainerName(body);
		String sql = "select count(*) from node where Status='connect' and ContainerName like '%"+container+"%'";
		rs = stmt.executeQuery(sql);
		if(rs.next())
			num = rs.getInt(1);
		return num;
	}

	public int getFilterSize(ArrayList<String> body) throws SQLException {
		// TODO Auto-generated method stub
		int num = 0;
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		String sql = "select count(*) from ";
		if(body.get(0).equalsIgnoreCase("service"))
			sql = sql + "services where "+body.get(1)+" like '%"+body.get(2)+"%'";
		else
			sql = sql + "node where "+body.get(1)+" like '%"+body.get(2)+"%'";
		rs = stmt.executeQuery(sql);
		if(rs.next())
			num = rs.getInt(1);
		return num;
	}

	public String getLogSize() throws SQLException {
		// TODO Auto-generated method stub
		int num = 0;
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		
		String sql = "select count(*) from log";
		rs = stmt.executeQuery(sql);
		if(rs.next())
			num = rs.getInt(1);
		return String.valueOf(num);
		
	}

	public String getFilterLogSize(ArrayList<String> body) throws SQLException {
		// TODO Auto-generated method stub
		int num = 0;
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		String sql = "select count(*) from ";
		if(body.get(0).equalsIgnoreCase("LogType"))
			sql = sql + "log where LogType='"+body.get(1)+"'";
		else
			sql = sql + "log where LogContent like '%"+body.get(1)+"%'";
		rs = stmt.executeQuery(sql);
		if(rs.next())
			num = rs.getInt(1);
		return String.valueOf(num);
	}
	public String getRestfulURI(String serviceid,String ip) throws SQLException
	{
		String result = "";
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		String sql = "select WSDL from duplicate where ServiceID='"+serviceid+"' and IPAddress='"+ip+"'";
		rs = stmt.executeQuery(sql);
		if(rs.next())
			result = rs.getString(1);
		connMgr.close(rs);
		connMgr.close(stmt);
		if(result.equals(""))
		{
			return null;
		}
		else
		{
			String[] str = result.split(":8080");
			return str[str.length-1];
		}
	}
	public void deleteDuplicateItem(String serviceid,String ip) throws SQLException
	{
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		String sql2 = "delete from duplicate where IPAddress='"+ip+"' and ServiceID='"+serviceid+"'";
		stmt.execute(sql2);
		connMgr.close(rs);
		connMgr.close(stmt);
	}

	public ArrayList<String[]> getFilterList(ArrayList<String> body) throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		ArrayList<String[]> al = new ArrayList<String[]>();
		if(body.get(0).equalsIgnoreCase("service"))
		{
			if(body.get(1).equalsIgnoreCase("sum"))
			{
				
				String sql = "select * from services";
				rs = stmt.executeQuery(sql);
				while(rs.next()){
					String[] temp = new String[11];
					temp[0]=rs.getString(1);//id
					temp[1]=rs.getString(2);//serviceName
					temp[2]=rs.getString(3);//version
					temp[3]=rs.getString(4);//description
					temp[4]=rs.getString(8);//serviceType
					temp[5]=rs.getString(9);//businessType
					temp[6]=String.valueOf(rs.getInt(10));//copynumber
					temp[7]=String.valueOf(rs.getInt(11));//maxrunningnumber
					temp[8]=rs.getString(13);//ownersystem
					temp[10]=ServiceRegistryNode.unifiedIP+rs.getLong("Port")+"/";
					al.add(temp);
				}
				String sql3 = "select count(*) from node where Status='connect' and ContainerName like '%";
				for(int i=0;i<al.size();i++)
				{
					rs = stmt.executeQuery(sql3+getContainerName(al.get(i)[4])+"%'");
					if(rs.next())
						al.get(i)[9] = rs.getString(1);
					else
						al.get(i)[9] = "0";
				}
			}
			else if(body.get(1).equalsIgnoreCase("running"))
			{
				ArrayList<String> services = new ArrayList<String>();
				//�õ����еķ���
				String sql = "select DISTINCT ServiceID from duplicate where ServiceStatus='start'";
				rs = stmt.executeQuery(sql);
				while(rs.next())
					services.add(rs.getString(1));
				//��ȡ�����б�
				String sql1 = "select * from services where ServiceID='";
				for(int i=0;i<services.size();i++)
				{
					rs = stmt.executeQuery(sql1+services.get(i)+"'");
					if(rs.next()){
						String[] temp = new String[11];
						temp[0]=rs.getString(1);//id
						temp[1]=rs.getString(2);//serviceName
						temp[2]=rs.getString(3);//version
						temp[3]=rs.getString(4);//description
						temp[4]=rs.getString(8);//serviceType
						temp[5]=rs.getString(9);//businessType
						temp[6]=String.valueOf(rs.getInt(10));//copynumber
						temp[7]=String.valueOf(rs.getInt(11));//maxrunningnumber
						temp[8]=rs.getString(13);//ownersystem
						temp[10]=ServiceRegistryNode.unifiedIP+rs.getLong("Port")+"/";
						al.add(temp);
					}
				}
				//��ȡ�����������ĸ�������
				String sql3 = "select count(*) from node where Status='connect' and ContainerName like '%";
				for(int i=0;i<al.size();i++)
				{
					rs = stmt.executeQuery(sql3+getContainerName(al.get(i)[4])+"%'");
					if(rs.next())
						al.get(i)[9] = rs.getString(1);
					else
						al.get(i)[9] = "0";
				}
			}
			else if(body.get(1).equalsIgnoreCase("stop"))
			{

				ArrayList<String> services = new ArrayList<String>();
				//�õ�stop�ķ���
				String sql = "select DISTINCT ServiceID from duplicate where ServiceStatus='stop'";
				rs = stmt.executeQuery(sql);
				while(rs.next())
					services.add(rs.getString(1));
				//��ȡ�����б�
				String sql1 = "select * from services where ServiceID='";
				for(int i=0;i<services.size();i++)
				{
					rs = stmt.executeQuery(sql1+services.get(i)+"'");
					if(rs.next()){
						String[] temp = new String[11];
						temp[0]=rs.getString(1);//id
						temp[1]=rs.getString(2);//serviceName
						temp[2]=rs.getString(3);//version
						temp[3]=rs.getString(4);//description
						temp[4]=rs.getString(8);//serviceType
						temp[5]=rs.getString(9);//businessType
						temp[6]=String.valueOf(rs.getInt(10));//copynumber
						temp[7]=String.valueOf(rs.getInt(11));//maxrunningnumber
						temp[8]=rs.getString(13);//ownersystem
						temp[10]=ServiceRegistryNode.unifiedIP+rs.getLong("Port")+"/";
						al.add(temp);
					}
				}
				//��ȡ�����������ĸ�������
				String sql3 = "select count(*) from node where Status='connect' and ContainerName like '%";
				for(int i=0;i<al.size();i++)
				{
					rs = stmt.executeQuery(sql3+getContainerName(al.get(i)[4])+"%'");
					if(rs.next())
						al.get(i)[9] = rs.getString(1);
					else
						al.get(i)[9] = "0";
				}
			
			}else{//�Ͽ�

				ArrayList<String> services = new ArrayList<String>();
				//�õ��Ͽ��ķ���
				String sql = "select * from services where ServiceID not in(select DISTINCT ServiceID from duplicate)";
				rs = stmt.executeQuery(sql);
				while(rs.next())
					services.add(rs.getString(1));
				//��ȡ�����б�
				String sql1 = "select * from services where ServiceID='";
				for(int i=0;i<services.size();i++)
				{
					rs = stmt.executeQuery(sql1+services.get(i)+"'");
					if(rs.next()){
						String[] temp = new String[11];
						temp[0]=rs.getString(1);//id
						temp[1]=rs.getString(2);//serviceName
						temp[2]=rs.getString(3);//version
						temp[3]=rs.getString(4);//description
						temp[4]=rs.getString(8);//serviceType
						temp[5]=rs.getString(9);//businessType
						temp[6]=String.valueOf(rs.getInt(10));//copynumber
						temp[7]=String.valueOf(rs.getInt(11));//maxrunningnumber
						temp[8]=rs.getString(13);//ownersystem
						temp[10]=ServiceRegistryNode.unifiedIP+rs.getLong("Port")+"/";
						al.add(temp);
					}
				}
				//��ȡ�����������ĸ�������
				String sql3 = "select count(*) from node where Status='connect' and ContainerName like '%";
				for(int i=0;i<al.size();i++)
				{
					rs = stmt.executeQuery(sql3+getContainerName(al.get(i)[4])+"%'");
					if(rs.next())
						al.get(i)[9] = rs.getString(1);
					else
						al.get(i)[9] = "0";
				}
			
			}
		}
		else
		{
			if(body.get(1).equalsIgnoreCase("sum"))
			{
				
			}
			else if(body.get(1).equalsIgnoreCase("running"))
			{
				
			}
			else{
				
			}
		}
		return al;
	}

	public long getFilterListSize(ArrayList<String> body) throws SQLException {
		
		// TODO Auto-generated method stub
		long count=0;
		Statement stmt = null;
		stmt = con.createStatement();
		ResultSet rs = null;
		ArrayList<String[]> al = new ArrayList<String[]>();
		if(body.get(0).equalsIgnoreCase("service"))
		{
			if(body.get(1).equalsIgnoreCase("sum"))
			{
				
				String sql = "select count(*) from services";
				rs = stmt.executeQuery(sql);
				if(rs.next()){
					count = rs.getLong(1);
				}
				
			}
			else if(body.get(1).equalsIgnoreCase("running"))
			{
				
				
				String sql = "select count(DISTINCT ServiceID) from duplicate where ServiceStatus='start'";
				rs = stmt.executeQuery(sql);
				if(rs.next())
					count=rs.getLong(1);
			
				
			}
			else if(body.get(1).equalsIgnoreCase("stop"))
			{

			
				String sql = "select count(DISTINCT ServiceID) from duplicate where ServiceStatus='stop'";
				rs = stmt.executeQuery(sql);
				if(rs.next())
					count=rs.getLong(1);
				
			
			}else{//�Ͽ�

				
				String sql = "select count(*) from services where ServiceID not in(select DISTINCT ServiceID from duplicate)";
				rs = stmt.executeQuery(sql);
				if(rs.next())
					count=rs.getLong(1);
				
			
			}
		}
		else
		{
			if(body.get(1).equalsIgnoreCase("sum"))
			{
				
			}
			else if(body.get(1).equalsIgnoreCase("running"))
			{
				
			}
			else{
				
			}
		}
		
		return count;
	}
//��ȡĳ�����״̬
	public String getServiceStatus(String serviceid) throws SQLException {
		// TODO Auto-generated method stub
		String result;
		Statement stmt = null;
		
		stmt = con.createStatement();
		ResultSet rs = null;
		ArrayList<String> al  = new ArrayList<String>();
		String sql = "select distinct ServiceStatus from duplicate where ServiceID='"+serviceid+"'";
		rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			al.add(rs.getString(1));
		}
		if(al.size()<=0)
			result ="unknow";
		else{
			if(al.contains("start")){
				result = "start";
			}
			else if(al.contains("stop"))
			{
				result = "stop";
			}
			else{
				result = "disconnect";
			}
		}
		return result;
	}
//��ȡ�����Ӧ�Ķ˿�
	public HashMap<String, String> getServiceMap() throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		HashMap<String,String> hm = new HashMap<String,String>();
		stmt = con.createStatement();
		ResultSet rs = null;
		String sql = "select * from services";
		rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			String serviceid = rs.getString("ServiceID");
			String port = rs.getString("Port");
			hm.put(port, serviceid);
		}
		return hm;
	}
//���뵽callinfo
	public void AddCallInfo1(ArrayList<String> al) throws SQLException {
		// TODO Auto-generated method stub

//		String Time = al.get(0);
//		String ServiceName = al.get(1);
//		String ServiceIP = al.get(2);
//		String ServiceType = al.get(3);
//		String Status = al.get(4);
		String start = al.get(0);
		String end = al.get(1);
		String agentIP = al.get(2);
		String serviceid = al.get(3);
		String status = al.get(4);
		
		Statement stmt = null;
		ResultSet rs=null;
		stmt = con.createStatement();
		String sql = "select ServiceType,BusinessType from services where ServiceID = '"+serviceid+"'";
		//stmt.execute("lock tables services write");
		rs = stmt.executeQuery(sql);
		rs.next();
		String ServiceType = rs.getString(1);
		String BusinessType = rs.getString(2);
		//stmt.execute("unlock tables");
//		String sql1 = "select count(*) from callinfo ";
//		//stmt.execute("lock tables callinfo write");
//		rs = stmt.executeQuery(sql1);
//		rs.next();
//		int num = rs.getInt(1)+1;
		String sql2 = "INSERT INTO callinfo(No,Time,EndTime,ServiceID,ServiceIP,ServiceType,BusinessType,Status) values (NULL,"+start+","+end+",'"+
				serviceid+"','"+agentIP+"','"+ServiceType+"','"+BusinessType+"','"+status+"')";
		stmt.execute(sql2);
		//stmt.execute("unlock tables");
		
	
	}

	public ArrayList<String> getServiceAddressInfo(String serviceid) throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs=null;
		stmt = con.createStatement();
		String sql = "select ServiceID,ServiceType,Port from services where ServiceID = '"+serviceid+"'";
		//stmt.execute("lock tables services write");
		rs = stmt.executeQuery(sql);
		if(rs.next())
		{
			result.add(rs.getString(1));
			result.add(rs.getString(2));
			result.add(rs.getString(3));
		}
		return result;
	}

	public boolean addServiceAdapter(ArrayList<String> serviceadapter) {
		// TODO Auto-generated method stub
		boolean flag = false;
		
		String serviceid = serviceadapter.get(0);
		System.out.println("serviceid:"+serviceid);
		String protocol = serviceadapter.get(1);
		System.out.println("protocol:"+protocol);
		String inprotocol = serviceadapter.get(2);
		System.out.println("inprotocol:"+inprotocol);
		String indataconvert = serviceadapter.get(3);
		System.out.println("indataconvert:"+indataconvert);
		String outdataconvert = serviceadapter.get(4);
		System.out.println("outdataconvert:"+outdataconvert);
		String outprotocol = serviceadapter.get(5);
		System.out.println("outprotocol:"+outprotocol);
		String xmlcontent = serviceadapter.get(6);
		System.out.println("xmlcontent:"+xmlcontent);
		Statement stmt = null;
		ResultSet rs=null;
		String sql =null;
		try{
		stmt = con.createStatement();
		sql = "insert into serviceadapter values('"+serviceid+"','"
														  +protocol+"','"
														  +inprotocol+"','"
														  +indataconvert+"','"
														  +outdataconvert+"','"
														  +outprotocol+"','"
														  +xmlcontent+"')";
		flag = stmt.execute(sql);
		}catch(Exception e)
		{
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public ArrayList<Object> getDKYServiceInfo() throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		ResultSet rs=null;
		String sql =null;
		ArrayList<Object> result = new ArrayList<Object>();
		ArrayList<String[]> al =null;
		
		stmt = con.createStatement();
		
		//serviceType
		sql = "select ServiceType,count(ServiceID) from services group by ServiceType";
		al = new ArrayList<String[]>();
		rs = stmt.executeQuery(sql);
		
		while(rs.next())
		{
			String[] item = new String[2];
			item[0] = rs.getString(1);
			item[1] = rs.getString(2);
			al.add(item);
		}
		rs.close();
		result.add(al);
		//buinessType
		sql = "select BusinessType,count(BusinessType) from services group by BusinessType";
		al = new ArrayList<String[]>();
		rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			String[] item = new String[2];
			item[0] = rs.getString(1);
			item[1] = rs.getString(2);
			al.add(item);
		}
		rs.close();
		result.add(al);
		// servicestatus
//		sql = "select distinct(ServiceID,ServiceStatus) from duplicate";
		sql = "select ServiceStatus,count(distinct ServiceID) from duplicate group by ServiceStatus";
		al = new ArrayList<String[]>();
		rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			String[] item = new String[2];
			item[0] = rs.getString(1);
			item[1] = rs.getString(2);
			al.add(item);
		}
		rs.close();
		result.add(al);
		return result;
	}
	public ArrayList<String[]> getDKYServiceList() throws SQLException
	{
		ArrayList<String[]> al = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "SELECT * FROM services";
        String sql1 = "lock tables services write";
        String sql2 = "unlock tables";
        //stmt.execute(sql1);
		rs = stmt.executeQuery(sql);
		while(rs.next()){
			String[] temp = new String[7];
			temp[0]=rs.getString("ServiceID");//id
			temp[1]=rs.getString("ServiceName");
			temp[2]=rs.getString("ServiceType");//serviceType
			temp[3]=rs.getString("BusinessType");//businessType
			temp[4] = rs.getString("ReleaseTime");
			temp[5]=rs.getString("OwnerSystem");//ownersystem
			al.add(temp);
		}
		for(String[] strs:al)
		{
			strs[6] = getServiceStatus(strs[0]);
		}
		return al;
		
	}
//����ͳ�ƴ���
	public ArrayList<String[]> ServiceCallStatistics(String param) throws ParseException, SQLException {
		// TODO Auto-generated method stub
		ArrayList<String[]> al = new ArrayList<String[]>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		Date start ;
		Date end;
		//��ȡ��ǰʱ��
		Date date = new Date();
		int year;
		int month;
		int day;
		c.setTime(date);
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
//		System.out.println("year:"+year+"month:"+month+"day:"+day);
		if(param.equalsIgnoreCase("month"))
		{
			
			
			for(int i=0;i<12;i++)
			{
				String[] item = new String[2];
				
				if(month+1-i <=0)
				{
					start = sdf.parse((year-1)+"-"+(12+month+1-i)+"-"+"1 00:00:00");
					item[0] = (year-1)+"-"+(12+month+1-i);
				}
				else
				{
					start = sdf.parse(year+"-"+(month+1-i)+"-"+"1 00:00:00");
					item[0] = year+"-"+(month+1-i);
				}
				if(month+2 -i <=0)
				{
					end = sdf.parse((year-1)+"-"+(12+month+2-i)+"-"+"1 00:00:00");
				}
				else
				{
					end = sdf.parse(year+"-"+(month+2-i)+"-"+"1 00:00:00");
				}
				if(i==0 && month+2==13)
				{
					end = sdf.parse((year+1)+"-"+1+"-"+"1 00:00:00");
				}
					
				System.out.println("start:"+start+";end:"+end);
				long startlong = start.getTime();
				long endlong = end.getTime();
				long count = getServicesCallCount(startlong,endlong);
				
				//
				
				item[1] = ""+count;
				al.add(item);
				
			}
		}
		else if(param.equalsIgnoreCase("week"))
		{

			
			
			for(int i=0;i<7;i++)
			{
				String[] item = new String[2];
				
				
				
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
				item[0] =year+"-"+(month+1)+"-"+day;
				start = sdf.parse(year+"-"+(month+1)+"-"+day+" 00:00:00");
				end = sdf.parse(year+"-"+(month+1)+"-"+day+" 23:59:59");
//				System.out.println("start:"+start+";end:"+end);
				long startlong = start.getTime();
				long endlong = end.getTime();
				long count = getServicesCallCount(startlong,endlong);
				
				//
				
				item[1] = ""+count;
				al.add(item);
				c.add(Calendar.DAY_OF_MONTH, -1);
			}
		}
		return al;
	}
	public  long getServicesCallCount(long start,long end) throws SQLException
	{
		long l = 0 ;
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "select count(*) from callinfo where Time between '"+start+"' and '"+end+"'";
//		System.out.println(sql);
		rs = stmt.executeQuery(sql);
		if(rs.next())
			l = rs.getLong(1);
		return l;
		
	}
	//������Ӧʱ��(
	public ArrayList<String[]> ServiceDelayStatistics() throws SQLException, ParseException
	{
		//��λʱ��
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		Date start ;
		Date end;
		//��ȡ��ǰʱ��
		Date date = new Date();
		int year;
		int month;
		int day;
		ArrayList<String[]> al = new ArrayList<String[]>();
		for(int i=0;i<7;i++)
		{
			String[] item = new String[2];
			
			
			
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			item[0] =year+"-"+(month+1)+"-"+day;
			start = sdf.parse(year+"-"+(month+1)+"-"+day+" 00:00:00");
			end = sdf.parse(year+"-"+(month+1)+"-"+day+" 23:59:59");
//			System.out.println("start:"+start+";end:"+end);
			long startlong = start.getTime();
			long endlong = end.getTime();
			double count = getServiceDelayStatistics(startlong,endlong);
			
			//
			
			item[1] = ""+count;
			al.add(item);
			c.add(Calendar.DAY_OF_MONTH, -1);
		}
		
		return al;
	}
	
	public double getServiceDelayStatistics(long start,long end) throws SQLException
	{
		long sum =0;
		long count=0;
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "select Time,EndTime from callinfo where Time between '"+start+"' and '"+end+"'";
		rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			long time= rs.getLong(1);
			long endtime = rs.getLong(2);
			sum += (endtime-time);
			count ++;
		}
		if(count == 0)
			return -1;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		
		return Double.valueOf(nf.format(sum/(double)count));
	}

	public ArrayList<String> getMuleContainerIPs() throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<String> al = new ArrayList<String>();
		Statement stmt = null;
		ResultSet rs =null;
		stmt=con.createStatement();
		String sql = "select HostIP from node where Status='start' and ContainerName like '%Mule%'";
		rs = stmt.executeQuery(sql);
		while(rs.next())
		{
			al.add(rs.getString(1));
		}
		return al;
	}
}
