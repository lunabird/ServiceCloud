package rgscenter.domain;
import java.sql.*;

public class DBConnectionManager {
	static private DBConnectionManager instance; // The single instance
	private static Connection con;
	private static String user = Config.newInstance().getAttribute("user");//用户名
	private static String password = Config.newInstance().getAttribute("password");//密码
	private static final String drivername = "com.mysql.jdbc.Driver";//驱动
	private static final String url = Config.newInstance().getAttribute("url");//数据库名为test
	//数据库连接对象
	//private Connection conn=null;
		//数据库预编译对象
	private PreparedStatement ps=null;
		//结果集
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
			System.out.println("driver出错");
		}
		try {
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("连接出错");
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
     * @param sql数据库更新(增、删、改) 语句      
     * @param pras参数列表（可传，可不传，不传为NULL，以数组形式存在）  
     * @return 返回受影响都行数  
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
        //释放资源
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
     * @param sql数据库查询语句  
     * @param pras参数列表（可传，可不传，不传为NULL，以数组形式存在）  
     * @return 返回结果集  
     */ 
	//处理玩结果 紧接着调用close关闭除conn的资源
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
    	//关闭资源
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
