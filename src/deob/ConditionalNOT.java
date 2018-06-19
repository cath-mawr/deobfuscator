package deob;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;

public final class ConditionalNOT {

	private static void unNotCondition(JumpInsnNode jump)
	{
		switch (jump.getOpcode()) {
		case IF_ICMPLT:
			jump.setOpcode(IF_ICMPGT);
			break;
		case IF_ICMPGT:
			jump.setOpcode(IF_ICMPLT);
			break;
		case IF_ICMPLE:
			jump.setOpcode(IF_ICMPGE);
			break;
		case IF_ICMPGE:
			jump.setOpcode(IF_ICMPLE);
			break;
		}
	}

	public static int remove(InsnList code)
	{
		// Turn ~a == ~b into a == b, ~a > ~b into a < b.
		int count = 0;
		for (int i = 0; i < code.size(); ++i) {
			AbstractInsnNode insn = code.get(i);
			switch (insn.getOpcode()) {
			case IF_ICMPEQ:
			case IF_ICMPNE:
			case IF_ICMPLT:
			case IF_ICMPGT:
			case IF_ICMPLE:
			case IF_ICMPGE: {
				StackNode n = Stack.buildMap(insn, 1);
				if (n == null)
					continue;
				if (n.children.size() != 2) {
					throw new RuntimeException("wrong number of children");
				}
				AbstractInsnNode xor_side;
				AbstractInsnNode other_side;
				if (n.children.get(0).insn.getOpcode() == IXOR) {
					xor_side = n.children.get(0).insn;
					other_side = n.children.get(1).insn;
				} else if (n.children.get(1).insn.getOpcode() == IXOR) {
					xor_side = n.children.get(1).insn;
					other_side = n.children.get(0).insn;
				} else {
					continue;
				}
				AbstractInsnNode xor_prev = Util.prev(xor_side);
				if (xor_prev.getOpcode() != ICONST_M1) {
					continue;
				}
				IntPush push = IntPush.get(other_side);
				if (push != null) {
					unNotCondition((JumpInsnNode) insn);
					code.set(other_side, new IntPush(~push.val).insn);
					code.remove(xor_prev);
					code.remove(xor_side);
					// i -= 2
					++count;
					continue;
				}
				if (other_side.getOpcode() != IXOR) {
					continue;
				}
				AbstractInsnNode other_prev = Util.prev(other_side);
				if (other_prev.getOpcode() == ICONST_M1) {
					unNotCondition((JumpInsnNode) insn);
					code.remove(xor_prev);
					code.remove(xor_side);
					code.remove(other_prev);
					code.remove(other_side);
					// i -= 4
					count += 2;
				}
			}
				break;
			case LCMP:
				// TODO
				break;
			case IFNE:
			case IFEQ: {
				AbstractInsnNode xor = Util.prev(insn);
				if (xor.getOpcode() != IXOR) {
					continue;
				}
				AbstractInsnNode iconst = Util.prev(xor);
				if (iconst.getOpcode() != ICONST_M1) {
					continue;
				}
				((JumpInsnNode) insn)
					.setOpcode(insn.getOpcode() == IFNE ? IF_ICMPNE : IF_ICMPEQ);
				// Let the ICONST_M1 stay, because ~0 == -1.
				code.remove(xor);
				++count;
			}
				break;
			default:
				continue;
			}
		}
		return count;
	}
}
