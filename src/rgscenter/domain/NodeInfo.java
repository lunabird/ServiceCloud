package rgscenter.domain;
//用于心跳
public class NodeInfo {
private long lastHeartTime;
private String status;
private String[] nodeRealTimeInfo;//分别是ip,cpu,mem,diskInfo
public long getLastHeartTime() {
	return lastHeartTime;
}
//默认为connect状态，lastHeartTime为创建时间
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
