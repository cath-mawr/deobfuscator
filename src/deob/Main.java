package deob;

import static java.lang.System.out;
import static org.objectweb.asm.Opcodes.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public final class Main {

	private static int simplifyMath(InsnList list)
	{
		int count = 0;
		for (int i = 1; i < list.size(); ++i) {
			AbstractInsnNode insn = list.get(i);
			AbstractInsnNode prev = Util.prev(insn);
			int op = insn.getOpcode();
			IntPush push;

			switch (op) {
			case ISHL:
			case LSHL:
			case ISHR:
			case LSHR:
			case IUSHR:
			case LUSHR:
				if (prev.getOpcode() == LDC) {
					int cst = (Integer) ((LdcInsnNode) prev).cst;
					int mask = op == ISHL || op == ISHR || op == IUSHR ? 31
						: 63;
					list.set(prev, new IntPush(cst & mask).insn);
					++count;
				}
				break;
			case IADD:
				push = IntPush.get(prev);
				if (push != null) {
					if (push.val < 0) {
						list.set(prev, new IntPush(-push.val).insn);
						list.set(insn, new InsnNode(ISUB));
						++count;
					}
				} else {
					StackNode n = Stack.buildMap(insn, 1);
					if (n == null)
						break;
					push = IntPush.get(n.children.get(0).insn);
					if (push != null && push.val < 0) {
						list.insertBefore(insn, new IntPush(-push.val).insn);
						list.set(insn, new InsnNode(ISUB));
						list.remove(push.insn);
						++count;
					}
				}
				break;
			case ISUB:
				push = IntPush.get(prev);
				if (push != null) {
					if (push.val < 0) {
						list.set(prev, new IntPush(-push.val).insn);
						list.set(insn, new InsnNode(IADD));
						++count;
					}
				}
				break;
			}
		}
		return count;
	}

	private static int foldGOTOs(InsnList code)
	{
		// fold GOTOs that target GOTOs
		// or remove those which target the next instruction
		int count = 0;
		for (int i = 0; i < code.size(); ++i) {
			AbstractInsnNode insn = code.get(i);
			JumpInsnNode jump;

			if (insn.getOpcode() != GOTO) {
				continue;
			}
			jump = (JumpInsnNode) insn;

			if (Util.next(jump.label) == Util.next(jump)) {
				code.remove(jump);
				--i;
				++count;
				continue;
			}
			AbstractInsnNode next = Util.next(jump.label);
			if (next.getOpcode() == GOTO) {
				jump = (JumpInsnNode) next;
				code.set(insn, new JumpInsnNode(GOTO, jump.label));
				i = 0;
				++count;
			}
		}
		return count;
	}

	private static void addParamNodes(ClassNode c, MethodNode m)
	{
		Type[] types = Type.getArgumentTypes(m.desc);

		LabelNode l1 = new LabelNode();
		LabelNode l2 = new LabelNode();
		if (m.localVariables == null) {
			m.localVariables = new ArrayList<>();
		}
		if (m.instructions.getFirst() != null) {
			m.instructions.insertBefore(m.instructions.getFirst(), l1);
			m.instructions.insert(m.instructions.getLast(), l2);
		} else {
			m.instructions.add(l1);
			m.instructions.add(l2);
		}

		int index = 0;

		if ((m.access & ACC_STATIC) == 0) {
			m.localVariables.add(new LocalVariableNode("this", "L" + c.name
				+ ";", null, l1, l2, index++));
		}

		for (int i = 0; i < types.length; ++i) {
			m.localVariables.add(new LocalVariableNode("arg" + i, types[i]
				.getDescriptor(), null, l1, l2, index++));
		}
	}

	private static void doRendering(ClassNode[] classes)
	{
		MethodNode m;
		InsnList list;
		LabelNode l;

		System.out.println("doing rendering");

		list = new InsnList();
		l = new LabelNode();
		m = Util.getMethod(classes, "t", "a", "(III[I[IIII)V");
		list.add(new FieldInsnNode(GETSTATIC,
		    "PaintListener", "render_solid", "Z"));
		list.add(new JumpInsnNode(IFNE, l));
		list.add(new InsnNode(RETURN));
		list.add(l);
		m.instructions.insert(list);

		/* fill 64 */
		list = new InsnList();
		l = new LabelNode();
		m = Util.getMethod(classes, "p", "a", "(IIIII[IIIII[IIIII)V");
		list.add(new FieldInsnNode(GETSTATIC,
		    "PaintListener", "render_textures", "Z"));
		list.add(new JumpInsnNode(IFNE, l));
		list.add(new InsnNode(RETURN));
		list.add(l);
		m.instructions.insert(list);

		/* fill 64 key */
		list = new InsnList();
		l = new LabelNode();
		m = Util.getMethod(classes, "cb", "a", "(IIIBIIII[I[IIIIIII)V");
		list.add(new FieldInsnNode(GETSTATIC,
		    "PaintListener", "render_textures", "Z"));
		list.add(new JumpInsnNode(IFNE, l));
		list.add(new InsnNode(RETURN));
		list.add(l);
		m.instructions.insert(list);

		/* fill 128 */
		list = new InsnList();
		l = new LabelNode();
		m = Util.getMethod(classes, "gb", "a", "(IIBIII[IIIIII[III)V");
		list.add(new FieldInsnNode(GETSTATIC,
		    "PaintListener", "render_textures", "Z"));
		list.add(new JumpInsnNode(IFNE, l));
		list.add(new InsnNode(RETURN));
		list.add(l);
		m.instructions.insert(list);

		/* fill 128 key */
		list = new InsnList();
		l = new LabelNode();
		m = Util.getMethod(classes, "wb", "a", "(IIII[IIIIIIIIII[II)V");
		list.add(new FieldInsnNode(GETSTATIC,
		    "PaintListener", "render_textures", "Z"));
		list.add(new JumpInsnNode(IFNE, l));
		list.add(new InsnNode(RETURN));
		list.add(l);
		m.instructions.insert(list);
	}

	private static void doGraphics(ClassNode[] classes)
		throws Throwable
	{
		ClassNode c = null;
		for (ClassNode n : classes) {
			if (n.name.equals("ua")) {
				c = n;
				break;
			}
		}
		for (MethodNode m : c.methods) {
			if (m.name.equals("a") && m.desc.equals("(B[BI)V")) {
				InsnList inj = new InsnList();
				inj.add(new VarInsnNode(ALOAD, 2));
				inj.add(new MethodInsnNode(INVOKESTATIC, "SleepListener",
					"newWord", "([B)V", false));
				m.instructions.insert(inj);
				System.out.println("done graphics");
				break;
			}
		}
	}

	private static void doUID(ClassNode[] classes)
		throws Throwable
	{
		/* if it's creating a byte array with a length of 24, it's the uid thing */
		ClassNode c = null;
		for (ClassNode n : classes) {
			if (n.name.equals("f")) {
				c = n;
				break;
			}
		}
		for (MethodNode m : c.methods) {
			if (!m.name.equals("a") || !m.desc.equals("(ILtb;)V")) {
				continue;
			}
			InsnList inj = new InsnList();
			inj.add(new TypeInsnNode(NEW, "java/util/Random"));
			inj.add(new InsnNode(DUP));
			inj.add(new MethodInsnNode(INVOKESPECIAL,
			    "java/util/Random", "<init>", "()V", false));
			inj.add(new VarInsnNode(ALOAD, 2));
			inj.add(new MethodInsnNode(INVOKEVIRTUAL,
			    "java/util/Random", "nextBytes", "([B)V", false));
			for (int i = 0; i < m.instructions.size(); ++i) {
				AbstractInsnNode ai = m.instructions.get(i);
				if (ai.getOpcode() != ALOAD) {
					continue;
				}
				VarInsnNode v = (VarInsnNode) ai;
				if (v.var != 1) {
					continue;
				}
				m.instructions.insertBefore(ai, inj);
				break;
			}
			m.instructions.add(inj);
			System.out.println("done uid");
			break;
		}
	}

	private static ClassNode[] load(String name)
		throws IOException
	{
		JarFile f = new JarFile("rsclassic.jar");
		final ClassNode[] classes;
		try {
			ArrayList<ClassNode> list = new ArrayList<>();
			Enumeration<JarEntry> e = f.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				if (!entry.getName().endsWith(".class")) {
					continue;
				}
				InputStream in = f.getInputStream(entry);
				try {
					if (entry.getSize() > Integer.MAX_VALUE) {
						throw new IOException(entry.getName() + " too big");
					}
					int size = (int) entry.getSize();
					byte[] b = new byte[size];
					int read = 0;
					do {
						read += in.read(b, read, size - read);
					} while (read < size);
					ClassReader r = new ClassReader(b);
					ClassNode c = new ClassNode();
					r.accept(c, ClassReader.SKIP_DEBUG
						| ClassReader.SKIP_FRAMES);
					list.add(c);
				} finally {
					in.close();
				}
			}
			classes = list.toArray(new ClassNode[list.size()]);
			Arrays.sort(classes, new Comparator<ClassNode>() {

				@Override
				public int compare(ClassNode o1, ClassNode o2)
				{
					return o1.name.compareTo(o2.name);
				}
			});
			list.clear();
			list = null;
		} finally {
			f.close();
		}
		return classes;
	}

	private static void save(String name, ClassNode... classes)
		throws IOException
	{
		JarOutputStream out = new JarOutputStream(new FileOutputStream(
			"deob.jar"));
		try {
			for (ClassNode c : classes) {
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				c.accept(cw);
				JarEntry je = new JarEntry(c.name + ".class");
				out.putNextEntry(je);
				out.write(cw.toByteArray());
				out.closeEntry();
			}
		} finally {
			out.close();
		}
	}

	private static Map<Integer, LoginResponse> getLoginResponses(ClassNode c)
	{
		MethodNode login = null;
		boolean reverse = false;

		l0: for (MethodNode m : c.methods) {
			InsnList code = m.instructions;
			for (int i = 0; i < code.size(); ++i) {
				AbstractInsnNode insn = code.get(i);
				if (!(insn instanceof LdcInsnNode)) {
					continue;
				}
				LdcInsnNode ldc = (LdcInsnNode)insn;
				if (!(ldc.cst instanceof String)) {
					continue;
				}
				String str = (String)ldc.cst;
				if (!str.equals("Sorry! This world is currently full.")) {
					continue;
				}
				if (Util.prev(insn) instanceof LdcInsnNode) {
					reverse = true;
				}
				login = m;
				break l0;
			}
		}
		InsnList code = login.instructions;
		Map<Integer, LoginResponse> map = new LinkedHashMap<>();
		for (int i = 0; i < code.size(); ++i) {
			AbstractInsnNode insn = code.get(i);
			AbstractInsnNode prev = Util.prev(insn);
			if (insn.getOpcode() == IF_ICMPEQ) {
				insn = ((JumpInsnNode) insn).label;
			} else if (insn.getOpcode() == IF_ICMPNE) {
				insn = ((JumpInsnNode) insn).getNext();
			} else {
				insn = null;
			}
			if (insn == null) {
				continue;
			}
			int number = IntPush.get(prev).val;
			String s1 = null;
			String s2 = null;
			for (AbstractInsnNode n = insn; n != null; n = n.getNext()) {
				if (n instanceof JumpInsnNode) {
					break;
				}
				if (n instanceof MethodInsnNode) {
					break;
				}
				if (n.getOpcode() == RETURN) {
					break;
				}
				if (n instanceof LdcInsnNode) {
					if (s1 == null) {
						s1 = (String)((LdcInsnNode)n).cst;
					} else {
						s2 = (String)((LdcInsnNode)n).cst;
						break;
					}
				}
			}
			if (s1 == null || s2 == null) {
				continue;
			}
			if (reverse) {
				String temp = s1;
				s1 = s2;
				s2 = temp;
			}
			if (s1.contains("must enter")) {
				continue;
			}
			boolean fatal =
				s1.contains("disabled") ||
				s1.contains("stolen") ||
				s2.contains("support") ||
				s1.contains("member") ||
				s1.contains("veteran") ||
				s1.contains("display name") ||
				s1.contains("updated") ||
				s2.contains("cannot access") ||
				s1.contains("new players");
			map.put(number, new LoginResponse(fatal, s1, s2));
		}
		return map;
	}

	public static void main(String[] args)
		throws Throwable
	{
		System.out.println("begin");
		ClassNode[] classes = load("rsclassic.jar");
		/*
		FieldRef cf = null;
		for (ClassNode c : classes) {
			if (c.name.equals("client")) {
				cf = FieldRef.getControlField(c);
				break;
			}
		}
		if (cf == null) {
			throw new RuntimeException("Control field not found");
		}
		*/
		int removed_nots = 0;
		int removed_preds = 0;
		int removed_ex = 0;
		int removed_rex = 0;
		int simple_math = 0;
		int folded_gotos = 0;
		int swapped_conds = 0;
		int deciphered = 0;
		int dead_locals = 0;
		for (ClassNode c : classes) {
			for (MethodNode m : c.methods) {
				removed_ex += TryCatch.removeRedundant(m);
				removed_rex += TryCatch.removeREX(m);
				/*removed_preds += OpaquePredicates.remove(cf, m.instructions);
				folded_gotos += foldGOTOs(m.instructions);
				simple_math += simplifyMath(m.instructions);
				removed_nots += ConditionalNOT.remove(m.instructions);
				swapped_conds += SwapConditionals.correctOrder(m,
				    m.instructions);
				dead_locals += DeadLocals.remove(m.instructions);
				addParamNodes(c, m);
				*/
			}
			//deciphered += DecipherStrings.decipher(c);
		}
		out.printf("Inlined deciphered strings: %d\n", deciphered);
		out.printf("Removed NOTs: %d\n", removed_nots);
		out.printf("Removed opaque predicates: %d\n", removed_preds);
		out.printf("Removed try catch blocks: %d\n", removed_ex);
		out.printf("Removed RuntimeException blocks: %d\n", removed_rex);
		out.printf("Simplified math instructions: %d\n", simple_math);
		out.printf("Folded GOTOs: %d\n", folded_gotos);
		out.printf("Swapped conditionals: %d\n", swapped_conds);
		out.printf("Removed dead locals: %d\n", dead_locals);
		//out.printf("Removed counters: %d\n", Counters.remove(classes));

		/*
		for (ClassNode c : classes) {
			if (c.name.equals("client")) {
				Map<Integer, LoginResponse> map = getLoginResponses(c);
				for (Integer key : map.keySet()) {
					if (key == -1) continue;
					LoginResponse r = map.get(key);
					System.out.printf("responses.put(%d, new LoginResponse(%s,\n\t\"%s\",\n\t\"%s\"));\n\n",
						key, String.valueOf(r.fatal), r.s1, r.s2);
				}
				System.out.println("private static final String[] fatal_resp = {");
				for (Integer key : map.keySet()) {
					if (key == -1) continue;
					LoginResponse r = map.get(key);
					if (r.s1.equals("Error unable to login.") || !r.fatal) {
						continue;
					}
					System.out.printf("\t\"%s\",\n", r.s1);
				}
				System.out.println("};");
				break;
			}
		}
		*/

		if (false) {
			ClassNode collection = new ClassNode();
			collection.name = "Static";
			collection.access = ACC_FINAL;
			collection.superName = "java/lang/Object";
			collection.version = classes[0].version; // FIXME
			out.printf("Collected statics: %d\n",
				CollectStatics.collect(collection, classes));
			ClassNode[] out = new
				ClassNode[classes.length + 1];
			System.arraycopy(classes, 0, out, 0,
				classes.length);
			out[classes.length] = collection;
			classes = out;
		}
		for (ClassNode c : classes) {
			c.access = Util.free(c.access);
			for (MethodNode m : c.methods) {
				m.access = Util.free(m.access);
			}
			for (FieldNode f : c.fields) {
				f.access = Util.free(f.access);
			}
			if (c.name.equals("client")) {
				Client.doClient(c);
			}
		}
		doRendering(classes);
		doGraphics(classes);
		doUID(classes);
		
		save("deob.jar", classes);
		System.out.println("end");
	}
}
