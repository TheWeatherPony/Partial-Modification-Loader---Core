package weatherpony.partial.asmedit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import weatherpony.partial.internal.ObfuscationHelper3;
import weatherpony.partial.launch.PMLMain;

public class ProxyListing{
	private static class ProxyList{
		HashMap<Data, Integer> reverse = new HashMap();
		ArrayList<Data> direct = new ArrayList();
	}
	private HashMap<String, ProxyList> lists = new HashMap();
	private static final String base = "proxyNum";
	static final ProxyListing instance = new ProxyListing((ObfuscationHelper3) PMLMain.instance.getObfHelper());
	private ProxyListing(ObfuscationHelper3 obfhelp){
		Runtime.getRuntime().addShutdownHook(new ProxyLogger(obfhelp));
	}
	class ProxyLogger extends Thread{
		ProxyLogger(ObfuscationHelper3 obfhelp){
			this.obfhelp = obfhelp;
		}
		ObfuscationHelper3 obfhelp;
		@Override
		public void run(){
			File log = new File("PML-proxyLog.txt");
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(log))){
				writer.append("This file is here to provide a log of all of PML's proxies from the last launch, which can be useful for debugging.");
				writer.newLine();
				for(Entry<String, ProxyList> each : lists.entrySet()){
					String classname = each.getKey();
					writer.append("class: ");
					/*Iterator<String> classit = this.obfhelp.classNames.getAllSynonyms(classname);
					while(classit.hasNext()){
						String next = classit.next();
						writer.append(next).append(", ");
					}*/
					writer.append(classname);
					writer.newLine();
					ProxyList list = each.getValue();
					List<Data> direct = list.direct;
					int len = direct.size();
					for(int cur=0;cur<len;cur++){
						Data spot = direct.get(cur);
						if(spot == null)
							continue;
						/*MethodData tester = this.obfhelp.denoteMethod(classname, spot.method, spot.desc);
						this.obfhelp.methods.getAllSynonyms(tester);*/
						writer.append(cur+":  ").append(spot.toString());
						writer.newLine();
					}
					writer.newLine();
				}
			} catch (IOException e) {
				System.err.println("PML Proxy Logger errored:");
				e.printStackTrace(System.err);
			}
			
		}
	}
	String generateClassName(String fromClass, String method, String desc){
		Data value = new Data(method, desc);
		ProxyList inner;
		if(this.lists.containsKey(fromClass)){
			inner = this.lists.get(fromClass);
		}else{
			this.lists.put(fromClass, (inner = new ProxyList()));
		}
		if(inner.reverse.containsKey(value))
			return fromClass+'/'+base+inner.reverse.get(value);
		int size = inner.direct.size();
		
		inner.reverse.put(value, size);
		inner.direct.add(value);
		return fromClass+'/'+base + size;
	}
	public Data getDataFromName(String name){
		int loc = name.indexOf(base);
		
		if(loc == -1)
			return null;
		String clazz = name.substring(0, loc-1);
		ProxyList inner = this.lists.get(clazz);
		if(inner == null)
			return null;
		try{
			return inner.direct.get(Integer.parseInt(name.substring(loc+base.length())));
		}catch(Throwable e){
			return null;
		}
	}
	public static final class Data{
		public final String method,desc;
		Data(String method, String desc){
			this.method = method;
			this.desc = desc;
		}
		public String getMethod(){
			return (method+desc);
		}
		@Override
		public String toString(){
			return getMethod();
		}
		@Override
		public int hashCode(){
			return method.hashCode() + desc.hashCode();
		}
		@Override
		public boolean equals(Object comp){
			if(comp instanceof Data){
				Data comp2 = (Data)comp;
				return this.method.equals(comp2.method) && this.desc.equals(comp2.desc);
			}
			return false;
		}
	}
}
