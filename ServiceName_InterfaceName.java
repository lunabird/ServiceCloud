import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import Message.Message;
import Message.MsgType;


public class ServiceName_InterfaceName implements Callable{

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		Object[] param = eventContext.getMessage().getPayload(Object[].class);
		
		boolean flag = false;
		String ret = null;
		
		do{
			//����Ӧ���������ע�����ģ���ע�����ķ������󣬵õ���Ҫ���ʵķ������ڵ�����ip
			//�����ip��port��ע�����ĵ�ip�Ͷ˿�
			String ip = "localhost";
			int port = 9090;
			Socket socket = new Socket(ip,port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ArrayList<Object> obj = new ArrayList<Object>();
			//���ͷ�����������
			obj.add("ServiceName");
			obj.add("ServiceType");
			//�����������,SERVICEREQUEST����
			Message outMes=new Message(MsgType.SERVICEREQUEST,obj);
			oos.writeObject(outMes);
			oos.flush();
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Message msg = (Message) ois.readObject();
			if(msg.getType() == MsgType.SERVICERRESPONSE){
				System.out.println("��Ϣ��ʽƥ��");
				ip = (String) msg.getBody();
				System.out.println(ip);
				
				socket.close();
				
				if(ip != null){
					//����������Ǵ�������ĳ�����ϵķ��񣬽�������õĽ������
					//����Ķ˿�port�Ǵ���������÷���Ķ˿�
					int port_1 = 9091;
					Socket socket_1 = new Socket(ip , port_1);
					ObjectOutputStream oos_1 = new ObjectOutputStream(socket_1.getOutputStream());
					ArrayList<Object> obj_1 = new ArrayList<Object>();
					obj_1.add("ServiceName");
					obj_1.add("ServiceType");
					obj_1.add("InterfaceName");
					obj_1.add(param);
					//������ͷ����������,SERVICEREQUEST����
					Message outMes_1 = new Message(MsgType.ServiceCall,obj_1);
					oos_1.writeObject(outMes_1);
					oos_1.flush();
					
					ObjectInputStream ois_1 = new ObjectInputStream(socket_1.getInputStream());
					Message msg_1 = (Message) ois_1.readObject();
					if(msg_1.getType() == MsgType.ServiceCallResult){
						System.out.println("��Ϣ��ʽƥ��");
						ret = (String) msg_1.getBody();
						
						flag = true;
					}
					else{
						//���������ҵ������Ƿ������ʧ��
						System.out.println("��Ϣ��ʽ��ƥ��");
						flag = false;
					}
					
					socket_1.close();
					
				}				
			}
			else{
				System.out.println("��Ϣ��ʽ��ƥ��");
				flag = false;
			}
		}while(flag == false);
		
		
		return ret;
	}
}
