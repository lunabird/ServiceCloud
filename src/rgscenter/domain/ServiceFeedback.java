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
		//���̲߳�ѯ���������Ƿ�������Ҫ���ҵķ���
		while(flag){
			try {
				this.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("���������Ϣ��ѯ�߳�");
			try {
				System.out.println(id);
				flag = u.isDuplicateExist(id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		//ѭ����������ʾ���������Ѿ��鵽����Ӧ�ķ�����Ϣ
		//���÷��������췢��ȫ�ֵ�ַ����Ļ�����Ϣ������
		System.out.println("�����������ҵ���¼");
		try {
			this.back = u.FeedBack(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("������Ϣ�ѹ������");
		//��ȫ�ֵ�ַ�����ͻ�����Ϣ
		QuickStart qs = new QuickStart();
		try {
			qs.post(back);
			System.out.println("post�������յ���Ӧ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("������Ϣ�������");
	}
	public static void main(String[] args)
	{
		new ServiceFeedback("97738cf089d4459aa7fadfea3cc2d028");
	}
	
}
