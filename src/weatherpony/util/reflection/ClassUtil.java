package weatherpony.util.reflection;

import org.objectweb.asm.Type;

public class ClassUtil{
	@Deprecated
	public static Class forName(String name, ClassLoader loader) throws Throwable{
		if(name.length() > 1){
			return loader.loadClass(name);
		} 
		char c = name.charAt(0); 
		switch(c){//this might not be needed, but it's good anyways. 
			case 'V': 
				return Void.TYPE; 
			case 'Z': 
				return Boolean.TYPE; 
			case 'C': 
				return Character.TYPE; 
			case 'B': 
				return Byte.TYPE; 
			case 'S': 
				return Short.TYPE; 
			case 'I': 
				return Integer.TYPE; 
			case 'F': 
				return Float.TYPE; 
			case 'J': 
				return Long.TYPE; 
			case 'D': 
				return Double.TYPE; 
			default: 
				throw new RuntimeException(); 
		} 

	}
	public static Class forName(Type type, ClassLoader loader) throws Throwable{
		switch(type.getSort()){
		case Type.VOID: 
			return Void.TYPE; 
		case Type.BOOLEAN: 
			return Boolean.TYPE; 
		case Type.CHAR: 
			return Character.TYPE; 
		case Type.BYTE: 
			return Byte.TYPE; 
		case Type.SHORT: 
			return Short.TYPE; 
		case Type.INT: 
			return Integer.TYPE; 
		case Type.FLOAT: 
			return Float.TYPE; 
		case Type.LONG: 
			return Long.TYPE; 
		case Type.DOUBLE: 
			return Double.TYPE;
		case Type.ARRAY:
		case Type.OBJECT:
			return loader.loadClass(type.getClassName().replace('/', '.'));
		}
		return null;
	}
	public static Class forArray(Class base, int dimentions, boolean add) throws Throwable{
		Type t = Type.getType(base);
		if(!add){
			if(t.getSort() == Type.ARRAY)
				t = t.getElementType();
		}
		String temp = "";
		for(int cur=0;cur<dimentions;cur++){
			temp+='[';
		}
		temp += t.getInternalName();
		
		return ClassUtil.forName(Type.getObjectType(temp), base.getClassLoader());
	}
}
