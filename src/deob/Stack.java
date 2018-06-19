package deob;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;

import static deob.Util.prev;

public final class Stack {

	static int getInsnReq(AbstractInsnNode insn)
	{
		switch (insn.getOpcode()) {
		case AALOAD:
			return 2;
		case AASTORE:
			return 3;
		case ACONST_NULL:
			return 0;
		case ALOAD:
			return 0;
		case ANEWARRAY:
			return 1;
		case ARETURN:
			return 1;
		case ARRAYLENGTH:
			return 1;
		case ASTORE:
			return 1;
		case ATHROW:
			return 1;
		case BALOAD:
			return 2;
		case BASTORE:
			return 3;
		case BIPUSH:
			return 0;
		case CALOAD:
			return 2;
		case CASTORE:
			return 3;
		case CHECKCAST:
			return 1;
		case D2F:
			return 1;
		case D2I:
			return 1;
		case D2L:
			return 1;
		case DADD:
			return 2;
		case DALOAD:
			return 2;
		case DASTORE:
			return 3;
		case DCMPG:
			return 2;
		case DCMPL:
			return 2;
		case DCONST_0:
			return 0;
		case DCONST_1:
			return 0;
		case DDIV:
			return 2;
		case DLOAD:
			return 0;
		case DMUL:
			return 2;
		case DNEG:
			return 1;
		case DREM:
			return 2;
		case DRETURN:
			return 1;
		case DSTORE:
			return 1;
		case DSUB:
			return 2;
		case DUP:
			return 1;
		case DUP_X1:
			return 2;
		case DUP_X2:
			return 3;
		case DUP2:
			throw new RuntimeException(); // FIXME
		case DUP2_X1:
			throw new RuntimeException(); // FIXME
		case DUP2_X2:
			throw new RuntimeException(); // FIXME
		case F2D:
			return 1;
		case F2I:
			return 1;
		case F2L:
			return 1;
		case FADD:
			return 2;
		case FALOAD:
			return 2;
		case FASTORE:
			return 3;
		case FCMPG:
			return 2;
		case FCMPL:
			return 2;
		case FCONST_0:
			return 0;
		case FCONST_1:
			return 0;
		case FCONST_2:
			return 0;
		case FDIV:
			return 2;
		case FLOAD:
			return 0;
		case FMUL:
			return 2;
		case FNEG:
			return 1;
		case FREM:
			return 2;
		case FRETURN:
			return 1;
		case FSTORE:
			return 1;
		case FSUB:
			return 2;
		case GETFIELD:
			return 1;
		case GETSTATIC:
			return 0;
		case GOTO:
			return 0;
		case I2B:
			return 1;
		case I2C:
			return 1;
		case I2D:
			return 1;
		case I2F:
			return 1;
		case I2L:
			return 1;
		case I2S:
			return 1;
		case IADD:
			return 2;
		case IALOAD:
			return 2;
		case IASTORE:
			return 3;
		case IAND:
			return 2;
		case ICONST_M1:
			return 0;
		case ICONST_0:
			return 0;
		case ICONST_1:
			return 0;
		case ICONST_2:
			return 0;
		case ICONST_3:
			return 0;
		case ICONST_4:
			return 0;
		case ICONST_5:
			return 0;
		case IDIV:
			return 2;
		case IF_ACMPEQ:
			return 2;
		case IF_ACMPNE:
			return 2;
		case IF_ICMPEQ:
			return 2;
		case IF_ICMPGE:
			return 2;
		case IF_ICMPGT:
			return 2;
		case IF_ICMPLE:
			return 2;
		case IF_ICMPLT:
			return 2;
		case IF_ICMPNE:
			return 2;
		case IFEQ:
			return 1;
		case IFNE:
			return 1;
		case IFGE:
			return 1;
		case IFGT:
			return 1;
		case IFLE:
			return 1;
		case IFLT:
			return 1;
		case IFNONNULL:
			return 1;
		case IFNULL:
			return 1;
		case IINC:
			return 0;
		case ILOAD:
			return 0;
		case IMUL:
			return 2;
		case INEG:
			return 1;
		case INSTANCEOF:
			return 1;
		case INVOKEDYNAMIC:
			return Type.getArgumentTypes(((MethodInsnNode) insn).desc).length;
		case INVOKEINTERFACE:
			return 1 + Type.getArgumentTypes(((MethodInsnNode) insn).desc).length;
		case INVOKESPECIAL:
			return 1 + Type.getArgumentTypes(((MethodInsnNode) insn).desc).length;
		case INVOKESTATIC:
			return Type.getArgumentTypes(((MethodInsnNode) insn).desc).length;
		case INVOKEVIRTUAL:
			return 1 + Type.getArgumentTypes(((MethodInsnNode) insn).desc).length;
		case IOR:
			return 2;
		case IREM:
			return 2;
		case IRETURN:
			return 1;
		case ISHL:
			return 2;
		case ISHR:
			return 2;
		case ISTORE:
			return 1;
		case ISUB:
			return 2;
		case IUSHR:
			return 2;
		case IXOR:
			return 2;
		case JSR:
			return 0;
		case L2D:
			return 1;
		case L2F:
			return 1;
		case L2I:
			return 1;
		case LADD:
			return 2;
		case LALOAD:
			return 2;
		case LASTORE:
			return 3;
		case LAND:
			return 2;
		case LCMP:
			return 2;
		case LCONST_0:
			return 0;
		case LCONST_1:
			return 0;
		case LDC:
			return 0;
		case LDIV:
			return 2;
		case LLOAD:
			return 0;
		case LMUL:
			return 2;
		case LNEG:
			return 1;
		case LOOKUPSWITCH:
			return 1;
		case LOR:
			return 2;
		case LREM:
			return 2;
		case LRETURN:
			return 1;
		case LSHL:
			return 2;
		case LSHR:
			return 2;
		case LSTORE:
			return 1;
		case LSUB:
			return 2;
		case LUSHR:
			return 2;
		case LXOR:
			return 2;
		case MONITORENTER:
			return 1;
		case MONITOREXIT:
			return 1;
		case MULTIANEWARRAY:
			return ((MultiANewArrayInsnNode) insn).dims;
		case NEW:
			return 0;
		case NEWARRAY:
			return 1;
		case NOP:
			return 0;
		case POP:
			return 1;
		case POP2:
			throw new RuntimeException(); // FIXME
		case PUTFIELD:
			return 2;
		case PUTSTATIC:
			return 1;
		case RET:
			return 0;
		case RETURN:
			return 0;
		case SALOAD:
			return 2;
		case SASTORE:
			return 3;
		case SIPUSH:
			return 0;
		case SWAP:
			return 2;
		case TABLESWITCH:
			return 1;
		default:
			throw new IllegalArgumentException("" + insn.getOpcode());
		}
	}

	public static int getInsnResult(AbstractInsnNode insn)
	{
		switch (insn.getOpcode()) {
		case AALOAD:
			return 1;
		case AASTORE:
			return 0;
		case ACONST_NULL:
			return 1;
		case ALOAD:
			return 1;
		case ANEWARRAY:
			return 1;
		case ARETURN:
			throw new RuntimeException("stack emptied");
		case ARRAYLENGTH:
			return 1;
		case ASTORE:
			return 0;
		case ATHROW:
			throw new RuntimeException("stack emptied");
		case BALOAD:
			return 1;
		case BASTORE:
			return 0;
		case BIPUSH:
			return 1;
		case CALOAD:
			return 1;
		case CASTORE:
			return 0;
		case CHECKCAST:
			return 1;
		case D2F:
			return 1;
		case D2I:
			return 1;
		case D2L:
			return 1;
		case DADD:
			return 1;
		case DALOAD:
			return 1;
		case DASTORE:
			return 0;
		case DCMPG:
			return 1;
		case DCMPL:
			return 1;
		case DCONST_0:
			return 1;
		case DCONST_1:
			return 1;
		case DDIV:
			return 1;
		case DLOAD:
			return 1;
		case DMUL:
			return 1;
		case DNEG:
			return 1;
		case DREM:
			return 1;
		case DRETURN:
			throw new RuntimeException("stack emptied");
		case DSTORE:
			return 0;
		case DSUB:
			return 1;
		case DUP:
			return 2;
		case DUP_X1:
			return 3;
		case DUP_X2:
			throw new RuntimeException(); // FIXME
		case DUP2:
			throw new RuntimeException(); // FIXME
		case DUP2_X1:
			throw new RuntimeException(); // FIXME
		case DUP2_X2:
			throw new RuntimeException(); // FIXME
		case F2D:
			return 1;
		case F2I:
			return 1;
		case F2L:
			return 1;
		case FADD:
			return 1;
		case FALOAD:
			return 1;
		case FASTORE:
			return 0;
		case FCMPG:
			return 1;
		case FCMPL:
			return 1;
		case FCONST_0:
			return 1;
		case FCONST_1:
			return 1;
		case FCONST_2:
			return 1;
		case FDIV:
			return 1;
		case FLOAD:
			return 1;
		case FMUL:
			return 1;
		case FNEG:
			return 1;
		case FREM:
			return 1;
		case FRETURN:
			throw new RuntimeException("stack emptied");
		case FSTORE:
			return 0;
		case FSUB:
			return 1;
		case GETFIELD:
			return 1;
		case GETSTATIC:
			return 1;
		case GOTO:
			return 0;
		case I2B:
			return 1;
		case I2C:
			return 1;
		case I2D:
			return 1;
		case I2F:
			return 1;
		case I2L:
			return 1;
		case I2S:
			return 1;
		case IADD:
			return 1;
		case IALOAD:
			return 1;
		case IASTORE:
			return 0;
		case IAND:
			return 1;
		case ICONST_M1:
			return 1;
		case ICONST_0:
			return 1;
		case ICONST_1:
			return 1;
		case ICONST_2:
			return 1;
		case ICONST_3:
			return 1;
		case ICONST_4:
			return 1;
		case ICONST_5:
			return 1;
		case IDIV:
			return 1;
		case IF_ACMPEQ:
			return 0;
		case IF_ACMPNE:
			return 0;
		case IF_ICMPEQ:
			return 0;
		case IF_ICMPGE:
			return 0;
		case IF_ICMPGT:
			return 0;
		case IF_ICMPLE:
			return 0;
		case IF_ICMPLT:
			return 0;
		case IF_ICMPNE:
			return 0;
		case IFEQ:
			return 0;
		case IFNE:
			return 0;
		case IFGE:
			return 0;
		case IFGT:
			return 0;
		case IFLE:
			return 0;
		case IFLT:
			return 0;
		case IFNONNULL:
			return 0;
		case IFNULL:
			return 0;
		case IINC:
			return 0;
		case ILOAD:
			return 1;
		case IMUL:
			return 1;
		case INEG:
			return 1;
		case INSTANCEOF:
			return 1;
		case INVOKEDYNAMIC:
			return (Type.getReturnType(((MethodInsnNode) insn).desc) == Type.VOID_TYPE ? 0
				: 1);
		case INVOKEINTERFACE:
			return (Type.getReturnType(((MethodInsnNode) insn).desc) == Type.VOID_TYPE ? 0
				: 1);
		case INVOKESPECIAL:
			return (Type.getReturnType(((MethodInsnNode) insn).desc) == Type.VOID_TYPE ? 0
				: 1);
		case INVOKESTATIC:
			return (Type.getReturnType(((MethodInsnNode) insn).desc) == Type.VOID_TYPE ? 0
				: 1);
		case INVOKEVIRTUAL:
			return (Type.getReturnType(((MethodInsnNode) insn).desc) == Type.VOID_TYPE ? 0
				: 1);
		case IOR:
			return 1;
		case IREM:
			return 1;
		case IRETURN:
			throw new RuntimeException("stack emptied");
		case ISHL:
			return 1;
		case ISHR:
			return 1;
		case ISTORE:
			return 0;
		case ISUB:
			return 1;
		case IUSHR:
			return 1;
		case IXOR:
			return 1;
		case JSR:
			return 1;
		case L2D:
			return 1;
		case L2F:
			return 1;
		case L2I:
			return 1;
		case LADD:
			return 1;
		case LALOAD:
			return 1;
		case LASTORE:
			return 0;
		case LAND:
			return 1;
		case LCMP:
			return 1;
		case LCONST_0:
			return 1;
		case LCONST_1:
			return 1;
		case LDC:
			return 1;
		case LDIV:
			return 1;
		case LLOAD:
			return 1;
		case LMUL:
			return 1;
		case LNEG:
			return 1;
		case LOOKUPSWITCH:
			return 0;
		case LOR:
			return 1;
		case LREM:
			return 1;
		case LRETURN:
			throw new RuntimeException("stack emptied");
		case LSHL:
			return 1;
		case LSHR:
			return 1;
		case LSTORE:
			return 0;
		case LSUB:
			return 1;
		case LUSHR:
			return 1;
		case LXOR:
			return 1;
		case MONITORENTER:
			return 0;
		case MONITOREXIT:
			return 0;
		case MULTIANEWARRAY:
			return 1;
		case NEW:
			return 1;
		case NEWARRAY:
			return 1;
		case NOP:
			return 0;
		case POP:
			return 0;
		case POP2:
			return 0;
		case PUTFIELD:
			return 0;
		case PUTSTATIC:
			return 0;
		case RET:
			return 0;
		case RETURN:
			throw new RuntimeException("stack emptied");
		case SALOAD:
			return 1;
		case SASTORE:
			return 0;
		case SIPUSH:
			return 1;
		case SWAP:
			return 2;
		case TABLESWITCH:
			return 0;
		default:
			throw new IllegalArgumentException("" + insn.getOpcode());
		}
	}

	static int getDifference(AbstractInsnNode insn)
	{
		return getInsnResult(insn) - getInsnReq(insn);
	}

	static ArrayList<StackNode> createList(AbstractInsnNode origin)
	{
		ArrayList<StackNode> list = new ArrayList<>();
		final int req = getInsnReq(origin);

		if (req == 0) {
			list.add(new StackNode(origin));
			return list;
		}

		int owed = -req;

		list.add(new StackNode(origin));
		for (AbstractInsnNode p = prev(origin); p != null; p = prev(p)) {
			list.add(new StackNode(p));
			owed += getDifference(p);
			if (owed == 0)
				break;
		}

		Collections.reverse(list);
		return list;
	}

	private static StackNode getNode(List<StackNode> list,
		AbstractInsnNode origin)
	{
		for (StackNode n : list) {
			if (n.insn.equals(origin)) {
				return n;
			}
		}
		throw new IllegalArgumentException("insn not in list");
	}

	private static void buildTree(List<StackNode> list, StackNode origin,
		int deepness, int max)
	{
		final int req = getInsnReq(origin.insn);

		if (req == 0)
			return;

		int owed = -req;

		for (AbstractInsnNode p = prev(origin.insn); p != null; p = prev(p)) {
			StackNode child = getNode(list, p);
			child.parent = origin;
			origin.children.add(child);
			owed += getDifference(p);
			if (owed == 0)
				break;
		}

		Collections.reverse(origin.children);

		if (deepness < max) {
			for (StackNode n : origin.children) {
				buildTree(list, n, deepness + 1, max);
			}

			Iterator<StackNode> it = origin.children.iterator();
			while (it.hasNext()) {
				StackNode n = it.next();
				if (!origin.equals(n.parent)) {
					it.remove();
					// System.out.println("bye");
				}
			}
		}
	}

	static void print(List<StackNode> list)
	{
		System.out.println("{");
		for (StackNode n : list) {
			System.out.println("\t" + Util.OPCODES[n.insn.getOpcode()]);
		}
		System.out.println("}");
	}

	static StackNode buildMap(AbstractInsnNode insn, int maxlevels)
	{
		try {
			ArrayList<StackNode> list = createList(insn);
			for (StackNode n : list) {
				int op = n.insn.getOpcode();
				if (op == GOTO || op == DUP || op == DUP_X1) {
					// print(list);
					// System.out.println("^ Failed to analyze");
					return null; // FIXME
				}
			}
			StackNode origin = list.get(list.size() - 1);
			buildTree(list, origin, 0, maxlevels);
			return origin;
		} catch (Throwable t) {
			// FIXME: this happens when we hit an unsupported opcode in
			// createList()
			return null;
		}
	}
}
