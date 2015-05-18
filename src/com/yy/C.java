package com.yy;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
//随机选择
public class C extends Strategy{

	@Override
	public ArrayList<String> getTargetService(Map<String,Float> cpumaps,float cpukey,Map<String,Float> memmaps,float memkey,int num) {
		// TODO Auto-generated method stub
		if(num == 0)
			return null;
		//筛选出服务要求的服务副本
		//符合cpu阈值的副本
		System.out.println("cpukey:"+cpukey+"memkey:"+memkey+"num:"+num);
		System.out.println("cpuinfo:");
		for(Map.Entry<String, Float> temp:cpumaps.entrySet())
		{
			System.out.println("key:"+temp.getKey()+"value:"+temp.getValue());
		}
		System.out.println("meminfo:");
		for(Map.Entry<String, Float> temp:memmaps.entrySet())
		{
			System.out.println("key:"+temp.getKey()+"value:"+temp.getValue());
		}
		ArrayList<String> list = new ArrayList<String>();
		if(num<0)
		{
			for(Map.Entry<String, Float> map :cpumaps.entrySet() )
			{
				Float value = map.getValue();
				
				list.add(map.getKey());
			}
		}
		else{
			for(Map.Entry<String, Float> map :cpumaps.entrySet() )
			{
				Float value = map.getValue();
				if(value.floatValue() <= cpukey)
					list.add(map.getKey());
			}
			//删除掉不符合mem阈值的副本
			for(int i = 0;i<list.size();i++)
			{
				String key = list.get(i);
				if(memmaps.get(key).floatValue() > memkey)
					list.remove(i);
			}
		}
		//没有满足要求的主机
		if(list.size() == 0)
			return null;
		//产生随机数
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		if(list.size() == 0)
			return null;
		
		
		//System.out.println("rand value:"+rand);
		ArrayList<String> l = new ArrayList<String>();
		if(num < 0)
		{
			num = 0 - num;
		}
		while(num > 0 && list.size()>0)
		{
			//System.out.println("size:"+list.size());
			long rand = random.nextInt(list.size());
			l.add(list.get((int) rand));
			num--;
			list.remove((int)rand);
		}
		
		return l;

	}

}
