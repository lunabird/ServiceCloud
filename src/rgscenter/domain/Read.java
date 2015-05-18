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
	//bussinessFlow里放的是所有组件的信息，allFlow里放的是一个all所有分支上的组件的信息，allFlow_A是所有all分支上的组件信息
	private static ArrayList<Comp> bussinessFlow = new ArrayList<Comp>();
	//allFlow_A中Object的类型是	Map<Integer,ArrayList<Comp>>
	//Map<Integer,Object>、Map<Integer,ArrayList<Comp>>中的Integer都是从1开始存放的
	private static Map<Integer,Object> allFlow_A = new HashMap<Integer,Object>();
	
	//flow_comp_count记录组件的个数,all_count记录all的个数，all_chain_count[i][j]记录的是第i个all上第j个分支
	private static int flow_comp_count = 0;
	private static Map<Integer,Integer> all_chain_count = new HashMap<Integer,Integer>();
	private static int all_count = 0;
	
	private ArrayList<String> RelatedCompP = new ArrayList<String>();//正向存储
	private ArrayList<String> RelatedCompN = new ArrayList<String>();	//反向存储
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
		//All里的while循环，每次遍历的是一个chain

		//这个不能做全局的参数，因为每一个all的信息都在里面存放
		//刚开始选择的是做全局变量，下面有用clear()函数来清空，导致的问题就是allFlow_A中没有数据，所以就做一个局部参数
		Map<Integer,ArrayList<Comp>> allFlow = new HashMap<Integer,ArrayList<Comp>>();
		
		Iterator<Element> ite = all.elementIterator();
		int chain_count = 0;
		while(ite.hasNext()){
			Element element = (Element) ite.next();
			String elementName = element.getName();
			//System.out.println(elementName);
			CompName.add(elementName);
			if(elementName.equals("chain")){
				//遍历到all的分支了，chain_count ++ 代表是第all_count个all的第chain_count个分支
				chain_count++;
				//System.out.println("第"+count+"个all上的第"+chain_count+"个分支");
				all_chain_count.put(count, chain_count);
				ArrayList<Comp> chain = xunhuanChain(element);
				allFlow.put(chain_count, chain);
			}
		}
		//while结束时遍历完一个all了
		allFlow_A.put(count, allFlow);

	} 
	
	@SuppressWarnings("unchecked")
	public ArrayList<Comp> xunhuanChain(Element chain){
		//如果考虑到all中再嵌套all的情况的话，那么这里组件的细腻些就会是comp或all了
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
				
				//bussinessFlow中要添加此comp
				bussinessFlow.add(comp);
				comps.add(comp);
			}
			else if(elementName.equals("ALL")){
				//all的chain分支中再遇见all
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
			//System.out.println(alls+"\t总共有"+alls.size()+"个all");
			for(int all : alls){
				//System.out.println("输出第"+all+"个all的信息:");
				Map<Integer,ArrayList<Comp>> all_chains = (Map<Integer, ArrayList<Comp>>) allFlow_A.get(all);
				Set<Integer> chains = all_chains.keySet();
				//System.out.println(chains+"\t第"+all+"个all总共有"+chains.size()+"个chain");
				for(int chain : chains){
					//System.out.println("\t第"+all+"个all上第"+chain+"个chain的组件信息：");
					ArrayList<Comp> comps = all_chains.get(chain);
					for(Comp comp : comps){
						displayComp(comp);
					}
				}
				System.out.println();
			}
		}
		
		//System.out.println("组件的总个数："+flow_comp_count);
		//System.out.println("all的个数："+all_count);
		
		for(String s : CompName){
			//System.out.print(s+"\t");
		}
		//System.out.println();
		
		if(!RelatedCompP.isEmpty()){
			//System.out.println("输出RelatedCompP的信息：");
			for(String s : RelatedCompP){
				//System.out.println(s);
			}
		}
		//System.out.println();
		if(!RelatedCompN.isEmpty()){
			//System.out.println("输出RelatedCompN的信息：");
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
	
	//得到所有ALL的第一个分支的第一个组件和最后一个分支的最后一个组件，这里选择保存的是组件的id，
	//Map<Integer,ArrayList<Integer>>,第i个all中每个的长度都是2
	public Map<Integer,ArrayList<Integer>> getAll_start_end(){
		Map<Integer,ArrayList<Integer>> ret = new HashMap<Integer,ArrayList<Integer>>();
		
		Set<Integer> alls = allFlow_A.keySet();
		for(int all : alls){
			ArrayList<Integer> start_all_end = new ArrayList<Integer>();
			Map<Integer,ArrayList<Comp>> all_chains = (Map<Integer, ArrayList<Comp>>) allFlow_A.get(all);
			//得到all的第一个组件id
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
		//i这里直接代表bussinessFlow的key,   i < businessFlow.size()
		int i = 0;
		Comp pre = new Comp();
		Comp post = new Comp();
		while(i < bussinessFlow.size()){
			//System.out.println("i的值："+i);
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
			//第all_num个all的所有信息
			Map<Integer,ArrayList<Comp>> chains = (Map<Integer, ArrayList<Comp>>) allFlow_A.get(all_num);
			//all第一个chain的第一个组件start和最后一条chain的最后一个组件的end
			int start = chains.get(1).get(0).getId();
			ArrayList<Comp> chain = chains.get(chains.size());
			int end = chain.get(chain.size()-1).getId();
			//System.out.println(start+"\t"+end);
			
			//all中所有chain的结尾组件id集合
			ArrayList<Integer> chain_ends = new ArrayList<Integer>();
			for(int k=1;k <= chains.size();k++){
				ArrayList<Comp> comps = chains.get(k);
				//System.out.println(comps.get(comps.size()-1).getId());
				chain_ends.add(comps.get(comps.size()-1).getId());
			}
			//System.out.println();
			//all中所有chain的开头组件id集合
			ArrayList<Integer> chain_starts = new ArrayList<Integer>();
			for(int k=1;k <= chains.size();k++){
				ArrayList<Comp> comps = chains.get(k);
				//System.out.println(comps.get(0).getId());
				chain_starts.add(comps.get(0).getId());
			}
			
			//i这里直接代表bussinessFlow的key,   i<businessFlow.size()
			int i = 1;
			while(i < bussinessFlow.size()){
				//System.out.println("i的值："+i);
				if(!chain_ends.contains(i)){	
					if(i == (start-1)){
						//添加id为i的组件与此all中所有chain开头的比较；bussinessFlow中id的值与参数i的关系：i= id-1
						for(int s : chain_starts){
							//这里做了处理。。。。。。all进入的一端数据存放是：数据流行1——>2,则存放是2——>1
							RelatedCompN.add(bussinessFlow.get(s-1).getId()+":"+bussinessFlow.get(i-1).getId());
						}
						
						if(end < bussinessFlow.size()){
							//添加all中所有chain的结尾与end+1为id的比较
							for(int e : chain_ends){
								//这里的数据没有变化
								RelatedCompP.add(bussinessFlow.get(e-1).getId()+":"+bussinessFlow.get(end).getId());
							}
						}
						
						//判断此chain中有没有all，如没有则i = start;
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
		//用来存储数据库查询数据
		ArrayList<String> mArrayList=new ArrayList<String>();
		//数据的最开是还有<flow name="copyFile" doc:name="flows">
		//结尾还有</flow>
		String flow_content = "";
		
		String ret = "";
		
		//定义一个stack
		Stack<Object> stack = new Stack<Object>();//存放的是1,2,3,4....代表第几个all
		int all_num = 0;
		
		int i=0;
		int j=0;
		boolean[] flag_chain = new boolean[all_count+1];//all中的chain是不是第一次出现，其中flag_chain[0]没有用到
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
				//添加all的信息:<all>
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
					//添加chain信息:<processor-chain>
					flow_content = flow_content + "\t\t\t<processor-chain>\n\t\t";
					flag_chain[all_num] = true;
				}else{
					//添加chain信息:</processor-chain>\n<processor-chain>
					flow_content = flow_content + "\t\t\t</processor-chain>\n" + "\t\t\t<processor-chain>\n\t\t";
				}
			}else if (compName.equals("END")){
				//添加:</all>
//				int pop = (int) stack.pop();
//				if(pop != 1){
//					flow_content = flow_content + "\t\t\t</processor-chain>\n" + "\t\t</all>\n";
//				}
				flow_content = flow_content + "\t\t\t</processor-chain>\n" + "\t\t</all>\n";
			}else if(compName.equals("comp")){
				if(j < bussinessFlow.size()){
					//System.out.println("j="+j);
					//添加组件信息
					if(j == 0){
						pre = bussinessFlow.get(j);
						String name = pre.getName();
						System.out.println(name);
						//添加组件为name的mule组件信息
					//	flow_content = flow_content + "\t\t<file:inbound-endpoint path="+'"'+"D:\\aaa\\IN"+'"'+" doc:name="+'"'+"file"+'"'+"/>\n";
						String compInfo = null;
						
						/**
						 * 新方法测试
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
						//得到post组件的ServiceName和CompInfo
						//添加post组件信息
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
					
						
						//查表看看pre华润post之间需要添加adapter
						for(String str : RelatedCompN){
							if(str.startsWith(post.getId()+":")){
								String[] s = str.split(":");
								pre = bussinessFlow.get(Integer.valueOf(s[1])-1);
								//访问数据库，查看pre组件和post组件的适配信息，并添加到post组件信息之前
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
						
						//添加post组件信息
						flow_content = flow_content +"\t\t"+ compInfo + "\n";
						
						
						for(String str : RelatedCompP){
							if(str.startsWith(post.getId()+":")){
								String[] s = str.split(":");
								pre = bussinessFlow.get(Integer.valueOf(s[1])-1);
								//访问数据库，查看post组件和pre组件的适配信息，并添加到post组件信息之后
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
