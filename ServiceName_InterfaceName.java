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
			//这里应该请求的是注册中心，向注册中心发送请求，得到所要访问的服务所在的主机ip
			//下面的ip和port是注册中心的ip和端口
			String ip = "localhost";
			int port = 9090;
			Socket socket = new Socket(ip,port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ArrayList<Object> obj = new ArrayList<Object>();
			//发送服务名和类型
			obj.add("ServiceName");
			obj.add("ServiceType");
			//服务调用请求,SERVICEREQUEST类型
			Message outMes=new Message(MsgType.SERVICEREQUEST,obj);
			oos.writeObject(outMes);
			oos.flush();
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Message msg = (Message) ois.readObject();
			if(msg.getType() == MsgType.SERVICERRESPONSE){
				System.out.println("消息格式匹配");
				ip = (String) msg.getBody();
				System.out.println(ip);
				
				socket.close();
				
				if(ip != null){
					//这里请求的是代理，访问某主机上的服务，将服务调用的结果返回
					//这里的端口port是代理监听调用服务的端口
					int port_1 = 9091;
					Socket socket_1 = new Socket(ip , port_1);
					ObjectOutputStream oos_1 = new ObjectOutputStream(socket_1.getOutputStream());
					ArrayList<Object> obj_1 = new ArrayList<Object>();
					obj_1.add("ServiceName");
					obj_1.add("ServiceType");
					obj_1.add("InterfaceName");
					obj_1.add(param);
					//向代理发送服务调用请求,SERVICEREQUEST类型
					Message outMes_1 = new Message(MsgType.ServiceCall,obj_1);
					oos_1.writeObject(outMes_1);
					oos_1.flush();
					
					ObjectInputStream ois_1 = new ObjectInputStream(socket_1.getInputStream());
					Message msg_1 = (Message) ois_1.readObject();
					if(msg_1.getType() == MsgType.ServiceCallResult){
						System.out.println("消息格式匹配");
						ret = (String) msg_1.getBody();
						
						flag = true;
					}
					else{
						//服务主机找到，但是服务调用失败
						System.out.println("消息格式不匹配");
						flag = false;
					}
					
					socket_1.close();
					
				}				
			}
			else{
				System.out.println("消息格式不匹配");
				flag = false;
			}
		}while(flag == false);
		
		
		return ret;
	}
}
