package Message;

public enum MsgType
{
	WSDLSET, REPLICATE, REMOVE, FILEREQUEST,STATE,SERVICENAMEs,SERVICEINFO,SERVICEREQUEST,MATCHSERVICEs,LOGIN,
	PublishService,ClientUploadService, ServiceManagement, DeleteService,GETUSERINFO,DELETEUSERINFO,UPDATEUSERINFO,INSERTUSERINFO, UPDATEUSERPASSWORD, ADMINSERVICE,
    
	
	
	
	//SMS-SEE
	SERVICEDEPLOY, SERVICELIST, SERVICECOPYLIST,HOSTLIST, SERVICEMESSAGE,FEEDBACKREQUEST,//服务部署
	SERVICESTATUS,  SERVICEDELETE, SETCOPYNUM, MESSAGELIST, //服务管理
	STRGLIST, SELECTSTRG, SETSTRGPARA, //策略管理
	SERVICEEXIST,  //判断服务是否存在
	SETCCRNUM,//修改服务最大并发数量
	FLOWLIST, FLOWCOPYLIST,FLOWLIST2,
	FLOWTRANS,  BSNSFLOWDEPLOY, FLOWEXECUTE,//业务流程建模
	SERVFLOWDEPLOY,   //服务流程建模
	FLOWSTATUS,  //流程启停
	
	//MON-SEE
	AllServiceStateRequest, AllHostRequest, //服务副本监控信息展示
	AlarmStorageRequest,   //监控警报入库
	CurrentInfoResult,  //返回当前所有主机CPU、内存状态
	AlarmListRequest,   //警报信息展示
	ServiceInvokeInfoRequest, //请求服务调用信息
	
	//SEE-SMS  结果返回
	SERVICELISTRESULT, HOSTLISTRESULT,SERVICECOPYLISTRESULT, FEEDBACK, //服务部署
	SERVICEDEPLOYRESULT, SERVICESTATUSRESULT,  SERVICEDELETERESULT, SETCOPYNUMRESULT,  FLOWCOPYLISTRESULT, MESSAGELISTRESULT,//服务管理
	STRGLISTRESULT,	SELECTSTRGRESULT, SETSTRGPARARESULT,  //策略管理
	
	FLOWLISTRESULT, FLOWLIST2RESULT,
	FLOWTRANSRESULT,  FLOWEXECUTERESULT, //业务流程建模
	FLOWDEPLOYRESULT,   //服务流程建模
	FLOWSTATUSRESULT,  //流程启停结果
	SERVICEEXISTRESULT,  //服务存在查询结果
	SETCCRNUMRESULT,//服务最大数设置结果


	//SEE-MON
	AllServiceState,  //向运行监控软件返回服务状态
	AllHost,          //向运行监控软件返回所有主机IP
	CurrentInfo,  //注册中心向运行监控软件请求当前主机信息，以备调用时查看
	AlarmStorageResult,    //返回监控警报入库结果 
	AlarmListResult,      //警报信息展示
	ServiceInvokeInfoResult,   //服务调用信息返回
	
	//SEE-SCAS
	HostResult,   //向容器代理软件返回主机表插入结果
	CopyReceiveResult,CopyAllocation,
	StartFlow,   //向容器代理软件发起启动流程命令
	DependInfoResult,  //向容器代理软件返回软件所依赖的其他软件IP	
	
	
	
	serviceCallInfoRequest,//服务调用信息统计
	serviceCallInfo,
	serviceBussinessTypeRequest,//获取服务业务类型
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
	Host,    //初次主机注册信息
	CallService,  //请求调用服务
	CopyAllocationResult,  //副本分配结果
	DeleteServiceResult,
	DependInfo,   //请求查找依赖的软件IP
	CallInfo,STOPRUNNING, UNDEPLOYEDSERVICE, HOSTINFO, HOSTHARDDISK, SERVICEDETAIL, NODEDETAIL, CFILTERNODERESULT, CFILTERNODE, FILTERSTRATEGY, FILTERSTRATEGYRESULT, CFILTERSTRATEGY, CFILTERSTRATEGYRESULT, FILTERNODERESULT, FILTERNODE, NODEINSERTRESULT, NODEINSERT, NODESETRESULT, NODESET, NODEDELETERESULT, NODEDELETE, NODELISTRESULT, NODELIST, FILTERSERVICERESULT, FILTERSERVICE, FILTERRESULT, FILTER, handleAlarmResult, handleAlarmRequest, FILTERWARN, SERVICEDIRECTDEPLOY, SERVICEDEPLOYFLAG, NODESTATUS, STARTRUNNING, KILL, NODEEXIST, SIZENUM, HOSTSIZE, FILTERSIZE, LOGSIZE, FILTERLOGSIZE, changeServiceStatus,changeServiceStatusResult, FILTERLIST, FILTERLISTSIZE, GETSERVICESTATUS, CallService1, SERVICEMAP, CallInfo1, SERVICEADDRESSINFO, SERVICEADAPTERINFO, dkyserviceinfo, batinsert, dkyservicelist, ServiceCallStatistics, ServiceDelayStatistics, AddServiceComp, AddTransformer, sendConverFile;     //向注册中心发服务调用信息，以备入库
	
	
/*
 * 
 * SMS--SEE
 * SERVICEDEPLOY   服务部署      ArrayList
 * SERVICELIST     显示服务列表     null
 * HOSTLIST		   显示主机列表    null
 * SERVICECOPYLIST  显示带副本的服务列表
 * SERVICEMESSAGE   填写服务消息信息  ArrayList<String[]>
 * FEEDBACKREQUEST  请求回填信息   String name
 * 
 * SERVICESTATUS  服务状态，启动停止     ArrayList
 * SERVICEDELETE 服务删除     String
 * SETCOPYNUM    设置服务副本数     ArrayLists
 * MESSAGELIST   展示服务消息内容  ArrayList<String[]>
 * 
 * STRGLIST   显示选定的策略列表  String
 * SELECTSTRG   选择策略  String
 * SETSTRGPARA   设置策略参数   ArrayList
 * 
 * FLOWLIST       流程列表  null
 * FLOWTRANS      业务流程转换    ArrayList<String[]>
 * BSNSFLOWDEPLOY    业务流程部署    ArrayList<String[]>
 * BSNSFLOWEXECUTE   业务流程执行    ArrayList<String[]>
 * 
 * SERVFLOWDEPLOY    服务流程部署    ArrayList<String[]>
 * SERVFLOWEXECUTE   服务流程执行    ArrayList<String[]>
 * FLOWSTART_STOP    流程启停  String
 * 
 * 
 * MON-SEE
 * AllServiceStateRequest  服务副本监控信息展示  null
 * AllHostRequest     获取主机IP null
 * AlarmStorageRequest   警报日志入库 ArrayList<String>
 * CurrentInfoResult   返回当前所有主机CPU、内存状态 ArrayList<String[]>
 * AlarmListRequest   警报信息展示请求 null
 * ServiceInvokeInfoRequest   服务调用信息请求 null
 *  
 * 
 * SEE-SMS
 * SERVICELISTRESULT  服务列表 ArrayList
 * HOSTLISTRESULT     主机列表 ArrayList
 * SERVICEDEPLOYRESULT  服务部署结果   Boolean
 * SERVICECOPYLISTRESULT  带服务副本的服务列表
 * FEEDBACK              服务信息回填结果 String[3] 服务、版本号、URL/IP
 * 
 * SERVICESTATUSRESULT  服务启动停止结果  Boolean
 * SERVICEDELETERESULT  服务删除结果 Boolean
 * SETCOPYNUMRESULT    服务副本因子设定结果  Boolean
 * 
 * STRGLISTRESULT    策略列表  ArrayList
 * SELECTSTRGRESULT   策略选择结果   Boolean
 * SETSTRGPARARESULT  策略参数设置结果  Boolean
 * 
 * FLOWLISTRESULT   返回流程列表  ArrayList<String[]>
 * FLOWTRANSRESULT   业务流程转换结果    ArrayList<String[]>
 * BSNSFLOWDEPLOYRESULT    业务流程部署结果    Boolean
 * BSNSFLOWEXECUTERESULT   业务流程执行结果    Boolean
 * 
 * SERVFLOWDEPLOYRESULT    服务流程部署结果    Boolean
 * SERVFLOWEXECUTERESULT   服务流程执行结果    Boolean
 * 
 * FLOWSTART_STOPRESULT    流程启停结果  Boolean
 * 
 * SEE-MON
 * AllServiceState     服务副本监控信息展示  ArrayList<String[]>
 * CurrentInfo         向运行监控软件请求调用时所需的CPU、内存等信息    String 代表需要信息的种类
 * AlarmStorageResult  返回警报信息入库结果 boolean
 * AlarmListResult     返回警报信息 ArrayList<String[]>
 * ServiceInvokeInfoResult  返回服务调用信息 ArrayList<String[]>


 */

}

//WSDLSET 表示 服务节点向注册中心发送心跳信息
//REPLICATE 表示 服务节点1接收到服务节点2的拷贝文件请求
//LOGIN 表示 客户登录消息
//SERVICEREQUEST 表示 调用服务请求
//PublishService 表示 客户端与注册中心之间的上传服务请求
//ClientUploadService 表示 客户端与指定服务节点的上传服务请求
//ServiceManagement 表示 客户端与注册中心之间 请求某用户发布过的服务列表
//DeleteService 表示 删除服务请求