package rgscenter.domain;
import java.sql.*;

public class DBConnectionManager {
	static private DBConnectionManager instance; // The single instance
	private static Connection con;
	private static String user = Config.newInstance().getAttribute("user");//�û���
	private static String password = Config.newInstance().getAttribute("password");//����
	private static final String drivername = "com.mysql.jdbc.Driver";//����
	private static final String url = Config.newInstance().getAttribute("url");//���ݿ���Ϊtest
	//���ݿ����Ӷ���
	//private Connection conn=null;
		//���ݿ�Ԥ�������
	private PreparedStatement ps=null;
		//�����
	private ResultSet rs=null;

	static public DBConnectionManager getInstance() {
		if (instance == null) {
			instance = new DBConnectionManager();
		}
		return instance;
	}

	private DBConnectionManager() {
		try {
			Class.forName(drivername);
		} catch (ClassNotFoundException e1) {
			System.out.println("driver����");
		}
		try {
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("���ӳ���");
		}
	}

	public Connection getConnection() {
		return con;
	}
	
	public void close()
	{
		if(rs!=null)
		{
			try{
				rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(ps!=null)
		{
			try{
				ps.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void closeAll()
	{
		if(rs!=null)
		{
			try{
				rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(ps!=null)
		{
			try{
				ps.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(con!=null)
		{
			try{
				con.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**  
     * @param sql���ݿ����(����ɾ����) ���      
     * @param pras�����б��ɴ����ɲ���������ΪNULL����������ʽ���ڣ�  
     * @return ������Ӱ�춼����  
     */
	public int update(String sql,String... pras){  
        int resu=0;  
        PreparedStatement pps=null;
        //conn=getConn();  
        try {  
            pps=con.prepareStatement(sql);  
            for(int i=0;i<pras.length;i++){  
                pps.setString(i+1,pras[i]);  
            }  
            resu=pps.executeUpdate();  
        } catch (SQLException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } 
        //�ͷ���Դ
        finally{
	        if(pps!=null)
			{
				try{
					pps.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
        }
        return resu;  
    }  
	/**  
     * @param sql���ݿ��ѯ���  
     * @param pras�����б��ɴ����ɲ���������ΪNULL����������ʽ���ڣ�  
     * @return ���ؽ����  
     */ 
	//�������� �����ŵ���close�رճ�conn����Դ
    public ResultSet query(String sql,String... pras){  
        //conn=getConn();  
        try {  
            ps=con.prepareStatement(sql);  
  
            if(pras!=null)  
                for(int i=0;i<pras.length;i++){  
                    ps.setString(i+1, pras[i]);  
                }  
            rs=ps.executeQuery();  
        } catch (SQLException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return rs;  
    } 
    public void close(PreparedStatement pps)
    {
    	if(pps != null)
			try {
				pps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    public void close(Statement pps)
    {
    	if(pps != null)
			try {
				pps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    public void close(ResultSet pps)
    {
    	if(pps != null)
			try {
				pps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    public void createTable(String sql)
    {
    	Statement stat =null;
    	try
    	{
    		stat = con.createStatement();
    		stat.execute(sql);
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    	}
    	//�ر���Դ
    	if(stat !=null)
    	{
    		try {
				stat.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    public void printResultSetElement() throws SQLException
    {
    	if(rs!=null)
    	{
    		while(rs.next())
    		{
    			System.out.println(rs.getString(1));
    		}
    	}
    }
    public void locktable(String table)
    {
    	
    	Statement stat = null; 
    	try{
	    	stat = con.createStatement();
	    	//String sql1 = "lock tables "+table+" read";
	    	String sql2 = "lock tables "+table+" write";
	    	//stat.execute(sql1);
	    	stat.execute(sql2);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally{
    		if(stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    }
    public void unlocktable(String table)
    {
    	Statement stat = null; 
    	try{
	    	stat = con.createStatement();
	    	//String sql1 = "lock tables "+table+" read";
	    	String sql2 = "unlock tables";
	    	//stat.execute(sql1);
	    	stat.execute(sql2);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally{
    		if(stat != null)
				try {
					stat.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    }
}
