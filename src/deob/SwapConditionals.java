package deob;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public final class SwapConditionals {

	private static int invertOp(int opcode)
	{
		switch (opcode) {
		case IF_ICMPLT:
			return IF_ICMPGT;
		case IF_ICMPGT:
			return IF_ICMPLT;
		case IF_ICMPGE:
			return IF_ICMPLE;
		case IF_ICMPLE:
			return IF_ICMPGE;
		default:
			return opcode;
		}
	}

	static boolean isAccessedLabel(AbstractInsnNode a, InsnList code)
	{
		if (a instanceof LabelNode) {
			for (int j = 0; j < code.size(); ++j) {
				AbstractInsnNode insn2 = code.get(j);
				if (!(insn2 instanceof JumpInsnNode)) {
					continue;
				}
				if (((JumpInsnNode) insn2).label.equals(a)) {
					return true;
				}
			}
		}
		return false;
	}

	static int correctOrder(MethodNode m, InsnList code)
	{
		int count = 0;
		for (int i = 0; i < code.size(); ++i) {
			AbstractInsnNode insn = code.get(i);
			switch (insn.getOpcode()) {
			case IF_ICMPEQ:
			case IF_ICMPNE:
			case IF_ICMPLT:
			case IF_ICMPGT:
			case IF_ICMPGE:
			case IF_ICMPLE:
			case IF_ACMPEQ:
			case IF_ACMPNE:
				StackNode n = Stack.buildMap(insn, 1);
				if (n == null)
					continue;
				if (n.children.size() != 2) {
					System.out.println(String.format(
						"wrong number of children: %d (expected 2)",
						n.children.size()));
					continue;
				}

				AbstractInsnNode push = n.children.get(0).insn;
				IntPush ipush = IntPush.get(push);
				if (ipush != null) {
					if (IntPush.get(n.children.get(1).insn) != null) {
						continue;
					}
				} else {
					switch (push.getOpcode()) {
					case ACONST_NULL:
						if (n.children.get(1).insn.getOpcode() == ACONST_NULL) {
							continue;
						}
						break;
					default:
						continue;
					}
				}

				// pure evil
				if (isAccessedLabel(n.children.get(1).insn.getPrevious(), code)) {
					continue;
				}

				((JumpInsnNode) insn).setOpcode(invertOp(insn.getOpcode()));
				code.remove(push);
				code.insertBefore(insn, push);
				++count;
				break;
			}
		}
		return count;
	}
}
