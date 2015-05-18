package rgscenter.wsdl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rgscenter.domain.ServiceRegistryNode;
import rgscenter.wsdl.ServiceInfo;
import rgscenter.wsdl.WSDLParser;

public class WSDLSet implements Serializable{
	
private static final long serialVersionUID = -5240152692961888097L;

    private List<String> wsdllocations;
   
    public  WSDLSet()
    {
    	wsdllocations = new ArrayList<String>();
    }
    
    public void show()
    {
    	Iterator iter = wsdllocations.iterator();
    	while (iter.hasNext())
    	{
    		System.out.println(iter.next());
    	}
    }
    public List<String> getWsdllocations() {
        return wsdllocations;
    }
    public void setWsdllocations(List<String> wsdls) {
        this.wsdllocations = wsdls;
    }
    
    public void addWsdllocation(String location)
    {
    	this.wsdllocations.add(location);
    }
    
    public void deleteWsdllocations(){
    	wsdllocations.clear(); 
    }

    
    // 添加服务信息
    public List<ServiceInfo> GetServiceInfosFromLocations()
    {
    	ArrayList<ServiceInfo> serviceSet = new ArrayList<ServiceInfo>();
    	Iterator iter = wsdllocations.iterator();
    	while (iter.hasNext())
    	{
    		WSDLParser builder = new WSDLParser();
			ServiceInfo serviceInfo = new ServiceInfo();
			String wsdllocation = (String)iter.next();
			serviceInfo.setWsdllocation(wsdllocation);
			try {
				serviceInfo = builder.buildserviceinformation(serviceInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			serviceSet.add(serviceInfo);
    	}
    	return serviceSet;
    }
}
