package rgscenter.wsdl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.xml.schema.Schema;

import rgscenter.domain.ServerNodeInfo;

public class ServiceInfo {
	/** 服务信息所属服务节点*/
	private ServerNodeInfo serverNode;
	/** 服务名*/
	private String name;
	/** WSDL文件位置 */
	private String wsdllocation;	
	private String endpoint;
	private String targetnamespace;
	private Schema wsdlType;	
	private List operations = new ArrayList(); //The list of operations that this service defines.
	
	/** 显示服务信息*/
	public void show()
	{
		System.out.println("服务名称: "+name);
		System.out.println("服务地址: "+wsdllocation);
		System.out.println("服务targetNamespace："+targetnamespace);	
		System.out.println("提供如下操作:");
		Iterator iter = operations.iterator();
		while(iter.hasNext())
		{
			OperationInfo oper = (OperationInfo) iter.next();
			oper.show();
		}
	}
	
	public Schema getWsdlType() {
		return wsdlType;
	}

	public void setWsdlTypes(Schema wsdlType) {
		this.wsdlType=wsdlType;
	}

	public List getOperation() {
		return operations;
	}
	
	public Iterator getOperations(){
	   return	operations.iterator();
	}
	public void addOperation(OperationInfo operation) {
	      operations.add(operation);
	}
	public String toString(){
	      return getName();
    }
	public String getTargetnamespace() {
		return targetnamespace;
	}
	public void setTargetnamespace(String targetnamespace) {
		this.targetnamespace = targetnamespace;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getWsdllocation() {
		return wsdllocation;
	}
	public void setWsdllocation(String wsdllocation) {
		this.wsdllocation = wsdllocation;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
