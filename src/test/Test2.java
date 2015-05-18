package test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import rgscenter.domain.DBConnectionManager;

public class Test2 {
	public long getport() throws SQLException {
		// TODO Auto-generated method stub
		Statement stmt = null;
		ResultSet rs = null;
		HashSet set = new HashSet();
//		stmt = con.createStatement();
		stmt = DBConnectionManager.getInstance().getConnection().createStatement();
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
	public static void main(String[] args) throws SQLException
	{
		System.out.println(new Test2().getport());
	}
}
