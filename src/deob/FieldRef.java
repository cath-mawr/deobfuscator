package deob;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

final class FieldRef {

	final String owner;
	final String name;
	final String desc;

	FieldRef(String owner, String name, String desc)
	{
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	boolean equalsInsn(FieldInsnNode n)
	{
		return owner.equals(n.owner) && name.equals(n.name)
			&& desc.equals(n.desc);
	}

	static FieldRef getControlField(ClassNode... classes)
	{
		ClassNode client = null;
		for (ClassNode c : classes) {
			if (!c.name.equals("client")) {
				continue;
			}
			client = c;
			break;
		}
		for (MethodNode m : client.methods) {
			if ((m.access & ACC_STATIC) != 0) {
				continue;
			}
			InsnList list = m.instructions;
			for (int i = 1; i < list.size(); i++) {
				AbstractInsnNode insn = list.get(i);
				AbstractInsnNode prev = insn.getPrevious();

				if (insn.getOpcode() != ISTORE || prev.getOpcode() != GETSTATIC) {
					continue;
				}
				FieldInsnNode gs = (FieldInsnNode) list.get(i - 1);
				for (FieldNode f : client.fields) {
					if (f.name != gs.name || f.desc != gs.desc) {
						continue;
					}
					if (!f.desc.equals("Z") && !f.desc.equals("I")) {
						throw new RuntimeException("Control field wrong type: "
							+ f.desc);
					}
					return new FieldRef("client", f.name, f.desc);
				}
			}
		}
		return null;
	}
}
