package deob;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import org.objectweb.asm.tree.*;

public final class Counters {

	static int remove(ClassNode... classes)
	{
		int count = 0;
		for (ClassNode c : classes) {
			for (MethodNode m : c.methods) {
				for (int i = 0; i < m.instructions.size(); ++i) {
					if (m.instructions.get(i).getOpcode() != GETSTATIC) {
						continue;
					}
					FieldInsnNode getstatic = (FieldInsnNode) m.instructions
						.get(i);
					if (!getstatic.owner.equals(c.name)) {
						continue;
					}
					AbstractInsnNode iconst = getstatic.getNext();
					if (iconst.getOpcode() != ICONST_1) {
						continue;
					}
					AbstractInsnNode iadd = iconst.getNext();
					if (iadd.getOpcode() != IADD) {
						continue;
					}
					if (iadd.getNext().getOpcode() != PUTSTATIC) {
						continue;
					}
					FieldRef ref = new FieldRef(c.name, getstatic.name,
						getstatic.desc);
					FieldInsnNode putstatic = (FieldInsnNode) iadd.getNext();
					if (!ref.equalsInsn(putstatic)) {
						continue;
					}
					if (findGets(ref, getstatic, putstatic, classes)) {
						continue;
					}
					m.instructions.remove(getstatic);
					m.instructions.remove(iconst);
					m.instructions.remove(iadd);
					m.instructions.remove(putstatic);
					removeField(c, ref);
					++count;
				}
			}
		}
		return count;
	}

	private static void removeField(ClassNode c, FieldRef ref)
	{
		Iterator<FieldNode> it = c.fields.iterator();
		while (it.hasNext()) {
			FieldNode f = it.next();
			if (f.name.equals(ref.name) && f.desc.equals(ref.desc)) {
				it.remove();
				return;
			}
		}
	}

	private static boolean findGets(FieldRef ref, FieldInsnNode get,
		FieldInsnNode put, ClassNode... classes)
	{
		for (ClassNode n : classes) {
			for (MethodNode m : n.methods) {
				for (int i = 0; i < m.instructions.size(); ++i) {
					AbstractInsnNode insn = m.instructions.get(i);
					if (!(insn instanceof FieldInsnNode)) {
						continue;
					}
					FieldInsnNode f = (FieldInsnNode) insn;
					if (ref.equalsInsn(f) && f != get && f != put) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
