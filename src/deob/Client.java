package deob;

import static java.lang.System.out;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

final class Client {

	static final String MESSAGE_NAME = "a";
	static final String MESSAGE_DESC = "(ZLjava/lang/String;ILjava/lang/String;IILjava/lang/String;Ljava/lang/String;)V";

	static final String DISPLAY_GAME_FUNC = "b";
	static final String DISPLAY_GAME_DESC = "(Z)V";

	static final String PROCESS_GAME_FUNC = "e";
	static final String PROCESS_GAME_DESC = "(I)V";

	static final String PROCESS_INGAME_NAME = "J";
	static final String PROCESS_INGAME_DESC = "(I)V";

	static final String LOGIN_RESPONSE_NAME = "b";
	static final String LOGIN_RESPONSE_DESC = "(BLjava/lang/String;Ljava/lang/String;)V";

	static final String FRIENDS_FUNC = "a";
	static final String FRIENDS_DESC = "(ZZ)V";

	static final String MENU_FUNC = "L";
	static final String MENU_DESC = "(I)V";

	static final String WIDTH_FIELD = "Wd";
	static final String HEIGHT_FIELD = "Oi";

	private Client() {
	}

	private static void safeify(MethodNode m)
	{
		LabelNode method_start = new LabelNode();
		LabelNode method_end = new LabelNode();
		m.instructions.insert(method_start);
		m.instructions.add(method_end);

		LabelNode handler = new LabelNode();
		InsnList list = new InsnList();
		list.add(handler);
		list.add(new InsnNode(RETURN));
		m.instructions.add(list);

		TryCatchBlockNode n = new TryCatchBlockNode(method_start, method_end, handler, "java/lang/RuntimeException");
		m.tryCatchBlocks.add(n);
	}

	private static void doLoginResponse(InsnList code)
	{
		InsnList list = new InsnList();	
		list.add(new VarInsnNode(ALOAD, 2));
		list.add(new VarInsnNode(ALOAD, 3));
		list.add(new MethodInsnNode(INVOKESTATIC, "AutoLogin", "onLoginResponse", "(Ljava/lang/String;Ljava/lang/String;)V"));
		code.insert(list);
	}

	private static void doFriends(InsnList code)
	{
		int changed = 0;
		int size = code.size();
		for (int i = 0; i < size; ++i) {
			AbstractInsnNode insn = code.get(i);
			if (!(insn instanceof IntInsnNode)) {
				continue;
			}
			IntInsnNode push = (IntInsnNode)insn;
			InsnList list;
			switch ((int)push.operand) {
			case 489:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 23));
				list.add(new InsnNode(ISUB));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case ~489:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 23));
				list.add(new InsnNode(ISUB));
				list.add(new InsnNode(ICONST_M1));
				list.add(new InsnNode(IXOR));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case 429:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 83));
				list.add(new InsnNode(ISUB));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case ~429:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 83));
				list.add(new InsnNode(ISUB));
				list.add(new InsnNode(ICONST_M1));
				list.add(new InsnNode(IXOR));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case 315:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 83));
				list.add(new InsnNode(ISUB));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case ~315:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 83));
				list.add(new InsnNode(ISUB));
				list.add(new InsnNode(ICONST_M1));
				list.add(new InsnNode(IXOR));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			}
		}
		out.printf("Changed %d friends list constants\n", changed);
	}

	private static void doMenu(InsnList code)
	{
		int changed = 0;
		int size = code.size();
		for (int i = 0; i < size; ++i) {
			AbstractInsnNode insn = code.get(i);
			if (!(insn instanceof IntInsnNode)) {
				continue;
			}
			IntInsnNode push = (IntInsnNode)insn;
			InsnList list;
			switch ((int)push.operand) {
			case 510:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new InsnNode(ICONST_2));
				list.add(new InsnNode(ISUB));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case ~510:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new InsnNode(ICONST_2));
				list.add(new InsnNode(ISUB));
				list.add(new InsnNode(ICONST_M1));
				list.add(new InsnNode(IXOR));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case 315:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", HEIGHT_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 19));
				list.add(new InsnNode(ISUB));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			case ~315:
				list = new InsnList();
				list.add(new VarInsnNode(ALOAD, 0));
				list.add(new FieldInsnNode(GETFIELD,
				    "client", HEIGHT_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 19));
				list.add(new InsnNode(ISUB));
				list.add(new InsnNode(ICONST_M1));
				list.add(new InsnNode(IXOR));
				code.insertBefore(push, list);
				code.remove(push);
				i = 0;
				changed += 1;
				break;
			}
		}
		out.printf("Changed %d action menu constants\n", changed);
	}

	private static void doRender(MethodNode m, boolean[] complete)
	{
		InsnList inj = new InsnList();
		inj.add(new MethodInsnNode(INVOKESTATIC,
		    "ScriptListener", "runScript", "()V", false));
		LabelNode label = new LabelNode();
		inj.add(new VarInsnNode(ALOAD, 0));
		inj.add(new FieldInsnNode(GETFIELD,
		    "client", "disable_gfx", "Z"));
		inj.add(new JumpInsnNode(IFEQ, label));
		inj.add(new InsnNode(RETURN));
		inj.add(label);
		m.instructions.insert(inj);
		complete[0] = true;

		for (int i = 0; i < m.instructions.size(); ++i) {
			AbstractInsnNode ai = m.instructions.get(i);
			if (ai.getOpcode() != SIPUSH)
				continue;
			IntInsnNode in = (IntInsnNode) ai;
			switch (in.operand) {
			case 2400:
			case 2300:
			case 2200:
			case 2100:
				InsnList getfog = new InsnList();
				getfog.add(new VarInsnNode(ALOAD, 0));
				getfog.add(new FieldInsnNode(GETFIELD,
				    "client", "fogginess", "I"));
				getfog.add(new InsnNode(IADD));
				m.instructions.insert(ai, getfog);
				complete[6] = true;
				break;
			}
		}
	}

	private static void doMessage(MethodNode m, boolean[] complete)
	{
		InsnList inj = new InsnList();
		inj.add(new VarInsnNode(ILOAD, 1));
		inj.add(new VarInsnNode(ALOAD, 2));
		inj.add(new VarInsnNode(ILOAD, 3));
		inj.add(new VarInsnNode(ALOAD, 4));
		inj.add(new VarInsnNode(ILOAD, 5));
		inj.add(new VarInsnNode(ILOAD, 6));
		inj.add(new VarInsnNode(ALOAD, 7));
		inj.add(new VarInsnNode(ALOAD, 8));
		inj.add(new MethodInsnNode(
			INVOKESTATIC,
			"ScriptListener", "message",
			"(ZLjava/lang/String;ILjava/lang/String;IILjava/lang/String;Ljava/lang/String;)V",
			false));
		m.instructions.insert(inj);
		complete[4] = true;
	}

	private static void doClientInit(MethodNode m, boolean[] complete)
	{
		for (int i = 0; i < m.instructions.size(); ++i) {
			AbstractInsnNode sipush = m.instructions.get(i);
			if (sipush.getOpcode() != SIPUSH) {
				continue;
			}
			AbstractInsnNode putfield = sipush.getNext();
			if (putfield.getOpcode() != PUTFIELD) {
				continue;
			}
			FieldInsnNode field = (FieldInsnNode) putfield;
			if (field.name.equals("ac") &&	
			    field.owner.equals("client")) {
				m.instructions.set(sipush,
				    new IntInsnNode(SIPUSH, 750));
				complete[8] = true;
				break;
			}
		}
	}

	private static void doClientProcess(MethodNode m, boolean[] complete)
	{
		// forced logout thingy. search for 15000
		for (int i = 0; i < m.instructions.size(); ++i) {
			AbstractInsnNode ai = m.instructions.get(i);
			if (ai.getOpcode() != INVOKESPECIAL) {
				continue;
			}
			MethodInsnNode mi = (MethodInsnNode) ai;
			if (!mi.owner.equals("client") ||
			    !mi.name.equals("B") ||
			    !mi.desc.equals("(I)V")) {
				out.println(mi.owner +
				    " " + mi.name + " " + mi.desc);
				continue;
			}
			m.instructions.set(ai, new InsnNode(POP2));
			complete[5] = true;
			break;
		}

		// camera height
		int found_count = 0;
		for (int i = 0; i < m.instructions.size(); ++i) {
			AbstractInsnNode ai = m.instructions.get(i);
			if (ai.getOpcode() != GETFIELD) {
				continue;
			}
			AbstractInsnNode iconst = ai.getNext();
			if (iconst.getOpcode() != ICONST_4) {
				continue;
			}
			int arith_op = iconst.getNext().getOpcode();
			if (arith_op != IADD && arith_op != ISUB) {
				continue;
			}
			FieldInsnNode getfield = (FieldInsnNode) ai;
			if (getfield.name.equals("ac")
			    && getfield.owner.equals("client")) {
				m.instructions.set(iconst,
				    new InsnNode(ICONST_0));
				++found_count;
			}
		}
		if (found_count == 2) {
			complete[7] = true;
		}
	}

	private static boolean fixChatBox(InsnList code, int w, int h)
	{
		final int[] values = {
			269, 502, 324, 498, 269, 502, 269, 502
		};
		final IntInsnNode[] found = new IntInsnNode[values.length];
		for (int i = 0; i < code.size(); ++i) {
			AbstractInsnNode insn = code.get(i);
			if (!(insn instanceof IntInsnNode)) {
				continue;
			}
			IntInsnNode n = (IntInsnNode)insn;
			for (int j = 0; j < values.length; ++j) {
				if (values[j] == n.operand &&
				    found[j] == null) {
					found[j] = n;
					break;
				}
			}
		}
		for (AbstractInsnNode insn : found) {
			if (insn == null) {
				return false;
			}
		}
		for (int i = 0; i < found.length; ++i) {
			InsnList list = new InsnList();
			list.add(new VarInsnNode(ALOAD, 0));
			if (((i + 1) % 2) == 0) {
				list.add(new FieldInsnNode(GETFIELD,
				    "client", WIDTH_FIELD, "I"));
				list.add(new IntInsnNode(SIPUSH, 512));
				//found[i].operand = w - (512 - values[i]);
			} else {
				list.add(new FieldInsnNode(GETFIELD,
				    "client", HEIGHT_FIELD, "I"));
				list.add(new IntInsnNode(BIPUSH, 12));
				list.add(new InsnNode(IADD));
				list.add(new IntInsnNode(SIPUSH, 346));
				//found[i].operand = h - (346 - values[i]);
			}
			code.insertBefore(found[i], list);

			list = new InsnList();
			list.add(new InsnNode(ISUB));
			list.add(new InsnNode(ISUB));
			code.insert(found[i], list);
		}
		return true;
	}

	private static void examineMethod(MethodNode m, boolean[] complete)
	{
		InsnList list = m.instructions;
		if (fixChatBox(list, 640, 480)) {
			out.println("fixed chat box");
			return;
		}
		if (m.name.equals("f") && m.desc.equals("(I)V")) {
			doRender(m, complete);
		} else if (m.name.equals("A") &&
		    m.desc.equals("(I)V")) {
			list.insert(new MethodInsnNode(INVOKESTATIC,
			    "PaintListener", "paint", "()V", false));
			complete[1] = true;
		} else if (m.name.equals("j") &&
		    m.desc.equals("(I)V")) {
			list.insert(new MethodInsnNode(INVOKESTATIC,
			    "AutoLogin", "welcomeBoxTick",
			    "()V", false));
			complete[2] = true;
		} else if (m.name.equals("x") &&
		    m.desc.equals("(I)V")) {
			list.insert(new MethodInsnNode(INVOKESTATIC,
			    "AutoLogin", "loginTick", "()V", false));
			complete[3] = true;
		} else if (m.name.equals(MESSAGE_NAME)
		    && m.desc.equals(MESSAGE_DESC)) {
			doMessage(m, complete);
		} else if (m.name.equals(PROCESS_INGAME_NAME) &&
		    m.desc.equals(PROCESS_INGAME_DESC)) {
			doClientProcess(m, complete);
		} else if (m.name.equals("<init>") &&
		    m.desc.equals("()V")) {
			doClientInit(m, complete);
		} else if (m.name.equals(FRIENDS_FUNC) &&
		    m.desc.equals(FRIENDS_DESC)) {
			doFriends(list);
		} else if (m.name.equals(MENU_FUNC) &&
		    m.desc.equals(MENU_DESC)) {
			doMenu(list);
		} else if (m.name.equals(LOGIN_RESPONSE_NAME) &&
		    m.desc.equals(LOGIN_RESPONSE_DESC)) {
			doLoginResponse(list);
		} else if (m.name.equals(PROCESS_GAME_FUNC) &&
		    m.desc.equals(PROCESS_GAME_DESC)) {
			safeify(m);
			System.out.println("Safe-ified process_game()");
		} else if (m.name.equals(DISPLAY_GAME_FUNC) &&
		    m.desc.equals(DISPLAY_GAME_DESC)) {
			safeify(m);
			System.out.println("Safe-ified display_game()");
		}
	}

	static void doClient(ClassNode c)
		throws Throwable
	{
		c.fields.add(new FieldNode(ACC_PROTECTED,
		    "disable_gfx", "Z", null, null));
		c.fields.add(new FieldNode(ACC_PROTECTED,
		    "fogginess", "I", null, null));
		boolean[] complete = new boolean[9];
		for (MethodNode m : c.methods) {
			examineMethod(m, complete);
		}
		out.println(Arrays.toString(complete));
	}
}
