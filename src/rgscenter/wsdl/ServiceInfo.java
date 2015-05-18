package rgscenter.wsdl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exolab.castor.xml.schema.Schema;

import rgscenter.domain.ServerNodeInfo;

public class ServiceInfo {
	/** ������Ϣ��������ڵ�*/
	private ServerNodeInfo serverNode;
	/** ������*/
	private String name;
	/** WSDL�ļ�λ�� */
	private String wsdllocation;	
	private String endpoint;
	private String targetnamespace;
	private Schema wsdlType;	
	private List operations = new ArrayList(); //The list of operations that this service defines.
	
	/** ��ʾ������Ϣ*/
	public void show()
	{
		System.out.println("��������: "+name);
		System.out.println("�����ַ: "+wsdllocation);
		System.out.println("����targetNamespace��"+targetnamespace);	
		System.out.println("�ṩ���²���:");
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
