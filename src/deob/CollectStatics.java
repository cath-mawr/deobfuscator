package deob;

import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.tree.*;

public final class CollectStatics {

	private static MethodNode getclinit(ClassNode dst)
	{
		for (MethodNode m : dst.methods) {
			if (m.name.equals("<clinit>") && m.desc.equals("()V")) {
				return m;
			}
		}
		MethodNode m = new MethodNode(ACC_STATIC, "<clinit>", "()V", null, null);
		m.instructions.add(new InsnNode(RETURN));
		dst.methods.add(m);
		return m;
	}

	private static String getKey(ClassNode c, FieldNode f)
	{
		return String.format("%s|%s|%s", c.name, f.name, f.desc);
	}

	private static String getKey(FieldInsnNode f)
	{
		return String.format("%s|%s|%s", f.owner, f.name, f.desc);
	}

	private static String getKey(ClassNode c, MethodNode m)
	{
		return String.format("%s|%s|%s", c.name, m.name, m.desc);
	}

	private static String getKey(MethodInsnNode m)
	{
		return String.format("%s|%s|%s", m.owner, m.name, m.desc);
	}

	private static int unprivate(int access)
	{
		if ((access & ACC_PRIVATE) != 0) {
			access &= ~ACC_PRIVATE;
		}
		return access;
	}

	private static void replaceRefs(String owner_name,
		HashMap<String, String> fields, HashMap<String, String> methods,
		InsnList list)
	{
		for (int i = 0; i < list.size(); ++i) {
			AbstractInsnNode insn = list.get(i);
			if (insn instanceof MethodInsnNode) {
				MethodInsnNode min = (MethodInsnNode) insn;
				String name = methods.get(getKey(min));
				if (name == null) {
					continue;
				}
				min.owner = owner_name;
				min.name = name;
			} else if (insn instanceof FieldInsnNode) {
				FieldInsnNode fin = (FieldInsnNode) insn;
				String name = fields.get(getKey(fin));
				if (name != null) {
					fin.owner = owner_name;
					fin.name = name;
				}
			}
		}
	}

	private static FieldNode getField(FieldInsnNode insn, ClassNode... classes)
	{
		for (ClassNode c : classes) {
			if (!c.name.equals(insn.owner))
				continue;
			FieldNode f = Util.getField(c, insn.name, insn.desc);
			if (f != null)
				return f;
		}
		return null;
	}

	private static MethodNode getMethod(MethodInsnNode insn,
		ClassNode... classes)
	{
		for (ClassNode c : classes) {
			if (!c.name.equals(insn.owner))
				continue;
			MethodNode m = Util.getMethod(c, insn.name, insn.desc);
			if (m != null)
				return m;
		}
		return null;
	}

	static int collect(ClassNode dst, ClassNode... classes)
	{
		HashMap<String, String> fields = new HashMap<>();
		HashMap<String, String> methods = new HashMap<>();
		MethodNode clinit = getclinit(dst);
		int fcount = 0;
		int mcount = 0;
		for (ClassNode c : classes) {
			Iterator<FieldNode> fit = c.fields.iterator();
			while (fit.hasNext()) {
				FieldNode f = fit.next();
				if ((f.access & ACC_STATIC) == 0) {
					continue;
				} else if (f.name.length() > 2) {
					continue;
				}
				String name = String.format("field%d", fcount++);
				dst.fields.add(new FieldNode(unprivate(f.access), name, f.desc,
					f.signature, f.value));
				fields.put(getKey(c, f), name);
				fit.remove();
			}

			Iterator<MethodNode> mit = c.methods.iterator();
			while (mit.hasNext()) {
				MethodNode m = mit.next();
				if ((m.access & ACC_STATIC) == 0) {
					continue;
				} else if (m.name.equals("<clinit>") && m.desc.equals("()V")) {
					AbstractInsnNode last = m.instructions.getLast();
					while (last.getOpcode() == -1) {
						last = last.getPrevious();
					}
					if (last.getOpcode() == RETURN) {
						m.instructions.remove(last);
					}
					clinit.instructions.insert(m.instructions);
					mit.remove();
					continue;
				} else if (m.name.length() > 2) {
					continue;
				}
				String name = String.format("method%d", mcount++);
				MethodNode replace = new MethodNode(unprivate(m.access), name,
					m.desc, m.signature,
					m.exceptions.toArray(new String[m.exceptions.size()]));
				replace.localVariables = m.localVariables;
				replace.instructions = m.instructions;
				dst.methods.add(replace);
				methods.put(getKey(c, m), name);
				mit.remove();
			}
		}
		for (ClassNode c : classes) {
			for (MethodNode m : c.methods) {
				replaceRefs(dst.name, fields, methods, m.instructions);
			}
		}
		for (MethodNode m : dst.methods) {
			replaceRefs(dst.name, fields, methods, m.instructions);
			for (int i = 0; i < m.instructions.size(); ++i) {
				AbstractInsnNode insn = m.instructions.get(i);
				if (insn instanceof FieldInsnNode) {
					FieldInsnNode fin = (FieldInsnNode) insn;
					if (fin.owner.equals(dst.name))
						continue;
					FieldNode f = getField(fin, classes);
					if (f != null) {
						f.access = unprivate(f.access);
					}
				} else if (insn instanceof MethodInsnNode) {
					MethodInsnNode min = (MethodInsnNode) insn;
					if (min.owner.equals(dst.name))
						continue;
					MethodNode temp = getMethod(min, classes);
					if (temp != null) {
						temp.access = unprivate(temp.access);
					}
				}
			}
		}
		return fcount + mcount;
	}
}
