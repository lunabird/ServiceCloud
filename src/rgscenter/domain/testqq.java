package rgscenter.domain;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

public class testqq {
	public static void main(String[] args) throws SQLException, UnknownHostException{
		UseUserTable u = new UseUserTable();
//		u.insertAllServerNodesTable("http://219.245.68.144:8080/axis2/services/AddService?wsdl");
//		u.appendText("http://219.245.68.144:8080/axis2/services/SubService?wsdl");
//		ArrayList<String> iplist = 
//				u.getOtherServernodes("http://219.245.68.144:8080/axis2/services/SubService?wsdl");
//		System.out.println(iplist);
//		ArrayList<ServiceItem> list=u.clientGetServiceList1();
//		System.out.println(list.toString());
		ArrayList<String[]> list=u.getUserInfo();
		for(int i=0;i<list.size();i++)
			for(int j=0;j<3;j++)
				System.out.println(list.get(i)[j]);
	}
}
