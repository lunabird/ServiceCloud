package rgscenter.domain;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Read {
	//bussinessFlow��ŵ��������������Ϣ��allFlow��ŵ���һ��all���з�֧�ϵ��������Ϣ��allFlow_A������all��֧�ϵ������Ϣ
	private static ArrayList<Comp> bussinessFlow = new ArrayList<Comp>();
	//allFlow_A��Object��������	Map<Integer,ArrayList<Comp>>
	//Map<Integer,Object>��Map<Integer,ArrayList<Comp>>�е�Integer���Ǵ�1��ʼ��ŵ�
	private static Map<Integer,Object> allFlow_A = new HashMap<Integer,Object>();
	
	//flow_comp_count��¼����ĸ���,all_count��¼all�ĸ�����all_chain_count[i][j]��¼���ǵ�i��all�ϵ�j����֧
	private static int flow_comp_count = 0;
	private static Map<Integer,Integer> all_chain_count = new HashMap<Integer,Integer>();
	private static int all_count = 0;
	
	private ArrayList<String> RelatedCompP = new ArrayList<String>();//����洢
	private ArrayList<String> RelatedCompN = new ArrayList<String>();	//����洢
	private ArrayList<String> CompName = new ArrayList<String>();	
	
	private UseUserTable u = new UseUserTable();
	private String path;
	
	public Read(String path){
		this.path = path;
	}
	
	@SuppressWarnings("unchecked")
	public void xunhuanFlow(Element flow){
		Iterator<Element> ite = flow.elementIterator();
		while(ite.hasNext()){
			Element element = (Element) ite.next();
			String elementName = element.getName();
			//System.out.println(elementName);
			CompName.add(elementName);
			Comp comp = new Comp();
			if(elementName.equals("comp")){
				String name = element.attributeValue("name");
				String type = element.attributeValue("type");
				//System.out.println(name+"\t"+type);
				
				flow_comp_count++;
				comp.setId(flow_comp_count);
				comp.setName(name);
				comp.setType(type);
				bussinessFlow.add(comp);
			}
			else if(elementName.equals("ALL")){
				all_count ++;
				xunhuanALL(element,all_count);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void xunhuanALL(Element all,int count){
		//All���whileѭ����ÿ�α�������һ��chain

		//���������ȫ�ֵĲ�������Ϊÿһ��all����Ϣ����������
		//�տ�ʼѡ�������ȫ�ֱ�������������clear()��������գ����µ��������allFlow_A��û�����ݣ����Ծ���һ���ֲ�����
		Map<Integer,ArrayList<Comp>> allFlow = new HashMap<Integer,ArrayList<Comp>>();
		
		Iterator<Element> ite = all.elementIterator();
		int chain_count = 0;
		while(ite.hasNext()){
			Element element = (Element) ite.next();
			String elementName = element.getName();
			//System.out.println(elementName);
			CompName.add(elementName);
			if(elementName.equals("chain")){
				//������all�ķ�֧�ˣ�chain_count ++ �����ǵ�all_count��all�ĵ�chain_count����֧
				chain_count++;
				//System.out.println("��"+count+"��all�ϵĵ�"+chain_count+"����֧");
				all_chain_count.put(count, chain_count);
				ArrayList<Comp> chain = xunhuanChain(element);
				allFlow.put(chain_count, chain);
			}
		}
		//while����ʱ������һ��all��
		allFlow_A.put(count, allFlow);

	} 
	
	@SuppressWarnings("unchecked")
	public ArrayList<Comp> xunhuanChain(Element chain){
		//������ǵ�all����Ƕ��all������Ļ�����ô���������ϸ��Щ�ͻ���comp��all��
		Iterator<Element> ite = chain.elementIterator();
		ArrayList<Comp> comps = new ArrayList<Comp>();
		while(ite.hasNext()){
			Element element = (Element) ite.next();
			String elementName = element.getName();
			//System.out.println(elementName);
			CompName.add(elementName);
			Comp comp = new Comp();
			if(elementName.equals("comp")){
				String name = element.attributeValue("name");
				String type = element.attributeValue("type");
				//System.out.println(name+"\t"+type);
				
				flow_comp_count++;
				comp.setId(flow_comp_count);
				comp.setName(name);
				comp.setType(type);
				
				//bussinessFlow��Ҫ��Ӵ�comp
				bussinessFlow.add(comp);
				comps.add(comp);
			}
			else if(elementName.equals("ALL")){
				//all��chain��֧��������all
				all_count ++;
				xunhuanALL(element,all_count);
			}
			
		}
		return comps;
	}
	
	@SuppressWarnings("unchecked")
	public void display(){
		for(Comp comp : bussinessFlow){
			displayComp(comp);
		}
		//System.out.println();
		
		if(all_count > 0){
			Set<Integer> alls = allFlow_A.keySet();
			//System.out.println(alls+"\t�ܹ���"+alls.size()+"��all");
			for(int all : alls){
				//System.out.println("�����"+all+"��all����Ϣ:");
				Map<Integer,ArrayList<Comp>> all_chains = (Map<Integer, ArrayList<Comp>>) allFlow_A.get(all);
				Set<Integer> chains = all_chains.keySet();
				//System.out.println(chains+"\t��"+all+"��all�ܹ���"+chains.size()+"��chain");
				for(int chain : chains){
					//System.out.println("\t��"+all+"��all�ϵ�"+chain+"��chain�������Ϣ��");
					ArrayList<Comp> comps = all_chains.get(chain);
					for(Comp comp : comps){
						displayComp(comp);
					}
				}
				System.out.println();
			}
		}
		
		//System.out.println("������ܸ�����"+flow_comp_count);
		//System.out.println("all�ĸ�����"+all_count);
		
		for(String s : CompName){
			//System.out.print(s+"\t");
		}
		//System.out.println();
		
		if(!RelatedCompP.isEmpty()){
			//System.out.println("���RelatedCompP����Ϣ��");
			for(String s : RelatedCompP){
				//System.out.println(s);
			}
		}
		//System.out.println();
		if(!RelatedCompN.isEmpty()){
			//System.out.println("���RelatedCompN����Ϣ��");
			for(String s : RelatedCompN){
				//System.out.println(s);
			}
		}
	}
	
	public void displayComp(Comp comp){
		//System.out.println(comp.getId()+"\t"+comp.getName()+"\t"+comp.getType());
	}
	
	public String Entrence(){
		String msg = null;
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new FileReader(path));
			Element flow = document.getRootElement();
			xunhuanFlow(flow);
			if(all_count > 0){
				getAllRelatedComp_ALL();
			}
			else{
				getAllRelatedComp_NoALL();
			}
			display();
			
			//System.out.println("...............\n...............\n.............\n");
			msg = convernt();
		//	System.out.println(msg);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return msg;
	}
	
	//�õ�����ALL�ĵ�һ����֧�ĵ�һ����������һ����֧�����һ�����������ѡ�񱣴���������id��
	//Map<Integer,ArrayList<Integer>>,��i��all��ÿ���ĳ��ȶ���2
	public Map<Integer,ArrayList<Integer>> getAll_start_end(){
		Map<Integer,ArrayList<Integer>> ret = new HashMap<Integer,ArrayList<Integer>>();
		
		Set<Integer> alls = allFlow_A.keySet();
		for(int all : alls){
			ArrayList<Integer> start_all_end = new ArrayList<Integer>();
			Map<Integer,ArrayList<Comp>> all_chains = (Map<Integer, ArrayList<Comp>>) allFlow_A.get(all);
			//�õ�all�ĵ�һ�����id
			ArrayList<Comp> chain = all_chains.get(1);
			int start = chain.get(0).getId();
			
			chain = all_chains.get(all_chains.size());
			int end = chain.get(chain.size()-1).getId();

			start_all_end.add(start);
			start_all_end.add(end);
			ret.put(all, start_all_end);
		}
		
		return ret;
	}
	
	public void getAllRelatedComp_NoALL(){
		//i����ֱ�Ӵ���bussinessFlow��key,   i < businessFlow.size()
		int i = 0;
		Comp pre = new Comp();
		Comp post = new Comp();
		while(i < bussinessFlow.size()){
			//System.out.println("i��ֵ��"+i);
			if(i == 0){
				pre = bussinessFlow.get(i);				
			}
			else{
				post = bussinessFlow.get(i);
				RelatedCompN.add(post.getId()+":"+pre.getId());
			}
			
			i++;
		}
		
	}
	
	public void getAllRelatedComp_ALL(){
		//all_count > 0;
		int all_num = 1;
		while(all_num <= all_count){
			//��all_num��all��������Ϣ
			Map<Integer,ArrayList<Comp>> chains = (Map<Integer, ArrayList<Comp>>) allFlow_A.get(all_num);
			//all��һ��chain�ĵ�һ�����start�����һ��chain�����һ�������end
			int start = chains.get(1).get(0).getId();
			ArrayList<Comp> chain = chains.get(chains.size());
			int end = chain.get(chain.size()-1).getId();
			//System.out.println(start+"\t"+end);
			
			//all������chain�Ľ�β���id����
			ArrayList<Integer> chain_ends = new ArrayList<Integer>();
			for(int k=1;k <= chains.size();k++){
				ArrayList<Comp> comps = chains.get(k);
				//System.out.println(comps.get(comps.size()-1).getId());
				chain_ends.add(comps.get(comps.size()-1).getId());
			}
			//System.out.println();
			//all������chain�Ŀ�ͷ���id����
			ArrayList<Integer> chain_starts = new ArrayList<Integer>();
			for(int k=1;k <= chains.size();k++){
				ArrayList<Comp> comps = chains.get(k);
				//System.out.println(comps.get(0).getId());
				chain_starts.add(comps.get(0).getId());
			}
			
			//i����ֱ�Ӵ���bussinessFlow��key,   i<businessFlow.size()
			int i = 1;
			while(i < bussinessFlow.size()){
				//System.out.println("i��ֵ��"+i);
				if(!chain_ends.contains(i)){	
					if(i == (start-1)){
						//���idΪi��������all������chain��ͷ�ıȽϣ�bussinessFlow��id��ֵ�����i�Ĺ�ϵ��i= id-1
						for(int s : chain_starts){
							//�������˴�������������all�����һ�����ݴ���ǣ���������1����>2,������2����>1
							RelatedCompN.add(bussinessFlow.get(s-1).getId()+":"+bussinessFlow.get(i-1).getId());
						}
						
						if(end < bussinessFlow.size()){
							//���all������chain�Ľ�β��end+1Ϊid�ıȽ�
							for(int e : chain_ends){
								//���������û�б仯
								RelatedCompP.add(bussinessFlow.get(e-1).getId()+":"+bussinessFlow.get(end).getId());
							}
						}
						
						//�жϴ�chain����û��all����û����i = start;
						i = start;
						
//						break;
					}
					else{
						//System.out.println(i);
						RelatedCompN.add(bussinessFlow.get(i).getId()+":"+bussinessFlow.get(i-1).getId());
						i++;
					}
				}
				else{
					i++;
				}
			}
			
			all_num++;
		}
	
	}
	
	public String convernt(){
		//�����洢���ݿ��ѯ����
		ArrayList<String> mArrayList=new ArrayList<String>();
		//���ݵ���ǻ���<flow name="copyFile" doc:name="flows">
		//��β����</flow>
		String flow_content = "";
		
		String ret = "";
		
		//����һ��stack
		Stack<Object> stack = new Stack<Object>();//��ŵ���1,2,3,4....����ڼ���all
		int all_num = 0;
		
		int i=0;
		int j=0;
		boolean[] flag_chain = new boolean[all_count+1];//all�е�chain�ǲ��ǵ�һ�γ��֣�����flag_chain[0]û���õ�
		for(int k = 0;k < flag_chain.length;k ++){
			flag_chain[k] = false;
		}
		
		Comp pre = new Comp();
		Comp post = new Comp();
		String pre_serviceName = null;
		String post_serviceName = null;
		while(i < CompName.size()){
			String compName = CompName.get(i);
			//System.out.println(compName);
			if(compName.equals("ALL")){
				//���all����Ϣ:<all>
				stack.push(++all_num);
				
				String blank = "";
				int blank_num =1;
				while(blank_num < all_num){
					blank = blank + "\t";
					blank_num ++;
				}
				
				flow_content = flow_content + "\t\t"+blank+"<all doc:name="+'"'+"all"+'"'+">\n";
			}else if(compName.equals("chain")){
				if(flag_chain[all_num] == false){
					//���chain��Ϣ:<processor-chain>
					flow_content = flow_content + "\t\t\t<processor-chain>\n\t\t";
					flag_chain[all_num] = true;
				}else{
					//���chain��Ϣ:</processor-chain>\n<processor-chain>
					flow_content = flow_content + "\t\t\t</processor-chain>\n" + "\t\t\t<processor-chain>\n\t\t";
				}
			}else if (compName.equals("END")){
				//���:</all>
//				int pop = (int) stack.pop();
//				if(pop != 1){
//					flow_content = flow_content + "\t\t\t</processor-chain>\n" + "\t\t</all>\n";
//				}
				flow_content = flow_content + "\t\t\t</processor-chain>\n" + "\t\t</all>\n";
			}else if(compName.equals("comp")){
				if(j < bussinessFlow.size()){
					//System.out.println("j="+j);
					//��������Ϣ
					if(j == 0){
						pre = bussinessFlow.get(j);
						String name = pre.getName();
						System.out.println(name);
						//������Ϊname��mule�����Ϣ
					//	flow_content = flow_content + "\t\t<file:inbound-endpoint path="+'"'+"D:\\aaa\\IN"+'"'+" doc:name="+'"'+"file"+'"'+"/>\n";
						String compInfo = null;
						
						/**
						 * �·�������
						 */
						//String sql = "select ServiceName,CompInfo from compinfo where CompName=";
						//String[] para = {name};
						//ResultSet rs = u.executeQuery(sql,para);
						
						mArrayList.clear();
						try {
							mArrayList = u.queryFlowCompInfo(name);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!mArrayList.isEmpty()) {
							compInfo = mArrayList.get(0);
							pre_serviceName=mArrayList.get(1);
							flow_content = flow_content +"\t\t"+ compInfo + "\n";
						}
						
						
						
						
					}else{
						post = bussinessFlow.get(j);
						
						String sql = null;
						ResultSet rs = null;
						//�õ�post�����ServiceName��CompInfo
						//���post�����Ϣ
						String compInfo = null;
						//sql = "select ServiceName,CompInfo from compinfo where CompName=";
						//String[] para = {post.getName()};
						//System.out.println(post.getName());
						//rs = u.executeQuery(sql,para);
						mArrayList.clear();
						try {
							mArrayList = u.queryFlowCompInfo(post.getName());
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (!mArrayList.isEmpty()) {
							compInfo = mArrayList.get(0);
							post_serviceName=mArrayList.get(1);
						}
					
						
						//�����pre����post֮����Ҫ���adapter
						for(String str : RelatedCompN){
							if(str.startsWith(post.getId()+":")){
								String[] s = str.split(":");
								pre = bussinessFlow.get(Integer.valueOf(s[1])-1);
								//�������ݿ⣬�鿴pre�����post�����������Ϣ������ӵ�post�����Ϣ֮ǰ
								//System.out.println(j+":\t"+pre.getId()+"\t"+post.getId());
						//		flow_content = flow_content + "\t\t<file:file-to-byte-array-transformer"+" doc:name="+'"'+"file to  byte array"+'"'+"/>\n";
								
								
								try {
									flow_content=u.queryAdapterInfo(pre_serviceName, post_serviceName, flow_content);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						
						//���post�����Ϣ
						flow_content = flow_content +"\t\t"+ compInfo + "\n";
						
						
						for(String str : RelatedCompP){
							if(str.startsWith(post.getId()+":")){
								String[] s = str.split(":");
								pre = bussinessFlow.get(Integer.valueOf(s[1])-1);
								//�������ݿ⣬�鿴post�����pre�����������Ϣ������ӵ�post�����Ϣ֮��
								//System.out.println(j+":\t"+post.getId()+"\t"+pre.getId());
								String inputName = null;
								String outputName = null;
								String adapterInfo = null;
								try {
									flow_content=u.queryAdapterInfo(post_serviceName, pre_serviceName, flow_content);
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							
							}
						}
					}
					j++;
				}
			}
			i++;
		}
		
		ret = flow_content ;
		return ret;
	}
		
	public static void main(String[] args){
		String path ="D:\\aaa\\test\\leida.xml";
		Read read = new Read(path);
		//System.out.println(read.Entrence());
	}
}
