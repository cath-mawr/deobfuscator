package deob;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class OpaquePredicates {

	static int remove(FieldRef cf, InsnList code)
	{
		int count = 0;
		int var = -1;
		for (int i = 1; i < code.size(); i++) {
			AbstractInsnNode insn = code.get(i);
			AbstractInsnNode prev = insn.getPrevious();

			if (prev.getOpcode() == GETSTATIC && insn.getOpcode() == ISTORE) {
				if (cf.equalsInsn((FieldInsnNode) prev)) {
					var = ((VarInsnNode) insn).var;
					code.remove(prev);
					code.remove(insn);
					break;
				}
			}
		}
		for (int i = 0; i < code.size(); i++) {
			AbstractInsnNode insn = code.get(i);
			AbstractInsnNode next = Util.next(insn);

			switch (insn.getOpcode()) {
			case ILOAD:
				if (var == -1 || ((VarInsnNode) insn).var != var) {
					continue;
				}
				// fall through
			case GETSTATIC:
				if (insn.getOpcode() == GETSTATIC) {
					if (!cf.equalsInsn((FieldInsnNode) insn)) {
						continue;
					}
				}
				while (next instanceof LabelNode) {
					next = next.getNext();
				}
				switch (next.getOpcode()) {
				case IFEQ:
					JumpInsnNode jump = new JumpInsnNode(GOTO,
						((JumpInsnNode) next).label);

					code.set(next, jump);
					break;
				case IFNE:
				case PUTSTATIC:
					code.remove(next);
					break;
				default:
					throw new RuntimeException("unexpected opcode: "
						+ insn.getOpcode());
				}
				code.remove(insn);
				++count;
				--i;
				break;
			case IINC:
				if (var != -1 && ((IincInsnNode) insn).var == var) {
					code.remove(code.get(i--));
				}
				break;
			}
		}
		return count;
	}
}
