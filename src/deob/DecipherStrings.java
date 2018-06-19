package deob;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;

import org.objectweb.asm.tree.*;

public final class DecipherStrings {

	private static int[] getKeys(MethodNode z2)
	{
		for (int i = 0; i < z2.instructions.size(); ++i) {
			AbstractInsnNode instr = z2.instructions.get(i);
			if (!(instr instanceof TableSwitchInsnNode)) {
				continue;
			}
			TableSwitchInsnNode ts = (TableSwitchInsnNode) instr;
			int[] keys = new int[ts.labels.size() + 1];
			for (int j = 0; j < ts.labels.size(); j++)
				keys[j] = IntPush.get(ts.labels.get(j).getNext()).val;
			keys[keys.length - 1] = IntPush.get(ts.dflt.getNext()).val;
			if (keys.length != 5) {
				return null;
			}
			for (int key : keys) {
				if (key == 0) {
					return null;
				}
			}
			return keys;
		}
		return null;
	}

	private static final String decipher(String enc, int[] keys)
	{
		char[] c = enc.toCharArray();
		for (int i = 0; i < c.length; ++i) {
			c[i] = (char) (c[i] ^ keys[i % keys.length]);
		}
		return new String(c);
	}

	static int decipher(ClassNode c)
	{
		int count = 0;
		MethodNode z1 = Util.getMethod(c, "z", "(Ljava/lang/String;)[C");
		MethodNode z2 = Util.getMethod(c, "z", "([C)Ljava/lang/String;");
		if (z1 == null || z2 == null) {
			return 0;
		}
		int[] keys = getKeys(z2);
		if (keys == null) {
			return 0;
		}
		MethodNode clinit = Util.getMethod(c, "<clinit>", "()V");
		if (clinit == null) {
			return 0;
		}
		ArrayList<String> decrypted = new ArrayList<>();
		FieldInsnNode storage = null;
		for (int i = 0; i < clinit.instructions.size(); i++) {
			AbstractInsnNode insn = clinit.instructions.get(i);
			if (!(insn instanceof LdcInsnNode)) {
				continue;
			}
			LdcInsnNode ldc = (LdcInsnNode) insn;
			if (!(ldc.cst instanceof String)) {
				continue;
			}
			AbstractInsnNode insn2 = insn.getNext();
			AbstractInsnNode insn3 = insn2.getNext();
			if (!(insn2 instanceof MethodInsnNode)
				|| !(insn3 instanceof MethodInsnNode)) {
				continue;
			}
			MethodInsnNode m1 = (MethodInsnNode) insn2;
			MethodInsnNode m2 = (MethodInsnNode) insn3;
			if (!m1.owner.equals(c.name) || !m1.name.equals(z1.name)
				|| !m1.desc.equals(z1.desc) || !m2.owner.equals(c.name)
				|| !m2.name.equals(z2.name) || !m2.desc.equals(z2.desc)) {
				continue;
			}
			String str = decipher((String) ldc.cst, keys);
			decrypted.add(str);
			if (storage == null) {
				for (AbstractInsnNode x = Util.next(insn3); x != null; x = Util
					.next(x)) {
					if (x.getOpcode() != PUTSTATIC) {
						continue;
					}
					FieldInsnNode fin = (FieldInsnNode) x;
					if (fin.desc.equals("[Ljava/lang/String;")
						|| fin.desc.equals("Ljava/lang/String;")) {
						storage = fin;
					}
					break;
				}
			}
			clinit.instructions.set(ldc, new LdcInsnNode(str));
			clinit.instructions.remove(m1);
			clinit.instructions.remove(m2);
			++count;
		}
		c.methods.remove(z1);
		c.methods.remove(z2);
		if (storage == null) {
			return 0;
		}
		for (int i = 0; i < clinit.instructions.size(); i++) {
			AbstractInsnNode insn = clinit.instructions.get(i);
			if (insn.getOpcode() != PUTSTATIC) {
				continue;
			}
			FieldInsnNode f = (FieldInsnNode) insn;
			if (!f.owner.equals(c.name) || !f.name.equals(storage.name)
				|| !f.desc.equals(storage.desc)) {
				continue;
			}
			for (StackNode n : Stack.createList(f)) {
				clinit.instructions.remove(n.insn);
			}
			FieldNode field = Util.getField(c, storage.name, storage.desc);
			if (field == null) {
				System.out.println(String.format(
					"warning: string storage field not found: %s %s:%s",
					storage.desc, c.name, storage.name));
			} else {
				c.fields.remove(field);
			}
			break;
		}
		for (MethodNode m : c.methods) {
			for (int i = 0; i < m.instructions.size(); i++) {
				AbstractInsnNode insn = m.instructions.get(i);
				if (insn.getOpcode() != GETSTATIC) {
					continue;
				}
				FieldInsnNode fin = (FieldInsnNode) insn;
				if (!fin.owner.equals(storage.owner)
					|| !fin.name.equals(storage.name)
					|| !fin.desc.equals(storage.desc)) {
					continue;
				}
				if (storage.desc.equals("Ljava/lang/String;")) {
					m.instructions.set(insn, new LdcInsnNode(decrypted.get(0)));
				} else {
					AbstractInsnNode integer = fin.getNext();
					String dec = decrypted.get(IntPush.get(integer).val);
					m.instructions.remove(integer.getNext()); // AALOAD
					m.instructions.remove(integer);
					m.instructions.set(insn, new LdcInsnNode(dec));
				}
				++count;
			}
		}
		return count;
	}
}
