package weatherpony.partial.asmedit;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import com.google.common.base.Throwables;

import weatherpony.partial.internal.ClassData;
import weatherpony.pml.launch.IClassManipulator;
import weatherpony.pml.launch.PMLRoot;
import weatherpony.util.reflection.ClassUtil;

public class ExtentionListenerTransformer implements IClassManipulator{

	@Override
	public byte[] transformClass(ClassLoader loader, String className, byte[] bytes){
		if(className == null)
			return bytes;
		if(bytes != null){
			ClassReader cr = new ClassReader(bytes);
			String internalName = cr.getClassName();
			String superName = cr.getSuperName();
			String extending;
			if(internalName.equals("java/lang/Object")){
				extending = null;
			}else{
				extending = superName.replace('/', '.');
			}
			String[] interfaces = cr.getInterfaces();
			if(interfaces == null)
				interfaces = new String[0];
			int interfacesLength = interfaces.length;
			List<String> interfacesTransformed = new ArrayList(interfacesLength);
			for(int cur=0;cur<interfacesLength;cur++){
				interfacesTransformed.add(interfaces[cur].replace('/', '.'));
			}
			this.registerClassInfo(className, extending, interfacesTransformed);
			
			//now to go through the parents' info. This will generate the full tree for the current class.
			//while(true){
				try {
					if(extending != null){
						ClassUtil.forName(Type.getType(extending), loader);
					}
					for(String each : interfacesTransformed){
						ClassUtil.forName(Type.getType(each), loader);
					}
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw Throwables.propagate(e);
				}
			//}
		}
		return bytes;
	}
	void registerClassInfo(String forClass, String Extends, List<String> interfaces){
		ClassData.ASM_giveExtendsAndImplements(forClass, Extends, interfaces);
	}
}
