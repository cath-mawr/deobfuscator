package deob;

import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.SIPUSH;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public final class IntPush {

	final int val;
	final AbstractInsnNode insn;

	public IntPush(int val)
	{
		switch (val) {
		case -1:
			insn = new InsnNode(ICONST_M1);
			break;
		case 0:
			insn = new InsnNode(ICONST_0);
			break;
		case 1:
			insn = new InsnNode(ICONST_1);
			break;
		case 2:
			insn = new InsnNode(ICONST_2);
			break;
		case 3:
			insn = new InsnNode(ICONST_3);
			break;
		case 4:
			insn = new InsnNode(ICONST_4);
			break;
		case 5:
			insn = new InsnNode(ICONST_5);
			break;
		default:
			if (val >= Byte.MIN_VALUE && val <= Byte.MAX_VALUE) {
				insn = new IntInsnNode(BIPUSH, val);
			} else if (val >= Short.MIN_VALUE && val <= Short.MAX_VALUE) {
				insn = new IntInsnNode(SIPUSH, val);
			} else {
				insn = new LdcInsnNode(val);
			}
		}
		this.val = val;
	}

	private IntPush(AbstractInsnNode insn, int val)
	{
		this.val = val;
		this.insn = insn;
	}

	static IntPush get(AbstractInsnNode insn)
	{
		if (insn instanceof IntInsnNode) {
			return new IntPush(insn, ((IntInsnNode) insn).operand);
		}
		if (insn instanceof LdcInsnNode) {
			LdcInsnNode ldc = (LdcInsnNode) insn;
			if (ldc.cst instanceof Integer) {
				return new IntPush(insn, (Integer) ldc.cst);
			}
		}
		switch (insn.getOpcode()) {
		case ICONST_M1:
			return new IntPush(insn, -1);
		case ICONST_0:
			return new IntPush(insn, 0);
		case ICONST_1:
			return new IntPush(insn, 1);
		case ICONST_2:
			return new IntPush(insn, 2);
		case ICONST_3:
			return new IntPush(insn, 3);
		case ICONST_4:
			return new IntPush(insn, 4);
		case ICONST_5:
			return new IntPush(insn, 5);
		}
		return null;
	}
}
