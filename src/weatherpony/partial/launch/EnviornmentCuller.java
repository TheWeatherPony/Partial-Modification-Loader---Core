package weatherpony.partial.launch;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import weatherpony.pml.implementorapi.IEnviornment;
import weatherpony.pml.implementorapi.IEnviornmentASMSetup;
import weatherpony.pml.launch.IClassManipulator;

public final class EnviornmentCuller<Env extends Enum<Env> & IEnviornment<Env>> implements IClassManipulator{
	EnviornmentCuller(IEnviornmentASMSetup<Env> setup, Env env){
		this.setup = setup;
		this.env = env;
		Env[] enviornments = env.getAllValues();
		List<String> envnames = new ArrayList();
		for(Env each : enviornments){
			envnames.add(each.name());
		}
		this.enviornmentNames = Collections.unmodifiableList(envnames);
		Class<? extends Annotation>[] annotations = setup.enviornmentCullingAnnotations();
		this.keeps = new boolean[annotations.length][enviornments.length];
		List<String> annotationdescs = new ArrayList();
		for(int curann=0;curann<annotations.length;curann++){
			annotationdescs.add(Type.getDescriptor(annotations[curann]));
			for(int curenv=0;curenv<enviornments.length;curenv++){
				this.keeps[curann][curenv] = setup.shouldKeepCode(annotations[curann], env, enviornments[curenv]);
			}
		}
		this.annotationDescs = Collections.unmodifiableList(annotationdescs);
	}
	private final IEnviornmentASMSetup<Env> setup;
	private final Env env;
	
	private final List<String> annotationDescs;
	private final List<String> enviornmentNames;
	private final boolean[][] keeps;//annotation, enviornment
	@Override
	public byte[] transformClass(ClassLoader loader, String name, byte[] data){
		ClassReader read = new ClassReader(data);
		ClassNode tree = new ClassNode();
		read.accept(tree, 0);
		
		if(tree.visibleAnnotations != null){
			for(AnnotationNode each : tree.visibleAnnotations){
				int annotationIndex = this.annotationDescs.indexOf(each.desc);
				if(annotationIndex == -1)
					continue;
				int envIndex = this.enviornmentNames.indexOf(((String[])each.values.get(1))[1]);
				if(envIndex == -1)
					throw new UnsupportedOperationException("Got a "+((String[])each.values.get(1))[1]+" but was expecting one of these: "+this.enviornmentNames.toString());
				boolean keep = this.keeps[annotationIndex][envIndex];
				if(keep)
					continue;
				else
					throw new RuntimeException("This class was annotated to be culled for the running enviornment ("+this.env+","+((String[])each.values.get(1))[1]+")", new ClassNotFoundException(name));
			}
		}
		
		List<MethodNode> removeMethods = new ArrayList();
		method: for(MethodNode eachmethod : tree.methods){
			if(eachmethod.visibleAnnotations != null){
				for(AnnotationNode each : eachmethod.visibleAnnotations){
					int annotationIndex = this.annotationDescs.indexOf(each.desc);
					if(annotationIndex == -1)
						continue;
					int envIndex = this.enviornmentNames.indexOf(((String[])each.values.get(1))[1]);
					if(envIndex == -1)
						throw new UnsupportedOperationException("Got a "+((String[])each.values.get(1))[1]+" but was expecting one of these: "+this.enviornmentNames.toString());
					boolean keep = this.keeps[annotationIndex][envIndex];
					if(keep)
						continue;
					else{
						removeMethods.add(eachmethod);
						continue method;
					}
				}
			}
		}
		tree.methods.removeAll(removeMethods);
		
		List<FieldNode> removeFields = new ArrayList();
		field: for(FieldNode eachfield : tree.fields){
			if(eachfield.visibleAnnotations != null){
				for(AnnotationNode each : eachfield.visibleAnnotations){
					int annotationIndex = this.annotationDescs.indexOf(each.desc);
					if(annotationIndex == -1)
						continue;
					int envIndex = this.enviornmentNames.indexOf(((String[])each.values.get(1))[1]);
					if(envIndex == -1)
						throw new UnsupportedOperationException("Got a "+((String[])each.values.get(1))[1]+" but was expecting one of these: "+this.enviornmentNames.toString());
					boolean keep = this.keeps[annotationIndex][envIndex];
					if(keep)
						continue;
					else{
						removeFields.add(eachfield);
						continue field;
					}
				}
			}
		}
		tree.fields.removeAll(removeFields);
		
		ClassWriter writer = new ClassWriter(0);
		tree.accept(writer);
		return writer.toByteArray();
	}
}
