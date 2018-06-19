package deob;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;

import org.objectweb.asm.tree.*;

public final class DeadLocals {

	static boolean findLoad(InsnList list, int var, int start, int end)
	{
		for (int i = start; i < end; ++i) {
			AbstractInsnNode insn = list.get(i);
			if (insn instanceof JumpInsnNode) {
				JumpInsnNode jump = (JumpInsnNode) insn;
				if (findLoad(list, var, Util.indexOf(list, jump.label), start)) {
					return true;
				}
			} else if (insn.getOpcode() == ILOAD
				&& ((VarInsnNode) insn).var == var) {
				return true;
			}
		}
		return false;
	}

	static boolean shouldRemove(ArrayList<StackNode> list, VarInsnNode origin)
	{
		for (int i = 0; i < list.size(); ++i) {
			StackNode n = list.get(i);
			if (n.insn instanceof MethodInsnNode
				|| n.insn instanceof InvokeDynamicInsnNode
				|| n.insn instanceof JumpInsnNode
				|| n.insn instanceof LabelNode) {
				return false;
			}
			if (n.insn.getOpcode() == ISTORE) {
				if (n.insn == origin)
					continue;
			}
		}
		return true;
	}

	static int remove(InsnList list)
	{
		int removed = 0;
		for (int i = 0; i < list.size(); ++i) {
			AbstractInsnNode insn = list.get(i);
			if (insn.getOpcode() != ISTORE) {
				continue;
			}
			VarInsnNode var = (VarInsnNode) insn;
			if (findLoad(list, var.var, i, list.size())) {
				continue;
			}
			try {
				ArrayList<StackNode> stack = Stack.createList(var);
				if (shouldRemove(stack, var)) {
					for (StackNode n : stack) {
						list.remove(n.insn);
					}
					++removed;
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return removed;
	}
}
