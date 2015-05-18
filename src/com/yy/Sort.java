package com.yy;

import java.util.ArrayList;

public class Sort {
//从小到大排序
public static void sortMyNode(ArrayList<Node> list)
{
	for(int i=0;i<list.size()-1;i++)
	{
		int minkey = i;
		for(int j=i+1;j<list.size();j++)
		{
			if(list.get(j).serviceNumber < list.get(i).serviceNumber)
				minkey = j;
		}
		if(minkey != i)
			swap(list.get(i),list.get(minkey));
	}
}
//将两内容交换
public static void swap(Node o1,Node o2)
{
	int tempint = o1.serviceNumber;
	String tempString  = o1.serverIP;
	o1.serviceNumber = o2.serviceNumber;
	o1.serverIP = o2.serverIP;
	o2.serverIP = tempString;
	o2.serviceNumber = tempint;
}
}
