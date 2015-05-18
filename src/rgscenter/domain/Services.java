package rgscenter.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Services {
	private String name;//服务名称
	private String  ipAddress;//服务节点IP
	private int serviceAmount;//服务副本数
	private String wsdllocation;//wsdl文件位置
	
	public Services getServices(String wsdllocation) throws UnknownHostException{
		serviceAmount = 1;
		int position1 = wsdllocation.indexOf("//");
		int position2 = wsdllocation.indexOf(':', position1);
		ipAddress = wsdllocation.substring(position1+2, position2);

		int position3 = wsdllocation.indexOf("services/");
		int position4 = wsdllocation.indexOf('?');
		name = wsdllocation.substring(position3+9, position4);
		this.wsdllocation = wsdllocation;
//		System.out.println(name);
		return this;
	}
	
	
	public String getName(String wsdllocation){
		int position3 = wsdllocation.indexOf("services/");
		int position4 = wsdllocation.indexOf('?');
		name = wsdllocation.substring(position3+9, position4);
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getIpAddress(String wsdllocation){
		int position1 = wsdllocation.indexOf("//");
		int position2 = wsdllocation.indexOf(':', position1);
		ipAddress = wsdllocation.substring(position1+2, position2);
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress){
		this.ipAddress = ipAddress;
	}
	
	public int getServiceAmount(){
		return serviceAmount;
	}
	
	public void setServicesAmount(int serviceAmount){
		this.serviceAmount = serviceAmount;
	}
	
	public String getWsdllocation(){
		return wsdllocation;
	}
	
	public void setWsdllocation(String wsdllocation){
		this.wsdllocation = wsdllocation;
	}
	
/*	public static void main(String[] args) throws UnknownHostException{
		String wsdl1="http://192.168.0.1:7000/axis2/services/Test?wsdl";
		Services s1 = new Services(wsdl1);	
	}
*/	
}
