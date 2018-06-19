package deob;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public final class StackNode {

	final AbstractInsnNode insn;

	final ArrayList<StackNode> children;
	StackNode parent;

	public StackNode(AbstractInsnNode insn)
	{
		this.insn = insn;
		this.children = new ArrayList<StackNode>();
	}

	void clear(InsnList list)
	{
		Iterator<StackNode> it = children.iterator();
		while (it.hasNext()) {
			StackNode n = it.next();
			n.clear(list);
			list.remove(n.insn);
		}
	}
}
