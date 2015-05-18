package rgscenter.wsdl;

import java.util.Iterator;
import java.util.List;

import rgscenter.wsdl.*;

public class WSClient {

	public static void testWSDL4J() {
		int i = 0, j = 0;

		try {
			WSDLParser builder = new WSDLParser();
			ServiceInfo serviceInfo = new ServiceInfo();
			String wsdllocation = "http://localhost:8080/axis2/services/AddService?wsdl";
			serviceInfo.setWsdllocation(wsdllocation);
			serviceInfo = builder.buildserviceinformation(serviceInfo);
			System.out.println("");
			Iterator iter = serviceInfo.getOperations();
			System.out.println("���ڿ��Բ鿴Զ��Web���������й������(��Ӧ����Web������,ServiceInfo)");
			System.out.println(serviceInfo.getName() + "�ṩ�Ĳ�����:");
			while (iter.hasNext()) {
				i++;
				OperationInfo oper = (OperationInfo) iter.next();
				System.out.println("");
				System.out.println("����:" + i + " " + oper.getTargetMethodName());
				List inps = oper.getInparameters();
				List outps = oper.getOutparameters();
				if (inps.size() == 0) {
					System.out.println("�˲���������������Ϊ:");
					System.out.println("ִ�д˲�������Ҫ�����κβ���!");
				} else {
					System.out.println("�˲���������������Ϊ:");
					for (Iterator iterator1 = inps.iterator(); iterator1.hasNext();) {
						ParameterInfo element = (ParameterInfo) iterator1.next();
						System.out.println("������Ϊ:" + element.getName());
						System.out.println("��������Ϊ:" + element.getKind());
					}
				}
				if (outps.size() == 0) {
					System.out.println("ִ�д˲����������κβ���!");
				} else {
					System.out.println("�˲������������Ϊ:");
					for (Iterator iterator2 = outps.iterator(); iterator2.hasNext();) {
						ParameterInfo element = (ParameterInfo) iterator2.next();
						System.out.println("������:" + element.getName());
						System.out.println("����Ϊ:" + element.getKind());
					}
				}
				System.out.println("");
			}
		}

		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		WSClient.testWSDL4J();
	}

}
