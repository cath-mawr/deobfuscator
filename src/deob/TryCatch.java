package deob;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.*;

public final class TryCatch {

	static int removeRedundant(MethodNode m)
	{
		int count = 0;
		for (int i = 0; i < m.tryCatchBlocks.size(); ++i) {
			TryCatchBlockNode block = m.tryCatchBlocks.get(i);
			AbstractInsnNode start = block.handler.getNext();

			if (start.getOpcode() == ATHROW) {
				AbstractInsnNode prev;

				prev = block.handler.getPrevious();
				m.instructions.remove(start);
				m.tryCatchBlocks.remove(i);
				if (prev != null && prev instanceof JumpInsnNode) {
					if (((JumpInsnNode) prev).label == block.handler) {
						m.instructions.remove(prev);
					}
				}
				--i;
				++count;
			}
		}
		for (int i = 0; i < (m.tryCatchBlocks.size() - 1); ++i) {
			TryCatchBlockNode cur = m.tryCatchBlocks.get(i);
			TryCatchBlockNode next = m.tryCatchBlocks.get(i + 1);
			if (cur.type == null || next.type == null) {
				continue;
			}

			if (cur.handler == next.handler) {
				cur.end = next.end;
				m.tryCatchBlocks.remove(i + 1);
				++count;
				--i;
			}
		}
		return count;
	}

	static int removeREX(MethodNode m)
	{
		int count = 0;
		tcloop: for (int i = 0; i < m.tryCatchBlocks.size(); ++i) {
			TryCatchBlockNode block = m.tryCatchBlocks.get(i);
			AbstractInsnNode insn;

			for (insn = block.start; insn != block.end; insn = insn.getNext()) {
				if (insn.getOpcode() == NEW
					&& ((TypeInsnNode) insn).desc
						.equals("java/lang/RuntimeException")) {
					continue tcloop;
				}
			}
			/*
			 * if (block.type == null) { }
			 */
			if (!"java/lang/RuntimeException".equals(block.type)) {
				continue;
			}
			for (insn = block.handler; insn != null; insn = insn.getNext()) {
				if (insn.getOpcode() == ATHROW) {
					break;
				}
				switch (insn.getOpcode()) {
				case IRETURN:
				case LRETURN:
				case FRETURN:
				case DRETURN:
				case ARETURN:
				case RETURN:
					continue tcloop;
				}
			}
			m.tryCatchBlocks.remove(i--);
			++count;
		}
		return count;
	}
}
