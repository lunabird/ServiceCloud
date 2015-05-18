package test;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import rgscenter.domain.UseUserTable;

import com.yy.ServiceHeaper;

public class Test3 {
	public static void main(String[] args) throws SQLException, ParseException
	{
//		ArrayList<String[]> al = new ArrayList<String[]>();
//		String[] temp;
//		for(int i=0;i<10;i++)
//		{
//			temp = new String[7];
//			temp[0] = String.valueOf(System.currentTimeMillis());
//			temp[1] = String.valueOf(System.currentTimeMillis());
//			temp[2] = "9.23";
//			temp[3] = "192.170.20.125";
//			temp[4] = "webapp";
//			temp[5] = "情报服务";
//			temp[6] = "success";
//			al.add(temp);
//		}
//		ServiceHeaper.insertCallinfo(al);
//		System.out.println("over");
//		
		
		
		
//		 UseUserTable u = new UseUserTable();
//			 ArrayList<String[]> al ;
//			 al = u.getDKYServiceList();
//			for(String[] strs:al)
//			{
//				for(String s:strs)
//				{
//					System.out.println(s+"	");
//				}
//				System.out.println();
//			}
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date d = sdf.parse("2011-1-18 14:22:00");
////		System.out.println("simple date format:"+d);
////		Date date = new Date();
////		System.out.println(date);
//		Calendar c = Calendar.getInstance();
////		
//		c.setTime(d);
//		c.add(Calendar.MONTH,-1);
//		System.out.println(c.getTime());
//		System.out.println(c.get(Calendar.MONTH));
//		System.out.println(c.get(Calendar.YEAR));
//		System.out.println(c.get(Calendar.DAY_OF_MONTH));
//		
//		c.add(Calendar.MONTH,-1);
//		System.out.println(c.getTime());
		 ArrayList<String[]> al ;
		UseUserTable u = new UseUserTable();
//		 al = u.ServiceCallStatistics("week");
//		for(String[] strs:al)
//		{
//			for(String s:strs)
//			{
//				System.out.println(s+"	");
//			}
//			System.out.println();
//		}
//		System.out.println(new Date());
// System.out.println(u.getServicesCallCount(1412696415179l, 1413696415179l));
//		 System.out.println(u.ServiceDelayStatistics());
		
		File file = new File("F:\\服务集成\\SerConAgent");
		System.out.println(file.length());
	}
}
