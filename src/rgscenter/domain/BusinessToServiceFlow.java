package rgscenter.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class BusinessToServiceFlow {
	private String flow_declare_start = "<?xml version="+'"'+"1.0"+'"'+" encoding="+'"'+"UTF-8"+'"'+"?>";
	private String flow_declare_mid_1 = "<mule xmlns="+'"'+"http://www.mulesoft.org/schema/mule/core"+'"';
	private String flow_declare_mid_2 = " xmlns:doc="+'"'+"http://www.mulesoft.org/schema/mule/documentation"+'"'
									  + " xmlns:file="+'"'+"http://www.mulesoft.org/schema/mule/file"+'"'
									  + " xmlns:ftp="+'"'+"http://www.mulesoft.org/schema/mule/ftp"+'"'
									  + " xmlns:http="+'"'+"http://www.mulesoft.org/schema/mule/http"+'"'
									  + " xmlns:spring="+'"'+"http://www.springframework.org/schema/beans" +'"'
									  + " xmlns:core="+'"'+"http://www.mulesoft.org/schema/mule/core"+'"'
									  + " xmlns:xsi="+'"'+"http://www.w3.org/2001/XMLSchema-instance"+'"'
									  + " version="+'"'+"CE-3.2.1" +'"'
									  + " xsi:schemaLocation="+'"'+"\n";
	private String flow_declare_mid_3 = "http://www.mulesoft.org/schema/mule/file"+" http://www.mulesoft.org/schema/mule/file/current/mule-file.xsd"+"\n"
									  + "http://www.mulesoft.org/schema/mule/ftp"+" http://www.mulesoft.org/schema/mule/ftp/current/mule-ftp.xsd"+"\n"
									  + "http://www.mulesoft.org/schema/mule/http"+" http://www.mulesoft.org/schema/mule/http/current/mule-http.xsd"+"\n"
									  +	"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd "+"\n"
									  + "http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd "+'"'+">";;
	
	private String flow_declare_end ="</mule>\n";
	
	private String flow_content_start = "\n\t<flow name="+'"'+"copyFile"+'"'+" doc:name="+'"'+"flows"+'"'+">\n";
	private String flow_content_mid ;
	private String flow_content_end = "\t</flow>\n";
	
	private String path;
	
	public BusinessToServiceFlow(){
	}
	
	public String getServiceFlow(){
		String retFlow = "";
		Read read = new Read(path);
		flow_content_mid  = read.Entrence();
		
		retFlow = flow_declare_start + flow_declare_mid_1 + flow_declare_mid_2 + flow_declare_mid_3 
				+ flow_content_start + flow_content_mid + flow_content_end 
				+ flow_declare_end ;
		return retFlow;
	}
	
	
	/*
	 * д�ļ�����
	 */
	public void writeByFileWrite(String _sDestFile, String _sContent)  throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(_sDestFile);
			fw.write(_sContent);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fw != null) {
				fw.close();
				fw = null;
			}
		}
	}
	
	//��ҵ������ת���ɷ������̿��ƺ���
	public String flowTransBtoS(String busFlowString){
		String serviceFlow="";
		String dirpath = "D:\\tmpflow";
		String tempflowName = "tempflowfile.xml";
		path = dirpath+"\\"+tempflowName;
		File  dfile = new File(dirpath);
		//ʹ��dom4j�����������Ҫ�洢��ʱ�ļ����Դ��xml�ļ�
		if (!dfile.exists()) {
			dfile.mkdirs();
		}
		File fileName = new File(path);
		if (!fileName.exists()) {
			try {
				fileName.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			//��ҵ������д���ļ�
			writeByFileWrite(path, busFlowString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serviceFlow = getServiceFlow();
		return serviceFlow;
	}
	public static void main(String[] args) throws IOException {
		String busFlowString="";
		StringBuffer sBuffer=new StringBuffer();
		File filename = new File("D:\\businessFlow.xml"); // Ҫ��ȡ����·����input.txt�ļ�  
        InputStreamReader reader;
        reader = new InputStreamReader(new FileInputStream(filename));
		BufferedReader br = new BufferedReader(reader);
        busFlowString = br.readLine();
        while (busFlowString!=null) {
			sBuffer.append(busFlowString);
			sBuffer.append("\n");
			busFlowString = br.readLine();
			
		}
        reader.close();
        busFlowString=sBuffer.toString();
        System.out.println(busFlowString);
        //System.out.println(busFlowString);
        //�ص�Ϊ��������
	//	BusinessToServiceFlow bts = new BusinessToServiceFlow();
	//	String msg = bts.flowTransBtoS(busFlowString);
	//	System.out.println(msg);
		
	}

}
