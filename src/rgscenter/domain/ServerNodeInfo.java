package rgscenter.domain;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import rgscenter.wsdl.ServiceInfo;

public class ServerNodeInfo {
	private InetAddress  inetAddress;// 服务节点IP地址
	private String serverName;			// 服务节点名称
	private List<ServiceInfo> services;// 服务节点包含的服务列表
	
	/** 显示服务节点相关信息*/
	public void show()
	{
		System.out.println("当前服务节点名称为："+serverName);
		Iterator iter = services.iterator();
		while(iter.hasNext()) 
		{
			ServiceInfo serviceinfo = (ServiceInfo)iter.next();
			serviceinfo.show();
		}
	}
}
