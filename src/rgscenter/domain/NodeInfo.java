package rgscenter.domain;
//��������
public class NodeInfo {
private long lastHeartTime;
private String status;
private String[] nodeRealTimeInfo;//�ֱ���ip,cpu,mem,diskInfo
public long getLastHeartTime() {
	return lastHeartTime;
}
//Ĭ��Ϊconnect״̬��lastHeartTimeΪ����ʱ��
public NodeInfo()
{
	lastHeartTime = System.currentTimeMillis();
	status = "connect";
	nodeRealTimeInfo = null;
}
public void setLastHeartTime(long lastHeartTime) {
	this.lastHeartTime = lastHeartTime;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String[] getNodeRealTimeInfo() {
	return nodeRealTimeInfo;
}
public void setNodeRealTimeInfo(String[] nodeRealTimeInfo) {
	this.nodeRealTimeInfo = nodeRealTimeInfo;
}

}
