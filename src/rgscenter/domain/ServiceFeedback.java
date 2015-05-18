package rgscenter.domain;

import java.io.IOException;
import java.sql.SQLException;

public class ServiceFeedback extends Thread {
	private String id = null;
	private boolean flag = true;
	private UseUserTable u = new UseUserTable();
	private String[] back = new String[3];
	public ServiceFeedback(String id){
		this.id = id;
		this.start();
	}
	public void run(){
		//启线程查询副本表中是否有了想要查找的服务
		while(flag){
			try {
				this.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("进入回填信息查询线程");
			try {
				System.out.println(id);
				flag = u.isDuplicateExist(id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		//循环跳出，表示副本表中已经查到了相应的服务信息
		//调用方法，构造发给全局地址服务的回填信息并发送
		System.out.println("副本表中已找到记录");
		try {
			this.back = u.FeedBack(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("回填信息已构造完成");
		//向全局地址服务发送回填信息
		QuickStart qs = new QuickStart();
		try {
			qs.post(back);
			System.out.println("post请求已收到回应");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("服务信息回填完成");
	}
	public static void main(String[] args)
	{
		new ServiceFeedback("97738cf089d4459aa7fadfea3cc2d028");
	}
	
}
