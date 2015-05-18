package rgscenter.domain;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import rgscenter.wsdl.ServiceInfo;

public class ServerNodeInfo {
	private InetAddress  inetAddress;// ����ڵ�IP��ַ
	private String serverName;			// ����ڵ�����
	private List<ServiceInfo> services;// ����ڵ�����ķ����б�
	
	/** ��ʾ����ڵ������Ϣ*/
	public void show()
	{
		System.out.println("��ǰ����ڵ�����Ϊ��"+serverName);
		Iterator iter = services.iterator();
		while(iter.hasNext()) 
		{
			ServiceInfo serviceinfo = (ServiceInfo)iter.next();
			serviceinfo.show();
		}
	}
}
