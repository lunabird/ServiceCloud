package Message;

public enum MsgType
{
	WSDLSET, REPLICATE, REMOVE, FILEREQUEST,STATE,SERVICENAMEs,SERVICEINFO,SERVICEREQUEST,MATCHSERVICEs,LOGIN,
	PublishService,ClientUploadService, ServiceManagement, DeleteService,GETUSERINFO,DELETEUSERINFO,UPDATEUSERINFO,INSERTUSERINFO, UPDATEUSERPASSWORD, ADMINSERVICE,
    
	
	
	
	//SMS-SEE
	SERVICEDEPLOY, SERVICELIST, SERVICECOPYLIST,HOSTLIST, SERVICEMESSAGE,FEEDBACKREQUEST,//������
	SERVICESTATUS,  SERVICEDELETE, SETCOPYNUM, MESSAGELIST, //�������
	STRGLIST, SELECTSTRG, SETSTRGPARA, //���Թ���
	SERVICEEXIST,  //�жϷ����Ƿ����
	SETCCRNUM,//�޸ķ�����󲢷�����
	FLOWLIST, FLOWCOPYLIST,FLOWLIST2,
	FLOWTRANS,  BSNSFLOWDEPLOY, FLOWEXECUTE,//ҵ�����̽�ģ
	SERVFLOWDEPLOY,   //�������̽�ģ
	FLOWSTATUS,  //������ͣ
	
	//MON-SEE
	AllServiceStateRequest, AllHostRequest, //���񸱱������Ϣչʾ
	AlarmStorageRequest,   //��ؾ������
	CurrentInfoResult,  //���ص�ǰ��������CPU���ڴ�״̬
	AlarmListRequest,   //������Ϣչʾ
	ServiceInvokeInfoRequest, //������������Ϣ
	
	//SEE-SMS  �������
	SERVICELISTRESULT, HOSTLISTRESULT,SERVICECOPYLISTRESULT, FEEDBACK, //������
	SERVICEDEPLOYRESULT, SERVICESTATUSRESULT,  SERVICEDELETERESULT, SETCOPYNUMRESULT,  FLOWCOPYLISTRESULT, MESSAGELISTRESULT,//�������
	STRGLISTRESULT,	SELECTSTRGRESULT, SETSTRGPARARESULT,  //���Թ���
	
	FLOWLISTRESULT, FLOWLIST2RESULT,
	FLOWTRANSRESULT,  FLOWEXECUTERESULT, //ҵ�����̽�ģ
	FLOWDEPLOYRESULT,   //�������̽�ģ
	FLOWSTATUSRESULT,  //������ͣ���
	SERVICEEXISTRESULT,  //������ڲ�ѯ���
	SETCCRNUMRESULT,//������������ý��


	//SEE-MON
	AllServiceState,  //�����м��������ط���״̬
	AllHost,          //�����м�����������������IP
	CurrentInfo,  //ע�����������м���������ǰ������Ϣ���Ա�����ʱ�鿴
	AlarmStorageResult,    //���ؼ�ؾ�������� 
	AlarmListResult,      //������Ϣչʾ
	ServiceInvokeInfoResult,   //���������Ϣ����
	
	//SEE-SCAS
	HostResult,   //����������������������������
	CopyReceiveResult,CopyAllocation,
	StartFlow,   //�����������������������������
	DependInfoResult,  //�����������������������������������IP	
	
	
	
	serviceCallInfoRequest,//���������Ϣͳ��
	serviceCallInfo,
	serviceBussinessTypeRequest,//��ȡ����ҵ������
	serviceBussinessType,
	serviceRunTimeRequest,
	serviceRunTime,
	activeServiceRequest,
	activeService,
	singleServiceCallTimesRequest,
	singleServiceCallTimes,
	singleServiceRunTimeRequest,
	singleServiceRunTime,
	serviceCopyRequest,
	serviceCopy,
	serviceDetailInfoWithCopyRequest,
	serviceDetailInfoWithCopy,
	nodeHistoryCPURAMRequest,
	nodeHistoryCPURAM,
	alarmListRequest,
	alarmListResult,
	
	//SCAS-SEE
	DisconnectRequest,
	//DisconnectResult,
	ConnectRequest,
	//ConnectResult,
	Host,    //��������ע����Ϣ
	CallService,  //������÷���
	CopyAllocationResult,  //����������
	DeleteServiceResult,
	DependInfo,   //����������������IP
	CallInfo,STOPRUNNING, UNDEPLOYEDSERVICE, HOSTINFO, HOSTHARDDISK, SERVICEDETAIL, NODEDETAIL, CFILTERNODERESULT, CFILTERNODE, FILTERSTRATEGY, FILTERSTRATEGYRESULT, CFILTERSTRATEGY, CFILTERSTRATEGYRESULT, FILTERNODERESULT, FILTERNODE, NODEINSERTRESULT, NODEINSERT, NODESETRESULT, NODESET, NODEDELETERESULT, NODEDELETE, NODELISTRESULT, NODELIST, FILTERSERVICERESULT, FILTERSERVICE, FILTERRESULT, FILTER, handleAlarmResult, handleAlarmRequest, FILTERWARN, SERVICEDIRECTDEPLOY, SERVICEDEPLOYFLAG, NODESTATUS, STARTRUNNING, KILL, NODEEXIST, SIZENUM, HOSTSIZE, FILTERSIZE, LOGSIZE, FILTERLOGSIZE, changeServiceStatus,changeServiceStatusResult, FILTERLIST, FILTERLISTSIZE, GETSERVICESTATUS, CallService1, SERVICEMAP, CallInfo1, SERVICEADDRESSINFO, SERVICEADAPTERINFO, dkyserviceinfo, batinsert, dkyservicelist, ServiceCallStatistics, ServiceDelayStatistics, AddServiceComp, AddTransformer, sendConverFile;     //��ע�����ķ����������Ϣ���Ա����
	
	
/*
 * 
 * SMS--SEE
 * SERVICEDEPLOY   ������      ArrayList
 * SERVICELIST     ��ʾ�����б�     null
 * HOSTLIST		   ��ʾ�����б�    null
 * SERVICECOPYLIST  ��ʾ�������ķ����б�
 * SERVICEMESSAGE   ��д������Ϣ��Ϣ  ArrayList<String[]>
 * FEEDBACKREQUEST  ���������Ϣ   String name
 * 
 * SERVICESTATUS  ����״̬������ֹͣ     ArrayList
 * SERVICEDELETE ����ɾ��     String
 * SETCOPYNUM    ���÷��񸱱���     ArrayLists
 * MESSAGELIST   չʾ������Ϣ����  ArrayList<String[]>
 * 
 * STRGLIST   ��ʾѡ���Ĳ����б�  String
 * SELECTSTRG   ѡ�����  String
 * SETSTRGPARA   ���ò��Բ���   ArrayList
 * 
 * FLOWLIST       �����б�  null
 * FLOWTRANS      ҵ������ת��    ArrayList<String[]>
 * BSNSFLOWDEPLOY    ҵ�����̲���    ArrayList<String[]>
 * BSNSFLOWEXECUTE   ҵ������ִ��    ArrayList<String[]>
 * 
 * SERVFLOWDEPLOY    �������̲���    ArrayList<String[]>
 * SERVFLOWEXECUTE   ��������ִ��    ArrayList<String[]>
 * FLOWSTART_STOP    ������ͣ  String
 * 
 * 
 * MON-SEE
 * AllServiceStateRequest  ���񸱱������Ϣչʾ  null
 * AllHostRequest     ��ȡ����IP null
 * AlarmStorageRequest   ������־��� ArrayList<String>
 * CurrentInfoResult   ���ص�ǰ��������CPU���ڴ�״̬ ArrayList<String[]>
 * AlarmListRequest   ������Ϣչʾ���� null
 * ServiceInvokeInfoRequest   ���������Ϣ���� null
 *  
 * 
 * SEE-SMS
 * SERVICELISTRESULT  �����б� ArrayList
 * HOSTLISTRESULT     �����б� ArrayList
 * SERVICEDEPLOYRESULT  ��������   Boolean
 * SERVICECOPYLISTRESULT  �����񸱱��ķ����б�
 * FEEDBACK              ������Ϣ������ String[3] ���񡢰汾�š�URL/IP
 * 
 * SERVICESTATUSRESULT  ��������ֹͣ���  Boolean
 * SERVICEDELETERESULT  ����ɾ����� Boolean
 * SETCOPYNUMRESULT    ���񸱱������趨���  Boolean
 * 
 * STRGLISTRESULT    �����б�  ArrayList
 * SELECTSTRGRESULT   ����ѡ����   Boolean
 * SETSTRGPARARESULT  ���Բ������ý��  Boolean
 * 
 * FLOWLISTRESULT   ���������б�  ArrayList<String[]>
 * FLOWTRANSRESULT   ҵ������ת�����    ArrayList<String[]>
 * BSNSFLOWDEPLOYRESULT    ҵ�����̲�����    Boolean
 * BSNSFLOWEXECUTERESULT   ҵ������ִ�н��    Boolean
 * 
 * SERVFLOWDEPLOYRESULT    �������̲�����    Boolean
 * SERVFLOWEXECUTERESULT   ��������ִ�н��    Boolean
 * 
 * FLOWSTART_STOPRESULT    ������ͣ���  Boolean
 * 
 * SEE-MON
 * AllServiceState     ���񸱱������Ϣչʾ  ArrayList<String[]>
 * CurrentInfo         �����м������������ʱ�����CPU���ڴ����Ϣ    String ������Ҫ��Ϣ������
 * AlarmStorageResult  ���ؾ�����Ϣ����� boolean
 * AlarmListResult     ���ؾ�����Ϣ ArrayList<String[]>
 * ServiceInvokeInfoResult  ���ط��������Ϣ ArrayList<String[]>


 */

}

//WSDLSET ��ʾ ����ڵ���ע�����ķ���������Ϣ
//REPLICATE ��ʾ ����ڵ�1���յ�����ڵ�2�Ŀ����ļ�����
//LOGIN ��ʾ �ͻ���¼��Ϣ
//SERVICEREQUEST ��ʾ ���÷�������
//PublishService ��ʾ �ͻ�����ע������֮����ϴ���������
//ClientUploadService ��ʾ �ͻ�����ָ������ڵ���ϴ���������
//ServiceManagement ��ʾ �ͻ�����ע������֮�� ����ĳ�û��������ķ����б�
//DeleteService ��ʾ ɾ����������