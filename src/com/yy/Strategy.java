package com.yy;

import java.util.ArrayList;
import java.util.Map;

public abstract class Strategy {
	//�ӷ����ڶั��ѡ������ķ���
	//maps �Ƿ��ϵļ�ֵ�ԣ�key ����ֵ��num��ѡ�����ĸ���
	//����������������numʱ����������
public abstract ArrayList<String> getTargetService(Map<String,Float> cpumaps,float cpukey,Map<String,Float> memmaps,float memkey,int num);

}
