package weatherpony.partial.asmedit;

import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import weatherpony.pml.launch.IClassManipulator;

public class DeAutoCastTransformer implements IClassManipulator{

	@Override
	public byte[] transformClass(ClassLoader loader, String name, byte[] data){
		ClassReader cr = new ClassReader(data);
		ClassNode tree = new ClassNode();
		cr.accept(tree, ClassReader.EXPAND_FRAMES);
		Stack<String> classstack = new Stack();
		for(MethodNode eachMethod : tree.methods){
			InsnList insns = eachMethod.instructions;
			AbstractInsnNode node = insns.getFirst();
			
			
		}
		return data;
	}
}
