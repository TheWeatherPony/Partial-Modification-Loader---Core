package weatherpony.partial;

import org.objectweb.asm.Type;

import weatherpony.util.structuring.MultiPathEnum_Plus;

public final class CallData {
	public CallData(CallHook data){
		this.inClass = data.inClass();
		String desc = data.methodDesc();
		this.methodDesc = (desc.isEmpty() ? null : desc.replace('.', '/'));
		this.methodName = data.methodName();
		this.needsTrace = data.needsTrace();
		this.pathClass = data.pathClass();
		this.pathDesc = data.pathDesc();
		this.pathMethod = data.pathMethod();
		this.timing = data.timing();
	}
	public CallData(CallDataFactory data){
		this.inClass = data.inClass;
		this.methodDesc = data.methodDesc;
		this.methodName = data.methodName;
		this.needsTrace = data.needsTrace;
		this.pathClass = data.pathClass;
		this.pathDesc = data.pathDesc;
		this.pathMethod = data.pathMethod;
		this.timing = data.timing;
	}
	public final String inClass;
	public final MultiPathEnum_Plus pathClass;
	public final String methodName;
	public final MultiPathEnum_Plus pathMethod;
	public final String methodDesc;
	public final MultiPathEnum_Plus pathDesc;
	public final WrapTiming timing;
	public final boolean needsTrace;
	
	public static class CallDataFactory{
		public CallDataFactory(){
			
		}
		public CallDataFactory setClass(String className){
			return this.setClass(className, MultiPathEnum_Plus.Direct);
		}
		public CallDataFactory setClass(String className, MultiPathEnum_Plus type){
			this.inClass = className;
			if(className == null)
				this.pathClass = MultiPathEnum_Plus.Plus;
			else
				this.pathClass = type;
			return this;
		}
		public CallDataFactory setMethodName(String methodName){
			return this.setMethodName(methodName, MultiPathEnum_Plus.General);
		}
		public CallDataFactory setMethodName(String methodName, MultiPathEnum_Plus type){
			this.methodName = methodName;
			if(methodName == null)
				this.pathMethod = MultiPathEnum_Plus.Plus;
			else
				this.pathMethod = type;
			return this;
		}
		public CallDataFactory setMethodDesc(String methodDesc){
			if(methodDesc != null)
				methodDesc = methodDesc.replace('.', '/');
			this.methodDesc = methodDesc;
			if(methodDesc == null)
				this.pathDesc = MultiPathEnum_Plus.Plus;
			else
				this.pathDesc = MultiPathEnum_Plus.General;
			return this;
		}
		public CallDataFactory setMethodDesc(Type[] methodParams, Type methodReturn){
			return this.setMethodDesc(methodParams, methodReturn);
		}
		public CallDataFactory setNeedsTrace(){
			this.needsTrace = true;
			return this;
		}
		public CallDataFactory setNeedsTrace(boolean needsTrace){
			this.needsTrace = needsTrace;
			return this;
		}
		public CallDataFactory setTiming(WrapTiming timing){
			this.timing = timing;
			return this;
		}
		public CallData create(){
			return new CallData(this);
		}
		String inClass = "";
		MultiPathEnum_Plus pathClass = MultiPathEnum_Plus.Plus;
		String methodName = "";
		MultiPathEnum_Plus pathMethod = MultiPathEnum_Plus.Plus;
		String methodDesc = null;
		MultiPathEnum_Plus pathDesc = MultiPathEnum_Plus.Plus;
		WrapTiming timing = WrapTiming.Mid;
		boolean needsTrace = false;
	}
}
