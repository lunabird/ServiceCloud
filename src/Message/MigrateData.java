package Message;

import java.io.Serializable;

public class MigrateData implements Serializable{
	private String serviceName;
	private String ip;
	private int port;
	
	public MigrateData()
	{
		
	}
	
	public MigrateData(String serviceName, String ip, int port)
	{
		this.serviceName = serviceName;
		this.ip = ip;
		this.port = port;
	}
	public void show()
	{
		System.out.println("服务名称："+serviceName);
		System.out.println("ip地址："+ip);
		System.out.println("端口号："+port);
	}
	public void setServiceName(String name)
	{
		serviceName = name;
	}
	public String getServiceName()
	{
		return serviceName;
	}
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	public String getIp()
	{
		return ip;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public int getPort()
	{
		return port;
	}
}
