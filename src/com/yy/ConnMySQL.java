package com.yy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class ConnMySQL {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		ConnMySQL co = new ConnMySQL();
		co.getConn();
		String sql = "select * from ServerInfo";
		co.query(sql);
		
		co.printResultSetElement();
		co.closeAll();
		System.out.println("over");
	}
	//���ݿ���������
	public static final String DRIVER="com.mysql.jdbc.Driver";
	//���ݿ����ӵ�ַ
	public static final String URL="jdbc:mysql://127.0.0.1:3306/test";
	//��¼��
	public static final String USER="root";
	//��¼����
	public static final String PWD="2080";
	//���ݿ����Ӷ���
	private Connection conn=null;
	//���ݿ�Ԥ�������
	private PreparedStatement ps=null;
	//�����
	private ResultSet rs=null;
	
	public Connection getConn(){
		try
		{
			Class.forName(DRIVER);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		try
		{
			conn = DriverManager.getConnection(URL,USER,PWD);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return conn;
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
		if(conn!=null)
		{
			try{
				conn.close();
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
        //conn=getConn();  
        try {  
            ps=conn.prepareStatement(sql);  
            for(int i=0;i<pras.length;i++){  
                ps.setString(i+1,pras[i]);  
            }  
            resu=ps.executeUpdate();  
        } catch (SQLException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        } 
        //�ͷ���Դ
        if(ps!=null)
		{
			try{
				ps.close();
			}catch(Exception e)
			{
				e.printStackTrace();
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
            ps=conn.prepareStatement(sql);  
  
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
    public void createTable(String sql)
    {
    	Statement stat =null;
    	try
    	{
    		stat = conn.createStatement();
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
}
