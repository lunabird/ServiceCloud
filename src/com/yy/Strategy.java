package com.yy;

import java.util.ArrayList;
import java.util.Map;

public abstract class Strategy {
	//从服务众多副本选出需求的服务
	//maps 是符合的键值对，key 是阈值，num是选副本的个数
	//当副本数不能满足num时，进最大分配
public abstract ArrayList<String> getTargetService(Map<String,Float> cpumaps,float cpukey,Map<String,Float> memmaps,float memkey,int num);

}
