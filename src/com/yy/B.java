package com.yy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import rgscenter.domain.DBConnectionManager;

//����num������ֵ��ѡ��Сֵ���Ǵ�ֵ������ֵ��ʾѡ����Ŀ��������ʱ����������
public class B extends Strategy{

	@Override
	public ArrayList<String> getTargetService(Map<String,Float> cpumaps,float cpukey,Map<String,Float> memmaps,float memkey,int num) {
		// TODO Auto-generated method stub
		//
		System.out.println("�����cpu������Ϣ");
		for(Map.Entry<String, Float> map:cpumaps.entrySet())
		{
			System.out.println(map.getKey() +"---"+map.getValue().toString());
		}
		System.out.println("�����mem������Ϣ");
		for(Map.Entry<String, Float> map:memmaps.entrySet())
		{
			System.out.println(map.getKey() +"---"+map.getValue().toString());
		}
		//�������ip
		if(num == 0)
			return null;
		//ɸѡ������Ҫ��ķ��񸱱�
		//����cpu��ֵ�ĸ���
		ArrayList<String> list = new ArrayList<String>();
		for(Map.Entry<String, Float> map :cpumaps.entrySet() )
		{
			Float value = map.getValue();
			if(value.floatValue() <= cpukey)
				list.add(map.getKey());
		}
		//ɾ����������mem��ֵ�ĸ���
		for(int i = 0;i<list.size();i++)
		{
			String key = list.get(i);
			if(memmaps.get(key).floatValue() > memkey)
				list.remove(i);
		}
		//û������Ҫ�������
		if(list.size() == 0)
			return null;
		ArrayList<Node> myNode = new ArrayList<Node>();
		DBConnectionManager co = DBConnectionManager.getInstance();
		co.getConnection();
		PreparedStatement ps = null;
		ResultSet resultSet = null;
		for(int i=0;i<list.size();i++)
		{
			
			String sql = "select ServiceNumber from node where HostIP=?";
			
			try{
				co.locktable("node");
				//resultSet =co.query(sql, list.get(i));
				ps = co.getConnection().prepareStatement(sql);
				ps.setString(1, list.get(i));
				resultSet = ps.executeQuery();
				if(resultSet.next())
					myNode.add(new Node(resultSet.getInt(1),list.get(i)));
				co.unlocktable("node");
			
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(resultSet != null)
					try {
						resultSet.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
		//����
		Sort.sortMyNode(myNode);
		ArrayList<String> result = new ArrayList<String>();
		if(num > 0)
		{
			for(int i= 0;i<num &&i<myNode.size();i++)
				result.add(myNode.get(i).serverIP);
		}
		else
		{
			num = 0-num;	
			for(int i= 0;i<num &&i<myNode.size();i++)
				result.add(myNode.get(myNode.size()-i-1).serverIP);
		}
		return result;
	}

}
