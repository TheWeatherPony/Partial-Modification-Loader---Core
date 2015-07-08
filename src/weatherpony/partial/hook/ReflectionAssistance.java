package weatherpony.partial.hook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.google.common.base.Throwables;

import weatherpony.partial.api.IObfuscationHelper;
import weatherpony.partial.api.IObfuscationHelper.ObfuscationException;

public class ReflectionAssistance{
	public ReflectionAssistance(IObfuscationHelper helper){
		this.helper = helper;
	}
	private IObfuscationHelper helper;
	public IObfuscationHelper getObfuscationHelper(){
		return this.helper;
	}
	private HashMap<String, Class> classMap = new HashMap();
	private HashMap<String, HashMap<String, Field>> fieldMap = new HashMap();
	private HashMap<String, HashMap<String, HashMap<String, Method>>> methodMap = new HashMap();
	public Class getClass(String classname, ClassLoader loader) throws ObfuscationException {
		if(this.classMap.containsKey(classname)){
			return this.classMap.get(classname);
		}
		Class val = this.helper.getClass(classname, loader);
		this.classMap.put(classname, val);
		return val;
	}
	public Field getField(String classname, String fieldname, ClassLoader loader) throws ObfuscationException {
		HashMap<String,Field> inner;
		if(this.fieldMap.containsKey(classname)){
			inner = this.fieldMap.get(classname);
		}else{
			inner = new HashMap(5);
			this.fieldMap.put(classname, inner);
		}
		if(inner.containsKey(fieldname)){
			return inner.get(fieldname);
		}
		Field f = this.helper.getField(classname, fieldname, loader);
		inner.put(fieldname, f);
		return f;
	}
	public Method getMethod(String classname, String method, String desc, ClassLoader loader) throws ObfuscationException {
		HashMap<String, HashMap<String, Method>> methods;
		if(this.methodMap.containsKey(classname)){
			methods = this.methodMap.get(classname);
		}else{
			methods = new HashMap(5);
			this.methodMap.put(classname, methods);
		}
		HashMap<String, Method> descs;
		if(methods.containsKey(method)){
			descs = methods.get(method);
		}else{
			descs = new HashMap(1);//this will usually just be 1 in the end... not always, but usually
			methods.put(method, descs);
		}
		if(descs.containsKey(desc)){
			return descs.get(desc);
		}
		Method m = this.helper.getMethod(classname, method, desc, loader);
		descs.put(desc, m);
		return m;
	}
	public <T> T getFieldValue_instance(String classname, String fieldname, Object from){
		try{
			return (T) (this.getField(classname, fieldname, from.getClass().getClassLoader()).get(from));
		}catch(Throwable e){
			throw Throwables.propagate(e);
		}
	}
	public <T> T getFieldValue_static(String classname, String fieldname, ClassLoader loader){
		try{
			return (T) (this.getField(classname, fieldname, loader).get(null));
		}catch(Throwable e){
			throw Throwables.propagate(e);
		}
	}
	public <T> void setFieldValue_instance(String classname, String fieldname, Object from, T value){
		try{
			this.getField(classname, fieldname, from.getClass().getClassLoader()).set(from, value);
		}catch(Throwable e){
			throw Throwables.propagate(e);
		}
	}
	public <T> void setFieldValue_static(String classname, String fieldname, ClassLoader loader, T value){
		try{
			this.getField(classname, fieldname, loader).set(null, value);
		}catch(Throwable e){
			throw Throwables.propagate(e);
		}
	}
	public <T> T invokeMethod_instance(String classname, String methodname, String methoddesc, Object from, Object...params){
		try{
			return (T) this.getMethod(classname, methodname, methoddesc, from.getClass().getClassLoader()).invoke(from, params);
		}catch(Throwable e){
			throw Throwables.propagate(e);
		}
	}
	public <T> T invokeMethod_static(String classname, String methodname, String methoddesc, ClassLoader loader, Object...params){
		try{
			return (T) this.getMethod(classname, methodname, methoddesc, loader).invoke(null, params);
		}catch(Throwable e){
			throw Throwables.propagate(e);
		}
	}
}
