package rgscenter.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Service_Interface {

	public boolean generateClass(String serviceName,String interfaceName,String serviceType){
		boolean flag = true;
		try {
			String path = System.getProperty("user.dir") + File.separator + serviceName + File.separator + "src" + File.separator;
			File f = new File(path);
			if(!f.exists()){
				f.mkdirs();
			}
			BufferedReader br = new BufferedReader(new FileReader("ServiceName_InterfaceName.java"));
			FileWriter fw = new FileWriter(new File(path + serviceName + "_" + interfaceName + ".java"));
			String line ;
			String str = "";
			while((line = br.readLine()) != null){
				str = str + line + "\n";
			}
			//将文件中的内容替换
			str = str.replace("ServiceName", serviceName);
			str = str.replace("InterfaceName", interfaceName);
			str = str.replace("ServiceType", serviceType);
			
			fw.write(str);
			fw.flush();
			
			br.close();
			fw.close();
			
		} catch (IOException e) {
			System.out.println("服务接口的java文件生成失败");
			flag = false;
			return flag;
		}
		return flag;
	}
	
	public static void main(String[] args) {
//		try {
//			DB db = DB.getInstance();
//			db.getConnection();
//			String[] param = {"sid1"};
//			String sql = "select ServiceName,InterfaceName,ServiceType from interface,services where interface.ServiceId=services.ServiceId and services.ServiceId=?";
//			ResultSet rs = db.query(sql,param);
//			String serviceName = null,
//					interfaceName = null,
//					serviceType = null;
//			Service_Interface si = new Service_Interface();
//			while(rs.next()){
//				serviceName = rs.getString(1);
//				interfaceName = rs.getString(2);
//				serviceType = rs.getString(3);
//				si.generateClass(serviceName, interfaceName, serviceType);
//			}
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		
	}

}
